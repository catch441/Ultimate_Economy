package com.ue.shopsystem.playershop.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Villager.Profession;

import com.ue.shopsystem.playershop.api.PlayershopController;

public class PlayershopTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
	List<String> list = new ArrayList<>();
	if (sender.hasPermission("ultimate_economy.adminshop")) {
	    list.add("deleteOther");
	} else if (args.length == 1) {
	    if ("deleteOther".contains(args[0])) {
		list.add("deleteOther");
	    }
	}
	if (args[0].equals("")) {
	    list.add("create");
	    list.add("delete");
	    list.add("move");
	    list.add("editShop");
	    list.add("rename");
	    list.add("resize");
	    list.add("changeProfession");
	    list.add("changeOwner");
	} else if (args.length == 1) {
	    if ("create".contains(args[0])) {
		list.add("create");
	    }
	    if ("delete".contains(args[0])) {
		list.add("delete");
	    }
	    if ("move".contains(args[0])) {
		list.add("move");
	    }
	    if ("editShop".contains(args[0])) {
		list.add("editShop");
	    }
	    if ("rename".contains(args[0])) {
		list.add("rename");
	    }
	    if ("resize".contains(args[0])) {
		list.add("resize");
	    }
	    if ("changeProfession".contains(args[0])) {
		list.add("changeProfession");
	    }
	    if ("changeOwner".contains(args[0])) {
		list.add("changeOwner");
	    }
	} else if (args.length == 2 && (args[0].equals("rename") || args[0].equals("editShop")
		|| args[0].equals("changeProfession") || args[0].equals("deleteOther") || args[0].equals("resize")
		|| args[0].equals("move") || args[0].equals("delete") || args[0].equals("changeOwner"))) {
	    if (args[0].equals("deleteOther")) {
		list = PlayershopController.getPlayerShopUniqueNameList();
	    } else {
		list = getPlayerShopList(args[1], sender.getName());
	    }
	} else if (args.length == 3 && args[0].equals("changeProfession")) {
	    if (args[2].equals("")) {
		for (Profession profession : Profession.values()) {
		    list.add(profession.name().toLowerCase());
		}
	    } else {
		for (Profession profession : Profession.values()) {
		    if (profession.name().toLowerCase().contains(args[2])) {
			list.add(profession.name().toLowerCase());
		    }
		}
	    }
	}
	return list;
    }

    private List<String> getPlayerShopList(String arg, String playerName) {
	List<String> temp = PlayershopController.getPlayerShopUniqueNameList();
	List<String> list = new ArrayList<>();
	if ("".equals(arg)) {
	    for (String shopName : temp) {
		if (shopName.substring(shopName.indexOf("_") + 1).equals(playerName)) {
		    list.add(shopName.substring(0, shopName.indexOf("_")));
		}
	    }
	} else {
	    for (String shopName : temp) {
		if (shopName.substring(0, shopName.indexOf("_")).contains(arg)
			&& shopName.substring(shopName.indexOf("_") + 1).equals(playerName)) {
		    list.add(shopName.substring(0, shopName.indexOf("_")));
		}
	    }
	}
	return list;
    }
}
