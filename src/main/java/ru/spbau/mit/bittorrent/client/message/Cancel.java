package ru.spbau.mit.bittorrent.client.message;

public class Cancel implements Message {
    private int index;
    private int begin;
    private int length;

    public Cancel(int index, int begin, int length) {
        this.index = index;
        this.begin = begin;
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public int getIndex() {
        return index;
    }

    public int getBegin() {
        return begin;
    }
    @Override
    public int getMessageLength() {
        return 13;
    }

    @Override
    public byte getMessageId() {
        return 8;
    }
}
