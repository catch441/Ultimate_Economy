package com.ue.common.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

import com.ue.ultimate_economy.UltimateEconomy;

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
	 * Returns the stack meta, that is provided by the Bukkit ItemFactory.
	 * 
	 * @param stack
	 * @return itemMeta
	 */
	public ItemMeta getItemMeta(ItemStack stack) {
		return stack.getItemMeta();
	}

	/**
	 * Sets the item meta for a stack. Performed by the Bukkit ItemFactory.
	 * 
	 * @param stack
	 * @param meta
	 */
	public void setItemMeta(ItemStack stack, ItemMeta meta) {
		stack.setItemMeta(meta);
	}
}
