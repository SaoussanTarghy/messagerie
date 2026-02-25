package beans;

import java.sql.Timestamp;

/**
 * Classe Bean représentant une ressource académique
 * Une ressource peut être un enseignant, une salle ou un cours
 * 
 * @author Système de Planification Académique
 * @version 1.0
 */
public class Ressource {
    
    // ==================== Attributs ====================
    
    private int id;
    private String nom;
    private String type; // ENSEIGNANT, SALLE, COURS
    private boolean disponibilite;
    private String description;
    private Timestamp dateCreation;
    private Timestamp dateModification;
    
    // ==================== Constructeurs ====================
    
    /**
     * Constructeur par défaut (requis pour JavaBean)
     */
    public Ressource() {
        this.disponibilite = true; // Par défaut, une ressource est disponible
    }
    
    /**
     * Constructeur avec tous les paramètres (sauf timestamps auto-générés)
     * 
     * @param nom Nom de la ressource
     * @param type Type de ressource (ENSEIGNANT, SALLE, COURS)
     * @param disponibilite Disponibilité de la ressource
     * @param description Description détaillée
     */
    public Ressource(String nom, String type, boolean disponibilite, String description) {
        this.nom = nom;
        this.type = type;
        this.disponibilite = disponibilite;
        this.description = description;
    }
    
    /**
     * Constructeur complet avec ID (pour récupération depuis BDD)
     */
    public Ressource(int id, String nom, String type, boolean disponibilite, String description, 
                     Timestamp dateCreation, Timestamp dateModification) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.disponibilite = disponibilite;
        this.description = description;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }
    
    // ==================== Getters et Setters ====================
    
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        // Validation du type
        if (type != null && (type.equals("ENSEIGNANT") || type.equals("SALLE") || type.equals("COURS"))) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("Type invalide. Doit être ENSEIGNANT, SALLE ou COURS");
        }
    }
    
    public boolean isDisponibilite() {
        return disponibilite;
    }
    
    public void setDisponibilite(boolean disponibilite) {
        this.disponibilite = disponibilite;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Timestamp getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public Timestamp getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(Timestamp dateModification) {
        this.dateModification = dateModification;
    }
    
    // ==================== Méthodes utilitaires ====================
    
    /**
     * Vérifie si la ressource est valide
     * @return true si la ressource a un nom non vide et un type valide
     */
    public boolean isValid() {
        return nom != null && !nom.trim().isEmpty() 
            && type != null && (type.equals("ENSEIGNANT") || type.equals("SALLE") || type.equals("COURS"));
    }
    
    /**
     * Retourne une représentation textuelle de la ressource
     */
    @Override
    public String toString() {
        return "Ressource{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", disponibilite=" + disponibilite +
                ", description='" + description + '\'' +
                ", dateCreation=" + dateCreation +
                ", dateModification=" + dateModification +
                '}';
    }
}
