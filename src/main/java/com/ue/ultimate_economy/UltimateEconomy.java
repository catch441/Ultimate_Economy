package com.ue.ultimate_economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import com.bstats.metrics.Metrics;
import com.ue.bank.api.BankController;
import com.ue.config.api.ConfigController;
import com.ue.config.commands.ConfigCommandExecutor;
import com.ue.config.commands.ConfigTabCompleter;
import com.ue.eventhandling.UltimateEconomyEventHandler;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.api.JobcenterController;
import com.ue.jobsystem.commands.JobCommandExecutor;
import com.ue.jobsystem.commands.JobTabCompleter;
import com.ue.language.MessageWrapper;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.player.commands.PlayerCommandExecutor;
import com.ue.player.commands.PlayerTabCompleter;
import com.ue.shopsystem.api.AdminshopController;
import com.ue.shopsystem.api.PlayershopController;
import com.ue.shopsystem.api.RentshopController;
import com.ue.shopsystem.commands.adminshop.AdminshopCommandExecutor;
import com.ue.shopsystem.commands.adminshop.AdminshopTabCompleterImpl;
import com.ue.shopsystem.commands.playershop.PlayershopCommandExecutor;
import com.ue.shopsystem.commands.playershop.PlayershopTabCompleter;
import com.ue.shopsystem.commands.rentshop.RentshopCommandExecutor;
import com.ue.shopsystem.commands.rentshop.RentshopTabCompleter;
import com.ue.shopsystem.impl.RentDailyTask;
import com.ue.townsystem.town.commands.TownCommandExecutor;
import com.ue.townsystem.town.commands.TownTabCompleter;
import com.ue.townsystem.townworld.api.TownworldController;
import com.ue.townsystem.townworld.impl.TownworldCommandExecutor;
import com.ue.townsystem.townworld.impl.TownworldTabCompleter;
import com.ue.vault.EconomyUltimateEconomy;
import com.ue.vault.VaultHook;

/**
 * @author Lukas Heubach (catch441)
 */
public class UltimateEconomy extends JavaPlugin {

	public static UltimateEconomy getInstance;
	public EconomyUltimateEconomy economyImplementer;
	private VaultHook vaultHook;

	/**
	 * Constructor for MockBukkit.
	 */
	public UltimateEconomy() {
		super();
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
	}

	@Override
	public void onEnable() {
		loadPlugin();
		setupBstatsMetrics();
		setupVault();
	}

	@Override
	public void onDisable() {
		disablePlugin();
		disableVault();
	}

	private void disablePlugin() {
		JobcenterController.despawnAllVillagers();
		TownworldController.despawnAllVillagers();
		AdminshopController.despawnAllVillagers();
		PlayershopController.despawnAllVillagers();
		RentshopController.despawnAllVillagers();
	}

