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

    const getInitials = (name) => {
        return name?.charAt(0).toUpperCase() || '?';
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (inputMessage.trim()) {
            onSendMessage(inputMessage);
            setInputMessage('');
        }
    };

    if (!selectedContact) {
        return (
            <div className="private-chat">
                <div className="private-chat-empty">
                    <div className="private-chat-empty-icon">💬</div>
                    <h3>Select a conversation</h3>
                    <p>Choose a contact to start messaging</p>
                </div>
            </div>
        );
    }

    return (
        <div className="private-chat">
            <div className="private-chat-header">
                <button className="back-button" onClick={onBack} title="Back to contacts">
                    ←
                </button>
                <div className="private-chat-avatar">
                    {getInitials(selectedContact.contactName || selectedContact.username)}
                </div>
                <div className="private-chat-info">
                    <h3>{selectedContact.contactName || selectedContact.username}</h3>
                    <p>Private conversation</p>
                </div>
            </div>

            <div className="private-messages-container">
                {messages.length === 0 ? (
                    <div className="private-chat-empty">
                        <p>No messages yet. Say hello!</p>
                    </div>
                ) : (
                    messages.map((msg, index) => (
                        <div
                            key={index}
                            className={`private-message ${msg.senderId === currentUser.id ? 'sent' : 'received'}`}
                        >
                            <div className="private-message-content">{msg.content}</div>
                            <div className="private-message-time">{formatTime(msg.timestamp)}</div>
                        </div>
                    ))
                )}
                <div ref={messagesEndRef} />
            </div>

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
