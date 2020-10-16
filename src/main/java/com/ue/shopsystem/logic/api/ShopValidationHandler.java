package com.ue.shopsystem.logic.api;

import java.io.File;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.logic.impl.ShopSystemException;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.townsystem.logic.impl.TownSystemException;

public interface ShopValidationHandler {

	/**
	 * Check for one price is greater then zero if both prices are available.
	 * 
	 * @param sellPrice
	 * @param buyPrice
	 * @throws ShopSystemException
	 */
	public void checkForOnePriceGreaterThenZeroIfBothAvailable(String sellPrice, String buyPrice)
			throws ShopSystemException;

	/**
	 * Check for both prices greater then zero.
	 * 
	 * @param sellPrice
	 * @param buyPrice
	 * @throws ShopSystemException
	 */
	public void checkForPricesGreaterThenZero(double sellPrice, double buyPrice) throws ShopSystemException;

	/**
	 * Check for slot is not empty.
	 * 
	 * @param slot
	 * @param inventory
	 * @param reservedSlots
	 * @throws GeneralEconomyException
	 * @throws ShopSystemException
	 */
	public void checkForSlotIsNotEmpty(int slot, Inventory inventory, int reservedSlots)
			throws GeneralEconomyException, ShopSystemException;
	
	/**
	 * Check for slot is empty.
	 * 
	 * @param slot
	 * @param inventory
	 * @param reservedSlots
	 * @throws GeneralEconomyException
	 * @throws EconomyPlayerException
	 */
	public void checkForSlotIsEmpty(int slot, Inventory inventory, int reservedSlots)
			throws GeneralEconomyException, EconomyPlayerException;
	
	/**
	 * Returns true, if the slot is empty.
	 * 
	 * @param slot
	 * @param inventory
	 * @param reservedSlots
	 * @return isEmpzy
	 * @throws GeneralEconomyException
	 */
	public boolean isSlotEmpty(int slot, Inventory inventory, int reservedSlots) throws GeneralEconomyException;
	
	/**
	 * Check for a valid amount.
	 * 
	 * @param amount
	 * @throws GeneralEconomyException
	 */
	public void checkForValidAmount(String amount) throws GeneralEconomyException;
	
	/**
	 * Check for a valid price.
	 * 
	 * @param price
	 * @throws GeneralEconomyException
	 */
	public void checkForValidPrice(String price) throws GeneralEconomyException;
	
	/**
	 * Check for a valid inventory size.
	 * 
	 * @param size
	 * @throws GeneralEconomyException
	 */
	public void checkForValidSize(int size) throws GeneralEconomyException;
	
	/**
	 * Check for a valid slot.
	 * 
	 * @param slot
	 * @param size
	 * @param reservedSlots
	 * @throws GeneralEconomyException
	 */
	public void checkForValidSlot(int slot, int size, int reservedSlots) throws GeneralEconomyException;
	
	/**
	 * Check for resize is possible.
	 * 
	 * @param inventory
	 * @param oldSize
	 * @param newSize
	 * @param reservedSlots
	 * @throws ShopSystemException
	 * @throws GeneralEconomyException
	 */
	public void checkForResizePossible(Inventory inventory, int oldSize, int newSize, int reservedSlots)
			throws ShopSystemException, GeneralEconomyException;
	
	/**
	 * Check for item does not exist.
	 * 
	 * @param itemString
	 * @param itemList
	 * @throws ShopSystemException
	 */
	public void checkForItemDoesNotExist(String itemString, List<ShopItem> itemList) throws ShopSystemException;
	
	/**
	 * Check for item can be deleted.
	 * 
	 * @param slot
	 * @param size
	 * @throws ShopSystemException
	 */
	public void checkForItemCanBeDeleted(int slot, int size) throws ShopSystemException;
	
	/**
	 * Check for positive value.
	 * 
	 * @param value
	 * @throws GeneralEconomyException
	 */
	public void checkForPositiveValue(double value) throws GeneralEconomyException;
	
	/**
	 * Check for valid stock decrease.
	 * 
	 * @param entireStock
	 * @param stock
	 * @throws ShopSystemException
	 */
	public void checkForValidStockDecrease(int entireStock, int stock) throws ShopSystemException;
	
	/**
	 * Check for change owner is possible.
	 * 
	 * @param uniqueShopNameList
	 * @param newOwner
	 * @param shopName
	 * @throws ShopSystemException
	 */
	public void checkForChangeOwnerIsPossible(List<String> uniqueShopNameList, EconomyPlayer newOwner, String shopName) throws ShopSystemException;
	
	/**
	 * Check for a valid shop name.
	 * 
	 * @param name
	 * @throws ShopSystemException
	 */
	public void checkForValidShopName(String name) throws ShopSystemException;
	
	/**
	 * Check for shop name is available.
	 * If the owner is null, then no shop name suffix (_ownername) is used.
	 * 
	 * @param shopNames
	 * @param name
	 * @param owner
	 * @throws GeneralEconomyException
	 */
	public void checkForShopNameIsFree(List<String> shopNames, String name, EconomyPlayer owner) throws GeneralEconomyException;
	
	/**
	 * Check for player has permissions at a specific location.
	 * 
	 * @param location
	 * @param owner
	 * @throws EconomyPlayerException
	 * @throws TownSystemException
	 */
	public void checkForPlayerHasPermissionAtLocation(Location location, EconomyPlayer owner)
			throws EconomyPlayerException, TownSystemException;
	
	/**
	 * Check for is rentable.
	 * 
	 * @param isRentable
	 * @throws ShopSystemException
	 */
	public void checkForIsRentable(boolean isRentable) throws ShopSystemException;
	
	/**
	 * Check for is rented.
	 * 
	 * @param isRentable
	 * @throws ShopSystemException
	 */
	public void checkForIsRented(boolean isRentable) throws ShopSystemException;
	
	/**
	 * Check if player is online.
	 * 
	 * @param ecoPlayer
	 * @throws EconomyPlayerException
	 */
	public void checkForPlayerIsOnline(EconomyPlayer ecoPlayer) throws EconomyPlayerException;
	
	/**
	 * Check if the player inventory is not full.
	 * 
	 * @param inventory
	 * @throws EconomyPlayerException
	 */
	public void checkForPlayerInventoryNotFull(Inventory inventory) throws EconomyPlayerException;
	
	/**
	 * Check if the shop owner has enough money.
	 * 
	 * @param ecoPlayer
	 * @param money
	 * @throws GeneralEconomyException
	 * @throws ShopSystemException
	 */
	public void checkForShopOwnerHasEnoughMoney(EconomyPlayer ecoPlayer, double money)
			throws GeneralEconomyException, ShopSystemException;
	
	/**
	 * Check for renaming is possible.
	 * 
	 * @param newFile
	 * @throws ShopSystemException
	 */
	public void checkForRenamingSavefileIsPossible(File newFile) throws ShopSystemException;

	/**
	 * Check for player reached max playershops.
	 * 
	 * @param shopList
	 * @param ecoPlayer
	 * @throws EconomyPlayerException
	 */
	public void checkForMaxPlayershopsForPlayer(List<Playershop> shopList, EconomyPlayer ecoPlayer)
			throws EconomyPlayerException;
}
