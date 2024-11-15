package buffer;

public abstract class LRUBuffer extends Buffer {
    Slot first = null;
    Slot last = null;

    class Slot extends Buffer.Slot {
        Slot prev = this;
        Slot next = this;

        public Slot(int index) {
            super(index);
        }

        void unlink() {
            if (prev != null)
                prev.next = next;
            else
                first = next;
            if (next != null)
                next.prev = prev;
            else
                last = prev;
            prev = next = this;
        }

        void fix() {
            super.fix();
            unlink();
        }

        void unfix() {
            super.unfix();
            unlink();
            next = first;
            first = this;
            prev = null;
            if (next != null)
                next.prev = this;
            else
                last = this;
        }

        void remove() {
            super.remove();
            unlink();
        }
    }

    public LRUBuffer(int capacity) {
        super(capacity);
    }

    Buffer.Slot newSlot(int index) {

        return new Slot(index);
    }


    Buffer.Slot victim() {

        if (last == null) {
            throw new IllegalStateException("No available slot for replacement.");
        }
        Slot victimSlot = last; // Pick the least recently used slot
        return victimSlot;
    }

}
