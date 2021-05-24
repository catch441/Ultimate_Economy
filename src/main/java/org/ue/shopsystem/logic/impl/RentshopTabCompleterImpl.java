package org.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.ue.common.utils.TabCompleterUtils;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.RentshopManager;

public class RentshopTabCompleterImpl extends TabCompleterUtils implements TabCompleter {

	private final RentshopManager rentshopManager;

	public RentshopTabCompleterImpl(RentshopManager rentshopManager) {
		this.rentshopManager = rentshopManager;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (args[0]) {
		case "create":
			return new ArrayList<>();
		case "delete":
		case "move":
			return handleDeleteAndMoveTabComplete(sender, args);
		case "rename":
		case "editShop":
			return handleRenameAndEditShopTabComplete(sender, args);
		default:
			return handleDefaultMatchingTabComplete(sender, args);
		}
	}

	private List<String> handleDeleteAndMoveTabComplete(CommandSender sender, String[] args) {
		if (sender.hasPermission("ultimate_economy.rentshop.admin") && args.length == 2) {
			return rentshopManager.getRentShopUniqueNameList();
		}
		return new ArrayList<>();
	}

	private List<String> handleRenameAndEditShopTabComplete(CommandSender sender, String[] args) {
		if (args.length == 2) {
			return getRentedShopsForPlayer(args[1], sender.getName());
		}
		return new ArrayList<>();
	}

	private List<String> handleDefaultMatchingTabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			List<String> list = new ArrayList<String>(getMatchingList(getAllPlayerCommands(), args[0]));
			if (sender.hasPermission("ultimate_economy.rentshop.admin")) {
				list.addAll(getMatchingList(getAllAdminCommands(), args[0]));
			}
			return list;
		}
		return new ArrayList<>();
	}

	private List<String> getAllAdminCommands() {
		return new ArrayList<String>(Arrays.asList("create", "delete", "move", "editShop"));
	}

	private List<String> getAllPlayerCommands() {
		return Arrays.asList("rename");
	}

	private List<String> getRentedShopsForPlayer(String arg, String player) {
		List<String> list = new ArrayList<>();
		for (Rentshop shop : rentshopManager.getRentShops()) {
			if (!shop.isRentable() && shop.getOwner().getName().equals(player)) {
				addIfMatching(list, shop.getName(), arg);
			}
		}
		return list;
	}
}
