package ru.spbau.mit.bittorrent.client;

import ru.spbau.mit.bittorrent.client.api.Client;
import ru.spbau.mit.bittorrent.client.message.Message;
import ru.spbau.mit.bittorrent.config.ClientConfig;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientImpl implements Client, Runnable {
    private int port;
    private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(ClientConfig.THREADS_COUNT);

    public ClientImpl(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        Runnable serverRun = () -> {
            try (ServerSocket server = new ServerSocket(port)) {
                serverSocket = server;
                while (true) {
                    executorService.submit(new ClientSeeder(serverSocket.accept()));
                }
            } catch (SocketException e) {
                // socket closed
            } catch (IOException e) {
                throw new IllegalStateException("Could not listen on port: " + port, e);
            }
        };
        executorService.submit(serverRun);
    }

    private final class ClientPeer implements Runnable {

        @Override
        public void run() {

        }
    }

    private final class ClientSeeder implements Runnable {
        private Socket socket;

        public ClientSeeder(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                while (true) {
                    Message message = (Message) in.readObject();
                }
            } catch (IOException e) {
                throw new IllegalStateException("cannot open client socket's stream", e);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public void start() {
        run();
    }

    @Override
    public void stop() {

    }
}
