package servlets;

import beans.Utilisateur;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet permettant au frontend de vérifier si une session est active.
 * URL : GET /api/auth/me
 *
 * Retourne le profil de l'utilisateur connecté ou 401 si non authentifié.
 * Utilisé au chargement de l'application React pour restaurer l'état de
 * connexion.
 *
 * @author Système de Planification Académique
 * @version 2.0
 */
public class AuthCheckServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        System.out.println("✓ AuthCheckServlet initialisé");
    }

    /**
     * GET /api/auth/me — retourne le profil de l'utilisateur connecté
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("utilisateur") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("{\"authenticated\":false}");
            return;
        }

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        String ressourceId = utilisateur.getRessourceId() != null
                ? String.valueOf(utilisateur.getRessourceId())
                : "null";

        response.getWriter().print("{" +
                "\"authenticated\":true," +
                "\"id\":" + utilisateur.getId() + "," +
                "\"nom\":\"" + escapeJson(utilisateur.getNom()) + "\"," +
                "\"email\":\"" + escapeJson(utilisateur.getEmail()) + "\"," +
                "\"role\":\"" + utilisateur.getRole() + "\"," +
                "\"ressourceId\":" + ressourceId +
                "}");
    }

    /**
     * OPTIONS — pour le preflight CORS
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
