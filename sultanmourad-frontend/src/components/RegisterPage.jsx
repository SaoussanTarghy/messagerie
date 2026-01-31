import { useState } from 'react';
import wsService from '../services/websocket';
import './RegisterPage.css';

function RegisterPage({ onRegisterSuccess, onSwitchToLogin }) {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        // Validation
        if (password !== confirmPassword) {
            setError('Passwords do not match!');
            return;
        }

        if (password.length < 6) {
            setError('Password must be at least 6 characters long!');
            return;
        }

        if (username.length < 3) {
            setError('Username must be at least 3 characters long!');
            return;
        }

        setLoading(true);

        try {
            await wsService.connect();

            // Set up register response listener
            wsService.on('onRegisterResponse', (user) => {
                setLoading(false);
                setSuccess(true);
                setTimeout(() => {
                    onRegisterSuccess(user);
                }, 1500);
            });

            wsService.on('onError', (errorMsg) => {
                setLoading(false);
                setError(errorMsg);
            });

            // Send register request
            await wsService.register(username, email, password);
        } catch (err) {
            setLoading(false);
            setError('Could not connect to server. Make sure the WebSocket server is running.');
        }
    };

    return (
        <div className="register-container">
            <div className="register-card">
                <div className="register-header">
                    <h1>ISGA Chat</h1>
                    <p>Create your account</p>
                </div>

                {success ? (
                    <div className="success-message">
                        <span className="success-icon">✓</span>
                        <p>Account created successfully!</p>
                        <p>Redirecting to chat...</p>
                    </div>
                ) : (
                    <form onSubmit={handleSubmit} className="register-form">
                        <div className="form-group">
                            <label htmlFor="username">Username</label>
                            <input
                                type="text"
                                id="username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                placeholder="Choose a username"
                                required
                                disabled={loading}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="email">Email</label>
                            <input
                                type="email"
                                id="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="Enter your email"
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
                                placeholder="Create a password"
                                required
                                disabled={loading}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="confirmPassword">Confirm Password</label>
                            <input
                                type="password"
                                id="confirmPassword"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder="Confirm your password"
                                required
                                disabled={loading}
                            />
                        </div>

                        {error && <div className="error-message">{error}</div>}

                        <button type="submit" className="register-button" disabled={loading}>
                            {loading ? 'Creating Account...' : 'Sign Up'}
                        </button>
                    </form>
                )}

                <div className="register-footer">
                    <p>Already have an account?</p>
                    <button className="switch-button" onClick={onSwitchToLogin}>
                        Sign In
                    </button>
                </div>
            </div>
        </div>
    );
}

export default RegisterPage;
