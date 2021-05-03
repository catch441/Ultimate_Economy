package org.ue.economyplayer.logic.impl;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ue.bank.logic.api.BankException;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerCommandEnum;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import org.ue.jobsystem.logic.api.Job;
import org.ue.townsystem.logic.api.TownworldManager;

public class EconomyPlayerCommandExecutorImpl implements CommandExecutor {

	private final ConfigManager configManager;
	private final MessageWrapper messageWrapper;
	private final EconomyPlayerManager ecoPlayerManager;
	private final TownworldManager townworldManager;
	private final EconomyPlayerValidationHandler validationHandler;

	@Inject
	public EconomyPlayerCommandExecutorImpl(EconomyPlayerValidationHandler validationHandler,
			ConfigManager configManager, MessageWrapper messageWrapper, EconomyPlayerManager ecoPlayerManager,
			TownworldManager townworldManager) {
		this.configManager = configManager;
		this.messageWrapper = messageWrapper;
		this.ecoPlayerManager = ecoPlayerManager;
		this.townworldManager = townworldManager;
		this.validationHandler = validationHandler;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			switch (label) {
			case "givemoney":
				return performGiveMoneyCommand(args);
			case "removemoney":
				return performRemoveMoneyCommand(args);
			default:
				if (sender instanceof Player) {
					EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(sender.getName());
					return performCommand(label, args, (Player) sender, ecoPlayer);
				}
			}
		} catch (EconomyPlayerException | BankException e) {
			sender.sendMessage(e.getMessage());
		} catch (NumberFormatException e) {
			sender.sendMessage(messageWrapper.getErrorString("invalid_parameter", args[1]));
		}
		return true;
	}

	private boolean performCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws EconomyPlayerException, NumberFormatException, BankException {
		switch (EconomyPlayerCommandEnum.getEnum(label)) {
		case BANK:
			return performBankCommand(args, player, ecoPlayer);
		case DELHOME:
			return performDelHomeCommand(args, player, ecoPlayer);
		case HOME:
			return performHomeCommand(args, player, ecoPlayer);
		case MONEY:
			return performMoneyCommand(args, player, ecoPlayer);
		case MYJOBS:
			return performMyJobsCommand(args, player, ecoPlayer);
		case PAY:
			return performPayCommand(args, player, ecoPlayer);
		case SETHOME:
			return performSetHomeCommand(args, player, ecoPlayer);
		default:
			return false;
		}
	}

	private boolean performBankCommand(String[] args, Player player, EconomyPlayer ecoPlayer) {
		if (args.length == 1) {
			if (args[0].equals("on") || args[0].equals("off")) {
				if (args[0].equals("on")) {
					ecoPlayer.setScoreBoardObjectiveVisible(true);
				} else {
					ecoPlayer.setScoreBoardObjectiveVisible(false);
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	private boolean performMoneyCommand(String[] args, Player player, EconomyPlayer ecoPlayer)
			throws EconomyPlayerException {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ROOT);
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(',');
		DecimalFormat dFormat = new DecimalFormat("#.##", otherSymbols);
		dFormat.setRoundingMode(RoundingMode.DOWN);
		if (args.length == 0) {
			player.sendMessage(
					messageWrapper.getString("money_info", dFormat.format(ecoPlayer.getBankAccount().getAmount()),
							configManager.getCurrencyText(ecoPlayer.getBankAccount().getAmount())));
		} else if (args.length == 1 && player.hasPermission("Ultimate_Economy.adminpay")) {
			EconomyPlayer otherPlayer = ecoPlayerManager.getEconomyPlayerByName(args[0]);
			player.sendMessage(
					messageWrapper.getString("money_info", dFormat.format(otherPlayer.getBankAccount().getAmount()),
							configManager.getCurrencyText(otherPlayer.getBankAccount().getAmount())));
		} else if (player.hasPermission("Ultimate_Economy.adminpay")) {
			player.sendMessage("/money or /money <player>");
		} else {
			return false;
		}
		return true;
	}

	private boolean performMyJobsCommand(String[] args, Player player, EconomyPlayer ecoPlayer) {
		if (args.length == 0) {
			List<Job> jobs = ecoPlayer.getJobList();
			List<String> jobNames = new ArrayList<>();
			for (Job job : jobs) {
				jobNames.add(job.getName());
			}
			player.sendMessage(messageWrapper.getString("myjobs_info", jobNames.toString()));
		} else {
			return false;
		}
		return true;
	}

	private boolean performHomeCommand(String[] args, Player player, EconomyPlayer ecoPlayer)
			throws EconomyPlayerException {
		if (args.length == 1) {
			Location location = ecoPlayer.getHome(args[0]);
			player.teleport(location);
			townworldManager.performTownWorldLocationCheck(ecoPlayer);
		} else if (args.length == 0) {
			player.sendMessage(messageWrapper.getString("home_info", ecoPlayer.getHomeList().keySet().toString()));
		} else {
			return false;
		}
		return true;
	}

	private boolean performSetHomeCommand(String[] args, Player player, EconomyPlayer ecoPlayer)
			throws EconomyPlayerException {
		if (args.length == 1) {
			ecoPlayer.addHome(args[0], player.getLocation(), true);
		} else {
			return false;
		}
		return true;
	}

	private boolean performDelHomeCommand(String[] args, Player player, EconomyPlayer ecoPlayer)
			throws EconomyPlayerException {
		if (args.length == 1) {
			ecoPlayer.removeHome(args[0], true);
		} else {
			return false;
		}
		return true;
	}

	private boolean performPayCommand(String[] args, Player player, EconomyPlayer ecoPlayer)
			throws NumberFormatException, EconomyPlayerException, BankException {
		if (args.length == 2) {
			ecoPlayer.payToOtherPlayer(ecoPlayerManager.getEconomyPlayerByName(args[0]), Double.valueOf(args[1]), true);
		} else {
			return false;
		}
		return true;
	}

	private boolean performGiveMoneyCommand(String[] args)
			throws EconomyPlayerException, BankException, NumberFormatException {
		if (args.length == 2) {
			double amount = Double.valueOf(args[1]);
			validationHandler.checkForPositiveValue(amount);
			EconomyPlayer receiver = ecoPlayerManager.getEconomyPlayerByName(args[0]);
			receiver.increasePlayerAmount(amount, true);
		} else {
			return false;
		}
		return true;
	}

	private boolean performRemoveMoneyCommand(String[] args)
			throws EconomyPlayerException, BankException, NumberFormatException {
		if (args.length == 2) {
			double amount = Double.valueOf(args[1]);
			validationHandler.checkForPositiveValue(amount);
			EconomyPlayer receiver = ecoPlayerManager.getEconomyPlayerByName(args[0]);
			receiver.decreasePlayerAmount(amount, false);
		} else {
			return false;
		}
		return true;
	}
}
