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

/*
app.get('/', function(req, res) {
    res.sendFile(path.join(__dirname, 'views', 'index.html'));
});
*/

app.get('/', function(req, res) {
    res.redirect('/register');
});
// Route to serve the registration form
app.get('/register', function(req, res) {
    res.sendFile(path.join(__dirname, 'views', 'register.html'));
});

app.get('/index', function(req, res) {
    res.sendFile(path.join(__dirname, 'views', 'index.html'));
});

app.post('/register', function(req, res) {
    const { full_name, email, password } = req.body;
    // Hash the password
    bcrypt.hash(password, 10, function(err, hash) {
        if (err) {
            console.error('Error hashing password:', err);
            res.status(500).send('Error registering user');
            return;
        }
        // Create a new user object
        var newUser = new User({
            full_name: full_name,
            email: email,
            password: hash // Store the hashed password
        });
        // Save the new user to the database
        User.create(newUser, function(err, user) {
            if (err) {
                console.error('Error registering user:', err);
                res.status(500).send('Error registering user');
            } else {
                console.log('User registered successfully:', user);
                res.redirect('/index'); // Redirect to index.html after successful registration
            }
        });
    });
})
// Use the user routes
app.use('/api/users', userRoutes);

app.listen(4000, function() {
    console.log('App listening on port 4000');
    connection.connect(function(err) {
        if (err) throw err;
        console.log('Database connected!');
    });
});
