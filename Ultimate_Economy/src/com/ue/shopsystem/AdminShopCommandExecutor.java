package com.ue.shopsystem;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.ue.exceptions.ShopSystemException;

import ultimate_economy.Ultimate_Economy;

public class AdminShopCommandExecutor implements CommandExecutor {
	
	private Ultimate_Economy plugin;
	
	public AdminShopCommandExecutor(Ultimate_Economy plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				if (label.equalsIgnoreCase("adminshop")) {
					if (args.length != 0) {
						if (args[0].equals("create")) {
							if (args.length == 3) {
								Bukkit.getLogger().info("test");
								AdminShop.createAdminShop(plugin.getDataFolder(), args[1], player.getLocation(),
										Integer.valueOf(args[2]));
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_create1")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
										+ Ultimate_Economy.messages.getString("shop_create2"));
								plugin.getConfig().set("ShopNames", AdminShop.getAdminShopNameList());
								plugin.saveConfig();
							} else {
								player.sendMessage("/adminshop create <shopname> <size (9,18,27...)>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("delete")) {
							if (args.length == 2) {
								AdminShop.deleteAdminShop(args[1]);
								player.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_delete1")
										+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
										+ Ultimate_Economy.messages.getString("shop_delete2"));
								plugin.getConfig().set("ShopNames", AdminShop.getAdminShopNameList());
								plugin.saveConfig();
							} else {
								player.sendMessage("/adminshop delete <shopname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("move")) {
							if (args.length == 5) {
								AdminShop.getAdminShopByName(args[1]).moveShop(Double.valueOf(args[2]),
										Double.valueOf(args[3]), Double.valueOf(args[4]));
							} else {
								player.sendMessage("/adminshop move <shopname> <x> <y> <z>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("editShop")) {
							if (args.length == 2) {
								AdminShop.getAdminShopByName(args[1]).openEditor(player);
							} else {
								player.sendMessage("/adminshop editShop <shopname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addItem")) {
							if (args.length == 7) {
								if (Material.matchMaterial(args[2].toUpperCase()) == null) {
									throw new ShopSystemException(ShopSystemException.INVALID_MATERIAL);
								} else {
									ItemStack itemStack = new ItemStack(Material.getMaterial(args[2].toUpperCase()),
											Integer.valueOf(args[4]));
									AdminShop.getAdminShopByName(args[1]).addItem(Integer.valueOf(args[3]) - 1,
											Double.valueOf(args[5]), Double.valueOf(args[6]), itemStack);
									player.sendMessage(
											ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_addItem1") + " "
													+ ChatColor.GREEN + itemStack.getType().toString().toLowerCase()
													+ ChatColor.GOLD + " "
													+ Ultimate_Economy.messages.getString("shop_addItem2"));
								}
							} else {
								player.sendMessage(
										"/adminshop addItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice>");
								player.sendMessage(Ultimate_Economy.messages.getString("shop_addItem_errorinfo"));
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("removeItem")) {
							if (args.length == 3) {
								AdminShop shop = AdminShop.getAdminShopByName(args[1]);
								String itemName = shop.getItem(Integer.valueOf(args[2])).getType().toString()
										.toLowerCase();
								shop.removeItem(Integer.valueOf(args[2]) - 1);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_removeItem1") + " "
												+ ChatColor.GREEN + itemName + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_removeItem2"));
							} else {
								player.sendMessage("/adminshop removeItem <shopname> <slot (> 0)>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addPotion")) {
							if (args.length == 9) {
								handleAddPotion(player, AdminShop.getAdminShopByName(args[1]), args);
							} else {
								player.sendMessage(
										"/adminshop addPotion <shopname> <potionType> <potionEffect> <extended/upgraded/none> <slot> <amount> <sellprice> <buyprice>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addEnchantedItem")) {
							if (args.length >= 9) {
								handleAddEnchantedItem(player, args, AdminShop.getAdminShopByName(args[1]));
							} else {
								player.sendMessage(
										"/adminshop addEnchantedItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice> [<enchantment> <lvl>]");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addSpawner")) {
							if (args.length == 5) {
								ItemStack itemStack = new ItemStack(Material.SPAWNER, 1);
								ItemMeta meta = itemStack.getItemMeta();
								meta.setDisplayName(args[2].toUpperCase());
								itemStack.setItemMeta(meta);
								AdminShop.getAdminShopByName(args[1]).addItem(Integer.valueOf(args[3]) - 1, 0.0,
										Double.valueOf(args[4]), itemStack);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_addSpawner1") + " "
												+ ChatColor.GREEN + args[2]
												+ ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_addSpawner2"));
							} else {
								player.sendMessage("/adminshop addSpawner <shopname> <entity type> <slot> <buyPrice>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("removeSpawner")) {
							if (args.length == 3) {
								AdminShop shop = AdminShop.getAdminShopByName(args[1]);
								String itemName = shop.getItem(Integer.valueOf(args[2])).getType().toString()
										.toLowerCase();
								shop.removeItem(Integer.valueOf(args[2]) - 1);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_removeSpawner1")
												+ " " + ChatColor.GREEN + itemName + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_removeSpawner1"));
							} else {
								player.sendMessage("/adminshop removeSpawner <shopname> <slot>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("editItem")) {
							if (args.length == 6) {
								player.sendMessage(AdminShop.getAdminShopByName(args[1])
										.editItem(Integer.valueOf(args[2]), args[3], args[4], args[5]));
							} else {
								player.sendMessage(
										"/adminshop editItem <shopname> <slot> <amount> <sellPrice> <buyPrice>");
								player.sendMessage(Ultimate_Economy.messages.getString("shop_editItem_errorinfo"));
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else {
							player.sendMessage(
									"/adminshop <create/delete/move/editShop/addItem/addEnchantedItem/addPotion/editItem/removeItem/addSpawner/removeSpawner>");
						}
					} else {
						player.sendMessage(
								"/adminshop <create/delete/move/editShop/addItem/addEnchantedItem/addPotion/editItem/removeItem/addSpawner/removeSpawner>");
					}
				}
			} catch(ShopSystemException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
			} catch (NumberFormatException e2) {
				player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
			}
		}
		return false;
	}
	
	static void handleAddPotion(Player p, Shop s, String[] args) throws ShopSystemException {
		if (!args[2].equalsIgnoreCase("potion") && !args[2].equalsIgnoreCase("splash_potion")
				&& !args[2].equalsIgnoreCase("lingering_potion")) {
			throw new ShopSystemException(ShopSystemException.INVALID_POTIONTYPE);
		} else if (!args[4].equalsIgnoreCase("extended") && !args[4].equalsIgnoreCase("upgraded")
				&& !args[4].equalsIgnoreCase("none")) {
			throw new ShopSystemException(ShopSystemException.INVALID_POTION_PROPERTY);
		} else if (!args[2].toUpperCase().equals("HAND") && Material.matchMaterial(args[2].toUpperCase()) == null) {
			throw new ShopSystemException(ShopSystemException.INVALID_MATERIAL);
		} else {
			ItemStack itemStack = new ItemStack(Material.valueOf(args[2].toUpperCase()), Integer.valueOf(args[6]));
			PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
			boolean extended = false;
			boolean upgraded = false;
			if (args[4].equalsIgnoreCase("extended")) {
				extended = true;
			} else if (args[4].equalsIgnoreCase("upgraded")) {
				upgraded = true;
			}
			meta.setBasePotionData(new PotionData(PotionType.valueOf(args[3].toUpperCase()), extended, upgraded));
			itemStack.setItemMeta(meta);
			s.addItem(Integer.valueOf(args[5]) - 1, Double.valueOf(args[7]), Double.valueOf(args[8]), itemStack);
			p.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_addItem1") + " " + ChatColor.GREEN
					+ itemStack.getType().toString().toLowerCase() + ChatColor.GOLD + " "
					+ Ultimate_Economy.messages.getString("shop_addItem2"));
		}
	}

	static void handleAddEnchantedItem(Player p, String[] args, Shop s) throws ShopSystemException {
		if (!args[2].toUpperCase().equals("HAND") && Material.matchMaterial(args[2].toUpperCase()) == null) {
			throw new ShopSystemException(ShopSystemException.INVALID_MATERIAL);
		} else {
			Integer length = args.length - 7;
			if (length % 2 == 0) {
				ArrayList<String> enchantmentList = new ArrayList<>();
				for (Integer i = 1; i < length; i = i + 2) {
					enchantmentList.add(args[i + 6].toLowerCase() + "-" + args[i + 7]);
				}
				ItemStack iStack = new ItemStack(Material.valueOf(args[2].toUpperCase()), Integer.valueOf(args[4]));
				ArrayList<String> newEnchantmentList = Shop.addEnchantments(iStack, enchantmentList);
				if (newEnchantmentList.size() < enchantmentList.size()) {
					p.sendMessage(ChatColor.RED + "Not all enchantments could be used!");
				}
				s.addItem(Integer.valueOf(args[3]) - 1, Double.valueOf(args[5]), Double.valueOf(args[6]), iStack);
				p.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_addItem1") + " "
						+ ChatColor.GREEN + iStack.getType().toString().toLowerCase() + ChatColor.GOLD + " "
						+ Ultimate_Economy.messages.getString("shop_addItem2"));
			} else {
				p.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("enchantmentlist_incomplete"));
			}
		}
	}
}
