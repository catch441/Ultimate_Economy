package org.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.shopsystem.logic.api.Playershop;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.ShopValidator;
import org.ue.shopsystem.logic.api.ShopsystemException;

public class PlayershopManagerImpl implements PlayershopManager {

	private static final Logger log = LoggerFactory.getLogger(PlayershopManagerImpl.class);
	private final MessageWrapper messageWrapper;
	private final ShopValidator validationHandler;
	private final ConfigDao configDao;
	private final ServerProvider serverProvider;
	private Map<String, Playershop> playerShopList = new HashMap<>();

	public PlayershopManagerImpl(ConfigDao configDao, ShopValidator validationHandler,
			MessageWrapper messageWrapper, ServerProvider serverProvider) {
		this.configDao = configDao;
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		this.serverProvider = serverProvider;
	}

	@Override
	public String generateFreePlayerShopId() {
		int id = -1;
		boolean free = false;
		while (!free) {
			id++;
			if (!playerShopList.containsKey("P" + id)) {
				free = true;
			}
		}
		return "P" + id;
	}

	@Override
	public List<String> getPlayerShopUniqueNameList() {
		List<String> list = new ArrayList<>();
		for (Playershop shop : playerShopList.values()) {
			list.add(shop.getName() + "_" + shop.getOwner().getName());
		}
		return list;
	}

	@Override
	public Playershop getPlayerShopByUniqueName(String name) throws ShopsystemException {
		for (Playershop shop : playerShopList.values()) {
			if (name.equals(shop.getName() + "_" + shop.getOwner().getName())) {
				return shop;
			}
		}
		throw new ShopsystemException(messageWrapper, ExceptionMessageEnum.DOES_NOT_EXIST, name);
	}

	@Override
	public Playershop getPlayerShopById(String id) throws ShopsystemException {
		Playershop shop = playerShopList.get(id);
		validationHandler.checkForValueExists(shop, id);
		return shop;
	}

	@Override
	public List<Playershop> getPlayerShops() {
		return new ArrayList<>(playerShopList.values());
	}

	@Override
	public List<String> getPlayershopIdList() {
		return new ArrayList<>(playerShopList.keySet());
	}

	@Override
	public void createPlayerShop(String name, Location spawnLocation, int size, EconomyPlayer ecoPlayer)
			throws ShopsystemException {
		validationHandler.checkForValidShopName(name);
		validationHandler.checkForMaxPlayershopsForPlayer(getPlayerShops(), ecoPlayer);
		validationHandler.checkForPlayerHasPermissionAtLocation(spawnLocation, ecoPlayer);
		validationHandler.checkForShopNameIsFree(getPlayerShopUniqueNameList(), name, ecoPlayer);
		validationHandler.checkForValidSize(size);
		Playershop shop = serverProvider.getProvider().createPlayershop();
		shop.setupNew(name, ecoPlayer, generateFreePlayerShopId(), spawnLocation, size);
		playerShopList.put(shop.getId(), shop);
		configDao.savePlayershopIds(getPlayershopIdList());
	}

	@Override
	public void deletePlayerShop(Playershop playershop) {
		playerShopList.remove(playershop.getId());
		playershop.deleteShop();
		configDao.savePlayershopIds(getPlayershopIdList());
	}

	@Override
	public void despawnAllVillagers() {
		for (Playershop shop : playerShopList.values()) {
			shop.despawn();
		}
	}

	@Override
	public void loadAllPlayerShops() {
		for (String shopId : configDao.loadPlayershopIds()) {
			try {
				Playershop shop = serverProvider.getProvider().createPlayershop();
				shop.setupExisting(shopId);
				playerShopList.put(shopId, shop);
			} catch (EconomyPlayerException e) {
				log.warn("[Ultimate_Economy] Failed to load the shop " + shopId);
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}
}
