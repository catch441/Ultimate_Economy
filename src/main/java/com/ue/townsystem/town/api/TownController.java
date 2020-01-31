package com.ue.townsystem.town.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
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
	 * @param owner the player who wants to be the mayor of the town
	 * @throws TownSystemException
	 * @throws PlayerException
	 */
	public static void createTown(Townworld townworld, String townName, Location location, EconomyPlayer player)
			throws TownSystemException, PlayerException {
		if (townNameList.contains(townName)) {
			throw TownSystemException.getException(TownExceptionMessageEnum.TOWN_ALREADY_EXIST);
		} else if (!townworld.chunkIsFree(location.getChunk())) {
			throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_ALREADY_CLAIMED);
		} else if (!player.hasEnoughtMoney(townworld.getFoundationPrice())) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.NOT_ENOUGH_MONEY_PERSONAL);
		} else if (player.reachedMaxJoinedTowns()) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.MAX_REACHED);
		} else {
			TownImpl townImpl = new TownImpl(townworld,player, townName, location,false);
			townworld.addTown(townImpl);;
			FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
			townNameList.add(townName);
			config.set("TownNames", townNameList);
			save(townworld.getSaveFile(), config);
			player.addJoinedTown(townName);
			player.decreasePlayerAmount(townworld.getFoundationPrice(), true);
		}
	}
	
	/**
	 * Dissolves a entire town. The Chunks are not resettet.
	 * 
	 * @param town
	 * @throws TownSystemException
	 * @throws PlayerException
	 */
	public static void dissolveTown(Town town, EconomyPlayer player) throws TownSystemException, PlayerException {
		if (town.isMayor(player)) {
			List<EconomyPlayer> tList = new ArrayList<>();
			tList.addAll(town.getCitizens());
			for (EconomyPlayer citizen : tList) {
				citizen.removeJoinedTown(town.getTownName());
			}
			town.despawnAllVillagers();
			town.getTownworld().removeTown(town);
			townNameList.remove(town.getTownName());
			FileConfiguration config = YamlConfiguration.loadConfiguration(town.getTownworld().getSaveFile());
			config.set("Towns." + town.getTownName(), null);
			config.set("TownNames", townNameList);
			save(town.getTownworld().getSaveFile(),config);
		} else {
			throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
		}
	}

	/**
	 * Static method for loading a existing town by name.
	 * EconomyPlayers have to be loaded first.
	 * 
	 * @param townworld
	 * @param townName
	 * @return Town
	 * @throws TownSystemException
	 * @throws PlayerException 
	 */
	public static Town loadTown(Townworld townworld, String townName) throws TownSystemException, PlayerException {
		FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
		World world = Bukkit.getWorld(config.getString("World"));
		if(world == null) {
			throw TownSystemException.getException(TownExceptionMessageEnum.WORLD_DOES_NOT_EXIST, config.getString("World"));
		}
		Location location = new Location(
				world,
				config.getDouble("Towns." + townName + ".TownManagerVillager.x"),
				config.getDouble("Towns." + townName + ".TownManagerVillager.y"),
				config.getDouble("Towns." + townName + ".TownManagerVillager.z"));
		EconomyPlayer mayor = EconomyPlayerController.getEconomyPlayerByName(config.getString("Towns." + townName + ".owner"));
		TownImpl townImpl = new TownImpl(townworld,mayor, townName, location,true);
		townImpl.setMayor(mayor);
		List<EconomyPlayer> deputys = new ArrayList<>();
		for(String name:config.getStringList("Towns." + townName + ".coOwners")) {
			deputys.add(EconomyPlayerController.getEconomyPlayerByName(name));
		}
		townImpl.setDeputys(deputys);
		List<EconomyPlayer> citizens = new ArrayList<>();
		for(String name:config.getStringList("Towns." + townName + ".citizens")) {
			citizens.add(EconomyPlayerController.getEconomyPlayerByName(name));
		}
		townImpl.setCitizens(citizens);
		townImpl.setChunkList(config.getStringList("Towns." + townName + ".chunks"));
		townImpl.setTax(config.getDouble("Towns." + townName + ".tax"));
		townImpl.setTownBankAmount(config.getDouble("Towns." + townName + ".bank"));
		String locationString = config.getString("Towns." + townName + ".townspawn");
		townImpl.setTownSpawn(new Location(world,
				Double.valueOf(locationString.substring(0, locationString.indexOf("/"))),
				Double.valueOf(
						locationString.substring(locationString.indexOf("/") + 1, locationString.lastIndexOf("/"))),
				Double.valueOf(locationString.substring(locationString.lastIndexOf("/") + 1))),mayor,false);
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
