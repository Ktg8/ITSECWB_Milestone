var connection = require('../database');

var User = function(user) {
    this.id = user.id;
    this.full_name = user.full_name;
    this.email = user.email;
    this.role = user.role || "user";
    this.password = user.password
};

User.getAll = function(result) {
    connection.query("SELECT * FROM USER_INFO", function(err, res) {
        if (err) {
            console.error('Error executing query:', err); // Log error
            result(err, null);
        } else {
            console.log('Query result:', res); // Log query result
            result(null, res);
        }
    });
};

User.create = function(newUser, result) {
    connection.query("INSERT INTO USER_INFO SET ?", newUser, function(err, res) {
        if (err) {
            console.error('Error creating user:', err);
            result(err, null);
        } else {
            console.log('User created successfully');
            result(null, { id: res.insertId, ...newUser });
        }
    });
};


module.exports = User;
