package ru.spbau.mit.bittorrent.client.message;

public class Choke implements Message {
    @Override
    public int getMessageLength() {
        return 1;
    }

    @Override
    public byte getMessageId() {
        return 0;
    }
}
