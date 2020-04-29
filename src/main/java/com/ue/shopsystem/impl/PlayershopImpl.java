package com.ue.shopsystem.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.shopsystem.api.Playershop;
import com.ue.townsystem.townworld.api.TownworldController;
import com.ue.ultimate_economy.UltimateEconomy;

public class PlayershopImpl extends AbstractShopImpl implements Playershop {

    private EconomyPlayer owner;
    private Inventory stockPile;

    /**
     * Constructor for creating a new playershop. No validation, if the shopId is
     * unique.
     * 
     * @param name
     * @param owner
     * @param shopId
     * @param spawnLocation
     * @param size
     */
    public PlayershopImpl(String name, EconomyPlayer owner, String shopId, Location spawnLocation, int size) {
	super(name, shopId, spawnLocation, size);
	setupPlayerShop(owner);
    }

    /**
     * Constructor for loading an existing playershop. No validation, if the shopId
     * is unique. If name != null then use old loading otherwise use new loading.
     * 
     * @param name
     *                   deprecated
     * @param shopId
     * @throws TownSystemException
     * @throws PlayerException
     * @throws ShopSystemException
     * @throws GeneralEconomyException
     */
    public PlayershopImpl(String name, String shopId)
	    throws TownSystemException, PlayerException, GeneralEconomyException, ShopSystemException {
	super(name, shopId);
	loadExistingPlayerShop(name);
    }
    /*
     * API methods
     * 
     */

    @Override
    public void openStockpile(Player player) {
	player.openInventory(getStockpileInventory());
    }

    /**
     * Overridden, because of the number of reserved slots. {@inheritDoc}
     */
    @Override
    public void changeShopSize(int newSize) throws ShopSystemException, PlayerException, GeneralEconomyException {
	getValidationHandler().checkForValidSize(newSize);
	getValidationHandler().checkForResizePossible(getShopInventory(), getSize(), newSize, 2);
	setSize(newSize);
	getSavefileHandler().saveShopSize(newSize);
	setupShopInventory();
	setupShopInvDefaultStockItem();
	setupEditor(2);
	reloadShopItems();
	loadStockpile();
    }

    /**
     * Overridden, because of the permission validation. {@inheritDoc}
     */
    @Override
    public void moveShop(Location location) throws TownSystemException, PlayerException {
	if (TownworldController.isTownWorld(location.getWorld().getName())) {
	    getValidationHandler().checkForPlayerHasPermissionAtLocation(location, getOwner());
	}
	super.moveShop(location);
    }

    /**
     * Overridden, because of the naming convention. {@inheritDoc}
     * 
     * @throws GeneralEconomyException
     */
    @Override
    public void changeShopName(String name) throws ShopSystemException, GeneralEconomyException {
	getValidationHandler().checkForShopNameIsFree(name, getOwner());
	getValidationHandler().checkForValidShopName(name);
	setupShopName(name);
	getShopVillager().setCustomName(name + "_" + getOwner().getName());
	changeInventoryNames(name);
    }

    @Override
    public EconomyPlayer getOwner() {
	return owner;
    }

