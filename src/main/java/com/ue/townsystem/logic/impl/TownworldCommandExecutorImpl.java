package com.ue.townsystem.logic.impl;

import javax.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.townsystem.logic.api.TownworldManager;

public class TownworldCommandExecutorImpl implements CommandExecutor {

	private final ConfigManager configManager;
	private final TownworldManager townworldManager;
	private final MessageWrapper messageWrapper;

	@Inject
	public TownworldCommandExecutorImpl(ConfigManager configManager, TownworldManager townworldManager,
			MessageWrapper messageWrapper) {
		this.configManager = configManager;
		this.townworldManager = townworldManager;
		this.messageWrapper = messageWrapper;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (args.length > 0) {
				return performCommand(sender, label, args);
			} else {
				return false;
			}
		} catch (EconomyPlayerException | TownSystemException | GeneralEconomyException e) {
			sender.sendMessage(e.getMessage());
		} catch (NumberFormatException e) {
			sender.sendMessage(messageWrapper.getErrorString("invalid_parameter", args[2]));
		}
		return true;
	}

	private boolean performCommand(CommandSender sender, String label, String[] args)
			throws EconomyPlayerException, GeneralEconomyException, TownSystemException {
		switch (TownworldCommandEnum.getEnum(args[0])) {
		case DISABLE:
			return performDisableCommand(sender, label, args);
		case ENABLE:
			return performEnableCommand(sender, label, args);
		case SETEXPANDPRICE:
			return performSetExpandPriceCommand(sender, label, args);
		case SETFOUNDATIONPRICE:
			return performSetFoundationPriceCommand(sender, label, args);
		default:
			return false;
		}
	}

	private boolean performSetExpandPriceCommand(CommandSender sender, String label, String[] args)
			throws TownSystemException, NumberFormatException, GeneralEconomyException {
		if (args.length == 3) {
			townworldManager.getTownWorldByName(args[1]).setExpandPrice(Double.valueOf(args[2]));
			sender.sendMessage(messageWrapper.getString("townworld_setExpandPrice", args[2],
					configManager.getCurrencyText(Double.valueOf(args[2]))));
		} else {
			sender.sendMessage("/" + label + " setExpandPrice <world> <price/chunk>");
		}
		return true;
	}

	private boolean performSetFoundationPriceCommand(CommandSender sender, String label, String[] args)
			throws TownSystemException, NumberFormatException, GeneralEconomyException {
		if (args.length == 3) {
			townworldManager.getTownWorldByName(args[1]).setFoundationPrice(Double.valueOf(args[2]));
			sender.sendMessage(messageWrapper.getString("townworld_setFoundationPrice", args[2],
					configManager.getCurrencyText(Double.valueOf(args[2]))));
		} else {
			sender.sendMessage("/" + label + " setFoundationPrice <world> <price>");
		}
		return true;
	}

	private boolean performDisableCommand(CommandSender sender, String label, String[] args)
			throws EconomyPlayerException, GeneralEconomyException, TownSystemException {
		if (args.length == 2) {
			townworldManager.deleteTownWorld(args[1]);
			sender.sendMessage(messageWrapper.getString("townworld_disable", args[1]));
		} else {
			sender.sendMessage("/" + label + " disable <world>");
		}
		return true;
	}

	private boolean performEnableCommand(CommandSender sender, String label, String[] args)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		if (args.length == 2) {
			townworldManager.createTownWorld(args[1]);
			sender.sendMessage(messageWrapper.getString("townworld_enable", args[1]));
		} else {
			sender.sendMessage("/" + label + " enable <world>");
		}
		return true;
	}
}
