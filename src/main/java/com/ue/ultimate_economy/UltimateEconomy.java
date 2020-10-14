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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.DaggerServiceComponent;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.ServiceComponent;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayerEventHandler;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.api.JobsystemEventHandler;
import com.ue.shopsystem.logic.api.AdminshopManager;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.RentshopManager;
import com.ue.shopsystem.logic.api.ShopEventHandler;
import com.ue.townsystem.logic.api.TownsystemEventHandler;
import com.ue.townsystem.logic.api.TownworldManager;
import com.ue.vault.impl.VaultHook;

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
	TownworldManager townworldManager;
	@Inject
	VaultHook vaultHook;
	@Inject
	Metrics metrics;
	@Inject
	MessageWrapper messageWrapper;
	@Inject
	CustomSkullService skullService;
	@Inject
	ServerProvider serverProvider;
	@Inject
	ShopEventHandler shopEventHandler;
	@Inject
	JobsystemEventHandler jobsystemEventHandler;
	@Inject
	EconomyPlayerEventHandler ecoPlayerEventHandler;
	@Inject
	TownsystemEventHandler townsystemEventHandler;
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
	@Named("AdminshopCommandExecutor")
	CommandExecutor adminshopCommandExecutor;
	@Inject
	@Named("RentshopCommandExecutor")
	CommandExecutor rentshopCommandExecutor;
	@Inject
	@Named("TownCommandExecutor")
	CommandExecutor townCommandExecutor;
	@Inject
	@Named("TownworldCommandExecutor")
	CommandExecutor townworldCommandExecutor;
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
	@Inject
	@Named("AdminshopTabCompleter")
	TabCompleter adminshopTabCompleter;
	@Inject
	@Named("RentshopTabCompleter")
	TabCompleter rentshopTabCompleter;
	@Inject
	@Named("TownTabCompleter")
	TabCompleter townTabCompleter;
	@Inject
	@Named("TownworldTabCompleter")
	TabCompleter townworldTabCompleter;

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
		townworldManager.despawnAllVillagers();
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
		getServer().getPluginManager().registerEvents(new UltimateEconomyEventHandler(townsystemEventHandler, serverProvider, shopEventHandler,
				jobsystemEventHandler, ecoPlayerEventHandler, messageWrapper, spawnerlist, spawner), this);
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
				Bukkit.getLogger().warning("[Ultimate_Economy] Error on enable homes feature.");
				Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
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
		getCommand("jobinfo").setTabCompleter(jobTabCompleter);
		getCommand("town").setTabCompleter(townTabCompleter);
		getCommand("townworld").setTabCompleter(townworldTabCompleter);
		getCommand("adminshop").setTabCompleter(adminshopTabCompleter);
		getCommand("shop").setTabCompleter(adminshopTabCompleter);
		getCommand("playershop").setTabCompleter(playershopTabCompleter);
		getCommand("rentshop").setTabCompleter(rentshopTabCompleter);
		getCommand("ue-config").setTabCompleter(configTabCompleter);
		getCommand("bank").setTabCompleter(ecoPlayerTabCompleter);
	}

	private void setupCommandExecutors() {
		getCommand("jobcenter").setExecutor(jobCommandExecutor);
		getCommand("jobinfo").setExecutor(jobCommandExecutor);
		getCommand("joblist").setExecutor(jobCommandExecutor);
		getCommand("town").setExecutor(townCommandExecutor);
		getCommand("townworld").setExecutor(townworldCommandExecutor);
		getCommand("adminshop").setExecutor(adminshopCommandExecutor);
		getCommand("shoplist").setExecutor(adminshopCommandExecutor);
		getCommand("shop").setExecutor(adminshopCommandExecutor);
		getCommand("playershop").setExecutor(playershopCommandExecutor);
		getCommand("rentshop").setExecutor(rentshopCommandExecutor);
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
		rentshopManager.setupRentDailyTask();
	}
}