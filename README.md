## Download
Download the latest stable release on Spigot: https://www.spigotmc.org/resources/atlas-custom-packet-listening-numerous-utils-block-boundingboxes-1-7-1-13.66845/

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

#### JavaDocs
https://funkemunky.github.io/Atlas/

#### Using Code from This Repo
I'm aware that more people use Atlas as a means of learning rather than as an API, and therefore many people take parts of this repository and put it into their own projects. I have no problem with this. However, if you are using code from this repository in a **closed source** project, I would like ***easily-readable credit*** on any official distribution page for your project/product. Thanks!

## Custom Event System

#### Creating an event
```java
package cc.funkemunky.anticheat.api.event;

import cc.funkemunky.api.events.AtlasEvent;
import cc.funkemunky.api.events.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;


public class TickEvent extends AtlasEvent implements Cancellable {
    private int currentTick;

    public TickEvent(int currentTick) {
        this.currentTick = currentTick;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {

    }
}

```

### Calling an event
```java
new BukkitRunnable() {
            public void run() {
                TickEvent tickEvent = new TickEvent(currentTicks++);

                Atlas.getInstance().getEventManager().callEvent(tickEvent);
            }
        }.runTaskTimerAsynchronously(this, 1L, 1L);
```

### Example Listener
```java
package cc.funkemunky.anticheat.impl.listeners;

import cc.funkemunky.anticheat.Rock;
import cc.funkemunky.anticheat.api.data.PlayerData;
import cc.funkemunky.anticheat.api.event.TickEvent;
import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.Listen;
import cc.funkemunky.api.utils.Init;

@Init
public class FunkeListeners implements AtlasListener {

    @Listen
    public void onTickEvent(TickEvent event) {
        Atlas.getInstance().executeTask(() -> Rock.getInstance().getDataManager().getDataObjects().keySet().forEach(key -> {
            PlayerData data = Rock.getInstance().getDataManager().getDataObjects().get(key);

            data.getActionProcessor().update(data);
        }));
    }
}
```

### Registering a Listener
```java
public void onEnable() {
        Atlas.getInstance().getEventManager().registerListeners(new FunkeListeners(), this);
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
@Listen
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
 @Listen
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

## Credits

### TinyProtocol and Additions
Credits to [@dmulloy2](https://github.com/dmulloy2) and [@aandk](https://github.com/aandk) for the TinyProtocol and Reflection utilities. Thanks to [@DeprecatedLuke](https://github.com/DeprecatedLuke) for his useful additions to TinyProtocol and his initial packet wrappers.

### Code Profile System
Credits to [@DeprecatedLuke](https://github.com/DeprecatedLuke) for the BaseProfiler and Profiler classes.
