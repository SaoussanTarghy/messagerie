import { useState, useRef, useEffect } from 'react';
import './PrivateChat.css';

function PrivateChat({ 
    selectedContact, 
    messages, 
    currentUser, 
    onSendMessage, 
    onBack,
    formatTime 
}) {
    const [inputMessage, setInputMessage] = useState('');
    const messagesEndRef = useRef(null);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (inputMessage.trim()) {
            onSendMessage(inputMessage);
            setInputMessage('');
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'online': return '#22c55e';
            case 'away': return '#f59e0b';
            case 'offline': return '#6b7280';
            default: return '#6b7280';
        }
    };

    if (!selectedContact) {
        return (
            <div className="private-chat empty-chat">
                <div className="empty-state">
                    <div className="empty-icon">💬</div>
                    <h3>Select a conversation</h3>
                    <p>Choose a contact from the list to start chatting</p>
                </div>
            </div>
        );
    }

    // Sort messages by timestamp (oldest first for display)
    const sortedMessages = [...messages].sort((a, b) => 
        new Date(a.timestamp) - new Date(b.timestamp)
    );

    return (
        <div className="private-chat">
            {/* Chat Header */}
            <div className="private-chat-header">
                <button className="back-btn" onClick={onBack}>
                    ← Back
                </button>
                <div className="contact-header-info">
                    <div className="contact-avatar-large">
                        <span className="avatar-letter">
                            {(selectedContact.contactName || selectedContact.username).charAt(0).toUpperCase()}
                        </span>
                        <div 
                            className="status-indicator"
                            style={{ backgroundColor: getStatusColor(selectedContact.status) }}
                        />
                    </div>
                    <div className="contact-details">
                        <h3>{selectedContact.contactName || selectedContact.username}</h3>
                        <span className="contact-status">{selectedContact.status}</span>
                    </div>
                </div>
            </div>

            {/* Messages */}
            <div className="private-messages-container">
                {sortedMessages.length === 0 ? (
                    <div className="no-messages">
                        <p>No messages yet</p>
                        <p className="hint">Send a message to start the conversation</p>
                    </div>
                ) : (
                    sortedMessages.map((msg, index) => (
                        <div
                            key={msg.id || index}
                            className={`private-message ${msg.senderId === currentUser.id ? 'sent' : 'received'}`}
                        >
                            <div className="message-bubble">
                                <div className="message-content">{msg.content}</div>
                                <div className="message-meta">
                                    <span className="message-time">{formatTime(msg.timestamp)}</span>
                                    {msg.senderId === currentUser.id && (
                                        <span className="message-status">
                                            {msg.isRead ? '✓✓' : '✓'}
                                        </span>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))
                )}
                <div ref={messagesEndRef} />
            </div>

            {/* Message Input */}
            <form className="private-message-form" onSubmit={handleSubmit}>
                <input
                    type="text"
                    value={inputMessage}
                    onChange={(e) => setInputMessage(e.target.value)}
                    placeholder={`Message ${selectedContact.contactName || selectedContact.username}...`}
                    autoFocus
                />
                <button type="submit" disabled={!inputMessage.trim()}>
                    Send
                </button>
            </form>
        </div>
    );
}

export default PrivateChat;
