package com.ue.shopsystem.playershop.impl;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.shopsystem.playershop.api.PlayershopController;
import com.ue.ultimate_economy.Ultimate_Economy;

public class PlayershopCommandExecutor implements CommandExecutor {

	private Ultimate_Economy plugin;

	public PlayershopCommandExecutor(Ultimate_Economy plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				if (args.length != 0) {
					switch (args[0]) {
						case "create":
							if (args.length == 3) {
								PlayershopController.createPlayerShop(plugin.getDataFolder(), args[1],
										player.getLocation(), Integer.valueOf(args[2]),
										EconomyPlayerController.getEconomyPlayerByName(player.getName()));
								plugin.getConfig().set("PlayerShopIds", PlayershopController.getPlayershopIdList());
								plugin.saveConfig();
								player.sendMessage(MessageWrapper.getString("shop_create", args[1]));
							} else {
								player.sendMessage(
										"/" + label + " create <shop> <size> <- size have to be a multible of 9");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "delete":
							if (args.length == 2) {
								PlayershopController.deletePlayerShop(PlayershopController
										.getPlayerShopByUniqueName(args[1] + "_" + player.getName()));
								player.sendMessage(MessageWrapper.getString("shop_delete", args[1]));
								plugin.getConfig().set("PlayerShopIds", PlayershopController.getPlayershopIdList());
								plugin.saveConfig();
							} else {
								player.sendMessage("/" + label + " delete <shop>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "deleteOther":
							if (player.hasPermission("ultimate_economy.adminshop")) {
								if (args.length == 2) {
									PlayershopController
											.deletePlayerShop(PlayershopController.getPlayerShopByUniqueName(args[1]));
									player.sendMessage(MessageWrapper.getString("shop_delete", args[1]));
									plugin.getConfig().set("PlayerShopIds",
											PlayershopController.getPlayerShopUniqueNameList());
									plugin.saveConfig();
								} else {
									player.sendMessage("/" + label + " deleteOther <shop>");
								}
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "rename":
							if (args.length == 3) {
								PlayershopController.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
										.changeShopName(args[2]);
								player.sendMessage(MessageWrapper.getString("shop_rename", args[1], args[2]));
							} else {
								player.sendMessage("/" + label + " rename <oldName> <newName>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "resize":
							if (args.length == 3) {
								PlayershopController.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
										.changeShopSize(Integer.valueOf(args[2]));
								player.sendMessage(MessageWrapper.getString("shop_resize", args[2]));
							} else {
								player.sendMessage("/" + label + " resize <shop> <new size>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "move":
							if (args.length == 2) {
								PlayershopController.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
										.moveShop(player.getLocation());
							} else {
								player.sendMessage("/" + label + " move <shop>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "changeOwner":
							if (args.length == 3) {
								EconomyPlayer newOwner = EconomyPlayerController.getEconomyPlayerByName(args[2]);
								PlayershopController.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
										.changeOwner(newOwner);
								player.sendMessage(MessageWrapper.getString("shop_changeOwner1", args[2]));
								if (newOwner.isOnline()) {
									newOwner.getPlayer().sendMessage(
											MessageWrapper.getString("shop_changeOwner", args[1], player.getName()));
								}
							} else {
								player.sendMessage("/" + label + " changeOwner <shop> <new owner>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "changeProfession":
							if (args.length == 3) {
								PlayershopController.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
										.changeProfession(Profession.valueOf(args[2].toUpperCase()));
								player.sendMessage(MessageWrapper.getString("profession_changed"));
							} else {
								player.sendMessage("/" + label + " changeProfession <shop> <profession>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "editShop":
							if (args.length == 2) {
								PlayershopController.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
										.openEditor(player);
							} else {
								player.sendMessage("/" + label + " editShop <shop>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						default:
							return false;
					}
				} else {
					return false;
				}
			} catch (TownSystemException | PlayerException | ShopSystemException e) {
				player.sendMessage(e.getMessage());
			} catch (IllegalArgumentException e2) {
				player.sendMessage(MessageWrapper.getErrorString("invalid_parameter",args[2]));
			}
		}
		return true;
	}
}
