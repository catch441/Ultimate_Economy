package com.ue.shopsystem.adminshop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.exceptions.ShopSystemException;
import com.ue.shopsystem.Shop;

import ultimate_economy.UEVillagerType;
import ultimate_economy.Ultimate_Economy;

public class AdminShop extends Shop {

	private static List<AdminShop> adminShopList = new ArrayList<>();

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
	private AdminShop(File dataFolder, String name, String shopId, Location spawnLocation, int size) {
		super(dataFolder, name, shopId, spawnLocation, size);
		// set the tye of the villager
		villager.setMetadata("ue-type", new FixedMetadataValue(Ultimate_Economy.getInstance, UEVillagerType.ADMINSHOP));
		for (String item : itemNames) {
			try {
				loadShopItem(item);
			} catch (ShopSystemException e) {
				Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
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
	 */
	private AdminShop(File dataFolder, Server server, String name, String shopId) {
		super(dataFolder, server, name, shopId);
		// set the tye of the villager
		villager.setMetadata("ue-type", new FixedMetadataValue(Ultimate_Economy.getInstance, UEVillagerType.ADMINSHOP));
		ArrayList<String> tempList = new ArrayList<>(itemNames);
		for (String item : tempList) {
			try {
				loadShopItem(item);
			} catch (ShopSystemException e) {
				Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
			}
		}
	}
	
	@Override
	public void changeShopName(String name) throws ShopSystemException {
		if(getAdminshopNameList().contains(name)) {
			throw new ShopSystemException(ShopSystemException.SHOP_ALREADY_EXISTS);
		} else if(name.contains("_")) {
			throw new ShopSystemException(ShopSystemException.INVALID_CHAR_IN_SHOP_NAME);
		} else {
			saveShopNameToFile(name);
			changeInventoryNames(name);
			villager.setCustomName(name);
		}
	}

	/**
	 * ONLY FOR THE COMMANDS
	 * 
	 * This method returns a AdminShop by it's name.
	 * 
	 * @param name
	 * @return
	 * @throws ShopSystemException
	 */
	public static AdminShop getAdminShopByName(String name) throws ShopSystemException {
		for (AdminShop shop : adminShopList) {
			if (shop.getName().equals(name)) {
				return shop;
			}
		}
		throw new ShopSystemException(ShopSystemException.SHOP_DOES_NOT_EXIST);
	}
	
	/**
	 * This method returns a AdminShop by it's id.
	 * 
	 * @param id
	 * @return
	 * @throws ShopSystemException
	 */
	public static AdminShop getAdminShopById(String id) throws ShopSystemException {
		for (AdminShop shop : adminShopList) {
			if (shop.getShopId().equals(id)) {
				return shop;
			}
		}
		throw new ShopSystemException(ShopSystemException.SHOP_DOES_NOT_EXIST);
	}
	
	/**
	 * Returns the list of adminshop ids.
	 * 
	 * @return List of Strings
	 */
	public static List<String> getAdminshopIdList() {
		List<String> list = new ArrayList<>();
		for(AdminShop shop: adminShopList) {
			list.add(shop.getShopId());
		}
		return list;
	}
	
	/**
	 * Returns the namelist of all adminshops
	 * 
	 * @return
	 */
	public static List<String> getAdminshopNameList() {
		List<String> list = new ArrayList<>();
		for(AdminShop shop: AdminShop.getAdminshopList()) {
			list.add(shop.getName());
		}
		return list;
	}
	
	/**
	 * Returns the adminshop list.
	 * @return
	 */
	public static List<AdminShop> getAdminshopList() {
		return adminShopList;
	}
	
	/**
	 * Returns a free unique id for a adminshop.
	 * 
	 * @return String
	 */
	public static String generateFreeAdminShopId() {
		int id = -1;
		boolean free = false;
		while(!free) {
			id++;
			if(!getAdminshopIdList().contains("A" + id)) {
				free = true;
			}
		}
		return "A" + id;
	}
	
	/**
	 * This method should be used to create a new adminshop.
	 * 
	 * @param dataFolder
	 * @param server
	 * @param name
	 * @param spawnLocation
	 * @param size
	 * @throws ShopSystemException
	 */
	public static void createAdminShop(File dataFolder, String name, Location spawnLocation, int size)
			throws ShopSystemException {
		if (name.contains("_")) {
			throw new ShopSystemException(ShopSystemException.INVALID_CHAR_IN_SHOP_NAME);
		} else if (getAdminshopNameList().contains(name)) {
			throw new ShopSystemException(ShopSystemException.SHOP_ALREADY_EXISTS);
		} else if (size % 9 != 0) {
			throw new ShopSystemException(ShopSystemException.INVALID_INVENTORY_SIZE);
		} else {
			adminShopList.add(new AdminShop(dataFolder, name, generateFreeAdminShopId(), spawnLocation, size));
		}
	}

	/**
	 * This method should be used to delete a adminshop.
	 * 
	 * @param name
	 * @throws ShopSystemException
	 */
	public static void deleteAdminShop(String name) throws ShopSystemException {
		AdminShop shop = getAdminShopByName(name);
		adminShopList.remove(shop);
		shop.deleteShop();
	}

	/**
	 * This method despawns all adminshop villager.
	 */
	public static void despawnAllVillagers() {
		for (AdminShop shop : adminShopList) {
			shop.despawnVillager();
		}
	}

	/**
	 * This method loads all adminShops.
	 * 
	 * @param fileConfig
	 * @param dataFolder
	 * @param server
	 */
	public static void loadAllAdminShops(FileConfiguration fileConfig, File dataFolder, Server server) {
		//old load system, can be deleted in the future
		if(fileConfig.contains("ShopNames")) {
			for (String shopName : fileConfig.getStringList("ShopNames")) {
				File file = new File(dataFolder, shopName + ".yml");
				if (file.exists()) {
					adminShopList.add(new AdminShop(dataFolder, server, shopName, generateFreeAdminShopId()));
				} else {
					Bukkit.getLogger().log(Level.WARNING, ShopSystemException.CANNOT_LOAD_SHOP,
							new ShopSystemException(ShopSystemException.CANNOT_LOAD_SHOP));
				}
			}
			//convert to new shopId save system
			fileConfig.set("ShopNames", null);
			fileConfig.set("AdminshopIds", AdminShop.getAdminshopIdList());
		} 
		//new load system
		else {
			for (String shopId : fileConfig.getStringList("AdminshopIds")) {
				File file = new File(dataFolder, shopId + ".yml");
				if (file.exists()) {
					adminShopList.add(new AdminShop(dataFolder, server,null, shopId));
				} else {
					Bukkit.getLogger().log(Level.WARNING, ShopSystemException.CANNOT_LOAD_SHOP,
							new ShopSystemException(ShopSystemException.CANNOT_LOAD_SHOP));
				}
			}
		}
	}
}