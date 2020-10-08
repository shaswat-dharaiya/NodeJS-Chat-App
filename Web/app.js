var express = require('express');
var app = express();

var http = require('http');
var server = http.createServer(app);
var io = require('socket.io').listen(server);
var body_parser = require('body-parser');

// app.set('view engine','ejs');

app.use(express.static(__dirname));
// app.use(express.static('./public'));

server.listen(3000);

const users = {}
io.on('connection', socket => {
    socket.on('new-user', name => {
        users[socket.id] = name
        socket.broadcast.emit('user-connected', name)        
        console.log(users[socket.id])
    })
    console.log("New user")    
    // socket.emit('chat-message', 'Hello World')
    socket.on('send-chat-message', msg =>{
        socket.broadcast.emit('chat-message',{msg: msg, name: users[socket.id]})        
    })
})