package com.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.ue.common.utils.TabCompleterUtils;
import com.ue.shopsystem.logic.api.PlayershopManager;

public class PlayershopTabCompleterImpl extends TabCompleterUtils implements TabCompleter {
	
	private final PlayershopManager playershopManager;
	
	@Inject
	public PlayershopTabCompleterImpl(PlayershopManager playershopManager) {
		this.playershopManager = playershopManager;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (args[0]) {
		case "deleteOther":
			return handleDeleteOtherTabComplete(args);
		case "create":
			return new ArrayList<>();
		case "delete":
		case "move":
		case "editShop":
		case "resize":
		case "changeOwner":
		case "rename":
			return handlePlayershopNameTabComplete(sender, args);
		case "changeProfession":
			return handleChangeProfessionTabComplete(sender, args);
		case "":
			return getAllCommands(sender);
		default:
			return handleDefaultMatchingTabComplete(sender, args);
		}
	}

	private List<String> handleChangeProfessionTabComplete(CommandSender sender, String[] args) {
		if (args.length == 3) {
			return getProfessions(args[2]);
		} else if (args.length == 2) {
			return getPlayerShopList(args[1], sender.getName());
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> handleDeleteOtherTabComplete(String[] args) {
		if (args.length == 2) {
			return playershopManager.getPlayerShopUniqueNameList();
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> handlePlayershopNameTabComplete(CommandSender sender, String[] args) {
		if (args.length == 2) {
			return getPlayerShopList(args[1], sender.getName());
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> handleDefaultMatchingTabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return getMatchingCommands(sender, args);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> getAllCommands(CommandSender sender) {
		List<String> list = new ArrayList<>();
		list.add("create");
		list.add("delete");
		list.add("move");
		list.add("editShop");
		list.add("rename");
		list.add("resize");
		list.add("changeProfession");
		list.add("changeOwner");
		if (sender.hasPermission("ultimate_economy.adminshop")) {
			list.add("deleteOther");
		}
		return list;
	}

	private List<String> getMatchingCommands(CommandSender sender, String[] args) {
		List<String> list = new ArrayList<>();
		for(String cmd: getAllCommands(sender)) {
			addIfMatching(list, cmd, args[0]);
		}
		return list;
	}

	private List<String> getPlayerShopList(String arg, String playerName) {
		List<String> list = new ArrayList<>();
		for (String shopName : getPlayershopNameListForPlayer(playerName)) {
			if ("".equals(arg)) {
				list.add(shopName);
			} else if(shopName.contains(arg)) {
				list.add(shopName);
			}
		}
		return list;
	}

	private List<String> getPlayershopNameListForPlayer(String playerName) {
		List<String> list = new ArrayList<>();
		for (String shopName : playershopManager.getPlayerShopUniqueNameList()) {
			if (shopName.substring(shopName.indexOf("_") + 1).equals(playerName)) {
				list.add(shopName.substring(0, shopName.indexOf("_")));
			}
		}
		return list;
	}
}
