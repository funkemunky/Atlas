package cc.funkemunky.api.config;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.FunkeFile;
import dev.brighten.db.utils.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MessageConfig {

    public String language;
    private FunkeFile file;
    private Map<String, String> messages = new HashMap<>();

    public MessageConfig(FunkeFile file, String language) {
        this.file = file;
        this.language = language;
        load();
    }

    public void save() {
        file.getLines().clear();
        messages.forEach((key, value) -> file.addLine(key + ": " + "" + value + ""));
        file.write();
    }

    public void reload() {
        file.readFile();
        load();
    }

    private void load() {
        messages.clear();

        for (int i = 0; i < file.getLines().size(); i++) {
            String line = file.getLines().get(i);

            int lineNumber = i; //For the sole purpose of using in the consumer below since it needs the int as final.
            if(runCheck(line, reason -> Atlas.getInstance().alog("&cError while parsing message "
                        + "&8(&f" + language + "&8) &con line " + lineNumber))) {
                return;
            }
        }

        file.getLines().stream().map(line -> {
            String[] split = line.replace("\"", "").split(": ", 2);

            return new Pair<>(split[0], split[1]);
        }).forEach(pair -> messages.put(pair.key, pair.value));
    }

    public String msg(String key, String def) {
        String string = Color.translate(messages.computeIfAbsent(key, stringKey -> {
            String nstring = def.replace("\n", "\\n");

            file.addLine(key + ": " + "" + nstring + "");
            file.write();
            return nstring;
        }));
        return string.replace("\\n", "\n");
    }

    //Returns true if the check failed.
    private boolean runCheck(String line, Consumer<String> reason) {
        if(line.length() == 0) {
            reason.accept("Line is blank. Please remove this line.");
            return true;
        } else if(!line.contains(":")) {
            reason.accept("No ':' was used.");
            return true;
        } else if(String.valueOf(line.charAt(0)).equals(":")) {
            reason.accept("There is no key value (name) before ':' character.");
            return true;
        }
        return false;
    }
}
