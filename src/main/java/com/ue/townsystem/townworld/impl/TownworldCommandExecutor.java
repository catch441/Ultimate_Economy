package com.ue.townsystem.townworld.impl;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.ue.config.api.ConfigController;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.townsystem.townworld.api.TownworldController;
import com.ue.ultimate_economy.Ultimate_Economy;

public class TownworldCommandExecutor implements CommandExecutor {

	private Ultimate_Economy plugin;

	public TownworldCommandExecutor(Ultimate_Economy plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (args.length != 1) {
				switch (args[0]) {
					case "enable":
						if (args.length == 2) {
							TownworldController.createTownWorld(plugin.getDataFolder(), args[1]);
							sender.sendMessage(MessageWrapper.getString("townworld_enable", args[1]));
						} else {
							sender.sendMessage("/" + label + " enable <world>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "disable":
						if (args.length == 2) {
							TownworldController.deleteTownWorld(args[1]);
							sender.sendMessage(MessageWrapper.getString("townworld_disable", args[1]));
						} else {
							sender.sendMessage("/" + label + " disable <world>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "setFoundationPrice":
						if (args.length == 3) {
							TownworldController.getTownWorldByName(args[1]).setFoundationPrice(Double.valueOf(args[2]),
									true);
							sender.sendMessage(MessageWrapper.getString("townworld_setFoundationPrice", args[2],
									ConfigController.getCurrencyText(Double.valueOf(args[2]))));
						} else {
							sender.sendMessage("/" + label + " setFoundationPrice <world> <price>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "setExpandPrice":
						if (args.length == 3) {
							TownworldController.getTownWorldByName(args[1]).setExpandPrice(Double.valueOf(args[2]),
									true);
							sender.sendMessage(MessageWrapper.getString("townworld_setExpandPrice", args[2],
									ConfigController.getCurrencyText(Double.valueOf(args[2]))));
						} else {
							sender.sendMessage("/" + label + " setExpandPrice <world> <price / chunk");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					default:
						return false;
				}
				plugin.getConfig().set("TownWorlds", TownworldController.getTownWorldNameList());
				plugin.saveConfig();
			} else {
				return false;
			}
		} catch (TownSystemException e) {
			sender.sendMessage(e.getMessage());
		} catch (NumberFormatException e2) {
			sender.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[2]));
		}
		return true;
	}
}
