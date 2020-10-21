package com.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.slf4j.Logger;

import com.ue.common.utils.TabCompleterUtils;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.townsystem.logic.api.TownworldManager;

public class TownTabCompleterImpl extends TabCompleterUtils implements TabCompleter {

	private final TownworldManager townworldManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private final Logger logger;

	/**
	 * Inject constructor.
	 * 
	 * @param logger
	 * @param townworldManager
	 * @param ecoPlayerManager
	 */
	@Inject
	public TownTabCompleterImpl(Logger logger, TownworldManager townworldManager,
			EconomyPlayerManager ecoPlayerManager) {
		this.townworldManager = townworldManager;
		this.ecoPlayerManager = ecoPlayerManager;
		this.logger = logger;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (args[0]) {
		case "expand":
		case "setTownSpawn":
		case "bank":
		case "withdraw":
		case "rename":
		case "delete":
		case "addDeputy":
		case "removeDeputy":
			return handleJoinedTownNameTabComplete(sender, args);
		case "tp":
		case "pay":
			return handleTownNameTabComplete(args);
		case "plot":
			return handlePlotTabComplete(args);
		case "":
			return getListWithAllCommands();
		case "create":
			return new ArrayList<>();
		default:
			return getListOfMatchingCommands(args);
		}
	}

	private List<String> handleTownNameTabComplete(String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length == 2) {
			if (args[1].equals("")) {
				list.addAll(townworldManager.getTownNameList());
			} else {
				for (String string : townworldManager.getTownNameList()) {
					addIfMatching(list, string, args[1]);
				}
			}
		}
		return list;
	}

	private List<String> handleJoinedTownNameTabComplete(CommandSender sender, String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length == 2) {
			try {
				if (args[1].equals("")) {
					list.addAll(ecoPlayerManager.getEconomyPlayerByName(sender.getName()).getJoinedTownList());
				} else {
					List<String> list2 = ecoPlayerManager.getEconomyPlayerByName(sender.getName()).getJoinedTownList();
					for (String string : list2) {
						addIfMatching(list, string, args[1]);
					}
				}
			} catch (GeneralEconomyException e) {
				logger.warn("[Ultimate_Economy] " + e.getMessage());
			}
		}
		return list;
	}

	private List<String> handlePlotTabComplete(String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length == 2) {
			if (args[1].equals("")) {
				list.add("setForSale");
			} else if (args.length == 2) {
				addIfMatching(list, "setForSale", args[1]);
			}
		}
		return list;
	}

	private List<String> getListOfMatchingCommands(String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length == 1) {
			for (String command : getListWithAllCommands()) {
				addIfMatching(list, command, args[0]);
			}
		}
		return list;
	}

	private List<String> getListWithAllCommands() {
		List<String> list = new ArrayList<>();
		list.add("create");
		list.add("delete");
		list.add("expand");
		list.add("addDeputy");
		list.add("removeDeputy");
		list.add("setTownSpawn");
		list.add("moveTownManager");
		list.add("plot");
		list.add("pay");
		list.add("tp");
		list.add("bank");
		list.add("withdraw");
		list.add("rename");
		return list;
	}

}
