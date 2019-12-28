package com.ue.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ConfigTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if (command.getName().equals("ue-config")) {

			if (args[0].equals("")) {
				list.add("language");
				list.add("maxHomes");
				list.add("homes");
				list.add("maxRentedDays");
				list.add("maxJobs");
				list.add("maxJoinedTowns");
			} else if (args.length == 1) {
				if ("language".contains(args[0])) {
					list.add("language");
				}
				if ("maxHomes".contains(args[0])) {
					list.add("maxHomes");
				}
				if ("homes".contains(args[0])) {
					list.add("homes");
				}
				if ("maxRentedDays".contains(args[0])) {
					list.add("maxRentedDays");
				}
				if ("maxJobs".contains(args[0])) {
					list.add("maxJobs");
				}
				if ("maxJoinedTowns".contains(args[0])) {
					list.add("maxJoinedTowns");
				}
			} else if (args.length == 2) {
				if(args[0].equals("homes")) {
					if (args[1].equals("")) {
						list.add("true");
						list.add("false");
					} else if (args[1].equals("true")) {
						list.add("true");
					} else if (args[1].equals("false")) {
						list.add("false");
					}
				} else if (args[0].equals("language")) {
					if (args[1].equals("")) {
						list.add("de");
						list.add("en");
						list.add("cs");
						list.add("fr");
						list.add("zh");
						list.add("ru");
						list.add("es");
					} else if (args[1].equals("de")) {
						list.add("DE");
					} else if (args[1].equals("en")) {
						list.add("US");
					} else if (args[1].equals("cs")) {
						list.add("CZ");
					} else if (args[1].equals("fr")) {
						list.add("FR");
					} else if (args[1].equals("zh")) {
						list.add("CN");
					} else if (args[1].equals("ru")) {
						list.add("RU");
					} else if (args[1].equals("es")) {
						list.add("ES");
					}
				}
			}
		}
		return list;
	}
}
