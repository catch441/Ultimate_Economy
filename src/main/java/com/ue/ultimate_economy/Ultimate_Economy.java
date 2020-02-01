package com.ue.ultimate_economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.bstats.metrics.Metrics;
import com.ue.config.api.ConfigController;
import com.ue.config.impl.ConfigCommandExecutor;
import com.ue.config.impl.ConfigTabCompleter;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.api.JobcenterController;
import com.ue.jobsystem.impl.JobCommandExecutor;
import com.ue.jobsystem.impl.JobTabCompleter;
import com.ue.language.MessageWrapper;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.player.impl.PlayerCommandExecutor;
import com.ue.player.impl.PlayerTabCompleter;
import com.ue.shopsystem.adminshop.api.AdminshopController;
import com.ue.shopsystem.adminshop.impl.AdminshopCommandExecutor;
import com.ue.shopsystem.adminshop.impl.AdminshopTabCompleterImpl;
import com.ue.shopsystem.playershop.api.PlayershopController;
import com.ue.shopsystem.playershop.impl.PlayershopCommandExecutor;
import com.ue.shopsystem.playershop.impl.PlayershopTabCompleter;
import com.ue.shopsystem.rentshop.api.RentshopController;
import com.ue.shopsystem.rentshop.impl.RentDailyTask;
import com.ue.shopsystem.rentshop.impl.RentshopCommandExecutor;
import com.ue.shopsystem.rentshop.impl.RentshopTabCompleter;
import com.ue.townsystem.town.impl.TownCommandExecutor;
import com.ue.townsystem.town.impl.TownTabCompleter;
import com.ue.townsystem.townworld.api.TownworldController;
import com.ue.townsystem.townworld.impl.TownworldCommandExecutor;
import com.ue.townsystem.townworld.impl.TownworldTabCompleter;
import com.ue.vault.Economy_UltimateEconomy;
import com.ue.vault.VaultHook;

/**
 * @author Lukas Heubach (catch441)
 */
public class Ultimate_Economy extends JavaPlugin {

	public static Ultimate_Economy getInstance;
	public Economy_UltimateEconomy economyImplementer;
	private VaultHook vaultHook;

	@Override
	public void onEnable() {
		loadPlugin();
		setupBstatsMetrics();
		setupVault();
	}

	@Override
	public void onDisable() {
		disablePlugin();
		disableVault();
	}

	private void disablePlugin() {
		JobcenterController.despawnAllVillagers();
		TownworldController.despawnAllVillagers();
		AdminshopController.despawnAllVillagers();
		PlayershopController.despawnAllVillagers();
		RentshopController.despawnAllVillagers();
	}

