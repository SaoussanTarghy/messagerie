import { useState, useEffect } from 'react';
import { ChevronLeft, ChevronRight, Plus, Loader2 } from 'lucide-react';
import { motion } from 'framer-motion';
import EventCard from './EventCard';
import CreateEventModal from './CreateEventModal';
import EventDetailModal from './EventDetailModal';


export default function Calendar() {
    const [currentWeek, setCurrentWeek] = useState(0);
    const [planifications, setPlanifications] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [selectedSlot, setSelectedSlot] = useState(null);
    const [selectedEvent, setSelectedEvent] = useState(null);


    // Load events from localStorage on mount
    useEffect(() => {
        const savedEvents = localStorage.getItem('calendar_events');
        if (savedEvents) {
            try {
                const events = JSON.parse(savedEvents);
                setPlanifications(events);
            } catch (error) {
                console.error('Error loading events:', error);
            }
        } else {
            // Initialize with mock data only if no saved events
            const mockEvents = [
                {
                    id: 1,
                    coursNom: 'Java EE Avancé',
                    enseignantNom: 'Prof. Martin',
                    salleNom: 'Salle A102',
                    dateHeure: '2026-02-17T14:00:00',
                    duree: 120,
                    notes: 'cours'
                },
                {
                    id: 2,
                    coursNom: 'React Workshop',
                    enseignantNom: 'Prof. Dubois',
                    salleNom: 'Lab B205',
                    dateHeure: '2026-02-18T10:00:00',
                    duree: 90,
                    notes: 'réunion'
                },
                {
                    id: 3,
                    coursNom: 'Examen Final',
                    enseignantNom: 'Prof. Laurent',
                    salleNom: 'Amphi 1',
                    dateHeure: '2026-02-19T14:00:00',
                    duree: 180,
                    notes: 'examen'
                }
            ];
            setPlanifications(mockEvents);
            localStorage.setItem('calendar_events', JSON.stringify(mockEvents));
        }
        setIsLoading(false);
    }, []);

    // Save events to localStorage whenever they change
    useEffect(() => {
        if (planifications.length > 0) {
            localStorage.setItem('calendar_events', JSON.stringify(planifications));
        }
    }, [planifications]);

    // Get week dates based on currentWeek offset
    const getWeekDates = () => {
        const today = new Date();
        const monday = new Date(today);
        monday.setDate(today.getDate() - today.getDay() + 1 + (currentWeek * 7));

        const week = [];
        for (let i = 0; i < 5; i++) {
            const day = new Date(monday);
            day.setDate(monday.getDate() + i);
            week.push(day);
        }
        return week;
    };

    const weekDates = getWeekDates();

    // Calculate event status based on current date/time
    const getEventStatus = (dateHeure, duree) => {
        const now = new Date();
        const eventStart = new Date(dateHeure);
        const eventEnd = new Date(eventStart.getTime() + duree * 60000);

        if (now < eventStart) {
            return 'À venir';
        } else if (now >= eventStart && now <= eventEnd) {
            return 'En cours';
        } else {
            return 'Terminé';
        }
    };

    // Filter events for current week
    const getEventsForCurrentWeek = () => {
        const weekStart = weekDates[0];
        const weekEnd = new Date(weekDates[4]);
        weekEnd.setHours(23, 59, 59, 999);

        return planifications.filter(event => {
            const eventDate = new Date(event.dateHeure);
            return eventDate >= weekStart && eventDate <= weekEnd;
        });
    };

    const currentWeekEvents = getEventsForCurrentWeek();

    // Group events by day and hour for current week
    const getEventsForDayAndHour = (dayIndex, hour) => {
        const targetDate = weekDates[dayIndex];

        return currentWeekEvents.filter(event => {
            const eventDate = new Date(event.dateHeure);
            return (
                eventDate.getDate() === targetDate.getDate() &&
                eventDate.getMonth() === targetDate.getMonth() &&
                eventDate.getFullYear() === targetDate.getFullYear() &&
                eventDate.getHours() === hour
            );
        });
    };

    const handleSlotClick = (jour, heure) => {
        setSelectedSlot({ jour, heure });
        setShowCreateModal(true);
    };

    const handleCloseModal = () => {
        setShowCreateModal(false);
        setSelectedSlot(null);
    };

    const handleCreateEvent = (eventData) => {
        // Add new event to planifications
        const newEvent = {
            id: Date.now(),
            ...eventData
        };
        setPlanifications(prev => [...prev, newEvent]);
        handleCloseModal();
    };

    // Format dates for display
    const formatDate = (date) => {
        const options = { day: 'numeric', month: 'short' };
        return date.toLocaleDateString('fr-FR', options);
    };

    const jours = weekDates.map((date, idx) => ({
        label: date.toLocaleDateString('fr-FR', { weekday: 'long' }).charAt(0).toUpperCase() +
            date.toLocaleDateString('fr-FR', { weekday: 'long' }).slice(1),
        date: formatDate(date)
    }));

    const heures = Array.from({ length: 10 }, (_, i) => i + 8); // 8h to 17h

    return (
        <div className="container mx-auto px-6 py-8">
            {/* Header */}
            <motion.div
                initial={{ y: -20, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                className="card-glass mb-8"
            >
                <div className="flex items-center justify-between flex-wrap gap-4">
                    <h1 className="text-4xl font-bold text-gradient flex items-center gap-3">
                        📅 Calendrier Académique
                    </h1>

                    <div className="flex items-center gap-3">
                        <button
                            onClick={() => setCurrentWeek(w => w - 1)}
                            className="btn-modern"
                        >
                            <ChevronLeft className="w-5 h-5" />
                            Semaine précédente
                        </button>

                        <button
                            onClick={() => setCurrentWeek(0)}
                            className="btn-today"
                        >
                            Aujourd'hui
                        </button>

                        <button
                            onClick={() => setCurrentWeek(w => w + 1)}
                            className="btn-modern"
                        >
                            Semaine suivante
                            <ChevronRight className="w-5 h-5" />
                        </button>

                        <button
                            onClick={() => setShowCreateModal(true)}
                            className="btn-primary"
                        >
                            <Plus className="w-5 h-5" />
                            Nouvelle planification
                        </button>
                    </div>
                </div>
            </motion.div>

            {/* Calendar Grid */}
            {isLoading ? (
                <div className="flex items-center justify-center h-96">
                    <Loader2 className="w-12 h-12 animate-spin text-primary-500" />
                </div>
            ) : (
                <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ delay: 0.2 }}
                >
                    <div className="calendar-grid">
                        {/* Header Row */}
                        <div className="calendar-header-cell">Heure</div>
                        {jours.map((jour, idx) => (
                            <div key={idx} className="calendar-header-cell">
                                <div>{jour.label}</div>
                                <div className="text-xs font-normal mt-1">{jour.date}</div>
                            </div>
                        ))}

                        {/* Time slots */}
                        {heures.map((heure) => (
                            <>
                                {/* Time cell */}
                                <div key={`time-${heure}`} className="time-cell">
                                    {heure}:00
                                </div>

                                {/* Day cells */}
                                {jours.map((jour, jourIndex) => {
                                    const eventsForSlot = getEventsForDayAndHour(jourIndex, heure);

                                    return (
                                        <div
                                            key={`${jourIndex}-${heure}`}
                                            className="day-cell"
                                            onClick={() => handleSlotClick(jourIndex, heure)}
                                        >
                                            {eventsForSlot.map((event) => (
                                                <EventCard
                                                    key={event.id}
                                                    event={event}
                                                    status={getEventStatus(event.dateHeure, event.duree)}
                                                    onClick={(e) => {
                                                        e.stopPropagation();
                                                        setSelectedEvent(event);
                                                    }}
                                                />
                                            ))}
                                        </div>
                                    );
                                })}
                            </>
                        ))}
                    </div>

                    {/* Legend */}
                    <div className="mt-6 card-glass">
                        <div className="flex items-center gap-6 flex-wrap">
                            <div className="flex items-center gap-2">
                                <div className="w-6 h-6 rounded bg-gradient-to-r from-blue-500 to-blue-600"></div>
                                <span className="text-sm font-medium">Cours</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <div className="w-6 h-6 rounded bg-gradient-to-r from-green-500 to-green-600"></div>
                                <span className="text-sm font-medium">Réunion</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <div className="w-6 h-6 rounded bg-gradient-to-r from-orange-500 to-orange-600"></div>
                                <span className="text-sm font-medium">Examen</span>
                            </div>
                        </div>
                    </div>
                </motion.div>
            )}

            {/* Create Event Modal */}
            {showCreateModal && (
                <CreateEventModal
                    onClose={handleCloseModal}
                    onCreate={handleCreateEvent}
                    initialSlot={selectedSlot}
                />
            )}

            {/* Event Detail Modal */}
            {selectedEvent && (
                <EventDetailModal
                    event={selectedEvent}
                    onClose={() => setSelectedEvent(null)}
                />
            )}
        </div>
    );
}
