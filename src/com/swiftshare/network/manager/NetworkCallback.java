package com.swiftshare.network.manager;

import com.swiftshare.models.FileMetadata;
import com.swiftshare.models.PeerInfo;

// callback interface for GUI to get network events
public interface NetworkCallback {
    void onRoomCreated(int port);
    void onRoomJoined(String host, int port);
    void onPeerConnected(PeerInfo peer);
    void onPeerDisconnected(PeerInfo peer);
    void onFileOfferReceived(FileMetadata metadata);
    void onTransferProgress(String fileName, double progress, String speed);
    void onTransferComplete(String fileName);
    void onError(String error);
    void onConnectionLost();
}