package com.elikill58.negativity.spigot.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.elikill58.negativity.spigot.ClickableText;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.UniversalUtils;
import com.elikill58.negativity.universal.permissions.Perm;

public class Utils {

	public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
			.split(",")[3];
	public static final ClickableText MESSAGE_UPDATE = new ClickableText().addOpenURLHoverEvent(ChatColor.YELLOW + "New version available (" + UniversalUtils.getLatestVersion().orElse("unknow") +  "). " + ChatColor.BOLD + "Download it here.", "Click here", "https://www.spigotmc.org/resources/aac-negativity-spigot-1-7-sponge-bungeecord-optimized.48399/");

	public static int getMultipleOf(int i, int multiple, int more) {
		while (i % multiple != 0)
			i += more;
		return i;
	}

	public static String coloredMessage(String msg) {
		return msg.replaceAll("&0", String.valueOf(ChatColor.BLACK))
				.replaceAll("&1", String.valueOf(ChatColor.DARK_BLUE))
				.replaceAll("&2", String.valueOf(ChatColor.DARK_GREEN))
				.replaceAll("&3", String.valueOf(ChatColor.DARK_AQUA))
				.replaceAll("&4", String.valueOf(ChatColor.DARK_RED))
				.replaceAll("&5", String.valueOf(ChatColor.DARK_PURPLE))
				.replaceAll("&6", String.valueOf(ChatColor.GOLD)).replaceAll("&7", String.valueOf(ChatColor.GRAY))
				.replaceAll("&8", String.valueOf(ChatColor.DARK_GRAY)).replaceAll("&9", String.valueOf(ChatColor.BLUE))
				.replaceAll("&a", String.valueOf(ChatColor.GREEN)).replaceAll("&b", String.valueOf(ChatColor.AQUA))
				.replaceAll("&c", String.valueOf(ChatColor.RED))
				.replaceAll("&d", String.valueOf(ChatColor.LIGHT_PURPLE))
				.replaceAll("&e", String.valueOf(ChatColor.YELLOW)).replaceAll("&f", String.valueOf(ChatColor.WHITE))
				.replaceAll("&k", String.valueOf(ChatColor.MAGIC)).replaceAll("&l", String.valueOf(ChatColor.BOLD))
				.replaceAll("&m", String.valueOf(ChatColor.STRIKETHROUGH))
				.replaceAll("&n", String.valueOf(ChatColor.UNDERLINE))
				.replaceAll("&o", String.valueOf(ChatColor.ITALIC)).replaceAll("&r", String.valueOf(ChatColor.RESET));
	}

	public static ItemStack createItem(Material m, String name, String... lore) {
		return createItem(m, name, 1, lore);
	}

	public static ItemStack createItem(Material m, String name, int quantite, String... lore) {
		ItemStack item = new ItemStack(m, quantite);
		ItemMeta meta = (ItemMeta) item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack createItem(Material m, String name, int amount, byte b, String... lore) {
		ItemStack item = new ItemStack(m, amount, b);
		ItemMeta meta = (ItemMeta) item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return item;
	}

	public static List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		try {
			Class<?> mcServer = Class.forName("net.minecraft.server." + VERSION + ".MinecraftServer");
			Object server = mcServer.getMethod("getServer").invoke(mcServer);
			Object craftServer = server.getClass().getField("server").get(server);
			Object getted = craftServer.getClass().getMethod("getOnlinePlayers").invoke(craftServer);
			if (getted instanceof Player[])
				for (Player obj : (Player[]) getted)
					list.add(obj);
			else if (getted instanceof List)
				for (Object obj : (List<?>) getted)
					list.add((Player) obj);
			else
				System.out.println("Unknow getOnlinePlayers");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static ItemStack createSkull(String name, int amount, String owner, String... lore) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta skullmeta = (SkullMeta) skull.getItemMeta();
		skullmeta.setDisplayName(ChatColor.RESET + name);
		skullmeta.setOwner(owner);
		List<String> lorel = new ArrayList<>();
		for (String s : lore)
			lorel.add(s);
		skullmeta.setLore(lorel);
		skull.setItemMeta(skullmeta);
		return skull;
	}

	public static int getPing(Player p) {
		try {
			Object object = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftPlayer").cast(p);
			Object entityPlayer = object.getClass().getMethod("getHandle", new Class[0]).invoke(object, new Object[0]);
			Field field = entityPlayer.getClass().getField("ping");
			return field.getInt(entityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int parseInPorcent(int i) {
		if (i > 100)
			return 100;
		else if (i < 0)
			return 0;
		else
			return i;
	}

	public static int parseInPorcent(double i) {
		if (i > 100)
			return 100;
		else if (i < 0)
			return 0;
		else
			return (int) i;
	}

	public static void sendPacket(Player p, String packetdir, Class<?> type, Object send) {
		try {
			Object craftPlayer = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftPlayer").cast(p);
			Object entityPlayer = craftPlayer.getClass().getMethod("getHandle", new Class[0]).invoke(craftPlayer,
					new Object[0]);
			Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
			Class<?> packetClass = Class
					.forName(packetdir.replaceAll("<version>", VERSION).replaceAll("%version%", VERSION));
			Constructor<?> packetConstructor = packetClass.getConstructor(type);
			Object packets = packetConstructor.newInstance(send);
			playerConnection.getClass()
					.getMethod("sendPacket", Class.forName("net.minecraft.server." + VERSION + ".Packet"))
					.invoke(playerConnection, packets);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Optional<Cheat> getCheatFromName(String s) {
		for (Cheat c : Cheat.values())
			if (c.getName().equalsIgnoreCase(s))
				return Optional.of(c);
		return Optional.empty();
	}

	public static Optional<Cheat> getCheatFromItem(Material m) {
		for (Cheat c : Cheat.values())
			if (c.getMaterial().equals(m))
				return Optional.of(c);
		return Optional.empty();
	}

	public static void sendUpdateMessageIfNeed(Player p) {
		if(!Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), "showAlert"))
			return;
		if(!(UniversalUtils.hasInternet() && !UniversalUtils.isLatestVersion(Optional.of(SpigotNegativity.getInstance().getDescription().getVersion()))))
			return;
		MESSAGE_UPDATE.sendToPlayer(p);
	}

	public static double getLastTPS() {
		double[] tps = getTPS();
		return tps[tps.length - 1];
	}
	
	public static double[] getTPS() {
		try {
			Class<?> mcServer = Class.forName("net.minecraft.server." + VERSION + ".MinecraftServer");
			Object server = mcServer.getMethod("getServer").invoke(mcServer);
			Object tps = server.getClass().getField("recentTps").get(server);
			return (double[]) tps;
		} catch (Exception e) {
			e.printStackTrace();
			return new double[] {};
		}
	}
	
	public enum Version {
		V1_7(7), V1_8(8), V1_9(9), V1_10(10), V1_11(11), V1_12(12), V1_13(13), V1_14(14), V1_15(15), V1_16(16), V1_17(17), HIGHER(42);

		private int power;

		Version(int power) {
			this.power = power;
		}

		public boolean isNewerThan(Version other) {
			return power > other.getPower();
		}

		public int getPower() {
			return power;
		}

		public static boolean isNewer(Version v1, Version v2) {
			return v1.isNewerThan(v2);
		}

		public static Version getVersion() {
			for (Version v : Version.values())
				if (VERSION.toLowerCase().startsWith(v.name().toLowerCase()))
					return v;
			return HIGHER;
		}
	}
}
