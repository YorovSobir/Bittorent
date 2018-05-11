package ru.spbau.mit.bittorrent;

import ru.spbau.mit.util.Hash;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MetaInfo {
    private Map<String, Object> metaInfo = new HashMap<>();
    private final BEncoder bEncoder = new BEncoder();
    private boolean mode;

    private String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    private void listf(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath(), files);
            }
        }
    }

    private String fileSHA1(File file, int pieceSize) throws IOException, NoSuchAlgorithmException {
        StringBuilder sha1 = new StringBuilder();
        try (RandomAccessFile data = new RandomAccessFile(file, "r")) {
            byte[] buff = new byte[pieceSize];
            for (long i = 0, len = data.length() / pieceSize; i < len; i++) {
                data.read(buff);
                sha1.append(Hash.sha1(buff.toString()), 0, 20);
            }
        }
        return sha1.toString();
    }

    public MetaInfo() {

    }

    public MetaInfo(String path) throws IOException {
        bEncoder.setInput(readFile(path));
        metaInfo = (Map<String, Object>) bEncoder.read();
    }

    public boolean CreateFile(String output,
                              String path,
                              String urlTracker,
                              String comment,
                              String author,
                              String encoding,
                              int pieceSize) throws IOException, NoSuchAlgorithmException {
        metaInfo.put("announce", urlTracker);
        metaInfo.put("comment", comment);
        metaInfo.put("created by", author);
        metaInfo.put("encoding", encoding);
        metaInfo.put("creation date", new Date().getTime());
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
                        .substring(f.getAbsolutePath().indexOf(absPathDir) + 1)
                        .split("/");
                BEncoder bEnc = new BEncoder();
                bEnc.writeAll((Object[]) pathToFile);
                fileInfo.put("path", bEnc.toString());
                sha1.append(fileSHA1(f, pieceSize));
                ((ArrayList<Map<String, Object>>) info.get("files")).add(fileInfo);
            }
            info.put("pieces", sha1.toString());
        }
        metaInfo.put("info", info);
        bEncoder.write(metaInfo);
        try (PrintWriter out = new PrintWriter(output)) {
            out.println(bEncoder.toString());
        }
        return true;
    }

    public String getAnnounce() {
        return (String) metaInfo.get("announce");
    }

    public long getCreationData() {
        return (long) metaInfo.get("creation date");
    }

    public String getComment() {
        return (String) metaInfo.get("comment");
    }

    public String getCreatedBy() {
        return (String) metaInfo.get("created by");
    }

    public String getEncoding() {
        return (String) metaInfo.get("encoding");
    }

    public Info getInfo() {
        if (mode) {
            return new MultipleInfo((Map<String, Object>) metaInfo.get("info"));
        }
        return new SingleInfo((Map<String, Object>) metaInfo.get("info"));
    }

}
