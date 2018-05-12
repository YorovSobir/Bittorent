package ru.spbau.mit.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FileManager implements Serializable {
    private long blockSize;
    private String name;
    private transient RandomAccessFile file;

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    private final class Block implements Serializable {
        private int id;
        private boolean status;

        public Block(int id, boolean status) {
            this.id = id;
            this.status = status;
        }

        public int getId() {
            return id;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public byte[] getData() {
            if (status) {
                try {
                    byte[] out = new byte[(int) blockSize];
                    file.seek(id * blockSize);
                    int count = file.read(out);
                    if (count < out.length) {
                        out = Arrays.copyOf(out, count);
                    }
                    return out;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public void setData(byte[] data) {
            try {
                file.seek(id * blockSize);
                file.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String path;
    private long size;
    private List<Block> blocks = new CopyOnWriteArrayList<>();

    public FileManager(String path, long blockSize) throws FileNotFoundException {
        this.path = path;
        this.blockSize = blockSize;
        File file = new File(path);
        name = file.getName();
        if (file.exists() && file.isFile() && file.canRead()) {
            this.file = new RandomAccessFile(file, "rw");
            long countBlock = (file.length() + blockSize - 1) / blockSize;
            for (int i = 0; i < countBlock; i++) {
                blocks.add(new Block(i, true));
            }
        } else {
            throw new IllegalStateException();
        }
    }

    public FileManager(String path, long size, long blockSize) throws IOException {
        this.size = size;
        this.path = path;
        this.blockSize = blockSize;
        File file = new File(path);
        name = file.getName();
        this.file = new RandomAccessFile(file, "rw");
        this.file.setLength(size);
        long countBlock = (file.length() + blockSize - 1) / blockSize;
        for (int i = 0; i < countBlock; i++) {
            blocks.add(new Block(i, false));
        }
    }

    public synchronized void write(int id, byte[] data) {
        blocks.get(id).setData(data);
        blocks.get(id).setStatus(true);
    }

    public synchronized byte[] read(int id) {
        return blocks.get(id).getData();
    }

    public List<Integer> getAvailableBlocks() {
        List<Integer> out = new ArrayList<>();
        for (Block block : blocks) {
            if (block.isStatus()) {
                out.add(block.getId());
            }
        }
        return out;
    }

    public List<Integer> getNotAvailableBlocks() {
        List<Integer> out = new ArrayList<>();
        for (Block block : blocks) {
            if (!block.isStatus()) {
                out.add(block.getId());
            }
        }
        return out;
    }

    public boolean isFull() throws IOException {
        for (Block block : blocks) {
            if (!block.isStatus()) {
                return false;
            }
        }
        file.getFD().sync();
        return true;
    }

    public int getTotalCountBlocks() {
        return blocks.size();
    }
//
//    public void load() throws IOException {
//        if (file != null) {
//            file.close();
//        }
//        File file = new File(path);
//        this.file = new RandomAccessFile(file, "rw");
//    }
}