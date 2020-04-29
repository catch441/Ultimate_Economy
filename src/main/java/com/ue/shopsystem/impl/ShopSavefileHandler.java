package com.ue.shopsystem.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.ultimate_economy.UltimateEconomy;

public class ShopSavefileHandler {

    private File file;
    private YamlConfiguration config;

    protected ShopSavefileHandler(String id, boolean createNewFile) throws IOException {
	file = new File(UltimateEconomy.getInstance.getDataFolder(), id + ".yml");
	if (createNewFile) {
	    file.createNewFile();
	}
	config = YamlConfiguration.loadConfiguration(getSaveFile());
    }

    protected void saveShopName(String name) {
	getConfig().set("ShopName", name);
	save();
    }

    protected void saveShopItem(ShopItem shopItem,int slot, boolean delete) {
	if (delete) {
	    getConfig().set("ShopItems." + shopItem.getItemString(), null);
	    save();
	} else {
	    if (shopItem.getItemStack().getType() == Material.SPAWNER) {
		getConfig().set("ShopItems." + shopItem.getItemString() + ".Name", shopItem.getItemString());
	    } else {
		getConfig().set("ShopItems." + shopItem.getItemString() + ".Name", shopItem.getItemStack());
	    }
	    getConfig().set("ShopItems." + shopItem.getItemString() + ".Slot", slot);
	    getConfig().set("ShopItems." + shopItem.getItemString() + ".newSaveMethod", "true");
	    save();
	    saveShopItemSellPrice(shopItem.getItemString(), shopItem.getSellPrice());
	    saveShopItemBuyPrice(shopItem.getItemString(), shopItem.getBuyPrice());
	    saveShopItemAmount(shopItem.getItemString(), shopItem.getAmount());
	}
    }

    protected void saveShopItemSellPrice(String itemString, double sellPrice) {
	getConfig().set("ShopItems." + itemString + ".sellPrice", sellPrice);
	save();
    }

    protected void saveShopItemBuyPrice(String itemString, double buyPrice) {
	getConfig().set("ShopItems." + itemString + ".buyPrice", buyPrice);
	save();
    }

    protected void saveShopItemAmount(String itemString, int amount) {
	getConfig().set("ShopItems." + itemString + ".Amount", amount);
	save();
    }

    protected void saveShopSize(int size) {
	getConfig().set("ShopSize", size);
	save();
    }

    protected void saveShopLocation(Location location) {
	getConfig().set("ShopLocation.x", location.getX());
	getConfig().set("ShopLocation.y", location.getY());
	getConfig().set("ShopLocation.z", location.getZ());
	getConfig().set("ShopLocation.World", location.getWorld().getName());
	save();
    }

    protected void saveItemNames(List<String> itemList) {
	getConfig().set("ShopItemList", itemList);
	save();
    }

    protected void saveProfession(Profession profession) {
	getConfig().set("Profession", profession.name());
	save();
    }

    protected void saveStock(String itemString, int stock) {
	getConfig().set("ShopItems." + itemString + ".stock", stock);
	save();
    }

    protected void saveOwner(EconomyPlayer ecoPlayer) {
	if (ecoPlayer != null) {
	    getConfig().set("Owner", ecoPlayer.getName());
	} else {
	    getConfig().set("Owner", null);
	}
	save();
    }

    protected void saveRentUntil(long rentUntil) {
	getConfig().set("RentUntil", rentUntil);
	save();
    }

    protected void saveRentalFee(double fee) {
	getConfig().set("RentalFee", fee);
	save();
    }

    protected void saveRentable(boolean isRentable) {
	getConfig().set("Rentable", isRentable);
	save();
    }

    protected void changeSavefileName(File dataFolder, String newName) throws ShopSystemException {
	File newFile = new File(dataFolder, newName + ".yml");
	checkForRenamingSavefileIsPossible(newFile);
	getSaveFile().delete();
	file = newFile;
	save();
    }

    /*
     * Loading methods
     */

