#!/bin/bash

# Compile and Run Server (Original + WebSocket)
echo "Compiling ISGA Chat..."
mkdir -p bin

# Compile all Java files with correct paths
javac -cp "lib/*" -d bin \
    common/src/main/java/com/isga/chat/common/*.java \
    src/main/java/com/isga/chat/dao/*.java \
    src/main/java/com/isga/chat/server/*.java \
    src/main/java/com/isga/chat/websocket/*.java

echo ""
echo "Compilation finished!"
echo ""
echo "Available commands:"
echo "==================="
echo ""
echo "1. Original Socket Server (port 12345):"
echo "   java -cp \"bin:lib/*\" com.isga.chat.server.ServerMain"
echo ""
echo "2. WebSocket Server for React Frontend (port 8080):"
echo "   java -cp \"bin:lib/*\" com.isga.chat.websocket.WebSocketServer"
echo ""
echo "3. React Frontend (in separate terminal):"
echo "   cd frontend && npm install && npm run dev"
echo ""
