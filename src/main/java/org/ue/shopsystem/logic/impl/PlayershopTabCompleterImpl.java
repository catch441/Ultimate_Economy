package org.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Villager.Profession;
import org.ue.common.utils.TabCompleterUtils;
import org.ue.shopsystem.logic.api.PlayershopManager;

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
			return getMatchingEnumList(Profession.values(), args[2]);
		} else if (args.length == 2) {
			return getMatchingList(getPlayershopNameListForPlayer(sender.getName()), args[1]);
		}
		return new ArrayList<>();
	}

	private List<String> handleDeleteOtherTabComplete(String[] args) {
		if (args.length == 2) {
			return playershopManager.getPlayerShopUniqueNameList();
		}
		return new ArrayList<>();
	}

	private List<String> handlePlayershopNameTabComplete(CommandSender sender, String[] args) {
		if (args.length == 2) {
			return getMatchingList(getPlayershopNameListForPlayer(sender.getName()), args[1]);
		}
		return new ArrayList<>();
	}

	private List<String> handleDefaultMatchingTabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return getMatchingList(getAllCommands(sender), args[0]);
		}
		return new ArrayList<>();
	}

	private List<String> getAllCommands(CommandSender sender) {
		List<String> list = new ArrayList<>(Arrays.asList("create", "delete", "move", "editShop", "rename", "resize", "changeProfession", "changeOwner"));
		if (sender.hasPermission("ultimate_economy.adminshop")) {
			list.add("deleteOther");
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
