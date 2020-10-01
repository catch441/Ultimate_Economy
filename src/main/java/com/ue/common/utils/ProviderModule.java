package com.ue.common.utils;

import javax.inject.Named;
import javax.inject.Singleton;

import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ue.bank.dataaccess.api.BankDao;
import com.ue.bank.dataaccess.impl.BankDaoImpl;
import com.ue.bank.logic.api.BankManager;
import com.ue.bank.logic.api.BankValidationHandler;
import com.ue.bank.logic.impl.BankManagerImpl;
import com.ue.bank.logic.impl.BankValidationHandlerImpl;
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
import com.ue.jobsyste.dataaccess.api.JobDao;
import com.ue.jobsyste.dataaccess.api.JobcenterDao;
import com.ue.jobsystem.dataaccess.impl.JobDaoImpl;
import com.ue.jobsystem.dataaccess.impl.JobcenterDaoImpl;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.api.JobsystemEventHandler;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;
import com.ue.jobsystem.logic.impl.JobCommandExecutorImpl;
import com.ue.jobsystem.logic.impl.JobManagerImpl;
import com.ue.jobsystem.logic.impl.JobTabCompleterImpl;
import com.ue.jobsystem.logic.impl.JobcenterManagerImpl;
import com.ue.jobsystem.logic.impl.JobsystemEventHandlerImpl;
import com.ue.jobsystem.logic.impl.JobsystemValidationHandlerImpl;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.dataaccess.impl.ShopDaoImpl;
import com.ue.shopsystem.logic.api.AdminshopManager;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.RentshopManager;
import com.ue.shopsystem.logic.api.ShopEventHandler;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.impl.AdminshopCommandExecutorImpl;
import com.ue.shopsystem.logic.impl.AdminshopManagerImpl;
import com.ue.shopsystem.logic.impl.AdminshopTabCompleterImpl;
import com.ue.shopsystem.logic.impl.CustomSkullServiceImpl;
import com.ue.shopsystem.logic.impl.PlayershopCommandExecutorImpl;
import com.ue.shopsystem.logic.impl.PlayershopManagerImpl;
import com.ue.shopsystem.logic.impl.PlayershopTabCompleterImpl;
import com.ue.shopsystem.logic.impl.RentshopCommandExecutorImpl;
import com.ue.shopsystem.logic.impl.RentshopManagerImpl;
import com.ue.shopsystem.logic.impl.RentshopTabCompleterImpl;
import com.ue.shopsystem.logic.impl.ShopEventHandlerImpl;
import com.ue.shopsystem.logic.impl.ShopValidationHandlerImpl;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.TownworldManager;
import com.ue.townsystem.logic.impl.TownCommandExecutorImpl;
import com.ue.townsystem.logic.impl.TownTabCompleterImpl;
import com.ue.townsystem.logic.impl.TownsystemValidationHandlerImpl;
import com.ue.townsystem.logic.impl.TownworldCommandExecutorImpl;
import com.ue.townsystem.logic.impl.TownworldManagerImpl;
import com.ue.townsystem.logic.impl.TownworldTabCompleterImpl;
import com.ue.ultimate_economy.UltimateEconomy;
import com.ue.vault.UltimateEconomyVault;
import com.ue.vault.VaultHook;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import net.milkbowl.vault.economy.Economy;

@Module
public class ProviderModule {

	@Singleton
	@Provides
	ComponentProvider provideComponentProvider() {
		return new ComponentProvider();
	}

	@Singleton
	@Provides
	Metrics provideMetrics() {
		return new Metrics(UltimateEconomy.getInstance, 4652);
	}

	@Singleton
	@Provides
	ServerProvider provideBukkitService() {
		return new ServerProvider();
	}

	@Singleton
	@Provides
	Economy provideVaultEconomy(EconomyPlayerManager ecoPlayerManager, BankManager bankManager,
			ConfigManager configManager) {
		return new UltimateEconomyVault(ecoPlayerManager, bankManager, configManager);
	}

	@Singleton
	@Provides
	VaultHook provideVaultHook(Economy vaultEconomy) {
		return new VaultHook(vaultEconomy);
	}

	@Singleton
	@Provides
	MessageWrapper provideMessageWrapper() {
		return new MessageWrapper();
	}

