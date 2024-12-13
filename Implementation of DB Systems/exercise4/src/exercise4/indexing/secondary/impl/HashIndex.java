package exercise4.indexing.secondary.impl;


import exercise4.indexing.secondary.SecondaryIndex;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

/**
 * An implementation of a SecondaryIndex around a HashMap.
 */
public class HashIndex implements SecondaryIndex {
    // TODO: add field(s) as necessary
    private HashMap<Object, List<Long>> index;

    public HashIndex() {
        // TODO
        //throw new UnsupportedOperationException("Not supported yet.");
        this.index = new HashMap<>();
    }

    @Override
    public void insert(Object value, Long tid) {
        // TODO
        //throw new UnsupportedOperationException("Not supported yet.");
        //return index.getOrDefault(value, new ArrayList<>());
        if (!index.containsKey(value)) {
            index.put(value, new ArrayList<>());
        }
        index.get(value).add(tid);
    }

    @Override
    public List<Long> get(Object value) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(Long tid, Object value) {
        // TODO: make sure to only delete the given row/tid
        //throw new UnsupportedOperationException("Not supported yet.");
        if (index.containsKey(value)) {
            List<Long> tids = index.get(value);
            tids.remove(tid);
            if (tids.isEmpty()) {
                index.remove(value);
            }
            return true;
        }
        return false;
    }
}