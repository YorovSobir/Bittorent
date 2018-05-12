package ru.spbau.mit.bittorrent.client.message;

public class Have implements Message {
    private int pieceIndex;

    public Have(int pieceIndex) {
        this.pieceIndex = pieceIndex;
    }

    @Override
    public int getMessageLength() {
        return 5;
    }

    @Override
    public byte getMessageId() {
        return 4;
    }

    public void setPieceIndex(int pieceIndex) {
        this.pieceIndex = pieceIndex;
    }

    public int getPieceIndex() {
        return pieceIndex;
    }
}
