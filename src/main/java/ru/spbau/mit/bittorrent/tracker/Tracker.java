package ru.spbau.mit.bittorrent.tracker;

import ru.spbau.mit.bittorrent.common.Peer;
import ru.spbau.mit.bittorrent.common.TrackerRequest;
import ru.spbau.mit.bittorrent.common.TrackerResponse;
import ru.spbau.mit.bittorrent.config.TrackerConfig;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Tracker implements Runnable {
    private int port;
    private ExecutorService executorService;
    private Map<String, TrackerResponse> trackerResponseMap = new HashMap<>();

    public Tracker(int port) {
        this.port = port;
        executorService = Executors.newFixedThreadPool(TrackerConfig.THREADS_COUNT);
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                executorService.submit(new ClientHandler(server.accept()));
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private final class ClientHandler implements Runnable {

        private Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                 DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
                while (true) {
                    String stringTrackerRequest = dataInputStream.readUTF();
                    String response = response(stringTrackerRequest, socket.getInetAddress().getHostName());
                    System.out.println("Write");
                    dataOutputStream.writeUTF(response);
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        private String response(String stringTrackerRequest, String ip) {
            TrackerRequest request = TrackerRequest.parse(stringTrackerRequest);
            TrackerResponse response = trackerResponseMap.get(request.getInfoHash());
            if (response == null) {
                response = TrackerResponse.EMPTY;
            }
            Peer peer = new Peer(request.getPeerId(), ip, request.getPort());
            if (request.getEvent() == TrackerRequest.Event.STOPPED) {
                response.removePeer(peer);
            } else {
                if (request.getLeft() < 100) {
                    response.addPeer(peer);
                }
            }
            response.setInterval(2);
            return response.getString();
        }
    }
}
