package com.ue.shopsystem.commands.adminshop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager.Profession;

import com.ue.shopsystem.api.AdminshopController;

public class AdminshopTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
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

	private List<String> handleChangeProfessionTabComplete(String[] args) {
		if(args.length == 2) {
			return getAdminshopList(args[1]);
		} else if(args.length == 3) {
			return getProfessions(args[2]);
		} else {
			return new ArrayList<>();
		}
	}
	
	private List<String> getProfessions(String arg) {
		List<String> list = new ArrayList<>();
		for (Profession profession : Profession.values()) {
			if ("".equals(arg)) {
				list.add(profession.name().toLowerCase());
			} else {
				if (profession.name().toLowerCase().contains(arg)) {
					list.add(profession.name().toLowerCase());
				}
			}
		}
		return list;
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

	private void addIfMatching(List<String> list, String command, String arg) {
		if (command.contains(arg)) {
			list.add(command);
		}
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
		List<String> temp = AdminshopController.getAdminshopNameList();
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
