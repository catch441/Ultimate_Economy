package com.ue.townsystem.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.TownSystemException;

public interface Plot {

    /**
     * Sets this plot for sale with saving it in the file. Spawns a SellVillager at
     * playerposition.
     * 
     * @param salePrice
     * @param playerLocation
     * @param player
     * @param sendMessage
     *            when true a message is send to the receiver and this player
     * @throws TownSystemException
     * @throws PlayerException
     */
    public void setForSale(double salePrice, Location playerLocation, EconomyPlayer player, boolean sendMessage)
	    throws TownSystemException, PlayerException;
    
    /**
     * Moves a sale villager to a new location.
     * 
     * @param newLocation
     * @throws PlayerException
     */
    public void moveSaleVillager(Location newLocation) throws PlayerException;

    /**
     * Returns the salePrice for this slot.
     * 
     * @return double
     */
    public double getSalePrice();
    
    /**
     * Get a list of all residents of this plot.
     * 
     * @return List
     */
    public List<EconomyPlayer> getResidents();
    
    /**
     * Add a residents to this plot.
     * 
     * @param player
     * @throws TownSystemException
     */
    public void addResident(EconomyPlayer player) throws TownSystemException;

    /**
     * Get the chunk coords of this plot. Format X/Z
     * 
     * @return String
     */
    public String getChunkCoords();

    /**
     * Set the owner of this plot. With saving.
     * 
     * @param player
     */
    public void setOwner(EconomyPlayer player);

    /**
     * Get the owner of this plot.
     * 
     * @return String
     */
    public EconomyPlayer getOwner();

    /**
     * Returns true if the player is the owner of this plot.
     * 
     * @param player
     * @return booelan
     */
    public boolean isOwner(EconomyPlayer player);

    /**
     * Removes a resident from this plot.
     * 
     * @param player
     * @throws TownSystemException
     */
    public void removeResident(EconomyPlayer player) throws TownSystemException;

    /**
     * Returns true if the player is a resident of this plot.
     * 
     * @param player
     * @return boolean
     */
    public boolean isResident(EconomyPlayer player);

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
     * @param player
     * @throws TownSystemException
     */
    public void removeFromSale(EconomyPlayer player) throws TownSystemException;

    /**
     * Opens the inventory of the saleManager.
     * 
     * @param player
     */
    public void openSaleVillagerInv(Player player);
}
