package ru.spbau.mit.bittorrent.common;

import ru.spbau.mit.bittorrent.metainfo.BEncoder;
import ru.spbau.mit.http.response.Response;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TrackerResponse {

    public static final TrackerResponse EMPTY = new TrackerResponse();
    static {
        EMPTY.setComplete(0);
        EMPTY.setIncomplete(0);
    }

    private Map<String, Object> torrentResponse = new HashMap<>();
    private final Response response = new Response();
    private final BEncoder bEncoder = new BEncoder();
    private final Set<Peer> peers = new HashSet<>();
//    private boolean mode;
//    private String IPPort = "";

    private final Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
    private Matcher matcher;
    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public TrackerResponse() {
        torrentResponse.put("peers", peers);
    }

    public TrackerResponse(String responce) {
        bEncoder.setInput(responce.split("\n\n")[1]);
        torrentResponse = (Map<String, Object>) bEncoder.read();
        ArrayList<Map<String, Object>> tmp = (ArrayList<Map<String, Object>>) torrentResponse.get("peers");
        for (Map<String, Object> i : tmp) {
            Peer p = new Peer((String) i.get("peerID"), (String) i.get("ip"), (Integer) i.get("port"));
            peers.add(p);
        }
    }

    public void setFailureReason(String failureReason) {
        this.torrentResponse.put("failure reason", failureReason);
    }

    public String getFailureReason() {
        return (String) this.torrentResponse.get("failure reason");
    }

    public void setWarningMessage(String warningMessage) {
        this.torrentResponse.put("warning message", warningMessage);
    }

    public String getWarningMessage() {
        return (String) this.torrentResponse.get("warning message");
    }

    public void setInterval(int interval) {
        this.torrentResponse.put("interval", interval);
    }

    public int getInterval() {
        return (int) this.torrentResponse.get("interval");
    }

    public void setMinInterval(int minInterval) {
        this.torrentResponse.put("min interval", minInterval);
    }

    public int getMinInterval() {
        return (int) this.torrentResponse.get("min interval");
    }

    public void setTrackerId(String trackerId) {
        this.torrentResponse.put("tracker id", trackerId);
    }

    public String getTrackerId() {
        return (String) this.torrentResponse.get("tracker id");
    }

    public void setComplete(int complete) {
        this.torrentResponse.put("complete", complete);
    }

    public int getComplete() {
        return (int) this.torrentResponse.get("complete");
    }

    public void setIncomplete(int incomplete) {
        this.torrentResponse.put("incomplete", incomplete);
    }

    public int getIncomplete() {
        return (int) this.torrentResponse.get("incomplete");
    }

    public Set<Peer> getPeers() {
        return peers;
    }

    public synchronized void addPeer(Peer peer) {
        peers.add(peer);
    }

    public synchronized boolean removePeer(Peer peer) {
        return peers.remove(peer);
    }

//    public synchronized void addPeer(String ip, int port) {
//        IPPort = ip.concat(String.valueOf(port));
//    }

    public String getString() {
        bEncoder.write(torrentResponse);
        response.setVersion(1, 1);
        response.setContentType("text/plain");
        response.setCode(200);
        response.setData(bEncoder.getString());
        return response.getString();
    }

//    @Override
//    public String toString() {
//        bEncoder.write(torrentResponse);
//        response.setVersion(1, 1);
//        response.setContentType("text/plain");
//        response.setCode(200);
//        response.setData(bEncoder.toString());
//        return response.toString();
//    }
}
