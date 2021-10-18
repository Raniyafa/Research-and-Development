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

        spriteLocations.push(new drawLocations(data.x, data.y));
        socket.broadcast.emit('updateCanvas', spriteLocations);
    });
   
    
    socket.broadcast.emit('newPlayer', {id: socket.id });
    socket.on('disconnect', function(){
        console.log("Player disconnected");
    });
});

function drawLocations(x, y){
    this.x = x;
    this.y = y;
}
