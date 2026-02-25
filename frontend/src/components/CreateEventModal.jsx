import { useState } from 'react';
import { motion } from 'framer-motion';
import { X } from 'lucide-react';

export default function CreateEventModal({ onClose, onCreate, initialSlot }) {
    const [formData, setFormData] = useState({
        coursId: '',
        enseignantId: '',
        salleId: '',
        dateHeure: '',
        duree: 60,
        statut: 'PLANIFIE',
        notes: ''
    });

    // Data mappings
    const coursOptions = {
        '1': 'Java EE Avancé',
        '2': 'React Workshop',
        '3': 'Database Design'
    };

    const enseignantOptions = {
        '1': 'Prof. Martin',
        '2': 'Prof. Dubois',
        '3': 'Prof. Laurent'
    };

    const salleOptions = {
        '1': 'Salle A102',
        '2': 'Lab B205',
        '3': 'Amphi 1'
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        // Validate that dateHeure is not empty
        if (!formData.dateHeure) {
            alert('Veuillez sélectionner une date et heure');
            return;
        }

        // Convert datetime-local format to ISO string
        // datetime-local gives: "2026-02-24T10:00"
        // We need: "2026-02-24T10:00:00"
        const dateTimeValue = formData.dateHeure.includes(':00:00')
            ? formData.dateHeure
            : formData.dateHeure + ':00';

        // Convert form data to event structure
        const eventData = {
            coursNom: coursOptions[formData.coursId],
            enseignantNom: enseignantOptions[formData.enseignantId],
            salleNom: salleOptions[formData.salleId],
            dateHeure: dateTimeValue,
            duree: parseInt(formData.duree),
            notes: formData.notes
        };

        console.log('Creating event with data:', eventData);
        onCreate(eventData);
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

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
                <div className="flex items-center justify-between p-6 border-b">
                    <h2 className="text-2xl font-bold text-gradient">
                        ➕ Nouvelle Planification
                    </h2>
                    <button
                        onClick={onClose}
                        className="p-2 hover:bg-gray-100 rounded-lg transition-all"
                    >
                        <X className="w-6 h-6" />
                    </button>
                </div>

                {/* Form */}
                <form onSubmit={handleSubmit} className="p-6 space-y-4">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        {/* Cours */}
                        <div>
                            <label className="block text-sm font-semibold mb-2">
                                📚 Cours
                            </label>
                            <select
                                name="coursId"
                                value={formData.coursId}
                                onChange={handleChange}
                                className="select-modern"
                                required
                            >
                                <option value="">Sélectionner un cours</option>
                                <option value="1">Java EE Avancé</option>
                                <option value="2">React Workshop</option>
                                <option value="3">Database Design</option>
                            </select>
                        </div>

                        {/* Enseignant */}
                        <div>
                            <label className="block text-sm font-semibold mb-2">
                                👨‍🏫 Enseignant
                            </label>
                            <select
                                name="enseignantId"
                                value={formData.enseignantId}
                                onChange={handleChange}
                                className="select-modern"
                                required
                            >
                                <option value="">Sélectionner un enseignant</option>
                                <option value="1">Prof. Martin</option>
                                <option value="2">Prof. Dubois</option>
                                <option value="3">Prof. Laurent</option>
                            </select>
                        </div>

                        {/* Salle */}
                        <div>
                            <label className="block text-sm font-semibold mb-2">
                                🏫 Salle
                            </label>
                            <select
                                name="salleId"
                                value={formData.salleId}
                                onChange={handleChange}
                                className="select-modern"
                                required
                            >
                                <option value="">Sélectionner une salle</option>
                                <option value="1">Salle A102</option>
                                <option value="2">Lab B205</option>
                                <option value="3">Amphi 1</option>
                            </select>
                        </div>

                        {/* Date et Heure */}
                        <div>
                            <label className="block text-sm font-semibold mb-2">
                                📅 Date et Heure
                            </label>
                            <input
                                type="datetime-local"
                                name="dateHeure"
                                value={formData.dateHeure}
                                onChange={handleChange}
                                className="input-modern"
                                required
                            />
                        </div>

                        {/* Durée */}
                        <div>
                            <label className="block text-sm font-semibold mb-2">
                                ⏱️ Durée (minutes)
                            </label>
                            <input
                                type="number"
                                name="duree"
                                value={formData.duree}
                                onChange={handleChange}
                                min="15"
                                step="15"
                                className="input-modern"
                                required
                            />
                        </div>

                        {/* Statut */}
                        <div>
                            <label className="block text-sm font-semibold mb-2">
                                📊 Statut
                            </label>
                            <select
                                name="statut"
                                value={formData.statut}
                                onChange={handleChange}
                                className="select-modern"
                            >
                                <option value="PLANIFIE">Planifié</option>
                                <option value="EN_COURS">En cours</option>
                                <option value="TERMINE">Terminé</option>
                                <option value="ANNULE">Annulé</option>
                            </select>
                        </div>
                    </div>

                    {/* Notes */}
                    <div>
                        <label className="block text-sm font-semibold mb-2">
                            📝 Notes
                        </label>
                        <textarea
                            name="notes"
                            value={formData.notes}
                            onChange={handleChange}
                            rows="3"
                            className="input-modern resize-none"
                            placeholder="Ajouter des notes... (ex: examen, réunion, cours)"
                        ></textarea>
                        <p className="text-xs text-gray-500 mt-1">
                            💡 Astuce: Utilisez "examen", "réunion" ou "cours" pour la couleur automatique
                        </p>
                    </div>

                    {/* Buttons */}
                    <div className="flex items-center justify-end gap-3 pt-4">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-all font-medium"
                        >
                            Annuler
                        </button>
                        <button
                            type="submit"
                            className="btn-primary"
                        >
                            💾 Enregistrer
                        </button>
                    </div>
                </form>
            </motion.div>
        </div>
    );
}
