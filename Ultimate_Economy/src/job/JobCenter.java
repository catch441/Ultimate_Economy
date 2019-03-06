package job;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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

import ultimate_economy.Ultimate_Economy;

public class JobCenter{
	
	private FileConfiguration config;
	private File file;
	private Villager villager;
	private Location location;
	private String name;
	private List<String> jobnames;
	private Inventory inventory;

	public JobCenter(Ultimate_Economy main,String name,Player player,int size) {
		jobnames = new ArrayList<>();
		this.name = name;
		inventory = Bukkit.createInventory(null, size,name);
		file = new File(main.getDataFolder(), name + "-JobCenter.yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			config = YamlConfiguration.loadConfiguration(file);
			this.name = name;
			location = player.getLocation();
			config.set("JobCenterName", name);
			config.set("JobCenterSize", size);
			config.set("ShopLocation.x", location.getX());
			config.set("ShopLocation.y", location.getY());
			config.set("ShopLocation.z", location.getZ());
			config.set("ShopLocation.World", location.getWorld().getName());
			save();
		}
		else {
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
			location = new Location(main.getServer().getWorld(config.getString("ShopLocation.World")),config.getDouble("ShopLocation.x"),config.getDouble("ShopLocation.y"),config.getDouble("ShopLocation.z"));
		}
		
		boolean test = false;
		Collection<Entity> entitys = location.getWorld().getNearbyEntities(location, 1,1,1);
		for(Entity e:entitys) {
			if(e.getType() == EntityType.VILLAGER && e.getCustomName().equals(name)) {
				villager = (Villager) e;
				test = true;
			}
		}
		if(!test) {
			villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);     
			villager.setCustomName(name);
			villager.setCustomNameVisible(true);
			villager.setProfession(Villager.Profession.NITWIT);
			villager.setSilent(true);
		}
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000,30000000));             
		villager.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30000000,30000000));
		setupJobCenter();
		
	}
	public void addJob(String jobname,Material itemMaterial,int slot,Player player) {
		boolean isfree = true;
		config = YamlConfiguration.loadConfiguration(file);
		for(String string:jobnames) {
			if(config.getInt("Jobs." + string + ".ItemSlot") == slot && isfree) {
				isfree = false;
			}
		}
		int slot2 = config.getInt("JobCenterSize") - 1;
		if(slot == slot2) {
			isfree = false;
		}
		if(isfree) {
			jobnames.add(jobname);
			config.set("Jobnames", jobnames);
			config.set("Jobs." + jobname + ".ItemMaterial", itemMaterial.toString());
			config.set("Jobs." + jobname + ".ItemSlot", slot);
			save();
			ItemStack jobItem = new ItemStack(itemMaterial);
			ItemMeta meta = jobItem.getItemMeta();
			meta.setDisplayName(jobname);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			jobItem.setItemMeta(meta);
			inventory.setItem(slot - 1, jobItem);
			player.sendMessage(ChatColor.GOLD + "The job " + ChatColor.GREEN + jobname + ChatColor.GOLD + " was added to the JobCenter " + ChatColor.GREEN + getName() + ".");
		}
		else {
			player.sendMessage(ChatColor.RED + "This slot is occupied!");
		}
	}
	public void removeJob(String jobname) {
		jobnames = config.getStringList("Jobnames");
		if(jobnames.contains(jobname)) {
			config = YamlConfiguration.loadConfiguration(file);
			inventory.clear(config.getInt("Jobs." + jobname + ".ItemSlot") - 1);
			config.set("Jobs." + jobname, null);
			jobnames.remove(jobname);
			config.set("Jobnames", jobnames);
			save();
		}	
	}
	public void moveShop(int x,int y,int z) {
		config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopLocation.x", x);
		config.set("ShopLocation.y", y);
		config.set("ShopLocation.z", z);
		villager.teleport(new Location(Bukkit.getWorld(config.getString("ShopLocation.World")), x, y, z));
		save();
	}
	public String getName() {
		return name;
	}
	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void despawnVillager() {
		villager.remove();
	}
	public void setupJobCenter() {
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
	public void deleteShop() {
		file.delete();
		World world = villager.getLocation().getWorld();
		villager.remove();
		world.save();
	}
	public void openInv(Player player) {
		player.openInventory(inventory);
	}
	public boolean hasJob (String jobname) {
		boolean exist = false;
		if(jobnames.contains(jobname)) {
			exist = true;
		}
		return exist;
	}
	public boolean slotIsEmpty(int slot) {
		slot--;
		boolean isEmpty = false;
		if(inventory.getItem(slot) == null) {
			isEmpty = true;
		}
		return isEmpty;
	}
}
