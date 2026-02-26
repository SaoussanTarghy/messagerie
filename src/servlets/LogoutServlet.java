package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet gérant la déconnexion des utilisateurs.
 * URL : POST /api/auth/logout
 *
 * Invalide la session en cours et retourne un JSON de confirmation.
 *
 * @author Système de Planification Académique
 * @version 2.0
 */
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        System.out.println("✓ LogoutServlet initialisé");
    }

    /**
     * POST /api/auth/logout — invalide la session
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        HttpSession session = request.getSession(false);
        if (session != null) {
            String email = "";
            if (session.getAttribute("utilisateur") != null) {
                email = ((beans.Utilisateur) session.getAttribute("utilisateur")).getEmail();
            }
            session.invalidate();
            System.out.println("✓ Déconnexion: " + email);
        }

        response.getWriter().print("{\"success\":true,\"message\":\"Déconnecté avec succès\"}");
    }

    /**
     * OPTIONS — pour le preflight CORS
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
