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

public class PlayerShopCommandExecutor implements CommandExecutor {
	
	private Ultimate_Economy plugin;

	public PlayerShopCommandExecutor(Ultimate_Economy plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				if (label.equalsIgnoreCase("playershop")) {
					if (args.length != 0) {
						if (args[0].equals("create")) {
							if (args.length == 3) {
								PlayerShop.createPlayerShop(plugin.getDataFolder(), args[1],
										player.getLocation(), Integer.valueOf(args[2]), player.getName());
								plugin.getConfig().set("PlayerShopIds", PlayerShop.getPlayershopIdList());
								plugin.saveConfig();
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_create1")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
										+ Ultimate_Economy.messages.getString("shop_create2"));
							} else {
								player.sendMessage("/playershop create <shopname> <size (9,18,27...)>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("delete")) {
							if (args.length == 2) {
								PlayerShop.deletePlayerShop(args[1] + "_" + player.getName());
								plugin.getConfig().set("PlayerShopIds", PlayerShop.getPlayershopIdList());
								plugin.saveConfig();
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_delete1")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
										+ Ultimate_Economy.messages.getString("shop_delete2"));
							} else {
								player.sendMessage("/playershop delete <shopname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (player.hasPermission("ultimate_economy.adminshop") && args[0].equals("deleteOther")) {
							if (args.length == 2) {
								PlayerShop.deletePlayerShop(args[1]);
								plugin.getConfig().set("PlayerShopIds", PlayerShop.getPlayerShopUniqueNameList());
								plugin.saveConfig();
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_delete1")
								+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
								+ Ultimate_Economy.messages.getString("shop_delete2"));
							} else {
								player.sendMessage("/playershop deleteOther <shopname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("rename")) {
							if (args.length == 3) {
								PlayerShop.getPlayerShopByUniqueName(args[1] + "_" + player.getName()).changeShopName(args[2]);
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_rename1")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
										+ Ultimate_Economy.messages.getString("shop_rename2") + " " + ChatColor.GREEN
										+ args[2] + ChatColor.GOLD + ".");
							} else {
								player.sendMessage("/playershop rename <oldName> <newName>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("resize")) {
							if (args.length == 3) {
								PlayerShop.getPlayerShopByUniqueName(args[1] + "_" + player.getName()).changeShopSize(Integer.valueOf(args[2]));
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_resize")
								+ " " + ChatColor.GREEN + args[2] + ChatColor.GOLD + ".");
							} else {
								player.sendMessage("/playershop resize <shopname> <new size>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("move")) {
							if (args.length == 2) {
								PlayerShop.getPlayerShopByUniqueName(args[1] + "_" + player.getName()).moveShop(player.getLocation());
							} else {
								player.sendMessage("/playershop move <shopname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("changeOwner")) {
							if (args.length == 3) {
									PlayerShop.getPlayerShopByUniqueName(args[1] + "_" + player.getName()).changeOwner(args[2]);
									player.sendMessage(
											ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_changeOwner3")
													+ " " + ChatColor.GREEN + args[2] + ChatColor.GOLD + ".");
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
								player.sendMessage("/playershop changeOwner <shopname> <new owner>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("changeProfession")) {
							if (args.length == 3) {
								try {
									PlayerShop.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
									.changeProfession(Profession.valueOf(args[2].toUpperCase()));
									player.sendMessage(
											ChatColor.GOLD + Ultimate_Economy.messages.getString("profession_changed"));
								} catch (IllegalArgumentException e) {
									player.sendMessage(
											ChatColor.RED + Ultimate_Economy.messages.getString("invalid_profession"));
								}
							} else {
								player.sendMessage("/playershop changeProfession <shopname> <profession>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("editShop")) {
							if (args.length == 2) {
								PlayerShop.getPlayerShopByUniqueName(args[1] + "_" + player.getName()).openEditor(player);
							} else {
								player.sendMessage("/playershop editShop <shopname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else {
							player.sendMessage(
									"/playershop <create/delete/move/rename/resize/editShop>");
						}
					} else {
						player.sendMessage(
								"/playershop <create/delete/move/rename/resize/editShop>");
					}
				}
			} catch(TownSystemException | PlayerException | ShopSystemException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
			} catch (NumberFormatException e2) {
				player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
			}
		}
		return false;
	}
}
