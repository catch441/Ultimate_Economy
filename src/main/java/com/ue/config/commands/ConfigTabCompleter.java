package com.ue.config.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ConfigTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
	switch (args[0]) {
	case "homes":
	case "extendedInteraction":
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
	} else if (args[1].equals("it")) {
	    list.add("IT");
	}
	return list;
    }

    private List<String> getMatchingLanguages(String[] args) {
	List<String> list = new ArrayList<>();
	if ("de".contains(args[1])) {
	    list.add("de");
	}
	if ("en".contains(args[1])) {
	    list.add("us");
	}
	if ("cs".contains(args[1])) {
	    list.add("cz");
	}
	if ("fr".contains(args[1])) {
	    list.add("fr");
	}
	if ("zh".contains(args[1])) {
	    list.add("cn");
	}
	if ("ru".contains(args[1])) {
	    list.add("ru");
	}
	if ("es".contains(args[1])) {
	    list.add("es");
	}
	if ("lt".contains(args[1])) {
	    list.add("lt");
	}
	if ("it".contains(args[1])) {
	    list.add("it");
	}
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
	if ("currency".contains(args[0])) {
	    list.add("currency");
	}
	return list;
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
	return list;
    }
}
