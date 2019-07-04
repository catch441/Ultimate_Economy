package com.ue.jobsystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.player.EconomyPlayer;

public class JobCenter {
	
	private static List<JobCenter> jobCenterList = new ArrayList<>();
	
	private FileConfiguration config;
	private File file;
	private Villager villager;
	private Location location;
	private String name;
	private List<String> jobnames;
	private Inventory inventory;

	/**
	 * Constructor for creating a new jobcenter.
	 * 
	 * @param server
	 * @param dataFolder
	 * @param name
	 * @param spawnLocation
	 * @param size
	 * @throws JobSystemException
	 */
	private JobCenter(Server server,File dataFolder,String name,Location spawnLocation,int size) throws JobSystemException {
		jobnames = new ArrayList<>();
		this.name = name;
		inventory = Bukkit.createInventory(null, size,name);
		file = new File(dataFolder, name + "-JobCenter.yml");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		config = YamlConfiguration.loadConfiguration(file);
		this.name = name;
		location = spawnLocation;
		config.set("JobCenterName", name);
		config.set("JobCenterSize", size);
		config.set("ShopLocation.x", location.getX());
		config.set("ShopLocation.y", location.getY());
		config.set("ShopLocation.z", location.getZ());
		config.set("ShopLocation.World", location.getWorld().getName());
		save();
		setupVillager();
		setupJobCenter();
	}
	
	/**
	 * Constructor for loading an existing jobcenter from the save file.
	 * 
	 * @param server
	 * @param dataFolder
	 * @param name
	 */
	private JobCenter(Server server,File dataFolder,String name) {
		jobnames = new ArrayList<>();
		this.name = name;
		file = new File(dataFolder, name + "-JobCenter.yml");
		config = YamlConfiguration.loadConfiguration(file);
		jobnames = config.getStringList("Jobnames");
		inventory = Bukkit.createInventory(villager, config.getInt("JobCenterSize"),name);
		for(String string:jobnames) {
			ItemStack jobItem = new ItemStack(Material.valueOf(config.getString("Jobs." + string + ".ItemMaterial")));
			ItemMeta meta = jobItem.getItemMeta();
			meta.setDisplayName(string);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			jobItem.setItemMeta(meta);
			inventory.setItem(config.getInt("Jobs." + string + ".ItemSlot") - 1, jobItem);
		}
		name = config.getString("JobCenterName");
		location = new Location(server.getWorld(config.getString("ShopLocation.World")),config.getDouble("ShopLocation.x"),config.getDouble("ShopLocation.y"),config.getDouble("ShopLocation.z"));
		setupVillager();
		setupJobCenter();
	}
	
