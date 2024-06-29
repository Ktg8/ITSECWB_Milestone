const Post = require('../models/postModel');

// Get all posts
exports.getPosts = (req, res) => {
    Post.getAll((err, data) => {
        if (err)
            res.status(500).send({
                message: err.message || "Some error occurred while retrieving posts."
            });
        else res.send(data);
    });
};

// Create a new post
exports.createPost = (req, res) => {
    if (!req.body) {
        res.status(400).send({
            message: "Content can not be empty!"
        });
    }

    const post = new Post({
        title: req.body.title,
        content: req.body.content,
        user_id: req.body.user_id
    });

    Post.create(post, (err, data) => {
        if (err)
            res.status(500).send({
                message: err.message || "Some error occurred while creating the post."
            });
        else res.send(data);
    });
};

// Find a post by ID
exports.findPostById = (req, res) => {
    Post.findById(req.params.postId, (err, data) => {
        if (err) {
            if (err.kind === "not_found") {
                res.status(404).send({
                    message: `Not found post with id ${req.params.postId}.`
                });
            } else {
                res.status(500).send({
                    message: "Error retrieving post with id " + req.params.postId
                });
            }
        } else res.send(data);
    });
};

// Update a post
exports.updatePost = (req, res) => {
    if (!req.body) {
        res.status(400).send({
            message: "Content can not be empty!"
        });
    }

    Post.update(
        req.params.postId,
        new Post(req.body),
        (err, data) => {
            if (err) {
                if (err.kind === "not_found") {
                    res.status(404).send({
                        message: `Not found post with id ${req.params.postId}.`
                    });
                } else {
                    res.status(500).send({
                        message: "Error updating post with id " + req.params.postId
                    });
                }
            } else res.send(data);
        }
    );
};

// Delete a post
exports.deletePost = (req, res) => {
    Post.remove(req.params.postId, (err, data) => {
        if (err) {
            if (err.kind === "not_found") {
                res.status(404).send({
                    message: `Not found post with id ${req.params.postId}.`
                });
            } else {
                res.status(500).send({
                    message: "Could not delete post with id " + req.params.postId
                });
            }
        } else res.send({ message: `Post was deleted successfully!` });
    });
};
