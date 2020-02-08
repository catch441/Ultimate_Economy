package com.ue.townsystem.town.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.ue.exceptions.PlayerException;
import com.ue.player.api.EconomyPlayerController;
import com.ue.townsystem.town.api.TownController;

public class TownTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
	if (command.getName().equals("town")) {
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
	    default:
		return getListOfMatchingCommands(args);
	    }
	}
	return new ArrayList<>();
    }

    private List<String> handleTownNameTabComplete(String[] args) {
	List<String> list = new ArrayList<>();
	if (args[1].equals("")) {
	    list.addAll(TownController.getTownNameList());
	} else if (args.length == 2) {
	    List<String> list2 = TownController.getTownNameList();
	    for (String string : list2) {
		if (string.contains(args[1])) {
		    list.add(string);
		}
	    }
	}
	return list;
    }

    private List<String> handleJoinedTownNameTabComplete(CommandSender sender, String[] args) {
	List<String> list = new ArrayList<>();
	try {
	    if (args[1].equals("")) {
		list.addAll(
			EconomyPlayerController.getEconomyPlayerByName(sender.getName()).getJoinedTownList());
	    } else if (args.length == 2) {
		List<String> list2 = EconomyPlayerController.getEconomyPlayerByName(sender.getName())
			.getJoinedTownList();
		for (String string : list2) {
		    if (string.contains(args[1])) {
			list.add(string);
		    }
		}
	    }
	} catch (PlayerException e) {
	    Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
	}
	return list;
    }

    private List<String> handlePlotTabComplete(String[] args) {
	List<String> list = new ArrayList<>();
	if (args[1].equals("")) {
	    list.add("setForSale");
	} else if (args.length == 2) {
	    if ("setForSale".contains(args[1])) {
		list.add("setForSale");
	    }
	}
	return list;
    }

    private List<String> getListOfMatchingCommands(String[] args) {
	List<String> list = new ArrayList<>();
	if ("create".contains(args[0])) {
	    list.add("create");
	}
	if ("delete".contains(args[0])) {
	    list.add("delete");
	}
	if ("expand".contains(args[0])) {
	    list.add("expand");
	}
	if ("adddeputy".contains(args[0])) {
	    list.add("adddeputy");
	}
	if ("removeDeputy".contains(args[0])) {
	    list.add("removeDeputy");
	}
	if ("setTownSpawn".contains(args[0])) {
	    list.add("setTownSpawn");
	}
	if ("moveTownManager".contains(args[0])) {
	    list.add("moveTownManager");
	}
	if ("plot".contains(args[0])) {
	    list.add("plot");
	}
	if ("pay".contains(args[0])) {
	    list.add("pay");
	}
	if ("tp".contains(args[0])) {
	    list.add("tp");
	}
	if ("bank".contains(args[0])) {
	    list.add("bank");
	}
	if ("withdraw".contains(args[0])) {
	    list.add("withdraw");
	}
	if ("rename".contains(args[0])) {
	    list.add("rename");
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
