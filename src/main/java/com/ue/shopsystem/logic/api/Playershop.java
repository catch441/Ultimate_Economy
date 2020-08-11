package com.ue.shopsystem.logic.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.shopsystem.logic.impl.ShopSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;

public interface Playershop extends AbstractShop {

    /**
     * This method sets the owner of this shop. The owner and the shopname are
     * validated.
     * 
     * @param newOwner
     * @throws EconomyPlayerException
     * @throws ShopSystemException
     */
    public void changeOwner(EconomyPlayer newOwner) throws EconomyPlayerException, ShopSystemException;

    /**
     * Returns true if the ecoPlayer is the owner of this shop.
     * 
     * @param ecoPlayer
     * @return boolean
     */
    public boolean isOwner(EconomyPlayer ecoPlayer);

    /**
     * Returns the shop owner.
     * 
     * @return EconomyPlayer
     */
    public EconomyPlayer getOwner();

    /**
     * This method decreases the stock of an shopitem in a playershop.
     * 
     * @param slot
     * @param stock positive
     * @exception GeneralEconomyException
     * @throws ShopSystemException 
     */
    public void decreaseStock(int slot, int stock) throws GeneralEconomyException, ShopSystemException;

    /**
     * This method increases the stock of an shopitem in a playershop.
     * 
     * @param slot
     * @param stock positive
     * @exception GeneralEconomyException
     * @throws ShopSystemException 
     */
    public void increaseStock(int slot, int stock) throws GeneralEconomyException, ShopSystemException;

    /**
     * This method returns true, if the stock of this item greater then the item amount.
     * 
     * @param slot
     * @return booelan
     * @exception ShopSystemException
     * @throws GeneralEconomyException 
     */
    public boolean isAvailable(int slot) throws ShopSystemException, GeneralEconomyException;

    /**
     * This method opens the stockpile inventory.
     * 
     * @param player
     * @throws ShopSystemException 
     */
    public void openStockpile(Player player) throws ShopSystemException;
    
    /**
     * Returns the stockpile inventory.
     * @return stockpile inventory
     * @throws ShopSystemException 
     */
    public Inventory getStockpileInventory() throws ShopSystemException;
}
