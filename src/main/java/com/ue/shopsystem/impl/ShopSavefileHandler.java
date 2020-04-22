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

public class ShopSavefileHandler {

    private File file;
    
    protected ShopSavefileHandler(String id,boolean createNewFile) throws IOException {
	file = new File(UltimateEconomy.getInstance.getDataFolder(), id + ".yml");
	if(createNewFile) {
	    file.createNewFile();
	}
    }

    protected void saveShopName(String name) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopName", name);
	save(config);
    }

    protected void saveShopItem(String uniqueItemString, ItemStack stack, int slot, double sellPrice, double buyPrice,
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

    protected void saveShopItemSellPrice(String itemString, double sellPrice) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopItems." + itemString + ".sellPrice", sellPrice);
	save(config);
    }

    protected void saveShopItemBuyPrice(String itemString, double buyPrice) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopItems." + itemString + ".buyPrice", buyPrice);
	save(config);
    }

    protected void saveShopItemAmount(String itemString, int amount) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopItems." + itemString + ".Amount", amount);
	save(config);
    }

    protected void saveShopSize(int size) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopSize", size);
	save(config);
    }

    protected void saveShopLocation(Location location) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopLocation.x", location.getX());
	config.set("ShopLocation.y", location.getY());
	config.set("ShopLocation.z", location.getZ());
	config.set("ShopLocation.World", location.getWorld().getName());
	save(config);
    }

    protected void saveItemNames(List<String> itemList) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopItemList", itemList);
	save(config);
    }

    protected void saveProfession(Profession profession) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("Profession", profession.name());
	save(config);
    }
    
    protected void saveStock(String itemString, int stock) throws GeneralEconomyException, ShopSystemException {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopItems." + itemString + ".stock", stock);
	save(config);
    }

    protected void saveOwner(EconomyPlayer ecoPlayer) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	if (ecoPlayer != null) {
	    config.set("Owner", ecoPlayer.getName());
	} else {
	    config.set("Owner", null);
	}
	save(config);
    }
    
    protected void saveRentUntil(long rentUntil) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("RentUntil", rentUntil);
	save(config);
    }

    protected void saveRentalFee(double fee) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("RentalFee", fee);
	save(config);
    }

    protected void saveRentable(boolean isRentable) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("Rentable", isRentable);
	save(config);
    }

    protected void changeSavefileName(File dataFolder, String newName) throws ShopSystemException {
	File newFile = new File(dataFolder, newName + ".yml");
	checkForRenamingSavefileIsPossible(newFile);
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	getSaveFile().delete();
	file = newFile;
	save(config);
    }

    /**
     * Retuns the savefile.
     * @return file
     */
    public File getSaveFile() {
	return file;
    }

    protected void save(YamlConfiguration config) {
	try {
	    config.save(getSaveFile());
	} catch (IOException e) {
	    Bukkit.getLogger().warning("[Ultimate_Economy] Error on save config to file");
	    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	}
    }

    private void checkForRenamingSavefileIsPossible(File newFile) throws ShopSystemException {
	if (newFile.exists()) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.ERROR_ON_RENAMING);
	}
    }
}
