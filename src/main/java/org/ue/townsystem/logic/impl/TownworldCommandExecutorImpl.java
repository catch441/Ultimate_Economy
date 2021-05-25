package org.ue.townsystem.logic.impl;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.TownworldCommandEnum;
import org.ue.townsystem.logic.api.TownworldManager;

public class TownworldCommandExecutorImpl implements CommandExecutor {

	private final ConfigManager configManager;
	private final TownworldManager townworldManager;
	private final MessageWrapper messageWrapper;

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
		} catch (TownsystemException e) {
			sender.sendMessage(e.getMessage());
		} catch (NumberFormatException e) {
			sender.sendMessage(messageWrapper.getErrorString(ExceptionMessageEnum.INVALID_PARAMETER, args[2]));
		}
		return true;
	}

	private boolean performCommand(CommandSender sender, String label, String[] args)
			throws NumberFormatException, TownsystemException {
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
			throws NumberFormatException, TownsystemException {
		if (args.length == 3) {
			townworldManager.getTownWorldByName(args[1]).setExpandPrice(Double.valueOf(args[2]));
			sender.sendMessage(messageWrapper.getString(MessageEnum.TOWNWORLD_SETEXPANDPRICE, args[2],
					configManager.getCurrencyText(Double.valueOf(args[2]))));
		} else {
			sender.sendMessage("/" + label + " setExpandPrice <world> <price/chunk>");
		}
		return true;
	}

	private boolean performSetFoundationPriceCommand(CommandSender sender, String label, String[] args)
			throws NumberFormatException, TownsystemException {
		if (args.length == 3) {
			townworldManager.getTownWorldByName(args[1]).setFoundationPrice(Double.valueOf(args[2]));
			sender.sendMessage(messageWrapper.getString(MessageEnum.TOWNWORLD_SETFOUNDATIONPRICE, args[2],
					configManager.getCurrencyText(Double.valueOf(args[2]))));
		} else {
			sender.sendMessage("/" + label + " setFoundationPrice <world> <price>");
		}
		return true;
	}

	private boolean performDisableCommand(CommandSender sender, String label, String[] args)
			throws TownsystemException {
		if (args.length == 2) {
			townworldManager.deleteTownWorld(args[1]);
			sender.sendMessage(messageWrapper.getString(MessageEnum.TOWNWORLD_DISABLE, args[1]));
		} else {
			sender.sendMessage("/" + label + " disable <world>");
		}
		return true;
	}

	private boolean performEnableCommand(CommandSender sender, String label, String[] args) throws TownsystemException {
		if (args.length == 2) {
			townworldManager.createTownWorld(args[1]);
			sender.sendMessage(messageWrapper.getString(MessageEnum.TOWNWORLD_ENABLE, args[1]));
		} else {
			sender.sendMessage("/" + label + " enable <world>");
		}
		return true;
	}
}
