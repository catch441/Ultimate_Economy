package org.ue.general.impl;

import java.lang.reflect.Field;

import org.bukkit.command.CommandMap;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.common.utils.UltimateEconomyProvider;

import net.milkbowl.vault.economy.Economy;

/**
 * @author Lukas Heubach (catch441)
 */
public class UltimateEconomy {

	private static final Logger log = LoggerFactory.getLogger(UltimateEconomy.class);
	private final UltimateEconomyProvider provider;
	private JavaPlugin plugin;

	/**
	 * Inject constructor.
	 * 
	 * @param provider
	 */
	public UltimateEconomy(UltimateEconomyProvider provider) {
		this.provider = provider;
	}

	/**
	 * Enables the entire plugin.
	 */
	public void onEnable() {
		plugin = provider.getServerProvider().getJavaPluginInstance();
		loadPlugin();
		setupVault();
	}

	/**
	 * Disables the entire plugin.
	 */
	public void onDisable() {
		plugin = provider.getServerProvider().getJavaPluginInstance();
		disablePlugin();
	}

	private void disablePlugin() {
		provider.getJobcenterManager().despawnAllVillagers();
		provider.getTownworldManager().despawnAllVillagers();
		provider.getAdminshopManager().despawnAllVillagers();
		provider.getPlayershopManager().despawnAllVillagers();
		provider.getRentshopManager().despawnAllVillagers();
		if (provider.getServerProvider().getServer().getPluginManager().getPlugin("Vault") != null) {
			provider.getServerProvider().getServicesManager().unregister(Economy.class, provider.getVaultEconomy());
		}
	}

	private void setupVault() {
		if (provider.getServerProvider().getServer().getPluginManager().getPlugin("Vault") != null) {
			provider.getServerProvider().getServicesManager().register(Economy.class, provider.getVaultEconomy(),
					plugin, ServicePriority.Normal);
		}
	}

