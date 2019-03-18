package cc.funkemunky.atlas.utils;

import cc.funkemunky.atlas.AtlasBungee;

public class MiscUtils {

    public static void printToConsole(String string) {
        AtlasBungee.getInstance().getConsoleSender().sendMessage(Color.translate(string));
    }

    public static <T> T parseObjectFromString(String s, Class<T> clazz) throws Exception {
        return clazz.getConstructor(new Class[] {String.class}).newInstance(s);
    }

}
