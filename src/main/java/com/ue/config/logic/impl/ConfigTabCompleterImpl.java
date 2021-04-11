package com.ue.config.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ConfigTabCompleterImpl implements TabCompleter {
	
	@Inject
	public ConfigTabCompleterImpl() {}

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
			return getMatchingCommands(args);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> handleLanguageTabComplete(String[] args) {
		if (args.length == 2) {
			if (args[1].equals("")) {
				return getAllLanguages();
			} else {
				return getMatchingLanguages(args);
			}
		} else if (args.length == 3) {
			return getMatchingCountry(args);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> getMatchingCountry(String[] args) {
		List<String> list = new ArrayList<>();
		if (args[1].equals("zh")) {
			list.add("CN");
		} else if (args[1].equals("en")) {
			list.add("US");
		} else if (args[1].equals("cs")) {
			list.add("CZ");
		} else {
			list.add(args[1].toUpperCase());
		}
		return list;
	}

	private List<String> getMatchingLanguages(String[] args) {
		List<String> list = new ArrayList<>();
		addIfContains(list, "de", args[1]);
		addIfContains(list, "en", args[1]);
		addIfContains(list, "cs", args[1]);
		addIfContains(list, "fr", args[1]);
		addIfContains(list, "zh", args[1]);
		addIfContains(list, "ru", args[1]);
		addIfContains(list, "es", args[1]);
		addIfContains(list, "lt", args[1]);
		addIfContains(list, "it", args[1]);
		addIfContains(list, "pl", args[1]);
		return list;
	}

	private List<String> getAllLanguages() {
		List<String> list = new ArrayList<>();
		list.add("de");
		list.add("en");
		list.add("cs");
		list.add("fr");
		list.add("zh");
		list.add("ru");
		list.add("es");
		list.add("lt");
		list.add("it");
		list.add("pl");
		return list;
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
			if ("true".contains(arg)) {
				list.add("true");
			}
			if ("false".contains(arg)) {
				list.add("false");
			}
		}
		return list;
	}

	private List<String> getMatchingCommands(String[] args) {
		List<String> list = new ArrayList<>();
		addIfContains(list, "language", args[0]);
		addIfContains(list, "maxHomes", args[0]);
		addIfContains(list, "homes", args[0]);
		addIfContains(list, "maxRentedDays", args[0]);
		addIfContains(list, "maxJobs", args[0]);
		addIfContains(list, "maxJoinedTowns", args[0]);
		addIfContains(list, "maxPlayershops", args[0]);
		addIfContains(list, "extendedInteraction", args[0]);
		addIfContains(list, "wildernessInteraction", args[0]);
		addIfContains(list, "currency", args[0]);
		addIfContains(list, "startAmount", args[0]);
		addIfContains(list, "allowQuickshop", args[0]);
		return list;
	}

	private void addIfContains(List<String> list, String orig, String val) {
		if (orig.contains(val)) {
			list.add(orig);
		}
	}

	private List<String> getAllCommands() {
		List<String> list = new ArrayList<>();
		list.add("language");
		list.add("maxHomes");
		list.add("homes");
		list.add("maxRentedDays");
		list.add("maxJobs");
		list.add("maxJoinedTowns");
		list.add("extendedInteraction");
		list.add("maxPlayershops");
		list.add("wildernessInteraction");
		list.add("currency");
		list.add("startAmount");
		list.add("allowQuickshop");
		return list;
	}
}
