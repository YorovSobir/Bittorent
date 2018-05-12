package ru.spbau.mit.bittorrent.client.message;

public class Piece implements Message {
    private int index;
    private int begin;
    private int block;

    public Piece(int index, int begin, int block) {
        this.index = index;
        this.begin = begin;
        this.block = block;
    }


    @Override
    public int getMessageLength() {
        return 0;
    }

    @Override
    public byte getMessageId() {
        return 0;
    }

    public int getIndex() {
        return index;
    }

    public int getBegin() {
        return begin;
    }

    public int getBlock() {
        return block;
    }
}
