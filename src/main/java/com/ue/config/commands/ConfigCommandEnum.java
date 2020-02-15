package com.ue.config.commands;

import org.bukkit.command.CommandSender;

import com.ue.config.api.ConfigController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.language.MessageWrapper;
import com.ue.ultimate_economy.UltimateEconomy;

public enum ConfigCommandEnum {

    LANGUAGE {
	@Override
	boolean perform(String label, String[] args, CommandSender sender) throws GeneralEconomyException {
	    // TODO refractor
	    if (args.length == 3) {
		if (!args[1].equals("cs") && !args[1].equals("de") && !args[1].equals("en") && !args[1].equals("fr")
			&& !args[1].equals("zh") && !args[1].equals("ru") && !args[1].equals("es")
			&& !args[1].equals("lt")) {
		    sender.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[1]));
		} else if (!args[2].equals("CZ") && !args[2].equals("DE") && !args[2].equals("US")
			&& !args[2].equals("FR") && !args[2].equals("CN") && !args[2].equals("RU")
			&& !args[2].equals("ES") && !args[2].equals("LT")) {
		    sender.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[2]));
		} else {
		    UltimateEconomy.getInstance.getConfig().set("localeLanguage", args[1]);
		    UltimateEconomy.getInstance.getConfig().set("localeCountry", args[2]);
		    UltimateEconomy.getInstance.saveConfig();
		    sender.sendMessage(MessageWrapper.getString("restart"));
		}
	    } else {
		sender.sendMessage("/" + label + " language <language> <country>");
	    }
	    return true;
	}
    },
    MAXHOMES {
	@Override
	boolean perform(String label, String[] args, CommandSender sender) throws GeneralEconomyException {
	    if (args.length == 2) {
		ConfigController.setMaxHomes(Integer.valueOf(args[1]));
		sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	    } else {
		sender.sendMessage("/" + label + " maxHomes <number>");
	    }
	    return true;
	}
    },
    MAXRENTEDDAYS {
	@Override
	boolean perform(String label, String[] args, CommandSender sender) throws GeneralEconomyException {
	    if (args.length == 2) {
		ConfigController.setMaxRentedDays(Integer.valueOf(args[1]));
		sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	    } else {
		sender.sendMessage("/" + label + " maxRentedDays <number>");
	    }
	    return true;
	}
    },
    MAXJOBS {
	@Override
	boolean perform(String label, String[] args, CommandSender sender) throws GeneralEconomyException {
	    if (args.length == 2) {
		ConfigController.setMaxJobs(Integer.valueOf(args[1]));
		sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	    } else {
		sender.sendMessage("/" + label + " maxJobs <number>");
	    }
	    return true;
	}
    },
    MAXJOINEDTOWNS {
	@Override
	boolean perform(String label, String[] args, CommandSender sender) throws GeneralEconomyException {
	    if (args.length == 2) {
		ConfigController.setMaxJoinedTowns(Integer.valueOf(args[1]));
		sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	    } else {
		sender.sendMessage("/" + label + " maxJoinedTowns <number>");
	    }
	    return true;
	}
    },
    HOMES {
	@Override
	boolean perform(String label, String[] args, CommandSender sender) throws GeneralEconomyException {
	    if (args.length == 2) {
		ConfigController.setHomeSystem(stringToBoolean(args[1]));
		sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
		sender.sendMessage(MessageWrapper.getString("restart"));
	    } else {
		sender.sendMessage("/" + label + " homes <true/false>");
	    }
	    return true;
	}
    },
    MAXPLAYERSHOPS {
	@Override
	boolean perform(String label, String[] args, CommandSender sender) throws GeneralEconomyException {
	    if (args.length == 2) {
		ConfigController.setMaxPlayershops(Integer.valueOf(args[1]));
		sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	    } else {
		sender.sendMessage("/" + label + " maxPlayershops <number>");
	    }
	    return true;
	}
    },
    EXTENDEDINTERACTION {
	@Override
	boolean perform(String label, String[] args, CommandSender sender) throws GeneralEconomyException {
	    if (args.length == 2) {
		ConfigController.setExtendedInteraction(stringToBoolean(args[1]));
		sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	    } else {
		sender.sendMessage("/" + label + " extendedInteraction <true/false>");
	    }
	    return true;
	}
    },
    WILDERNESSINTERACTION {
	@Override
	boolean perform(String label, String[] args, CommandSender sender) throws GeneralEconomyException {
	    if (args.length == 2) {
		ConfigController.setWildernessInteraction(stringToBoolean(args[1]));
		sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	    } else {
		sender.sendMessage("/" + label + " wildernessInteraction <true/false>");
	    }
	    return true;
	}
    },
    CURRENCY {
	@Override
	boolean perform(String label, String[] args, CommandSender sender) throws GeneralEconomyException {
	    if (args.length == 3) {
		ConfigController.setCurrencyPl(args[2]);
		ConfigController.setCurrencySg(args[1]);
		sender.sendMessage(MessageWrapper.getString("config_change", args[1] + " " + args[2]));
		sender.sendMessage(MessageWrapper.getString("restart"));
	    } else {
		sender.sendMessage("/" + label + " currency <singular> <plural>");
	    }
	    return true;
	}
    };

    abstract boolean perform(String label, String[] args, CommandSender sender) throws GeneralEconomyException;

    /**
     * Returns a enum. Returns null, if no enum is found.
     * 
     * @param value
     * @return config command enum
     */
    public static ConfigCommandEnum getEnum(String value) {
	for (ConfigCommandEnum command : values()) {
	    if (command.name().equalsIgnoreCase(value)) {
		return command;
	    }
	}
	return null;
    }
    
    private static boolean stringToBoolean(String string) {
	if("true".equalsIgnoreCase(string)) {
	    return true;
	} else if("false".equalsIgnoreCase(string)) {
	    return false;
	} else {
	    throw new IllegalArgumentException();
	}
    }
}
