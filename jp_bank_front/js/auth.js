const API_BASE = "http://localhost:8080";

function saveToken(token) {
    localStorage.setItem("jwt", token);
}

function getToken() {
    return localStorage.getItem("jwt");
}

function clearToken() {
    localStorage.removeItem("jwt");
}

function logout() {
    clearToken();
    window.location.href = "login.html";
}

async function authFetch(url, options = {}) {
    const token = getToken();

    const headers = options.headers || {};
    headers["Authorization"] = "Bearer " + token;

    return fetch(url, {
        ...options,
        headers
    });
}