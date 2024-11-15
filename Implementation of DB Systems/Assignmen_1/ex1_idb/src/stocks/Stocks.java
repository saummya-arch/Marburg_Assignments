package stocks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.*;

public class Stocks implements Iterable<StockEntry> {

    private final RandomAccessFile file;

    Stocks(String path) throws FileNotFoundException {
        this.file = new RandomAccessFile(new File(path), "r");
    }

    public StockEntry get(int i) {
        try {
            // Move the file pointer to the correct position based on the index
            file.seek(i * new StockEntry(0, "", 0, 0).getSerializedLength());

            ByteBuffer bb = ByteBuffer.allocate(new StockEntry(0, "", 0, 0).getSerializedLength());
            file.readFully(bb.array()); // Read the bytes into the ByteBuffer
            bb.flip();

            return new StockEntry(bb);
        } catch (IOException e) {
            throw new RuntimeException("Error reading stock entry at index: " + i, e);
        }
    }

    @Override
    public Iterator<StockEntry> iterator() {
        return new StockEntryIterator(file);
    }
}