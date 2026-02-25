import { motion } from 'framer-motion';
import { X, Calendar, Clock, MapPin, User, FileText, Tag } from 'lucide-react';

export default function EventDetailModal({ event, onClose }) {
    if (!event) return null;

    // Determine event type based on notes
    const getEventType = () => {
        const notes = event.notes?.toLowerCase() || '';
        if (notes.includes('examen')) return { type: 'Examen', color: 'orange' };
        if (notes.includes('réunion') || notes.includes('reunion')) return { type: 'Réunion', color: 'green' };
        return { type: 'Cours', color: 'blue' };
    };

    // Calculate event status
    const getEventStatus = () => {
        const now = new Date();
        const eventStart = new Date(event.dateHeure);
        const eventEnd = new Date(eventStart.getTime() + (event.duree || 60) * 60000);

        if (now < eventStart) {
            return { label: 'À venir', color: 'bg-blue-500' };
        } else if (now >= eventStart && now <= eventEnd) {
            return { label: 'En cours', color: 'bg-green-500' };
        } else {
            return { label: 'Terminé', color: 'bg-gray-500' };
        }
    };

    const eventType = getEventType();
    const eventStatus = getEventStatus();

    // Format date and time
    const formatDateTime = (dateTimeStr) => {
        const date = new Date(dateTimeStr);
        return {
            date: date.toLocaleDateString('fr-FR', {
                weekday: 'long',
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            }),
            time: date.toLocaleTimeString('fr-FR', {
                hour: '2-digit',
                minute: '2-digit'
            })
        };
    };

    const dateTime = formatDateTime(event.dateHeure);

    return (
        <div className="modal-overlay" onClick={onClose}>
            <motion.div
                initial={{ scale: 0.9, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.9, opacity: 0 }}
                className="modal-content"
                onClick={(e) => e.stopPropagation()}
            >
                {/* Header */}
                <div className={`bg-gradient-to-r from-${eventType.color}-500 to-${eventType.color}-600 text-white p-6 rounded-t-2xl`}>
                    <div className="flex justify-between items-start">
                        <div className="flex-1">
                            <div className="flex items-center gap-3 mb-2">
                                <span className={`px-3 py-1 rounded-full text-xs font-semibold ${eventStatus.color} text-white`}>
                                    {eventStatus.label}
                                </span>
                                <span className="px-3 py-1 rounded-full text-xs font-semibold bg-white/20">
                                    {eventType.type}
                                </span>
                            </div>
                            <h2 className="text-3xl font-bold">{event.coursNom || 'Événement'}</h2>
                        </div>
                        <button
                            onClick={onClose}
                            className="text-white hover:bg-white/20 rounded-lg p-2 transition-all"
                        >
                            <X className="w-6 h-6" />
                        </button>
                    </div>
                </div>

                {/* Content */}
                <div className="p-6 space-y-6">
                    {/* Date and Time */}
                    <div className="bg-gradient-to-r from-indigo-50 to-purple-50 p-4 rounded-xl">
                        <div className="flex items-start gap-4">
                            <div className="bg-white p-3 rounded-lg shadow-sm">
                                <Calendar className="w-6 h-6 text-indigo-600" />
                            </div>
                            <div>
                                <h3 className="font-semibold text-gray-900 mb-1">Date et Heure</h3>
                                <p className="text-gray-700 capitalize">{dateTime.date}</p>
                                <p className="text-2xl font-bold text-indigo-600 mt-1">{dateTime.time}</p>
                            </div>
                        </div>
                    </div>

                    {/* Details Grid */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        {/* Duration */}
                        <div className="bg-blue-50 p-4 rounded-xl">
                            <div className="flex items-center gap-3">
                                <div className="bg-white p-2 rounded-lg shadow-sm">
                                    <Clock className="w-5 h-5 text-blue-600" />
                                </div>
                                <div>
                                    <p className="text-xs text-gray-600 font-medium">Durée</p>
                                    <p className="text-lg font-bold text-gray-900">{event.duree || 60} minutes</p>
                                </div>
                            </div>
                        </div>

                        {/* Room */}
                        <div className="bg-green-50 p-4 rounded-xl">
                            <div className="flex items-center gap-3">
                                <div className="bg-white p-2 rounded-lg shadow-sm">
                                    <MapPin className="w-5 h-5 text-green-600" />
                                </div>
                                <div>
                                    <p className="text-xs text-gray-600 font-medium">Salle</p>
                                    <p className="text-lg font-bold text-gray-900">{event.salleNom || 'Non spécifié'}</p>
                                </div>
                            </div>
                        </div>

                        {/* Teacher */}
                        <div className="bg-purple-50 p-4 rounded-xl md:col-span-2">
                            <div className="flex items-center gap-3">
                                <div className="bg-white p-2 rounded-lg shadow-sm">
                                    <User className="w-5 h-5 text-purple-600" />
                                </div>
                                <div>
                                    <p className="text-xs text-gray-600 font-medium">Enseignant</p>
                                    <p className="text-lg font-bold text-gray-900">{event.enseignantNom || 'Non spécifié'}</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Notes Section */}
                    {event.notes && (
                        <div className="bg-amber-50 p-4 rounded-xl">
                            <div className="flex items-start gap-3">
                                <div className="bg-white p-2 rounded-lg shadow-sm mt-1">
                                    <FileText className="w-5 h-5 text-amber-600" />
                                </div>
                                <div className="flex-1">
                                    <h3 className="font-semibold text-gray-900 mb-2 flex items-center gap-2">
                                        Notes
                                    </h3>
                                    <p className="text-gray-700 whitespace-pre-wrap">{event.notes}</p>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Additional Info */}
                    <div className="border-t pt-4">
                        <div className="flex items-center justify-between text-sm text-gray-500">
                            <span>ID de l'événement: {event.id}</span>
                            {event.statut && (
                                <span className="flex items-center gap-1">
                                    <Tag className="w-4 h-4" />
                                    Statut: {event.statut}
                                </span>
                            )}
                        </div>
                    </div>
                </div>

                {/* Footer */}
                <div className="bg-gray-50 p-4 rounded-b-2xl flex justify-end gap-3">
                    <button
                        onClick={onClose}
                        className="px-6 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-all font-medium"
                    >
                        Fermer
                    </button>
                </div>
            </motion.div>
        </div>
    );
}
