package com.ue.general.impl;

import java.lang.reflect.Field;

import javax.inject.Inject;

import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ue.bank.logic.api.BankManager;
import com.ue.common.api.CustomSkullService;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.Updater;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayerEventHandler;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.api.JobsystemEventHandler;
import com.ue.shopsystem.logic.api.AdminshopManager;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.RentshopManager;
import com.ue.shopsystem.logic.api.ShopEventHandler;
import com.ue.spawnersystem.logic.api.SpawnerManager;
import com.ue.spawnersystem.logic.api.SpawnerSystemEventHandler;
import com.ue.townsystem.logic.api.TownsystemEventHandler;
import com.ue.townsystem.logic.api.TownworldManager;

import net.milkbowl.vault.economy.Economy;

/**
 * @author Lukas Heubach (catch441)
 */
public class UltimateEconomy {

	private static final Logger log = LoggerFactory.getLogger(UltimateEconomy.class);
	private final Economy vaultEconomy;
	private final SpawnerManager spawnerManager;
	private final ConfigManager configManager;
	private final BankManager bankManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private final JobManager jobManager;
	private final JobcenterManager jobcenterManager;
	private final AdminshopManager adminshopManager;
	private final PlayershopManager playershopManager;
	private final RentshopManager rentshopManager;
	private final TownworldManager townworldManager;
	@SuppressWarnings("unused")
	private final Metrics metrics;
	private final Updater updater;
	private final MessageWrapper messageWrapper;
	private final CustomSkullService skullService;
	private final ServerProvider serverProvider;
	private final ShopEventHandler shopEventHandler;
	private final JobsystemEventHandler jobsystemEventHandler;
	private final EconomyPlayerEventHandler ecoPlayerEventHandler;
	private final TownsystemEventHandler townsystemEventHandler;
	private final SpawnerSystemEventHandler spawnerSystemEventHandler;
	private final CommandExecutor configCommandExecutor;
	private final CommandExecutor ecoPlayerCommandExecutor;
	private final CommandExecutor jobCommandExecutor;
	private final CommandExecutor playershopCommandExecutor;
	private final CommandExecutor adminshopCommandExecutor;
	private final CommandExecutor rentshopCommandExecutor;
	private final CommandExecutor townCommandExecutor;
	private final CommandExecutor townworldCommandExecutor;
	private final TabCompleter ecoPlayerTabCompleter;
	private final TabCompleter configTabCompleter;
	private final TabCompleter jobTabCompleter;
	private final TabCompleter playershopTabCompleter;
	private final TabCompleter adminshopTabCompleter;
	private final TabCompleter rentshopTabCompleter;
	private final TabCompleter townTabCompleter;
	private final TabCompleter townworldTabCompleter;
	private JavaPlugin plugin;
	
