package com.ue.townsystem.townworld.impl;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.ue.config.api.ConfigController;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.townsystem.townworld.api.TownworldController;
import com.ue.ultimate_economy.UltimateEconomy;

public class TownworldCommandExecutor implements CommandExecutor {

    private UltimateEconomy plugin;

    /**
     * Constructor of townworld command executor.
     * @param plugin
     */
    public TownworldCommandExecutor(UltimateEconomy plugin) {
	this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	try {
	    if (args.length != 1) {
		switch (args[0]) {
		case "enable":
		    return handleEnableCommand(sender, label, args);
		case "disable":
		    return handleDisableCommand(sender, label, args);
		case "setFoundationPrice":
		    return handleSetFoundationPriceCommand(sender, label, args);
		case "setExpandPrice":
		    return handleSetExpandPriceCommand(sender, label, args);
		default:
		    return false;
		}
	    } else {
		return false;
	    }
	} catch (TownSystemException e) {
	    sender.sendMessage(e.getMessage());
	} catch (NumberFormatException e) {
	    sender.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[2]));
	}
	return true;
    }

    private boolean handleSetExpandPriceCommand(CommandSender sender, String label, String[] args)
	    throws TownSystemException {
	if (args.length == 3) {
	TownworldController.getTownWorldByName(args[1]).setExpandPrice(Double.valueOf(args[2]), true);
	sender.sendMessage(MessageWrapper.getString("townworld_setExpandPrice", args[2],
		ConfigController.getCurrencyText(Double.valueOf(args[2]))));
	} else {
	sender.sendMessage("/" + label + " setExpandPrice <world> <price / chunk");
	}
	return true;
    }

    private boolean handleSetFoundationPriceCommand(CommandSender sender, String label, String[] args)
	    throws TownSystemException {
	if (args.length == 3) {
	TownworldController.getTownWorldByName(args[1]).setFoundationPrice(Double.valueOf(args[2]),
		true);
	sender.sendMessage(MessageWrapper.getString("townworld_setFoundationPrice", args[2],
		ConfigController.getCurrencyText(Double.valueOf(args[2]))));
	} else {
	sender.sendMessage("/" + label + " setFoundationPrice <world> <price>");
	}
	return true;
    }

    private boolean handleDisableCommand(CommandSender sender, String label, String[] args) throws TownSystemException {
	if (args.length == 2) {
	TownworldController.deleteTownWorld(args[1]);
	sender.sendMessage(MessageWrapper.getString("townworld_disable", args[1]));
	} else {
	sender.sendMessage("/" + label + " disable <world>");
	}
	return true;
    }

    private boolean handleEnableCommand(CommandSender sender, String label, String[] args) throws TownSystemException {
	if (args.length == 2) {
	TownworldController.createTownWorld(plugin.getDataFolder(), args[1]);
	sender.sendMessage(MessageWrapper.getString("townworld_enable", args[1]));
	} else {
	sender.sendMessage("/" + label + " enable <world>");
	}
	return true;
    }
}
