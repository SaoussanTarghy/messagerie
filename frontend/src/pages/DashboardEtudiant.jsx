import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import {
    GraduationCap, Calendar, BookOpen, Clock, ChevronRight,
    User, AlertCircle
} from 'lucide-react';

/**
 * Dashboard réservé aux ÉTUDIANTS.
 * Affiche uniquement les planifications liées à la ressource de l'étudiant.
 */
export default function DashboardEtudiant() {
    const { user } = useAuth();
    const [planifications, setPlanifications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);
                const res = await api.get('/planifications');
                const all = res.data || [];

                // Filtrer par ressourceId si l'étudiant en a un
                const filtered = user?.ressourceId
                    ? all.filter(p =>
                        p.enseignantId === user.ressourceId ||
                        p.salleId === user.ressourceId ||
                        p.coursId === user.ressourceId
                    )
                    : all;

                setPlanifications(filtered);
            } catch (err) {
                setError('Impossible de charger vos planifications.');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [user]);

    const statut_style = {
        'PLANIFIE': { label: '🗓 Planifié', cls: 'badge-planifie' },
        'EN_COURS': { label: '▶ En cours', cls: 'badge-encours' },
        'TERMINE': { label: '✓ Terminé', cls: 'badge-termine' },
        'ANNULE': { label: '✗ Annulé', cls: 'badge-annule' },
    };

    return (
        <div className="dashboard-page">
            {/* En-tête de bienvenue */}
            <div className="dashboard-hero dashboard-hero-student">
                <div className="dashboard-hero-content">
                    <div className="dashboard-hero-icon">
                        <GraduationCap size={40} />
                    </div>
                    <div>
                        <h1 className="dashboard-hero-title">
                            Bonjour, {user?.nom?.split(' ')[0] || 'Étudiant'} ! 👋
                        </h1>
                        <p className="dashboard-hero-sub">
                            Voici votre planning personnel
                        </p>
                    </div>
                </div>
                <div className="dashboard-role-badge">
                    <User size={14} />
                    <span>ÉTUDIANT</span>
                </div>
            </div>

            <div className="dashboard-body">
                {/* Stats rapides */}
                <div className="stats-grid">
                    <div className="stat-card stat-blue">
                        <Calendar size={28} />
                        <div>
                            <span className="stat-number">{planifications.length}</span>
                            <span className="stat-label">Cours planifiés</span>
                        </div>
                    </div>
                    <div className="stat-card stat-green">
                        <BookOpen size={28} />
                        <div>
                            <span className="stat-number">
                                {planifications.filter(p => p.statut === 'PLANIFIE').length}
                            </span>
                            <span className="stat-label">À venir</span>
                        </div>
                    </div>
                    <div className="stat-card stat-orange">
                        <Clock size={28} />
                        <div>
                            <span className="stat-number">
                                {planifications.filter(p => p.statut === 'EN_COURS').length}
                            </span>
                            <span className="stat-label">En cours</span>
                        </div>
                    </div>
                </div>

                {/* Liste des cours */}
                <div className="section-card">
                    <h2 className="section-title">
                        <Calendar size={20} />
                        Mes cours &amp; planifications
                    </h2>

                    {loading && (
                        <div className="loading-state">
                            <div className="spinner" />
                            <p>Chargement...</p>
                        </div>
                    )}

                    {error && (
                        <div className="error-state">
                            <AlertCircle size={20} />
                            <p>{error}</p>
                        </div>
                    )}

                    {!loading && !error && planifications.length === 0 && (
                        <div className="empty-state-card">
                            <GraduationCap size={48} className="empty-icon" />
                            <p>Aucune planification pour le moment.</p>
                        </div>
                    )}

                    {!loading && !error && planifications.length > 0 && (
                        <div className="planif-list">
                            {planifications.map(p => {
                                const st = statut_style[p.statut] || { label: p.statut, cls: '' };
                                return (
                                    <div key={p.id} className="planif-item">
                                        <div className="planif-item-left">
                                            <div className="planif-date">
                                                <span className="planif-day">
                                                    {new Date(p.dateHeure).toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' })}
                                                </span>
                                                <span className="planif-hour">
                                                    {p.heureDebut || new Date(p.dateHeure).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })}
                                                </span>
                                            </div>
                                            <div className="planif-info">
                                                <span className="planif-name">{p.coursNom || p.notes || 'Cours'}</span>
                                                <span className="planif-meta">
                                                    {p.enseignantNom && `👨‍🏫 ${p.enseignantNom}`}
                                                    {p.salleNom && ` · 📍 ${p.salleNom}`}
                                                    {p.duree && ` · ⏱ ${p.duree} min`}
                                                </span>
                                            </div>
                                        </div>
                                        <span className={`status-badge ${st.cls}`}>{st.label}</span>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>

                {/* Lien vers le calendrier */}
                <div className="quick-action-card">
                    <Link to="/calendar" className="quick-action-link">
                        <Calendar size={20} />
                        Voir le calendrier complet
                        <ChevronRight size={16} />
                    </Link>
                </div>
            </div>
        </div>
    );
}
