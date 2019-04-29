package cc.funkemunky.api.bungee;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BungeeObject {
    private String id;
    private long timeStamp = System.currentTimeMillis();
    private Object object;
}
