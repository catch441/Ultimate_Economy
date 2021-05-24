package org.ue.common.utils;

import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Villager.Type;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.ue.bank.dataaccess.api.BankDao;
import org.ue.bank.dataaccess.impl.BankDaoImpl;
import org.ue.bank.logic.api.BankManager;
import org.ue.bank.logic.api.BankValidator;
import org.ue.bank.logic.impl.BankManagerImpl;
import org.ue.bank.logic.impl.BankValidationHandlerImpl;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.logic.api.InventoryGuiHandler;
import org.ue.common.logic.impl.CustomSkullServiceImpl;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.config.dataaccess.impl.ConfigDaoImpl;
import org.ue.config.logic.api.ConfigManager;
import org.ue.config.logic.api.ConfigValidator;
import org.ue.config.logic.impl.ConfigCommandExecutorImpl;
import org.ue.config.logic.impl.ConfigManagerImpl;
import org.ue.config.logic.impl.ConfigTabCompleterImpl;
import org.ue.config.logic.impl.ConfigValidatorImpl;
import org.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import org.ue.economyplayer.dataaccess.impl.EconomyPlayerDaoImpl;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerEventHandler;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.api.EconomyPlayerValidator;
import org.ue.economyplayer.logic.impl.EconomyPlayerCommandExecutorImpl;
import org.ue.economyplayer.logic.impl.EconomyPlayerEventHandlerImpl;
import org.ue.economyplayer.logic.impl.EconomyPlayerImpl;
import org.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import org.ue.economyplayer.logic.impl.EconomyPlayerTabCompleterImpl;
import org.ue.economyplayer.logic.impl.EconomyPlayerValidatorImpl;
import org.ue.economyvillager.logic.api.EconomyVillager;
import org.ue.economyvillager.logic.impl.EconomyVillagerCustomizeHandlerImpl;
import org.ue.general.impl.PluginImpl;
import org.ue.general.impl.UltimateEconomyEventHandlerImpl;
import org.ue.jobsystem.dataaccess.impl.JobDaoImpl;
import org.ue.jobsystem.dataaccess.impl.JobcenterDaoImpl;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.jobsystem.logic.api.JobcenterManager;
import org.ue.jobsystem.logic.api.JobsystemEventHandler;
import org.ue.jobsystem.logic.api.JobsystemValidator;
import org.ue.jobsystem.logic.impl.JobCommandExecutorImpl;
import org.ue.jobsystem.logic.impl.JobImpl;
import org.ue.jobsystem.logic.impl.JobManagerImpl;
import org.ue.jobsystem.logic.impl.JobTabCompleterImpl;
import org.ue.jobsystem.logic.impl.JobcenterImpl;
import org.ue.jobsystem.logic.impl.JobcenterManagerImpl;
import org.ue.jobsystem.logic.impl.JobsystemEventHandlerImpl;
import org.ue.jobsystem.logic.impl.JobsystemValidatorImpl;
import org.ue.shopsystem.dataaccess.impl.ShopDaoImpl;
import org.ue.shopsystem.logic.api.Adminshop;
import org.ue.shopsystem.logic.api.AdminshopManager;
import org.ue.shopsystem.logic.api.Playershop;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.RentshopManager;
import org.ue.shopsystem.logic.api.ShopEditorHandler;
import org.ue.shopsystem.logic.api.ShopEventHandler;
import org.ue.shopsystem.logic.api.ShopSlotEditorHandler;
import org.ue.shopsystem.logic.api.ShopValidator;
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
import org.ue.shopsystem.logic.impl.RentshopRentGuiHandlerImpl;
import org.ue.shopsystem.logic.impl.RentshopTabCompleterImpl;
import org.ue.shopsystem.logic.impl.ShopEditorHandlerImpl;
import org.ue.shopsystem.logic.impl.ShopEventHandlerImpl;
import org.ue.shopsystem.logic.impl.ShopSlotEditorHandlerImpl;
import org.ue.shopsystem.logic.impl.ShopValidatorImpl;
import org.ue.spawnersystem.dataaccess.api.SpawnersystemDao;
import org.ue.spawnersystem.dataaccess.impl.SpawnerystemDaoImpl;
import org.ue.spawnersystem.logic.api.SpawnerManager;
import org.ue.spawnersystem.logic.api.SpawnersystemEventHandler;
import org.ue.spawnersystem.logic.impl.SpawnerManagerImpl;
import org.ue.spawnersystem.logic.impl.SpawnerystemEventHandlerImpl;
import org.ue.townsystem.logic.api.TownworldManager;
import org.ue.townsystem.logic.impl.PlotImpl;
import org.ue.townsystem.logic.impl.TownCommandExecutorImpl;
import org.ue.townsystem.logic.impl.TownImpl;
import org.ue.townsystem.logic.impl.TownTabCompleterImpl;
import org.ue.townsystem.logic.impl.TownsystemEventHandlerImpl;
import org.ue.townsystem.logic.impl.TownsystemValidatorImpl;
import org.ue.townsystem.logic.impl.TownworldCommandExecutorImpl;
import org.ue.townsystem.logic.impl.TownworldImpl;
import org.ue.townsystem.logic.impl.TownworldManagerImpl;
import org.ue.townsystem.logic.impl.TownworldTabCompleterImpl;
import org.ue.townsystem.dataaccess.api.TownworldDao;
import org.ue.townsystem.dataaccess.impl.TownworldDaoImpl;
import org.ue.townsystem.logic.api.Plot;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownsystemEventHandler;
import org.ue.townsystem.logic.api.TownsystemValidator;
import org.ue.townsystem.logic.api.Townworld;
import org.ue.vault.impl.UltimateEconomyVaultImpl;

