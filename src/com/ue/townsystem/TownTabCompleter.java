package com.ue.townsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.ue.exceptions.PlayerException;
import com.ue.player.EconomyPlayer;

public class TownTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if (command.getName().equals("town")) {
			if (args[0].equals("")) {
				list.add("create");
				list.add("delete");
				list.add("expand");
				list.add("addCoOwner");
				list.add("removeCoOwner");
				list.add("setTownSpawn");
				list.add("moveTownManager");
				list.add("plot");
				list.add("pay");
				list.add("tp");
				list.add("bank");
				list.add("withdraw");
				list.add("rename");
			} else if (args.length == 1) {
				if ("create".contains(args[0])) {
					list.add("create");
				}
				if ("delete".contains(args[0])) {
					list.add("delete");
				}
				if ("expand".contains(args[0])) {
					list.add("expand");
				}
				if ("addCoOwner".contains(args[0])) {
					list.add("addCoOwner");
				}
				if ("removeCoOwner".contains(args[0])) {
					list.add("removeCoOwner");
				}
				if ("setTownSpawn".contains(args[0])) {
					list.add("setTownSpawn");
				}
				if ("moveTownManager".contains(args[0])) {
					list.add("moveTownManager");
				}
				if ("plot".contains(args[0])) {
					list.add("plot");
				}
				if ("pay".contains(args[0])) {
					list.add("pay");
				}
				if ("tp".contains(args[0])) {
					list.add("tp");
				}
				if ("bank".contains(args[0])) {
					list.add("bank");
				}
				if ("withdraw".contains(args[0])) {
					list.add("withdraw");
				}
				if ("rename".contains(args[0])) {
					list.add("rename");
				}
			} else if (args[0].equals("delete") || args[0].equals("expand") || args[0].equals("setTownSpawn")
					|| args[0].equals("bank") || args[0].equals("addCoOwner") || args[0].equals("removeCoOwner")
					|| args[0].equals("withdraw") || args[0].equals("rename")) {
				try {
					if (args[1].equals("")) {
						list.addAll(EconomyPlayer.getEconomyPlayerByName(sender.getName()).getJoinedTownList());
					} else if (args.length == 2) {
						List<String> list2 = EconomyPlayer.getEconomyPlayerByName(sender.getName()).getJoinedTownList();
						for (String string : list2) {
							if (string.contains(args[1])) {
								list.add(string);
							}
						}
					}
				} catch (PlayerException e) {
					Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
				}
			} else if (args[0].equals("pay") || args[0].equals("tp") || args[0].equals("withdraw")) {
				if (args[1].equals("")) {
					list.addAll(Town.getTownNameList());
				} else if (args.length == 2) {
					List<String> list2 = Town.getTownNameList();
					for (String string : list2) {
						if (string.contains(args[1])) {
							list.add(string);
						}
					}
				}
			} else if (args[0].equals("plot")) {
				if (args[1].equals("")) {
					list.add("setForSale");
				} else if (args.length == 2) {
					if ("setForSale".contains(args[1])) {
						list.add("setForSale");
					}
				}
			}
		}
		return list;
	}

}
