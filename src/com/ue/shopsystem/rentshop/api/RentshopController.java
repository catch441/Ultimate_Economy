package com.ue.shopsystem.rentshop.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.rentshop.impl.RentshopImpl;

public class RentshopController {

	private static List<RentshopImpl> rentShopList = new ArrayList<>();
	private static int maxRentedDays;
	
	/**
	 * Returns the max rented days.
	 * 
	 * @return int
	 */
	public static int getMaxRentedDays() {
		return maxRentedDays;
	}
	
	/**
	 * Returns a free unique id for a rentshop.
	 * 
	 * @return String
	 */
	public static String generateFreeRentShopId() {
		int id = -1;
		boolean free = false;
		while(!free) {
			id++;
			if(!getRentShopIdList().contains("R" + id)) {
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
		for (RentshopImpl shop : rentShopList) {
			list.add(shop.getShopId());
		}
		return list;
	}

	/**
	 * This method returns a rentshop by the shipId.
	 * 
	 * @param id
	 * @return RentShop
	 * @throws ShopSystemException
	 */
	public static RentshopImpl getRentShopById(String id) throws ShopSystemException {
		for (RentshopImpl shop : rentShopList) {
			if (shop.getShopId().equals(id)) {
				return shop;
			}
		}
		throw new ShopSystemException(ShopSystemException.SHOP_DOES_NOT_EXIST);
	}
	
	/**
	 * This method returns a rentshop by a unique name.
	 * <p>
	 * rented: name = name_owner unique name.
	 * <p>
	 * not rented: name = RentShop# + shopId
	 * 
	 * @param id
	 * @return RentShop
	 * @throws ShopSystemException
	 */
	public static RentshopImpl getRentShopByUniqueName(String name) throws ShopSystemException {
		for (RentshopImpl shop : rentShopList) {
			if(shop.isRentable()) {
				if (name.equals("RentShop#" + shop.getShopId())) {
					return shop;
				}
			} else {
				if (name.equals(shop.getName() + "_" + shop.getOwner())) {
					return shop;
				}
			}
		}
		throw new ShopSystemException(ShopSystemException.SHOP_DOES_NOT_EXIST);
	}
	
	/**
	 * This method returns a list of rentshop names.
	 * name = name_owner || RentShop# + id for unique names
	 * 
	 * @return List of Strings
	 */
	public static List<String> getRentShopUniqueNameList() {
		List<String> list = new ArrayList<>();
		for (RentshopImpl shop : rentShopList) {
			if(shop.isRentable()) {
				list.add("RentShop#" + shop.getShopId());
			} else {
				list.add(shop.getName() + "_" + shop.getOwner());
			}
		}
		return list;
	}
	
	/*
	 * Returns all rentshops
	 * @return List<RentShop>
	 */
	public static List<RentshopImpl> getRentShops() {
		return rentShopList;
	}

	/**
	 * This method should be used to create a new rentshop.
	 * 
	 * @param dataFolder
	 * @param spawnLocation
	 * @param size
	 * @param rentalFee
	 * @return RentShop
	 * @throws ShopSystemException
	 * @throws TownSystemException 
	 */
	public static RentshopImpl createRentShop(File dataFolder, Location spawnLocation, int size, double rentalFee)
			throws ShopSystemException, TownSystemException {		
		if (size % 9 != 0) {
			throw new ShopSystemException(ShopSystemException.INVALID_INVENTORY_SIZE);
		} else if(rentalFee < 0) {
			throw new ShopSystemException(PlayerException.INVALID_NUMBER);
		} else {
			RentshopImpl shop = new RentshopImpl(dataFolder, spawnLocation, size, generateFreeRentShopId(), rentalFee);
			rentShopList.add(shop);
			return shop;
		}
	}

	/**
	 * This method should be used to delete a rentshop.
	 * 
	 * @param id
	 * @throws ShopSystemException
	 */
	public static void deleteRentShop(String name) throws ShopSystemException {
		RentshopImpl shop = getRentShopByUniqueName(name);
		rentShopList.remove(shop);
		shop.deleteShop();
	}

	/**
	 * This method despawns all rentshop villager.
	 */
	public static void despawnAllVillagers() {
		for (RentshopImpl shop : rentShopList) {
			shop.despawnVillager();
		}
	}

	/**
	 * This method loads all rentShops.
	 * 
	 * @param fileConfig
	 * @param dataFolder
	 * @param server
	 */
	public static void loadAllRentShops(FileConfiguration fileConfig, File dataFolder, Server server) {
		for (String shopId : fileConfig.getStringList("RentShopIds")) {
			File file = new File(dataFolder, shopId + ".yml");
			if (file.exists()) {
				rentShopList.add(new RentshopImpl(dataFolder, server, shopId));
			} else {
				Bukkit.getLogger().log(Level.WARNING, ShopSystemException.CANNOT_LOAD_SHOP,
						new ShopSystemException(ShopSystemException.CANNOT_LOAD_SHOP));
			}
		}
	}
	
	/**
	 * This method sets the maxRentedDays value.
	 * 
	 * @param config
	 * @param days
	 * @throws PlayerException
	 */
	public static void setMaxRentedDays(FileConfiguration config,int days) throws PlayerException {
		if (days < 0) {
			throw new PlayerException(PlayerException.INVALID_NUMBER);
		} else {
			config.set("MaxRentedDays", days);
			maxRentedDays = days;
		}
	}
	
	/**
	 * Loads the maxRentedDays value.
	 * 
	 * @param fileConfig
	 */
	public static void setupConfig(FileConfiguration fileConfig) {
		if (!fileConfig.isSet("MaxRentedDays")) {
			fileConfig.set("MaxRentedDays", 14);
			maxRentedDays = 14;
		} else {
			maxRentedDays = fileConfig.getInt("MaxRentedDays");
		}
	}
}
