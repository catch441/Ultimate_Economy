package com.ue.townsystem.town.impl;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.townsystem.town.api.Plot;
import com.ue.townsystem.town.api.Town;
import com.ue.townsystem.town.api.TownController;
import com.ue.townsystem.townworld.api.Townworld;
import com.ue.townsystem.townworld.api.TownworldController;

public class TownCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName(player.getName());
				if (args.length != 0) {
					switch (args[0]) {
						case "create":
							if (args.length == 2) {
								Townworld townworld = TownworldController
										.getTownWorldByName(player.getWorld().getName());
								TownController.createTown(townworld, args[1], player.getLocation(), ecoPlayer);
								for (Player p : Bukkit.getOnlinePlayers()) {
									TownworldController.handleTownWorldLocationCheck(p.getWorld().getName(),
											p.getLocation().getChunk(), p.getName());
								}
								player.sendMessage(MessageWrapper.getString("town_create", args[1]));
							} else {
								player.sendMessage("/" + label + " create <town>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "delete":
							if (args.length == 2) {
								Townworld townworld = TownworldController
										.getTownWorldByName(player.getWorld().getName());
								Town town = townworld.getTownByName(args[1]);
								TownController.dissolveTown(town, ecoPlayer);
								for (Player p : Bukkit.getOnlinePlayers()) {
									TownworldController.handleTownWorldLocationCheck(p.getWorld().getName(),
											p.getLocation().getChunk(), p.getName());
								}
								player.sendMessage(MessageWrapper.getString("town_delete", args[1]));
							} else {
								player.sendMessage("/" + label + " delete <town>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "expand":
							if (args.length == 2) {
								Townworld townworld = TownworldController
										.getTownWorldByName(player.getWorld().getName());
								townworld.getTownByName(args[1]).expandTown(player.getLocation().getChunk(), ecoPlayer,
										true);
								;
							} else {
								player.sendMessage("/" + label + " expand <town>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "rename":
							if (args.length == 3) {
								Townworld townworld = TownworldController
										.getTownWorldByName(player.getWorld().getName());
								townworld.getTownByName(args[1]).renameTown(args[2], ecoPlayer, true);
								;
							} else {
								player.sendMessage("/" + label + " rename <old name> <new name>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "setTownSpawn":
							if (args.length == 2) {
								Townworld tWorld = TownworldController.getTownWorldByName(player.getWorld().getName());
								Town town = tWorld.getTownByName(args[1]);
								town.setTownSpawn(player.getLocation(),ecoPlayer,true);
							} else {
								player.sendMessage("/" + label + " setTownSpawn <town>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "addCoOwner":
							if (args.length == 3) {
								for (Townworld townworld : TownworldController.getTownWorldList()) {
									if (townworld.getTownNameList().contains(args[1])) {
										Town town = townworld.getTownByName(args[1]);
										if (town.isTownOwner(ecoPlayer)) {
											town.addCoOwner(EconomyPlayerController.getEconomyPlayerByName(args[2]));
											player.sendMessage(MessageWrapper.getString("town_addCoOwner", args[2]));
										} else {
											player.sendMessage(MessageWrapper.getErrorString("town_not_town_owner"));
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
								for (Townworld townworld : TownworldController.getTownWorldList()) {
									if (townworld.getTownNameList().contains(args[1])) {
										Town town = townworld.getTownByName(args[1]);
										if (town.isTownOwner(ecoPlayer)) {
											town.removeCoOwner(EconomyPlayerController.getEconomyPlayerByName(args[2]));
											player.sendMessage(MessageWrapper.getString("town_removeCoOwner", args[2]));
										} else {
											player.sendMessage(MessageWrapper.getErrorString("town_not_town_owner"));
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
								Townworld townworld = TownworldController
										.getTownWorldByName(player.getWorld().getName());
								Town town = townworld.getTownByChunk(player.getLocation().getChunk());
								town.moveTownManagerVillager(player.getLocation(), ecoPlayer);
							} else {
								player.sendMessage("/" + label + " moveTownManager");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "tp":
							if (args.length == 2) {
								for (Townworld townworld : TownworldController.getTownWorldList()) {
									if (townworld.getTownNameList().contains(args[1])) {
										player.teleport(townworld.getTownByName(args[1]).getTownSpawn());
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
								for (Townworld townworld : TownworldController.getTownWorldList()) {
									if (townworld.getTownNameList().contains(args[1])) {
										double amount = Double.valueOf(args[2]);
										ecoPlayer.decreasePlayerAmount(amount, true);
										townworld.getTownByName(args[1]).increaseTownBankAmount(amount);
										player.sendMessage(MessageWrapper.getString("town_pay", args[1],amount));
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
								for (Townworld townworld : TownworldController.getTownWorldList()) {
									if (townworld.getTownNameList().contains(args[1])) {
										Town town = townworld.getTownByName(args[1]);
										if (town.hasCoOwnerPermission(ecoPlayer)) {
											double amount = Double.valueOf(args[2]);
											town.decreaseTownBankAmount(amount);
											ecoPlayer.increasePlayerAmount(amount, true);
										} else {
											throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
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
								for (Townworld townworld : TownworldController.getTownWorldList()) {
									if (townworld.getTownNameList().contains(args[1])) {
										Town town = townworld.getTownByName(args[1]);
										if (town.hasCoOwnerPermission(ecoPlayer)) {
											player.sendMessage(MessageWrapper.getString("town_bank", town.getTownBankAmount()));
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
										Townworld townworld = TownworldController
												.getTownWorldByName(player.getWorld().getName());
										Town town = townworld.getTownByChunk(player.getLocation().getChunk());
										Plot plot = town.getPlotByChunk(player.getLocation().getChunk().getX() + "/"
												+ player.getLocation().getChunk().getZ());
										plot.setForSale(Double.valueOf(args[2]), player.getLocation(), ecoPlayer, true);
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
						default:
							return false;
					}
				} else {
					return false;
				}
			} catch (PlayerException | TownSystemException e) {
				player.sendMessage(e.getMessage());
			} catch (NumberFormatException e1) {
				
			}
		}
		return true;
	}
}
