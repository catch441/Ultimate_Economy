package org.ue.common.utils;

import javax.inject.Named;
import javax.inject.Singleton;

import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.ue.bank.dataaccess.api.BankDao;
import org.ue.bank.dataaccess.impl.BankDaoImpl;
import org.ue.bank.logic.api.BankManager;
import org.ue.bank.logic.api.BankValidationHandler;
import org.ue.bank.logic.impl.BankManagerImpl;
import org.ue.bank.logic.impl.BankValidationHandlerImpl;
import org.ue.common.api.CustomSkullService;
import org.ue.common.impl.CustomSkullServiceImpl;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.config.dataaccess.impl.ConfigDaoImpl;
import org.ue.config.logic.api.ConfigManager;
import org.ue.config.logic.impl.ConfigCommandExecutorImpl;
import org.ue.config.logic.impl.ConfigManagerImpl;
import org.ue.config.logic.impl.ConfigTabCompleterImpl;
import org.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import org.ue.economyplayer.dataaccess.impl.EconomyPlayerDaoImpl;
import org.ue.economyplayer.logic.api.EconomyPlayerEventHandler;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import org.ue.economyplayer.logic.impl.EconomyPlayerCommandExecutorImpl;
import org.ue.economyplayer.logic.impl.EconomyPlayerEventHandlerImpl;
import org.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import org.ue.economyplayer.logic.impl.EconomyPlayerTabCompleterImpl;
import org.ue.economyplayer.logic.impl.EconomyPlayerValidationHandlerImpl;
import org.ue.general.api.GeneralEconomyValidationHandler;
import org.ue.general.impl.GeneralEconomyValidationHandlerImpl;
import org.ue.general.impl.PluginImpl;
import org.ue.general.impl.UltimateEconomy;
import org.ue.jobsystem.dataaccess.api.JobDao;
import org.ue.jobsystem.dataaccess.api.JobcenterDao;
import org.ue.jobsystem.dataaccess.impl.JobDaoImpl;
import org.ue.jobsystem.dataaccess.impl.JobcenterDaoImpl;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.jobsystem.logic.api.JobcenterManager;
import org.ue.jobsystem.logic.api.JobsystemEventHandler;
import org.ue.jobsystem.logic.api.JobsystemValidationHandler;
import org.ue.jobsystem.logic.impl.JobCommandExecutorImpl;
import org.ue.jobsystem.logic.impl.JobManagerImpl;
import org.ue.jobsystem.logic.impl.JobTabCompleterImpl;
import org.ue.jobsystem.logic.impl.JobcenterImpl;
import org.ue.jobsystem.logic.impl.JobcenterManagerImpl;
import org.ue.jobsystem.logic.impl.JobsystemEventHandlerImpl;
import org.ue.jobsystem.logic.impl.JobsystemValidationHandlerImpl;
import org.ue.shopsystem.dataaccess.api.ShopDao;
import org.ue.shopsystem.dataaccess.impl.ShopDaoImpl;
import org.ue.shopsystem.logic.api.Adminshop;
import org.ue.shopsystem.logic.api.AdminshopManager;
import org.ue.shopsystem.logic.api.Playershop;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.RentshopManager;
import org.ue.shopsystem.logic.api.ShopEventHandler;
import org.ue.shopsystem.logic.api.ShopValidationHandler;
import org.ue.shopsystem.logic.impl.AdminshopCommandExecutorImpl;
import org.ue.shopsystem.logic.impl.AdminshopImpl;
import org.ue.shopsystem.logic.impl.AdminshopManagerImpl;
import org.ue.shopsystem.logic.impl.AdminshopTabCompleterImpl;
import org.ue.shopsystem.logic.impl.PlayershopCommandExecutorImpl;
import org.ue.shopsystem.logic.impl.PlayershopImpl;
import org.ue.shopsystem.logic.impl.PlayershopManagerImpl;
import org.ue.shopsystem.logic.impl.PlayershopTabCompleterImpl;
import org.ue.shopsystem.logic.impl.RentshopCommandExecutorImpl;
import org.ue.shopsystem.logic.impl.RentshopImpl;
import org.ue.shopsystem.logic.impl.RentshopManagerImpl;
import org.ue.shopsystem.logic.impl.RentshopTabCompleterImpl;
import org.ue.shopsystem.logic.impl.ShopEventHandlerImpl;
import org.ue.shopsystem.logic.impl.ShopValidationHandlerImpl;
import org.ue.spawnersystem.dataaccess.api.SpawnerSystemDao;
import org.ue.spawnersystem.dataaccess.impl.SpawnerSystemDaoImpl;
import org.ue.spawnersystem.logic.api.SpawnerManager;
import org.ue.spawnersystem.logic.api.SpawnerSystemEventHandler;
import org.ue.spawnersystem.logic.impl.SpawnerManagerImpl;
import org.ue.spawnersystem.logic.impl.SpawnerSystemEventHandlerImpl;
import org.ue.townsystem.dataaccess.api.TownworldDao;
import org.ue.townsystem.dataaccess.impl.TownworldDaoImpl;
import org.ue.townsystem.logic.api.TownsystemEventHandler;
import org.ue.townsystem.logic.api.TownsystemValidationHandler;
import org.ue.townsystem.logic.api.TownworldManager;
import org.ue.townsystem.logic.impl.TownCommandExecutorImpl;
import org.ue.townsystem.logic.impl.TownTabCompleterImpl;
import org.ue.townsystem.logic.impl.TownsystemEventHandlerImpl;
import org.ue.townsystem.logic.impl.TownsystemValidationHandlerImpl;
import org.ue.townsystem.logic.impl.TownworldCommandExecutorImpl;
import org.ue.townsystem.logic.impl.TownworldManagerImpl;
import org.ue.townsystem.logic.impl.TownworldTabCompleterImpl;
import org.ue.vault.impl.UltimateEconomyVaultImpl;

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
		return new MessageWrapperImpl();
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
