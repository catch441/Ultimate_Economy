package com.ue.player;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.exceptions.PlayerException;

import ultimate_economy.Ultimate_Economy;

public class PlayerCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("giveMoney")) {
			try {
				if (args.length == 2) {
					double amount = Double.valueOf(args[1]);
					EconomyPlayer receiver = EconomyPlayer.getEconomyPlayerByName(args[0]);
					Player p = Bukkit.getPlayer(args[0]);
					if (amount < 0) {
						receiver.decreasePlayerAmount(-amount, false);
					} else {
						receiver.increasePlayerAmount(amount);
					}
					if (p.isOnline()) {
						p.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("got_money") + " "
								+ ChatColor.GREEN + amount + " $");
					}
				} else {
					sender.sendMessage("/giveMoney <player> <amount>");
				}
			} catch (PlayerException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		else if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				EconomyPlayer ecoPlayer = EconomyPlayer.getEconomyPlayerByName(player.getName());
				if (label.equalsIgnoreCase("bank")) {
					if (args.length == 1) {
						if (args[0].equals("on") || args[0].equals("off")) {
							if (args[0].equals("on")) {
								ecoPlayer.setScoreBoardDisabled(false);
							} else {
								ecoPlayer.setScoreBoardDisabled(true);
							}
							ecoPlayer.updateScoreBoard(player);
						} else {
							player.sendMessage(ChatColor.RED + "/bank on/off");
						}
					} else {
						player.sendMessage("/bank <on/off>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("money")) {
					if (args.length == 0) {
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("money_info") + " "
								+ ChatColor.GREEN + ecoPlayer.getBankAmount());
					} else if(args.length == 1 && player.hasPermission("Ultimate_Economy.adminpay")) {
						EconomyPlayer otherPlayer = EconomyPlayer.getEconomyPlayerByName(args[0]);
						player.sendMessage(ChatColor.GREEN + args[0] + " " + ChatColor.GOLD + Ultimate_Economy.messages.getString("money_info") + " "
								+ ChatColor.GREEN + otherPlayer.getBankAmount());
					} else if(player.hasPermission("Ultimate_Economy.adminpay")) {
						player.sendMessage("/money or /money <player>");
					} else {
						player.sendMessage("/money");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("myJobs")) {
					if (args.length == 0) {
						List<String> jobNames = ecoPlayer.getJobList();
						String jobString = jobNames.toString();
						jobString = jobString.replace("[", "");
						jobString = jobString.replace("]", "");
						if (jobNames.size() > 0) {
							player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("myjobs_info1")
									+ " " + ChatColor.GREEN + jobString);
						} else {
							player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("myjobs_info2"));
						}
					}

					else {
						player.sendMessage("/myJobs");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("home")) {
					if (args.length == 1) {
						Location location = ecoPlayer.getHome(args[0]);
						player.teleport(location);
					} else {
						player.sendMessage("/home <homename>");
						Set<String> homes = ecoPlayer.getHomeList().keySet();
						String homeString = String.join(",", homes);
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("home_info") + " "
								+ ChatColor.GREEN + homeString);
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("sethome")) {
					if (args.length == 1) {
						ecoPlayer.addHome(args[0], player.getLocation());
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("sethome") + " "
								+ ChatColor.GREEN + args[0] + ChatColor.GOLD + ".");
					} else {
						player.sendMessage("/sethome <homename>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("delhome")) {
					if (args.length == 1) {
						ecoPlayer.removeHome(args[0]);
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("delhome1") + " "
								+ ChatColor.GREEN + args[0] + ChatColor.GOLD + " "
								+ Ultimate_Economy.messages.getString("delhome2") + ".");
					} else {
						player.sendMessage("/deletehome <homename>");
					}
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				else if (label.equalsIgnoreCase("pay")) {
					if (args.length == 2) {
						double money = Double.valueOf(args[1]);
						ecoPlayer.payToOtherPlayer(EconomyPlayer.getEconomyPlayerByName(args[0]), money);
						Player p = Bukkit.getPlayer(args[0]);
						if (p != null && p.isOnline()) {
							p.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("got_money") + " "
									+ ChatColor.GREEN + " " + money + " $ " + ChatColor.GOLD
									+ Ultimate_Economy.messages.getString("got_money_from") + " " + ChatColor.GREEN
									+ player.getName());
						}
						player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("gave_money") + " "
								+ ChatColor.GREEN + args[0] + " " + money + " $ ");
					} else {
						player.sendMessage("/pay <name> <amount>");
					}
				}
			} catch (PlayerException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
			} catch (NumberFormatException e2) {
				player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
			}
		}
		return false;
	}
}
