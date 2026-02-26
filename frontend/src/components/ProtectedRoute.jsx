import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Composant de route protégée.
 *
 * Props :
 *   children   — le composant à rendre si autorisé
 *   roles      — tableau de rôles autorisés (ex: ['ADMIN', 'ENSEIGNANT'])
 *                Si omis, toute session valide est acceptée.
 */
export default function ProtectedRoute({ children, roles }) {
    const { user, loading } = useAuth();
    const location = useLocation();

    // Pendant la vérification de session
    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="flex flex-col items-center gap-4">
                    <div className="spinner" />
                    <p className="text-gray-500">Vérification de la session...</p>
                </div>
            </div>
        );
    }

    // Non authentifié → vers /login
    if (!user) {
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    // Rôle insuffisant → vers /unauthorized
    if (roles && !roles.includes(user.role)) {
        return <Navigate to="/unauthorized" replace />;
    }

    return children;
}
