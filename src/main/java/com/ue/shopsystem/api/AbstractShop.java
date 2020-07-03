package com.ue.shopsystem.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.impl.ShopItem;
import com.ue.shopsystem.impl.ShopSavefileHandler;

public abstract interface AbstractShop {

	/**
	 * Returns the name of this shop.
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * Returns the shopId.
	 * 
	 * @return shopId
	 */
	public String getShopId();

	/**
	 * Returns the shop inventory.
	 * 
	 * @return shopInventory
	 * @throws ShopSystemException
	 */
	public Inventory getShopInventory() throws ShopSystemException;

	/**
	 * Returns the location of the shop.
	 * 
	 * @return location
	 */
	public Location getShopLocation();

	/**
	 * Returns the world in which the shop is.
	 * 
	 * @return World
	 */
	public World getWorld();

	/**
	 * Returns the savefile handler of this shop.
	 * 
	 * @return ShopSavefileManager
	 */
	public ShopSavefileHandler getSavefileHandler();

	/**
	 * Returns the itemslist of this shop.
	 * 
	 * @return list of ShopItems
	 * @throws ShopSystemException
	 */
	public List<ShopItem> getItemList() throws ShopSystemException;

	/**
	 * Returns a itemstack by a given slot.
	 * 
	 * @param slot
	 * @return itemstack
	 * @throws ShopSystemException
	 * @throws GeneralEconomyException
	 */
	public ShopItem getShopItem(int slot) throws GeneralEconomyException, ShopSystemException;

	/**
	 * This method adds a item to this shop.
	 * 
	 * @param slot      intern
	 * @param sellPrice
	 * @param buyPrice
	 * @param itemStack
	 * @throws ShopSystemException
	 * @throws PlayerException
	 * @throws GeneralEconomyException
	 */
	public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack)
			throws ShopSystemException, PlayerException, GeneralEconomyException;

	/**
	 * This method removes a item from this shop.
	 * 
	 * @param slot intern
	 * @throws ShopSystemException
	 * @throws GeneralEconomyException
	 */
	public void removeShopItem(int slot) throws ShopSystemException, GeneralEconomyException;

	/**
	 * This method edits an existing item in this shop.
	 * 
	 * @param slot      intern
	 * @param amount
	 * @param sellPrice
	 * @param buyPrice
	 * @return String
	 * @throws ShopSystemException
	 * @throws PlayerException
	 * @throws GeneralEconomyException
	 */
	public String editShopItem(int slot, String amount, String sellPrice, String buyPrice)
			throws ShopSystemException, PlayerException, GeneralEconomyException;

	/**
	 * Buy the shop item from a specific slot for the given economy player. The
	 * economy player have to be online to recieve the items.
	 * 
	 * @param slot
	 * @param ecoPlayer
	 * @param sendMessage
	 * @throws GeneralEconomyException
	 * @throws PlayerException
	 * @throws ShopSystemException
	 */
	public void buyShopItem(int slot, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, PlayerException, ShopSystemException;

	/**
	 * Sells a shopitem to this shop. Make sure, that the player has the amount of
	 * items in his inventory. There is no validation for that.
	 * 
	 * @param slot
	 * @param amount
	 * @param ecoPlayer
	 * @param sendMessage
	 * @throws GeneralEconomyException
	 * @throws ShopSystemException
	 * @throws PlayerException
	 */
	public void sellShopItem(int slot, int amount, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, ShopSystemException, PlayerException;

	/**
	 * Change the profession of a shopvillager.
	 * 
	 * @param profession
	 */
	public void changeProfession(Profession profession);

	/**
	 * Change the name of a shop. Name gets checked, if a shop with this name
	 * already exists.
	 * 
	 * @param name Forbidden char is "_"
	 * @throws ShopSystemException     thrown, when a shop with this name already
	 *                                 exists or the name contains "_"
	 * @throws GeneralEconomyException
	 */
	public abstract void changeShopName(String name) throws ShopSystemException, GeneralEconomyException;

	/**
	 * Change the size of the shop. Size gets validated. With only the info slot in
	 * the shop. Have to be overridden, if you have more then one reserved shot in
	 * your shop.
	 * 
	 * @param newSize
	 * @throws ShopSystemException
	 * @throws GeneralEconomyException
	 * @throws PlayerException
	 */
	public void changeShopSize(int newSize) throws ShopSystemException, GeneralEconomyException, PlayerException;

	/**
	 * This method moves a shop to a new location.
	 * 
	 * @param location
	 * @throws TownSystemException
	 * @throws PlayerException
	 */
	public void moveShop(Location location) throws TownSystemException, PlayerException;

	/**
	 * Despawns the shop villager.
	 */
	public void despawnVillager();

	/**
	 * Despawns the shop villager.
	 */
	public void deleteShop();

	/**
	 * Opens the shop inventory.
	 * 
	 * @param player
	 * @throws ShopSystemException
	 */
	public void openShopInventory(Player player) throws ShopSystemException;

	/**
	 * Opens the slot editor GUI.
	 * 
	 * @param player
	 * @param slot   internal
	 * @throws ShopSystemException
	 * @throws GeneralEconomyException
	 */
	public void openSlotEditor(Player player, int slot) throws ShopSystemException, GeneralEconomyException;

	/**
	 * Opens the editor GUI with occupied and free slots The 2 last slots are not
	 * used. If you need more then 2 slots for other usage, then override this
	 * method.
	 * 
	 * @param player
	 * @throws ShopSystemException
	 */
	public void openEditor(Player player) throws ShopSystemException;

	/**
	 * Returns the size of the shop.
	 * 
	 * @return size
	 */
	public int getSize();

	/**
	 * Returns the shop villager.
	 * 
	 * @return shop villager
	 */
	public Villager getShopVillager();

	/**
	 * Returns a shop item.
	 * 
	 * @param stack
	 * @return
	 * @throws ShopSystemException
	 */
	public ShopItem getShopItem(ItemStack stack) throws ShopSystemException;
}
