package com.ue.shopsystem.rentshop.impl;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.shopsystem.rentshop.api.RentshopController;
import com.ue.ultimate_economy.Ultimate_Economy;

public class RentshopCommandExecutor implements CommandExecutor {

	private Ultimate_Economy plugin;

	public RentshopCommandExecutor(Ultimate_Economy plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// check if the sender is a player
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				if (args.length != 0) {
					if (player.hasPermission("ultimate_economy.rentshop.admin") && args[0].equals("create")) {
						if (args.length == 3) {
							RentshopImpl shop = RentshopController.createRentShop(plugin.getDataFolder(), player.getLocation(),
									Integer.valueOf(args[1]), Double.valueOf(args[2]));
							player.sendMessage(MessageWrapper.getString("shop_create", shop.getName()));
							plugin.getConfig().set("RentShopIds", RentshopController.getRentShopIdList());
							plugin.saveConfig();
						} else {
							player.sendMessage("/" + label + " create <size> <rentalFee per 24h>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (player.hasPermission("ultimate_economy.rentshop.admin") && args[0].equals("delete")) {
						if (args.length == 2) {
							RentshopController.deleteRentShop(RentshopController.getRentShopByUniqueName(args[1]));
							player.sendMessage(MessageWrapper.getString("shop_delete", args[1]));
							plugin.getConfig().set("RentShopIds", RentshopController.getRentShopIdList());
							plugin.saveConfig();
						} else {
							player.sendMessage("/" + label + " delete <shopname>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (player.hasPermission("ultimate_economy.rentshop.admin") && args[0].equals("move")) {
						if (args.length == 2) {
							RentshopController.getRentShopByUniqueName(args[1]).moveShop(player.getLocation());
						} else {
							player.sendMessage("/" + label + " move <shopname>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (player.hasPermission("ultimate_economy.rentshop.admin") && args[0].equals("resize")) {
						if (args.length == 3) {
							RentshopController.getRentShopByUniqueName(args[1]).changeShopSize(Integer.valueOf(args[2]));
							player.sendMessage(MessageWrapper.getString("shop_resize", args[2]));
						} else {
							player.sendMessage("/" + label + " resize <shopname> <new size>");
						}
					}
					// Player commands
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (args[0].equals("changeProfession")) {
						if (args.length == 3) {
							try {
								RentshopController.getRentShopByUniqueName(args[1] + "_" + player.getName())
										.changeProfession(Profession.valueOf(args[2].toUpperCase()));
								player.sendMessage(MessageWrapper.getString("profession_changed"));
							} catch (IllegalArgumentException e) {
								player.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[2]));
							}
						} else {
							player.sendMessage("/" + label + " changeProfession <shopname> <profession>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (args[0].equals("rename")) {
						if (args.length == 3) {
							RentshopController.getRentShopByUniqueName(args[1] + "_" + player.getName()).changeShopName(args[2]);
							player.sendMessage(MessageWrapper.getString("shop_rename", args[1],args[2]));
						} else {
							player.sendMessage("/" + label + " rename <oldName> <newName>");
						}
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////
					else if (args[0].equals("editShop")) {
						if (args.length == 2) {
							RentshopController.getRentShopByUniqueName(args[1] + "_" + player.getName()).openEditor(player);
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
			} catch (NumberFormatException e) {
				player.sendMessage(MessageWrapper.getErrorString("invalid_parameter", ""));
			} catch (ShopSystemException | PlayerException e) {
				player.sendMessage(e.getMessage());
			} catch (TownSystemException e) {
			}
		}
		return true;
	}
}
