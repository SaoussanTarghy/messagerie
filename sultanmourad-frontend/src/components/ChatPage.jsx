import { useState, useEffect, useRef } from 'react';
import wsService from '../services/websocket';
import UserList from './UserList';
import './ChatPage.css';

function ChatPage({ user, onLogout }) {
    const [messages, setMessages] = useState([]);
    const [users, setUsers] = useState([]);
    const [inputMessage, setInputMessage] = useState('');
    const messagesEndRef = useRef(null);

    useEffect(() => {
        // Set up message listeners
        wsService.on('onMessage', (msg) => {
            setMessages((prev) => [...prev, msg]);
        });

        wsService.on('onUserListUpdate', (userList) => {
            setUsers(userList);
        });

        wsService.on('onError', (error) => {
            if (error === 'You have been banned.') {
                alert('You have been banned!');
                onLogout();
            }
        });

        wsService.on('onClose', () => {
            onLogout();
        });

        return () => {
            // Cleanup
        };
    }, [onLogout]);

    useEffect(() => {
        // Scroll to bottom when new messages arrive
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    const handleSendMessage = (e) => {
        e.preventDefault();
        if (inputMessage.trim()) {
            wsService.sendMessage(inputMessage);
            setInputMessage('');
        }
    };

    const handleStatusChange = (status) => {
        wsService.changeStatus(status);
    };

    const handleBan = (userId) => {
        if (window.confirm('Are you sure you want to ban this user?')) {
            wsService.banUser(userId);
        }
    };

    const handleLogout = () => {
        wsService.logout();
        onLogout();
    };

    const formatTime = (timestamp) => {
        if (!timestamp) return '';
        const date = new Date(timestamp);
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    };

    return (
        <div className="chat-container">
            {/* Header */}
            <div className="chat-header">
                <div className="header-info">
                    <h1>ISGA Chat</h1>
                    <span className="welcome-text">Welcome, {user.username}!</span>
                </div>
                <button className="logout-button" onClick={handleLogout}>
                    Logout
                </button>
            </div>

            {/* Main Content */}
            <div className="chat-main">
                {/* Messages Area */}
                <div className="messages-area">
                    <div className="messages-container">
                        {messages.map((msg, index) => (
                            <div
                                key={index}
                                className={`message ${msg.userId === user.id ? 'own-message' : ''}`}
                            >
                                <div className="message-header">
                                    <span className="message-username">{msg.username}</span>
                                    <span className="message-time">{formatTime(msg.timestamp)}</span>
                                </div>
                                <div className="message-content">{msg.content}</div>
                            </div>
                        ))}
                        <div ref={messagesEndRef} />
                    </div>

                    {/* Message Input */}
                    <form className="message-form" onSubmit={handleSendMessage}>
                        <input
                            type="text"
                            value={inputMessage}
                            onChange={(e) => setInputMessage(e.target.value)}
                            placeholder="Type a message..."
                            autoFocus
                        />
                        <button type="submit" disabled={!inputMessage.trim()}>
                            Send
                        </button>
                    </form>
                </div>

                {/* User List Sidebar */}
                <UserList
                    users={users}
                    currentUser={user}
                    onBan={handleBan}
                    onStatusChange={handleStatusChange}
                />
            </div>
        </div>
    );
}

export default ChatPage;
