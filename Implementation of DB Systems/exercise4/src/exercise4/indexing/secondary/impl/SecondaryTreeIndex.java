package exercise4.indexing.secondary.impl;


import exercise4.indexing.secondary.SecondaryIndex;

import java.util.*;

/**
 * An implementation of a SecondaryIndex around a TreeMap.
 */
public class SecondaryTreeIndex implements SecondaryIndex {
    // TODO: add field(s) as necessary
    private TreeMap<Object, List<Long>> index;

    public SecondaryTreeIndex(Comparator<Object> cmp) {
        // TODO
        //throw new UnsupportedOperationException("Not supported yet.");
        this.index = new TreeMap<>(cmp);
    }

    @Override
    public void insert(Object value, Long tid) {
        // TODO
        //throw new UnsupportedOperationException("Not supported yet.");
        if (!index.containsKey(value)) {
            index.put(value, new ArrayList<>());
        }
        index.get(value).add(tid);
    }

    @Override
    public List<Long> get(Object value) {
        // TODO
        //throw new UnsupportedOperationException("Not supported yet.");
        return index.getOrDefault(value, new ArrayList<>());
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

    @Override
    public List<Long> getRange(Object from, Object to) {
        // TODO
        //throw new UnsupportedOperationException("Not supported yet.");

        List<Long> result = new ArrayList<>();
        for (Map.Entry<Object, List<Long>> entry : index.subMap(from, true, to, true).entrySet()) {
            result.addAll(entry.getValue());
        }
        return result;
    }
}
