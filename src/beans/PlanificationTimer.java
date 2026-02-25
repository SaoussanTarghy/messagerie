package beans;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import utils.DatabaseConnection;

/**
 * EJB Timer pour les tâches planifiées automatiques
 * Cette classe utilise @Schedule pour exécuter des tâches à intervalles
 * réguliers
 * 
 * Fonctionnalités :
 * - Vérification automatique des disponibilités
 * - Mise à jour des statuts de planification
 * - Génération de rapports périodiques
 * - Logging de toutes les activités
 * 
 * @author Système de Planification Académique
 * @version 1.0
 */
@Singleton
@Startup
public class PlanificationTimer {

    /**
     * Méthode exécutée automatiquement toutes les 5 minutes
     * 
     * @Schedule annotation permet de définir l'intervalle d'exécution
     */
    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void executerTachePlanifiee() {
        System.out.println("========================================");
        System.out.println("DÉBUT EXÉCUTION TIMER - " + new Date());
        System.out.println("========================================");

        try {
            // Tâche 1 : Vérifier et mettre à jour les planifications en cours
            mettreAJourPlanificationsEnCours();

            // Tâche 2 : Vérifier les planifications terminées
            mettreAJourPlanificationsTerminees();

            // Tâche 3 : Générer les statistiques
            genererStatistiques();

            // Tâche 4 : Logger l'exécution
            loggerExecution("Exécution Timer réussie - Toutes les tâches complétées");

            System.out.println("========================================");
            System.out.println("FIN EXÉCUTION TIMER - Succès");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("ERREUR lors de l'exécution du Timer : " + e.getMessage());
            e.printStackTrace();
            try {
                loggerExecution("ERREUR Timer : " + e.getMessage());
            } catch (Exception logError) {
                System.err.println("Impossible de logger l'erreur : " + logError.getMessage());
            }
        }
    }

    /**
     * Met à jour les planifications qui devraient être "EN_COURS"
     * Si la date_heure est passée et statut = PLANIFIE, on passe à EN_COURS
     */
    private void mettreAJourPlanificationsEnCours() {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();

            String sql = "UPDATE planification " +
                    "SET statut = 'EN_COURS' " +
                    "WHERE statut = 'PLANIFIE' " +
                    "AND date_heure <= NOW() " +
                    "AND DATE_ADD(date_heure, INTERVAL duree MINUTE) > NOW()";

            pstmt = conn.prepareStatement(sql);
            int nbMaj = pstmt.executeUpdate();

            if (nbMaj > 0) {
                System.out.println("✓ " + nbMaj + " planification(s) passée(s) à EN_COURS");
                loggerExecution(nbMaj + " planification(s) passée(s) à EN_COURS");
            }

        } catch (Exception e) {
            System.err.println("Erreur mise à jour planifications EN_COURS : " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    /**
     * Met à jour les planifications qui devraient être "TERMINE"
     * Si date_heure + durée est passée et statut = EN_COURS, on passe à TERMINE
     */
    private void mettreAJourPlanificationsTerminees() {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();

            String sql = "UPDATE planification " +
                    "SET statut = 'TERMINE' " +
                    "WHERE statut = 'EN_COURS' " +
                    "AND DATE_ADD(date_heure, INTERVAL duree MINUTE) <= NOW()";

            pstmt = conn.prepareStatement(sql);
            int nbMaj = pstmt.executeUpdate();

            if (nbMaj > 0) {
                System.out.println("✓ " + nbMaj + " planification(s) passée(s) à TERMINE");
                loggerExecution(nbMaj + " planification(s) passée(s) à TERMINE");
            }

        } catch (Exception e) {
            System.err.println("Erreur mise à jour planifications TERMINE : " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    /**
     * Génère et affiche les statistiques du système
     */
    private void genererStatistiques() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();

            // Compter les ressources par type
            String sql = "SELECT type, COUNT(*) as total, " +
                    "SUM(CASE WHEN disponibilite = 1 THEN 1 ELSE 0 END) as disponibles " +
                    "FROM ressource GROUP BY type";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            System.out.println("\n--- STATISTIQUES RESSOURCES ---");
            while (rs.next()) {
                System.out.println("  " + rs.getString("type") + " : " +
                        rs.getInt("total") + " total, " +
                        rs.getInt("disponibles") + " disponibles");
            }

            rs.close();
            pstmt.close();

            // Compter les planifications par statut
            sql = "SELECT statut, COUNT(*) as total FROM planification GROUP BY statut";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            System.out.println("\n--- STATISTIQUES PLANIFICATIONS ---");
            while (rs.next()) {
                System.out.println("  " + rs.getString("statut") + " : " + rs.getInt("total"));
            }
            System.out.println();

        } catch (Exception e) {
            System.err.println("Erreur génération statistiques : " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
    }

    /**
     * Enregistre l'activité du Timer dans la table logs
     * 
     * @param details Détails de l'action effectuée
     */
    private void loggerExecution(String details) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();

            String sql = "INSERT INTO logs (action, details) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "TIMER_EXECUTION");
            pstmt.setString(2, details);

            pstmt.executeUpdate();

        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }
}
