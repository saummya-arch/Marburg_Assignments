package exercise4.cola;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;

import xxl.core.collections.containers.io.BlockFileContainer;
import xxl.core.cursors.sources.io.FileInputCursor;
import xxl.core.io.converters.Converter;
import xxl.core.io.converters.DoubleConverter;
import xxl.core.io.converters.LongConverter;
import xxl.core.util.Pair;

public class Main {
    public static void main(String[] args) {

        BlockFileContainer raw = new BlockFileContainer("COLA", BasicCOLA.DISK_BLOCK_SIZE);

        BasicCOLA<Long, Double> cola = new BasicCOLA<>(
                LongConverter.SIZE + DoubleConverter.SIZE,
                LongConverter.DEFAULT_INSTANCE,
                DoubleConverter.DEFAULT_INSTANCE,
                raw);

        FileInputCursor<Pair<Long, Double>> input = new FileInputCursor<>(new Converter<>() {
            @Override
            public Pair<Long, Double> read(DataInput dataInput,
                                           Pair<Long, Double> object) throws IOException {
                return new Pair<>(dataInput.readLong(), dataInput.readDouble());
            }

            @Override
            public void write(DataOutput dataOutput, Pair<Long, Double> object)
                    throws IOException {
                dataOutput.writeLong(object.getFirst());
                dataOutput.writeDouble(object.getSecond());
            }
        }, new File("timeseries.bin"));

        input.open();
        for (int i = 0; input.hasNext(); i++) {
            Pair<Long, Double> p = input.next();
            cola.insertElement(p.getFirst(), p.getSecond());
            if (i < 10) {
                System.out.println("Inserting: " + p);
                cola.printLevels();
            }
        }
        input.close();

        // Search for the first and last inserted elements
        long key = 1325458800000L;
        System.out.println("Searching key: " + key + ", result: " + cola.searchElement(key));
        key = 1262127600000L;
        System.out.println("Searching key: " + key + ", result: " + cola.searchElement(key));

        // TODO: benchmark the search for the first and the last inserted keys
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            cola.searchElement(1325458800000L);
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // in milliseconds
        System.out.println("Time to search for first key: " + duration + "ms");

        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            cola.searchElement(1262127600000L);
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000; // in milliseconds
        System.out.println("Time to search for last key: " + duration + "ms");

        cola.close();
    }
}
