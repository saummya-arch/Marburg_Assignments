package stocks;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StockEntry {
    private final long id;
    private final String name;
    private final long ts;
    private final double value;

    public StockEntry(int id, String name, int timestamp, double market_value) {
        this.id = id;
        this.name = name;
        this.ts = timestamp;
        this.value = market_value;
    }

    public StockEntry(ByteBuffer bb) {
        // TODO
        if (bb.remaining() < Long.BYTES + Short.BYTES) {
            throw  new IllegalArgumentException("Buffer has no data for stock entry");
        }

        this.id = bb.getLong();
        if (bb.remaining() <  Short.BYTES) {
            throw new IllegalArgumentException("Buffer does not contain enough data for name length");
        }

        // Read name length
        int nameLength = bb.getShort();
        if (bb.remaining() < nameLength + Long.BYTES + Double.BYTES) {
            throw new IllegalArgumentException("Buffer does not contain enough data for name, time, and market value");
        }


        byte[] nameBytes = new byte[nameLength];
        bb.get(nameBytes);
        this.name = new String(nameBytes, StandardCharsets.UTF_8);

        this.ts = bb.getLong();
        this.value = bb.getDouble();
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public long getTimeStamp() {
        return this.ts;
    }

    public double getMarketValue() {
        return this.value;
    }

    public int getSerializedLength() {
        return 3 * 8 + 2 + name.getBytes(StandardCharsets.UTF_8).length + Long.BYTES + Double.BYTES;
    }

    @Override
    public String toString() {
        return id + " " + name + " " + ts + " " + value;
    }

    public ByteBuffer getBytes() {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        ByteBuffer bb = ByteBuffer.allocate(getSerializedLength());

        bb.putLong(id);
        bb.putShort((short) nameBytes.length);
        bb.put(nameBytes);
        bb.putLong(ts);
        bb.putDouble(value);
        
        bb.flip(); // Prepare for reading
        return bb;
    }

    public boolean equals(Object obj) {
        if (obj instanceof StockEntry) {
            StockEntry entry = (StockEntry) obj;
            return id == entry.id && name.equals(entry.name) && ts == entry.ts && value == entry.value;
        }
        return false;
    }
}