	@Inject
	public UltimateEconomy(Economy vaultEconomy, SpawnerManager spawnerManager, ConfigManager configManager,
			BankManager bankManager, EconomyPlayerManager ecoPlayerManager, JobManager jobManager,
			JobcenterManager jobcenterManager, AdminshopManager adminshopManager, PlayershopManager playershopManager,
			RentshopManager rentshopManager, TownworldManager townworldManager, Metrics metrics, Updater updater,
			MessageWrapper messageWrapper, CustomSkullService skullService,
			ServerProvider serverProvider, ShopEventHandler shopEventHandler,
			JobsystemEventHandler jobsystemEventHandler, EconomyPlayerEventHandler ecoPlayerEventHandler,
			TownsystemEventHandler townsystemEventHandler, SpawnerSystemEventHandler spawnerSystemEventHandler,
			CommandExecutor configCommandExecutor, CommandExecutor ecoPlayerCommandExecutor,
			CommandExecutor jobCommandExecutor, CommandExecutor playershopCommandExecutor,
			CommandExecutor adminshopCommandExecutor, CommandExecutor rentshopCommandExecutor,
			CommandExecutor townCommandExecutor, CommandExecutor townworldCommandExecutor,
			TabCompleter ecoPlayerTabCompleter, TabCompleter configTabCompleter, TabCompleter jobTabCompleter,
			TabCompleter playershopTabCompleter, TabCompleter adminshopTabCompleter, TabCompleter rentshopTabCompleter,
			TabCompleter townTabCompleter, TabCompleter townworldTabCompleter) {
		this.spawnerManager = spawnerManager;
		this.configManager = configManager;
		this.bankManager = bankManager;
		this.ecoPlayerManager = ecoPlayerManager;
		this.jobManager = jobManager;
		this.jobcenterManager = jobcenterManager;
		this.adminshopManager = adminshopManager;
		this.playershopManager = playershopManager;
		this.rentshopManager = rentshopManager;
		this.townworldManager = townworldManager;
		this.metrics = metrics;
		this.updater = updater;
		this.messageWrapper = messageWrapper;
		this.skullService = skullService;
		this.serverProvider = serverProvider;
		this.shopEventHandler = shopEventHandler;
		this.jobsystemEventHandler = jobsystemEventHandler;
		this.ecoPlayerEventHandler = ecoPlayerEventHandler;
		this.townsystemEventHandler = townsystemEventHandler;
		this.spawnerSystemEventHandler = spawnerSystemEventHandler;
		this.configCommandExecutor = configCommandExecutor;
		this.ecoPlayerCommandExecutor = ecoPlayerCommandExecutor;
		this.jobCommandExecutor = jobCommandExecutor;
		this.playershopCommandExecutor = playershopCommandExecutor;
		this.adminshopCommandExecutor = adminshopCommandExecutor;
		this.rentshopCommandExecutor = rentshopCommandExecutor;
		this.townCommandExecutor = townCommandExecutor;
		this.townworldCommandExecutor = townworldCommandExecutor;
		this.ecoPlayerTabCompleter = ecoPlayerTabCompleter;
		this.configTabCompleter = configTabCompleter;
		this.jobTabCompleter = jobTabCompleter;
		this.playershopTabCompleter = playershopTabCompleter;
		this.adminshopTabCompleter = adminshopTabCompleter;
		this.rentshopTabCompleter = rentshopTabCompleter;
		this.townTabCompleter = townTabCompleter;
		this.townworldTabCompleter = townworldTabCompleter;
		this.vaultEconomy = vaultEconomy;
	}

	/**
	 * Enables the entire plugin.
	 */
	public void onEnable() {
		plugin = serverProvider.getJavaPluginInstance();
		loadPlugin();
		setupVault();
	}

	/**
	 * Disables the entire plugin.
	 */
	public void onDisable() {
		plugin = serverProvider.getJavaPluginInstance();
		disablePlugin();
	}

	private void disablePlugin() {
		jobcenterManager.despawnAllVillagers();
		townworldManager.despawnAllVillagers();
		adminshopManager.despawnAllVillagers();
		playershopManager.despawnAllVillagers();
		rentshopManager.despawnAllVillagers();
		if (serverProvider.getServer().getPluginManager().getPlugin("Vault") != null) {
			serverProvider.getServicesManager().unregister(Economy.class, vaultEconomy);
		}
	}

	private void setupVault() {
		if (serverProvider.getServer().getPluginManager().getPlugin("Vault") != null) {
			serverProvider.getServicesManager().register(Economy.class, vaultEconomy, plugin, ServicePriority.Normal);
		}
	}

