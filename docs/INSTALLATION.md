# Guide d'Installation Détaillé - Système de Planification Académique

## 📋 Table des Matières

1. [Prérequis Système](#prérequis-système)
2. [Installation desPrérequis](#installation-des-prérequis)
3. [Configuration de la Base de Données](#configuration-de-la-base-de-données)
4. [Configuration du Projet](#configuration-du-projet)
5. [Déploiement sur Tomcat](#déploiement-sur-tomcat)
6. [Déploiement sur GlassFish](#déploiement-sur-glassfish)
7. [Vérification de l'Installation](#vérification-de-linstallation)
8. [Dépannage](#dépannage)

## Prérequis Système

### Configuration Minimale

- **Système d'exploitation** : Windows 10+, macOS 10.14+, ou Linux (Ubuntu 18.04+)
- **RAM** : 4 GB minimum, 8 GB recommandé
- **Espace disque** : 2 GB disponible
- **Connexion Internet** : Pour télécharger les dépendances Maven

### Logiciels Requis

- **JDK 1.8 ou supérieur**
- **MySQL Server 8.0 ou supérieur**
- **Apache Tomcat 9.0** (ou GlassFish 5.0 pour EJB Timer)
- **Maven 3.6+** (optionnel mais recommandé)
- **IDE** : Eclipse, IntelliJ IDEA, ou NetBeans (optionnel)

## Installation des Prérequis

### 1. Installation du JDK

#### Windows
1. Télécharger le JDK depuis [Oracle](https://www.oracle.com/java/technologies/downloads/)
2. Exécuter l'installateur
3. Configurer la variable d'environnement `JAVA_HOME` :
   ```
   JAVA_HOME=C:\Program Files\Java\jdk1.8.0_xxx
   ```
4. Ajouter `%JAVA_HOME%\bin` au PATH
5. Vérifier l'installation :
   ```cmd
   java -version
   javac -version
   ```

#### macOS
```bash
# Avec Homebrew
brew install openjdk@8

# Configurer JAVA_HOME dans ~/.zshrc ou ~/.bash_profile
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export PATH=$JAVA_HOME/bin:$PATH

# Vérifier
java -version
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-8-jdk

# Vérifier
java -version
javac -version
```

### 2. Installation de MySQL

#### Windows
1. Télécharger MySQL Installer depuis [MySQL Downloads](https://dev.mysql.com/downloads/installer/)
2. Choisir "MySQL Server" et "MySQL Workbench"
3. Définir un mot de passe root
4. Démarrer le service MySQL

#### macOS
```bash
# Avec Homebrew
brew install mysql

# Démarrer MySQL
brew services start mysql

# Sécuriser l'installation
mysql_secure_installation
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install mysql-server

# Démarrer MySQL
sudo systemctl start mysql
sudo systemctl enable mysql

# Configurer
sudo mysql_secure_installation
```

### 3. Installation de Tomcat 9

#### Windows
1. Télécharger depuis [Tomcat Downloads](https://tomcat.apache.org/download-90.cgi)
2. Extraire dans `C:\Program Files\Apache\Tomcat9`
3. Configurer `CATALINA_HOME` :
   ```
   CATALINA_HOME=C:\Program Files\Apache\Tomcat9
   ```
4. Démarrer : `bin\startup.bat`

#### macOS/Linux
```bash
# Télécharger
cd /opt
sudo wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.80/bin/apache-tomcat-9.0.80.tar.gz
sudo tar -xzf apache-tomcat-9.0.80.tar.gz
sudo mv apache-tomcat-9.0.80 tomcat9

# Configurer les permissions
sudo chmod +x /opt/tomcat9/bin/*.sh

# Démarrer
sudo /opt/tomcat9/bin/startup.sh

# Accéder à http://localhost:8080
```

### 4. Installation de Maven (Optionnel)

#### Windows
1. Télécharger depuis [Maven Downloads](https://maven.apache.org/download.cgi)
2. Extraire dans `C:\Program Files\Apache\Maven`
3. Configurer `M2_HOME` et ajouter `%M2_HOME%\bin` au PATH
4. Vérifier : `mvn -version`

#### macOS
```bash
brew install maven
mvn -version
```

#### Linux
```bash
sudo apt install maven
mvn -version
```

## Configuration de la Base de Données

### Étape 1 : Connexion à MySQL

```bash
# Connexion en tant que root
mysql -u root -p
# Entrer le mot de passe
```

### Étape 2 : Exécution du Script SQL

#### Méthode 1 : Depuis la ligne de commande
```bash
mysql -u root -p < /chemin/vers/database/planification.sql
```

#### Méthode 2 : Depuis MySQL Client
```sql
-- Se connecter d'abord
mysql -u root -p

-- Puis exécuter
source /chemin/vers/database/planification.sql;
```

#### Méthode 3 : Avec MySQL Workbench
1. Ouvrir MySQL Workbench
2. Se connecter au serveur local
3. File → Open SQL Script
4. Sélectionner `database/planification.sql`
5. Cliquer sur l'icône éclair (Execute)

### Étape 3 : Vérification

```sql
-- Vérifier que la base existe
SHOW DATABASES LIKE 'planification_academique';

-- Utiliser la base
USE planification_academique;

-- Lister les tables
SHOW TABLES;

-- Vérifier les données
SELECT COUNT(*) FROM ressource;
SELECT COUNT(*) FROM planification;

-- Devrait afficher 18 ressources et 10 planifications
```

### Étape 4 : Création d'un Utilisateur (Recommandé)

```sql
-- Créer un utilisateur dédié (plus sécurisé que root)
CREATE USER 'planif_user'@'localhost' IDENTIFIED BY 'motdepasse123';

-- Donner les permissions
GRANT ALL PRIVILEGES ON planification_academique.* TO 'planif_user'@'localhost';

-- Appliquer les changements
FLUSH PRIVILEGES;
```

## Configuration du Projet

### Étape 1 : Récupérer le Projet

```bash
# Si vous avez Git
git clone <url-du-projet>
cd PlanificationAcademique

# Ou extraire le fichier ZIP
unzip PlanificationAcademique.zip
cd PlanificationAcademique
```

### Étape 2 : Configurer la Connexion à la BDD

Éditer `src/utils/DatabaseConnection.java` :

```java
private static final String URL = "jdbc:mysql://localhost:3306/planification_academique";
private static final String USER = "planif_user";  // ou "root"
private static final STRING PASSWORD = "motdepasse123";  // votre mot de passe
```

⚠️ **Important** : N'oubliez pas de modifier ces valeurs selon votre configuration !

### Étape 3 : Compiler le Projet

#### Avec Maven
```bash
# Nettoyer et compiler
mvn clean compile

# Créer le WAR
mvn package

# Le fichier WAR sera dans target/PlanificationAcademique.war
```

#### Avec un IDE (Eclipse)

1. File → Import → Existing Maven Projects
2. Sélectionner le dossier du projet
3. Clic droit sur le projet → Maven → Update Project
4. Clic droit → Run As → Maven build
5. Goals : `clean package`

#### Compilation Manuelle (Sans Maven)

```bash
# Créer les répertoires
mkdir -p build/WEB-INF/classes
mkdir -p build/WEB-INF/lib

# Copier les librairies nécessaires
cp mysql-connector-java-8.0.33.jar build/WEB-INF/lib/
cp jstl-1.2.jar build/WEB-INF/lib/

# Compiler les sources Java
javac -d build/WEB-INF/classes -cp "build/WEB-INF/lib/*" src/**/*.java

# Copier les fichiers web
cp -r WebContent/* build/

# Créer le WAR
cd build
jar -cvf ../PlanificationAcademique.war *
cd ..
```

## Déploiement sur Tomcat

### Méthode 1 : Copie Manuelle

```bash
# Copier le WAR dans webapps
cp target/PlanificationAcademique.war /opt/tomcat9/webapps/

# Tomcat déploie automatiquement le WAR
# Attendre quelques secondes
```

### Méthode 2 : Manager Web UI

1. Accéder à `http://localhost:8080/manager`
2. Configurer d'abord un utilisateur dans `conf/tomcat-users.xml` :
   ```xml
   <role rolename="manager-gui"/>
   <user username="admin" password="admin" roles="manager-gui"/>
   ```
3. Redémarrer Tomcat
4. Se connecter au Manager
5. Section "WAR file to deploy" → Choisir le fichier → Deploy

### Méthode 3 : Maven Plugin

Ajouter dans `pom.xml` :
```xml
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <url>http://localhost:8080/manager/text</url>
        <server>TomcatServer</server>
        <path>/PlanificationAcademique</path>
    </configuration>
</plugin>
```

Puis déployer :
```bash
mvn tomcat7:deploy
# ou pour redéployer
mvn tomcat7:redeploy
```

### Méthode 4 : Depuis l'IDE

#### Eclipse
1. Window → Show View → Servers
2. Clic droit dans Servers → New → Server
3. Choisir "Apache Tomcat v9.0"
4. Configurer le chemin d'installation de Tomcat
5. Finish
6. Clic droit sur le projet → Run As → Run on Server
7. Choisir le serveur Tomcat configuré

#### IntelliJ IDEA
1. Run → Edit Configurations
2. + → Tomcat Server → Local
3. Configure → Chemin vers Tomcat
4. Deployment tab → + → Artifact → WAR exploded
5. Apply → OK
6. Run → Run 'Tomcat'

## Déploiement sur GlassFish (Recommandé pour EJB Timer)

### Installation de GlassFish

```bash
# Télécharger GlassFish 5.1
wget https://download.eclipse.org/ee4j/glassfish/glassfish-5.1.0.zip

# Extraire
unzip glassfish-5.1.0.zip
cd glassfish5

# Démarrer
bin/asadmin start-domain
```

### Déploiement

```bash
# Méthode 1 : Ligne de commande
bin/asadmin deploy /chemin/vers/PlanificationAcademique.war

# Méthode 2 : Console Web
# Accéder à http://localhost:4848
# Applications → Deploy → Choisir le WAR → OK
```

### Configuration du Pool de Connexions

1. Console GlassFish → Resources → JDBC → JDBC Connection Pools
2. New → Pool Name: `PlanifPool`, Resource Type: `javax.sql.DataSource`
3. Database Driver Vendor: MySQL
4. Propriétés :
   - ServerName: `localhost`
   - PortNumber: `3306`
   - DatabaseName: `planification_academique`
   - User: `planif_user`
   - Password: `motdepasse123`
5. Sauvegarder et Ping pour tester

## Vérification de l'Installation

### 1. Vérifier le Démarrage du Serveur

**Tomcat** :
```bash
# Vérifier les logs
tail -f /opt/tomcat9/logs/catalina.out

# Rechercher :
# INFO [main] org.apache.catalina.startup.HostConfig.deployWAR Deployment of web application archive [**/PlanificationAcademique.war] has finished
```

**GlassFish** :
```bash
# Vérifier le domaine
bin/asadmin list-applications

# Devrait afficher PlanificationAcademique
```

### 2. Tester l'Application

Ouvrir un navigateur et accéder à :
```
http://localhost:8080/PlanificationAcademique/
```

Vous devriez voir la page d'accueil avec les statistiques.

### 3. Vérifier l'EJB Timer (GlassFish seulement)

```bash
# Observer les logs
tail -f glassfish5/glassfish/domains/domain1/logs/server.log

# Toutes les 5 minutes, vous devriez voir :
# ========================================
# DÉBUT EXÉCUTION TIMER - [date]
# ✓ X planification(s) passée(s) à EN_COURS
# FIN EXÉCUTION TIMER - Succès
# ========================================
```

### 4. Tester les Fonctionnalités

- [ ] Page d'accueil s'affiche correctement
- [ ] Liste des ressources affiche les 18 ressources
- [ ] Filtres fonctionnent (Enseignants, Salles, Cours)
- [ ] Recherche fonctionne
- [ ] Ajout d'une ressource réussit
- [ ] Modification d'une ressource réussit
- [ ] Suppression d'une ressource réussit
- [ ] Page 404 s'affiche pour URL inexistante
- [ ] Design responsive fonctionne

## Dépannage

### Problème : Erreur "Driver MySQL introuvable"

**Symptôme** : `java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver`

**Solution** :
1. Vérifier que `mysql-connector-java-8.0.33.jar` est dans `WEB-INF/lib/`
2. Si vous utilisez Maven, vérifier `pom.xml` :
   ```xml
   <dependency>
       <groupId>mysql</groupId>
       <artifactId>mysql-connector-java</artifactId>
       <version>8.0.33</version>
   </dependency>
   ```
3. Rebuild : `mvn clean package`

### Problème : Connexion à MySQL échouée

**Symptôme** : `SQLException: Access denied for user 'root'@'localhost'`

**Solution** :
1. Vérifier le mot de passe MySQL
2. S'assurer que MySQL est démarré :
   ```bash
   # Linux/Mac
   sudo systemctl status mysql
   
   # Windows
   # Services → MySQL → Démarré
   ```
3. Vérifier les permissions :
   ```sql
   SHOW GRANTS FOR 'planif_user'@'localhost';
   ```

### Problème : Page 404 Not Found

**Solutions** :
1. Vérifier l'URL : doit être `/PlanificationAcademique/` (avec le nom du contexte)
2. Vérifier que le WAR est déployé :
   ```bash
   ls -la /opt/tomcat9/webapps/
   # Devrait montrer PlanificationAcademique.war et PlanificationAcademique/
   ```
3. Consulter les logs : `/opt/tomcat9/logs/catalina.out`

### Problème : EJB Timer ne fonctionne pas sur Tomcat

**Explication** : Tomcat est un conteneur de servlets, pas un serveur d'application complet. Il ne supporte pas nativement les EJB.

**Solutions** :
1. **Option 1** : Utiliser GlassFish ou WildFly
2. **Option 2** : Remplacer EJB Timer par `@WebListener` avec `ScheduledExecutorService`
3. **Option 3** : Utiliser Quartz Scheduler

### Problème : Erreurs de compilation Maven

**Symptôme** : Erreurs lors de `mvn compile`

**Solutions** :
1. Nettoyer le projet :
   ```bash
   mvn clean
   rm -rf target/
   ```
2. Mise à jour des dépendances :
   ```bash
   mvn clean install -U
   ```
3. Vérifier la version de Maven : `mvn -version` (minimum 3.6)

### Problème : Encodage des caractères (accents)

**Symptôme** : Les accents s'affichent mal (é → Ã©)

**Solutions** :
1. Vérifier `web.xml` :
   ```xml
   <jsp-config>
       <jsp-property-group>
           <url-pattern>*.jsp</url-pattern>
           <page-encoding>UTF-8</page-encoding>
       </jsp-property-group>
   </jsp-config>
   ```
2. Connector Tomcat dans `server.xml` :
   ```xml
   <Connector port="8080" protocol="HTTP/1.1"
              URIEncoding="UTF-8"/>
   ```
3. Redémarrer Tomcat

### Problème : Permissions sur Linux/Mac

**Symptôme** : Permission denied lors du déploiement

**Solutions** :
```bash
# Donner les permissions sur Tomcat
sudo chown -R $USER:$USER /opt/tomcat9

# Ou exécuter en tant que root (déconseillé)
sudo /opt/tomcat9/bin/startup.sh
```

## Support et Aide

### Logs Utiles

**Tomcat** :
- Logs généraux : `/opt/tomcat9/logs/catalina.out`
- Logs d'application : `/opt/tomcat9/logs/localhost.YYYY-MM-DD.log`

**GlassFish** :
- Logs serveur : `glassfish5/glassfish/domains/domain1/logs/server.log`

**MySQL** :
- Logs erreurs Linux : `/var/log/mysql/error.log`
- Logs erreurs Mac : `/usr/local/var/mysql/*.err`

### Commandes Utiles

```bash
# Vérifier les ports
netstat -an | grep 8080  # Tomcat
netstat -an | grep 3306  # MySQL

# Arrêter/Démarrer Tomcat
/opt/tomcat9/bin/shutdown.sh
/opt/tomcat9/bin/startup.sh

# Arrêter/Démarrer MySQL
sudo systemctl stop mysql
sudo systemctl start mysql

# Voir les processus Java
jps -v
```

---

**🎉 Installation terminée !** Votre système est maintenant opérationnel.

Pour toute question, consultez le `README.md` ou le `GUIDE_DEMONSTRATION.md`.
