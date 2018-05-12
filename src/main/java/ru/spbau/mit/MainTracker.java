package ru.spbau.mit;

import ru.spbau.mit.bittorrent.tracker.Tracker;

public class MainTracker {
    public static void main(String[] args) {
        Tracker tracker = new Tracker(2, 1234);
        tracker.run();
    }
}
