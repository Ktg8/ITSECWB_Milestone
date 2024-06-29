var express = require('express');
var router = express.Router();
var userController = require('../controllers/userController');
const rateLimit = require('express-rate-limit');

const loginLimiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 5, // Limit each IP to 5 login requests per windowMs
    message: 'Too many login attempts from this IP, please try again after 15 minutes'
});


router.get('/users', userController.getAllUsers);

router.post('/register', userController.registerUser);

router.post('/login', loginLimiter, userController.loginUser);

module.exports = router;
