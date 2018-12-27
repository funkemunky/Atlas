## Description
#### What is Atlas?
Atlas is an all-in-one and cohesive API for developers who want to improve their Bukkit plugins or anti-cheats.

#### What does it include?

* An advanced and seemless custom packet-sniffing system, with individual packet-wrappers.
* Custom event system, running asynchronously so servers using your plugin will not experience any slowdowns on the main-thread.
* Enough math utilities to make you forget what you learned in math class.
* Many useful methods for doing little things like sending a colored message to console.
* The most accurate and light, public hitbox-grabbing system.
* Numerous reflection methods and utilities, including grabbers for NMS.

## Custom Event System

#### Creating an event
```java
package cc.funkemunky.fiona.events.custom;

import cc.funkemunky.api.event.system.Cancellable;
import cc.funkemunky.api.event.system.Event;
import cc.funkemunky.fiona.utils.FionaLocation;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PacketFunkeMoveEvent
        extends Event
        implements Cancellable {
    private Player player;
    private FionaLocation from, to;
    private boolean cancelled, onGround, jumped;

    public PacketFunkeMoveEvent(Player player, FionaLocation from, FionaLocation to, boolean onGround, boolean jumped) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.onGround = onGround;
        this.jumped = jumped;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

```

### Calling an event
```java
 PacketFunkeMoveEvent event = new PacketFunkeMoveEvent(data.player, new FionaLocation(data.movement.from), new FionaLocation(to), packet.isGround(), data.movement.hasJumped);
            if (data.lastTeleport.hasPassed(5) && (packet.isLook() || to.distance(data.movement.from) > 0.005) && data.lastLogin.hasPassed(40)) {
                EventManager.callEvent(event);
            }
```

### Example Listener
```java
package cc.funkemunky.fiona.events.bukkit;

import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.events.system.EventMethod;
import cc.funkemunky.fiona.events.system.Listener;

public class DataEvents implements Listener {
	@EventMethod
    public void onPacketMoveEvent(PacketFunkeMoveEvent event) {
        //blah blah blah
    }
}
```

### Registering a Listener
```java
private void registerEvents() {
        EventManager.register(new DataEvents());
}
```

## Custom Packet System

### Sending a packet
```java
TinyProtocolHandler.sendPacket(e.getPlayer(), new WrappedOutKeepAlivePacket(233 + e.getPlayer().getEntityId() + 935));
```

### Listening for Packets

#### Client Packets
```java
@EventMethod
    public void onEvent(PacketRecieveEvent e) {
        if(e.getType().equals(Packet.Client.ENTITY_ACTION)) {
            WrappedInEntityActionPacket packet = new WrappedInEntityActionPacket(e.getPacket(), e.getPlayer());

            switch(packet.getAction()) {
                case START_SNEAKING:
                    data.skiderino.sneak = true;
                    break;
                case STOP_SNEAKING:
                    data.skiderino.sneak = false;
                    break;
                case START_SPRINTING:
                    data.skiderino.sprint = true;
                    break;
                case STOP_SPRINTING:
                    data.skiderino.sprint = false;
                    break;
            }
        }
    }
```

#### Server Packets
```java
 @EventMethod
    public void onPacketSend(PacketSendEvent e) {
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(e.getPlayer());

        if (data != null) {
            switch (e.getType()) {
                case Packet.Server.KEEP_ALIVE:
                    data.lastServerKeepAlive = System.currentTimeMillis();
                    break;
                case Packet.Server.ENTITY_METADATA:
                    WrappedOutEntityMetadata wrapper = new WrappedOutEntityMetadata(e.getPacket(), e.getPlayer());

                    if(wrapper.getObjects().size() > 0) {
                        if(wrapper.getObjects().get(0).getObject() instanceof Byte && (data.isUsingItem = ((Byte) wrapper.getObjects().get(0).getObject()) % 0x5 == 1)) {
                            data.lastUseItem.reset();
                        }
                    }
                break;
			}
        }
    }
```

## Collision Hit-Boxes

### Getting a specific block hit-box
```java
@EventHandler(priority = EventPriority.HIGHEST)
    public void onEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null
                    && event.getItem().getType().equals(Material.BLAZE_ROD)
                    && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Color.Red + "Magic Box Wand")) {
                Block block = event.getClickedBlock();

                event.getPlayer().sendMessage(block.getType().name() + "'s Data: " + block.getData());
                for (BoundingBox box : Atlas.getInstance().getBlockBoxManager().getBlockBox().getSpecificBox(block.getLocation())) {
                    for (float x = box.minX; x < box.maxX; x += 0.2f) {
                        for (float y = box.minY; y < box.maxY; y += 0.2f) {
                            for (float z = box.minZ; z < box.maxZ; z += 0.2f) {
                                WrappedPacketPlayOutWorldParticle packet = new WrappedPacketPlayOutWorldParticle(WrappedEnumParticle.FLAME, true, x, y, z, 0f, 0f, 0f, 0f, 1, null);
                                packet.sendPacket(event.getPlayer());
                            }
                        }
                    }
                    event.getPlayer().sendMessage(ReflectionsUtil.getVanillaBlock(event.getClickedBlock()).getClass().getSimpleName() + ": " + box.toString());
                }
            }
        }
    }
```

### Getting all collided hit-boxes
```java
List<BoundingBox> box = Atlas.getInstance().getBlockBoxManager().getBlockBox().getCollidingBoxes(to.getWorld(), data.boundingBox.grow(0.5f, 0.1f, 0.5f).subtract(0, 0.5f, 0, 0, 0, 0));

        CollisionAssessment assessment = new CollisionAssessment(data.boundingBox, data);
        box.forEach(bb -> assessment.assessBox(bb, to.getWorld()));
```

### Getting an entity hitbox
```java
MiscUtils.getEntityBoundingBox(e.getPlayer());
```
[Latest]: https://github.com/funkemunky/Atlas/releases "Download Latest"
