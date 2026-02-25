# 📘 Guide de lancement sous Windows avec Eclipse

---

## 📦 Prérequis à installer

| Outil | Lien de téléchargement |
|---|---|
| **Eclipse IDE** (Enterprise Java) | https://www.eclipse.org/downloads/ → choisir **"Eclipse IDE for Enterprise Java and Web Developers"** |
| **Java JDK 11+** | https://adoptium.net/ |
| **Apache Tomcat 10** | https://tomcat.apache.org/download-10.cgi → prendre le ZIP |
| **Node.js** | https://nodejs.org/ (version LTS) |
| **MySQL** | https://dev.mysql.com/downloads/installer/ |

---

## 🗄️ Étape 1 : Créer la base de données MySQL

1. Ouvrir **MySQL Workbench** ou l'invite de commande
2. Exécuter :

```sql
CREATE DATABASE planification_academique CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. Importer le fichier SQL du projet :
   - Dans MySQL Workbench : **File → Open SQL Script** → choisir `database/planification.sql` → Exécuter
   - Ou en ligne de commande :
   ```bash
   mysql -u root -p planification_academique < database\planification.sql
   ```

4. Si votre MySQL a un mot de passe root, modifier le fichier `src/utils/DatabaseConnection.java` :
   ```java
   private static final String PASSWORD = "votre_mot_de_passe"; // ligne 22
   ```

---

## ☕ Étape 2 : Importer et lancer avec Eclipse

### 2.1 Importer le projet Maven

1. Ouvrir Eclipse
2. **File → Import → Maven → Existing Maven Projects**
3. Cliquer **Browse...** → sélectionner le dossier du projet cloné
4. Cliquer **Finish**

### 2.2 Configurer le serveur Tomcat dans Eclipse

1. Aller dans l'onglet **Servers** en bas (si absent : **Window → Show View → Servers**)
2. Faire un clic droit dans l'onglet Servers → **New → Server**
3. Choisir **Apache → Tomcat v10.0 Server**
4. Cliquer **Browse...** → sélectionner le dossier Tomcat téléchargé
5. Cliquer **Finish**

### 2.3 Ajouter le projet au serveur

1. Double-cliquer sur votre serveur Tomcat
2. Cliquer **Add and Remove...**
3. Ajouter **PlanificationAcademique** dans la colonne "Configured"
4. Cliquer **Finish**

### 2.4 Démarrer Tomcat

1. Faire un clic droit sur le serveur → **Start**
2. Le backend Java sera accessible sur :
   👉 `http://localhost:8080/PlanificationAcademique/`

---

## ⚛️ Étape 3 : Lancer le Frontend React (Calendrier)

> **C'est cette étape qui affiche le CALENDRIER !**

1. Double-cliquer sur **`LANCER_FRONTEND.bat`** dans le dossier du projet
2. Une fenêtre noire s'ouvre et installe automatiquement les dépendances
3. Le navigateur s'ouvre automatiquement sur :
   👉 **http://localhost:5173/**

> ⚠️ Node.js doit être installé pour que cette étape fonctionne.

---

## ✅ Résumé : ordre de lancement

```
1. Démarrer MySQL (via les Services Windows ou MySQL Workbench)
2. Démarrer Tomcat depuis Eclipse → Start Server
3. Double-cliquer LANCER_FRONTEND.bat
4. Ouvrir http://localhost:5173/ dans le navigateur
```

---

## 🔧 Problèmes fréquents

| Problème | Solution |
|---|---|
| Erreur de connexion MySQL | Vérifier le mot de passe dans `DatabaseConnection.java` |
| Port 8080 déjà utilisé | Changer le port Tomcat dans Eclipse (Server → double-clic → changer port) |
| `npm` non reconnu | Réinstaller Node.js et redémarrer Windows |
| Page 404 sur `/PlanificationAcademique/` | Vérifier que le projet est bien ajouté au serveur Tomcat |
