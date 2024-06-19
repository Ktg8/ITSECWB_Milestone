var User = require('../models/userModel');
var bcrypt = require('bcrypt');


exports.getAllUsers = function(req, res) {
    User.getAll(function(err, users) {
        if (err) {
            console.error('Error fetching users:', err); // Log error
            res.status(500).send(err);
        } else {
            console.log('Fetched users:', users); // Log fetched users
            res.json(users);
        }
    });
};

exports.registerUser = function(req, res) {
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
                res.redirect('/index.html'); // Redirect to index.html after successful registration
            }
        });
    });
};
