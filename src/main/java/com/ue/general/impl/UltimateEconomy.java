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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.milkbowl.vault.economy.Economy;

/**
 * @author Lukas Heubach (catch441)
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UltimateEconomy {

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

	public static void main(String[] args) {
		// HibernateUtil.getSessionFactory().openSession();

		/*
		 * Enumeration<Driver> drivers = DriverManager.getDrivers();
		 * while(drivers.hasMoreElements()) { Driver driver = drivers.nextElement();
		 * System.out.println(driver.getClass()); }
		 */

	}

	/**
	 * Enables the entire plugin.
	 */
	public void onEnable() {
		plugin = serverProvider.getJavaPluginInstance();
		loadPlugin();
		setupVault();

		/*
		 * String url = "jdbc:h2:" +
		 * serverProvider.getPluginInstance().getDataFolder().getAbsolutePath() +
		 * "/data";
		 * 
		 * try { Class.forName("org.h2.Driver"); } catch (ClassNotFoundException e1) {
		 * // TODO Auto-generated catch block e1.printStackTrace(); } try { Connection
		 * conn = DriverManager.getConnection(url); Statement s =
		 * conn.createStatement(); s.
		 * execute("CREATE TABLE cars(id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), price INT)"
		 * ); s.executeUpdate("INSERT INTO cars(name, price) VALUES('Audi', 52642)");
		 * System.out.println("print123"); } catch (SQLException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

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
				.registerEvents(new UltimateEconomyEventHandlerImpl(ecoPlayerEventHandler, jobsystemEventHandler,
						shopEventHandler, townsystemEventHandler, spawnerSystemEventHandler, updater, adminshopManager,
						playershopManager, rentshopManager, jobcenterManager), plugin);
	}
}