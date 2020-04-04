package com.ue.shopsystem.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.player.api.EconomyPlayer;
import com.ue.ultimate_economy.UltimateEconomy;

public class ShopSavefileManager {

    private File file;
    
    /**
     * Create a new savefile manager.
     * @param id
     * @param createNewFile
     * @throws IOException
     */
    public ShopSavefileManager(String id,boolean createNewFile) throws IOException {
	file = new File(UltimateEconomy.getInstance.getDataFolder(), id + ".yml");
	if(createNewFile) {
	    file.createNewFile();
	}
    }

    /**
     * Saves the shop name.
     * @param name
     */
    public void saveShopName(String name) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopName", name);
	save(config);
    }

    /**
     * Saves a shop item.
     * @param uniqueItemString
     * @param stack
     * @param slot
     * @param sellPrice
     * @param buyPrice
     * @param delete
     */
    public void saveShopItem(String uniqueItemString, ItemStack stack, int slot, double sellPrice, double buyPrice,
	    boolean delete) {
	ItemStack itemStackCopy = stack.clone();
	int amount = itemStackCopy.getAmount();
	itemStackCopy.setAmount(1);
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	if (delete) {
	    config.set("ShopItems." + uniqueItemString, null);
	    save(config);
	} else {
	    if (stack.getType() == Material.SPAWNER) {
		config.set("ShopItems." + uniqueItemString + ".Name", uniqueItemString);
	    } else {
		config.set("ShopItems." + uniqueItemString + ".Name", itemStackCopy);
	    }
	    config.set("ShopItems." + uniqueItemString + ".Slot", slot);
	    config.set("ShopItems." + uniqueItemString + ".newSaveMethod", "true");
	    save(config);
	    saveShopItemSellPrice(uniqueItemString, sellPrice);
	    saveShopItemBuyPrice(uniqueItemString, buyPrice);
	    saveShopItemAmount(uniqueItemString, amount);
	}
    }

    /**
     * Save item sell price.
     * @param itemString
     * @param sellPrice
     */
    public void saveShopItemSellPrice(String itemString, double sellPrice) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopItems." + itemString + ".sellPrice", sellPrice);
	save(config);
    }

    /**
     * Save item buy price.
     * @param itemString
     * @param buyPrice
     */
    public void saveShopItemBuyPrice(String itemString, double buyPrice) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopItems." + itemString + ".buyPrice", buyPrice);
	save(config);
    }

    /**
     * Save item amount.
     * @param itemString
     * @param amount
     */
    public void saveShopItemAmount(String itemString, int amount) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopItems." + itemString + ".Amount", amount);
	save(config);
    }

    /**
     * Saves the shop size.
     * @param size
     */
    public void saveShopSize(int size) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopSize", size);
	save(config);
    }

    /**
     * Saves the shop location.
     * @param location
     */
    public void saveShopLocation(Location location) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopLocation.x", location.getX());
	config.set("ShopLocation.y", location.getY());
	config.set("ShopLocation.z", location.getZ());
	config.set("ShopLocation.World", location.getWorld().getName());
	save(config);
    }

    /**
     * Saves the unique item name list.
     * @param itemList
     */
    public void saveItemNames(List<String> itemList) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopItemList", itemList);
	save(config);
    }

    /**
     * Saves the profession.
     * @param profession
     */
    public void saveProfession(Profession profession) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("Profession", profession.name());
	save(config);
    }
    
    /**
     * Saves the stock of a item.
     * @param itemString
     * @param stock
     * @throws GeneralEconomyException
     * @throws ShopSystemException
     */
    public void saveStock(String itemString, int stock) throws GeneralEconomyException, ShopSystemException {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopItems." + itemString + ".stock", stock);
	save(config);
    }

    /**
     * Saves the owner of the shop.
     * @param ecoPlayer
     */
    public void saveOwner(EconomyPlayer ecoPlayer) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	if (ecoPlayer != null) {
	    config.set("Owner", ecoPlayer.getName());
	} else {
	    config.set("Owner", null);
	}
	save(config);
    }
    
    /**
     * Saves rent until.
     * @param rentUntil
     */
    public void saveRentUntil(long rentUntil) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("RentUntil", rentUntil);
	save(config);
    }

    /**
     * Saves the rental fee.
     * @param fee
     */
    public void saveRentalFee(double fee) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("RentalFee", fee);
	save(config);
    }

    /**
     * Save is rentable.
     * @param isRentable
     */
    public void saveRentable(boolean isRentable) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("Rentable", isRentable);
	save(config);
    }

    /**
     * Changes the savefile name.
     * @param dataFolder
     * @param newName
     * @throws ShopSystemException
     */
    public void changeSavefileName(File dataFolder, String newName) throws ShopSystemException {
	File newFile = new File(dataFolder, newName + ".yml");
	checkForRenamingSavefileIsPossible(newFile);
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	getSaveFile().delete();
	file = newFile;
	save(config);
    }

    /**
     * Returns the save file.
     * @return file
     */
    public File getSaveFile() {
	return file;
    }

    /**
     * Saves a config into the save file.
     * @param config
     */
    public void save(YamlConfiguration config) {
	try {
	    config.save(getSaveFile());
	} catch (IOException e) {
	    Bukkit.getLogger().warning("[Ultimate_Economy] Error on save config to file");
	    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	}
    }

    /*
     * Validation methods
     * 
     */
    private void checkForRenamingSavefileIsPossible(File newFile) throws ShopSystemException {
	if (newFile.exists()) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.ERROR_ON_RENAMING);
	}
    }
}
