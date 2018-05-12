package ru.spbau.mit.bittorrent.client;

import ru.spbau.mit.bittorrent.client.api.Client;
import ru.spbau.mit.bittorrent.client.message.Message;
import ru.spbau.mit.bittorrent.common.Status;
import ru.spbau.mit.bittorrent.common.TrackerRequest;
import ru.spbau.mit.bittorrent.common.TrackerResponse;
import ru.spbau.mit.bittorrent.config.ClientConfig;
import ru.spbau.mit.bittorrent.metainfo.MetaInfo;
import ru.spbau.mit.bittorrent.client.message.*;
import ru.spbau.mit.util.Hash;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class ClientImpl implements Client, Runnable {
    private int port;
    private Map<MetaInfo, Status> currentStatus = new HashMap<>();

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
    private String peerId;
    private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(ClientConfig.THREADS_COUNT);
    private Map<String, State> states = new ConcurrentHashMap<>();
    private Map<String, List<Integer>> peerToPieces = new ConcurrentHashMap<>();
    private Map<String, String> infoHashToFile = new ConcurrentHashMap<>();

    public ClientImpl(int port, String peerId) {
        this.port = port;
        try {
            this.peerId = Hash.sha1(peerId);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new IllegalArgumentException("failed when getting sha1 of peerId", e);
        }
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
        private String clientPeerId;
        private String infoHash;

        public ClientSeeder(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                socket.setSoTimeout(2 * 60);
                Handshake initiatorHandshake = (Handshake) in.readObject();
                if (!infoHashToFile.containsKey(initiatorHandshake.getInfoHash())) {
                    return;
                }
                clientPeerId = initiatorHandshake.getPeerId();
                infoHash = initiatorHandshake.getInfoHash();
                states.put(clientPeerId, State.INITIAL);
                Handshake handshake = new Handshake(infoHash, peerId);
                out.writeObject(handshake);
                while (true) {
                    Message message = (Message) in.readObject();
                    if (message instanceof KeepAlive) {
                        continue;
                    }
                    Message response;
                    switch (message.getMessageId()) {
                        case 1:
                            response = response((Unchoke) message);
                            break;
                        case 2:
                            response = response((Interested) message);
                            break;
                        case 3:
                            response = response((NotInterested) message);
                            break;
                        case 4:
                            response = response((Have) message);
                            break;
                        case 5:
                            response = response((BitField) message);
                            break;
                        case 6:
                            response = response((Request) message);
                            break;
                        case 7:
                            response = response((Piece) message);
                            break;
                        case 8:
                            response = response((Cancel) message);
                            break;
                        case 9:
                            response = response((Port) message);
                            break;
                        default:
                            throw new IllegalArgumentException("unsupported message id");
                    }
                    if (response != null) {
                        out.writeObject(response);
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException("cannot open client socket's stream", e);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        private Message response(Port message) {
            // TODO it's optional
            return null;
        }

        private Message response(Cancel message) {
            // TODO it's used only during "End Game"
            return null;
        }

        private Message response(Piece message) {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(getPath(infoHash), "w")) {
                randomAccessFile.seek(message.getIndex() * ClientConfig.FILE_PART_SIZE + message.getBegin());
                randomAccessFile.write(message.getBlock());
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("requested file not found", e);
            } catch (IOException e) {
                throw new IllegalStateException("illegal part number", e);
            }
            return null;
        }

        private Message response(Request message) {
            State clientState = states.get(clientPeerId);
            if (clientState.peerInterested && clientState.amChocking) {
                try (RandomAccessFile randomAccessFile = new RandomAccessFile(getPath(infoHash), "r")) {
                    randomAccessFile.seek(message.getIndex() * ClientConfig.FILE_PART_SIZE + message.getBegin());
                    byte[] data = new byte[message.getLength()];
                    randomAccessFile.read(data);
                    return new Piece(message.getIndex(), message.getBegin(), data);
                } catch (FileNotFoundException e) {
                    throw new IllegalStateException("requested file not found", e);
                } catch (IOException e) {
                    throw new IllegalStateException("illegal part number", e);
                }
            }
            return null;
        }

        private String getPath(String infoHash) {
            return infoHashToFile.get(infoHash);
        }

        private Message response(BitField message) {
            // TODO it's optional
            return null;
        }

        private Message response(Have message) {
            if (peerToPieces.containsKey(clientPeerId)) {
                List<Integer> pieces = peerToPieces.get(clientPeerId);
                pieces.add(message.getPieceIndex());
            } else {
                peerToPieces.put(clientPeerId, Collections.singletonList(message.getPieceIndex()));
            }
            return null;
        }

        private Message response(NotInterested message) {
            State state = states.get(clientPeerId);
            state.peerInterested = false;
            return null;
        }

        private Message response(Unchoke message) {
            State state = states.get(clientPeerId);
            state.peerChocking = false;
            return null;
        }

        private Message response(Interested message) {
            State state = states.get(clientPeerId);
            state.peerInterested = true;
            return null;
        }

        private Message response(Choke message) {
            State state = states.get(clientPeerId);
            state.peerChocking = true;
            return null;
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
