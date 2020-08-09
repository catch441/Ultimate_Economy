package com.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.ue.common.utils.TabCompleterUtils;

public class TownworldTabCompleterImpl extends TabCompleterUtils implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (args[0]) {
		case "enable":
		case "disable":
		case "setFoundationPrice":
		case "setExpandPrice":
			return handleSetExpandPriceTabCompleter(args);
		case "":
			return getAllCommands();
		default:
			return handleDefaultMatchingCommands(args);
		}
	}

	private List<String> handleSetExpandPriceTabCompleter(String[] args) {
		if (args.length == 2) {
			if (args[1].equals("")) {
				return getAllWorlds();
			} else {
				return getAllMatchingWorlds(args);
			}
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> getAllMatchingWorlds(String[] args) {
		List<String> list = new ArrayList<>();
		for (World world : Bukkit.getWorlds()) {
			if (world.getName().contains(args[1])) {
				list.add(world.getName());
			}
		}
		return list;
	}

	private List<String> getAllWorlds() {
		List<String> list = new ArrayList<>();
		for (World world : Bukkit.getWorlds()) {
			list.add(world.getName());
		}
		return list;
	}

	private List<String> handleDefaultMatchingCommands(String[] args) {
		if (args.length == 1) {
			return getAllMatchingCommands(args);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> getAllMatchingCommands(String[] args) {
		List<String> list = new ArrayList<>();
		addIfMatching(list, "enable", args[0]);
		addIfMatching(list, "disable", args[0]);
		addIfMatching(list, "setFoundationPrice", args[0]);
		addIfMatching(list, "setExpandPrice", args[0]);
		return list;
	}

	private List<String> getAllCommands() {
		List<String> list = new ArrayList<>();
		list.add("enable");
		list.add("disable");
		list.add("setFoundationPrice");
		list.add("setExpandPrice");
		return list;
	}

}
