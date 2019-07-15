package com.ue.shopsystem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.player.EconomyPlayer;

import ultimate_economy.Ultimate_Economy;

public class PlayerShopCommandExecuter implements CommandExecutor {
	
	private Ultimate_Economy plugin;

	public PlayerShopCommandExecuter(Ultimate_Economy plugin) {
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
								PlayerShop.createPlayerShop(plugin.getDataFolder(), args[1] + "_" + player.getName(),
										player.getLocation(), Integer.valueOf(args[2]));
								plugin.getConfig().set("PlayerShopNames", PlayerShop.getPlayerShopNameList());
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
								plugin.getConfig().set("PlayerShopNames", PlayerShop.getPlayerShopNameList());
								plugin.saveConfig();
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_delete1")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
										+ Ultimate_Economy.messages.getString("shop_delete2"));
							} else {
								player.sendMessage("/playershop delete <shopname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("move")) {
							if (args.length == 5) {
								PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName()).moveShop(
										Double.valueOf(args[2]), Double.valueOf(args[3]), Double.valueOf(args[4]));
							} else {
								player.sendMessage("/playershop move <shopname> <x> <y> <z>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("changeOwner")) {
							if (args.length == 3) {
								if (EconomyPlayer.getEconomyPlayerNameList().contains(args[1] + "_" + args[2])) {
									player.sendMessage(
											ChatColor.RED + Ultimate_Economy.messages.getString("shop_changeOwner1")
													+ " " + ChatColor.GREEN + args[2] + ChatColor.RED + " "
													+ Ultimate_Economy.messages.getString("shop_changeOwner2"));
								} else {
									PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName()).setOwner(args[2],
											plugin.getDataFolder());
									plugin.getConfig().set("PlayerShopNames", PlayerShop.getPlayerShopNameList());
									plugin.saveConfig();
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
								}
							} else {
								player.sendMessage("/playershop changeOwner <shopname> <new owner>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("editShop")) {
							if (args.length == 2) {
								PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName()).openEditor(player);
							} else {
								player.sendMessage("/player editShop <shopname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addItem")) {
							if (args.length == 7) {
								if (!args[2].toUpperCase().equals("HAND")
										&& Material.matchMaterial(args[2].toUpperCase()) == null) {
									throw new ShopSystemException(ShopSystemException.INVALID_MATERIAL);
								} else {
									ItemStack itemStack = new ItemStack(Material.getMaterial(args[2].toUpperCase()),
											Integer.valueOf(args[4]));
									PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName()).addItem(
											Integer.valueOf(args[3]) - 1, Double.valueOf(args[5]),
											Double.valueOf(args[6]), itemStack);
									player.sendMessage(
											ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_addItem1") + " "
													+ ChatColor.GREEN + itemStack.getType().toString().toLowerCase()
													+ ChatColor.GOLD + " "
													+ Ultimate_Economy.messages.getString("shop_addItem2"));
								}
							} else {
								player.sendMessage(
										"/playershop addItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice>");
								player.sendMessage(Ultimate_Economy.messages.getString("shop_addItem_errorinfo"));
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addPotion")) {
							if (args.length == 9) {
								PlayerShop shop = PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName());
								AdminShopCommandExecutor.handleAddPotion(player, shop, args);
							} else {
								player.sendMessage(
										"/playershop addPotion <shopname> <potionType> <potionEffect> <extended/upgraded/none> <slot> <amount> <sellprice> <buyprice>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addEnchantedItem")) {
							if (args.length >= 9) {
								PlayerShop shop = PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName());
								AdminShopCommandExecutor.handleAddEnchantedItem(player, args, shop);
							} else {
								player.sendMessage(
										"/playershop addEnchantedItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice> [<enchantment> <lvl>]");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("removeItem")) {
							if (args.length == 3) {
								PlayerShop shop = PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName());
								String itemName = shop.getItem(Integer.valueOf(args[2])).getType().toString()
										.toLowerCase();
								shop.removeItem(Integer.valueOf(args[2]) - 1);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_removeItem1") + " "
												+ ChatColor.GREEN + itemName + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_removeItem2"));
							} else {
								player.sendMessage("/playershop removeItem <shopname> <slot (> 0)>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("editItem")) {
							if (args.length == 6) {
								PlayerShop shop = PlayerShop.getPlayerShopByName(args[1] + "_" + player.getName());
								player.sendMessage(shop.editItem(Integer.valueOf(args[2]), args[3], args[4], args[5]));
							} else {
								player.sendMessage(
										"/playershop editItem <shopname> <slot> <amount> <sellPrice> <buyPrice>");
								player.sendMessage(Ultimate_Economy.messages.getString("shop_editItem_errorinfo"));
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else {
							player.sendMessage(
									"/playershop <create/delete/move/editShop/addItem/addEnchantedItem/addPotion/removeItem/editItem>");
						}
					} else {
						player.sendMessage(
								"/playershop <create/delete/move/editShop/addItem/addEnchantedItem/addPotion/removeItem/editItem>");
					}
				}
			} catch(PlayerException | ShopSystemException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
			} catch (NumberFormatException e2) {
				player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
			}
		}
		return false;
	}
}
