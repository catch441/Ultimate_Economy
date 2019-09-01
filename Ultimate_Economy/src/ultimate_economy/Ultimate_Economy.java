package ultimate_economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.jobsystem.Job;
import com.ue.jobsystem.JobCenter;
import com.ue.jobsystem.JobCommandExecutor;
import com.ue.jobsystem.JobTabCompleter;
import com.ue.metrics.Metrics;
import com.ue.player.EconomyPlayer;
import com.ue.player.PlayerCommandExecutor;
import com.ue.player.PlayerTabCompleter;
import com.ue.shopsystem.AdminShop;
import com.ue.shopsystem.AdminShopCommandExecutor;
import com.ue.shopsystem.PlayerShop;
import com.ue.shopsystem.PlayerShopCommandExecuter;
import com.ue.shopsystem.ShopTabCompleter;
import com.ue.townsystem.TownCommandExecutor;
import com.ue.townsystem.TownTabCompleter;
import com.ue.townsystem.TownWorld;
import com.ue.townsystem.TownworldCommandExecutor;
import com.ue.townsystem.TownworldTabCompleter;
import com.ue.vault.Economy_UltimateEconomy;
import com.ue.vault.VaultHook;

import lang.UTF8Control;

/**
 * @author Lukas Heubach
 */
public class Ultimate_Economy extends JavaPlugin {

	public static Ultimate_Economy getInstance;
	public static ResourceBundle messages;
	public Economy_UltimateEconomy economyImplementer;
	private VaultHook vaultHook;

	public void onEnable() {

		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		// can be removed in a future update
		else {
			getConfig().set("ItemList", null);
			getConfig().set("TownNames", null);
			saveConfig();
		}

		// config to disable/enable homes feature
		boolean homesFeature = true;
		if (getConfig().contains("homes") && !getConfig().getBoolean("homes")) {
			Field commandMapField;
			try {
				commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
				commandMapField.setAccessible(true);
				CommandMap map = (CommandMap) commandMapField.get(Bukkit.getServer().getPluginManager());
				getCommand("home").unregister(map);
				getCommand("delHome").unregister(map);
				getCommand("setHome").unregister(map);
				homesFeature = false;
			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
				Bukkit.getLogger().warning("Error on disable homes feature.");
			}
		} else if (!getConfig().contains("homes")) {
			getConfig().set("homes", true);
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
		messages = ResourceBundle.getBundle("lang.MessagesBundle", currentLocale, new UTF8Control());

		JobCenter.loadAllJobCenters(getServer(), getConfig(), getDataFolder());
		Job.loadAllJobs(getDataFolder(), getConfig());
		AdminShop.loadAllAdminShops(getConfig(), getDataFolder(), getServer());
		PlayerShop.loadAllPlayerShops(getConfig(), getDataFolder(), getServer());

		try {
			TownWorld.loadAllTownWorlds(getDataFolder(), getConfig(), getServer());
		} catch (TownSystemException e) {
			Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
		}

		try {
			EconomyPlayer.loadAllEconomyPlayers(getDataFolder());
		} catch (JobSystemException e) {
			Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
		}

		EconomyPlayer.setupConfig(getConfig());
		saveConfig();

		File spawner = new File(getDataFolder(), "SpawnerLocations.yml");
		if (!spawner.exists()) {
			try {
				spawner.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// setup command executors and tab completer
		getCommand("jobcenter").setExecutor(new JobCommandExecutor(this));
		getCommand("jobcenter").setTabCompleter(new JobTabCompleter(getConfig()));
		getCommand("town").setExecutor(new TownCommandExecutor());
		getCommand("town").setTabCompleter(new TownTabCompleter());
		getCommand("townworld").setExecutor(new TownworldCommandExecutor(this));
		getCommand("townworld").setTabCompleter(new TownworldTabCompleter());
		getCommand("adminshop").setExecutor(new AdminShopCommandExecutor(this));
		getCommand("adminshop").setTabCompleter(new ShopTabCompleter(getConfig()));
		getCommand("playershop").setTabCompleter(new ShopTabCompleter(getConfig()));
		getCommand("playershop").setExecutor(new PlayerShopCommandExecuter(this));
		getCommand("playershop").setExecutor(new PlayerShopCommandExecuter(this));
		PlayerCommandExecutor playerCommandExecutor = new PlayerCommandExecutor();
		PlayerTabCompleter playerTabCompleter = new PlayerTabCompleter();
		getCommand("bank").setExecutor(playerCommandExecutor);
		getCommand("bank").setTabCompleter(playerTabCompleter);
		if (homesFeature) {
			getCommand("home").setExecutor(playerCommandExecutor);
			getCommand("home").setTabCompleter(playerTabCompleter);
			getCommand("delHome").setExecutor(playerCommandExecutor);
			getCommand("delHome").setTabCompleter(playerTabCompleter);
			getCommand("setHome").setExecutor(playerCommandExecutor);
		}
		getCommand("giveMoney").setExecutor(playerCommandExecutor);
		getCommand("pay").setExecutor(playerCommandExecutor);
		getCommand("money").setExecutor(playerCommandExecutor);
		getCommand("myJobs").setExecutor(playerCommandExecutor);

		// spawn all spawners
		List<String> spawnerlist = new ArrayList<>();
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
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		//setup eventhandler
		getServer().getPluginManager().registerEvents(new Ultimate_EconomyEventHandler(this,spawnerlist,spawner), this);

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
				list.add("ru");
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
			} else if (args[0].equals("ru")) {
				list.add("RU");
			}
		} else if (command.getName().equals("shop")) {
			if (args.length <= 1) {
				list = getAdminShopList(args[0]);
			}
		} else if (command.getName().equals("jobInfo")) {
			if (args.length <= 1) {
				list = getJobList(args[0]);
			}
		} else if (command.getName().equals("ue-homes")) {
			if (args[0].equals("")) {
				list.add("true");
				list.add("false");
			} else if (args[0].equals("true")) {
				list.add("true");
			} else if (args[0].equals("false")) {
				list.add("false");
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
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				if (label.equalsIgnoreCase("ue-language")) {
					if (args.length == 2) {
						if (!args[0].equals("cs") && !args[0].equals("de") && !args[0].equals("en")
								&& !args[0].equals("fr") && !args[0].equals("zh") && !args[0].equals("ru")) {
							player.sendMessage(ChatColor.RED + messages.getString("invalid_language"));
						} else if (!args[1].equals("CZ") && !args[1].equals("DE") && !args[1].equals("US")
								&& !args[1].equals("FR") && !args[1].equals("CN") && !args[1].equals("RU")) {
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
				else if (label.equalsIgnoreCase("ue-homes")) {
					if (args.length == 1) {
						if (args[0].equals("true") || args[0].equals("false")) {
							getConfig().set("homes", Boolean.valueOf(args[0]));
							saveConfig();
							player.sendMessage(ChatColor.GOLD + messages.getString("ue_homes") + " " + ChatColor.GREEN
									+ args[0] + ChatColor.GOLD + ".");
						} else {
							player.sendMessage("/ue-homes <true/false>");
						}
					} else {
						player.sendMessage("/ue-homes <true/false>");
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

			} catch (PlayerException | ShopSystemException | JobSystemException e1) {
				player.sendMessage(ChatColor.RED + e1.getMessage());
			} catch (NumberFormatException e2) {
				player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
			}
		}
		return false;
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
}