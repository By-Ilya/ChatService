'use strict';


var userListArea = document.querySelector('#userListArea');

function getUserList() {
    fetch("/users/")
        .then(response => {
        response.text().then(data =>{
        console.log(data);
    let users = JSON.parse(data);
    for (let user of users) {
        let userElement = document.createElement("li");
        let userLogin = user.login;
        let userLoginElement = document.createElement("span");
        userLoginElement.innerText = userLogin;
        userElement.appendChild(userLoginElement);
        userListArea.appendChild(userElement);
    }
});
})
.catch(response => {
        console.log("Something wrong!", response);
})
}

getUserList();