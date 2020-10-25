package com.ue.economyplayer.logic.api;

import java.util.List;

import com.ue.general.impl.GeneralEconomyException;

public interface EconomyPlayerManager {

	/**
	 * This method returns a list of all player names.
	 * 
	 * @return list of player names
	 */
	public List<String> getEconomyPlayerNameList();
	
	/**
	 * This method returns a economyplayer by it's name.
	 * 
	 * @param name
	 * @return EconomyPlayer
	 * @throws GeneralEconomyException
	 */
	public EconomyPlayer getEconomyPlayerByName(String name) throws GeneralEconomyException;
	
	/**
	 * This method returns all economyPlayers.
	 * 
	 * @return List of EcnomyPlayers
	 */
	public List<EconomyPlayer> getAllEconomyPlayers();
	
	/**
	 * This method should be used to create a new EconomyPlayer.
	 * 
	 * @param playerName
	 * @throws GeneralEconomyException
	 */
	public void createEconomyPlayer(String playerName) throws GeneralEconomyException;
	
	/**
	 * Deletes an economy player.
	 * 
	 * @param player
	 */
	public void deleteEconomyPlayer(EconomyPlayer player);
	
	/**
	 * This method loads all economyPlayers. !!! The jobs have to be loaded first.
	 * The bank accounts have to be loaded first too.
	 * 
	 */
	public void loadAllEconomyPlayers();
}
