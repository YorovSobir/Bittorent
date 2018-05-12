package ru.spbau.mit.bittorrent.metainfo;

import java.util.Map;

public class SingleInfo implements Info {
    private Map<String, Object> singleInfo;
    private final BEncoder bEncoder = new BEncoder();

    public SingleInfo(Map<String, Object> info) {
        singleInfo = info;
        bEncoder.write(info);
    }

    @Override
    public long getPieceLength() {
        return (long) singleInfo.get("piece length");
    }

    @Override
    public String getPieces() {
        return (String) singleInfo.get("pieces");
    }

    public String getName() {
        return (String) singleInfo.get("name");
    }

    public long getLength() {
        return (long) singleInfo.get("length");
    }

    @Override
    public String toString() {
        return bEncoder.toString();
    }

    @Override
    public long getPieceCount() {
        return (getLength() / getPieceLength());
    }
}
