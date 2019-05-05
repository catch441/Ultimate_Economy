package com.ue.townsystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.ue.utils.LimitationUtils;
import com.ue.utils.PaymentUtils;
import com.ue.exceptions.TownSystemException;
import com.ue.exceptions.banksystem.PlayerDoesNotExistException;
import com.ue.exceptions.banksystem.PlayerHasNotEnoughtMoneyException;
import com.ue.exceptions.banksystem.TownHasNotEnoughMoneyException;

public class TownWorld {
	
	private static List<TownWorld> townWorldList = new ArrayList<>();

	private double foundationPrice,expandPrice;
	private final String worldName;
	private File file;
	private FileConfiguration config;
	private List<String> townNames;
	private List<Town> towns;
	
	/**
	 * <p>
	 * Represents a townworld.
	 * <p>
	 * @param main
	 * @param world
	 */
	private TownWorld(File mainDataFolder,String world) {
		worldName = world;
		towns = new ArrayList<>();
		file = new File(mainDataFolder , world + "_TownWorld" + ".yml");
		config = YamlConfiguration.loadConfiguration(file);
		if(!file.exists()) {
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
		}
		else {
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
	}
	
	/**
	 * <p>
	 * Despawns all town villagers in this townworld.
	 * <p>
	 */
	private void despawnAllTownVillagers() {
		for(Town town:towns) {
			town.despawnAllVillagers();
		}
	}
	
	/**
	 * <p>
	 * Returns a list of all townnames in this townworld.
	 * <p>
	 * @return List
	 */
	public List<String> getTownNameList() {
		return townNames;
	}
	
	/**
	 * <p>
	 * Returns the founding price of this townworld.
	 * <p>
	 * @return
	 */
	public double getFoundationPrice() {
		return foundationPrice;
	}
	
	/**
	 * <p>
	 * Set the town list.
	 * <p>
	 * @param towns
	 */
	public void setTownList(List<Town> towns) {
		this.towns.addAll(towns);
	}
	
	/**
	 * <p>
	 * Set the FoundationPrice for a town in this townworld.
	 * Set 'saving' true if the value should be saved in the file.
	 * <p>
	 * @param foundationPrice
	 * @param saving
	 */
	public void setFoundationPrice(double foundationPrice,boolean saving) {
		this.foundationPrice = foundationPrice;
		if(saving) {
			config = YamlConfiguration.loadConfiguration(file);
			config.set("Config.foundationPrice", foundationPrice);
			save();
		}
	}
	
	/**
	 * <p>
	 * Returns the expand price for a town in this townworld.
	 * <p>
	 * @return double
	 */
	public double getExpandPrice() {
		return expandPrice;
	}
	
	/**
	 * <p>
	 * Set the ExpandPrice for a town in this townworld.
	 * Set 'saving' true if the value should be saved in the file.
	 * <p>
	 * @param expandPrice
	 * @param saving
	 */
	public void setExpandPrice(double expandPrice,boolean saving) {
		this.expandPrice = expandPrice;
		if(saving) {
			config = YamlConfiguration.loadConfiguration(file);
			config.set("Config.expandPrice", expandPrice);
			save();
		}
	}
	
	
	/**
	 * <p>
	 * Returns the Town World name.
	 * <p>
	 * @return String
	 */
	public String getWorldName() {
		return worldName;
	}
	/**
	 * <p>
	 * Creates a new town if player has enough money. Player money decreases if player has enough money.
	 * <p>
	 * @param townName
	 * @param chunk
	 * @param owner
	 * @param playerfile
	 * @return file (playerfile)
	 * @throws TownSystemException 
	 * @throws PlayerHasNotEnoughtMoneyException 
	 * @throws PlayerDoesNotExistException 
	 */
	public File createTown(File playerfile,FileConfiguration configFile,String townName,Location location,String owner) throws TownSystemException, PlayerHasNotEnoughtMoneyException, PlayerDoesNotExistException {
		config = YamlConfiguration.loadConfiguration(file);
		if(townNames.contains(townName) ) {
			throw new TownSystemException(TownSystemException.TOWN_ALREADY_EXISTS);
		}
		else if(!chunkIsFree(location.getChunk())) {
			throw new TownSystemException(TownSystemException.CHUNK_ALREADY_CLAIMED);
		}
		else if(!PaymentUtils.playerHasEnoughtMoney(playerfile, owner, this.getFoundationPrice())) {
			throw new PlayerHasNotEnoughtMoneyException(owner,true);
		}
		else if(LimitationUtils.playerReachedMaxTowns(playerfile, configFile, owner)) {
			throw new TownSystemException(TownSystemException.PLAYER_REACHED_MAX_JOINED_TOWNS);
		}
		else {
			Town town = new Town(file, owner, townName, location);
			file = town.getFile();
			towns.add(town);
			config = YamlConfiguration.loadConfiguration(file);
			townNames.add(townName);
			config.set("TownNames", townNames);
			save();
			FileConfiguration pf = YamlConfiguration.loadConfiguration(playerfile);
			List<String> list = pf.getStringList(owner + ".joinedTowns");
			list.add(townName);
			pf.set(owner + ".joinedTowns", list);
			playerfile = save(playerfile, pf);
			return PaymentUtils.decreasePlayerAmount(playerfile, owner, this.getFoundationPrice(),true);
		}
	}
	/**
	 * <p>
	 * Dissolves a hole town and resets the chunks.
	 * <p>
	 * @param playerfile
	 * @param townname
	 * @param playername
	 * @return file (playerfile)
	 * @throws TownSystemException 
	 */
	public File dissolveTown(File playerfile,String townname,String playername) throws TownSystemException {
		if(!townNames.contains(townname) ) {
			throw new TownSystemException(TownSystemException.TOWN_DOES_NOT_EXISTS);
		}
		else {
			Town town = getTownByName(townname);
			if(town.isTownOwner(playername)) {
				FileConfiguration c = YamlConfiguration.loadConfiguration(playerfile);
				List<String> tList = new ArrayList<>();
				tList.addAll(town.getCitizens());
				for(String citizen:tList) {
					List<String> list = c.getStringList(citizen + ".joinedTowns");
					list.remove(townname);
					c.set(citizen + ".joinedTowns", list);
				}
				file = town.deleteTown(file);
				towns.remove(town);
				townNames.remove(townname);
				config.set("TownNames", townNames);
				save();
				return save(playerfile, c);
			}
			else {
				throw new TownSystemException(TownSystemException.PLAYER_HAS_NO_PERMISSION);
			}
			
		}
	}
	
	/**
	 * <p>
	 * Expands a town by a chunk
	 * <p>
	 * @param townname
	 * @param chunk
	 * @param player (playername who executed this method)
	 * @throws TownSystemException 
	 * @throws TownHasNotEnoughMoneyException 
	 */
	public void expandTown(String townname,Chunk chunk,String player) throws TownSystemException, TownHasNotEnoughMoneyException {
		config = YamlConfiguration.loadConfiguration(file);
		if(!townNames.contains(townname)) {
			throw new TownSystemException(TownSystemException.TOWN_ALREADY_EXISTS);
		}
		else if(!chunkIsFree(chunk)) {
			throw new TownSystemException(TownSystemException.CHUNK_ALREADY_CLAIMED);
		}
		else {
			Town town = getTownByName(townname);
			if(!town.chunkIsConnectedToTown(chunk.getX(), chunk.getZ())) {
				throw new TownSystemException(TownSystemException.CHUNK_IS_NOT_CONNECTED_WITH_TOWN);
			}
			else if(!town.hasCoOwnerPermission(player)) {
				throw new TownSystemException(TownSystemException.PLAYER_HAS_NO_PERMISSION);
			}
			else {
				town.decreaseTownBankAmount(file, this.getExpandPrice());
				file = town.addChunk(file, chunk.getX(),chunk.getZ(),player);
			}
		}
	}
	
	/**
	 * <p>
	 * Joins a player to a town.
	 * <p>
	 * @param playerfile
	 * @param configFile
	 * @param player
	 * @return File
	 * @throws TownSystemException
	 * @throws PlayerDoesNotExistException
	 */
	public File joinTown(File playerfile,FileConfiguration configFile,Player player) throws TownSystemException, PlayerDoesNotExistException {
		if(LimitationUtils.playerReachedMaxTowns(playerfile, configFile, player.getName())) {
			throw new TownSystemException(TownSystemException.PLAYER_REACHED_MAX_JOINED_TOWNS);
		}
		else {
			Town town = getTownByChunk(player.getLocation().getChunk());
			file = town.addCitizen(file, player.getName());
			FileConfiguration pf = YamlConfiguration.loadConfiguration(playerfile);
			List<String> list = pf.getStringList(player.getName() + ".joinedTowns");
			list.add(town.getTownName());
			pf.set(player.getName() + ".joinedTowns", list);
			return save(playerfile, pf);
		}
	}
	
	/**
	 * <p>
	 * Leaves a player from a town.
	 * <p>
	 * @param playerfile
	 * @param configFile
	 * @param player
	 * @return File
	 * @throws TownSystemException
	 */
	public File leaveTown(File playerfile,FileConfiguration configFile,Player player) throws TownSystemException {
		Town town = getTownByChunk(player.getLocation().getChunk());
		file = town.removeCitizen(file, player.getName());
		FileConfiguration pf = YamlConfiguration.loadConfiguration(playerfile);
		List<String> list = pf.getStringList(player.getName() + ".joinedTowns");
		list.remove(town.getTownName());
		pf.set(player.getName() + ".joinedTowns", list);
		return save(playerfile, pf);
	}
	
	/**
	 * <p>
	 * Returns a town by townname.
	 * <p>
	 * @param townName
	 * @return Town
	 * @throws TownSystemException 
	 */
	public Town getTownByName(String townName) throws TownSystemException {
		Town t = null;
		for(Town town:towns) {
			if(town.getTownName().equals(townName)) {
				t= town;
			}
		}
		if(t != null) {
			return t;
		}
		else {
			throw new TownSystemException(TownSystemException.TOWN_DOES_NOT_EXISTS);
		}
	}
	
	/**
	 * <p>
	 * Returns true if the chunk is not claimed by any town
	 * <p>
	 * @param chunk
	 * @return boolean
	 */
	public boolean chunkIsFree(Chunk chunk) {
		boolean isFree = true;
		config = YamlConfiguration.loadConfiguration(file);
		String chunkCoords = chunk.getX() + "/" + chunk.getZ();
		for(String name:townNames) {
			if(config.getStringList("Towns." + name + ".chunks").contains(chunkCoords)) {
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
	  * @return List
	  */
	public List<Town> getTownList() {
		return towns;
	}
	
	/**
	 * <p>
	 * Returns town by chunk.
	 * <p>
	 * @param chunk
	 * @return Town
	 * @throws TownSystemException 
	 */
	public Town getTownByChunk(Chunk chunk) throws TownSystemException {
		config = YamlConfiguration.loadConfiguration(file);
		for(Town town:towns) {
			if(town.isClaimedByTown(chunk)) {
				return town;
			}
		}
		throw new TownSystemException(TownSystemException.CHUNK_NOT_CLAIMED);
	}
	
	/**
	 * <p>
	 * Returns the savefile of this townworld.
	 * <p>
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
	 * <p>
	 * Handles clicks in a town villager.
	 * Handles payment too.
	 * <p>
	 * @param playerfile
	 * @param e
	 * @return file (playerfile)
	 * @throws PlayerHasNotEnoughtMoneyException 
	 * @throws PlayerDoesNotExistException 
	 * @throws TownSystemException 
	 */
	public File handleTownVillagerInvClick(File playerfile,FileConfiguration configFile,InventoryClickEvent e) throws PlayerHasNotEnoughtMoneyException, PlayerDoesNotExistException, TownSystemException {
		Chunk chunk = e.getWhoClicked().getLocation().getChunk();
		String playerName = e.getWhoClicked().getName();
		Town town = getTownByChunk(chunk);
		Plot plot = town.getPlotByChunkCoords(chunk.getX() + "/" + chunk.getZ());
		switch(e.getCurrentItem().getItemMeta().getDisplayName()) {
			case "Buy": 
				if(!PaymentUtils.playerHasEnoughtMoney(playerfile, playerName, plot.getSalePrice())){
					throw new PlayerHasNotEnoughtMoneyException(e.getWhoClicked().getName(),true);
				}
				else {
					String reciever = plot.getOwner();
					playerfile = PaymentUtils.payToOtherPlayer(playerfile, playerName, reciever, plot.getSalePrice(), true);
					file = town.buyPlot(file, playerName, chunk.getX(), chunk.getZ());
					if(Bukkit.getPlayer(reciever).isOnline()) {
						PaymentUtils.updateScoreBoard(playerfile,Bukkit.getPlayer(reciever));
					}
					e.getWhoClicked().sendMessage(ChatColor.GOLD + "Congratulation! You bought this plot!");
				}
				break;
			case "Cancel Sale":
				if(plot.isOwner(playerName)) {
					file = town.removePlotFromSale(file, chunk.getX(), chunk.getZ(), playerName);
					e.getWhoClicked().sendMessage(ChatColor.GOLD + "You removed this plot from sale!");
				}
				break;
			case "Join":
				playerfile = joinTown(playerfile, configFile,(Player) e.getWhoClicked());
				e.getWhoClicked().sendMessage(ChatColor.GOLD + "You joined the town " + town.getTownName() + ".");
				break;
			case "Leave":
				playerfile = leaveTown(playerfile, configFile, (Player) e.getWhoClicked());
				e.getWhoClicked().sendMessage(ChatColor.GOLD + "You left the town " + town.getTownName() + ".");
				break;
		}
		return playerfile;
	}
	
	private File save(File file,FileConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * This method returns a townworld by it's name,
	 * 
	 * @param name
	 * @return TownWorld
	 * @throws TownSystemException
	 */
	public static TownWorld getTownWorldByName(String name) throws TownSystemException {
		for(TownWorld townWorld:townWorldList) {
			if(townWorld.getWorldName().equals(name)) {
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
		for(TownWorld townWorld:townWorldList) {
			townWorld.despawnAllTownVillagers();
		}
	}
	
	private static List<String> getTownWorldNameList() {
		List<String> nameList = new ArrayList<>();
		for(TownWorld townWorld:townWorldList) {
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
		if(getTownWorldNameList().contains(worldName)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * This method should be used to create/enble a new townworld.
	 * 
	 * @param mainDataFolder
	 * @param world
	 * @throws TownSystemException
	 */
	public static void createTownWorld(File mainDataFolder,String world) throws TownSystemException {
		if(Bukkit.getWorld(world) == null) {
			throw new TownSystemException(TownSystemException.WORLD_DOES_NOT_EXIST);
		}
		else if(isTownWorld(world)) {
			throw new TownSystemException(TownSystemException.TOWNWORLD_ALREADY_EXIST);
		}
		else {
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
		if(Bukkit.getWorld(world) == null) {
			throw new TownSystemException(TownSystemException.WORLD_DOES_NOT_EXIST);
		}
		else {
			TownWorld townWorld = getTownWorldByName(world);
			townWorldList.remove(townWorld);
			townWorld.delete();
		}
	}
	
	/**
	 * <p>
	 * This method loads all townworlds from the save file.
	 * <p>
	 * @param main
	 * @param worldname the townworld has to exist!
	 * @throws TownSystemException 
	 */
	public static void loadAllTownWorlds(File mainDataFolder,FileConfiguration fileConfig) throws TownSystemException {
		for(String townWorldName:fileConfig.getStringList("TownNames")) {
			TownWorld townWorld = new TownWorld(mainDataFolder, townWorldName);
			List<Town> towns = new ArrayList<>();
			for(String townName: townWorld.getTownNameList()) {
				towns.add(Town.loadTown(townWorld.getSaveFile(), townName));
			}
			townWorld.setTownList(towns);
			townWorldList.add(townWorld);
		}
	}
}
