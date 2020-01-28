package com.ue.config.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ConfigTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if (args[0].equals("")) {
			list.add("language");
			list.add("maxHomes");
			list.add("homes");
			list.add("maxRentedDays");
			list.add("maxJobs");
			list.add("maxJoinedTowns");
			list.add("extendedInteraction");
			list.add("maxPlayershops");
			list.add("wildernessInteraction");
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
			if ("maxPlayershops".contains(args[0])) {
				list.add("maxPlayershops");
			}
			if ("extendedInteraction".contains(args[0])) {
				list.add("extendedInteraction");
			}
			if ("wildernessInteraction".contains(args[0])) {
				list.add("wildernessInteraction");
			}
		} else if (args.length == 2) {
			if (args[0].equals("homes") || args[0].equals("wildernessInteraction")) {
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
					list.add("lt");
				} else if ("de".contains(args[1])) {
					list.add("de");
				} else if ("en".contains(args[1])) {
					list.add("us");
				} else if ("cs".contains(args[1])) {
					list.add("cz");
				} else if ("fr".contains(args[1])) {
					list.add("fr");
				} else if ("zh".contains(args[1])) {
					list.add("cn");
				} else if ("ru".contains(args[1])) {
					list.add("ru");
				} else if ("es".contains(args[1])) {
					list.add("es");
				} else if ("lt".contains(args[1])) {
					list.add("lt");
				}
			}
		} else if (args.length == 3) {
			if (args[0].equals("language")) {
				if (args[1].equals("de")) {
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
				} else if (args[1].equals("lt")) {
					list.add("LT");
				}
			}
		}
		return list;
	}
}
