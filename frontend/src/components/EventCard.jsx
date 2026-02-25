import { motion } from 'framer-motion';
import { Clock, MapPin, User } from 'lucide-react';

export default function EventCard({ event, status, onClick }) {
    // Determine event type based on notes
    const getEventType = () => {
        const notes = event.notes?.toLowerCase() || '';
        if (notes.includes('examen')) return 'examen';
        if (notes.includes('réunion') || notes.includes('reunion')) return 'reunion';
        return 'cours';
    };

    // Get status badge color
    const getStatusColor = () => {
        if (status === 'Terminé') return 'bg-red-500';
        if (status === 'En cours') return 'bg-green-500';
        return 'bg-blue-500'; // À venir
    };

    const eventType = getEventType();
    const eventClass = `event-card event-${eventType}`;
    const statusColor = getStatusColor();

    return (
        <motion.div
            initial={{ scale: 0.9, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            whileHover={{ scale: 1.02 }}
            className={eventClass}
            onClick={onClick}
        >
            {/* Status Badge */}
            <div className="flex items-center justify-between mb-2">
                <div className="text-xs font-bold">{event.coursNom}</div>
                <span className={`text-[10px] px-2 py-0.5 rounded-full ${statusColor} text-white font-semibold`}>                    {status}
                </span>
            </div>

            <div className="text-xs flex items-center gap-1 opacity-90">
                <MapPin className="w-3 h-3" />
                {event.salleNom}
            </div>
            <div className="text-xs flex items-center gap-1 opacity-90">
                <User className="w-3 h-3" />
                {event.enseignantNom}
            </div>
            <div className="text-xs flex items-center gap-1 opacity-90 mt-1">
                <Clock className="w-3 h-3" />
                {event.duree} min
            </div>
        </motion.div>
    );
}
