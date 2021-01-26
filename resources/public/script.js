window.onload = () => {
     const chatWindow = document.querySelector('#chat-window');
     const chat = document.querySelector('.chat');
     if (chatWindow != null && chatWindow.scrollHeight == 0) {
          chat.style.visibility = "hidden";
     }
     if (chatWindow != null) {
          chatWindow.scrollTop = chatWindow.scrollHeight;
     }
}