import net.milkbowl.vault.economy.Economy;

public class UltimateEconomyProvider {

	private final MessageWrapper messageWrapper;
	private final ServerProvider serverProvider;
	private final CustomSkullService customSkullService;
	private final Updater updater;
	private final Metrics metrics;
	private final Economy vaultEconomy;
	// Manager
	private final ConfigManager configManager;
	private final BankManager bankManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private final AdminshopManager adminshopManager;
	private final PlayershopManager playershopManager;
	private final RentshopManager rentshopManager;
	private final JobcenterManager jobcenterManager;
	private final JobManager jobManager;
	private final TownworldManager townworldManager;
	private final SpawnerManager spawnerManager;
	// TabCompleter
	private final TabCompleter townworldTabCompleter;
	private final TabCompleter townTabCompleter;
	private final TabCompleter rentshopTabCompleter;
	private final TabCompleter adminshopTabCompleter;
	private final TabCompleter playershopTabCompleter;
	private final TabCompleter jobTabCompleter;
	private final TabCompleter configTabCompleter;
	private final TabCompleter ecoPlayerTabCompleter;
	// CommandExecutor
	private final CommandExecutor configCommandExecutor;
	private final CommandExecutor ecoPlayerCommandExecutor;
	private final CommandExecutor jobCommandExecutor;
	private final CommandExecutor playershopCommandExecutor;
	private final CommandExecutor adminshopCommandExecutor;
	private final CommandExecutor rentshopCommandExecutor;
	private final CommandExecutor townCommandExecutor;
	private final CommandExecutor townworldCommandExecutor;
	// Event handlers
	private final TownsystemEventHandler townsystemEventHandler;
	private final SpawnersystemEventHandler spawnersystemEventHandler;
	private final EconomyPlayerEventHandler economyPlayerEventHandler;
	private final JobsystemEventHandler jobsystemEventHandler;
	private final ShopEventHandler shopEventHandler;
	private final Listener ultimateEconomyEventHandler;
	// Validators
	private final ShopValidator shopValidator;
	private final TownsystemValidator townsystemValidator;
	private final BankValidator bankValidator;
	private final EconomyPlayerValidator ecoPlayerValidator;
	private final JobsystemValidator jobsystemValidator;
	private final ConfigValidator configValidator;
	// Data access objects
	private final ConfigDao configDao;
	private final BankDao bankDao;
	private final SpawnersystemDao spawnersystemDao;
	private final EconomyPlayerDao ecoPlayerDao;

	public static UltimateEconomyProvider build() {
		return new UltimateEconomyProvider();
	}

