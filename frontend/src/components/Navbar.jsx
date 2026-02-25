import { Link, useLocation } from 'react-router-dom';
import { Calendar, List, BookOpen } from 'lucide-react';

export default function Navbar() {
    const location = useLocation();

    const isActive = (path) => location.pathname === path;

    return (
        <nav className="navbar">
            <div className="container mx-auto px-6 py-4">
                <div className="flex items-center justify-between">
                    {/* Brand */}
                    <Link to="/" className="flex items-center gap-3 text-xl font-bold">
                        <BookOpen className="w-8 h-8" />
                        <span>Planification Académique</span>
                    </Link>

                    {/* Navigation Links */}
                    <div className="flex items-center gap-2">
                        <Link
                            to="/calendar"
                            className={`nav-link ${isActive('/calendar') ? 'active' : ''}`}
                        >
                            <Calendar className="w-5 h-5" />
                            <span>Calendrier</span>
                        </Link>

                        <Link
                            to="/resources"
                            className={`nav-link ${isActive('/resources') ? 'active' : ''}`}
                        >
                            <List className="w-5 h-5" />
                            <span>Ressources</span>
                        </Link>
                    </div>
                </div>
            </div>
        </nav>
    );
}
