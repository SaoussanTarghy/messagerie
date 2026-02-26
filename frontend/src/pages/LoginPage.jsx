import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { BookOpen, Mail, Lock, AlertCircle, Eye, EyeOff } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

/**
 * Page de connexion.
 * Après succès, redirige vers le dashboard correspondant au rôle.
 */
export default function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPwd, setShowPwd] = useState(false);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const { login } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const redirectAfterLogin = (role) => {
        const from = location.state?.from?.pathname;
        if (from && from !== '/login') {
            navigate(from, { replace: true });
        } else if (role === 'ETUDIANT') {
            navigate('/dashboard/etudiant', { replace: true });
        } else {
            // ADMIN ou ENSEIGNANT
            navigate('/dashboard/admin', { replace: true });
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        const result = await login(email, password);
        setLoading(false);
        if (result.success) {
            redirectAfterLogin(result.user.role);
        } else {
            setError(result.message);
        }
    };

    return (
        <div className="login-page">
            {/* Arrière plan avec formes décoratives */}
            <div className="login-bg">
                <div className="login-blob login-blob-1" />
                <div className="login-blob login-blob-2" />
            </div>

            <div className="login-container">
                {/* Logo / Brand */}
                <div className="login-brand">
                    <div className="login-icon-box">
                        <BookOpen size={36} className="login-icon" />
                    </div>
                    <h1 className="login-title">Planification Académique</h1>
                    <p className="login-subtitle">Connectez-vous pour accéder à votre espace</p>
                </div>

                {/* Formulaire */}
                <form onSubmit={handleSubmit} className="login-form">
                    {error && (
                        <div className="login-error">
                            <AlertCircle size={16} />
                            <span>{error}</span>
                        </div>
                    )}

                    {/* Email */}
                    <div className="login-field">
                        <label htmlFor="email" className="login-label">
                            Adresse email
                        </label>
                        <div className="login-input-wrapper">
                            <Mail size={18} className="login-input-icon" />
                            <input
                                id="email"
                                type="email"
                                className="login-input"
                                placeholder="exemple@academie.ma"
                                value={email}
                                onChange={e => setEmail(e.target.value)}
                                required
                                autoFocus
                            />
                        </div>
                    </div>

                    {/* Mot de passe */}
                    <div className="login-field">
                        <label htmlFor="password" className="login-label">
                            Mot de passe
                        </label>
                        <div className="login-input-wrapper">
                            <Lock size={18} className="login-input-icon" />
                            <input
                                id="password"
                                type={showPwd ? 'text' : 'password'}
                                className="login-input"
                                placeholder="••••••••"
                                value={password}
                                onChange={e => setPassword(e.target.value)}
                                required
                            />
                            <button
                                type="button"
                                className="login-eye-btn"
                                onClick={() => setShowPwd(p => !p)}
                                tabIndex={-1}
                                aria-label={showPwd ? 'Masquer' : 'Afficher'}
                            >
                                {showPwd ? <EyeOff size={16} /> : <Eye size={16} />}
                            </button>
                        </div>
                    </div>

                    <button
                        type="submit"
                        className="login-btn"
                        disabled={loading}
                    >
                        {loading ? (
                            <span className="login-btn-loading">
                                <span className="spinner-sm" />
                                Connexion...
                            </span>
                        ) : (
                            'Se connecter'
                        )}
                    </button>
                </form>

                {/* Comptes de démo */}
                <div className="login-demo">
                    <p className="login-demo-title">Comptes de démonstration :</p>
                    <div className="login-demo-accounts">
                        <button
                            type="button"
                            className="login-demo-btn login-demo-admin"
                            onClick={() => { setEmail('admin@academie.ma'); setPassword('password123'); }}
                        >
                            👨‍💼 Admin
                        </button>
                        <button
                            type="button"
                            className="login-demo-btn login-demo-teacher"
                            onClick={() => { setEmail('alami@academie.ma'); setPassword('password123'); }}
                        >
                            👨‍🏫 Enseignant
                        </button>
                        <button
                            type="button"
                            className="login-demo-btn login-demo-student"
                            onClick={() => { setEmail('etudiant@academie.ma'); setPassword('password123'); }}
                        >
                            🎓 Étudiant
                        </button>
                    </div>
                    <p className="login-demo-hint">
                        Mot de passe de tous les comptes : <code>password123</code>
                    </p>
                </div>
            </div>
        </div>
    );
}
