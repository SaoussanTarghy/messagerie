<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="java.util.*, dao.RessourceDAO" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Planification Académique - Accueil</title>
            <link rel="stylesheet" href="css/style.css">
        </head>

        <body>
            <div class="page-wrapper">
                <!-- Navigation -->
                <nav class="navbar">
                    <div class="container">
                        <div class="nav-brand">
                            <h1>🎓 Planification Académique</h1>
                        </div>
                        <ul class="nav-menu">
                            <li><a href="index.jsp" class="active">Accueil</a></li>
                            <li><a href="ressources?action=list">Ressources</a></li>
                            <li><a href="ressources?action=add" class="btn-primary">+ Nouvelle Ressource</a></li>
                        </ul>
                    </div>
                </nav>

                <!-- Hero Section -->
                <section class="hero">
                    <div class="container">
                        <div class="hero-content">
                            <h2 class="animate-fade-in">Bienvenue sur le Système de Planification Académique</h2>
                            <p class="hero-subtitle animate-fade-in">
                                Gérez efficacement vos ressources académiques : enseignants, salles de classe et cours.
                                Notre système utilise des technologies Java EE avancées avec planification automatisée
                                via EJB Timer.
                            </p>
                            <div class="hero-actions">
                                <a href="<%=request.getContextPath()%>/calendrier" class="btn btn-primary">
                                    📅 Voir le Calendrier
                                </a>
                                <a href="<%=request.getContextPath()%>/ressources" class="btn btn-secondary">
                                    📋 Gérer les Ressources
                                </a>
                            </div>
                        </div>
                    </div>
                </section>

                <!-- Statistics Section -->
                <section class="statistics">
                    <div class="container">
                        <h3>Statistiques du Système</h3>
                        <div class="stats-grid">
                            <% try { RessourceDAO dao=new RessourceDAO(); int totalRessources=dao.countRessources(); int
                                nbEnseignants=dao.getRessourcesByType("ENSEIGNANT").size(); int
                                nbSalles=dao.getRessourcesByType("SALLE").size(); int
                                nbCours=dao.getRessourcesByType("COURS").size(); %>
                                <div class="stat-card glass-card">
                                    <div class="stat-icon">👨‍🏫</div>
                                    <div class="stat-number">
                                        <%= nbEnseignants %>
                                    </div>
                                    <div class="stat-label">Enseignants</div>
                                </div>

                                <div class="stat-card glass-card">
                                    <div class="stat-icon">🏫</div>
                                    <div class="stat-number">
                                        <%= nbSalles %>
                                    </div>
                                    <div class="stat-label">Salles</div>
                                </div>

                                <div class="stat-card glass-card">
                                    <div class="stat-icon">📚</div>
                                    <div class="stat-number">
                                        <%= nbCours %>
                                    </div>
                                    <div class="stat-label">Cours</div>
                                </div>

                                <div class="stat-card glass-card">
                                    <div class="stat-icon">📊</div>
                                    <div class="stat-number">
                                        <%= totalRessources %>
                                    </div>
                                    <div class="stat-label">Total Ressources</div>
                                </div>
                                <% } catch (Exception e) { out.println("<div class='alert alert-error'>Erreur de chargement des statistiques : " + e.getMessage() + "</div>"); } %>
                    </div>
            </div>
            </section>

            <!-- Features Section -->
            <section class="features">
                <div class="container">
                    <h3>Fonctionnalités Principales</h3>
                    <div class="features-grid">
                        <div class="feature-card glass-card">
                            <div class="feature-icon">⚡</div>
                            <h4>Gestion Complète CRUD</h4>
                            <p>Créez, lisez, modifiez et supprimez des ressources académiques en toute simplicité avec
                                une interface intuitive.</p>
                        </div>

                        <div class="feature-card glass-card">
                            <div class="feature-icon">⏰</div>
                            <h4>EJB Timer Automatisé</h4>
                            <p>Planification automatique toutes les 5 minutes pour vérifier les disponibilités et mettre
                                à jour les statuts.</p>
                        </div>

                        <div class="feature-card glass-card">
                            <div class="feature-icon">🔍</div>
                            <h4>Recherche Avancée</h4>
                            <p>Filtrez et recherchez des ressources par type, nom, ou description avec des résultats
                                instantanés.</p>
                        </div>

                        <div class="feature-card glass-card">
                            <div class="feature-icon">🗄️</div>
                            <h4>Architecture MVC + DAO</h4>
                            <p>Code structuré et professionnel utilisant les meilleurs pratiques Java EE et patterns de
                                conception.</p>
                        </div>

                        <div class="feature-card glass-card">
                            <div class="feature-icon">🎨</div>
                            <h4>Design Moderne</h4>
                            <p>Interface glassmorphism avec animations fluides, 100% responsive et optimisée pour tous
                                les appareils.</p>
                        </div>

                        <div class="feature-card glass-card">
                            <div class="feature-icon">🔒</div>
                            <h4>Sécurité Renforcée</h4>
                            <p>Protection contre l'injection SQL avec PreparedStatement et gestion complète des erreurs.
                            </p>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Footer -->
            <footer class="footer">
                <div class="container">
                    <p>&copy; 2026 Système de Planification Académique v1.0.0</p>
                    <p>Développé avec Java EE, JSP, Servlets, EJB Timer &amp; MySQL</p>
                </div>
            </footer>
            </div>
        </body>

        </html>