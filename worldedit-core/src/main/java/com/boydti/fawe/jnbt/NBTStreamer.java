package com.boydti.fawe.jnbt;

import com.boydti.fawe.FaweCache;
import com.boydti.fawe.config.BBC;
import com.boydti.fawe.object.exception.FaweException;

import com.sk89q.jnbt.NBTInputStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class NBTStreamer {
    private final NBTInputStream is;
    private final HashMap<String, BiConsumer> readers;

    public NBTStreamer(NBTInputStream stream) {
        this.is = stream;
        readers = new HashMap<>();
    }

    /**
     * Reads the entire stream and runs the applicable readers
     *
     * @throws IOException
     */
    public void readFully() throws IOException {
        is.readNamedTagLazy(readers::get);
        is.close();
    }

    /**
     * Reads the stream until all readers have been used<br>
     * - Use readFully if you expect a reader to appear more than once
     * - Can exit early without having reading the entire file
     *
     * @throws IOException
     */
    public void readQuick() throws IOException {
        try {
            is.readNamedTagLazy(node -> {
                if (readers.isEmpty()) {
                    throw FaweCache.MANUAL;
                }
                return readers.remove(node);
            });
        } catch (FaweException ignore) {}
        is.close();
    }

    public <T, V> void addReader(String node, BiConsumer<T, V> run) {
        if (run instanceof NBTStreamReader) {
            ((NBTStreamReader) run).init(node);
        }
        readers.put(node, run);
    }

    public static abstract class NBTStreamReader<T, V> implements BiConsumer<T, V> {
        private String node;

        public void init(String node) {
            this.node = node;
        }

        public String getNode() {
            return node;
        }
    }

    public static abstract class ByteReader implements BiConsumer<Integer, Integer> {
        @Override
        public void accept(Integer index, Integer value) {
            run(index, value);
        }

        public abstract void run(int index, int byteValue);
    }

    public interface LazyReader extends BiConsumer<Integer, DataInputStream> {}

    public static abstract class LongReader implements BiConsumer<Integer, Long> {
        @Override
        public void accept(Integer index, Long value) {
            run(index, value);
        }

        public abstract void run(int index, long byteValue);
    }
}
