
var app =require('express')();
var http =require('http').Server(app);
var io = require('socket.io')(http);
app.get('/',function(res,res){
        res.sendFile(__dirname+'/index.html');
})


users = [];
var sockets = {};

io.on('connection',function(socket){
    console.log('User connected:'+ socket.id);

    socket.on('setUsername', function(data){
         console.log(data);
        if(users.indexOf(data) > -1){
          socket.emit('userExists', data + ' username is taken! Try some other username.');
        }
        else{
          sockets[data] = socket;
          users.push(data);
          socket.emit('userSet', {username: data, userslist: users});
    	  //socket.emit('useradded',users);
        }
      });


       socket.on('message', function(data,to){
            //Send message to everyone
              console.log(to+' '+sockets[to]);
            if(sockets[to])
             {sockets[to].emit('message', {message:data});}
        });

      socket.on('messgeToRoom', function(data){
            //Send message to everyone
            //console.log(to+' '+sockets[to]);

            //sockets[to].emit('message', data);

            Object.keys(io.sockets.sockets).forEach(function(id){
                  if(id != socket.id)
                  {
                  io.to(id).emit('messgeToRoom',{message:data})
                  }
            });


        });




  /*  socket.on('message',function(data){
           console.log(data);

            //socket.emit('message',{msg:data});
           Object.keys(io.sockets.sockets).forEach(function(Id){
               if(Id != socket.id)
               {
                     io.to(Id).emit('message',data);
               }
           })
    });*/
    socket.on('disconnect',function(){
    console.log("User disconnected" + socket.id);
    });
})

http.listen(3000,function(){
console.log('server listening port:3000')
})



