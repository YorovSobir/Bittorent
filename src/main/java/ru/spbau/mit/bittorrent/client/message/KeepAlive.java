package ru.spbau.mit.bittorrent.client.message;

public class KeepAlive implements Message {
    @Override
    public int getMessageLength() {
        return 0;
    }

    @Override
    public byte getMessageId() {
        throw new UnsupportedOperationException("keep alive doesn't have message id");
    }
}
