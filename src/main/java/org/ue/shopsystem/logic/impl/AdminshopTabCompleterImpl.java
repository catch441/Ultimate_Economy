package org.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.ue.common.utils.TabCompleterUtils;
import org.ue.shopsystem.logic.api.AdminshopManager;

public class AdminshopTabCompleterImpl extends TabCompleterUtils implements TabCompleter {

	private final AdminshopManager adminshopManager;

	public AdminshopTabCompleterImpl(AdminshopManager adminshopManager) {
		this.adminshopManager = adminshopManager;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (command.getLabel()) {
		case "shop":
			return handleShopTabComplete(args);
		case "adminshop":
			return handleAdminshopTabComplete(args);
		default:
			return new ArrayList<>();
		}
	}

	private List<String> handleShopTabComplete(String[] args) {
		if (args.length == 1) {
			return getMatchingList(adminshopManager.getAdminshopNameList(), args[0]);
		}
		return new ArrayList<>();
	}

	private List<String> handleAdminshopTabComplete(String[] args) {
		if (args.length > 0) {
			switch (args[0]) {
			case "delete":
			case "editShop":
			case "move":
			case "rename":
				return handleAdminshopNameTabComplete(args);
			case "addSpawner":
				return handlerAddSpawnerTabComplete(args);
			case "":
				return getAllCommands();
			default:
				if (args.length == 1) {
					return getMatchingList(getAllCommands(), args[0]);
				}
			}
		}
		return new ArrayList<>();
	}

	private List<String> handlerAddSpawnerTabComplete(String[] args) {
		if (args.length == 2) {
			return getMatchingList(adminshopManager.getAdminshopNameList(), args[1]);
		} else if (args.length == 3) {
			return getMatchingEnumList(EntityType.values(), args[2]);
		}
		return new ArrayList<>();
	}

	private List<String> getAllCommands() {
		return Arrays.asList("create", "delete", "move", "editShop", "rename", "addSpawner");
	}

	private List<String> handleAdminshopNameTabComplete(String[] args) {
		if (args.length == 2) {
			return getMatchingList(adminshopManager.getAdminshopNameList(), args[1]);
		}
		return new ArrayList<>();
	}
}