	@Singleton
	@Provides
	ConfigManager provideConfigManager(ConfigDao configDao, MessageWrapper messageWrapper) {
		return new ConfigManagerImpl(configDao, messageWrapper);
	}

	@Singleton
	@Provides
	BankManager providesBankManager(MessageWrapper messageWrapper, BankDao bankDao,
			BankValidationHandler validationHandler) {
		return new BankManagerImpl(messageWrapper, bankDao, validationHandler);
	}

	@Singleton
	@Provides
	EconomyPlayerManager provideEcoPlayerManager(EconomyPlayerDao ecoPlayerDao, MessageWrapper messageWrapper,
			EconomyPlayerValidationHandler validationHandler, BankManager bankManager, ConfigManager configManager,
			Lazy<JobManager> jobManager, ServerProvider serverProvider) {
		return new EconomyPlayerManagerImpl(ecoPlayerDao, messageWrapper, validationHandler, bankManager, configManager,
				jobManager, serverProvider);
	}

	@Singleton
	@Provides
	JobManager provideJobManager(ConfigDao configDao, ComponentProvider componentProvider,
			JobcenterManager jobcenterManager, JobsystemValidationHandler validationHandler,
			EconomyPlayerManager ecoPlayerManager, MessageWrapper messageWrapper) {
		Logger logger = LoggerFactory.getLogger(JobManagerImpl.class);
		return new JobManagerImpl(configDao, componentProvider, jobcenterManager, validationHandler, ecoPlayerManager,
				messageWrapper, logger);
	}

	@Singleton
	@Provides
	JobcenterManager provideJobcenterManager(ComponentProvider componentProvider, ConfigDao configDao,
			Lazy<JobManager> jobManager, ServerProvider serverProvider, JobsystemValidationHandler validationHandler,
			EconomyPlayerManager ecoPlayerManager, MessageWrapper messageWrapper) {
		Logger logger = LoggerFactory.getLogger(JobcenterManagerImpl.class);
		return new JobcenterManagerImpl(componentProvider, configDao, jobManager, serverProvider, validationHandler,
				ecoPlayerManager, messageWrapper, logger);
	}

	@Singleton
	@Provides
	AdminshopManager provideAdminshopManager(ComponentProvider componentProvider,
			ShopValidationHandler validationHandler, MessageWrapper messageWrapper, ServerProvider serverProvider,
			CustomSkullService skullService, ConfigDao configDao, ConfigManager configManager) {
		Logger logger = LoggerFactory.getLogger(AdminshopManagerImpl.class);
		return new AdminshopManagerImpl(componentProvider, validationHandler, messageWrapper, logger, serverProvider,
				skullService, configDao, configManager);
	}

	@Singleton
	@Provides
	PlayershopManager providePlayershopManager(ConfigDao configDao, ComponentProvider componentProvider,
			TownsystemValidationHandler townsystemValidationHandler, ShopValidationHandler validationHandler,
			MessageWrapper messageWrapper, ServerProvider serverProvider, CustomSkullService skullService,
			EconomyPlayerManager ecoPlayerManager, ConfigManager configManager, TownworldManager townworldManager) {
		Logger logger = LoggerFactory.getLogger(PlayershopManagerImpl.class);
		return new PlayershopManagerImpl(configDao, townsystemValidationHandler, validationHandler, messageWrapper,
				componentProvider, logger, serverProvider, skullService, ecoPlayerManager, configManager,
				townworldManager);
	}

	@Singleton
	@Provides
	RentshopManager provideRentshopManager(ComponentProvider componentProvider, ServerProvider serverProvider,
			ShopValidationHandler validationHandler, MessageWrapper messageWrapper, CustomSkullService skullService,
			EconomyPlayerManager ecoPlayerManager, ConfigManager configManager, TownworldManager townworldManager,
			PlayershopManager playershopManager, ConfigDao configDao) {
		Logger logger = LoggerFactory.getLogger(RentshopManagerImpl.class);
		return new RentshopManagerImpl(serverProvider, validationHandler, messageWrapper, componentProvider, logger,
				skullService, ecoPlayerManager, configManager, townworldManager, playershopManager, configDao);
	}

	@Singleton
	@Provides
	TownworldManager provideTownworldManager() {
		return new TownworldManagerImpl();
	}

