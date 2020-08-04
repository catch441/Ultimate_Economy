package com.ue.shopsystem.logic.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.logic.api.Playershop;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.townsystem.api.Town;
import com.ue.townsystem.api.Townworld;
import com.ue.townsystem.api.TownworldController;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;
import com.ue.ultimate_economy.UltimateEconomy;

public class PlayershopManagerImpl implements PlayershopManager {

	private List<Playershop> playerShopList = new ArrayList<>();

	private final MessageWrapper messageWrapper;
	private final ShopValidationHandler validationHandler;

	/**
	 * Inject constructor.
	 * 
	 * @param validationHandler
	 * @param messageWrapper
	 */
	@Inject
	public PlayershopManagerImpl(ShopValidationHandler validationHandler, MessageWrapper messageWrapper) {
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
	}

	@Override
	public String generateFreePlayerShopId() {
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

	@Override
	public List<String> getPlayerShopUniqueNameList() {
		List<String> list = new ArrayList<>();
		for (Playershop shop : getPlayerShops()) {
			list.add(shop.getName() + "_" + shop.getOwner().getName());
		}
		return list;
	}

	@Override
	public Playershop getPlayerShopByUniqueName(String name) throws GeneralEconomyException {
		for (Playershop shop : getPlayerShops()) {
			if (name.equals(shop.getName() + "_" + shop.getOwner().getName())) {
				return shop;
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
	}

	@Override
	public Playershop getPlayerShopById(String id) throws GeneralEconomyException {
		for (Playershop shop : getPlayerShops()) {
			if (shop.getShopId().equals(id)) {
				return shop;
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, id);
	}

	@Override
	public List<Playershop> getPlayerShops() {
		return playerShopList;
	}

	@Override
	public List<String> getPlayershopIdList() {
		List<String> list = new ArrayList<>();
		for (Playershop shop : getPlayerShops()) {
			list.add(shop.getShopId());
		}
		return list;
	}

	@Override
	public void createPlayerShop(String name, Location spawnLocation, int size, EconomyPlayer ecoPlayer)
			throws ShopSystemException, TownSystemException, EconomyPlayerException, GeneralEconomyException {
		validationHandler.checkForValidShopName(name);
		validationHandler.checkForMaxPlayershopsForPlayer(getPlayerShops(), ecoPlayer);
		checkForTownworldPlotPermission(spawnLocation, ecoPlayer);
		validationHandler.checkForShopNameIsFree(getPlayerShopUniqueNameList(), name, ecoPlayer);
		validationHandler.checkForValidSize(size);
		getPlayerShops().add(new PlayershopImpl(name, ecoPlayer, generateFreePlayerShopId(), spawnLocation, size));
		UltimateEconomy.getInstance.getConfig().set("PlayerShopIds", getPlayershopIdList());
		UltimateEconomy.getInstance.saveConfig();
	}

	@Override
	public void deletePlayerShop(Playershop playershop) {
		getPlayerShops().remove(playershop);
		playershop.deleteShop();
		// to make sure that all references are no more available
		playershop = null;
		UltimateEconomy.getInstance.getConfig().set("PlayerShopIds", getPlayershopIdList());
		UltimateEconomy.getInstance.saveConfig();
	}

	@Override
	public void despawnAllVillagers() {
		for (Playershop shop : getPlayerShops()) {
			shop.despawnVillager();
		}
	}

	@Override
	public void loadAllPlayerShops() {
		if (UltimateEconomy.getInstance.getConfig().contains("PlayerShopNames")) {
			playerShopsOldLoadingAll();
		} else {
			playerShopsNewLoadingAll();
		}
	}

	private void playerShopsNewLoadingAll() {
		for (String shopId : UltimateEconomy.getInstance.getConfig().getStringList("PlayerShopIds")) {
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), shopId + ".yml");
			if (file.exists()) {
				try {
					getPlayerShops().add(new PlayershopImpl(null, shopId));
				} catch (TownSystemException | EconomyPlayerException | GeneralEconomyException
						| ShopSystemException e) {
					Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopId);
					Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
				}
			} else {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopId);
			}
		}
	}

	@Deprecated
	private void playerShopsOldLoadingAll() {
		for (String shopName : UltimateEconomy.getInstance.getConfig().getStringList("PlayerShopNames")) {
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), shopName + ".yml");
			if (file.exists()) {
				String shopId = generateFreePlayerShopId();
				try {
					getPlayerShops().add(new PlayershopImpl(shopName, shopId));
				} catch (TownSystemException | EconomyPlayerException | GeneralEconomyException
						| ShopSystemException e) {
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

	/*
	 * TODO extract to townworld validation handler
	 */
	private void checkForTownworldPlotPermission(Location spawnLocation, EconomyPlayer ecoPlayer)
			throws EconomyPlayerException, TownSystemException {
		if (TownworldController.isTownWorld(spawnLocation.getWorld().getName())) {
			Townworld townworld = TownworldController.getTownWorldByName(spawnLocation.getWorld().getName());
			if (townworld.isChunkFree(spawnLocation.getChunk())) {
				throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.NO_PERMISSION);
			} else {
				Town town = townworld.getTownByChunk(spawnLocation.getChunk());
				if (!town.hasBuildPermissions(ecoPlayer,
						town.getPlotByChunk(spawnLocation.getChunk().getX() + "/" + spawnLocation.getChunk().getZ()))) {
					throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.NO_PERMISSION);
				}
			}
		}
	}
}
