const express = require('express');
const router = express.Router();
const postController = require('../controllers/postController');

router.get('/posts', postController.getPosts); // Get all posts
router.post('/posts', postController.createPost); // Create a new post
router.get('/posts/:postId', postController.findPostById); // Get a post by ID
router.put('/posts/:postId', postController.updatePost); // Update a post
router.delete('/posts/:postId', postController.deletePost); // Delete a post

module.exports = router;
