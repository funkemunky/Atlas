package cc.funkemunky.api.utils.world;

import cc.funkemunky.api.utils.XMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

@AllArgsConstructor
@Getter
@Setter
public class WrappedBlock {
    private Location location;
    private XMaterial type;
    private byte data;
}
