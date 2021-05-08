package org.ue.common.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.ue.general.impl.PluginImpl;
import org.ue.general.impl.UltimateEconomyCommand;

public class ServerProvider {
	
	/**
	 * Returns the system time in ms.
	 * @return time
	 */
	public long getSystemTime() {
		return System.currentTimeMillis();
	}
	
	/**
	 * Returns the default world game time.
	 * @return time
	 */
	public long getWorldTime() {
		return Bukkit.getWorlds().get(0).getGameTime();
	}

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
		return PluginImpl.getInstance.getDataFolder().getPath();
	}

	/**
	 * Returns the java plugin instance.
	 * 
	 * @return ultimate economy plugin
	 */
	public JavaPlugin getJavaPluginInstance() {
		return PluginImpl.getInstance;
	}

	/**
	 * Returns the plugin instance.
	 * 
	 * @return ultimate economy plugin
	 */
	public Plugin getPluginInstance() {
		return PluginImpl.getInstance;
	}

	/**
	 * Returns the service component of the plugin.
	 * 
	 * @return service component
	 */
	public ServiceComponent getServiceComponent() {
		return PluginImpl.serviceComponent;
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
	 * Returns a new scoreboard.
	 * 
	 */
	public Scoreboard createScoreboard() {
		return Bukkit.getScoreboardManager().getNewScoreboard();
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

	/**
	 * Returns the bukkit server.
	 * 
	 * @return server
	 */
	public Server getServer() {
		return Bukkit.getServer();
	}

	/**
	 * Returns the bukkit services manager.
	 * 
	 * @return services manager
	 */
	public ServicesManager getServicesManager() {
		return Bukkit.getServicesManager();
	}

	/**
	 * Returns the comamnd map of the plugin.
	 * 
	 * @param commandMapField
	 * @return command map
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public CommandMap getCommandMap(Field commandMapField) throws IllegalArgumentException, IllegalAccessException {
		return (CommandMap) commandMapField.get(getServer().getPluginManager());
	}
	
	/**
	 * Returns a new command associated with the plugin.
	 * @param name
	 * @return command
	 */
	public UltimateEconomyCommand createUltimateEconomyCommand(String name) {
		return new UltimateEconomyCommand(name, getPluginInstance());
	}
}
