package com.ue.shopsystem.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.shopsystem.api.Adminshop;
import com.ue.shopsystem.impl.AdminshopImpl;
import com.ue.ultimate_economy.UltimateEconomy;

public class AdminshopController {

    private static List<Adminshop> adminShopList = new ArrayList<>();

    /**
     * ONLY FOR THE COMMANDS
     * 
     * This method returns a AdminShop by it's name.
     * 
     * @param name
     * @return adminshop
     * @throws GeneralEconomyException 
     */
    public static Adminshop getAdminShopByName(String name) throws GeneralEconomyException {
	for (Adminshop shop : adminShopList) {
	    if (shop.getName().equals(name)) {
		return shop;
	    }
	}	
	throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST,name);
    }

    /**
     * This method returns a AdminShop by it's id.
     * 
     * @param id
     * @return adminshop
     * @throws GeneralEconomyException 
     */
    public static Adminshop getAdminShopById(String id) throws GeneralEconomyException {
	for (Adminshop shop : adminShopList) {
	    if (shop.getShopId().equals(id)) {
		return shop;
	    }
	}
	throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST,id);
    }

    /**
     * Returns the list of adminshop ids.
     * 
     * @return List of Strings
     */
    public static List<String> getAdminshopIdList() {
	List<String> list = new ArrayList<>();
	for (Adminshop shop : adminShopList) {
	    list.add(shop.getShopId());
	}
	return list;
    }

    /**
     * Returns a list of all adminshop names.
     * @return list of adminshop names
     */
    public static List<String> getAdminshopNameList() {
	List<String> list = new ArrayList<>();
	for (Adminshop shop : getAdminshopList()) {
	    list.add(shop.getName());
	}
	return list;
    }

    /**
     * Returns the adminshop list.
     * 
     * @return list of adminshops
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
	while (!free) {
	    id++;
	    if (!getAdminshopIdList().contains("A" + id)) {
		free = true;
	    }
	}
	return "A" + id;
    }

    /**
     * This method should be used to create a new adminshop.
     * 
     * @param name
     * @param spawnLocation
     * @param size
     * @throws ShopSystemException
     * @throws GeneralEconomyException
     */
    public static void createAdminShop(String name, Location spawnLocation, int size)
	    throws ShopSystemException, GeneralEconomyException {
	if (name.contains("_")) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_CHAR_IN_SHOP_NAME);
	} else if (getAdminshopNameList().contains(name)) {
		throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST,name);
	} else if (size % 9 != 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, size);
	} else {
	    adminShopList.add(new AdminshopImpl(name, generateFreeAdminShopId(), spawnLocation, size));
	    UltimateEconomy.getInstance.getConfig().set("AdminShopIds", AdminshopController.getAdminshopIdList());
	    UltimateEconomy.getInstance.saveConfig();
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
	UltimateEconomy.getInstance.getConfig().set("AdminShopIds", AdminshopController.getAdminshopIdList());
	UltimateEconomy.getInstance.saveConfig();
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
     */
    public static void loadAllAdminShops(FileConfiguration fileConfig, File dataFolder) {
	// old load system, can be deleted in the future
	if (fileConfig.contains("ShopNames")) {
	    loadAllAdminshopsOld(fileConfig, dataFolder);
	}
	// new load system
	else {
	    loadAllAdminshopsNew(fileConfig, dataFolder);
	}
    }

    private static void loadAllAdminshopsNew(FileConfiguration fileConfig, File dataFolder) {
	if (fileConfig.contains("AdminshopIds")) {
	fileConfig.set("AdminShopIds", fileConfig.get("AdminshopIds"));
	}
	for (String shopId : fileConfig.getStringList("AdminShopIds")) {
	File file = new File(dataFolder, shopId + ".yml");
	if (file.exists()) {
	    try {
		adminShopList.add(new AdminshopImpl(dataFolder, null, shopId));
	    } catch (TownSystemException e) {
		Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
		Bukkit.getLogger().warning(
			"[Ultimate_Economy] " + MessageWrapper.getErrorString("cannot_load_shop", shopId));
	    }
	} else {
	    Bukkit.getLogger()
		    .warning("[Ultimate_Economy] " + MessageWrapper.getErrorString("cannot_load_shop", shopId));
	}
	}
    }

    @Deprecated
    private static void loadAllAdminshopsOld(FileConfiguration fileConfig, File dataFolder) {
	for (String shopName : fileConfig.getStringList("ShopNames")) {
	File file = new File(dataFolder, shopName + ".yml");
	if (file.exists()) {
	    try {
		adminShopList.add(new AdminshopImpl(dataFolder, shopName, generateFreeAdminShopId()));
	    } catch (TownSystemException e) {
		Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
		Bukkit.getLogger().warning(
			"[Ultimate_Economy] " + MessageWrapper.getErrorString("cannot_load_shop", shopName));
	    }
	} else {
	    Bukkit.getLogger().warning(
		    "[Ultimate_Economy] " + MessageWrapper.getErrorString("cannot_load_shop", shopName));
	}
	}
	// convert to new shopId save system
	fileConfig.set("ShopNames", null);
	fileConfig.set("AdminShopIds", getAdminshopIdList());
    }
}
