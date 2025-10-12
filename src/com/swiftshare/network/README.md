## Networking Component for SwiftShare

The networking layer of SwiftShare handles all peer-to-peer communication, file discovery, and data transfer between connected clients.

### Architecture Overview

The networking system is built around a room-based model where one peer acts as a host running a server, while other peers join as clients. This design provides simplicity while maintaining true P2P capabilities for file transfers.

**Core Components:**
- **RoomServer** and **RoomClient** handle the server/client connection model
- **PeerConnection** manages individual peer-to-peer sockets with dual stream support for text messages and binary data
- **Message** provides a simple serialization protocol for control messages
- **NetworkDiscovery** enables peers to find rooms on the local network via UDP broadcast
- **NetworkManager** acts as the main API, coordinating all networking operations
- **FileTransferManager** handles chunked file transfers with progress tracking
- **HeartbeatManager** keeps idle connections alive

### Quick Start

#### Creating a Room (Host)

```java
NetworkManager nm = new NetworkManager(callback);
nm.createRoom(8080);
```

#### Joining a Room (Client)

```java
NetworkManager nm = new NetworkManager(callback);
nm.joinRoom("192.168.1.100", 8080);
```

#### Discovering Rooms

```java
NetworkDiscovery discovery = new NetworkDiscovery();
discovery.startListening((host, port) -> {
    System.out.println("Found room at " + host + ":" + port);
});
```

#### Sending Files

```java
File file = new File("document.pdf");
FileMetadata metadata = new FileMetadata(
    file.getName(),
    file.length(),
    calculateHash(file),
    totalChunks
);
nm.sendFile(file, fileChunks, metadata);
```

### Message Protocol

All control messages use a pipe-delimited format for easy serialization:

- `JOIN_ROOM|roomId` - Peer joins a room
- `LEAVE_ROOM` - Peer leaves
- `FILE_OFFER|fileName|fileSize|totalChunks|fileHash` - Initiates transfer
- `FILE_ACCEPT|fileName` - Accepts incoming file
- `FILE_REJECT|fileName` - Declines incoming file
- `CHUNK_META|fileName|chunkIndex|totalChunks` - Metadata before sending chunk
- `FILE_COMPLETE|fileName|fileHash` - Transfer finished
- `HEARTBEAT` - Keep-alive signal
- `ERROR|errorMessage` - Error notification

### Data Transfer

File transfers use a dual-stream approach on each socket:
- **Text stream** for control messages
- **Binary stream** for raw file chunks (prefixed with 4-byte length)

This allows efficient parallel handling of control flow and data transfer without message framing overhead.

### Callbacks

The `NetworkCallback` interface provides real-time updates:

```java
public interface NetworkCallback {
    void onRoomCreated(int port);
    void onRoomJoined(String host, int port);
    void onPeerConnected(PeerInfo peer);
    void onPeerDisconnected(PeerInfo peer);
    void onFileOfferReceived(FileMetadata metadata);
    void onTransferProgress(String fileName, double percent, String speed);
    void onTransferComplete(String fileName);
    void onConnectionLost();
    void onError(String message);
}
```

### Threading Model

All blocking operations run on background threads:
- Server accepts connections in a cached thread pool
- Clients listen for messages in a dedicated thread
- File transfers happen asynchronously with progress callbacks
- Heartbeats are sent on a scheduled executor

### Connection Management

- Connections timeout after 10 seconds of inactivity
- Binary chunk size is capped at 10MB for safety
- Heartbeat manager can be used to prevent timeout on idle connections
- Graceful disconnection cleans up all resources

### Package Structure

```
network/
  ├── core/           (Connection handling)
  ├── discovery/      (Peer discovery)
  ├── manager/        (High-level API)
  ├── transfer/       (File transfer logic)
  └── utils/          (Helper utilities)
models/
  ├── FileMetadata    (File transfer info)
  ├── PeerInfo        (Peer details)
  └── TransferStatus  (Progress tracking)
```

### Integration

The `NetworkManager` class provides a clean single-point API for GUI and other components. It handles switching between host and client modes automatically and manages all underlying components.

For most use cases, you only need to interact with `NetworkManager` and its callback interface.