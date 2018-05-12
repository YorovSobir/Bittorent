package ru.spbau.mit.bittorrent.client.message;

public class Piece implements Message {
    private int index;
    private int begin;
    private byte[] block;

    public Piece(int index, int begin, byte[] block) {
        this.index = index;
        this.begin = begin;
        this.block = block;
    }


    @Override
    public int getMessageLength() {
        return 9 + block.length;
    }

    @Override
    public byte getMessageId() {
        return 7;
    }

    public int getIndex() {
        return index;
    }

    public int getBegin() {
        return begin;
    }

    public byte[] getBlock() {
        return block;
    }
}
