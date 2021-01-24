package com.ue.common.utils;

import javax.inject.Named;
import javax.inject.Singleton;

import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import com.ue.bank.dataaccess.api.BankDao;
import com.ue.bank.dataaccess.impl.BankDaoImpl;
import com.ue.bank.logic.api.BankManager;
import com.ue.bank.logic.api.BankValidationHandler;
import com.ue.bank.logic.impl.BankManagerImpl;
import com.ue.bank.logic.impl.BankValidationHandlerImpl;
import com.ue.common.api.CustomSkullService;
import com.ue.common.api.InventoryGui;
import com.ue.common.impl.CustomSkullServiceImpl;
import com.ue.common.impl.InventoryGuiImpl;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.config.dataaccess.impl.ConfigDaoImpl;
import com.ue.config.logic.api.ConfigManager;
import com.ue.config.logic.impl.ConfigCommandExecutorImpl;
import com.ue.config.logic.impl.ConfigManagerImpl;
import com.ue.config.logic.impl.ConfigTabCompleterImpl;
import com.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import com.ue.economyplayer.dataaccess.impl.EconomyPlayerDaoImpl;
import com.ue.economyplayer.logic.api.EconomyPlayerEventHandler;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import com.ue.economyplayer.logic.impl.EconomyPlayerCommandExecutorImpl;
import com.ue.economyplayer.logic.impl.EconomyPlayerEventHandlerImpl;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.economyplayer.logic.impl.EconomyPlayerTabCompleterImpl;
import com.ue.economyplayer.logic.impl.EconomyPlayerValidationHandlerImpl;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.GeneralEconomyValidationHandlerImpl;
import com.ue.general.impl.PluginImpl;
import com.ue.general.impl.UltimateEconomy;
import com.ue.jobsyste.dataaccess.api.JobDao;
import com.ue.jobsyste.dataaccess.api.JobcenterDao;
import com.ue.jobsystem.dataaccess.impl.JobDaoImpl;
import com.ue.jobsystem.dataaccess.impl.JobcenterDaoImpl;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.Jobcenter;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.api.JobsystemEventHandler;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;
import com.ue.jobsystem.logic.impl.JobCommandExecutorImpl;
import com.ue.jobsystem.logic.impl.JobManagerImpl;
import com.ue.jobsystem.logic.impl.JobTabCompleterImpl;
import com.ue.jobsystem.logic.impl.JobcenterImpl;
import com.ue.jobsystem.logic.impl.JobcenterManagerImpl;
import com.ue.jobsystem.logic.impl.JobsystemEventHandlerImpl;
import com.ue.jobsystem.logic.impl.JobsystemValidationHandlerImpl;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.dataaccess.impl.ShopDaoImpl;
import com.ue.shopsystem.logic.api.Adminshop;
import com.ue.shopsystem.logic.api.AdminshopManager;
import com.ue.shopsystem.logic.api.Playershop;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.RentshopManager;
import com.ue.shopsystem.logic.api.ShopEventHandler;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.impl.AdminshopCommandExecutorImpl;
import com.ue.shopsystem.logic.impl.AdminshopImpl;
import com.ue.shopsystem.logic.impl.AdminshopManagerImpl;
import com.ue.shopsystem.logic.impl.AdminshopTabCompleterImpl;
import com.ue.shopsystem.logic.impl.PlayershopCommandExecutorImpl;
import com.ue.shopsystem.logic.impl.PlayershopImpl;
import com.ue.shopsystem.logic.impl.PlayershopManagerImpl;
import com.ue.shopsystem.logic.impl.PlayershopTabCompleterImpl;
import com.ue.shopsystem.logic.impl.RentshopCommandExecutorImpl;
import com.ue.shopsystem.logic.impl.RentshopImpl;
import com.ue.shopsystem.logic.impl.RentshopManagerImpl;
import com.ue.shopsystem.logic.impl.RentshopTabCompleterImpl;
import com.ue.shopsystem.logic.impl.ShopEventHandlerImpl;
import com.ue.shopsystem.logic.impl.ShopValidationHandlerImpl;
import com.ue.spawnersystem.dataaccess.api.SpawnerSystemDao;
import com.ue.spawnersystem.dataaccess.impl.SpawnerSystemDaoImpl;
import com.ue.spawnersystem.logic.api.SpawnerManager;
import com.ue.spawnersystem.logic.api.SpawnerSystemEventHandler;
import com.ue.spawnersystem.logic.impl.SpawnerManagerImpl;
import com.ue.spawnersystem.logic.impl.SpawnerSystemEventHandlerImpl;
import com.ue.townsystem.dataaccess.api.TownworldDao;
import com.ue.townsystem.dataaccess.impl.TownworldDaoImpl;
import com.ue.townsystem.logic.api.TownsystemEventHandler;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.TownworldManager;
import com.ue.townsystem.logic.impl.TownCommandExecutorImpl;
import com.ue.townsystem.logic.impl.TownTabCompleterImpl;
import com.ue.townsystem.logic.impl.TownsystemEventHandlerImpl;
import com.ue.townsystem.logic.impl.TownsystemValidationHandlerImpl;
import com.ue.townsystem.logic.impl.TownworldCommandExecutorImpl;
import com.ue.townsystem.logic.impl.TownworldManagerImpl;
import com.ue.townsystem.logic.impl.TownworldTabCompleterImpl;
import com.ue.vault.impl.UltimateEconomyVaultImpl;

