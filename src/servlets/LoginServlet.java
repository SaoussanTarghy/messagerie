package servlets;

import beans.Utilisateur;
import dao.UtilisateurDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet gérant la connexion des utilisateurs.
 * URL : POST /api/auth/login
 *
 * Reçoit JSON : { "email": "...", "password": "..." }
 * Répond JSON : { "success": true, "role": "...", "nom": "...", "ressourceId":
 * ... }
 * ou : { "success": false, "message": "..." }
 *
 * @author Système de Planification Académique
 * @version 2.0
 */
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAO();
        System.out.println("✓ LoginServlet initialisé");
    }

    /**
     * POST /api/auth/login
     * Authentifie l'utilisateur et crée la session.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        request.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            // Lire le corps de la requête JSON
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            String body = sb.toString();

            // Parser JSON manuellement (pas de dépendance externe)
            String email = extraireChampJson(body, "email");
            String password = extraireChampJson(body, "password");

            if (email == null || password == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false,\"message\":\"Email et mot de passe requis\"}");
                return;
            }

            // Authentifier via DAO
            Utilisateur utilisateur = utilisateurDAO.authentifier(email, password);

            if (utilisateur == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\":false,\"message\":\"Email ou mot de passe incorrect\"}");
                return;
            }

            // Créer la session
            HttpSession session = request.getSession(true);
            session.setAttribute("utilisateur", utilisateur);
            session.setAttribute("userRole", utilisateur.getRole());
            session.setAttribute("userId", utilisateur.getId());

            // Répondre avec les infos de l'utilisateur
            String ressourceId = utilisateur.getRessourceId() != null
                    ? String.valueOf(utilisateur.getRessourceId())
                    : "null";

            out.print("{" +
                    "\"success\":true," +
                    "\"id\":" + utilisateur.getId() + "," +
                    "\"nom\":\"" + escapeJson(utilisateur.getNom()) + "\"," +
                    "\"email\":\"" + escapeJson(utilisateur.getEmail()) + "\"," +
                    "\"role\":\"" + utilisateur.getRole() + "\"," +
                    "\"ressourceId\":" + ressourceId +
                    "}");

            System.out.println("✓ Connexion réussie: " + utilisateur.getEmail() + " [" + utilisateur.getRole() + "]");

        } catch (Exception e) {
            System.err.println("Erreur LoginServlet: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"Erreur serveur interne\"}");
        }
    }

    /**
     * OPTIONS /api/auth/login — pour le preflight CORS
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // ==================== Méthodes utilitaires ====================

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    /**
     * Extrait un champ d'une chaîne JSON simple (sans bibliothèque externe).
     * Fonctionne pour les chaînes entre guillemets : "champ":"valeur"
     */
    private String extraireChampJson(String json, String champ) {
        String searchKey = "\"" + champ + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1)
            return null;

        int colonIndex = json.indexOf(":", keyIndex + searchKey.length());
        if (colonIndex == -1)
            return null;

        // Avancer après les espaces
        int startQuote = json.indexOf("\"", colonIndex + 1);
        if (startQuote == -1)
            return null;

        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote == -1)
            return null;

        return json.substring(startQuote + 1, endQuote);
    }

    /** Échappe les caractères spéciaux pour JSON */
    private String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
