import { useState } from 'react';
import wsService from '../services/websocket';
import './LoginPage.css';

function LoginPage({ onLoginSuccess, onSwitchToRegister }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await wsService.connect();

            // Set up login response listener
            wsService.on('onLoginResponse', (user) => {
                setLoading(false);
                onLoginSuccess(user);
            });

            wsService.on('onError', (errorMsg) => {
                setLoading(false);
                setError(errorMsg);
            });

            // Send login request
            await wsService.login(username, password);
        } catch (err) {
            setLoading(false);
            setError('Could not connect to server. Make sure the WebSocket server is running.');
        }
    };

    return (
        <div className="login-container">
            <div className="login-card">
                <div className="login-header">
                    <h1>ISGA Chat</h1>
                    <p>Sign in to continue</p>
                </div>

                <form onSubmit={handleSubmit} className="login-form">
                    <div className="form-group">
                        <label htmlFor="username">Username</label>
                        <input
                            type="text"
                            id="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            placeholder="Enter your username"
                            required
                            disabled={loading}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Enter your password"
                            required
                            disabled={loading}
                        />
                    </div>

                    {error && <div className="error-message">{error}</div>}

                    <button type="submit" className="login-button" disabled={loading}>
                        {loading ? 'Connecting...' : 'Sign In'}
                    </button>
                </form>

                <div className="login-footer">
                    <p>Don't have an account?</p>
                    <button className="switch-button" onClick={onSwitchToRegister}>
                        Create Account
                    </button>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;
