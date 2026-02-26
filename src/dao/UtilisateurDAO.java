package dao;

import beans.Utilisateur;
import utils.DatabaseConnection;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO pour la gestion des utilisateurs et l'authentification
 * Utilise SHA-256 pour la vérification des mots de passe
 *
 * @author Système de Planification Académique
 * @version 2.0
 */
public class UtilisateurDAO {

    /**
     * Tente d'authentifier un utilisateur avec son email et son mot de passe.
     *
     * @param email    L'email de l'utilisateur
     * @param password Le mot de passe en clair (sera hashé en SHA-256)
     * @return L'objet Utilisateur si les credentials sont corrects, null sinon
     */
    public Utilisateur authentifier(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }

        String passwordHash = hashSHA256(password);
        if (passwordHash == null) {
            System.err.println("Erreur: impossible de calculer le hash SHA-256");
            return null;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM utilisateurs " +
                    "WHERE email = ? AND mot_de_passe_hash = ? AND actif = TRUE";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email.trim().toLowerCase());
            pstmt.setString(2, passwordHash);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extraireUtilisateur(rs);
            }

        } catch (Exception e) {
            System.err.println("Erreur authentification: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }

        return null;
    }

    /**
     * Recherche un utilisateur par son email.
     *
     * @param email L'email à rechercher
     * @return L'utilisateur trouvé ou null
     */
    public Utilisateur findByEmail(String email) {
        if (email == null || email.isEmpty())
            return null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM utilisateurs WHERE email = ? AND actif = TRUE";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email.trim().toLowerCase());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extraireUtilisateur(rs);
            }

        } catch (Exception e) {
            System.err.println("Erreur findByEmail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }

        return null;
    }

    // ==================== Méthodes utilitaires ====================

    /**
     * Extrait un Utilisateur depuis un ResultSet
     */
    private Utilisateur extraireUtilisateur(ResultSet rs) throws Exception {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getInt("id"));
        u.setNom(rs.getString("nom"));
        u.setEmail(rs.getString("email"));
        u.setMotDePasseHash(rs.getString("mot_de_passe_hash"));
        u.setRole(rs.getString("role"));
        int rid = rs.getInt("ressource_id");
        u.setRessourceId(rs.wasNull() ? null : rid);
        u.setDateCreation(rs.getTimestamp("date_creation"));
        u.setActif(rs.getBoolean("actif"));
        return u;
    }

    /**
     * Calcule le hash SHA-256 d'une chaîne en hexadécimal.
     *
     * @param input La chaîne à hasher
     * @return La représentation hexadécimale du hash, ou null en cas d'erreur
     */
    public static String hashSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 non disponible: " + e.getMessage());
            return null;
        }
    }
}
