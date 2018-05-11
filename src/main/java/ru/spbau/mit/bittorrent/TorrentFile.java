package ru.spbau.mit.bittorrent;

import java.util.Map;

public class TorrentFile {

    private Map<String, Object> filesInfo;
    private final BEncoder bEncoder = new BEncoder();

    public TorrentFile(Map<String, Object> filesInfo) {
        this.filesInfo = filesInfo;
        bEncoder.write(this.filesInfo);
    }

    public long getLength() {
        return (long) filesInfo.get("length");
    }

    public String getMD5Sum() {
        return (String) filesInfo.get("md5sum");
    }

    public String getPath() {
        return (String) filesInfo.get("path");
    }
}
