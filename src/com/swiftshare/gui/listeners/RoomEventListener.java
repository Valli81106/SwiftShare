package com.swiftshare.gui.listeners;

import com.swiftshare.models.PeerInfo;
import com.swiftshare.models.FileMetadata;
import com.swiftshare.models.TransferStatus;

/**
 * Interface for room-related events
 * Implement this in GUI to receive callbacks from networking layer
 */
public interface RoomEventListener {
    /**
     * Called when a new peer joins the room
     */
    void onPeerJoined(PeerInfo peer);
    
    /**
     * Called when a peer leaves the room
     */
    void onPeerLeft(PeerInfo peer);
    
    /**
     * Called when a new file is shared in the room
     */
    void onFileReceived(FileMetadata file);
    
    /**
     * Called when file transfer progress updates
     */
    void onTransferProgress(TransferStatus status);
    
    /**
     * Called when the room is closed or expires
     */
    void onRoomClosed(String reason);
    
    /**
     * Called when an error occurs
     */
    void onError(String errorMessage);
}