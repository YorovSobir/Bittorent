package ru.spbau.mit.bittorrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultipleInfo implements Info {
    private Map<String, Object> multipleInfo;
    private final BEncoder bEncoder = new BEncoder();

    public MultipleInfo(Map<String, Object> info) {
        multipleInfo = info;
        bEncoder.write(info);
    }

    @Override
    public long getPieceLength() {
        return (long) multipleInfo.get("piece length");
    }

    @Override
    public String getPieces() {
        return (String) multipleInfo.get("pieces");
    }

    public String getName() {
        return (String) multipleInfo.get("name");
    }

    public List<TorrentFile> getFiles() {
        ArrayList<Map<String, Object>> out = (ArrayList<Map<String, Object>>) multipleInfo.get("files");
        List<TorrentFile> result = new ArrayList<>();
        for(Map<String, Object> i : out) {
            result.add(new TorrentFile(i));
        }
        return result;
    }
}
