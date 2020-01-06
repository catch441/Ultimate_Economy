package com.ue.config;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.ue.exceptions.PlayerException;
import com.ue.player.EconomyPlayer;
import com.ue.shopsystem.rentshop.api.RentshopController;

import ultimate_economy.Ultimate_Economy;

public class ConfigCommandExecutor implements CommandExecutor {

	private Ultimate_Economy plugin;

	public ConfigCommandExecutor(Ultimate_Economy plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 0) {
			try {
				switch (args[0]) {
					case "language":
						if (args.length == 3) {
							if (!args[1].equals("cs") && !args[1].equals("de") && !args[1].equals("en")
									&& !args[1].equals("fr") && !args[1].equals("zh") && !args[1].equals("ru")
									&& !args[1].equals("es") && !args[1].equals("lt")) {
								sender.sendMessage(
										ChatColor.RED + Ultimate_Economy.messages.getString("invalid_language"));
							} else if (!args[2].equals("CZ") && !args[2].equals("DE") && !args[2].equals("US")
									&& !args[2].equals("FR") && !args[2].equals("CN") && !args[2].equals("RU")
									&& !args[2].equals("ES") && !args[2].equals("LT")) {
								sender.sendMessage(
										ChatColor.RED + Ultimate_Economy.messages.getString("invalid_country"));
							} else {
								plugin.getConfig().set("localeLanguage", args[1]);
								plugin.getConfig().set("localeCountry", args[2]);
								plugin.saveConfig();
								sender.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("restart"));
							}
						} else {
							sender.sendMessage("/" + label + " language <language> <country>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "maxHomes":
						if (args.length == 2) {
							EconomyPlayer.setMaxHomes(plugin.getConfig(), Integer.valueOf(args[1]));
							plugin.saveConfig();
							sender.sendMessage(
									ChatColor.GOLD + Ultimate_Economy.messages.getString("max_homes_change") + " "
											+ ChatColor.GREEN + args[1] + ChatColor.GOLD + ".");
						} else {
							sender.sendMessage("/" + label + " maxHomes <number>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "maxRentedDays":
						if (args.length == 2) {
							RentshopController.setMaxRentedDays(plugin.getConfig(), Integer.valueOf(args[1]));
							plugin.saveConfig();
							sender.sendMessage(
									ChatColor.GOLD + Ultimate_Economy.messages.getString("max_rented_days") + " "
											+ ChatColor.GREEN + args[1] + ChatColor.GOLD + ".");
						} else {
							sender.sendMessage("/" + label + " maxRentedDays <number>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "maxJobs":
						if (args.length == 2) {
							EconomyPlayer.setMaxJobs(plugin.getConfig(), Integer.valueOf(args[1]));
							plugin.saveConfig();
							sender.sendMessage(
									ChatColor.GOLD + Ultimate_Economy.messages.getString("max_jobs_change") + " "
											+ ChatColor.GREEN + args[1] + ChatColor.GOLD + ".");
						} else {
							sender.sendMessage("/" + label + " maxJobs <number>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "maxJoinedTowns":
						if (args.length == 2) {
							EconomyPlayer.setMaxJoinedTowns(plugin.getConfig(), Integer.valueOf(args[1]));
							plugin.saveConfig();
							sender.sendMessage(
									ChatColor.GOLD + Ultimate_Economy.messages.getString("max_joined_towns_change")
											+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + ".");
						} else {
							sender.sendMessage("/" + label + " maxJoinedTowns <number>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "homes":
						if (args.length == 2) {
							if (args[1].equals("true") || args[1].equals("false")) {
								plugin.getConfig().set("homes", Boolean.valueOf(args[1]));
								plugin.saveConfig();
								sender.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("ue_homes")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + ".");
							} else {
								sender.sendMessage("/" + label + " homes <true/false>");
							}
						} else {
							sender.sendMessage("/" + label + " homes <true/false>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					default: return false;
				}
			} catch (PlayerException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			} catch (NumberFormatException e2) {
				sender.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
			}
		} else {
			return false;
		}
		return true;
	}
}
