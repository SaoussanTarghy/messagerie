# Guide de Démonstration - Système de Planification Académique

## 🎯 Objectif de la Démonstration

Ce guide vous accompagne pas à pas dans la démonstration complète du Système de Planification Académique. Il est conçu pour mettre en valeur toutes les fonctionnalités implémentées et les aspects techniques importants du projet.

## 🚀 Préparation Avant la Démonstration

### Vérifications Préalables

1. **Base de données** : S'assurer que MySQL est démarré et que la base `planification_academique` contient les données de test
2. **Serveur** : Démarrer Tomcat/GlassFish et vérifier que l'application est déployée
3. **Navigation** : Ouvrir l'URL `http://localhost:8080/PlanificationAcademique/` dans un navigateur moderne
4. **Console** : Avoir une fenêtre avec les logs du serveur visible pour montrer l'EJB Timer

### Points à Souligner

- Architecture MVC + DAO professionnelle
- Toutes les méthodes Servlet requises implémentées
- Sécurité avec PreparedStatement
- EJB Timer automatisé
- Design moderne avec glassmorphism
- 100% responsive

## 📋 Scénario de Démonstration

### 1. Page d'Accueil (2-3 minutes)

**URL** : `http://localhost:8080/PlanificationAcademique/`

**Points à démontrer** :
- Design moderne avec effet glassmorphism
- Statistiques en temps réel calculées depuis la base de données
- Navigation claire et intuitive
- Présentation des fonctionnalités principales

**Ce qu'il faut dire** :
> "La page d'accueil présente une vue d'ensemble du système avec des statistiques calculées dynamiquement depuis la base de données MySQL. Vous pouvez voir que nous avons actuellement [X] enseignants, [Y] salles et [Z] cours dans le système. Le design utilise l'effet glassmorphism moderne avec des arrière-plans semi-transparents et des effets de flou."

**Actions à effectuer** :
1. Montrer les cartes de statistiques animées
2. Scroller pour montrer la section des fonctionnalités
3. Pointer la navigation en haut de page

### 2. Liste des Ressources (3-4 minutes)

**URL** : `http://localhost:8080/PlanificationAcademique/ressources?action=list`

**Points à démontrer** :
- Affichage de toutes les ressources dans un tableau professionnel
- Filtres par type (Enseignant, Salle, Cours)
- Barre de recherche fonctionnelle
- Badges de disponibilité colorés
- Actions disponibles (Voir, Modifier, Supprimer)

**Ce qu'il faut dire** :
> "Cette page implémente la méthode getParameter() pour récupérer les filtres et setAttribute() pour passer les données à la JSP. Le servlet utilise getRequestDispatcher() puis forward() pour afficher la vue. Tous les accès à la base de données passent par le RessourceDAO qui utilise systématiquement des PreparedStatement pour prévenir l'injection SQL."

**Actions à effectuer** :
1. Cliquer sur le filtre "Enseignants" pour montrer le filtrage
2. Utiliser la recherche pour trouver une ressource spécifique (ex: "Mohammed")
3. Montrer les badges de disponibilité (vert = disponible, rouge = indisponible)
4. Expliquer les icônes d'action (œil pour voir, crayon pour modifier, poubelle pour supprimer)

### 3. Détails d'une Ressource (2 minutes)

**URL** : Cliquer sur l'icône "👁️" d'une ressource

**Points à démontrer** :
- Affichage complet de tous les attributs
- Indicateur de disponibilité animé (pulsant)
- Dates de création et modification automatiques
- Boutons d'action bien visibles

**Ce qu'il faut dire** :
> "La page de détails récupère l'ID via getParameter(), interroge le DAO pour obtenir la ressource, puis passe les données à la JSP via setAttribute(). Si la ressource n'existe pas, une erreur 404 personnalisée est affichée. L'indicateur de disponibilité utilise une animation CSS pour attirer l'attention."

**Actions à effectuer** :
1. Montrer toutes les informations affichées
2. Pointer l'indicateur de disponibilité pulsant
3. Montrer les dates de création/modification

### 4. Ajout d'une Ressource (4-5 minutes)

**URL** : Cliquer sur "+ Nouvelle Ressource" dans la navigation

**Points à démontrer** :
- Formulaire avec validation client (JavaScript)
- Champs requis marqués avec *
- Information contextuelle (hints)
- Soumission et redirection

**Ce qu'il faut dire** :
> "Le formulaire implémente une double validation : côté client avec JavaScript pour l'ergonomie, et côté serveur dans le servlet pour la sécurité. Après la soumission en POST, le servlet utilise sendRedirect() pour implémenter le pattern Post-Redirect-Get qui évite la resoumission accidentelle du formulaire lors d'un rafraîchissement de page."

