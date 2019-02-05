package cc.funkemunky.atlas.utils;

import cc.funkemunky.atlas.AtlasBungee;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FunkeFile {
    public List<String> lines = new ArrayList<>();
    private File file;
    private String name;

    public FunkeFile(Plugin plugin, String path, String name) {
        this.file = new File(plugin.getDataFolder() + File.separator + path);
        this.file.mkdirs();
        this.file = new File(plugin.getDataFolder() + File.separator + path, name);
        try {
            this.file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.name = name;

        readFile();
    }

    public FunkeFile(File file, String name) {
        this.file = AtlasBungee.getInstance().getDataFolder();
        this.file.mkdirs();
        this.file = new File(AtlasBungee.getInstance().getDataFolder(), name);
        try {
            this.file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.name = name;

        readFile();
    }

    public void clear() {
        this.lines.clear();
        if(file.delete()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void addLine(String line) {
        this.lines.add(line);
    }

    public void write() {
        try {
            FileWriter fw = new FileWriter(this.file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String line : this.lines) {
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            fw.close();
        } catch (Exception localException) {
        }
    }

    public void readFile() {
        this.lines.clear();
        try {
            FileReader fr = new FileReader(this.file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                this.lines.add(line);
            }
            br.close();
            fr.close();
        } catch (Exception exx) {
            exx.printStackTrace();
        }
    }

    public String getName() {
        return this.name;
    }

    public String getText() {
        String text = "";
        for (int i = 0; i < this.lines.size(); i++) {
            String line = (String) this.lines.get(i);

            text = text + line + (this.lines.size() - 1 == i ? "" : "\n");
        }
        return text;
    }

    public List<String> getLines() {
        return this.lines;
    }
}