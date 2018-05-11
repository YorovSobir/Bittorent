package ru.spbau.mit.bittorrent;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public boolean CreateFile(String output,
                           String path,
                           String urlTracker,
                           String comment,
                           String author,
                           String encoding) throws FileNotFoundException {
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
        if (file.isFile()) {
            mode = false;
            info.put("name", file.getName());
            info.put("length", file.length());
        } else {
            mode = true;
            info.put("name", file.getName());
            info.put("files", new ArrayList<Map<String, Object>>());
            ArrayList<File> files = new ArrayList<>();
            listf(path, files);
            String absPathDir = file.getAbsolutePath();
            for (File f : files) {
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("length", f.length());
                String[] pathToFile = f.getAbsolutePath()
                        .substring(f.getAbsolutePath().indexOf(absPathDir) + 1)
                        .split("/");
                BEncoder bEnc = new BEncoder();
                bEnc.writeAll((Object[]) pathToFile);
                fileInfo.put("path", bEnc.toString());
                ((ArrayList<Map<String, Object>>) info.get("files")).add(fileInfo);
            }
        }
        metaInfo.put("info", info);
        bEncoder.write(metaInfo);
        try (PrintWriter out = new PrintWriter(output)) {
            out.println(bEncoder.toString());
        }
        return true;
    }

    public void LoadFromFile(String path) throws IOException {
        bEncoder.setInput(readFile(path));
        metaInfo = (Map<String, Object>) bEncoder.read();
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
