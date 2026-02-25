<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="beans.Ressource" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <% Boolean isEdit=(Boolean) request.getAttribute("isEdit"); Ressource ressource=(Ressource)
                request.getAttribute("ressource"); boolean editMode=(isEdit !=null && isEdit==true); %>
                <title>
                    <%= editMode ? "Modifier" : "Ajouter" %> une Ressource - Planification Académique
                </title>
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
                        <h2>
                            <%= editMode ? "✏️ Modifier" : "➕ Ajouter" %> une Ressource
                        </h2>
                        <p class="page-subtitle">
                            <%= editMode ? "Modifiez les informations de la ressource"
                                : "Remplissez le formulaire pour créer une nouvelle ressource" %>
                        </p>
                    </div>

                    <!-- Messages d'erreur -->
                    <% String error=(String) request.getAttribute("error"); if (error !=null) { %>
                        <div class="alert alert-error">
                            ✗ <%= error %>
                        </div>
                        <% } %>

                            <!-- Formulaire -->
                            <div class="form-container glass-card">
                                <form action="../ressources" method="post" class="resource-form" id="resourceForm">
                                    <input type="hidden" name="action" value="save">
                                    <% if (editMode && ressource !=null) { %>
                                        <input type="hidden" name="id" value="<%= ressource.getId() %>">
                                        <% } %>

                                            <!-- Nom -->
                                            <div class="form-group">
                                                <label for="nom" class="form-label required">
                                                    📝 Nom de la ressource
                                                </label>
                                                <input type="text" id="nom" name="nom" class="form-input"
                                                    placeholder="Ex : Dr. Mohammed ALAMI"
                                                    value="<%= editMode && ressource != null ? ressource.getNom() : "" %>"
                                                    required maxlength="200">
                                                <small class="form-hint">Le nom complet de la ressource (maximum 200
                                                    caractères)</small>
                                            </div>

                                            <!-- Type -->
                                            <div class="form-group">
                                                <label for="type" class="form-label required">
                                                    🏷️ Type de ressource
                                                </label>
                                                <select id="type" name="type" class="form-select" required>
                                                    <option value="">-- Sélectionnez un type --</option>
                                                    <option value="ENSEIGNANT" <%=editMode && ressource !=null
                                                        && "ENSEIGNANT" .equals(ressource.getType()) ? "selected" : ""
                                                        %>>
                                                        👨‍🏫 Enseignant
                                                    </option>
                                                    <option value="SALLE" <%=editMode && ressource !=null && "SALLE"
                                                        .equals(ressource.getType()) ? "selected" : "" %>>
                                                        🏫 Salle
                                                    </option>
                                                    <option value="COURS" <%=editMode && ressource !=null && "COURS"
                                                        .equals(ressource.getType()) ? "selected" : "" %>>
                                                        📚 Cours
                                                    </option>
                                                </select>
                                                <small class="form-hint">Catégorie de la ressource</small>
                                            </div>

                                            <!-- Disponibilité -->
                                            <div class="form-group">
                                                <label class="form-label">
                                                    ✅ Disponibilité
                                                </label>
                                                <div class="checkbox-wrapper">
                                                    <input type="checkbox" id="disponibilite" name="disponibilite"
                                                        class="form-checkbox" <%=editMode && ressource !=null &&
                                                        ressource.isDisponibilite() ? "checked" : "" %>
                                                    <%= !editMode ? "checked" : "" %>>
                                                        <label for="disponibilite" class="checkbox-label">
                                                            Cette ressource est actuellement disponible
                                                        </label>
                                                </div>
                                                <small class="form-hint">Décochez si la ressource est temporairement
                                                    indisponible</small>
                                            </div>

                                            <!-- Description -->
                                            <div class="form-group">
                                                <label for="description" class="form-label">
                                                    📋 Description
                                                </label>
                                                <textarea id="description" name="description" class="form-textarea"
                                                    rows="5"
                                                    placeholder="Ajoutez une description détaillée de la ressource..."><%= editMode && ressource != null && ressource.getDescription() != null ? ressource.getDescription() : "" %></textarea>
                                                <small class="form-hint">Informations complémentaires (spécialités,
                                                    équipements, etc.)</small>
                                            </div>

                                            <!-- Boutons d'action -->
                                            <div class="form-actions">
                                                <button type="submit" class="btn-large btn-primary">
                                                    <%= editMode ? "💾 Enregistrer les modifications"
                                                        : "➕ Créer la ressource" %>
                                                </button>
                                                <a href="../ressources?action=list" class="btn-large btn-secondary">
                                                    ❌ Annuler
                                                </a>
                                            </div>
                                </form>
                            </div>

                            <!-- Info Box -->
                            <div class="info-box glass-card">
                                <h4>ℹ️ Informations importantes</h4>
                                <ul>
                                    <li>Les champs avec <span class="required-star">*</span> sont obligatoires</li>
                                    <li>Le nom de la ressource doit être unique et descriptif</li>
                                    <li>La disponibilité peut être modifiée à tout moment</li>
                                    <li>
                                        <%=editMode
                                            ? "Les dates de création et modification sont gérées automatiquement"
                                            : "La date de création sera enregistrée automatiquement" %>
                                    </li>
                                </ul>
                            </div>
                </div>

                <!-- Footer -->
                <footer class="footer">
                    <div class="container">
                        <p>&copy; 2026 Système de Planification Académique v1.0.0</p>
                    </div>
                </footer>
            </div>

            <!-- Validation JavaScript -->
            <script>
                document.getElementById('resourceForm').addEventListener('submit', function (e) {
                    const nom = document.getElementById('nom').value.trim();
                    const type = document.getElementById('type').value;

                    if (nom.length < 3) {
                        e.preventDefault();
                        alert('Le nom doit contenir au moins 3 caractères');
                        document.getElementById('nom').focus();
                        return false;
                    }

                    if (!type) {
                        e.preventDefault();
                        alert('Veuillez sélectionner un type de ressource');
                        document.getElementById('type').focus();
                        return false;
                    }

                    return true;
                });

                // Animation du formulaire
                document.addEventListener('DOMContentLoaded', function () {
                    const formGroups = document.querySelectorAll('.form-group');
                    formGroups.forEach((group, index) => {
                        group.style.opacity = '0';
                        group.style.transform = 'translateY(20px)';
                        setTimeout(() => {
                            group.style.transition = 'all 0.4s ease';
                            group.style.opacity = '1';
                            group.style.transform = 'translateY(0)';
                        }, index * 100);
                    });
                });
            </script>
        </body>

        </html>