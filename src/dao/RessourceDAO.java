package dao;

import beans.Ressource;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object pour la gestion des ressources
 * Implémente toutes les opérations CRUD sur la table ressource
 * 
 * @author Système de Planification Académique
 * @version 1.0
 */
public class RessourceDAO {

    // ==================== Méthodes de récupération (SELECT) ====================

    /**
     * Récupère toutes les ressources
     */
    public List<Ressource> getAllRessources() {
        List<Ressource> ressources = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM ressource ORDER BY type, nom";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ressources.add(extractRessourceFromResultSet(rs)); // Assuming extractRessourceFromResultSet is the
                                                                   // correct method name
            }

        } catch (Exception e) {
            System.err.println("Erreur getAllRessources: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }

        return ressources;
    }

    /**
     * Récupère les ressources par type
     * 
     * @param type Le type de ressource (ENSEIGNANT, SALLE, COURS)
     */
    public List<Ressource> getRessourcesParType(String type) {
        List<Ressource> ressources = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM ressource WHERE type = ? ORDER BY nom";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, type);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ressources.add(extractRessourceFromResultSet(rs)); // Assuming extractRessourceFromResultSet is the
                                                                   // correct method name
            }

        } catch (Exception e) {
            System.err.println("Erreur getRessourcesParType: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }

        return ressources;
    }

    /**
     * Récupère une ressource par son ID
     * 
     * @param id Identifiant de la ressource
     * @return La ressource correspondante ou null si non trouvée
     * @throws SQLException si une erreur SQL survient
     */
    public Ressource getRessourceById(int id) throws SQLException {
        Ressource ressource = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM ressource WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                ressource = extractRessourceFromResultSet(rs);
            }

        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }

        return ressource;
    }

    /**
     * Récupère les ressources par type
     * 
     * @param type Type de ressource (ENSEIGNANT, SALLE, COURS)
     * @return Liste des ressources du type spécifié
     * @throws SQLException si une erreur SQL survient
     */
    public List<Ressource> getRessourcesByType(String type) throws SQLException {
        List<Ressource> ressources = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM ressource WHERE type = ? ORDER BY nom";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, type);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ressources.add(extractRessourceFromResultSet(rs));
            }

        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }

        return ressources;
    }

    /**
     * Recherche des ressources par mot-clé dans le nom ou la description
     * 
     * @param keyword Mot-clé de recherche
     * @return Liste des ressources correspondantes
     * @throws SQLException si une erreur SQL survient
     */
    public List<Ressource> searchRessources(String keyword) throws SQLException {
        List<Ressource> ressources = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM ressource " +
                    "WHERE nom LIKE ? OR description LIKE ? " +
                    "ORDER BY type, nom";
            pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ressources.add(extractRessourceFromResultSet(rs));
            }

        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }

        return ressources;
    }

    // ==================== Méthodes de modification (INSERT, UPDATE, DELETE)
    // ====================

    /**
     * Ajoute une nouvelle ressource dans la base de données
     * 
     * @param ressource La ressource à ajouter
     * @return true si l'ajout réussit, false sinon
     * @throws SQLException si une erreur SQL survient
     */
    public boolean addRessource(Ressource ressource) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO ressource (nom, type, disponibilite, description) " +
                    "VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ressource.getNom());
            pstmt.setString(2, ressource.getType());
            pstmt.setBoolean(3, ressource.isDisponibilite());
            pstmt.setString(4, ressource.getDescription());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    /**
     * Met à jour une ressource existante
     * 
     * @param ressource La ressource avec les nouvelles valeurs
     * @return true si la mise à jour réussit, false sinon
     * @throws SQLException si une erreur SQL survient
     */
    public boolean updateRessource(Ressource ressource) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE ressource SET nom = ?, type = ?, disponibilite = ?, " +
                    "description = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ressource.getNom());
            pstmt.setString(2, ressource.getType());
            pstmt.setBoolean(3, ressource.isDisponibilite());
            pstmt.setString(4, ressource.getDescription());
            pstmt.setInt(5, ressource.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    /**
     * Supprime une ressource de la base de données
     * 
     * @param id Identifiant de la ressource à supprimer
     * @return true si la suppression réussit, false sinon
     * @throws SQLException si une erreur SQL survient
     */
    public boolean deleteRessource(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM ressource WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    // ==================== Méthodes utilitaires ====================

    /**
     * Vérifie la disponibilité d'une ressource
     * 
     * @param id Identifiant de la ressource
     * @return true si disponible, false sinon
     * @throws SQLException si une erreur SQL survient
     */
    public boolean checkDisponibilite(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT disponibilite FROM ressource WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("disponibilite");
            }
            return false;

        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
    }

    /**
     * Compte le nombre total de ressources
     * 
     * @return Le nombre total de ressources
     * @throws SQLException si une erreur SQL survient
     */
    public int countRessources() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) as total FROM ressource";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;

        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
    }

    /**
     * Extrait un objet Ressource à partir d'un ResultSet
     * Méthode utilitaire privée pour éviter la duplication de code
     * 
     * @param rs ResultSet positionné sur une ligne
     * @return Objet Ressource créé à partir des données
     * @throws SQLException si une erreur de lecture survient
     */
    private Ressource extractRessourceFromResultSet(ResultSet rs) throws SQLException {
        return new Ressource(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("type"),
                rs.getBoolean("disponibilite"),
                rs.getString("description"),
                rs.getTimestamp("date_creation"),
                rs.getTimestamp("date_modification"));
    }
}
