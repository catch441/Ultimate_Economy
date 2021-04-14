package org.ue.general.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.io.File;
import java.lang.reflect.Field;

import javax.inject.Named;

import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.bank.logic.api.BankManager;
import org.ue.common.api.CustomSkullService;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.Updater;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayerEventHandler;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.jobsystem.logic.api.JobcenterManager;
import org.ue.jobsystem.logic.api.JobsystemEventHandler;
import org.ue.shopsystem.logic.api.AdminshopManager;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.RentshopManager;
import org.ue.shopsystem.logic.api.ShopEventHandler;
import org.ue.spawnersystem.logic.api.SpawnerManager;
import org.ue.spawnersystem.logic.api.SpawnerSystemEventHandler;
import org.ue.townsystem.logic.api.TownsystemEventHandler;
import org.ue.townsystem.logic.api.TownworldManager;

import net.milkbowl.vault.economy.Economy;

@ExtendWith(MockitoExtension.class)
public class UltimateEconomyTest {
	
	@Mock
	SpawnerManager spawnerManager;
	@Mock
	ConfigManager configManager;
	@Mock
	BankManager bankManager;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	JobManager jobManager;
	@Mock
	JobcenterManager jobcenterManager;
	@Mock
	AdminshopManager adminshopManager;
	@Mock
	PlayershopManager playershopManager;
	@Mock
	RentshopManager rentshopManager;
	@Mock
	TownworldManager townworldManager;
	@Mock
	Updater updater;
	@Mock
	Economy vaultEconomy;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	CustomSkullService skullService;
	@Mock
	ServerProvider serverProvider;
	@Mock
	ShopEventHandler shopEventHandler;
	@Mock
	JobsystemEventHandler jobsystemEventHandler;
	@Mock
	EconomyPlayerEventHandler ecoPlayerEventHandler;
	@Mock
	TownsystemEventHandler townsystemEventHandler;
	@Mock
	SpawnerSystemEventHandler spawnerSystemEventHandler;
	@Mock
	@Named("ConfigCommandExecutor")
	CommandExecutor configCommandExecutor;
	@Mock
	@Named("EconomyPlayerCommandExecutor")
	CommandExecutor ecoPlayerCommandExecutor;
	@Mock
	@Named("JobCommandExecutor")
	CommandExecutor jobCommandExecutor;
	@Mock
	@Named("PlayershopCommandExecutor")
	CommandExecutor playershopCommandExecutor;
	@Mock
	@Named("AdminshopCommandExecutor")
	CommandExecutor adminshopCommandExecutor;
	@Mock
	@Named("RentshopCommandExecutor")
	CommandExecutor rentshopCommandExecutor;
	@Mock
	@Named("TownCommandExecutor")
	CommandExecutor townCommandExecutor;
	@Mock
	@Named("TownworldCommandExecutor")
	CommandExecutor townworldCommandExecutor;
	@Mock
	@Named("EconomyPlayerTabCompleter")
	TabCompleter ecoPlayerTabCompleter;
	@Mock
	@Named("ConfigTabCompleter")
	TabCompleter configTabCompleter;
	@Mock
	@Named("JobTabCompleter")
	TabCompleter jobTabCompleter;
	@Mock
	@Named("PlayershopTabCompleter")
	TabCompleter playershopTabCompleter;
	@Mock
	@Named("AdminshopTabCompleter")
	TabCompleter adminshopTabCompleter;
	@Mock
	@Named("RentshopTabCompleter")
	TabCompleter rentshopTabCompleter;
	@Mock
	@Named("TownTabCompleter")
	TabCompleter townTabCompleter;
	@Mock
	@Named("TownworldTabCompleter")
	TabCompleter townworldTabCompleter;

