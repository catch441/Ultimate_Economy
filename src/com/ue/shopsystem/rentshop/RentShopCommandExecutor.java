package com.ue.shopsystem.rentshop;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;

import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;

import ultimate_economy.Ultimate_Economy;

public class RentShopCommandExecutor implements CommandExecutor {

	private Ultimate_Economy plugin;

	public RentShopCommandExecutor(Ultimate_Economy plugin) {
		this.plugin = plugin;
	}

	// admin commands
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// check if the sender is a player
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				if (args.length != 0) {
					if (player.hasPermission("ultimate_economy.rentshop.admin") && args[0].equals("create")) {
						if (args.length == 3) {
							RentShop shop = RentShop.createRentShop(plugin.getDataFolder(), player.getLocation(),
									Integer.valueOf(args[1]), Double.valueOf(args[2]));
							plugin.getConfig().set("RentShopIds", RentShop.getRentShopIdList());
							plugin.saveConfig();
							player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_create1")
									+ " " + ChatColor.GREEN + shop.getName() + ChatColor.GOLD + " "
									+ Ultimate_Economy.messages.getString("shop_create2"));
						} else {
							player.sendMessage("/" + label + " create <size> <rentalFee per 24h>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (player.hasPermission("ultimate_economy.rentshop.admin") && args[0].equals("delete")) {
						if (args.length == 2) {
							RentShop.deleteRentShop(args[1]);
							plugin.getConfig().set("RentShopIds", RentShop.getRentShopIdList());
							plugin.saveConfig();
							player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_delete1")
									+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
									+ Ultimate_Economy.messages.getString("shop_delete2"));
						} else {
							player.sendMessage("/" + label + " delete <shopname>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (player.hasPermission("ultimate_economy.rentshop.admin") && args[0].equals("move")) {
						if (args.length == 2) {
							RentShop.getRentShopByUniqueName(args[1]).moveShop(player.getLocation());
						} else {
							player.sendMessage("/" + label + " move <shopname>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (player.hasPermission("ultimate_economy.rentshop.admin") && args[0].equals("resize")) {
						if (args.length == 3) {
							RentShop.getRentShopByUniqueName(args[1]).changeShopSize(Integer.valueOf(args[2]));
							player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_resize")
										+ " " + ChatColor.GREEN + args[2] + ChatColor.GOLD + ".");
						} else {
							player.sendMessage("/" + label + " resize <shopname> <new size>");
						}
					}
					// Player commands
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (args[0].equals("changeProfession")) {
						if (args.length == 3) {
							try {
								RentShop.getRentShopByUniqueName(args[1] + "_" + player.getName())
										.changeProfession(Profession.valueOf(args[2].toUpperCase()));
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("profession_changed"));
							} catch (IllegalArgumentException e) {
								player.sendMessage(
										ChatColor.RED + Ultimate_Economy.messages.getString("invalid_profession"));
							}
						} else {
							player.sendMessage("/" + label + " changeProfession <shopname> <profession>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (args[0].equals("rename")) {
						if (args.length == 3) {
							RentShop.getRentShopByUniqueName(args[1] + "_" + player.getName()).changeShopName(args[2]);
							player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_rename1")
									+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
									+ Ultimate_Economy.messages.getString("shop_rename2") + " " + ChatColor.GREEN
									+ args[2] + ChatColor.GOLD + ".");
						} else {
							player.sendMessage("/" + label + " rename <oldName> <newName>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (args[0].equals("editShop")) {
						if (args.length == 2) {
							RentShop.getRentShopByUniqueName(args[1] + "_" + player.getName()).openEditor(player);
						} else {
							player.sendMessage("/" + label + " editShop <shopname>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else {
						if(player.hasPermission("ultimate_economy.rentshop.admin")) {
							player.sendMessage("/" + label + " [create/delete/move/resize/editShop]");
						} else {
							player.sendMessage("/" + label + " [editShop]");
						}
					}
				} else {
					if(player.hasPermission("ultimate_economy.rentshop.admin")) {
						player.sendMessage("/" + label + " [create/delete/move/resize/editShop]");
					} else {
						player.sendMessage("/" + label + " [editShop]");
					}
				}
			} catch (IllegalArgumentException e) {
				player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
			} catch (ShopSystemException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
			} catch (TownSystemException e) {
			}
		}
		return true;
	}
}
