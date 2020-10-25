package com.ue.townsystem.logic.api;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.townsystem.logic.impl.TownSystemException;

public interface Townworld {

	/**
	 * Returns the townlist.
	 * 
	 * @return List
	 */
	public List<Town> getTownList();

	/**
	 * Founds a new town in this townworld, if player has enough money. Player money
	 * decreases if player has enough money.
	 * 
	 * @param townName
	 * @param location
	 * @param player
	 * @throws GeneralEconomyException
	 * @throws EconomyPlayerException
	 * @throws TownSystemException
	 */
	public void foundTown(String townName, Location location, EconomyPlayer player)
			throws GeneralEconomyException, EconomyPlayerException, TownSystemException;

	/**
	 * Dissolves a entire town. The Chunks are not resettet.
	 * 
	 * @param ecoPlayer
	 * @param town
	 * @throws GeneralEconomyException
	 * @throws EconomyPlayerException
	 * @throws TownSystemException
	 */
	public void dissolveTown(EconomyPlayer ecoPlayer, Town town)
			throws GeneralEconomyException, TownSystemException, EconomyPlayerException;

	/**
	 * Returns town by chunk.
	 * 
	 * @param chunk
	 * @return Town
	 * @throws TownSystemException
	 */
	public Town getTownByChunk(Chunk chunk) throws TownSystemException;

	/**
	 * Returns the Town World name.
	 * 
	 * @return String
	 */
	public String getWorldName();

	/**
	 * Returns a town by townname.
	 * 
	 * @param townName
	 * @return Town
	 * @throws GeneralEconomyException
	 */
	public Town getTownByName(String townName) throws GeneralEconomyException;

	/**
	 * Returns a list of all townnames in this townworld.
	 * 
	 * @return List
	 */
	public List<String> getTownNameList();

	/**
	 * Set the FoundationPrice for a town in this townworld.
	 * 
	 * @param foundationPrice
	 * @throws GeneralEconomyException 
	 */
	public void setFoundationPrice(double foundationPrice) throws GeneralEconomyException;

	/**
	 * Returns the founding price of this townworld.
	 * 
	 * @return double
	 */
	public double getFoundationPrice();

	/**
	 * Set the ExpandPrice for a town in this townworld.
	 * 
	 * @param expandPrice
	 * @throws GeneralEconomyException 
	 */
	public void setExpandPrice(double expandPrice) throws GeneralEconomyException;

	/**
	 * Returns the expand price for a town in this townworld.
	 * 
	 * @return double
	 */
	public double getExpandPrice();

	/**
	 * Despawns all town villagers in this townworld.
	 * 
	 */
	public void despawnAllTownVillagers();

	/**
	 * Returns true if the chunk is not claimed by any town.
	 * 
	 * @param chunk
	 * @return boolean
	 */
	public boolean isChunkFree(Chunk chunk);

	/**
	 * Delets all save files and towns.
	 * <p>
	 * 
	 * @throws GeneralEconomyException
	 * @throws EconomyPlayerException
	 * @throws TownSystemException
	 */
	public void delete() throws TownSystemException, EconomyPlayerException, GeneralEconomyException;
}
