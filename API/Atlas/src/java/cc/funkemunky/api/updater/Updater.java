package cc.funkemunky.api.updater;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.ConfigSetting;
import lombok.Getter;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Getter
public class Updater {
    private int update = - 1, currentUpdate = 17;
    private String version, downloadLink;
    private File pluginLocation;
    private boolean importantUpdate = true;

    @ConfigSetting(path = "updater")
    private boolean checkForUpdates = true;

    public Updater() {
        if(checkForUpdates) {
            String[] toSort = readFromUpdaterPastebin();

            if(toSort.length > 0) {
                update = Integer.parseInt(toSort[0]);
                version = toSort[1];
                downloadLink = toSort[2];
                importantUpdate = Boolean.parseBoolean(toSort[3]);
            } else {
                version = downloadLink = "N/A";
            }
        } else {
            version = Atlas.getInstance().getDescription().getVersion();

        }
    }

    private String[] readFromUpdaterPastebin() {
        try {
            URL url = new URL("https://pastebin.com/raw/fX2Ebkpz");
            URLConnection connection = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = reader.readLine();

            if(line != null) return line.split(";");
        } catch(Exception e) {
            e.printStackTrace();
        }

        return new String[0];
    }

    public boolean needsToUpdate() {
        return update > currentUpdate;
    }

    public boolean needsToUpdateIfImportant() {
        return importantUpdate && update > currentUpdate;
    }

    public void downloadNewVersion() {
        pluginLocation = UpdaterUtils.findPluginFile(Atlas.getInstance().getDescription().getName());
        try {
            InputStream in = new URL(downloadLink).openStream();
            Files.copy(in, Paths.get(pluginLocation.getPath()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
