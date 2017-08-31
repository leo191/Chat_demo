
var app =require('express')();
var http =require('http').Server(app);
var io = require('socket.io')(http);
var mongoose = require('mongoose');
app.get('/',function(res,res){
        res.sendFile(__dirname+'/index.html');
})


users = [];
var sockets = {};


mongoose.connect('mongodb://leo191:leo@ds149433.mlab.com:49433/abchats',function(err)
{
    if(err)
    {

    }
    else{
    console.log("Success");
    }
});





var userSchema = mongoose.Schema({


apertmentID: String,
userID: String,
userName: String,

});


var messageSchema = mongoose.Schema({
userID: String,
message: String,
created: {type:Date,
            default:Date.Now}
});


var chatSchema = mongoose.Schema({

userID: String,
toWhom: String,
fromWho: String

});




//var ChatModel = mongoose.model('Message',chatschema);




io.on('connection', function(socket){
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

        }
      });


       socket.on('message', function(data,to){
            //Send message to everyone



              console.log(to+' '+ sockets[to]);
            if(sockets[to])
             {
             sockets[to].emit('message', {message:data});
             }
             //online or not still gonna send message
              /*var newMsg = new ChatModel({msg: data, nick:to});
                          newMsg.save(function(err)
                          {
                             if(err) throw err;
                          });*/



        });

      var mainroom="";

      socket.on('create', function(room){
        mainroom=room;
        console.log(mainroom);
      socket.join(room);

      })

      socket.on('messageToRoom', function(data){
            //Send message to everyone


            socket.to(mainroom).emit("messageToRoom",{message:data});

            /*Object.keys(io.sockets.sockets).forEach(function(id){
                  if(id != socket.id)
                  {
                  io.to(id).emit('messageToRoom',{message:data});
                  }
            });*/


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
});

http.listen(3000,function(){
console.log('server listening port:3000')
})



