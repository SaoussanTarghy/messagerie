import api from './api';

// Get planifications for a specific week
export const getPlanificationsParSemaine = async (weekOffset = 0) => {
    try {
        const response = await api.get(`/planifications/semaine`, {
            params: { offset: weekOffset }
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching planifications:', error);
        throw error;
    }
};

// Get all planifications
export const getAllPlanifications = async () => {
    try {
        const response = await api.get('/planifications');
        return response.data;
    } catch (error) {
        console.error('Error fetching planifications:', error);
        throw error;
    }
};

// Get single planification by ID
export const getPlanificationById = async (id) => {
    try {
        const response = await api.get(`/planifications/${id}`);
        return response.data;
    } catch (error) {
        console.error('Error fetching planification:', error);
        throw error;
    }
};

// Create new planification
export const createPlanification = async (planificationData) => {
    try {
        const response = await api.post('/planifications', planificationData);
        return response.data;
    } catch (error) {
        console.error('Error creating planification:', error);
        throw error;
    }
};

// Update planification
export const updatePlanification = async (id, planificationData) => {
    try {
        const response = await api.put(`/planifications/${id}`, planificationData);
        return response.data;
    } catch (error) {
        console.error('Error updating planification:', error);
        throw error;
    }
};

// Delete planification
export const deletePlanification = async (id) => {
    try {
        const response = await api.delete(`/planifications/${id}`);
        return response.data;
    } catch (error) {
        console.error('Error deleting planification:', error);
        throw error;
    }
};

// Check for conflicts when creating/updating planification
export const checkConflict = async (conflictData) => {
    try {
        const response = await api.post('/planifications/check-conflict', conflictData);
        return response.data;
    } catch (error) {
        console.error('Error checking conflict:', error);
        throw error;
    }
};
