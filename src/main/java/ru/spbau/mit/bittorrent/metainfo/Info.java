package ru.spbau.mit.bittorrent.metainfo;

public interface Info {
    long getPieceLength();

    String getPieces();
}
