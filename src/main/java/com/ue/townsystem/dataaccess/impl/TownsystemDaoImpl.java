package com.ue.townsystem.dataaccess.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.SaveFileUtils;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.exceptions.TownSystemException;
import com.ue.townsystem.dataaccess.api.TownsystemDao;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.ultimate_economy.UltimateEconomy;

public class TownsystemDaoImpl extends SaveFileUtils implements TownsystemDao {

	private File file;
	private YamlConfiguration config;
	@Inject
	TownsystemValidationHandler validationHandler;
	@Inject
	EconomyPlayerManager ecoPlayerManager;
	@Inject
	BankManager bankManager;

	/**
	 * Default constructor.
	 * 
	 * @param file
	 */
	public TownsystemDaoImpl(String world) {
		file = new File(UltimateEconomy.getInstance.getDataFolder(), world + "_TownWorld" + ".yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to create savefile");
				Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
		config = YamlConfiguration.loadConfiguration(file);
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
		config.set("Towns." + townName + ".townspawn",
				location.getX() + "/" + location.getY() + "/" + location.getZ());
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
		config.set("Town." + townName + ".Plots." + chunkCoords, null);
		save(config, file);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void saveRenameTown(String oldName, String newName) {				
		Map<String, Object> vals = config.getConfigurationSection("Towns." + oldName).getValues(true);
	    for (String s : vals.keySet()){
	        Object val = vals.get(s);
	        if (val instanceof List) {
	        	val = new ArrayList<Object>((List<Object>)val);
	        }
	        config.set("Towns." + newName + s, val);
	    }
	    save(config, file);
	}
	
	@Override
	public void saveTownBankIban(String townName, String iban) {
		config.set("Towns." + townName + ".Iban", iban);
		save(config, file);
	}

	@Override
	public Location loadPlotVillagerLocation(String townName, String chunkCoords) throws TownSystemException {
		double x = config.getDouble("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.x");
		double y = config.getDouble("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.y");
		double z = config.getDouble("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.z");
		String world = config.getString("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.world");
		validationHandler.checkForWorldExists(world);
		return new Location(Bukkit.getWorld(world), x, y, z);
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
				Bukkit.getLogger().warning(e.getMessage());
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
	public List<EconomyPlayer> loadDeputies(String townName) throws EconomyPlayerException {
		List<EconomyPlayer> deputys = new ArrayList<>();
		for (String name : config.getStringList("Towns." + townName + ".coOwners")) {
			deputys.add(ecoPlayerManager.getEconomyPlayerByName(name));
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
		return new Location(Bukkit.getWorld(world), Double.valueOf(locationString.substring(0, locationString.indexOf("/"))),
				Double.valueOf(
						locationString.substring(locationString.indexOf("/") + 1, locationString.lastIndexOf("/"))),
				Double.valueOf(locationString.substring(locationString.lastIndexOf("/") + 1)));
	}
	
	@Override
	public List<EconomyPlayer> loadCitizens(String townName) throws EconomyPlayerException {
		List<EconomyPlayer> citizens = new ArrayList<>();
		for (String name : config.getStringList("Towns." + townName + ".citizens")) {
			citizens.add(ecoPlayerManager.getEconomyPlayerByName(name));
		}
		return citizens;
	}
	
	@Override
	public Location loadTownManagerLocation(String townName) throws TownSystemException {
		String world = config.getString("World");
		validationHandler.checkForWorldExists(config.getString("World"));
		return new Location(Bukkit.getWorld(world), config.getDouble("Towns." + townName + ".TownManagerVillager.x"),
				config.getDouble("Towns." + townName + ".TownManagerVillager.y"),
				config.getDouble("Towns." + townName + ".TownManagerVillager.z"));
	}
	
	@Override
	public String loadTownBankIban(String townName) {
		convertToBankAccount(townName);
		return config.getString("Towns." + townName + ".Iban");
	}

	/**
	 * 
	 * @param townName
	 * @since 1.2.6
	 * @deprecated
	 */
	@Deprecated
	private void convertToBankAccount(String townName) {
		if(!config.contains("Towns." + townName + ".Iban")) {
			double startAmount = config.getDouble("Towns." + townName + ".bank");
			BankAccount account = bankManager.createBankAccount(startAmount);
			saveTownBankIban(townName, account.getIban());
		}
	}
	
	/**
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
