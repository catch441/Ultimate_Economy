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
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.RentshopManager;

public class RentshopTabCompleterImpl extends TabCompleterUtils implements TabCompleter {

	private final RentshopManager rentshopManager;

	@Inject
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
		case "resize":
			return handleDeleteAndMoveAndResizeTabComplete(sender, args);
		case "rename":
		case "editShop":
			return handleRenameAndEditShopTabComplete(sender, args);
		case "changeProfession":
			return handleChangeProfessionTabComplete(sender, args);
		default:
			return handleDefaultMatchingTabComplete(sender, args);
		}
	}

	private List<String> handleChangeProfessionTabComplete(CommandSender sender, String[] args) {
		if (args.length == 2) {
			return getRentedShopsForPlayer(args[1], sender.getName());
		} else if (args.length == 3) {
			return getMatchingEnumList(Profession.values(), args[2]);
		}
		return new ArrayList<>();
	}

	private List<String> handleDeleteAndMoveAndResizeTabComplete(CommandSender sender, String[] args) {
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
		return new ArrayList<String>(Arrays.asList("create", "delete", "move", "resize"));
	}

	private List<String> getAllPlayerCommands() {
		return Arrays.asList("rename", "editShop", "changeProfession");
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
