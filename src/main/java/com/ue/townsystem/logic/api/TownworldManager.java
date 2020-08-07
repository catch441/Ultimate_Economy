package com.ue.townsystem.logic.api;

import java.util.List;

import org.bukkit.Chunk;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.exceptions.TownSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;

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
	public void createTownWorld(String world) throws TownSystemException;

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
}