import dagger.Module;
import dagger.Provides;
import net.milkbowl.vault.economy.Economy;

@Module
public class ProviderModule {

	@Singleton
	@Provides
	UltimateEconomy provideUltimateEconomy(Economy vaultEconomy, SpawnerManager spawnerManager, ConfigManager configManager,
			BankManager bankManager, EconomyPlayerManager ecoPlayerManager, JobManager jobManager,
			JobcenterManager jobcenterManager, AdminshopManager adminshopManager, PlayershopManager playershopManager,
			RentshopManager rentshopManager, TownworldManager townworldManager, Metrics metrics,
			Updater updater, MessageWrapper messageWrapper, CustomSkullService skullService,
			ServerProvider serverProvider, ShopEventHandler shopEventHandler,
			JobsystemEventHandler jobsystemEventHandler, EconomyPlayerEventHandler ecoPlayerEventHandler,
			TownsystemEventHandler townsystemEventHandler, SpawnerSystemEventHandler spawnerSystemEventHandler,
			@Named("ConfigCommandExecutor") CommandExecutor configCommandExecutor,
			@Named("EconomyPlayerCommandExecutor") CommandExecutor ecoPlayerCommandExecutor,
			@Named("JobCommandExecutor") CommandExecutor jobCommandExecutor,
			@Named("PlayershopCommandExecutor") CommandExecutor playershopCommandExecutor,
			@Named("AdminshopCommandExecutor") CommandExecutor adminshopCommandExecutor,
			@Named("RentshopCommandExecutor") CommandExecutor rentshopCommandExecutor,
			@Named("TownCommandExecutor") CommandExecutor townCommandExecutor,
			@Named("TownworldCommandExecutor") CommandExecutor townworldCommandExecutor,
			@Named("EconomyPlayerTabCompleter") TabCompleter ecoPlayerTabCompleter,
			@Named("ConfigTabCompleter") TabCompleter configTabCompleter,
			@Named("JobTabCompleter") TabCompleter jobTabCompleter,
			@Named("PlayershopTabCompleter") TabCompleter playershopTabCompleter,
			@Named("AdminshopTabCompleter") TabCompleter adminshopTabCompleter,
			@Named("RentshopTabCompleter") TabCompleter rentshopTabCompleter,
			@Named("TownTabCompleter") TabCompleter townTabCompleter,
			@Named("TownworldTabCompleter") TabCompleter townworldTabCompleter) {
		return new UltimateEconomy(vaultEconomy, spawnerManager, configManager, bankManager, ecoPlayerManager, jobManager,
				jobcenterManager, adminshopManager, playershopManager, rentshopManager, townworldManager,
				metrics, updater, messageWrapper, skullService, serverProvider, shopEventHandler,
				jobsystemEventHandler, ecoPlayerEventHandler, townsystemEventHandler, spawnerSystemEventHandler,
				configCommandExecutor, ecoPlayerCommandExecutor, jobCommandExecutor, playershopCommandExecutor,
				adminshopCommandExecutor, rentshopCommandExecutor, townCommandExecutor, townworldCommandExecutor,
				ecoPlayerTabCompleter, configTabCompleter, jobTabCompleter, playershopTabCompleter,
				adminshopTabCompleter, rentshopTabCompleter, townTabCompleter, townworldTabCompleter);
	}

	@Singleton
	@Provides
	Metrics provideMetrics() {
		return new Metrics(PluginImpl.getInstance, 4652);
	}

	@Singleton
	@Provides
	Updater provideUpdater() {
		return new Updater();
	}

	@Singleton
	@Provides
	ServerProvider provideBukkitService() {
		return new ServerProvider();
	}

	@Singleton
	@Provides
	Economy provideVaultEconomy(UltimateEconomyVaultImpl euVault) {
		return euVault;
	}
	
	@Singleton
	@Provides
	MessageWrapper provideMessageWrapper() {
		return new MessageWrapper();
	}
	
