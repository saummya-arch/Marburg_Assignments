package stocks;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class StockEntryIterator implements Iterator<StockEntry> {

    private long pos;
    private final RandomAccessFile file;
    private boolean hasNextEntry;

    public StockEntryIterator(RandomAccessFile file) {
        this.file = file;
        this.pos = 0; // Start at the beginning of the file
        this.hasNextEntry = checkHasNext();
    }


    private boolean checkHasNext() {
        try {
            long currentPos = file.getFilePointer(); // Save current position
            if (file.readLong() != -1) { // Attempt to read the id
                file.seek(currentPos); // Restore position
                return true;
            }
        } catch (IOException e) {
            // Handle EOF or read error
        }
        return false;
    }

    
    @Override
    public boolean hasNext() {
        return hasNextEntry;
    }


    @Override
    public StockEntry next() {
        try {
            if (!hasNextEntry) {
                throw new NoSuchElementException("No more StockEntries available.");
            }

            // Read the entry from the file
            long id = file.readLong(); // Read ID
            short nameLength = file.readShort(); // Read name length
            byte[] nameBytes = new byte[nameLength]; // Create byte array for name
            file.readFully(nameBytes); // Read name bytes
            String name = new String(nameBytes, StandardCharsets.UTF_8); // Convert to String
            long timestamp = file.readLong(); // Read timestamp
            double marketValue = file.readDouble(); // Read market value

            // Create the StockEntry object
            StockEntry entry = new StockEntry((int) id, name, (int) timestamp, marketValue);

            // Update the position
            pos = file.getFilePointer();

            // Check if there's another entry
            hasNextEntry = checkHasNext();

            return entry;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read StockEntry", e);
        }
    }
}
