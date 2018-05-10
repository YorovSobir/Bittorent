package ru.spbau.mit.bittorrent;

import ru.spbau.mit.http.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TrackerResponse {
    private Map<String, Object> torrentResponce;
    private Response response;
    private BEncoder bEncoder;
    private List<Map<String, Object>> peersDict;
    private boolean mode;
    private String IPPort;

    private final Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
    private Matcher matcher;
    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public TrackerResponse(boolean mode) {
        this.mode = mode;
        if (!mode) {
            torrentResponce.put("peers", peersDict);
        } else {
            torrentResponce.put("peers", IPPort);
        }
    }

    public void setFailureReason(String failureReason) {
        this.torrentResponce.put("failure reason", failureReason);
    }

    public void setWarningMessage(String warningMessage) {
        this.torrentResponce.put("warning message", warningMessage);
    }

    public void setInterval(int interval) {
        this.torrentResponce.put("interval", interval);
    }

    public void setMinInterval(int minInterval) {
        this.torrentResponce.put("min interval", minInterval);
    }

    public void setTrackerId(String trackerId) {
        this.torrentResponce.put("tracker id", trackerId);
    }

    public void setComplete(int complete) {
        this.torrentResponce.put("complete", complete);
    }

    public void setIncomplete(int incomplete) {
        this.torrentResponce.put("incomplete", incomplete);
    }

    public void push(String peerID, String ip, int port) {
        Map<String, Object> tmp = new HashMap<>();
        tmp.put("peer id", peerID);
        tmp.put("ip", ip);
        tmp.put("port", port);
        peersDict.add(tmp);
    }

    public void push(String ip, int port) {
        IPPort = ip.concat(String.valueOf(port));
    }

    @Override
    public String toString() {
        bEncoder.write(torrentResponce);
        response.setVersion(1, 1);
        response.setContentType("text/plain");
        response.setCode(200);
        response.setData(bEncoder.toString());
        return response.toString();
    }
}
