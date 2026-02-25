package servlets;

import beans.Planification;
import dao.PlanificationDAO;
import dao.RessourceDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Servlet pour gérer l'affichage et les opérations du calendrier academique
 * 
 * @author Système de Planification Académique
 */
@WebServlet("/calendrier")
public class CalendrierServlet extends HttpServlet {

    private PlanificationDAO planificationDAO;
    private RessourceDAO ressourceDAO;

    @Override
    public void init() throws ServletException {
        planificationDAO = new PlanificationDAO();
        ressourceDAO = new RessourceDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null)
            action = "afficher";

        switch (action) {
            case "afficher":
                afficherCalendrier(request, response);
                break;
            case "detail":
                afficherDetailPlanification(request, response);
                break;
            case "formulaire":
                afficherFormulairePlanification(request, response);
                break;
            default:
                afficherCalendrier(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("sauvegarder".equals(action)) {
            sauvegarderPlanification(request, response);
        } else if ("supprimer".equals(action)) {
            supprimerPlanification(request, response);
        } else {
            afficherCalendrier(request, response);
        }
    }

    /**
     * Affiche le calendrier pour la semaine en cours ou spécifiée
     */
    private void afficherCalendrier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Déterminer la semaine à afficher
        String semaineParam = request.getParameter("semaine");
        Calendar cal = Calendar.getInstance();

        if (semaineParam != null) {
            try {
                int offset = Integer.parseInt(semaineParam);
                cal.add(Calendar.WEEK_OF_YEAR, offset);
            } catch (NumberFormatException e) {
                // Utiliser la semaine actuelle si paramètre invalide
            }
        }

        // Calculer le lundi et vendredi de la semaine
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Timestamp debutSemaine = new Timestamp(cal.getTimeInMillis());

        cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        Timestamp finSemaine = new Timestamp(cal.getTimeInMillis());

        // Récupérer les planifications de la semaine
        List<Planification> planifications = planificationDAO.getPlanificationsParSemaine(debutSemaine, finSemaine);

        // Préparer les attributs pour la JSP
        request.setAttribute("planifications", planifications);
        request.setAttribute("debutSemaine", debutSemaine);
        request.setAttribute("finSemaine", finSemaine);
        request.setAttribute("semaineOffset", semaineParam != null ? semaineParam : "0");

        // Forward vers la vue
        request.getRequestDispatcher("/calendrier.jsp").forward(request, response);
    }

    /**
     * Affiche les détails d'une planification
     */
    private void afficherDetailPlanification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Planification planification = planificationDAO.getPlanificationById(id);

                if (planification != null) {
                    request.setAttribute("planification", planification);
                    request.getRequestDispatcher("/views/detailPlanification.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("ID invalide: " + idParam);
            }
        }

        response.sendRedirect(request.getContextPath() + "/calendrier");
    }

    /**
     * Affiche le formulaire pour créer/modifier une planification
     */
    private void afficherFormulairePlanification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        Planification planification = null;

        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                planification = planificationDAO.getPlanificationById(id);
            } catch (NumberFormatException e) {
                System.err.println("ID invalide: " + idParam);
            }
        }

        // Récupérer les ressources pour les dropdowns
        request.setAttribute("planification", planification);
        request.setAttribute("enseignants", ressourceDAO.getRessourcesParType("ENSEIGNANT"));
        request.setAttribute("salles", ressourceDAO.getRessourcesParType("SALLE"));
        request.setAttribute("cours", ressourceDAO.getRessourcesParType("COURS"));

        // Récupérer date/heure si passée en paramètre (pour création rapide)
        String dateParam = request.getParameter("date");
        String heureParam = request.getParameter("heure");
        request.setAttribute("dateInitiale", dateParam);
        request.setAttribute("heureInitiale", heureParam);

        request.getRequestDispatcher("/views/formulairePlanification.jsp").forward(request, response);
    }

    /**
     * Sauvegarde une nouvelle planification ou met à jour une existante
     */
    private void sauvegarderPlanification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Récupérer les paramètres du formulaire
            String idParam = request.getParameter("id");
            int enseignantId = Integer.parseInt(request.getParameter("enseignantId"));
            int salleId = Integer.parseInt(request.getParameter("salleId"));
            int coursId = Integer.parseInt(request.getParameter("coursId"));
            String dateHeureStr = request.getParameter("dateHeure");
            int duree = Integer.parseInt(request.getParameter("duree"));
            String statut = request.getParameter("statut");
            String notes = request.getParameter("notes");

            // Parser la date/heure
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Timestamp dateHeure = new Timestamp(sdf.parse(dateHeureStr).getTime());

            // Créer ou récupérer la planification
            Planification planification;
            boolean isNew = (idParam == null || idParam.isEmpty());

            if (isNew) {
                planification = new Planification();
            } else {
                planification = planificationDAO.getPlanificationById(Integer.parseInt(idParam));
                if (planification == null) {
                    request.setAttribute("erreur", "Planification introuvable");
                    afficherFormulairePlanification(request, response);
                    return;
                }
            }

            // Vérifier les conflits
            boolean conflit = planificationDAO.verifierConflit(
                    salleId, enseignantId, dateHeure, duree,
                    planification.getId());

            if (conflit) {
                request.setAttribute("erreur",
                        "Conflit d'horaire détecté ! La salle ou l'enseignant est déjà réservé(e) à cette heure.");
                afficherFormulairePlanification(request, response);
                return;
            }

            // Remplir l'objet
            planification.setEnseignantId(enseignantId);
            planification.setSalleId(salleId);
            planification.setCoursId(coursId);
            planification.setDateHeure(dateHeure);
            planification.setDuree(duree);
            planification.setStatut(statut);
            planification.setNotes(notes);

            // Sauvegarder
            boolean success;
            if (isNew) {
                success = planificationDAO.creerPlanification(planification);
            } else {
                success = planificationDAO.modifierPlanification(planification);
            }

            if (success) {
                response.sendRedirect(
                        request.getContextPath() + "/calendrier?success=" + (isNew ? "create" : "update"));
            } else {
                request.setAttribute("erreur", "Erreur lors de la sauvegarde");
                afficherFormulairePlanification(request, response);
            }

        } catch (Exception e) {
            System.err.println("Erreur sauvegarderPlanification: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("erreur", "Erreur lors du traitement: " + e.getMessage());
            afficherFormulairePlanification(request, response);
        }
    }

    /**
     * Supprime une planification
     */
    private void supprimerPlanification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                boolean success = planificationDAO.supprimerPlanification(id);

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/calendrier?success=delete");
                } else {
                    response.sendRedirect(request.getContextPath() + "/calendrier?error=delete");
                }
                return;
            } catch (NumberFormatException e) {
                System.err.println("ID invalide: " + idParam);
            }
        }

        response.sendRedirect(request.getContextPath() + "/calendrier");
    }
}
