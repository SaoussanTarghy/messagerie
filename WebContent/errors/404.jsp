<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="false" %>
    <!DOCTYPE html>
    <html lang="fr">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Page introuvable - Erreur 404</title>
        <link rel="stylesheet" href="../css/style.css">
        <style>
            .error-page {
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                background: linear-gradient(135deg, #8B0000 0%, #5D001E 100%);
                padding: 2rem;
            }

            .error-container {
                text-align: center;
                max-width: 600px;
            }

            .error-code {
                font-size: 120px;
                font-weight: 900;
                color: rgba(255, 255, 255, 0.2);
                line-height: 1;
                margin: 0;
                text-shadow: 2px 2px 10px rgba(0, 0, 0, 0.3);
            }

            .error-icon {
                font-size: 80px;
                margin: 1rem 0;
                animation: bounce 2s infinite;
            }

            .error-title {
                font-size: 2rem;
                color: white;
                margin: 1rem 0;
            }

            .error-message {
                font-size: 1.1rem;
                color: rgba(255, 255, 255, 0.9);
                margin: 1.5rem 0;
                line-height: 1.6;
            }

            .error-actions {
                margin-top: 2rem;
                display: flex;
                gap: 1rem;
                justify-content: center;
                flex-wrap: wrap;
            }

            @keyframes bounce {

                0%,
                100% {
                    transform: translateY(0);
                }

                50% {
                    transform: translateY(-20px);
                }
            }
        </style>
    </head>

    <body>
        <div class="error-page">
            <div class="error-container">
                <div class="error-code">404</div>
                <div class="error-icon">🔍</div>
                <h1 class="error-title">Page introuvable</h1>
                <p class="error-message">
                    Désolé, la page que vous recherchez n'existe pas ou a été déplacée.
                    <br>
                    Vérifiez l'URL ou retournez à la page d'accueil.
                </p>
                <div class="error-actions">
                    <a href="<%= request.getContextPath() %>/index.jsp" class="btn-large btn-primary">
                        🏠 Retour à l'accueil
                    </a>
                    <a href="<%= request.getContextPath() %>/ressources?action=list" class="btn-large btn-secondary">
                        📋 Liste des ressources
                    </a>
                </div>
            </div>
        </div>
    </body>

    </html>