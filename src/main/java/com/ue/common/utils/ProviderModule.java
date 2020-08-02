package com.ue.common.utils;

import java.io.File;

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
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;
import com.ue.jobsystem.logic.impl.JobManagerImpl;
import com.ue.jobsystem.logic.impl.JobcenterManagerImpl;
import com.ue.jobsystem.logic.impl.JobsystemValidationHandlerImpl;
import com.ue.ultimate_economy.UltimateEconomy;
import com.ue.vault.UltimateEconomyVault;
import com.ue.vault.VaultHook;

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
	ConfigManager provideConfigManager(EconomyPlayerManager ecoPlayerManager, ConfigDao configDao,
			MessageWrapper messageWrapper) {
		return new ConfigManagerImpl(ecoPlayerManager, configDao, messageWrapper);
	}

	@Singleton
	@Provides
	BankManager providesBankManager(BankValidationHandler bankValidationHandler, BankDao bankDao,
			MessageWrapper messageWrapper) {
		return new BankManagerImpl(bankValidationHandler, bankDao, messageWrapper);
	}

	@Singleton
	@Provides
	EconomyPlayerManager provideEcoPlayerManager(BankManager bankManager,
			EconomyPlayerValidationHandler validationHandler, MessageWrapper messageWrapper,
			EconomyPlayerDao ecoPlayerDao) {
		return new EconomyPlayerManagerImpl(bankManager, validationHandler, messageWrapper, ecoPlayerDao);
	}

	@Singleton
	@Provides
	JobManager provideJobManager(JobcenterManager jobcenterManager, JobsystemValidationHandler validationHandler,
			EconomyPlayerManager ecoPlayerManager, MessageWrapper messageWrapper) {
		return new JobManagerImpl(jobcenterManager, validationHandler, ecoPlayerManager, messageWrapper);
	}

	@Singleton
	@Provides
	JobcenterManager provideJobcenterManager(JobsystemValidationHandler validationHandler, JobManager jobManager,
			MessageWrapper messageWrapper) {
		return new JobcenterManagerImpl(validationHandler, jobManager, messageWrapper);
	}

	@Singleton
	@Provides
	@Named("ConfigCommandExecutor")
	CommandExecutor provideConfigCommandExecutor(ConfigManager configManager, MessageWrapper messageWrapper) {
		return new ConfigCommandExecutorImpl(configManager, messageWrapper);
	}

	@Singleton
	@Provides
	@Named("EconomyPlayerCommandExecutor")
	CommandExecutor provideEconomyPlayerCommandExecutor(EconomyPlayerManager ecoPlayerManager,
			ConfigManager configManager, MessageWrapper messageWrapper) {
		return new EconomyPlayerCommandExecutorImpl(ecoPlayerManager, configManager, messageWrapper);
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
	ConfigDao provideConfigDao() {
		return new ConfigDaoImpl(new File(UltimateEconomy.getInstance.getDataFolder(), "config.yml"));
	}

	@Singleton
	@Provides
	BankDao provideBankDao() {
		return new BankDaoImpl();
	}

	@Singleton
	@Provides
	EconomyPlayerDao provideEcoPlayerDao(BankManager bankManager) {
		return new EconomyPlayerDaoImpl(bankManager);
	}

	@Singleton
	@Provides
	BankValidationHandler provideBankValidationHandler(MessageWrapper messageWrapper) {
		return new BankValidationHandlerImpl(messageWrapper);
	}

	@Singleton
	@Provides
	EconomyPlayerValidationHandler provideEcoPlayerValidationHandler(MessageWrapper messageWrapper) {
		return new EconomyPlayerValidationHandlerImpl(messageWrapper);
	}

	@Singleton
	@Provides
	EconomyPlayerEventHandler provideEcoPlayerEventHandler(EconomyPlayerManager ecoPlayerManager,
			ConfigManager configManager) {
		return new EconomyPlayerEventHandlerImpl(ecoPlayerManager, configManager);
	}

	@Singleton
	@Provides
	JobsystemValidationHandler provideJobsystemValidationHandler(MessageWrapper messageWrapper) {
		return new JobsystemValidationHandlerImpl(messageWrapper);
	}
}
