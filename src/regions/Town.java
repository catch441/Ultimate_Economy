package regions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Town {

	private String townName;
	private String owner;
	private ArrayList<String> citizens,coOwners;
	private ArrayList<Chunk> chunks;
	private Location townSpawn;
	private File file;
	private FileConfiguration config;
	
	public Town(File file,String owner,String townName,Chunk startChunk) {
		this.file = file;
		this.townName = townName;
		this.owner = owner;
		chunks = new ArrayList<>();
		citizens = new ArrayList<>();
		coOwners = new ArrayList<>();
		citizens.add(owner);
		config = YamlConfiguration.loadConfiguration(file);
		List<String> chunkCoords = new ArrayList<>();
		chunkCoords.add(startChunk.getX() + "/" + startChunk.getZ());
		List<String> citizens = new ArrayList<>();
		citizens.add(owner);
		List<String> citizens2 = new ArrayList<>();
		citizens2.add(owner);
		config.set("Towns." + townName + ".mayor", citizens);
		config.set("Towns." + townName + ".citizens", citizens2);
		config.set("Towns." + townName + ".chunks", chunkCoords);
		Location spawn = new Location(startChunk.getWorld(), (startChunk.getX() << 4) + 7, 0, (startChunk.getZ() << 4) + 7);
		spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));
		config.set("Towns." + townName + ".townspawn", spawn.getX() + "/" + spawn.getY() + "/" + spawn.getZ());
		save();
	}
	public void addChunk(Chunk chunk) {
		chunks.add(chunk);
	}
	public boolean removeChunk(Chunk chunk) {
		boolean success = false;
		for(Chunk c:chunks) {
			if(c.equals(chunk)) {
				success = true;
			}
		}
		if(success) {
			chunks.remove(chunk);
			/*config = YamlConfiguration.loadConfiguration(file);
			List<Pair<Integer, Integer>> chunkCoords = new ArrayList<>();
			for(Chunk c:chunks) {
				chunkCoords.add(new Pair<Integer, Integer>(c.getX(), c.getZ()));
			}
			config.set("Towns." + townName + ".chunks", chunkCoords);
			save();*/
		}
		return success;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public ArrayList<String> getCitizens() {
		return citizens;
	}
	public void addCitizen(String newCitizen) {
		citizens.add(newCitizen);
	}
	public boolean removeCitizen(String citizen) {
		boolean success= false;
		for(String name : citizens) {
			if(name.equals(citizen) ) {
				success = true;
			}
		}
		if(success) {
			citizens.remove(citizen);
		}
		return success;
	}
	public String getTownName() {
		return townName;
	}
	public void setTownName(String townName) {
		this.townName = townName;
	}
	public ArrayList<Chunk> getChunkList() {
		return chunks;
	}
	public Location getTownSpawn() {
		return townSpawn;
	}
	public void setTownSpawn(Location townSpawn) {
		this.townSpawn = townSpawn;
	}
	public ArrayList<String> getCoOwners() {
		return coOwners;
	}
	private void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
