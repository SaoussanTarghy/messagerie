import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Calendar, List, BookOpen, LogOut, User, ShieldCheck, GraduationCap } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
    const location = useLocation();
    const navigate = useNavigate();
    const { user, logout } = useAuth();

    const isActive = (path) => location.pathname.startsWith(path);

    const handleLogout = async () => {
        await logout();
        navigate('/login', { replace: true });
    };

    const roleIcon = user?.role === 'ETUDIANT'
        ? <GraduationCap size={16} />
        : <ShieldCheck size={16} />;

    const roleColor = user?.role === 'ETUDIANT' ? 'navbar-role-student' : 'navbar-role-admin';

    // Masquer la navbar sur la page de login
    if (location.pathname === '/login') return null;

    return (
        <nav className="navbar">
            <div className="container mx-auto px-6 py-4">
                <div className="flex items-center justify-between">
                    {/* Brand */}
                    <Link to="/" className="flex items-center gap-3 text-xl font-bold">
                        <BookOpen className="w-8 h-8" />
                        <span>Planification Académique</span>
                    </Link>

                    <div className="flex items-center gap-3">
                        {user ? (
                            <>
                                {/* Liens de navigation */}
                                <Link
                                    to="/calendar"
                                    className={`nav-link ${isActive('/calendar') ? 'active' : ''}`}
                                >
                                    <Calendar className="w-5 h-5" />
                                    <span>Calendrier</span>
                                </Link>

                                {/* Lien Dashboard selon rôle */}
                                {user.role === 'ETUDIANT' ? (
                                    <Link
                                        to="/dashboard/etudiant"
                                        className={`nav-link ${isActive('/dashboard/etudiant') ? 'active' : ''}`}
                                    >
                                        <GraduationCap className="w-5 h-5" />
                                        <span>Mon Planning</span>
                                    </Link>
                                ) : (
                                    <Link
                                        to="/dashboard/admin"
                                        className={`nav-link ${isActive('/dashboard/admin') ? 'active' : ''}`}
                                    >
                                        <ShieldCheck className="w-5 h-5" />
                                        <span>Administration</span>
                                    </Link>
                                )}

                                <Link
                                    to="/resources"
                                    className={`nav-link ${isActive('/resources') ? 'active' : ''}`}
                                >
                                    <List className="w-5 h-5" />
                                    <span>Ressources</span>
                                </Link>

                                {/* Séparateur */}
                                <div className="navbar-separator" />

                                {/* Profil utilisateur */}
                                <div className="navbar-user">
                                    <User size={16} />
                                    <span className="navbar-user-name">{user.nom}</span>
                                    <span className={`navbar-role-badge ${roleColor}`}>
                                        {roleIcon}
                                        {user.role}
                                    </span>
                                </div>

                                {/* Bouton déconnexion */}
                                <button
                                    onClick={handleLogout}
                                    className="navbar-logout-btn"
                                    title="Se déconnecter"
                                >
                                    <LogOut size={16} />
                                    <span>Déconnexion</span>
                                </button>
                            </>
                        ) : (
                            <Link to="/login" className="nav-link">
                                <User className="w-5 h-5" />
                                <span>Se connecter</span>
                            </Link>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    );
}
