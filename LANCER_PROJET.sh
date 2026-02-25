#!/bin/bash

# ========================================
# Script de Lancement du Projet
# Planification Académique
# ========================================

echo "========================================="
echo "🚀 LANCEMENT DU PROJET"
echo "Planification Académique"
echo "========================================="
echo ""

# Fonction pour afficher les messages
print_step() {
    echo ""
    echo "📌 $1"
    echo "---"
}

print_error() {
    echo "❌ ERREUR: $1"
}

print_success() {
    echo "✅ $1"
}

# Vérifier Java
print_step "1. Vérification de Java"
if command -v java &> /dev/null; then
    java -version
    print_success "Java est installé"
else
    print_error "Java n'est pas installé !"
    echo "Installez Java JDK 1.8+ avant de continuer"
    exit 1
fi

# Vérifier MySQL
print_step "2. Vérification de MySQL"
if command -v mysql &> /dev/null; then
    mysql --version
    print_success "MySQL est installé"
else
    print_error "MySQL n'est pas trouvé !"
    echo "Installez MySQL Server 8.0+ avant de continuer"
fi

# Options de lancement
print_step "3. Options de Lancement"
echo ""
echo "Choisissez une méthode de lancement :"
echo ""
echo "A) Avec Maven (recommandé - nécessite Maven installé)"
echo "   $ mvn clean package"
echo "   $ mvn tomcat7:deploy"
echo ""
echo "B) Compilation manuelle + Tomcat"
echo "   1. Télécharger les JARs nécessaires :"
echo "      - mysql-connector-java-8.0.33.jar"
echo "      - javax.servlet-api-4.0.1.jar"
echo "      - jstl-1.2.jar"
echo "   2. Compiler les sources Java"
echo "   3. Créer le fichier WAR"
echo "   4. Déployer sur Tomcat"
echo ""
echo "C) Utiliser un IDE (Eclipse, IntelliJ, NetBeans)"
echo "   1. Importer le projet Maven"
echo "   2. Configurer Tomcat dans l'IDE"
echo "   3. Run As > Run on Server"
echo ""

print_step "4. Prochaines Étapes"
echo ""
echo "1️⃣  Installer les prérequis manquants (voir ci-dessus)"
echo ""
echo "2️⃣  Créer la base de données MySQL :"
echo "    $ mysql -u root -p < database/planification.sql"
echo ""
echo "3️⃣  Configurer la connexion dans :"
echo "    src/utils/DatabaseConnection.java"
echo "    (modifier USER et PASSWORD)"
echo ""
echo "4️⃣  Compiler et déployer selon la méthode choisie"
echo ""
echo "5️⃣  Accéder à l'application :"
echo "    http://localhost:8080/PlanificationAcademique/"
echo ""

print_step "📚 Documentation Complète"
echo ""
echo "Pour plus de détails, consultez :"
echo "• README.md                  - Vue d'ensemble"
echo "• docs/INSTALLATION.md       - Guide d'installation détaillé"
echo "• docs/GUIDE_DEMONSTRATION.md - Guide de démonstration"
echo ""

echo "========================================="
echo "✨ Projet Prêt à Être Lancé !"
echo "========================================="
echo ""
echo "Besoin d'aide ? Consultez docs/INSTALLATION.md"
echo ""
