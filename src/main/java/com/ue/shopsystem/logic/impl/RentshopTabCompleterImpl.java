package com.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Villager.Profession;

import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.RentshopManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RentshopTabCompleterImpl implements TabCompleter {

	private final RentshopManager rentshopManager;

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
		case "":
			return handleAllCommands(sender);
		default:
			return handleDefaultMatchingTabComplete(sender, args);
		}
	}

	private List<String> handleChangeProfessionTabComplete(CommandSender sender, String[] args) {
		if (args.length == 2) {
			return getRentedShopsForPlayer(args[1], sender.getName());
		} else if (args.length == 3) {
			return getProfessions(args[2]);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> getProfessions(String arg) {
		if ("".equals(arg)) {
			return getAllProfessions();
		} else {
			return getAllMatchingProfessions(arg);
		}
	}

	private List<String> getAllMatchingProfessions(String arg) {
		List<String> list = new ArrayList<>();
		for (Profession profession : Profession.values()) {
			if (profession.name().toLowerCase().contains(arg)) {
				list.add(profession.name().toLowerCase());
			}
		}
		return list;
	}

	private List<String> getAllProfessions() {
		List<String> list = new ArrayList<>();
		for (Profession profession : Profession.values()) {
			list.add(profession.name().toLowerCase());
		}
		return list;
	}

	private List<String> handleDeleteAndMoveAndResizeTabComplete(CommandSender sender, String[] args) {
		if (sender.hasPermission("ultimate_economy.rentshop.admin") && args.length == 2) {
			return rentshopManager.getRentShopUniqueNameList();
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> handleRenameAndEditShopTabComplete(CommandSender sender, String[] args) {
		if (args.length == 2) {
			return getRentedShopsForPlayer(args[1], sender.getName());
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> handleDefaultMatchingTabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			if (sender.hasPermission("ultimate_economy.rentshop.admin")) {
				return getAllMatchingAdminCommands(args);
			} else {
				return getAllMatchingPlayerCommands(args);
			}
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> handleAllCommands(CommandSender sender) {
		if (sender.hasPermission("ultimate_economy.rentshop.admin")) {
			return getAllAdminCommands();
		} else {
			return getAllPlayerCommands();
		}
	}

	private List<String> getAllMatchingAdminCommands(String[] args) {
		List<String> list = getAllMatchingPlayerCommands(args);
		addIfMatching(list, "create", args[0]);
		addIfMatching(list, "delete", args[0]);
		addIfMatching(list, "move", args[0]);
		addIfMatching(list, "resize", args[0]);
		return list;
	}

	private List<String> getAllMatchingPlayerCommands(String[] args) {
		List<String> list = new ArrayList<>();
		addIfMatching(list, "rename", args[0]);
		addIfMatching(list, "editShop", args[0]);
		addIfMatching(list, "changeProfession", args[0]);
		return list;
	}

	private void addIfMatching(List<String> list, String command, String arg) {
		if (command.contains(arg)) {
			list.add(command);
		}
	}

	private List<String> getAllAdminCommands() {
		List<String> list = getAllPlayerCommands();
		list.add("create");
		list.add("delete");
		list.add("move");
		list.add("resize");
		return list;
	}

	private List<String> getAllPlayerCommands() {
		List<String> list = new ArrayList<>();
		list.add("rename");
		list.add("editShop");
		list.add("changeProfession");
		return list;
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
