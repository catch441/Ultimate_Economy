package com.ue.shopsystem.adminshop;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.Shop;

import ultimate_economy.Ultimate_Economy;

public class AdminshopCommandExecutor implements CommandExecutor {

	private Ultimate_Economy plugin;

	public AdminshopCommandExecutor(Ultimate_Economy plugin) {
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
								Adminshop.createAdminShop(plugin.getDataFolder(), args[1], player.getLocation(),
										Integer.valueOf(args[2]));
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_create1") + " "
												+ ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_create2"));
								plugin.getConfig().set("AdminShopIds", Adminshop.getAdminshopIdList());
								plugin.saveConfig();
							} else {
								player.sendMessage("/" + label + " create <shopname> <size> <- size have to be a multible of 9");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "delete":
							if (args.length == 2) {
								Adminshop.deleteAdminShop(args[1]);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_delete1") + " "
												+ ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_delete2"));
								plugin.getConfig().set("AdminShopIds", Adminshop.getAdminshopIdList());
								plugin.saveConfig();
							} else {
								player.sendMessage("/" + label + " delete <shopname>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "rename":
							if (args.length == 3) {
								Adminshop.getAdminShopByName(args[1]).changeShopName(args[2]);
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
								Adminshop.getAdminShopByName(args[1]).changeShopSize(Integer.valueOf(args[2]));
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_resize") + " "
												+ ChatColor.GREEN + args[2] + ChatColor.GOLD + ".");
							} else {
								player.sendMessage("/" + label + " resize <shopname> <new size>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "move":
							if (args.length == 2) {
								Adminshop.getAdminShopByName(args[1]).moveShop(player.getLocation());
							} else {
								player.sendMessage("/" + label + " move <shopname>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "editShop":
							if (args.length == 2) {
								Adminshop.getAdminShopByName(args[1]).openEditor(player);
							} else {
								player.sendMessage("/" + label + " editShop <shopname>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "changeProfession":
							if (args.length == 3) {
								try {
									Adminshop.getAdminShopByName(args[1])
											.changeProfession(Profession.valueOf(args[2].toUpperCase()));
									player.sendMessage(ChatColor.GOLD
											+ Ultimate_Economy.messages.getString("profession_changed"));
								} catch (IllegalArgumentException e) {
									player.sendMessage(ChatColor.RED
											+ Ultimate_Economy.messages.getString("invalid_profession"));
								}
							} else {
								player.sendMessage("/" + label + " changeProfession <shopname> <profession>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "addItem":
							if (args.length == 7) {
								if (Material.matchMaterial(args[2].toUpperCase()) == null) {
									throw new ShopSystemException(ShopSystemException.INVALID_MATERIAL);
								} else {
									ItemStack itemStack = new ItemStack(Material.getMaterial(args[2].toUpperCase()),
											Integer.valueOf(args[4]));
									Adminshop.getAdminShopByName(args[1]).addShopItem(Integer.valueOf(args[3]) - 1,
											Double.valueOf(args[5]), Double.valueOf(args[6]), itemStack);
									player.sendMessage(
											ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_addItem1")
													+ " " + ChatColor.GREEN
													+ itemStack.getType().toString().toLowerCase() + ChatColor.GOLD
													+ " " + Ultimate_Economy.messages.getString("shop_addItem2"));
								}
							} else {
								player.sendMessage(
										"/" + label + " addItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice>");
								player.sendMessage(Ultimate_Economy.messages.getString("shop_addItem_errorinfo"));
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "removeItem":
							if (args.length == 3) {
								Adminshop shop = Adminshop.getAdminShopByName(args[1]);
								String itemName = shop.getItem(Integer.valueOf(args[2])).getType().toString()
										.toLowerCase();
								shop.removeShopItem(Integer.valueOf(args[2]) - 1);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_removeItem1")
												+ " " + ChatColor.GREEN + itemName + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_removeItem2"));
							} else {
								player.sendMessage("/" + label + " removeItem <shopname> <slot (> 0)>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "addPotion":
							if (args.length == 9) {
								handleAddPotion(player, Adminshop.getAdminShopByName(args[1]), args);
							} else {
								player.sendMessage(
										"/" + label + " addPotion <shopname> <potionType> <potionEffect> <extended/upgraded/none> <slot> <amount> <sellprice> <buyprice>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "addEnchantedItem":
							if (args.length >= 9) {
								handleAddEnchantedItem(player, args, Adminshop.getAdminShopByName(args[1]));
							} else {
								player.sendMessage(
										"/" + label + " addEnchantedItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice> [<enchantment> <lvl>]");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "addSpawner":
							if (args.length == 5) {
								ItemStack itemStack = new ItemStack(Material.SPAWNER, 1);
								ItemMeta meta = itemStack.getItemMeta();
								meta.setDisplayName(args[2].toUpperCase());
								itemStack.setItemMeta(meta);
								Adminshop.getAdminShopByName(args[1]).addShopItem(Integer.valueOf(args[3]) - 1, 0.0,
										Double.valueOf(args[4]), itemStack);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_addSpawner1") + " "
												+ ChatColor.GREEN + args[2] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_addSpawner2"));
							} else {
								player.sendMessage("/" + label + " addSpawner <shopname> <entity type> <slot> <buyPrice>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "removeSpawner":
							if (args.length == 3) {
								Adminshop shop = Adminshop.getAdminShopByName(args[1]);
								String itemName = shop.getItem(Integer.valueOf(args[2])).getType().toString()
										.toLowerCase();
								shop.removeShopItem(Integer.valueOf(args[2]) - 1);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_removeSpawner1")
												+ " " + ChatColor.GREEN + itemName + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_removeSpawner1"));
							} else {
								player.sendMessage("/" + label + " removeSpawner <shopname> <slot>");
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						case "editItem":
							if (args.length == 6) {
								player.sendMessage(Adminshop.getAdminShopByName(args[1])
										.editShopItem(Integer.valueOf(args[2]), args[3], args[4], args[5]));
							} else {
								player.sendMessage(
										"/" + label + " editItem <shopname> <slot> <amount> <sellPrice> <buyPrice>");
								player.sendMessage(Ultimate_Economy.messages.getString("shop_editItem_errorinfo"));
							}
							break;
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						default:
							return false;
					}
				} else {
					return false;
				}
			} catch (ShopSystemException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
			} catch (NumberFormatException e2) {
				player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
			} catch (TownSystemException e) {}
		}
		return true;
	}

	public static void handleAddPotion(Player p, Shop s, String[] args) throws ShopSystemException {
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
			s.addShopItem(Integer.valueOf(args[5]) - 1, Double.valueOf(args[7]), Double.valueOf(args[8]), itemStack);
			p.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_addItem1") + " " + ChatColor.GREEN
					+ itemStack.getType().toString().toLowerCase() + ChatColor.GOLD + " "
					+ Ultimate_Economy.messages.getString("shop_addItem2"));
		}
	}

	public static void handleAddEnchantedItem(Player p, String[] args, Shop s) throws ShopSystemException {
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
				s.addShopItem(Integer.valueOf(args[3]) - 1, Double.valueOf(args[5]), Double.valueOf(args[6]), iStack);
				p.sendMessage(ChatColor.GOLD + Ultimate_Economy.messages.getString("shop_addItem1") + " "
						+ ChatColor.GREEN + iStack.getType().toString().toLowerCase() + ChatColor.GOLD + " "
						+ Ultimate_Economy.messages.getString("shop_addItem2"));
			} else {
				p.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("enchantmentlist_incomplete"));
			}
		}
	}
}