**Actions à effectuer** :
1. Remplir le formulaire avec une nouvelle ressource :
   - Nom : "Dr. Exemple DEMO"
   - Type : Enseignant
   - Disponibilité : coché
   - Description : "Ajouté lors de la démonstration du système"
2. Essayer de soumettre sans remplir le nom pour montrer la validation
3. Soumettre le formulaire complet
4. Montrer le message de succès "Ressource ajoutée avec succès !"

### 5. Modification d'une Ressource (3 minutes)

**URL** : Cliquer sur l'icône "✏️" de la ressource qu'on vient de créer

**Points à démontrer** :
- Formulaire pré-rempli avec les données existantes
- Modification des informations
- Mise à jour réussie

**Ce qu'il faut dire** :
> "Le formulaire de modification est le même que celui d'ajout, mais le servlet détecte la présence d'un ID pour savoir s'il s'agit d'un UPDATE ou d'un INSERT. Les dates sont gérées automatiquement par MySQL avec les triggers ON UPDATE CURRENT_TIMESTAMP."

**Actions à effectuer** :
1. Modifier la description pour ajouter quelque chose
2. Changer la disponibilité
3. Soumettre
4. Vérifier le message "Ressource modifiée avec succès !"
5. Retourner voir les détails pour confirmer les changements

### 6. Recherche (2 minutes)

**URL** : Retour à la liste, utiliser la barre de recherche

**Points à démontrer** :
- Recherche en temps réel
- Résultats filtrés
- Recherche dans nom ET description

**Ce qu'il faut dire** :
> "La recherche utilise une requête SQL avec LIKE pour chercher dans les champs nom et description. Le DAO construit un pattern '%keyword%' pour trouver les correspondances partielles."

**Actions à effectuer** :
1. Rechercher "Java" (devrait trouver les cours avec Java)
2. Rechercher "Salle" (devrait trouver toutes les salles)
3. Vider la recherche pour voir toutes les ressources

### 7. Suppression d'une Ressource (2 minutes)

**URL** : Cliquer sur l'icône "🗑️" de la ressource de démonstration

**Points à démontrer** :
- Confirmation JavaScript
- Suppression réussie

- Message de succès

**Ce qu'il faut dire** :
> "Avant de supprimer, une confirmation JavaScript s'affiche pour éviter les suppressions accidentelles. Après confirmation, le servlet supprime l'enregistrement via le DAO et utilise sendRedirect() pour revenir à la liste."

**Actions à effectuer** :
1. Cliquer sur supprimer
2. Montrer la boîte de confirmation
3. Confirmer la suppression
4. Vérifier le message "Ressource supprimée avec succès !"

### 8. Pages d'Erreur (2 minutes)

**Points à démontrer** :
- Page 404 personnalisée
- Page 500 personnalisée
- Design cohérent

**Ce qu'il faut dire** :
> "Les pages d'erreur sont personnalisées dans web.xml. Elles maintiennent le design de l'application tout en fournissant des informations utiles et des liens de navigation pour que l'utilisateur ne soit pas bloqué."

**Actions à effectuer** :
1. Taper une URL inexistante : `http://localhost:8080/PlanificationAcademique/inexistant`
2. Montrer la page 404 avec son design
3. Cliquer pour revenir à l'accueil

### 9. EJB Timer et Logs (3-4 minutes)

**URL** : Console du serveur

**Points à démontrer** :
- Exécution automatique toutes les 5 minutes
- Logs détaillés
- Mises à jour dans la base de données

**Ce qu'il faut dire** :
> "L'EJB Timer est annoté avec @Singleton, @Startup et @Schedule. Il s'exécute automatiquement toutes les 5 minutes pour mettre à jour les statuts de planification et générer des statistiques. Toutes ses activités sont enregistrées dans la table logs pour assurer la traçabilité."

**Actions à effectuer** :
1. Montrer les logs du serveur avec les exécutions du Timer
2. Ouvrir MySQL Workbench ou phpMyAdmin
3. Exécuter : `SELECT * FROM logs ORDER BY date_action DESC LIMIT 10`
4. Montrer les entrées créées par le Timer
5. Exécuter : `SELECT * FROM planification WHERE statut = 'EN_COURS'` pour montrer les mises à jour automatiques

### 10. Responsive Design (2 minutes)

**URL** : N'importe quelle page

**Points à démontrer** :
- Adaptation mobile
- Adaptation tablette
- Navigation burger sur mobile

**Ce qu'il faut dire** :
> "L'application est 100% responsive grâce à l'utilisation de CSS Grid, Flexbox et media queries. Le design s'adapte automatiquement à tous les types d'écrans sans perdre en fonctionnalité."