	@Singleton
	@Provides
	@Named("ConfigCommandExecutor")
	CommandExecutor provideConfigCommandExecutor(EconomyPlayerManager ecoPlayerManager, ConfigManager configManager,
			MessageWrapper messageWrapper) {
		return new ConfigCommandExecutorImpl(ecoPlayerManager, configManager, messageWrapper);
	}

	@Singleton
	@Provides
	@Named("EconomyPlayerCommandExecutor")
	CommandExecutor provideEconomyPlayerCommandExecutor(ConfigManager configManager, MessageWrapper messageWrapper,
			EconomyPlayerManager ecoPlayerManager, TownworldManager townworldManager) {
		return new EconomyPlayerCommandExecutorImpl(configManager, messageWrapper, ecoPlayerManager, townworldManager);
	}

	@Singleton
	@Provides
	@Named("JobCommandExecutor")
	CommandExecutor provideJobCommandExecutor(JobcenterManager jobcenterManager, JobManager jobManager,
			MessageWrapper messageWrapper) {
		return new JobCommandExecutorImpl(jobcenterManager, jobManager, messageWrapper);
	}

	@Singleton
	@Provides
	@Named("PlayershopCommandExecutor")
	CommandExecutor providePlayershopCommandExecutor(EconomyPlayerManager ecoPlayerManager,
			PlayershopManager playershopManager, MessageWrapper messageWrapper) {
		return new PlayershopCommandExecutorImpl(ecoPlayerManager, playershopManager, messageWrapper);
	}

	@Singleton
	@Provides
	@Named("AdminshopCommandExecutor")
	CommandExecutor provideAdminshopCommandExecutor(AdminshopManager adminshopManager, MessageWrapper messageWrapper,
			ServerProvider serverProvider) {
		return new AdminshopCommandExecutorImpl(adminshopManager, messageWrapper, serverProvider);
	}

	@Singleton
	@Provides
	@Named("RentshopCommandExecutor")
	CommandExecutor provideRentshopCommandExecutor(RentshopManager rentshopManager, MessageWrapper messageWrapper) {
		return new RentshopCommandExecutorImpl(rentshopManager, messageWrapper);
	}

	@Singleton
	@Provides
	@Named("TownCommandExecutor")
	CommandExecutor provideTownCommandExecutor() {
		return new TownCommandExecutorImpl();
	}

	@Singleton
	@Provides
	@Named("TownworldCommandExecutor")
	CommandExecutor provideTownworldCommandExecutor() {
		return new TownworldCommandExecutorImpl();
	}

	@Singleton
	@Provides
	@Named("EconomyPlayerTabCompleter")
	TabCompleter provideEcoPlayerTabCompleter(EconomyPlayerManager ecoPlayerManager) {
		return new EconomyPlayerTabCompleterImpl(ecoPlayerManager);
	}

	@Singleton
	@Provides
	@Named("ConfigTabCompleter")
	TabCompleter provideConfigTabCompleter() {
		return new ConfigTabCompleterImpl();
	}

	@Singleton
	@Provides
	@Named("JobTabCompleter")
	TabCompleter provideJobTabCompleter(JobManager jobManager, JobcenterManager jobcenterManager) {
		return new JobTabCompleterImpl(jobManager, jobcenterManager);
	}

	@Singleton
	@Provides
	@Named("PlayershopTabCompleter")
	TabCompleter providePlayershopTabCompleter(PlayershopManager playershopManager) {
		return new PlayershopTabCompleterImpl(playershopManager);
	}

	@Singleton
	@Provides
	@Named("AdminshopTabCompleter")
	TabCompleter provideAdminshopTabCompleter(AdminshopManager adminshopManager) {
		return new AdminshopTabCompleterImpl(adminshopManager);
	}

	@Singleton
	@Provides
	@Named("RentshopTabCompleter")
	TabCompleter provideRentshopTabCompleter(RentshopManager rentshopManager) {
		return new RentshopTabCompleterImpl(rentshopManager);
	}

	@Singleton
	@Provides
	@Named("TownTabCompleter")
	TabCompleter provideTownTabCompleter() {
		return new TownTabCompleterImpl();
	}

