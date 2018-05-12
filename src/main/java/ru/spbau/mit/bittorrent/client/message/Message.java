package ru.spbau.mit.bittorrent.client.message;

public interface Message {
    int getMessageLength();
    byte getMessageId();
}
