import axios from 'axios';

// Base URL for API - adjust based on your deployment
const API_BASE_URL = 'http://localhost:8080/PlanificationAcademique/api';

// Create axios instance with default config
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add response interceptor for error handling
api.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('API Error:', error);
        return Promise.reject(error);
    }
);

export default api;
