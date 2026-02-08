import './UserList.css';

function UserList({ users, currentUser, onBan, onStatusChange, onUserClick }) {
    const currentStatus = currentUser?.status || 'online';

    const getInitials = (username) => {
        return username?.charAt(0).toUpperCase() || '?';
    };

    const getRoleLabel = (permission) => {
        switch (permission) {
            case 1: return 'Admin';
            case 2: return 'Mod';
            default: return 'Member';
        }
    };

    const getRoleClass = (permission) => {
        switch (permission) {
            case 1: return 'admin';
            case 2: return 'mod';
            default: return '';
        }
    };

    const canBan = currentUser?.permission <= 2;
    const onlineUsers = users.filter(u => u.status === 'online');

    const handleUserClick = (user) => {
        if (user.id !== currentUser?.id && onUserClick) {
            onUserClick(user);
        }
    };

    return (
        <div className="user-list">
            <div className="user-list-header">
                <h3>Your Status</h3>
                <div className="status-selector">
                    <button
                        className={`status-option ${currentStatus === 'online' ? 'active' : ''}`}
                        onClick={() => onStatusChange('online')}
                    >
                        <span className="status-dot online"></span>
                        On
                    </button>
                    <button
                        className={`status-option ${currentStatus === 'away' ? 'active' : ''}`}
                        onClick={() => onStatusChange('away')}
                    >
                        <span className="status-dot away"></span>
                        Away
                    </button>
                    <button
                        className={`status-option ${currentStatus === 'busy' ? 'active' : ''}`}
                        onClick={() => onStatusChange('busy')}
                    >
                        <span className="status-dot busy"></span>
                        Busy
                    </button>
                </div>
            </div>

            <div className="user-list-content">
                {users.map((user) => (
                    <div
                        key={user.id}
                        className={`user-item ${user.id !== currentUser?.id ? 'clickable' : ''}`}
                        onClick={() => handleUserClick(user)}
                        title={user.id !== currentUser?.id ? 'Click to add contact / start chat' : ''}
                    >
                        <div className="user-avatar">
                            {getInitials(user.username)}
                            <span className={`user-status-indicator ${user.status || 'offline'}`}></span>
                        </div>
                        <div className="user-info">
                            <div className="user-name">
                                {user.username}
                                {user.id === currentUser?.id && ' (You)'}
                            </div>
                            <div className={`user-role ${getRoleClass(user.permission)}`}>
                                {getRoleLabel(user.permission)}
                            </div>
                        </div>
                        {canBan && user.id !== currentUser?.id && user.permission > currentUser?.permission && (
                            <button
                                className="ban-button"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    onBan(user.id);
                                }}
                                title="Ban user"
                            >
                                Ban
                            </button>
                        )}
                    </div>
                ))}
            </div>

            <div className="online-count">
                <span>{onlineUsers.length}</span> / {users.length} users online
            </div>
        </div>
    );
}

export default UserList;
