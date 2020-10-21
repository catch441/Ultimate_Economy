package com.ue.townsystem.logic.api;

import java.util.List;

import org.bukkit.Chunk;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.townsystem.logic.impl.TownSystemException;

public interface TownworldManager {

	/**
	 * Returns a townworld by it's name.
	 * 
	 * @param name
	 * @return Townworld
	 * @throws TownSystemException
	 */
	public Townworld getTownWorldByName(String name) throws TownSystemException;

	/**
	 * Returns a town by townname.
	 * 
	 * @param townName
	 * @return Town
	 * @throws GeneralEconomyException
	 */
	public Town getTownByName(String townName) throws GeneralEconomyException;

	/**
	 * This method returns a list of all townworlds.
	 * 
	 * @return List of TownWorlds
	 */
	public List<Townworld> getTownWorldList();

	/**
	 * Despawns all town villager from all townworlds.
	 */
	public void despawnAllVillagers();

	/**
	 * Returns a list of all townworld names.
	 * 
	 * @return list of strings
	 */
	public List<String> getTownWorldNameList();

	/**
	 * Returns true, if the world is an townworld.
	 * 
	 * @param worldName
	 * @return boolean
	 */
	public boolean isTownWorld(String worldName);

	/**
	 * This method should be used to create/enble a new townworld.
	 * 
	 * @param world
	 * @throws TownSystemException
	 */
	public void createTownWorld(String world)
			throws TownSystemException;

	/**
	 * This method should be used to delete/disable a townworld.
	 * 
	 * @param world
	 * @throws TownSystemException
	 * @throws EconomyPlayerException
	 * @throws GeneralEconomyException
	 */
	public void deleteTownWorld(String world)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException;

	/**
	 * This method loads all townworlds from the save file. Loads all towns and
	 * plots in the townworld as well. EconomyPlayers have to be loaded first.
	 * 
	 */
	public void loadAllTownWorlds();

	/**
	 * Handles the townworld location check.
	 * 
	 * @param worldname
	 * @param chunk
	 * @param ecoPlayer
	 */
	public void performTownWorldLocationCheck(String worldname, Chunk chunk, EconomyPlayer ecoPlayer);

	/**
	 * Returns the town name list.
	 * 
	 * @return town names
	 */
	public List<String> getTownNameList();

	/**
	 * Performs the townworld location check for all online players.
	 * 
	 * @throws GeneralEconomyException
	 */
	public void performTownworldLocationCheckAllPlayers() throws GeneralEconomyException;
}
