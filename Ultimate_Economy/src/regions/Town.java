package regions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.exceptions.town.ChunkAlreadyClaimedException;
import com.ue.exceptions.town.ChunkNotClaimedByThisTownException;
import com.ue.exceptions.town.PlayerIsAlreadyCitizenException;
import com.ue.exceptions.town.PlayerIsAlreadyCoOwnerException;
import com.ue.exceptions.town.PlayerIsNotCitizenException;

public class Town {

	private String townName;
	private String owner;
	private ArrayList<String> citizens,coOwners;
	private ArrayList<String> chunkCoords;
	private Location townSpawn;
	private File file;
	
	/**
	 * <p>
	 * Creates a town object.
	 * Only if you load a existing town, the parameter startChunk should be 'null'
	 * <p>
	 * @param file
	 * @param owner
	 * @param townName
	 * @param startChunk
	 */
	public Town(File file,String owner,String townName,Chunk startChunk) {
		this.townName = townName;
		this.owner = owner;
		citizens = new ArrayList<>();
		coOwners = new ArrayList<>();
		chunkCoords = new ArrayList<>();
		this.owner = owner;
		citizens.add(owner);
		chunkCoords.add(startChunk.getX() + "/" + startChunk.getZ());
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Towns." + townName + ".owner", owner);
		config.set("Towns." + townName + ".coOwners", coOwners);
		config.set("Towns." + townName + ".citizens", citizens);
		config.set("Towns." + townName + ".chunks", chunkCoords);
		if(startChunk != null) {
			Location spawn = new Location(startChunk.getWorld(), (startChunk.getX() << 4) + 7, 0, (startChunk.getZ() << 4) + 7);
			spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));
			config.set("Towns." + townName + ".townspawn", spawn.getX() + "/" + spawn.getY() + "/" + spawn.getZ());
		}
		save(file);
	}
	
	public void createTownManagerVillager() {
		//TODO
	}
	
	public void moveTownManagerVillager() {
		//TODO
	}
	
	public void despawnTownManagerVillager() {
		//TODO
	}
	
	public void removeTownManagerVillager() {
		//TODO
	}
	
	/**
	 * <p>
	 * Adds a chunk to a town
	 * <p>
	 * @param file
	 * @param chunk
	 * @throws ChunkAlreadyClaimedException 
	 */
	public void addChunk(File file,int chunkX,int chunkZ) throws ChunkAlreadyClaimedException {
		String coords = chunkX + "/" + chunkZ;
		if(!chunkCoords.contains(coords)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			chunkCoords.add(coords);
			config.set("Towns." + townName + ".chunks", chunkCoords);
			save(file);
		}
		else {
			throw new ChunkAlreadyClaimedException(coords);
		}
	}
	
	/**
	 * <p>
	 * Removes a chunk from a town
	 * <p>
	 * @param file
	 * @param chunk
	 * @throws ChunkNotClaimedByThisTownException 
	 */
	@SuppressWarnings("deprecation")
	public void removeChunk(File file,int chunkX,int chunkZ,World world) throws ChunkNotClaimedByThisTownException {
		String coords = chunkX + "/" + chunkZ;
		if(chunkCoords.contains(coords)) {
			chunkCoords.remove(coords);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Towns." + townName + ".chunks", chunkCoords);
			save(file);
			//TODO not for future, find a better solution
			world.regenerateChunk(chunkX, chunkZ);
			world.save();
		}
		else {
			throw new ChunkNotClaimedByThisTownException(coords);
		}
	}
	
	/**
	 * <p>
	 * Get town owner
	 * <p>
	 * @return String
	 */
	public String getOwner() {
		return owner;
	}
	
	/**
	 * <p>
	 * Set town owner
	 * <p>
	 * @param file
	 * @param owner
	 */
	public void setOwner(File file,String owner) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		this.owner = owner;
		config.set("Towns." + townName + ".owner", owner);
		save(file);
	}
	
	/**
	 * <p>
	 * Get list of citizens
	 * <p>
	 * @return ArrayList
	 */
	public ArrayList<String> getCitizens() {
		return citizens;
	}
	/**
	 * <p>
	 * Set all citizens.
	 * <p>
	 * @param citizens
	 */
	public void setCiticens(List<String> citizens) {
		this.citizens.addAll(citizens);
	}
	
	/**
	 * <p>
	 * Add a player as citizen to a town
	 * <p>
	 * @param file
	 * @param newCitizen
	 * @throws PlayerIsAlreadyCitizenException 
	 */
	public void addCitizen(File file,String newCitizen) throws PlayerIsAlreadyCitizenException {
		if(!citizens.contains(newCitizen)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			citizens.add(newCitizen);
			config.set("Towns." + townName + ".citizens", citizens);
			save(file);
		}
		else {
			throw new PlayerIsAlreadyCitizenException(newCitizen);
		}
	}
	
	/**
	 * <p>
	 * Remove a player as citizen from a town
	 * <p>
	 * @param file
	 * @param citizen
	 * @throws PlayerIsNotCitizenException 
	 */
	public void removeCitizen(File file,String citizen) throws PlayerIsNotCitizenException {
		if(citizens.contains(citizen)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			citizens.remove(citizen);
			config.set("Towns." + townName + ".citizens", citizens);
			save(file);
		}
		else {
			throw new PlayerIsNotCitizenException(citizen);
		}
	}
	
	/**
	 * <p>
	 * Get town name
	 * <p>
	 * @return String
	 */
	public String getTownName() {
		return townName;
	}
	
	/**
	 * <p>
	 * Set town name
	 * <p>
	 * @param file
	 * @param townName
	 */
	public void setTownName(File file,String townName) {
		this.townName = townName;
	}
	
	/**
	 * <p>
	 * Get a list of all claimed chunks
	 * <p>
	 * @return ArrayList
	 */
	public ArrayList<String> getChunkList() {
		return chunkCoords;
	}
	/**
	 * <p>
	 * Set chunklist.
	 * <p>
	 * @param chunkCoords
	 */
	public void setChunkList(List<String> chunkCoords) {
		this.chunkCoords.addAll(chunkCoords);
	}
	
	/**
	 * <p>
	 * Get the town spawn location
	 * <p>
	 * @return Location
	 */
	public Location getTownSpawn() {
		return townSpawn;
	}
	
	/**
	 * <p>
	 * Set the town spawn location
	 * <p>
	 * @param file
	 * @param townSpawn
	 */
	public void setTownSpawn(File file,Location townSpawn) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		this.townSpawn = townSpawn;
		config.set("Towns." + townName + ".townspawn", townSpawn.getX() + "/" + townSpawn.getY() + "/" + townSpawn.getZ());
		save(file);
	}
	
	/**
	 * <p>
	 * Get a list of CoOwners of the town
	 * <p>
	 * @return ArrayList
	 */
	public ArrayList<String> getCoOwners() {
		return coOwners;
	}
	/**
	 * <p>
	 * Set all coOwners.
	 * <p>
	 * @param coOwners
	 */
	public void setCoOwners(List<String> coOwners) {
		this.coOwners.addAll(coOwners);
	}
	
	/**
	 * <p>
	 * Set a player as CoOwner of a town
	 * <p>
	 * @param file
	 * @param coOwner
	 * @return
	 * @throws PlayerIsAlreadyCoOwnerException 
	 */
	public void addCoOwner(File file,String coOwner) throws PlayerIsAlreadyCoOwnerException {
		if(!coOwners.contains(coOwner)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			coOwners.add(coOwner);
			config.set("Towns." + townName + ".coOwners", coOwners);
			save(file);
		}
		else {
			throw new PlayerIsAlreadyCoOwnerException(coOwner);
		}
	}
	
	private void save(File file) {
		try {
			this.file = file;
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>
	 * Get savefile of townworld with this town
	 * <p>
	 * @return File
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * <p>
	 * Returns true if the cunk is owned by any town
	 * <p>
	 * @param chunk
	 * @return
	 */
	public boolean isClaimedByTown(Chunk chunk) {
		boolean is = false;
		if(chunkCoords.contains(chunk.getX() + "/" + chunk.getZ())) {
			is = true;
		}
		return is;
	}
	
	/**
	 * <p>
	 * Static method for loading a existing town by name.
	 * <p>
	 * @param file
	 * @param townName
	 * @return
	 */
	public static Town loadTown(File file,String townName) {
		//TODO 
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		Town town = new Town(file, config.getString("Towns." + townName + ".owner"), townName, null);
		town.setCoOwners(config.getStringList("Towns." + townName + ".coOwners"));
		town.setCiticens(config.getStringList("Towns." + townName + ".citizens"));
		town.setChunkList(config.getStringList("Towns." + townName + ".chunks"));
		String locationString = config.getString("Towns." + townName + ".townspawn");
		town.setTownSpawn(file, new Location(Bukkit.getWorld(config.getString("World")), Integer.valueOf(locationString.substring(0, locationString.indexOf("/"))), Integer.valueOf(locationString.substring(locationString.indexOf("/")+1,locationString.lastIndexOf("/"))), Integer.valueOf(locationString.substring(locationString.lastIndexOf("/")+1))));
		return town;
	}
}
