package ru.spbau.mit.bittorrent.client;

import ru.spbau.mit.bittorrent.client.api.Client;
import ru.spbau.mit.bittorrent.client.message.Message;
import ru.spbau.mit.bittorrent.common.Status;
import ru.spbau.mit.bittorrent.common.TrackerRequest;
import ru.spbau.mit.bittorrent.common.TrackerResponse;
import ru.spbau.mit.bittorrent.config.ClientConfig;
import ru.spbau.mit.bittorrent.metainfo.MetaInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class ClientImpl implements Client, Runnable {
    private int port;
    private String ip;
    private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(ClientConfig.THREADS_COUNT);
    private Map<MetaInfo, Status> currentStatus = new HashMap<>();

    public ClientImpl(String ip, int port) {
        this.port = port;
        this.ip = ip;
    }

    public void createUpload(String path,
                             String url,
                             String comment,
                             String author,
                             String encoding,
                             String output) throws IOException, NoSuchAlgorithmException {
        MetaInfo metaInfo = new MetaInfo();
        metaInfo.CreateFile(output, path, url, comment, author, encoding, 76800);
        currentStatus.put(metaInfo, new Status(0, 0, 0, TrackerRequest.Event.COMPLETED));
        executorService.submit(new ClientRequest(metaInfo));
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

    private final class ClientRequest implements Runnable {
        private MetaInfo metaInfo;

        public ClientRequest(MetaInfo metaInfo) {
            this.metaInfo = metaInfo;
        }

        @Override
        public void run() {
            try {
                String[] server = metaInfo.getAnnounce().split(":");
                Socket socket = new Socket(server[0], Integer.valueOf(server[1]));
                TrackerRequest trackerRequest = new TrackerRequest(metaInfo);
                try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
                    while (true) {
                        Status status = currentStatus.get(metaInfo);
                        trackerRequest.setEvent(status.getEvent());
                        trackerRequest.setHttpVersion("1.1");
                        trackerRequest.setKey("hello");
                        trackerRequest.setUploaded(status.getUpload());
                        trackerRequest.setPort(port);
                        trackerRequest.setIp(ip);
                        dataOutputStream.writeUTF(trackerRequest.toString());
                        String response = dataInputStream.readUTF();
                        TrackerResponse trackerResponse = new TrackerResponse(response);
                        status.setResponse(trackerResponse);
                        sleep(trackerResponse.getInterval());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
