'use strict';

let inviteContainer = document.querySelector('#invite-container');
let inventionArea = document.querySelector('#inventionArea');
let username = document.querySelector('#username').innerText.trim();
let connectingElement = document.querySelector('.connecting');

let chatElement = document.querySelector('#chat-container');
let roomNameDisplay = document.querySelector('#room_id_display');
let messageArea = document.querySelector('#messageArea');
let messageForm = document.querySelector('#messageForm');
let messageInput = document.querySelector('#message');

let stompClient = null;
let currentSubscription = null;
let topic = null;

let roomId = null;
let roomName = null;

let colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

//--------------------------------------------------------------------------------------------------------------------//

function S4() {
    return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
}

function uuid() {
    return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}

//--------------------------------------------------------------------------------------------------------------------//

function connectToInviteForm() {
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnectedToForm, onError);
}


if (!currentSubscription) {
    connectToInviteForm();
}


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
    let message = JSON.parse(payload.body);

    let inventionElement = document.createElement('li');

    if (message.type === 'INVITE') {
        inventionElement.classList.add('chat-message');

        let buttonRoom = document.createElement('button');
        buttonRoom.type = 'button';
        buttonRoom.classList.add('default');
        buttonRoom.setAttribute('onclick', 'connectUser(\'' + message.sender + '\', \'' + message.content + '\')');

        let textRoom = document.createTextNode(message.content);
        buttonRoom.appendChild(textRoom);

        inventionElement.appendChild(buttonRoom);

        inventionArea.appendChild(inventionElement);
        inventionArea.scrollTop = inventionArea.scrollHeight;
    }
}

//--------------------------------------------------------------------------------------------------------------------//

function connectOperator() {
    roomName = document.getElementById('newRoomName').value;

    if (roomName === "") {
        document.getElementById('emptyRoomName').classList.remove('hidden');
    }
    else {
        roomId = uuid();
        document.getElementById('emptyRoomName').classList.add('hidden');
        connectToChat();
    }
}

function connectUser(newRoomId, newRoomName) {
    roomId = newRoomId;
    roomName = newRoomName;

    connectToChat();
}

function connectAnonymous() {
    roomId = document.getElementById('roomUID').innerText.trim();
    roomName = document.getElementById('currRoomName').innerText.trim();
    inviteContainer = document.querySelector('#anonymous-container');

    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    inviteContainer.classList.add('hidden');
    chatElement.classList.remove('hidden');

    stompClient.connect({}, onConnected, onError);
}

//--------------------------------------------------------------------------------------------------------------------//

function connectToChat() {
    currentSubscription.unsubscribe();
    stompClient.disconnect();

    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    if (inventionArea != null) {
        inventionArea.innerHTML = '';
    }

    inviteContainer.classList.add('hidden');
    chatElement.classList.remove('hidden');

    stompClient.connect({}, onConnected, onError);
}

function enterRoom() {
    roomNameDisplay.textContent = roomName;
    topic = '/app/chat/' + roomId;

    if (currentSubscription) {
        currentSubscription.unsubscribe();
    }

    currentSubscription = stompClient.subscribe('/channel/' + roomId, onMessageReceived);
    getLastDialogMessages(roomId);

    stompClient.send(topic + '/addUser', {}, JSON.stringify({sender: username, type: 'JOIN', content: roomName}));
}

function onConnected() {
    enterRoom();
    connectingElement.classList.add('hidden');
}


