package com.ue.shopsystem.commands.rentshop;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.shopsystem.api.Rentshop;
import com.ue.shopsystem.api.RentshopController;

public enum RentshopCommandEnum {

	CREATE {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws NumberFormatException, GeneralEconomyException {
			if (player.hasPermission("ultimate_economy.rentshop.admin")) {
				if (args.length == 3) {
					Rentshop shop = RentshopController.createRentShop(player.getLocation(), Integer.valueOf(args[1]),
							Double.valueOf(args[2]));
					player.sendMessage(MessageWrapper.getString("shop_create", shop.getName()));

				} else {
					player.sendMessage("/" + label + " create <size> <rentalFee per 24h>");
				}
			}
			return true;
		}
	},
	DELETE {
		@Override
		boolean perform(String label, String[] args, Player player) throws GeneralEconomyException {
			if (player.hasPermission("ultimate_economy.rentshop.admin")) {
				if (args.length == 2) {
					RentshopController.deleteRentShop(RentshopController.getRentShopByUniqueName(args[1], null));
					player.sendMessage(MessageWrapper.getString("shop_delete", args[1]));
				} else {
					player.sendMessage("/" + label + " delete <shopname>");
				}
			}
			return true;
		}
	},
	MOVE {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws PlayerException, GeneralEconomyException, TownSystemException {
			if (player.hasPermission("ultimate_economy.rentshop.admin")) {
				if (args.length == 2) {
					RentshopController.getRentShopByUniqueName(args[1], null).moveShop(player.getLocation());
				} else {
					player.sendMessage("/" + label + " move <shopname>");
				}
			}
			return true;
		}
	},
	RESIZE {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws NumberFormatException, ShopSystemException, GeneralEconomyException, PlayerException {
			if (player.hasPermission("ultimate_economy.rentshop.admin")) {
				if (args.length == 3) {
					RentshopController.getRentShopByUniqueName(args[1], null).changeShopSize(Integer.valueOf(args[2]));
					player.sendMessage(MessageWrapper.getString("shop_resize", args[2]));
				} else {
					player.sendMessage("/" + label + " resize <shopname> <new size>");
				}
			}
			return true;
		}
	},
	CHANGEPROFESSION {
		@Override
		boolean perform(String label, String[] args, Player player) throws GeneralEconomyException {
			if (args.length == 3) {
				try {
					RentshopController.getRentShopByUniqueName(args[1], player)
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
	RENAME {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, GeneralEconomyException {
			if (args.length == 3) {
				RentshopController.getRentShopByUniqueName(args[1], player).changeShopName(args[2]);
				player.sendMessage(MessageWrapper.getString("shop_rename", args[1], args[2]));
			} else {
				player.sendMessage("/" + label + " rename <oldName> <newName>");
			}
			return true;
		}
	},
	EDITSHOP {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws ShopSystemException, GeneralEconomyException {
			if (args.length == 2) {
				RentshopController.getRentShopByUniqueName(args[1], player).openEditor(player);
			} else {
				player.sendMessage("/" + label + " editShop <shopname>");
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
	 * Returns a enum. Returns RentshopCommandEnum.UNKNOWN , if no enum is found.
	 * 
	 * @param value
	 * @return rentshop command enum
	 */
	public static RentshopCommandEnum getEnum(String value) {
		for (RentshopCommandEnum command : values()) {
			if (command.name().equalsIgnoreCase(value)) {
				return command;
			}
		}
		return RentshopCommandEnum.UNKNOWN;
	}
}
