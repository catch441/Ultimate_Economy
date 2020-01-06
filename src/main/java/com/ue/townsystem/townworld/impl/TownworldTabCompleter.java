package com.ue.townsystem.townworld.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TownworldTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if (command.getName().equalsIgnoreCase("townWorld")) {
			if (args[0].equals("")) {
				list.add("enable");
				list.add("disable");
				list.add("setFoundationPrice");
				list.add("setExpandPrice");
			} else if (args.length == 1) {
				if ("enable".contains(args[0])) {
					list.add("enable");
				}
				if ("disable".contains(args[0])) {
					list.add("disable");
				}
				if ("setFoundationPrice".contains(args[0])) {
					list.add("setFoundationPrice");
				}
				if ("setExpandPrice".contains(args[0])) {
					list.add("setExpandPrice");
				}
			} else if (args[0].equals("enable") || args[0].equals("disable") || args[0].equals("setFoundationPrice")
					|| args[0].equals("setExpandPrice")) {
				if (args[1].equals("")) {
					for (World world : Bukkit.getWorlds()) {
						list.add(world.getName());
					}
				} else if (args.length == 2) {
					for (World world : Bukkit.getWorlds()) {
						if (world.getName().contains(args[1])) {
							list.add(world.getName());
						}
					}
				}
			}
		} 
		return list;
	}

}
