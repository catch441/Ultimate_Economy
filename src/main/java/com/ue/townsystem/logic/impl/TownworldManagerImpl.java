package com.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.bukkit.Chunk;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.townsystem.dataaccess.api.TownworldDao;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.townsystem.logic.api.TownworldManager;

public class TownworldManagerImpl implements TownworldManager {

	private final EconomyPlayerManager ecoPlayerManager;
	private final MessageWrapper messageWrapper;
	private final ServerProvider serverProvider;
	private final TownsystemValidationHandler townsystemValidationHandler;
	private final GeneralEconomyValidationHandler generalValidator;
	private final BankManager bankManager;
	private final EconomyPlayerValidationHandler ecoPlayerValidationHandler;
	private final ConfigDao configDao;

	private Map<String, Townworld> townWorldList = new HashMap<>();
	private List<String> townNameList = new ArrayList<>();

	@Inject
	public TownworldManagerImpl(ConfigDao configDao, EconomyPlayerValidationHandler ecoPlayerValidationHandler,
			BankManager bankManager, EconomyPlayerManager ecoPlayerManager, MessageWrapper messageWrapper,
			TownsystemValidationHandler townsystemValidationHandler, ServerProvider serverProvider,
			GeneralEconomyValidationHandler generalValidator) {
		this.ecoPlayerManager = ecoPlayerManager;
		this.townsystemValidationHandler = townsystemValidationHandler;
		this.messageWrapper = messageWrapper;
		this.bankManager = bankManager;
		this.ecoPlayerValidationHandler = ecoPlayerValidationHandler;
		this.serverProvider = serverProvider;
		this.configDao = configDao;
		this.generalValidator = generalValidator;
	}

	protected void setTownNameList(List<String> townNameList) {
		this.townNameList = townNameList;
	}

	@Override
	public List<String> getTownNameList() {
		return new ArrayList<>(townNameList);
	}

	@Override
	public Townworld getTownWorldByName(String name) throws TownSystemException {
		for (Entry<String, Townworld> townworld : townWorldList.entrySet()) {
			if (townworld.getKey().equals(name)) {
				return townworld.getValue();
			}
		}
		throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.TOWNWORLD_DOES_NOT_EXIST);
	}

	@Override
	public Town getTownByName(String name) throws GeneralEconomyException {
		for (Townworld world : getTownWorldList()) {
			for (Town town : world.getTownList()) {
				if (town.getTownName().equals(name)) {
					return town;
				}
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
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
	public void performTownWorldLocationCheck(String worldname, Chunk chunk, EconomyPlayer ecoPlayer) {
		BossBar bossbar = ecoPlayer.getBossBar();
		try {
			Townworld townworld = getTownWorldByName(worldname);
			try {
				Town town = townworld.getTownByChunk(chunk);
				bossbar.setTitle(town.getTownName());
				bossbar.setColor(BarColor.RED);
				bossbar.setVisible(true);
			} catch (TownSystemException e) {
				// if chunk is in the wilderness
				bossbar.setTitle("Wilderness");
				bossbar.setColor(BarColor.GREEN);
				bossbar.setVisible(true);
			}
		} catch (TownSystemException e) {
			// disable bossbar in other worlds
			bossbar.setVisible(false);
		}
	}

	@Override
	public void performTownworldLocationCheckAllPlayers() throws GeneralEconomyException {
		for (Player p : serverProvider.getOnlinePlayers()) {
			EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(p.getName());
			performTownWorldLocationCheck(p.getWorld().getName(), p.getLocation().getChunk(), ecoPlayer);
		}
	}

	@Override
	public void createTownWorld(String world) throws TownSystemException {
		townsystemValidationHandler.checkForWorldExists(world);
		townsystemValidationHandler.checkForTownworldDoesNotExist(townWorldList, world);
		TownworldDao townworldDao = serverProvider.getServiceComponent().getTownworldDao();
		townworldDao.setupSavefile(world);
		townWorldList.put(world, new TownworldImpl(world, true, townworldDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, this, messageWrapper, bankManager, serverProvider, generalValidator));
		configDao.saveTownworldNamesList(getTownWorldNameList());
	}

	@Override
	public void deleteTownWorld(String world)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		townsystemValidationHandler.checkForWorldExists(world);
		townsystemValidationHandler.checkForTownworldExists(townWorldList, world);
		getTownWorldByName(world).delete();
		townWorldList.remove(world);
		configDao.saveTownworldNamesList(getTownWorldNameList());
	}

	@Override
	public void loadAllTownWorlds() {
		for (String townWorldName : configDao.loadTownworldNames()) {
			TownworldDao townworldDao = serverProvider.getServiceComponent().getTownworldDao();
			townworldDao.setupSavefile(townWorldName);
			Townworld townworld = new TownworldImpl(townWorldName, false, townworldDao, townsystemValidationHandler,
					ecoPlayerValidationHandler, this, messageWrapper, bankManager, serverProvider, generalValidator);
			townNameList.addAll(townworld.getTownNameList());
			townWorldList.put(townWorldName, townworld);
		}
	}
}
