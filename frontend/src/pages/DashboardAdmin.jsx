import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import {
    ShieldCheck, BarChart2, Users, BookOpen, Building2,
    Calendar, Plus, Trash2, Pencil, AlertCircle, ChevronRight
} from 'lucide-react';

/**
 * Dashboard complet pour ADMIN et ENSEIGNANT.
 * Accès CRUD sur les ressources, toutes les planifications, statistiques.
 */
export default function DashboardAdmin() {
    const { user } = useAuth();
    const [ressources, setRessources] = useState([]);
    const [planifications, setPlanifications] = useState([]);
    const [stats, setStats] = useState({ enseignants: 0, salles: 0, cours: 0 });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [activeTab, setActiveTab] = useState('overview');
    const [deleteMsg, setDeleteMsg] = useState('');

    useEffect(() => {
        fetchAll();
    }, []);

    const fetchAll = async () => {
        try {
            setLoading(true);
            const [resR, resP] = await Promise.all([
                api.get('/ressources'),
                api.get('/planifications'),
            ]);
            const rs = resR.data || [];
            const ps = resP.data || [];
            setRessources(rs);
            setPlanifications(ps);
            setStats({
                enseignants: rs.filter(r => r.type === 'ENSEIGNANT').length,
                salles: rs.filter(r => r.type === 'SALLE').length,
                cours: rs.filter(r => r.type === 'COURS').length,
            });
        } catch (err) {
            setError('Impossible de charger les données.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Supprimer cette ressource ?')) return;
        try {
            await api.delete(`/ressources/${id}`);
            setDeleteMsg('Ressource supprimée.');
            fetchAll();
        } catch {
            setDeleteMsg('Erreur lors de la suppression.');
        }
        setTimeout(() => setDeleteMsg(''), 3000);
    };

    const tabs = [
        { id: 'overview', label: '📊 Vue d\'ensemble' },
        { id: 'ressources', label: '📋 Ressources' },
        { id: 'planifications', label: '📅 Planifications' },
    ];

    return (
        <div className="dashboard-page">
            {/* Hero */}
            <div className="dashboard-hero dashboard-hero-admin">
                <div className="dashboard-hero-content">
                    <div className="dashboard-hero-icon">
                        <ShieldCheck size={40} />
                    </div>
                    <div>
                        <h1 className="dashboard-hero-title">
                            Tableau de bord — {user?.nom?.split(' ')[0]} 👋
                        </h1>
                        <p className="dashboard-hero-sub">Gestion complète du système académique</p>
                    </div>
                </div>
                <div className="dashboard-role-badge dashboard-role-admin">
                    <ShieldCheck size={14} />
                    <span>{user?.role}</span>
                </div>
            </div>

            <div className="dashboard-body">
                {/* Stats */}
                <div className="stats-grid">
                    <div className="stat-card stat-purple">
                        <Users size={28} />
                        <div>
                            <span className="stat-number">{stats.enseignants}</span>
                            <span className="stat-label">Enseignants</span>
                        </div>
                    </div>
                    <div className="stat-card stat-blue">
                        <Building2 size={28} />
                        <div>
                            <span className="stat-number">{stats.salles}</span>
                            <span className="stat-label">Salles</span>
                        </div>
                    </div>
                    <div className="stat-card stat-green">
                        <BookOpen size={28} />
                        <div>
                            <span className="stat-number">{stats.cours}</span>
                            <span className="stat-label">Cours</span>
                        </div>
                    </div>
                    <div className="stat-card stat-orange">
                        <Calendar size={28} />
                        <div>
                            <span className="stat-number">{planifications.length}</span>
                            <span className="stat-label">Planifications</span>
                        </div>
                    </div>
                </div>

                {/* Onglets */}
                <div className="admin-tabs">
                    {tabs.map(t => (
                        <button
                            key={t.id}
                            className={`admin-tab-btn ${activeTab === t.id ? 'active' : ''}`}
                            onClick={() => setActiveTab(t.id)}
                        >
                            {t.label}
                        </button>
                    ))}
                </div>

                {/* Notifications */}
                {deleteMsg && (
                    <div className={`admin-msg ${deleteMsg.startsWith('Erreur') ? 'admin-msg-error' : 'admin-msg-success'}`}>
                        {deleteMsg}
                    </div>
                )}
                {error && (
                    <div className="error-state">
                        <AlertCircle size={18} />
                        <span>{error}</span>
                    </div>
                )}

                {loading && (
                    <div className="loading-state">
                        <div className="spinner" />
                        <p>Chargement des données...</p>
                    </div>
                )}

                {/* === Onglet : Vue d'ensemble === */}
                {!loading && activeTab === 'overview' && (
                    <div className="section-card">
                        <h2 className="section-title"><BarChart2 size={20} /> Résumé</h2>
                        <div className="overview-grid">
                            <div className="overview-item">
                                <strong>Total ressources :</strong>
                                <span>{ressources.length}</span>
                            </div>
                            <div className="overview-item">
                                <strong>Planifications actives :</strong>
                                <span>{planifications.filter(p => p.statut === 'PLANIFIE' || p.statut === 'EN_COURS').length}</span>
                            </div>
                            <div className="overview-item">
                                <strong>Planifications terminées :</strong>
                                <span>{planifications.filter(p => p.statut === 'TERMINE').length}</span>
                            </div>
                            <div className="overview-item">
                                <strong>Ressources indisponibles :</strong>
                                <span>{ressources.filter(r => !r.disponibilite).length}</span>
                            </div>
                        </div>
                        <div className="quick-actions">
                            <Link to="/calendar" className="quick-action-link">
                                <Calendar size={18} /> Voir le calendrier <ChevronRight size={14} />
                            </Link>
                        </div>
                    </div>
                )}

                {/* === Onglet : Ressources === */}
                {!loading && activeTab === 'ressources' && (
                    <div className="section-card">
                        <div className="section-header">
                            <h2 className="section-title"><BookOpen size={20} /> Gestion des ressources</h2>
                            <a
                                href="http://localhost:8080/PlanificationAcademique/ressources?action=add"
                                className="btn-add"
                                target="_blank"
                                rel="noreferrer"
                            >
                                <Plus size={16} /> Ajouter
                            </a>
                        </div>

                        {ressources.length === 0 ? (
                            <p className="empty-text">Aucune ressource.</p>
                        ) : (
                            <div className="admin-table-wrapper">
                                <table className="admin-table">
                                    <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>Nom</th>
                                            <th>Type</th>
                                            <th>Disponible</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {ressources.map(r => (
                                            <tr key={r.id}>
                                                <td className="td-id">{r.id}</td>
                                                <td>{r.nom}</td>
                                                <td>
                                                    <span className={`type-badge type-${r.type?.toLowerCase()}`}>
                                                        {r.type}
                                                    </span>
                                                </td>
                                                <td>
                                                    <span className={r.disponibilite ? 'dispo-yes' : 'dispo-no'}>
                                                        {r.disponibilite ? '✓' : '✗'}
                                                    </span>
                                                </td>
                                                <td className="td-actions">
                                                    <a
                                                        href={`http://localhost:8080/PlanificationAcademique/ressources?action=edit&id=${r.id}`}
                                                        className="action-btn action-edit"
                                                        target="_blank"
                                                        rel="noreferrer"
                                                        title="Modifier"
                                                    >
                                                        <Pencil size={14} />
                                                    </a>
                                                    <button
                                                        className="action-btn action-delete"
                                                        onClick={() => handleDelete(r.id)}
                                                        title="Supprimer"
                                                    >
                                                        <Trash2 size={14} />
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>
                )}

                {/* === Onglet : Planifications === */}
                {!loading && activeTab === 'planifications' && (
                    <div className="section-card">
                        <h2 className="section-title"><Calendar size={20} /> Toutes les planifications</h2>
                        {planifications.length === 0 ? (
                            <p className="empty-text">Aucune planification.</p>
                        ) : (
                            <div className="admin-table-wrapper">
                                <table className="admin-table">
                                    <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>Date / Heure</th>
                                            <th>Durée</th>
                                            <th>Notes</th>
                                            <th>Statut</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {planifications.map(p => (
                                            <tr key={p.id}>
                                                <td className="td-id">{p.id}</td>
                                                <td>
                                                    {new Date(p.dateHeure).toLocaleString('fr-FR', {
                                                        day: '2-digit', month: '2-digit', year: 'numeric',
                                                        hour: '2-digit', minute: '2-digit'
                                                    })}
                                                </td>
                                                <td>{p.duree} min</td>
                                                <td className="td-notes">{p.notes || '—'}</td>
                                                <td>
                                                    <span className={`status-badge badge-${p.statut?.toLowerCase()}`}>
                                                        {p.statut}
                                                    </span>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}
