#!/bin/bash

# =========================================
#   LANCEMENT COMPLET DU PROJET
#   Planification Académique
# =========================================

set -e

TOMCAT_HOME="/opt/homebrew/Cellar/tomcat@10/10.1.52/libexec"
TOMCAT_BIN="$TOMCAT_HOME/bin/catalina.sh"
WEBAPPS="$TOMCAT_HOME/webapps"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
FRONTEND_DIR="$PROJECT_DIR/frontend"
DB_NAME="planification_academique"
WAR_NAME="PlanificationAcademique"

echo ""
echo "========================================="
echo "   🚀 LANCEMENT DU PROJET"
echo "   Planification Académique"
echo "========================================="
echo ""

# -------- 1. Vérification Java --------
echo "📌 1. Vérification de Java..."
if ! command -v java &> /dev/null; then
    echo "❌ Java n'est pas installé. Installer Java 11+ et réessayer."
    exit 1
fi
java -version
echo "✅ Java OK"
echo ""

# -------- 2. Vérification Maven --------
echo "📌 2. Vérification de Maven..."
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven n'est pas installé."
    echo "   Installer avec: brew install maven"
    exit 1
fi
mvn -version
echo "✅ Maven OK"
echo ""

# -------- 3. Vérification Node.js / npm --------
echo "📌 3. Vérification de Node.js/npm..."
if ! command -v npm &> /dev/null; then
    echo "❌ Node.js/npm n'est pas installé."
    echo "   Installer depuis: https://nodejs.org"
    exit 1
fi
node --version && npm --version
echo "✅ Node.js OK"
echo ""

# -------- 4. Vérification MySQL --------
echo "📌 4. Vérification de MySQL..."
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL n'est pas installé."
    echo "   Installer avec: brew install mysql"
    exit 1
fi
mysql --version
echo "✅ MySQL OK"
echo ""

# -------- 5. Vérification Tomcat --------
echo "📌 5. Vérification de Tomcat..."
if [ ! -f "$TOMCAT_BIN" ]; then
    echo "⚠️  Tomcat non trouvé à $TOMCAT_BIN"
    echo "   Installation automatique via Homebrew..."
    brew install tomcat@10
    echo "✅ Tomcat installé"
else
    echo "✅ Tomcat trouvé"
fi
echo ""

# -------- 6. Démarrage MySQL --------
echo "📌 6. Démarrage de MySQL..."
brew services start mysql 2>/dev/null || true
sleep 2
echo "✅ MySQL démarré"
echo ""

# -------- 7. Création Base de Données --------
echo "📌 7. Configuration de la base de données..."
mysql -u root -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
mysql -u root $DB_NAME < "$PROJECT_DIR/database/planification.sql" 2>/dev/null || true
echo "✅ Base de données prête"
echo ""

# -------- 8. Installation des dépendances npm --------
echo "📌 8. Installation des dépendances npm (frontend)..."
cd "$FRONTEND_DIR"
npm install --silent
echo "✅ Dépendances npm installées"
echo ""

# -------- 9. Compilation Maven (WAR) --------
echo "📌 9. Compilation du projet Java (WAR)..."
cd "$PROJECT_DIR"
mvn clean package -q
echo "✅ WAR compilé: target/$WAR_NAME.war"
echo ""

# -------- 10. Arrêt Tomcat si déjà lancé --------
echo "📌 10. Redémarrage de Tomcat..."
export CATALINA_HOME="$TOMCAT_HOME"
"$TOMCAT_BIN" stop 2>/dev/null || true
sleep 2

# -------- 11. Déploiement WAR --------
cp "$PROJECT_DIR/target/$WAR_NAME.war" "$WEBAPPS/"
echo "✅ WAR déployé"

# -------- 12. Démarrage Tomcat --------
"$TOMCAT_BIN" start
sleep 4
echo "✅ Tomcat démarré sur http://localhost:8080/$WAR_NAME/"
echo ""

# -------- 13. Démarrage Frontend React --------
echo "📌 11. Démarrage du Frontend React..."
cd "$FRONTEND_DIR"
npm run dev &
FRONTEND_PID=$!
sleep 3
echo "✅ Frontend React démarré sur http://localhost:5173/"
echo ""

# -------- Résumé --------
echo "========================================="
echo "✨ PROJET LANCÉ AVEC SUCCÈS !"
echo "========================================="
echo ""
echo "🌐 ACCÈS À L'APPLICATION :"
echo ""
echo "  ➜  Frontend React (Calendrier) :"
echo "     http://localhost:5173/"
echo ""
echo "  ➜  Backend Java (JSP) :"
echo "     http://localhost:8080/$WAR_NAME/"
echo ""
echo "Pour arrêter :"
echo "  - Frontend : Ctrl+C dans ce terminal"
echo "  - Tomcat   : $TOMCAT_BIN stop"
echo "  - MySQL    : brew services stop mysql"
echo ""

# Ouvrir automatiquement le navigateur sur le frontend React
sleep 2
open "http://localhost:5173/" 2>/dev/null || xdg-open "http://localhost:5173/" 2>/dev/null || true

# Garder le terminal ouvert (frontend tourne en foreground)
wait $FRONTEND_PID
