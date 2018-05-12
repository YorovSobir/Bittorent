package ru.spbau.mit.bittorrent.client.message;

public class Request implements Message {
    private int index;
    private int begin;
    private long length;

    public Request(int index, int begin, long length) {
        this.index = index;
        this.begin = begin;
        this.length = length;
    }

    @Override
    public int getMessageLength() {
        return 13;
    }

    @Override
    public byte getMessageId() {
        return 6;
    }

    public long getLength() {
        return length;
    }

    public int getIndex() {
        return index;
    }

    public int getBegin() {
        return begin;
    }
}
