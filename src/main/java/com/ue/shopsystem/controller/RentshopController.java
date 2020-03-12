package com.ue.shopsystem.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.shopsystem.api.Rentshop;
import com.ue.shopsystem.impl.RentshopImpl;
import com.ue.ultimate_economy.UltimateEconomy;

public class RentshopController {

    private static List<Rentshop> rentShopList = new ArrayList<>();

    /**
     * Returns a free unique id for a rentshop.
     * 
     * @return String
     */
    public static String generateFreeRentShopId() {
	int id = -1;
	boolean free = false;
	while (!free) {
	    id++;
	    if (!getRentShopIdList().contains("R" + id)) {
		free = true;
	    }
	}
	return "R" + id;
    }

    /**
     * This method returns a list of rentshop ids.
     * 
     * @return List of Strings
     */
    public static List<String> getRentShopIdList() {
	List<String> list = new ArrayList<>();
	for (Rentshop shop : rentShopList) {
	    list.add(shop.getShopId());
	}
	return list;
    }

    /**
     * This method returns a rentshop by the shipId.
     * 
     * @param id
     * @return RentShop
     * @throws GeneralEconomyException 
     */
    public static Rentshop getRentShopById(String id) throws GeneralEconomyException {
	for (Rentshop shop : rentShopList) {
	    if (shop.getShopId().equals(id)) {
		return shop;
	    }
	}
	throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST,id);
    }

    /**
     * This method returns a rentshop by a unique name.
     * <p>
     * rented: name = name_owner unique name.
     * <p>
     * not rented: name = RentShop# + shopId
     * 
     * @param name
     * @return RentShop
     * @throws GeneralEconomyException 
     */
    public static Rentshop getRentShopByUniqueName(String name) throws GeneralEconomyException {
	for (Rentshop shop : rentShopList) {
	    if (shop.isRentable()) {
		if (("RentShop#" + shop.getShopId()).equals(name)) {
		    return shop;
		}
	    } else {
		if (name.equals(shop.getName() + "_" + shop.getOwner().getName())) {
		    return shop;
		}
	    }
	}
	throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST,name);
    }

    /**
     * This method returns a list of rentshop names. name = name_owner || RentShop#
     * + id for unique names
     * 
     * @return List of Strings
     */
    public static List<String> getRentShopUniqueNameList() {
	List<String> list = new ArrayList<>();
	for (Rentshop shop : rentShopList) {
	    if (shop.isRentable()) {
		list.add("RentShop#" + shop.getShopId());
	    } else {
		list.add(shop.getName() + "_" + shop.getOwner().getName());
	    }
	}
	return list;
    }

    /**
     * Returns all rentshops.
     * 
     * @return list of rent shops
     */
    public static List<Rentshop> getRentShops() {
	return rentShopList;
    }

    /**
     * This method should be used to create a new rentshop.
     * 
     * @param spawnLocation
     * @param size
     * @param rentalFee
     * @return rentshop
     * @throws GeneralEconomyException
     */
    public static Rentshop createRentShop(Location spawnLocation, int size, double rentalFee)
	    throws GeneralEconomyException {
	if (size % 9 != 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, size);
	} else if (rentalFee < 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, rentalFee);
	} else {
	    Rentshop shop = new RentshopImpl(spawnLocation, size, generateFreeRentShopId(), rentalFee);
	    rentShopList.add(shop);
	    UltimateEconomy.getInstance.getConfig().set("RentShopIds", RentshopController.getRentShopIdList());
	    UltimateEconomy.getInstance.saveConfig();
	    return shop;
	}
    }

    /**
     * This method should be used to delete a rentshop.
     * 
     * @param rentshop
     * @throws ShopSystemException
     */
    public static void deleteRentShop(Rentshop rentshop) throws ShopSystemException {
	rentShopList.remove(rentshop);
	rentshop.despawnVillager();
	rentshop.getWorld().save();
	rentshop.getSaveFile().delete();
	// to make sure that all references are no more available
	rentshop = null;
	UltimateEconomy.getInstance.getConfig().set("RentShopIds", RentshopController.getRentShopIdList());
	UltimateEconomy.getInstance.saveConfig();
    }

    /**
     * This method despawns all rentshop villager.
     */
    public static void despawnAllVillagers() {
	for (Rentshop shop : rentShopList) {
	    shop.despawnVillager();
	}
    }

    /**
     * This method loads all rentShops. EconomyPlayers have to be loaded first.
     * 
     * @param fileConfig
     * @param dataFolder
     * @throws TownSystemException
     */
    public static void loadAllRentShops(FileConfiguration fileConfig, File dataFolder) {
	for (String shopId : fileConfig.getStringList("RentShopIds")) {
	    File file = new File(dataFolder, shopId + ".yml");
	    if (file.exists()) {
		try {
		    rentShopList.add(new RentshopImpl(dataFolder, shopId));
		} catch (TownSystemException e) {
		    Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
		    Bukkit.getLogger()
			    .warning("[Ultimate_Economy] " + MessageWrapper.getErrorString("cannot_load_shop", shopId));
		}
	    } else {
		Bukkit.getLogger()
			.warning("[Ultimate_Economy] " + MessageWrapper.getErrorString("cannot_load_shop", shopId));
	    }
	}
    }
}