	private void loadCommands() {
		setupCommandExecutors();
		setupTabCompleters();
		if (configManager.isHomeSystem()) {
			try {
				Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
				commandMapField.setAccessible(true);
				CommandMap map = serverProvider.getCommandMap(commandMapField);
				setupHomeCommand(map);
				setupSetHomeCommand(map);
				setupDeleteHomeCommand(map);
			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
				log.warn("[Ultimate_Economy] Error on enable homes feature.");
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}

	private void setupSetHomeCommand(CommandMap map) {
		UltimateEconomyCommand setHome = serverProvider.createUltimateEconomyCommand("sethome");
		setHome.setDescription("Sets a homepoint.");
		setHome.setPermission("ultimate_economy.home");
		setHome.setLabel("sethome");
		setHome.setPermissionMessage("You don't have the permission.");
		setHome.setUsage("/<command> [home]");
		map.register("ultimate_economy", setHome);
		setHome.setExecutor(ecoPlayerCommandExecutor);
	}

	private void setupDeleteHomeCommand(CommandMap map) {
		UltimateEconomyCommand delHome = serverProvider.createUltimateEconomyCommand("delhome");
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
		UltimateEconomyCommand home = serverProvider.createUltimateEconomyCommand("home");
		home.setDescription("Teleports you to a homepoint.");
		home.setPermission("ultimate_economy.home");
		home.setLabel("home");
		home.setPermissionMessage("You don't have the permission.");
		map.register("ultimate_economy", home);
		home.setExecutor(ecoPlayerCommandExecutor);
		home.setTabCompleter(ecoPlayerTabCompleter);
	}

	private void setupTabCompleters() {
		plugin.getCommand("jobcenter").setTabCompleter(jobTabCompleter);
		plugin.getCommand("jobinfo").setTabCompleter(jobTabCompleter);
		plugin.getCommand("town").setTabCompleter(townTabCompleter);
		plugin.getCommand("townworld").setTabCompleter(townworldTabCompleter);
		plugin.getCommand("adminshop").setTabCompleter(adminshopTabCompleter);
		plugin.getCommand("shop").setTabCompleter(adminshopTabCompleter);
		plugin.getCommand("playershop").setTabCompleter(playershopTabCompleter);
		plugin.getCommand("rentshop").setTabCompleter(rentshopTabCompleter);
		plugin.getCommand("ue-config").setTabCompleter(configTabCompleter);
		plugin.getCommand("bank").setTabCompleter(ecoPlayerTabCompleter);
	}

	private void setupCommandExecutors() {
		plugin.getCommand("jobcenter").setExecutor(jobCommandExecutor);
		plugin.getCommand("jobinfo").setExecutor(jobCommandExecutor);
		plugin.getCommand("joblist").setExecutor(jobCommandExecutor);
		plugin.getCommand("town").setExecutor(townCommandExecutor);
		plugin.getCommand("townworld").setExecutor(townworldCommandExecutor);
		plugin.getCommand("adminshop").setExecutor(adminshopCommandExecutor);
		plugin.getCommand("shoplist").setExecutor(adminshopCommandExecutor);
		plugin.getCommand("shop").setExecutor(adminshopCommandExecutor);
		plugin.getCommand("playershop").setExecutor(playershopCommandExecutor);
		plugin.getCommand("rentshop").setExecutor(rentshopCommandExecutor);
		plugin.getCommand("pay").setExecutor(ecoPlayerCommandExecutor);
		plugin.getCommand("givemoney").setExecutor(ecoPlayerCommandExecutor);
		plugin.getCommand("removemoney").setExecutor(ecoPlayerCommandExecutor);
		plugin.getCommand("money").setExecutor(ecoPlayerCommandExecutor);
		plugin.getCommand("myjobs").setExecutor(ecoPlayerCommandExecutor);
		plugin.getCommand("bank").setExecutor(ecoPlayerCommandExecutor);
		plugin.getCommand("ue-config").setExecutor(configCommandExecutor);
	}

	private void loadPlugin() {
		if (!serverProvider.getPluginInstance().getDataFolder().exists()) {
			serverProvider.getPluginInstance().getDataFolder().mkdirs();
		}
		configManager.setupConfig();
		skullService.setup();
		rentshopManager.setupRentDailyTask();
		messageWrapper.loadLanguage(configManager.getLocale());
		bankManager.loadBankAccounts();
		jobManager.loadAllJobs();

		/*
		 * TODO: Only commented out to fix a cluser of issues. When spawning the
		 * villagers at startup without any player, then no changes to these villagers
		 * are visible ingame (rename, move ...). In spigot it works, but in paper it
		 * doesn't. This is just a quickfix and not a solution. [UE-139,UE-140]
		 */
		// jobcenterManager.loadAllJobcenters();
		ecoPlayerManager.loadAllEconomyPlayers();
		// adminshopManager.loadAllAdminShops();
		// playershopManager.loadAllPlayerShops();
		// rentshopManager.loadAllRentShops();
		townworldManager.loadAllTownWorlds();
		loadCommands();
		spawnerManager.loadAllSpawners();
		// check for new versions
		updater.checkForUpdate(plugin.getDescription().getVersion());
		// setup eventhandler

		serverProvider.getServer().getPluginManager()
				.registerEvents(new UltimateEconomyEventHandlerImpl(jobcenterManager, rentshopManager,
						playershopManager, adminshopManager, updater, spawnerSystemEventHandler, townsystemEventHandler,
						shopEventHandler, jobsystemEventHandler, ecoPlayerEventHandler), plugin);
	}
}