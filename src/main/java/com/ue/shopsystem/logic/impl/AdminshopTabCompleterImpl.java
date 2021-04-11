package com.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;

import com.ue.common.utils.TabCompleterUtils;
import com.ue.shopsystem.logic.api.AdminshopManager;

public class AdminshopTabCompleterImpl extends TabCompleterUtils implements TabCompleter {
	
	private final AdminshopManager adminshopManager;
	
	@Inject
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
			return getAdminshopList(args[0]);
		} else {
			return new ArrayList<>();
		}
	}
	
	private List<String> handleAdminshopTabComplete(String[] args) {
		if(args.length > 0) {
			switch (args[0]) {
			case "delete":
			case "editShop":
			case "resize":
			case "move":
			case "rename":
			case "removeSpawner":
				return handleAdminshopNameTabComplete(args);
			case "addSpawner":
				return handlerAddSpawnerTabComplete(args);
			case "changeProfession":
				return handleChangeProfessionTabComplete(args);
			case "":
				return getAllCommands();
			default:
				return handleDefaultMatchingTabComplete(args);
			}
		}
		return new ArrayList<>();
	}

	private List<String> handleChangeProfessionTabComplete(String[] args) {
		if(args.length == 2) {
			return getAdminshopList(args[1]);
		} else if(args.length == 3) {
			return getProfessions(args[2]);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> handlerAddSpawnerTabComplete(String[] args) {
		if (args.length == 2) {
			return getAdminshopList(args[1]);
		} else if (args.length == 3) {
			return getEntityList(args[2]);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> handleDefaultMatchingTabComplete(String[] args) {
		List<String> list = new ArrayList<>();
		for(String cmd: getAllCommands()) {
			addIfMatching(list, cmd, args[0]);
		}
		return list;
	}

	private List<String> getAllCommands() {
		List<String> list = new ArrayList<>();
		list.add("create");
		list.add("delete");
		list.add("move");
		list.add("editShop");
		list.add("rename");
		list.add("resize");
		list.add("changeProfession");
		list.add("addSpawner");
		return list;
	}

	private List<String> handleAdminshopNameTabComplete(String[] args) {
		if (args.length == 2) {
			return getAdminshopList(args[1]);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> getAdminshopList(String arg) {
		List<String> temp = adminshopManager.getAdminshopNameList();
		List<String> list = new ArrayList<>();
		if ("".equals(arg)) {
			list = temp;
		} else {
			for (String shopName : temp) {
				addIfMatching(list, shopName, arg);
			}
		}
		return list;
	}

	private static List<String> getEntityList(String arg) {
		List<String> list = new ArrayList<>();
		EntityType[] entityTypes = EntityType.values();
		if ("".equals(arg)) {
			for (EntityType entityname : entityTypes) {
				list.add(entityname.name().toLowerCase());
			}
		} else {
			for (EntityType entityname : entityTypes) {
				if (entityname.name().toLowerCase().contains(arg)) {
					list.add(entityname.name().toLowerCase());
				}
			}
		}
		return list;
	}
}