	@Provides
	InventoryGui provideInventoryGui(InventoryGuiImpl gui) {
		return gui;
	}
	
	@Provides
	Jobcenter provideJobcenter(JobcenterImpl jobcenter) {
		return jobcenter;
	}
	
	@Provides
	Adminshop provideAdminshop(AdminshopImpl shop) {
		return shop;
	}
	
	@Provides
	Playershop providePlayershop(PlayershopImpl shop) {
		return shop;
	}
	
	@Provides
	Rentshop provideRentshop(RentshopImpl shop) {
		return shop;
	}

	@Singleton
	@Provides
	SpawnerManager provideSpawnerManager(SpawnerManagerImpl spawnerManager) {
		return spawnerManager;
	}

	@Singleton
	@Provides
	ConfigManager provideConfigManager(ConfigManagerImpl configManager) {
		return configManager;
	}

	@Singleton
	@Provides
	BankManager providesBankManager(BankManagerImpl bankManager) {
		return bankManager;
	}

	@Singleton
	@Provides
	EconomyPlayerManager provideEcoPlayerManager(EconomyPlayerManagerImpl ecoPlayerManager) {
		return ecoPlayerManager;
	}

	@Singleton
	@Provides
	JobManager provideJobManager(JobManagerImpl jobManager) {
		return jobManager;
	}

	@Singleton
	@Provides
	JobcenterManager provideJobcenterManager(JobcenterManagerImpl jobcenterManager) {
		return jobcenterManager;
	}

	@Singleton
	@Provides
	AdminshopManager provideAdminshopManager(AdminshopManagerImpl adminshopManager) {
		return adminshopManager;
	}

	@Singleton
	@Provides
	PlayershopManager providePlayershopManager(PlayershopManagerImpl playershopManager) {
		return playershopManager;
	}

	@Singleton
	@Provides
	RentshopManager provideRentshopManager(RentshopManagerImpl rentshopManager) {
		return rentshopManager;
	}

	@Singleton
	@Provides
	TownworldManager provideTownworldManager(TownworldManagerImpl townworldManager) {
		return townworldManager;
	}

	@Singleton
	@Provides
	@Named("ConfigCommandExecutor")
	CommandExecutor provideConfigCommandExecutor(ConfigCommandExecutorImpl configcommandExecutor) {
		return configcommandExecutor;
	}

	@Singleton
	@Provides
	@Named("EconomyPlayerCommandExecutor")
	CommandExecutor provideEconomyPlayerCommandExecutor(EconomyPlayerCommandExecutorImpl ecoPlayerCommandExecutor) {
		return ecoPlayerCommandExecutor;
	}

	@Singleton
	@Provides
	@Named("JobCommandExecutor")
	CommandExecutor provideJobCommandExecutor(JobCommandExecutorImpl jobCommandExecutor) {
		return jobCommandExecutor;
	}

	@Singleton
	@Provides
	@Named("PlayershopCommandExecutor")
	CommandExecutor providePlayershopCommandExecutor(PlayershopCommandExecutorImpl playershopCommandExecutor) {
		return playershopCommandExecutor;
	}

	@Singleton
	@Provides
	@Named("AdminshopCommandExecutor")
	CommandExecutor provideAdminshopCommandExecutor(AdminshopCommandExecutorImpl adminsgopCommandExecutor) {
		return adminsgopCommandExecutor;
	}

	@Singleton
	@Provides
	@Named("RentshopCommandExecutor")
	CommandExecutor provideRentshopCommandExecutor(RentshopCommandExecutorImpl rentshopCommandExecutor) {
		return rentshopCommandExecutor;
	}

	@Singleton
	@Provides
	@Named("TownCommandExecutor")
	CommandExecutor provideTownCommandExecutor(TownCommandExecutorImpl townCommandExecutor) {
		return townCommandExecutor;
	}

	@Singleton
	@Provides
	@Named("TownworldCommandExecutor")
	CommandExecutor provideTownworldCommandExecutor(TownworldCommandExecutorImpl townworldCommandExecutor) {
		return townworldCommandExecutor;
	}

	@Singleton
	@Provides
	@Named("EconomyPlayerTabCompleter")
	TabCompleter provideEcoPlayerTabCompleter(EconomyPlayerTabCompleterImpl ecoPlayerTabCompleter) {
		return ecoPlayerTabCompleter;
	}

	@Singleton
	@Provides
	@Named("ConfigTabCompleter")
	TabCompleter provideConfigTabCompleter(ConfigTabCompleterImpl configTabCompleter) {
		return configTabCompleter;
	}

	@Singleton
	@Provides
	@Named("JobTabCompleter")
	TabCompleter provideJobTabCompleter(JobTabCompleterImpl jobTabCompleter) {
		return jobTabCompleter;
	}

