package ru.spbau.mit.bittorrent;

import ru.spbau.mit.http.request.Request;
import ru.spbau.mit.util.Hash;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class TrackerRequest {
    private Map<String, Object> fields = new HashMap<>();
    private MetaInfo metaInfo;
    private Request request;
    private String httpVersion;

    public TrackerRequest(MetaInfo metaInfo) {
        this.metaInfo = metaInfo;
        fields.put("infoHash", Hash.sha1(metaInfo.getInfo()));
    }

    public enum Event {
        STARTED,
        STOPPED,
        COMPLETED
    }

    @Override
    public String toString() {
        if (request == null) {
            StringBuilder uriBuilder = new StringBuilder();
            uriBuilder.append(metaInfo.getAnnounce());
            uriBuilder.append("?");
            for (Map.Entry<String, Object> entry: fields.entrySet()) {
                uriBuilder.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
            uriBuilder.deleteCharAt(uriBuilder.length() - 1);

            Request.RequestURI requestURI = new Request.RequestURI(Request.RequestURI.TYPE.ABSOLUTE_URI,
                    uriBuilder.toString());
            Request.RequestLine requestLine = new Request.RequestLine(Request.Method.GET, requestURI, httpVersion);
            request = new Request(requestLine);
        }
        return request.toString();
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setPeerId(String peerId) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        fields.put("peerId", Hash.sha1(peerId));
    }

    public void setPort(short port) {
        fields.put("port", port);
    }

    public void setUploaded(int uploaded) {
        fields.put("uploaded", uploaded);
    }

    public void setDownloaded(int downloaded) {
        fields.put("downloaded", downloaded);
    }

    public void setLeft(int left) {
        fields.put("left", left);
    }

    public void setEvent(Event event) {
        String value = "";
        switch (event) {
            case STARTED:
                value = "started";
                break;
            case STOPPED:
                value = "stopped";
                break;
            case COMPLETED:
                value = "completed";
                break;
        }
        fields.put("event", value);
    }

    public void setIp(String ip) {
        fields.put("ip", ip);
    }

    public void setNumwant(int numwant) {
        fields.put("numwant", numwant);
    }

    public void setKey(String key) {
        fields.put("key", key);
    }

    public void setTrackerId(String trackerId) {
        fields.put("trackerId", trackerId);
    }
}
