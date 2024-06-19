var express = require('express');
var router = express.Router();
var userController = require('../controllers/userController');

router.get('/users', userController.getAllUsers);

router.post('/register', userController.registerUser);


module.exports = router;
