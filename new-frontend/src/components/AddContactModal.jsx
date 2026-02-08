import { useState } from 'react';
import './AddContactModal.css';

function AddContactModal({ users, currentUser, existingContacts, onAdd, onClose }) {
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedUser, setSelectedUser] = useState(null);

    const getInitials = (name) => {
        return name?.charAt(0).toUpperCase() || '?';
    };

    // Filter out:
    // 1. Current user
    // 2. Users already in contacts
    const existingContactIds = existingContacts.map(c => c.userId);

    const availableUsers = users.filter(user =>
        user.id !== currentUser.id &&
        !existingContactIds.includes(user.id) &&
        user.username.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const handleAdd = () => {
        if (selectedUser) {
            onAdd(selectedUser.id, selectedUser.username);
        }
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={e => e.stopPropagation()}>
                <div className="modal-header">
                    <h2>Add Contact</h2>
                    <button className="modal-close" onClick={onClose}>×</button>
                </div>

                <div className="modal-search">
                    <input
                        type="text"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        placeholder="Search users..."
                        autoFocus
                    />
                </div>

                {availableUsers.length === 0 ? (
                    <div className="modal-empty">
                        <div className="modal-empty-icon">👥</div>
                        <p>{searchTerm ? 'No users found' : 'No users available to add'}</p>
                    </div>
                ) : (
                    <div className="modal-user-list">
                        {availableUsers.map(user => (
                            <div
                                key={user.id}
                                className={`modal-user-item ${selectedUser?.id === user.id ? 'selected' : ''}`}
                                onClick={() => setSelectedUser(user)}
                            >
                                <div className="modal-user-avatar">
                                    {getInitials(user.username)}
                                </div>
                                <div className="modal-user-info">
                                    <div className="modal-user-name">{user.username}</div>
                                    <div className="modal-user-status">{user.status || 'offline'}</div>
                                </div>
                                {selectedUser?.id === user.id && (
                                    <div className="modal-user-check">✓</div>
                                )}
                            </div>
                        ))}
                    </div>
                )}

                <div className="modal-actions">
                    <button className="modal-cancel" onClick={onClose}>
                        Cancel
                    </button>
                    <button
                        className="modal-confirm"
                        onClick={handleAdd}
                        disabled={!selectedUser}
                    >
                        Add Contact
                    </button>
                </div>
            </div>
        </div>
    );
}

export default AddContactModal;
