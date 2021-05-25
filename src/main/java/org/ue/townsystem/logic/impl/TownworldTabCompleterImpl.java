package org.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.TabCompleterUtils;
import org.ue.townsystem.logic.api.TownworldManager;

public class TownworldTabCompleterImpl extends TabCompleterUtils implements TabCompleter {

	private final ServerProvider serverProvider;
	private final TownworldManager townworldManager;

	public TownworldTabCompleterImpl(ServerProvider serverProvider, TownworldManager townworldManager) {
		this.serverProvider = serverProvider;
		this.townworldManager = townworldManager;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (args[0]) {
		case "enable":
			return handleEnableTabCompleter(args);
		case "disable":
		case "setFoundationPrice":
		case "setExpandPrice":
			return handleExistingWorldTabCompleter(args);
		case "":
			return getAllCommands();
		default:
			return handleDefaultMatchingCommands(args);
		}
	}

	private List<String> handleExistingWorldTabCompleter(String[] args) {
		if (args.length == 2) {
			return getMatchingList(townworldManager.getTownWorldNameList(), args[1]);
		}
		return new ArrayList<>();
	}

	private List<String> handleEnableTabCompleter(String[] args) {
		if (args.length == 2) {
			return getAllMatchingWorlds(args);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> getAllMatchingWorlds(String[] args) {
		List<String> list = new ArrayList<>();
		for (World world : serverProvider.getWorlds()) {
			addIfMatching(list, world.getName(), args[1]);
		}
		return list;
	}

	private List<String> handleDefaultMatchingCommands(String[] args) {
		if (args.length == 1) {
			return getMatchingList(getAllCommands(), args[0]);
		}
		return new ArrayList<>();
	}

	private List<String> getAllCommands() {
		return Arrays.asList("enable", "disable", "setFoundationPrice", "setExpandPrice");
	}
}
