package com.ue.townsystem.townworld.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.ue.exceptions.TownSystemException;
import com.ue.townsystem.townworld.api.TownworldController;

import ultimate_economy.Ultimate_Economy;

public class TownworldCommandExecutor implements CommandExecutor {
	
	private Ultimate_Economy plugin;
	
	public TownworldCommandExecutor(Ultimate_Economy plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (args.length != 1) {
				switch(args[0]) {
					case "enable": 
						if (args.length == 2) {
							TownworldController.createTownWorld(plugin.getDataFolder(), args[1]);
							sender.sendMessage(ChatColor.GREEN + args[1] + " " + ChatColor.GOLD
									+ Ultimate_Economy.messages.getString("townworld_enable") + ".");
						} else {
							sender.sendMessage("/" + label + " enable <world>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "disable": 
						if (args.length == 2) {
							TownworldController.deleteTownWorld(args[1]);
							sender.sendMessage(ChatColor.GREEN + args[1] + " " + ChatColor.GOLD
									+ Ultimate_Economy.messages.getString("townworld_disable") + ".");
						} else {
							sender.sendMessage("/" + label + " disable <world>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "setFoundationPrice": 
						if (args.length == 3) {
							TownworldController.getTownWorldByName(args[1]).setFoundationPrice(Double.valueOf(args[2]), true);
							sender.sendMessage(ChatColor.GOLD
									+ Ultimate_Economy.messages.getString("townworld_setFoundationPrice") + " "
									+ ChatColor.GREEN + args[2]);
						} else {
							sender.sendMessage("/" + label + " setFoundationPrice <world> <price>");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					case "setExpandPrice": 
						if (args.length == 3) {
							TownworldController.getTownWorldByName(args[1]).setExpandPrice(Double.valueOf(args[2]), true);
							sender.sendMessage(
									ChatColor.GOLD + Ultimate_Economy.messages.getString("townworld_setExpandPrice")
											+ " " + ChatColor.GREEN + args[2]);
						} else {
							sender.sendMessage("/" + label + " setExpandPrice <world> <price / chunk");
						}
						break;
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					default: return false;
				}	
				plugin.getConfig().set("TownWorlds", TownworldController.getTownWorldNameList());
				plugin.saveConfig();
			} else {
				return false;
			}
		} catch(TownSystemException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
		} catch (NumberFormatException e2) {
			sender.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
		}
		return true;
	}
}
