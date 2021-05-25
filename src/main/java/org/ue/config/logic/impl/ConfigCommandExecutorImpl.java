package org.ue.config.logic.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigCommandEnum;
import org.ue.config.logic.api.ConfigException;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;

public class ConfigCommandExecutorImpl implements CommandExecutor {

	private final ConfigManager configManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private final MessageWrapper messageWrapper;

	public ConfigCommandExecutorImpl(ConfigManager configManager, EconomyPlayerManager ecoPlayerManager,
			MessageWrapper messageWrapper) {
		this.configManager = configManager;
		this.ecoPlayerManager = ecoPlayerManager;
		this.messageWrapper = messageWrapper;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 0) {
			try {
				return performCommand(label, args, sender);
			} catch (ConfigException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			} catch (IllegalArgumentException e) {
				sender.sendMessage(messageWrapper.getErrorString(ExceptionMessageEnum.INVALID_PARAMETER, args[1]));
			}
		} else {
			return false;
		}
		return true;
	}

	private boolean performCommand(String label, String[] args, CommandSender sender)
			throws NumberFormatException, ConfigException {
		switch (ConfigCommandEnum.getEnum(args[0])) {
		case CURRENCY:
			return performCurrencyCommand(label, args, sender);
		case EXTENDEDINTERACTION:
			return performExtendedInteractionCommand(label, args, sender);
		case HOMES:
			return performHomesCommand(label, args, sender);
		case LANGUAGE:
			return performLanguageCommand(label, args, sender);
		case MAXHOMES:
			return performMaxHomesCommand(label, args, sender);
		case MAXJOBS:
			return performMaxJobsCommand(label, args, sender);
		case MAXJOINEDTOWNS:
			return performMaxJoinedTownsCommand(label, args, sender);
		case MAXPLAYERSHOPS:
			return performMaxPlayershopsCommand(label, args, sender);
		case MAXRENTEDDAYS:
			return performMaxRentedDaysCommand(label, args, sender);
		case WILDERNESSINTERACTION:
			return performWildernessInteractionCommand(label, args, sender);
		case STARTAMOUNT:
			return performStartAmountCommand(label, args, sender);
		case ALLOWQUICKSHOP:
			return performAllowQuickshopCommand(label, args, sender);
		default:
			return false;
		}
	}

	private boolean performAllowQuickshopCommand(String label, String[] args, CommandSender sender)
			throws NumberFormatException, ConfigException {
		if (args.length == 2) {
			boolean input = stringToBoolean(args[1]);
			configManager.setAllowQuickshop(input);
			sender.sendMessage(messageWrapper.getString(MessageEnum.CONFIG_CHANGE, args[1]));
		} else {
			sender.sendMessage("/" + label + " allowQuickshop <true/false>");
		}
		return true;
	}

	private boolean performStartAmountCommand(String label, String[] args, CommandSender sender)
			throws NumberFormatException, ConfigException {
		if (args.length == 2) {
			configManager.setStartAmount(Double.valueOf(args[1]));
			sender.sendMessage(messageWrapper.getString(MessageEnum.CONFIG_CHANGE, args[1]));
		} else {
			sender.sendMessage("/" + label + " startAmount <amount>");
		}
		return true;
	}

	private boolean performLanguageCommand(String label, String[] args, CommandSender sender) throws ConfigException {
		if (args.length == 3) {
			configManager.setLocale(args[1], args[2]);
			sender.sendMessage(messageWrapper.getString(MessageEnum.RESTART));
		} else {
			sender.sendMessage("/" + label + " language <language> <country>");
		}
		return true;
	}

	private boolean performMaxHomesCommand(String label, String[] args, CommandSender sender)
			throws NumberFormatException, ConfigException {
		if (args.length == 2) {
			configManager.setMaxHomes(Integer.valueOf(args[1]));
			sender.sendMessage(messageWrapper.getString(MessageEnum.CONFIG_CHANGE, args[1]));
		} else {
			sender.sendMessage("/" + label + " maxHomes <number>");
		}
		return true;
	}

	private boolean performMaxRentedDaysCommand(String label, String[] args, CommandSender sender)
			throws NumberFormatException, ConfigException {
		if (args.length == 2) {
			configManager.setMaxRentedDays(Integer.valueOf(args[1]));
			sender.sendMessage(messageWrapper.getString(MessageEnum.CONFIG_CHANGE, args[1]));
		} else {
			sender.sendMessage("/" + label + " maxRentedDays <number>");
		}
		return true;
	}

	private boolean performMaxJobsCommand(String label, String[] args, CommandSender sender)
			throws NumberFormatException, ConfigException {
		if (args.length == 2) {
			configManager.setMaxJobs(Integer.valueOf(args[1]));
			sender.sendMessage(messageWrapper.getString(MessageEnum.CONFIG_CHANGE, args[1]));
		} else {
			sender.sendMessage("/" + label + " maxJobs <number>");
		}
		return true;
	}

	private boolean performMaxJoinedTownsCommand(String label, String[] args, CommandSender sender)
			throws NumberFormatException, ConfigException {
		if (args.length == 2) {
			configManager.setMaxJoinedTowns(Integer.valueOf(args[1]));
			sender.sendMessage(messageWrapper.getString(MessageEnum.CONFIG_CHANGE, args[1]));
		} else {
			sender.sendMessage("/" + label + " maxJoinedTowns <number>");
		}
		return true;
	}

	private boolean performHomesCommand(String label, String[] args, CommandSender sender) {
		if (args.length == 2) {
			configManager.setHomeSystem(stringToBoolean(args[1]));
			sender.sendMessage(messageWrapper.getString(MessageEnum.CONFIG_CHANGE, args[1]));
			sender.sendMessage(messageWrapper.getString(MessageEnum.RESTART));
		} else {
			sender.sendMessage("/" + label + " homes <true/false>");
		}
		return true;
	}

	private boolean performMaxPlayershopsCommand(String label, String[] args, CommandSender sender)
			throws NumberFormatException, ConfigException {
		if (args.length == 2) {
			configManager.setMaxPlayershops(Integer.valueOf(args[1]));
			sender.sendMessage(messageWrapper.getString(MessageEnum.CONFIG_CHANGE, args[1]));
		} else {
			sender.sendMessage("/" + label + " maxPlayershops <number>");
		}
		return true;
	}

	private boolean performExtendedInteractionCommand(String label, String[] args, CommandSender sender) {
		if (args.length == 2) {
			configManager.setExtendedInteraction(stringToBoolean(args[1]));
			sender.sendMessage(messageWrapper.getString(MessageEnum.CONFIG_CHANGE, args[1]));
		} else {
			sender.sendMessage("/" + label + " extendedInteraction <true/false>");
		}
		return true;
	}

	private boolean performWildernessInteractionCommand(String label, String[] args, CommandSender sender) {
		if (args.length == 2) {
			boolean input = stringToBoolean(args[1]);
			configManager.setWildernessInteraction(input);
			if (input) {
				for (EconomyPlayer player : ecoPlayerManager.getAllEconomyPlayers()) {
					player.addWildernessPermission();
				}
			} else {
				for (EconomyPlayer player : ecoPlayerManager.getAllEconomyPlayers()) {
					player.denyWildernessPermission();
				}
			}
			sender.sendMessage(messageWrapper.getString(MessageEnum.CONFIG_CHANGE, args[1]));
		} else {
			sender.sendMessage("/" + label + " wildernessInteraction <true/false>");
		}
		return true;
	}

	private boolean performCurrencyCommand(String label, String[] args, CommandSender sender) {
		if (args.length == 3) {
			configManager.setCurrencyPl(args[2]);
			configManager.setCurrencySg(args[1]);
			sender.sendMessage(messageWrapper.getString(MessageEnum.CONFIG_CHANGE, args[1] + " " + args[2]));
			sender.sendMessage(messageWrapper.getString(MessageEnum.RESTART));
		} else {
			sender.sendMessage("/" + label + " currency <singular> <plural>");
		}
		return true;
	}

	private boolean stringToBoolean(String string) {
		if ("true".equalsIgnoreCase(string)) {
			return true;
		} else if ("false".equalsIgnoreCase(string)) {
			return false;
		} else {
			throw new IllegalArgumentException();
		}
	}
}
