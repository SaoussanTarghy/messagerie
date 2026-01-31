/**
 * WebSocket service for connecting to the Java WebSocket server.
 * Handles all communication with the backend.
 */

const WS_URL = 'ws://localhost:8080';

class WebSocketService {
  constructor() {
    this.socket = null;
    this.listeners = {
      onLoginResponse: null,
      onRegisterResponse: null,
      onMessage: null,
      onUserListUpdate: null,
      onError: null,
      onClose: null,
    };
  }

  connect() {
    return new Promise((resolve, reject) => {
      this.socket = new WebSocket(WS_URL);

      this.socket.onopen = () => {
        console.log('WebSocket connected');
        resolve();
      };

      this.socket.onerror = (error) => {
        console.error('WebSocket error:', error);
        reject(error);
      };

      this.socket.onclose = () => {
        console.log('WebSocket disconnected');
        if (this.listeners.onClose) {
          this.listeners.onClose();
        }
      };

      this.socket.onmessage = (event) => {
        this.handleMessage(event.data);
      };
    });
  }

  handleMessage(data) {
    try {
      const message = JSON.parse(data);
      console.log('Received:', message);

      switch (message.type) {
        case 'LOGIN_RESPONSE':
          if (this.listeners.onLoginResponse) {
            this.listeners.onLoginResponse(message.payload);
          }
          break;
        case 'MESSAGE_BROADCAST':
          if (this.listeners.onMessage) {
            this.listeners.onMessage(message.payload);
          }
          break;
        case 'USER_LIST_UPDATE':
          if (this.listeners.onUserListUpdate) {
            this.listeners.onUserListUpdate(message.payload);
          }
          break;
        case 'ERROR':
          if (this.listeners.onError) {
            this.listeners.onError(message.payload);
          }
          break;
        case 'REGISTER_RESPONSE':
          if (this.listeners.onRegisterResponse) {
            this.listeners.onRegisterResponse(message.payload);
          }
          break;
        default:
          console.log('Unknown message type:', message.type);
      }
    } catch (error) {
      console.error('Failed to parse message:', error);
    }
  }

  send(type, payload) {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      const message = { type, payload };
      console.log('Sending:', message);
      this.socket.send(JSON.stringify(message));
    } else {
      console.error('WebSocket not connected');
    }
  }

  // Login with username and hashed password
  async login(username, password) {
    const hashedPassword = await this.hashPassword(password);
    this.send('LOGIN_REQUEST', { username, password: hashedPassword });
  }

  // Register a new user
  async register(username, email, password) {
    const hashedPassword = await this.hashPassword(password);
    this.send('REGISTER_REQUEST', { username, email, password: hashedPassword });
  }

  // Send chat message
  sendMessage(content) {
    this.send('MESSAGE_SEND', content);
  }

  // Change user status
  changeStatus(status) {
    this.send('STATUS_CHANGE_REQUEST', status);
  }

  // Ban a user (admin/mod only)
  banUser(userId) {
    this.send('BAN_REQUEST', userId);
  }

  // Logout
  logout() {
    this.send('LOGOUT_REQUEST', null);
    if (this.socket) {
      this.socket.close();
    }
  }

  // SHA-256 hash using Web Crypto API
  async hashPassword(password) {
    const encoder = new TextEncoder();
    const data = encoder.encode(password);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
    return hashHex;
  }

  // Set event listeners
  on(event, callback) {
    if (event in this.listeners) {
      this.listeners[event] = callback;
    }
  }

  disconnect() {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  }
}

// Singleton instance
const wsService = new WebSocketService();
export default wsService;
