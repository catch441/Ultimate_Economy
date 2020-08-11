package com.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.townsystem.dataaccess.api.TownsystemDao;
import com.ue.townsystem.dataaccess.impl.TownsystemDaoImpl;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.townsystem.logic.api.TownworldManager;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;
import com.ue.ultimate_economy.UltimateEconomy;

public class TownworldManagerImpl implements TownworldManager {

	@Inject
	EconomyPlayerManager ecoPlayerManager;
	@Inject
	MessageWrapper messageWrapper;
	@Inject
	TownsystemValidationHandler townsystemValidationHandler;
	private Map<String, Townworld> townWorldList = new HashMap<>();
	private List<String> townNameList = new ArrayList<>();

	protected void setTownNameList(List<String> townNameList) {
		this.townNameList = townNameList;
	}

	@Override
	public List<String> getTownNameList() {
		return townNameList;
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
	public void performTownworldLocationCheckAllPlayers() throws EconomyPlayerException {
		for (Player p : Bukkit.getOnlinePlayers()) {
			EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(p.getName());
			performTownWorldLocationCheck(p.getWorld().getName(), p.getLocation().getChunk(),
					ecoPlayer);
		}
	}

	@Override
	public void createTownWorld(String world)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		townsystemValidationHandler.checkForWorldExists(world);
		townsystemValidationHandler.checkForTownworldDoesNotExist(townWorldList, world);
		TownsystemDao townsystemDao = new TownsystemDaoImpl(world);
		townWorldList.put(world, new TownworldImpl(townsystemDao, world, true));
		saveTownworldNameList();
	}

	@Override
	public void deleteTownWorld(String world)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		townsystemValidationHandler.checkForWorldExists(world);
		townsystemValidationHandler.checkForTownworldExists(townWorldList, world);
		getTownWorldByName(world).delete();
		townWorldList.remove(world);
		saveTownworldNameList();
	}

	@Override
	public void loadAllTownWorlds() {
		for (String townWorldName : UltimateEconomy.getInstance.getConfig().getStringList("TownWorlds")) {
			try {
				TownsystemDao townsystemDao = new TownsystemDaoImpl(townWorldName);
				Townworld townworld = new TownworldImpl(townsystemDao, townWorldName, false);
				townNameList.addAll(townworld.getTownNameList());
				townWorldList.put(townWorldName, townworld);
			} catch (TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the townworld " + townWorldName);
				Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}

	/*
	 * TODO extract into config dao
	 */
	private void saveTownworldNameList() {
		UltimateEconomy.getInstance.getConfig().set("TownWorlds", getTownWorldNameList());
		UltimateEconomy.getInstance.saveConfig();
	}
}
