package ru.spbau.mit.bittorrent.client;

import java.util.Arrays;

public class Handshake {
    private static final int RESERVED_BYTES_COUNT = 8;
    private String pstr;
    private byte[] reserved = new byte[RESERVED_BYTES_COUNT];
    private String infoHash;
    private String peerId;

    public Handshake(String pstr, String infoHash, String peerId) {
        this.pstr = pstr;
        this.infoHash = infoHash;
        this.peerId = peerId;
    }

    @Override
    public String toString() {
        return String.valueOf(pstr.length()) + pstr + Arrays.toString(reserved) + infoHash + peerId;
    }
}
