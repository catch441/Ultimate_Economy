package com.ue.shopsystem.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
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
	throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, id);
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
	throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
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
	checkForValidSize(size);
	checkForPositiveValue(rentalFee);
	Rentshop shop = new RentshopImpl(spawnLocation, size, generateFreeRentShopId(), rentalFee);
	rentShopList.add(shop);
	UltimateEconomy.getInstance.getConfig().set("RentShopIds", RentshopController.getRentShopIdList());
	UltimateEconomy.getInstance.saveConfig();
	return shop;
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
	rentshop.getSavefileHandler().getSaveFile().delete();
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
     * @throws TownSystemException
     */
    public static void loadAllRentShops() {
	for (String shopId : UltimateEconomy.getInstance.getConfig().getStringList("RentShopIds")) {
	    File file = new File(UltimateEconomy.getInstance.getDataFolder(), shopId + ".yml");
	    if (file.exists()) {
		try {
		    rentShopList.add(new RentshopImpl(shopId));
		} catch (TownSystemException | PlayerException | GeneralEconomyException | ShopSystemException e) {
		    Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopId);
		    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	    } else {
		Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopId);
	    }
	}
    }

    /*
     * Validation checks
     * 
     */
    
    private static void checkForPositiveValue(double value) throws GeneralEconomyException {
	if (value < 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, value);
	}
    }

    private static void checkForValidSize(int size) throws GeneralEconomyException {
	if (size % 9 != 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, size);
	}
    }
}
