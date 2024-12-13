package exercise4.indexing.utils;


import exercise4.indexing.primary.PrimaryIndex;
import exercise4.indexing.secondary.SecondaryIndex;

public class Table {
    private final Schema schema;
    private final PrimaryIndex primaryIndex;
    private final SecondaryIndex[] secondaryIndexes;

    public Table(Schema schema, PrimaryIndex primaryIndex) {
        this.schema = schema;
        this.primaryIndex = primaryIndex;
        this.secondaryIndexes = new SecondaryIndex[schema.columnCount()];
    }

    /**
     * Set the secondary index for the respective column, replacing any existing index.
     * Rows already in the table must be added to the index.
     */
    public void setSecondaryIndex(int columnIndex, SecondaryIndex index) {
        this.primaryIndex.scan().forEach(entry -> index
                .insert(entry.getValue().getColumn(columnIndex), entry.getKey()));
        this.secondaryIndexes[columnIndex] = index;
    }

    /**
     * Insert a row into the table by assigning a new TID and adding it to all primary/secondary indexes.
     */
    public void insert(Row row) {
        // TODO
        //throw new UnsupportedOperationException("Not supported yet.");
        long tid = primaryIndex.insert(row);
        for (SecondaryIndex index : secondaryIndexes) {
            index.insert(row.getValueAt(index.getColumnIndex()), tid);
        }
    }

    public boolean remove(ResultSet resultSet) {
        // TODO
        //throw new UnsupportedOperationException("Not supported yet.");
        boolean success = true;
        for (Row row : set.getRows()) {
            long tid = row.getTid();
            for (SecondaryIndex index : secondaryIndexes) {
                success &= index.remove(tid, row.getValueAt(index.getColumnIndex()));
            }
            success &= primaryIndex.remove(tid) != null;
        }
        return success;
    }

    /**
     * Perform a point query on the table. Returns the ResultSet of all rows with the respective column value at the given column.
     */
    public ResultSet pointQueryAtColumn(int columnIndex, Object value) {
        // TODO
        //throw new UnsupportedOperationException("Not supported yet.");
        List<SecondaryIndex> relevantIndexes = getSecondaryIndexesForColumn(columnIndex);
        if (!relevantIndexes.isEmpty()) {
            List<Long> tids = relevantIndexes.get(0).get(value);
            return new ResultSet(tids.stream().map(primaryIndex::get).toList());
        } else {
            return new ResultSet(primaryIndex.scan()
                    .filter(row -> row.getValueAt(columnIndex).equals(value))
                    .map(Row::getTid)
                    .toList());
        }
    }

    /**
     * Perform a range query on the table. Returns the ResultSet of all rows with column value at columnIndex in the interval [from, to).
     */
    public ResultSet rangeQueryAtColumn(int columnIndex, Object from, Object to) {
        // TODO
        //throw new UnsupportedOperationException("Not supported yet.");

        List<SecondaryIndex> relevantIndexes = getSecondaryIndexesForColumn(columnIndex);
        if (!relevantIndexes.isEmpty() && relevantIndexes.get(0) instanceof SecondaryTreeIndex) {
            SecondaryTreeIndex index = (SecondaryTreeIndex) relevantIndexes.get(0);
            List<Long> tids = index.getInRange(from, to);
            return new ResultSet(tids.stream().map(primaryIndex::get).toList());
        } else {
            return new ResultSet(primaryIndex.scan()
                    .filter(row -> {
                        Object value = row.getValueAt(columnIndex);
                        return value.compareTo(from) >= 0 && value.compareTo(to) <= 0;
                    })
                    .map(Row::getTid)
                    .toList());
        }
    }
}