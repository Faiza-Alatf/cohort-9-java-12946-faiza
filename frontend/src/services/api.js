import axios from "axios";

const api = axios.create({
baseURL: "http://localhost:8080/api",

headers: {
"Content-Type": "application/json",
},

// Allow browser to send HttpOnly JWT cookie
withCredentials: true,
});

export default api;
