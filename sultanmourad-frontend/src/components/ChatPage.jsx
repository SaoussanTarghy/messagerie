import { useState, useEffect, useRef } from 'react';
import wsService from '../services/websocket';
import UserList from './UserList';
import ContactsSidebar from './ContactsSidebar';
import PrivateChat from './PrivateChat';
import AddContactModal from './AddContactModal';
import './ChatPage.css';

function ChatPage({ user, onLogout }) {
    // View state: 'group' or 'private'
    const [activeView, setActiveView] = useState('group');
    
    // Group chat state
    const [messages, setMessages] = useState([]);
    const [users, setUsers] = useState([]);
    const [inputMessage, setInputMessage] = useState('');
    
    // Private messaging state
    const [contacts, setContacts] = useState([]);
    const [conversations, setConversations] = useState([]);
    const [selectedContact, setSelectedContact] = useState(null);
    const [privateMessages, setPrivateMessages] = useState([]);
    
    // UI state
    const [showAddContact, setShowAddContact] = useState(false);
    const [unreadPrivate, setUnreadPrivate] = useState(0);
    
    const messagesEndRef = useRef(null);

    useEffect(() => {
        // ===== Group Chat Listeners =====
        wsService.on('onMessage', (msg) => {
            setMessages((prev) => [...prev, msg]);
        });

        wsService.on('onUserListUpdate', (userList) => {
            setUsers(userList);
        });

        // ===== Private Messaging Listeners =====
        wsService.on('onPrivateMessage', (msg) => {
            // If this message is from/to the selected contact, add it to the chat
            if (selectedContact && 
                (msg.senderId === selectedContact.userId || msg.receiverId === selectedContact.userId)) {
                setPrivateMessages((prev) => [msg, ...prev]);
            }
            // Update unread count if not viewing this conversation
            if (msg.senderId !== user.id && 
                (!selectedContact || msg.senderId !== selectedContact.userId || activeView !== 'private')) {
                setUnreadPrivate((prev) => prev + 1);
            }
        });

        wsService.on('onConversationHistory', (data) => {
            setPrivateMessages(data.messages);
        });

        wsService.on('onConversationsList', (convList) => {
            setConversations(convList);
            // Calculate total unread
            const totalUnread = convList.reduce((sum, c) => sum + c.unreadCount, 0);
            setUnreadPrivate(totalUnread);
        });

        // ===== Contacts Listeners =====
        wsService.on('onContactsList', (contactList) => {
            setContacts(contactList);
        });

        wsService.on('onSuccess', (message) => {
            console.log('Success:', message);
        });

        // ===== Error Handling =====
        wsService.on('onError', (error) => {
            if (error === 'You have been banned.') {
                alert('You have been banned!');
                onLogout();
            } else {
                console.error('Error:', error);
            }
        });

        wsService.on('onClose', () => {
            onLogout();
        });

        return () => {
            // Cleanup listeners
        };
    }, [onLogout, selectedContact, activeView, user.id]);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages, privateMessages]);

    // ===== Group Chat Handlers =====
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

    // ===== Private Chat Handlers =====
    const handleSelectContact = (contact) => {
        setSelectedContact(contact);
        setPrivateMessages([]);
        wsService.getConversation(contact.userId);
    };

    const handleSendPrivateMessage = (content) => {
        if (selectedContact && content.trim()) {
            wsService.sendPrivateMessage(selectedContact.userId, content);
        }
    };

    const handleBackToContacts = () => {
        setSelectedContact(null);
        setPrivateMessages([]);
    };

    // ===== Contact Management =====
    const handleAddContact = (userId, contactName) => {
        wsService.addContact(userId, contactName);
        setShowAddContact(false);
    };

    const handleRemoveContact = (userId) => {
        if (window.confirm('Remove this contact?')) {
            wsService.removeContact(userId);
            if (selectedContact?.userId === userId) {
                setSelectedContact(null);
            }
        }
    };

    // ===== Logout =====
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
                
                {/* View Toggle Tabs */}
                <div className="view-tabs">
                    <button 
                        className={`tab-button ${activeView === 'group' ? 'active' : ''}`}
                        onClick={() => setActiveView('group')}
                    >
                        💬 Groupe
                    </button>
                    <button 
                        className={`tab-button ${activeView === 'private' ? 'active' : ''}`}
                        onClick={() => setActiveView('private')}
                    >
                        👤 Privé
                        {unreadPrivate > 0 && (
                            <span className="unread-badge">{unreadPrivate}</span>
                        )}
                    </button>
                </div>

                <button className="logout-button" onClick={handleLogout}>
                    Logout
                </button>
            </div>

            {/* Main Content */}
            <div className="chat-main">
                {activeView === 'group' ? (
                    // ===== GROUP CHAT VIEW =====
                    <>
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

                        <UserList
                            users={users}
                            currentUser={user}
                            onBan={handleBan}
                            onStatusChange={handleStatusChange}
                        />
                    </>
                ) : (
                    // ===== PRIVATE CHAT VIEW =====
                    <>
                        <ContactsSidebar
                            contacts={contacts}
                            conversations={conversations}
                            selectedContact={selectedContact}
                            onSelectContact={handleSelectContact}
                            onAddContact={() => setShowAddContact(true)}
                            onRemoveContact={handleRemoveContact}
                        />

                        <PrivateChat
                            selectedContact={selectedContact}
                            messages={privateMessages}
                            currentUser={user}
                            onSendMessage={handleSendPrivateMessage}
                            onBack={handleBackToContacts}
                            formatTime={formatTime}
                        />

                        <UserList
                            users={users}
                            currentUser={user}
                            onBan={handleBan}
                            onStatusChange={handleStatusChange}
                        />
                    </>
                )}
            </div>

            {/* Add Contact Modal */}
            {showAddContact && (
                <AddContactModal
                    onAdd={handleAddContact}
                    onClose={() => setShowAddContact(false)}
                    existingContacts={contacts}
                />
            )}
        </div>
    );
}

export default ChatPage;
