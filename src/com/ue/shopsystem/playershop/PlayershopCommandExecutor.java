package com.ue.shopsystem.playershop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;

import ultimate_economy.Ultimate_Economy;

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
								Playershop.createPlayerShop(plugin.getDataFolder(), args[1], player.getLocation(),
										Integer.valueOf(args[2]), player.getName());
								plugin.getConfig().set("PlayerShopIds", Playershop.getPlayershopIdList());
								plugin.saveConfig();
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_create1") + " "
												+ ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_create2"));
							} else {
								player.sendMessage("/" + label + " create <shop> <size> <- size have to be a multible of 9");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "delete":
							if (args.length == 2) {
								Playershop.deletePlayerShop(args[1] + "_" + player.getName());
								plugin.getConfig().set("PlayerShopIds", Playershop.getPlayershopIdList());
								plugin.saveConfig();
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_delete1") + " "
												+ ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_delete2"));
							} else {
								player.sendMessage("/" + label + " delete <shop>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "deleteOther":
							if (player.hasPermission("ultimate_economy.adminshop")) {
								if (args.length == 2) {
									Playershop.deletePlayerShop(args[1]);
									plugin.getConfig().set("PlayerShopIds",
											Playershop.getPlayerShopUniqueNameList());
									plugin.saveConfig();
									player.sendMessage(
											ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_delete1")
													+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
													+ Ultimate_Economy.messages.getString("shop_delete2"));
								} else {
									player.sendMessage("/" + label + " deleteOther <shop>");
								}
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "rename":
							if (args.length == 3) {
								Playershop.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
										.changeShopName(args[2]);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_rename1") + " "
												+ ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_rename2") + " "
												+ ChatColor.GREEN + args[2] + ChatColor.GOLD + ".");
							} else {
								player.sendMessage("/" + label + " rename <oldName> <newName>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "resize":
							if (args.length == 3) {
								Playershop.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
										.changeShopSize(Integer.valueOf(args[2]));
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_resize") + " "
												+ ChatColor.GREEN + args[2] + ChatColor.GOLD + ".");
							} else {
								player.sendMessage("/" + label + " resize <shop> <new size>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "move": 
							if (args.length == 2) {
								Playershop.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
										.moveShop(player.getLocation());
							} else {
								player.sendMessage("/" + label + " move <shop>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "changeOwner": 
							if (args.length == 3) {
								Playershop.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
										.changeOwner(args[2]);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_changeOwner3") + " "
												+ ChatColor.GREEN + args[2] + ChatColor.GOLD + ".");
								for (Player pl : Bukkit.getOnlinePlayers()) {
									if (pl.getName().equals(args[2])) {
										pl.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("shop_changeOwner4") + " "
												+ ChatColor.GREEN + args[1] + ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("shop_changeOwner5") + " "
												+ ChatColor.GREEN + player.getName());
										break;
									}
								}
							} else {
								player.sendMessage("/" + label + " changeOwner <shop> <new owner>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "changeProfession": 
							if (args.length == 3) {
								try {
									Playershop.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
											.changeProfession(Profession.valueOf(args[2].toUpperCase()));
									player.sendMessage(
											ChatColor.GOLD + Ultimate_Economy.messages.getString("profession_changed"));
								} catch (IllegalArgumentException e) {
									player.sendMessage(
											ChatColor.RED + Ultimate_Economy.messages.getString("invalid_profession"));
								}
							} else {
								player.sendMessage("/" + label + " changeProfession <shop> <profession>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "editShop": 
							if (args.length == 2) {
								Playershop.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
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
				player.sendMessage(ChatColor.RED + e.getMessage());
			} catch (NumberFormatException e2) {
				player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
			}
		}
		return true;
	}
}
