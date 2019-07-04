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
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.jobsystem.Job;
import com.ue.jobsystem.JobCenter;
import com.ue.player.EconomyPlayer;
import com.ue.shopsystem.AdminShop;
import com.ue.shopsystem.PlayerShop;
import com.ue.shopsystem.Shop;
import com.ue.shopsystem.Spawner;
import com.ue.townsystem.Plot;
import com.ue.townsystem.Town;
import com.ue.townsystem.TownWorld;
import com.ue.vault.Economy_UltimateEconomy;
import com.ue.vault.VaultHook;

import metrics.Metrics;

public class Ultimate_Economy extends JavaPlugin implements Listener {

	/*
	 * Lukas Heubach permission tp to town buyable regions | add coOwner<-
	 * leaderboard start money amount town bank amount in scoreboard für town owner
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
		messages = ResourceBundle.getBundle("lang.MessagesBundle", currentLocale);

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
		if (command.getName().equals("jobcenter")) {
			if (args.length == 1) {
				if (args[0].equals("")) {
					list.add("create");
					list.add("delete");
					list.add("move");
					list.add("job");
					list.add("addJob");
					list.add("removeJob");
				} else if (args.length == 1) {
					if ("create".contains(args[0])) {
						list.add("create");
					}
					if ("delete".contains(args[0])) {
						list.add("delete");
					}
					if ("move".contains(args[0])) {
						list.add("move");
					}
					if ("job".contains(args[0])) {
						list.add("job");
					}
					if ("addJob".contains(args[0])) {
						list.add("addJob");
					}
					if ("removeJob".contains(args[0])) {
						list.add("removeJob");
					}
				}
			} else if (args[0].equals("delete") || args[0].equals("move") || args[0].equals("removeJob")
					|| args[0].equals("addJob")) {
				if (args.length == 2) {
					List<String> temp = getConfig().getStringList("JobCenterNames");
					if (args[1].equals("")) {
						list = temp;
					} else {
						for (String jobname : temp) {
							if (jobname.contains(args[1])) {
								list.add(jobname);
							}
						}
					}
				} else if (args[0].equals("removeJob") || args[0].equals("addJob")) {
					if (args.length == 3) {
						list = getJobList(args[2]);
					} else if (args.length == 4 && args[0].equals("addJob")) {
						list = getMaterialList(args[3]);
					}
				}
			} else if (args[0].equals("job")) {
				if (args[1].equals("")) {
					list.add("createJob");
					list.add("delJob");
					list.add("addItem");
					list.add("removeItem");
					list.add("addFisher");
					list.add("removeFisher");
					list.add("addMob");
					list.add("removeMob");
				} else if (args[1].equals("addItem") || args[1].equals("removeItem") || args[1].equals("addFisher")
						|| args[1].equals("removeFisher") || args[1].equals("removeFisher") || args[1].equals("addMob")
						|| args[1].equals("removeMob")) {
					if (args.length == 3) {
						list = getJobList(args[2]);
					} else if (args[1].equals("addItem") || args[1].equals("removeItem") || args[1].equals("addItem")) {
						if (args.length == 4) {
							list = getMaterialList(args[3]);
						}
					} else if (args[1].equals("addMob") || args[1].equals("removeMob")) {
						if (args.length == 4) {
							list = getEntityList(args[3]);
						}
					} else if (args[1].equals("addFisher") || args[1].equals("removeFisher")) {
						if (args.length == 4) {
							if (args[3].equals("")) {
								list.add("fish");
								list.add("treasure");
								list.add("junk");
							} else {
								if ("fish".contains(args[3])) {
									list.add("fish");
								}
								if ("treasure".contains(args[3])) {
									list.add("treasure");
								}
								if ("junk".contains(args[3])) {
									list.add("junk");
								}
							}
						}
					}
				} else if (args[1].equals("delJob")) {
					if (args.length == 3) {
						list = getJobList(args[2]);
					}
				} else if (args.length == 2) {
					if ("createJob".contains(args[1])) {
						list.add("createJob");
					}
					if ("delJob".contains(args[1])) {
						list.add("delJob");
					}
					if ("addItem".contains(args[1])) {
						list.add("addItem");
					}
					if ("removeItem".contains(args[1])) {
						list.add("removeItem");
					}
					if ("addFisher".contains(args[1])) {
						list.add("addFisher");
					}
					if ("delFisher".contains(args[1])) {
						list.add("delFisher");
					}
					if ("addMob".contains(args[1])) {
						list.add("addMob");
					}
					if ("removeMob".contains(args[1])) {
						list.add("removeMob");
					}
				}
			}
		} else if (command.getName().equals("ue-language")) {
			if (args[0].equals("")) {
				list.add("de");
				list.add("en");
				list.add("cs");
				list.add("dn");
			} else if (args[0].equals("de")) {
				list.add("DE");
			} else if (args[0].equals("en")) {
				list.add("US");
			} else if (args[0].equals("cs")) {
				list.add("CZ");
			} else if (args[0].equals("dn")) {
				list.add("FR");
			}
		} else if (command.getName().equals("adminshop") || command.getName().equals("playershop")) {
			if (args[0].equals("")) {
				list.add("create");
				list.add("delete");
				list.add("move");
				list.add("editShop");
				list.add("addItem");
				list.add("removeItem");
				list.add("editItem");
				list.add("addEnchantedItem");
				list.add("addPotion");
				if (command.getName().equals("adminshop")) {
					list.add("addSpawner");
					list.add("removeSpawner");
				} else {
					list.add("changeOwner");
				}
			} else if (args[0].equals("delete") || args[0].equals("addItem") || args[0].equals("removeItem")
					|| args[0].equals("addSpawner") || args[0].equals("removeSpawner") || args[0].equals("editShop")
					|| args[0].equals("addEnchantedItem") || args[0].equals("addPotion")
					|| args[0].equals("changeOwner")) {
				if (args.length == 2) {
					if (command.getName().equals("adminshop")) {
						list = getAdminShopList(args[1]);
					} else {
						list = getPlayerShopList(args[1], sender.getName());
					}
				} else if (args.length == 3) {
					if (args[0].equals("addItem") || args[0].equals("addEnchantedItem")) {
						list = getMaterialList(args[2]);
					} else if (args[0].equals("addPotion")) {
						if (args[2].equals("")) {
							for (PotionType pType : PotionType.values()) {
								list.add(pType.name().toLowerCase());
							}
						} else {
							for (PotionType pType : PotionType.values()) {
								if (pType.name().toLowerCase().contains(args[2])) {
									list.add(pType.name().toLowerCase());
								}
							}
						}
					} else if (args[0].equals("addSpawner")) {
						list = getEntityList(args[2]);
					}
				} else if (args.length == 4 && args[0].equals("addPotion")) {
					if (args[3].equals("")) {
						for (PotionEffectType peType : PotionEffectType.values()) {
							if (peType != null) {
								list.add(peType.getName().toLowerCase());
							}
						}
					} else {
						for (PotionEffectType peType : PotionEffectType.values()) {
							if (peType != null && peType.getName().toLowerCase().contains(args[3])) {
								list.add(peType.getName().toLowerCase());
							}
						}
					}
				} else if (args.length == 5 && args[0].equals("addPotion")) {
					if (args[4].equals("")) {
						list.add("extended");
						list.add("upgraded");
						list.add("none");
					} else {
						if ("extended".contains(args[4])) {
							list.add("extended");
						}
						if ("upgraded".contains(args[4])) {
							list.add("upgraded");
						}
						if ("none".contains(args[4])) {
							list.add("none");
						}
					}
				} else if (args[0].equals("addEnchantedItem") && args.length >= 7 && (args.length % 2) == 0) {
					if (args[args.length - 1].equals("")) {
						for (Enchantment enchantment : Enchantment.values()) {
							if (enchantment != null) {
								list.add(enchantment.getKey().getKey());
							}
						}
					} else {
						for (Enchantment enchantment : Enchantment.values()) {
							if (enchantment != null && enchantment.getKey().getKey().contains(args[args.length - 1])) {
								list.add(enchantment.getKey().getKey().toLowerCase());
							}
						}
					}
				}
			} else if (args.length == 1) {
				if ("create".contains(args[0])) {
					list.add("create");
				}
				if ("delete".contains(args[0])) {
					list.add("delete");
				}
				if ("move".contains(args[0])) {
					list.add("move");
				}
				if ("addItem".contains(args[0])) {
					list.add("addItem");
				}
				if ("removeItem".contains(args[0])) {
					list.add("removeItem");
				}
				if ("editItem".contains(args[0])) {
					list.add("editItem");
				}
				if ("editShop".contains(args[0])) {
					list.add("editShop");
				}
				if ("addEnchantedItem".contains(args[0])) {
					list.add("addEnchantedItem");
				}
				if ("addPotion".contains(args[0])) {
					list.add("addPotion");
				}
				if (command.getName().equals("adminshop")) {
					if ("addSpawner".contains(args[0])) {
						list.add("addSpawner");
					}
					if ("removeSpawner".contains(args[0])) {
						list.add("removeSpawner");
					}
				} else {
					if ("changeOwner".contains(args[0])) {
						list.add("changeOwner");
					}
				}
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
		} else if (command.getName().equals("town")) {
			if (args[0].equals("")) {
				list.add("create");
				list.add("delete");
				list.add("expand");
				list.add("addCoOwner");
				list.add("removeCoOwner");
				list.add("setTownSpawn");
				list.add("moveTownManager");
				list.add("plot");
				list.add("pay");
				list.add("tp");
				list.add("bank");
			} else if (args.length == 1) {
				if ("create".contains(args[0])) {
					list.add("create");
				}
				if ("delete".contains(args[0])) {
					list.add("delete");
				}
				if ("expand".contains(args[0])) {
					list.add("expand");
				}
				if ("addCoOwner".contains(args[0])) {
					list.add("addCoOwner");
				}
				if ("removeCoOwner".contains(args[0])) {
					list.add("removeCoOwner");
				}
				if ("setTownSpawn".contains(args[0])) {
					list.add("setTownSpawn");
				}
				if ("moveTownManager".contains(args[0])) {
					list.add("moveTownManager");
				}
				if ("plot".contains(args[0])) {
					list.add("plot");
				}
				if ("pay".contains(args[0])) {
					list.add("pay");
				}
				if ("tp".contains(args[0])) {
					list.add("tp");
				}
				if ("bank".contains(args[0])) {
					list.add("bank");
				}
			} else if (args[0].equals("delete") || args[0].equals("expand") || args[0].equals("setTownSpawn")
					|| args[0].equals("bank") || args[0].equals("addCoOwner") || args[0].equals("removeCoOwner")) {
				try {
					if (args[1].equals("")) {
						list.addAll(EconomyPlayer.getEconomyPlayerByName(sender.getName()).getJoinedTownList());
					} else if (args.length == 2) {
						List<String> list2 = EconomyPlayer.getEconomyPlayerByName(sender.getName()).getJoinedTownList();
						for (String string : list2) {
							if (string.contains(args[1])) {
								list.add(string);
							}
						}
					}
				} catch (PlayerException e) {
					Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
				}
			} else if (args[0].equals("pay") || args[0].equals("tp")) {
				if (args[1].equals("")) {
					list.addAll(Town.getTownNameList());
				} else if (args.length == 2) {
					List<String> list2 = Town.getTownNameList();
					for (String string : list2) {
						if (string.contains(args[1])) {
							list.add(string);
						}
					}
				}
			} else if (args[0].equals("plot")) {
				if (args[1].equals("")) {
					list.add("setForSale");
				} else if (args.length == 2) {
					if ("setForSale".contains(args[1])) {
						list.add("setForSale");
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
								&& !args[0].equals("dn")) {
							player.sendMessage(ChatColor.RED + messages.getString("invalid_language"));
						} else if (!args[1].equals("CZ") && !args[1].equals("DE") && !args[1].equals("US")
								&& !args[1].equals("FR")) {
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
								player.sendMessage(ChatColor.GREEN + args[1] + ChatColor.GOLD
										+ Ultimate_Economy.messages.getString("townworld_enable") + ".");
							} else {
								player.sendMessage("/townWorld enable <worldname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("disable")) {
							if (args.length == 2) {
								TownWorld.deleteTownWorld(args[1]);
								player.sendMessage(ChatColor.GREEN + args[1] + ChatColor.GOLD
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
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// towns
				else if (label.equalsIgnoreCase("town")) {
					if (args.length != 0) {
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						if (args[0].equals("create")) {
							if (args.length == 2) {
								TownWorld tWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								tWorld.createTown(args[1], player.getLocation(), ecoPlayer);
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("town_create")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + "!");
							} else {
								player.sendMessage("/town create <townname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("delete")) {
							if (args.length == 2) {
								TownWorld tWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								tWorld.dissolveTown(args[1], player.getName());
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("town_delete1")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
										+ Ultimate_Economy.messages.getString("town_delete2"));
							} else {
								player.sendMessage("/town delete <townname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("expand")) {
							if (args.length == 2) {
								TownWorld tWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								tWorld.expandTown(args[1], player.getLocation().getChunk(), player.getName());
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("town_expand"));
							} else {
								player.sendMessage("/town expand <townname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("setTownSpawn")) {
							if (args.length == 2) {
								TownWorld tWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								Town town = tWorld.getTownByName(args[1]);
								if (town.hasCoOwnerPermission(player.getName())) {
									File file = town.setTownSpawn(tWorld.getSaveFile(), player.getLocation());
									tWorld.setSaveFile(file);
									player.sendMessage(ChatColor.GOLD + "The townspawn was set to " + ChatColor.GREEN
											+ (int) player.getLocation().getX() + "/"
											+ (int) player.getLocation().getY() + "/"
											+ (int) player.getLocation().getZ() + ChatColor.GOLD + ".");
								}
							} else {
								player.sendMessage("/town setTownSpawn <townname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("setTax")) {
							// TODO
							if (args.length == 3) {
							} else {
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addCoOwner")) {
							if (args.length == 3) {
								for (TownWorld townWorld : TownWorld.getTownWorldList()) {
									if (townWorld.getTownNameList().contains(args[1])) {
										Town town = townWorld.getTownByName(args[1]);
										if (town.isTownOwner(player.getName())) {
											townWorld.setSaveFile(town.addCoOwner(townWorld.getSaveFile(), args[2]));
											player.sendMessage(ChatColor.GOLD
													+ Ultimate_Economy.messages.getString("town_addCoOwner1") + " "
													+ ChatColor.GREEN + args[2] + ChatColor.GOLD + " "
													+ Ultimate_Economy.messages.getString("town_addCoOwner2"));
										} else {
											player.sendMessage(ChatColor.RED
													+ Ultimate_Economy.messages.getString("town_addCoOwner3"));
										}
										break;
									}
								}
							} else {
								player.sendMessage("/town addCoOwner <town> <playername>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("removeCoOwner")) {
							if (args.length == 3) {
								for (TownWorld townWorld : TownWorld.getTownWorldList()) {
									if (townWorld.getTownNameList().contains(args[1])) {
										Town town = townWorld.getTownByName(args[1]);
										if (town.isTownOwner(player.getName())) {
											townWorld.setSaveFile(town.removeCoOwner(townWorld.getSaveFile(), args[2]));
											player.sendMessage(ChatColor.GOLD
													+ Ultimate_Economy.messages.getString("town_removeCoOwner1") + " "
													+ ChatColor.GREEN + args[2] + ChatColor.GOLD + " "
													+ Ultimate_Economy.messages.getString("town_removeCoOwner2"));
										} else {
											player.sendMessage(ChatColor.RED
													+ Ultimate_Economy.messages.getString("town_removeCoOwner3"));
										}
										break;
									}
								}
							} else {
								player.sendMessage("/town removeCoOwner <town> <playername>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equalsIgnoreCase("moveTownManager")) {
							if (args.length == 1) {
								TownWorld townWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								Town town = townWorld.getTownByChunk(player.getLocation().getChunk());
								File file = town.moveTownManagerVillager(townWorld.getSaveFile(), player.getLocation(),
										player.getName());
								townWorld.setSaveFile(file);
							} else {
								player.sendMessage("/town moveTownManager");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("plot")) {
							if (args.length > 1) {
								if (args[1].equals("setForSale")) {
									if (args.length == 3) {
										TownWorld townWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
										Town town = townWorld.getTownByChunk(player.getLocation().getChunk());
										File file = town.setPlotForSale(townWorld.getSaveFile(),
												Double.valueOf(args[2]), player.getName(), player.getLocation());
										townWorld.setSaveFile(file);
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("town_plot_setForSale"));
									} else {
										player.sendMessage("/town plot setForSale <price> ");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("setForRent")) {
									if (args.length == 4) {
										// TODO
									} else {
										player.sendMessage("/town plot setForRent <townname> <price/24h>");
									}
								}
							} else {
								player.sendMessage("/town plot <setForSale/setForRent>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("tp")) {
							if (args.length == 2) {
								for (TownWorld townWorld : TownWorld.getTownWorldList()) {
									if (townWorld.getTownNameList().contains(args[1])) {
										player.teleport(townWorld.getTownByName(args[1]).getTownSpawn());
										break;
									}
								}
							} else {
								player.sendMessage("/town tp <townname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("pay")) {
							if (args.length == 3) {
								for (TownWorld townWorld : TownWorld.getTownWorldList()) {
									if (townWorld.getTownNameList().contains(args[1])) {
										double amount = Double.valueOf(args[2]);
										townWorld.setSaveFile(townWorld.getTownByName(args[1])
												.increaseTownBankAmount(townWorld.getSaveFile(), amount));
										ecoPlayer.decreasePlayerAmount(amount, true);
										player.sendMessage(
												ChatColor.GOLD + Ultimate_Economy.messages.getString("town_pay1") + " "
														+ ChatColor.GREEN + args[1] + ChatColor.GOLD
														+ Ultimate_Economy.messages.getString("town_pay2") + " "
														+ ChatColor.GREEN + amount + " $" + ChatColor.GOLD + " "
														+ Ultimate_Economy.messages.getString("town_pay3"));
										break;
									}
								}
							} else {
								player.sendMessage("/town pay <townname> <amount>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("bank")) {
							if (args.length == 2) {
								for (TownWorld townWorld : TownWorld.getTownWorldList()) {
									if (townWorld.getTownNameList().contains(args[1])) {
										Town town = townWorld.getTownByName(args[1]);
										if (town.hasCoOwnerPermission(player.getName())) {
											player.sendMessage(
													ChatColor.GOLD + Ultimate_Economy.messages.getString("town_bank")
															+ " " + ChatColor.GREEN + town.getTownBankAmount()
															+ ChatColor.GOLD + " $");
										}
										break;
									}
								}
							} else {
								player.sendMessage("/town bank <townname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else {
							player.sendMessage(
									"/town <create/delete/expand/setTownSpawn/setTax/moveTownManager/plot/pay/tp/bank>");
						}
					} else {
						player.sendMessage(
								"/town <create/delete/expand/setTownSpawn/setTax/moveTownManager/plot/pay/tp/bank>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// adminshop
				else if (label.equalsIgnoreCase("adminshop")) {
					if (args.length != 0) {
						if (args[0].equals("create")) {
							if (args.length == 3) {
								AdminShop.createAdminShop(getDataFolder(), args[1], player.getLocation(),
										Integer.valueOf(args[2]));
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_create1")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
										+ Ultimate_Economy.messages.getString("shop_create2"));
								getConfig().set("ShopNames", AdminShop.getAdminShopNameList());
								saveConfig();
							} else {
								player.sendMessage("/adminshop create <shopname> <size (9,18,27...)>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("delete")) {
							if (args.length == 2) {
								AdminShop.deleteAdminShop(args[1]);
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_delete1")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
										+ Ultimate_Economy.messages.getString("shop_delete2"));
								getConfig().set("ShopNames", AdminShop.getAdminShopNameList());
								saveConfig();
							} else {
								player.sendMessage("/adminshop delete <shopname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("move")) {
							if (args.length == 5) {
								AdminShop.getAdminShopByName(args[1]).moveShop(Double.valueOf(args[2]),
										Double.valueOf(args[3]), Double.valueOf(args[4]));
							} else {
								player.sendMessage("/adminshop move <shopname> <x> <y> <z>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("editShop")) {
							if (args.length == 2) {
								AdminShop.getAdminShopByName(args[1]).openEditor(player);
							} else {
								player.sendMessage("/adminshop editShop <shopname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addItem")) {
							if (args.length == 7) {
								if (Material.matchMaterial(args[2].toUpperCase()) == null) {
									throw new ShopSystemException(ShopSystemException.INVALID_MATERIAL);
								} else {
									ItemStack itemStack = new ItemStack(Material.getMaterial(args[2].toUpperCase()),
											Integer.valueOf(args[4]));
									AdminShop.getAdminShopByName(args[1]).addItem(Integer.valueOf(args[3]) - 1,
											Double.valueOf(args[5]), Double.valueOf(args[6]), itemStack);
									player.sendMessage(
											ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_addItem1") + " "
													+ ChatColor.GREEN + itemStack.getType().toString().toLowerCase()
													+ ChatColor.GOLD + " "
													+ Ultimate_Economy.messages.getString("shop_addItem2"));
								}
							} else {
								player.sendMessage(
										"/adminshop addItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice>");
								player.sendMessage(Ultimate_Economy.messages.getString("shop_addItem_errorinfo"));
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("removeItem")) {
							if (args.length == 3) {
								AdminShop shop = AdminShop.getAdminShopByName(args[1]);
								String itemName = shop.getItem(Integer.valueOf(args[2])).getType().toString()
										.toLowerCase();
								shop.removeItem(Integer.valueOf(args[2]) - 1);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_removeItem1") + " "
												+ ChatColor.GREEN + itemName + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_removeItem2"));
							} else {
								player.sendMessage("/adminshop removeItem <shopname> <slot (> 0)>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addPotion")) {
							if (args.length == 9) {
								handleAddPotion(player, AdminShop.getAdminShopByName(args[1]), args);
							} else {
								player.sendMessage(
										"/adminshop addPotion <shopname> <potionType> <potionEffect> <extended/upgraded/none> <slot> <amount> <sellprice> <buyprice>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addEnchantedItem")) {
							if (args.length >= 9) {
								handleAddEnchantedItem(player, args, AdminShop.getAdminShopByName(args[1]));
							} else {
								player.sendMessage(
										"/adminshop addEnchantedItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice> [<enchantment> <lvl>]");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addSpawner")) {
							if (args.length == 5) {
								ItemStack itemStack = new ItemStack(Material.SPAWNER, 1);
								ItemMeta meta = itemStack.getItemMeta();
								meta.setDisplayName(args[2].toUpperCase());
								itemStack.setItemMeta(meta);
								AdminShop.getAdminShopByName(args[1]).addItem(Integer.valueOf(args[3]) - 1, 0.0,
										Double.valueOf(args[4]), itemStack);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_addSpawner1") + " "
												+ ChatColor.GREEN + itemStack.getType().toString().toLowerCase()
												+ ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_addSpawner2"));
							} else {
								player.sendMessage("/adminshop addSpawner <shopname> <entity type> <slot> <buyPrice>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("removeSpawner")) {
							if (args.length == 3) {
								AdminShop shop = AdminShop.getAdminShopByName(args[1]);
								String itemName = shop.getItem(Integer.valueOf(args[2])).getType().toString()
										.toLowerCase();
								shop.removeItem(Integer.valueOf(args[2]) - 1);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_removeSpawner1")
												+ " " + ChatColor.GREEN + itemName + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_removeSpawner1"));
							} else {
								player.sendMessage("/adminshop removeSpawner <shopname> <slot>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("editItem")) {
							if (args.length == 6) {
								player.sendMessage(AdminShop.getAdminShopByName(args[1])
										.editItem(Integer.valueOf(args[2]), args[3], args[4], args[5]));
							} else {
								player.sendMessage(
										"/adminshop editItem <shopname> <slot> <amount> <sellPrice> <buyPrice>");
								player.sendMessage(Ultimate_Economy.messages.getString("shop_editItem_errorinfo"));
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else {
							player.sendMessage(
									"/adminshop <create/delete/move/editShop/addItem/addEnchantedItem/addPotion/editItem/removeItem/addSpawner/removeSpawner>");
						}
					} else {
						player.sendMessage(
								"/adminshop <create/delete/move/editShop/addItem/addEnchantedItem/addPotion/editItem/removeItem/addSpawner/removeSpawner>");
					}
				}
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// playershop
				if (label.equalsIgnoreCase("playershop")) {
					if (args.length != 0) {
						if (args[0].equals("create")) {
							if (args.length == 3) {
								PlayerShop.createPlayerShop(getDataFolder(), args[1] + "_" + player.getName(),
										player.getLocation(), Integer.valueOf(args[2]));
								getConfig().set("PlayerShopNames", PlayerShop.getPlayerShopNameList());
								saveConfig();
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_create1")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
										+ Ultimate_Economy.messages.getString("shop_create2"));
							} else {
								player.sendMessage("/playershop create <shopname> <size (9,18,27...)>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("delete")) {
							if (args.length == 2) {
								PlayerShop.deletePlayerShop(args[1] + "_" + player.getName());
								getConfig().set("PlayerShopNames", PlayerShop.getPlayerShopNameList());
								saveConfig();
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_delete1")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
										+ Ultimate_Economy.messages.getString("shop_delete2"));
							} else {
								player.sendMessage("/playershop delete <shopname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("move")) {
							if (args.length == 5) {
								PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName()).moveShop(
										Double.valueOf(args[2]), Double.valueOf(args[3]), Double.valueOf(args[4]));
							} else {
								player.sendMessage("/playershop move <shopname> <x> <y> <z>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("changeOwner")) {
							if (args.length == 3) {
								if (EconomyPlayer.getEconomyPlayerNameList().contains(args[1] + "_" + args[2])) {
									player.sendMessage(
											ChatColor.RED + Ultimate_Economy.messages.getString("shop_changeOwner1")
													+ " " + ChatColor.GREEN + args[2] + ChatColor.RED + " "
													+ Ultimate_Economy.messages.getString("shop_changeOwner2"));
								} else {
									PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName()).setOwner(args[2],
											getDataFolder());
									getConfig().set("PlayerShopNames", PlayerShop.getPlayerShopNameList());
									saveConfig();
									player.sendMessage(
											ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_changeOwner3")
													+ " " + ChatColor.GREEN + args[2] + ChatColor.GOLD + ".");
									for (Player pl : Bukkit.getOnlinePlayers()) {
										if (pl.getName().equals(args[2])) {
											pl.sendMessage(ChatColor.GOLD
													+ Ultimate_Economy.messages.getString("shop_changeOwner4") + " "
													+ ChatColor.GREEN + args[1] + ChatColor.GOLD
													+ Ultimate_Economy.messages.getString("shop_changeOwner5") + " "
													+ ChatColor.GREEN + player.getName());
											break;
										}
									}
								}
							} else {
								player.sendMessage("/playershop changeOwner <shopname> <new owner>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("editShop")) {
							if (args.length == 2) {
								PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName()).openEditor(player);
							} else {
								player.sendMessage("/player editShop <shopname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addItem")) {
							if (args.length == 7) {
								if (!args[2].toUpperCase().equals("HAND")
										&& Material.matchMaterial(args[2].toUpperCase()) == null) {
									throw new ShopSystemException(ShopSystemException.INVALID_MATERIAL);
								} else {
									ItemStack itemStack = new ItemStack(Material.getMaterial(args[2].toUpperCase()),
											Integer.valueOf(args[4]));
									PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName()).addItem(
											Integer.valueOf(args[3]) - 1, Double.valueOf(args[5]),
											Double.valueOf(args[6]), itemStack);
									player.sendMessage(
											ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_addItem1") + " "
													+ ChatColor.GREEN + itemStack.getType().toString().toLowerCase()
													+ ChatColor.GOLD + " "
													+ Ultimate_Economy.messages.getString("shop_addItem2"));
								}
							} else {
								player.sendMessage(
										"/playershop addItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice>");
								player.sendMessage(Ultimate_Economy.messages.getString("shop_addItem_errorinfo"));
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addPotion")) {
							if (args.length == 9) {
								PlayerShop shop = PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName());
								handleAddPotion(player, shop, args);
							} else {
								player.sendMessage(
										"/playershop addPotion <shopname> <potionType> <potionEffect> <extended/upgraded/none> <slot> <amount> <sellprice> <buyprice>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addEnchantedItem")) {
							if (args.length >= 9) {
								PlayerShop shop = PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName());
								handleAddEnchantedItem(player, args, shop);
							} else {
								player.sendMessage(
										"/playershop addEnchantedItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice> [<enchantment> <lvl>]");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("removeItem")) {
							if (args.length == 3) {
								PlayerShop shop = PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName());
								String itemName = shop.getItem(Integer.valueOf(args[2])).getType().toString()
										.toLowerCase();
								shop.removeItem(Integer.valueOf(args[2]) - 1);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_removeItem1") + " "
												+ ChatColor.GREEN + itemName + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_removeItem2"));
							} else {
								player.sendMessage("/playershop removeItem <shopname> <slot (> 0)>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("editItem")) {
							if (args.length == 6) {
								PlayerShop shop = PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName());
								player.sendMessage(shop.editItem(Integer.valueOf(args[2]), args[3], args[4], args[5]));
							} else {
								player.sendMessage(
										"/playershop editItem <shopname> <slot> <amount> <sellPrice> <buyPrice>");
								player.sendMessage(Ultimate_Economy.messages.getString("shop_editItem_errorinfo"));
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else {
							player.sendMessage(
									"/playershop <create/delete/move/editShop/addItem/addEnchantedItem/addPotion/removeItem/editItem>");
						}
					} else {
						player.sendMessage(
								"/playershop <create/delete/move/editShop/addItem/addEnchantedItem/addPotion/removeItem/editItem>");
					}
				}
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Jobcenter
				if (label.equalsIgnoreCase("jobcenter")) {
					if (args.length != 0) {
						if (args[0].equals("create")) {
							if (args.length == 3) {
								JobCenter.createJobCenter(getServer(), getDataFolder(), args[1], player.getLocation(),
										Integer.parseInt(args[2]));
								getConfig().set("JobCenterNames", JobCenter.getJobCenterNameList());
								saveConfig();
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("jobcenter_create1") + " "
												+ ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_create2"));
							} else {
								player.sendMessage("/jobcenter create <name> <size (9,18,27...)>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("delete")) {
							if (args.length == 2) {
								JobCenter.deleteJobCenter(args[1]);
								getConfig().set("JobCenterNames", JobCenter.getJobCenterNameList());
								saveConfig();
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("jobcenter_delete1") + " "
												+ ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_delete2"));
							} else {
								player.sendMessage("/jobcenter delete <name>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("move")) {
							if (args.length == 5) {
								JobCenter jobCenter = JobCenter.getJobCenterByName(args[1]);
								jobCenter.moveShop(Double.valueOf(args[2]), Double.valueOf(args[3]),
										Double.valueOf(args[4]));
							} else {
								player.sendMessage("/jobcenter move <name> <x> <y> <z>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addJob")) {
							if (args.length == 5) {
								JobCenter jobCenter = JobCenter.getJobCenterByName(args[1]);
								jobCenter.addJob(args[2], args[3], Integer.valueOf(args[4]));
								player.sendMessage(
										ChatColor.GOLD + "The job " + ChatColor.GREEN + args[2] + ChatColor.GOLD
												+ " was added to the JobCenter " + ChatColor.GREEN + args[1] + ".");
							} else {
								player.sendMessage("/jobcenter addJob <jobcentername> <jobname> <material> <slot>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("removeJob")) {
							if (args.length == 3) {
								JobCenter jobCenter = JobCenter.getJobCenterByName(args[1]);
								jobCenter.removeJob(args[2]);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("jobcenter_removeJob1")
												+ " " + ChatColor.GREEN + args[2] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_removeJob2"));
							} else {
								player.sendMessage("/jobcenter removeJob <jobcentername> <jobname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("job")) {
							if (args.length == 1) {
								player.sendMessage(
										"/jobcenter job <createJob/delJob/addItem/removeItem/addMob/removeMob/addFisher/removeFisher>");
							} else {
								if (args[1].equals("createJob")) {
									if (args.length == 3) {
										Job.createJob(this.getDataFolder(), args[2]);
										getConfig().set("JobList", Job.getJobNameList());
										saveConfig();
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_createJob1") + " "
												+ ChatColor.GREEN + args[2] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_createJob2"));
									} else {
										player.sendMessage("/jobcenter job createJob <jobname>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("delJob")) {
									if (args.length == 3) {
										Job.deleteJob(args[2]);
										getConfig().set("JobList", Job.getJobNameList());
										saveConfig();
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_delJob1") + " "
												+ ChatColor.GREEN + args[2] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_delJob2"));
									} else {
										player.sendMessage("/jobcenter job delJob <jobname>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("addMob")) {
									if (args.length == 5) {
										Job job = Job.getJobByName(args[2]);
										job.addMob(args[3], Double.valueOf(args[4]));
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_addMob1") + " "
												+ ChatColor.GREEN + args[0] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_addMob2"));
									} else {
										player.sendMessage("/jobcenter job addMob <jobname> <entity> <price>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("removeMob")) {
									if (args.length == 4) {
										Job job = Job.getJobByName(args[2]);
										job.deleteMob(args[3]);
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_removeMob1") + " "
												+ ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_removeMob2") + " "
												+ ChatColor.GREEN + job.getName() + ".");
									} else {
										player.sendMessage("/jobcenter job removeMob <jobname> <entity>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("addItem")) {
									if (args.length == 5) {
										Job job = Job.getJobByName(args[2]);
										job.addItem(args[3], Double.valueOf(args[4]));
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_addItem1") + " "
												+ ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_addItem2") + " "
												+ ChatColor.GREEN + job.getName() + ".");
									} else {
										player.sendMessage("/jobcenter job addItem <jobname> <material> <price>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("removeItem")) {
									if (args.length == 4) {
										Job job = Job.getJobByName(args[2]);
										job.deleteItem(args[3]);
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_removeItem1") + " "
												+ ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_removeItem2") + " "
												+ ChatColor.GREEN + job.getName() + ".");
									} else {
										player.sendMessage("/jobcenter job removeItem <jobname> <material>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("addFisher")) {
									if (args.length == 5) {
										Job job = Job.getJobByName(args[2]);
										job.addFisherLootType(args[3], Double.valueOf(args[4]));
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_addFisher1") + " "
												+ ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_addFisher2") + " "
												+ ChatColor.GREEN + job.getName() + ".");
									} else {
										player.sendMessage(
												"/jobcenter job addFisher <jobname> <fish/treasure/junk> <price>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("removeFisher")) {
									if (args.length == 4) {
										Job job = Job.getJobByName(args[2]);
										job.delFisherLootType(args[3]);
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_removeFisher1") + " "
												+ ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_removeFisher2") + " "
												+ ChatColor.GREEN + job.getName() + ".");
									} else {
										player.sendMessage(
												"/jobcenter job removeFisher <jobname> <fish/treasure/junk>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else {
									player.sendMessage(
											"/jobcenter job <createJob/delJob/addItem/addFisher/removeFisher/removeItem/addMob/removeMob>");
								}
							}
						} else {
							player.sendMessage("/jobcenter <create/delete/move/job/addJob>");
						}
					} else {
						player.sendMessage("/jobcenter <create/delete/move/job/addjob>");
					}
				}
			} catch (PlayerException | ShopSystemException | TownSystemException | JobSystemException e1) {
				player.sendMessage(ChatColor.RED + e1.getMessage());
			} catch (NumberFormatException e2) {
				player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
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
				if (inventoryname.contains("-Editor") && meta.getDisplayName() != null) {
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
								Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
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
	private List<String> getMaterialList(String arg) {
		Material[] materials = Material.values();
		List<String> list = new ArrayList<>();
		if (arg.equals("")) {
			for (Material material : materials) {
				list.add(material.name().toLowerCase());
			}
		} else {
			for (Material material : materials) {
				if (material.name().toLowerCase().contains(arg)) {
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
		if (arg.equals("")) {
			for (EntityType entityname : entityTypes) {
				list.add(entityname.name().toLowerCase());
			}
		} else {
			for (EntityType entityname : entityTypes) {
				if (entityname.name().toLowerCase().contains(arg)) {
					list.add(entityname.name().toLowerCase());
				}
			}
		}
		return list;
	}

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
	private List<String> getPlayerShopList(String arg, String playerName) {
		List<String> temp = getConfig().getStringList("PlayerShopNames");
		List<String> list = new ArrayList<>();
		if (arg.equals("")) {
			for (String shopName : temp) {
				if (shopName.substring(shopName.indexOf("_") + 1).equals(playerName)) {
					list.add(shopName.substring(0, shopName.indexOf("_")));
				}
			}
		} else {
			for (String shopName : temp) {
				if (shopName.substring(0, shopName.indexOf("_")).contains(arg)
						&& shopName.substring(shopName.indexOf("_") + 1).equals(playerName)) {
					list.add(shopName.substring(0, shopName.indexOf("_")));
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
	private void handleAddPotion(Player p, Shop s, String[] args) throws ShopSystemException {
		if (!args[2].equalsIgnoreCase("potion") && !args[2].equalsIgnoreCase("splash_potion")
				&& !args[2].equalsIgnoreCase("lingering_potion")) {
			throw new ShopSystemException(ShopSystemException.INVALID_POTIONTYPE);
		} else if (!args[4].equalsIgnoreCase("extended") && !args[4].equalsIgnoreCase("upgraded")
				&& !args[4].equalsIgnoreCase("none")) {
			throw new ShopSystemException(ShopSystemException.INVALID_POTION_PROPERTY);
		} else if (!args[2].toUpperCase().equals("HAND") && Material.matchMaterial(args[2].toUpperCase()) == null) {
			throw new ShopSystemException(ShopSystemException.INVALID_MATERIAL);
		} else {
			ItemStack itemStack = new ItemStack(Material.valueOf(args[2].toUpperCase()), Integer.valueOf(args[6]));
			PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
			boolean extended = false;
			boolean upgraded = false;
			if (args[4].equalsIgnoreCase("extended")) {
				extended = true;
			} else if (args[4].equalsIgnoreCase("upgraded")) {
				upgraded = true;
			}
			meta.setBasePotionData(new PotionData(PotionType.valueOf(args[3].toUpperCase()), extended, upgraded));
			itemStack.setItemMeta(meta);
			s.addItem(Integer.valueOf(args[5]) - 1, Double.valueOf(args[7]), Double.valueOf(args[8]), itemStack);
			p.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_addItem1") + " " + ChatColor.GREEN
					+ itemStack.getType().toString().toLowerCase() + ChatColor.GOLD + " "
					+ Ultimate_Economy.messages.getString("shop_addItem2"));
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void handleAddEnchantedItem(Player p, String[] args, Shop s) throws ShopSystemException {
		if (!args[2].toUpperCase().equals("HAND") && Material.matchMaterial(args[2].toUpperCase()) == null) {
			throw new ShopSystemException(ShopSystemException.INVALID_MATERIAL);
		} else {
			Integer length = args.length - 7;
			if (length % 2 == 0) {
				ArrayList<String> enchantmentList = new ArrayList<>();
				for (Integer i = 1; i < length; i = i + 2) {
					enchantmentList.add(args[i + 6].toLowerCase() + "-" + args[i + 7]);
				}
				ItemStack iStack = new ItemStack(Material.valueOf(args[2].toUpperCase()), Integer.valueOf(args[4]));
				ArrayList<String> newEnchantmentList = Shop.addEnchantments(iStack, enchantmentList);
				if (newEnchantmentList.size() < enchantmentList.size()) {
					p.sendMessage(ChatColor.RED + "Not all enchantments could be used!");
				}
				s.addItem(Integer.valueOf(args[3]) - 1, Double.valueOf(args[5]), Double.valueOf(args[6]), iStack);
				p.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_addItem1") + " "
						+ ChatColor.GREEN + iStack.getType().toString().toLowerCase() + ChatColor.GOLD + " "
						+ Ultimate_Economy.messages.getString("shop_addItem2"));
			} else {
				p.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("enchantmentlist_incomplete"));
			}
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
				ItemStack clickedItem = event.getCurrentItem();
				ItemMeta itemMeta = clickedItem.getItemMeta();
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
				clickedItem.setAmount(1);
				String clickedItemString = clickedItem.toString();
				if (itemString.contains("SPAWNER_")) {
					isSpawner = true;
					clickedItemString = "SPAWNER_" + event.getCurrentItem().getItemMeta().getDisplayName();
				}
				if (itemString.equals(clickedItemString)) {
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