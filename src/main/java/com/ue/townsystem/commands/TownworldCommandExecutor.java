package com.ue.townsystem.commands;

import javax.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.exceptions.TownSystemException;
import com.ue.townsystem.api.TownworldManagerImpl;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

public class TownworldCommandExecutor implements CommandExecutor {

	@Inject
	ConfigManager configManager;
	private UltimateEconomy plugin;

	/**
	 * Constructor of townworld command executor.
	 * 
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
		} catch (EconomyPlayerException | TownSystemException | GeneralEconomyException e) {
			sender.sendMessage(e.getMessage());
		} catch (NumberFormatException e) {
			sender.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[2]));
		}
		return true;
	}

	private boolean handleSetExpandPriceCommand(CommandSender sender, String label, String[] args)
			throws TownSystemException {
		if (args.length == 3) {
			TownworldManagerImpl.getTownWorldByName(args[1]).setExpandPrice(Double.valueOf(args[2]), true);
			sender.sendMessage(MessageWrapper.getString("townworld_setExpandPrice", args[2],
					configManager.getCurrencyText(Double.valueOf(args[2]))));
		} else {
			sender.sendMessage("/" + label + " setExpandPrice <world> <price / chunk");
		}
		return true;
	}

	private boolean handleSetFoundationPriceCommand(CommandSender sender, String label, String[] args)
			throws TownSystemException {
		if (args.length == 3) {
			TownworldManagerImpl.getTownWorldByName(args[1]).setFoundationPrice(Double.valueOf(args[2]), true);
			sender.sendMessage(MessageWrapper.getString("townworld_setFoundationPrice", args[2],
					configManager.getCurrencyText(Double.valueOf(args[2]))));
		} else {
			sender.sendMessage("/" + label + " setFoundationPrice <world> <price>");
		}
		return true;
	}

	private boolean handleDisableCommand(CommandSender sender, String label, String[] args)
			throws EconomyPlayerException, GeneralEconomyException, TownSystemException {
		if (args.length == 2) {
			TownworldManagerImpl.deleteTownWorld(args[1]);
			sender.sendMessage(MessageWrapper.getString("townworld_disable", args[1]));
		} else {
			sender.sendMessage("/" + label + " disable <world>");
		}
		return true;
	}

	private boolean handleEnableCommand(CommandSender sender, String label, String[] args) throws TownSystemException {
		if (args.length == 2) {
			TownworldManagerImpl.createTownWorld(args[1]);
			sender.sendMessage(MessageWrapper.getString("townworld_enable", args[1]));
		} else {
			sender.sendMessage("/" + label + " enable <world>");
		}
		return true;
	}
}
