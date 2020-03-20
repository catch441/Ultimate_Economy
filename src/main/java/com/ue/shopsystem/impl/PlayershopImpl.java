package com.ue.shopsystem.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
import com.ue.shopsystem.controller.PlayershopController;
import com.ue.townsystem.town.api.Town;
import com.ue.townsystem.townworld.api.Townworld;
import com.ue.townsystem.townworld.api.TownworldController;
import com.ue.ultimate_economy.UltimateEconomy;

public class PlayershopImpl extends AbstractShopImpl implements Playershop {

    protected EconomyPlayer owner;
    // true = shop, false = stock
    private boolean shopMode;

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
	shopMode = true;
	// neccessary for rentshops with a owner
	if (owner != null) {
	    saveOwnerToFile(owner);
	    getShopVillager().setCustomName(name + "_" + owner.getName());
	}
	getShopVillager().setMetadata("ue-type",
		new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.PLAYERSHOP));
    }

    /**
     * Constructor for loading an existing playershop. No validation, if the shopId
     * is unique. If name != null then use old loading otherwise use new loading.
     * 
     * @param name
     * @param shopId
     * @throws TownSystemException
     */
    public PlayershopImpl(String name, String shopId) throws TownSystemException {
	super(name, shopId);
	shopMode = true;
	try {
	    // old loading, can be deleted in the future
	    if (name != null) {
		saveOwnerToFile(EconomyPlayerController.getEconomyPlayerByName(name.substring(name.indexOf("_") + 1)));
		setupShopName(name.substring(0, name.indexOf("_")));
	    }
	    // new loading
	    else {
		loadOwner();
	    }
	} catch (PlayerException e) {
	}
	// set the type of the villager
	getShopVillager().setMetadata("ue-type",
		new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.PLAYERSHOP));
	// update villager name to naming convention
	if (owner == null) {
	    getShopVillager().setCustomName(getName() + "_");
	} else {
	    getShopVillager().setCustomName(getName() + "_" + owner.getName());
	}
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////// overridden

    /**
     * Overridden, because of the stock item.
     */
    private void setupShopInvDefaultStockItem() {
	ItemStack itemStack = new ItemStack(Material.CRAFTING_TABLE);
	ItemMeta meta = itemStack.getItemMeta();
	meta.setDisplayName("Stock");
	itemStack.setItemMeta(meta);
	addShopItemToInv(itemStack, 1, getSize() - 2, 0.0, 0.0);
    }

    /**
     * Overridden, because of the number of reserved slots.
     * 
     * @throws ShopSystemException
     * @throws PlayerException
     * @throws GeneralEconomyException
     */
    @Override
    public void changeShopSize(int newSize) throws ShopSystemException, PlayerException, GeneralEconomyException {
	if (newSize % 9 != 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, newSize);
	} else {
	    boolean possible = true;
	    int diff = getSize() - newSize;
	    // number or reserved slots
	    int temp = 2;
	    if (getSize() > newSize) {
		for (int i = 1; i <= diff; i++) {
		    ItemStack stack = getShopInventory().getItem(getSize() - i - temp);
		    if (stack != null) {
			possible = false;
		    }
		}
	    }
	    if (possible) {
		setupShopSize(newSize);
		setupShopInventory();
		setupShopInvDefaultStockItem();
		reloadShopItems();
	    } else {
		throw ShopSystemException.getException(ShopExceptionMessageEnum.RESIZING_FAILED);
	    }
	}
    }

    /**
     * Overridden, because of the naming convention.
     * <p>
     * name_owner
     * 
     * @throws GeneralEconomyException
     */
    @Override
    public void changeShopName(String name) throws ShopSystemException, GeneralEconomyException {
	checkForShopNameIsFree(name);
	checkForValidShopName(name);
	setupShopName(name);
	getShopVillager().setCustomName(name + "_" + owner.getName());
	changeInventoryNames(name);
    }

    /**
     * Overridden, because of the permission validation.
     */
    @Override
    public void moveShop(Location location) throws TownSystemException, PlayerException {
	if (TownworldController.isTownWorld(location.getWorld().getName())) {
	    Townworld townworld = null;
	    try {
		townworld = TownworldController.getTownWorldByName(location.getWorld().getName());
	    } catch (TownSystemException e) {
		// should never happen
	    }
	    if (townworld.isChunkFree(location.getChunk())) {
		throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
	    }
	    Town town = townworld.getTownByChunk(location.getChunk());
	    if (!town.hasBuildPermissions(owner,
		    town.getPlotByChunk(location.getChunk().getX() + "/" + location.getChunk().getZ()))) {
		throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
	    }
	}
	setupShopLocation(location);
	getShopVillager().teleport(location);
    }

    /**
     * Overridden, because if the reserved slots.
     */
    @Override
    public void openEditor(Player player) {
	// value = reserved slots
	int value = 2;
	for (int i = 0; i < (getSize() - value); i++) {
	    try {
		if (isSlotEmpty(i + 1)) {
		    getEditorInventory().setItem(i, getSkull(SLOTEMPTY, "Slot " + (i + 1)));
		} else {
		    getEditorInventory().setItem(i, getSkull(SLOTFILLED, "Slot " + (i + 1)));
		}
	    } catch (GeneralEconomyException e) {
	    }
	}
	player.openInventory(getEditorInventory());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// Save
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// file
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// edit
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// methods

    @Override
    public void increaseStock(String itemString, int stock) {
	if (stock >= 0) {
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	    config.set("ShopItems." + itemString + ".stock",
		    (config.getInt("ShopItems." + itemString + ".stock") + stock));
	    save(config);
	}
    }

    @Override
    public void decreaseStock(String itemString, int stock) {
	if (stock >= 0) {
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	    if ((config.getInt("ShopItems." + itemString + ".stock") - stock) >= 0) {
		config.set("ShopItems." + itemString + ".stock",
			(config.getInt("ShopItems." + itemString + ".stock") - stock));
	    }
	    save(config);
	}
    }

    /**
     * --Save file edit method--
     * <p>
     * NOT FOR COMMERCIAL USE.
     * <p>
     * Saves the owner to the savefile.
     * 
     * @param owner
     */
    protected void saveOwnerToFile(EconomyPlayer owner) {
	this.owner = owner;
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	if (owner == null) {
	    config.set("Owner", "");
	} else {
	    config.set("Owner", owner.getName());
	}
	save(config);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// save
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// file
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// read/
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// get
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// methods

    @Override
    public EconomyPlayer getOwner() {
	return owner;
    }

    /**
     * --Save file read method--
     * <p>
     * Loads the shop owner from the savefile.
     * 
     */
    private void loadOwner() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	try {
	    owner = EconomyPlayerController.getEconomyPlayerByName(config.getString("Owner"));
	} catch (PlayerException e) {
	}

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// change
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// methods

    @Override
    public void changeOwner(EconomyPlayer newOwner) throws PlayerException, ShopSystemException {
	checkForChangeOwnerIsPossible(newOwner);
	saveOwnerToFile(newOwner);
	getShopVillager().setCustomName(getName() + "_" + newOwner.getName());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// stockpile
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// methods

    @Override
    public void switchStockpile() {
	// switch to stockpile
	if (shopMode) {
	    shopMode = false;
	    getShopInventory().clear();
	    setupStockpile();
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
	    getShopInventory().setItem(getSize() - 1, stockpileSwitchItem);
	}
	// switch back to the shop
	else {
	    shopMode = true;
	    getShopInventory().clear();
	    for (String item : getItemList()) {
		if (!"ANVIL_0".equals(item) && !"CRAFTING_TABLE_0".equals(item)) {
		    try {
			loadShopItem(item);
		    } catch (ShopSystemException | PlayerException | GeneralEconomyException e) {
			Bukkit.getLogger().warning(e.getMessage());
		    }
		}
	    }
	    setupShopInvDefaultItems();
	    setupShopInvDefaultStockItem();
	    try {
		reloadShopItems();
	    } catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
		Bukkit.getLogger().warning("[Ultimate_Economy] Failed to switch back to the shop inventory");
		Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	    }
	}
    }

    @Override
    public void setupStockpile() {
	if (!shopMode) {
	    for (String item : getItemList()) {
		if (!"ANVIL_0".equals(item) && !"CRAFTING_TABLE_0".equals(item)) {
		    YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
		    ItemStack itemStack = config.getItemStack("ShopItems." + item + ".Name");
		    int slot = config.getInt("ShopItems." + item + ".Slot");
		    int stock = config.getInt("ShopItems." + item + ".stock");
		    ItemMeta meta = itemStack.getItemMeta();
		    List<String> list = new ArrayList<>();
		    if (meta.hasLore()) {
			list.addAll(meta.getLore());
		    }
		    if (stock != 1) {
			list.add(ChatColor.GREEN + String.valueOf(stock) + ChatColor.GOLD + " Items");
		    } else {
			list.add(ChatColor.GREEN + String.valueOf(stock) + ChatColor.GOLD + " Item");
		    }
		    meta.setLore(list);
		    itemStack.setItemMeta(meta);
		    getShopInventory().setItem(slot, itemStack);
		}
	    }
	}
    }

    @Override
    public boolean isAvailable(String itemString) {
	boolean available = false;
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	if (config.getInt("ShopItems." + itemString + ".stock") >= config
		.getInt("ShopItems." + itemString + ".Amount")) {
	    available = true;
	}
	return available;
    }

    @Override
    public boolean isOwner(EconomyPlayer ecoPlayer) {
	if (ecoPlayer.equals(owner)) {
	    return true;
	}
	return false;
    }

    /*
     * API methods
     * 
     */

    /*
     * Overridden, because of the stock value.
     */
    @Override
    public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack)
	    throws ShopSystemException, PlayerException, GeneralEconomyException {
	super.addShopItem(slot, sellPrice, buyPrice, itemStack);
	ItemStack itemStackCopy = new ItemStack(itemStack);
	String itemString = itemStackCopy.toString();

	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopItems." + itemString + ".stock", 0);
	save(config);
    }

    /*
     * Save methods
     * 
     */

    /*
     * Validation check methods
     * 
     */

    private void checkForChangeOwnerIsPossible(EconomyPlayer newOwner) throws ShopSystemException {
	if (PlayershopController.getPlayerShopUniqueNameList().contains(getName() + "_" + newOwner.getName())) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.SHOP_CHANGEOWNER_ERROR);
	}
    }

    private void checkForValidShopName(String name) throws ShopSystemException {
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
}