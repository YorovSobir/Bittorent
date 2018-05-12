package ru.spbau.mit.bittorrent.client;

import ru.spbau.mit.bittorrent.client.message.BitField;
import ru.spbau.mit.bittorrent.client.message.Piece;
import ru.spbau.mit.bittorrent.client.message.Request;
import ru.spbau.mit.bittorrent.common.Peer;
import ru.spbau.mit.bittorrent.metainfo.MetaInfo;
import java.io.*;
import java.net.Socket;

public class Communication {
    private String peerId;
    private Peer peer;
    private MetaInfo metaInfo;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private BitField bitField;

    public Communication(String peerId, Peer peer, MetaInfo metaInfo) {
        this.peerId = peerId;
        this.peer = peer;
        this.metaInfo = metaInfo;
    }

    public void start() {
        try {
            socket = new Socket(peer.getIp(), peer.getPort());
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            Handshake handshake = new Handshake(metaInfo.getInfo().toString(), peerId);
            out.writeObject(handshake);
            Handshake peerHandshake = (Handshake) in.readObject();
            if (!peerHandshake.getPeerId().equals(peer.getPeerId())) {
                throw new IllegalArgumentException("Get another peerId from peer");
            }
            // peer send BitField message after handshake
            bitField = (BitField) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("cannot read BitField", e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public byte[] getPart(int part) throws IOException, ClassNotFoundException {
        if (getBit(part) == 0) {
            return null;
        }
        Request request = new Request(part, 0, metaInfo.getInfo().getPieceLength());
        out.writeObject(request);
        Piece piece = (Piece) in.readObject();
        return piece.getBlock();
    }

    private int getBit(int part) {
        byte[] temp = bitField.getBitField();
        int bytePos = part / 8;
        return (temp[bytePos] >> (7 - part % 8)) & 0x01;
    }

}
