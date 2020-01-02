package com.ue.townsystem;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.TownSystemException;
import com.ue.player.EconomyPlayer;

import ultimate_economy.Ultimate_Economy;

public class TownCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				EconomyPlayer ecoPlayer = EconomyPlayer.getEconomyPlayerByName(player.getName());
				if (args.length != 0) {
					switch(args[0]) {
						case "create": 
							if (args.length == 2) {
								TownWorld tWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								tWorld.createTown(args[1], player.getLocation(), ecoPlayer);
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("town_create")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + "!");
							} else {
								player.sendMessage("/" + label + " create <town>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "delete": 
							if (args.length == 2) {
								TownWorld tWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								tWorld.dissolveTown(args[1], player.getName());
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("town_delete1")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
										+ Ultimate_Economy.messages.getString("town_delete2"));
							} else {
								player.sendMessage("/" + label + " delete <town>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "expand": 
							if (args.length == 2) {
								TownWorld tWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								tWorld.expandTown(args[1], player.getLocation().getChunk(), player.getName());
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("town_expand"));
							} else {
								player.sendMessage("/" + label + " expand <town>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "rename": 
							if (args.length == 3) {
								TownWorld tWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								tWorld.renameTown(ecoPlayer.getName(), args[1], args[2]);
								for(Player p:Bukkit.getOnlinePlayers()) {
									TownWorld.handleTownWorldLocationCheck(p.getWorld().getName(),
											p.getLocation().getChunk(), p.getName());
								}
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("town_rename1")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
										+ Ultimate_Economy.messages.getString("town_rename2") + " " + ChatColor.GREEN
										+ args[2] + ChatColor.GOLD + ".");
							} else {
								player.sendMessage("/" + label + " rename <old name> <new name>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "setTownSpawn": 
							if (args.length == 2) {
								TownWorld tWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								Town town = tWorld.getTownByName(args[1]);
								if (town.hasCoOwnerPermission(player.getName())) {
									File file = town.setTownSpawn(tWorld.getSaveFile(), player.getLocation());
									tWorld.setSaveFile(file);
									player.sendMessage(ChatColor.GOLD + "The townspawn was set to " + ChatColor.GREEN
											+ (int) player.getLocation().getX() + "/"
											+ (int) player.getLocation().getY() + "/"
											+ (int) player.getLocation().getZ() + ChatColor.GOLD + ".");
								}
							} else {
								player.sendMessage("/" + label + " setTownSpawn <town>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "addCoOwner": 
							if (args.length == 3) {
								for (TownWorld townWorld : TownWorld.getTownWorldList()) {
									if (townWorld.getTownNameList().contains(args[1])) {
										Town town = townWorld.getTownByName(args[1]);
										if (town.isTownOwner(player.getName())) {
											townWorld.setSaveFile(town.addCoOwner(townWorld.getSaveFile(), args[2]));
											player.sendMessage(ChatColor.GOLD
													+ Ultimate_Economy.messages.getString("town_addCoOwner1") + " "
													+ ChatColor.GREEN + args[2] + ChatColor.GOLD + " "
													+ Ultimate_Economy.messages.getString("town_addCoOwner2"));
										} else {
											player.sendMessage(ChatColor.RED
													+ Ultimate_Economy.messages.getString("town_addCoOwner3"));
										}
										break;
									}
								}
							} else {
								player.sendMessage("/" + label + " addCoOwner <town> <player>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "removeCoOwner": 
							if (args.length == 3) {
								for (TownWorld townWorld : TownWorld.getTownWorldList()) {
									if (townWorld.getTownNameList().contains(args[1])) {
										Town town = townWorld.getTownByName(args[1]);
										if (town.isTownOwner(player.getName())) {
											townWorld.setSaveFile(town.removeCoOwner(townWorld.getSaveFile(), args[2]));
											player.sendMessage(ChatColor.GOLD
													+ Ultimate_Economy.messages.getString("town_removeCoOwner1") + " "
													+ ChatColor.GREEN + args[2] + ChatColor.GOLD + " "
													+ Ultimate_Economy.messages.getString("town_removeCoOwner2"));
										} else {
											player.sendMessage(ChatColor.RED
													+ Ultimate_Economy.messages.getString("town_removeCoOwner3"));
										}
										break;
									}
								}
							} else {
								player.sendMessage("/" + label + " removeCoOwner <town> <player>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "moveTownManager": 
							if (args.length == 1) {
								TownWorld townWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								Town town = townWorld.getTownByChunk(player.getLocation().getChunk());
								File file = town.moveTownManagerVillager(townWorld.getSaveFile(), player.getLocation(),
										player.getName());
								townWorld.setSaveFile(file);
							} else {
								player.sendMessage("/" + label + " moveTownManager");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "tp": 
							if (args.length == 2) {
								for (TownWorld townWorld : TownWorld.getTownWorldList()) {
									if (townWorld.getTownNameList().contains(args[1])) {
										player.teleport(townWorld.getTownByName(args[1]).getTownSpawn());
										break;
									}
								}
							} else {
								player.sendMessage("/" + label + " tp <town>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "pay":
							if (args.length == 3) {
								for (TownWorld townWorld : TownWorld.getTownWorldList()) {
									if (townWorld.getTownNameList().contains(args[1])) {
										double amount = Double.valueOf(args[2]);
										ecoPlayer.decreasePlayerAmount(amount, true);
										townWorld.setSaveFile(townWorld.getTownByName(args[1])
												.increaseTownBankAmount(townWorld.getSaveFile(), amount));
										player.sendMessage(
												ChatColor.GOLD + Ultimate_Economy.messages.getString("town_pay1") + " "
														+ ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
														+ Ultimate_Economy.messages.getString("town_pay2") + " "
														+ ChatColor.GREEN + amount + " $" + ChatColor.GOLD + " "
														+ Ultimate_Economy.messages.getString("town_pay3"));
										break;
									}
								}
							} else {
								player.sendMessage("/" + label + " pay <town> <amount>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "withdraw": 
							if (args.length == 3) {
								for (TownWorld townWorld : TownWorld.getTownWorldList()) {
									if (townWorld.getTownNameList().contains(args[1])) {
										Town town = townWorld.getTownByName(args[1]);
										if (town.hasCoOwnerPermission(player.getName())) {
											double amount = Double.valueOf(args[2]);
											townWorld.setSaveFile(
													town.decreaseTownBankAmount(townWorld.getSaveFile(), amount));
											ecoPlayer.increasePlayerAmount(amount);
											player.sendMessage(
													ChatColor.GOLD + Ultimate_Economy.messages.getString("got_money")
															+ " " + ChatColor.GREEN + amount + " $");
										} else {
											player.sendMessage(ChatColor.RED
													+ Ultimate_Economy.messages.getString("player_has_no_permission"));
										}
										break;
									}
								}
							} else {
								player.sendMessage("/" + label + " pay <town> <amount>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "bank": 
							if (args.length == 2) {
								for (TownWorld townWorld : TownWorld.getTownWorldList()) {
									if (townWorld.getTownNameList().contains(args[1])) {
										Town town = townWorld.getTownByName(args[1]);
										if (town.hasCoOwnerPermission(player.getName())) {
											player.sendMessage(
													ChatColor.GOLD + Ultimate_Economy.messages.getString("town_bank")
															+ " " + ChatColor.GREEN + town.getTownBankAmount()
															+ ChatColor.GOLD + " $");
										}
										break;
									}
								}
							} else {
								player.sendMessage("/" + label + " bank <town>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "plot": 
							if (args.length > 1) {
								if (args[1].equals("setForSale")) {
									if (args.length == 3) {
										TownWorld townWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
										Town town = townWorld.getTownByChunk(player.getLocation().getChunk());
										File file = town.setPlotForSale(townWorld.getSaveFile(),
												Double.valueOf(args[2]), player.getName(), player.getLocation());
										townWorld.setSaveFile(file);
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("town_plot_setForSale"));
									} else {
										player.sendMessage("/" + label + " plot setForSale <price>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("setForRent")) {
									if (args.length == 4) {
										// TODO
									} else {
										player.sendMessage("/" + label + " plot setForRent <town> <price/24h>");
									}
								}
							} else {
								player.sendMessage("/" + label + " plot [setForSale/setForRent]");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						default: return false;
					}
				} else {
					return false;
				}
			} catch (PlayerException | TownSystemException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
			}
		}
		return true;
	}
}
