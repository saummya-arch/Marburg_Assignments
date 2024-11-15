package buffer;

import java.util.ArrayDeque;

public class SimpleTwoQueueBuffer extends PageFaultRateBuffer {

    private final ArrayDeque<Slot> a1 = new ArrayDeque<>();
    private final ArrayDeque<Slot> am = new ArrayDeque<>();
    private int kin;

    public SimpleTwoQueueBuffer(int capacity) {
        super(capacity);
        this.kin = capacity / 4; //25 % of total capacity
        // TODO
    }

    @Override
    protected Buffer.Slot fix(char c) throws IllegalStateException {
        // TODO
        //return null;
        sCount++;
        Slot slot = lookUp(c);

        if (slot == null) { // Page fault occurs
            fsCount++;

            // Check if FIFO queue (a1) has space, else evict oldest
            if (a1.size() >= kin) {
                Slot victim = a1.poll(); // Remove oldest page from a1
                if (victim != null) victim.remove();
            }

            slot = newSlot(size()); // Create new slot for page
            slot.insert(c);
            a1.offer(slot); // Add to FIFO queue
        } else if (a1.contains(slot)) { // Move from a1 to am
            a1.remove(slot);
            am.offer(slot);
        } else {
            // Update position in LRU (am)
            am.remove(slot);
            am.offer(slot);
        }

        slot.fix();
        return slot;
    }

    protected Slot victim() {
        // TODO
        return !a1.isEmpty() ? a1.poll() : am.poll();
        //return null;
    }

}