function sendMessage(event) {
    let messageContent = messageInput.value.trim();

    if(messageContent && stompClient) {
        let chatMessage = {
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
    let message = JSON.parse(payload.body);

    let messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else if (message.type === 'NEW_DIALOG') {
        messageElement.classList.add('event-message');
        message.content = 'The dialogue is over! A new dialogue begins.'
    } else {
            messageElement.classList.add('chat-message');

            let avatarElement = document.createElement('i');
            let avatarText = document.createTextNode(message.sender[0]);
            avatarElement.appendChild(avatarText);
            avatarElement.style['background-color'] = getAvatarColor(message.sender);

            messageElement.appendChild(avatarElement);

            let usernameElement = document.createElement('span');
            let usernameText = document.createTextNode(message.sender);
            usernameElement.appendChild(usernameText);
            messageElement.appendChild(usernameElement);
    }

    let textElement = document.createElement('p');
    let messageText = document.createTextNode(message.content);

    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;

    if (message.type === 'CHAT') {
        postMessageTime(message.time);
    }
}

function postMessageTime(messageTime) {
    //let dateTime = new Date(messageTime);
    let dateTime = moment(messageTime);
    dateTime = moment(dateTime).format("DD.MM.YYYY HH:mm:ss");
    //dateTime = dateTime.toLocaleString('ru-RU');

    let timeElement = document.createElement('li');
    timeElement.classList.add('time-message');
    let textElement = document.createElement('p');

    let timeText = document.createTextNode('(' + dateTime + ')');
    textElement.appendChild(timeText);

    timeElement.appendChild(textElement);
    messageArea.appendChild(timeElement);
}


function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    let index = Math.abs(hash % colors.length);
    return colors[index];
}


function quit() {
    let result = confirm("Are you sure you want to quit?");
    if (result) {
        let chatMessage = {
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
}

function logout() {
    let chatMessage = {
        sender: username,
        type: 'LEAVE'
    };
    currentSubscription.unsubscribe();
    stompClient.send(topic + '/sendMessage', {}, JSON.stringify(chatMessage));
}


messageForm.addEventListener('submit', sendMessage, true);

//--------------------------------------------------------------------------------------------------------------------//

let invitationsLabel = document.getElementById("usersLabel");
let usersInvitations = document.getElementById("usersInvitations");
let createdRoomsLabel = document.getElementById("createdRoomsLabel");
let createdRooms = document.getElementById("createdRooms");

function showCreatedRooms() {
    invitationsLabel.classList.add("hidden");
    usersInvitations.classList.add("hidden");

    createdRoomsLabel.classList.remove("hidden");
    createdRooms.classList.remove("hidden");

    // TODO: fetech query, show all created rooms by operator
}

function closeCreatedRooms() {
    invitationsLabel.classList.remove("hidden");
    usersInvitations.classList.remove("hidden");

    createdRoomsLabel.classList.add("hidden");
    createdRooms.classList.add("hidden");
}

//--------------------------------------------------------------------------------------------------------------------//

let allUsersContainer = document.querySelector('#online-users-container');
let userListArea = document.querySelector('#userListArea');

function openUsersList() {
    chatElement.classList.add('hidden');
    allUsersContainer.classList.remove('hidden');
    allUsersContainer.classList.add('username-page-container');

    getUsersList();
}

function closeUsersList() {
    allUsersContainer.classList.add('hidden');
    userListArea.innerHTML = '';
    chatElement.classList.remove('hidden');
}

function getUsersList() {
    fetch("/users/")
        .then(response => {
            response.text().then(data =>{
                let users = JSON.parse(data);
                let userListArea = document.getElementById('userListArea');
                for (let user of users) {
                    let userElement = document.createElement("li");

                    let userLoginButton = document.createElement('button');
                    userLoginButton.type = 'button';
                    userLoginButton.classList.add('default');
                    userLoginButton.setAttribute('onclick', 'sendInvite(\'' + user.login.toString() + '\')');

                    let userText = document.createTextNode(user.login.toString());

                    userLoginButton.appendChild(userText);
                    userElement.appendChild(userLoginButton);
                    userListArea.appendChild(userElement);
                }
            });
        })
        .catch(response => {
            console.log("Something wrong!", response);
        })
}


function sendInvite(user) {
    let chatMessage = {
        sender: roomId,
        content: roomName,
        type: 'INVITE'
    };
    stompClient.send('/app/chat/' + user + 'PI/sendMessage', {}, JSON.stringify(chatMessage));

    alert('Invitation sent!');
}

function sendInviteByEmail() {
    let email = document.getElementById("email").value;

    if (email === '') {
        document.getElementById('emptyEmail').classList.remove('hidden');
    }
    else {
        document.getElementById('emptyEmail').classList.add('hidden');

        let chatMessage = {
            sender: username,
            sendTo: email,
            content: roomName,
            type: 'INVITE_BY_EMAIL'
        };
        stompClient.send(topic + '/sendMessage', {}, JSON.stringify(chatMessage));

        alert('Invitation to ' + email + ' sent!');
        document.getElementById("email").value = '';
    }
}

function changeDialog() {
    let chatMessage = {
        sender: username,
        type: 'NEW_DIALOG'
    };

    stompClient.send(topic + '/sendMessage', {}, JSON.stringify(chatMessage));
}

//--------------------------------------------------------------------------------------------------------------------//

function getLastDialogMessages(uuid) {
    fetch("/messages/" + uuid)
        .then(response => {
            response.text().then(data =>{

                if (data != null) {
                    let messages = JSON.parse(data);

                    for (let message of messages) {
                        let messageElement = document.createElement('li');

                        messageElement.classList.add('chat-message');

                        let avatarElement = document.createElement('i');
                        let avatarText = document.createTextNode(message.author[0]);
                        avatarElement.appendChild(avatarText);
                        avatarElement.style['background-color'] = getAvatarColor(message.author);

                        messageElement.appendChild(avatarElement);

                        let usernameElement = document.createElement('span');
                        let usernameText = document.createTextNode(message.author);
                        usernameElement.appendChild(usernameText);
                        messageElement.appendChild(usernameElement);

                        let textElement = document.createElement('p');
                        let messageText = document.createTextNode(message.text);

                        textElement.appendChild(messageText);
                        messageElement.appendChild(textElement);
                        messageArea.appendChild(messageElement);
                        messageArea.scrollTop = messageArea.scrollHeight;

                        postMessageTime(message.dateTime);
                    }
                }
            });
        })
        .catch(response => {
            console.log("Something wrong!", response);
        })
}