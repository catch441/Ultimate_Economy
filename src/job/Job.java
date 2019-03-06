package job;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

public class Job {

	private List<String> itemList,entityList,fisherList;
	private File file;
	private FileConfiguration config;
	private String name;
	
	public Job(File dataFolder, String name) {
		itemList = new ArrayList<>();
		entityList = new ArrayList<>();
		fisherList = new ArrayList<>();
		this.name = name;
		file = new File(dataFolder , name + "-Job.yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			config = YamlConfiguration.loadConfiguration(file);
			config.set("Jobname", name);
			save();
		}
		else {
			config = YamlConfiguration.loadConfiguration(file);
			load();
		}
	}
	public void load() {
		config = YamlConfiguration.loadConfiguration(file);
		itemList = config.getStringList("Itemlist");
		entityList = config.getStringList("Entitylist");
		fisherList = config.getStringList("Fisherlist");
	}
	public boolean addFisher(String lootType, double price) {
		boolean success = true;
		config = YamlConfiguration.loadConfiguration(file);
		double d = config.getDouble("Fisher." + lootType);
		if(d == 0.0) {
			config.set("Fisher." + lootType, price);
			fisherList.add(lootType);
			removedoubleObjects(entityList);
			config.set("Fisherlist", fisherList);
		}
		else {
			success = false;
		}
		save();
		return success;
	}
	public boolean delFisher(String lootType) {
		boolean exists = false;
		config = YamlConfiguration.loadConfiguration(file);
		Double d = config.getDouble("Fisher." + lootType);
		if(d != 0.0) {
			exists = true;
			config.set("Fisher." + lootType,null);
			fisherList.remove(lootType);
			removedoubleObjects(entityList);
			config.set("Fisherlist", fisherList);
		}
		save();
		return exists;
	}
	public boolean addMob(EntityType entity,double price) {
		boolean success = true;
		String entityName = entity.toString();
		if(entityList.contains(entityName)) {
			success = false;
		}
		entityList.add(entityName);
		config = YamlConfiguration.loadConfiguration(file);
		config.set("JobEntitys." + entityName + ".killprice", price);
		removedoubleObjects(entityList);
		config.set("Entitylist", entityList);
		save();
		return success;
	}
	public boolean deleteMob(EntityType entity) {
		boolean exists = false;
		String entityName = entity.toString();
		if(entityList.contains(entityName)) {
			exists = true;
		}
		entityList.remove(entityName);
		config = YamlConfiguration.loadConfiguration(file);
		config.set("JobEntitys." + entityName, null);
		config.set("Entitylist", entityList);
		save();
		return exists;
	}
	public boolean addItem(Material material,double price) {
		boolean success = true;
		String itemname = material.toString();
		if(itemList.contains(itemname)) {
			success = false;
		}
		itemList.add(itemname);
		config = YamlConfiguration.loadConfiguration(file);
		config.set("JobItems." + itemname + ".breakprice", price);
		removedoubleObjects(itemList);
		config.set("Itemlist", itemList);
		save();
		return success;
	}
	public boolean deleteItem(Material material) {
		boolean exists = false;
		String itemname = material.toString();
		if(itemList.contains(itemname)) {
			exists = true;
		}
		itemList.remove(itemname);
		config = YamlConfiguration.loadConfiguration(file);
		config.set("JobItems." + itemname, null);
		config.set("Itemlist", itemList);
		save();
		return exists;
	}
	public String getName() {
		return name;
	}
	public double getPrice(Material material) {
		config = YamlConfiguration.loadConfiguration(file);
		String itemname = material.toString();
		double price = config.getDouble("JobItems." + itemname + ".breakprice");
		return price;
	}
	public double getFisherPrice(String lootType) {
		config = YamlConfiguration.loadConfiguration(file);
		double price1 = config.getDouble("Fisher." + lootType);
		return price1;
	}
	public double getKillPrice(String entityName) {
		config = YamlConfiguration.loadConfiguration(file);
		double price2 = config.getDouble("JobEntitys." + entityName + ".killprice");
		return price2;
	}		
	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private List<String> removedoubleObjects(List<String> list) {
		Set<String> set = new LinkedHashSet<String>(list);
		list = new ArrayList<String>(set);
		return list;
	}
	public void deleteJob() {
		file.delete();
	}
	public List<String> getItemList() {
		return itemList;
	}
	public List<String> getEntityList() {
		return entityList;
	}
	public List<String> getFisherList() {
		return fisherList;
	}
}
