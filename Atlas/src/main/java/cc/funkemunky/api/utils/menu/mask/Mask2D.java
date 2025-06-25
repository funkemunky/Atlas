package cc.funkemunky.api.utils.menu.mask;

import cc.funkemunky.api.utils.menu.Menu;
import cc.funkemunky.api.utils.menu.button.Button;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author Missionary (missionarymc@gmail.com)
 * @since 5/17/2018
 * <p>
 * Note: The use of a blank space ( ) or underscore (_) will remove the item.
 */
public class Mask2D implements Mask {

    private final Map<Character, Button> maskButtonKey;
    private String maskPattern;

    public Mask2D() {
        this.maskButtonKey = new HashMap<>();
    }

    @Override
    public Mask setButton(char key, Button button) {
        maskButtonKey.put(key, button);
        return this;
    }

    @Override
    public Mask setMaskPattern(String... maskPattern) {
        String concatPattern = String.join("", maskPattern);
        for (char c : concatPattern.toCharArray()) {
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (!maskButtonKey.containsKey(c)) {
                throw new IllegalArgumentException(String.format("%s is a unrecognized mapping for the Mask.", c));
            }
        }
        this.maskPattern = concatPattern.replace("\n", "");
        return this;
    }

    @Override
    public void applyTo(Menu menu) {
        if (maskButtonKey.isEmpty()) {
            throw new IllegalArgumentException("The maskButtonKey map is empty!");
        }
        if (maskPattern == null) {
            throw new IllegalArgumentException("The maskPattern is null!");
        }
        if (maskPattern.length() > menu.getMenuDimension().getSize()) {
            throw new IllegalArgumentException(String.format("The maskPattern length: %d is longer than the menu dimension: %d", maskPattern.length(), menu.getMenuDimension().getSize()));
        }

        IntStream.range(0, maskPattern.length()).forEach(i -> {
            char ch = maskPattern.charAt(i);
            menu.setItem(i, ch == ' ' || ch == '_' ? null : maskButtonKey.get(ch));
        });
    }
}
