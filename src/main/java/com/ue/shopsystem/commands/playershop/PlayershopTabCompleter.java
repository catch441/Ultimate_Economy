package com.ue.shopsystem.commands.playershop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Villager.Profession;

import com.ue.shopsystem.controller.PlayershopController;

public class PlayershopTabCompleter implements TabCompleter {

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
	    return getProfessions(args);
	} else if (args.length == 2) {
	    return getPlayerShopList(args[1], sender.getName());
	} else {
	    return new ArrayList<>();
	}
    }

    private List<String> handleDeleteOtherTabComplete(String[] args) {
	if (args.length == 2) {
	    return PlayershopController.getPlayerShopUniqueNameList();
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

    private List<String> getProfessions(String[] args) {
	List<String> list = new ArrayList<>();
	for (Profession profession : Profession.values()) {
	    if (args[2].equals("")) {
		list.add(profession.name().toLowerCase());
	    } else {
		if (profession.name().toLowerCase().contains(args[2])) {
		    list.add(profession.name().toLowerCase());
		}
	    }
	}
	return list;
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
	if (sender.hasPermission("ultimate_economy.adminshop")) {
	    if ("deleteOther".contains(args[0])) {
		list.add("deleteOther");
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
