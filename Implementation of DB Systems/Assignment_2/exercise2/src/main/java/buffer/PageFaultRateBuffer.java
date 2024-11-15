package buffer;

public abstract class PageFaultRateBuffer extends Buffer {
    protected int fsCount = 0;
    protected int sCount = 0;
    protected abstract Buffer.Slot victim();

    public PageFaultRateBuffer(int capacity) {
        super(capacity);
    }

    public double getFSR() {
        // TODO
        return sCount == 0 ? 0 : (double) fsCount / sCount;
        //return 0.0;
    }

    @Override
    protected Slot fix(char c) throws IllegalStateException {
        // TODO
        sCount++; // Increment total accesses counter
        Slot slot = lookUp(c);

        if (slot == null) {
            fsCount++; // Increment page fault counter if page not found
            slot = victim(); // Get victim page for replacement
            if (slot != null) {
                slot.remove();
            } else {
                // Handle the case where there's no victim (optional: throw exception or log)
                throw new IllegalStateException("No victim available for replacement.");
            }
            slot.insert(c);
            slot.fix();
        } else {
            slot.fix();
        }

        return slot;

        //return null;
    }
}
