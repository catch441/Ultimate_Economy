package com.ue.jobsystem.impl;

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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyMessageEnum;
import com.ue.exceptions.JobExceptionMessageEnum;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.api.Jobcenter;
import com.ue.jobsystem.api.JobcenterController;
import com.ue.language.MessageWrapper;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.ultimate_economy.UEVillagerType;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobcenterImpl implements Jobcenter {

    private FileConfiguration config;
    private File file;
    private Villager villager;
    private Location location;
    private String name;
    private List<Job> jobs;
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
    public JobcenterImpl(Server server, File dataFolder, String name, Location spawnLocation, int size)
	    throws JobSystemException {
	jobs = new ArrayList<>();
	this.name = name;
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
	inventory = Bukkit.createInventory(villager, size, name);
	setupJobCenter();
    }

    /**
     * Constructor for loading an existing jobcenter from the save file.
     * 
     * @param server
     * @param dataFolder
     * @param name
     */
    public JobcenterImpl(Server server, File dataFolder, String name) {
	jobs = new ArrayList<>();
	this.name = name;
	file = new File(dataFolder, name + "-JobCenter.yml");
	config = YamlConfiguration.loadConfiguration(file);
	List<String> jobNames = config.getStringList("Jobnames");
	for (String jobName : jobNames) {
	    try {
		jobs.add(JobController.getJobByName(jobName));
	    } catch (JobSystemException e) {
		Bukkit.getLogger().warning(
			"[Ultimate_Economy] " + MessageWrapper.getErrorString("job_does_not_exist") + ":" + jobName);
	    }
	}
	name = config.getString("JobCenterName");
	location = new Location(server.getWorld(config.getString("ShopLocation.World")),
		config.getDouble("ShopLocation.x"), config.getDouble("ShopLocation.y"),
		config.getDouble("ShopLocation.z"));
	setupVillager();
	inventory = Bukkit.createInventory(villager, config.getInt("JobCenterSize"), name);
	for (Job job : jobs) {
	    ItemStack jobItem = new ItemStack(
		    Material.valueOf(config.getString("Jobs." + job.getName() + ".ItemMaterial")));
	    ItemMeta meta = jobItem.getItemMeta();
	    meta.setDisplayName(job.getName());
	    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
	    jobItem.setItemMeta(meta);
	    inventory.setItem(config.getInt("Jobs." + job.getName() + ".ItemSlot") - 1, jobItem);
	}
	setupJobCenter();
    }

    private void setupVillager() {
	location.getChunk().load();
	Collection<Entity> entitys = location.getWorld().getNearbyEntities(location, 10, 10, 10);
	for (Entity e : entitys) {
	    if (e.getName().equals(name)) {
		e.remove();
	    }
	}
	villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
	villager.setCustomName(name);
	villager.setMetadata("ue-type", new FixedMetadataValue(UltimateEconomy.getInstance, UEVillagerType.JOBCENTER));
	villager.setCustomNameVisible(true);
	villager.setProfession(Villager.Profession.NITWIT);
	villager.setSilent(true);
	villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
	villager.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30000000, 30000000));
    }

    @Override
    public void addJob(Job job, String itemMaterial, int slot)
	    throws JobSystemException, GeneralEconomyException, PlayerException {
	itemMaterial = itemMaterial.toUpperCase();
	if (slot < 0 || slot > inventory.getSize()) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, slot);
	} else if (!slotIsEmpty(slot)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.INVENTORY_SLOT_OCCUPIED);
	} else if (jobs.contains(job)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.JOB_ALREADY_EXIST_IN_JOBCENTER);
	} else if (Material.matchMaterial(itemMaterial) == null) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, itemMaterial);
	} else {
	    config = YamlConfiguration.loadConfiguration(file);
	    jobs.add(job);
	    config.set("Jobnames", getJobNameList());
	    config.set("Jobs." + job.getName() + ".ItemMaterial", itemMaterial);
	    config.set("Jobs." + job.getName() + ".ItemSlot", slot);
	    save();
	    ItemStack jobItem = new ItemStack(Material.valueOf(itemMaterial));
	    ItemMeta meta = jobItem.getItemMeta();
	    meta.setDisplayName(job.getName());
	    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
	    jobItem.setItemMeta(meta);
	    inventory.setItem(slot - 1, jobItem);
	}
    }

    @Override
    public void removeJob(Job job) throws JobSystemException {
	if (!jobs.contains(job)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.JOB_NOT_EXIST_IN_JOBCENTER);
	} else {
	    config = YamlConfiguration.loadConfiguration(file);
	    inventory.clear(config.getInt("Jobs." + job.getName() + ".ItemSlot") - 1);
	    config.set("Jobs." + job.getName(), null);
	    jobs.remove(job);
	    config.set("Jobnames", getJobNameList());
	    save();
	    int i = 0;
	    for (Jobcenter jobcenter : JobcenterController.getJobCenterList()) {
		if (jobcenter.hasJob(job)) {
		    i++;
		}
	    }
	    if (i == 0) {
		for (EconomyPlayer ecoPlayer : EconomyPlayerController.getAllEconomyPlayers()) {
		    if (ecoPlayer.hasJob(job)) {
			try {
			    ecoPlayer.leaveJob(job, false);
			} catch (PlayerException e) {
			}
		    }
		}
	    }
	}
    }

    private List<String> getJobNameList() {
	List<String> list = new ArrayList<>();
	for (Job job : jobs) {
	    list.add(job.getName());
	}
	return list;
    }

    @Override
    public List<Job> getJobList() {
	return jobs;
    }

    @Override
    public void moveJobCenter(Location location) {
	config = YamlConfiguration.loadConfiguration(file);
	config.set("ShopLocation.x", location.getX());
	config.set("ShopLocation.y", location.getY());
	config.set("ShopLocation.z", location.getZ());
	villager.teleport(new Location(Bukkit.getWorld(config.getString("ShopLocation.World")), location.getX(),
		location.getY(), location.getZ()));
	save();
    }

    @Override
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

    @Override
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

    @Override
    public void deleteJobCenter() {
	file.delete();
	World world = villager.getLocation().getWorld();
	villager.remove();
	world.save();
    }

    @Override
    public void openInv(Player player) {
	player.openInventory(inventory);
    }

    @Override
    public boolean hasJob(Job job) throws JobSystemException {
	if (jobs.contains(job)) {
	    return true;
	}
	return false;
    }

    private boolean slotIsEmpty(int slot) {
	slot--;
	boolean isEmpty = false;
	if (inventory.getItem(slot) == null) {
	    isEmpty = true;
	}
	return isEmpty;
    }
}
