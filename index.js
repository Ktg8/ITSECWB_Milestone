var express = require('express');
var path = require('path');
var app = express();
var userRoutes = require('./routes/userRoutes');
var connection = require('./database');

// Serve static files from the 'public' directory
app.use(express.static(path.join(__dirname, 'public')));

// Route to serve the index.html file
app.get('/', function(req, res) {
    res.sendFile(path.join(__dirname, 'views', 'index.html'));
});

// Use the user routes
app.use('/api/users', userRoutes);

app.listen(4000, function() {
    console.log('App listening on port 4000');
    connection.connect(function(err) {
        if (err) throw err;
        console.log('Database connected!');
    });
});