	private void setupBstatsMetrics() {
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);
	}

	private void disableVault() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			vaultHook.unhook();
		}
	}

	private void loadSpawners() {
		File spawner = new File(getDataFolder(), "SpawnerLocations.yml");
		if (!spawner.exists()) {
			try {
				spawner.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// spawn all spawners
		List<String> spawnerlist = new ArrayList<>();
		FileConfiguration spawnerconfig = YamlConfiguration.loadConfiguration(spawner);
		for (String spawnername : getConfig().getStringList("Spawnerlist")) {
			spawnerlist.add(spawnername);
			World world = getServer().getWorld(spawnerconfig.getString(spawnername + ".World"));
			Location location = new Location(world, spawnerconfig.getDouble(spawnername + ".X"),
					spawnerconfig.getDouble(spawnername + ".Y"), spawnerconfig.getDouble(spawnername + ".Z"));
			world.getBlockAt(location).setMetadata("name",
					new FixedMetadataValue(this, spawnerconfig.getString(spawnername + ".player")));
			world.getBlockAt(location).setMetadata("entity",
					new FixedMetadataValue(this, spawnerconfig.getString(spawnername + ".EntityType")));
		}
		getConfig().options().copyDefaults(true);
		saveConfig();
		// setup eventhandler
		getServer().getPluginManager().registerEvents(new Ultimate_EconomyEventHandler(this, spawnerlist, spawner),
				this);
	}

	private void setupVault() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			economyImplementer = new Economy_UltimateEconomy();
			vaultHook = new VaultHook();
			vaultHook.hook();
		}
	}

	private void loadCommands() {
		getCommand("jobcenter").setExecutor(new JobCommandExecutor(this));
		getCommand("jobcenter").setTabCompleter(new JobTabCompleter(getConfig()));
		getCommand("town").setExecutor(new TownCommandExecutor());
		getCommand("town").setTabCompleter(new TownTabCompleter());
		getCommand("townworld").setExecutor(new TownworldCommandExecutor(this));
		getCommand("townworld").setTabCompleter(new TownworldTabCompleter());
		getCommand("adminshop").setExecutor(new AdminshopCommandExecutor(this));
		getCommand("adminshop").setTabCompleter(new AdminshopTabCompleterImpl());
		getCommand("playershop").setTabCompleter(new PlayershopTabCompleter());
		getCommand("playershop").setExecutor(new PlayershopCommandExecutor(this));
		getCommand("rentshop").setExecutor(new RentshopCommandExecutor(this));
		getCommand("rentshop").setTabCompleter(new RentshopTabCompleter());
		PlayerCommandExecutor playerCommandExecutor = new PlayerCommandExecutor();
		PlayerTabCompleter playerTabCompleter = new PlayerTabCompleter();
		getCommand("bank").setExecutor(playerCommandExecutor);
		getCommand("bank").setTabCompleter(playerTabCompleter);
		if (ConfigController.isHomeSystem()) {
			try {
				Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
				commandMapField.setAccessible(true);
				CommandMap map = (CommandMap) commandMapField.get(Bukkit.getServer().getPluginManager());

				UltimateEconomyCommand home = new UltimateEconomyCommand("home", this);
				home.setDescription("Teleports you to a homepoint.");
				home.setPermission("ultimate_economy.home");
				home.setLabel("home");
				home.setPermissionMessage("You don't have the permission.");
				map.register("ultimate_economy", home);

				UltimateEconomyCommand setHome = new UltimateEconomyCommand("sethome", this);
				setHome.setDescription("Sets a homepoint.");
				setHome.setPermission("ultimate_economy.home");
				setHome.setLabel("sethome");
				setHome.setPermissionMessage("You don't have the permission.");
				setHome.setUsage("/<command> [home]");
				map.register("ultimate_economy", setHome);

				UltimateEconomyCommand delHome = new UltimateEconomyCommand("delhome", this);
				delHome.setDescription("Remove a homepoint.");
				delHome.setPermission("ultimate_economy.home");
				delHome.setLabel("delhome");
				delHome.setPermissionMessage("You don't have the permission.");
				delHome.setUsage("/<command> [home]");
				map.register("ultimate_economy", delHome);

				home.setExecutor(playerCommandExecutor);
				home.setTabCompleter(playerTabCompleter);
				delHome.setExecutor(playerCommandExecutor);
				delHome.setTabCompleter(playerTabCompleter);
				setHome.setExecutor(playerCommandExecutor);
			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
				Bukkit.getLogger().warning("[Ultimate_Economy] Error on enable homes feature.");
			}
		}
		getCommand("givemoney").setExecutor(playerCommandExecutor);
		getCommand("pay").setExecutor(playerCommandExecutor);
		getCommand("money").setExecutor(playerCommandExecutor);
		getCommand("myjobs").setExecutor(playerCommandExecutor);
		getCommand("ue-config").setExecutor(new ConfigCommandExecutor());
		getCommand("ue-config").setTabCompleter(new ConfigTabCompleter());
	}

	private void loadPlugin() {
		getInstance = this;
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		setupPlugin();
		MessageWrapper.loadLanguage();
		JobController.loadAllJobs(getDataFolder(), getConfig());
		JobcenterController.loadAllJobCenters(getServer(), getConfig(), getDataFolder());
		EconomyPlayerController.loadAllEconomyPlayers(getDataFolder());
		TownworldController.loadAllTownWorlds(getDataFolder(), getConfig());
		AdminshopController.loadAllAdminShops(getConfig(), getDataFolder());
		PlayershopController.loadAllPlayerShops(getConfig(), getDataFolder());
		RentshopController.loadAllRentShops(getConfig(), getDataFolder());

		loadCommands();
		loadSpawners();
	}

	private void setupPlugin() {
		ConfigController.setupConfig();
		// setup and start RentDailyTask
		new RentDailyTask().runTaskTimerAsynchronously(this, 1, 1000);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if (command.getName().equals("shop")) {
			if (args.length <= 1) {
				list = getAdminShopList(args[0]);
			}
		} else if (command.getName().equals("jobinfo")) {
			if (args.length <= 1) {
				list = getJobList(args[0]);
			}
		}
		return list;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName(player.getName());
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				// Commands
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				switch (label) {
					case "shop":
						if (args.length == 1) {
							if (ecoPlayer.hasJob(JobController.getJobByName(args[0]))) {
								AdminshopController.getAdminShopByName(args[0]).openInv(player);
							} else {
								player.sendMessage(MessageWrapper.getErrorString("job_not_joined"));
							}
						} else {
							return false;
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "shoplist":
						List<String> shopNames = AdminshopController.getAdminshopNameList();
						player.sendMessage(MessageWrapper.getString("shoplist_info", shopNames.toArray()));
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "joblist":
						List<String> jobNames = JobController.getJobNameList();
						player.sendMessage(MessageWrapper.getString("joblist_info", jobNames.toArray()));
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "jobinfo":
						if (args.length == 1) {
							Job job = JobController.getJobByName(args[0]);
							player.sendMessage(MessageWrapper.getString("jobinfo_info", job.getName()));
							for (String string : job.getItemList()) {
								player.sendMessage(ChatColor.GOLD + string.toLowerCase() + " " + ChatColor.GREEN
										+ job.getItemPrice(string) + ConfigController.getCurrencyText(job.getItemPrice(string)));
							}
							for (String string : job.getFisherList()) {
								player.sendMessage(MessageWrapper.getString("jobinfo_fishingprice",
										string.toLowerCase(), job.getFisherPrice(string),
										ConfigController.getCurrencyText(job.getFisherPrice(string))));
							}
							for (String string : job.getEntityList()) {
								player.sendMessage(MessageWrapper.getString("jobinfo_killprice", string.toLowerCase(),
										job.getKillPrice(string), ConfigController.getCurrencyText(job.getKillPrice(string))));
							}
						} else {
							return false;
						}
						break;
				}
			} catch (PlayerException | ShopSystemException | JobSystemException e1) {
				player.sendMessage(e1.getMessage());
			}
		}
		return true;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Methoden
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List<String> getJobList(String arg) {
		List<String> temp = JobController.getJobNameList();
		List<String> list = new ArrayList<>();
		if (arg.equals("")) {
			list = temp;
		} else {
			for (String jobname : temp) {
				if (jobname.contains(arg)) {
					list.add(jobname);
				}
			}
		}
		return list;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List<String> getAdminShopList(String arg) {
		List<String> temp = getConfig().getStringList("ShopNames");
		List<String> list = new ArrayList<>();
		if (arg.equals("")) {
			list = temp;
		} else {
			for (String shopName : temp) {
				if (shopName.contains(arg)) {
					list.add(shopName);
				}
			}
		}
		return list;
	}
}