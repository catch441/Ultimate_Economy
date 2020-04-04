package com.ue.shopsystem.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.shopsystem.api.Playershop;
import com.ue.shopsystem.api.PlayershopController;
import com.ue.townsystem.town.api.Town;
import com.ue.townsystem.townworld.api.Townworld;
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
     * @param name deprecated
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
	checkForValidSize(newSize);
	checkForResizePossible(newSize, 2);
	setSize(newSize);
	getSavefileManager().saveShopSize(newSize);
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
	    checkForPlayerHasPermissionInLocation(location);
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
	checkForShopNameIsFree(name);
	checkForValidShopName(name);
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
	getSavefileManager().saveStock(getItemString(getShopItem(slot), true), 0);
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
	Bukkit.getLogger().info(slot + " davor");
	checkForValidSlot(slot);
	int stock = loadStock(slot);
	int amount = getItemAmount(slot);
	if (stock >= amount) {
	    return true;
	}
	return false;
    }

    @Override
    public void changeOwner(EconomyPlayer newOwner) throws PlayerException, ShopSystemException {
	checkForChangeOwnerIsPossible(newOwner);
	setOwner(newOwner);
	getSavefileManager().saveOwner(newOwner);
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
	checkForPositiveValue(stock);
	checkForValidSlot(slot);
	int entireStock = loadStock(slot);
	checkForValidStockDecrease(entireStock, stock);
	if ((entireStock - stock) >= 0) {
	    getSavefileManager().saveStock(getItemString(getShopItem(slot), true), entireStock - stock);
	}
	updateItemInStockpile(slot);
    }

    @Override
    public void increaseStock(int slot, int stock) throws GeneralEconomyException, ShopSystemException {
	checkForPositiveValue(stock);
	checkForValidSlot(slot);
	int entireStock = loadStock(slot);
	getSavefileManager().saveStock(getItemString(getShopItem(slot), true), entireStock + stock);
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
	    int stock = loadStock(slot);
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
	    getSavefileManager().saveOwner(owner);
	    getShopVillager().setCustomName(getName() + "_" + owner.getName());
	}
    }

    /*
     * Loading methods
     * 
     */

    private void loadExistingPlayerShop(String name)
	    throws PlayerException, GeneralEconomyException, ShopSystemException {
	if (name != null) {
	    loadOwnerOld(name);
	}
	// new loading
	else {
	    loadOwner();
	}
	loadStockpile();
	setupEconomyVillagerType();
    }

    private int loadStock(int slot) throws GeneralEconomyException, ShopSystemException {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSavefileManager().getSaveFile());
	String itemString = getItemString(getShopItem(slot), true);
	return config.getInt("ShopItems." + itemString + ".stock");
    }

    private void loadStockpile() throws GeneralEconomyException, ShopSystemException {
	setupStockpile();
	for (int i = 0; i < (getSize() - 2); i++) {
	    updateItemInStockpile(i);
	}
    }

    private void loadOwner() throws PlayerException {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSavefileManager().getSaveFile());
	if (config.isSet("Owner") && !config.getString("Owner").equals("")) {
	    setOwner(EconomyPlayerController.getEconomyPlayerByName(config.getString("Owner")));
	    getShopVillager().setCustomName(getName() + "_" + getOwner().getName());
	}
    }

    /*
     * Validation check methods
     * 
     */

    protected void checkForPositiveValue(double value) throws GeneralEconomyException {
	if (value < 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, value);
	}
    }

    private void checkForValidStockDecrease(int entireStock, int stock) throws GeneralEconomyException {
	if ((entireStock - stock) < 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, stock);
	}
    }

    private void checkForChangeOwnerIsPossible(EconomyPlayer newOwner) throws ShopSystemException {
	if (PlayershopController.getPlayerShopUniqueNameList().contains(getName() + "_" + newOwner.getName())) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.SHOP_CHANGEOWNER_ERROR);
	}
    }

    protected void checkForValidShopName(String name) throws ShopSystemException {
	if (name.contains("_")) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_CHAR_IN_SHOP_NAME);
	}
    }

    private void checkForShopNameIsFree(String name) throws GeneralEconomyException {
	if (PlayershopController.getPlayerShopUniqueNameList().contains(name + "_" + getOwner().getName())
		|| PlayershopController.getPlayerShopUniqueNameList().contains(name)) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS,
		    name + getOwner().getName());
	}
    }

    private void checkForPlayerHasPermissionInLocation(Location location) throws PlayerException, TownSystemException {
	Townworld townworld = TownworldController.getTownWorldByName(location.getWorld().getName());
	if (townworld.isChunkFree(location.getChunk())) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
	} else {
	    Town town = townworld.getTownByChunk(location.getChunk());
	    if (!town.hasBuildPermissions(getOwner(),
		    town.getPlotByChunk(location.getChunk().getX() + "/" + location.getChunk().getZ()))) {
		throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
	    }
	}
    }

    private void checkForValidSlot(int slot) throws GeneralEconomyException {
	if (slot > (getSize() - 2) || slot < 0) {
	    // +1 for player readable style
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, slot + 1);
	}
    }

    /*
     * Deprecated
     * 
     */

    @Deprecated
    private void loadOwnerOld(String name) throws PlayerException {
	EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName(name.substring(name.indexOf("_") + 1));
	setOwner(ecoPlayer);
	getSavefileManager().saveOwner(ecoPlayer);
	setupShopName(name.substring(0, name.indexOf("_")));
    }
}