-- ========================================
-- Base de données : Planification Académique
-- Auteur : Système de Gestion Académique
-- Date : 2026
-- Description : Schéma complet pour la gestion des ressources et planifications
-- ========================================

-- Création de la base de données
DROP DATABASE IF EXISTS planification_academique;
CREATE DATABASE planification_academique CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE planification_academique;

-- ========================================
-- Table : ressource
-- Description : Stocke toutes les ressources académiques (enseignants, salles, cours)
-- ========================================
CREATE TABLE ressource (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    type ENUM('ENSEIGNANT', 'SALLE', 'COURS') NOT NULL,
    disponibilite BOOLEAN DEFAULT TRUE,
    description TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (type),
    INDEX idx_disponibilite (disponibilite),
    INDEX idx_nom (nom)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- Table : planification
-- Description : Gère les planifications des ressources
-- ========================================
CREATE TABLE planification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ressource_id INT NOT NULL,
    date_heure DATETIME NOT NULL,
    duree INT NOT NULL COMMENT 'Durée en minutes',
    statut ENUM('PLANIFIE', 'EN_COURS', 'TERMINE', 'ANNULE') DEFAULT 'PLANIFIE',
    notes TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ressource_id) REFERENCES ressource(id) ON DELETE CASCADE,
    INDEX idx_date_heure (date_heure),
    INDEX idx_statut (statut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- Table : logs
-- Description : Trace l'activité du système et de l'EJB Timer
-- ========================================
CREATE TABLE logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    date_action TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_date_action (date_action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- Insertion des données de test : ENSEIGNANTS
-- ========================================
INSERT INTO ressource (nom, type, disponibilite, description) VALUES
('Dr. Mohammed ALAMI', 'ENSEIGNANT', TRUE, 'Professeur de Génie Logiciel et Architectures Distribuées. Spécialiste Java EE et Spring Framework.'),
('Pr. Fatima BENCHEKROUN', 'ENSEIGNANT', TRUE, 'Professeure de Bases de Données et Systèmes d''Information. Expert en optimisation SQL et Big Data.'),
('Dr. Youssef TAHIRI', 'ENSEIGNANT', FALSE, 'Professeur de Développement Web et Technologies Frontend. Actuellement en congé sabbatique.'),
('Pr. Samira IDRISSI', 'ENSEIGNANT', TRUE, 'Professeure de Réseaux et Sécurité Informatique. Certifiée CISSP et CEH.'),
('Dr. Karim BENNANI', 'ENSEIGNANT', TRUE, 'Professeur d''Intelligence Artificielle et Machine Learning. Chercheur en Deep Learning.'),
('Pr. Laila HAMMADI', 'ENSEIGNANT', TRUE, 'Professeure de Gestion de Projets et Méthodologies Agiles. Scrum Master certifiée.');

-- ========================================
-- Insertion des données de test : SALLES
-- ========================================
INSERT INTO ressource (nom, type, disponibilite, description) VALUES
('Amphi A - Bloc Sciences', 'SALLE', TRUE, 'Grand amphithéâtre de 200 places. Équipé projecteur HD, système audio, climatisation.'),
('Salle TP Informatique 1', 'SALLE', TRUE, 'Laboratoire informatique 30 postes. Windows 11, Visual Studio, IntelliJ IDEA, MySQL.'),
('Salle TP Informatique 2', 'SALLE', FALSE, 'En maintenance - Mise à niveau réseau et installation nouveaux ordinateurs.'),
('Salle TD 101', 'SALLE', TRUE, 'Salle de 40 places pour travaux dirigés. Tableau interactif, WiFi haut débit.'),
('Salle Conférence', 'SALLE', TRUE, 'Salle équipée pour visioconférences. Système Zoom, écrans multiples, 50 places.'),
('Laboratoire Réseaux', 'SALLE', TRUE, 'Lab spécialisé avec switchs Cisco, routeurs, outils de simulation réseau. 25 postes.');

-- ========================================
-- Insertion des données de test : COURS
-- ========================================
INSERT INTO ressource (nom, type, disponibilite, description) VALUES
('Développement JEE Avancé', 'COURS', TRUE, 'Servlets, JSP, EJB, JPA, Spring Boot. Projets pratiques et études de cas réels.'),
('Base de Données Avancées', 'COURS', TRUE, 'MySQL, PostgreSQL, MongoDB. Optimisation, indexation, transactions, procédures stockées.'),
('Frameworks JavaScript', 'COURS', TRUE, 'React, Vue.js, Angular. Développement d''applications web modernes et responsives.'),
('Sécurité des Systèmes', 'COURS', TRUE, 'Cryptographie, authentification, pare-feu, détection d''intrusions, audit de sécurité.'),
('Machine Learning Pratique', 'COURS', TRUE, 'Python, TensorFlow, Scikit-learn. Algorithmes supervisés et non supervisés, projets IA.'),
('Architecture Microservices', 'COURS', FALSE, 'Cours en préparation - Docker, Kubernetes, API REST, messagerie asynchrone.');

-- ========================================
-- Insertion des planifications de test
-- ========================================
INSERT INTO planification (ressource_id, date_heure, duree, statut, notes) VALUES
(1, '2026-02-17 09:00:00', 120, 'PLANIFIE', 'Cours JEE - Amphi A - Introduction aux Servlets et cycle de vie'),
(2, '2026-02-17 14:00:00', 90, 'PLANIFIE', 'TD Bases de Données - Salle TD 101 - Exercices sur les jointures SQL'),
(7, '2026-02-18 10:00:00', 180, 'PLANIFIE', 'TP Développement JEE - TP Info 1 - Création application CRUD avec Servlets'),
(5, '2026-02-18 15:00:00', 120, 'PLANIFIE', 'Cours Machine Learning - Salle Conférence - Algorithmes de classification'),
(8, '2026-02-19 08:30:00', 150, 'EN_COURS', 'Examen Base de Données - Amphi A - QCM et exercices pratiques'),
(4, '2026-02-19 13:00:00', 120, 'PLANIFIE', 'Cours Sécurité - Salle TD 101 - Cryptographie symétrique et asymétrique'),
(9, '2026-02-20 09:00:00', 180, 'PLANIFIE', 'TP Frameworks JS - TP Info 1 - Développement application React'),
(6, '2026-02-20 14:00:00', 90, 'TERMINE', 'Séminaire Gestion de Projets - Salle Conférence - Méthodologie Scrum (Terminé)'),
(1, '2026-02-21 10:00:00', 120, 'ANNULE', 'Cours JEE annulé - Professeur en déplacement académique'),
(11, '2026-02-21 15:00:00', 150, 'PLANIFIE', 'Examen Machine Learning - Amphi A - Projet final à présenter');

-- ========================================
-- Insertion de logs initiaux
-- ========================================
INSERT INTO logs (action, details) VALUES
('SYSTEM_INIT', 'Base de données initialisée avec succès. Tables créées : ressource, planification, logs.'),
('DATA_IMPORT', 'Importation de 18 ressources : 6 enseignants, 6 salles, 6 cours.'),
('DATA_IMPORT', 'Importation de 10 planifications de test avec différents statuts.'),
('TIMER_READY', 'Système de planification automatique (EJB Timer) prêt à être déployé.');

-- ========================================
-- Vue : Statistiques des ressources
-- ========================================
CREATE VIEW vue_statistiques_ressources AS
SELECT 
    type,
    COUNT(*) as total,
    SUM(CASE WHEN disponibilite = TRUE THEN 1 ELSE 0 END) as disponibles,
    SUM(CASE WHEN disponibilite = FALSE THEN 1 ELSE 0 END) as indisponibles
FROM ressource
GROUP BY type;

-- ========================================
-- Vue : Planifications à venir
-- ========================================
CREATE VIEW vue_planifications_futures AS
SELECT 
    p.id,
    r.nom as ressource_nom,
    r.type as ressource_type,
    p.date_heure,
    p.duree,
    p.statut,
    p.notes
FROM planification p
JOIN ressource r ON p.ressource_id = r.id
WHERE p.date_heure > NOW() AND p.statut = 'PLANIFIE'
ORDER BY p.date_heure ASC;

-- ========================================
-- Procédure stockée : Vérifier disponibilité
-- ========================================
DELIMITER //
CREATE PROCEDURE VerifierDisponibilite(IN ressource_id_param INT)
BEGIN
    SELECT 
        r.id,
        r.nom,
        r.type,
        r.disponibilite,
        COUNT(p.id) as planifications_futures
    FROM ressource r
    LEFT JOIN planification p ON r.id = p.ressource_id 
        AND p.date_heure > NOW() 
        AND p.statut = 'PLANIFIE'
    WHERE r.id = ressource_id_param
    GROUP BY r.id, r.nom, r.type, r.disponibilite;
END //
DELIMITER ;

-- ========================================
-- Affichage des statistiques
-- ========================================
SELECT '========================================' as '';
SELECT 'STATISTIQUES DE LA BASE DE DONNÉES' as '';
SELECT '========================================' as '';
SELECT * FROM vue_statistiques_ressources;
SELECT '' as '';
SELECT 'Total de planifications :' as '', COUNT(*) as total FROM planification;
SELECT 'Planifications à venir :' as '', COUNT(*) as total FROM vue_planifications_futures;
SELECT '' as '';
SELECT '========================================' as '';
SELECT 'BASE DE DONNÉES PRÊTE !' as '';
SELECT '========================================' as '';