	private void loadCommands() {
		setupCommandExecutors();
		setupTabCompleters();
		if (provider.getConfigManager().isHomeSystem()) {
			try {
				Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
				commandMapField.setAccessible(true);
				CommandMap map = provider.getServerProvider().getCommandMap(commandMapField);
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
		UltimateEconomyCommand setHome = provider.getServerProvider().createUltimateEconomyCommand("sethome");
		setHome.setDescription("Sets a homepoint.");
		setHome.setPermission("ultimate_economy.home");
		setHome.setLabel("sethome");
		setHome.setPermissionMessage("You don't have the permission.");
		setHome.setUsage("/<command> [home]");
		map.register("ultimate_economy", setHome);
		setHome.setExecutor(provider.getEconomyPlayerCommandExecutor());
	}

	private void setupDeleteHomeCommand(CommandMap map) {
		UltimateEconomyCommand delHome = provider.getServerProvider().createUltimateEconomyCommand("delhome");
		delHome.setDescription("Remove a homepoint.");
		delHome.setPermission("ultimate_economy.home");
		delHome.setLabel("delhome");
		delHome.setPermissionMessage("You don't have the permission.");
		delHome.setUsage("/<command> [home]");
		map.register("ultimate_economy", delHome);
		delHome.setExecutor(provider.getEconomyPlayerCommandExecutor());
		delHome.setTabCompleter(provider.getEconomyPlayerTabCompleter());
	}

	private void setupHomeCommand(CommandMap map) {
		UltimateEconomyCommand home = provider.getServerProvider().createUltimateEconomyCommand("home");
		home.setDescription("Teleports you to a homepoint.");
		home.setPermission("ultimate_economy.home");
		home.setLabel("home");
		home.setPermissionMessage("You don't have the permission.");
		map.register("ultimate_economy", home);
		home.setExecutor(provider.getEconomyPlayerCommandExecutor());
		home.setTabCompleter(provider.getEconomyPlayerTabCompleter());
	}

	private void setupTabCompleters() {
		plugin.getCommand("jobcenter").setTabCompleter(provider.getJobTabCompleter());
		plugin.getCommand("jobinfo").setTabCompleter(provider.getJobTabCompleter());
		plugin.getCommand("town").setTabCompleter(provider.getTownTabCompleter());
		plugin.getCommand("townworld").setTabCompleter(provider.getTownworldTabCompleter());
		plugin.getCommand("adminshop").setTabCompleter(provider.getAdminshopTabCompleter());
		plugin.getCommand("shop").setTabCompleter(provider.getAdminshopTabCompleter());
		plugin.getCommand("playershop").setTabCompleter(provider.getPlayershopTabCompleter());
		plugin.getCommand("rentshop").setTabCompleter(provider.getRentshopTabCompleter());
		plugin.getCommand("ue-config").setTabCompleter(provider.getConfigTabCompleter());
		plugin.getCommand("bank").setTabCompleter(provider.getEconomyPlayerTabCompleter());
	}

	private void setupCommandExecutors() {
		plugin.getCommand("jobcenter").setExecutor(provider.getJobCommandExecutor());
		plugin.getCommand("jobinfo").setExecutor(provider.getJobCommandExecutor());
		plugin.getCommand("joblist").setExecutor(provider.getJobCommandExecutor());
		plugin.getCommand("town").setExecutor(provider.getTownCommandExecutor());
		plugin.getCommand("townworld").setExecutor(provider.getTownworldCommandExecutor());
		plugin.getCommand("adminshop").setExecutor(provider.getAdminshopCommandExecutor());
		plugin.getCommand("shoplist").setExecutor(provider.getAdminshopCommandExecutor());
		plugin.getCommand("shop").setExecutor(provider.getAdminshopCommandExecutor());
		plugin.getCommand("playershop").setExecutor(provider.getPlayershopCommandExecutor());
		plugin.getCommand("rentshop").setExecutor(provider.getRentshopCommandExecutor());
		plugin.getCommand("pay").setExecutor(provider.getEconomyPlayerCommandExecutor());
		plugin.getCommand("givemoney").setExecutor(provider.getEconomyPlayerCommandExecutor());
		plugin.getCommand("removemoney").setExecutor(provider.getEconomyPlayerCommandExecutor());
		plugin.getCommand("money").setExecutor(provider.getEconomyPlayerCommandExecutor());
		plugin.getCommand("myjobs").setExecutor(provider.getEconomyPlayerCommandExecutor());
		plugin.getCommand("bank").setExecutor(provider.getEconomyPlayerCommandExecutor());
		plugin.getCommand("ue-config").setExecutor(provider.getConfigCommandExecutor());
	}

	private void loadPlugin() {
		if (!provider.getServerProvider().getPluginInstance().getDataFolder().exists()) {
			provider.getServerProvider().getPluginInstance().getDataFolder().mkdirs();
		}
		provider.getConfigManager().setupConfig();
		provider.getCustomSkullService().setup();
		provider.getRentshopManager().setupRentDailyTask();
		provider.getMessageWrapper().loadLanguage(provider.getConfigManager().getLocale());
		provider.getBankManager().loadBankAccounts();
		provider.getJobManager().loadAllJobs();

		/*
		 * TODO: Only commented out to fix a cluser of issues. When spawning the
		 * villagers at startup without any player, then no changes to these villagers
		 * are visible ingame (rename, move ...). In spigot it works, but in paper it
		 * doesn't. This is just a quickfix and not a solution. [UE-139,UE-140]
		 */
		// jobcenterManager.loadAllJobcenters();
		provider.getEconomyPlayerManager().loadAllEconomyPlayers();
		// adminshopManager.loadAllAdminShops();
		// playershopManager.loadAllPlayerShops();
		// rentshopManager.loadAllRentShops();
		provider.getTownworldManager().loadAllTownWorlds();
		loadCommands();
		provider.getSpawnerManager().loadAllSpawners();
		// check for new versions
		provider.getUpdater().checkForUpdate(plugin.getDescription().getVersion());
		// setup eventhandler
		provider.getServerProvider().getServer().getPluginManager()
				.registerEvents(provider.getUltimateEconomyEventHandler(), plugin);
	}
}