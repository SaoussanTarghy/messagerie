<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="beans.Ressource" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Détails de la Ressource - Planification Académique</title>
            <link rel="stylesheet" href="../css/style.css">
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
                            <li><a href="../index.jsp">Accueil</a></li>
                            <li><a href="../ressources?action=list" class="active">Ressources</a></li>
                            <li><a href="../ressources?action=add" class="btn-primary">+ Nouvelle Ressource</a></li>
                        </ul>
                    </div>
                </nav>

                <div class="container main-content">
                    <% Ressource ressource=(Ressource) request.getAttribute("ressource"); if (ressource==null) { %>
                        <div class="alert alert-error">
                            Ressource introuvable !
                        </div>
                        <a href="../ressources?action=list" class="btn-secondary">← Retour à la liste</a>
                        <% } else { %>
                            <div class="page-header">
                                <h2>📄 Détails de la Ressource</h2>
                                <div class="header-actions">
                                    <a href="../ressources?action=edit&id=<%= ressource.getId() %>"
                                        class="btn-primary">✏️ Modifier</a>
                                    <a href="../ressources?action=list" class="btn-secondary">← Retour</a>
                                </div>
                            </div>

                            <div class="detail-card glass-card">
                                <div class="detail-header">
                                    <div class="detail-title">
                                        <h3>
                                            <%= ressource.getNom() %>
                                        </h3>
                                        <span class="badge badge-<%= ressource.getType().toLowerCase() %> badge-large">
                                            <%= ressource.getType() %>
                                        </span>
                                    </div>
                                    <div class="detail-status">
                                        <% if (ressource.isDisponibilite()) { %>
                                            <span class="status-indicator status-available">
                                                <span class="status-dot"></span>
                                                Disponible
                                            </span>
                                            <% } else { %>
                                                <span class="status-indicator status-unavailable">
                                                    <span class="status-dot"></span>
                                                    Indisponible
                                                </span>
                                                <% } %>
                                    </div>
                                </div>

                                <div class="detail-grid">
                                    <div class="detail-item">
                                        <div class="detail-label">🆔 Identifiant</div>
                                        <div class="detail-value"><strong>#<%= ressource.getId() %></strong></div>
                                    </div>

                                    <div class="detail-item">
                                        <div class="detail-label">📝 Nom complet</div>
                                        <div class="detail-value">
                                            <%= ressource.getNom() %>
                                        </div>
                                    </div>

                                    <div class="detail-item">
                                        <div class="detail-label">🏷️ Type de ressource</div>
                                        <div class="detail-value">
                                            <span class="badge badge-<%= ressource.getType().toLowerCase() %>">
                                                <% String typeLabel="" ; switch(ressource.getType()) { case "ENSEIGNANT"
                                                    : typeLabel="👨‍🏫 Enseignant" ; break; case "SALLE" :
                                                    typeLabel="🏫 Salle" ; break; case "COURS" : typeLabel="📚 Cours" ;
                                                    break; } %>
                                                    <%= typeLabel %>
                                            </span>
                                        </div>
                                    </div>

                                    <div class="detail-item">
                                        <div class="detail-label">✅ Disponibilité</div>
                                        <div class="detail-value">
                                            <% if (ressource.isDisponibilite()) { %>
                                                <span class="badge badge-success">✓ Disponible</span>
                                                <% } else { %>
                                                    <span class="badge badge-error">✗ Indisponible</span>
                                                    <% } %>
                                        </div>
                                    </div>

                                    <div class="detail-item full-width">
                                        <div class="detail-label">📋 Description</div>
                                        <div class="detail-value description-box">
                                            <%= ressource.getDescription() !=null &&
                                                !ressource.getDescription().isEmpty() ? ressource.getDescription()
                                                : "<em>Aucune description fournie</em>" %>
                                        </div>
                                    </div>

                                    <div class="detail-item">
                                        <div class="detail-label">📅 Date de création</div>
                                        <div class="detail-value">
                                            <%= new java.text.SimpleDateFormat("dd MMMM yyyy 'à' HH:mm",
                                                java.util.Locale.FRENCH) .format(ressource.getDateCreation()) %>
                                        </div>
                                    </div>

                                    <div class="detail-item">
                                        <div class="detail-label">🔄 Dernière modification</div>
                                        <div class="detail-value">
                                            <%= new java.text.SimpleDateFormat("dd MMMM yyyy 'à' HH:mm",
                                                java.util.Locale.FRENCH) .format(ressource.getDateModification()) %>
                                        </div>
                                    </div>
                                </div>

                                <!-- Actions -->
                                <div class="detail-actions">
                                    <a href="../ressources?action=edit&id=<%= ressource.getId() %>"
                                        class="btn-large btn-primary">✏️ Modifier cette ressource</a>
                                    <a href="../ressources?action=delete&id=<%= ressource.getId() %>"
                                        class="btn-large btn-danger"
                                        onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette ressource ?');">
                                        🗑️ Supprimer
                                    </a>
                                    <a href="../ressources?action=list" class="btn-large btn-secondary">← Retour à la
                                        liste</a>
                                </div>
                            </div>
                            <% } %>
                </div>

                <!-- Footer -->
                <footer class="footer">
                    <div class="container">
                        <p>&copy; 2026 Système de Planification Académique v1.0.0</p>
                    </div>
                </footer>
            </div>
        </body>

        </html>