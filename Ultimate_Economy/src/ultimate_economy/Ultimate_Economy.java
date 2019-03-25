package ultimate_economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.Ageable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import job.Job;
import job.JobCenter;
import regions.TownWorld;
import shop.AdminShop;
import shop.PlayerShop;
import shop.Shop;
import shop.Spawner;

public class Ultimate_Economy extends JavaPlugin implements Listener{
	
	/*
	 * Lukas Heubach
	 * buyable regions
	 * 		set/break block event anpassen
	 * maxjobs bug
	 * job crafter,enchanter
	 * op should remove playershops
	 * limit playershops per player
	 * check inventory name for right cancel 
	 * givemoney bugs herausfinden
	 * 
	 */

	private Player player = null;
	private List<String> playershopNames,adminShopNames,playerlist,jobCenterNames,homeList,spawnerlist,jobList;
	private List<AdminShop> adminShopList;
	private List<PlayerShop> playerShopList;
	private List<Job> jobs;
	private List<TownWorld> townWorlds;
	private File playerFile,spawner;
	private FileConfiguration config;
	private List<JobCenter> jobCenterList;

	public void onEnable() {	
		Bukkit.getPluginManager().registerEvents(this,this);
		adminShopNames = new ArrayList<>();    
		adminShopList = new ArrayList<>();
		playerlist = new ArrayList<>();
		jobCenterList = new ArrayList<>();
		jobCenterNames = new ArrayList<>();
		playerShopList = new ArrayList<>();
		playershopNames = new ArrayList<>();
		homeList = new ArrayList<>();
		spawnerlist = new ArrayList<>();
		jobList = new ArrayList<>();
		jobs = new ArrayList<>();
		townWorlds = new ArrayList<>();

		if(!getDataFolder().exists()) {
			getDataFolder().mkdirs();
			getConfig().set("MaxHomes", 3);
			getConfig().set("MaxJobs", 2);
			getConfig().set("MaxJoinedTowns", 1);
			saveConfig();
		}
		//can be removed in a future update
		else {
			getConfig().set("ItemList", null);
			saveConfig();
		}
		if(getConfig().getInt("MaxHomes") == 0) {
			getConfig().set("MaxHomes", 3);
		}
		if(getConfig().getInt("MaxJobs") == 0) {
			getConfig().set("MaxJobs", 2);
		}
		if(getConfig().getInt("MaxJoinedTowns") == 0) {
			getConfig().set("MaxJoinedTowns", 1);
		}
		for(String world:getConfig().getStringList("TownWorlds")) {
			townWorlds.add(new TownWorld(this, world));
		}
		for(String jobCentername:getConfig().getStringList("JobCenterNames")) {
			jobCenterNames.add(jobCentername);
			jobCenterList.add(new JobCenter(this,jobCentername,player,9));
		}
		for(String shopname1:getConfig().getStringList("ShopNames")) {
			adminShopNames.add(shopname1);		
			adminShopList.add(new AdminShop(this,shopname1,player,"9"));
			}
		for(String shopname1:getConfig().getStringList("PlayerShopNames")) {
			playershopNames.add(shopname1);		
			playerShopList.add(new PlayerShop(this,shopname1,player,"9"));
			}
		for(String s:getConfig().getStringList("JobList")) {
			jobList.add(s);
			jobs.add(new Job(this.getDataFolder(), s));
		}	
		playerFile = new File(getDataFolder() , "PlayerFile.yml");
		if(!playerFile.exists()) {
			try {
				playerFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(playerFile);
		spawner = new File(getDataFolder() , "SpawnerLocations.yml");
		if(!spawner.exists()) {
			try {
				spawner.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileConfiguration spawnerconfig = YamlConfiguration.loadConfiguration(spawner);
		for(String spawnername:getConfig().getStringList("Spawnerlist")) {
			spawnerlist.add(spawnername);
			World world = getServer().getWorld(spawnerconfig.getString(spawnername + ".World"));
			Location location = new Location(world, spawnerconfig.getDouble(spawnername + ".X"), spawnerconfig.getDouble(spawnername + ".Y"), spawnerconfig.getDouble(spawnername + ".Z"));			world.getBlockAt(location).setMetadata("name", new FixedMetadataValue(this, spawnerconfig.getString(spawnername + ".player")));
			world.getBlockAt(location).setMetadata("entity", new FixedMetadataValue(this, spawnerconfig.getString(spawnername + ".EntityType")));
		}
		playerlist = config.getStringList("Player");
		getConfig().options().copyDefaults(true);
		saveConfig();	
	}
	public void onDisable() {
		for(AdminShop s:adminShopList) {
			s.despawnVillager();
		}
		for(PlayerShop s:playerShopList) {
			s.despawnVillager();
		}
		for(JobCenter js: jobCenterList) {
			js.despawnVillager();
		}
		saveConfig();
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if(command.getName().equals("jobcenter")) {
			if(args.length == 1) {
				if(args[0].equals("")) {
					list.add("create");
					list.add("delete");
					list.add("move");
					list.add("job");
					list.add("addJob");
					list.add("removeJob");
				}
				else if(args.length == 1){
					if("create".contains(args[0])) {
						list.add("create");
					}
					if("delete".contains(args[0])) {
						list.add("delete");
					}
					if("move".contains(args[0])) {
						list.add("move");
					}
					if("job".contains(args[0])) {
						list.add("job");
					}
					if("addJob".contains(args[0])) {
						list.add("addJob");
					}
				}
			}
			else if(args[0].equals("delete") || args[0].equals("move") || args[0].equals("removeJob") || args[0].equals("addJob")) {
				if(args.length == 2) {
					List<String> temp = getConfig().getStringList("JobCenterNames");
					if(args[1].equals("")) {
						list = temp;
					}
					else {
						for(String jobname:temp) {
							if(jobname.contains(args[1])) {
								list.add(jobname);								
							}
						}
					}
				}
				else if(args[0].equals("removeJob") || args[0].equals("addJob")) {
					if(args.length == 3) {
						list = getJobList(args[2]);
					}
					else if(args.length == 4 && args[0].equals("addJob")) {
						list = getMaterialList(args[3]);
					}
				}	
			}
			else if(args[0].equals("job")) {
				if(args[1].equals("")) {
					list.add("createJob");
					list.add("delJob");
					list.add("addItem");
					list.add("removeItem");
					list.add("addFisher");
					list.add("delFisher");
					list.add("addMob");
					list.add("removeMob");
				}
				else if(args[1].equals("addItem") || args[1].equals("removeItem") || args[1].equals("addFisher") || args[1].equals("delFisher") || args[1].equals("removeFisher") || args[1].equals("addMob") || args[1].equals("removeMob")) {
					if(args.length == 3) {
						list = getJobList(args[2]);
					}
					else if(args[1].equals("addItem") || args[1].equals("removeItem") || args[1].equals("addItem")){
						if(args.length == 4) {
							list = getMaterialList(args[3]);
						}
					}
					else if(args[1].equals("addMob") || args[1].equals("removeMob")) {
						if(args.length == 4) {
							list = getEntityList(args[3]);
						}
					}
					else if(args[1].equals("addFisher") || args[1].equals("delFisher")) {
						if(args.length == 4) {
							if(args[3].equals("")) {
								list.add("fish");
								list.add("treasure");
								list.add("junk");
							}
							else {
								if("fish".contains(args[3])) {
									list.add("fish");
								}
								if("treasure".contains(args[3])) {
									list.add("treasure");
								}
								if("junk".contains(args[3])) {
									list.add("junk");
								}
							}
						}
					}
				}
				else if(args[1].equals("delJob")) {
					if(args.length == 3) {
						list = getJobList(args[2]);
					}
				}
				else if (args.length == 2) {
					if("createJob".contains(args[1])) {
						list.add("createJob");
					}
					if("delJob".contains(args[1])) {
						list.add("delJob");
					}
					if("addItem".contains(args[1])) {
						list.add("addItem");
					}
					if("removeItem".contains(args[1])) {
						list.add("removeItem");
					}
					if("addFisher".contains(args[1])) {
						list.add("addFisher");
					}
					if("delFisher".contains(args[1])) {
						list.add("delFisher");
					}
					if("addMob".contains(args[1])) {
						list.add("addMob");
					}
					if("removeMob".contains(args[1])) {
						list.add("removeMob");
					}
				}
			}
		}
		else if(command.getName().equals("adminshop") || command.getName().equals("playershop")) {
			if(args[0].equals("")) {
				list.add("create");
				list.add("delete");
				list.add("move");
				list.add("editShop");
				list.add("addItem");
				list.add("removeItem");
				list.add("editItem");
				list.add("addEnchantedItem");
				list.add("addPotion");
				if(command.getName().equals("adminshop")) {
					list.add("addSpawner");
					list.add("removeSpawner");
				}
				else {
					list.add("changeOwner");
				}
			}
			else if(args[0].equals("delete") || args[0].equals("addItem") || args[0].equals("removeItem") || args[0].equals("addSpawner")  || args[0].equals("removeSpawner")  || args[0].equals("editShop") || args[0].equals("addEnchantedItem") || args[0].equals("addPotion") || args[0].equals("changeOwner")) {
				if(args.length == 2) {
					if(command.getName().equals("adminshop")) {
						list = getAdminShopList(args[1]);
					}
					else {
						list = getPlayerShopList(args[1],sender.getName());
					}
				}
				else if(args.length == 3) {
					if(args[0].equals("addItem") || args[0].equals("addEnchantedItem")) {
						list = getMaterialList(args[2]);
					}
					else if(args[0].equals("addPotion")) {
						if(args[2].equals("")) {
							for(PotionType pType: PotionType.values()) {
								list.add(pType.name().toLowerCase());
							}
						}
						else  {
							for(PotionType pType: PotionType.values()) {
								if(pType.name().toLowerCase().contains(args[2])) {
									list.add(pType.name().toLowerCase());
								}
							}
						}
					}
					else if(args[0].equals("addSpawner")) {
						list = getEntityList(args[2]);
					}
				}
				else if(args.length == 4 && args[0].equals("addPotion")) {
					if(args[3].equals("")) {
						for(PotionEffectType peType: PotionEffectType.values()) {
							if(peType != null) {
								list.add(peType.getName().toLowerCase());
							}
						}
					}
					else  {
						for(PotionEffectType peType: PotionEffectType.values()) {
							if(peType != null && peType.getName().toLowerCase().contains(args[3])) {
								list.add(peType.getName().toLowerCase());
							}
						}
					}
				}
				else if(args.length == 5 && args[0].equals("addPotion")) {
					if(args[4].equals("")) {
						list.add("extended");
						list.add("upgraded");
						list.add("none");
					}
					else  {
						if("extended".contains(args[4])) {
							list.add("extended");
						}
						if("upgraded".contains(args[4])) {
							list.add("upgraded");
						}
						if("none".contains(args[4])) {
							list.add("none");
						}
					}
				}
				else if(args[0].equals("addEnchantedItem") && args.length >= 7 && (args.length % 2) == 0) {
					if(args[args.length-1].equals("")) {
						for(Enchantment enchantment: Enchantment.values()) {
							if(enchantment != null) {
								list.add(enchantment.getKey().getKey());
							}
						}
					}
					else  {
						for(Enchantment enchantment: Enchantment.values()) {
							if(enchantment != null && enchantment.getKey().getKey().contains(args[args.length-1])) {
								list.add(enchantment.getKey().getKey().toLowerCase());
							}
						}
					}
				}
			}
			else if(args.length == 1){
				if("create".contains(args[0])) {
					list.add("create");
				}
				if("delete".contains(args[0])) {
					list.add("delete");
				}
				if("move".contains(args[0])) {
					list.add("move");
				}
				if("addItem".contains(args[0])) {
					list.add("addItem");
				}
				if("removeItem".contains(args[0])) {
					list.add("removeItem");
				}
				if("editItem".contains(args[0])) {
					list.add("editItem");
				}
				if("editShop".contains(args[0])) {
					list.add("editShop");
				}
				if("addEnchantedItem".contains(args[0])) {
					list.add("addEnchantedItem");
				}
				if("addPotion".contains(args[0])) {
					list.add("addPotion");
				}
				if(command.getName().equals("adminshop")) {
					if("addSpawner".contains(args[0])) {
						list.add("addSpawner");
					}
					if("removeSpawner".contains(args[0])) {
						list.add("removeSpawner");
					}
				}
				else {
					if("changeOwner".contains(args[0])) {
						list.add("changeOwner");
					}
				}
			}
		}
		else if(command.getName().equals("bank")) {
			if(args[0].equals("")) {
				list.add("on");
				list.add("off");
			}
			else if(args.length == 1){
				if("on".contains(args[0])) {
					list.add("on");
				}
				if("off".contains(args[0])) {
					list.add("off");
				}
			}
		}
		else if(command.getName().equals("shop")) {
			if(args.length <= 1) {
				list = getAdminShopList(args[0]);
			}
		}
		else if(command.getName().equals("jobInfo")) {
			if(args.length <= 1) {
				list = getJobList(args[0]);
			}
		}
		else if(command.getName().equals("delHome") || command.getName().equals("home")) {
			if(args.length <= 1) {
				list = getHomeList(args[0],sender.getName());
			}
		}
		return list;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			player = (Player) sender;
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//Commands
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////single commands
			if(label.equalsIgnoreCase("bank")) {
				if(args.length == 1) {
					if(args[0].equals("on") || args[0].equals("off")) {
						config = YamlConfiguration.loadConfiguration(playerFile);
						if(!args[0].equals("on")) {
							config.set(player.getName() + ".bank", true);
							saveFile(playerFile);
							updateScoreBoard(player);
						}
						else {
							config.set(player.getName() + ".bank", false);
							saveFile(playerFile);
							updateScoreBoard(player);
						}
					}
					else {
						player.sendMessage(ChatColor.RED + "/bank on/off");
					}
				}
				else {
					player.sendMessage("/bank <on/off>");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("money")) {
				if(args.length == 0) {
					config = YamlConfiguration.loadConfiguration(playerFile);
					player.sendMessage(ChatColor.GOLD + "Money: " + ChatColor.GREEN + String.valueOf(config.getDouble(player.getName() + ".account amount")));
				}
				else {
					player.sendMessage("/money");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("maxHomes")) {
				if(args.length == 1) {
					getConfig().set("MaxHomes", Integer.valueOf(args[0]));
					saveConfig();
					player.sendMessage(ChatColor.GOLD + "MaxHomes changed to " + ChatColor.GREEN + args[0]);
				}
				else {
					player.sendMessage("/maxHomes <number>");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("maxJobs")) {
				if(args.length == 1) {
					getConfig().set("MaxJobs", Integer.valueOf(args[0]));
					saveConfig();
					player.sendMessage(ChatColor.GOLD + "MaxJobs changed to " + ChatColor.GREEN + args[0]);
				}
				else {
					player.sendMessage("/maxJobs <number>");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("maxJoinedTowns")) {
				if(args.length == 1) {
					getConfig().set("MaxJoinedTowns", Integer.valueOf(args[0]));
					saveConfig();
					player.sendMessage(ChatColor.GOLD + "MaxJoinedTowns changed to " + ChatColor.GREEN + args[0]);
				}
				else {
					player.sendMessage("/maxJoinedTowns <number>");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("townWorld")) {
				if(args.length > 1) {
					if(Bukkit.getWorld(args[1]) != null) {
						List<String> worldList = getConfig().getStringList("TownWorlds");
						if(worldList == null) {
							worldList = new ArrayList<>();
						}
						if(args[0].equals("enable")) {
							if(args.length == 2) {
								if(!isTownWorld(args[1]) ) {
									worldList.add(args[1]);
									townWorlds.add(new TownWorld(this,args[1]));
									player.sendMessage(ChatColor.GREEN + args[1] + ChatColor.GOLD + " is now a TownWold.");
								}
								else {
									player.sendMessage(ChatColor.RED + "TownWorld is already enabled in this world!");
								}
							}
							else {
								player.sendMessage("/townWorld activate <worldname>");
							}
						}
						else if(args[0].equals("disable")) {
							if(args.length == 2) {
								if(isTownWorld(args[1])) {
									worldList.remove(args[1]);
									getTownWorld(args[1]).delete();
									townWorlds.remove(getTownWorld(args[1]));
									player.sendMessage(ChatColor.GREEN + args[1] + ChatColor.GOLD + " is no longer a TownWold.");
								}
								else {
									player.sendMessage(ChatColor.RED + "TownWorld is not aenabled in this world!");
								}
							}
							else {
								player.sendMessage("/townWorld deactivate <worldname>");
							}
						}
						else if(args[0].equals("setFoundationPrice")) {
							if(args.length == 3) {
								if(isTownWorld(args[1])) {
									getTownWorld(args[1]).setFoundationPrice(Double.valueOf(args[2]));
									player.sendMessage(ChatColor.GOLD + "FoundationPrice changed to " + ChatColor.GREEN + args[2]);
								}
								else {
									player.sendMessage(ChatColor.RED + "TownWorld is not enabled in this world!");
								}
							}
							else {
								player.sendMessage("/townWorld setFoundationPrice <worldname> <price>");
							}
						}
						else if(args[0].equals("setExpandPrice")) {
							if(args.length == 3) {
								if(isTownWorld(args[1])) {
									getTownWorld(args[1]).setExpandPrice(Double.valueOf(args[2]));
									player.sendMessage(ChatColor.GOLD + "ExpandPrice changed to " + ChatColor.GREEN + args[2]);
								}
								else {
									player.sendMessage(ChatColor.RED + "TownWorld is not enabled in this world!");
								}
							}
							else {
								player.sendMessage("/townWorld setExpandPrice <worldname> <price per chunk");
							}
						}
						getConfig().set("TownWorlds", worldList);
						saveConfig();
					}
					else {
						player.sendMessage(ChatColor.RED + "This world doesn't exist!");
					}
				}
				else if(args.length == 1 && args[0].equals("enable")){
					player.sendMessage("/townWorld <enable> <worldname>");
				}
				else if(args.length == 1 && args[0].equals("disable")){
					player.sendMessage("/townWorld <disable> <worldname>");
				}
				else if(args.length == 1 && args[0].equals("setFoundationPrice")){
					player.sendMessage("/townWorld <setFoundationPrice> <price>");
				}
				else if(args.length == 1 && args[0].equals("setExpandPrice")){
					player.sendMessage("/townWorld <setExpandPrice> <price per chunk>");
				}
				else {
					player.sendMessage("/townWorld <enable/disable/setFoundationPrice/setExpandPrice>");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("setTownFoundationPrice")) {
				if(args.length == 1) {
					getConfig().set("TownFoundationPrice", Integer.valueOf(args[0]));
					saveConfig();
				}
				else {
					player.sendMessage("/setTownFoundationPrice <price>");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("setTownExpandPrice")) {
				if(args.length == 1) {
					getConfig().set("TownExpandPrice", Integer.valueOf(args[0]));
					saveConfig();
				}
				else {
					player.sendMessage("/setTownExpandPrice <price/chunk>");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("myJobs")) {
				if(args.length == 0) {
					config = YamlConfiguration.loadConfiguration(playerFile);
					List<String> jobs = config.getStringList(player.getDisplayName() + ".Jobs");
					String myJobs = null;
					for(String j : jobs) {
						if(myJobs == null) {
							myJobs = j;
						}
						else {
							myJobs = myJobs + "," + j;
						}
					}
					if(myJobs != null) {
						player.sendMessage(ChatColor.GOLD + myJobs);
					}
					else {
						player.sendMessage(ChatColor.GOLD + "No Jobs");
					}
				}
				else {
					player.sendMessage("/myJobs");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("shop")) {
				if(args.length == 1) {
					config = YamlConfiguration.loadConfiguration(playerFile);
					List<String> joblist = config.getStringList(player.getName() + ".Jobs");
					if(joblist.contains(args[0])) {
						for(AdminShop shop5: adminShopList) {
							if(shop5.getName().equals(args[0])) {
								shop5.openInv(player); 
								break;
							}
						}
					}
					else {
						player.sendMessage(ChatColor.RED + "You not joined this job!");
					}
				}
				else {
					player.sendMessage("/shop <shopname>");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("ShopList")) {
				String namelist = null;
				for(String name: adminShopNames) {
					if(namelist == null) {
						namelist = name;
					}
					else {
						namelist = namelist + "," + name;
					}
				}
				if(namelist != null) {
					player.sendMessage(ChatColor.GOLD + namelist);
				}
				else {
					player.sendMessage(ChatColor.RED + "No shops exist!");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("giveMoney")) {
				if(player.isOp()) {
					if(args.length == 2) {
						config = YamlConfiguration.loadConfiguration(playerFile);
						List<String> playerList = config.getStringList("Player");
						if(playerList.contains(args[0])) {
							double before = config.getDouble(args[0] + ".account amount");
							double after = before + Double.valueOf(args[1]);
							if(after >= 0) {
								config.set(args[0] + ".account amount", (before + Double.valueOf(args[1])));
								saveFile(playerFile);
								for(Player pl:Bukkit.getOnlinePlayers()) {
									if(pl.getName().equals(args[0])) {
										updateScoreBoard(pl);
										pl.sendMessage(ChatColor.GOLD + "You got " + ChatColor.GREEN  + " " + args[1] +  " $ "); 
										break;
									}
								}
							}
							else {
								player.sendMessage(ChatColor.RED + args[0] + " has not enough money to remove!");
							}
						}
						else {
							player.sendMessage(ChatColor.RED + "This player is/was never on this server!");
						}
					}
					else {
						player.sendMessage("/giveMoney <player> <amount>");
					}
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("home")) {
				String pname = player.getName();
				if(args.length == 1) {
					config = YamlConfiguration.loadConfiguration(playerFile);
					if(config.isSet(pname + ".Home." + args[0] + ".Name")) {
						Location location = new Location(getServer().getWorld(config.getString(pname + ".Home." + args[0] + ".World")), config.getDouble(pname + ".Home." + args[0] + ".X"), config.getDouble(pname + ".Home." + args[0] + ".Y"), config.getDouble(pname + ".Home." + args[0] + ".Z"));
						player.teleport(location);
					}
					else {
						player.sendMessage(ChatColor.RED + "This homepoint doesn't exist!");
					}
				}
				else {
					player.sendMessage("/home <homename>");
					homeList = config.getStringList(pname + ".Home.Homelist");
					player.sendMessage(ChatColor.GOLD + homeList.toString());
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("sethome")) {
				String pname = player.getName();
				config = YamlConfiguration.loadConfiguration(playerFile);
				int number = config.getInt(pname + ".Home.Number");
				if(args.length == 1) {
					if(number < getConfig().getInt("MaxHomes")) {
						if(!config.isSet(pname + ".Home." + args[0] + ".Name")) {
							number = number + 1;
							config.set(pname + ".Home.Number",number);
							Location location = player.getLocation();
							homeList = config.getStringList(pname + ".Home.Homelist");
							homeList.add(args[0]);
							config.set(pname + ".Home.Homelist",homeList);
							config.set(pname + ".Home." + args[0] + ".Name", args[0]);
							config.set(pname + ".Home." + args[0] + ".World", location.getWorld().getName());
							config.set(pname + ".Home." + args[0] + ".X", location.getX());
							config.set(pname + ".Home." + args[0] + ".Y", location.getY());
							config.set(pname + ".Home." + args[0] + ".Z", location.getZ());
							saveFile(playerFile);
							player.sendMessage(ChatColor.GOLD + "Your home " + ChatColor.GREEN + args[0] + ChatColor.GOLD + " was set.");
						}
						else {
							player.sendMessage(ChatColor.RED + "This homename already exist!");
						}
					}
					else {
						player.sendMessage(ChatColor.RED + "You already reached the maximum homepoints!");
					}
				}
				else {
					player.sendMessage("/sethome <homename>");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("delhome")) {
				config = YamlConfiguration.loadConfiguration(playerFile);
				if(args.length == 1) {
					String pname = player.getName();
					if(config.isSet(pname + ".Home." + args[0] + ".Name")) {
						config.set(pname + ".Home." + args[0], null);
						homeList = config.getStringList(pname + ".Home.Homelist");
						homeList.remove(args[0]);
						config.set(pname + ".Home.Homelist",homeList);
						int i = config.getInt(pname + ".Home.Number");
						i = i - 1;
						config.set(pname + ".Home.Number", i);
						saveFile(playerFile);
						player.sendMessage(ChatColor.GOLD + "Your home " + ChatColor.GREEN + args[0] + ChatColor.GOLD + " was deleted.");
					}
				}
				else {
					player.sendMessage("/deletehome <homename>");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("jobList")) {
				String namelist = null;
				for(String name: jobList) {
					if(namelist == null) {
						namelist = name;
					}
					else {
						namelist = namelist + "," + name;
					}
				}
				if(namelist != null) {
					player.sendMessage(ChatColor.GOLD + namelist);
				}
				else {
					player.sendMessage(ChatColor.RED + "No jobs exist!");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("jobInfo")) {
				if(args.length == 1) {
					boolean exist = false;
					List<String> list,entityList,fisherList = null;
					for(Job job:jobs) {
						if(job.getName().equals(args[0]) && !exist) {
							exist = true;
							list = job.getItemList();
							entityList = job.getEntityList();
							fisherList = job.getFisherList();
							player.sendMessage("");
							player.sendMessage(ChatColor.GOLD +"Jobinfo for " + ChatColor.GREEN + job.getName() + ChatColor.GOLD + ":");
							for(String string:list) {
								player.sendMessage(ChatColor.GOLD + string.toLowerCase() + " " + ChatColor.GREEN + job.getPrice(Material.valueOf(string)) + "$");
							}
							for(String string:fisherList) {
								player.sendMessage(ChatColor.GOLD + "Fishing " + string.toLowerCase() + " " + ChatColor.GREEN + job.getFisherPrice(string) + "$");
							}
							for(String string:entityList) {
								player.sendMessage(ChatColor.GOLD + "Kill " + string.toLowerCase() + " " + ChatColor.GREEN + job.getKillPrice(string) + "$");
							}
							break;
						}
					}
					if(!exist) {
						player.sendMessage(ChatColor.RED + "This job doesn't exist!");
					}
				}
				else {
					player.sendMessage("/jobInfo <jobname>");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(label.equalsIgnoreCase("pay")) {
				if(args.length == 2 && Integer.valueOf(args[1]) > 0) {
					config = YamlConfiguration.loadConfiguration(playerFile);
					List<String> playerList = config.getStringList("Player");
					double playeramount = config.getDouble(player.getName() + ".account amount");
					double money = Double.valueOf(args[1]);
					if(playerList.contains(args[0])) {
						double otherPlayerAmount = config.getDouble(args[0] + ".account amount");
						if(playeramount >= money) {
							otherPlayerAmount = otherPlayerAmount + money;
							config.set(args[0] + ".account amount", otherPlayerAmount);
							for(Player pl:Bukkit.getOnlinePlayers()) {
								if(pl.getName().equals(args[0])) {
									updateScoreBoard(pl);
									pl.sendMessage(ChatColor.GOLD + "You got " + ChatColor.GREEN  + " " + money +  " $ " + ChatColor.GOLD + "from " + ChatColor.GREEN + player.getName()); 
									break;
								}
							}
							playeramount = config.getDouble(player.getName() + ".account amount");
							playeramount = playeramount - money;
							config.set(player.getName() + ".account amount", playeramount);
							saveFile(playerFile);
							updateScoreBoard(player);
							player.sendMessage(ChatColor.GOLD + "You gave " + ChatColor.GREEN + args[0] + " "+ money + " $ ");
						}
						else {
							player.sendMessage(ChatColor.RED + "You don't have enough money to give!");
						}
					}
					else {
						player.sendMessage(ChatColor.RED + "This player is/was never on this server!");
					}
				}
				else if(args.length != 2) {
					player.sendMessage("/pay <name> <amount>");
				}
				else {
					player.sendMessage(ChatColor.RED + "Use a amount above 0!");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////towns
			else if(label.equalsIgnoreCase("town")) {
				if(args.length != 0) {
					if(isTownWorld(player.getWorld().getName())) {
						config = YamlConfiguration.loadConfiguration(playerFile);
						if(args[0].equals("create")) {
							if(args.length == 2) {
								int joinedTowns = config.getStringList(player.getName() + ".joinedTowns").size();
								TownWorld tWorld = getTownWorld(player.getWorld().getName());
								if(joinedTowns < getConfig().getInt("MaxJoinedTowns") && tWorld.getFoundationPrice() <= config.getDouble(player.getName() + ".account amount")) {
									List<String> towns = getConfig().getStringList("TownList");
									if(towns.contains(args[1])) {
										player.sendMessage(ChatColor.RED + "This town name is already used!");
									}
									else if(!tWorld.chunkIsFree(player.getLocation().getChunk())) {
										player.sendMessage(ChatColor.RED + "This chunk is owned by another city!");
									}
									else {
										tWorld.createTown(args[1], player.getLocation().getChunk(),player.getName());
										config.set(player.getName() + ".account amount", config.getDouble(player.getName() + ".account amount") - tWorld.getFoundationPrice());
										List<String> list = config.getStringList(player.getName() + ".joinedTowns");
										list.add(args[1]);
										config.set(player.getName() + ".joinedTowns", list);
										saveFile(playerFile);
										towns.add(args[1]);
										getConfig().set("TownList", towns);
										saveConfig();
										updateScoreBoard(player);
										player.sendMessage(ChatColor.GOLD + "Congratulation! You founded the new city " + ChatColor.GREEN + args[1] + ChatColor.GOLD + "!");
									}
								}
								else if(tWorld.getFoundationPrice() > config.getDouble(player.getName() + ".account amount")){
									player.sendMessage(ChatColor.RED + "You don't have enough money to found a new city!");
								}
								else if(joinedTowns >= getConfig().getInt("MaxJoinedTowns")){
									player.sendMessage(ChatColor.RED +  "You already reached the maximum joined towns!");
								}
							}
							else {
								player.sendMessage("/town create <townname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("delete")) {
							if(args.length == 2) {
								
							}
							else {
								player.sendMessage("/town delete <townname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("expand")) {
							if(args.length == 2) {
								
							}
							else {
								player.sendMessage("/town expand <townname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("setTownSpawn")) {
							if(args.length == 5) {
								
							}
							else {
								player.sendMessage("/town setTownSpawn <townname> <x> <y> <z>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("setTax")) {
							if(args.length == 3) {
								
							}
							else {
								
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("townManager")) {
							
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("plot")) {
							
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					}
					else {
						player.sendMessage(ChatColor.RED + "You are not in a Townworld!");
					}
				}
				else {
					player.sendMessage("/town <create/delete/expand/setTownSpawn/setTax/townManager/plot>");
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////adminshop
			else if(label.equalsIgnoreCase("adminshop")) {
				if(player.isOp()) {
					if(args.length != 0) {
						if(args[0].equals("create")) {
							if(args.length == 3) {
								if(adminShopNames.contains(args[1])) {
									player.sendMessage(ChatColor.RED + "This shop already exists");
								}
								else {
									if(Integer.valueOf(args[2])%9 == 0) {
										AdminShop adminShop = new AdminShop(this,args[1],player,args[2]);
										adminShopNames.add(args[1]);
										adminShopList.add(adminShop);
										getConfig().set("ShopNames", adminShopNames);
										saveConfig();
										player.sendMessage(ChatColor.GOLD + "The shop " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " was created.");
									}
									else {
										player.sendMessage(ChatColor.RED + args[2] + " is not a multiple of 9!");
									}
								}
							}
							else {
								player.sendMessage("/adminshop create <shopname> <size (9,18,27...)>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("delete")) {
							if(args.length == 2) {
								if(adminShopNames.contains(args[1])) {
									adminShopNames.remove(args[1]);
									getConfig().set("ShopNames", adminShopNames);
									saveConfig();
									Iterator<AdminShop> iter2 = adminShopList.iterator();
									while(iter2.hasNext()) {
										AdminShop s = iter2.next();
										if(s.getName().equals(args[1])) {
											s.deleteShop();
											iter2.remove();
											player.sendMessage(ChatColor.GOLD + "The shop " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " was deleted."); break;
										}
									}
								}
								else {
									player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
								}
							}
							else {
								player.sendMessage("/adminshop delete <shopname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("move")) {
							if(args.length == 5) {
								boolean exists = false;
								for(AdminShop shop: adminShopList) {
									if(shop.getName().equals(args[1])) {
										exists = true;
										shop.moveShop(Integer.valueOf(args[2]), Integer.valueOf(args[3]), Integer.valueOf(args[4]));
										break;
									}
								}
								if(!exists) {
									player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
								}
							}
							else {
								player.sendMessage("/adminshop move <shopname> <x> <y> <z>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("editShop")) {
							if(args.length == 2) {
								boolean exists = false;
								for(AdminShop shop: adminShopList) {
									if(shop.getName().equals(args[1])) {
										exists = true;
										shop.openEditor(player);
										break;
									}
								}
								if(!exists) {
									player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
								}
							} 
							else {
								player.sendMessage("/adminshop editShop <shopname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("addItem")) {
							boolean exists = false;
							if(args.length == 7 && Integer.valueOf(args[3]) <= 64) {
								for(AdminShop shop5:adminShopList) {
									if(shop5.getName().equals(args[1])) {  
										exists = true;
										if(checkValidation(player,shop5,args[2],args[3], args[4], args[5], args[6])) {
											ItemStack itemStack = new ItemStack(Material.getMaterial(args[2].toUpperCase()),Integer.valueOf(args[4]));
											shop5.addItem(player, Integer.valueOf(args[3]) - 1, Double.valueOf(args[5]), Double.valueOf(args[6]), itemStack);
										}
										break;
									}
								}
								if(!exists) {
									player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
								}
							}
							else if(args.length != 7){
								player.sendMessage("/adminshop addItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice>");
								player.sendMessage("If you only want to buy/sell something set sell/buyPrice = 0.0");
							}	
							else {
								player.sendMessage(ChatColor.RED + "The item amount is highter then 64!");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("removeItem")) {
							if(args.length == 3 && Integer.valueOf(args[2]) >= 0) {
								boolean shopExists = false;
								boolean size = false;
								for(AdminShop s : adminShopList) {
									if(s.getName().equals(args[1])) {
										shopExists = true;
										s.removeItem(Integer.valueOf(args[2]) - 1, player);
										if(s.getSize() >= Integer.valueOf(args[2])) {
											size = true;
										}
										break;
									}
								}
								if(!shopExists) {
									player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
								}
								else if(!size) {
									player.sendMessage(ChatColor.RED + "This slot doesn't exist in this shop!");
								}
							}
							else {
								player.sendMessage("/adminshop removeItem <shopname> <slot (> 0)>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("addPotion")) {
							if(args.length == 9) {
								boolean shopExists = false;
								for(AdminShop s: adminShopList) {
									if(s.getName().equals(args[1])) {
										shopExists = true;
										handleAddPotion(s, args);
										break;
									}
								}
								if(!shopExists) {
									player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
								}
							}
							else {
								player.sendMessage("/adminshop addPotion <shopname> <potionType> <potionEffect> <extended/upgraded/none> <slot> <amount> <sellprice> <buyprice>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("addEnchantedItem")) {
							if(args.length >= 9) {
								boolean shopExists = false;
								for(AdminShop s: adminShopList) {
									if(s.getName().equals(args[1])) {
										shopExists = true;
										handleAddEnchantedItem(player,args, s);
										break;
									}
								}
								if(!shopExists) {
									player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
								}
							}
							else {
								player.sendMessage("/adminshop addEnchantedItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice> [<enchantment> <lvl>]");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("addSpawner")) {
							boolean exists = false;
							if(args.length == 5) {	
								for(AdminShop shop5:adminShopList) {
									if(shop5.getName().equals(args[1])) {
										exists = true;
										if(Integer.valueOf(args[3]) <= 0) {
											player.sendMessage(ChatColor.RED + "The slot should be higher then 0!");
										}
										else if(!shop5.slotIsEmpty(Integer.valueOf(args[3]))) {
											player.sendMessage(ChatColor.RED + "This slot is occupied!");
										}
										else if(Double.valueOf(args[4]) <= 0) {
											player.sendMessage(ChatColor.RED + "The buyPrice should higher then 0!");
										}
										else {
											ItemStack itemStack = new ItemStack(Material.SPAWNER,1);
											ItemMeta meta = itemStack.getItemMeta();
											meta.setDisplayName(args[2].toUpperCase());
											itemStack.setItemMeta(meta);
											shop5.addItem(player, Integer.valueOf(args[3]) - 1, 0.0,Double.valueOf(args[4]), itemStack);
										}
										break;
									}
								}
								if(!exists) {
									player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
								}
							}
							else {
								player.sendMessage("/adminshop addSpawner <shopname> <entity type> <slot> <buyPrice>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("removeSpawner")) {
							if(args.length == 3) {
								boolean exists = false;
								for(AdminShop shop5:adminShopList) {
									if(shop5.getName().equals(args[1])) {
										exists = true;
										if(Integer.valueOf(args[2]) <= 0) {
											player.sendMessage(ChatColor.RED + "The slot should be higher then 0!");
										}
										else {
											shop5.removeItem(Integer.valueOf(args[2]) - 1, player); 
										}
										break;
									}
								}
								if(!exists) {
									player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
								}
							}
							else {
								player.sendMessage("/adminshop removeSpawner <shopname> <slot>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("editItem")) {
							if(args.length == 6) {
								boolean exists = false;	
								for(AdminShop shop5:adminShopList) {
									if(shop5.getName().equals(args[1])) {
										exists = true;
										if(Integer.valueOf(args[2]) <= 0) {
											player.sendMessage(ChatColor.RED + "The slot should be higher then 0!");
										}
										else if(shop5.slotIsEmpty(Integer.valueOf(args[2]))) {
											player.sendMessage(ChatColor.RED + "This slot is empty!");
										}
										else if(!args[3].equals("none") && Integer.valueOf(args[3]) <= 0) {
											player.sendMessage(ChatColor.RED + "The amount should be higher then 0!");
										}
										else if(!args[4].equals("none") && Integer.valueOf(args[4]) < 0) {
											player.sendMessage(ChatColor.RED + "The sellPrice should be 0 or highter!");
										}
										else if(!args[5].equals("none") && Integer.valueOf(args[5]) < 0) {
											player.sendMessage(ChatColor.RED + "The buyPrice should be 0 or highter!");
										}
										else {
											player.sendMessage(shop5.editItem(Integer.valueOf(args[2]),args[3],args[4],args[5]));
										}
									}
									break;
								}
								if(!exists) {
									player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
								}
							}
							else {
								player.sendMessage("/adminshop editItem <shopname> <slot> <amount> <sellPrice> <buyPrice>");
								player.sendMessage("If you didn't want to change one of these values set the value = none");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else {
							player.sendMessage("/adminshop <create/delete/move/editShop/addItem/addEnchantedItem/addPotion/editItem/removeItem/addSpawner/removeSpawner>");
						}
					}
					else {
						player.sendMessage("/adminshop <create/delete/move/editShop/addItem/addEnchantedItem/addPotion/editItem/removeItem/addSpawner/removeSpawner>");
					}
				}
			}
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////playershop
			if(label.equalsIgnoreCase("playershop")) {
				if(args.length != 0) {
					if(args[0].equals("create")) {
						if(args.length == 3) {
							if(playershopNames.contains(args[1] + "_" + player.getName())) {
								player.sendMessage(ChatColor.RED + "This shop already exist!");
							}
							else {
								if(Integer.valueOf(args[2])%9 == 0) {
									PlayerShop shop = new PlayerShop(this, args[1] + "_" + player.getName(), player,args[2]);
									playerShopList.add(shop);
									playershopNames.add(args[1] + "_" + player.getName());
									getConfig().set("PlayerShopNames", playershopNames);
									saveConfig();
									player.sendMessage(ChatColor.GOLD + "The shop " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " was created.");
								}
								else {
									player.sendMessage(ChatColor.RED + args[2] + " is not a multiple of 9!");
								}
							}
						}
						else {
							player.sendMessage("/playershop create <shopname> <size (9,18,27...)>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (args[0].equals("delete")) {
						if(args.length == 2) {
							Iterator<PlayerShop> iter2 = playerShopList.iterator();
							if(playershopNames.contains(args[1] + "_" + player.getName())) {
								playershopNames.remove(args[1] + "_" + player.getName());
								getConfig().set("PlayerShopNames", adminShopNames);
								saveConfig(); 
								while(iter2.hasNext()) {
									PlayerShop s = iter2.next();
									if(s.getName().equals(args[1] + "_" + player.getName())) {
										s.deleteShop();
										iter2.remove();
										player.sendMessage(ChatColor.GOLD + "The shop " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " was deleted."); break;
									}
								}
							}
							else {
								player.sendMessage(ChatColor.RED +"This shop doesn't exist!");
							}
						}
						else {
							player.sendMessage("/playershop delete <shopname>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if(args[0].equals("move")) {
						if(args.length == 5) {
							boolean exists = false;
							for(PlayerShop shop: playerShopList) {
								if(shop.getName().equals(args[1] + "_" + player.getName())) {
									exists = true;
									shop.moveShop(Integer.valueOf(args[2]), Integer.valueOf(args[3]), Integer.valueOf(args[4]));
									break;
								}
							}
							if(!exists) {
								player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
							}
						}
						else {
							player.sendMessage("/playershop move <shopname> <x> <y> <z>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if(args[0].equals("changeOwner")) {
						if(args.length == 3) {
							boolean exists = false;
							for(PlayerShop shop: playerShopList) {
								if(shop.getName().equals(args[1] + "_" + player.getName())) {
									exists = true;
									config = YamlConfiguration.loadConfiguration(playerFile);
									List<String> playerList = config.getStringList("Player");
									if(playerList.contains(args[2])) {
										List<String> psNames = getConfig().getStringList("PlayerShopNames");
										if(psNames.contains(args[1] + "_" + args[2])) {
											player.sendMessage(ChatColor.RED + "The player " + ChatColor.GREEN + args[2] + ChatColor.RED + " has already a shop with the same name!");
										}
										else {
											playershopNames.remove(shop.getName());
											shop.setOwner(args[2]);
											playershopNames.add(shop.getName());
											getConfig().set("PlayerShopNames", playershopNames);
											saveConfig();
											player.sendMessage(ChatColor.GOLD + "The new owner of your shop is " + ChatColor.GREEN + args[2] + ChatColor.GOLD + ".");
											for(Player pl:Bukkit.getOnlinePlayers()) {
												if(pl.getName().equals(args[2])) {
													pl.sendMessage(ChatColor.GOLD + "You got the shop " + ChatColor.GREEN  + args[1] + ChatColor.GOLD + "from " + ChatColor.GREEN + player.getName()); break;
												}
											}
										}
									}
									else {
										player.sendMessage(ChatColor.RED + "This player was never on this server!");
									}
									break;
								}
							}
							if(!exists) {
								player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
							}
						}
						else {
							player.sendMessage("/playershop changeOwner <shopname> <new owner>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if(args[0].equals("editShop")) {
						if(args.length == 2) {
							boolean exists = false;
							for(PlayerShop shop: playerShopList) {
								if(shop.getName().equals(args[1] + "_" + player.getName())) {
									exists = true;
									shop.openEditor(player);
									break;
								}
							}
							if(!exists) {
								player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
							}
						} 
						else {
							player.sendMessage("/player editShop <shopname>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (args[0].equals("addItem")) {
						boolean exists = false;
						if(args.length == 7 && Integer.valueOf(args[3]) <= 64) {
							for(PlayerShop shop5:playerShopList) {
								if(shop5.getName().equals(args[1] + "_" + player.getName())) {  
									exists = true;
									if(checkValidation(player, shop5,args[2], args[3], args[4], args[5], args[6])) {
										ItemStack itemStack = new ItemStack(Material.getMaterial(args[2].toUpperCase()),Integer.valueOf(args[4]));
										shop5.addItem(player, Integer.valueOf(args[3]) - 1, Double.valueOf(args[5]), Double.valueOf(args[6]), itemStack);
									}
									break;
								}
							}
							if(!exists) {
								player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
							}
						}
						else if(args.length != 7){
							player.sendMessage("/playershop addItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice>");
							player.sendMessage("If you only want to buy/sell something set sell/buyPrice = 0.0");
						}	
						else {
							player.sendMessage(ChatColor.RED + "The item amount is highter then 64!");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if(args[0].equals("addPotion")) {
						if(args.length == 9) {
							boolean shopExists = false;
							for(PlayerShop s: playerShopList) {
								if(s.getName().equals(args[1] + "_" + player.getName())) {
									shopExists = true;
									handleAddPotion(s,args);
									break;
								}
							}
							if(!shopExists) {
								player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
							}
						}
						else {
							player.sendMessage("/playershop addPotion <shopname> <potionType> <potionEffect> <extended/upgraded/none> <slot> <amount> <sellprice> <buyprice>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if(args[0].equals("addEnchantedItem")) {
						if(args.length >= 9) {
							boolean shopExists = false;
							for(PlayerShop s: playerShopList) {
								if(s.getName().equals(args[1] + "_" + player.getName())) {
									shopExists = true;
									handleAddEnchantedItem(player,args, s);
									break;
								}
							}
							if(!shopExists) {
								player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
							}
						}
						else {
							player.sendMessage("/playershop addEnchantedItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice> [<enchantment> <lvl>]");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (args[0].equals("removeItem")) {
						if(args.length == 3 && Integer.valueOf(args[2]) >= 0) {
							boolean shopExists = false;
							boolean size = false;
							for(PlayerShop s : playerShopList) {
								if(s.getName().equals(args[1] + "_" + player.getName())) {
									shopExists = true;
									if(Integer.valueOf(args[2]) <= 0) {
										player.sendMessage(ChatColor.RED + "The slot should be higher then 0!");
									}
									else {
										s.removeItem(Integer.valueOf(args[2]) - 1, player);
										if(s.getSize() >= Integer.valueOf(args[2])) {
											size = true;
										}
									}
									break;
								}
							}
							if(!shopExists) {
								player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
							}
							else if(!size) {
								player.sendMessage(ChatColor.RED + "This slot doesn't exist in this shop!");
							}
						}
						else {
							player.sendMessage("/playershop removeItem <shopname> <slot (> 0)>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if(args[0].equals("editItem")) {
						if(args.length == 6) {
							boolean exists = false;
							for(PlayerShop shop5:playerShopList) {
								if(shop5.getName().equals(args[1] + "_" + player.getName())) {
									exists = true;
									if(Integer.valueOf(args[2]) <= 0) {
										player.sendMessage(ChatColor.RED + "The slot should be higher then 0!");
									}
									else if(shop5.slotIsEmpty(Integer.valueOf(args[2]))) {
										player.sendMessage(ChatColor.RED + "This slot is empty!");
									}
									else if(!args[3].equals("none") && Integer.valueOf(args[3]) <= 0) {
										player.sendMessage(ChatColor.RED + "The amount should be higher then 0!");
									}
									else if(!args[4].equals("none") && Integer.valueOf(args[4]) < 0) {
										player.sendMessage(ChatColor.RED + "The sellPrice should be 0 or highter!");
									}
									else if(!args[5].equals("none") && Integer.valueOf(args[5]) < 0) {
										player.sendMessage(ChatColor.RED + "The buyPrice should be 0 or highter!");
									}
									else {
										player.sendMessage(shop5.editItem(Integer.valueOf(args[2]),args[3],args[4],args[5]));
									}
								}
								break;
							}
							if(!exists) {
								player.sendMessage(ChatColor.RED + "This shop doesn't exist!");
							}
						}
						else {
							player.sendMessage("/playershop editItem <shopname> <slot> <amount> <sellPrice> <buyPrice>");
							player.sendMessage("If you didn't want to change one of these value set the value = none");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else {
						player.sendMessage("/playershop <create/delete/move/editShop/addItem/addEnchantedItem/addPotion/removeItem/editItem>");
					}
				}
				else {
					player.sendMessage("/playershop <create/delete/move/editShop/addItem/addEnchantedItem/addPotion/removeItem/editItem>");
				}
			}
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////Jobcenter
			if(label.equalsIgnoreCase("jobcenter")) {
				if(player.isOp()) {
					if(args.length != 0) {
						if(args[0].equals("create")) {
							if(args.length == 3) {
								if(jobCenterNames.contains(args[1])) {
									player.sendMessage(ChatColor.RED + "This jobcenter already exists!");
								}
								else {
									if(Integer.valueOf(args[2])%9 == 0) {
										JobCenter jobCenter = new JobCenter(this,args[1],player,Integer.parseInt(args[2]));
										jobCenterNames.add(args[1]);
										jobCenterList.add(jobCenter);
										getConfig().set("JobCenterNames", jobCenterNames);
										saveConfig();
										player.sendMessage(ChatColor.GOLD + "The shopcenter " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " was created.");
									}
									else {
										player.sendMessage(ChatColor.RED + args[2] + " is not a multiple of 9!");
									}
								}
							}
							else {
								player.sendMessage("/jobcenter create <name> <size (9,18,27...)>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("delete")) {
							if(args.length == 2) {
								Iterator<JobCenter> iter2 = jobCenterList.iterator();
								if(jobCenterNames.contains(args[1])) {
									jobCenterNames.remove(args[1]);
									getConfig().set("JobCenterNames", jobCenterNames);
									saveConfig();
									while(iter2.hasNext()) {
										JobCenter s = iter2.next();
										if(s.getName().equals(args[1])) {
											s.deleteShop();
											iter2.remove();
											player.sendMessage(ChatColor.GOLD + "The shopcenter " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " was deleted."); break;
										}
									}
								}
								else {
									player.sendMessage(ChatColor.RED + "This jobcenter doesn't exist!");
								}
							}
							else {
								player.sendMessage("/jobcenter delete <name>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("move")) {
							if(args.length == 5) {
								boolean exists = false;
								for(JobCenter jobcenter:jobCenterList) {
									if(jobcenter.getName().equals(args[1])) {
										exists = true;
										jobcenter.moveShop(Integer.valueOf(args[2]), Integer.valueOf(args[3]), Integer.valueOf(args[4]));
									}
								}
								if(!exists) {
									player.sendMessage(ChatColor.RED + "This jobcenter doesn't exist!");
								}
							}
							else {
								player.sendMessage("/jobcenter move <name> <x> <y> <z>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("addJob")) {
							if(args.length == 5) {
								boolean exist = false;
								for(JobCenter jobcenter:jobCenterList) {
									if(jobcenter.getName().equals(args[1])) {
										exist = true;
										if(!jobcenter.hasJob(args[2])) {
											if(Integer.valueOf(args[4]) <= 0) {
												player.sendMessage(ChatColor.RED + "The slot should be higher then 0!");
											}
											else if(Material.matchMaterial(args[3].toUpperCase()) == null){
												player.sendMessage(ChatColor.RED + "Invalid material!");
											}
											else {
												jobcenter.addJob(args[2], Material.valueOf(args[3].toUpperCase()), Integer.valueOf(args[4]),player);
											}
										}
										else {
											player.sendMessage(ChatColor.RED + "This job already exist in this jobcenter!");
										}
										break;
									}
								}
								if(!exist) {
									player.sendMessage(ChatColor.RED + "This jobcenter doesn't exist!");
								}
							}
							else {
								player.sendMessage("/jobcenter addJob <jobcentername> <jobname> <material> <slot>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("removeJob")) {
							if(args.length == 3) {
								Iterator<String> iter = jobList.iterator();
								Iterator<Job> iter2 = jobs.iterator();
								boolean exists = false;
								boolean centerExist = false;
								for(JobCenter jc: jobCenterList) {
									if(jc.getName().equals(args[1])) {
										centerExist = true;
										while(iter.hasNext()) {
											if(args[2].equals(iter.next())) {
												exists = true; 
												int inCenters = 0;
												for(JobCenter jc2: jobCenterList) {
													if(jc2.hasJob(args[2])) {
														inCenters++;
													}
												}
												if(inCenters <= 1) {
													config = YamlConfiguration.loadConfiguration(playerFile);
													List<String> pList = config.getStringList("Player");
													for(String p : pList) {
														List<String> jobs = config.getStringList(p + ".Jobs");
														Iterator<String> iter3 = jobs.iterator();
														while(iter3.hasNext()) {
															if(iter3.next().equals(args[2])) {
																jobs.remove(args[2]);
																config.set(p + ".Jobs", jobs);
																saveFile(playerFile);
																break;
															}
														}
													}
												}
												while(iter2.hasNext()) {
													Job s = iter2.next();
													if(s.getName().equals(args[2])) {
														for(JobCenter jCenter : jobCenterList) {
															if(jCenter.getName().equals(args[1])) {
																if(jCenter.hasJob(args[2])) {
																	jCenter.removeJob(args[2]);
																	player.sendMessage(ChatColor.GOLD + "The job " + ChatColor.GREEN + args[2] + ChatColor.GOLD + " was removed.");
																}
																else {
																	player.sendMessage(ChatColor.RED + "This job was not added to this jobcenter!");
																}
															}
														}
													}
												}
												break;
											}
										}
										if(!exists) {		
											player.sendMessage(ChatColor.RED + "This job doesn't exists!");
										}
										break;
									}
								}
								if(!centerExist) {
									player.sendMessage(ChatColor.RED + "This jobcenter doesn't exists!");
								}
							}
							else {
								player.sendMessage("/jobcenter removeJob <jobcentername> <jobname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("job")) {
							if(args.length == 1) {
								player.sendMessage("/jobcenter job <createJob/delJob/addItem/removeItem/addMob/removeMob/addFisher/delFisher>");
							}
							else {
								if(args[1].equals("createJob")) {
									if(args.length == 3) {
										if(jobList.contains(args[2])) {
											player.sendMessage(ChatColor.RED + "This job already exists!");
										}
										else {
											jobList.add(args[2]);
											getConfig().set("JobList", jobList);
											saveConfig();
											Job job = new Job(this.getDataFolder(), args[2]);
											jobs.add(job);
											player.sendMessage(ChatColor.GOLD + "The job " + ChatColor.GREEN + args[2] + ChatColor.GOLD + " was created.");
										}
									}
									else {
										player.sendMessage("/jobcenter job createJob <jobname>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if(args[1].equals("delJob")) {
									if(args.length == 3) {
										if(jobList.contains(args[2])) {
											jobList.remove(args[2]);
											getConfig().set("JobList", jobList);
											saveConfig();
											config = YamlConfiguration.loadConfiguration(playerFile);
											List<String> pList = config.getStringList("Player");
											for(String p : pList) {
												List<String> jobs = config.getStringList(p + ".Jobs");
												if(jobs.contains(args[2])) {
													jobs.remove(args[2]);
													config.set(p + ".Jobs", jobs);
													saveFile(playerFile);
												}
											}
											Iterator<Job> iter2 = jobs.iterator();
											while(iter2.hasNext()) {
												Job s = iter2.next();
												if(s.getName().equals(args[2])) {
													s.deleteJob();
													iter2.remove();
													for(JobCenter jCenter : jobCenterList) {
														if(jCenter.hasJob(args[2])) {
															jCenter.removeJob(args[2]);
														}
													}
													player.sendMessage(ChatColor.GOLD + "The job " + ChatColor.GREEN + args[2] + ChatColor.GOLD + " was deleted.");
												}
											}
										}
										else {
											player.sendMessage(ChatColor.RED + "This job doesn't exists!");
										}
									}
									else {
										player.sendMessage("/jobcenter job delJob <jobname>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if(args[1].equals("addMob")) {
									if(args.length == 5) {
										boolean exist = false;
										for(Job job:jobs) {
											if(job.getName().equals(args[2]) && !exist) {
												exist = true;
												if(Double.valueOf(args[4]) <= 0) {
													player.sendMessage(ChatColor.RED + "The price should be heigher then 0");
												}
												else {
													try {
														boolean success = job.addMob(EntityType.valueOf(args[3].toUpperCase()), Double.valueOf(args[4]));
														if(success) {
															player.sendMessage(ChatColor.GOLD + "The entity " + ChatColor.GREEN + args[3] + ChatColor.GOLD + " was added to the job " + ChatColor.GREEN + job.getName() + ".");
														}
														else {
															player.sendMessage(ChatColor.RED + "The entity already exists in this job!");
														}
													} catch(IllegalArgumentException e) {
														player.sendMessage(ChatColor.RED + "Invalid entity!");
													}
												}
												break;
											}
										}
										if(!exist) {
											player.sendMessage(ChatColor.RED + "This Jobs doesn't exist!");
										}
									}
									else {
										player.sendMessage("/jobcenter job addMob <jobname> <entity> <price>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if(args[1].equals("removeMob")) {
									if(args.length == 4) {
										boolean exist = false;
										for(Job job:jobs) {
											if(job.getName().equals(args[2]) && !exist) {
												exist = true;
												try {
													boolean success = job.deleteMob(EntityType.valueOf(args[3].toUpperCase()));
													if(success) {
														player.sendMessage(ChatColor.GOLD + "The entity " + ChatColor.GREEN + args[3] + ChatColor.GOLD + " was deleted from the job " + ChatColor.GREEN + job.getName() + ".");
													}
													else {
														player.sendMessage(ChatColor.RED + "This entity doesn't exist in this job!");
													}
												} catch (IllegalArgumentException e) {
													player.sendMessage(ChatColor.RED + "Invalid entity!");
												}
												break;
											}
										}
										if(!exist) {
											player.sendMessage(ChatColor.RED + "This job doesn't exist!");
										}
									}
									else {
										player.sendMessage("/jobcenter job removeMob <jobname> <entity>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if(args[1].equals("addItem")) {
									if(args.length == 5) {
										boolean exist = false;
										for(Job job:jobs) {
											if(job.getName().equals(args[2]) && !exist) {
												exist = true;
												if(Double.valueOf(args[4]) <= 0) {
													player.sendMessage(ChatColor.RED + "The price should be heigher then 0");
												}
												else if(Material.matchMaterial(args[3].toUpperCase()) == null){
													player.sendMessage(ChatColor.RED + "Invalid material!");
												}
												else {
													boolean success = job.addItem(Material.valueOf(args[3].toUpperCase()),Double.valueOf(args[4]));
													if(success) {
														player.sendMessage(ChatColor.GOLD + "The item " + ChatColor.GREEN + args[3] + ChatColor.GOLD + " was added to the job " + ChatColor.GREEN + job.getName() + ".");
													}
													else {
														player.sendMessage(ChatColor.RED + "The item already exists in this job!");
													}
												}
												break;
											}
										}
										if(!exist) {
											player.sendMessage(ChatColor.RED + "This Jobs doesn't exist!");
										}
									}
									else {
										player.sendMessage("/jobcenter job addItem <jobname> <material> <price>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if(args[1].equals("removeItem")) {
									if(args.length == 4) {
										boolean exist = false;
										for(Job job:jobs) {
											if(job.getName().equals(args[2]) && !exist) {
												exist = true;
												if(Material.matchMaterial(args[3].toUpperCase()) != null) {
													boolean success = job.deleteItem(Material.valueOf(args[3].toUpperCase()));
													if(success) {
														player.sendMessage(ChatColor.GOLD + "The item " + ChatColor.GREEN + args[3] + ChatColor.GOLD + " was deleted from the job " + ChatColor.GREEN + job.getName() + ".");
													}		
													else {
														player.sendMessage(ChatColor.RED + "This item doesn't exist in this job!");
													}
												}
												else {
													player.sendMessage(ChatColor.RED + "Invalid material!");
												}
												break;
											}
										}
										if(!exist) {
											player.sendMessage(ChatColor.RED + "This job doesn't exist!");
										}
									}
									else {
										player.sendMessage("/jobcenter job removeItem <jobname> <material>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if(args[1].equals("addFisher")) {
									if(args.length == 5) {
										boolean exist = false;
										for(Job job:jobs) {
											if(job.getName().equals(args[2]) && !exist) {
												exist = true;
												if(Double.valueOf(args[4]) <= 0) {
													player.sendMessage(ChatColor.RED + "The price should be heigher then 0!");
												}
												else if(!args[3].equals("treasure") && !args[3].equals("junk") && !args[3].equals("fish")){
													player.sendMessage(ChatColor.RED + "Invalid input -> <fish/treasure/junk>!");
												}
												else {
													boolean success = job.addFisher(args[3], Double.valueOf(args[4]));
													if(success) {
														player.sendMessage(ChatColor.GOLD + "The loot " + ChatColor.GREEN + args[3] + ChatColor.GOLD + " was added to the job " + ChatColor.GREEN + job.getName() + ".");
													}
													else {
														player.sendMessage(ChatColor.RED + "This loot already exists in this job!");
													}
												}
												break;
											}
										}
										if(!exist) {
											player.sendMessage(ChatColor.RED + "This Jobs doesn't exist!");
										}
									}
									else {
										player.sendMessage("/jobcenter job addFisher <jobname> <fish/treasure/junk> <price>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if(args[1].equals("delFisher")) {
									if(args.length == 4) {
										boolean exist = false;
										for(Job job:jobs) {
											if(job.getName().equals(args[2]) && !exist) {
												exist = true;
												if(!args[3].equals("treasure") && !args[3].equals("junk") && !args[3].equals("fish")){
													player.sendMessage(ChatColor.RED + "Invalid loottable -> <fish/treasure/junk>!");
												}
												else {
													boolean success = job.delFisher(args[3]);
													if(success) {
														player.sendMessage(ChatColor.GOLD + "The loottable " + ChatColor.GREEN + args[3] + ChatColor.GOLD + " was removed to the job " + ChatColor.GREEN + job.getName() + ".");
													}
													else {
														player.sendMessage(ChatColor.RED + "This loot doesn't exist in this shop!");
													}
												}
												break;
											}
										}
										if(!exist) {
											player.sendMessage(ChatColor.RED + "This Jobs doesn't exist!");
										}
									}
									else {
										player.sendMessage("/jobcenter job delFisher <jobname> <fish/treasure/junk>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else {
									player.sendMessage("/jobcenter job <createJob/delJob/addItem/addFisher/delFisher/removeItem/addMob/removeMob>");
								}
							}
						}
						else {
							player.sendMessage("/jobcenter <create/delete/move/job/addJob>");
						}
					}
					else {
						player.sendMessage("/jobcenter <create/delete/move/job/addjob>");
					}
				}
			}
		}
		return false;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Events
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void NPCOpenInv(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		if(entity instanceof Villager) {
			String name1 = entity.getCustomName();
			for(AdminShop shop4:adminShopList) {
				if(shop4.getName().equals(name1)) {   
				event.setCancelled(true);
				shop4.openInv(event.getPlayer());
				}
			}
			for(PlayerShop shop4:playerShopList) {
				if(shop4.getName().equals(name1)) {   
				event.setCancelled(true);
				shop4.openInv(event.getPlayer());
				}
			}
			for(JobCenter jobcenter:jobCenterList) {
				if(jobcenter.getName().equals(name1)) {
					event.setCancelled(true);
					jobcenter.openInv(event.getPlayer());
				}
			}
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void HitVillagerEvent(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Villager) {
			String entityname = event.getEntity().getCustomName();
			Player damager = (Player) event.getDamager();
			if(jobCenterNames.contains(entityname)) {
				event.setCancelled(true);
				damager.sendMessage(ChatColor.RED + "You are not allowed to hit a jobCenterVillager!");
			}
			else if(adminShopNames.contains(entityname)) {
				event.setCancelled(true);
				damager.sendMessage(ChatColor.RED + "You are not allowed to hit a shopVillager!");
			}
			else if(playershopNames.contains(entityname)) {
				event.setCancelled(true);
				damager.sendMessage(ChatColor.RED + "You are not allowed to hit a shopVillager!");
			}
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if(entity.getKiller() instanceof Player) {
			Player player = entity.getKiller();
			config = YamlConfiguration.loadConfiguration(playerFile);
			List<String> list = config.getStringList(player.getName() + ".Jobs");
			if(!list.isEmpty() && player.getGameMode() == GameMode.SURVIVAL) {
				for(String job: list) {
					config = YamlConfiguration.loadConfiguration(new File(getDataFolder() ,job + "-Job.yml"));
					List<String> entitylist = config.getStringList("Entitylist");
					if(entitylist.contains(entity.getType().toString())) {
						double d = config.getDouble("JobEntitys." + entity.getType().toString() + ".killprice");
						config = YamlConfiguration.loadConfiguration(playerFile);
						config.set(player.getName() + ".account amount", config.getDouble(player.getName() + ".account amount") + d);
						saveFile(playerFile);
						updateScoreBoard(player);
						break;
					}
				}
			}		
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void InvClickEvent(InventoryClickEvent event) {
		Player playe = (Player) event.getWhoClicked();
		if(event.getCurrentItem() != null && event.getInventory().getType() == InventoryType.ANVIL && event.getCurrentItem().getType() == Material.SPAWNER) {
			event.setCancelled(true);
		}
		String inventoryname = event.getInventory().getName();
		if(event.getInventory().getType() == InventoryType.CHEST && event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null) {
			ItemMeta meta = event.getCurrentItem().getItemMeta();
////////////////////////////////////////////////////////////////////////////editor
			if(inventoryname.contains("Editor")) {
				event.setCancelled(true);
				if(meta.getDisplayName() != null) {
					for(AdminShop adminShop: adminShopList) {
						handleEditor(playe, adminShop, event);
					}
					for(PlayerShop playerShop: playerShopList) {
						handleEditor(playe, playerShop, event);
					}
				}
			}
////////////////////////////////////////////////////////////////////////////
			ClickType clickType = event.getClick();
			if(event.getCurrentItem().getItemMeta() != null) {
////////////////////////////////////////////////////////////////////////////Job
				for(JobCenter jobCenter:jobCenterList) {
					if(jobCenter.getName().equals(inventoryname)) {
						event.setCancelled(true);
						String displayname = meta.getDisplayName();
						if(clickType == ClickType.RIGHT && displayname != null) {
							config = YamlConfiguration.loadConfiguration(playerFile);
							if(!config.getStringList(playe.getName() + ".Jobs").isEmpty()) {
								List<String> joblist = config.getStringList(playe.getName() + ".Jobs");
								if(joblist.contains(displayname)) {
									joblist.remove(displayname);
									playe.sendMessage(ChatColor.GOLD + "You have left the job " + ChatColor.GREEN + displayname);
									config.set(playe.getName() + ".Jobs", joblist);
								}
								else {
									playe.sendMessage(ChatColor.RED + "You didn't joined this job yet!");
								}		
							}
							else if(!(displayname.equals("Info"))){
								List<String> strings = new ArrayList<>();
								config.set(playe.getName() + ".Jobs", strings);
								playe.sendMessage(ChatColor.RED + "You didn't joined a job yet!");
							}
							saveFile(playerFile);
						}
						else if(clickType == ClickType.LEFT && displayname != null) {
							config = YamlConfiguration.loadConfiguration(playerFile);
							List<String> joblist = new ArrayList<>();
							if(!config.getStringList(playe.getName() + ".Jobs").isEmpty()) {
								joblist = config.getStringList(playe.getName() + ".Jobs");
							}
							else {
								config.set(playe.getName() + ".Jobs", joblist);
							}
							if(joblist.size() < getConfig().getInt("MaxJobs")) {
								if(!joblist.contains(displayname) && !(displayname.equals("Info"))) {
									joblist.add(displayname);
									config.set(playe.getName() + ".Jobs",joblist);
									saveFile(playerFile);
									playe.sendMessage(ChatColor.GOLD + "You have joined the job " + ChatColor.GREEN + displayname);
								}
								else if(!(displayname.equals("Info"))){
									playe.sendMessage(ChatColor.RED + "You already joined this job!");
								}
							}
							else {
								playe.sendMessage(ChatColor.RED + "You have already reached the maximum number of jobs!");				
							}
						}
						break;
					}
				}
////////////////////////////////////////////////////////////////////////////playershop
				for(PlayerShop playerShop:playerShopList) {
					handleBuySell(playerShop, event, playe);
				}
//////////////////////////////////////////////////////////////////////////// adminshop
				for(AdminShop adminShop:adminShopList) {
					handleBuySell(adminShop, event, playe);
				}
			}
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void setBlockEvent(BlockPlaceEvent event) {
		if(event.getBlock().getBlockData().getMaterial() == Material.SPAWNER) {
			if(event.getItemInHand().getItemMeta().getDisplayName().contains("-") && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
				String spawnerowner = event.getItemInHand().getItemMeta().getDisplayName().substring(event.getItemInHand().getItemMeta().getDisplayName().lastIndexOf("-") + 1);
				if(spawnerowner.equals(event.getPlayer().getName())) {
						String string = event.getItemInHand().getItemMeta().getDisplayName();
						Spawner.setSpawner(EntityType.valueOf(string.substring(0,string.lastIndexOf("-"))), event.getBlock());
						event.getBlock().setMetadata("name", new FixedMetadataValue(this, string.substring(string.lastIndexOf("-") + 1)));
						event.getBlock().setMetadata("entity", new FixedMetadataValue(this, string.substring(0,string.lastIndexOf("-"))));
						config = YamlConfiguration.loadConfiguration(spawner);
						double x = event.getBlock().getX();
						double y = event.getBlock().getY();
						double z = event.getBlock().getZ();
						String spawnername = String.valueOf(x) + String.valueOf(y) + String.valueOf(z);
						spawnername = spawnername.replace(".", "-");
						spawnerlist.add(spawnername);
						getConfig().set("Spawnerlist", spawnerlist);
						saveConfig();
						config.set(spawnername + ".X", x);
						config.set(spawnername + ".Y", y);
						config.set(spawnername + ".Z", z);
						config.set(spawnername + ".World", event.getBlock().getWorld().getName());
						config.set(spawnername + ".player",string.substring(string.lastIndexOf("-") + 1));
						config.set(spawnername + ".EntityType", string.substring(0,string.lastIndexOf("-")));
						saveFile(spawner);
					}
				else {
					event.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to place this spawner!");
					event.setCancelled(true);
				}
			}
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void breakBlockEvent(BlockBreakEvent event) {
		if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			config = YamlConfiguration.loadConfiguration(playerFile);
			if(isTownWorld(event.getBlock().getWorld().getName())) {
				TownWorld townWorld = getTownWorld(event.getBlock().getWorld().getName());
				if(townWorld.chunkIsFree(event.getBlock().getChunk())) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "You are in the wilderness!");
				}
				else if(townWorld.isPlayerCitizen(townWorld.getTownNameByChunk(event.getBlock().getChunk()), event.getPlayer().getName())){
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "You are not a citizen of this town!");
				}
			}
			else {
				List<String> list = config.getStringList(event.getPlayer().getName() + ".Jobs");
				if(!list.isEmpty()) {
					String eventblockname = event.getBlock().getBlockData().getMaterial().toString();
					for(String job:list) {
						config = YamlConfiguration.loadConfiguration(new File(getDataFolder() ,job + "-Job.yml"));
						List<String> itemlist = config.getStringList("Itemlist");
						if(itemlist.contains(eventblockname)) {
							Material material = event.getBlock().getBlockData().getMaterial();
							if(material == Material.POTATOES || material == Material.CARROTS ||material == Material.WHEAT || material == Material.NETHER_WART_BLOCK || material == Material.BEETROOTS) {
								Ageable ageable = (Ageable) event.getBlock().getBlockData();
								if(ageable.getAge() == ageable.getMaximumAge()) {
									double d = config.getDouble("JobItems." + eventblockname + ".breakprice");
									config = YamlConfiguration.loadConfiguration(playerFile);
									config.set(event.getPlayer().getName() + ".account amount", config.getDouble(event.getPlayer().getName() + ".account amount") + d);
									saveFile(playerFile);
									updateScoreBoard(event.getPlayer());
								}	
							}
							else {
								double d = config.getDouble("JobItems." + eventblockname + ".breakprice");
								config = YamlConfiguration.loadConfiguration(playerFile);
								config.set(event.getPlayer().getName() + ".account amount", config.getDouble(event.getPlayer().getName() + ".account amount") + d);
								saveFile(playerFile);
								updateScoreBoard(event.getPlayer());
							}
							break;
						}
					}
				}
				if(event.getBlock().getBlockData().getMaterial() == Material.SPAWNER) {
					List<MetadataValue> blockmeta = event.getBlock().getMetadata("name");
					if(!blockmeta.isEmpty()) {
						MetadataValue s = blockmeta.get(0);
						String blockname = s.asString();
						if(event.getPlayer().getName().equals(blockname)) {
							if(!event.getBlock().getMetadata("entity").isEmpty()) {
								config = YamlConfiguration.loadConfiguration(spawner);
								double x = event.getBlock().getX();
								double y = event.getBlock().getY();
								double z = event.getBlock().getZ();
								String spawnername = String.valueOf(x) + String.valueOf(y) + String.valueOf(z);
								spawnername = spawnername.replace(".", "-");
								spawnerlist.remove(spawnername);
								getConfig().set("Spawnerlist", spawnerlist);
								saveConfig();
								config.set(spawnername, null);
								saveFile(spawner);
								ItemStack stack = new ItemStack(Material.SPAWNER, 1);
								ItemMeta meta = stack.getItemMeta();
								meta.setDisplayName(event.getBlock().getMetadata("entity").get(0).asString() + "-" + event.getPlayer().getName());     
								stack.setItemMeta(meta);
								event.getPlayer().getInventory().addItem(stack);
							}
						}
						else {
							event.setCancelled(true);
							event.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to break this spawner!");
						}
					}
				}
			}
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void JoinEvent(PlayerJoinEvent event) {
		int score = 0;
		config = YamlConfiguration.loadConfiguration(playerFile);
		String playername = event.getPlayer().getName();
		if(playerlist.contains(playername)) {
			config = YamlConfiguration.loadConfiguration(playerFile); 
			score = (int) config.getDouble(playername + ".account amount");
		}
		else {
			playerlist.add(playername);
			score = 0;	
			config.set("Player", playerlist);
			config.set(playername + ".bank", true);
			config.set(playername + ".account amount", 0.0);
			saveFile(playerFile);
		}	
		setScoreboard(event.getPlayer(),score);
		
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void FishingEvent(PlayerFishEvent event) {
		config = YamlConfiguration.loadConfiguration(playerFile);
		List<String> list = config.getStringList(event.getPlayer().getName() + ".Jobs");
		if(!list.isEmpty()) {
			int lootType = 0;
			Item caught =(Item) event.getCaught();
			if(caught != null) {
				switch(caught.getItemStack().getType().toString()) {
				case "COD": 
				case "SALMON": 
				case "TROPICAL_FISH":
				case "PUFFERFISH": lootType = 1; break;
				case "BOW": 
				case "ENCHANTED_BOOK": 
				case "FISHING_ROD":
				case "NAME_TAG": 
				case "NAUTILUS_SHELL": 
				case "SADDLE":
				case "LILY_PAD": lootType = 2; break;
				default: lootType = 3;
				}
				for(String job:list) {
					config = YamlConfiguration.loadConfiguration(new File(getDataFolder() ,job + "-Job.yml"));
					Double price = null;
					switch(lootType) {
					case 1: 
						price = config.getDouble("Fisher.fish");
						break;
					case 2:
						price = config.getDouble("Fisher.treasure");
						break;
					case 3: 
						price = config.getDouble("Fisher.junk");
						break;
					}
					event.getPlayer().sendMessage("Price: " + price + " Type: " + lootType + " Material: " + caught.getItemStack().getType().toString());
					if(price != 0.0) {
						config = YamlConfiguration.loadConfiguration(playerFile);
						config.set(event.getPlayer().getName() + ".account amount", config.getDouble(event.getPlayer().getName() + ".account amount") + price);
						saveFile(playerFile);
						updateScoreBoard(event.getPlayer());
					}
				}
			}
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Methoden
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List<String> getMaterialList(String arg) {
		Material[] materials = Material.values();
		List<String> list = new ArrayList<>();
		if(arg.equals("")) {
			for(Material material: materials) {
				list.add(material.name().toLowerCase());
			}
		}
		else {
			for(Material material: materials) {
				if(material.name().toLowerCase().contains(arg)) {
					list.add(material.name().toLowerCase());
				}
			}
		}
		return list;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List<String> getEntityList(String arg) {
		List<String> list = new ArrayList<>();
		EntityType[] entityTypes = EntityType.values();
		if(arg.equals("")) {
			for(EntityType entityname: entityTypes) {
				list.add(entityname.name().toLowerCase());
			}
		}
		else {
			for(EntityType entityname: entityTypes) {
				if(entityname.name().toLowerCase().contains(arg)) {
					list.add(entityname.name().toLowerCase());
				}
			}
		}
		return list;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List<String> getJobList(String arg) {
		List<String> temp = getConfig().getStringList("JobList");
		List<String> list = new ArrayList<>();
		if(arg.equals("")) {
			list = temp;
		}
		else {
			for(String jobname:temp) {
				if(jobname.contains(arg)) {
					list.add(jobname);
				}
			}
		}
		return list;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List<String> getAdminShopList(String arg) {
		List<String> temp = getConfig().getStringList("ShopNames");
		List<String> list = new ArrayList<>();
		if(arg.equals("")) {
			list = temp;
		}
		else {
			for(String shopName:temp) {
				if(shopName.contains(arg)) {
					list.add(shopName);
				}
			}
		}
		return list;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List<String> getPlayerShopList(String arg,String playerName) {
		List<String> temp = getConfig().getStringList("PlayerShopNames");
		List<String> list = new ArrayList<>();
		if(arg.equals("")) {
			for(String shopName:temp) {
				if(shopName.substring(shopName.indexOf("_")+1).equals(playerName)) {
					list.add(shopName.substring(0,shopName.indexOf("_")));
				}
			}
		}
		else {
			for(String shopName:temp) {
				if(shopName.substring(0,shopName.indexOf("_")).contains(arg) && shopName.substring(shopName.indexOf("_")+1).equals(playerName)) {
					list.add(shopName.substring(0,shopName.indexOf("_")));
				}
			}
		}
		return list;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List<String> getHomeList(String arg,String playerName) {
		config = YamlConfiguration.loadConfiguration(playerFile);
		List<String> temp = config.getStringList(playerName + ".Home.Homelist");
		List<String> list = new ArrayList<>();
		if(arg.equals("")) {
			list = temp;
		}
		else {
			for(String homeName:temp) {
				if(homeName.contains(arg)) {
					list.add(homeName);
				}
			}
		}
		return list;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void setScoreboard(Player p,int score) {
		config = YamlConfiguration.loadConfiguration(playerFile); 
		if(!config.getBoolean(p.getName() + ".bank")) {
			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective o = board.registerNewObjective("test", "dummy","§6§lBank");
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			o.getScore("§6Money:").setScore(score);
			p.setScoreboard(board);
		}
		else {
			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			p.setScoreboard(board);
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void updateScoreBoard(Player p) {
		config = YamlConfiguration.loadConfiguration(playerFile);
		int score = (int) config.getDouble(p.getName() + ".account amount");
		setScoreboard(p,score);
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void saveFile(File file) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean checkValidation(Player player, Shop s,String material, String slot, String amount, String sellPrice, String buyPrice) {
		boolean correct = false;
		if(Integer.valueOf(slot) <= 0) {
			player.sendMessage(ChatColor.RED + "The slot should be higher then 0!");
		}
		else if(!s.slotIsEmpty(Integer.valueOf(slot))) {
			player.sendMessage(ChatColor.RED + "This slot is occupied!");
		}
		else if(Integer.valueOf(amount) <= 0) {
			player.sendMessage(ChatColor.RED + "The amount should be higher then 0!");
		}
		else if(Double.valueOf(sellPrice) < 0) {
			player.sendMessage(ChatColor.RED + "The sellPrice should be 0 or highter!");
		}
		else if(Double.valueOf(buyPrice) < 0) {
			player.sendMessage(ChatColor.RED + "The buyPrice should be 0 or highter!");
		}
		else if(!material.toUpperCase().equals("HAND") && Material.matchMaterial(material.toUpperCase()) == null) {
			player.sendMessage(ChatColor.RED + "Invalid material!");
		}
		else {
			correct = true;
		}
		return correct;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean isTownWorld(String arg) {
		boolean isTownWorld = false;
		if(getConfig().getStringList("TownWorlds").contains(arg)) {
			isTownWorld = true;
		}
		return isTownWorld;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private TownWorld getTownWorld(String arg) {
		TownWorld tWorld = null;
		for(TownWorld world:townWorlds) {
			if(world.getWorldName().equals(arg)) {
				tWorld = world;
				break;
			}
		}
		return tWorld;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void handleAddPotion(Shop s, String[] args) {
		if(!args[2].equalsIgnoreCase("potion") && !args[2].equalsIgnoreCase("splash_potion") && !args[2].equalsIgnoreCase("lingering_potion")) {
			player.sendMessage(ChatColor.RED + "PotionType is not correct! Use potion/splash_potion/lingering_potion");
		}
		else if(!args[4].equalsIgnoreCase("extended") && !args[4].equalsIgnoreCase("upgraded") && !args[4].equalsIgnoreCase("none")) {
			player.sendMessage(ChatColor.RED + "Use extended/upgraded/none!");
		}
		else if(checkValidation(player, s,args[2], args[5], args[6], args[7], args[8])) {
			ItemStack itemStack = new ItemStack(Material.valueOf(args[2].toUpperCase()),Integer.valueOf(args[6]));
			PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
			boolean extended = false;
			boolean upgraded = false;
			if(args[4].equalsIgnoreCase("extended")) {
				extended = true;
			}
			else if(args[4].equalsIgnoreCase("upgraded")) {
				upgraded = true;
			}
			meta.setBasePotionData(new PotionData(PotionType.valueOf(args[3].toUpperCase()),extended,upgraded));
			itemStack.setItemMeta(meta);
			s.addItem(player, Integer.valueOf(args[5]) - 1, Double.valueOf(args[7]), Double.valueOf(args[8]), itemStack);
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void handleAddEnchantedItem(Player p,String[] args,Shop s) {
		if(checkValidation(player, s,args[2], args[3], args[4], args[5], args[6])) {
			Integer length = args.length - 7;
			if(length % 2 == 0) {
				ArrayList<String> enchantmentList = new ArrayList<>();
				for(Integer i=1; i<length; i = i + 2) {
					enchantmentList.add(args[i+6].toLowerCase() + "-" + args[i+7]);
				}
				ItemStack iStack = new ItemStack(Material.valueOf(args[2].toUpperCase()),Integer.valueOf(args[4]));
				ArrayList<String> newEnchantmentList = Shop.addEnchantments(iStack, enchantmentList);
				if(newEnchantmentList.size() < enchantmentList.size()) {
					p.sendMessage(ChatColor.RED + "Not all enchantments could be used!");
				}
				s.addItem(p, Integer.valueOf(args[3]) - 1, Double.valueOf(args[5]), Double.valueOf(args[6]), iStack);
			}
			else {
				player.sendMessage(ChatColor.RED + "Your list [<enchantment> <lvl>] is incomplete!");
			}
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void handleEditor(Player playe,Shop shop,InventoryClickEvent event) {
		ItemMeta meta = event.getCurrentItem().getItemMeta();
		String command = meta.getDisplayName();
		String inventoryname = event.getInventory().getName();
		if(inventoryname.equals(shop.getName() + "-Editor") && meta.getDisplayName().contains("Slot")) {
			int slot = Integer.valueOf(meta.getDisplayName().substring(5));
			shop.openSlotEditor(playe, slot);
		}
		else if(inventoryname.equals(shop.getName() + "-SlotEditor")) {
			shop.handleSlotEditor(event);
			if(command.equals(ChatColor.RED + "remove item") || command.equals(ChatColor.RED + "exit without save") || command.equals(ChatColor.YELLOW + "save changes")) {
				shop.openEditor(playe);
			}
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean inventoryContainsItems(Inventory inventory,ItemStack item, int amount) {
		boolean bool = false;
		int realAmount = 0;
		int amountStack = 0;
		for(ItemStack s: inventory.getContents()) {
			if(s != null) {
				ItemStack stack = new ItemStack(s);
				Repairable repairable = (Repairable) stack.getItemMeta();
				if(repairable != null) {
					repairable.setRepairCost(0);
					stack.setItemMeta((ItemMeta) repairable);
				}
				item.setAmount(1);
				amountStack = stack.getAmount();
				stack.setAmount(1);
				if(item.equals(stack)) {
					realAmount += amountStack;
				}
			}
		}
		if(realAmount >= amount) {
			bool = true;
		}
		return bool;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void removeItemFromInventory(Inventory inventory,ItemStack item,int amount) {
		int amount2 = amount;
		int amountStack = 0;
		int repairCosts = 0;
		boolean isRepairable = false;
		for(ItemStack s: inventory.getContents()) {
			if(s != null) {
				ItemStack stack = new ItemStack(s);
				Repairable repairable = (Repairable) stack.getItemMeta();
				if(repairable != null) {
					repairCosts = repairable.getRepairCost();
					repairable.setRepairCost(0);
					stack.setItemMeta((ItemMeta) repairable);
					isRepairable = true;
				}
				item.setAmount(1);
				amountStack = stack.getAmount();
				stack.setAmount(1);
				if(item.equals(stack) && amount2 != 0) {
					if(isRepairable) {
						repairable.setRepairCost(repairCosts);
						stack.setItemMeta((ItemMeta) repairable);
					}
					if(amount2 >= amountStack) {
						stack.setAmount(amountStack);
						inventory.removeItem(stack);
						amount2 -= amountStack;
					}
					else {
						stack.setAmount(amount2);
						inventory.removeItem(stack);
						amount2-= amount2;
					}
				}
			}
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void handleBuySell(Shop shop,InventoryClickEvent event,Player playe) {
		double sellprice;
		double buyprice;
		double playermoney;
		int amount;
		boolean isPlayershop = false;
		boolean alreadysay = false;
		List<String> lore = new ArrayList<>();
		int damage = 0;
		String shopOwner = "";
		ClickType clickType = event.getClick();
		String inventoryname = event.getInventory().getName();
		Inventory inventoryplayer = event.getWhoClicked().getInventory();
		PlayerShop playerShop = null;
		if(shop instanceof PlayerShop) {
			isPlayershop = true;
			playerShop = (PlayerShop) shop;
			shopOwner = playerShop.getOwner();
		}
		if(shop.getName().equals(inventoryname)) {
			event.setCancelled(true);
			config = YamlConfiguration.loadConfiguration(new File(getDataFolder() , shop.getName() + ".yml"));
			List<String> itemlist = shop.getItemList();
			String item3;
			//Playershop
			if(isPlayershop && clickType == ClickType.MIDDLE && shopOwner.equals(playe.getName())) {
				playerShop.switchStockpile();
			} //
			else {
				for(String item2: itemlist) {
					String s = event.getCurrentItem().getType().toString();
					//only relevant for adminshop
					boolean isSpawner = false;
					if(item2.contains("SPAWNER")) {
						item3 = "SPAWNER";
						isSpawner = true;
					}
					else {
						item3 = item2;
					}
					EnchantmentStorageMeta metaEnchanted = null;
					PotionMeta metaPotion = null;
					if(event.getCurrentItem().getType().toString().equals("ENCHANTED_BOOK")) {
						metaEnchanted = (EnchantmentStorageMeta) event.getCurrentItem().getItemMeta();
						if(!metaEnchanted.getStoredEnchants().isEmpty()) {
							List<String> list = new ArrayList<>();
							for(Entry<Enchantment, Integer> map: metaEnchanted.getStoredEnchants().entrySet()) {
								list.add(map.getKey().getKey().toString().substring(map.getKey().getKey().toString().indexOf(":") + 1) + "-" + map.getValue().intValue());
								list.sort(String.CASE_INSENSITIVE_ORDER);
							}
							s = event.getCurrentItem().getType().toString().toLowerCase() + "#Enchanted_" + list.toString();
						}
					}
					else if(item3.contains("#Enchanted_")) {
						if(!event.getCurrentItem().getEnchantments().isEmpty()) {
							List<String> list = new ArrayList<>();
							for(Entry<Enchantment, Integer> map: event.getCurrentItem().getEnchantments().entrySet()) {
								list.add(map.getKey().getKey().toString().substring(map.getKey().getKey().toString().indexOf(":") + 1) + "-" + map.getValue().intValue());
								list.sort(String.CASE_INSENSITIVE_ORDER);
							}
							s = event.getCurrentItem().getType().toString().toLowerCase() + "#Enchanted_" + list.toString();
						}
					}
					else if(item3.contains("potion:")) {
						if(event.getCurrentItem().getType().toString().contains("POTION")) {
							metaPotion = (PotionMeta) event.getCurrentItem().getItemMeta();
							String type = metaPotion.getBasePotionData().getType().toString().toLowerCase();
							String property;
							if(metaPotion.getBasePotionData().isExtended()) {
								property = "extended";
							}
							else if(metaPotion.getBasePotionData().isUpgraded()) {
								property = "upgraded";
							}
							else {
								property = "none";
							}
							s = event.getCurrentItem().getType().toString().toLowerCase() + ":" + type + "#" + property;
						}
					}
					else if(!item3.contains("SPAWNER")){
						s = event.getCurrentItem().getType().name();
					}
					ItemMeta meta2 = event.getCurrentItem().getItemMeta();
					ItemStack testStack = new ItemStack(event.getCurrentItem().getType());
					ItemMeta testMeta = testStack.getItemMeta();
					if(!isSpawner && !event.getCurrentItem().getItemMeta().getDisplayName().equals(testMeta.getDisplayName())) {
						s = meta2.getDisplayName() + "|" + s;
					}
					if(item3.equals(s)) {
						config = YamlConfiguration.loadConfiguration(new File(getDataFolder() , shop.getName() + ".yml"));
						String materialName = null;
						//only relevant for adminshop
						if(isSpawner) {
							materialName = "SPAWNER";
						}
						else {
							materialName = item2;
						}
						if(materialName.equals(item3)) {
							sellprice = config.getDouble("ShopItems." + item2 +  ".sellPrice");	
							buyprice = config.getDouble("ShopItems." + item2 +  ".buyPrice");
							amount = config.getInt("ShopItems." + item2 +  ".Amount");	
							lore = config.getStringList("ShopItems." + item2 + ".lore");
							damage = config.getInt("ShopItems." + item2 + ".damage");
							List<String> loreRealItem = event.getCurrentItem().getItemMeta().getLore();
							if(loreRealItem != null) {
								Iterator<String> iterator = loreRealItem.iterator();
								while(iterator.hasNext()) {
									String string = iterator.next();
									if(string.contains("buy for") || string.contains("sell for")) {
										iterator.remove();
									}
								}	
							}
							else {
								loreRealItem = new ArrayList<>();
							}
							Damageable damageMeta = (Damageable) event.getCurrentItem().getItemMeta();
							int realDamage = 0;
							if(damageMeta != null) {
								realDamage = damageMeta.getDamage();
							}
							if(lore.equals(loreRealItem) && damage == realDamage) {
								config = YamlConfiguration.loadConfiguration(playerFile);
								playermoney = config.getDouble(playe.getName() + ".account amount"); 
								if(clickType == ClickType.LEFT) {
									if(buyprice != 0.0 && playermoney >= buyprice || playe.getName().equals(shopOwner)) {
										if(!isPlayershop || playerShop.available(materialName)) {  
											if(inventoryplayer.firstEmpty() != -1) {
												//only adminshop
												if(isSpawner && event.getCurrentItem().getItemMeta().getDisplayName().equals(item2.substring(8))) {
													ItemStack stack = new ItemStack(Material.getMaterial(materialName), amount);
													ItemMeta meta = stack.getItemMeta();
													meta.setDisplayName(item2.substring(8) + "-" + playe.getName());   
													stack.setItemMeta(meta);
													inventoryplayer.addItem(stack);
													playermoney = playermoney - buyprice;
													config.set(playe.getName() + ".account amount", playermoney);
													saveFile(playerFile);
													updateScoreBoard(playe);
													if(amount > 1) {
														playe.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " " + ChatColor.GOLD + "Items" + ChatColor.GOLD + " were bought for " + ChatColor.GREEN + buyprice + ChatColor.GREEN + "$"+ ChatColor.GOLD + ".");
													}
													else {
														playe.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " " + ChatColor.GOLD + "Item" + ChatColor.GOLD + " was bought for " + ChatColor.GREEN + buyprice + ChatColor.GREEN + "$"+ ChatColor.GOLD + ".");
													}
												} //
												else if(!isSpawner){
													String displayName = "default";
													
													if(materialName.contains("|")) {
														displayName = materialName.substring(0,materialName.indexOf("|"));
														materialName = materialName.substring(materialName.indexOf("|")+1);
													}
													boolean isEnchanted = false;
													boolean isPotion = false;
													if(materialName.contains("#Enchanted_")) {
														isEnchanted = true;
														materialName = materialName.substring(0,materialName.indexOf("#")).toUpperCase();
													}
													else if(materialName.contains("potion:")) {
														isPotion = true;
														materialName = materialName.substring(0,materialName.indexOf(":")).toUpperCase();
													}
													ItemStack stack = new ItemStack(Material.getMaterial(materialName), amount);
													if(isEnchanted) {
														if(metaEnchanted != null) {
															metaEnchanted.setLore(lore);
															stack.setItemMeta(metaEnchanted);
														}
														else {
															stack.addEnchantments(event.getCurrentItem().getEnchantments());
														}
														if(isPlayershop) {
															playerShop.decreaseStock(s, amount);
														}
													}
													else if(isPotion) {
														if(metaPotion != null) {
															metaPotion.setLore(lore);
															stack.setItemMeta(metaPotion);
														}
														//only playershop
														if(isPlayershop) {
															playerShop.decreaseStock(s, amount);
														}
													}
													//only playershop
													else if(isPlayershop){
														playerShop.decreaseStock(s, amount);
													} //
													ItemMeta meta = stack.getItemMeta();
													meta.setLore(lore);
													if(!displayName.equals("default")) {
														meta.setDisplayName(displayName);
													}
													if(damage > 0) {
														Damageable damageMeta2 = (Damageable) meta;
														damageMeta2.setDamage(damage);
														meta = (ItemMeta) damageMeta2;
													}
													stack.setItemMeta(meta);
													inventoryplayer.addItem(stack);
													playermoney = playermoney - buyprice;
													if(!isPlayershop || !shopOwner.equals(playe.getName())) {
														config.set(playe.getName() + ".account amount", playermoney);
														if(isPlayershop) {
															config.set(shopOwner + ".account amount", config.getDouble(shopOwner + ".account amount") + buyprice);
														}
														saveFile(playerFile);
														updateScoreBoard(playe);
														//only playershop
														if(isPlayershop && Bukkit.getPlayer(shopOwner).isOnline()) {
															updateScoreBoard(Bukkit.getPlayer(shopOwner));
														}
														if(amount > 1) {
															playe.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " " + ChatColor.GOLD + "Items" + ChatColor.GOLD + " were bought for " + ChatColor.GREEN + buyprice + ChatColor.GREEN + "$"+ ChatColor.GOLD + ".");
														}
														else {
															playe.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " " + ChatColor.GOLD + "Item" + ChatColor.GOLD + " was bought for " + ChatColor.GREEN + buyprice + ChatColor.GREEN + "$"+ ChatColor.GOLD + ".");
														}
													}
													//only playershop
													else if(isPlayershop && shopOwner.equals(playe.getName())) {
														if(amount > 1) {
															playe.sendMessage(ChatColor.GOLD + "You got " + ChatColor.GREEN + String.valueOf(amount) + ChatColor.GOLD + " Items from the shop.");
														}
														else {
															playe.sendMessage(ChatColor.GOLD + "You got " + ChatColor.GREEN + String.valueOf(amount) + ChatColor.GOLD + " Item from the shop.");
														}
													}
													//only playershop
													if(isPlayershop) {
														playerShop.refreshStockpile();
													}
													break;
												}
											}
											else {
												playe.sendMessage(ChatColor.RED + "There is no free slot in your inventory!");
											}
										}
										//only playershop
										else if(isPlayershop){
											playe.sendMessage(ChatColor.GOLD + "This item is unavailable.");
										}
									}
									else if(playermoney < buyprice && !alreadysay) {
										playe.sendMessage(ChatColor.RED + "You don't have enough money!");
										alreadysay = true;
									}
								}
								else if(clickType == ClickType.RIGHT && !materialName.contains("ANVIL_0") && !materialName.contains("CRAFTING_TABLE_0")&& sellprice != 0.0 || clickType == ClickType.RIGHT && playe.getName().equals(shopOwner) && inventoryplayer.containsAtLeast(new ItemStack(Material.getMaterial(materialName), 1), amount)) {
									String displayName = "default";
									if(materialName.contains("|")) {
										displayName = materialName.substring(0,materialName.indexOf("|"));
										materialName = materialName.substring(materialName.indexOf("|")+1);
									}
									boolean isEnchanted = false;
									boolean isPotion = false;
									if(materialName.contains("#Enchanted_")) {
										isEnchanted = true;
										materialName = materialName.substring(0,materialName.indexOf("#")).toUpperCase();
									}
									if(materialName.contains("potion:")) {
										isPotion = true;
										materialName = materialName.substring(0,materialName.indexOf(":")).toUpperCase();
									}
									ItemStack iStack = new ItemStack(Material.getMaterial(materialName),amount);
									if(isEnchanted) {
										if(metaEnchanted != null) {
											iStack.setItemMeta(metaEnchanted);
										}
										else {
											iStack.addEnchantments(event.getCurrentItem().getEnchantments());
										}
									}
									else if(isPotion) {
										if(metaPotion != null) {
											iStack.setItemMeta(metaPotion);
										}
									}
									ItemMeta meta = iStack.getItemMeta();
									meta.setLore(lore);
									if(!displayName.equals("default")) {
										meta.setDisplayName(displayName);
									}
									Damageable damageMeta2 = (Damageable) meta;
									if(damage > 0) {
										damageMeta2.setDamage(damage);
										meta = (ItemMeta) damageMeta2;
									}
									iStack.setItemMeta(meta);
									if(inventoryContainsItems(inventoryplayer,iStack,amount) ) {
										playermoney = playermoney + sellprice;
										if(!shopOwner.equals(playe.getName()) || !isPlayershop) {
											if(config.getDouble(shopOwner + ".account amount") >= sellprice && isPlayershop|| !isPlayershop) {
												config.set(playe.getName() + ".account amount", playermoney);
												//only playershop
												if(isPlayershop) {
													config.set(shopOwner + ".account amount", config.getDouble(shopOwner + ".account amount") - sellprice);
												}
												saveFile(playerFile);
												if(amount > 1) {
													playe.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " " + ChatColor.GOLD + "Items" + ChatColor.GOLD + " were sold for " + ChatColor.GREEN + sellprice + ChatColor.GREEN + "$"+ ChatColor.GOLD + ".");
												}
												else {
													playe.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " " + ChatColor.GOLD + "Item" + ChatColor.GOLD + " was sold for " + ChatColor.GREEN + sellprice + ChatColor.GREEN + "$"+ ChatColor.GOLD + ".");
												}
												updateScoreBoard(playe);
												//only playershop
												if(isPlayershop && Bukkit.getPlayer(shopOwner).isOnline()) {
													updateScoreBoard(Bukkit.getPlayer(shopOwner));
												}
											}
											//only playershop
											else if(isPlayershop){
												playe.sendMessage(ChatColor.RED + "The owner has not enough money to buy your items!");
											}
										}
										//only playershop
										else if(isPlayershop) {
											if(amount > 1) {
												playe.sendMessage(ChatColor.GOLD + "You added " + ChatColor.GREEN + String.valueOf(amount) + ChatColor.GOLD + " Items to your shop.");
											}
											else {
												playe.sendMessage(ChatColor.GOLD + "You added " + ChatColor.GREEN + String.valueOf(amount) + ChatColor.GOLD + " Item to your shop.");
											}
										}
										removeItemFromInventory(inventoryplayer,iStack,amount);
										//inventoryplayer.removeItem(iStack);
										//only playershop
										if(isPlayershop) {
											playerShop.increaseStock(s, amount);
											playerShop.refreshStockpile();
										}
										break;
									}
								}
								else if(clickType == ClickType.SHIFT_RIGHT && sellprice != 0.0 || clickType == ClickType.SHIFT_RIGHT && playe.getName().equals(shopOwner) && inventoryplayer.containsAtLeast(new ItemStack(Material.getMaterial(materialName), 1), amount)) {
									String displayName = "default";
									if(materialName.contains("|")) {
										displayName = materialName.substring(0,materialName.indexOf("|"));
										materialName = materialName.substring(materialName.indexOf("|")+1);
									}
									boolean isEnchanted = false;
									boolean isPotion = false;
									if(materialName.contains("#Enchanted_")) {
										isEnchanted = true;
										materialName = materialName.substring(0,materialName.indexOf("#")).toUpperCase();
									}
									if(materialName.contains("potion:")) {
										isPotion = true;
										materialName = materialName.substring(0,materialName.indexOf(":")).toUpperCase();
									}
									ItemStack iStack = new ItemStack(Material.getMaterial(materialName));
									if(isEnchanted) {
										if(metaEnchanted != null) {
											iStack.setItemMeta(metaEnchanted);
										}
										else {
											iStack.addEnchantments(event.getCurrentItem().getEnchantments());
										}
									}
									else if(isPotion) {
										if(metaPotion != null) {
											iStack.setItemMeta(metaPotion);
										}
									}
									ItemMeta meta = iStack.getItemMeta();
									meta.setLore(lore);
									if(!displayName.equals("default")) {
										meta.setDisplayName(displayName);
									}
									if(damage > 0) {
										Damageable damageMeta2 = (Damageable) meta;
										damageMeta2.setDamage(damage);
										meta = (ItemMeta) damageMeta2;
									}
									iStack.setItemMeta(meta);
									if(inventoryContainsItems(inventoryplayer,iStack,1) ) {
										ItemStack[] i = inventoryplayer.getContents();
										int itemAmount = 0;
										double iA = 0.0;
										double newprice = 0;
										Repairable repairable = (Repairable) iStack.getItemMeta();
										if(repairable != null) {
											repairable.setRepairCost(0);
											iStack.setItemMeta((ItemMeta) repairable);
										}
										for(ItemStack is1 : i){
											if(is1 != null) {
												ItemStack is = new ItemStack(is1);
												Repairable repairable2 = (Repairable) is.getItemMeta();
												if(repairable2 != null) {
													repairable2.setRepairCost(0);
													is.setItemMeta((ItemMeta) repairable2);
												}
												iStack.setAmount(is.getAmount());
												if(is.toString().equals(iStack.toString())) {
													itemAmount = itemAmount + is.getAmount();
												}
											}
										}
										iA = Double.valueOf(String.valueOf(itemAmount));
										newprice = sellprice/amount * iA;
										if(!shopOwner.equals(playe.getName()) || !isPlayershop) {
											if(config.getDouble(shopOwner + ".account amount") >= newprice && isPlayershop || !isPlayershop) {
												if(itemAmount > 1) {
													playe.sendMessage(ChatColor.GREEN + String.valueOf(itemAmount) + " " + ChatColor.GOLD + "Items" + ChatColor.GOLD + " were sold for " + ChatColor.GREEN + newprice + ChatColor.GREEN + "$"+ ChatColor.GOLD + ".");
												}
												else {
													playe.sendMessage(ChatColor.GREEN + String.valueOf(itemAmount) + " " + ChatColor.GOLD + "Item" + ChatColor.GOLD + " was sold for " + ChatColor.GREEN + newprice + ChatColor.GREEN + "$"+ ChatColor.GOLD + ".");
												}
												playermoney = playermoney + newprice;
												config.set(playe.getName() + ".account amount", playermoney);
												//only playershop
												if(isPlayershop) {
													config.set(shopOwner + ".account amount", config.getDouble(shopOwner + ".account amount") - newprice);
												}
												saveFile(playerFile);
												updateScoreBoard(playe);
												//only playershop
												if(isPlayershop && Bukkit.getPlayer(shopOwner).isOnline()) {
													updateScoreBoard(Bukkit.getPlayer(shopOwner));
												}
											}
											//only playershop
											else if(isPlayershop) {
											playe.sendMessage(ChatColor.RED + "The owner has not enough money to buy your items!");
											}
										}
										//only playershop
										else if(isPlayershop) {
											if(itemAmount > 1) {
												playe.sendMessage(ChatColor.GOLD + "You added " + ChatColor.GREEN + String.valueOf(itemAmount) + ChatColor.GOLD + " Items to your shop.");
											}
											else {
												playe.sendMessage(ChatColor.GOLD + "You added " + ChatColor.GREEN + String.valueOf(itemAmount) + ChatColor.GOLD + " Item to your shop.");
											}
										}
										iStack.setAmount(itemAmount);
										removeItemFromInventory(inventoryplayer,iStack,itemAmount);
										//only playershop
										if(isPlayershop) {
											playerShop.increaseStock(s, itemAmount);
											playerShop.refreshStockpile();
										}
										break;
									}
								}
							}
						}										
					}
				}
			}
		}
	}
}