	@Singleton
	@Provides
	@Named("TownworldTabCompleter")
	TabCompleter provideTownworldTabCompleter() {
		return new TownworldTabCompleterImpl();
	}

	@Singleton
	@Provides
	ConfigDao provideConfigDao(ServerProvider serverProvider) {
		Logger logger = LoggerFactory.getLogger(ConfigDaoImpl.class);
		return new ConfigDaoImpl(serverProvider, logger);
	}

	@Singleton
	@Provides
	BankDao provideBankDao(ServerProvider serverProvider) {
		Logger logger = LoggerFactory.getLogger(BankDaoImpl.class);
		return new BankDaoImpl(serverProvider, logger);
	}

	@Singleton
	@Provides
	EconomyPlayerDao provideEcoPlayerDao(BankManager bankManager, ServerProvider bukkitServic) {
		Logger logger = LoggerFactory.getLogger(EconomyPlayerDaoImpl.class);
		return new EconomyPlayerDaoImpl(bankManager, bukkitServic, logger);
	}

	@Provides
	JobDao provideJobDao(ServerProvider serverProvider) {
		Logger logger = LoggerFactory.getLogger(JobDaoImpl.class);
		return new JobDaoImpl(serverProvider, logger);
	}

	@Provides
	JobcenterDao provideJobcenterDao(ServerProvider serverProvider) {
		Logger logger = LoggerFactory.getLogger(JobcenterDaoImpl.class);
		return new JobcenterDaoImpl(serverProvider, logger);
	}

	@Provides
	ShopDao provideShopDao(ServerProvider serverProvider, EconomyPlayerManager ecoPlayerManager,
			ShopValidationHandler validationHandler, TownsystemValidationHandler townsystemValidationHandler) {
		Logger logger = LoggerFactory.getLogger(ShopDaoImpl.class);
		return new ShopDaoImpl(serverProvider, ecoPlayerManager, validationHandler, townsystemValidationHandler,
				logger);
	}

	@Singleton
	@Provides
	BankValidationHandler provideBankValidationHandler(MessageWrapper messageWrapper) {
		return new BankValidationHandlerImpl();
	}

	@Singleton
	@Provides
	EconomyPlayerValidationHandler provideEcoPlayerValidationHandler(MessageWrapper messageWrapper) {
		return new EconomyPlayerValidationHandlerImpl(messageWrapper);
	}

	@Singleton
	@Provides
	JobsystemValidationHandler provideJobsystemValidationHandler(MessageWrapper messageWrapper) {
		return new JobsystemValidationHandlerImpl(messageWrapper);
	}

	@Singleton
	@Provides
	ShopValidationHandler provideShopValidationHandler(TownworldManager townworldManager, ConfigManager configManager,
			MessageWrapper messageWrapper) {
		return new ShopValidationHandlerImpl(townworldManager, configManager, messageWrapper);
	}

	@Singleton
	@Provides
	EconomyPlayerEventHandler provideEcoPlayerEventHandler(EconomyPlayerManager ecoPlayerManager,
			ConfigManager configManager) {
		return new EconomyPlayerEventHandlerImpl(ecoPlayerManager, configManager);
	}

	@Singleton
	@Provides
	JobsystemEventHandler provideJobsystemEventHandler(ServerProvider serverProvider, JobcenterManager jobcenterManager,
			JobManager jobManager, EconomyPlayerManager ecoPlayerManager) {
		return new JobsystemEventHandlerImpl(serverProvider, jobcenterManager, jobManager, ecoPlayerManager);
	}

	@Singleton
	@Provides
	ShopEventHandler provideShopEventHandler(RentshopManager rentshopManager, AdminshopManager adminshopManager,
			PlayershopManager playershopManager, EconomyPlayerManager ecoPlayerManager) {
		return new ShopEventHandlerImpl(rentshopManager, adminshopManager, playershopManager, ecoPlayerManager);
	}

	@Singleton
	@Provides
	TownsystemValidationHandler provideTownsystemValidationHandler(TownworldManager townworldManager,
			MessageWrapper messageWrapper) {
		return new TownsystemValidationHandlerImpl(townworldManager, messageWrapper);
	}

	@Singleton
	@Provides
	CustomSkullService provideCustomSkullService() {
		return new CustomSkullServiceImpl();
	}
}
