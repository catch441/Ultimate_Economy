package com.ue.shopsystem.logic.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.logic.impl.ShopSystemException;

public interface Rentshop extends Playershop {

	/**
	 * Setup a new rentshop.
	 * 
	 * @param shopId
	 * @param spawnLocation
	 * @param size
	 * @param rentalFee
	 */
	public void setupNew(String shopId, Location spawnLocation, int size, double rentalFee);

	/**
	 * Returns the rental until time in milliseconds.
	 * 
	 * @return rentUntil
	 */
	public long getRentUntil();

	/**
	 * Returns true, if this shop is not rented by a other player.
	 * 
	 * @return rentable
	 */
	public boolean isRentable();

	/**
	 * Resets the entire shop. Sets the shop back to the "rentable" state. Removes
	 * all items from the shop.
	 * 
	 * @throws GeneralEconomyException
	 * @throws ShopSystemException
	 * 
	 */
	public void resetShop() throws ShopSystemException, GeneralEconomyException;

	/**
	 * Change the rental fee of this shop. Does not affect a active rent.
	 * 
	 * @param fee
	 * @throws GeneralEconomyException
	 */
	public void changeRentalFee(double fee) throws GeneralEconomyException;

	/**
	 * Rent this shop for a given player.
	 * 
	 * @param player
	 * @param duration
	 * @throws ShopSystemException
	 * @throws GeneralEconomyException
	 * @throws EconomyPlayerException
	 */
	public void rentShop(EconomyPlayer player, int duration)
			throws ShopSystemException, GeneralEconomyException, EconomyPlayerException;

	/**
	 * Opens the rentshop GUI, if the shop is not rented.
	 * 
	 * @param player
	 * @throws ShopSystemException
	 */
	public void openRentGUI(Player player) throws ShopSystemException;

	/**
	 * Returns the rental fee amount.
	 * 
	 * @return rentalFee
	 */
	public double getRentalFee();
}