	private UltimateEconomyProvider() {
		messageWrapper = new MessageWrapperImpl();
		serverProvider = new ServerProvider();
		updater = new Updater();
		metrics = new Metrics(PluginImpl.getInstance, 4652);
		customSkullService = new CustomSkullServiceImpl(serverProvider);

		bankValidator = new BankValidationHandlerImpl(messageWrapper);
		ecoPlayerValidator = new EconomyPlayerValidatorImpl(messageWrapper);
		jobsystemValidator = new JobsystemValidatorImpl(serverProvider, messageWrapper);
		configValidator = new ConfigValidatorImpl(messageWrapper);

		configDao = new ConfigDaoImpl(serverProvider);
		bankDao = new BankDaoImpl(serverProvider);
		spawnersystemDao = new SpawnerystemDaoImpl(serverProvider);

		spawnerManager = new SpawnerManagerImpl(spawnersystemDao, serverProvider);
		configManager = new ConfigManagerImpl(configDao, configValidator);
		bankManager = new BankManagerImpl(bankDao, bankValidator);
		ecoPlayerDao = new EconomyPlayerDaoImpl(bankManager, serverProvider);

		ecoPlayerManager = new EconomyPlayerManagerImpl(ecoPlayerDao, ecoPlayerValidator, bankManager, serverProvider);
		jobcenterManager = new JobcenterManagerImpl(configDao, serverProvider, ecoPlayerManager, jobsystemValidator);
		jobManager = new JobManagerImpl(serverProvider, configDao, jobcenterManager, jobsystemValidator,
				ecoPlayerManager);

		TownsystemValidatorImpl townsystemValidator = new TownsystemValidatorImpl(messageWrapper, serverProvider);
		townworldManager = new TownworldManagerImpl(configDao, ecoPlayerManager, messageWrapper, townsystemValidator,
				serverProvider);
		townsystemValidator.lazyInjection(townworldManager);
		this.townsystemValidator = townsystemValidator;

		shopValidator = new ShopValidatorImpl(serverProvider, messageWrapper, configManager, townworldManager);
		adminshopManager = new AdminshopManagerImpl(shopValidator, messageWrapper, serverProvider, configDao);
		playershopManager = new PlayershopManagerImpl(configDao, shopValidator, messageWrapper, serverProvider);
		rentshopManager = new RentshopManagerImpl(messageWrapper, shopValidator, serverProvider, configDao);

		townTabCompleter = new TownTabCompleterImpl(townworldManager, ecoPlayerManager);
		townworldTabCompleter = new TownworldTabCompleterImpl(serverProvider, townworldManager);
		rentshopTabCompleter = new RentshopTabCompleterImpl(rentshopManager);
		adminshopTabCompleter = new AdminshopTabCompleterImpl(adminshopManager);
		playershopTabCompleter = new PlayershopTabCompleterImpl(playershopManager);
		jobTabCompleter = new JobTabCompleterImpl(jobManager, jobcenterManager);
		configTabCompleter = new ConfigTabCompleterImpl();
		ecoPlayerTabCompleter = new EconomyPlayerTabCompleterImpl(ecoPlayerManager);

		configCommandExecutor = new ConfigCommandExecutorImpl(configManager, ecoPlayerManager, messageWrapper);
		ecoPlayerCommandExecutor = new EconomyPlayerCommandExecutorImpl(ecoPlayerValidator, configManager,
				messageWrapper, ecoPlayerManager, townworldManager);
		jobCommandExecutor = new JobCommandExecutorImpl(configManager, jobcenterManager, jobManager, messageWrapper);
		playershopCommandExecutor = new PlayershopCommandExecutorImpl(messageWrapper, playershopManager,
				ecoPlayerManager);
		adminshopCommandExecutor = new AdminshopCommandExecutorImpl(jobManager, ecoPlayerManager, adminshopManager,
				messageWrapper, serverProvider, configManager);
		rentshopCommandExecutor = new RentshopCommandExecutorImpl(rentshopManager, messageWrapper);
		townCommandExecutor = new TownCommandExecutorImpl(configManager, ecoPlayerManager, townworldManager,
				messageWrapper, townsystemValidator);
		townworldCommandExecutor = new TownworldCommandExecutorImpl(configManager, townworldManager, messageWrapper);

		townsystemEventHandler = new TownsystemEventHandlerImpl(configManager, townworldManager, ecoPlayerManager,
				messageWrapper);
		spawnersystemEventHandler = new SpawnerystemEventHandlerImpl(messageWrapper, serverProvider, spawnerManager);
		economyPlayerEventHandler = new EconomyPlayerEventHandlerImpl(configManager, ecoPlayerManager);
		jobsystemEventHandler = new JobsystemEventHandlerImpl(serverProvider, jobcenterManager, ecoPlayerManager);
		shopEventHandler = new ShopEventHandlerImpl(ecoPlayerManager, adminshopManager, playershopManager,
				rentshopManager);

		ultimateEconomyEventHandler = new UltimateEconomyEventHandlerImpl(jobcenterManager, rentshopManager,
				playershopManager, adminshopManager, updater, spawnersystemEventHandler, townsystemEventHandler,
				shopEventHandler, jobsystemEventHandler, economyPlayerEventHandler);
		vaultEconomy = new UltimateEconomyVaultImpl(configManager, bankManager, ecoPlayerManager, serverProvider);
	}

