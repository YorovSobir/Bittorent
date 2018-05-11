package ru.spbau.mit.bittorrent;

public class Peer {
    private String peerId;
    private String ip;
    private int port;

    public Peer(String peerId, String ip, int port) {
        this.peerId = peerId;
        this.ip = ip;
        this.port = port;
    }
}
