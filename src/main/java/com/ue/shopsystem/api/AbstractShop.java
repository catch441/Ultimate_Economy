package com.ue.shopsystem.api;

import java.io.File;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;

public abstract interface AbstractShop {

    /**
     * --Get Method--
     * <p>
     * Returns the name of this shop.
     * 
     * @return name
     */
    public String getName();

    /**
     * --Get Method--
     * <p>
     * Returns the shopId.
     * 
     * @return shopId
     */
    public String getShopId();

    /**
     * Returns the world in which the shop is.
     * 
     * @return World
     */
    public World getWorld();

    /**
     * Returns the savefile of this shop.
     * 
     * @return File
     */
    public File getSaveFile();

    /**
     * --Get Method--
     * <p>
     * Returns the itemslist of this shop.
     * 
     * @return itemNames
     */
    public List<String> getItemList();

    /**
     * --Get Method--
     * <p>
     * Returns a itemstack by a given slot.
     * 
     * @param slot
     * @return itemstack
     */
    public ItemStack getItem(int slot);

    /**
     * --Save file read method--
     * <p>
     * This method returns the sellprice of a item.
     * 
     * @param itemName
     * @return double
     * @throws ShopSystemException
     */
    public double getItemSellPrice(String itemName) throws ShopSystemException;

    /**
     * --Save file read method--
     * <p>
     * Returns a itemstack from a itemstring.
     * 
     * @param itemString
     * @return itemstack
     * @throws ShopSystemException
     */
    public ItemStack getItemStack(String itemString) throws ShopSystemException;

    /**
     * --Save file read method--
     * <p>
     * This method returns the amount of a item.
     * 
     * @param itemName
     * @return int
     * @throws ShopSystemException
     */
    public int getItemAmount(String itemName) throws ShopSystemException;

    /**
     * --Save file read method--
     * <p>
     * This method returns the buyprice of a item.
     * 
     * @param itemName
     * @return double
     * @throws ShopSystemException
     */
    public double getItemBuyPrice(String itemName) throws ShopSystemException;

    /**
     * --ShopItem Methode--
     * <p>
     * This method adds a item to this shop.
     * 
     * @param slot
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
     * --ShopItem Methode--
     * <p>
     * This method removes a item from this shop.
     * 
     * @param slot
     * @throws ShopSystemException
     * @throws GeneralEconomyException
     */
    public void removeShopItem(int slot) throws ShopSystemException, GeneralEconomyException;

    /**
     * --ShopItem Methode--
     * <p>
     * This method edits an existing item in this shop.
     * 
     * @param slot
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
     * --Change Methode--
     * <p>
     * Change the profession of a shopvillager.
     * 
     * @param profession
     */
    public void changeProfession(Profession profession);

    /**
     * --Change Methode--
     * <p>
     * Change the name of a shop. Name gets checked, if a shop with this name
     * already exists.
     * 
     * @param name
     *            Forbidden char is "_"
     * @throws ShopSystemException
     *             thrown, when a shop with this name already exists or the name
     *             contains "_"
     */
    public abstract void changeShopName(String name) throws ShopSystemException;

    /**
     * --Change Methode--
     * <p>
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
     * --Shop Method--
     * <p>
     * This method moves a shop to a new location.
     * 
     * @param location
     * @throws TownSystemException
     * @throws PlayerException
     */
    public void moveShop(Location location) throws TownSystemException, PlayerException;

    /**
     * --Shop Method--
     * <p>
     * Despawns the shop villager.
     */
    public void despawnVillager();

    /**
     * --Shop Method--
     * <p>
     * Opens the shop inventory.
     * 
     * @param player
     */
    public void openInv(Player player);

    /**
     * --Editor Method--
     * <p>
     * Opens the slot editor GUI.
     * 
     * @param player
     * @param slot
     * @throws IllegalArgumentException
     * @throws ShopSystemException
     * @throws GeneralEconomyException
     */
    public void openSlotEditor(Player player, int slot)
	    throws IllegalArgumentException, ShopSystemException, GeneralEconomyException;

    /**
     * --Editor Method--
     * <p>
     * Opens the editor GUI with occupied and free slots The 2 last slots are not
     * used. If you need more then 2 slots for other usage, then override this
     * method.
     * 
     * @param player
     */
    public void openEditor(Player player);

    /**
     * --Editor Method--
     * <p>
     * Not for commercial use.
     * <p>
     * This method handles the SlotEditor for the InventoryClickEvent.
     * 
     * @param event
     * @throws ShopSystemException
     * @throws PlayerException
     * @throws GeneralEconomyException
     */
    public void handleSlotEditor(InventoryClickEvent event)
	    throws ShopSystemException, PlayerException, GeneralEconomyException;
}
