package ultimate_economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.Ageable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.jobsystem.Job;
import com.ue.jobsystem.JobCenter;
import com.ue.jobsystem.JobCommandExecutor;
import com.ue.jobsystem.JobTabCompleter;
import com.ue.player.EconomyPlayer;
import com.ue.shopsystem.AdminShop;
import com.ue.shopsystem.AdminShopCommandExecutor;
import com.ue.shopsystem.PlayerShop;
import com.ue.shopsystem.PlayerShopCommandExecuter;
import com.ue.shopsystem.Shop;
import com.ue.shopsystem.ShopTabCompleter;
import com.ue.shopsystem.Spawner;
import com.ue.townsystem.Plot;
import com.ue.townsystem.Town;
import com.ue.townsystem.TownCommandExecutor;
import com.ue.townsystem.TownTabCompleter;
import com.ue.townsystem.TownWorld;
import com.ue.vault.Economy_UltimateEconomy;
import com.ue.vault.VaultHook;

import lang.UTF8Control;
import metrics.Metrics;

public class Ultimate_Economy extends JavaPlugin implements Listener {

	/*
	 * Lukas Heubach 
	 * 
	 * Todo List:
	 * - get money from the town
	 * 
	 * permission tp to town buyable regions | add coOwner<-
	 * leaderboard start money amount town bank amount in scoreboard f�r town owner
	 * und town coowner job crafter,enchanter op should remove playershops (more op
	 * commands) limit playershops per player check inventory name for right cancel
	 * 
	 * permission for town tp
	 */

	public static Ultimate_Economy getInstance;
	public static ResourceBundle messages;
	public Economy_UltimateEconomy economyImplementer;
	private VaultHook vaultHook;
	private List<String> playerlist, spawnerlist;
	private File spawner;
	private FileConfiguration config;

	public void onEnable() {
		
		Bukkit.getPluginManager().registerEvents(this, this);
		playerlist = new ArrayList<>();
		spawnerlist = new ArrayList<>();

		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		// can be removed in a future update
		else {
			getConfig().set("ItemList", null);
			getConfig().set("TownNames", null);
			saveConfig();
		}

		// load language file
		Locale currentLocale;
		if (getConfig().getString("localeLanguage") == null) {
			getConfig().set("localeLanguage", "en");
			getConfig().set("localeCountry", "US");
			currentLocale = new Locale("en", "US");
			Bukkit.getLogger().info("Loaded default language file: 'en' 'US'");
		} else {
			String lang = getConfig().getString("localeLanguage");
			String country = getConfig().getString("localeCountry");
			currentLocale = new Locale(lang, country);
			Bukkit.getLogger().info("Loaded language file: '" + lang + "' '" + country + "'");
		}
		messages = ResourceBundle.getBundle("lang.MessagesBundle", currentLocale,new UTF8Control());

		try {
			JobCenter.loadAllJobCenters(getServer(), getConfig(), getDataFolder());
		} catch (JobSystemException e) {
			Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
		}

		try {
			Job.loadAllJobs(getDataFolder(), getConfig());
		} catch (JobSystemException e) {
			Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
		}

		try {
			TownWorld.loadAllTownWorlds(getDataFolder(), getConfig(), getServer());
		} catch (TownSystemException e) {
			Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
		}

		try {
			AdminShop.loadAllAdminShops(getConfig(), getDataFolder(), getServer());
		} catch (ShopSystemException e) {
			Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
		}

		try {
			PlayerShop.loadAllPlayerShops(getConfig(), getDataFolder(), getServer());
		} catch (ShopSystemException e) {
			Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
		}

		try {
			EconomyPlayer.loadAllEconomyPlayers(getDataFolder());
		} catch (JobSystemException e) {
			Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
		}

		EconomyPlayer.setupConfig(getConfig());
		saveConfig();

		config = YamlConfiguration.loadConfiguration(EconomyPlayer.getPlayerFile());
		spawner = new File(getDataFolder(), "SpawnerLocations.yml");
		if (!spawner.exists()) {
			try {
				spawner.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// setup command executors
		getCommand("jobcenter").setExecutor(new JobCommandExecutor(this));
		getCommand("jobcenter").setTabCompleter(new JobTabCompleter(getConfig()));
		getCommand("town").setExecutor(new TownCommandExecutor());
		getCommand("town").setTabCompleter(new TownTabCompleter());
		getCommand("adminshop").setExecutor(new AdminShopCommandExecutor(this));
		getCommand("adminshop").setTabCompleter(new ShopTabCompleter(getConfig()));
		getCommand("playershop").setTabCompleter(new ShopTabCompleter(getConfig()));
		getCommand("playershop").setExecutor(new PlayerShopCommandExecuter(this));
		
		
		
		// spawn all spawners
		FileConfiguration spawnerconfig = YamlConfiguration.loadConfiguration(spawner);
		for (String spawnername : getConfig().getStringList("Spawnerlist")) {
			spawnerlist.add(spawnername);
			World world = getServer().getWorld(spawnerconfig.getString(spawnername + ".World"));
			Location location = new Location(world, spawnerconfig.getDouble(spawnername + ".X"),
					spawnerconfig.getDouble(spawnername + ".Y"), spawnerconfig.getDouble(spawnername + ".Z"));
			world.getBlockAt(location).setMetadata("name",
					new FixedMetadataValue(this, spawnerconfig.getString(spawnername + ".player")));
			world.getBlockAt(location).setMetadata("entity",
					new FixedMetadataValue(this, spawnerconfig.getString(spawnername + ".EntityType")));
		}
		playerlist = config.getStringList("Player");
		getConfig().options().copyDefaults(true);
		saveConfig();

		// setup metrics for bstats
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);

		// vault setup
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			getInstance = this;
			economyImplementer = new Economy_UltimateEconomy();
			vaultHook = new VaultHook();
			vaultHook.hook();
		}
	}

