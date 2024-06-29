var connection = require('../database');

var Post = function(post) {
    this.id = post.id;
    this.title = post.title;
    this.content = post.content;
    this.user_id = post.user_id;
    this.created_at = post.created_at || new Date();
};

// Fetch all posts
Post.getAll = function(result) {
    connection.query("SELECT * FROM POSTS", function(err, res) {
        if (err) {
            console.error('Error fetching posts:', err);
            result(err, null);
        } else {
            result(null, res);
        }
    });
};

// Create a new post
Post.create = function(newPost, result) {
    connection.query("INSERT INTO POSTS SET ?", newPost, function(err, res) {
        if (err) {
            console.error('Error creating post:', err);
            result(err, null);
        } else {
            console.log('Post created successfully');
            result(null, { id: res.insertId, ...newPost });
        }
    });
};

// Find post by ID
Post.findById = function(postId, result) {
    connection.query("SELECT * FROM POSTS WHERE id = ?", [postId], function(err, res) {
        if (err) {
            console.error('Error finding post:', err);
            result(err, null);
        } else if (res.length > 0) {
            result(null, res[0]);
        } else {
            result(null, null);
        }
    });
};

// Update a post
Post.update = function(postId, updatedPost, result) {
    connection.query(
        "UPDATE POSTS SET title = ?, content = ? WHERE id = ?",
        [updatedPost.title, updatedPost.content, postId],
        function(err, res) {
            if (err) {
                console.error('Error updating post:', err);
                result(err, null);
            } else if (res.affectedRows == 0) {
                result({ kind: "not_found" }, null);
            } else {
                result(null, { id: postId, ...updatedPost });
            }
        }
    );
};

// Delete a post
Post.remove = function(postId, result) {
    connection.query("DELETE FROM POSTS WHERE id = ?", [postId], function(err, res) {
        if (err) {
            console.error('Error deleting post:', err);
            result(err, null);
        } else if (res.affectedRows == 0) {
            result({ kind: "not_found" }, null);
        } else {
            result(null, res);
        }
    });
};

module.exports = Post;
