package com.ue.shopsystem.impl;

import java.io.File;
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
	// neccecsary for rentshops without a owner
	if(owner != null) {
	    saveOwnerToFile(owner);
	    villager.setCustomName(name + "_" + owner.getName());
	}
	// set the type of the villager
	villager.setMetadata("ue-type",
		new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.PLAYERSHOP));
    }

    /**
     * Constructor for loading an existing playershop. No validation, if the shopId
     * is unique. If name != null then use old loading otherwise use new loading.
     * 
     * @param dataFolder
     * @param name
     * @param shopId
     * @throws TownSystemException
     */
    public PlayershopImpl(File dataFolder, String name, String shopId) throws TownSystemException {
	super(dataFolder, name, shopId);
	shopMode = true;
	try {
	    // old loading, can be deleted in the future
	    if (name != null) {
		saveOwnerToFile(EconomyPlayerController.getEconomyPlayerByName(name.substring(name.indexOf("_") + 1)));
		saveShopNameToFile(name.substring(0, name.indexOf("_")));

	    }
	    // new loading
	    else {
		loadOwner();
	    }
	} catch (PlayerException e) {
	}
	// set the type of the villager
	villager.setMetadata("ue-type",
		new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.PLAYERSHOP));
	// update villager name to naming convention
	if (owner == null) {
	    villager.setCustomName(getName() + "_");
	} else {
	    villager.setCustomName(getName() + "_" + owner.getName());
	}
	// load shop items
	for (String item : itemNames) {
	    try {
		loadShopItem(item);
	    } catch (ShopSystemException | PlayerException | GeneralEconomyException e) {
		Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
	    }
	}
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////// overridden

    /**
     * Overridden, because of the stock item.
     */
    @Override
    public void setupShopItems() {
	super.setupShopItems();
	ItemStack itemStack = new ItemStack(Material.CRAFTING_TABLE);
	ItemMeta meta = itemStack.getItemMeta();
	meta.setDisplayName("Stock");
	itemStack.setItemMeta(meta);
	addShopItemToInv(itemStack, 1, size - 2, 0.0, 0.0);
	itemNames.add("CRAFTING_TABLE_0");
    }

    /**
     * Overridden, because of the stock value.
     * 
     * @throws PlayerException
     * @throws GeneralEconomyException
     */
    @Override
    public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack)
	    throws ShopSystemException, PlayerException, GeneralEconomyException {
	super.addShopItem(slot, sellPrice, buyPrice, itemStack);
	// create a new ItemStack to avoid changes to the original stack
	ItemStack itemStackCopy = new ItemStack(itemStack);
	String itemString = itemStackCopy.toString();
	// set and save shop item stock to 0
	config = YamlConfiguration.loadConfiguration(file);
	config.set("ShopItems." + itemString + ".stock", 0);
	save();
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
	    int diff = size - newSize;
	    // number or reserved slots
	    int temp = 2;
	    if (inventory.getSize() > newSize) {
		for (int i = 1; i <= diff; i++) {
		    ItemStack stack = inventory.getItem(size - i - temp);
		    if (stack != null) {
			possible = false;
		    }
		}
	    }
	    if (possible) {
		config = YamlConfiguration.loadConfiguration(file);
		saveShopSizeToFile(newSize);
		inventory = Bukkit.createInventory(null, size, getName());
		reload();
		setupShopItems();
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
	if (PlayershopController.getPlayerShopUniqueNameList().contains(name + owner.getName())
		|| PlayershopController.getPlayerShopUniqueNameList().contains(name)) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS,
		    name + owner.getName());
	} else if (name.contains("_")) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_CHAR_IN_SHOP_NAME);
	} else {
	    saveShopNameToFile(name);
	    villager.setCustomName(name + "_" + owner.getName());
	    changeInventoryNames(name);
	}
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
	    } else {
		Town town = townworld.getTownByChunk(location.getChunk());
		if (!town.hasBuildPermissions(owner,
			town.getPlotByChunk(location.getChunk().getX() + "/" + location.getChunk().getZ()))) {
		    throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
		}
	    }
	}
	saveLocationToFile(location);
	villager.teleport(location);
    }

    /**
     * Overridden, because if the reserved slots.
     */
    @Override
    public void openEditor(Player player) {
	// value = reserved slots
	int value = 2;
	for (int i = 0; i < (size - value); i++) {
	    try {
		if (slotIsEmpty(i + 1)) {
		    editor.setItem(i, getSkull(SLOTEMPTY, "Slot " + (i + 1)));
		} else {
		    editor.setItem(i, getSkull(SLOTFILLED, "Slot " + (i + 1)));
		}
	    } catch (GeneralEconomyException e) {
	    }
	}
	player.openInventory(editor);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// Save
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// file
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// edit
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// methods

    @Override
    public void increaseStock(String itemString, int stock) {
	if (stock >= 0) {
	    config = YamlConfiguration.loadConfiguration(file);
	    config.set("ShopItems." + itemString + ".stock",
		    (config.getInt("ShopItems." + itemString + ".stock") + stock));
	    save();
	}
    }

    @Override
    public void decreaseStock(String itemString, int stock) {
	if (stock >= 0) {
	    config = YamlConfiguration.loadConfiguration(file);
	    if ((config.getInt("ShopItems." + itemString + ".stock") - stock) >= 0) {
		config.set("ShopItems." + itemString + ".stock",
			(config.getInt("ShopItems." + itemString + ".stock") - stock));
	    }
	    save();
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
	config = YamlConfiguration.loadConfiguration(file);
	if(owner == null) {
	    config.set("Owner", "");
	} else {
	    config.set("Owner", owner.getName());
	}
	save();
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
	config = YamlConfiguration.loadConfiguration(file);
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
	// validation, check if the new owner has already a shop with this name.
	if (PlayershopController.getPlayerShopUniqueNameList().contains(getName() + "_" + newOwner)) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.SHOP_CHANGEOWNER_ERROR);
	} else {
	    saveOwnerToFile(newOwner);
	    villager.setCustomName(getName() + "_" + newOwner.getName());
	}
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// stockpile
    ////////////////////////////////////////////////////////////////////////////////////////////////////////// methods

    @Override
    public void switchStockpile() {
	// switch to stockpile
	if (shopMode) {
	    shopMode = false;
	    inventory.clear();
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
	    inventory.setItem(size - 1, stockpileSwitchItem);
	}
	// switch back to the shop
	else {
	    shopMode = true;
	    inventory.clear();
	    for (String item : itemNames) {
		if (!"ANVIL_0".equals(item) && !"CRAFTING_TABLE_0".equals(item)) {
		    try {
			loadShopItem(item);
		    } catch (ShopSystemException | PlayerException | GeneralEconomyException e) {
			Bukkit.getLogger().warning(e.getMessage());
		    }
		}
	    }
	    setupShopItems();
	}
    }

    @Override
    public void setupStockpile() {
	if (!shopMode) {
	    for (String item : itemNames) {
		if (!"ANVIL_0".equals(item) && !"CRAFTING_TABLE_0".equals(item)) {
		    config = YamlConfiguration.loadConfiguration(file);
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
		    inventory.setItem(slot, itemStack);
		}
	    }
	}
    }

    @Override
    public boolean isAvailable(String itemString) {
	boolean available = false;
	config = YamlConfiguration.loadConfiguration(file);
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
}