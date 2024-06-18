document.addEventListener("DOMContentLoaded", function() {
    fetch('/api/users')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            console.log('Fetched data:', data); // Debug log to see the fetched data
            const userList = document.getElementById('users');
            userList.innerHTML = ''; // Clear any existing content
            data.forEach(user => {
                const li = document.createElement('li');
                li.textContent = `Name: ${user.full_name}, Email: ${user.email}, Role: ${user.role}`;
                userList.appendChild(li);
            });
        })
        .catch(error => {
            console.error('Error fetching user data:', error);
        });
});
