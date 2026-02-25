package dao;

import beans.Planification;
import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des planifications
 * Gère les opérations CRUD et les requêtes spécifiques au calendrier
 * 
 * @author Système de Planification Académique
 * @version 1.0
 */
public class PlanificationDAO {

    /**
     * Récupère toutes les planifications avec les informations jointes
     */
    public List<Planification> getAllPlanifications() {
        List<Planification> planifications = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();

            String sql = "SELECT p.*, " +
                    "e.nom as enseignant_nom, " +
                    "s.nom as salle_nom, " +
                    "c.nom as cours_nom " +
                    "FROM planification p " +
                    "LEFT JOIN ressource e ON p.enseignant_id = e.id " +
                    "LEFT JOIN ressource s ON p.salle_id = s.id " +
                    "LEFT JOIN ressource c ON p.cours_id = c.id " +
                    "ORDER BY p.date_heure ASC";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                planifications.add(mapResultSetToPlanification(rs));
            }

        } catch (Exception e) {
            System.err.println("Erreur getAllPlanifications: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }

        return planifications;
    }

    /**
     * Récupère les planifications pour une semaine donnée
     * 
     * @param dateDebut Premier jour de la semaine (lundi à 00:00)
     * @param dateFin   Dernier jour de la semaine (vendredi à 23:59)
     */
    public List<Planification> getPlanificationsParSemaine(Timestamp dateDebut, Timestamp dateFin) {
        List<Planification> planifications = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();

            String sql = "SELECT p.*, " +
                    "e.nom as enseignant_nom, " +
                    "s.nom as salle_nom, " +
                    "c.nom as cours_nom " +
                    "FROM planification p " +
                    "LEFT JOIN ressource e ON p.enseignant_id = e.id " +
                    "LEFT JOIN ressource s ON p.salle_id = s.id " +
                    "LEFT JOIN ressource c ON p.cours_id = c.id " +
                    "WHERE p.date_heure >= ? AND p.date_heure <= ? " +
                    "ORDER BY p.date_heure ASC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, dateDebut);
            pstmt.setTimestamp(2, dateFin);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                planifications.add(mapResultSetToPlanification(rs));
            }

        } catch (Exception e) {
            System.err.println("Erreur getPlanificationsParSemaine: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }

        return planifications;
    }

    /**
     * Récupère une planification par son ID
     */
    public Planification getPlanificationById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();

            String sql = "SELECT p.*, " +
                    "e.nom as enseignant_nom, " +
                    "s.nom as salle_nom, " +
                    "c.nom as cours_nom " +
                    "FROM planification p " +
                    "LEFT JOIN ressource e ON p.enseignant_id = e.id " +
                    "LEFT JOIN ressource s ON p.salle_id = s.id " +
                    "LEFT JOIN ressource c ON p.cours_id = c.id " +
                    "WHERE p.id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPlanification(rs);
            }

        } catch (Exception e) {
            System.err.println("Erreur getPlanificationById: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }

        return null;
    }

    /**
     * Crée une nouvelle planification
     */
    public boolean creerPlanification(Planification planification) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();

            String sql = "INSERT INTO planification " +
                    "(enseignant_id, salle_id, cours_id, date_heure, duree, statut, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, planification.getEnseignantId());
            pstmt.setInt(2, planification.getSalleId());
            pstmt.setInt(3, planification.getCoursId());
            pstmt.setTimestamp(4, planification.getDateHeure());
            pstmt.setInt(5, planification.getDuree());
            pstmt.setString(6, planification.getStatut());
            pstmt.setString(7, planification.getNotes());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            System.err.println("Erreur creerPlanification: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }

        return false;
    }

    /**
     * Met à jour une planification existante
     */
    public boolean modifierPlanification(Planification planification) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();

            String sql = "UPDATE planification SET " +
                    "enseignant_id = ?, salle_id = ?, cours_id = ?, " +
                    "date_heure = ?, duree = ?, statut = ?, notes = ? " +
                    "WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, planification.getEnseignantId());
            pstmt.setInt(2, planification.getSalleId());
            pstmt.setInt(3, planification.getCoursId());
            pstmt.setTimestamp(4, planification.getDateHeure());
            pstmt.setInt(5, planification.getDuree());
            pstmt.setString(6, planification.getStatut());
            pstmt.setString(7, planification.getNotes());
            pstmt.setInt(8, planification.getId());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            System.err.println("Erreur modifierPlanification: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }

        return false;
    }

    /**
     * Supprime une planification
     */
    public boolean supprimerPlanification(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();

            String sql = "DELETE FROM planification WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            System.err.println("Erreur supprimerPlanification: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }

        return false;
    }

    /**
     * Vérifie s'il y a un conflit d'horaire pour une salle ou un enseignant
     */
    public boolean verifierConflit(int salleId, int enseignantId, Timestamp dateHeure, int duree,
            int planificationIdExistant) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();

            // Calculer l'heure de fin
            Timestamp dateFin = new Timestamp(dateHeure.getTime() + (duree * 60 * 1000L));

            String sql = "SELECT COUNT(*) as count FROM planification " +
                    "WHERE (salle_id = ? OR enseignant_id = ?) " +
                    "AND statut != 'ANNULE' " +
                    "AND id != ? " +
                    "AND ( " +
                    "  (date_heure <= ? AND DATE_ADD(date_heure, INTERVAL duree MINUTE) > ?) " +
                    "  OR (date_heure < ? AND DATE_ADD(date_heure, INTERVAL duree MINUTE) >= ?) " +
                    "  OR (date_heure >= ? AND date_heure < ?) " +
                    ")";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, salleId);
            pstmt.setInt(2, enseignantId);
            pstmt.setInt(3, planificationIdExistant);
            pstmt.setTimestamp(4, dateHeure);
            pstmt.setTimestamp(5, dateHeure);
            pstmt.setTimestamp(6, dateFin);
            pstmt.setTimestamp(7, dateFin);
            pstmt.setTimestamp(8, dateHeure);
            pstmt.setTimestamp(9, dateFin);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (Exception e) {
            System.err.println("Erreur verifierConflit: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }

        return false;
    }

    /**
     * Mappe un ResultSet vers un objet Planification
     */
    private Planification mapResultSetToPlanification(ResultSet rs) throws SQLException {
        Planification p = new Planification();
        p.setId(rs.getInt("id"));
        p.setEnseignantId(rs.getInt("enseignant_id"));
        p.setSalleId(rs.getInt("salle_id"));
        p.setCoursId(rs.getInt("cours_id"));
        p.setDateHeure(rs.getTimestamp("date_heure"));
        p.setDuree(rs.getInt("duree"));
        p.setStatut(rs.getString("statut"));
        p.setNotes(rs.getString("notes"));
        p.setDateCreation(rs.getTimestamp("date_creation"));

        // Informations jointes
        p.setEnseignantNom(rs.getString("enseignant_nom"));
        p.setSalleNom(rs.getString("salle_nom"));
        p.setCoursNom(rs.getString("cours_nom"));

        return p;
    }
}
