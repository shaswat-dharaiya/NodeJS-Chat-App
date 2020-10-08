var socket = io();
const messageContainer = document.getElementById('message-container')
const messageForm = document.getElementById('send-container')
const messageInput = document.getElementById('message-input')

const name = prompt('Enter your name: ')
appendMessage('You joined')
socket.emit('new-user', name)

socket.on('chat-message', data =>{
    appendMessage(`${data.name}: ${data.msg}`)
})

socket.on('user-connected', name =>{
    appendMessage(`${name} joined`)
})

messageForm.addEventListener('submit', e=>{
    e.preventDefault()
    const msg = messageInput.value
    appendMessage(`You: ${msg}`)
    socket.emit('send-chat-message',msg)
    messageInput.value = ''
})

function appendMessage(msg){
    const messageElement = document.createElement('div')
    messageElement.innerText = msg
    messageContainer.append(messageElement)
}