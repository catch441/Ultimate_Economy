package com.ue.townsystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.TownSystemException;
import com.ue.player.EconomyPlayer;

public class TownWorld {

	private static List<TownWorld> townWorldList = new ArrayList<>();

	private double foundationPrice, expandPrice;
	private final String worldName;
	private File file;
	private FileConfiguration config;
	private List<String> townNames;
	private List<Town> towns;

	/**
	 * <p>
	 * Represents a townworld.
	 * <p>
	 * 
	 * @param main
	 * @param world
	 */
	private TownWorld(File mainDataFolder, String world) {
		worldName = world;
		towns = new ArrayList<>();
		file = new File(mainDataFolder, world + "_TownWorld" + ".yml");
		config = YamlConfiguration.loadConfiguration(file);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			foundationPrice = 0;
			expandPrice = 0;
			config.set("World", world);
			config.set("Config.foundationPrice", 0);
			config.set("Config.expandPrice", 0);
			townNames = new ArrayList<>();
			save();
		} else {
			townNames = config.getStringList("TownNames");
			foundationPrice = config.getDouble("Config.foundationPrice");
			expandPrice = config.getDouble("Config.expandPrice");
		}
	}

	/**
	 * <p>
	 * Delete the save file.
	 * <p>
	 */
	public void delete() {
		file.delete();
		despawnAllTownVillagers();
		for (EconomyPlayer ecoPlayer : EconomyPlayer.getAllEconomyPlayers()) {
			if (!ecoPlayer.getJoinedTownList().isEmpty()) {
				for (String townName : townNames) {
					if (ecoPlayer.getJoinedTownList().contains(townName)) {
						try {
							ecoPlayer.removeJoinedTown(townName);
						} catch (PlayerException e) {
						}
					}
				}
			}
		}
		for (String townName : townNames) {
			Town.getTownNameList().remove(townName);
		}
	}
	
	/**
	 * Renames a town.
	 * 
	 * @param player have to be the townowner
	 * @param oldName
	 * @param newName
	 * @throws TownSystemException
	 * @throws PlayerException 
	 */
	public void renameTown(String player,String oldName, String newName) throws TownSystemException, PlayerException {
		if(!Town.getTownNameList().contains(oldName)) {
			throw new TownSystemException(TownSystemException.TOWN_DOES_NOT_EXIST);
		} else {
			Town town = getTownByName(oldName);
			if(!town.isTownOwner(player)) {
				throw new TownSystemException(TownSystemException.YOU_ARE_NOT_OWNER);
			} else {
				town.renameTown(file, newName);
				config = YamlConfiguration.loadConfiguration(file);
				townNames.remove(oldName);
				townNames.add(newName);
				config.set("TownNames", townNames);
				save();
			}
		}
	}

	/**
	 * <p>
	 * Despawns all town villagers in this townworld.
	 * <p>
	 */
	private void despawnAllTownVillagers() {
		for (Town town : towns) {
			town.despawnAllVillagers();
		}
	}

	/**
	 * <p>
	 * Returns a list of all townnames in this townworld.
	 * <p>
	 * 
	 * @return List
	 */
	public List<String> getTownNameList() {
		return townNames;
	}

	/**
	 * <p>
	 * Returns the founding price of this townworld.
	 * <p>
	 * 
	 * @return
	 */
	public double getFoundationPrice() {
		return foundationPrice;
	}

	/**
	 * <p>
	 * Set the town list.
	 * <p>
	 * 
	 * @param towns
	 */
	public void setTownList(List<Town> towns) {
		this.towns.addAll(towns);
	}

	/**
	 * <p>
	 * Set the FoundationPrice for a town in this townworld. Set 'saving' true if
	 * the value should be saved in the file.
	 * <p>
	 * 
	 * @param foundationPrice
	 * @param saving
	 */
	public void setFoundationPrice(double foundationPrice, boolean saving) {
		this.foundationPrice = foundationPrice;
		if (saving) {
			config = YamlConfiguration.loadConfiguration(file);
			config.set("Config.foundationPrice", foundationPrice);
			save();
		}
	}

	/**
	 * <p>
	 * Returns the expand price for a town in this townworld.
	 * <p>
	 * 
	 * @return double
	 */
	public double getExpandPrice() {
		return expandPrice;
	}

	/**
	 * <p>
	 * Set the ExpandPrice for a town in this townworld. Set 'saving' true if the
	 * value should be saved in the file.
	 * <p>
	 * 
	 * @param expandPrice
	 * @param saving
	 */
	public void setExpandPrice(double expandPrice, boolean saving) {
		this.expandPrice = expandPrice;
		if (saving) {
			config = YamlConfiguration.loadConfiguration(file);
			config.set("Config.expandPrice", expandPrice);
			save();
		}
	}

	/**
	 * <p>
	 * Returns the Town World name.
	 * <p>
	 * 
	 * @return String
	 */
	public String getWorldName() {
		return worldName;
	}

	/**
	 * Creates a new town if player has enough money. Player money decreases if
	 * player has enough money.
	 * 
	 * @param townName
	 * @param chunk
	 * @param owner
	 * @throws TownSystemException
	 * @throws PlayerException
	 */
	public void createTown(String townName, Location location, EconomyPlayer owner)
			throws TownSystemException, PlayerException {
		config = YamlConfiguration.loadConfiguration(file);
		if (Town.getTownNameList().contains(townName)) {
			throw new TownSystemException(TownSystemException.TOWN_ALREADY_EXIST);
		} else if (!chunkIsFree(location.getChunk())) {
			throw new TownSystemException(TownSystemException.CHUNK_ALREADY_CLAIMED);
		} else if (!owner.hasEnoughtMoney(foundationPrice)) {
			throw new PlayerException(PlayerException.NOT_ENOUGH_MONEY_PERSONAL);
		} else if (owner.reachedMaxJoinedTowns()) {
			throw new PlayerException(PlayerException.MAX_JOINED_TOWNS);
		} else {
			Town town = new Town(file, owner.getName(), townName, location);
			file = town.getFile();
			towns.add(town);
			config = YamlConfiguration.loadConfiguration(file);
			townNames.add(townName);
			config.set("TownNames", townNames);
			save();
			owner.addJoinedTown(townName);
			owner.decreasePlayerAmount(foundationPrice, true);
		}
	}

	/**
	 * Dissolves a hole town and resets the chunks.
	 * 
	 * @param townname
	 * @param playername
	 * @throws TownSystemException
	 * @throws PlayerException
	 */
	public void dissolveTown(String townname, String playername) throws TownSystemException, PlayerException {
		if (!Town.getTownNameList().contains(townname)) {
			throw new TownSystemException(TownSystemException.TOWN_DOES_NOT_EXIST);
		} else {
			Town town = getTownByName(townname);
			if (town.isTownOwner(playername)) {
				List<String> tList = new ArrayList<>();
				tList.addAll(town.getCitizens());
				for (String citizen : tList) {
					EconomyPlayer.getEconomyPlayerByName(citizen).removeJoinedTown(townname);
				}
				file = town.deleteTown(file);
				towns.remove(town);
				townNames.remove(townname);
				config.set("Towns." + townname, null);
				config.set("TownNames", townNames);
				save();
			} else {
				throw new TownSystemException(TownSystemException.PLAYER_HAS_NO_PERMISSION);
			}

		}
	}

	/**
	 * <p>
	 * Expands a town by a chunk
	 * <p>
	 * 
	 * @param townname
	 * @param chunk
	 * @param player
	 *            (playername who executed this method)
	 * @throws TownSystemException
	 */
	public void expandTown(String townname, Chunk chunk, String player)
			throws TownSystemException {
		config = YamlConfiguration.loadConfiguration(file);
		if (!townNames.contains(townname)) {
			throw new TownSystemException(TownSystemException.TOWN_DOES_NOT_EXIST);
		} else if (!chunkIsFree(chunk)) {
			throw new TownSystemException(TownSystemException.CHUNK_ALREADY_CLAIMED);
		} else {
			Town town = getTownByName(townname);
			if (!town.chunkIsConnectedToTown(chunk.getX(), chunk.getZ())) {
				throw new TownSystemException(TownSystemException.CHUNK_IS_NOT_CONNECTED_WITH_TOWN);
			} else if (!town.hasCoOwnerPermission(player)) {
				throw new TownSystemException(TownSystemException.PLAYER_HAS_NO_PERMISSION);
			} else {
				town.decreaseTownBankAmount(file, this.getExpandPrice());
				file = town.addChunk(file, chunk.getX(), chunk.getZ(), player);
			}
		}
	}

	/**
	 * Joins a player to a town.
	 * 
	 * @param ecoPlayer
	 * @param town
	 * @throws PlayerException
	 * @throws TownSystemException
	 */
	public void joinTown(EconomyPlayer ecoPlayer, Town town) throws PlayerException, TownSystemException {
		if (ecoPlayer.reachedMaxJoinedTowns()) {
			throw new PlayerException(PlayerException.MAX_JOINED_TOWNS);
		} else {
			file = town.addCitizen(file, ecoPlayer.getName());
			ecoPlayer.addJoinedTown(town.getTownName());
		}
	}

	/**
	 * Leaves a player from a town.
	 * 
	 * @param ecoPlayer
	 * @return File
	 * @throws TownSystemException
	 * @throws PlayerException
	 */
	public void leaveTown(EconomyPlayer ecoPlayer, Town town) throws TownSystemException, PlayerException {
		file = town.removeCitizen(file, ecoPlayer.getName());
		ecoPlayer.removeJoinedTown(town.getTownName());
	}

	/**
	 * <p>
	 * Returns a town by townname.
	 * <p>
	 * 
	 * @param townName
	 * @return Town
	 * @throws TownSystemException
	 */
	public Town getTownByName(String townName) throws TownSystemException {
		Town t = null;
		for (Town town : towns) {
			if (town.getTownName().equals(townName)) {
				t = town;
			}
		}
		if (t != null) {
			return t;
		} else {
			throw new TownSystemException(TownSystemException.TOWN_DOES_NOT_EXIST);
		}
	}

	/**
	 * <p>
	 * Returns true if the chunk is not claimed by any town
	 * <p>
	 * 
	 * @param chunk
	 * @return boolean
	 */
	public boolean chunkIsFree(Chunk chunk) {
		boolean isFree = true;
		config = YamlConfiguration.loadConfiguration(file);
		String chunkCoords = chunk.getX() + "/" + chunk.getZ();
		for (String name : townNames) {
			if (config.getStringList("Towns." + name + ".chunks").contains(chunkCoords)) {
				isFree = false;
				break;
			}
		}
		return isFree;
	}

	/**
	 * <p>
	 * Returns the townlist.
	 * <p>
	 * 
	 * @return List
	 */
	public List<Town> getTownList() {
		return towns;
	}

	/**
	 * <p>
	 * Returns town by chunk.
	 * <p>
	 * 
	 * @param chunk
	 * @return Town
	 * @throws TownSystemException
	 */
	public Town getTownByChunk(Chunk chunk) throws TownSystemException {
		config = YamlConfiguration.loadConfiguration(file);
		for (Town town : towns) {
			if (town.isClaimedByTown(chunk)) {
				return town;
			}
		}
		throw new TownSystemException(TownSystemException.CHUNK_NOT_CLAIMED);
	}

	/**
	 * <p>
	 * Returns the savefile of this townworld.
	 * <p>
	 * 
	 * @return File
	 */
	public File getSaveFile() {
		return file;
	}

	/**
	 * <p>
	 * Set townworld save file.
	 * <p>
	 */
	public void setSaveFile(File file) {
		this.file = file;
	}

	private void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles clicks in a town villager. Handles payment too.
	 * 
	 * @param e
	 * @throws TownSystemException
	 * @throws PlayerException
	 */
	public void handleTownVillagerInvClick(InventoryClickEvent e) throws TownSystemException, PlayerException {
		Chunk chunk = e.getWhoClicked().getLocation().getChunk();
		String playerName = e.getWhoClicked().getName();
		EconomyPlayer ecoPlayer = EconomyPlayer.getEconomyPlayerByName(playerName);
		Town town = getTownByChunk(chunk);
		Plot plot = town.getPlotByChunkCoords(chunk.getX() + "/" + chunk.getZ());
		switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
			case "Buy":
				if (!ecoPlayer.hasEnoughtMoney(plot.getSalePrice())) {
					throw new PlayerException(PlayerException.NOT_ENOUGH_MONEY_PERSONAL);
				} else {
					String receiverName = plot.getOwner();
					Bukkit.getLogger().info(receiverName);
					EconomyPlayer receiver = EconomyPlayer.getEconomyPlayerByName(receiverName);
					ecoPlayer.payToOtherPlayer(receiver, plot.getSalePrice());
					file = town.buyPlot(file, playerName, chunk.getX(), chunk.getZ());
					e.getWhoClicked().sendMessage(ChatColor.GOLD + "Congratulation! You bought this plot!");
				}
				break;
			case "Cancel Sale":
				if (plot.isOwner(playerName)) {
					file = town.removePlotFromSale(file, chunk.getX(), chunk.getZ(), playerName);
					e.getWhoClicked().sendMessage(ChatColor.GOLD + "You removed this plot from sale!");
				}
				break;
			case "Join":
				joinTown(ecoPlayer, town);
				e.getWhoClicked().sendMessage(ChatColor.GOLD + "You joined the town " + town.getTownName() + ".");
				break;
			case "Leave":
				leaveTown(ecoPlayer, town);
				e.getWhoClicked().sendMessage(ChatColor.GOLD + "You left the town " + town.getTownName() + ".");
				break;
		}
	}

	/**
	 * This method returns a townworld by it's name,
	 * 
	 * @param name
	 * @return TownWorld
	 * @throws TownSystemException
	 */
	public static TownWorld getTownWorldByName(String name) throws TownSystemException {
		for (TownWorld townWorld : townWorldList) {
			if (townWorld.getWorldName().equals(name)) {
				return townWorld;
			}
		}
		throw new TownSystemException(TownSystemException.TOWNWORLD_DOES_NOT_EXIST);
	}

	/**
	 * This method returns a list of all townworlds.
	 * 
	 * @return List of TownWorlds
	 */
	public static List<TownWorld> getTownWorldList() {
		return townWorldList;
	}

	/**
	 * This method despawns all town villager in this townworld.
	 */
	public static void despawnAllVillagers() {
		for (TownWorld townWorld : townWorldList) {
			townWorld.despawnAllTownVillagers();
		}
	}

	public static List<String> getTownWorldNameList() {
		List<String> nameList = new ArrayList<>();
		for (TownWorld townWorld : townWorldList) {
			nameList.add(townWorld.getWorldName());
		}
		return nameList;
	}

	/**
	 * This method returns true, if the world is an townworld.
	 * 
	 * @param worldName
	 * @return boolean
	 */
	public static boolean isTownWorld(String worldName) {
		if (getTownWorldNameList().contains(worldName)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void handleTownWorldLocationCheck(String worldname, Chunk chunk, String playername) {
		try {
			BossBar bossbar = EconomyPlayer.getEconomyPlayerByName(playername).getBossBar();
			try {
				TownWorld townWorld = TownWorld.getTownWorldByName(worldname);
				try {
					Town town = townWorld.getTownByChunk(chunk);
					bossbar.setTitle(town.getTownName());
					bossbar.setColor(BarColor.RED);
					bossbar.setVisible(true);
				} catch (TownSystemException e1) {
					// if chunk is in the wilderness
					bossbar.setTitle("Wilderness");
					bossbar.setColor(BarColor.GREEN);
					bossbar.setVisible(true);
				}
			} catch (TownSystemException e) {
				// disable bossbar in other worlds
				bossbar.setVisible(false);
			}
		} catch (PlayerException e2) {
			// should never happen
		}
	}


	/**
	 * This method should be used to create/enble a new townworld.
	 * 
	 * @param mainDataFolder
	 * @param world
	 * @throws TownSystemException
	 */
	public static void createTownWorld(File mainDataFolder, String world) throws TownSystemException {
		if (Bukkit.getWorld(world) == null) {
			throw new TownSystemException(TownSystemException.WORLD_DOES_NOT_EXIST);
		} else if (isTownWorld(world)) {
			throw new TownSystemException(TownSystemException.TOWNWORLD_ALREADY_EXIST);
		} else {
			townWorldList.add(new TownWorld(mainDataFolder, world));
		}
	}

	/**
	 * This method should be used to delete/disable a townworld.
	 * 
	 * @param world
	 * @throws TownSystemException
	 */
	public static void deleteTownWorld(String world) throws TownSystemException {
		if (Bukkit.getWorld(world) == null) {
			throw new TownSystemException(TownSystemException.WORLD_DOES_NOT_EXIST);
		} else {
			TownWorld townWorld = getTownWorldByName(world);
			townWorldList.remove(townWorld);
			townWorld.delete();
		}
	}

	/**
	 * <p>
	 * This method loads all townworlds from the save file.
	 * <p>
	 * 
	 * @param main
	 * @param worldname
	 *            the townworld has to exist!
	 * @throws TownSystemException
	 */
	public static void loadAllTownWorlds(File mainDataFolder, FileConfiguration fileConfig,Server server) throws TownSystemException {
		for (String townWorldName : fileConfig.getStringList("TownWorlds")) {
			TownWorld townWorld = new TownWorld(mainDataFolder, townWorldName);
			List<Town> towns = new ArrayList<>();
			for (String townName : townWorld.getTownNameList()) {
				towns.add(Town.loadTown(townWorld.getSaveFile(),server, townName));
			}
			townWorld.setTownList(towns);
			townWorldList.add(townWorld);
		}
	}
}
