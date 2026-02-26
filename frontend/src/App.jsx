import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import Calendar from './components/Calendar';
import LoginPage from './pages/LoginPage';
import DashboardEtudiant from './pages/DashboardEtudiant';
import DashboardAdmin from './pages/DashboardAdmin';
import './index.css';

/** Redirige la route racine selon le rôle de l'utilisateur */
function RootRedirect() {
  const { user, loading } = useAuth();
  if (loading) return null;
  if (!user) return <Navigate to="/login" replace />;
  if (user.role === 'ETUDIANT') return <Navigate to="/dashboard/etudiant" replace />;
  return <Navigate to="/dashboard/admin" replace />;
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <div className="min-h-screen">
          <Navbar />
          <Routes>
            {/* Route publique */}
            <Route path="/login" element={<LoginPage />} />

            {/* Redirection intelligente depuis / */}
            <Route path="/" element={<RootRedirect />} />

            {/* Calendrier — accessible à tous les utilisateurs connectés */}
            <Route path="/calendar" element={
              <ProtectedRoute>
                <Calendar />
              </ProtectedRoute>
            } />

            {/* Ressources — accessible à tous les utilisateurs connectés */}
            <Route path="/resources" element={
              <ProtectedRoute>
                <div className="container mx-auto px-6 py-8">
                  <div className="card-glass">
                    <h1 className="text-3xl font-bold text-gradient mb-4">
                      📋 Gestion des Ressources
                    </h1>
                    <p className="text-gray-600">
                      Retrouvez la gestion des ressources dans l'onglet{' '}
                      <a
                        href="http://localhost:8080/PlanificationAcademique/ressources"
                        className="text-blue-600 underline"
                        target="_blank"
                        rel="noreferrer"
                      >
                        Ressources (backend)
                      </a>{' '}
                      ou dans le dashboard Admin ci-dessous.
                    </p>
                  </div>
                </div>
              </ProtectedRoute>
            } />

            {/* Dashboard Étudiant */}
            <Route path="/dashboard/etudiant" element={
              <ProtectedRoute roles={['ETUDIANT']}>
                <DashboardEtudiant />
              </ProtectedRoute>
            } />

            {/* Dashboard Admin / Enseignant */}
            <Route path="/dashboard/admin" element={
              <ProtectedRoute roles={['ADMIN', 'ENSEIGNANT']}>
                <DashboardAdmin />
              </ProtectedRoute>
            } />

            {/* Page non autorisé */}
            <Route path="/unauthorized" element={
              <div className="min-h-screen flex items-center justify-center">
                <div className="text-center card-glass p-12">
                  <div className="text-6xl mb-4">🚫</div>
                  <h1 className="text-2xl font-bold mb-2">Accès refusé</h1>
                  <p className="text-gray-600 mb-6">Vous n'avez pas les droits pour accéder à cette page.</p>
                  <Navigate to="/" />
                </div>
              </div>
            } />

            {/* 404 */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </div>
      </AuthProvider>
    </Router>
  );
}

export default App;
