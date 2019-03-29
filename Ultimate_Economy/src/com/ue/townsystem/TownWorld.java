package com.ue.townsystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.utils.LimitationUtils;
import com.ue.utils.PaymentUtils;
import com.ue.exceptions.banksystem.PlayerDoesNotExistException;
import com.ue.exceptions.banksystem.PlayerHasNotEnoughtMoneyException;
import com.ue.exceptions.townsystem.ChunkAlreadyClaimedException;
import com.ue.exceptions.townsystem.ChunkNotClaimedByThisTownException;
import com.ue.exceptions.townsystem.PlayerReachedMaxJoinedTownsException;
import com.ue.exceptions.townsystem.TownAlreadyExistsException;
import com.ue.exceptions.townsystem.TownDoesNotExistException;

import ultimate_economy.Ultimate_Economy;

public class TownWorld {

	private double foundationPrice,expandPrice;
	private final String worldName;
	private File file;
	private FileConfiguration config;
	private List<String> townNames;
	private List<Town> towns;
	
	public TownWorld(Ultimate_Economy main,String world) {
		worldName = world;
		towns = new ArrayList<>();
		file = new File(main.getDataFolder() , world + "_TownWorld" + ".yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		config = YamlConfiguration.loadConfiguration(file);
		foundationPrice = 0;
		expandPrice = 0;
		config.set("World", world);
		config.set("Config.foundationPrice", 0);
		config.set("Config.expandPrice", 0);
		townNames = new ArrayList<>();
		save();
		}
		else {
			config = YamlConfiguration.loadConfiguration(file);
			townNames = config.getStringList("TownList");
			foundationPrice = config.getDouble("Config.foundationPrice");
			expandPrice = config.getDouble("Config.expandPrice");
			save();
		}
	}
	public void delete() {
		file.delete();
	}
	public double getFoundationPrice() {
		return foundationPrice;
	}
	public void setFoundationPrice(double foundationPrice) {
		this.foundationPrice = foundationPrice;
		config = YamlConfiguration.loadConfiguration(file);
		config.set("Config.foundationPrice", foundationPrice);
		save();
	}
	public double getExpandPrice() {
		return expandPrice;
	}
	public void setExpandPrice(double expandPrice) {
		this.expandPrice = expandPrice;
		config = YamlConfiguration.loadConfiguration(file);
		config.set("Config.expandPrice", expandPrice);
		save();
	}
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
	 * @throws TownAlreadyExistsException
	 * @throws ChunkAlreadyClaimedException
	 * @throws PlayerDoesNotExistException 
	 * @throws PlayerHasNotEnoughtMoneyException 
	 * @throws PlayerReachedMaxJoinedTownsException 
	 */
	public void createTown(File playerfile,FileConfiguration configFile,String townName,Chunk chunk,String owner) throws TownAlreadyExistsException, ChunkAlreadyClaimedException, PlayerDoesNotExistException, PlayerHasNotEnoughtMoneyException, PlayerReachedMaxJoinedTownsException {
		config = YamlConfiguration.loadConfiguration(file);
		if(townNames.contains(townName) ) {
			throw new TownAlreadyExistsException(townName);
		}
		else if(!chunkIsFree(chunk)) {
			throw new ChunkAlreadyClaimedException(chunk.getX() + "/" + chunk.getZ());
		}
		else if(!PaymentUtils.playerHasEnoughtMoney(playerfile, owner, this.getFoundationPrice())) {
			throw new PlayerHasNotEnoughtMoneyException(owner,true);
		}
		else if(LimitationUtils.playerReachedMaxTowns(playerfile, configFile, owner)) {
			throw new PlayerReachedMaxJoinedTownsException(owner);
		}
		else {
			Town town = new Town(file, owner, townName, chunk);
			towns.add(town);
			config = YamlConfiguration.loadConfiguration(file);
			townNames.add(townName);
			config.set("TownNames", townNames);
			save();
			FileConfiguration pf = YamlConfiguration.loadConfiguration(playerfile);
			List<String> list = pf.getStringList(owner + ".joinedTowns");
			list.add(townName);
			pf.set(owner + ".joinedTowns", list);
			save(playerfile, pf);
			PaymentUtils.decreasePlayerAmount(playerfile, owner, this.getFoundationPrice());
		}
	}
	/**
	 * <p>
	 * Dissolves a hole town and resets the chunks.
	 * <p>
	 * @param townname
	 * @throws TownDoesNotExistException
	 * @throws NumberFormatException
	 * @throws ChunkNotClaimedByThisTownException
	 */
	public void dissolveTown(String townname) throws TownDoesNotExistException, NumberFormatException, ChunkNotClaimedByThisTownException {
		//TODO expand with payment
		config = YamlConfiguration.loadConfiguration(file);
		if(!townNames.contains(townname) ) {
			throw new TownDoesNotExistException(townname);
		}
		else {
			for(Town town: towns) {
				if(town.getTownName().equals(townname)) {
					for(String coords:town.getChunkList()) {
						town.removeChunk(file, Integer.valueOf(coords.substring(0,coords.indexOf("/"))), Integer.valueOf(coords.substring(coords.indexOf("/")+1)),Bukkit.getWorld(worldName));
					}
					break;
				}
			}
		}
	}
	
	/**
	 * <p>
	 * Expands a town by a chunk
	 * <p>
	 * @param townname
	 * @param chunk
	 * @throws ChunkAlreadyClaimedException
	 * @throws TownDoesNotExistException
	 */
	public void expandTown(String townname,Chunk chunk) throws ChunkAlreadyClaimedException, TownDoesNotExistException {
		//TODO expand with payment
		config = YamlConfiguration.loadConfiguration(file);
		if(!townNames.contains(townname)) {
			throw new TownDoesNotExistException(townname);
		}
		else if(!chunkIsFree(chunk)) {
			throw new ChunkAlreadyClaimedException(chunk.getX() + "/" + chunk.getZ());
		}
		else {
			for(Town town: towns) {
				if(town.getTownName().equals(townname)) {
					town.addChunk(file, chunk.getX(),chunk.getZ());
					break;
				}
			}
		}
	}
	
	public Town getTownByName(String townName) {
		//TODO
		return null;
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
	 * Get town name by chunk. Returns null if chunk is not claimed by any town.
	 * <p>
	 * @param chunk
	 * @return String
	 */
	public String getTownNameByChunk(Chunk chunk) {
		config = YamlConfiguration.loadConfiguration(file);
		String townname = null;
		for(Town town:towns) {
			if(town.isClaimedByTown(chunk)) {
				townname = town.getTownName();
				break;
			}
		}
		return townname;
	}
	/**
	 * <p>
	 * Returns true if player is a citizen of this town.
	 * <p>
	 * @param townname
	 * @param playername
	 * @return boolean
	 */
	public boolean isPlayerCitizen(String townname,String playername) {
		config = YamlConfiguration.loadConfiguration(file);
		boolean is = false;
		if(config.getStringList("Towns." + townname + ".citizens").contains(playername)) {
			is = true;
		}
		return is;
	}
	public static TownWorld loadTownWorld(String name) {
		//TODO
		return null;
	}
	private void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void save(File file,FileConfiguration config) {
		try {
			config = YamlConfiguration.loadConfiguration(file);
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