	public UltimateEconomyProvider(Economy vaultEconomy, Metrics metrics, Updater updater,
			CustomSkullService customSkullService, ServerProvider serverProvider, MessageWrapper messageWrapper,
			ConfigManager configManager, SpawnerManager spawnerManager, BankManager bankManager,
			EconomyPlayerManager ecoPlayerManager, AdminshopManager adminshopManager,
			PlayershopManager playershopManager, RentshopManager rentshopManager, JobcenterManager jobcenterManager,
			JobManager jobManager, TownworldManager townworldManager, TabCompleter townTabCompleter,
			TabCompleter townworldTabCompleter, TabCompleter rentshopTabCompleter, TabCompleter adminshopTabCompleter,
			TabCompleter playershopTabCompleter, TabCompleter jobTabCompleter, TabCompleter configTabCompleter,
			TabCompleter ecoPlayerTabCompleter, CommandExecutor configCommandExecutor,
			CommandExecutor ecoPlayerCommandExecutor, CommandExecutor jobCommandExecutor,
			CommandExecutor playershopCommandExecutor, CommandExecutor adminshopCommandExecutor,
			CommandExecutor rentshopCommandExecutor, CommandExecutor townCommandExecutor,
			CommandExecutor townworldCommandExecutor, TownsystemEventHandler townsystemEventHandler,
			SpawnersystemEventHandler spawnersystemEventHandler, EconomyPlayerEventHandler economyPlayerEventHandler,
			JobsystemEventHandler jobsystemEventHandler, ShopEventHandler shopEventHandler, ShopValidator shopValidator,
			TownsystemValidator townsystemValidator, BankValidator bankValidator,
			EconomyPlayerValidator ecoPlayerValidator, JobsystemValidator jobsystemValidator,
			ConfigValidator configValidator, ConfigDao configDao, BankDao bankDao, SpawnersystemDao spawnersystemDao,
			EconomyPlayerDao ecoPlayerDao, Listener ultimateEconomyEventHandler) {
		this.serverProvider = serverProvider;
		this.messageWrapper = messageWrapper;
		this.customSkullService = customSkullService;
		this.updater = updater;
		this.metrics = metrics;
		this.vaultEconomy = vaultEconomy;
		this.configManager = configManager;
		this.bankManager = bankManager;
		this.ecoPlayerManager = ecoPlayerManager;
		this.adminshopManager = adminshopManager;
		this.playershopManager = playershopManager;
		this.rentshopManager = rentshopManager;
		this.jobcenterManager = jobcenterManager;
		this.jobManager = jobManager;
		this.townworldManager = townworldManager;
		this.townTabCompleter = townTabCompleter;
		this.townworldTabCompleter = townworldTabCompleter;
		this.rentshopTabCompleter = rentshopTabCompleter;
		this.adminshopTabCompleter = adminshopTabCompleter;
		this.playershopTabCompleter = playershopTabCompleter;
		this.jobTabCompleter = jobTabCompleter;
		this.configTabCompleter = configTabCompleter;
		this.ecoPlayerTabCompleter = ecoPlayerTabCompleter;
		this.configCommandExecutor = configCommandExecutor;
		this.ecoPlayerCommandExecutor = ecoPlayerCommandExecutor;
		this.jobCommandExecutor = jobCommandExecutor;
		this.playershopCommandExecutor = playershopCommandExecutor;
		this.adminshopCommandExecutor = adminshopCommandExecutor;
		this.rentshopCommandExecutor = rentshopCommandExecutor;
		this.townCommandExecutor = townCommandExecutor;
		this.townworldCommandExecutor = townworldCommandExecutor;
		this.townsystemEventHandler = townsystemEventHandler;
		this.spawnersystemEventHandler = spawnersystemEventHandler;
		this.economyPlayerEventHandler = economyPlayerEventHandler;
		this.jobsystemEventHandler = jobsystemEventHandler;
		this.shopEventHandler = shopEventHandler;
		this.ultimateEconomyEventHandler = ultimateEconomyEventHandler;
		this.shopValidator = shopValidator;
		this.townsystemValidator = townsystemValidator;
		this.spawnerManager = spawnerManager;
		this.bankValidator = bankValidator;
		this.ecoPlayerValidator = ecoPlayerValidator;
		this.jobsystemValidator = jobsystemValidator;
		this.configValidator = configValidator;
		this.configDao = configDao;
		this.bankDao = bankDao;
		this.spawnersystemDao = spawnersystemDao;
		this.ecoPlayerDao = ecoPlayerDao;
	}

