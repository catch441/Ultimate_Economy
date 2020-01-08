package com.ue.shopsystem.playershop.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;

import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.playershop.impl.PlayershopImpl;
import com.ue.townsystem.town.api.Town;
import com.ue.townsystem.townworld.api.Townworld;
import com.ue.townsystem.townworld.api.TownworldController;

public class PlayershopController {

	private static List<Playershop> playerShopList = new ArrayList<>();

	/**
	 * Returns a free unique id for a playershop.
	 * 
	 * @return String
	 */
	public static String generateFreePlayerShopId() {
		int id = -1;
		boolean free = false;
		while (!free) {
			id++;
			if (!getPlayershopIdList().contains("P" + id)) {
				free = true;
			}
		}
		return "P" + id;
	}

	/**
	 * This method returns a list of playershop names. name = name_owner for unique
	 * names
	 * 
	 * @return List of Strings
	 */
	public static List<String> getPlayerShopUniqueNameList() {
		List<String> list = new ArrayList<>();
		for (Playershop shop : playerShopList) {
			list.add(shop.getName() + "_" + shop.getOwner());
		}
		return list;
	}

	/**
	 * ONLY FOR COMMANDS This method returns a playershop by it's name. name =
	 * name_owner unique names
	 * 
	 * @param name
	 * @return PlayerShop
	 * @throws ShopSystemException
	 */
	public static Playershop getPlayerShopByUniqueName(String name) throws ShopSystemException {
		for (Playershop shop : playerShopList) {
			if (name.equals(shop.getName() + "_" + shop.getOwner())) {
				return shop;
			}
		}
		throw new ShopSystemException(ShopSystemException.SHOP_DOES_NOT_EXIST);
	}

	/**
	 * This method returns a PlayerShop by it's id.
	 * 
	 * @param id
	 * @return PlayerShop
	 * @throws ShopSystemException
	 */
	public static Playershop getPlayerShopById(String id) throws ShopSystemException {
		for (Playershop shop : playerShopList) {
			if (shop.getShopId().equals(id)) {
				return shop;
			}
		}
		throw new ShopSystemException(ShopSystemException.SHOP_DOES_NOT_EXIST);
	}

	/*
	 * Returns all player shops
	 * 
	 * @return List<Playershop>
	 */
	public static List<Playershop> getPlayerShops() {
		return playerShopList;
	}

	/**
	 * Returns a list of all player shop ids.
	 * 
	 * @return
	 */
	public static List<String> getPlayershopIdList() {
		List<String> list = new ArrayList<>();
		for (Playershop shop : playerShopList) {
			list.add(shop.getShopId());
		}
		return list;
	}

	/**
	 * This method should be used to create a new playershop.
	 * 
	 * @param dataFolder
	 * @param name
	 * @param spawnLocation
	 * @param size
	 * @param playerName
	 * @throws ShopSystemException
	 * @throws TownSystemException
	 */
	public static void createPlayerShop(File dataFolder, String name, Location spawnLocation, int size,
			String playerName) throws ShopSystemException, TownSystemException {
		if (name.contains("_")) {
			throw new ShopSystemException(ShopSystemException.INVALID_CHAR_IN_SHOP_NAME);

		}
		if (TownworldController.isTownWorld(spawnLocation.getWorld().getName())) {
			Townworld townworld = null;
			try {
				townworld = TownworldController.getTownWorldByName(spawnLocation.getWorld().getName());
			} catch (TownSystemException e) {
				// should never happen
			}
			if (townworld.chunkIsFree(spawnLocation.getChunk())) {
				throw new TownSystemException(TownSystemException.PLAYER_HAS_NO_PERMISSION);
			} else {
				Town town = townworld.getTownByChunk(spawnLocation.getChunk());
				if (!town.hasBuildPermissions(playerName,
						town.getPlotByChunk(spawnLocation.getChunk().getX() + "/" + spawnLocation.getChunk().getZ()))) {
					throw new TownSystemException(TownSystemException.PLAYER_HAS_NO_PERMISSION);
				}
			}
		}
		if (getPlayerShopUniqueNameList().contains(name + "_" + playerName)) {
			throw new ShopSystemException(ShopSystemException.SHOP_ALREADY_EXISTS);
		} else if (size % 9 != 0) {
			throw new ShopSystemException(ShopSystemException.INVALID_INVENTORY_SIZE);
		} else {
			playerShopList.add(
					new PlayershopImpl(dataFolder, name, playerName, generateFreePlayerShopId(), spawnLocation, size));
		}
	}

	/**
	 * This method should be used to delete a playershop.
	 * 
	 * @param playershop
	 * @throws ShopSystemException
	 */
	public static void deletePlayerShop(Playershop playershop) throws ShopSystemException {
		playerShopList.remove(playershop);
		playershop.despawnVillager();
		playershop.getWorld().save();
		playershop.getSaveFile().delete();
		// to make sure that all references are no more available
		playershop = null;
	}

	/**
	 * This method despawns all playershop villager.
	 */
	public static void despawnAllVillagers() {
		for (Playershop shop : playerShopList) {
			shop.despawnVillager();
		}
	}

	/**
	 * This method loads all playerShops.
	 * 
	 * @param fileConfig
	 * @param dataFolder
	 * @param server
	 */
	public static void loadAllPlayerShops(FileConfiguration fileConfig, File dataFolder, Server server) {
		// old load system, can be deleted in the future
		if (fileConfig.contains("PlayerShopNames")) {
			for (String shopName : fileConfig.getStringList("PlayerShopNames")) {
				File file = new File(dataFolder, shopName + ".yml");
				if (file.exists()) {
					String shopId = generateFreePlayerShopId();
					playerShopList.add(new PlayershopImpl(dataFolder, server, shopName, shopId));
				} else {
					Bukkit.getLogger().log(Level.WARNING, ShopSystemException.CANNOT_LOAD_SHOP,
							new ShopSystemException(ShopSystemException.CANNOT_LOAD_SHOP));
				}
			}
			// convert to new shopId save system
			fileConfig.set("PlayerShopNames", null);
			fileConfig.set("PlayerShopIds", getPlayershopIdList());
		}
		// new load system
		else {
			for (String shopId : fileConfig.getStringList("PlayerShopIds")) {
				File file = new File(dataFolder, shopId + ".yml");
				if (file.exists()) {
					playerShopList.add(new PlayershopImpl(dataFolder, server, null, shopId));
				} else {
					Bukkit.getLogger().log(Level.WARNING, ShopSystemException.CANNOT_LOAD_SHOP,
							new ShopSystemException(ShopSystemException.CANNOT_LOAD_SHOP));
				}
			}
		}
	}
}
