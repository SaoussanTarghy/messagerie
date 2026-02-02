import { useState, useEffect } from 'react';
import wsService from '../services/websocket';
import './AddContactModal.css';

function AddContactModal({ onAdd, onClose, existingContacts }) {
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    const [contactName, setContactName] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        wsService.on('onUserSearchResults', (results) => {
            // Filter out existing contacts
            const existingIds = existingContacts.map(c => c.userId);
            const filtered = results.filter(u => !existingIds.includes(u.id));
            setSearchResults(filtered);
            setLoading(false);
        });

        return () => {
            wsService.off('onUserSearchResults');
        };
    }, [existingContacts]);

    const handleSearch = (value) => {
        setSearchTerm(value);
        setSelectedUser(null);
        
        if (value.trim().length >= 2) {
            setLoading(true);
            wsService.searchUsers(value);
        } else {
            setSearchResults([]);
        }
    };

    const handleSelectUser = (user) => {
        setSelectedUser(user);
        setContactName(user.username);
    };

    const handleAdd = () => {
        if (selectedUser) {
            onAdd(selectedUser.id, contactName || null);
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

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                    <h2>Add Contact</h2>
                    <button className="close-btn" onClick={onClose}>×</button>
                </div>

                <div className="modal-body">
                    {/* Search Input */}
                    <div className="search-section">
                        <label>Search by username or email:</label>
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={(e) => handleSearch(e.target.value)}
                            placeholder="Type to search..."
                            autoFocus
                        />
                    </div>

                    {/* Search Results */}
                    {loading && <div className="loading">Searching...</div>}
                    
                    {!loading && searchResults.length > 0 && !selectedUser && (
                        <div className="search-results">
                            <label>Select a user:</label>
                            {searchResults.map((user) => (
                                <div
                                    key={user.id}
                                    className="search-result-item"
                                    onClick={() => handleSelectUser(user)}
                                >
                                    <div className="user-avatar">
                                        <span>{user.username.charAt(0).toUpperCase()}</span>
                                        <div 
                                            className="status-dot"
                                            style={{ backgroundColor: getStatusColor(user.status) }}
                                        />
                                    </div>
                                    <div className="user-info">
                                        <span className="username">{user.username}</span>
                                        <span className="email">{user.email}</span>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}

                    {!loading && searchTerm.length >= 2 && searchResults.length === 0 && !selectedUser && (
                        <div className="no-results">
                            No users found matching "{searchTerm}"
                        </div>
                    )}

                    {/* Selected User */}
                    {selectedUser && (
                        <div className="selected-user-section">
                            <label>Selected User:</label>
                            <div className="selected-user">
                                <div className="user-avatar">
                                    <span>{selectedUser.username.charAt(0).toUpperCase()}</span>
                                </div>
                                <div className="user-info">
                                    <span className="username">{selectedUser.username}</span>
                                    <span className="name">{selectedUser.firstName} {selectedUser.lastName}</span>
                                </div>
                                <button 
                                    className="change-btn"
                                    onClick={() => setSelectedUser(null)}
                                >
                                    Change
                                </button>
                            </div>

                            <div className="contact-name-section">
                                <label>Contact Name (optional):</label>
                                <input
                                    type="text"
                                    value={contactName}
                                    onChange={(e) => setContactName(e.target.value)}
                                    placeholder="How should this contact appear?"
                                />
                                <span className="hint">
                                    Leave empty to use their username
                                </span>
                            </div>
                        </div>
                    )}
                </div>

                <div className="modal-footer">
                    <button className="cancel-btn" onClick={onClose}>
                        Cancel
                    </button>
                    <button 
                        className="add-btn" 
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
