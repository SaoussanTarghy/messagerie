# 📅 Academic Planning System - React Frontend

## Vue d'ensemble

Interface React moderne et professionnelle pour le Système de Planification Académique.

## 🚀 Démarrage Rapide

```bash
# Installer les dépendances
npm install

# Lancer le serveur de développement
npm run dev

# Build pour production
npm run build
```

L'application sera accessible sur `http://localhost:5173`

## 📦 Technologies Utilisées

- **React 18** - Framework UI
- **Vite** - Build tool ultra rapide
- **Tailwind CSS** - Styling moderne
- **React Router** - Navigation
- **Axios** - API client
- **Framer Motion** - Animations
- **Lucide React** - Icônes modernes

## 🎨 Fonctionnalités

### Calendrier Académique
- Vue hebdomadaire interactive (Lun-Ven, 8h-17h)
- Événements color-codés :
  - 🔵 Bleu = Cours
  - 🟢 Vert = Réunion
  - 🟠 Orange = Examen
- Navigation entre semaines
- Création rapide par click sur créneau
- Modal de création moderne

### Gestion des Ressources
- Liste des enseignants, salles, cours
- Recherche et filtrage
- CRUD complet

## 🔧 Configuration

### API Backend

Par défaut, l'application se connecte à :
```
http://localhost:8080/PlanificationAcademique/api
```

Pour changer l'URL, éditez `src/services/api.js`

## 📁 Structure du Projet

```
frontend/
├── src/
│   ├── components/          # Composants réutilisables
│   │   ├── Navbar.jsx
│   │   ├── Calendar.jsx
│   │   ├── EventCard.jsx
│   │   └── CreateEventModal.jsx
│   ├── services/            # Services API
│   │   ├── api.js
│   │   ├── planificationService.js
│   │   └── ressourceService.js
│   ├── App.jsx              # Composant principal
│   ├── main.jsx             # Point d'entrée
│   └── index.css            # Styles Tailwind
├── public/                  # Assets statiques
├── tailwind.config.js       # Config Tailwind
└── package.json
```

## 🎨 Personnalisation du Design

Les couleurs et thèmes sont définis dans `tailwind.config.js` :

```javascript
colors: {
  primary: { /* Indigo palette */ },
  burgundy: { /* Burgundy palette */ }
}
```

Classes CSS personnalisées dans `src/index.css` :
- `.btn-modern`, `.btn-primary`, `.btn-today`
- `.card-glass`, `.event-card`
- `.calendar-grid`

## 🔌 Connexion Backend

L'application utilise des services pour communiquer avec le backend Java :

### Planifications
```javascript
import { getPlanificationsParSemaine, createPlanification } from '@/services/planificationService';

// Récupérer planifications
const data = await getPlanificationsParSemaine(0); // Semaine actuelle

// Créer planification
await createPlanification({
  coursId: 1,
  enseignantId: 2,
  salleId: 3,
  dateHeure: '2026-02-17T14:00:00',
  duree: 120
});
```

### Ressources
```javascript
import { getRessourcesByType } from '@/services/ressourceService';

const enseignants = await getRessourcesByType('ENSEIGNANT');
const salles = await getRessourcesByType('SALLE');
```

## 🚧 TODO

- [ ] Connecter à l'API backend réelle
- [ ] Ajouter drag & drop pour déplacer événements
- [ ] Implémenter page Ressources complète
- [ ] Ajouter mode dark
- [ ] Export PDF du calendrier
- [ ] Notifications toast
- [ ] Gestion authentification

## 📝 Notes de Développement

### Données de Test

Pour le moment, l'application utilise des données mockées dans `Calendar.jsx`.
Une fois le backend REST API prêt, remplacer par de vrais appels :

```javascript
// Dans Calendar.jsx, loadPlanifications()
const data = await getPlanificationsParSemaine(currentWeek);
setPlanifications(data);
```

### Couleurs d'Événements

Les couleurs sont déterminées automatiquement selon le champ `notes` :
- Contient "examen" → Orange
- Contient "réunion" → Vert
- Sinon → Bleu (cours par défaut)

## 🎓 Démonstration

1. Ouvrir le navigateur sur `http://localhost:5173`
2. Voir le calendrier avec événements mockés
3. Naviguer entre semaines
4. Cliquer sur "Nouvelle planification"
5. Remplir le formulaire et enregistrer

## ⚡ Performance

- Build Vite optimisé
- Code splitting automatique
- Lazy loading des routes
- Animations GPU-accélérées (Framer Motion)

---

**Développé avec ❤️ par l'équipe Academic Planning**
