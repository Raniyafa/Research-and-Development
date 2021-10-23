var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var spriteLocations = [];

server.listen(8080, function(){
    console.log("Server is now running");
});

io.on('connection', function(socket){
    console.log("Player connected!");
    socket.emit('socketID', {id: socket.id });
    socket.on('sendCanvas', function(data){
        spriteLocations.push(new drawLocations(data.x, data.y, data.type, data.radius, data.width, data.height, data.corners, data.colour));
        socket.broadcast.emit('updateCanvas', spriteLocations);
    });
    socket.broadcast.emit('newPlayer', {id: socket.id });
    socket.on('disconnect', function(){
        console.log("Player disconnected");
    });
});

function drawLocations(x, y, type, radius, width, height, corners, colour){
    this.x = x;
    this.y = y;
    this.type = type;
    this.radius = radius;
    this.width = width;
    this.height = height;
    this.corners = corners;
    this.colour = colour;
}
