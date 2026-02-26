package beans;

import java.sql.Timestamp;

/**
 * Bean représentant un utilisateur du système
 * Rôles possibles : ETUDIANT, ADMIN, ENSEIGNANT
 *
 * @author Système de Planification Académique
 * @version 2.0
 */
public class Utilisateur {

    private int id;
    private String nom;
    private String email;
    private String motDePasseHash; // SHA-256 hex
    private String role; // ETUDIANT | ADMIN | ENSEIGNANT
    private Integer ressourceId; // Null pour ADMIN, référence ressource pour ENSEIGNANT/ETUDIANT
    private Timestamp dateCreation;
    private boolean actif;

    // ==================== Constructeurs ====================

    public Utilisateur() {
        this.actif = true;
    }

    public Utilisateur(int id, String nom, String email, String role, Integer ressourceId) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.role = role;
        this.ressourceId = ressourceId;
        this.actif = true;
    }

    // ==================== Getters & Setters ====================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getRessourceId() {
        return ressourceId;
    }

    public void setRessourceId(Integer ressourceId) {
        this.ressourceId = ressourceId;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    // ==================== Méthodes utilitaires ====================

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isEnseignant() {
        return "ENSEIGNANT".equals(role);
    }

    public boolean isEtudiant() {
        return "ETUDIANT".equals(role);
    }

    /**
     * Retourne true si l'utilisateur a accès au dashboard admin (ADMIN ou
     * ENSEIGNANT)
     */
    public boolean hasAdminAccess() {
        return "ADMIN".equals(role) || "ENSEIGNANT".equals(role);
    }

    @Override
    public String toString() {
        return "Utilisateur{id=" + id + ", nom='" + nom + "', email='" + email
                + "', role='" + role + "', actif=" + actif + "}";
    }
}