    /**
     * Overridden, because of the stock value. {@inheritDoc}
     */
    @Override
    public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack)
	    throws ShopSystemException, PlayerException, GeneralEconomyException {
	super.addShopItem(slot, sellPrice, buyPrice, itemStack);
	ShopItem item = getShopItem(slot);
	getSavefileHandler().saveStock(item.getItemString(), 0);
	updateItemInStockpile(slot);
    }

    /**
     * Overidden, because of stockpile. {@inheritDoc}
     */
    @Override
    public void removeShopItem(int slot) throws ShopSystemException, GeneralEconomyException {
	super.removeShopItem(slot);
	updateItemInStockpile(slot);
    }

    @Override
    public boolean isAvailable(int slot) throws ShopSystemException, GeneralEconomyException {
	getValidationHandler().checkForValidSlot(slot, getSize(), 2);
	ShopItem item = getShopItem(slot);
	if (item.getStock() >= item.getAmount()) {
	    return true;
	}
	return false;
    }

    @Override
    public void changeOwner(EconomyPlayer newOwner) throws PlayerException, ShopSystemException {
	getValidationHandler().checkForChangeOwnerIsPossible(newOwner, getName());
	setOwner(newOwner);
	getSavefileHandler().saveOwner(newOwner);
	getShopVillager().setCustomName(getName() + "_" + newOwner.getName());
    }

    @Override
    public boolean isOwner(EconomyPlayer ecoPlayer) {
	if (ecoPlayer.equals(getOwner())) {
	    return true;
	}
	return false;
    }

    @Override
    public void decreaseStock(int slot, int stock) throws GeneralEconomyException, ShopSystemException {
	getValidationHandler().checkForPositiveValue(stock);
	getValidationHandler().checkForValidSlot(slot, getSize(), 2);
	ShopItem item = getShopItem(slot);
	int entireStock = item.getStock();
	getValidationHandler().checkForValidStockDecrease(entireStock, stock);
	item.setStock(entireStock - stock);
	getSavefileHandler().saveStock(item.getItemString(), item.getStock());
	updateItemInStockpile(slot);
    }

    @Override
    public void increaseStock(int slot, int stock) throws GeneralEconomyException, ShopSystemException {
	getValidationHandler().checkForPositiveValue(stock);
	getValidationHandler().checkForValidSlot(slot, getSize(), 2);
	ShopItem item = getShopItem(slot);
	int entireStock = item.getStock();
	item.setStock(entireStock + stock);
	getSavefileHandler().saveStock(item.getItemString(), item.getStock());
	updateItemInStockpile(slot);
    }

    @Override
    public Inventory getStockpileInventory() {
	return stockPile;
    }

    /*
     * Utility methods
     * 
     */

    private void updateItemInStockpile(int slot) throws GeneralEconomyException, ShopSystemException {
	ItemStack stack = getShopInventory().getItem(slot).clone();
	if (stack != null && stack.getType() != Material.AIR) {
	    int stock = getShopItem(slot).getStock();
	    ItemMeta meta = stack.getItemMeta();
	    List<String> list = removeShopItemPriceLore(meta.getLore());
	    if (stock != 1) {
		list.add(ChatColor.GREEN + String.valueOf(stock) + ChatColor.GOLD + " Items");
	    } else {
		list.add(ChatColor.GREEN + String.valueOf(stock) + ChatColor.GOLD + " Item");
	    }
	    meta.setLore(list);
	    stack.setItemMeta(meta);
	    getStockpileInventory().setItem(slot, stack);
	} else {
	    getStockpileInventory().clear(slot);
	}
    }

    protected void setOwner(EconomyPlayer owner) {
	this.owner = owner;
    }

    /*
     * Setup methods
     * 
     */

    private void setupPlayerShop(EconomyPlayer owner) {
	setupShopInvDefaultStockItem();
	setupShopOwner(owner);
	setupEditor(2);
	setupStockpile();
	setupEconomyVillagerType();
    }

    private void setupStockpile() {
	stockPile = Bukkit.createInventory(getShopVillager(), getSize());
	ItemStack stockpileSwitchItem = new ItemStack(Material.CRAFTING_TABLE, 1);
	ItemMeta meta = stockpileSwitchItem.getItemMeta();
	List<String> infos = new ArrayList<>();
	infos.add(ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "close stockpile");
	infos.add(ChatColor.GOLD + "Rightclick: " + ChatColor.GREEN + "add specified amount");
	infos.add(ChatColor.GOLD + "Shift-Rightclick: " + ChatColor.GREEN + "add all");
	infos.add(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "get specified amount");
	meta.setLore(infos);
	meta.setDisplayName("Infos");
	stockpileSwitchItem.setItemMeta(meta);
	getStockpileInventory().setItem(getSize() - 1, stockpileSwitchItem);
    }

    private void setupEconomyVillagerType() {
	getShopVillager().setMetadata("ue-type",
		new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.PLAYERSHOP));
    }

    private void setupShopInvDefaultStockItem() {
	ItemStack itemStack = new ItemStack(Material.CRAFTING_TABLE);
	ItemMeta meta = itemStack.getItemMeta();
	meta.setDisplayName("Stock");
	itemStack.setItemMeta(meta);
	addShopItemToInv(itemStack, 1, getSize() - 2, 0.0, 0.0);
    }

    private void setupShopOwner(EconomyPlayer owner) {
	if (owner != null) {
	    setOwner(owner);
	    getSavefileHandler().saveOwner(owner);
	    getShopVillager().setCustomName(getName() + "_" + owner.getName());
	}
    }

    /*
     * Loading methods
     * 
     */

    private void loadExistingPlayerShop(String name)
	    throws PlayerException, GeneralEconomyException, ShopSystemException {
	loadStock();
	loadOwner(name);
	loadStockpile();
	setupEconomyVillagerType();
    }

    private void loadStock() {
	for(ShopItem item: getItemList()) {
	    item.setStock(getSavefileHandler().loadStock(item.getItemString()));
	}
    }

    private void loadStockpile() throws GeneralEconomyException, ShopSystemException {
	setupStockpile();
	for (int i = 0; i < (getSize() - 2); i++) {
	    updateItemInStockpile(i);
	}
    }

    private void loadOwner(String oldName) throws PlayerException {
	String owner = getSavefileHandler().loadOwner(oldName);
	if (owner != null && !"".equals(owner)) {
	    setOwner(EconomyPlayerController.getEconomyPlayerByName(owner));
	    getShopVillager().setCustomName(getName() + "_" + getOwner().getName());
	}
    }
}