package cc.funkemunky.api.utils;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.handlers.PlayerSizeHandler;
import cc.funkemunky.api.utils.world.BlockData;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Helper {

	public static int angularDistance(double alpha, double beta) {
		while (alpha < 0) alpha += 360;
		while (beta < 0) beta += 360;
		double phi = Math.abs(beta - alpha) % 360;
		return (int) (phi > 180 ? 360 - phi : phi);
	}

	public static Vector vector(double yaw, double pitch) {
		Vector vector = new Vector();
		vector.setY(-Math.sin(Math.toRadians(pitch)));
		double xz = Math.cos(Math.toRadians(pitch));
		vector.setX(-xz * Math.sin(Math.toRadians(yaw)));
		vector.setZ(xz * Math.cos(Math.toRadians(yaw)));
		return vector;
	}

	public static SimpleCollisionBox getMovementHitbox(Player player, double x, double y, double z) {
		return PlayerSizeHandler.instance.bounds(player, x, y, z);
	}

	public static SimpleCollisionBox getMovementHitbox(Player player) {
		return PlayerSizeHandler.instance.bounds(player);
	}

	public static SimpleCollisionBox getCombatHitbox(Player player, ProtocolVersion version) {
		return version.isBelow(ProtocolVersion.V1_9)
				? PlayerSizeHandler.instance.bounds(player).expand(.1, 0, .1)
				: PlayerSizeHandler.instance.bounds(player);
	}

	public static Block getBlockAt(World world, int x, int y, int z) {
		return world.isChunkLoaded(x >> 4, z >> 4)
				? world.getChunkAt(x >> 4, z >> 4).getBlock(x & 15, y, z & 15)
				: null;
	}

	public static SimpleCollisionBox wrap(SimpleCollisionBox a, SimpleCollisionBox b) {
		double minX = Math.min(a.xMin, b.xMin);
		double minY = Math.min(a.yMin, b.yMin);
		double minZ = Math.min(a.zMin, b.zMin);
		double maxX = Math.max(a.xMax, b.xMax);
		double maxY = Math.max(a.yMax, b.yMax);
		double maxZ = Math.max(a.zMax, b.zMax);
		return new SimpleCollisionBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static SimpleCollisionBox wrap(List<SimpleCollisionBox> box) {
		if (!box.isEmpty()) {
			SimpleCollisionBox wrap = box.get(0).copy();
			for (int i = 1; i < box.size(); i++) {
				SimpleCollisionBox a = box.get(i);
				if (wrap.xMin > a.xMin) wrap.xMin = a.xMin;
				if (wrap.yMin > a.yMin) wrap.yMin = a.yMin;
				if (wrap.zMin > a.zMin) wrap.zMin = a.zMin;
				if (wrap.xMax < a.xMax) wrap.xMax = a.xMax;
				if (wrap.yMax < a.yMax) wrap.yMax = a.yMax;
				if (wrap.zMax < a.zMax) wrap.zMax = a.zMax;
			}
			return wrap;
		}
		return null;
	}

	public static List<Block> blockCollisions(List<Block> blocks, CollisionBox box) {
		return blocks.stream()
				.filter(b -> BlockData.getData(b.getType()).getBox(b, ProtocolVersion.getGameVersion()).isCollided(box))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	public static boolean isCollided(SimpleCollisionBox toCheck, CollisionBox other) {
		List<SimpleCollisionBox> downcasted = new ArrayList<>();

		other.downCast(downcasted);

		return downcasted.stream().anyMatch(box -> box.xMax >= toCheck.xMin && box.xMin <= toCheck.xMax
				&& box.yMax >= toCheck.yMin && box.yMin <= toCheck.yMax && box.zMax >= toCheck.zMin
				&& box.zMin <= toCheck.zMax);
	}

	public static List<Block> blockCollisions(List<Block> blocks, CollisionBox box, int material) {
		return blocks.stream().filter(b -> Materials.checkFlag(b.getType(), material))
				.filter(b -> BlockData.getData(b.getType()).getBox(b, ProtocolVersion.getGameVersion()).isCollided(box))
				.collect(Collectors.toCollection(LinkedList::new));
	}


	public static <C extends CollisionBox> List<C> collisions(List<C> boxes, CollisionBox box) {
		return boxes.stream().filter(b -> b.isCollided(box))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	public static List<Block> getBlocksNearby2(World world, SimpleCollisionBox collisionBox, int mask) {
		int x1 = (int) Math.floor(collisionBox.xMin);
		int y1 = (int) Math.floor(collisionBox.yMin);
		int z1 = (int) Math.floor(collisionBox.zMin);
		int x2 = (int) Math.ceil(collisionBox.xMax);
		int y2 = (int) Math.ceil(collisionBox.yMax);
		int z2 = (int) Math.ceil(collisionBox.zMax);
		List<Block> blocks = new LinkedList<>();
		Block block;
		for (int x = x1; x <= x2; x++)
			for (int y = y1; y <= y2; y++)
				for (int z = z1; z <= z2; z++)
					if ((block = getBlockAt(world, x, y, z)) != null
							&& block.getType()!= XMaterial.AIR.parseMaterial())
						if (Materials.checkFlag(block.getType(),mask))
							blocks.add(block);
		return blocks;
	}

	private static final int[] decimalPlaces = {0, 10, 100, 1000, 10000, 100000, 1000000,
			10000000, 100000000, 1000000000};

	public static double format(double d, int dec) {
		return (long) (d * decimalPlaces[dec] + 0.5) / (double) decimalPlaces[dec];
	}

	public static String drawUsage(long max, long time) {
		double chunk = max / 50.;
		String line = IntStream.range(0, 50).mapToObj(i -> (chunk * i < time ? "§c" : "§7") + "❘")
				.collect(Collectors.joining("", "[", ""));
		String zeros = "00";
		String nums = Integer.toString((int) ((time / (double) max) * 100));
		return line + "§f] §c" + zeros.substring(0, 3 - nums.length()) + nums + "% §f❘";
	}

	public static String drawUsage(long max, double time) {
		double chunk = max / 50.;
		String line = IntStream.range(0, 50).mapToObj(i -> (chunk * i < time ? "§c" : "§7") + "❘")
				.collect(Collectors.joining("", "[", ""));
		String nums = String.valueOf(format((time / (double) max) * 100, 3));
		return line + "§f] §c" + nums + "%";
	}

	public static List<CollisionBox> toCollisions(List<Block> blocks) {
		return blocks.stream().map(b -> BlockData.getData(b.getType()).getBox(b, ProtocolVersion.getGameVersion()))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	public static List<SimpleCollisionBox> toCollisionsDowncasted(List<Block> blocks) {
		List<SimpleCollisionBox> collisions = new LinkedList<>();
		blocks.forEach(b -> BlockData.getData(b.getType())
				.getBox(b, ProtocolVersion.getGameVersion()).downCast(collisions));
		return collisions;
	}

	public static CollisionBox toCollisions(Block b) {
		return BlockData.getData(b.getType()).getBox(b, ProtocolVersion.getGameVersion());
	}
}
