<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
    <!DOCTYPE html>
    <html lang="fr">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Erreur serveur - Erreur 500</title>
        <link rel="stylesheet" href="../css/style.css">
        <style>
            .error-page {
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                background: linear-gradient(135deg, #5D001E 0%, #8B0000 100%);
                padding: 2rem;
            }

            .error-container {
                text-align: center;
                max-width: 700px;
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
                animation: shake 0.5s infinite alternate;
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

            .error-details {
                background: rgba(0, 0, 0, 0.2);
                padding: 1.5rem;
                border-radius: 10px;
                margin: 2rem 0;
                text-align: left;
            }

            .error-details h3 {
                color: white;
                margin-bottom: 1rem;
            }

            .error-details p {
                color: rgba(255, 255, 255, 0.8);
                font-family: monospace;
                font-size: 0.9rem;
            }

            .error-actions {
                margin-top: 2rem;
                display: flex;
                gap: 1rem;
                justify-content: center;
                flex-wrap: wrap;
            }

            @keyframes shake {
                0% {
                    transform: rotate(-5deg);
                }

                100% {
                    transform: rotate(5deg);
                }
            }
        </style>
    </head>

    <body>
        <div class="error-page">
            <div class="error-container">
                <div class="error-code">500</div>
                <div class="error-icon">⚠️</div>
                <h1 class="error-title">Erreur serveur interne</h1>
                <p class="error-message">
                    Une erreur s'est produite lors du traitement de votre requête.
                    <br>
                    Nos équipes techniques ont été notifiées et travaillent sur la résolution du problème.
                </p>

                <% if (exception !=null) { %>
                    <div class="error-details">
                        <h3>Détails techniques :</h3>
                        <p><strong>Type d'erreur :</strong>
                            <%= exception.getClass().getName() %>
                        </p>
                        <p><strong>Message :</strong>
                            <%= exception.getMessage() !=null ? exception.getMessage() : "Aucun message d'erreur" %>
                        </p>
                    </div>
                    <% } %>

                        <p class="error-message">
                            Si le problème persiste, veuillez contacter l'administrateur système.
                        </p>

                        <div class="error-actions">
                            <a href="<%= request.getContextPath() %>/index.jsp" class="btn-large btn-primary">
                                🏠 Retour à l'accueil
                            </a>
                            <a href="<%= request.getContextPath() %>/ressources?action=list"
                                class="btn-large btn-secondary">
                                📋 Liste des ressources
                            </a>
                        </div>
            </div>
        </div>
    </body>

    </html>