	private void setupVillager() {
		location.getChunk().load();
		Collection<Entity> entitys = location.getWorld().getNearbyEntities(location, 10,10,10);
		for(Entity e:entitys) {
			if(e.getName().equals(name)) {
				e.remove();
			}
		}
		villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);     
		villager.setCustomName(name);
		villager.setCustomNameVisible(true);
		villager.setProfession(Villager.Profession.NITWIT);
		villager.setSilent(true);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000,30000000));             
		villager.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30000000,30000000));
	}
	
	/**
	 * This method adds a job to this jobcenter.
	 * 
	 * @param jobname
	 * @param itemMaterial
	 * @param slot
	 * @throws JobSystemException
	 */
	public void addJob(String jobname,String itemMaterial,int slot) throws JobSystemException {
		itemMaterial = itemMaterial.toUpperCase();
		if(slot < 0 || slot > inventory.getSize()) {
			throw new JobSystemException(JobSystemException.INVENTORY_SLOT_INVALID);
		}
		else if(!slotIsEmpty(slot)) {
			throw new JobSystemException(JobSystemException.INVENTORY_SLOT_OCCUPIED);
		}
		else if(!Job.getJobNameList().contains(jobname)) {
			throw new JobSystemException(JobSystemException.JOB_DOES_NOT_EXIST);
		}
		else if(jobnames.contains(jobname)) {
			throw new JobSystemException(JobSystemException.JOB_ALREADY_EXIST_IN_JOBCENTER);
		}
		else if(Material.matchMaterial(itemMaterial) == null) {
			throw new JobSystemException(JobSystemException.ITEM_IS_INVALID);
		}
		else {
			config = YamlConfiguration.loadConfiguration(file);
			jobnames.add(jobname);
			config.set("Jobnames", jobnames);
			config.set("Jobs." + jobname + ".ItemMaterial", itemMaterial);
			config.set("Jobs." + jobname + ".ItemSlot", slot);
			save();
			ItemStack jobItem = new ItemStack(Material.valueOf(itemMaterial));
			ItemMeta meta = jobItem.getItemMeta();
			meta.setDisplayName(jobname);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			jobItem.setItemMeta(meta);
			inventory.setItem(slot - 1, jobItem);
		}
	}
	
	/**
	 * This method removes a job from this jobcenter.
	 * 
	 * @param jobname
	 * @throws JobSystemException
	 */
	public void removeJob(String jobname) throws JobSystemException {
		if(!Job.getJobNameList().contains(jobname)) {
			throw new JobSystemException(JobSystemException.JOB_DOES_NOT_EXIST);
		}
		else if(!jobnames.contains(jobname)) {
			throw new JobSystemException(JobSystemException.JOB_NOT_EXIST_IN_JOBCENTER);
		}	
		else {
			config = YamlConfiguration.loadConfiguration(file);
			inventory.clear(config.getInt("Jobs." + jobname + ".ItemSlot") - 1);
			config.set("Jobs." + jobname, null);
			jobnames.remove(jobname);
			config.set("Jobnames", jobnames);
			save();
			int i = 0;
			for(JobCenter jobCenter:jobCenterList) {
				if(jobCenter.hasJob(jobname)) {
					i++;
				}
			}
			if(i==0) {
				for(EconomyPlayer ecoPlayer : EconomyPlayer.getAllEconomyPlayers()) {
					if(ecoPlayer.hasJob(jobname)) {
						try {
							ecoPlayer.removeJob(jobname);
						} catch (PlayerException e) {}
					}
				}
			}
		}
	}
	
	/**
	 * This method returns a list of all jobnames in this jobcenter.
	 * 
	 * @return
	 */
	public List<String> getJobNameList() {
		return jobnames;
	}
	
	/**
	 * This method moves a jobcenter villager to a other location.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void moveShop(double x,double y,double z) {
		config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopLocation.x", x);
		config.set("ShopLocation.y", y);
		config.set("ShopLocation.z", z);
		villager.teleport(new Location(Bukkit.getWorld(config.getString("ShopLocation.World")), x, y, z));
		save();
	}
	
	/**
	 * This method returns the name of this jobcenter.
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	private void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method despawns the jobcenter villager.
	 */
	public void despawnVillager() {
		villager.remove();
	}
	
	private void setupJobCenter() {
		int slot = config.getInt("JobCenterSize") - 1;
		ItemStack info = new ItemStack(Material.ANVIL);
		ItemMeta meta = info.getItemMeta();
		meta.setDisplayName("Info");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "Join");
		lore.add(ChatColor.GOLD + "Rightclick: " + ChatColor.RED + "Leave");
		meta.setLore(lore);
		info.setItemMeta(meta);
		inventory.setItem(slot, info);
	}
	
	private void deleteJobCenter() {
		file.delete();
		World world = villager.getLocation().getWorld();
		villager.remove();
		world.save();
	}
	
	/**
	 * This method opens the jobcenter inventory.
	 * 
	 * @param player
	 */
	public void openInv(Player player) {
		player.openInventory(inventory);
	}
	
	/**
	 * This method returns true if this jobcenter contains this job.
	 * 
	 * @param jobname
	 * @return boolean
	 * @throws JobSystemException
	 */
	public boolean hasJob (String jobname) throws JobSystemException {
		if(!Job.getJobNameList().contains(jobname)) {
			throw new JobSystemException(JobSystemException.JOB_DOES_NOT_EXIST);
		}
		boolean exist = false;
		if(jobnames.contains(jobname)) {
			exist = true;
		}
		return exist;
	}
	
	private boolean slotIsEmpty(int slot) {
		slot--;
		boolean isEmpty = false;
		if(inventory.getItem(slot) == null) {
			isEmpty = true;
		}
		return isEmpty;
	}
	
	/**
	 * This method returns a jobcenter by it's name.
	 * 
	 * @param name
	 * @return JobCenter
	 * @throws JobSystemException
	 */
	public static JobCenter getJobCenterByName(String name) throws JobSystemException {
		for(JobCenter jobCenter:jobCenterList) {
			if(jobCenter.getName().equals(name)) {
				return jobCenter;
			}
		}
		throw new JobSystemException(JobSystemException.JOBCENTER_DOES_NOT_EXIST);
	}
	
	/**
	 * This method returns a namelist of all jobcenters.
	 * 
	 * @return List of Strings
	 */
	public static List<String> getJobCenterNameList() {
		List<String> jobCenterNames = new ArrayList<>();
		for(JobCenter jobCenter:jobCenterList) {
			jobCenterNames.add(jobCenter.getName());
		}
		return jobCenterNames;
	}
	
	/**
	 * This method returns a list of all existing jobcenters.
	 * 
	 * @return List of JobCenters
	 */
	public static List<JobCenter> getJobCenterList() {
		return jobCenterList;
	}
	
	/**
	 * This method should me used to delete a jobcenter.
	 * 
	 * @param name
	 * @throws JobSystemException
	 */
	public static void deleteJobCenter(String name) throws JobSystemException {
		JobCenter jobCenter = getJobCenterByName(name);
		jobCenter.deleteJobCenter();
		List<String> jobList = jobCenter.getJobNameList();
		jobCenterList.remove(jobCenter);
		int i = 0;
		for(String jobName:jobList) {
			for(JobCenter jobCenter2:jobCenterList) {
				if(jobCenter2.hasJob(jobName)) {
					i++;
				}
			}
			if(i == 0) {
				for(EconomyPlayer ecoPlayer : EconomyPlayer.getAllEconomyPlayers()) {
					if(ecoPlayer.hasJob(jobName)) {
						try {
							ecoPlayer.removeJob(jobName);
						} catch (PlayerException e){} 
					}
				}
			}
		}
	}
	
	/**
	 * This method should be used to create a new jobcenter.
	 * 
	 * @param server
	 * @param dataFolder
	 * @param name
	 * @param spawnLocation
	 * @param size
	 * @throws JobSystemException
	 */
	public static void createJobCenter(Server server,File dataFolder,String name,Location spawnLocation,int size) throws JobSystemException {
		if(getJobCenterNameList().contains(name)) {
			throw new JobSystemException(JobSystemException.JOBCENTER_ALREADY_EXIST);
		}
		else if(size%9 != 0) {
			throw new JobSystemException(JobSystemException.INVALID_INVENTORY_SIZE);
		}
		else {
			jobCenterList.add(new JobCenter(server, dataFolder, name, spawnLocation, size));
		}
	}
	
	/**
	 * This method loads all jobcenters from the save files.
	 * 
	 * @param server
	 * @param fileConfig
	 * @param dataFolder
	 * @throws JobSystemException
	 */
	public static void loadAllJobCenters(Server server,FileConfiguration fileConfig,File dataFolder) throws JobSystemException {
		for(String jobCenterName:fileConfig.getStringList("JobCenterNames")) {
			File file = new File(dataFolder, jobCenterName + "-JobCenter.yml");
			if(file.exists()) {
				jobCenterList.add(new JobCenter(server, dataFolder, jobCenterName));
			}
			else {
				throw new JobSystemException(JobSystemException.CANNOT_LOAD_JOBCENTER);
			}
		}
	}
	
	/**
	 * This method despawns all jobcenter villager.
	 */
	public static void despawnAllVillagers() {
		for(JobCenter jobCenter:jobCenterList) {
			jobCenter.despawnVillager();
		}
	}
}
