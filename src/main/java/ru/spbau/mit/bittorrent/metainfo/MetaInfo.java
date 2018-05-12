package ru.spbau.mit.bittorrent.metainfo;

import ru.spbau.mit.util.Hash;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Stream;

public final class MetaInfo {
    private Map<String, Object> fields = new HashMap<>();
    private final BEncoder bEncoder = new BEncoder();
    private boolean mode;

    private static String readFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sb = new StringBuilder();
            String line;
            do {
                line = br.readLine();
                sb.append(line);
                sb.append("\n");
            } while (line != null);
            return sb.toString();
        } catch (IOException e) {
            throw new IllegalStateException("cannot read file", e);
        }
    }

    private void listf(String directoryName, ArrayList<File> files) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryName))) {
            paths.filter(Files::isRegularFile).forEach(f -> files.add(f.toFile()));
        }
    }

    private String fileSHA1(File file, long pieceSize) throws IOException, NoSuchAlgorithmException {
        StringBuilder sha1 = new StringBuilder();
        try (RandomAccessFile data = new RandomAccessFile(file, "r")) {
            byte[] buff = new byte[(int)pieceSize];
            for (long i = 0, len = data.length() / pieceSize; i < len; i++) {
                data.read(buff);
                sha1.append(Hash.sha1(buff.toString()), 0, 20);
            }
        }
        return sha1.toString();
    }

    public MetaInfo() {

    }

    public static MetaInfo parse(String pathToFile) {
        MetaInfo metaInfo = new MetaInfo();
        metaInfo.bEncoder.setInput(readFile(pathToFile));
        metaInfo.fields = (Map<String, Object>) metaInfo.bEncoder.read();
        return metaInfo;
    }

    public boolean CreateFile(String output,
                              String path,
                              String urlTracker,
                              String comment,
                              String author,
                              String encoding,
                              long pieceSize) throws IOException, NoSuchAlgorithmException {
        fields.put("announce", urlTracker);
        fields.put("comment", comment);
        fields.put("created by", author);
        fields.put("encoding", encoding);
        fields.put("creation date", new Date().getTime());
        File file = new File(path);
        Map<String, Object> info = new HashMap<>();
        if (!file.exists()) {
            return false;
        }
        info.put("piece length", pieceSize);
        if (file.isFile()) {
            mode = false;
            info.put("name", file.getName());
            info.put("length", file.length());
            info.put("pieces", fileSHA1(file, pieceSize));
        } else {
            mode = true;
            info.put("name", file.getName());
            info.put("files", new ArrayList<Map<String, Object>>());
            ArrayList<File> files = new ArrayList<>();
            listf(path, files);
            String absPathDir = file.getAbsolutePath();
            StringBuilder sha1 = new StringBuilder();
            for (File f : files) {
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("length", f.length());
                String[] pathToFile = f.getAbsolutePath()
                        .substring(f.getAbsolutePath().indexOf(absPathDir) + absPathDir.length() + 1)
                        .split("/");
                BEncoder bEnc = new BEncoder();
                bEnc.writeAll((Object[]) pathToFile);
                fileInfo.put("path", bEnc.toString());
                sha1.append(fileSHA1(f, pieceSize));
                ((ArrayList<Map<String, Object>>) info.get("files")).add(fileInfo);
            }
            info.put("pieces", sha1.toString());
        }
        fields.put("info", info);
        bEncoder.write(fields);
        try (BufferedWriter out = new BufferedWriter(new FileWriter(output))) {
            out.write(bEncoder.toString());
        }
        return true;
    }

    public String getAnnounce() {
        return (String) fields.get("announce");
    }

    public long getCreationData() {
        return (long) fields.get("creation date");
    }

    public String getComment() {
        return (String) fields.get("comment");
    }

    public String getCreatedBy() {
        return (String) fields.get("created by");
    }

    public String getEncoding() {
        return (String) fields.get("encoding");
    }

    public Info getInfo() {
        if (mode) {
            return new MultipleInfo((Map<String, Object>) fields.get("info"));
        }
        return new SingleInfo((Map<String, Object>) fields.get("info"));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MetaInfo) {
            MetaInfo metaInfo = (MetaInfo) obj;
            return metaInfo.fields.equals(this.fields);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.fields.hashCode();
    }
}
