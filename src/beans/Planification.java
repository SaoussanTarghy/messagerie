package beans;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Bean représentant une planification/réservation
 * Une planification associe des ressources (enseignant, salle, cours) à un
 * créneau horaire
 * 
 * @author Système de Planification Académique
 * @version 1.0
 */
public class Planification {

    // Attributs
    private int id;
    private int enseignantId;
    private int salleId;
    private int coursId;
    private Timestamp dateHeure;
    private int duree; // en minutes
    private String statut; // PLANIFIE, EN_COURS, TERMINE, ANNULE
    private String notes;
    private Timestamp dateCreation;

    // Attributs supplémentaires pour l'affichage (joints depuis les autres tables)
    private String enseignantNom;
    private String salleNom;
    private String coursNom;

    /**
     * Constructeur par défaut
     */
    public Planification() {
        this.statut = "PLANIFIE";
    }

    /**
     * Constructeur avec paramètres principaux
     */
    public Planification(int enseignantId, int salleId, int coursId,
            Timestamp dateHeure, int duree) {
        this.enseignantId = enseignantId;
        this.salleId = salleId;
        this.coursId = coursId;
        this.dateHeure = dateHeure;
        this.duree = duree;
        this.statut = "PLANIFIE";
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEnseignantId() {
        return enseignantId;
    }

    public void setEnseignantId(int enseignantId) {
        this.enseignantId = enseignantId;
    }

    public int getSalleId() {
        return salleId;
    }

    public void setSalleId(int salleId) {
        this.salleId = salleId;
    }

    public int getCoursId() {
        return coursId;
    }

    public void setCoursId(int coursId) {
        this.coursId = coursId;
    }

    public Timestamp getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(Timestamp dateHeure) {
        this.dateHeure = dateHeure;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getEnseignantNom() {
        return enseignantNom;
    }

    public void setEnseignantNom(String enseignantNom) {
        this.enseignantNom = enseignantNom;
    }

    public String getSalleNom() {
        return salleNom;
    }

    public void setSalleNom(String salleNom) {
        this.salleNom = salleNom;
    }

    public String getCoursNom() {
        return coursNom;
    }

    public void setCoursNom(String coursNom) {
        this.coursNom = coursNom;
    }

    /**
     * Calcule l'heure de fin de la planification
     * 
     * @return Timestamp représentant la fin
     */
    public Timestamp getDateHeureFin() {
        if (dateHeure != null) {
            long ms = dateHeure.getTime() + (duree * 60 * 1000L);
            return new Timestamp(ms);
        }
        return null;
    }

    /**
     * Formatte l'heure de début pour l'affichage (ex: "14:30")
     */
    public String getHeureDebut() {
        if (dateHeure != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(dateHeure);
        }
        return "";
    }

    /**
     * Formatte l'heure de fin pour l'affichage (ex: "16:00")
     */
    public String getHeureFin() {
        Timestamp fin = getDateHeureFin();
        if (fin != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(fin);
        }
        return "";
    }

    /**
     * Retourne le jour de la semaine (0=Dimanche, 1=Lundi, etc.)
     */
    @SuppressWarnings("deprecation")
    public int getJourSemaine() {
        if (dateHeure != null) {
            return dateHeure.getDay();
        }
        return -1;
    }

    @Override
    public String toString() {
        return "Planification{" +
                "id=" + id +
                ", cours='" + coursNom + '\'' +
                ", enseignant='" + enseignantNom + '\'' +
                ", salle='" + salleNom + '\'' +
                ", dateHeure=" + dateHeure +
                ", duree=" + duree +
                ", statut='" + statut + '\'' +
                '}';
    }
}
