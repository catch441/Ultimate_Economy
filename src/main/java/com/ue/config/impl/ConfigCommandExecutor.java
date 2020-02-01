package com.ue.config.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.ue.config.api.ConfigController;
import com.ue.exceptions.PlayerException;
import com.ue.language.MessageWrapper;
import com.ue.ultimate_economy.Ultimate_Economy;

public class ConfigCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 0) {
			try {
				switch (args[0]) {
					case "language":
						// TODO refractor
						if (args.length == 3) {
							if (!args[1].equals("cs") && !args[1].equals("de") && !args[1].equals("en")
									&& !args[1].equals("fr") && !args[1].equals("zh") && !args[1].equals("ru")
									&& !args[1].equals("es") && !args[1].equals("lt")) {
								sender.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[1]));
							} else if (!args[2].equals("CZ") && !args[2].equals("DE") && !args[2].equals("US")
									&& !args[2].equals("FR") && !args[2].equals("CN") && !args[2].equals("RU")
									&& !args[2].equals("ES") && !args[2].equals("LT")) {
								sender.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[2]));
							} else {
								Ultimate_Economy.getInstance.getConfig().set("localeLanguage", args[1]);
								Ultimate_Economy.getInstance.getConfig().set("localeCountry", args[2]);
								Ultimate_Economy.getInstance.saveConfig();
								sender.sendMessage(MessageWrapper.getString("restart"));
							}
						} else {
							sender.sendMessage("/" + label + " language <language> <country>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "maxHomes":
						if (args.length == 2) {
							ConfigController.setMaxHomes(Integer.valueOf(args[1]));
							sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
						} else {
							sender.sendMessage("/" + label + " maxHomes <number>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "maxRentedDays":
						if (args.length == 2) {
							ConfigController.setMaxRentedDays(Integer.valueOf(args[1]));
							sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
						} else {
							sender.sendMessage("/" + label + " maxRentedDays <number>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "maxJobs":
						if (args.length == 2) {
							ConfigController.setMaxJobs(Integer.valueOf(args[1]));
							sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
						} else {
							sender.sendMessage("/" + label + " maxJobs <number>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "maxJoinedTowns":
						if (args.length == 2) {
							ConfigController.setMaxJoinedTowns(Integer.valueOf(args[1]));
							sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
						} else {
							sender.sendMessage("/" + label + " maxJoinedTowns <number>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "homes":
						if (args.length == 2) {
							ConfigController.setHomeSystem(Boolean.valueOf(args[1]));
							sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
							sender.sendMessage(MessageWrapper.getString("restart"));
						} else {
							sender.sendMessage("/" + label + " homes <true/false>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "maxPlayershops":
						if (args.length == 2) {
							ConfigController.setMaxPlayershops(Integer.valueOf(args[1]));
							sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
						} else {
							sender.sendMessage("/" + label + " maxPlayershops <number>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "extendedInteraction": 
						if (args.length == 2) {
							ConfigController.setExtendedInteraction(Boolean.valueOf(args[1]));
							sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
						} else {
							sender.sendMessage("/" + label + " extendedInteraction <true/false>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "wildernessInteraction":
						if(args.length == 2) {
							ConfigController.setWildernessInteraction(Boolean.valueOf(args[1]));
							sender.sendMessage(MessageWrapper.getString("config_change", args[1]));
						} else {
							sender.sendMessage("/" + label + " wildernessInteraction <true/false>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "currency":
						if(args.length == 3) {
							ConfigController.setCurrencyPl(args[2]);
							ConfigController.setCurrencySg(args[1]);
							sender.sendMessage(MessageWrapper.getString("config_change", args[1] + " " + args[2]));
							sender.sendMessage(MessageWrapper.getString("restart"));
						} else {
							sender.sendMessage("/" + label + " currency <singular> <plural>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					default: return false;
				}
			} catch (PlayerException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			} catch (IllegalArgumentException e2) {
				sender.sendMessage(MessageWrapper.getErrorString("invalid_parameter",args[1]));
			}
		} else {
			return false;
		}
		return true;
	}
}
