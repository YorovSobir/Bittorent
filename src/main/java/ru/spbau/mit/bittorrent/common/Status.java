package ru.spbau.mit.bittorrent.common;

public final class Status {
    private int download;
    private int upload;
    private int left;
    private TrackerRequest.Event event;
    private TrackerResponse trackerResponse;

    public Status(int download, int upload, int left, TrackerRequest.Event event) {
        this.download = download;
        this.upload = upload;
        this.left = left;
        this.event = event;
    }

    public synchronized int getDownload() {
        return download;
    }

    public synchronized void setDownload(int download) {
        this.download = download;
    }

    public synchronized int getUpload() {
        return upload;
    }

    public synchronized void setUpload(int upload) {
        this.upload = upload;
    }

    public synchronized int getLeft() {
        return left;
    }

    public synchronized void setLeft(int left) {
        this.left = left;
    }

    public synchronized TrackerRequest.Event getEvent() {
        return event;
    }

    public synchronized void setEvent(TrackerRequest.Event event) {
        this.event = event;
    }

    public synchronized void setResponse(TrackerResponse response) {
        trackerResponse = response;
    }

    public synchronized TrackerResponse getResponse() {
        return trackerResponse;
    }
}
