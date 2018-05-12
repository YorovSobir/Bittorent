package ru.spbau.mit;

import ru.spbau.mit.bittorrent.tracker.Tracker;

public class MainTracker {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("ERROR: please enter port number!");
            return;
        }
        Tracker tracker = new Tracker(Integer.valueOf(args[0]));
        tracker.run();
    }
}
