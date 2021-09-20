package cc.funkemunky.api.updater;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.ConfigSetting;
import dev.brighten.db.utils.json.JSONException;
import dev.brighten.db.utils.json.JSONObject;
import dev.brighten.db.utils.json.JsonReader;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Getter
public class Updater {
    private String downloadLink = "N/A";
    private String viewLink = "N/A";
    private String releaseDate = "N/A";
    private final String currentUpdate = Atlas.getInstance().getDescription().getVersion();
    private String latestUpdate = "N/A";
    private File pluginLocation;

    @ConfigSetting(path = "updater")
    private static boolean checkForUpdates = true;

    public Updater() {
        runUpdateCheck();
    }

    public void runUpdateCheck() {
        if(checkForUpdates) {
            try {
                JSONObject object = JsonReader
                        .readJsonFromUrl("https://api.github.com/repos/funkemunky/Atlas/releases/latest");

                latestUpdate = object.getString("tag_name");
                viewLink = object.getString("html_url");
                releaseDate = object.getString("published_at");
                downloadLink = object.getJSONArray("assets").getJSONObject(0)
                        .getString("browser_download_url");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean needsToUpdate() {
        return !currentUpdate.equals(latestUpdate);
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
