package ru.spbau.mit.bittorrent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class MetaInfoTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void createSingleFile() throws IOException {
        final File file = tempFolder.newFile("test");

    }

    @Test
    public void createMultipleFile() {

    }

    @Test
    public void loadFromFile() {
    }
}