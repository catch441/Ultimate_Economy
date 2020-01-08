package com.ue.player.impl;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.api.Job;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.townsystem.townworld.api.TownworldController;
import com.ue.ultimate_economy.Ultimate_Economy;

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
				sender.sendMessage(ChatColor.RED + e.getMessage());
			} catch (NumberFormatException e2) {
				sender.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
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
							player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("money_info") + " "
									+ ChatColor.GREEN + dFormat.format(ecoPlayer.getBankAmount()));
						} else if (args.length == 1 && player.hasPermission("Ultimate_Economy.adminpay")) {
							EconomyPlayer otherPlayer = EconomyPlayerController.getEconomyPlayerByName(args[0]);
							player.sendMessage(ChatColor.GREEN + args[0] + " " + ChatColor.GOLD
									+ Ultimate_Economy.messages.getString("money_info") + " " + ChatColor.GREEN
									+ dFormat.format(otherPlayer.getBankAmount()));
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
							String jobString = "";
							for(Job job:jobs) {
								jobString = jobString + job.getName() + ",";
							}
							jobString = jobString.substring(0, jobString.length()-1);
							if (jobs.size() > 0) {
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("myjobs_info1")
										+ " " + ChatColor.GREEN + jobString);
							} else {
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("myjobs_info2"));
							}
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
							Set<String> homes = ecoPlayer.getHomeList().keySet();
							String homeString = String.join(",", homes);
							player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("home_info") + " "
									+ ChatColor.GREEN + homeString);
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
							ecoPlayer.removeHome(args[0],true);
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
				player.sendMessage(ChatColor.RED + e.getMessage());
			} catch (NumberFormatException e2) {
				player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
			}
		}
		return true;
	}
}
