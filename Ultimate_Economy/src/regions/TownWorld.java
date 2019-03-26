package regions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.exceptions.Town.ChunkAlreadyClaimedException;
import com.ue.exceptions.Town.TownAlreadyExistsException;
import com.ue.exceptions.Town.TownDoesNotExistException;

import ultimate_economy.Ultimate_Economy;

public class TownWorld {

	private double foundationPrice,expandPrice;
	private static String worldName;
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
	 * Creates a new town
	 * <p>
	 * @param name
	 * @param chunk
	 * @param owner
	 * @throws TownAlreadyExistsException
	 * @throws ChunkAlreadyClaimedException
	 */
	public void createTown(String name,Chunk chunk,String owner) throws TownAlreadyExistsException, ChunkAlreadyClaimedException {
		//handle town exists
		config = YamlConfiguration.loadConfiguration(file);
		if(townNames.contains(name) ) {
			throw new TownAlreadyExistsException(name);
		}
		else if(!chunkIsFree(chunk)) {
			throw new ChunkAlreadyClaimedException(chunk.getX() + "/" + chunk.getZ());
		}
		else {
			Town town = new Town(file, owner, name, chunk);
			towns.add(town);
			file = town.getFile();
			config = YamlConfiguration.loadConfiguration(file);
			townNames.add(name);
			config.set("TownNames", townNames);
			save();
		}
	}
	public void dissolveTown(String name) {
		config = YamlConfiguration.loadConfiguration(file);
		//TODO
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
					town.addChunk(file, chunk);
					break;
				}
			}
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
	 * Get town name by chunk. Returns null if chunk is not claimed by any town.
	 * <p>
	 * @param chunk
	 * @return
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
	public boolean isPlayerCitizen(String townname,String playername) {
		config = YamlConfiguration.loadConfiguration(file);
		boolean is = false;
		if(config.getStringList("Towns." + townname + ".citizens").contains(playername)) {
			is = true;
		}
		return is;
	}
	public static void loadTownWorld(String name) {
		//TODO
	}
	private void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
