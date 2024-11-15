package buffer;

public class PageFaultRateLRUBuffer extends LRUBuffer {
    private int fsCount = 0;
    private int sCount = 0;

    public PageFaultRateLRUBuffer(int capacity) {
        super(capacity);
    }

    public double getFSR() {
        // TODO
        return sCount == 0 ? 0 : (double) fsCount / sCount;
        //return 0.0;
    }

    @Override
    protected Buffer.Slot victim() {
        // Ensure there is always a slot available for eviction
        if (slots.isEmpty()) {
            throw new IllegalStateException("No available slot for replacement.");
        }

        // Use the LRU policy to pick the last slot (or adjust as needed for testing)
        Slot victimSlot = last; // Using the LRU logic from LRUBuffer
        if (victimSlot != null) {
            victimSlot.unlink(); // Remove from LRU chain
        }
        return victimSlot;
    }


    @Override
    protected Buffer.Slot fix(char c) throws IllegalStateException {
        // TODO
        sCount++;
        Buffer.Slot slot = lookUp(c);

        if (slot == null) {
            fsCount++;

            if (size() == capacity()) {
                slot = victim(); // Get the victim slot
                if (slot == null) {
                    throw new IllegalStateException("No available slot for replacement.");
                }
                slot.remove(); // Remove the old page
            }

            slot = newSlot(size()); // Create a new slot
            slot.insert(c);
            slots.add(slot); // Add the slot to the buffer
        }

        slot.fix(); // Mark the slot as fixed (recently used)
        return slot;
    }

}
