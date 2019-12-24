package com.ue.townsystem;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.exceptions.TownSystemException;

import ultimate_economy.Ultimate_Economy;

public class TownworldCommandExecutor implements CommandExecutor {
	
	private Ultimate_Economy plugin;
	
	public TownworldCommandExecutor(Ultimate_Economy plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				if (label.equalsIgnoreCase("townWorld")) {
					if (args.length > 1) {
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						if (args[0].equals("enable")) {
							if (args.length == 2) {
								TownWorld.createTownWorld(plugin.getDataFolder(), args[1]);
								player.sendMessage(ChatColor.GREEN + args[1] + " " + ChatColor.GOLD
										+ Ultimate_Economy.messages.getString("townworld_enable") + ".");
							} else {
								player.sendMessage("/townWorld enable <worldname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("disable")) {
							if (args.length == 2) {
								TownWorld.deleteTownWorld(args[1]);
								player.sendMessage(ChatColor.GREEN + args[1] + " " + ChatColor.GOLD
										+ Ultimate_Economy.messages.getString("townworld_disable") + ".");
							} else {
								player.sendMessage("/townWorld disable <worldname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("setFoundationPrice")) {
							if (args.length == 3) {
								TownWorld.getTownWorldByName(args[1]).setFoundationPrice(Double.valueOf(args[2]), true);
								player.sendMessage(ChatColor.GOLD
										+ Ultimate_Economy.messages.getString("townworld_setFoundationPrice") + " "
										+ ChatColor.GREEN + args[2]);
							} else {
								player.sendMessage("/townWorld setFoundationPrice <worldname> <price>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("setExpandPrice")) {
							if (args.length == 3) {
								TownWorld.getTownWorldByName(args[1]).setExpandPrice(Double.valueOf(args[2]), true);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("townworld_setExpandPrice")
												+ " " + ChatColor.GREEN + args[2]);
							} else {
								player.sendMessage("/townWorld setExpandPrice <worldname> <price per chunk");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						plugin.getConfig().set("TownWorlds", TownWorld.getTownWorldNameList());
						plugin.saveConfig();
					} else if (args.length == 1 && args[0].equals("enable")) {
						player.sendMessage("/townWorld enable <worldname>");
					} else if (args.length == 1 && args[0].equals("disable")) {
						player.sendMessage("/townWorld disable <worldname>");
					} else if (args.length == 1 && args[0].equals("setFoundationPrice")) {
						player.sendMessage("/townWorld setFoundationPrice <price>");
					} else if (args.length == 1 && args[0].equals("setExpandPrice")) {
						player.sendMessage("/townWorld setExpandPrice <price per chunk>");
					} else {
						player.sendMessage("/townWorld <enable/disable/setFoundationPrice/setExpandPrice>");
					}
				}
			} catch(TownSystemException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
			} catch (NumberFormatException e2) {
				player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
			}
		}
		return false;
	}

}
