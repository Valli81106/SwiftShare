package com.swiftshare.gui.listeners;

import com.swiftshare.models.TransferStatus;

public interface FileTransferListener {
    void onTransferStarted(TransferStatus status);
    void onTransferProgress(TransferStatus status);
    void onTransferCompleted(TransferStatus status);
    void onTransferFailed(TransferStatus status);
    void onTransferCancelled(TransferStatus status);
}