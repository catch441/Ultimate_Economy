package com.ue.townsystem.town.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ue.exceptions.TownSystemException;

public interface Plot {
	
	/**
	 * Sets this plot for sale with saving it in the file. Spawns a SellVillager at
	 * playerposition.
	 * 
	 * @param salePrice
	 * @param playerLocation
	 * @param player
	 * @throws TownSystemException
	 */
	public void setForSale(double salePrice, Location playerLocation, String player) throws TownSystemException;
	
	/**
	 * Returns the salePrice for this slot.
	 * 
	 * @return double
	 */
	public double getSalePrice();

	/**
	 * Get the chunk coords of this plot.
	 * Format X/Z
	 * 
	 * @return String
	 */
	public String getChunkCoords();
	
	/**
	 * Set the owner of this plot. With saving.
	 * 
	 * @param owner
	 */
	public void setOwner(String owner);
	
	/**
	 * Get the owner of this plot.
	 * 
	 * @return String
	 */
	public String getOwner();
	
	/**
	 * Returns true if the player is the owner of this plot.
	 * 
	 * @param owner
	 * @return booelan
	 */
	public boolean isOwner(String owner);
	
	/**
	 * Removes a coOwner from this plot.
	 * 
	 * @param citizen
	 * @throws TownSystemException
	 */
	public void removeCoOwner(String citizen) throws TownSystemException;
	
	/**
	 * Returns true if the player is a coOwner of this plot.
	 * 
	 * @param coOwner
	 * @return boolean
	 */
	public boolean isCoOwner(String coOwner);
	
	/**
	 * Returns true if this plot is for sale.
	 * 
	 * @return boolean
	 */
	public boolean isForSale();
	
	/**
	 * Despawns the sale villager.
	 * 
	 */
	public void despawnSaleVillager();
	
	/**
	 * Removes a plot from sale. Removes also the saleVillager.
	 * 
	 * @param owner
	 * @throws TownSystemException
	 */
	public void removeFromSale(String owner) throws TownSystemException;
	
	/**
	 * Opens the inventory of the saleManager.
	 * 
	 */
	public void openSaleVillagerInv(Player player);
}
