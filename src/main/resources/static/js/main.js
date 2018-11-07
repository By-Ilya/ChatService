'use strict';

var inviteContainer = document.querySelector('#invite-container');
var inventionArea = document.querySelector('#inventionArea');
var username = document.querySelector('#username').innerText.trim();
var connectingElement = document.querySelector('.connecting');

var chatElement = document.querySelector('#chat-container');
var roomIdDisplay = document.querySelector('#room_id_display');
var messageArea = document.querySelector('#messageArea');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');

var stompClient = null;
var currentSubscription;
var topic = null;
var roomId = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

//--------------------------------------------------------------------------------------------------------------------//

function connectToInviteForm() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnectedToForm, onError);
}

connectToInviteForm();


function onConnectedToForm() {
    topic = '/app/chat/' + username + 'PI';

    connectingElement.classList.add('hidden');
    currentSubscription = stompClient.subscribe('/channel/' + username + 'PI', onInventionReceived);
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function onInventionReceived(payload) {
    var message = JSON.parse(payload.body);

    var inventionElement = document.createElement('li');

    if (message.type === 'INVITE') {
        inventionElement.classList.add('chat-message');

        var inputRoom = document.createElement('input');
        inputRoom.type = 'button';
        inputRoom.value = message.content;
        inputRoom.setAttribute('onclick', 'connectToChat(\'' + message.content + '\')');

        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.sender + ' пригласил Вас в комнату. ' +
            'Нажмите на название комнаты, чтобы войти.');
        textElement.appendChild(messageText);

        inventionElement.appendChild(inputRoom);
        inventionElement.appendChild(textElement);

        inventionArea.appendChild(inventionElement);
        inventionArea.scrollTop = inventionArea.scrollHeight;
    }
}

//--------------------------------------------------------------------------------------------------------------------//

function connectOperator() {
    var roomInput = document.getElementById('newRoom').value;

    if (roomInput === "") {
        document.getElementById('emptyRoom').classList.remove('hidden')
    }
    else {
        connectToChat(roomInput);
    }
}

function connectAnonymous() {
    var roomInput = document.getElementById('currRoom').innerText.trim();
    inviteContainer = document.querySelector('#anonymous-container');
    inviteContainer.classList.add('hidden');

    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    roomId = roomInput;
    chatElement.classList.remove('hidden');

    stompClient.connect({}, onConnected, onError);
}

//--------------------------------------------------------------------------------------------------------------------//

function connectToChat(roomName) {
    inventionArea.innerHTML = '';
    currentSubscription.unsubscribe();
    stompClient.disconnect();

    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    roomId = roomName;
    inviteContainer.classList.add('hidden');
    chatElement.classList.remove('hidden');

    stompClient.connect({}, onConnected, onError);
}

function enterRoom(newRoomId) {
    roomIdDisplay.textContent = roomId;
    topic = '/app/chat/' + newRoomId;

    if (currentSubscription) {
        currentSubscription.unsubscribe();
    }

    currentSubscription = stompClient.subscribe('/channel/' + roomId, onMessageReceived);

    stompClient.send(topic + '/addUser', {}, JSON.stringify({sender: username, type: 'JOIN'}));
}

function onConnected() {
    enterRoom(roomId);
    connectingElement.classList.add('hidden');
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    var date = new Date();

    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT',
        };
        stompClient.send(topic + '/sendMessage', {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);

    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;

    if (message.type === 'CHAT') {
        postMessageTime(message.time);
    }
}

function postMessageTime(messageTime) {
    var dateTime = new Date(messageTime);
    dateTime = dateTime.toLocaleString("ru-RU");

    var timeElement = document.createElement('li');
    timeElement.classList.add('time-message');
    var textElement = document.createElement('p');

    var timeText = document.createTextNode('(' + dateTime + ')');
    textElement.appendChild(timeText);

    timeElement.appendChild(textElement);
    messageArea.appendChild(timeElement);
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}


function back() {
    var chatMessage = {
        sender: username,
        type: 'LEAVE'
    };
    stompClient.send(topic + '/sendMessage', {}, JSON.stringify(chatMessage));
    currentSubscription.unsubscribe();

    chatElement.classList.add('hidden');
    messageArea.innerHTML = '';
    inviteContainer.classList.remove('hidden');

    connectToInviteForm();
}

function logout() {
    var chatMessage = {
        sender: username,
        type: 'LEAVE'
    };
    stompClient.send(topic + '/sendMessage', {}, JSON.stringify(chatMessage));
}


messageForm.addEventListener('submit', sendMessage, true);

//--------------------------------------------------------------------------------------------------------------------//

function sendInvite() {
    var chatMessage = {
        sender: username,
        content: roomId,
        type: 'INVITE'
    };
    stompClient.send('/app/chat/UtopiaPI/sendMessage', {}, JSON.stringify(chatMessage));
}

//--------------------------------------------------------------------------------------------------------------------//