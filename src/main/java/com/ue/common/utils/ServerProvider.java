package com.ue.common.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

import com.ue.general.impl.UltimateEconomy;

public class ServerProvider {

	/**
	 * Returns a bukkit world or null, if no world with the given name exists.
	 * 
	 * @param world
	 * @return world
	 */
	public World getWorld(String world) {
		return Bukkit.getWorld(world);
	}

	/**
	 * Returns a list of all online players.
	 * 
	 * @return online players
	 */
	public List<Player> getOnlinePlayers() {
		return new ArrayList<>(Bukkit.getOnlinePlayers());
	}

	/**
	 * Returns a list pf all bukkit worlds.
	 * 
	 * @return list of worlds
	 */
	public List<World> getWorlds() {
		return Bukkit.getWorlds();
	}

	/**
	 * Returns a bukkit player or null, if player is not online.
	 * 
	 * @param name
	 * @return player
	 */
	public Player getPlayer(String name) {
		return Bukkit.getPlayer(name);
	}

	/**
	 * Returns a bukkit boss bar.
	 * 
	 * @return boss bar
	 */
	public BossBar createBossBar() {
		return Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
	}

	/**
	 * Returns the bukkit plugin data folder path.
	 * 
	 * @return path
	 */
	public String getDataFolderPath() {
		return UltimateEconomy.getInstance.getDataFolder().getPath();
	}

	/**
	 * Returns the plugin instance.
	 * 
	 * @return ultimate economy plugin
	 */
	public Plugin getPluginInstance() {
		return UltimateEconomy.getInstance;
	}

	/**
	 * Returns the service component of the plugin.
	 * 
	 * @return service component
	 */
	public ServiceComponent getServiceComponent() {
		return UltimateEconomy.serviceComponent;
	}

	/**
	 * Returns the actual time in milliseconds. Its not the servertime, its the real
	 * life time.
	 * 
	 * @return time in ms
	 */
	public long getActualTime() {
		return Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Returns a new bukkit scoreboard.
	 * 
	 * @return scoreboard
	 */
	public Scoreboard createScoreBoard() {
		return Bukkit.getScoreboardManager().getNewScoreboard();
	}

	/**
	 * Returns a bukkit inventory with the given parameters.
	 * 
	 * @param owner
	 * @param size
	 * @param name
	 * @return inventory
	 */
	public Inventory createInventory(InventoryHolder owner, int size, String name) {
		return Bukkit.createInventory(owner, size, name);
	}

	/**
	 * Creates a new itemstack. Makes it possible to use the verify method of
	 * mockito in the unit tests.
	 * 
	 * @param material
	 * @param amount
	 * @return item stack
	 */
	public ItemStack createItemStack(Material material, int amount) {
		return new ItemStack(material, amount);
	}
}
