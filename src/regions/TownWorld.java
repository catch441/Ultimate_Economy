package regions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import ultimate_economy.Ultimate_Economy;

public class TownWorld {

	private double foundationPrice,expandPrice;
	private static String worldName;
	private File file;
	private FileConfiguration config;
	private List<String> townNames;
	
	public TownWorld(Ultimate_Economy main,String world) {
		worldName = world;
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
	public void createTown(String name,Chunk chunk,String owner) {
		config = YamlConfiguration.loadConfiguration(file);
		List<String> chunkCoords = new ArrayList<>();
		chunkCoords.add(chunk.getX() + "/" + chunk.getZ());
		List<String> citizens = new ArrayList<>();
		citizens.add(owner);
		List<String> citizens2 = new ArrayList<>();
		citizens2.add(owner);
		config.set("Towns." + name + ".mayor", citizens);
		config.set("Towns." + name + ".citizens", citizens2);
		config.set("Towns." + name + ".chunks", chunkCoords);
		Location spawn = new Location(chunk.getWorld(), (chunk.getX() << 4) + 7, 0, (chunk.getZ() << 4) + 7);
		spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));
		config.set("Towns." + name + ".townspawn", spawn.getX() + "/" + spawn.getY() + "/" + spawn.getZ());
		townNames.add(name);
		config.set("TownNames", townNames);
		save();
	}
	public boolean expandTown(String townname) {
		boolean success = false;
		
		return success;
	}
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
	public String getTownByChunk(Chunk chunk) {
		config = YamlConfiguration.loadConfiguration(file);
		String townname = null;
		for(String town:townNames) {
			if(config.getStringList("Towns." + town + ".chunks").contains(chunk.getX() + "/" + chunk.getZ())) {
				townname = town;
				break;
			}
		}
		return townname;
	}
	public boolean playerIsCitizen(String townname,String playername) {
		config = YamlConfiguration.loadConfiguration(file);
		boolean is = false;
		if(config.getStringList("Towns." + townname + ".citizens").contains(playername)) {
			is = true;
		}
		return is;
	}
	private void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
