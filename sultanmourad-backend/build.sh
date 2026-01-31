#!/bin/bash

# Compile and Run Server (Original + WebSocket)
echo "Compiling ISGA Chat..."
mkdir -p bin

# Compile all Java files
javac -cp "lib/*" -d bin \
    common/src/main/java/com/isga/chat/common/*.java \
    server/src/main/java/com/isga/chat/dao/*.java \
    server/src/main/java/com/isga/chat/server/*.java \
    server/src/main/java/com/isga/chat/websocket/*.java \
    client/src/main/java/com/isga/chat/view/*.java \
    client/src/main/java/com/isga/chat/controller/*.java \
    client/src/main/java/com/isga/chat/client/*.java

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
echo "3. Java Swing Client:"
echo "   java -cp \"bin\" com.isga.chat.client.ClientMain"
echo ""
echo "4. React Frontend (in separate terminal):"
echo "   cd frontend && npm install && npm run dev"
echo ""
