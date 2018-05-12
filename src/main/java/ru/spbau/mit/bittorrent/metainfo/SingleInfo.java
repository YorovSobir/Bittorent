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
    public int getPieceLength() {
        return (int) singleInfo.get("piece length");
    }

    @Override
    public String getPieces() {
        return (String) singleInfo.get("pieces");
    }

    @Override
    public String getString() {
        return bEncoder.toString();
    }

    public String getName() {
        return (String) singleInfo.get("name");
    }

    public int getLength() {
        return (int) singleInfo.get("length");
    }

//    @Override
//    public String toString() {
//        return bEncoder.toString();
//    }
}
