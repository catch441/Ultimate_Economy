package com.ue.townsystem.dataaccess.api;

import java.util.List;

import org.bukkit.Location;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.exceptions.TownSystemException;

public interface TownsystemDao {

	/**
	 * Saves the plot villager location.
	 * 
	 * @param townName
	 * @param chunkCoords
	 * @param location
	 */
	public void savePlotVillagerLocation(String townName, String chunkCoords, Location location);

	/**
	 * Saves the owner of the plot.
	 * 
	 * @param townName
	 * @param chunkCoords
	 * @param player
	 */
	public void savePlotOwner(String townName, String chunkCoords, EconomyPlayer player);

	/**
	 * Saves the plot residents.
	 * 
	 * @param townName
	 * @param chunkCoords
	 * @param residents
	 */
	public void savePlotResidents(String townName, String chunkCoords, List<EconomyPlayer> residents);

	/**
	 * Saves the plot is for sale value.
	 * 
	 * @param townName
	 * @param chunkCoords
	 * @param isForSale
	 */
	public void savePlotIsForSale(String townName, String chunkCoords, boolean isForSale);

	/**
	 * Saves the plot sale price.
	 * 
	 * @param townName
	 * @param chunkCoords
	 * @param salePrice
	 */
	public void savePlotSalePrice(String townName, String chunkCoords, double salePrice);

	/**
	 * Loads the plot villager location.
	 * 
	 * @param townName
	 * @param chunkCoords
	 * @return location
	 * @throws TownSystemException
	 */
	public Location loadPlotVillagerLocation(String townName, String chunkCoords) throws TownSystemException;

	/**
	 * Loads owner.
	 * 
	 * @param townName
	 * @param chunkCoords
	 * @return economy player
	 * @throws EconomyPlayerException
	 */
	public EconomyPlayer loadPlotOwner(String townName, String chunkCoords) throws EconomyPlayerException;

	/**
	 * Loads the plot sale price.
	 * @param townName
	 * @param chunkCoords
	 * @return plot sale price
	 */
	public double loadPlotSalePrice(String townName, String chunkCoords);
	
	/**
	 * Load the plot is for sale value.
	 * @param townName
	 * @param chunkCoords
	 * @return isForSale
	 */
	public boolean loadPlotIsForSale(String townName, String chunkCoords);

	/**
	 * Load all residents of a plot.
	 * @param townName
	 * @param chunkCoords
	 * @return list of ecoPlayers
	 */
	public List<EconomyPlayer> loadResidents(String townName, String chunkCoords);
}
