package com.ue.townsystem.dataaccess.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.common.utils.SaveFileUtils;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.exceptions.TownSystemException;
import com.ue.townsystem.dataaccess.api.TownsystemDao;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;

public class TownsystemDaoImpl extends SaveFileUtils implements TownsystemDao {

	private File file;
	private YamlConfiguration config;
	private final TownsystemValidationHandler validationHandler;
	private final EconomyPlayerManager ecoPlayerManager;

	/**
	 * Inject constructor.
	 * 
	 * @param ecoPlayerManager
	 * @param validationHandler
	 */
	@Inject
	public TownsystemDaoImpl(EconomyPlayerManager ecoPlayerManager, TownsystemValidationHandler validationHandler) {
		this.validationHandler = validationHandler;
		this.ecoPlayerManager = ecoPlayerManager;
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
}
