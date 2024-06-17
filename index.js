var express = require("express");
var app = express();
var connection = require('./database')

app.get('/', function(req, res){
    let sql= "SELECT * FROM USER_INFO"
    connection.query(sql, function(err, result){
        if(err) throw err;
        res.send(result);
    })
})

app.listen(4000, function(){
    console.log('App listening on port 4000');
    connection.connect(function(err){
        if(err) throw err;
        console.log('Database connected!');
    })
});

