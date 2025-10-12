package fileio;

/**
 * Interface for reporting progress to GUI
 */
public interface ChunkProgressListener {
    void onChunkProcessed(int chunkNumber, int totalChunks);
}