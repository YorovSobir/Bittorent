package ru.spbau.mit.bittorrent.metainfo;

public interface Info {
    int getPieceLength();

    String getPieces();

    String getString();

    long getPieceCount();

}
