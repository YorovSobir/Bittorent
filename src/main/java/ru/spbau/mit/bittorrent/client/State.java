package ru.spbau.mit.bittorrent.client;

public class State {
    public static final State INITIAL = new State(true, false, true, false);
    public boolean amChocking;
    public boolean amInterested;
    public boolean peerChocking;
    public boolean peerInterested;

    public State(boolean amChocking, boolean amInterested, boolean peerChocking, boolean peerInterested) {
        this.amChocking = amChocking;
        this.amInterested = amInterested;
        this.peerChocking = peerChocking;
        this.peerInterested = peerInterested;
    }
}
