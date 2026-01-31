import { useState } from 'react';
import LoginPage from './components/LoginPage';
import ChatPage from './components/ChatPage';
import './App.css';

function App() {
  const [user, setUser] = useState(null);

  const handleLoginSuccess = (loggedInUser) => {
    setUser(loggedInUser);
  };

  const handleLogout = () => {
    setUser(null);
  };

  return (
    <div className="app">
      {user ? (
        <ChatPage user={user} onLogout={handleLogout} />
      ) : (
        <LoginPage onLoginSuccess={handleLoginSuccess} />
      )}
    </div>
  );
}

export default App;
