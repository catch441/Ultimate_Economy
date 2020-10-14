package com.ue.townsystem.dataaccess.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.SaveFileUtils;
import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.townsystem.dataaccess.api.TownworldDao;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.impl.TownSystemException;

public class TownworldDaoImpl extends SaveFileUtils implements TownworldDao {

	private final TownsystemValidationHandler validationHandler;
	private final EconomyPlayerManager ecoPlayerManager;
	private final BankManager bankManager;
	private final ServerProvider serverProvider;

	/**
	 * Inject constructor.
	 * 
	 * @param serverProvider
	 * @param logger
	 * @param validationHandler
	 * @param ecoPlayerManager
	 * @param bankManager
	 */
	@Inject
	public TownworldDaoImpl(ServerProvider serverProvider, Logger logger, TownsystemValidationHandler validationHandler,
			EconomyPlayerManager ecoPlayerManager, BankManager bankManager) {
		super(logger);
		this.validationHandler = validationHandler;
		this.ecoPlayerManager = ecoPlayerManager;
		this.bankManager = bankManager;
		this.serverProvider = serverProvider;
	}

	@Override
	public void setupSavefile(String name) {
		file = new File(serverProvider.getDataFolderPath(), name + "_TownWorld.yml");
		if (!file.exists()) {
			createFile(file);
		}
		config = YamlConfiguration.loadConfiguration(file);
	}

	@Override
	public void deleteSavefile() {
		file.delete();
	}

