package ru.spbau.mit.bittorrent.client.api;

public interface Client {
    void start();
    void stop();
    void download(String pathToMetaFile);
    void upload(String pathToFile);
}
