window.onload=function () {
     var objDiv = document.getElementById("chat-window");
     objDiv.scrollTop = objDiv.scrollHeight;
}


const chatWindowHeight = document.querySelector('#chat-window');
const chatTitleHeight = document.querySelector('.chat-title');
const chat = document.querySelector('.chat');

if (chatWindowHeight.scrollHeight == 0) {
     chat.style.visibility = "hidden";
}
