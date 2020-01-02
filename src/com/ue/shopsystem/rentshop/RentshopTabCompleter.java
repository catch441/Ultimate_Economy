package com.ue.shopsystem.rentshop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Villager.Profession;

public class RentshopTabCompleter implements TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
			if (sender.hasPermission("ultimate_economy.rentshop.admin")) {
				if (args[0].equals("")) {
					list.add("create");
					list.add("delete");
					list.add("move");
					list.add("resize");
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
					if ("resize".contains(args[0])) {
						list.add("resize");
					}
				} else if (args.length == 2
						&& (args[0].equals("delete") || args[0].equals("move") || args[0].equals("resize"))) {
					list = Rentshop.getRentShopUniqueNameList();
				}
			}
			if (args[0].equals("")) {
				list.add("rename");
				list.add("editShop");
				list.add("changeProfession");
			} else if (args.length == 1) {
				if ("rename".contains(args[0])) {
					list.add("rename");
				}
				if ("editShop".contains(args[0])) {
					list.add("editShop");
				}
				if ("changeProfession".contains(args[0])) {
					list.add("changeProfession");
				}
			} else if (args.length == 2
					&& (args[0].equals("rename") || args[0].equals("editShop") || args[0].equals("changeProfession"))) {
				list = getRentedShopsForPlayer(args[1], sender.getName());
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

	private List<String> getRentedShopsForPlayer(String arg, String player) {
		List<String> list = new ArrayList<>();
		for (Rentshop shop : Rentshop.getRentShops()) {
			if (!shop.isRentable() && shop.getOwner().equals(player)) {
				if (arg.equals("") || shop.getName().contains(arg)) {
					list.add(shop.getName());
				}
			}
		}
		return list;
	}
}
