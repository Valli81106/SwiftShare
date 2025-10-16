package com.swiftshare.gui.listeners;

/**
 * Interface for network connection events
 */
public interface NetworkEventListener {
    /**
     * Called when connected to network
     */
    void onConnected();
    
    /**
     * Called when disconnected from network
     */
    void onDisconnected();
    
    /**
     * Called when connection fails
     */
    void onConnectionFailed(String reason);
    
    /**
     * Called when network error occurs
     */
    void onNetworkError(String errorMessage);
}