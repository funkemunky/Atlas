package cc.funkemunky.api.utils;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Color {

    public static final String Red = ChatColor.RED.toString();
    public static final String Yellow = ChatColor.YELLOW.toString();
    public static final String Gold = ChatColor.GOLD.toString();
    public static final String Green = ChatColor.GREEN.toString();
    public static final String Aqua = ChatColor.AQUA.toString();
    public static final String Dark_Aqua = ChatColor.DARK_AQUA.toString();
    public static final String Gray = ChatColor.GRAY.toString();
    public static final String Dark_Gray = ChatColor.DARK_GRAY.toString();
    public static final String Bold = ChatColor.BOLD.toString();
    public static final String Italics = ChatColor.ITALIC.toString();
    public static final String Strikethrough = ChatColor.STRIKETHROUGH.toString();
    public static final String White = ChatColor.WHITE.toString();
    public static final String Dark_Red = ChatColor.DARK_RED.toString();
    public static final String Dark_Green = ChatColor.DARK_GREEN.toString();
    public static final String Blue = ChatColor.BLUE.toString();
    public static final String Dark_Blue = ChatColor.DARK_BLUE.toString();
    public static final String Pink = ChatColor.LIGHT_PURPLE.toString();
    public static final String Purple = ChatColor.DARK_PURPLE.toString();
    public static final String Black = ChatColor.BLACK.toString();
    public static final String Underline = ChatColor.UNDERLINE.toString();

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String translate(String string) {
        Matcher match = pattern.matcher(string);
        if (match.matches())
            while (match.find()) {
                String colorToTranslate = string.substring(match.start(), match.end());
                int rgb;
                try {
                    rgb = Integer.parseInt(colorToTranslate.substring(1), 16);
                } catch (NumberFormatException var7) {
                    throw new IllegalArgumentException("Illegal hex string " + string);
                }
                if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_16)) {

                    StringBuilder magic = new StringBuilder("§x");
                    char[] var3 = string.substring(1).toCharArray();

                    for (char c : var3) {
                        magic.append('§').append(c);
                    }

                    string = string.replace(colorToTranslate, magic.toString());
                } else string = string.replace(colorToTranslate, getClosestColor(rgb));
            }

        return
                ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String strip(String string) {
        return ChatColor.stripColor(string);
    }

    @Deprecated
    public static String getColorFromString(String string) {
        if (string.contains("&")) {
            return Color.translate(string);
        } else {
            String color = (String) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(null, string), null);

            if (color == null) {
                Bukkit.getLogger().log(Level.WARNING, "The color '" + string + "' does not exist.");
                return Strikethrough;
            }
            return color;
        }
    }

    /** Method from TranslatableRewriter1_16.java in https://github.com/ViaVersion/ViaBackwards **/
    public static String getClosestColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        TranslatedColor closest = null;
        int smallestDiff = 0;

        for (TranslatedColor color : COLORS) {
            if (color.rgb == rgb) {
                return color.color;
            }

            // Check by the greatest diff of the 3 values
            int rAverage = (color.r + r) / 2;
            int rDiff = color.r - r;
            int gDiff = color.g - g;
            int bDiff = color.b - b;
            int diff = ((2 + (rAverage >> 8)) * rDiff * rDiff)
                    + (4 * gDiff * gDiff)
                    + ((2 + ((255 - rAverage) >> 8)) * bDiff * bDiff);
            if (closest == null || diff < smallestDiff) {
                closest = color;
                smallestDiff = diff;
            }
        }
        return closest.color;
    }

    /** Method from TranslatableRewriter1_16.java in https://github.com/ViaVersion/ViaBackwards **/
    public static ChatColor getClosestChatColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        TranslatedColor closest = null;
        int smallestDiff = 0;

        for (TranslatedColor color : COLORS) {
            if (color.rgb == rgb) {
                return color.chatcolor;
            }

            // Check by the greatest diff of the 3 values
            int rAverage = (color.r + r) / 2;
            int rDiff = color.r - r;
            int gDiff = color.g - g;
            int bDiff = color.b - b;
            int diff = ((2 + (rAverage >> 8)) * rDiff * rDiff)
                    + (4 * gDiff * gDiff)
                    + ((2 + ((255 - rAverage) >> 8)) * bDiff * bDiff);
            if (closest == null || diff < smallestDiff) {
                closest = color;
                smallestDiff = diff;
            }
        }
        return closest.chatcolor;
    }

    /** From TranslatableRewriter1_16.java in https://github.com/ViaVersion/ViaBackwards **/
    private static final TranslatedColor[] COLORS = {
            new TranslatedColor(Black, ChatColor.BLACK, 0x000000),
            new TranslatedColor(Dark_Blue, ChatColor.DARK_BLUE, 0x0000aa),
            new TranslatedColor(Dark_Green, ChatColor.DARK_GREEN, 0x00aa00),
            new TranslatedColor(Dark_Aqua, ChatColor.DARK_AQUA, 0x00aaaa),
            new TranslatedColor(Dark_Red, ChatColor.DARK_RED,0xaa0000),
            new TranslatedColor(Purple, ChatColor.DARK_PURPLE,0xaa00aa),
            new TranslatedColor(Gold, ChatColor.GOLD,0xffaa00),
            new TranslatedColor(Gray, ChatColor.GRAY,0xaaaaaa),
            new TranslatedColor(Dark_Gray, ChatColor.DARK_GRAY,0x555555),
            new TranslatedColor(Blue, ChatColor.BLUE,0x5555ff),
            new TranslatedColor(Green, ChatColor.GREEN,0x55ff55),
            new TranslatedColor(Aqua, ChatColor.AQUA,0x55ffff),
            new TranslatedColor(Red, ChatColor.RED,0xff5555),
            new TranslatedColor(Pink, ChatColor.LIGHT_PURPLE,0xff55ff),
            new TranslatedColor(Yellow, ChatColor.YELLOW,0xffff55),
            new TranslatedColor(White, ChatColor.WHITE,0xffffff)
    };

    /** From TranslatableRewriter1_16.java in https://github.com/ViaVersion/ViaBackwards **/
    private static final class TranslatedColor {

        private final String color;
        private final ChatColor chatcolor;
        private final int rgb;
        private final int r, g, b;

        TranslatedColor(String colorName, ChatColor chatColor, int rgb) {
            this.color = colorName;
            this.chatcolor = chatColor;
            this.rgb = rgb;
            r = (rgb >> 16) & 0xFF;
            g = (rgb >> 8) & 0xFF;
            b = rgb & 0xFF;
        }
    }
}