	public void onDisable() {
		JobCenter.despawnAllVillagers();
		TownWorld.despawnAllVillagers();
		AdminShop.despawnAllVillagers();
		PlayerShop.despawnAllVillagers();
		saveConfig();
		// vault
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			vaultHook.unhook();
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if (command.getName().equals("ue-language")) {
			if (args[0].equals("")) {
				list.add("de");
				list.add("en");
				list.add("cs");
				list.add("fr");
				list.add("zh");
			} else if (args[0].equals("de")) {
				list.add("DE");
			} else if (args[0].equals("en")) {
				list.add("US");
			} else if (args[0].equals("cs")) {
				list.add("CZ");
			} else if (args[0].equals("fr")) {
				list.add("FR");
			} else if (args[0].equals("zh")) {
				list.add("CN");
			}
		} else if (command.getName().equals("bank")) {
			if (args[0].equals("")) {
				list.add("on");
				list.add("off");
			} else if (args.length == 1) {
				if ("on".contains(args[0])) {
					list.add("on");
				}
				if ("off".contains(args[0])) {
					list.add("off");
				}
			}
		} else if (command.getName().equals("shop")) {
			if (args.length <= 1) {
				list = getAdminShopList(args[0]);
			}
		} else if (command.getName().equals("jobInfo")) {
			if (args.length <= 1) {
				list = getJobList(args[0]);
			}
		} else if (command.getName().equals("delHome") || command.getName().equals("home")) {
			if (args.length <= 1) {
				list = getHomeList(args[0], sender.getName());
			}
		} else if (command.getName().equalsIgnoreCase("townWorld")) {
			if (args[0].equals("")) {
				list.add("enable");
				list.add("disable");
				list.add("setFoundationPrice");
				list.add("setExpandPrice");
			} else if (args.length == 1) {
				if ("enable".contains(args[0])) {
					list.add("enable");
				}
				if ("disable".contains(args[0])) {
					list.add("disable");
				}
				if ("setFoundationPrice".contains(args[0])) {
					list.add("setFoundationPrice");
				}
				if ("setExpandPrice".contains(args[0])) {
					list.add("setExpandPrice");
				}
			} else if (args[0].equals("enable") || args[0].equals("disable") || args[0].equals("setFoundationPrice")
					|| args[0].equals("setExpandPrice")) {
				if (args[1].equals("")) {
					for (World world : Bukkit.getWorlds()) {
						list.add(world.getName());
					}
				} else if (args.length == 2) {
					for (World world : Bukkit.getWorlds()) {
						if (world.getName().contains(args[1])) {
							list.add(world.getName());
						}
					}
				}
			}
		} 
		return list;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				EconomyPlayer ecoPlayer = EconomyPlayer.getEconomyPlayerByName(player.getName());
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// Commands
				////////////////////////////////////////////////////////////////////////////////////////////////////////////// single
				////////////////////////////////////////////////////////////////////////////////////////////////////////////// commands
				if (label.equalsIgnoreCase("bank")) {
					if (args.length == 1) {
						if (args[0].equals("on") || args[0].equals("off")) {
							if (args[0].equals("on")) {
								ecoPlayer.setScoreBoardDisabled(false);
							} else {
								ecoPlayer.setScoreBoardDisabled(true);
							}
							ecoPlayer.updateScoreBoard(player);
						} else {
							player.sendMessage(ChatColor.RED + "/bank on/off");
						}
					} else {
						player.sendMessage("/bank <on/off>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("ue-language")) {
					if (args.length == 2) {
						if (!args[0].equals("cs") && !args[0].equals("de") && !args[0].equals("en")
								&& !args[0].equals("fr") && !args[0].equals("zh")) {
							player.sendMessage(ChatColor.RED + messages.getString("invalid_language"));
						} else if (!args[1].equals("CZ") && !args[1].equals("DE") && !args[1].equals("US")
								&& !args[1].equals("FR") && !args[1].equals("CN")) {
							player.sendMessage(ChatColor.RED + messages.getString("invalid_country"));
						} else {
							getConfig().set("localeLanguage", args[0]);
							getConfig().set("localeCountry", args[1]);
							saveConfig();
							player.sendMessage(ChatColor.GOLD + messages.getString("restart"));
						}
					} else {
						player.sendMessage("/ue-language <language> <country>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("money")) {
					if (args.length == 0) {
						config = YamlConfiguration.loadConfiguration(EconomyPlayer.getPlayerFile());
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("money_info") + " "
								+ ChatColor.GREEN
								+ String.valueOf(config.getDouble(player.getName() + ".account amount")));
					} else {
						player.sendMessage("/money");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("maxHomes")) {
					if (args.length == 1) {
						EconomyPlayer.setMaxHomes(getConfig(), Integer.valueOf(args[0]));
						saveConfig();
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("max_homes_change")
								+ " " + ChatColor.GREEN + args[0] + ChatColor.GOLD + ".");
					} else {
						player.sendMessage("/maxHomes <number>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("maxJobs")) {
					if (args.length == 1) {
						EconomyPlayer.setMaxJobs(getConfig(), Integer.valueOf(args[0]));
						saveConfig();
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("max_jobs_change") + " "
								+ ChatColor.GREEN + args[0] + ChatColor.GOLD + ".");
					} else {
						player.sendMessage("/maxJobs <number>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("maxJoinedTowns")) {
					if (args.length == 1) {
						EconomyPlayer.setMaxJoinedTowns(getConfig(), Integer.valueOf(args[0]));
						saveConfig();
						player.sendMessage(
								ChatColor.GOLD + Ultimate_Economy.messages.getString("max_joined_towns_change") + " "
										+ ChatColor.GREEN + args[0] + ChatColor.GOLD + ".");
					} else {
						player.sendMessage("/maxJoinedTowns <number>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("townWorld")) {
					if (args.length > 1) {
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						if (args[0].equals("enable")) {
							if (args.length == 2) {
								TownWorld.createTownWorld(getDataFolder(), args[1]);
								player.sendMessage(ChatColor.GREEN + args[1] + " " + ChatColor.GOLD
										+ Ultimate_Economy.messages.getString("townworld_enable") + ".");
							} else {
								player.sendMessage("/townWorld enable <worldname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("disable")) {
							if (args.length == 2) {
								TownWorld.deleteTownWorld(args[1]);
								player.sendMessage(ChatColor.GREEN + args[1] + " " + ChatColor.GOLD
										+ Ultimate_Economy.messages.getString("townworld_disable") + ".");
							} else {
								player.sendMessage("/townWorld disable <worldname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("setFoundationPrice")) {
							if (args.length == 3) {
								TownWorld.getTownWorldByName(args[1]).setFoundationPrice(Double.valueOf(args[2]), true);
								player.sendMessage(ChatColor.GOLD
										+ Ultimate_Economy.messages.getString("townworld_setFoundationPrice") + " "
										+ ChatColor.GREEN + args[2]);
							} else {
								player.sendMessage("/townWorld setFoundationPrice <worldname> <price>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("setExpandPrice")) {
							if (args.length == 3) {
								TownWorld.getTownWorldByName(args[1]).setExpandPrice(Double.valueOf(args[2]), true);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("townworld_setExpandPrice")
												+ " " + ChatColor.GREEN + args[2]);
							} else {
								player.sendMessage("/townWorld setExpandPrice <worldname> <price per chunk");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						getConfig().set("TownWorlds", TownWorld.getTownWorldNameList());
						saveConfig();
					} else if (args.length == 1 && args[0].equals("enable")) {
						player.sendMessage("/townWorld enable <worldname>");
					} else if (args.length == 1 && args[0].equals("disable")) {
						player.sendMessage("/townWorld disable <worldname>");
					} else if (args.length == 1 && args[0].equals("setFoundationPrice")) {
						player.sendMessage("/townWorld setFoundationPrice <price>");
					} else if (args.length == 1 && args[0].equals("setExpandPrice")) {
						player.sendMessage("/townWorld setExpandPrice <price per chunk>");
					} else {
						player.sendMessage("/townWorld <enable/disable/setFoundationPrice/setExpandPrice>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("myJobs")) {
					if (args.length == 0) {
						List<String> jobNames = ecoPlayer.getJobList();
						String jobString = jobNames.toString();
						jobString = jobString.replace("[", "");
						jobString = jobString.replace("]", "");
						if (jobNames.size() > 0) {
							player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("myjobs_info1")
									+ " " + ChatColor.GREEN + jobString);
						} else {
							player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("myjobs_info2"));
						}
					}

					else {
						player.sendMessage("/myJobs");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("shop")) {
					if (args.length == 1) {
						if (ecoPlayer.hasJob(args[0])) {
							AdminShop.getAdminShopByName(args[0]).openInv(player);
						} else {
							player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("shop_info"));
						}
					} else {
						player.sendMessage("/shop <shopname>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("ShopList")) {
					List<String> shopNames = AdminShop.getAdminShopNameList();
					String shopString = shopNames.toString();
					shopString = shopString.replace("[", "");
					shopString = shopString.replace("]", "");
					if (shopNames.size() > 0) {
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shoplist_info1") + " "
								+ ChatColor.GREEN + shopString);
					} else {
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shoplist_info2"));
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("giveMoney")) {
					if (args.length == 2) {
						double amount = Double.valueOf(args[1]);
						EconomyPlayer receiver = EconomyPlayer.getEconomyPlayerByName(args[0]);
						Player p = Bukkit.getPlayer(args[0]);
						if (amount < 0) {
							receiver.decreasePlayerAmount(-amount, false);
						} else {
							receiver.increasePlayerAmount(amount);
						}
						if (p.isOnline()) {
							p.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("got_money") + " "
									+ ChatColor.GREEN + amount + " $");
						}
					} else {
						player.sendMessage("/giveMoney <player> <amount>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("home")) {
					if (args.length == 1) {
						Location location = ecoPlayer.getHome(args[0]);
						player.teleport(location);
					} else {
						player.sendMessage("/home <homename>");
						Set<String> homes = ecoPlayer.getHomeList().keySet();
						String homeString = String.join(",", homes);
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("home_info") + " "
								+ ChatColor.GREEN + homeString);
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("sethome")) {
					if (args.length == 1) {
						ecoPlayer.addHome(args[0], player.getLocation());
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("sethome") + " "
								+ ChatColor.GREEN + args[0] + ChatColor.GOLD + ".");
					} else {
						player.sendMessage("/sethome <homename>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("delhome")) {
					if (args.length == 1) {
						ecoPlayer.removeHome(args[0]);
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("delhome1") + " "
								+ ChatColor.GREEN + args[0] + ChatColor.GOLD + " "
								+ Ultimate_Economy.messages.getString("delhome2") + ".");
					} else {
						player.sendMessage("/deletehome <homename>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("jobList")) {
					List<String> jobNames = Job.getJobNameList();
					String jobString = jobNames.toString();
					jobString = jobString.replace("[", "");
					jobString = jobString.replace("]", "");
					if (jobNames.size() > 0) {
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("joblist_info1") + " "
								+ ChatColor.GREEN + jobString);
					} else {
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("joblist_info2"));
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("jobInfo")) {
					if (args.length == 1) {
						Job job = Job.getJobByName(args[0]);
						player.sendMessage("");
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("jobinfo_info1") + " "
								+ ChatColor.GREEN + job.getName() + ChatColor.GOLD + ":");
						for (String string : job.getItemList()) {
							player.sendMessage(ChatColor.GOLD + string.toLowerCase() + " " + ChatColor.GREEN
									+ job.getItemPrice(string) + "$");
						}
						for (String string : job.getFisherList()) {
							player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("jobinfo_info2")
									+ " " + string.toLowerCase() + " " + ChatColor.GREEN + job.getFisherPrice(string)
									+ "$");
						}
						for (String string : job.getEntityList()) {
							player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("jobinfo_info3")
									+ " " + string.toLowerCase() + " " + ChatColor.GREEN + job.getKillPrice(string)
									+ "$");
						}
					} else {
						player.sendMessage("/jobInfo <jobname>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("pay")) {
					if (args.length == 2) {
						double money = Double.valueOf(args[1]);
						ecoPlayer.payToOtherPlayer(EconomyPlayer.getEconomyPlayerByName(args[0]), money);
						Player p = Bukkit.getPlayer(args[0]);
						if (p.isOnline()) {
							p.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("got_money") + " "
									+ ChatColor.GREEN + " " + money + " $ " + ChatColor.GOLD
									+ Ultimate_Economy.messages.getString("got_money_from") + " " + ChatColor.GREEN
									+ player.getName());
						}
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("gave_money") + " "
								+ ChatColor.GREEN + args[0] + " " + money + " $ ");
					} else {
						player.sendMessage("/pay <name> <amount>");
					}
				}	
			} catch (PlayerException | ShopSystemException | TownSystemException | JobSystemException e1) {
				player.sendMessage(ChatColor.RED + e1.getMessage());
			} catch (NumberFormatException e2) {
				player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
			}
		}
		else {
			if (label.equalsIgnoreCase("giveMoney")) {
				try {
					if (args.length == 2) {
						double amount = Double.valueOf(args[1]);
						EconomyPlayer receiver = EconomyPlayer.getEconomyPlayerByName(args[0]);
						Player p = Bukkit.getPlayer(args[0]);
						if (amount < 0) {
							receiver.decreasePlayerAmount(-amount, false);
						} else {
							receiver.increasePlayerAmount(amount);
						}
						if (p.isOnline()) {
							p.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("got_money") + " "
									+ ChatColor.GREEN + amount + " $");
						}
					} else {
						sender.sendMessage("/giveMoney <player> <amount>");
					}
				} catch (PlayerException e) {
					sender.sendMessage(ChatColor.RED + e.getMessage());
				}
			}
		}
		return false;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Events
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null) {
			Location location = event.getClickedBlock().getLocation();
			try {
				TownWorld townWorld = TownWorld.getTownWorldByName(location.getWorld().getName());
				if (townWorld.chunkIsFree(location.getChunk())) {
					if (!event.getPlayer().hasPermission("ultimate_economy.wilderness")) {
						event.setCancelled(true);
						event.getPlayer()
								.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("wilderness"));
					}
				} else {
					Town town = townWorld.getTownByChunk(location.getChunk());
					if (!town.isPlayerCitizen(event.getPlayer().getName())
							|| !town.hasBuildPermissions(event.getPlayer().getName(),
									location.getChunk().getX() + "/" + location.getChunk().getZ())) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(
								ChatColor.RED + Ultimate_Economy.messages.getString("no_permission_on_plot"));
					}
				}
			} catch (TownSystemException e) {
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onNPCOpenInv(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		if (entity instanceof Villager && ((Villager) entity).getProfession() == Profession.NITWIT) {
			String name = entity.getCustomName();
			try {
				if (name.contains("For Sale!")) {
					event.setCancelled(true);
					TownWorld townWorld = TownWorld.getTownWorldByName(entity.getWorld().getName());
					Town town = townWorld.getTownByChunk(entity.getLocation().getChunk());
					Plot plot = town.getPlotByChunk(entity.getLocation().getChunk().getX(),
							entity.getLocation().getChunk().getZ());
					plot.openSaleVillagerInv(event.getPlayer());
				} else if (name.contains("TownManager")) {
					event.setCancelled(true);
					TownWorld townWorld = TownWorld.getTownWorldByName(entity.getWorld().getName());
					Town town = townWorld.getTownByChunk(entity.getLocation().getChunk());
					town.openTownManagerVillagerInv(event.getPlayer());
				} else if (AdminShop.getAdminShopNameList().contains(name)) {
					event.setCancelled(true);
					AdminShop.getAdminShopByName(name).openInv(event.getPlayer());
				} else if (PlayerShop.getPlayerShopNameList().contains(name)) {
					event.setCancelled(true);
					PlayerShop.getPlayerShopByName(name).openInv(event.getPlayer());
				} else if (JobCenter.getJobCenterNameList().contains(name)) {
					event.setCancelled(true);
					JobCenter.getJobCenterByName(name).openInv(event.getPlayer());
				}
			} catch (TownSystemException | ShopSystemException | JobSystemException e) {
				Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onHitVillagerEvent(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Villager) {
			String entityname = event.getEntity().getCustomName();
			Player damager = (Player) event.getDamager();
			if (JobCenter.getJobCenterNameList().contains(entityname)) {
				event.setCancelled(true);
				damager.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("jobcenter_villager_hitevent"));
			} else if (AdminShop.getAdminShopNameList().contains(entityname)) {
				event.setCancelled(true);
				damager.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("shop_villager_hitevent"));
			} else if (PlayerShop.getPlayerShopNameList().contains(entityname)) {
				event.setCancelled(true);
				damager.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("shop_villager_hitevent"));
			} else if (entityname.contains("For Sale!")) {
				event.setCancelled(true);
				damager.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("sale_villager_hitevent"));
			} else if (entityname.contains("TownManager")) {
				event.setCancelled(true);
				damager.sendMessage(
						ChatColor.RED + Ultimate_Economy.messages.getString("townManager_villager_hitevent"));
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.getKiller() instanceof Player) {
			try {
				EconomyPlayer ecoPlayer = EconomyPlayer.getEconomyPlayerByName(entity.getKiller().getName());
				if (!ecoPlayer.getJobList().isEmpty() && entity.getKiller().getGameMode() != GameMode.CREATIVE
						&& entity.getKiller().getGameMode() != GameMode.SPECTATOR) {
					for (Job job : Job.getJobList()) {
						try {
							double d = job.getKillPrice(entity.getType().toString());
							ecoPlayer.increasePlayerAmount(d);
							break;
						} catch (PlayerException | JobSystemException e) {
							Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
						}
					}
				}
			} catch (PlayerException e1) {
				Bukkit.getLogger().log(Level.WARNING, e1.getMessage(), e1);
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onInvClickEvent(InventoryClickEvent event) {
		Player playe = (Player) event.getWhoClicked();
		try {
			EconomyPlayer ecoPlayer = EconomyPlayer.getEconomyPlayerByName(playe.getName());
			if (event.getCurrentItem() != null && event.getInventory().getType() == InventoryType.ANVIL
					&& event.getCurrentItem().getType() == Material.SPAWNER) {
				event.setCancelled(true);
			}
			String inventoryname = event.getView().getTitle();
			if (event.getInventory().getType() == InventoryType.CHEST && event.getCurrentItem() != null
					&& event.getCurrentItem().getItemMeta() != null) {
				ItemMeta meta = event.getCurrentItem().getItemMeta();
				//////////////////////////////////////////////////////////////////////////// editor
				if (inventoryname.contains("Editor") && meta.getDisplayName() != null) {
					event.setCancelled(true);
					String command = meta.getDisplayName();
					String shopName = inventoryname.substring(0, inventoryname.indexOf("-"));
					Shop shop = null;
					if (AdminShop.getAdminShopNameList().contains(shopName)) {
						shop = AdminShop.getAdminShopByName(shopName);
					} else if (PlayerShop.getPlayerShopNameList().contains(shopName)) {
						shop = PlayerShop.getPlayerShopByName(shopName);
					}
					if (shop != null) {
						if (inventoryname.equals(shop.getName() + "-Editor")
								&& meta.getDisplayName().contains("Slot")) {
							int slot = Integer.valueOf(meta.getDisplayName().substring(5));
							shop.openSlotEditor(playe, slot);
						} else if (inventoryname.equals(shop.getName() + "-SlotEditor")) {
							shop.handleSlotEditor(event);
							if (command.equals(ChatColor.RED + "remove item")
									|| command.equals(ChatColor.RED + "exit without save")
									|| command.equals(ChatColor.YELLOW + "save changes")) {
								shop.openEditor(playe);
							}
						}
					}
				}
				//////////////////////////////////////////////////////////////////////////// saleVillager
				else if (inventoryname.contains("Plot") || inventoryname.contains("TownManager")) {
					event.setCancelled(true);
					TownWorld.getTownWorldByName(playe.getWorld().getName()).handleTownVillagerInvClick(event);
					playe.closeInventory();
				} else {
					////////////////////////////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////
					ClickType clickType = event.getClick();
					if (event.getCurrentItem().getItemMeta() != null) {
						//////////////////////////////////////////////////////////////////////////// Job
						if (JobCenter.getJobCenterNameList().contains(inventoryname)) {
							event.setCancelled(true);
							String displayname = meta.getDisplayName();
							if (clickType == ClickType.RIGHT && displayname != null) {
								if (!ecoPlayer.getJobList().isEmpty()) {
									ecoPlayer.removeJob(displayname);
									playe.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("job_left")
											+ " " + ChatColor.GREEN + displayname);
								} else if (!displayname.equals("Info")) {
									playe.sendMessage(
											ChatColor.RED + Ultimate_Economy.messages.getString("no_job_joined"));
								}
							} else if (clickType == ClickType.LEFT && displayname != null) {
								ecoPlayer.addJob(displayname);
								playe.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("job_join") + " "
										+ ChatColor.GREEN + displayname);
							}
						}
						//////////////////////////////////////////////////////////////////////////// shops
						if (PlayerShop.getPlayerShopNameList().contains(inventoryname)) {
							PlayerShop shop = PlayerShop.getPlayerShopByName(inventoryname);
							handleBuySell(shop, event, playe);
						} else if (AdminShop.getAdminShopNameList().contains(inventoryname)) {
							AdminShop shop = AdminShop.getAdminShopByName(inventoryname);
							handleBuySell(shop, event, playe);
						}
					}
				}
			}
		} catch (TownSystemException | JobSystemException | ShopSystemException | IllegalArgumentException e) {
		} catch (PlayerException e) {
			playe.sendMessage(ChatColor.RED + e.getMessage());
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void setBlockEvent(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL
				&& event.getBlock().getBlockData().getMaterial() == Material.SPAWNER
				&& event.getItemInHand().getItemMeta().getDisplayName().contains("-")) {
			String spawnerowner = event.getItemInHand().getItemMeta().getDisplayName()
					.substring(event.getItemInHand().getItemMeta().getDisplayName().lastIndexOf("-") + 1);
			if (spawnerowner.equals(event.getPlayer().getName())) {
				String string = event.getItemInHand().getItemMeta().getDisplayName();
				Spawner.setSpawner(EntityType.valueOf(string.substring(0, string.lastIndexOf("-"))), event.getBlock());
				event.getBlock().setMetadata("name",
						new FixedMetadataValue(this, string.substring(string.lastIndexOf("-") + 1)));
				event.getBlock().setMetadata("entity",
						new FixedMetadataValue(this, string.substring(0, string.lastIndexOf("-"))));
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
				config.set(spawnername + ".player", string.substring(string.lastIndexOf("-") + 1));
				config.set(spawnername + ".EntityType", string.substring(0, string.lastIndexOf("-")));
				saveFile(spawner);
			} else {
				event.getPlayer()
						.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("no_permision_set_spawner"));
				event.setCancelled(true);
			}
		} else if (event.getPlayer().getGameMode() == GameMode.SURVIVAL
				&& !(event.getBlock().getBlockData().getMaterial() == Material.SPAWNER)) {
			event.getBlock().setMetadata("placedBy", new FixedMetadataValue(this, event.getPlayer().getName()));
			;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler(priority = EventPriority.HIGHEST)
	public void breakBlockEvent(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			List<MetadataValue> list = event.getBlock().getMetadata("placedBy");
			if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
				try {
					EconomyPlayer ecoPlayer = EconomyPlayer.getEconomyPlayerByName(event.getPlayer().getName());
					List<String> jobList = ecoPlayer.getJobList();
					if (!jobList.isEmpty()) {
						Material blockMaterial = event.getBlock().getBlockData().getMaterial();
						for (String jobName : jobList) {
							try {
								Job job = Job.getJobByName(jobName);
								if (blockMaterial == Material.POTATOES || blockMaterial == Material.CARROTS
										|| blockMaterial == Material.WHEAT
										|| blockMaterial == Material.NETHER_WART_BLOCK
										|| blockMaterial == Material.BEETROOTS) {
									Ageable ageable = (Ageable) event.getBlock().getBlockData();
									if (ageable.getAge() == ageable.getMaximumAge()) {
										double d = job.getItemPrice(blockMaterial.toString());
										ecoPlayer.increasePlayerAmount(d);
									}
								} else if (list.isEmpty() || !list.isEmpty()
										&& !list.get(0).asString().contains(event.getPlayer().getName())) {
									double d = job.getItemPrice(blockMaterial.toString());
									ecoPlayer.increasePlayerAmount(d);
								}
								break;
							} catch (JobSystemException e) {
							} catch (PlayerException e) {
								Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
							}
						}
					}
				} catch (PlayerException e1) {
					Bukkit.getLogger().log(Level.WARNING, e1.getMessage(), e1);
				}
				if (event.getBlock().getBlockData().getMaterial() == Material.SPAWNER) {
					List<MetadataValue> blockmeta = event.getBlock().getMetadata("name");
					if (!blockmeta.isEmpty()) {
						MetadataValue s = blockmeta.get(0);
						String blockname = s.asString();
						if (event.getPlayer().getName().equals(blockname)) {
							if (!event.getBlock().getMetadata("entity").isEmpty()) {
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
								meta.setDisplayName(event.getBlock().getMetadata("entity").get(0).asString() + "-"
										+ event.getPlayer().getName());
								stack.setItemMeta(meta);
								event.getPlayer().getInventory().addItem(stack);
							}
						} else {
							event.setCancelled(true);
							event.getPlayer().sendMessage(
									ChatColor.RED + Ultimate_Economy.messages.getString("no_permision_break_spawner"));
						}
					}
				}
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onJoinEvent(PlayerJoinEvent event) {
		String playername = event.getPlayer().getName();
		try {
			if (!playerlist.contains(playername)) {
				playerlist.add(playername);
				EconomyPlayer.createEconomyPlayer(playername);
			}
			EconomyPlayer.getEconomyPlayerByName(playername).updateScoreBoard(event.getPlayer());
		} catch (PlayerException e) {
			Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onFishingEvent(PlayerFishEvent event) {
		if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
			try {
				EconomyPlayer ecoPlayer = EconomyPlayer.getEconomyPlayerByName(event.getPlayer().getName());
				List<String> jobNameList = ecoPlayer.getJobList();
				if (!jobNameList.isEmpty()) {
					String lootType = "";
					Item caught = (Item) event.getCaught();
					if (caught != null) {
						switch (caught.getItemStack().getType().toString()) {
							case "COD":
							case "SALMON":
							case "TROPICAL_FISH":
							case "PUFFERFISH":
								lootType = "fish";
								break;
							case "BOW":
							case "ENCHANTED_BOOK":
							case "FISHING_ROD":
							case "NAME_TAG":
							case "NAUTILUS_SHELL":
							case "SADDLE":
							case "LILY_PAD":
								lootType = "treasure";
								break;
							default:
								lootType = "junk";
						}
						for (String jobName : jobNameList) {
							Job job = Job.getJobByName(jobName);
							Double price = job.getFisherPrice(lootType);
							ecoPlayer.increasePlayerAmount(price);
							break;
						}
					}
				}
			} catch (ClassCastException | JobSystemException | PlayerException e) {
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Methoden
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List<String> getJobList(String arg) {
		List<String> temp = Job.getJobNameList();
		List<String> list = new ArrayList<>();
		if (arg.equals("")) {
			list = temp;
		} else {
			for (String jobname : temp) {
				if (jobname.contains(arg)) {
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
		if (arg.equals("")) {
			list = temp;
		} else {
			for (String shopName : temp) {
				if (shopName.contains(arg)) {
					list.add(shopName);
				}
			}
		}
		return list;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List<String> getHomeList(String arg, String playerName) {
		List<String> list = new ArrayList<>();
		try {
			List<String> temp = new ArrayList<String>(
					EconomyPlayer.getEconomyPlayerByName(playerName).getHomeList().keySet());
			if (arg.equals("")) {
				list = temp;
			} else {
				for (String homeName : temp) {
					if (homeName.contains(arg)) {
						list.add(homeName);
					}
				}
			}
		} catch (PlayerException e) {
			Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
		}
		return list;
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
	private boolean inventoryContainsItems(Inventory inventory, ItemStack item, int amount) {
		boolean bool = false;
		int realAmount = 0;
		int amountStack = 0;
		for (ItemStack s : inventory.getContents()) {
			if (s != null) {
				ItemStack stack = new ItemStack(s);
				Repairable repairable = (Repairable) stack.getItemMeta();
				if (repairable != null) {
					repairable.setRepairCost(0);
					stack.setItemMeta((ItemMeta) repairable);
				}
				item.setAmount(1);
				amountStack = stack.getAmount();
				stack.setAmount(1);
				if (item.equals(stack)) {
					realAmount += amountStack;
				}
			}
		}
		if (realAmount >= amount) {
			bool = true;
		}
		return bool;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void removeItemFromInventory(Inventory inventory, ItemStack item, int amount) {
		int amount2 = amount;
		int amountStack = 0;
		int repairCosts = 0;
		boolean isRepairable = false;
		for (ItemStack s : inventory.getContents()) {
			if (s != null) {
				ItemStack stack = new ItemStack(s);
				Repairable repairable = (Repairable) stack.getItemMeta();
				if (repairable != null) {
					repairCosts = repairable.getRepairCost();
					repairable.setRepairCost(0);
					stack.setItemMeta((ItemMeta) repairable);
					isRepairable = true;
				}
				item.setAmount(1);
				amountStack = stack.getAmount();
				stack.setAmount(1);
				if (item.equals(stack) && amount2 != 0) {
					if (isRepairable) {
						repairable.setRepairCost(repairCosts);
						stack.setItemMeta((ItemMeta) repairable);
					}
					if (amount2 >= amountStack) {
						stack.setAmount(amountStack);
						inventory.removeItem(stack);
						amount2 -= amountStack;
					} else {
						stack.setAmount(amount2);
						inventory.removeItem(stack);
						amount2 -= amount2;
					}
				}
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void handleBuySell(Shop shop, InventoryClickEvent event, Player playe) {
		event.setCancelled(true);
		boolean isPlayershop = false;
		boolean alreadysay = false;
		ClickType clickType = event.getClick();
		Inventory inventoryplayer = event.getWhoClicked().getInventory();
		PlayerShop playerShop = null;
		if (shop instanceof PlayerShop) {
			isPlayershop = true;
			playerShop = (PlayerShop) shop;
		}
		// Playershop
		if (isPlayershop && clickType == ClickType.MIDDLE && playerShop.getOwner().equals(playe.getName())) {
			playerShop.switchStockpile();
		} //
		else {
			for (String itemString : shop.getItemList()) {
				// only relevant for adminshop
				boolean isSpawner = false;
				// standardize the itemstack for the string
				ItemStack clickedItemReal = event.getCurrentItem();
				ItemStack clickedItem = new ItemStack(clickedItemReal);
				ItemMeta itemMeta = clickedItem.getItemMeta();
				if(itemMeta.hasLore()) {
					List<String> loreList = itemMeta.getLore();
					Iterator<String> loreIter = loreList.iterator();
					while (loreIter.hasNext()) {
						String lore = loreIter.next();
						if (lore.contains(" buy for ") || lore.contains(" sell for ")) {
							loreIter.remove();
						}
					}
					itemMeta.setLore(loreList);
					clickedItem.setItemMeta(itemMeta);
				}
				clickedItem.setAmount(1);
				String clickedItemString = clickedItem.toString();
				if (itemString.contains("SPAWNER_")) {
					isSpawner = true;
					clickedItemString = "SPAWNER_" + event.getCurrentItem().getItemMeta().getDisplayName();
				}
				playe.sendMessage(itemString + " | " + clickedItemString);
				if (itemString.equals(clickedItemString)) {
					playe.sendMessage("test1");
					try {
						double sellprice = shop.getItemSellPrice(itemString);
						double buyprice = shop.getItemBuyPrice(itemString);
						int amount = shop.getItemAmount(itemString);
						EconomyPlayer playerShopOwner = null;
						if (isPlayershop) {
							playerShopOwner = EconomyPlayer.getEconomyPlayerByName(playerShop.getOwner());
						}
						EconomyPlayer ecoPlayer = EconomyPlayer.getEconomyPlayerByName(playe.getName());
						if (clickType == ClickType.LEFT) {
							if (buyprice != 0.0 && ecoPlayer.hasEnoughtMoney(buyprice)
									|| (isPlayershop && playe.getName().equals(playerShopOwner.getName()))) {
								if (!isPlayershop || playerShop.available(clickedItemString)) {
									if (inventoryplayer.firstEmpty() != -1) {
										// only adminshop
										if (isSpawner && event.getCurrentItem().getItemMeta().getDisplayName()
												.equals(itemString.substring(8))) {
											Bukkit.getLogger().info("test2");
											ItemStack stack = new ItemStack(Material.SPAWNER, amount);
											ItemMeta meta = stack.getItemMeta();
											meta.setDisplayName(itemString.substring(8) + "-" + playe.getName());
											stack.setItemMeta(meta);
											inventoryplayer.addItem(stack);
											ecoPlayer.decreasePlayerAmount(buyprice, true);
											if (amount > 1) {
												playe.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " "
														+ ChatColor.GOLD
														+ Ultimate_Economy.messages.getString("shop_buy_plural")
														+ " " + ChatColor.GREEN + buyprice + ChatColor.GREEN + "$"
														+ ChatColor.GOLD + ".");
											} else {
												playe.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " "
														+ ChatColor.GOLD
														+ Ultimate_Economy.messages.getString("shop_buy_singular")
														+ " " + ChatColor.GREEN + buyprice + ChatColor.GREEN + "$"
														+ ChatColor.GOLD + ".");
											}
										} //
										else if (!isSpawner) {
											ItemStack itemStack = shop.getItemStack(itemString);
											if (isPlayershop) {
												playerShop.decreaseStock(itemString, amount);
												playerShop.refreshStockpile();
											}
											itemStack.setAmount(amount);
											inventoryplayer.addItem(itemStack);

											if (!isPlayershop
													|| !playerShopOwner.getName().equals(playe.getName())) {
												ecoPlayer.decreasePlayerAmount(buyprice, true);
												// only playershop
												if (isPlayershop) {
													playerShopOwner.increasePlayerAmount(buyprice);
												}
												if (amount > 1) {
													playe.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " "
															+ ChatColor.GOLD
															+ Ultimate_Economy.messages.getString("shop_buy_plural")
															+ " " + ChatColor.GREEN + buyprice + ChatColor.GREEN
															+ "$" + ChatColor.GOLD + ".");
												} else {
													playe.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " "
															+ ChatColor.GOLD
															+ Ultimate_Economy.messages
																	.getString("shop_buy_singular")
															+ " " + ChatColor.GREEN + buyprice + ChatColor.GREEN
															+ "$" + ChatColor.GOLD + ".");
												}
											}
											// only playershop
											else if (isPlayershop
													&& playerShopOwner.getName().equals(playe.getName())) {
												if (amount > 1) {
													playe.sendMessage(ChatColor.GOLD
															+ Ultimate_Economy.messages
																	.getString("shop_got_item_plural1")
															+ " " + ChatColor.GREEN + String.valueOf(amount)
															+ ChatColor.GOLD + " " + Ultimate_Economy.messages
																	.getString("shop_got_item_plural2"));
												} else {
													playe.sendMessage(ChatColor.GOLD
															+ Ultimate_Economy.messages
																	.getString("shop_got_item_singular1")
															+ " " + ChatColor.GREEN + String.valueOf(amount)
															+ ChatColor.GOLD + " " + Ultimate_Economy.messages
																	.getString("shop_got_item_singular2"));
												}
											}
											break;
										}
									} else {
										playe.sendMessage(ChatColor.RED
												+ Ultimate_Economy.messages.getString("inventory_full"));
									}
								}
								// only playershop
								else if (isPlayershop) {
									playe.sendMessage(ChatColor.GOLD
											+ Ultimate_Economy.messages.getString("item_unavailable"));
								}
							} else if (!ecoPlayer.hasEnoughtMoney(buyprice) && !alreadysay) {
								playe.sendMessage(ChatColor.RED
										+ Ultimate_Economy.messages.getString("not_enough_money_personal"));
								alreadysay = true;
							}
						} else if (clickType == ClickType.RIGHT && !itemString.contains("ANVIL_0")
								&& !itemString.contains("CRAFTING_TABLE_0") && sellprice != 0.0
								|| clickType == ClickType.RIGHT && isPlayershop
										&& playe.getName().equals(playerShopOwner.getName())
										&& inventoryplayer.containsAtLeast(clickedItem, amount)) {
							
							ItemStack itemStack = shop.getItemStack(itemString);
							itemStack.setAmount(amount);
							if (inventoryContainsItems(inventoryplayer, itemStack, amount)) {
								if (isPlayershop && !playerShopOwner.getName().equals(playe.getName())
										|| !isPlayershop) {
									if (!isPlayershop
											|| (isPlayershop && playerShopOwner.hasEnoughtMoney(sellprice))) {
										ecoPlayer.increasePlayerAmount(sellprice);
										// only playershop
										if (isPlayershop) {
											playerShopOwner.decreasePlayerAmount(sellprice, false);
										}
										if (amount > 1) {
											playe.sendMessage(
													ChatColor.GREEN + String.valueOf(amount) + " " + ChatColor.GOLD
															+ Ultimate_Economy.messages
																	.getString("shop_sell_plural")
															+ " " + ChatColor.GREEN + sellprice + ChatColor.GREEN
															+ "$" + ChatColor.GOLD + ".");
										} else {
											playe.sendMessage(
													ChatColor.GREEN + String.valueOf(amount) + " " + ChatColor.GOLD
															+ Ultimate_Economy.messages
																	.getString("shop_sell_singular")
															+ " " + ChatColor.GREEN + sellprice + ChatColor.GREEN
															+ "$" + ChatColor.GOLD + ".");
										}
									}
									// only playershop
									else if (isPlayershop) {
										playe.sendMessage(ChatColor.RED + Ultimate_Economy.messages
												.getString("shopowner_not_enough_money"));
									}
								}
								// only playershop
								else if (isPlayershop) {
									if (amount > 1) {
										playe.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("shop_added_item_plural1")
												+ " " + ChatColor.GREEN + String.valueOf(amount) + ChatColor.GOLD
												+ " "
												+ Ultimate_Economy.messages.getString("shop_added_item_plural2"));
									} else {
										playe.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("shop_added_item_singular1")
												+ " " + ChatColor.GREEN + String.valueOf(amount) + ChatColor.GOLD
												+ " "
												+ Ultimate_Economy.messages.getString("shop_added_item_singular2"));
									}
									playerShop.increaseStock(clickedItemString, amount);
									playerShop.refreshStockpile();
								}
								removeItemFromInventory(inventoryplayer, itemStack, amount);
								break;
							}
						} else if (clickType == ClickType.SHIFT_RIGHT && sellprice != 0.0
								|| clickType == ClickType.SHIFT_RIGHT
										&& playe.getName().equals(playerShopOwner.getName())
										&& inventoryplayer.containsAtLeast(clickedItem, amount)) {
							ItemStack itemStack = shop.getItemStack(itemString);
							if (inventoryContainsItems(inventoryplayer, itemStack, 1)) {
								ItemStack[] i = inventoryplayer.getContents();
								int itemAmount = 0;
								double iA = 0.0;
								double newprice = 0;
								for (ItemStack is1 : i) {
									if (is1 != null) {
										ItemStack is = new ItemStack(is1);
										itemStack.setAmount(is.getAmount());
										if (is.toString().equals(itemStack.toString())) {
											itemAmount = itemAmount + is.getAmount();
										}
									}
								}
								iA = Double.valueOf(String.valueOf(itemAmount));
								newprice = sellprice / amount * iA;
								if (isPlayershop && !playerShopOwner.getName().equals(playe.getName())
										|| !isPlayershop) {
									if ((isPlayershop && playerShopOwner.hasEnoughtMoney(newprice))
											|| !isPlayershop) {
										if (itemAmount > 1) {
											playe.sendMessage(ChatColor.GREEN + String.valueOf(itemAmount) + " "
													+ ChatColor.GOLD
													+ Ultimate_Economy.messages.getString("shop_sell_plural") + " "
													+ ChatColor.GREEN + newprice + ChatColor.GREEN + "$"
													+ ChatColor.GOLD + ".");
										} else {
											playe.sendMessage(ChatColor.GREEN + String.valueOf(itemAmount) + " "
													+ ChatColor.GOLD
													+ Ultimate_Economy.messages.getString("shop_sell_singular")
													+ " " + ChatColor.GREEN + newprice + ChatColor.GREEN + "$"
													+ ChatColor.GOLD + ".");
										}
										ecoPlayer.increasePlayerAmount(newprice);
										// only playershop
										if (isPlayershop) {
											playerShopOwner.decreasePlayerAmount(newprice, false);
										}
									}
									// only playershop
									else if (isPlayershop) {
										playe.sendMessage(ChatColor.RED + Ultimate_Economy.messages
												.getString("shopowner_not_enough_money"));
									}
								}
								// only playershop
								else if (isPlayershop) {
									if (itemAmount > 1) {
										playe.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("shop_added_item_plural1")
												+ " " + ChatColor.GREEN + String.valueOf(itemAmount)
												+ ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_added_item_plural2"));
									} else {
										playe.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("shop_added_item_singular1")
												+ " " + ChatColor.GREEN + String.valueOf(itemAmount)
												+ ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_added_item_singular2"));
									}
									playerShop.increaseStock(clickedItemString, itemAmount);
									playerShop.refreshStockpile();
								}
								itemStack.setAmount(itemAmount);
								removeItemFromInventory(inventoryplayer, itemStack, itemAmount);
								break;
							}
						}
					} catch (PlayerException | ShopSystemException e) {
						Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
					}
				}
			}
		}
	}
}