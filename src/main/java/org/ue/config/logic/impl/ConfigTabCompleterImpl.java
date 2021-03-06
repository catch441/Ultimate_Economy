package org.ue.config.logic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.ue.common.utils.TabCompleterUtils;

public class ConfigTabCompleterImpl extends TabCompleterUtils implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (args[0]) {
		case "homes":
		case "extendedInteraction":
		case "allowQuickshop":
		case "wildernessInteraction":
			return handleHomesAndWildernessInteractionTabComplete(args);
		case "language":
			return handleLanguageTabComplete(args);
		case "":
			return getAllCommands();
		case "maxHomes":
		case "maxRentedDays":
		case "maxJobs":
		case "maxJoinedTowns":
		case "maxPlayershops":
		case "startAmount":
		case "currency":
			return new ArrayList<>();
		default:
			return handleDefaultMatchingTabComplete(args);
		}
	}

	private List<String> handleDefaultMatchingTabComplete(String[] args) {
		if (args.length == 1) {
			return getMatchingList(getAllCommands(), args[0]);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> handleLanguageTabComplete(String[] args) {
		if (args.length == 2) {
			if (args[1].equals("")) {
				return getAllLanguages();
			} else {
				return getMatchingList(getAllLanguages(), args[1]);
			}
		} else if (args.length == 3) {
			return getMatchingCountry(args);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> getMatchingCountry(String[] args) {
		List<String> list = new ArrayList<>();
		switch (args[1]) {
		case "pt":
			list.add("BR");
			break;
		case "cs":
			list.add("CZ");
			break;
		case "en":
			list.add("US");
			break;
		case "zh":
			list.add("CN");
			break;
		case "sw":
			list.add("DE");
			break;
		default:
			list.add(args[1].toUpperCase());
		}
		return list;
	}

	private List<String> getAllLanguages() {
		return Arrays.asList("de", "sw", "en", "cs", "fr", "zh", "ru", "es", "lt", "it", "pl", "pt");
	}

	private List<String> handleHomesAndWildernessInteractionTabComplete(String[] args) {
		if (args.length == 2) {
			return getTrueFalse(args[1]);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> getTrueFalse(String arg) {
		List<String> list = new ArrayList<>();
		if ("".equals(arg)) {
			list.add("true");
			list.add("false");
		} else {
			addIfMatching(list, "true", arg);
			addIfMatching(list, "false", arg);
		}
		return list;
	}

	private List<String> getAllCommands() {
		return Arrays.asList("language", "maxHomes", "homes", "maxRentedDays", "maxJobs", "maxJoinedTowns",
				"maxPlayershops", "extendedInteraction", "wildernessInteraction", "currency", "startAmount",
				"allowQuickshop");
	}
}
