package com.ue.shopsystem.dataaccess.api;

import java.io.File;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Villager.Profession;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.shopsystem.logic.impl.ShopSystemException;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.townsystem.logic.impl.TownSystemException;

public interface ShopDao {

	/**
	 * Saves the shopname.
	 * 
	 * @param name
	 */
	public void saveShopName(String name);
	
	/**
	 * Saves a shopitem.
	 * 
	 * @param shopItem
	 * @param delete
	 */
	public void saveShopItem(ShopItem shopItem, boolean delete);
	
	/**
	 * Saves a shop item sell price.
	 * 
	 * @param itemHash
	 * @param sellPrice
	 */
	public void saveShopItemSellPrice(int itemHash, double sellPrice);
	
	/**
	 * Saves a shop item buy price.
	 * 
	 * @param itemHash
	 * @param buyPrice
	 */
	public void saveShopItemBuyPrice(int itemHash, double buyPrice);
	
	/**
	 * Saves a shop item amount.
	 * 
	 * @param itemHash
	 * @param amount
	 */
	public void saveShopItemAmount(int itemHash, int amount);
	
	/**
	 * Saves the shop size.
	 * 
	 * @param size
	 */
	public void saveShopSize(int size);
	
	/**
	 * Saves the shop location.
	 * 
	 * @param location
	 */
	public void saveShopLocation(Location location);
	
	/**
	 * Saves the shop villager profession.
	 * 
	 * @param profession
	 */
	public void saveProfession(Profession profession);
	
	/**
	 * Saves the stock for a item.
	 * 
	 * @param itemHash
	 * @param stock
	 */
	public void saveStock(int itemHash, int stock);
	
	/**
	 * Saves the shop owner.
	 * 
	 * @param ecoPlayer
	 */
	public void saveOwner(EconomyPlayer ecoPlayer);
	
	/**
	 * Saves the expire at time.
	 * 
	 * @param expiresAt
	 */
	public void saveExpiresAt(long expiresAt);
	
	/**
	 * Saves the rental fee.
	 * 
	 * @param fee
	 */
	public void saveRentalFee(double fee);
	
	/**
	 * Saves if the shop is rentable.
	 * 
	 * @param isRentable
	 */
	public void saveRentable(boolean isRentable);
	
	/**
	 * Changes the savefile name.
	 * 
	 * @param dataFolder
	 * @param newName
	 * @throws ShopSystemException
	 */
	public void changeSavefileName(File dataFolder, String newName) throws ShopSystemException;
	
	/**
	 * Loads the shop villager profession.
	 * 
	 * @return profession
	 */
	public Profession loadShopVillagerProfession();
	
	/**
	 * Loads the shop size.
	 * 
	 * @return shop size
	 */
	public int loadShopSize();
	
	/**
	 * Loads the shop name.
	 * 
	 * @return shop name
	 */
	public String loadShopName();
	
	/**
	 * Loads the shop location.
	 * 
	 * @return location
	 * @throws TownSystemException
	 */
	public Location loadShopLocation() throws TownSystemException;
	
	/**
	 * Loads a shop item.
	 * 
	 * @param itemHash
	 * @return shop item
	 */
	public ShopItem loadItem(int itemHash);
	
	/**
	 * Loads the item hash list.
	 * 
	 * @return list of integers
	 */
	public List<Integer> loadItemHashList();
	
	/**
	 * Loads the stock of a item.
	 * 
	 * @param itemHash
	 * @return stock
	 */
	public int loadStock(int itemHash);
	
	/**
	 * Loads the shop owner.
	 * 
	 * @param name
	 * @return owner name
	 */
	public String loadOwner(String name);
	
	/**
	 * Loads if the shop is rentable.
	 * 
	 * @return rentable
	 */
	public boolean loadRentable();
	
	/**
	 * Loads the rent until time.
	 * 
	 * @return rent until
	 */
	public long loadExpiresAt();
	
	/**
	 * Loads the rental fee.
	 * 
	 * @return rental fee
	 */
	public double loadRentalFee();
	
	/**
	 * Deletes the savefile.
	 */
	public void deleteFile();

	/**
	 * Setup a new savefile.
	 * 
	 * @param shopId
	 */
	public void setupSavefile(String shopId);
	
	/**
	 * @since 1.2.6
	 * @param itemHash
	 * @return true, if corrupted found and removed
	 * @deprecated removed items, that are corrupted. The newer savefile system
	 *             cannot handle old corrupted items, that are ignored before.
	 */
	@Deprecated
	public boolean removeIfCorrupted(int itemHash);
}
