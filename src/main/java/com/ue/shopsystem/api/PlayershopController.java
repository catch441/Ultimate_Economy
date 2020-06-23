package com.ue.shopsystem.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.impl.PlayershopImpl;
import com.ue.townsystem.town.api.Town;
import com.ue.townsystem.townworld.api.Townworld;
import com.ue.townsystem.townworld.api.TownworldController;
import com.ue.ultimate_economy.UltimateEconomy;

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
		for (Playershop shop : getPlayerShops()) {
			list.add(shop.getName() + "_" + shop.getOwner().getName());
		}
		return list;
	}

	/**
	 * ONLY FOR COMMANDS This method returns a playershop by it's name. name =
	 * name_owner unique names
	 * 
	 * @param name
	 * @return PlayerShop
	 * @throws GeneralEconomyException
	 */
	public static Playershop getPlayerShopByUniqueName(String name) throws GeneralEconomyException {
		for (Playershop shop : getPlayerShops()) {
			if (name.equals(shop.getName() + "_" + shop.getOwner().getName())) {
				return shop;
			}
		}
		throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
	}

	/**
	 * This method returns a PlayerShop by it's id.
	 * 
	 * @param id
	 * @return PlayerShop
	 * @throws GeneralEconomyException
	 */
	public static Playershop getPlayerShopById(String id) throws GeneralEconomyException {
		for (Playershop shop : getPlayerShops()) {
			if (shop.getShopId().equals(id)) {
				return shop;
			}
		}
		throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, id);
	}

	/**
	 * Returns all player shops.
	 * 
	 * @return list of playershops
	 */
	public static List<Playershop> getPlayerShops() {
		return playerShopList;
	}

	/**
	 * Returns a list of all player shop ids.
	 * 
	 * @return list of playershop ids
	 */
	public static List<String> getPlayershopIdList() {
		List<String> list = new ArrayList<>();
		for (Playershop shop : getPlayerShops()) {
			list.add(shop.getShopId());
		}
		return list;
	}

	/**
	 * This method should be used to create a new playershop.
	 * 
	 * @param name
	 * @param spawnLocation
	 * @param size
	 * @param ecoPlayer
	 * @throws ShopSystemException
	 * @throws TownSystemException
	 * @throws PlayerException
	 * @throws GeneralEconomyException
	 */
	public static void createPlayerShop(String name, Location spawnLocation, int size, EconomyPlayer ecoPlayer)
			throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
		checkForValidShopName(name);
		checkForMaxPlayershopsForPlayer(ecoPlayer);
		checkForTownworldPlotPermission(spawnLocation, ecoPlayer);
		checkForUniqueShopnameForPlayer(name, ecoPlayer);
		checkForValidSize(size);

		getPlayerShops().add(new PlayershopImpl(name, ecoPlayer, generateFreePlayerShopId(), spawnLocation, size));
		UltimateEconomy.getInstance.getConfig().set("PlayerShopIds", PlayershopController.getPlayershopIdList());
		UltimateEconomy.getInstance.saveConfig();
	}

	/**
	 * This method should be used to delete a playershop.
	 * 
	 * @param playershop
	 * @throws ShopSystemException
	 */
	public static void deletePlayerShop(Playershop playershop) {
		getPlayerShops().remove(playershop);
		playershop.deleteShop();
		// to make sure that all references are no more available
		playershop = null;
		UltimateEconomy.getInstance.getConfig().set("PlayerShopIds", PlayershopController.getPlayershopIdList());
		UltimateEconomy.getInstance.saveConfig();
	}

	/**
	 * This method despawns all playershop villager.
	 */
	public static void despawnAllVillagers() {
		for (Playershop shop : getPlayerShops()) {
			shop.despawnVillager();
		}
	}

	/**
	 * This method loads all playerShops. EconomyPlayer have to be loaded first.
	 * 
	 */
	public static void loadAllPlayerShops() {
		if (UltimateEconomy.getInstance.getConfig().contains("PlayerShopNames")) {
			playerShopsOldLoadingAll();
		}
		// new load system
		else {
			playerShopsNewLoadingAll();
		}
	}

	private static void playerShopsNewLoadingAll() {
		for (String shopId : UltimateEconomy.getInstance.getConfig().getStringList("PlayerShopIds")) {
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), shopId + ".yml");
			if (file.exists()) {
				try {
					getPlayerShops().add(new PlayershopImpl(null, shopId));
				} catch (TownSystemException | PlayerException | GeneralEconomyException | ShopSystemException e) {
					Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopId);
					Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
				}
			} else {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopId);
			}
		}
	}

	@Deprecated
	private static void playerShopsOldLoadingAll() {
		for (String shopName : UltimateEconomy.getInstance.getConfig().getStringList("PlayerShopNames")) {
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), shopName + ".yml");
			if (file.exists()) {
				String shopId = generateFreePlayerShopId();
				try {
					getPlayerShops().add(new PlayershopImpl(shopName, shopId));
				} catch (TownSystemException | PlayerException | GeneralEconomyException | ShopSystemException e) {
					Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopName);
					Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
				}
			} else {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopName);
			}
		}
		// convert to new shopId save system
		UltimateEconomy.getInstance.getConfig().set("PlayerShopNames", null);
		UltimateEconomy.getInstance.getConfig().set("PlayerShopIds", getPlayershopIdList());
		UltimateEconomy.getInstance.saveConfig();
	}

	/*
	 * Validation check methods
	 * 
	 */

	private static void checkForValidSize(int size) throws GeneralEconomyException {
		if (size % 9 != 0) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, size);
		}
	}

	private static void checkForUniqueShopnameForPlayer(String name, EconomyPlayer ecoPlayer)
			throws GeneralEconomyException {
		if (getPlayerShopUniqueNameList().contains(name + "_" + ecoPlayer.getName())) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS,
					name + "_" + ecoPlayer.getName());
		}
	}

	private static void checkForTownworldPlotPermission(Location spawnLocation, EconomyPlayer ecoPlayer)
			throws PlayerException, TownSystemException {
		if (TownworldController.isTownWorld(spawnLocation.getWorld().getName())) {
			Townworld townworld = TownworldController.getTownWorldByName(spawnLocation.getWorld().getName());
			if (townworld.isChunkFree(spawnLocation.getChunk())) {
				throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
			} else {
				Town town = townworld.getTownByChunk(spawnLocation.getChunk());
				if (!town.hasBuildPermissions(ecoPlayer,
						town.getPlotByChunk(spawnLocation.getChunk().getX() + "/" + spawnLocation.getChunk().getZ()))) {
					throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
				}
			}
		}
	}

	private static void checkForMaxPlayershopsForPlayer(EconomyPlayer ecoPlayer) throws PlayerException {
		int actualNumber = 0;
		for (Playershop shop : PlayershopController.getPlayerShops()) {
			if (shop.isOwner(ecoPlayer)) {
				actualNumber++;
			}
		}
		if (actualNumber >= ConfigController.getMaxPlayershops()) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.MAX_REACHED);
		}
	}

	private static void checkForValidShopName(String name) throws ShopSystemException {
		if (name.contains("_")) {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_CHAR_IN_SHOP_NAME);
		}
	}
}
