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

    // Track message IDs to prevent duplicates
    const seenMessageIds = useRef(new Set());
    const seenPrivateMessageIds = useRef(new Set());

    // Refs to track current values for use in listeners (avoid stale closures)
    const selectedContactRef = useRef(null);
    const activeViewRef = useRef('group');

    const messagesEndRef = useRef(null);

    // Keep refs in sync with state
    useEffect(() => {
        selectedContactRef.current = selectedContact;
    }, [selectedContact]);

    useEffect(() => {
        activeViewRef.current = activeView;
    }, [activeView]);

    useEffect(() => {
        // ===== Group Chat Listeners =====
        wsService.on('onMessage', (msg) => {
            // Create a unique key for this message to detect duplicates
            const msgKey = `${msg.userId}-${msg.timestamp}-${msg.content}`;

            // Only add if we haven't seen this message before
            if (!seenMessageIds.current.has(msgKey)) {
                seenMessageIds.current.add(msgKey);
                setMessages((prev) => [...prev, msg]);
            }
        });

        wsService.on('onUserListUpdate', (userList) => {
            setUsers(userList);
        });

        // ===== Private Messaging Listeners =====
        wsService.on('onPrivateMessage', (msg) => {
            // Use refs to get current values (avoid stale closure)
            const currentContact = selectedContactRef.current;
            const currentView = activeViewRef.current;

            // Create a unique key to prevent duplicates
            const msgKey = `${msg.id}-${msg.senderId}-${msg.receiverId}`;

            if (seenPrivateMessageIds.current.has(msgKey)) {
                return; // Skip duplicate
            }
            seenPrivateMessageIds.current.add(msgKey);

            // Check if this message is for the currently open conversation
            const isCurrentConversation = currentContact &&
                (msg.senderId === currentContact.userId || msg.receiverId === currentContact.userId);

            if (isCurrentConversation) {
                // Add to current conversation (prepend for newest first)
                setPrivateMessages((prev) => [msg, ...prev]);
            }

            // Update unread count if message is from someone else and not viewing that chat
            if (msg.senderId !== user.id) {
                const isViewingThisChat = currentView === 'private' &&
                    currentContact && msg.senderId === currentContact.userId;

                if (!isViewingThisChat) {
                    setUnreadPrivate((prev) => prev + 1);
                }
            }
        });

        wsService.on('onConversationHistory', (data) => {
            // Clear seen IDs for this conversation and set new messages
            seenPrivateMessageIds.current.clear();
            data.messages.forEach(msg => {
                const msgKey = `${msg.id}-${msg.senderId}-${msg.receiverId}`;
                seenPrivateMessageIds.current.add(msgKey);
            });
            setPrivateMessages(data.messages);
        });

        wsService.on('onConversationsList', (convList) => {
            setConversations(convList);
            const totalUnread = convList.reduce((sum, c) => sum + c.unreadCount, 0);
            setUnreadPrivate(totalUnread);
        });

        // ===== Contacts Listeners =====
        wsService.on('onContactsList', (contactList) => {
            setContacts(contactList);
        });

        wsService.on('onContactAdded', (contact) => {
            setContacts((prev) => [...prev, contact]);
        });

        wsService.on('onSuccess', (message) => {
            console.log('Success:', message);
            // Refresh contacts when a contact is added
            wsService.getContacts();
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

        // Request initial contacts list
        wsService.getContacts();

        return () => {
            // Cleanup listeners
        };
    }, [onLogout, user.id]);

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
        seenPrivateMessageIds.current.clear();
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

    // ===== Quick Add Contact from User List =====
    const handleQuickAddContact = (targetUser) => {
        // Don't add yourself
        if (targetUser.id === user.id) return;

        // Check if already a contact
        const isExistingContact = contacts.some(c => c.userId === targetUser.id);
        if (isExistingContact) {
            // Switch to private view and select this contact
            const contact = contacts.find(c => c.userId === targetUser.id);
            if (contact) {
                setActiveView('private');
                handleSelectContact(contact);
            }
        } else {
            // Add as new contact
            wsService.addContact(targetUser.id, targetUser.username);
            // Switch to private view
            setActiveView('private');
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
                        💬 Group
                    </button>
                    <button
                        className={`tab-button ${activeView === 'private' ? 'active' : ''}`}
                        onClick={() => setActiveView('private')}
                    >
                        👤 Private
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
                                {messages.length === 0 ? (
                                    <div className="empty-state">
                                        <div className="empty-state-icon">💬</div>
                                        <p>No messages yet. Start the conversation!</p>
                                    </div>
                                ) : (
                                    messages.map((msg, index) => (
                                        <div
                                            key={`${msg.userId}-${msg.timestamp}-${index}`}
                                            className={`message ${msg.userId === user.id ? 'own-message' : ''}`}
                                        >
                                            <div className="message-header">
                                                <span className="message-username">{msg.username}</span>
                                                <span className="message-time">{formatTime(msg.timestamp)}</span>
                                            </div>
                                            <div className="message-content">{msg.content}</div>
                                        </div>
                                    ))
                                )}
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
                            onUserClick={handleQuickAddContact}
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
                            onUserClick={handleQuickAddContact}
                        />
                    </>
                )}
            </div>

            {/* Add Contact Modal */}
            {showAddContact && (
                <AddContactModal
                    users={users}
                    currentUser={user}
                    existingContacts={contacts}
                    onAdd={handleAddContact}
                    onClose={() => setShowAddContact(false)}
                />
            )}
        </div>
    );
}

export default ChatPage;
