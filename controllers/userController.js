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
    const { full_name, email, password, phone_no, profile_photo } = req.body;
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
            profile_photo: profile_photo,
            phone_no: phone_no,
            password: hash // Store the hashed password
        });
        // Save the new user to the database
        User.create(newUser, function(err, user) {
            if (err) {
                console.error('Error registering user:', err);
                res.status(500).send('Error registering user');
            } else {
                console.log('User registered successfully:', user);
                res.redirect('/login');
            }
        });
    });
};

exports.loginUser = function (req, res) {
    const { email, password } = req.body;

    User.findByEmail(email, function (err, user) {
        if (err) {
            console.error('Error fetching user:', err);
            res.status(500).send('Error logging in');
            return;
        }
        if (!user) {
            res.status(401).send('Invalid email or password');
            return;
        }
        bcrypt.compare(password, user.password, function (err, isMatch) {
            if (err) {
                console.error('Error comparing passwords:', err);
                res.status(500).send('Error logging in');
                return;
            }
            if (isMatch) {
                console.log('User logged in successfully:', user);
                res.cookie('userId', user._id, { httpOnly: true, secure: true, sameSite: 'Strict' });
                res.redirect('/index');
            } else {
                res.status(401).send('Invalid email or password');
            }
        });
    });
};