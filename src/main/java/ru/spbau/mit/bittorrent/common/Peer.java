package ru.spbau.mit.bittorrent.common;

public final class Peer {
    public String getPeerId() {
        return peerId;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    private String peerId;
    private String ip;
    private int port;

    public Peer(String peerId, String ip, int port) {
        this.peerId = peerId;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public int hashCode() {
        return peerId.hashCode() ^ ip.hashCode() ^ port;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Peer) {
            Peer otherPeer = (Peer) other;
            return peerId.equals(otherPeer.peerId) && ip.equals(otherPeer.ip) && port == otherPeer.port;
        }
        return false;
    }
}
