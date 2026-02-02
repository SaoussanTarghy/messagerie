import { useState } from 'react';
import './ContactsSidebar.css';

function ContactsSidebar({
    contacts,
    conversations,
    selectedContact,
    onSelectContact,
    onAddContact,
    onRemoveContact
}) {
    const [searchTerm, setSearchTerm] = useState('');

    const getInitials = (name) => {
        return name?.charAt(0).toUpperCase() || '?';
    };

    // Merge contacts with conversation data for unread counts
    const contactsWithUnread = contacts.map(contact => {
        const conv = conversations.find(c => c.otherUserId === contact.userId);
        return {
            ...contact,
            unreadCount: conv?.unreadCount || 0,
            lastMessage: conv?.lastMessage || null
        };
    });

    const filteredContacts = contactsWithUnread.filter(contact =>
        contact.contactName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        contact.username?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="contacts-sidebar">
            <div className="contacts-header">
                <h3>Contacts</h3>
                <button className="add-contact-button" onClick={onAddContact} title="Add contact">
                    +
                </button>
            </div>

            <div className="contacts-search">
                <input
                    type="text"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    placeholder="Search contacts..."
                />
            </div>

            {filteredContacts.length === 0 ? (
                <div className="contacts-empty">
                    <div className="contacts-empty-icon">👥</div>
                    <p>{searchTerm ? 'No contacts found' : 'No contacts yet'}</p>
                </div>
            ) : (
                <div className="contacts-list">
                    {filteredContacts.map((contact) => (
                        <div
                            key={contact.userId}
                            className={`contact-item ${selectedContact?.userId === contact.userId ? 'selected' : ''}`}
                            onClick={() => onSelectContact(contact)}
                        >
                            <div className="contact-avatar">
                                {getInitials(contact.contactName || contact.username)}
                            </div>
                            <div className="contact-info">
                                <div className="contact-name">
                                    {contact.contactName || contact.username}
                                </div>
                                {contact.lastMessage && (
                                    <div className="contact-preview">
                                        {contact.lastMessage}
                                    </div>
                                )}
                            </div>
                            {contact.unreadCount > 0 && (
                                <span className="contact-unread">{contact.unreadCount}</span>
                            )}
                            <button
                                className="contact-remove"
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
