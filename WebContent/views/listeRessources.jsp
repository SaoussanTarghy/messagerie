<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="java.util.*, beans.Ressource" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Liste des Ressources - Planification Académique</title>
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
                    <div class="page-header">
                        <h2>📋 Liste des Ressources</h2>
                        <p class="page-subtitle">Gérez toutes vos ressources académiques</p>
                    </div>

                    <!-- Messages de succès/erreur -->
                    <% String successMessage=(String) session.getAttribute("successMessage"); String
                        errorMessage=(String) session.getAttribute("errorMessage"); if (successMessage !=null) { %>
                        <div class="alert alert-success">
                            ✓ <%= successMessage %>
                        </div>
                        <% session.removeAttribute("successMessage"); } if (errorMessage !=null) { %>
                            <div class="alert alert-error">
                                ✗ <%= errorMessage %>
                            </div>
                            <% session.removeAttribute("errorMessage"); } %>

                                <!-- Filtres et recherche -->
                                <div class="filters-section glass-card">
                                    <div class="filters-row">
                                        <!-- Filtre par type -->
                                        <div class="filter-group">
                                            <label>Filtrer par type :</label>
                                            <div class="filter-buttons">
                                                <a href="../ressources?action=list"
                                                    class="filter-btn <%= request.getAttribute(" typeFiltre")==null
                                                    ? "active" : "" %>">
                                                    Tous
                                                </a>
                                                <a href="../ressources?action=list&type=ENSEIGNANT"
                                                    class="filter-btn <%= "
                                                    ENSEIGNANT".equals(request.getAttribute("typeFiltre")) ? "active"
                                                    : "" %>">
                                                    👨‍🏫 Enseignants
                                                </a>
                                                <a href="../ressources?action=list&type=SALLE" class="filter-btn <%= "
                                                    SALLE".equals(request.getAttribute("typeFiltre")) ? "active" : ""
                                                    %>">
                                                    🏫 Salles
                                                </a>
                                                <a href="../ressources?action=list&type=COURS" class="filter-btn <%= "
                                                    COURS".equals(request.getAttribute("typeFiltre")) ? "active" : ""
                                                    %>">
                                                    📚 Cours
                                                </a>
                                            </div>
                                        </div>

                                        <!-- Barre de recherche -->
                                        <div class="search-group">
                                            <form action="../ressources" method="get" class="search-form">
                                                <input type="hidden" name="action" value="search">
                                                <input type="text" name="keyword" placeholder="Rechercher..."
                                                    value="<%= request.getAttribute(" keyword") !=null ?
                                                    request.getAttribute("keyword") : "" %>"
                                                class="search-input">
                                                <button type="submit" class="btn-primary">🔍 Rechercher</button>
                                            </form>
                                        </div>
                                    </div>
                                </div>

                                <!-- Tableau des ressources -->
                                <% List<Ressource> ressources = (List<Ressource>) request.getAttribute("ressources");

                                        if (ressources == null || ressources.isEmpty()) {
                                        %>
                                        <div class="empty-state glass-card">
                                            <div class="empty-icon">📭</div>
                                            <h3>Aucune ressource trouvée</h3>
                                            <p>Commencez par ajouter votre première ressource !</p>
                                            <a href="../ressources?action=add" class="btn-large btn-primary">+ Ajouter
                                                une ressource</a>
                                        </div>
                                        <% } else { %>
                                            <div class="table-container glass-card">
                                                <table class="data-table">
                                                    <thead>
                                                        <tr>
                                                            <th>ID</th>
                                                            <th>Nom</th>
                                                            <th>Type</th>
                                                            <th>Disponibilité</th>
                                                            <th>Description</th>
                                                            <th>Date de création</th>
                                                            <th>Actions</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <% for (Ressource r : ressources) { %>
                                                            <tr>
                                                                <td><strong>#<%= r.getId() %></strong></td>
                                                                <td>
                                                                    <%= r.getNom() %>
                                                                </td>
                                                                <td>
                                                                    <span
                                                                        class="badge badge-<%= r.getType().toLowerCase() %>">
                                                                        <%= r.getType() %>
                                                                    </span>
                                                                </td>
                                                                <td>
                                                                    <% if (r.isDisponibilite()) { %>
                                                                        <span class="badge badge-success">✓
                                                                            Disponible</span>
                                                                        <% } else { %>
                                                                            <span class="badge badge-error">✗
                                                                                Indisponible</span>
                                                                            <% } %>
                                                                </td>
                                                                <td class="description-cell">
                                                                    <%= r.getDescription() !=null &&
                                                                        r.getDescription().length()> 60
                                                                        ? r.getDescription().substring(0, 60) + "..."
                                                                        : r.getDescription() %>
                                                                </td>
                                                                <td>
                                                                    <%= new java.text.SimpleDateFormat("dd/MM/yyyy
                                                                        HH:mm").format(r.getDateCreation()) %>
                                                                </td>
                                                                <td class="actions-cell">
                                                                    <a href="../ressources?action=view&id=<%= r.getId() %>"
                                                                        class="btn-icon" title="Voir détails">👁️</a>
                                                                    <a href="../ressources?action=edit&id=<%= r.getId() %>"
                                                                        class="btn-icon" title="Modifier">✏️</a>
                                                                    <a href="../ressources?action=delete&id=<%= r.getId() %>"
                                                                        class="btn-icon btn-danger"
                                                                        onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette ressource ?');"
                                                                        title="Supprimer">🗑️</a>
                                                                </td>
                                                            </tr>
                                                            <% } %>
                                                    </tbody>
                                                </table>
                                            </div>

                                            <div class="table-footer">
                                                <p><strong>
                                                        <%= ressources.size() %>
                                                    </strong> ressource(s) trouvée(s)</p>
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