package ru.spbau.mit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TorrentManager {
    private Map<String, List<FileManager>> files = new HashMap<>();

    public TorrentManager() {

    }

    public void addFile(String current, FileManager file) {
        if (files.containsKey(current)) {
            files.get(current).add(file);
        } else {
            files.put(current, new ArrayList<>());
            files.get(current).add(file);
        }
    }

    public byte[] getBlock(String id, int blockNumber) {
        List<FileManager> fileManagers = files.get(id);
        int count = 0;
        for (FileManager i : fileManagers) {
            count += i.getTotalCountBlocks();
            if (count >= blockNumber) {
                return i.read(count - blockNumber);
            }
        }
        return null;
    }

    public void setBlock(String id, int blockNumber, byte[] data) {
        List<FileManager> fileManagers = files.get(id);
        int count = 0;
        for (FileManager i : fileManagers) {
            count += i.getTotalCountBlocks();
            if (count >= blockNumber) {
                i.write(count - blockNumber, data);
            }
        }
    }
}
