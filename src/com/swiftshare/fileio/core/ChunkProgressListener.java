package com.swiftshare.fileio.core;

/**
 * Interface for reporting progress to GUI
 */
public interface ChunkProgressListener {
    void onChunkProcessed(int chunkNumber, int totalChunks);
}