package com.ue.shopsystem.commands.rentshop;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.impl.RentshopManagerImpl;
import com.ue.ultimate_economy.GeneralEconomyException;

public enum RentshopCommandEnum {

	CREATE {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws NumberFormatException, GeneralEconomyException {
			if (player.hasPermission("ultimate_economy.rentshop.admin")) {
				if (args.length == 3) {
					Rentshop shop = RentshopManagerImpl.createRentShop(player.getLocation(), Integer.valueOf(args[1]),
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
					RentshopManagerImpl.deleteRentShop(RentshopManagerImpl.getRentShopByUniqueName(args[1], null));
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
				throws EconomyPlayerException, GeneralEconomyException, TownSystemException {
			if (player.hasPermission("ultimate_economy.rentshop.admin")) {
				if (args.length == 2) {
					RentshopManagerImpl.getRentShopByUniqueName(args[1], null).moveShop(player.getLocation());
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
				throws NumberFormatException, ShopSystemException, GeneralEconomyException, EconomyPlayerException {
			if (player.hasPermission("ultimate_economy.rentshop.admin")) {
				if (args.length == 3) {
					RentshopManagerImpl.getRentShopByUniqueName(args[1], null).changeShopSize(Integer.valueOf(args[2]));
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
					RentshopManagerImpl.getRentShopByUniqueName(args[1], player)
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
				RentshopManagerImpl.getRentShopByUniqueName(args[1], player).changeShopName(args[2]);
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
				RentshopManagerImpl.getRentShopByUniqueName(args[1], player).openEditor(player);
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
			throws ShopSystemException, TownSystemException, EconomyPlayerException, GeneralEconomyException;

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
