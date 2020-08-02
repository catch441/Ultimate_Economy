package com.ue.shopsystem.commands.adminshop;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.api.AdminshopController;
import com.ue.ultimate_economy.GeneralEconomyException;

public enum AdminshopCommandEnum {

	CREATE {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, EconomyPlayerException, GeneralEconomyException {
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
				throws ShopSystemException, TownSystemException, EconomyPlayerException, GeneralEconomyException {
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
				throws ShopSystemException, TownSystemException, EconomyPlayerException, GeneralEconomyException {
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
				throws ShopSystemException, TownSystemException, EconomyPlayerException, GeneralEconomyException {
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
				throws ShopSystemException, TownSystemException, EconomyPlayerException, GeneralEconomyException {
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
				throws ShopSystemException, TownSystemException, EconomyPlayerException, GeneralEconomyException {
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
				throws ShopSystemException, TownSystemException, EconomyPlayerException, GeneralEconomyException {
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
	ADDSPAWNER {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, TownSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 5) {
				try {
					EntityType.valueOf(args[2].toUpperCase());
					ItemStack itemStack = new ItemStack(Material.SPAWNER, 1);
					ItemMeta meta = itemStack.getItemMeta();
					meta.setDisplayName(args[2].toUpperCase());
					itemStack.setItemMeta(meta);
					AdminshopController.getAdminShopByName(args[1]).addShopItem(Integer.valueOf(args[3]) - 1, 0.0,
							Double.valueOf(args[4]), itemStack);
					player.sendMessage(MessageWrapper.getString("shop_addSpawner", args[2]));
				} catch (IllegalArgumentException e) {
					player.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[2]));
				}
			} else {
				player.sendMessage("/" + label + " addSpawner <shopname> <entity type> <slot> <buyPrice>");
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
			throws ShopSystemException, TownSystemException, EconomyPlayerException, GeneralEconomyException;

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
}
