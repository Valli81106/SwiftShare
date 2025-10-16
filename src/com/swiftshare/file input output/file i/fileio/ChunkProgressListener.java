package com.swiftshare.file input output.file i.o;

/**
 * Interface for reporting progress to GUI
 */
public interface ChunkProgressListener {
    void onChunkProcessed(int chunkNumber, int totalChunks);
}