package ru.spbau.mit.bittorrent.client.message;

public class Request implements Message {
    private int index;
    private int begin;

    private int length;

    public Request(int index, int begin, int length) {
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

    public int getLength() {
        return length;
    }

    public int getIndex() {
        return index;
    }

    public int getBegin() {
        return begin;
    }
}
