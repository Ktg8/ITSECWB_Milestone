document.addEventListener("DOMContentLoaded", function () {
  const registerForm = document.getElementById("register-form");
  registerForm.addEventListener("submit", function (event) {
    event.preventDefault(); 

    const formData = new FormData(registerForm);
    fetch("/api/register", {
      method: "POST",
      body: formData,
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok " + response.statusText);
        }
        return response.json();
      })
      .then((data) => {
        console.log("User registered successfully:", data);
      })
      .catch((error) => {
        console.error("Error registering user:", error);
      });
  });
});
