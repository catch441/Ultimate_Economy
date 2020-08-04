package com.ue.ultimate_economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.DaggerServiceComponent;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServiceComponent;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.shopsystem.commands.adminshop.AdminshopCommandExecutor;
import com.ue.shopsystem.commands.adminshop.AdminshopTabCompleter;
import com.ue.shopsystem.commands.rentshop.RentshopCommandExecutor;
import com.ue.shopsystem.commands.rentshop.RentshopTabCompleter;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.RentshopManager;
import com.ue.shopsystem.logic.impl.AdminshopManager;
import com.ue.shopsystem.logic.impl.RentDailyTask;
import com.ue.townsystem.api.TownworldController;
import com.ue.townsystem.commands.TownCommandExecutor;
import com.ue.townsystem.commands.TownTabCompleter;
import com.ue.townsystem.commands.TownworldCommandExecutor;
import com.ue.townsystem.commands.TownworldTabCompleter;
import com.ue.vault.VaultHook;

/**
 * @author Lukas Heubach (catch441)
 */
public class UltimateEconomy extends JavaPlugin {

	public static UltimateEconomy getInstance;
	@Inject
	ConfigManager configManager;
	@Inject
	BankManager bankManager;
	@Inject
	EconomyPlayerManager ecoPlayerManager;
	@Inject
	JobManager jobManager;
	@Inject
	JobcenterManager jobcenterManager;
	@Inject
	AdminshopManager adminshopManager;
	@Inject
	PlayershopManager playershopManager;
	@Inject
	RentshopManager rentshopManager;
	@Inject
	VaultHook vaultHook;
	@Inject
	Metrics metrics;
	@Inject
	MessageWrapper messageWrapper;
	@Inject
	CustomSkullService skullService;
	@Inject
	@Named("ConfigCommandExecutor")
	CommandExecutor configCommandExecutor;
	@Inject
	@Named("EconomyPlayerCommandExecutor")
	CommandExecutor ecoPlayerCommandExecutor;
	@Inject
	@Named("JobCommandExecutor")
	CommandExecutor jobCommandExecutor;
	@Inject
	@Named("PlayershopCommandExecutor")
	CommandExecutor playershopCommandExecutor;
	@Inject
	@Named("EconomyPlayerTabCompleter")
	TabCompleter ecoPlayerTabCompleter;
	@Inject
	@Named("ConfigTabCompleter")
	TabCompleter configTabCompleter;
	@Inject
	@Named("JobTabCompleter")
	TabCompleter jobTabCompleter;
	@Inject
	@Named("PlayershopTabCompleter")
	TabCompleter playershopTabCompleter;

	private ServiceComponent serviceComponent;

	/**
	 * Default constructor.
	 */
	public UltimateEconomy() {
		super();
		getInstance = this;
		serviceComponent = DaggerServiceComponent.builder().build();
		serviceComponent.inject(this);
	}

	/**
	 * Constructor for MockBukkit.
	 * 
	 * @param loader
	 * @param description
	 * @param dataFolder
	 * @param file
	 */
	public UltimateEconomy(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
		super(loader, description, dataFolder, file);
		getInstance = this;
	}

	@Override
	public void onEnable() {
		loadPlugin();
		setupVault();
	}

	@Override
	public void onDisable() {
		disablePlugin();
		disableVault();
	}

	private void disablePlugin() {
		jobcenterManager.despawnAllVillagers();
		TownworldController.despawnAllVillagers();
		adminshopManager.despawnAllVillagers();
		playershopManager.despawnAllVillagers();
		rentshopManager.despawnAllVillagers();
	}

