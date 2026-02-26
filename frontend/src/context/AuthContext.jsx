import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { authLogin, authLogout, authMe } from '../services/api';

/**
 * AuthContext : gère l'état global d'authentification.
 * Expose : user, loading, login(), logout()
 */
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true); // vrai au démarrage

    // Au montage, vérifier si une session active existe côté serveur
    useEffect(() => {
        authMe()
            .then(res => {
                if (res.data.authenticated) {
                    setUser({
                        id: res.data.id,
                        nom: res.data.nom,
                        email: res.data.email,
                        role: res.data.role,
                        ressourceId: res.data.ressourceId,
                    });
                }
            })
            .catch(() => {
                // 401 → non connecté, c'est normal
                setUser(null);
            })
            .finally(() => setLoading(false));
    }, []);

    /**
     * Tente de connecter l'utilisateur.
     * @returns {Promise<{success, user?, message?}>}
     */
    const login = useCallback(async (email, password) => {
        try {
            const res = await authLogin(email, password);
            const data = res.data;
            if (data.success) {
                const u = {
                    id: data.id,
                    nom: data.nom,
                    email: data.email,
                    role: data.role,
                    ressourceId: data.ressourceId,
                };
                setUser(u);
                return { success: true, user: u };
            }
            return { success: false, message: data.message || 'Échec de la connexion' };
        } catch (err) {
            const msg = err.response?.data?.message || 'Email ou mot de passe incorrect';
            return { success: false, message: msg };
        }
    }, []);

    /**
     * Déconnecte l'utilisateur.
     */
    const logout = useCallback(async () => {
        try {
            await authLogout();
        } catch (_) {
            // On continue même si le serveur échoue
        } finally {
            setUser(null);
        }
    }, []);

    return (
        <AuthContext.Provider value={{ user, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

/** Hook pratique pour accéder au contexte */
export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be used inside <AuthProvider>');
    return ctx;
}
