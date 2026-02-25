package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe utilitaire pour gérer les connexions à la base de données MySQL
 * Utilise le pattern Singleton pour assurer une gestion optimale des connexions
 * 
 * @author Système de Planification Académique
 * @version 1.0
 */
public class DatabaseConnection {

    // ==================== Configuration BDD ====================

    private static final String URL = "jdbc:mysql://localhost:3306/planification_academique";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // À modifier selon votre configuration

    // Paramètres de connexion
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // ==================== Chargement du Driver ====================

    static {
        try {
            // Charger le driver MySQL au démarrage de l'application
            Class.forName(DRIVER);
            System.out.println("✓ Driver MySQL chargé avec succès");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ ERREUR : Driver MySQL introuvable !");
            System.err.println("Vérifiez que mysql-connector-java est dans le classpath");
            e.printStackTrace();
        }
    }

    // ==================== Méthodes publiques ====================

    /**
     * Obtient une connexion à la base de données
     * 
     * @return Connection active vers la base de données
     * @throws SQLException si la connexion échoue
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✓ Connexion à la base de données établie");
            return conn;
        } catch (SQLException e) {
            System.err.println("✗ ERREUR de connexion à la base de données !");
            System.err.println("URL : " + URL);
            System.err.println("USER : " + USER);
            System.err.println("Message : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Teste la connexion à la base de données
     * 
     * @return true si la connexion réussit, false sinon
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Test de connexion échoué : " + e.getMessage());
            return false;
        }
    }

    /**
     * Ferme proprement une connexion
     * 
     * @param conn Connexion à fermer
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("✓ Connexion fermée");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de connexion : " + e.getMessage());
            }
        }
    }

    /**
     * Ferme proprement un PreparedStatement
     * 
     * @param pstmt PreparedStatement à fermer
     */
    public static void closeStatement(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture du statement : " + e.getMessage());
            }
        }
    }

    /**
     * Ferme proprement un ResultSet
     * 
     * @param rs ResultSet à fermer
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture du resultset : " + e.getMessage());
            }
        }
    }

    /**
     * Ferme proprement toutes les ressources JDBC
     * Méthode pratique pour le pattern try-finally
     * 
     * @param conn  Connection à fermer
     * @param pstmt PreparedStatement à fermer
     * @param rs    ResultSet à fermer
     */
    public static void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        closeResultSet(rs);
        closeStatement(pstmt);
        closeConnection(conn);
    }

    // ==================== Méthodes de configuration ====================

    /**
     * Retourne l'URL de connexion (pour debug)
     */
    public static String getUrl() {
        return URL;
    }

    /**
     * Retourne l'utilisateur de connexion (pour debug)
     */
    public static String getUser() {
        return USER;
    }
}
