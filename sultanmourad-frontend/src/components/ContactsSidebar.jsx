import './ContactsSidebar.css';

function ContactsSidebar({ 
    contacts, 
    conversations, 
    selectedContact, 
    onSelectContact, 
    onAddContact,
    onRemoveContact 
}) {
    const getStatusColor = (status) => {
        switch (status) {
            case 'online': return '#22c55e';
            case 'away': return '#f59e0b';
            case 'offline': return '#6b7280';
            default: return '#6b7280';
        }
    };

    // Merge contacts with conversation data
    const getContactsWithConversations = () => {
        return contacts.map(contact => {
            const conv = conversations.find(c => c.otherUserId === contact.userId);
            return {
                ...contact,
                lastMessage: conv?.lastMessage || null,
                lastMessageTime: conv?.lastMessageTime || null,
                unreadCount: contact.unreadCount || conv?.unreadCount || 0
            };
        }).sort((a, b) => {
            // Sort by last message time, contacts with messages first
            if (a.lastMessageTime && !b.lastMessageTime) return -1;
            if (!a.lastMessageTime && b.lastMessageTime) return 1;
            if (a.lastMessageTime && b.lastMessageTime) {
                return new Date(b.lastMessageTime) - new Date(a.lastMessageTime);
            }
            return 0;
        });
    };

    const formatLastMessage = (message) => {
        if (!message) return 'No messages yet';
        return message.length > 30 ? message.substring(0, 30) + '...' : message;
    };

    const formatTime = (timestamp) => {
        if (!timestamp) return '';
        const date = new Date(timestamp);
        const today = new Date();
        
        if (date.toDateString() === today.toDateString()) {
            return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        }
        return date.toLocaleDateString([], { month: 'short', day: 'numeric' });
    };

    const contactsWithConv = getContactsWithConversations();

    return (
        <div className="contacts-sidebar">
            <div className="contacts-header">
                <h3>💬 Messages</h3>
                <button className="add-contact-btn" onClick={onAddContact}>
                    + Add
                </button>
            </div>

            {contactsWithConv.length === 0 ? (
                <div className="no-contacts">
                    <p>No contacts yet</p>
                    <p className="hint">Click "+ Add" to add contacts</p>
                </div>
            ) : (
                <div className="contacts-list">
                    {contactsWithConv.map((contact) => (
                        <div
                            key={contact.userId}
                            className={`contact-item ${selectedContact?.userId === contact.userId ? 'selected' : ''}`}
                            onClick={() => onSelectContact(contact)}
                        >
                            <div className="contact-avatar">
                                <div 
                                    className="status-dot"
                                    style={{ backgroundColor: getStatusColor(contact.status) }}
                                />
                                <span className="avatar-letter">
                                    {(contact.contactName || contact.username).charAt(0).toUpperCase()}
                                </span>
                            </div>
                            
                            <div className="contact-info">
                                <div className="contact-name-row">
                                    <span className="contact-name">
                                        {contact.contactName || contact.username}
                                    </span>
                                    {contact.lastMessageTime && (
                                        <span className="last-time">
                                            {formatTime(contact.lastMessageTime)}
                                        </span>
                                    )}
                                </div>
                                <div className="contact-preview-row">
                                    <span className="last-message">
                                        {formatLastMessage(contact.lastMessage)}
                                    </span>
                                    {contact.unreadCount > 0 && (
                                        <span className="unread-count">{contact.unreadCount}</span>
                                    )}
                                </div>
                            </div>

                            <button 
                                className="remove-contact-btn"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    onRemoveContact(contact.userId);
                                }}
                                title="Remove contact"
                            >
                                ×
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default ContactsSidebar;
