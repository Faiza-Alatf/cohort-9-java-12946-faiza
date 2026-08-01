import axios from "axios";

const api = axios.create({
baseURL: "http://localhost:8080/api",

headers: {
"Content-Type": "application/json",
},

// Allow browser to send HttpOnly JWT cookie
withCredentials: true,

// Abort requests that take longer than 10 seconds
timeout: 10000,
});

export default api;