    protected Profession loadShopVillagerProfession() {
	if (getConfig().isSet("Profession")) {
	    return Profession.valueOf(config.getString("Profession"));
	} else {
	    return Profession.NITWIT;
	}
    }

    protected int loadShopSize() {
	return getConfig().getInt("ShopSize");
    }

    protected String loadShopName() {
	return getConfig().getString("ShopName");
    }

    protected Location loadShopLocation() throws TownSystemException {
	World world = Bukkit.getWorld(getConfig().getString("ShopLocation.World"));
	checkForWorldExists(world);
	return new Location(world, getConfig().getDouble("ShopLocation.x"), getConfig().getDouble("ShopLocation.y"),
		getConfig().getDouble("ShopLocation.z"));
    }

    protected ShopItem loadItem(String itemString) {
	ItemStack stack = null;
	if (itemString.contains("SPAWNER_")) {
	    stack = new ItemStack(Material.SPAWNER, 1);
	    ItemMeta meta = stack.getItemMeta();
	    meta.setDisplayName("COW");
	    stack.setItemMeta(meta);
	} else {
	    stack = getConfig().getItemStack("ShopItems." + itemString + ".Name");
	}
	int amount = getConfig().getInt("ShopItems." + itemString + ".Amount");
	double sellPrice = getConfig().getInt("ShopItems." + itemString + ".sellPrice");
	double buyPrice = getConfig().getInt("ShopItems." + itemString + ".buyPrice");
	return new ShopItem(stack, amount, sellPrice, buyPrice);
    }

    protected int loadItemSlot(String itemString) {
	return getConfig().getInt("ShopItems." + itemString + ".Slot");
    }

    protected List<String> loadItemNameList() {
	removeDefaultItemFromItemList();
	return getConfig().getStringList("ShopItemList");
    }

    protected int loadStock(String itemString) {
	return getConfig().getInt("ShopItems." + itemString + ".stock");
    }

    protected String loadOwner(String name) throws PlayerException {
	convertToNewOwnerSaving(name);
	return getConfig().getString("Owner");
    }

    protected boolean loadRentable() {
	return getConfig().getBoolean("Rentable");
    }

    protected long loadRentUntil() {
	return getConfig().getLong("RentUntil");
    }

    protected double loadRentalFee() {
	return getConfig().getInt("RentalFee");
    }

    private File getSaveFile() {
	return file;
    }

    private YamlConfiguration getConfig() {
	return config;
    }

    protected void save() {
	try {
	    getConfig().save(getSaveFile());
	} catch (IOException e) {
	    Bukkit.getLogger().warning("[Ultimate_Economy] Error on save config to file");
	    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	}
    }

    /**
     * Deletes the
     */
    protected void deleteFile() {
	getSaveFile().delete();
    }

    private void checkForRenamingSavefileIsPossible(File newFile) throws ShopSystemException {
	if (newFile.exists()) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.ERROR_ON_RENAMING);
	}
    }

    private void checkForWorldExists(World world) throws TownSystemException {
	if (world == null) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.WORLD_DOES_NOT_EXIST, "<unknown>");
	}
    }

    /*
     * Deprecated methods
     */

    /**
     * @deprecated can be removed later
     */
    @Deprecated
    private void convertToNewOwnerSaving(String name) {
	if (name != null) {
	    try {
		EconomyPlayer ecoPlayer = EconomyPlayerController
			.getEconomyPlayerByName(name.substring(name.indexOf("_") + 1));
		saveOwner(ecoPlayer);
	    } catch (PlayerException e) {
		Bukkit.getLogger().warning("[Ultimate_Economy] Error on save config to file");
		Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	    }
	}
    }

    /**
     * @since 1.2.6
     * @deprecated can be removed later
     */
    @Deprecated
    private void removeDefaultItemFromItemList() {
	List<String> list = getConfig().getStringList("ShopItemList");
	Iterator<String> iterator = list.iterator();
	while (iterator.hasNext()) {
	    String element = iterator.next();
	    if ("ANVIL_0".equals(element) || "CRAFTING_TABLE_0".equals(element)) {
		iterator.remove();
	    }
	}
	saveItemNames(list);
    }
}
