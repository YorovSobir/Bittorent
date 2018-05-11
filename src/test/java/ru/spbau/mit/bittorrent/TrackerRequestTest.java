package ru.spbau.mit.bittorrent;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.mit.util.Hash;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class TrackerRequestTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void parseTest() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        TrackerRequest trackerRequest = TrackerRequest.parse("GET localhost?peerId=dde57e820d65c87a0777da46aba7cd35a0c8436a&trackerId=TID&left=100&port=8080&ip=localhost&uploaded=15&infoHash=ba91bdfc9cae521a59b3b7ca4a519a8069c456a5&numwant=5&event=completed&downloaded=10&key=key 1.1\n");
        Assert.assertEquals(8080, trackerRequest.getPort());
        Assert.assertEquals("TID", trackerRequest.getTrackerId());
        Assert.assertEquals(15, trackerRequest.getUploaded());
        Assert.assertEquals(Hash.sha1("PID"), trackerRequest.getPeerId());
        Assert.assertEquals(5, trackerRequest.getNumwant());
        Assert.assertEquals("key", trackerRequest.getkey());
        Assert.assertEquals(100, trackerRequest.getLeft());
        Assert.assertEquals(TrackerRequest.Event.COMPLETED, trackerRequest.getEvent());
        Assert.assertEquals("localhost", trackerRequest.getIp());
        Assert.assertEquals(10, trackerRequest.getDownloaded());
    }

    @Test
    public void toStringTest() throws IOException, NoSuchAlgorithmException {
        final File file = tempFolder.newFile("test");
        final File torrentFile = tempFolder.newFolder("TorrentFile");
        try (RandomAccessFile raFile = new RandomAccessFile(file, "rw")) {
            raFile.setLength(1024 * 1024 * 1024);
            MetaInfo metaInfo = new MetaInfo();
            Assert.assertTrue(metaInfo.CreateFile(torrentFile.getAbsolutePath() + "/" + "new.torrent",
                    file.getAbsolutePath(),
                    "localhost",
                    "bla-bla",
                    "me",
                    "utf-8",
                    76800));
            TrackerRequest trackerRequest = new TrackerRequest(metaInfo);
            trackerRequest.setDownloaded(10);
            trackerRequest.setHttpVersion("1.1");
            trackerRequest.setIp("localhost");
            trackerRequest.setEvent(TrackerRequest.Event.COMPLETED);
            trackerRequest.setLeft(100);
            trackerRequest.setKey("key");
            trackerRequest.setNumwant(5);
            trackerRequest.setPeerId("PID");
            trackerRequest.setUploaded(15);
            trackerRequest.setTrackerId("TID");
            trackerRequest.setPort(8080);
            Assert.assertEquals("GET localhost?peerId=dde57e820d65c87a0777da46aba7cd35a0c8436a&trackerId=TID&left=100&port=8080&ip=localhost&uploaded=15&infoHash=ba91bdfc9cae521a59b3b7ca4a519a8069c456a5&numwant=5&event=completed&downloaded=10&key=key 1.1\n", trackerRequest.toString());
        }

    }
}