<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="java.util.*, beans.Planification, java.sql.Timestamp, java.text.SimpleDateFormat" %>
        <% @SuppressWarnings("unchecked") List<Planification> planifications = (List<Planification>)
                request.getAttribute("planifications");
                if (planifications == null) planifications = new ArrayList<>();

                    Timestamp debutSemaine = (Timestamp) request.getAttribute("debutSemaine");
                    String semaineOffset = (String) request.getAttribute("semaineOffset");
                    if (semaineOffset == null) semaineOffset = "0";

                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM yyyy", new Locale("fr", "FR"));
                    SimpleDateFormat sdfJour = new SimpleDateFormat("EEEE", new Locale("fr", "FR"));

                    // Calculer les dates de la semaine
                    Calendar cal = Calendar.getInstance();
                    if (debutSemaine != null) {
                    cal.setTime(debutSemaine);
                    }
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    %>
                    <!DOCTYPE html>
                    <html lang="fr">

                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>📅 Calendrier Académique - Planification Académique</title>
                        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
                        <style>
                            /* Styles spécifiques au calendrier */
                            .calendar-container {
                                max-width: 1400px;
                                margin: 20px auto;
                                padding: 0 20px;
                            }

                            .calendar-header {
                                display: flex;
                                justify-content: space-between;
                                align-items: center;
                                margin-bottom: 30px;
                                padding: 20px;
                                background: rgba(255, 255, 255, 0.95);
                                backdrop-filter: blur(10px);
                                border-radius: 15px;
                                box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                            }

                            .week-navigation {
                                display: flex;
                                align-items: center;
                                gap: 15px;
                            }

                            .nav-btn {
                                padding: 10px 20px;
                                background: var(--primary-color);
                                color: white;
                                border: none;
                                border-radius: 8px;
                                cursor: pointer;
                                font-weight: bold;
                                transition: all 0.3s;
                            }

                            .nav-btn:hover {
                                background: var(--secondary-color);
                                transform: translateY(-2px);
                            }

                            .calendar-grid {
                                display: grid;
                                grid-template-columns: 80px repeat(5, 1fr);
                                gap: 1px;
                                background: #ddd;
                                border-radius: 10px;
                                overflow: hidden;
                                box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
                            }

                            .calendar-header-cell {
                                background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
                                color: white;
                                padding: 20px;
                                text-align: center;
                                font-weight: bold;
                                font-size: 14px;
                            }

                            .time-cell {
                                background: rgba(139, 0, 0, 0.05);
                                padding: 10px;
                                text-align: center;
                                font-size: 12px;
                                font-weight: 600;
                                color: var(--primary-color);
                                display: flex;
                                align-items: center;
                                justify-content: center;
                            }

                            .day-cell {
                                background: white;
                                min-height: 60px;
                                padding: 5px;
                                position: relative;
                                cursor: pointer;
                                transition: all 0.2s;
                            }

                            .day-cell:hover {
                                background: rgba(139, 0, 0, 0.02);
                                box-shadow: inset 0 0 0 2px var(--primary-color);
                            }

                            .event {
                                background: linear-gradient(135deg, #4a90e2, #357abd);
                                color: white;
                                padding: 8px;
                                border-radius: 6px;
                                margin-bottom: 4px;
                                font-size: 12px;
                                line-height: 1.3;
                                cursor: pointer;
                                transition: all 0.3s;
                                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
                            }

                            .event:hover {
                                transform: translateY(-2px);
                                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
                            }

                            .event.type-cours {
                                background: linear-gradient(135deg, #4a90e2, #357abd);
                            }

                            .event.type-reunion {
                                background: linear-gradient(135deg, #27ae60, #229954);
                            }

                            .event.type-examen {
                                background: linear-gradient(135deg, #e67e22, #d35400);
                            }

                            .event-title {
                                font-weight: bold;
                                margin-bottom: 2px;
                            }

                            .event-details {
                                font-size: 10px;
                                opacity: 0.9;
                            }

                            .btn-add-event {
                                padding: 12px 25px;
                                background: linear-gradient(135deg, #27ae60, #229954);
                                color: white;
                                text-decoration: none;
                                border-radius: 8px;
                                font-weight: bold;
                                display: inline-flex;
                                align-items: center;
                                gap: 8px;
                                transition: all 0.3s;
                                box-shadow: 0 4px 15px rgba(39, 174, 96, 0.3);
                            }

                            .btn-add-event:hover {
                                transform: translateY(-2px);
                                box-shadow: 0 6px 20px rgba(39, 174, 96, 0.4);
                            }

                            .legend {
                                margin-top: 20px;
                                padding: 15px;
                                background: rgba(255, 255, 255, 0.95);
                                border-radius: 10px;
                                display: flex;
                                gap: 20px;
                                flex-wrap: wrap;
                            }

                            .legend-item {
                                display: flex;
                                align-items: center;
                                gap: 8px;
                            }

                            .legend-color {
                                width: 20px;
                                height: 20px;
                                border-radius: 4px;
                            }

                            @media (max-width: 768px) {
                                .calendar-grid {
                                    grid-template-columns: 60px repeat(5, 1fr);
                                    font-size: 11px;
                                }

                                .calendar-header-cell {
                                    padding: 10px 5px;
                                    font-size: 11px;
                                }

                                .event {
                                    font-size: 10px;
                                    padding: 5px;
                                }
                            }
                        </style>
                    </head>

                    <body>
                        <!-- Navigation -->
                        <nav class="navbar">
                            <div class="nav-brand">
                                <span class="icon">📚</span>
                                <span class="brand-text">Planification Académique</span>
                            </div>
                            <div class="nav-links">
                                <a href="<%=request.getContextPath()%>/calendrier" class="nav-link active">📅
                                    Calendrier</a>
                                <a href="<%=request.getContextPath()%>/ressources" class="nav-link">📋 Ressources</a>
                            </div>
                        </nav>

                        <div class="calendar-container">
                            <!-- En-tête du calendrier -->
                            <div class="calendar-header">
                                <h1 style="margin: 0; color: var(--primary-color);">
                                    📅 Calendrier Académique
                                </h1>

                                <div class="week-navigation">
                                    <a href="?semaine=<%=Integer.parseInt(semaineOffset) - 1%>" class="nav-btn">
                                        ← Semaine précédente
                                    </a>
                                    <a href="?semaine=0" class="nav-btn" style="background: var(--secondary-color);">
                                        📍 Aujourd'hui
                                    </a>
                                    <a href="?semaine=<%=Integer.parseInt(semaineOffset) + 1%>" class="nav-btn">
                                        Semaine suivante →
                                    </a>
                                </div>

                                <a href="<%=request.getContextPath()%>/calendrier?action=formulaire"
                                    class="btn-add-event">
                                    ➕ Nouvelle planification
                                </a>
                            </div>

                            <% // Afficher message de succès/erreur String success=request.getParameter("success");
                                String error=request.getParameter("error"); if (success !=null) { %>
                                <div class="success-message"
                                    style="padding: 15px; background: #d4edda; color: #155724; border-radius: 8px; margin-bottom: 20px;">
                                    ✓ Planification <%= success.equals("create") ? "créée" : success.equals("update")
                                        ? "modifiée" : "supprimée" %> avec succès !
                                </div>
                                <% } if (error !=null) { %>
                                    <div class="error-message"
                                        style="padding: 15px; background: #f8d7da; color: #721c24; border-radius: 8px; margin-bottom: 20px;">
                                        ✗ Erreur lors de l'opération. Veuillez réessayer.
                                    </div>
                                    <% } %>

                                        <!-- Grille du calendrier -->
                                        <div class="calendar-grid">
                                            <!-- En-tête -->
                                            <div class="calendar-header-cell">Heure</div>
                                            <% String[] jours={"Lundi", "Mardi" , "Mercredi" , "Jeudi" , "Vendredi" };
                                                for (int i=0; i < 5; i++) { Date jourDate=cal.getTime(); String
                                                jourFormate=sdfJour.format(jourDate) + "<br>" +
                                                sdfDate.format(jourDate); %>
                                                <div class="calendar-header-cell">
                                                    <%=jourFormate%>
                                                </div>
                                                <% cal.add(Calendar.DAY_OF_MONTH, 1); } // Reset calendar to Monday
                                                    cal.add(Calendar.DAY_OF_MONTH, -5); // Créer une structure pour
                                                    mapper les planifications par jour et heure Map<String,
                                                    List<Planification>> planifMap = new HashMap<>();
                                                        for (Planification p : planifications) {
                                                        @SuppressWarnings("deprecation")
                                                        int jour = p.getDateHeure().getDay(); // 0=Dim, 1=Lun, ...,
                                                        5=Ven
                                                        int heure = p.getDateHeure().getHours();
                                                        String key = jour + "-" + heure;

                                                        if (!planifMap.containsKey(key)) {
                                                        planifMap.put(key, new ArrayList<>());
                                                            }
                                                            planifMap.get(key).add(p);
                                                            }

                                                            // Générer les créneaux horaires (8h-18h)
                                                            for (int heure = 8; heure < 18; heure++) { String
                                                                heureStr=String.format("%02d:00", heure); %>
                                                                <div class="time-cell">
                                                                    <%=heureStr%>
                                                                </div>
                                                                <% // Pour chaque jour de la semaine for (int jour=1;
                                                                    jour <=5; jour++) { // 1=Lun, 5=Ven String key=jour
                                                                    + "-" + heure; List<Planification> planifsCreneau =
                                                                    planifMap.get(key);
                                                                    %>
                                                                    <div class="day-cell"
                                                                        onclick="window.location.href='<%=request.getContextPath()%>/calendrier?action=formulaire&date=<%=new SimpleDateFormat("
                                                                        yyyy-MM-dd").format(cal.getTime())%>&heure=
                                                                        <%=heureStr%>'">
                                                                            <% if (planifsCreneau !=null &&
                                                                                !planifsCreneau.isEmpty()) { for
                                                                                (Planification p : planifsCreneau) {
                                                                                String typeClass="type-cours" ; // Par
                                                                                défaut if (p.getNotes() !=null &&
                                                                                p.getNotes().toLowerCase().contains("réunion"))
                                                                                { typeClass="type-reunion" ; } else if
                                                                                (p.getNotes() !=null &&
                                                                                p.getNotes().toLowerCase().contains("examen"))
                                                                                { typeClass="type-examen" ; } %>
                                                                                <div class="event <%=typeClass%>"
                                                                                    onclick="event.stopPropagation(); window.location.href='<%=request.getContextPath()%>/calendrier?action=detail&id=<%=p.getId()%>'">
                                                                                    <div class="event-title">
                                                                                        <%=p.getCoursNom()%>
                                                                                    </div>
                                                                                    <div class="event-details">
                                                                                        📍 <%=p.getSalleNom()%><br>
                                                                                            👨‍🏫
                                                                                            <%=p.getEnseignantNom()%>
                                                                                                <br>
                                                                                                ⏰ <%=p.getHeureDebut()%>
                                                                                                    -
                                                                                                    <%=p.getHeureFin()%>
                                                                                    </div>
                                                                                </div>
                                                                                <% } } %>
                                                                    </div>
                                                                    <% } } %>
                                        </div>

                                        <!-- Légende -->
                                        <div class="legend">
                                            <div class="legend-item">
                                                <div class="legend-color"
                                                    style="background: linear-gradient(135deg, #4a90e2, #357abd);">
                                                </div>
                                                <span>Cours</span>
                                            </div>
                                            <div class="legend-item">
                                                <div class="legend-color"
                                                    style="background: linear-gradient(135deg, #27ae60, #229954);">
                                                </div>
                                                <span>Réunion</span>
                                            </div>
                                            <div class="legend-item">
                                                <div class="legend-color"
                                                    style="background: linear-gradient(135deg, #e67e22, #d35400);">
                                                </div>
                                                <span>Examen</span>
                                            </div>
                                        </div>
                        </div>

                        <!-- Footer -->
                        <footer class="footer">
                            <p>&copy; 2026 Système de Planification Académique v1.0.0</p>
                        </footer>
                    </body>

                    </html>