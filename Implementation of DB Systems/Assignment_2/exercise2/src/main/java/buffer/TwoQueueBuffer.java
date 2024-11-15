package buffer;

import java.util.ArrayDeque;

public class TwoQueueBuffer extends PageFaultRateBuffer {

    private final ArrayDeque<Slot> a1in = new ArrayDeque<>();
    private final ArrayDeque<Character> a1out = new ArrayDeque<>();
    private final ArrayDeque<Slot> am = new ArrayDeque<>();
    private final int kin;
    private final int kout;

    public TwoQueueBuffer(int capacity) {
        super(capacity);
        // TODO
        //kin = kout = 0;
        this.kin = capacity / 4; // Set size of A1_in
        this.kout = capacity / 2; // Set size of A1_out
    }

    @Override
    protected Slot fix(char c) throws IllegalStateException {
        // TODO
        //return null;
        sCount++;
        Slot slot = lookUp(c);

        if (slot == null) { // Page fault
            fsCount++;

            // Check if a1in has capacity, if not evict to a1out
            if (a1in.size() >= kin) {
                Slot old = a1in.poll();
                if (old != null) {
                    a1out.offer(old.c);
                    old.remove();
                    if (a1out.size() > kout) {
                        a1out.poll();
                    }
                }
            }

            slot = newSlot(size());
            slot.insert(c);
            a1in.offer(slot);
        } else if (a1in.contains(slot)) { // Move to am if in a1in
            a1in.remove(slot);
            am.offer(slot);
        } else {
            // Refresh position in am (move to end of LRU queue)
            am.remove(slot);
            am.offer(slot);
        }

        slot.fix();
        return slot;
    }

    protected Slot victim() {
        // TODO
        //return null;
        if (!a1in.isEmpty()) {
            return a1in.poll(); // Remove from A1_in
        } else {
            return am.poll(); // Remove from AM
        }
    }
}
