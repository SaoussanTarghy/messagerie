<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="beans.Planification, beans.Ressource, java.util.*, java.sql.Timestamp, java.text.SimpleDateFormat"
        %>
        <% Planification planification=(Planification) request.getAttribute("planification");
            @SuppressWarnings("unchecked") List<Ressource> enseignants = (List<Ressource>)
                request.getAttribute("enseignants");
                @SuppressWarnings("unchecked")
                List<Ressource> salles = (List<Ressource>) request.getAttribute("salles");
                        @SuppressWarnings("unchecked")
                        List<Ressource> cours = (List<Ressource>) request.getAttribute("cours");

                                String erreur = (String) request.getAttribute("erreur");
                                String dateInitiale = (String) request.getAttribute("dateInitiale");
                                String heureInitiale = (String) request.getAttribute("heureInitiale");

                                boolean isEdit = (planification != null && planification.getId() > 0);
                                String action = isEdit ? "Modifier" : "Ajouter";

                                // Formater la date/heure si modification
                                String dateHeureValue = "";
                                if (isEdit && planification.getDateHeure() != null) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                dateHeureValue = sdf.format(planification.getDateHeure());
                                } else if (dateInitiale != null && heureInitiale != null) {
                                dateHeureValue = dateInitiale + "T" + heureInitiale;
                                }
                                %>
                                <!DOCTYPE html>
                                <html lang="fr">

                                <head>
                                    <meta charset="UTF-8">
                                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                    <title>
                                        <%=action%> une Planification - Planification Académique
                                    </title>
                                    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
                                </head>

                                <body>
                                    <!-- Navigation -->
                                    <nav class="navbar">
                                        <div class="nav-brand">
                                            <span class="icon">📚</span>
                                            <span class="brand-text">Planification Académique</span>
                                        </div>
                                        <div class="nav-links">
                                            <a href="<%=request.getContextPath()%>/calendrier" class="nav-link">📅
                                                Calendrier</a>
                                            <a href="<%=request.getContextPath()%>/ressources" class="nav-link">📋
                                                Ressources</a>
                                        </div>
                                    </nav>

                                    <div class="container"
                                        style="max-width: 800px; margin: 40px auto; padding: 0 20px;">
                                        <div class="card">
                                            <h1 style="color: var(--primary-color); margin-bottom: 30px;">
                                                <%=isEdit ? "✏️ Modifier" : "➕ Ajouter" %> une Planification
                                            </h1>

                                            <% if (erreur !=null) { %>
                                                <div class="error-message"
                                                    style="padding: 15px; background: #f8d7da; color: #721c24; border-radius: 8px; margin-bottom: 20px;">
                                                    ✗ <%=erreur%>
                                                </div>
                                                <% } %>

                                                    <form action="<%=request.getContextPath()%>/calendrier"
                                                        method="post" onsubmit="return validerFormulaire()">
                                                        <input type="hidden" name="action" value="sauvegarder">
                                                        <% if (isEdit) { %>
                                                            <input type="hidden" name="id"
                                                                value="<%=planification.getId()%>">
                                                            <% } %>

                                                                <!-- Enseignant -->
                                                                <div class="form-group">
                                                                    <label for="enseignantId">👨‍🏫 Enseignant *</label>
                                                                    <select id="enseignantId" name="enseignantId"
                                                                        required>
                                                                        <option value="">-- Sélectionner un enseignant
                                                                            --</option>
                                                                        <% for (Ressource e : enseignants) { %>
                                                                            <option value="<%=e.getId()%>" <%=isEdit &&
                                                                                planification.getEnseignantId()==e.getId()
                                                                                ? "selected" : "" %>>
                                                                                <%=e.getNom()%>
                                                                            </option>
                                                                            <% } %>
                                                                    </select>
                                                                </div>

                                                                <!-- Salle -->
                                                                <div class="form-group">
                                                                    <label for="salleId">🏫 Salle *</label>
                                                                    <select id="salleId" name="salleId" required>
                                                                        <option value="">-- Sélectionner une salle --
                                                                        </option>
                                                                        <% for (Ressource s : salles) { %>
                                                                            <option value="<%=s.getId()%>" <%=isEdit &&
                                                                                planification.getSalleId()==s.getId()
                                                                                ? "selected" : "" %>>
                                                                                <%=s.getNom()%>
                                                                            </option>
                                                                            <% } %>
                                                                    </select>
                                                                </div>

                                                                <!-- Cours -->
                                                                <div class="form-group">
                                                                    <label for="coursId">📚 Cours *</label>
                                                                    <select id="coursId" name="coursId" required>
                                                                        <option value="">-- Sélectionner un cours --
                                                                        </option>
                                                                        <% for (Ressource c : cours) { %>
                                                                            <option value="<%=c.getId()%>" <%=isEdit &&
                                                                                planification.getCoursId()==c.getId()
                                                                                ? "selected" : "" %>>
                                                                                <%=c.getNom()%>
                                                                            </option>
                                                                            <% } %>
                                                                    </select>
                                                                </div>

                                                                <!-- Date et Heure -->
                                                                <div class="form-group">
                                                                    <label for="dateHeure">📅 Date et Heure *</label>
                                                                    <input type="datetime-local" id="dateHeure"
                                                                        name="dateHeure" value="<%=dateHeureValue%>"
                                                                        required>
                                                                    <small>Format: jj/mm/aaaa hh:mm</small>
                                                                </div>

                                                                <!-- Durée -->
                                                                <div class="form-group">
                                                                    <label for="duree">⏱️ Durée (minutes) *</label>
                                                                    <input type="number" id="duree" name="duree"
                                                                        min="15" max="480" step="15"
                                                                        value="<%=isEdit ? planification.getDuree() : 60%>"
                                                                        required>
                                                                    <small>Entre 15 et 480 minutes (8 heures)</small>
                                                                </div>

                                                                <!-- Statut -->
                                                                <div class="form-group">
                                                                    <label for="statut">📊 Statut *</label>
                                                                    <select id="statut" name="statut" required>
                                                                        <option value="PLANIFIE" <%=isEdit && "PLANIFIE"
                                                                            .equals(planification.getStatut())
                                                                            ? "selected" : "" %>>Planifié</option>
                                                                        <option value="EN_COURS" <%=isEdit && "EN_COURS"
                                                                            .equals(planification.getStatut())
                                                                            ? "selected" : "" %>>En cours</option>
                                                                        <option value="TERMINE" <%=isEdit && "TERMINE"
                                                                            .equals(planification.getStatut())
                                                                            ? "selected" : "" %>>Terminé</option>
                                                                        <option value="ANNULE" <%=isEdit && "ANNULE"
                                                                            .equals(planification.getStatut())
                                                                            ? "selected" : "" %>>Annulé</option>
                                                                    </select>
                                                                </div>

                                                                <!-- Notes -->
                                                                <div class="form-group">
                                                                    <label for="notes">📝 Notes / Description</label>
                                                                    <textarea id="notes" name="notes" rows="4"
                                                                        placeholder="Notes supplémentaires (type: réunion, examen, etc.)"><%=isEdit && planification.getNotes() != null ? planification.getNotes() : ""%></textarea>
                                                                    <small>Ajoutez "réunion" ou "examen" pour changer la
                                                                        couleur dans le calendrier</small>
                                                                </div>

                                                                <!-- Boutons -->
                                                                <div class="form-actions"
                                                                    style="display: flex; gap: 15px; margin-top: 30px;">
                                                                    <button type="submit" class="btn btn-primary"
                                                                        style="flex: 1;">
                                                                        <%=isEdit ? "💾 Enregistrer" : "➕ Créer" %>
                                                                    </button>
                                                                    <a href="<%=request.getContextPath()%>/calendrier"
                                                                        class="btn btn-secondary"
                                                                        style="flex: 1; text-align: center; padding: 12px;">
                                                                        ✖️ Annuler
                                                                    </a>
                                                                </div>
                                                    </form>
                                        </div>
                                    </div>

                                    <!-- Footer -->
                                    <footer class="footer">
                                        <p>&copy; 2026 Système de Planification Académique v1.0.0</p>
                                    </footer>

                                    <script>
                                        function validerFormulaire() {
                                            const enseignant = document.getElementById('enseignantId').value;
                                            const salle = document.getElementById('salleId').value;
                                            const cours = document.getElementById('coursId').value;
                                            const dateHeure = document.getElementById('dateHeure').value;
                                            const duree = document.getElementById('duree').value;

                                            if (!enseignant || !salle || !cours || !dateHeure || !duree) {
                                                alert('⚠️ Veuillez remplir tous les champs obligatoires (*)');
                                                return false;
                                            }

                                            if (duree < 15 || duree > 480) {
                                                alert('⚠️ La durée doit être entre 15 et 480 minutes');
                                                return false;
                                            }

                                            return true;
                                        }
                                    </script>
                                </body>

                                </html>