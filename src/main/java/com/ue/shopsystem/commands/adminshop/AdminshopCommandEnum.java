package com.ue.shopsystem.commands.adminshop;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.shopsystem.api.Adminshop;
import com.ue.shopsystem.api.AdminshopController;
import com.ue.shopsystem.impl.AbstractShopImpl;

public enum AdminshopCommandEnum {

	CREATE {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
			if (args.length == 3) {
				AdminshopController.createAdminShop(args[1], player.getLocation(), Integer.valueOf(args[2]));
				player.sendMessage(MessageWrapper.getString("shop_create", args[1]));
			} else {
				player.sendMessage("/" + label + " create <shopname> <size> <- size have to be a multible of 9");
			}
			return true;
		}
	},
	DELETE {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
			if (args.length == 2) {
				AdminshopController.deleteAdminShop(AdminshopController.getAdminShopByName(args[1]));
				player.sendMessage(MessageWrapper.getString("shop_delete", args[1]));
			} else {
				player.sendMessage("/" + label + " delete <shopname>");
			}
			return true;
		}
	},
	RENAME {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
			if (args.length == 3) {
				AdminshopController.getAdminShopByName(args[1]).changeShopName(args[2]);
				player.sendMessage(MessageWrapper.getString("shop_rename", args[1], args[2]));
			} else {
				player.sendMessage("/" + label + " rename <oldName> <newName>");
			}
			return true;
		}
	},
	RESIZE {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
			if (args.length == 3) {
				AdminshopController.getAdminShopByName(args[1]).changeShopSize(Integer.valueOf(args[2]));
				player.sendMessage(MessageWrapper.getString("shop_resize", args[2]));
			} else {
				player.sendMessage("/" + label + " resize <shopname> <new size>");
			}
			return true;
		}
	},
	MOVE {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
			if (args.length == 2) {
				AdminshopController.getAdminShopByName(args[1]).moveShop(player.getLocation());
			} else {
				player.sendMessage("/" + label + " move <shopname>");
			}
			return true;
		}
	},
	EDITSHOP {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
			if (args.length == 2) {
				AdminshopController.getAdminShopByName(args[1]).openEditor(player);
			} else {
				player.sendMessage("/" + label + " editShop <shopname>");
			}
			return true;
		}
	},
	CHANGEPROFESSION {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
			if (args.length == 3) {
				try {
					AdminshopController.getAdminShopByName(args[1])
							.changeProfession(Profession.valueOf(args[2].toUpperCase()));
					player.sendMessage(MessageWrapper.getString("profession_changed"));
				} catch (IllegalArgumentException e) {
					player.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[2]));
				}
			} else {
				player.sendMessage("/" + label + " changeProfession <shopname> <profession>");
			}
			return true;
		}
	},
	ADDITEM {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
			if (args.length == 7) {
				if (Material.matchMaterial(args[2].toUpperCase()) == null) {
					throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
							args[2]);
				} else {
					ItemStack itemStack = new ItemStack(Material.getMaterial(args[2].toUpperCase()),
							Integer.valueOf(args[4]));
					AdminshopController.getAdminShopByName(args[1]).addShopItem(Integer.valueOf(args[3]) - 1,
							Double.valueOf(args[5]), Double.valueOf(args[6]), itemStack);
					player.sendMessage(
							MessageWrapper.getString("shop_addItem", itemStack.getType().toString().toLowerCase()));
				}
			} else {
				player.sendMessage(
						"/" + label + " addItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice>");
			}
			return true;
		}
	},
	REMOVEITEM {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
			if (args.length == 3) {
				Adminshop shop = AdminshopController.getAdminShopByName(args[1]);
				String itemName = shop.getShopItem(Integer.valueOf(args[2])).getItemStack().getType().toString()
						.toLowerCase();
				shop.removeShopItem(Integer.valueOf(args[2]) - 1);
				player.sendMessage(MessageWrapper.getString("shop_removeItem", itemName));
			} else {
				player.sendMessage("/" + label + " removeItem <shopname> <slot (> 0)>");
			}
			return true;
		}
	},
	ADDPOTION {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
			if (args.length == 9) {
				handleAddPotion(player, AdminshopController.getAdminShopByName(args[1]), args);
			} else {
				player.sendMessage("/" + label + " addPotion <shopname> <potionType> <potionEffect> "
						+ "<extended/upgraded/none> <slot> <amount> <sellprice> <buyprice>");
			}
			return true;
		}
	},
	ADDENCHANTEDITEM {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
			if (args.length >= 9) {
				handleAddEnchantedItem(player, args, AdminshopController.getAdminShopByName(args[1]));
			} else {
				player.sendMessage("/" + label + " addEnchantedItem <shopname> <material> <slot> "
						+ "<amount> <sellPrice> <buyPrice> [<enchantment> <lvl>]");
			}
			return true;
		}
	},
	ADDSPAWNER {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
			if (args.length == 5) {
				ItemStack itemStack = new ItemStack(Material.SPAWNER, 1);
				ItemMeta meta = itemStack.getItemMeta();
				meta.setDisplayName(args[2].toUpperCase());
				itemStack.setItemMeta(meta);
				AdminshopController.getAdminShopByName(args[1]).addShopItem(Integer.valueOf(args[3]) - 1, 0.0,
						Double.valueOf(args[4]), itemStack);
				player.sendMessage(MessageWrapper.getString("shop_addSpawner", args[2]));
			} else {
				player.sendMessage("/" + label + " addSpawner <shopname> <entity type> <slot> <buyPrice>");
			}
			return true;
		}
	},
	REMOVESPAWNER {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
			if (args.length == 3) {
				Adminshop shop = AdminshopController.getAdminShopByName(args[1]);
				String itemName = shop.getShopItem(Integer.valueOf(args[2])).getItemStack().getType().toString()
						.toLowerCase();
				shop.removeShopItem(Integer.valueOf(args[2]) - 1);
				player.sendMessage(MessageWrapper.getString("shop_removeSpawner", itemName));
			} else {
				player.sendMessage("/" + label + " removeSpawner <shopname> <slot>");
			}
			return true;
		}
	},
	EDITITEM {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
			if (args.length == 6) {
				player.sendMessage(AdminshopController.getAdminShopByName(args[1])
						.editShopItem(Integer.valueOf(args[2]), args[3], args[4], args[5]));
			} else {
				player.sendMessage(MessageWrapper.getString("shop_editItem_errorinfo"));
				player.sendMessage("/" + label + " editItem <shopname> <slot> <amount> <sellPrice> <buyPrice>");
			}
			return true;
		}
	},
	UNKNOWN {
		@Override
		boolean perform(String label, String[] args, Player player) {
			return false;
		}
	};

	abstract boolean perform(String label, String[] args, Player player)
			throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException;

	/**
	 * Returns a enum. Return AdminshopCommandEnum.UNKNOWN, if no enum is found.
	 * 
	 * @param value
	 * @return adminshop command enum
	 */
	public static AdminshopCommandEnum getEnum(String value) {
		for (AdminshopCommandEnum command : values()) {
			if (command.name().equalsIgnoreCase(value)) {
				return command;
			}
		}
		return AdminshopCommandEnum.UNKNOWN;
	}

	private static void handleAddPotion(Player p, Adminshop s, String[] args)
			throws ShopSystemException, PlayerException, GeneralEconomyException {
		if (!args[2].equalsIgnoreCase("potion") && !args[2].equalsIgnoreCase("splash_potion")
				&& !args[2].equalsIgnoreCase("lingering_potion")) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, args[2]);
		} else if (!args[4].equalsIgnoreCase("extended") && !args[4].equalsIgnoreCase("upgraded")
				&& !args[4].equalsIgnoreCase("none")) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, args[4]);
		} else if (!args[2].toUpperCase().equals("HAND") && Material.matchMaterial(args[2].toUpperCase()) == null) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, args[2]);
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
			p.sendMessage(MessageWrapper.getString("shop_addItem", itemStack.getType().toString().toLowerCase()));
		}
	}

	private static void handleAddEnchantedItem(Player p, String[] args, Adminshop s)
			throws ShopSystemException, PlayerException, GeneralEconomyException {
		if (!args[2].toUpperCase().equals("HAND") && Material.matchMaterial(args[2].toUpperCase()) == null) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, args[2]);
		} else {
			Integer length = args.length - 7;
			if (length % 2 == 0) {
				ArrayList<String> enchantmentList = new ArrayList<>();
				for (Integer i = 1; i < length; i = i + 2) {
					enchantmentList.add(args[i + 6].toLowerCase() + "-" + args[i + 7]);
				}
				ItemStack iStack = new ItemStack(Material.valueOf(args[2].toUpperCase()), Integer.valueOf(args[4]));
				ArrayList<String> newEnchantmentList = addEnchantments(iStack, enchantmentList);
				if (newEnchantmentList.size() < enchantmentList.size()) {
					p.sendMessage(ChatColor.RED + "Not all enchantments could be used!");
				}
				s.addShopItem(Integer.valueOf(args[3]) - 1, Double.valueOf(args[5]), Double.valueOf(args[6]), iStack);
				p.sendMessage(MessageWrapper.getString("shop_addItem", iStack.getType().toString().toLowerCase()));
			} else {
				throw PlayerException.getException(PlayerExceptionMessageEnum.ENCHANTMENTLIST_INCOMPLETE);
			}
		}
	}
	
	/**
	 * --Utils Method--
	 * <p>
	 * This Method adds a list of enchantments to a given itemstack and returns a
	 * list of all valid used enchantments.
	 * 
	 * @param itemStack
	 * @param enchantmentList
	 * @return list of used enachantments
	 */
	public static ArrayList<String> addEnchantments(ItemStack itemStack, ArrayList<String> enchantmentList) {
		Enchantment e = null;
		int lvl = 0;
		ArrayList<String> newList = new ArrayList<>();
		for (String enchantment : enchantmentList) {
			e = Enchantment.getByKey(NamespacedKey.minecraft(enchantment.substring(0, enchantment.indexOf("-"))));
			lvl = Integer.valueOf(enchantment.substring(enchantment.indexOf("-") + 1));
			if (e.getMaxLevel() < lvl) {
				lvl = e.getMaxLevel();
				enchantment = enchantment.substring(0, enchantment.indexOf("-") + 1) + String.valueOf(lvl);
			}
			if (itemStack.getType().toString().equals("ENCHANTED_BOOK")) {
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
				meta.addStoredEnchant(e, lvl, true);
				itemStack.setItemMeta(meta);
			} else if (e.canEnchantItem(itemStack)) {
				itemStack.addEnchantment(e, lvl);
			}
		}
		if (itemStack.getType().toString().equals("ENCHANTED_BOOK")) {
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
			for (Entry<Enchantment, Integer> map : meta.getStoredEnchants().entrySet()) {
				newList.add(
						map.getKey().getKey().toString().substring(map.getKey().getKey().toString().indexOf(":") + 1)
								+ "-" + map.getValue().intValue());
				newList.sort(String.CASE_INSENSITIVE_ORDER);
			}
		} else {
			for (Entry<Enchantment, Integer> map : itemStack.getEnchantments().entrySet()) {
				newList.add(
						map.getKey().getKey().toString().substring(map.getKey().getKey().toString().indexOf(":") + 1)
								+ "-" + map.getValue().intValue());
				newList.sort(String.CASE_INSENSITIVE_ORDER);
			}
		}
		return newList;
	}
}