	@Test
	public void onDisableTest() {
		UltimateEconomy ue = new UltimateEconomy(vaultEconomy, spawnerManager, configManager, bankManager,
				ecoPlayerManager, jobManager, jobcenterManager, adminshopManager, playershopManager, rentshopManager,
				townworldManager, null, updater, messageWrapper, skullService, serverProvider, shopEventHandler,
				jobsystemEventHandler, ecoPlayerEventHandler, townsystemEventHandler, spawnerSystemEventHandler,
				configCommandExecutor, ecoPlayerCommandExecutor, jobCommandExecutor, playershopCommandExecutor,
				adminshopCommandExecutor, rentshopCommandExecutor, townCommandExecutor, townworldCommandExecutor,
				ecoPlayerTabCompleter, configTabCompleter, jobTabCompleter, playershopTabCompleter,
				adminshopTabCompleter, rentshopTabCompleter, townTabCompleter, townworldTabCompleter);

		JavaPlugin plugin = mock(JavaPlugin.class);
		Server server = mock(Server.class);
		PluginManager pluginManager = mock(PluginManager.class);
		ServicesManager servicesManager = mock(ServicesManager.class);
		when(serverProvider.getServicesManager()).thenReturn(servicesManager);
		when(pluginManager.getPlugin("Vault")).thenReturn(mock(Plugin.class));
		when(server.getPluginManager()).thenReturn(pluginManager);
		when(serverProvider.getServer()).thenReturn(server);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		ue.onDisable();

		verify(servicesManager).unregister(Economy.class, vaultEconomy);
		verify(jobcenterManager).despawnAllVillagers();
		verify(townworldManager).despawnAllVillagers();
		verify(adminshopManager).despawnAllVillagers();
		verify(playershopManager).despawnAllVillagers();
		verify(rentshopManager).despawnAllVillagers();
	}

