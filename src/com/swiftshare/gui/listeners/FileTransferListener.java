package com.swiftshare.gui.listeners;

import com.swiftshare.models.TransferStatus;

/**
 * Interface for file transfer events
 */
public interface FileTransferListener {
    /**
     * Called when transfer starts
     */
    void onTransferStarted(String fileId, String fileName);
    
    /**
     * Called when transfer progress updates
     */
    void onProgressUpdate(TransferStatus status);
    
    /**
     * Called when transfer completes
     */
    void onTransferCompleted(String fileId);
    
    /**
     * Called when transfer fails
     */
    void onTransferFailed(String fileId, String reason);
    
    /**
     * Called when transfer is cancelled
     */
    void onTransferCancelled(String fileId);
}