	public MessageWrapper getMessageWrapper() {
		return messageWrapper;
	}

	public ServerProvider getServerProvider() {
		return serverProvider;
	}

	public CustomSkullService getCustomSkullService() {
		return customSkullService;
	}

	public Updater getUpdater() {
		return updater;
	}

	public Metrics getMetrics() {
		return metrics;
	}

	public Economy getVaultEconomy() {
		return vaultEconomy;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public SpawnerManager getSpawnerManager() {
		return spawnerManager;
	}

	public BankManager getBankManager() {
		return bankManager;
	}

	public EconomyPlayerManager getEconomyPlayerManager() {
		return ecoPlayerManager;
	}

	public AdminshopManager getAdminshopManager() {
		return adminshopManager;
	}

	public PlayershopManager getPlayershopManager() {
		return playershopManager;
	}

	public RentshopManager getRentshopManager() {
		return rentshopManager;
	}

	public JobcenterManager getJobcenterManager() {
		return jobcenterManager;
	}

	public JobManager getJobManager() {
		return jobManager;
	}

	public TownworldManager getTownworldManager() {
		return townworldManager;
	}

	public TabCompleter getTownTabCompleter() {
		return townTabCompleter;
	}

	public TabCompleter getTownworldTabCompleter() {
		return townworldTabCompleter;
	}

	public TabCompleter getRentshopTabCompleter() {
		return rentshopTabCompleter;
	}

	public TabCompleter getAdminshopTabCompleter() {
		return adminshopTabCompleter;
	}

	public TabCompleter getPlayershopTabCompleter() {
		return playershopTabCompleter;
	}

	public TabCompleter getJobTabCompleter() {
		return jobTabCompleter;
	}

	public TabCompleter getConfigTabCompleter() {
		return configTabCompleter;
	}

	public TabCompleter getEconomyPlayerTabCompleter() {
		return ecoPlayerTabCompleter;
	}

	public CommandExecutor getConfigCommandExecutor() {
		return configCommandExecutor;
	}

	public CommandExecutor getEconomyPlayerCommandExecutor() {
		return ecoPlayerCommandExecutor;
	}

	public CommandExecutor getJobCommandExecutor() {
		return jobCommandExecutor;
	}

	public CommandExecutor getPlayershopCommandExecutor() {
		return playershopCommandExecutor;
	}

	public CommandExecutor getAdminshopCommandExecutor() {
		return adminshopCommandExecutor;
	}

	public CommandExecutor getRentshopCommandExecutor() {
		return rentshopCommandExecutor;
	}

	public CommandExecutor getTownCommandExecutor() {
		return townCommandExecutor;
	}

	public CommandExecutor getTownworldCommandExecutor() {
		return townworldCommandExecutor;
	}

	public TownsystemEventHandler getTownsystemEventHandler() {
		return townsystemEventHandler;
	}

	public SpawnersystemEventHandler getSpawnersystemEventHandler() {
		return spawnersystemEventHandler;
	}

	public EconomyPlayerEventHandler getEconomyPlayerEventHandler() {
		return economyPlayerEventHandler;
	}

	public JobsystemEventHandler getJobsystemEventHandler() {
		return jobsystemEventHandler;
	}

	public ShopEventHandler getShopEventHandler() {
		return shopEventHandler;
	}

	public Listener getUltimateEconomyEventHandler() {
		return ultimateEconomyEventHandler;
	}

	public ShopValidator getShopValidator() {
		return shopValidator;
	}

	public TownsystemValidator getTownsystemValidator() {
		return townsystemValidator;
	}

	public BankValidator getBankValidator() {
		return bankValidator;
	}

	public EconomyPlayerValidator getEconomyPlayerValidator() {
		return ecoPlayerValidator;
	}

	public JobsystemValidator getJobsystemValidator() {
		return jobsystemValidator;
	}

	public ConfigValidator getConfigValidator() {
		return configValidator;
	}

	public ConfigDao getConfigDao() {
		return configDao;
	}

	public BankDao getBankDao() {
		return bankDao;
	}

	public SpawnersystemDao getSpawnersystemDao() {
		return spawnersystemDao;
	}

	// #######################
	// Multitons
	// #######################

	public Adminshop createAdminshop() {
		return new AdminshopImpl(new ShopDaoImpl(serverProvider), serverProvider, customSkullService, adminshopManager,
				shopValidator, messageWrapper, configManager);
	}

	public Playershop createPlayershop() {
		return new PlayershopImpl(new ShopDaoImpl(serverProvider), serverProvider, customSkullService, shopValidator,
				ecoPlayerManager, messageWrapper, configManager, townworldManager, playershopManager);
	}

	public Rentshop createRentshop() {
		return new RentshopImpl(new ShopDaoImpl(serverProvider), serverProvider, customSkullService, shopValidator,
				ecoPlayerManager, messageWrapper, configManager, townworldManager, playershopManager);
	}

	public Jobcenter createJobcenter() {
		return new JobcenterImpl(new JobcenterDaoImpl(serverProvider), jobManager, jobcenterManager, ecoPlayerManager,
				jobsystemValidator, serverProvider, customSkullService);
	}

	public Townworld createTownWorld() {
		return new TownworldImpl(
				new TownworldDaoImpl(townsystemValidator, ecoPlayerManager, bankManager, serverProvider),
				townsystemValidator, townworldManager, messageWrapper, serverProvider);
	}

	public Town createTown(Townworld townworld, TownworldDao townworldDao) {
		return new TownImpl(townworldManager, bankManager, townsystemValidator, messageWrapper, townworldDao, townworld,
				serverProvider, customSkullService);
	}

	public Plot createPlot(TownworldDao townworldDao) {
		return new PlotImpl(townsystemValidator, townworldDao, serverProvider, customSkullService, ecoPlayerValidator);
	}

	public Job createJob() {
		return new JobImpl(jobsystemValidator, new JobDaoImpl(serverProvider));
	}

	public EconomyPlayer createEconomyPlayer() {
		return new EconomyPlayerImpl(serverProvider, ecoPlayerValidator, ecoPlayerDao, messageWrapper, configManager,
				bankManager, jobManager);
	}

	public <T extends GeneralEconomyException> InventoryGuiHandler createEconomyVillagerCustomizeHandler(
			EconomyVillager<T> ecoVillager, Type biomeType, Profession profession) {
		return new EconomyVillagerCustomizeHandlerImpl<T>(messageWrapper, serverProvider, customSkullService,
				ecoVillager, biomeType, profession);
	}

	public <T extends GeneralEconomyException> InventoryGuiHandler createRentshopGuiHandler(Rentshop shop) {
		return new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager, customSkullService, configManager, shop,
				serverProvider);
	}

	public ShopEditorHandler createShopEditorHandler() {
		return new ShopEditorHandlerImpl(serverProvider, customSkullService);
	}

	public ShopSlotEditorHandler createShopSlotEditorHandler(Inventory backLink) {
		return new ShopSlotEditorHandlerImpl(serverProvider, messageWrapper, shopValidator, customSkullService,
				backLink);
	}
}
