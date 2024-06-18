var connection = require('../database');

var User = function(user) {
    this.id = user.id;
    this.name = user.full_name;
    this.email = user.email;
    this.role = user.role
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

module.exports = User;
