import axios from 'axios';

// Base URL for API - adjust based on your deployment
const API_BASE_URL = 'http://localhost:8080/PlanificationAcademique/api';

// Create axios instance with default config
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true, // Envoie les cookies de session Java EE
});

// Add response interceptor for error handling
api.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('API Error:', error);
        return Promise.reject(error);
    }
);

// ==================== Auth API ====================

/** Connecte l'utilisateur et retourne son profil */
export const authLogin = (email, password) =>
    api.post('/auth/login', { email, password });

/** Déconnecte l'utilisateur et invalide la session */
export const authLogout = () =>
    api.post('/auth/logout');

/** Retourne le profil de l'utilisateur connecté (ou 401) */
export const authMe = () =>
    api.get('/auth/me');

export default api;
