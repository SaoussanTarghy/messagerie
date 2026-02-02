/**
 * WebSocket service for connecting to the Java WebSocket server.
 * Handles group chat, private messaging, and contacts.
 */

const WS_URL = 'ws://localhost:8888';

class WebSocketService {
  constructor() {
    this.socket = null;
    this.listeners = {
      // Auth
      onLoginResponse: null,
      onRegisterResponse: null,
      onError: null,
      onClose: null,
      // Group Chat
      onMessage: null,
      onUserListUpdate: null,
      // Private Messaging
      onPrivateMessage: null,
      onConversationHistory: null,
      onConversationsList: null,
      // Contacts
      onContactsList: null,
      onContactAdded: null,
      onUserSearchResults: null,
      onSuccess: null,
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
        // Auth responses
        case 'LOGIN_RESPONSE':
          if (this.listeners.onLoginResponse) {
            this.listeners.onLoginResponse(message.payload);
          }
          break;
        case 'REGISTER_RESPONSE':
          if (this.listeners.onRegisterResponse) {
            this.listeners.onRegisterResponse(message.payload);
          }
          break;
        case 'ERROR':
          if (this.listeners.onError) {
            this.listeners.onError(message.payload);
          }
          break;
        case 'SUCCESS':
          if (this.listeners.onSuccess) {
            this.listeners.onSuccess(message.payload);
          }
          break;

        // Group Chat
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

        // Private Messaging
        case 'PRIVATE_MESSAGE':
          if (this.listeners.onPrivateMessage) {
            this.listeners.onPrivateMessage(message.payload);
          }
          break;
        case 'CONVERSATION_HISTORY':
          if (this.listeners.onConversationHistory) {
            this.listeners.onConversationHistory(message.payload);
          }
          break;
        case 'CONVERSATIONS_LIST':
          if (this.listeners.onConversationsList) {
            this.listeners.onConversationsList(message.payload);
          }
          break;

        // Contacts
        case 'CONTACTS_LIST':
          if (this.listeners.onContactsList) {
            this.listeners.onContactsList(message.payload);
          }
          break;
        case 'CONTACT_ADDED':
          if (this.listeners.onContactAdded) {
            this.listeners.onContactAdded(message.payload);
          }
          break;
        case 'USER_SEARCH_RESULTS':
          if (this.listeners.onUserSearchResults) {
            this.listeners.onUserSearchResults(message.payload);
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

  // ==================== Auth ====================

  async login(username, password) {
    const hashedPassword = await this.hashPassword(password);
    this.send('LOGIN_REQUEST', { username, password: hashedPassword });
  }

  async register(username, email, password) {
    const hashedPassword = await this.hashPassword(password);
    this.send('REGISTER_REQUEST', { username, email, password: hashedPassword });
  }

  logout() {
    this.send('LOGOUT_REQUEST', null);
    if (this.socket) {
      this.socket.close();
    }
  }

  // ==================== Group Chat ====================

  sendMessage(content) {
    this.send('MESSAGE_SEND', content);
  }

  changeStatus(status) {
    this.send('STATUS_CHANGE_REQUEST', status);
  }

  banUser(userId) {
    this.send('BAN_REQUEST', userId);
  }

  // ==================== Private Messaging ====================

  sendPrivateMessage(receiverId, content) {
    this.send('PRIVATE_MESSAGE_SEND', { receiverId, content });
  }

  getConversation(otherUserId) {
    this.send('GET_CONVERSATION', otherUserId);
  }

  getConversations() {
    this.send('GET_CONVERSATIONS', null);
  }

  markAsRead(senderId) {
    this.send('MARK_AS_READ', senderId);
  }

  // ==================== Contacts ====================

  addContact(userId, contactName = null) {
    this.send('ADD_CONTACT', { userId, contactName });
  }

  removeContact(userId) {
    this.send('REMOVE_CONTACT', userId);
  }

  getContacts() {
    this.send('GET_CONTACTS', null);
  }

  searchUsers(searchTerm) {
    this.send('SEARCH_USERS', searchTerm);
  }

  // ==================== Utilities ====================

  async hashPassword(password) {
    const encoder = new TextEncoder();
    const data = encoder.encode(password);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
    return hashHex;
  }

  on(event, callback) {
    if (event in this.listeners) {
      this.listeners[event] = callback;
    }
  }

  off(event) {
    if (event in this.listeners) {
      this.listeners[event] = null;
    }
  }

  disconnect() {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  }
}

const wsService = new WebSocketService();
export default wsService;