	private void setupBstatsMetrics() {
		try {
			@SuppressWarnings("unused")
			Metrics metrics = new Metrics(this);
		} catch (Exception e) {
		}
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
			economyImplementer = new EconomyUltimateEconomy();
			vaultHook = new VaultHook();
			vaultHook.hook();
		}
	}

	private void loadCommands() {
		PlayerCommandExecutor playerCommandExecutor = setupCommandExecutors();
		PlayerTabCompleter playerTabCompleter = setupTabCompleters();
		if (ConfigController.isHomeSystem()) {
			try {
				Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
				commandMapField.setAccessible(true);
				CommandMap map = (CommandMap) commandMapField.get(Bukkit.getServer().getPluginManager());
				setupHomeCommand(playerCommandExecutor, playerTabCompleter, map);
				setupSetHomeCommand(playerCommandExecutor, map);
				setupDeleteHomeCommand(playerCommandExecutor, playerTabCompleter, map);
			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
		// TODO einkommentieren		Bukkit.getLogger().warning("[Ultimate_Economy] Error on enable homes feature.");
			}
		}
	}

	private void setupSetHomeCommand(PlayerCommandExecutor playerCommandExecutor, CommandMap map) {
		UltimateEconomyCommand setHome = new UltimateEconomyCommand("sethome", this);
		setHome.setDescription("Sets a homepoint.");
		setHome.setPermission("ultimate_economy.home");
		setHome.setLabel("sethome");
		setHome.setPermissionMessage("You don't have the permission.");
		setHome.setUsage("/<command> [home]");
		map.register("ultimate_economy", setHome);
		setHome.setExecutor(playerCommandExecutor);
	}

	private void setupDeleteHomeCommand(PlayerCommandExecutor playerCommandExecutor,
			PlayerTabCompleter playerTabCompleter, CommandMap map) {
		UltimateEconomyCommand delHome = new UltimateEconomyCommand("delhome", this);
		delHome.setDescription("Remove a homepoint.");
		delHome.setPermission("ultimate_economy.home");
		delHome.setLabel("delhome");
		delHome.setPermissionMessage("You don't have the permission.");
		delHome.setUsage("/<command> [home]");
		map.register("ultimate_economy", delHome);
		delHome.setExecutor(playerCommandExecutor);
		delHome.setTabCompleter(playerTabCompleter);
	}

	private void setupHomeCommand(PlayerCommandExecutor playerCommandExecutor, PlayerTabCompleter playerTabCompleter,
			CommandMap map) {
		UltimateEconomyCommand home = new UltimateEconomyCommand("home", this);
		home.setDescription("Teleports you to a homepoint.");
		home.setPermission("ultimate_economy.home");
		home.setLabel("home");
		home.setPermissionMessage("You don't have the permission.");
		map.register("ultimate_economy", home);
		home.setExecutor(playerCommandExecutor);
		home.setTabCompleter(playerTabCompleter);
	}

	private PlayerTabCompleter setupTabCompleters() {
		getCommand("jobcenter").setTabCompleter(new JobTabCompleter());
		getCommand("town").setTabCompleter(new TownTabCompleter());
		getCommand("townworld").setTabCompleter(new TownworldTabCompleter());
		getCommand("adminshop").setTabCompleter(new AdminshopTabCompleterImpl());
		getCommand("playershop").setTabCompleter(new PlayershopTabCompleter());
		getCommand("rentshop").setTabCompleter(new RentshopTabCompleter());
		getCommand("ue-config").setTabCompleter(new ConfigTabCompleter());
		PlayerTabCompleter playerTabCompleter = new PlayerTabCompleter();
		getCommand("bank").setTabCompleter(playerTabCompleter);
		return playerTabCompleter;
	}

	private PlayerCommandExecutor setupCommandExecutors() {
		getCommand("jobcenter").setExecutor(new JobCommandExecutor());
		getCommand("town").setExecutor(new TownCommandExecutor());
		getCommand("townworld").setExecutor(new TownworldCommandExecutor(this));
		getCommand("adminshop").setExecutor(new AdminshopCommandExecutor());
		getCommand("playershop").setExecutor(new PlayershopCommandExecutor());
		getCommand("rentshop").setExecutor(new RentshopCommandExecutor());
		PlayerCommandExecutor playerCommandExecutor = new PlayerCommandExecutor();
		getCommand("pay").setExecutor(playerCommandExecutor);
		getCommand("givemoney").setExecutor(playerCommandExecutor);
		getCommand("money").setExecutor(playerCommandExecutor);
		getCommand("myjobs").setExecutor(playerCommandExecutor);
		getCommand("bank").setExecutor(playerCommandExecutor);
		getCommand("ue-config").setExecutor(new ConfigCommandExecutor());
		return playerCommandExecutor;
	}

	private void loadPlugin() {
		getInstance = this;
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		setupPlugin();
		MessageWrapper.loadLanguage();
		BankController.loadBankAccounts();
		JobController.loadAllJobs();
		JobcenterController.loadAllJobcenters();
		EconomyPlayerController.loadAllEconomyPlayers();
		TownworldController.loadAllTownWorlds();
		AdminshopController.loadAllAdminShops();
		PlayershopController.loadAllPlayerShops();
		RentshopController.loadAllRentShops();

		loadCommands();
		loadSpawners();
	}

	private void setupPlugin() {
		ConfigController.setupConfig();
		// setup and start RentDailyTask
		new RentDailyTask().runTaskTimerAsynchronously(this, 1, 1000);
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
				EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName(player.getName());
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
			} catch (PlayerException | JobSystemException | GeneralEconomyException e) {
				player.sendMessage(e.getMessage());
			}
		}
		return true;
	}

	private boolean handleJobInfoCommand(String[] args, Player player)
			throws JobSystemException, GeneralEconomyException {
		if (args.length == 1) {
			Job job = JobController.getJobByName(args[0]);
			player.sendMessage(MessageWrapper.getString("jobinfo_info", job.getName()));
			for (String string : job.getBlockList().keySet()) {
				player.sendMessage(ChatColor.GOLD + string.toLowerCase() + " " + ChatColor.GREEN
						+ job.getBlockPrice(string) + ConfigController.getCurrencyText(job.getBlockPrice(string)));
			}
			for (String string : job.getFisherList().keySet()) {
				player.sendMessage(MessageWrapper.getString("jobinfo_fishingprice", string.toLowerCase(),
						job.getFisherPrice(string), ConfigController.getCurrencyText(job.getFisherPrice(string))));
			}
			for (String string : job.getEntityList().keySet()) {
				player.sendMessage(MessageWrapper.getString("jobinfo_killprice", string.toLowerCase(),
						job.getKillPrice(string), ConfigController.getCurrencyText(job.getKillPrice(string))));
			}
		} else {
			return false;
		}
		return true;
	}

	private boolean handleJobListCommand(Player player) {
		List<String> jobNames = JobController.getJobNameList();
		player.sendMessage(MessageWrapper.getString("joblist_info", jobNames.toString()));
		return true;
	}

	private boolean handleShopListCommand(Player player) {
		List<String> shopNames = AdminshopController.getAdminshopNameList();
		player.sendMessage(MessageWrapper.getString("shoplist_info", shopNames.toString()));
		return true;
	}

	private boolean handleShopCommand(String[] args, Player player, EconomyPlayer ecoPlayer)
			throws JobSystemException, GeneralEconomyException {
		if (args.length == 1) {
			if (ecoPlayer.hasJob(JobController.getJobByName(args[0]))) {
				AdminshopController.getAdminShopByName(args[0]).openShopInventory(player);
			} else {
				player.sendMessage(MessageWrapper.getErrorString("job_not_joined"));
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
		List<String> temp = JobController.getJobNameList();
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