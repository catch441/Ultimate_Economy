package com.ue.townsystem;

import java.io.File;

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
				if (label.equalsIgnoreCase("town")) {
					if (args.length != 0) {
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						if (args[0].equals("create")) {
							if (args.length == 2) {
								TownWorld tWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								tWorld.createTown(args[1], player.getLocation(), ecoPlayer);
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("town_create")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + "!");
							} else {
								player.sendMessage("/town create <townname>");
							}
						}	
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("delete")) {
							if (args.length == 2) {
								TownWorld tWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								tWorld.dissolveTown(args[1], player.getName());
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("town_delete1")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
										+ Ultimate_Economy.messages.getString("town_delete2"));
							} else {
								player.sendMessage("/town delete <townname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("expand")) {
							if (args.length == 2) {
								TownWorld tWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								tWorld.expandTown(args[1], player.getLocation().getChunk(), player.getName());
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("town_expand"));
							} else {
								player.sendMessage("/town expand <townname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("setTownSpawn")) {
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
								player.sendMessage("/town setTownSpawn <townname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("setTax")) {
							// TODO
							if (args.length == 3) {
							} else {
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addCoOwner")) {
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
								player.sendMessage("/town addCoOwner <town> <playername>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("removeCoOwner")) {
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
								player.sendMessage("/town removeCoOwner <town> <playername>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equalsIgnoreCase("moveTownManager")) {
							if (args.length == 1) {
								TownWorld townWorld = TownWorld.getTownWorldByName(player.getWorld().getName());
								Town town = townWorld.getTownByChunk(player.getLocation().getChunk());
								File file = town.moveTownManagerVillager(townWorld.getSaveFile(), player.getLocation(),
										player.getName());
								townWorld.setSaveFile(file);
							} else {
								player.sendMessage("/town moveTownManager");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("plot")) {
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
										player.sendMessage("/town plot setForSale <price> ");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("setForRent")) {
									if (args.length == 4) {
										// TODO
									} else {
										player.sendMessage("/town plot setForRent <townname> <price/24h>");
									}
								}
							} else {
								player.sendMessage("/town plot <setForSale/setForRent>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("tp")) {
							if (args.length == 2) {
								for (TownWorld townWorld : TownWorld.getTownWorldList()) {
									if (townWorld.getTownNameList().contains(args[1])) {
										player.teleport(townWorld.getTownByName(args[1]).getTownSpawn());
										break;
									}
								}
							} else {
								player.sendMessage("/town tp <townname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("pay")) {
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
								player.sendMessage("/town pay <townname> <amount>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if(args[0].equals("withdraw")) {
							if (args.length == 3) {
								for (TownWorld townWorld : TownWorld.getTownWorldList()) {
									if (townWorld.getTownNameList().contains(args[1])) {
										Town town = townWorld.getTownByName(args[1]);
										if(town.hasCoOwnerPermission(player.getName())) {
											double amount = Double.valueOf(args[2]);
											townWorld.setSaveFile(town.decreaseTownBankAmount(townWorld.getSaveFile(), amount));
											ecoPlayer.increasePlayerAmount(amount);
											player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("got_money") + " "
													+ ChatColor.GREEN + amount + " $");
										} else {
											player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("player_has_no_permission"));
										}
										break;
									}
								}
							} else {
								player.sendMessage("/town pay <townname> <amount>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("bank")) {
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
								player.sendMessage("/town bank <townname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else {
							player.sendMessage(
									"/town <create/delete/expand/setTownSpawn/setTax/moveTownManager/plot/pay/tp/bank>");
						}
					} else {
						player.sendMessage(
								"/town <create/delete/expand/setTownSpawn/setTax/moveTownManager/plot/pay/tp/bank>");
					}
				}
			} catch (PlayerException | TownSystemException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
			}
		}
		return false;
	}

}
