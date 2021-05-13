package org.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.ue.bank.logic.api.BankManager;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.api.EconomyPlayerValidator;
import org.ue.townsystem.dataaccess.api.TownworldDao;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.TownsystemValidator;
import org.ue.townsystem.logic.api.Townworld;
import org.ue.townsystem.logic.api.TownworldManager;

public class TownworldManagerImpl implements TownworldManager {

	private final EconomyPlayerManager ecoPlayerManager;
	private final MessageWrapper messageWrapper;
	private final ServerProvider serverProvider;
	private final CustomSkullService skullService;
	private final TownsystemValidator validationHandler;
	private final BankManager bankManager;
	private final ConfigDao configDao;
	private final EconomyPlayerValidator ecoPlayerValidator;

	private Map<String, Townworld> townWorldList = new HashMap<>();
	private List<String> townNameList = new ArrayList<>();

	@Inject
	public TownworldManagerImpl(ConfigDao configDao, BankManager bankManager, EconomyPlayerManager ecoPlayerManager,
			MessageWrapper messageWrapper, TownsystemValidator validationHandler, ServerProvider serverProvider,
			CustomSkullService skullService, EconomyPlayerValidator ecoPlayerValidator) {
		this.ecoPlayerManager = ecoPlayerManager;
		this.validationHandler = validationHandler;
		this.messageWrapper = messageWrapper;
		this.bankManager = bankManager;
		this.serverProvider = serverProvider;
		this.configDao = configDao;
		this.skullService = skullService;
		this.ecoPlayerValidator = ecoPlayerValidator;
	}

	protected void setTownNameList(List<String> townNameList) {
		this.townNameList = townNameList;
	}

	@Override
	public List<String> getTownNameList() {
		return new ArrayList<>(townNameList);
	}

	@Override
	public Townworld getTownWorldByName(String name) throws TownsystemException {
		for (Entry<String, Townworld> townworld : townWorldList.entrySet()) {
			if (townworld.getKey().equals(name)) {
				return townworld.getValue();
			}
		}
		throw new TownsystemException(messageWrapper, ExceptionMessageEnum.TOWNWORLD_DOES_NOT_EXIST);
	}

	@Override
	public Town getTownByName(String name) throws TownsystemException {
		for (Townworld world : townWorldList.values()) {
			for (Town town : world.getTownList()) {
				if (town.getTownName().equals(name)) {
					return town;
				}
			}
		}
		throw new TownsystemException(messageWrapper, ExceptionMessageEnum.DOES_NOT_EXIST, name);
	}

	@Override
	public List<Townworld> getTownWorldList() {
		return new ArrayList<Townworld>(townWorldList.values());
	}

	@Override
	public void despawnAllVillagers() {
		for (Townworld townworld : townWorldList.values()) {
			townworld.despawnAllTownVillagers();
		}
	}

	@Override
	public List<String> getTownWorldNameList() {
		return new ArrayList<>(townWorldList.keySet());
	}

	@Override
	public boolean isTownWorld(String worldName) {
		if (townWorldList.containsKey(worldName)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void performTownWorldLocationCheck(EconomyPlayer ecoPlayer, Location location) {
		if (ecoPlayer.isOnline()) {
			BossBar bossbar = ecoPlayer.getBossBar();
			Location loc = ecoPlayer.getPlayer().getLocation();
			if (location != null) {
				loc = location;
			}
			try {
				Townworld townworld = getTownWorldByName(loc.getWorld().getName());
				try {
					Town town = townworld.getTownByChunk(loc.getChunk());
					bossbar.setTitle(town.getTownName());
					bossbar.setColor(BarColor.RED);
					bossbar.setVisible(true);
				} catch (TownsystemException e) {
					// if chunk is in the wilderness
					bossbar.setTitle("Wilderness");
					bossbar.setColor(BarColor.GREEN);
					bossbar.setVisible(true);
				}
			} catch (TownsystemException e) {
				// disable bossbar in other worlds
				bossbar.setVisible(false);
			}
		}
	}

	@Override
	public void performTownworldLocationCheckAllPlayers() {
		for (EconomyPlayer ecoPlayer : ecoPlayerManager.getAllEconomyPlayers()) {
			performTownWorldLocationCheck(ecoPlayer, null);
		}
	}

	@Override
	public void createTownWorld(String world) throws TownsystemException {
		validationHandler.checkForWorldExists(world);
		validationHandler.checkForTownworldDoesNotExist(townWorldList, world);
		TownworldDao townworldDao = serverProvider.getServiceComponent().getTownworldDao();
		townworldDao.setupSavefile(world);
		townWorldList.put(world, new TownworldImpl(world, true, townworldDao, validationHandler, this, messageWrapper,
				bankManager, serverProvider, skullService, ecoPlayerValidator));
		configDao.saveTownworldNamesList(getTownWorldNameList());
	}

	@Override
	public void deleteTownWorld(String world) throws TownsystemException {
		validationHandler.checkForWorldExists(world);
		validationHandler.checkForTownworldExists(townWorldList, world);
		getTownWorldByName(world).delete();
		townWorldList.remove(world);
		configDao.saveTownworldNamesList(getTownWorldNameList());
	}

	@Override
	public void loadAllTownWorlds() {
		for (String townWorldName : configDao.loadTownworldNames()) {
			TownworldDao townworldDao = serverProvider.getServiceComponent().getTownworldDao();
			townworldDao.setupSavefile(townWorldName);
			Townworld townworld = new TownworldImpl(townWorldName, false, townworldDao, validationHandler, this,
					messageWrapper, bankManager, serverProvider, skullService, ecoPlayerValidator);
			townNameList.addAll(townworld.getTownNameList());
			townWorldList.put(townWorldName, townworld);
		}
	}
}
