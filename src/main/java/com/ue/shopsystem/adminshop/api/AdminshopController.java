package com.ue.shopsystem.adminshop.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.language.MessageWrapper;
import com.ue.shopsystem.adminshop.api.AdminshopController;
import com.ue.shopsystem.adminshop.impl.AdminshopImpl;

public class AdminshopController {
	
	private static List<Adminshop> adminShopList = new ArrayList<>();

	/**
	 * ONLY FOR THE COMMANDS
	 * 
	 * This method returns a AdminShop by it's name.
	 * 
	 * @param name
	 * @return
	 * @throws ShopSystemException
	 */
	public static Adminshop getAdminShopByName(String name) throws ShopSystemException {
		for (Adminshop shop : adminShopList) {
			if (shop.getName().equals(name)) {
				return shop;
			}
		}
		throw ShopSystemException.getException(ShopExceptionMessageEnum.SHOP_DOES_NOT_EXIST);
	}
	
	/**
	 * This method returns a AdminShop by it's id.
	 * 
	 * @param id
	 * @return
	 * @throws ShopSystemException
	 */
	public static Adminshop getAdminShopById(String id) throws ShopSystemException {
		for (Adminshop shop : adminShopList) {
			if (shop.getShopId().equals(id)) {
				return shop;
			}
		}
		throw ShopSystemException.getException(ShopExceptionMessageEnum.SHOP_DOES_NOT_EXIST);
	}
	
	/**
	 * Returns the list of adminshop ids.
	 * 
	 * @return List of Strings
	 */
	public static List<String> getAdminshopIdList() {
		List<String> list = new ArrayList<>();
		for(Adminshop shop: adminShopList) {
			list.add(shop.getShopId());
		}
		return list;
	}
	
	public static List<String> getAdminshopNameList() {
		List<String> list = new ArrayList<>();
		for(Adminshop shop: getAdminshopList()) {
			list.add(shop.getName());
		}
		return list;
	}
	
	/**
	 * Returns the adminshop list.
	 * @return
	 */
	public static List<Adminshop> getAdminshopList() {
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
	 * @throws PlayerException 
	 */
	public static void createAdminShop(File dataFolder, String name, Location spawnLocation, int size)
			throws ShopSystemException, PlayerException {
		if (name.contains("_")) {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_CHAR_IN_SHOP_NAME);
		} else if (getAdminshopNameList().contains(name)) {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.SHOP_ALREADY_EXISTS);
		} else if (size % 9 != 0) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVALID_PARAMETER, size);
		} else {
			adminShopList.add(new AdminshopImpl(dataFolder, name, generateFreeAdminShopId(), spawnLocation, size));
		}
	}

	/**
	 * This method should be used to delete a adminshop.
	 * 
	 * @param adminshop
	 * @throws ShopSystemException
	 */
	public static void deleteAdminShop(Adminshop adminshop) throws ShopSystemException {
		adminShopList.remove(adminshop);
		adminshop.despawnVillager();
		adminshop.getSaveFile().delete();
		adminshop.getWorld().save();
		// to make sure that all references are no more available
		adminshop = null;
	}

	/**
	 * This method despawns all adminshop villager.
	 */
	public static void despawnAllVillagers() {
		for (Adminshop shop : adminShopList) {
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
					adminShopList.add(new AdminshopImpl(dataFolder, server, shopName, generateFreeAdminShopId()));
				} else {
					Bukkit.getLogger().info(MessageWrapper.getErrorString("cannot_load_shop", shopName));
				}
			}
			//convert to new shopId save system
			fileConfig.set("ShopNames", null);
			fileConfig.set("AdminShopIds", getAdminshopIdList());
		} 
		//new load system
		else {
			if(fileConfig.contains("AdminshopIds")) {
				fileConfig.set("AdminShopIds", fileConfig.get("AdminshopIds"));
			}
			for (String shopId : fileConfig.getStringList("AdminShopIds")) {
				File file = new File(dataFolder, shopId + ".yml");
				if (file.exists()) {
					adminShopList.add(new AdminshopImpl(dataFolder, server,null, shopId));
				} else {
					Bukkit.getLogger().info(MessageWrapper.getErrorString("cannot_load_shop", shopId));
				}
			}
		}
	}
}
