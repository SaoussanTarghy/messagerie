import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Calendar from './components/Calendar';
import './index.css';

function App() {
  return (
    <Router>
      <div className="min-h-screen">
        <Navbar />
        <Routes>
          <Route path="/" element={<Navigate to="/calendar" replace />} />
          <Route path="/calendar" element={<Calendar />} />
          <Route path="/resources" element={
            <div className="container mx-auto px-6 py-8">
              <div className="card-glass">
                <h1 className="text-3xl font-bold text-gradient mb-4">
                  📋 Gestion des Ressources
                </h1>
                <p className="text-gray-600">
                  Page des ressources en développement...
                </p>
              </div>
            </div>
          } />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
