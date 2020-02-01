package com.ue.player.impl;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.config.api.ConfigController;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.api.Job;
import com.ue.language.MessageWrapper;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.townsystem.townworld.api.TownworldController;

public class PlayerCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equals("givemoney")) {
			try {
				if (args.length == 2) {
					double amount = Double.valueOf(args[1]);
					EconomyPlayer receiver = EconomyPlayerController.getEconomyPlayerByName(args[0]);
					if (amount < 0) {
						receiver.decreasePlayerAmount(-amount, false);
					} else {
						receiver.increasePlayerAmount(amount, true);
					}
				} else {
					return false;
				}
			} catch (PlayerException e) {
				sender.sendMessage(e.getMessage());
			} catch (NumberFormatException e2) {
				sender.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[1]));
			}
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		else if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName(player.getName());
				switch (label) {
					case "bank":
						if (args.length == 1) {
							if (args[0].equals("on") || args[0].equals("off")) {
								if (args[0].equals("on")) {
									ecoPlayer.setScoreBoardDisabled(false);
								} else {
									ecoPlayer.setScoreBoardDisabled(true);
								}
							} else {
								return false;
							}
						} else {
							return false;
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "money":
						DecimalFormat dFormat = new DecimalFormat(".##");
						dFormat.setRoundingMode(RoundingMode.DOWN);
						if (args.length == 0) {
							player.sendMessage(
									MessageWrapper.getString("money_info", dFormat.format(ecoPlayer.getBankAmount()),
											ConfigController.getCurrencyText(ecoPlayer.getBankAmount())));
						} else if (args.length == 1 && player.hasPermission("Ultimate_Economy.adminpay")) {
							EconomyPlayer otherPlayer = EconomyPlayerController.getEconomyPlayerByName(args[0]);
							player.sendMessage(
									MessageWrapper.getString("money_info", dFormat.format(otherPlayer.getBankAmount()),
											ConfigController.getCurrencyText(otherPlayer.getBankAmount())));
						} else if (player.hasPermission("Ultimate_Economy.adminpay")) {
							player.sendMessage("/money or /money <player>");
						} else {
							return false;
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "myjobs":
						if (args.length == 0) {
							List<Job> jobs = ecoPlayer.getJobList();
							List<String> jobNames = new ArrayList<>();
							for (Job job : jobs) {
								jobNames.add(job.getName());
							}
							player.sendMessage(MessageWrapper.getString("myjobs_info", jobNames.toArray()));
						} else {
							return false;
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "home":
						if (args.length == 1) {
							Location location = ecoPlayer.getHome(args[0]);
							player.teleport(location);
							TownworldController.handleTownWorldLocationCheck(player.getWorld().getName(),
									player.getLocation().getChunk(), player.getName());
						} else if (args.length == 0) {
							Object[] homes = ecoPlayer.getHomeList().keySet().toArray();
							player.sendMessage(MessageWrapper.getString("home_info", homes));
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "sethome":
						if (args.length == 1) {
							ecoPlayer.addHome(args[0], player.getLocation(), true);
						} else {
							return false;
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "delhome":
						if (args.length == 1) {
							ecoPlayer.removeHome(args[0], true);
						} else {
							return false;
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "pay":
						if (args.length == 2) {
							ecoPlayer.payToOtherPlayer(EconomyPlayerController.getEconomyPlayerByName(args[0]),
									Double.valueOf(args[1]), true);
						} else {
							return false;
						}
						break;
				}
			} catch (PlayerException e) {
				player.sendMessage(e.getMessage());
			} catch (NumberFormatException e2) {
				player.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[1]));
			}
		}
		return true;
	}
}
