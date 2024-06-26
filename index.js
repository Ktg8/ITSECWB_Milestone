var express = require('express');
var path = require('path');
var app = express();
var userRoutes = require('./routes/userRoutes');
var connection = require('./database');
var bodyParser = require('body-parser');
var bcrypt = require('bcrypt');
var User = require('./models/userModel');

// Serve static files from the 'public' directory
app.use(express.static(path.join(__dirname, 'public')));
app.use(bodyParser.urlencoded({ extended: true }));

//page redirection
app.get('/', function(req, res) {
    res.redirect('/login');
});

// Route to serve the registration form
app.get('/register', function(req, res) {
    res.sendFile(path.join(__dirname, 'views', 'register.html'));
});

app.get('/login', function(req, res) {
    res.sendFile(path.join(__dirname, 'views', 'login.html'));
});

app.get('/index', function(req, res) {
    res.sendFile(path.join(__dirname, 'views', 'index.html'));
});

// Use the user routes / backend handling
app.use('/api', userRoutes);

app.listen(4000, function() {
    console.log('App listening on port 4000');
    connection.connect(function(err) {
        if (err) throw err;
        console.log('Database connected!');
    });
});
