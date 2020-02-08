package com.ue.shopsystem.playershop.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.ue.config.api.ConfigController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyMessageEnum;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.player.api.EconomyPlayer;
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
     * @throws ShopSystemException
     */
    public static Playershop getPlayerShopByUniqueName(String name) throws ShopSystemException {
	for (Playershop shop : playerShopList) {
	    if (name.equals(shop.getName() + "_" + shop.getOwner().getName())) {
		return shop;
	    }
	}
	throw ShopSystemException.getException(ShopExceptionMessageEnum.SHOP_DOES_NOT_EXIST);
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
	throw ShopSystemException.getException(ShopExceptionMessageEnum.SHOP_DOES_NOT_EXIST);
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
     * @param ecoPlayer
     * @throws ShopSystemException
     * @throws TownSystemException
     * @throws PlayerException
     * @throws GeneralEconomyException
     */
    public static void createPlayerShop(File dataFolder, String name, Location spawnLocation, int size,
	    EconomyPlayer ecoPlayer)
	    throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
	checkForValidShopName(name);
	checkForMaxPlayershopsForPlayer(ecoPlayer);
	checkForTownworldPlotPermission(spawnLocation, ecoPlayer);
	checkForUniqueShopnameForPlayer(name, ecoPlayer); 
	checkForValidSize(size);
	
	playerShopList.add(
		    new PlayershopImpl(dataFolder, name, ecoPlayer, generateFreePlayerShopId(), spawnLocation, size));
    }

    private static void checkForValidSize(int size) throws GeneralEconomyException {
	if (size % 9 != 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, size);
	}
    }

    private static void checkForUniqueShopnameForPlayer(String name, EconomyPlayer ecoPlayer)
	    throws ShopSystemException {
	if (getPlayerShopUniqueNameList().contains(name + "_" + ecoPlayer.getName())) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.SHOP_ALREADY_EXISTS);
	}
    }

    private static void checkForTownworldPlotPermission(Location spawnLocation, EconomyPlayer ecoPlayer)
	    throws PlayerException, TownSystemException {
	if (TownworldController.isTownWorld(spawnLocation.getWorld().getName())) {
	    Townworld townworld = null;
	    try {
		townworld = TownworldController.getTownWorldByName(spawnLocation.getWorld().getName());
	    } catch (TownSystemException e) {
		// should never happen
	    }
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
     * This method loads all playerShops. EconomyPlayer have to be loaded first.
     * 
     * @param fileConfig
     * @param dataFolder
     */
    public static void loadAllPlayerShops(FileConfiguration fileConfig, File dataFolder) {
	// old load system, can be deleted in the future
	if (fileConfig.contains("PlayerShopNames")) {
	    playerShopsOldLoadingAll(fileConfig, dataFolder);
	}
	// new load system
	else {
	    playerShopsNewLoadingAll(fileConfig, dataFolder);
	}
    }

    private static void playerShopsNewLoadingAll(FileConfiguration fileConfig, File dataFolder) {
	for (String shopId : fileConfig.getStringList("PlayerShopIds")) {
	File file = new File(dataFolder, shopId + ".yml");
	if (file.exists()) {
	    try {
		playerShopList.add(new PlayershopImpl(dataFolder, null, shopId));
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
    private static void playerShopsOldLoadingAll(FileConfiguration fileConfig, File dataFolder) {
	for (String shopName : fileConfig.getStringList("PlayerShopNames")) {
	File file = new File(dataFolder, shopName + ".yml");
	if (file.exists()) {
	    String shopId = generateFreePlayerShopId();
	    try {
		playerShopList.add(new PlayershopImpl(dataFolder, shopName, shopId));
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
	fileConfig.set("PlayerShopNames", null);
	fileConfig.set("PlayerShopIds", getPlayershopIdList());
    }
}
