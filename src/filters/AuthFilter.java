package filters;

import beans.Utilisateur;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtre d'authentification.
 *
 * Règles :
 * - Les endpoints /api/auth/* sont toujours publics (login, logout, me).
 * - Les endpoints /api/* non authentifiés reçoivent un 401 JSON.
 * - Les pages JSP (/ressources/*, /calendrier/*) renvoient vers
 * /views/login.jsp.
 * - Les ressources statiques et la page de login sont toujours accessibles.
 *
 * @author Système de Planification Académique
 * @version 2.0
 */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("✓ AuthFilter initialisé");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getServletPath();
        if (request.getPathInfo() != null) {
            path += request.getPathInfo();
        }

        // --- 1. Toujours autoriser les endpoints d'authentification ---
        if (path.startsWith("/api/auth/")) {
            chain.doFilter(req, res);
            return;
        }

        // --- 2. Toujours autoriser les ressources statiques et la page login ---
        if (isPublicResource(path)) {
            chain.doFilter(req, res);
            return;
        }

        // --- 3. Vérifier si l'utilisateur est connecté ---
        HttpSession session = request.getSession(false);
        boolean authenticated = (session != null && session.getAttribute("utilisateur") != null);

        if (authenticated) {
            // Vérifications de rôle pour les routes protégées
            if (path.startsWith("/api/admin/")) {
                Utilisateur user = (Utilisateur) session.getAttribute("utilisateur");
                if (!user.hasAdminAccess()) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().print("{\"error\":\"Accès refusé\",\"message\":\"Rôle insuffisant\"}");
                    return;
                }
            }
            chain.doFilter(req, res);
            return;
        }

        // --- 4. Non authentifié ---
        if (path.startsWith("/api/")) {
            // API → retourner 401 JSON
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.getWriter().print("{\"authenticated\":false,\"message\":\"Session expirée ou non connecté\"}");
        } else {
            // Page JSP → rediriger vers la page de login
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    @Override
    public void destroy() {
        System.out.println("AuthFilter détruit");
    }

    /**
     * Ressources accessibles sans authentification.
     */
    private boolean isPublicResource(String path) {
        return path.equals("/")
                || path.equals("/index.jsp")
                || path.equals("/views/login.jsp")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/assets/")
                || path.startsWith("/favicon")
                || path.startsWith("/errors/")
                || path.endsWith(".ico")
                || path.endsWith(".png")
                || path.endsWith(".jpg");
    }
}
