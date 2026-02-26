-- ========================================
-- Table utilisateurs + données de test
-- Ajouter après avoir exécuté planification.sql
-- ========================================
USE planification_academique;

-- ========================================
-- Table : utilisateurs
-- ========================================
CREATE TABLE IF NOT EXISTS utilisateurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe_hash VARCHAR(64) NOT NULL COMMENT 'SHA-256 en hex',
    role ENUM('ETUDIANT', 'ADMIN', 'ENSEIGNANT') NOT NULL DEFAULT 'ETUDIANT',
    ressource_id INT NULL COMMENT 'Ref vers ressource si ENSEIGNANT ou ETUDIANT',
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actif BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (ressource_id) REFERENCES ressource(id) ON DELETE SET NULL,
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- Données de test
-- Mots de passe : SHA-256 de "password123"
-- = ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f
-- ========================================
INSERT INTO utilisateurs (nom, email, mot_de_passe_hash, role, ressource_id) VALUES
(
    'Administrateur Système',
    'admin@academie.ma',
    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
    'ADMIN',
    NULL
),
(
    'Dr. Mohammed ALAMI',
    'alami@academie.ma',
    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
    'ENSEIGNANT',
    1  -- référence le Dr. Mohammed ALAMI dans la table ressource
),
(
    'Étudiant Ahmed ZIANI',
    'etudiant@academie.ma',
    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
    'ETUDIANT',
    NULL
);

SELECT '==============================' as '';
SELECT 'TABLE UTILISATEURS CRÉÉE !' as '';
SELECT 'Comptes disponibles :' as '';
SELECT email, role FROM utilisateurs;
SELECT 'Mot de passe par défaut : password123' as '';
SELECT '==============================' as '';
