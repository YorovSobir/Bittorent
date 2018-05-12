package ru.spbau.mit;

import ru.spbau.mit.bittorrent.client.ClientImpl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class MainClient {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        ClientImpl client = new ClientImpl(1235, "hello");
//        client.run();
        client.createUpload("/Users/vadim/testTorrent/SGD.pkl",
                "127.0.0.1:1234",
                "bla-bla",
                "me",
                "utf-8",
                "/Users/vadim/testTorrent/t.mtorrent");
    }
}
