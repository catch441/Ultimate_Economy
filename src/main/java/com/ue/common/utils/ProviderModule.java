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
import com.ue.jobsystem.dataaccese.impl.JobDaoImpl;
import com.ue.jobsystem.dataaccese.impl.JobcenterDaoImpl;
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
	Metrics provideMetrics() {
		return new Metrics(UltimateEconomy.getInstance, 4652);
	}

	@Singleton
	@Provides
	BukkitService provideBukkitService() {
		return new BukkitService();
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
			Lazy<JobManager> jobManager, BukkitService bukkitService) {
		return new EconomyPlayerManagerImpl(ecoPlayerDao, messageWrapper, validationHandler, bankManager, configManager,
				jobManager, bukkitService);
	}

	@Singleton
	@Provides
	JobManager provideJobManager(JobcenterManager jobcenterManager, JobsystemValidationHandler validationHandler,
			EconomyPlayerManager ecoPlayerManager, MessageWrapper messageWrapper) {
		return new JobManagerImpl(jobcenterManager, validationHandler, ecoPlayerManager, messageWrapper);
	}

	@Singleton
	@Provides
	JobcenterManager provideJobcenterManager(JobsystemValidationHandler validationHandler,
			EconomyPlayerManager ecoPlayerManager, MessageWrapper messageWrapper) {
		return new JobcenterManagerImpl(validationHandler, ecoPlayerManager, messageWrapper);
	}

	@Singleton
	@Provides
	AdminshopManager provideAdminshopManager(ShopValidationHandler validationHandler, MessageWrapper messageWrapper) {
		return new AdminshopManagerImpl(validationHandler, messageWrapper);
	}

	@Singleton
	@Provides
	PlayershopManager providePlayershopManager(TownworldManager townworldManager,
			ShopValidationHandler validationHandler, MessageWrapper messageWrapper) {
		return new PlayershopManagerImpl(townworldManager, validationHandler, messageWrapper);
	}

	@Singleton
	@Provides
	RentshopManager provideRentshopManager(ShopValidationHandler validationHandler, MessageWrapper messageWrapper) {
		return new RentshopManagerImpl(validationHandler, messageWrapper);
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
	CommandExecutor provideAdminshopCommandExecutor(AdminshopManager adminshopManager, MessageWrapper messageWrapper) {
		return new AdminshopCommandExecutorImpl(adminshopManager, messageWrapper);
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
	ConfigDao provideConfigDao(BukkitService bukkitService) {
		return new ConfigDaoImpl(bukkitService);
	}

	@Singleton
	@Provides
	BankDao provideBankDao(BukkitService bukkitService) {
		return new BankDaoImpl(bukkitService);
	}

	@Singleton
	@Provides
	EconomyPlayerDao provideEcoPlayerDao(BankManager bankManager, BukkitService bukkitServic) {
		return new EconomyPlayerDaoImpl(bankManager, bukkitServic);
	}

	@Provides
	JobDao provideJobDao() {
		return new JobDaoImpl();
	}

	@Provides
	JobcenterDao provideJobcenterDao(BukkitService bukkitService) {
		return new JobcenterDaoImpl(bukkitService);
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
	JobsystemEventHandler provideJobsystemEventHandler(JobcenterManager jobcenterManager, JobManager jobManager,
			EconomyPlayerManager ecoPlayerManager) {
		return new JobsystemEventHandlerImpl(jobcenterManager, jobManager, ecoPlayerManager);
	}

	@Singleton
	@Provides
	ShopEventHandler provideShopEventHandler(RentshopManager rentshopManager, AdminshopManager adminshopManager,
			PlayershopManager playershopManager, EconomyPlayerManager ecoPlayerManager) {
		return new ShopEventHandlerImpl(rentshopManager, adminshopManager, playershopManager, ecoPlayerManager);
	}

	@Singleton
	@Provides
	TownsystemValidationHandler provideTownsystemValidationHandler(MessageWrapper messageWrapper) {
		return new TownsystemValidationHandlerImpl(messageWrapper);
	}

	@Singleton
	@Provides
	CustomSkullService provideCustomSkullService() {
		return new CustomSkullServiceImpl();
	}
}
