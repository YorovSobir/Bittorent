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

    public TrackerRequest() {

    }

    public TrackerRequest(MetaInfo metaInfo) {
        this.metaInfo = metaInfo;
        try {
            fields.put("infoHash", Hash.sha1(metaInfo.getInfo().toString()));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
           throw new IllegalArgumentException(e);
        }
    }

    public enum Event {
        STARTED,
        STOPPED,
        COMPLETED
    }

    public static TrackerRequest parse(String stringTrackerRequest) {
        Request request = Request.parse(stringTrackerRequest);
        Request.RequestURI requestURI = request.getRequestLine().getRequestURI();
        String uri = requestURI.getUri();
//        MetaInfo metaInfo = new MetaInfo();
        TrackerRequest trackerRequest = new TrackerRequest();
        int index = uri.indexOf('?');
        if (index >= 0) {
            int end = index + 1;
            while (end < uri.length()) {
                int begin = end;
                while (end < uri.length() && Character.compare(uri.charAt(end), '=') != 0) {
                    ++end;
                }
                String key = uri.substring(begin, end);
                ++end;
                begin = end;
                while (end < uri.length() && Character.compare(uri.charAt(end), '&') != 0) {
                    ++end;
                }
                String value = uri.substring(begin, end);
                ++end;
                if (key.equals("numwant") ||
                        key.equals("left") ||
                        key.equals("downloaded") ||
                        key.equals("uploaded") ||
                        key.equals("port")) {
                    trackerRequest.fields.put(key, Integer.valueOf(value));
                } else if (key.equals("event")) {
                    switch (value) {
                        case "started": trackerRequest.fields.put(key, Event.STARTED); break;
                        case "stopped": trackerRequest.fields.put(key, Event.STOPPED); break;
                        case "completed": trackerRequest.fields.put(key, Event.COMPLETED); break;
                    }
                } else {
                    trackerRequest.fields.put(key, value);
                }
            }
        }
        return trackerRequest;
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

    public String getInfoHash() {
        return (String) fields.get("infoHash");
    }

    public void setInfoHash(String info) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        fields.put("infoHash", Hash.sha1(info));
    }

    public void setPeerId(String peerId) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        fields.put("peerId", Hash.sha1(peerId));
    }

    public String getPeerId() {
        return (String) fields.get("peerId");
    }

    public void setPort(int port) {
        fields.put("port", port);
    }

    public int getPort() {
        return (int) fields.get("port");
    }

    public void setUploaded(int uploaded) {
        fields.put("uploaded", uploaded);
    }

    public int getUploaded() {
        return (int) fields.get("uploaded");
    }

    public void setDownloaded(int downloaded) {
        fields.put("downloaded", downloaded);
    }

    public int getDownloaded() {
        return (int) fields.get("downloaded");
    }

    public void setLeft(int left) {
        fields.put("left", left);
    }

    public int getLeft() {
        return (int) fields.get("left");
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

    public Event getEvent() {
        return (Event) fields.get("event");
    }

    public void setIp(String ip) {
        fields.put("ip", ip);
    }

    public String getIp() {
        return (String) fields.get("ip");
    }

    public void setNumwant(int numwant) {
        fields.put("numwant", numwant);
    }

    public int getNumwant() { return (int) fields.get("numwant"); }

    public void setKey(String key) {
        fields.put("key", key);
    }

    public String getkey() { return (String) fields.get("key"); }

    public void setTrackerId(String trackerId) {
        fields.put("trackerId", trackerId);
    }

    public String getTrackerId() { return (String) fields.get("trackerId"); }
}
