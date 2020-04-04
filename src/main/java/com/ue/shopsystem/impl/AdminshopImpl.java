package com.ue.shopsystem.impl;


import org.bukkit.Location;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.api.Adminshop;
import com.ue.shopsystem.api.AdminshopController;
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
	getShopVillager().setMetadata("ue-type",
		new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.ADMINSHOP));
    }

    /**
     * Constructor for loading an existing adminShop. No validation, if the shopId
     * is unique. If name != null then use old loading otherwise use new loading
     * 
     * @param name
     * @param shopId
     * @throws TownSystemException
     */
    public AdminshopImpl(String name, String shopId) throws TownSystemException {
	super(name, shopId);
	getShopVillager().setMetadata("ue-type",
		new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.ADMINSHOP));
    }
    
    /*
     * API methods
     * 
     */

    @Override
    public void changeShopName(String name) throws ShopSystemException, GeneralEconomyException {
	checkForShopNameDoesNotExist(name);
	checkForValidShopName(name);
	setName(name);
	getSavefileHandler().saveShopName(name);
	changeInventoryNames(name);
	getShopVillager().setCustomName(name);
    }

    /*
     * Validation check methods
     * 
     */

    private void checkForValidShopName(String name) throws ShopSystemException {
	if (name.contains("_")) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_CHAR_IN_SHOP_NAME);
	}
    }

    private void checkForShopNameDoesNotExist(String name) throws GeneralEconomyException {
	if (AdminshopController.getAdminshopNameList().contains(name)) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, name);
	}
    }
}