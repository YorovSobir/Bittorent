package ru.spbau.mit.bittorrent;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.mit.bittorrent.metainfo.MetaInfo;
import ru.spbau.mit.bittorrent.metainfo.MultipleInfo;
import ru.spbau.mit.bittorrent.metainfo.SingleInfo;
import ru.spbau.mit.bittorrent.metainfo.TorrentFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MetaInfoTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void createSingleFile() throws IOException, NoSuchAlgorithmException {
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
            Assert.assertEquals("localhost", metaInfo.getAnnounce());
            Assert.assertEquals("bla-bla", metaInfo.getComment());
            Assert.assertEquals("me", metaInfo.getCreatedBy());
            Assert.assertEquals("utf-8", metaInfo.getEncoding());
            Assert.assertTrue(new File(torrentFile.getAbsolutePath() + "/" + "new.torrent").isFile());
            SingleInfo info = (SingleInfo) metaInfo.getInfo();
            Assert.assertEquals( 1024 * 1024 * 1024, info.getLength());
            Assert.assertEquals("test", info.getName());
            Assert.assertEquals( 76800, info.getPieceLength());
        }
    }

    @Test
    public void createMultipleFile() throws IOException, NoSuchAlgorithmException {
        final File file1 = tempFolder.newFile("test1");
        final File file2 = tempFolder.newFile("test2");
        try (RandomAccessFile raFile1 = new RandomAccessFile(file1, "rw");
             RandomAccessFile raFile2 = new RandomAccessFile(file2, "rw")) {
            raFile1.setLength(1024 * 1024 * 1024);
            raFile2.setLength(1024 * 1024 * 1024);
            MetaInfo metaInfo = new MetaInfo();
            Assert.assertTrue(metaInfo.CreateFile(tempFolder.getRoot().getAbsolutePath() + "/" + "new.torrent",
                    tempFolder.getRoot().getAbsolutePath(),
                    "localhost",
                    "bla-bla",
                    "me",
                    "utf-8",
                    76800));
            Assert.assertEquals("localhost", metaInfo.getAnnounce());
            Assert.assertEquals("bla-bla", metaInfo.getComment());
            Assert.assertEquals("me", metaInfo.getCreatedBy());
            Assert.assertEquals("utf-8", metaInfo.getEncoding());
            Assert.assertTrue(new File(tempFolder.getRoot().getAbsolutePath() + "/" + "new.torrent").isFile());
            final MultipleInfo info = (MultipleInfo) metaInfo.getInfo();
            Assert.assertEquals(tempFolder.getRoot().getName(), info.getName());
            Assert.assertEquals( 76800, info.getPieceLength());
            final List<TorrentFile> files = info.getFiles();
            for (TorrentFile tf : files) {
                Assert.assertEquals(1024 * 1024 * 1024, tf.getLength());
            }
        }
    }

    @Test
    public void loadFromFile() throws IOException, NoSuchAlgorithmException {
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
            MetaInfo newMetaInfo = new MetaInfo(torrentFile.getAbsolutePath() + "/" + "new.torrent");
            Assert.assertEquals("localhost", newMetaInfo.getAnnounce());
            Assert.assertEquals("bla-bla", newMetaInfo.getComment());
            Assert.assertEquals("me", newMetaInfo.getCreatedBy());
            Assert.assertEquals("utf-8", newMetaInfo.getEncoding());
            Assert.assertTrue(new File(torrentFile.getAbsolutePath() + "/" + "new.torrent").isFile());
            SingleInfo info = (SingleInfo) newMetaInfo.getInfo();
            Assert.assertEquals( 1024 * 1024 * 1024, info.getLength());
            Assert.assertEquals("test", info.getName());
            Assert.assertEquals( 76800, info.getPieceLength());
        }
    }
}