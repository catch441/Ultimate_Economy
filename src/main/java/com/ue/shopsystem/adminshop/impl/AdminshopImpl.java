package com.ue.shopsystem.adminshop.impl;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.adminshop.api.Adminshop;
import com.ue.shopsystem.adminshop.api.AdminshopController;
import com.ue.shopsystem.impl.ShopImpl;
import com.ue.ultimate_economy.UEVillagerType;
import com.ue.ultimate_economy.Ultimate_Economy;

public class AdminshopImpl extends ShopImpl implements Adminshop{

	/**
	 * Constructor for creating a new adminShop.
	 * No validation, if the shopId is unique.
	 * 
	 * @param dataFolder
	 * @param name
	 * @param shopId
	 * @param spawnLocation
	 * @param size
	 */
	public AdminshopImpl(File dataFolder, String name, String shopId, Location spawnLocation, int size) {
		super(dataFolder, name, shopId, spawnLocation, size);
		// set the tye of the villager
		villager.setMetadata("ue-type", new FixedMetadataValue(Ultimate_Economy.getInstance, UEVillagerType.ADMINSHOP));
		for (String item : itemNames) {
			try {
				loadShopItem(item);
			} catch (ShopSystemException | PlayerException e) {
				Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
			}
		}
	}

	/**
	 * Constructor for loading an existing adminShop.
	 * No validation, if the shopId is unique.
	 * If name != null then use old loading otherwise use new loading
	 * 
	 * @param dataFolder
	 * @param server
	 * @param name
	 * @throws TownSystemException 
	 */
	public AdminshopImpl(File dataFolder, String name, String shopId) throws TownSystemException {
		super(dataFolder, name, shopId);
		// set the tye of the villager
		villager.setMetadata("ue-type", new FixedMetadataValue(Ultimate_Economy.getInstance, UEVillagerType.ADMINSHOP));
		ArrayList<String> tempList = new ArrayList<>(itemNames);
		for (String item : tempList) {
			try {
				loadShopItem(item);
			} catch (ShopSystemException | PlayerException e) {
				Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
			}
		}
	}
	
	@Override
	public void changeShopName(String name) throws ShopSystemException {
		if(AdminshopController.getAdminshopNameList().contains(name)) {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.SHOP_ALREADY_EXISTS);
		} else if(name.contains("_")) {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_CHAR_IN_SHOP_NAME);
		} else {
			saveShopNameToFile(name);
			changeInventoryNames(name);
			villager.setCustomName(name);
		}
	}
}