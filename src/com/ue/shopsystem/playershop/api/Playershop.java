package com.ue.shopsystem.playershop.api;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.shopsystem.api.Shop;

public interface Playershop extends Shop {

	/**
	 * --Change Method--
	 * <p>
	 * This method sets the owner of this shop.
	 * The owner and the shopname are validated.
	 * 
	 * @param newOwner
	 * @throws PlayerException
	 * @throws ShopSystemException 
	 */
	public void changeOwner(String newOwner) throws PlayerException, ShopSystemException;
	
	/**
	 * --Get Method--
	 * <p>
	 * Returns the name of the shop owner.
	 * 
	 * @return
	 */
	public String getOwner();
	
	/**
	 * --Save file edit method--
	 * <p>
	 * For commercial use.
	 * <p>
	 * This method decreases the stock of an shopitem in a playershop.
	 * 
	 * @param itemString
	 * @param stock (only positive)
	 */
	public void decreaseStock(String itemString, int stock);
	
	/**
	 * --Save file edit method--
	 * <p>
	 * For commercial use.
	 * <p>
	 * This method increases the stock of an shopitem in a playershop.
	 * 
	 * @param itemString
	 * @param stock (only positive)
	 */
	public void increaseStock(String itemString, int stock);
	
	/**
	 * --Stockpile Method--
	 * <p>
	 * This method returns true, if the stock of this item is positiv.
	 * 
	 * @param itemString
	 * @return booelan
	 */
	public boolean isAvailable(String itemString);
	
	/**
	 * --Stockpile Method--
	 * <p>
	 * This method switch in the playershop between the shop and the stockpile.
	 * 
	 */
	public void switchStockpile();
	
	/**
	 * --Stockpile Method--
	 * <p>
	 * Setup/Build the stockpile GUI, if the stockpile mode is activated. (mode = false)
	 * 
	 */
	public void setupStockpile();
}