**Actions à effectuer** :
1. Ouvrir les DevTools du navigateur (F12)
2. Activer le mode responsive
3. Tester différentes tailles d'écran (iPhone, iPad, Desktop)
4. Montrer comment le menu se transforme
5. Montrer que les tableaux restent utilisables

### 11. Architecture et Code (3-4 minutes)

**Points à démontrer** :
- Structure des packages
- Méthodes Servlet commentées
- Pattern DAO
- Gestion des ressources JDBC

**Ce qu'il faut dire** :
> "Le projet suit une architecture MVC + DAO professionnelle avec une séparation claire des responsabilités. Le package beans contient les entités, dao contient l'accès aux données, servlets contient les contrôleurs, et utils contient les utilitaires comme DatabaseConnection."

**Actions à effectuer** :
1. Ouvrir l'IDE et montrer la structure
2. Ouvrir `RessourceServlet.java` et montrer les commentaires sur les méthodes requises
3. Montrer un exemple de PreparedStatement dans `RessourceDAO.java`
4. Montrer la méthode `closeResources()` dans `DatabaseConnection.java`
5. Montrer l'annotation `@Schedule` dans `PlanificationTimer.java`

## 💬 Questions Fréquentes et Réponses

### Q: Pourquoi utiliser PreparedStatement au lieu de Statement ?
**R**: Les PreparedStatement préviennent les attaques par injection SQL en séparant le code SQL des données utilisateur. De plus, ils sont compilés une fois et réutilisables, offrant de meilleures performances.

### Q: Quelle est la différence entre forward() et sendRedirect() ?
**R**: forward() transfère la requête côté serveur sans changer l'URL du navigateur, conservant tous les attributs de requête. sendRedirect() envoie une commande au navigateur de faire une nouvelle requête GET, changeant l'URL et perdant les attributs de requête. On utilise sendRedirect() après un POST pour implémenter le pattern Post-Redirect-Get.

### Q: Comment fonctionne exactement l'EJB Timer ?
**R**: L'annotation @Schedule configure une tâche planifiée qui s'exécute automatiquement selon l'intervalle spécifié. Dans notre cas, `minute = "*/5"` signifie toutes les 5 minutes. Le conteneur EJB gère automatiquement l'exécution de la méthode annotée.

### Q: Le design responsive utilise-t-il Bootstrap ou un framework CSS ?
**R**: Non, le CSS est entièrement écrit à la main (vanilla CSS) en utilisant les fonctionnalités modernes comme CSS Grid, Flexbox, et Custom Properties (variables CSS). Cela démontre une maîtrise des fondamentaux CSS sans dépendance à des frameworks.

### Q: Comment garantissez-vous la fermeture des ressources JDBC ?
**R**: Nous utilisons systématiquement le pattern try-finally avec une méthode utilitaire `closeResources()` qui ferme Connection, PreparedStatement et ResultSet dans l'ordre inverse de leur création, en vérifiant que chacun n'est pas null.

### Q: Peut-on ajouter plus de types de ressources ?
**R**: Oui, il suffit de modifier l'ENUM dans MySQL et d'ajouter l'option dans le formulaire JSP. Le code Java est suffisamment générique pour gérer n'importe quel type.

## 🎓 Points Pédagogiques à Souligner

1. **Architecture MVC + DAO** : Séparation claire des responsabilités
2. **Toutes les méthodes Servlet** : getParameter, setAttribute, getRequestDispatcher, forward, sendRedirect
3. **Sécurité** : PreparedStatement partout, validation double (client + serveur)
4. **Gestion mémoire** : Fermeture systématique des ressources JDBC
5. **Pattern Post-Redirect-Get** : Évite la resoumission de formulaires
6. **EJB Timer** : Automatisation de tâches planifiées
7. **Design moderne** : Glassmorphism, responsive, animations
8. **Bonnes pratiques** : Code commenté, nommage clair, gestion d'erreurs

## 📊 Durée Totale Estimée

- Introduction : 1 minute
- Démonstration complète : 25-30 minutes
- Questions/réponses : 5-10 minutes
- **TOTAL : 30-40 minutes**

## ✅ Checklist Finale

Avant de commencer la démonstration, vérifier :
- [ ] MySQL est démarré
- [ ] La base de données contient les données de test
- [ ] Le serveur est démarré et l'application déployée
- [ ] Les logs du serveur sont visibles
- [ ] Un navigateur moderne est ouvert
- [ ] L'IDE est ouvert sur le projet
- [ ] Les slides de présentation sont prêtes (si applicable)

---

**Bonne démonstration !** 🎉
