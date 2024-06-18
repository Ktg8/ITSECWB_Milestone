var mysql = require("mysql2");

var connection = mysql.createConnection({
    host: '127.0.0.1',
    database: 'user_database',
    user: 'root',
    password: 'root',
});

module.exports = connection;