	@Test
	public void onEnableTestWithHomeEnableError() throws IllegalArgumentException, IllegalAccessException {
		UltimateEconomy ue = new UltimateEconomy(vaultEconomy, spawnerManager, configManager, bankManager,
				ecoPlayerManager, jobManager, jobcenterManager, adminshopManager, playershopManager, rentshopManager,
				townworldManager, null, updater, messageWrapper, skullService, serverProvider, shopEventHandler,
				jobsystemEventHandler, ecoPlayerEventHandler, townsystemEventHandler, spawnerSystemEventHandler,
				configCommandExecutor, ecoPlayerCommandExecutor, jobCommandExecutor, playershopCommandExecutor,
				adminshopCommandExecutor, rentshopCommandExecutor, townCommandExecutor, townworldCommandExecutor,
				ecoPlayerTabCompleter, configTabCompleter, jobTabCompleter, playershopTabCompleter,
				adminshopTabCompleter, rentshopTabCompleter, townTabCompleter, townworldTabCompleter);

		File dataFolder = mock(File.class);
		Plugin plugin = mock(Plugin.class);
		JavaPlugin javaPlugin = mock(JavaPlugin.class);

		PluginCommand jobcenter = mock(PluginCommand.class);
		PluginCommand jobinfo = mock(PluginCommand.class);
		PluginCommand joblist = mock(PluginCommand.class);
		PluginCommand town = mock(PluginCommand.class);
		PluginCommand townworld = mock(PluginCommand.class);
		PluginCommand adminshop = mock(PluginCommand.class);
		PluginCommand shoplist = mock(PluginCommand.class);
		PluginCommand shop = mock(PluginCommand.class);
		PluginCommand playershop = mock(PluginCommand.class);
		PluginCommand rentshop = mock(PluginCommand.class);
		PluginCommand pay = mock(PluginCommand.class);
		PluginCommand givemoney = mock(PluginCommand.class);
		PluginCommand money = mock(PluginCommand.class);
		PluginCommand myjobs = mock(PluginCommand.class);
		PluginCommand bank = mock(PluginCommand.class);
		PluginCommand ueConfig = mock(PluginCommand.class);
		PluginCommand removemoney = mock(PluginCommand.class);

		when(javaPlugin.getCommand("removemoney")).thenReturn(removemoney);
		when(javaPlugin.getCommand("jobcenter")).thenReturn(jobcenter);
		when(javaPlugin.getCommand("jobinfo")).thenReturn(jobinfo);
		when(javaPlugin.getCommand("joblist")).thenReturn(joblist);
		when(javaPlugin.getCommand("town")).thenReturn(town);
		when(javaPlugin.getCommand("townworld")).thenReturn(townworld);
		when(javaPlugin.getCommand("adminshop")).thenReturn(adminshop);
		when(javaPlugin.getCommand("shoplist")).thenReturn(shoplist);
		when(javaPlugin.getCommand("shop")).thenReturn(shop);
		when(javaPlugin.getCommand("playershop")).thenReturn(playershop);
		when(javaPlugin.getCommand("rentshop")).thenReturn(rentshop);
		when(javaPlugin.getCommand("pay")).thenReturn(pay);
		when(javaPlugin.getCommand("givemoney")).thenReturn(givemoney);
		when(javaPlugin.getCommand("money")).thenReturn(money);
		when(javaPlugin.getCommand("myjobs")).thenReturn(myjobs);
		when(javaPlugin.getCommand("bank")).thenReturn(bank);
		when(javaPlugin.getCommand("ue-config")).thenReturn(ueConfig);

		PluginDescriptionFile description = mock(PluginDescriptionFile.class);
		IllegalArgumentException e = mock(IllegalArgumentException.class);
		when(serverProvider.getCommandMap(any(Field.class))).thenThrow(e);
		when(e.getMessage()).thenReturn("my error");
		Server server = mock(Server.class);
		PluginManager pluginManager = mock(PluginManager.class);
		ServicesManager servicesManager = mock(ServicesManager.class);
		when(plugin.getDataFolder()).thenReturn(dataFolder);
		when(dataFolder.exists()).thenReturn(false);
		when(serverProvider.getServicesManager()).thenReturn(servicesManager);
		when(pluginManager.getPlugin("Vault")).thenReturn(mock(Plugin.class));
		when(server.getPluginManager()).thenReturn(pluginManager);
		when(serverProvider.getServer()).thenReturn(server);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(serverProvider.getJavaPluginInstance()).thenReturn(javaPlugin);
		when(javaPlugin.getDescription()).thenReturn(description);
		when(description.getVersion()).thenReturn("1.2.6");
		when(configManager.isHomeSystem()).thenReturn(true);
		ue.onEnable();

		verify(dataFolder).mkdirs();

		verify(configManager).setupConfig();
		verify(skullService).setup();
		verify(rentshopManager).setupRentDailyTask();
		verify(messageWrapper).loadLanguage(configManager.getLocale());
		verify(bankManager).loadBankAccounts();
		verify(jobManager).loadAllJobs();
		
		/*
		 * TODO: Only commented out to fix a cluser of issues. When spawning the villagers at
		 * startup without any player, then no changes to these villagers are visible
		 * ingame (rename, move ...). In spigot it works, but in paper it doesn't. This
		 * is just a quickfix and not a solution.
		 * [UE-139,UE-140]
		 */
		
		//verify(jobcenterManager).loadAllJobcenters();
		verify(ecoPlayerManager).loadAllEconomyPlayers();
		//verify(adminshopManager).loadAllAdminShops();
		//verify(playershopManager).loadAllPlayerShops();
		//verify(rentshopManager).loadAllRentShops();
		verify(townworldManager).loadAllTownWorlds();
		verify(spawnerManager).loadAllSpawners();
		verify(updater).checkForUpdate("1.2.6");
		
		verify(e).getMessage();

		verify(jobcenter).setExecutor(jobCommandExecutor);
		verify(jobinfo).setExecutor(jobCommandExecutor);
		verify(joblist).setExecutor(jobCommandExecutor);
		verify(town).setExecutor(townCommandExecutor);
		verify(townworld).setExecutor(townworldCommandExecutor);
		verify(adminshop).setExecutor(adminshopCommandExecutor);
		verify(shoplist).setExecutor(adminshopCommandExecutor);
		verify(shop).setExecutor(adminshopCommandExecutor);
		verify(playershop).setExecutor(playershopCommandExecutor);
		verify(rentshop).setExecutor(rentshopCommandExecutor);
		verify(pay).setExecutor(ecoPlayerCommandExecutor);
		verify(givemoney).setExecutor(ecoPlayerCommandExecutor);
		verify(removemoney).setExecutor(ecoPlayerCommandExecutor);
		verify(money).setExecutor(ecoPlayerCommandExecutor);
		verify(myjobs).setExecutor(ecoPlayerCommandExecutor);
		verify(bank).setExecutor(ecoPlayerCommandExecutor);
		verify(ueConfig).setExecutor(configCommandExecutor);

		verify(jobcenter).setTabCompleter(jobTabCompleter);
		verify(jobinfo).setTabCompleter(jobTabCompleter);
		verify(town).setTabCompleter(townTabCompleter);
		verify(townworld).setTabCompleter(townworldTabCompleter);
		verify(adminshop).setTabCompleter(adminshopTabCompleter);
		verify(shop).setTabCompleter(adminshopTabCompleter);
		verify(playershop).setTabCompleter(playershopTabCompleter);
		verify(rentshop).setTabCompleter(rentshopTabCompleter);
		verify(ueConfig).setTabCompleter(configTabCompleter);
		verify(bank).setTabCompleter(ecoPlayerTabCompleter);

		verify(pluginManager).registerEvents(any(UltimateEconomyEventHandlerImpl.class), eq(javaPlugin));
	}
	
