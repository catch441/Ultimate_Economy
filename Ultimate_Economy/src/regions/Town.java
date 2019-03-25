package regions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Town {

	private String townName;
	private String owner;
	private ArrayList<String> citizens,coOwners;
	private ArrayList<String> chunkCoords;
	private Location townSpawn;
	private File file;
	
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
		Location spawn = new Location(startChunk.getWorld(), (startChunk.getX() << 4) + 7, 0, (startChunk.getZ() << 4) + 7);
		spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));
		config.set("Towns." + townName + ".townspawn", spawn.getX() + "/" + spawn.getY() + "/" + spawn.getZ());
		save(file);
	}
	/**
	 * <p>
	 * Adds a chunk to this town
	 * <p>
	 * @param file
	 * @param chunk
	 * @return boolean success
	 * <p>
	 * false if this chunk is already owned by this town
	 */
	public boolean addChunk(File file,Chunk chunk) {
		boolean success = false;
		String coords = chunk.getX() + "/" + chunk.getZ();
		if(!chunkCoords.contains(coords)) {
			success = true;
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			chunkCoords.add(chunk.getX() + "/" + chunk.getZ());
			config.set("Towns." + townName + ".chunks", chunkCoords);
			save(file);
		}
		return success;
	}
	public boolean removeChunk(File file,Chunk chunk) {
		boolean success = false;
		String coords = chunk.getX() + "/" + chunk.getZ();
		if(chunkCoords.contains(coords)) {
			success = true;
			chunkCoords.remove(coords);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Towns." + townName + ".chunks", chunkCoords);
			save(file);
		}
		return success;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(File file,String owner) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		this.owner = owner;
		config.set("Towns." + townName + ".owner", owner);
		save(file);
	}
	public ArrayList<String> getCitizens() {
		return citizens;
	}
	public boolean addCitizen(File file,String newCitizen) {
		boolean success = false;
		if(!citizens.contains(newCitizen)) {
			success = true;
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			citizens.add(newCitizen);
			config.set("Towns." + townName + ".citizens", citizens);
			save(file);
		}
		return success;
	}
	public boolean removeCitizen(File file,String citizen) {
		boolean success= false;
		if(citizens.contains(citizen)) {
			success = true;
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			citizens.remove(citizen);
			config.set("Towns." + townName + ".citizens", citizens);
			save(file);
		}
		return success;
	}
	public String getTownName() {
		return townName;
	}
	public void setTownName(File file,String townName) {
		this.townName = townName;
	}
	public ArrayList<String> getChunkList() {
		return chunkCoords;
	}
	public Location getTownSpawn() {
		return townSpawn;
	}
	public void setTownSpawn(File file,Location townSpawn) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		this.townSpawn = townSpawn;
		config.set("Towns." + townName + ".townspawn", townSpawn.getX() + "/" + townSpawn.getY() + "/" + townSpawn.getZ());
		save(file);
	}
	public ArrayList<String> getCoOwners() {
		return coOwners;
	}
	public boolean addCoOwner(File file,String coOwner) {
		boolean success = false;
		if(!coOwners.contains(coOwner)) {
			success = true;
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			coOwners.add(coOwner);
			config.set("Towns." + townName + ".coOwners", coOwners);
			save(file);
		}
		return success;
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
	public File getFile() {
		return file;
	}
	public boolean isInTown(Chunk chunk) {
		boolean is = false;
		if(chunkCoords.contains(chunk.getX() + "/" + chunk.getZ())) {
			is = true;
		}
		return is;
	}
	//For loading a town
	/*public static Town loadTown() {
		
	}*/
}