	@Override
	public void savePlotVillagerLocation(String townName, String chunkCoords, Location location) {
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.x", location.getX());
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.y", location.getY());
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.z", location.getZ());
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.world",
				location.getWorld().getName());
		save(config, file);
	}

	@Override
	public void savePlotOwner(String townName, String chunkCoords, EconomyPlayer player) {
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".owner", player.getName());
		save(config, file);
	}

	@Override
	public void savePlotResidents(String townName, String chunkCoords, List<EconomyPlayer> residents) {
		List<String> list = new ArrayList<>();
		for (EconomyPlayer ecoPlayer : residents) {
			list.add(ecoPlayer.getName());
		}
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".coOwners", list);
		save(config, file);
	}

	@Override
	public void savePlotIsForSale(String townName, String chunkCoords, boolean isForSale) {
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".isForSale", isForSale);
		save(config, file);
	}

	@Override
	public void savePlotSalePrice(String townName, String chunkCoords, double salePrice) {
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".salePrice", salePrice);
		save(config, file);
	}

	@Override
	public void saveTownManagerLocation(String townName, Location location) {
		config.set("Towns." + townName + ".TownManagerVillager.x", location.getX());
		config.set("Towns." + townName + ".TownManagerVillager.y", location.getY());
		config.set("Towns." + townName + ".TownManagerVillager.z", location.getZ());
		config.set("Towns." + townName + ".TownManagerVillager.world", location.getWorld().getName());
		save(config, file);
	}

	@Override
	public void saveTownSpawn(String townName, Location location) {
		config.set("Towns." + townName + ".townspawn", location.getX() + "/" + location.getY() + "/" + location.getZ());
		save(config, file);
	}

	@Override
	public void saveDeputies(String townName, List<EconomyPlayer> deputies) {
		List<String> list = new ArrayList<>();
		for (EconomyPlayer economyPlayer : deputies) {
			list.add(economyPlayer.getName());
		}
		config.set("Towns." + townName + ".coOwners", list);
		save(config, file);
	}

	@Override
	public void saveCitizens(String townName, List<EconomyPlayer> citizens) {
		List<String> list = new ArrayList<>();
		for (EconomyPlayer economyPlayer : citizens) {
			list.add(economyPlayer.getName());
		}
		config.set("Towns." + townName + ".citizens", list);
		save(config, file);
	}

	@Override
	public void saveTax(String townName, double tax) {
		config.set("Towns." + townName + ".tax", tax);
		save(config, file);
	}

	@Override
	public void saveMayor(String townName, EconomyPlayer player) {
		config.set("Towns." + townName + ".owner", player.getName());
		save(config, file);
	}

	@Override
	public void saveRemovePlot(String townName, String chunkCoords) {
		config.set("Towns." + townName + ".Plots." + chunkCoords, null);
		save(config, file);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveRenameTown(String oldName, String newName) {
		Map<String, Object> vals = config.getConfigurationSection("Towns." + oldName).getValues(true);
		for (String s : vals.keySet()) {
			Object val = vals.get(s);
			if (val instanceof List) {
				val = new ArrayList<Object>((List<Object>) val);
			}
			config.set("Towns." + newName + "." + s, val);
		}
		config.set("Towns." + oldName, null);
		save(config, file);
	}

	@Override
	public void saveTownBankIban(String townName, String iban) {
		config.set("Towns." + townName + ".Iban", iban);
		save(config, file);
	}

	@Override
	public void saveFoundationPrice(double foundationPrice) {
		config.set("Config.foundationPrice", foundationPrice);
		save(config, file);
	}

	@Override
	public void saveExpandPrice(double expandPrice) {
		config.set("Config.expandPrice", expandPrice);
		save(config, file);
	}

	@Override
	public void saveWorldName(String world) {
		config.set("World", world);
		save(config, file);
	}

	@Override
	public Location loadPlotVillagerLocation(String townName, String chunkCoords) throws TownSystemException {
		double x = config.getDouble("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.x");
		double y = config.getDouble("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.y");
		double z = config.getDouble("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.z");
		String world = config.getString("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.world");
		validationHandler.checkForWorldExists(world);
		return new Location(serverProvider.getWorld(world), x, y, z);
	}

	@Override
	public EconomyPlayer loadPlotOwner(String townName, String chunkCoords) throws EconomyPlayerException {
		String playerName = config.getString("Towns." + townName + ".Plots." + chunkCoords + ".owner");
		return ecoPlayerManager.getEconomyPlayerByName(playerName);
	}

	@Override
	public List<EconomyPlayer> loadResidents(String townName, String chunkCoords) {
		List<String> playerNames = config.getStringList("Towns." + townName + ".Plots." + chunkCoords + ".coOwners");
		List<EconomyPlayer> ecoPlayers = new ArrayList<>();
		for (String name : playerNames) {
			try {
				ecoPlayers.add(ecoPlayerManager.getEconomyPlayerByName(name));
			} catch (EconomyPlayerException e) {
				logger.warn("[Ultimate_Economy] Failed to load resident " + name + " of town " + townName + " and plot "
						+ chunkCoords);
				logger.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
		return ecoPlayers;
	}

	@Override
	public boolean loadPlotIsForSale(String townName, String chunkCoords) {
		return config.getBoolean("Towns." + townName + ".Plots." + chunkCoords + ".isForSale");
	}

	@Override
	public double loadPlotSalePrice(String townName, String chunkCoords) {
		return config.getDouble("Towns." + townName + ".Plots." + chunkCoords + ".salePrice");
	}

	@Override
	public List<String> loadTownPlotCoords(String townName) {
		removeDeprecatedChunkCoordList(townName);
		return new ArrayList<>(config.getConfigurationSection("Towns." + townName + ".Plots").getKeys(false));
	}

	@Override
	public List<String> loadTownworldTownNames() {
		removeDeprecatedTownNameList();
		return new ArrayList<>(config.getConfigurationSection("Towns").getKeys(false));
	}

	@Override
	public EconomyPlayer loadMayor(String townName) throws EconomyPlayerException {
		return ecoPlayerManager.getEconomyPlayerByName(config.getString("Towns." + townName + ".owner"));
	}

	@Override
	public List<EconomyPlayer> loadDeputies(String townName) {
		List<EconomyPlayer> deputys = new ArrayList<>();
		for (String name : config.getStringList("Towns." + townName + ".coOwners")) {
			try {
				deputys.add(ecoPlayerManager.getEconomyPlayerByName(name));
			} catch (EconomyPlayerException e) {
				logger.warn("[Ultimate_Economy] Failed to load deputy " + name + " of town " + townName);
				logger.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
		return deputys;

	}

	@Override
	public double loadTax(String townName) {
		return config.getDouble("Towns." + townName + ".tax");
	}

	@Override
	public Location loadTownSpawn(String townName) throws TownSystemException, NumberFormatException {
		String world = config.getString("World");
		validationHandler.checkForWorldExists(world);
		String locationString = config.getString("Towns." + townName + ".townspawn");
		return new Location(serverProvider.getWorld(world),
				Double.valueOf(locationString.substring(0, locationString.indexOf("/"))),
				Double.valueOf(
						locationString.substring(locationString.indexOf("/") + 1, locationString.lastIndexOf("/"))),
				Double.valueOf(locationString.substring(locationString.lastIndexOf("/") + 1)));
	}

	@Override
	public List<EconomyPlayer> loadCitizens(String townName) {
		List<EconomyPlayer> citizens = new ArrayList<>();
		for (String name : config.getStringList("Towns." + townName + ".citizens")) {
			try {
				citizens.add(ecoPlayerManager.getEconomyPlayerByName(name));
			} catch (EconomyPlayerException e) {
				logger.warn("[Ultimate_Economy] Failed to load citizen " + name + " of town " + townName);
				logger.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
		return citizens;
	}

	@Override
	public Location loadTownManagerLocation(String townName) throws TownSystemException {
		String world = config.getString("World");
		validationHandler.checkForWorldExists(config.getString("World"));
		return new Location(serverProvider.getWorld(world),
				config.getDouble("Towns." + townName + ".TownManagerVillager.x"),
				config.getDouble("Towns." + townName + ".TownManagerVillager.y"),
				config.getDouble("Towns." + townName + ".TownManagerVillager.z"));
	}

	@Override
	public double loadFoundationPrice() {
		return config.getDouble("Config.foundationPrice");
	}

	@Override
	public double loadExpandPrice() {
		return config.getDouble("Config.expandPrice");
	}

	@Override
	public String loadTownBankIban(String townName) {
		convertToBankAccount(townName);
		return config.getString("Towns." + townName + ".Iban");
	}

	/**
	 * Convert to bank account.
	 * 
	 * @param townName
	 * @since 1.2.6
	 * @deprecated
	 */
	@Deprecated
	private void convertToBankAccount(String townName) {
		if (!config.contains("Towns." + townName + ".Iban")) {
			double startAmount = config.getDouble("Towns." + townName + ".bank");
			config.set("Towns." + townName + ".bank", null);
			BankAccount account = bankManager.createBankAccount(startAmount);
			saveTownBankIban(townName, account.getIban());
		}
	}

	/**
	 * Remove deprecated chunk coord list.
	 * 
	 * @param townName
	 * @since 1.2.6
	 * @deprecated
	 */
	@Deprecated
	private void removeDeprecatedChunkCoordList(String townName) {
		config.set("Towns." + townName + ".chunks", null);
		save(config, file);
	}

	/**
	 * Remove deprecated townnNameList.
	 * 
	 * @param townName
	 * @since 1.2.6
	 * @deprecated
	 */
	@Deprecated
	private void removeDeprecatedTownNameList() {
		config.set("TownNames", null);
		save(config, file);
	}
}