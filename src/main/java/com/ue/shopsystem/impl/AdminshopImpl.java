package com.ue.shopsystem.impl;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.api.Adminshop;
import com.ue.shopsystem.controller.AdminshopController;
import com.ue.ultimate_economy.UltimateEconomy;

public class AdminshopImpl extends AbstractShopImpl implements Adminshop {

    /**
     * Constructor for creating a new adminShop. No validation, if the shopId is
     * unique.
     * 
     * @param name
     * @param shopId
     * @param spawnLocation
     * @param size
     */
    public AdminshopImpl(String name, String shopId, Location spawnLocation, int size) {
	super(name, shopId, spawnLocation, size);
	// set the tye of the villager
	villager.setMetadata("ue-type", new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.ADMINSHOP));
	for (String item : itemNames) {
	    try {
		loadShopItem(item);
	    } catch (ShopSystemException | PlayerException | GeneralEconomyException e) {
		Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
	    }
	}
    }

    /**
     * Constructor for loading an existing adminShop. No validation, if the shopId
     * is unique. If name != null then use old loading otherwise use new loading
     * 
     * @param dataFolder
     * @param name
     * @param shopId
     * @throws TownSystemException
     */
    public AdminshopImpl(File dataFolder, String name, String shopId) throws TownSystemException {
	super(dataFolder, name, shopId);
	// set the tye of the villager
	villager.setMetadata("ue-type", new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.ADMINSHOP));
	ArrayList<String> tempList = new ArrayList<>(itemNames);
	for (String item : tempList) {
	    try {
		loadShopItem(item);
	    } catch (ShopSystemException | PlayerException | GeneralEconomyException e) {
		Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
	    }
	}
    }

    @Override
    public void changeShopName(String name) throws ShopSystemException, GeneralEconomyException {
	if (AdminshopController.getAdminshopNameList().contains(name)) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS,name);
	} else if (name.contains("_")) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_CHAR_IN_SHOP_NAME);
	} else {
	    saveShopNameToFile(name);
	    changeInventoryNames(name);
	    villager.setCustomName(name);
	}
    }
}