	@Singleton
	@Provides
	@Named("PlayershopTabCompleter")
	TabCompleter providePlayershopTabCompleter(PlayershopTabCompleterImpl playershopTabCompleter) {
		return playershopTabCompleter;
	}

	@Singleton
	@Provides
	@Named("AdminshopTabCompleter")
	TabCompleter provideAdminshopTabCompleter(AdminshopTabCompleterImpl adminshopTabCompleter) {
		return adminshopTabCompleter;
	}

	@Singleton
	@Provides
	@Named("RentshopTabCompleter")
	TabCompleter provideRentshopTabCompleter(RentshopTabCompleterImpl rentshopTabCompleter) {
		return rentshopTabCompleter;
	}

	@Singleton
	@Provides
	@Named("TownTabCompleter")
	TabCompleter provideTownTabCompleter(TownTabCompleterImpl townTabCompleter) {
		return townTabCompleter;
	}

	@Singleton
	@Provides
	@Named("TownworldTabCompleter")
	TabCompleter provideTownworldTabCompleter(TownworldTabCompleterImpl townworldTabCompleter) {
		return townworldTabCompleter;
	}

	@Singleton
	@Provides
	ConfigDao provideConfigDao(ConfigDaoImpl configDao) {
		return configDao;
	}

	@Singleton
	@Provides
	BankDao provideBankDao(BankDaoImpl bankDao) {
		return bankDao;
	}

	@Singleton
	@Provides
	EconomyPlayerDao provideEcoPlayerDao(EconomyPlayerDaoImpl ecoPlayerDao) {
		return ecoPlayerDao;
	}

	@Singleton
	@Provides
	SpawnerSystemDao provideSpawnerSystemDao(SpawnerSystemDaoImpl spawnerSystemDao) {
		return spawnerSystemDao;
	}

	@Provides
	JobDao provideJobDao(JobDaoImpl jobDao) {
		return jobDao;
	}

	@Provides
	JobcenterDao provideJobcenterDao(JobcenterDaoImpl jobcenterDao) {
		return jobcenterDao;
	}

	@Provides
	ShopDao provideShopDao(ShopDaoImpl shopDao) {
		return shopDao;
	}

	@Provides
	TownworldDao provideTownworldDao(TownworldDaoImpl townworldDao) {
		return townworldDao;
	}

	@Singleton
	@Provides
	BankValidationHandler provideBankValidationHandler(BankValidationHandlerImpl bankValidator) {
		return bankValidator;
	}
	
	@Singleton
	@Provides
	GeneralEconomyValidationHandler provideEconomyValidationHandler(GeneralEconomyValidationHandlerImpl validator) {
		return validator;
	}

	@Singleton
	@Provides
	EconomyPlayerValidationHandler provideEcoPlayerValidationHandler(
			EconomyPlayerValidationHandlerImpl ecoPlayerValidator) {
		return ecoPlayerValidator;
	}

	@Singleton
	@Provides
	JobsystemValidationHandler provideJobsystemValidationHandler(JobsystemValidationHandlerImpl jobsystemValidator) {
		return jobsystemValidator;
	}

	@Singleton
	@Provides
	ShopValidationHandler provideShopValidationHandler(ShopValidationHandlerImpl shopValidator) {
		return shopValidator;
	}

	@Singleton
	@Provides
	TownsystemValidationHandler provideTownsystemValidationHandler(
			TownsystemValidationHandlerImpl townsystemValidator) {
		return townsystemValidator;
	}

	@Singleton
	@Provides
	SpawnerSystemEventHandler provideSpawnerEventHandler(SpawnerSystemEventHandlerImpl spawnerEventHandler) {
		return spawnerEventHandler;
	}

	@Singleton
	@Provides
	TownsystemEventHandler provideTownsystemEventHandler(TownsystemEventHandlerImpl townsystemEventHandler) {
		return townsystemEventHandler;
	}

	@Singleton
	@Provides
	EconomyPlayerEventHandler provideEcoPlayerEventHandler(EconomyPlayerEventHandlerImpl ecoPlayerEventHandler) {
		return ecoPlayerEventHandler;
	}

	@Singleton
	@Provides
	JobsystemEventHandler provideJobsystemEventHandler(JobsystemEventHandlerImpl jobsystemEventHandler) {
		return jobsystemEventHandler;
	}

	@Singleton
	@Provides
	ShopEventHandler provideShopEventHandler(ShopEventHandlerImpl shopEventHandler) {
		return shopEventHandler;
	}

	@Singleton
	@Provides
	CustomSkullService provideCustomSkullService(CustomSkullServiceImpl customSkullService) {
		return customSkullService;
	}
}
