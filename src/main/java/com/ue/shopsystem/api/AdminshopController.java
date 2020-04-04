package com.ue.shopsystem.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.impl.AdminshopImpl;
import com.ue.ultimate_economy.UltimateEconomy;

public class AdminshopController {

    private static List<Adminshop> adminShopList = new ArrayList<>();

    /**
     * ONLY FOR THE COMMANDS
     * <p>
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
	throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
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
	throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, id);
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
     * 
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
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, name);
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
	adminshop.getSavefileHandler().getSaveFile().delete();
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
     */
    public static void loadAllAdminShops() {
	// old load system, can be deleted in the future
	if (UltimateEconomy.getInstance.getConfig().contains("ShopNames")) {
	    loadAllAdminshopsOld();
	}
	// new load system
	else {
	    loadAllAdminshopsNew();
	}
    }

    private static void loadAllAdminshopsNew() {
	// renaming, can be deleted later
	if (UltimateEconomy.getInstance.getConfig().contains("AdminshopIds")) {
	    UltimateEconomy.getInstance.getConfig().set("AdminShopIds",
		    UltimateEconomy.getInstance.getConfig().get("AdminshopIds"));
	}
	
	for (String shopId : UltimateEconomy.getInstance.getConfig().getStringList("AdminShopIds")) {
	    File file = new File(UltimateEconomy.getInstance.getDataFolder(), shopId + ".yml");
	    if (file.exists()) {
		try {
		    adminShopList.add(new AdminshopImpl(null, shopId));
		} catch (TownSystemException e) {
		    Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopId);
		    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	    } else {
		Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopId);
	    }
	}
    }

    @Deprecated
    private static void loadAllAdminshopsOld() {
	for (String shopName : UltimateEconomy.getInstance.getConfig().getStringList("ShopNames")) {
	    File file = new File(UltimateEconomy.getInstance.getDataFolder(), shopName + ".yml");
	    if (file.exists()) {
		try {
		    adminShopList.add(new AdminshopImpl(shopName, generateFreeAdminShopId()));
		} catch (TownSystemException e) {
		    Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopName);
		    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	    } else {
		Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopName);
	    }
	}
	// convert to new shopId save system
	UltimateEconomy.getInstance.getConfig().set("ShopNames", null);
	UltimateEconomy.getInstance.getConfig().set("AdminShopIds", getAdminshopIdList());
	UltimateEconomy.getInstance.saveConfig();
    }
}
