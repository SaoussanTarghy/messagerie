# Système de Planification Académique

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Java](https://img.shields.io/badge/Java-1.8+-orange)
![License](https://img.shields.io/badge/license-Academic-green)

## 📋 Description

Le **Système de Planification Académique** est une application web complète développée en Java EE qui permet la gestion efficace des ressources académiques. Cette application démontre une maîtrise des technologies Java EE modernes avec une architecture MVC + DAO, incluant Servlets, JSP, EJB Timer, et une interface utilisateur moderne avec design glassmorphism.

## ✨ Fonctionnalités Principales

### Gestion CRUD Complète
- ✅ **Créer** des ressources (enseignants, salles, cours)
- ✅ **Lire** et afficher toutes les ressources avec filtres
- ✅ **Modifier** les informations des ressources existantes
- ✅ **Supprimer** des ressources avec confirmation

### Fonctionnalités Avancées
- 🔍 **Recherche en temps réel** par mot-clé
- 🏷️ **Filtrage par type** (Enseignant, Salle, Cours)
- ⏰ **EJB Timer automatisé** - Exécution toutes les 5 minutes pour :
  - Vérification des disponibilités
  - Mise à jour des statuts de planification
  - Génération de statistiques
  - Logging des activités
- 📊 **Tableau de bord** avec statistiques en temps réel
- ⚠️ **Pages d'erreur personnalisées** (404, 500)

### Design et UX
- 🎨 **Interface moderne** avec effet glassmorphism
- 📱 **100% Responsive** - Compatible mobile, tablette, desktop
- ⚡ **Animations fluides** et micro-interactions
- 🎯 **Navigation intuitive** avec messages de confirmation

## 🛠️ Technologies Utilisées

### Backend
- **Java 1.8+**
- **Java EE (Servlets 4.0, JSP 2.3, EJB 3.2)**
- **MySQL 8.0** - Base de données relationnelle
- **JDBC** - Accès aux données avec PreparedStatement
- **Maven** - Gestion des dépendances

### Frontend
- **JSP** (JavaServer Pages avec JSTL)
- **HTML5 & CSS3**
- **JavaScript (Vanilla)** - Validation formulaires
- **Design Glassmorphism** - Effet de verre moderne

### Architecture
- **Pattern MVC** (Model-View-Controller)
- **Pattern DAO** (Data Access Object)
- **Pattern Singleton** (DatabaseConnection)
- **EJB Timer** (@Schedule pour tâches automatisées)

## 📁 Structure du Projet

```
PlanificationAcademique/
├── src/
│   ├── beans/
│   │   ├── Ressource.java              # Bean entité
│   │   └── PlanificationTimer.java     # EJB Timer @Schedule
│   ├── dao/
│   │   └── RessourceDAO.java           # Data Access Object
│   ├── servlets/
│   │   └── RessourceServlet.java       # Contrôleur principal
│   └── utils/
│       └── DatabaseConnection.java     # Gestion connexions MySQL
├── WebContent/
│   ├── WEB-INF/
│   │   └── web.xml                     # Configuration webapp
│   ├── views/
│   │   ├── listeRessources.jsp         # Liste des ressources
│   │   ├── detailRessource.jsp         # Détails ressource
│   │   └── formulaireRessource.jsp     # Formulaire add/edit
│   ├── errors/
│   │   ├── 404.jsp                     # Page erreur 404
│   │   └── 500.jsp                     # Page erreur 500
│   ├── css/
│   │   └── style.css                   # Styles glassmorphism
│   └── index.jsp                       # Page d'accueil
├── database/
│   └── planification.sql               # Script SQL complet
├── docs/
│   ├── DESCRIPTION_PROJET.md           # Description académique
│   ├── GUIDE_DEMONSTRATION.md          # Guide de démo
│   └── INSTALLATION.md                 # Guide d'installation
├── pom.xml                             # Configuration Maven
├── .gitignore                          # Fichiers à ignorer
└── README.md                           # Ce fichier
```

## 🚀 Installation et Déploiement

### Prérequis

- **JDK 1.8+** installé
- **MySQL Server 8.0+** installé et démarré
- **Serveur d'application** : Apache Tomcat 9+ ou GlassFish 5+
- **Maven 3.6+** (optionnel, pour build automatisé)
- **IDE** : Eclipse, IntelliJ IDEA, ou NetBeans

### Étape 1 : Cloner ou Télécharger le Projet

```bash
git clone <url-du-projet>
cd PlanificationAcademique
```

### Étape 2 : Créer la Base de Données

```bash
# Connectez-vous à MySQL
mysql -u root -p

# Exécutez le script SQL
source database/planification.sql
```

Ou avec un client graphique (MySQL Workbench, phpMyAdmin) :
- Importez le fichier `database/planification.sql`
- Vérifiez que la base `planification_academique` est créée avec les données de test

### Étape 3 : Configurer la Connexion à la Base de Données

Éditez `src/utils/DatabaseConnection.java` :

```java
private static final String URL = "jdbc:mysql://localhost:3306/planification_academique";
private static final String USER = "root";
private static final String PASSWORD = "votre_mot_de_passe";
```

### Étape 4 : Build avec Maven (Optionnel)

```bash
mvn clean package
```

Cela générera un fichier `PlanificationAcademique.war` dans le dossier `target/`.

### Étape 5 : Déployer sur Tomcat

#### Méthode 1 : Via Maven
```bash
mvn tomcat7:deploy
```

#### Méthode 2 : Manuellement
1. Copiez le fichier `.war` dans le dossier `webapps/` de Tomcat
2. Démarrez Tomcat :
   ```bash
   # Linux/Mac
   ./bin/catalina.sh run
   
   # Windows
   bin\catalina.bat run
   ```

#### Méthode 3 : Depuis l'IDE
1. Ajoutez Tomcat comme serveur dans votre IDE
2. Faites un clic droit sur le projet → Run As → Run on Server

### Étape 6 : Accéder à l'Application

Ouvrez votre navigateur et accédez à :
```
http://localhost:8080/PlanificationAcademique/
```

## 📖 Utilisation

### Page d'Accueil
- Affiche les statistiques globales du système
- Présente les fonctionnalités principales
- Menu de navigation vers toutes les sections

### Gestion des Ressources

#### Afficher la Liste
```
URL : http://localhost:8080/PlanificationAcademique/ressources?action=list
```
- Liste complète avec filtres par type
- Barre de recherche par mot-clé
- Actions : Voir, Modifier, Supprimer

#### Ajouter une Ressource
```
URL : http://localhost:8080/PlanificationAcademique/ressources?action=add
```
- Formulaire avec validation client et serveur
- Champs : Nom, Type, Disponibilité, Description

#### Voir les Détails
```
URL : http://localhost:8080/PlanificationAcademique/ressources?action=view&id=1
```
- Affichage complet de tous les attributs
- Historique (dates de création/modification)

#### Modifier une Ressource
```
URL : http://localhost:8080/PlanificationAcademique/ressources?action=edit&id=1
```
- Formulaire pré-rempli avec les données existantes

#### Supprimer une Ressource
```
URL : http://localhost:8080/PlanificationAcademique/ressources?action=delete&id=1
```
- Confirmation JavaScript avant suppression

### EJB Timer

Le timer s'exécute **automatiquement toutes les 5 minutes** après le déploiement. Il effectue :
- Mise à jour des statuts de planification
- Vérification des disponibilités
- Génération de statistiques
- Enregistrement dans la table `logs`

Consultez les logs du serveur pour voir les exécutions :
```
========================================
DÉBUT EXÉCUTION TIMER - [date]
✓ 2 planification(s) passée(s) à EN_COURS
✓ 1 planification(s) passée(s) à TERMINE
--- STATISTIQUES RESSOURCES ---
  ENSEIGNANT : 6 total, 5 disponibles
  SALLE : 6 total, 5 disponibles
  COURS : 6 total, 5 disponibles
FIN EXÉCUTION TIMER - Succès
========================================
```

## 🎓 Points Pédagogiques Importants

### Méthodes Servlet Démontrées

Le projet implémente **TOUTES** les méthodes requises pour un exercice académique :

#### HttpServletRequest
- ✅ `getParameter()` - Récupération des paramètres URL et formulaires
- ✅ `setAttribute()` - Passage de données aux JSP
- ✅ `getRequestDispatcher()` - Obtention du dispatcher

#### HttpServletResponse
- ✅ `sendRedirect()` - Redirection (pattern Post-Redirect-Get)

#### RequestDispatcher
- ✅ `forward()` - Transfert vers les JSP

Consultez `src/servlets/RessourceServlet.java` pour voir l'implémentation commentée de chaque méthode.

### Architecture MVC + DAO

- **Model** : `beans/Ressource.java`
- **View** : Toutes les JSP dans `WebContent/views/`
- **Controller** : `servlets/RessourceServlet.java`
- **DAO** : `dao/RessourceDAO.java`

### Sécurité

- **PreparedStatement** partout pour prévenir l'injection SQL
- **Validation côté client** (JavaScript) ET côté serveur (Java)
- **Gestion complète des exceptions** avec try-catch-finally
- **Fermeture appropriée** des ressources JDBC

## 🐛 Dépannage

### Erreur : Driver MySQL introuvable
**Solution** : Vérifiez que `mysql-connector-java` est dans le classpath ou dans `WEB-INF/lib/`

### Erreur : Connexion à la base de données échouée
**Solution** : 
- Vérifiez que MySQL est démarré
- Vérifiez les identifiants dans `DatabaseConnection.java`
- Vérifiez que la base `planification_academique` existe

### Erreur : 404 Page Not Found
**Solution ** :
- Vérifiez l'URL : doit commencer par `/PlanificationAcademique/`
- Vérifiez que le serveur est démarré
- Consultez les logs de Tomcat/GlassFish

### EJB Timer ne s'exécute pas
**Solution** :
- L'EJB Timer nécessite un serveur d'application compatible (GlassFish recommandé)
- Sur Tomcat, utilisez `@WebListener` ou des tâches planifiées alternatives
- Consultez les logs du serveur pour voir les messages du Timer

## 📊 Base de Données

### Tables Principales

#### Table `ressource`
- `id` : Identifiant unique
- `nom` : Nom de la ressource
- `type` : ENSEIGNANT | SALLE | COURS
- `disponibilite` : BOOLEAN
- `description` : TEXT
- `date_creation`, `date_modification` : TIMESTAMP

#### Table `planification`
- `id` : Identifiant unique
- `ressource_id` : Référence vers ressource
- `date_heure` : Date et heure de la planification
- `duree` : Durée en minutes
- `statut` : PLANIFIE | EN_COURS | TERMINE | ANNULE
- `notes` : TEXT

#### Table `logs`
- `id` : Identifiant unique
- `action` : Type d'action
- `details` : Détails de l'action
- `date_action` : TIMESTAMP

### Données de Test

Le script SQL inclut :
- **18 ressources** : 6 enseignants, 6 salles, 6 cours
- **10 planifications** avec différents statuts
- **Vues SQL** pour statistiques
- **Procédure stockée** pour vérification de disponibilité

## 🎨 Design

### Palette de Couleurs
- **Primaire** : #8B0000 (Bordeaux)
- **Secondaire** : #5D001E (Bordeaux foncé)
- **Accent** : #A0153E (Bordeaux clair)
- **Succès** : #28A745 (Vert)
- **Erreur** : #DC3545 (Rouge)

### Effet Glassmorphism
- Arrière-plans semi-transparents
- `backdrop-filter: blur(10px)`
- Bordures subtiles
- Ombres douces

## 📝 Auteur

Système de Planification Académique v1.0.0  
Développé avec ❤️ en Java EE

## 📄 Licence

Projet académique - Usage éducatif uniquement

---

**Pour plus d'informations** :
- Consultez `/docs/DESCRIPTION_PROJET.md` pour la description académique complète
- Consultez `/docs/GUIDE_DEMONSTRATION.md` pour le guide de démonstration
- Consultez `/docs/INSTALLATION.md` pour un guide d'installation détaillé
