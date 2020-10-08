const io = require('socket.io')(80)

const users = {}
io.on('connection', socket => {
    socket.on('new-user', name => {
        users[socket.id] = name
        socket.broadcast.emit('user-connected', name)
    })
    console.log("New user")
    // socket.emit('chat-message', 'Hello World')
    socket.on('send-chat-message', msg =>{
        socket.broadcast.emit('chat-message',{msg: msg, name: users[socket.id]})        
    })
})