package com.swiftshare.gui.listeners;

import com.swiftshare.models.RoomInfo;
import com.swiftshare.models.PeerInfo;
import com.swiftshare.models.FileMetadata;

public interface RoomEventListener {
    void onRoomCreated(RoomInfo roomInfo);
    void onRoomJoined(RoomInfo roomInfo);
    void onRoomLeft();
    void onPeerJoined(PeerInfo peerInfo);
    void onPeerLeft(PeerInfo peerInfo);
    void onFileAdded(FileMetadata fileMetadata);
    void onFileRemoved(FileMetadata fileMetadata);
    void onError(String errorMessage);
}