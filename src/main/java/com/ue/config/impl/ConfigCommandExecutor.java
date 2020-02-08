package com.ue.config.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.ue.config.api.ConfigController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.language.MessageWrapper;
import com.ue.ultimate_economy.UltimateEconomy;

public class ConfigCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (args.length != 0) {
	    try {
		switch (args[0]) {
		case "language":
		    return handleLanguageCommand(sender, label, args);
		case "maxHomes":
		    return handleMaxHomesCommand(sender, label, args);
		case "maxRentedDays":
		    return handleMaxRentedDaysCommand(sender, label, args);
		case "maxJobs":
		    return handleMaxJobsCommand(sender, label, args);
		case "maxJoinedTowns":
		    return handleMaxJoinedTownsCommand(sender, label, args);
		case "homes":
		    return handleHomesCommand(sender, label, args);
		case "maxPlayershops":
		    return handleMaxPlayershopsCommand(sender, label, args);
		case "extendedInteraction":
		    return handleExtendedInteractionCommand(sender, label, args);
		case "wildernessInteraction":
		    return handleWildernessInteractionCommand(sender, label, args);
		case "currency":
		    return handleCurrencyCommand(sender, label, args);
		default:
		    return false;
		}
	    } catch (GeneralEconomyException e) {
		sender.sendMessage(ChatColor.RED + e.getMessage());
	    } catch (IllegalArgumentException e) {
		sender.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[1]));
	    }
	} else {
	    return false;
	}
	return true;
    }

    private boolean handleLanguageCommand(CommandSender sender, String label, String[] args) {
	// TODO refractor
	if (args.length == 3) {
	if (!args[1].equals("cs") && !args[1].equals("de") && !args[1].equals("en")
		&& !args[1].equals("fr") && !args[1].equals("zh") && !args[1].equals("ru")
		&& !args[1].equals("es") && !args[1].equals("lt")) {
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

    private boolean handleCurrencyCommand(CommandSender sender, String label, String[] args) {
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

    private boolean handleWildernessInteractionCommand(CommandSender sender, String label, String[] args) {
	if (args.length == 2) {
	ConfigController.setWildernessInteraction(Boolean.valueOf(args[1]));
	sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	} else {
	sender.sendMessage("/" + label + " wildernessInteraction <true/false>");
	}
	return true;
    }

    private boolean handleExtendedInteractionCommand(CommandSender sender, String label, String[] args) {
	if (args.length == 2) {
	ConfigController.setExtendedInteraction(Boolean.valueOf(args[1]));
	sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	} else {
	sender.sendMessage("/" + label + " extendedInteraction <true/false>");
	}
	return true;
    }

    private boolean handleMaxPlayershopsCommand(CommandSender sender, String label, String[] args)
	    throws GeneralEconomyException {
	if (args.length == 2) {
	ConfigController.setMaxPlayershops(Integer.valueOf(args[1]));
	sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	} else {
	sender.sendMessage("/" + label + " maxPlayershops <number>");
	}
	return true;
    }

    private boolean handleHomesCommand(CommandSender sender, String label, String[] args) {
	if (args.length == 2) {
	ConfigController.setHomeSystem(Boolean.valueOf(args[1]));
	sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	sender.sendMessage(MessageWrapper.getString("restart"));
	} else {
	sender.sendMessage("/" + label + " homes <true/false>");
	}
	return true;
    }

    private boolean handleMaxJoinedTownsCommand(CommandSender sender, String label, String[] args)
	    throws GeneralEconomyException {
	if (args.length == 2) {
	ConfigController.setMaxJoinedTowns(Integer.valueOf(args[1]));
	sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	} else {
	sender.sendMessage("/" + label + " maxJoinedTowns <number>");
	}
	return true;
    }

    private boolean handleMaxJobsCommand(CommandSender sender, String label, String[] args)
	    throws GeneralEconomyException {
	if (args.length == 2) {
	ConfigController.setMaxJobs(Integer.valueOf(args[1]));
	sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	} else {
	sender.sendMessage("/" + label + " maxJobs <number>");
	}
	return true;
    }

    private boolean handleMaxRentedDaysCommand(CommandSender sender, String label, String[] args)
	    throws GeneralEconomyException {
	if (args.length == 2) {
	ConfigController.setMaxRentedDays(Integer.valueOf(args[1]));
	sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	} else {
	sender.sendMessage("/" + label + " maxRentedDays <number>");
	}
	return true;
    }

    private boolean handleMaxHomesCommand(CommandSender sender, String label, String[] args)
	    throws GeneralEconomyException {
	if (args.length == 2) {
	ConfigController.setMaxHomes(Integer.valueOf(args[1]));
	sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
	} else {
	sender.sendMessage("/" + label + " maxHomes <number>");
	}
	return true;
    }
}