	@Test
	public void onEnableTest() {
		UltimateEconomy ue = new UltimateEconomy(vaultEconomy, spawnerManager, configManager, bankManager,
				ecoPlayerManager, jobManager, jobcenterManager, adminshopManager, playershopManager, rentshopManager,
				townworldManager, null, updater, messageWrapper, skullService, serverProvider, shopEventHandler,
				jobsystemEventHandler, ecoPlayerEventHandler, townsystemEventHandler, spawnerSystemEventHandler,
				configCommandExecutor, ecoPlayerCommandExecutor, jobCommandExecutor, playershopCommandExecutor,
				adminshopCommandExecutor, rentshopCommandExecutor, townCommandExecutor, townworldCommandExecutor,
				ecoPlayerTabCompleter, configTabCompleter, jobTabCompleter, playershopTabCompleter,
				adminshopTabCompleter, rentshopTabCompleter, townTabCompleter, townworldTabCompleter);

		File dataFolder = mock(File.class);
		Plugin plugin = mock(Plugin.class);
		JavaPlugin javaPlugin = mock(JavaPlugin.class);

		PluginCommand jobcenter = mock(PluginCommand.class);
		PluginCommand jobinfo = mock(PluginCommand.class);
		PluginCommand joblist = mock(PluginCommand.class);
		PluginCommand town = mock(PluginCommand.class);
		PluginCommand townworld = mock(PluginCommand.class);
		PluginCommand adminshop = mock(PluginCommand.class);
		PluginCommand shoplist = mock(PluginCommand.class);
		PluginCommand shop = mock(PluginCommand.class);
		PluginCommand playershop = mock(PluginCommand.class);
		PluginCommand rentshop = mock(PluginCommand.class);
		PluginCommand pay = mock(PluginCommand.class);
		PluginCommand givemoney = mock(PluginCommand.class);
		PluginCommand money = mock(PluginCommand.class);
		PluginCommand myjobs = mock(PluginCommand.class);
		PluginCommand bank = mock(PluginCommand.class);
		PluginCommand ueConfig = mock(PluginCommand.class);
		PluginCommand removemoney = mock(PluginCommand.class);

		when(javaPlugin.getCommand("removemoney")).thenReturn(removemoney);
		when(javaPlugin.getCommand("jobcenter")).thenReturn(jobcenter);
		when(javaPlugin.getCommand("jobinfo")).thenReturn(jobinfo);
		when(javaPlugin.getCommand("joblist")).thenReturn(joblist);
		when(javaPlugin.getCommand("town")).thenReturn(town);
		when(javaPlugin.getCommand("townworld")).thenReturn(townworld);
		when(javaPlugin.getCommand("adminshop")).thenReturn(adminshop);
		when(javaPlugin.getCommand("shoplist")).thenReturn(shoplist);
		when(javaPlugin.getCommand("shop")).thenReturn(shop);
		when(javaPlugin.getCommand("playershop")).thenReturn(playershop);
		when(javaPlugin.getCommand("rentshop")).thenReturn(rentshop);
		when(javaPlugin.getCommand("pay")).thenReturn(pay);
		when(javaPlugin.getCommand("givemoney")).thenReturn(givemoney);
		when(javaPlugin.getCommand("money")).thenReturn(money);
		when(javaPlugin.getCommand("myjobs")).thenReturn(myjobs);
		when(javaPlugin.getCommand("bank")).thenReturn(bank);
		when(javaPlugin.getCommand("ue-config")).thenReturn(ueConfig);

		PluginDescriptionFile description = mock(PluginDescriptionFile.class);
		CommandMap map = mock(CommandMap.class);
		UltimateEconomyCommand sethome = mock(UltimateEconomyCommand.class);
		UltimateEconomyCommand delhome = mock(UltimateEconomyCommand.class);
		UltimateEconomyCommand home = mock(UltimateEconomyCommand.class);
		
		assertDoesNotThrow(() -> when(serverProvider.getCommandMap(any(Field.class))).thenReturn(map));
		when(serverProvider.createUltimateEconomyCommand("sethome")).thenReturn(sethome);
		when(serverProvider.createUltimateEconomyCommand("delhome")).thenReturn(delhome);
		when(serverProvider.createUltimateEconomyCommand("home")).thenReturn(home);
		
		Server server = mock(Server.class);
		PluginManager pluginManager = mock(PluginManager.class);
		ServicesManager servicesManager = mock(ServicesManager.class);
		when(plugin.getDataFolder()).thenReturn(dataFolder);
		when(dataFolder.exists()).thenReturn(false);
		when(serverProvider.getServicesManager()).thenReturn(servicesManager);
		when(pluginManager.getPlugin("Vault")).thenReturn(mock(Plugin.class));
		when(server.getPluginManager()).thenReturn(pluginManager);
		when(serverProvider.getServer()).thenReturn(server);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(serverProvider.getJavaPluginInstance()).thenReturn(javaPlugin);
		when(javaPlugin.getDescription()).thenReturn(description);
		when(description.getVersion()).thenReturn("1.2.6");
		when(configManager.isHomeSystem()).thenReturn(true);
		ue.onEnable();

		verify(dataFolder).mkdirs();

		verify(configManager).setupConfig();
		verify(skullService).setup();
		verify(rentshopManager).setupRentDailyTask();
		verify(messageWrapper).loadLanguage(configManager.getLocale());
		verify(bankManager).loadBankAccounts();
		verify(jobManager).loadAllJobs();
		
		/*
		 * TODO: Only commented out to fix a cluser of issues. When spawning the villagers at
		 * startup without any player, then no changes to these villagers are visible
		 * ingame (rename, move ...). In spigot it works, but in paper it doesn't. This
		 * is just a quickfix and not a solution.
		 * [UE-139,UE-140]
		 */
		
		//verify(jobcenterManager).loadAllJobcenters();
		verify(ecoPlayerManager).loadAllEconomyPlayers();
		//verify(adminshopManager).loadAllAdminShops();
		//verify(playershopManager).loadAllPlayerShops();
		//verify(rentshopManager).loadAllRentShops();
		verify(townworldManager).loadAllTownWorlds();
		verify(spawnerManager).loadAllSpawners();
		verify(updater).checkForUpdate("1.2.6");
		
		verify(sethome).setDescription("Sets a homepoint.");
		verify(sethome).setPermission("ultimate_economy.home");
		verify(sethome).setLabel("sethome");
		verify(sethome).setPermissionMessage("You don't have the permission.");
		verify(sethome).setUsage("/<command> [home]");
		verify(sethome).setExecutor(ecoPlayerCommandExecutor);
		verify(map).register("ultimate_economy", sethome);
		
		verify(delhome).setDescription("Remove a homepoint.");
		verify(delhome).setPermission("ultimate_economy.home");
		verify(delhome).setLabel("delhome");
		verify(delhome).setPermissionMessage("You don't have the permission.");
		verify(delhome).setUsage("/<command> [home]");
		verify(delhome).setExecutor(ecoPlayerCommandExecutor);
		verify(delhome).setTabCompleter(ecoPlayerTabCompleter);
		verify(map).register("ultimate_economy", delhome);
		
		verify(home).setDescription("Teleports you to a homepoint.");
		verify(home).setPermission("ultimate_economy.home");
		verify(home).setLabel("home");
		verify(home).setPermissionMessage("You don't have the permission.");
		verify(home).setExecutor(ecoPlayerCommandExecutor);
		verify(home).setTabCompleter(ecoPlayerTabCompleter);
		verify(map).register("ultimate_economy", home);

		verify(jobcenter).setExecutor(jobCommandExecutor);
		verify(jobinfo).setExecutor(jobCommandExecutor);
		verify(joblist).setExecutor(jobCommandExecutor);
		verify(town).setExecutor(townCommandExecutor);
		verify(townworld).setExecutor(townworldCommandExecutor);
		verify(adminshop).setExecutor(adminshopCommandExecutor);
		verify(shoplist).setExecutor(adminshopCommandExecutor);
		verify(shop).setExecutor(adminshopCommandExecutor);
		verify(playershop).setExecutor(playershopCommandExecutor);
		verify(rentshop).setExecutor(rentshopCommandExecutor);
		verify(pay).setExecutor(ecoPlayerCommandExecutor);
		verify(givemoney).setExecutor(ecoPlayerCommandExecutor);
		verify(removemoney).setExecutor(ecoPlayerCommandExecutor);
		verify(money).setExecutor(ecoPlayerCommandExecutor);
		verify(myjobs).setExecutor(ecoPlayerCommandExecutor);
		verify(bank).setExecutor(ecoPlayerCommandExecutor);
		verify(ueConfig).setExecutor(configCommandExecutor);

		verify(jobcenter).setTabCompleter(jobTabCompleter);
		verify(jobinfo).setTabCompleter(jobTabCompleter);
		verify(town).setTabCompleter(townTabCompleter);
		verify(townworld).setTabCompleter(townworldTabCompleter);
		verify(adminshop).setTabCompleter(adminshopTabCompleter);
		verify(shop).setTabCompleter(adminshopTabCompleter);
		verify(playershop).setTabCompleter(playershopTabCompleter);
		verify(rentshop).setTabCompleter(rentshopTabCompleter);
		verify(ueConfig).setTabCompleter(configTabCompleter);
		verify(bank).setTabCompleter(ecoPlayerTabCompleter);

		verify(pluginManager).registerEvents(any(UltimateEconomyEventHandlerImpl.class), eq(javaPlugin));
	}
}
