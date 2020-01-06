package com.ue.townsystem.town.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.TownSystemException;
import com.ue.player.EconomyPlayer;
import com.ue.townsystem.town.impl.TownImpl;
import com.ue.townsystem.townworld.api.Townworld;

public class TownController {
	
	private static List<String> townNameList = new ArrayList<>();
	
	/**
	 * Creates a new town if player has enough money. Player money decreases if
	 * player has enough money.
	 * 
	 * @param townworld
	 * @param townName
	 * @param location
	 * @param owner
	 * @throws TownSystemException
	 * @throws PlayerException
	 */
	public static void createTown(Townworld townworld, String townName, Location location, EconomyPlayer owner)
			throws TownSystemException, PlayerException {
		if (townNameList.contains(townName)) {
			throw new TownSystemException(TownSystemException.TOWN_ALREADY_EXIST);
		} else if (!townworld.chunkIsFree(location.getChunk())) {
			throw new TownSystemException(TownSystemException.CHUNK_ALREADY_CLAIMED);
		} else if (!owner.hasEnoughtMoney(townworld.getFoundationPrice())) {
			throw new PlayerException(PlayerException.NOT_ENOUGH_MONEY_PERSONAL);
		} else if (owner.reachedMaxJoinedTowns()) {
			throw new PlayerException(PlayerException.MAX_JOINED_TOWNS);
		} else {
			TownImpl townImpl = new TownImpl(townworld,owner.getName(), townName, location,false);
			townworld.addTown(townImpl);;
			FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
			townNameList.add(townName);
			config.set("TownNames", townNameList);
			save(townworld.getSaveFile(), config);
			owner.addJoinedTown(townName);
			owner.decreasePlayerAmount(townworld.getFoundationPrice(), true);
		}
	}
	
	/**
	 * Dissolves a entire town. The Chunks are not resettet.
	 * 
	 * @param townname
	 * @param playername
	 * @throws TownSystemException
	 * @throws PlayerException
	 */
	public static void dissolveTown(Town town, String playername) throws TownSystemException, PlayerException {
		if (town.isTownOwner(playername)) {
			List<String> tList = new ArrayList<>();
			tList.addAll(town.getCitizens());
			for (String citizen : tList) {
				EconomyPlayer.getEconomyPlayerByName(citizen).removeJoinedTown(town.getTownName());
			}
			town.despawnAllVillagers();
			town.getTownworld().removeTown(town);
			townNameList.remove(town.getTownName());
			FileConfiguration config = YamlConfiguration.loadConfiguration(town.getTownworld().getSaveFile());
			config.set("Towns." + town.getTownName(), null);
			config.set("TownNames", townNameList);
			save(town.getTownworld().getSaveFile(),config);
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_HAS_NO_PERMISSION);
		}
	}

	/**
	 * Static method for loading a existing town by name.
	 * 
	 * @param file
	 * @param townName
	 * @return Town
	 * @throws TownSystemException
	 * @throws PlayerException 
	 */
	public static TownImpl loadTown(Townworld townworld, Server server, String townName) throws TownSystemException, PlayerException {
		FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
		Location location = new Location(
				server.getWorld(config.getString("Towns." + townName + ".TownManagerVillager.world")),
				config.getDouble("Towns." + townName + ".TownManagerVillager.x"),
				config.getDouble("Towns." + townName + ".TownManagerVillager.y"),
				config.getDouble("Towns." + townName + ".TownManagerVillager.z"));
		TownImpl townImpl = new TownImpl(townworld,config.getString("Towns." + townName + ".owner"), townName, location,true);
		townImpl.setCoOwners(config.getStringList("Towns." + townName + ".coOwners"));
		townImpl.setCitizens(config.getStringList("Towns." + townName + ".citizens"));
		townImpl.setChunkList(config.getStringList("Towns." + townName + ".chunks"));
		townImpl.setTax(config.getDouble("Towns." + townName + ".tax"));
		townImpl.setTownBankAmount(config.getDouble("Towns." + townName + ".bank"));
		String locationString = config.getString("Towns." + townName + ".townspawn");
		townImpl.setTownSpawn(new Location(server.getWorld(config.getString("World")),
				Double.valueOf(locationString.substring(0, locationString.indexOf("/"))),
				Double.valueOf(
						locationString.substring(locationString.indexOf("/") + 1, locationString.lastIndexOf("/"))),
				Double.valueOf(locationString.substring(locationString.lastIndexOf("/") + 1))));
		ArrayList<Plot> plotList = new ArrayList<>();
		for (String coords : townImpl.getChunkList()) {
			Plot plot = PlotController.loadPlot(townImpl, coords);
			plotList.add(plot);
		}
		townImpl.setPlotList(plotList);
		townNameList.add(townName);
		return townImpl;
	}

	/**
	 * This method returns the townNameList.
	 * 
	 * @return List of Strings
	 */
	public static List<String> getTownNameList() {
		return townNameList;
	}
	
	/**
	 * Sets and saves the townName list. Do not use this if you create and load towns woth the TownController.
	 * 
	 * @param townworld
	 * @param townNames
	 */
	public static void setAndSaveTownNameList(Townworld townworld,List<String> townNames) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
		townNameList = townNames;
		config.set("TownNames", townNameList);
		save(townworld.getSaveFile(),config);
	}
	
	/**
	 * Saves a config in a file.
	 * 
	 * @param config
	 */
	private static void save(File file, FileConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
