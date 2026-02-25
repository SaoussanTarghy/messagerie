import api from './api';

// Get all ressources
export const getAllRessources = async () => {
    try {
        const response = await api.get('/ressources');
        return response.data;
    } catch (error) {
        console.error('Error fetching ressources:', error);
        throw error;
    }
};

// Get ressources by type (ENSEIGNANT, SALLE, COURS)
export const getRessourcesByType = async (type) => {
    try {
        const response = await api.get(`/ressources/type/${type}`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching ressources of type ${type}:`, error);
        throw error;
    }
};

// Get single ressource by ID
export const getRessourceById = async (id) => {
    try {
        const response = await api.get(`/ressources/${id}`);
        return response.data;
    } catch (error) {
        console.error('Error fetching ressource:', error);
        throw error;
    }
};

// Create new ressource
export const createRessource = async (ressourceData) => {
    try {
        const response = await api.post('/ressources', ressourceData);
        return response.data;
    } catch (error) {
        console.error('Error creating ressource:', error);
        throw error;
    }
};

// Update ressource
export const updateRessource = async (id, ressourceData) => {
    try {
        const response = await api.put(`/ressources/${id}`, ressourceData);
        return response.data;
    } catch (error) {
        console.error('Error updating ressource:', error);
        throw error;
    }
};

// Delete ressource
export const deleteRessource = async (id) => {
    try {
        const response = await api.delete(`/ressources/${id}`);
        return response.data;
    } catch (error) {
        console.error('Error deleting ressource:', error);
        throw error;
    }
};
