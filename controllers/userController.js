var User = require('../models/userModel');

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