	private void disableVault() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			vaultHook.unhook();
		}
	}

	private void loadSpawners() {
		File spawner = new File(getDataFolder(), "SpawnerLocations.yml");
		if (!spawner.exists()) {
			try {
				spawner.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
		// setup eventhandler
		getServer().getPluginManager().registerEvents(new UltimateEconomyEventHandler(this, spawnerlist, spawner),
				this);
	}

	private void setupVault() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			vaultHook.hook();
		}
	}

	private void loadCommands() {
		setupCommandExecutors();
		setupTabCompleters();
		if (configManager.isHomeSystem()) {
			try {
				Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
				commandMapField.setAccessible(true);
				CommandMap map = (CommandMap) commandMapField.get(Bukkit.getServer().getPluginManager());
				setupHomeCommand(map);
				setupSetHomeCommand(map);
				setupDeleteHomeCommand(map);
			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
				// TODO einkommentieren
				// Bukkit.getLogger().warning("[Ultimate_Economy] Error on enable homes
				// feature.");
				// Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " +
				// e.getMessage());
			}
		}
	}

	private void setupSetHomeCommand(CommandMap map) {
		UltimateEconomyCommand setHome = new UltimateEconomyCommand("sethome", this);
		setHome.setDescription("Sets a homepoint.");
		setHome.setPermission("ultimate_economy.home");
		setHome.setLabel("sethome");
		setHome.setPermissionMessage("You don't have the permission.");
		setHome.setUsage("/<command> [home]");
		map.register("ultimate_economy", setHome);
		setHome.setExecutor(ecoPlayerCommandExecutor);
	}

	private void setupDeleteHomeCommand(CommandMap map) {
		UltimateEconomyCommand delHome = new UltimateEconomyCommand("delhome", this);
		delHome.setDescription("Remove a homepoint.");
		delHome.setPermission("ultimate_economy.home");
		delHome.setLabel("delhome");
		delHome.setPermissionMessage("You don't have the permission.");
		delHome.setUsage("/<command> [home]");
		map.register("ultimate_economy", delHome);
		delHome.setExecutor(ecoPlayerCommandExecutor);
		delHome.setTabCompleter(ecoPlayerTabCompleter);
	}

	private void setupHomeCommand(CommandMap map) {
		UltimateEconomyCommand home = new UltimateEconomyCommand("home", this);
		home.setDescription("Teleports you to a homepoint.");
		home.setPermission("ultimate_economy.home");
		home.setLabel("home");
		home.setPermissionMessage("You don't have the permission.");
		map.register("ultimate_economy", home);
		home.setExecutor(ecoPlayerCommandExecutor);
		home.setTabCompleter(ecoPlayerTabCompleter);
	}

	private void setupTabCompleters() {
		getCommand("jobcenter").setTabCompleter(jobTabCompleter);
		getCommand("town").setTabCompleter(new TownTabCompleter());
		getCommand("townworld").setTabCompleter(new TownworldTabCompleter());
		getCommand("adminshop").setTabCompleter(new AdminshopTabCompleter());
		getCommand("playershop").setTabCompleter(playershopTabCompleter);
		getCommand("rentshop").setTabCompleter(new RentshopTabCompleter());
		getCommand("ue-config").setTabCompleter(configTabCompleter);
		getCommand("bank").setTabCompleter(ecoPlayerTabCompleter);
	}

	private void setupCommandExecutors() {
		getCommand("jobcenter").setExecutor(jobCommandExecutor);
		getCommand("town").setExecutor(new TownCommandExecutor());
		getCommand("townworld").setExecutor(new TownworldCommandExecutor(this));
		getCommand("adminshop").setExecutor(new AdminshopCommandExecutor());
		getCommand("playershop").setExecutor(playershopCommandExecutor);
		getCommand("rentshop").setExecutor(new RentshopCommandExecutor());
		getCommand("pay").setExecutor(ecoPlayerCommandExecutor);
		getCommand("givemoney").setExecutor(ecoPlayerCommandExecutor);
		getCommand("money").setExecutor(ecoPlayerCommandExecutor);
		getCommand("myjobs").setExecutor(ecoPlayerCommandExecutor);
		getCommand("bank").setExecutor(ecoPlayerCommandExecutor);
		getCommand("ue-config").setExecutor(configCommandExecutor);
	}

	private void loadPlugin() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		setupPlugin();
		messageWrapper.loadLanguage(configManager.getLocale());
		bankManager.loadBankAccounts();
		jobManager.loadAllJobs();
		jobcenterManager.loadAllJobcenters();
		ecoPlayerManager.loadAllEconomyPlayers();
		adminshopManager.loadAllAdminShops();
		playershopManager.loadAllPlayerShops();
		rentshopManager.loadAllRentShops();

		loadCommands();
		loadSpawners();
	}

	private void setupPlugin() {
		configManager.setupConfig();
		skullService.setup();
		// setup and start RentDailyTask
		new RentDailyTask(rentshopManager, messageWrapper).runTaskTimerAsynchronously(this, 1, 1000);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if (command.getName().equals("shop")) {
			if (args.length <= 1) {
				list = getAdminShopList(args[0]);
			}
		} else if (command.getName().equals("jobinfo")) {
			if (args.length <= 1) {
				list = getJobList(args[0]);
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
				EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(player.getName());
				switch (label) {
				case "shop":
					return handleShopCommand(args, player, ecoPlayer);
				case "shoplist":
					return handleShopListCommand(player);
				case "joblist":
					return handleJobListCommand(player);
				case "jobinfo":
					return handleJobInfoCommand(args, player);
				default:
					break;
				}
			} catch (EconomyPlayerException | JobSystemException | GeneralEconomyException | ShopSystemException e) {
				player.sendMessage(e.getMessage());
			}
		}
		return true;
	}

	private boolean handleJobInfoCommand(String[] args, Player player)
			throws JobSystemException, GeneralEconomyException {
		if (args.length == 1) {
			Job job = jobManager.getJobByName(args[0]);
			player.sendMessage(messageWrapper.getString("jobinfo_info", job.getName()));
			for (String string : job.getBlockList().keySet()) {
				player.sendMessage(ChatColor.GOLD + string.toLowerCase() + " " + ChatColor.GREEN
						+ job.getBlockPrice(string) + configManager.getCurrencyText(job.getBlockPrice(string)));
			}
			for (String string : job.getFisherList().keySet()) {
				player.sendMessage(messageWrapper.getString("jobinfo_fishingprice", string.toLowerCase(),
						job.getFisherPrice(string), configManager.getCurrencyText(job.getFisherPrice(string))));
			}
			for (String string : job.getEntityList().keySet()) {
				player.sendMessage(messageWrapper.getString("jobinfo_killprice", string.toLowerCase(),
						job.getKillPrice(string), configManager.getCurrencyText(job.getKillPrice(string))));
			}
		} else {
			return false;
		}
		return true;
	}

	private boolean handleJobListCommand(Player player) {
		List<String> jobNames = jobManager.getJobNameList();
		player.sendMessage(messageWrapper.getString("joblist_info", jobNames.toString()));
		return true;
	}

	private boolean handleShopListCommand(Player player) {
		List<String> shopNames = adminshopManager.getAdminshopNameList();
		player.sendMessage(messageWrapper.getString("shoplist_info", shopNames.toString()));
		return true;
	}

	private boolean handleShopCommand(String[] args, Player player, EconomyPlayer ecoPlayer)
			throws JobSystemException, GeneralEconomyException, ShopSystemException {
		if (args.length == 1) {
			if (ecoPlayer.hasJob(jobManager.getJobByName(args[0]))) {
				adminshopManager.getAdminShopByName(args[0]).openShopInventory(player);
			} else {
				player.sendMessage(messageWrapper.getErrorString("job_not_joined"));
			}
		} else {
			return false;
		}
		return true;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Methoden
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List<String> getJobList(String arg) {
		List<String> temp = jobManager.getJobNameList();
		List<String> list = new ArrayList<>();
		if ("".equals(arg)) {
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
		if ("".equals(arg)) {
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