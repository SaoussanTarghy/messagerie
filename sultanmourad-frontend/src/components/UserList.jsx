import './UserList.css';

function UserList({ users, currentUser, onBan, onStatusChange }) {
    const getStatusColor = (status) => {
        switch (status) {
            case 'online': return '#22c55e';
            case 'away': return '#f59e0b';
            case 'offline': return '#6b7280';
            default: return '#6b7280';
        }
    };

    const getPermissionBadge = (permission) => {
        switch (permission) {
            case 1: return { text: 'Admin', color: '#ef4444' };
            case 2: return { text: 'Mod', color: '#8b5cf6' };
            default: return null;
        }
    };

    const canBan = currentUser && currentUser.permission <= 2;

    return (
        <div className="user-list">
            <div className="user-list-header">
                <h3>Online Users</h3>
                <span className="user-count">{users.filter(u => u.status === 'online').length} online</span>
            </div>

            <div className="status-selector">
                <label>My Status:</label>
                <select
                    onChange={(e) => onStatusChange(e.target.value)}
                    defaultValue={currentUser?.status || 'online'}
                >
                    <option value="online">🟢 Online</option>
                    <option value="away">🟡 Away</option>
                    <option value="offline">⚫ Offline</option>
                </select>
            </div>

            <div className="users-container">
                {users.map((user) => {
                    const badge = getPermissionBadge(user.permission);
                    const isCurrentUser = currentUser && user.id === currentUser.id;

                    return (
                        <div
                            key={user.id}
                            className={`user-item ${isCurrentUser ? 'current-user' : ''} ${user.isBanned ? 'banned' : ''}`}
                        >
                            <div className="user-info">
                                <div
                                    className="status-indicator"
                                    style={{ backgroundColor: getStatusColor(user.status) }}
                                />
                                <span className="username">
                                    {user.username}
                                    {isCurrentUser && ' (you)'}
                                </span>
                                {badge && (
                                    <span className="badge" style={{ backgroundColor: badge.color }}>
                                        {badge.text}
                                    </span>
                                )}
                                {user.isBanned && (
                                    <span className="badge banned-badge">Banned</span>
                                )}
                            </div>

                            {canBan && !isCurrentUser && !user.isBanned && user.permission > currentUser.permission && (
                                <button
                                    className="ban-button"
                                    onClick={() => onBan(user.id)}
                                    title="Ban user"
                                >
                                    Ban
                                </button>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
}

export default UserList;
