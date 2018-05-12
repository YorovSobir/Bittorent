package ru.spbau.mit.bittorrent.client.message;

public class Port implements Message {
    private int listenPort;

    public Port(int listenPort) {
        this.listenPort = listenPort;
    }

    @Override
    public int getMessageLength() {
        return 3;
    }

    @Override
    public byte getMessageId() {
        return 9;
    }

    public int getListenPort() {
        return listenPort;
    }
}
