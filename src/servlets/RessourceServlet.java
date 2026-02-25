package servlets;

import beans.Ressource;
import dao.RessourceDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Servlet principal pour la gestion des ressources
 * Implémente TOUTES les méthodes requises par l'exercice :
 * - HttpServletRequest.getParameter() ✅
 * - HttpServletRequest.setAttribute() ✅
 * - HttpServletRequest.getRequestDispatcher() ✅
 * - HttpServletResponse.sendRedirect() ✅
 * - RequestDispatcher.forward() ✅
 * 
 * @author Système de Planification Académique
 * @version 1.0
 */
public class RessourceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private RessourceDAO ressourceDAO;

    /**
     * Initialisation du servlet
     */
    @Override
    public void init() throws ServletException {
        ressourceDAO = new RessourceDAO();
        System.out.println("✓ RessourceServlet initialisé");
    }

    /**
     * Gère les requêtes GET
     * GET est utilisé pour afficher des ressources (liste, détails, formulaires)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ===== MÉTHODE 1 : getParameter() =====
        // Récupère le paramètre 'action' de l'URL
        String action = request.getParameter("action");

        // Action par défaut si non spécifiée
        if (action == null || action.isEmpty()) {
            action = "list";
        }

        try {
            switch (action) {
                case "list":
                    afficherListe(request, response);
                    break;
                case "view":
                    afficherDetails(request, response);
                    break;
                case "add":
                    afficherFormulaireAjout(request, response);
                    break;
                case "edit":
                    afficherFormulaireModification(request, response);
                    break;
                case "delete":
                    supprimerRessource(request, response);
                    break;
                case "search":
                    rechercherRessources(request, response);
                    break;
                default:
                    afficherListe(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // En cas d'erreur, on redirige vers la page d'erreur 500
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erreur serveur : " + e.getMessage());
        }
    }

    /**
     * Gère les requêtes POST
     * POST est utilisé pour soumettre des formulaires (ajout, modification)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configuration de l'encodage pour supporter les caractères UTF-8
        request.setCharacterEncoding("UTF-8");

        // ===== MÉTHODE 1 : getParameter() =====
        String action = request.getParameter("action");

        try {
            if ("save".equals(action)) {
                enregistrerRessource(request, response);
            } else {
                // Action inconnue, on redirige vers la liste
                response.sendRedirect("ressources?action=list");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erreur serveur : " + e.getMessage());
        }
    }

    // ==================== Méthodes d'affichage (GET) ====================

    /**
     * Affiche la liste de toutes les ressources
     * Démontre : getParameter(), setAttribute(), getRequestDispatcher(), forward()
     */
    private void afficherListe(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // ===== MÉTHODE 1 : getParameter() =====
        // Récupère le filtre de type s'il existe
        String typeFiltre = request.getParameter("type");

        List<Ressource> ressources;

        if (typeFiltre != null && !typeFiltre.isEmpty()) {
            // Si un filtre est appliqué, on filtre par type
            ressources = ressourceDAO.getRessourcesByType(typeFiltre);
        } else {
            // Sinon, on récupère toutes les ressources
            ressources = ressourceDAO.getAllRessources();
        }

        // ===== MÉTHODE 2 : setAttribute() =====
        // Passe la liste des ressources à la JSP
        request.setAttribute("ressources", ressources);
        request.setAttribute("typeFiltre", typeFiltre);

        // ===== MÉTHODE 3 : getRequestDispatcher() =====
        // Obtient le dispatcher pour la page JSP
        // ===== MÉTHODE 4 : forward() =====
        // Transfère la requête vers la JSP
        request.getRequestDispatcher("/views/listeRessources.jsp").forward(request, response);
    }

    /**
     * Affiche les détails d'une ressource
     * Démontre : getParameter(), setAttribute(), getRequestDispatcher(), forward()
     */
    private void afficherDetails(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // ===== MÉTHODE 1 : getParameter() =====
        // Récupère l'ID de la ressource depuis l'URL
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            // Si pas d'ID, on redirige vers la liste
            response.sendRedirect("ressources?action=list");
            return;
        }

        int id = Integer.parseInt(idParam);
        Ressource ressource = ressourceDAO.getRessourceById(id);

        if (ressource == null) {
            // Si la ressource n'existe pas, erreur 404
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Ressource #" + id + " introuvable");
            return;
        }

        // ===== MÉTHODE 2 : setAttribute() =====
        request.setAttribute("ressource", ressource);

        // ===== MÉTHODES 3 & 4 : getRequestDispatcher() + forward() =====
        request.getRequestDispatcher("/views/detailRessource.jsp").forward(request, response);
    }

    /**
     * Affiche le formulaire d'ajout d'une ressource
     * Démontre : getRequestDispatcher(), forward()
     */
    private void afficherFormulaireAjout(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // ===== MÉTHODE 2 : setAttribute() =====
        // On indique qu'il s'agit d'un ajout (pas de modification)
        request.setAttribute("isEdit", false);

        // ===== MÉTHODES 3 & 4 : getRequestDispatcher() + forward() =====
        request.getRequestDispatcher("/views/formulaireRessource.jsp").forward(request, response);
    }

    /**
     * Affiche le formulaire de modification d'une ressource
     * Démontre : getParameter(), setAttribute(), getRequestDispatcher(), forward()
     */
    private void afficherFormulaireModification(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // ===== MÉTHODE 1 : getParameter() =====
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("ressources?action=list");
            return;
        }

        int id = Integer.parseInt(idParam);
        Ressource ressource = ressourceDAO.getRessourceById(id);

        if (ressource == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Ressource #" + id + " introuvable");
            return;
        }

        // ===== MÉTHODE 2 : setAttribute() =====
        request.setAttribute("ressource", ressource);
        request.setAttribute("isEdit", true);

        // ===== MÉTHODES 3 & 4 : getRequestDispatcher() + forward() =====
        request.getRequestDispatcher("/views/formulaireRessource.jsp").forward(request, response);
    }

    /**
     * Recherche des ressources par mot-clé
     * Démontre : getParameter(), setAttribute(), getRequestDispatcher(), forward()
     */
    private void rechercherRessources(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // ===== MÉTHODE 1 : getParameter() =====
        String keyword = request.getParameter("keyword");

        if (keyword == null || keyword.trim().isEmpty()) {
            // Si pas de mot-clé, afficher toutes les ressources
            afficherListe(request, response);
            return;
        }

        List<Ressource> ressources = ressourceDAO.searchRessources(keyword);

        // ===== MÉTHODE 2 : setAttribute() =====
        request.setAttribute("ressources", ressources);
        request.setAttribute("keyword", keyword);

        // ===== MÉTHODES 3 & 4 : getRequestDispatcher() + forward() =====
        request.getRequestDispatcher("/views/listeRessources.jsp").forward(request, response);
    }

    // ==================== Méthodes de modification (POST) ====================

    /**
     * Enregistre une ressource (ajout ou modification)
     * Démontre : getParameter(), sendRedirect() avec pattern Post-Redirect-Get
     */
    private void enregistrerRessource(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // ===== MÉTHODE 1 : getParameter() =====
        // Récupère tous les paramètres du formulaire
        String idParam = request.getParameter("id");
        String nom = request.getParameter("nom");
        String type = request.getParameter("type");
        String disponibiliteParam = request.getParameter("disponibilite");
        String description = request.getParameter("description");

        // Validation des données
        if (nom == null || nom.trim().isEmpty()) {
            request.setAttribute("error", "Le nom est obligatoire");
            afficherFormulaireAjout(request, response);
            return;
        }

        if (type == null || type.trim().isEmpty()) {
            request.setAttribute("error", "Le type est obligatoire");
            afficherFormulaireAjout(request, response);
            return;
        }

        // Création de l'objet Ressource
        Ressource ressource = new Ressource();
        ressource.setNom(nom.trim());
        ressource.setType(type);
        ressource.setDisponibilite(disponibiliteParam != null);
        ressource.setDescription(description != null ? description.trim() : "");

        HttpSession session = request.getSession();
        boolean success;

        if (idParam == null || idParam.isEmpty()) {
            // AJOUT d'une nouvelle ressource
            success = ressourceDAO.addRessource(ressource);
            if (success) {
                session.setAttribute("successMessage", "Ressource ajoutée avec succès !");
            } else {
                session.setAttribute("errorMessage", "Erreur lors de l'ajout de la ressource");
            }
        } else {
            // MODIFICATION d'une ressource existante
            ressource.setId(Integer.parseInt(idParam));
            success = ressourceDAO.updateRessource(ressource);
            if (success) {
                session.setAttribute("successMessage", "Ressource modifiée avec succès !");
            } else {
                session.setAttribute("errorMessage", "Erreur lors de la modification");
            }
        }

        // ===== MÉTHODE 5 : sendRedirect() =====
        // Pattern POST-REDIRECT-GET pour éviter la resoumission du formulaire
        response.sendRedirect("ressources?action=list");
    }

    /**
     * Supprime une ressource
     * Démontre : getParameter(), sendRedirect()
     */
    private void supprimerRessource(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // ===== MÉTHODE 1 : getParameter() =====
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("ressources?action=list");
            return;
        }

        int id = Integer.parseInt(idParam);
        boolean success = ressourceDAO.deleteRessource(id);

        HttpSession session = request.getSession();
        if (success) {
            session.setAttribute("successMessage", "Ressource supprimée avec succès !");
        } else {
            session.setAttribute("errorMessage", "Erreur lors de la suppression");
        }

        // ===== MÉTHODE 5 : sendRedirect() =====
        response.sendRedirect("ressources?action=list");
    }
}
