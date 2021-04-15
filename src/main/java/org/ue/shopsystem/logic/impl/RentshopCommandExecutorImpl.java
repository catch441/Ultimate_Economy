package org.ue.shopsystem.logic.impl;

import javax.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.GeneralEconomyException;
import org.ue.shopsystem.logic.RentshopCommandEnum;
import org.ue.shopsystem.logic.ShopSystemException;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.RentshopManager;
import org.ue.townsystem.logic.TownSystemException;

public class RentshopCommandExecutorImpl implements CommandExecutor {

	private final RentshopManager rentshopManager;
	private final MessageWrapper messageWrapper;

	@Inject
	public RentshopCommandExecutorImpl(RentshopManager rentshopManager, MessageWrapper messageWrapper) {
		this.rentshopManager = rentshopManager;
		this.messageWrapper = messageWrapper;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				if (args.length != 0) {
					return performCommand(label, args, player);
				}
				return false;
			} catch (NumberFormatException e) {
				player.sendMessage(messageWrapper.getErrorString("invalid_parameter", "number"));
			} catch (ShopSystemException | EconomyPlayerException | GeneralEconomyException | TownSystemException e) {
				player.sendMessage(e.getMessage());
			}
		}
		return true;
	}

	private boolean performCommand(String label, String[] args, Player player)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException, ShopSystemException {
		switch (RentshopCommandEnum.getEnum(args[0])) {
		case CHANGEPROFESSION:
			return performChangeProfessionCommand(label, args, player);
		case CREATE:
			return performCreateCommand(label, args, player);
		case DELETE:
			return performDeleteCommand(label, args, player);
		case EDITSHOP:
			return performEditShopCommand(label, args, player);
		case MOVE:
			return performMoveCommand(label, args, player);
		case RENAME:
			return performRenameCommand(label, args, player);
		case RESIZE:
			return performResizeCommand(label, args, player);
		default:
			if (player.hasPermission("ultimate_economy.rentshop.admin")) {
				player.sendMessage("/" + label + " [create/delete/move/resize/editShop]");
			} else {
				player.sendMessage("/" + label + " [editShop]");
			}
			return true;
		}
	}

	private boolean performCreateCommand(String label, String[] args, Player player)
			throws NumberFormatException, GeneralEconomyException {
		if (player.hasPermission("ultimate_economy.rentshop.admin")) {
			if (args.length == 3) {
				Rentshop shop = rentshopManager.createRentShop(player.getLocation(), Integer.valueOf(args[1]),
						Double.valueOf(args[2]));
				player.sendMessage(messageWrapper.getString("created", shop.getName()));

			} else {
				player.sendMessage("/" + label + " create <size> <rentalFee per 24h>");
			}
		}
		return true;
	}

	private boolean performDeleteCommand(String label, String[] args, Player player) throws GeneralEconomyException {
		if (player.hasPermission("ultimate_economy.rentshop.admin")) {
			if (args.length == 2) {
				rentshopManager.deleteRentShop(rentshopManager.getRentShopByUniqueName(args[1]));
				player.sendMessage(messageWrapper.getString("deleted", args[1]));
			} else {
				player.sendMessage("/" + label + " delete <shopname>");
			}
		}
		return true;
	}

	private boolean performMoveCommand(String label, String[] args, Player player)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		if (player.hasPermission("ultimate_economy.rentshop.admin")) {
			if (args.length == 2) {
				rentshopManager.getRentShopByUniqueName(args[1]).moveShop(player.getLocation());
			} else {
				player.sendMessage("/" + label + " move <shopname>");
			}
		}
		return true;
	}

	private boolean performResizeCommand(String label, String[] args, Player player)
			throws NumberFormatException, ShopSystemException, GeneralEconomyException, EconomyPlayerException {
		if (player.hasPermission("ultimate_economy.rentshop.admin")) {
			if (args.length == 3) {
				rentshopManager.getRentShopByUniqueName(args[1]).changeShopSize(Integer.valueOf(args[2]));
				player.sendMessage(messageWrapper.getString("shop_resize", args[2]));
			} else {
				player.sendMessage("/" + label + " resize <shopname> <new size>");
			}
		}
		return true;
	}

	private boolean performChangeProfessionCommand(String label, String[] args, Player player)
			throws GeneralEconomyException {
		if (args.length == 3) {
			try {
				rentshopManager.getRentShopByUniqueName(args[1] + "_" + player.getName())
						.changeProfession(Profession.valueOf(args[2].toUpperCase()));
				player.sendMessage(messageWrapper.getString("profession_changed"));
			} catch (IllegalArgumentException e) {
				player.sendMessage(messageWrapper.getErrorString("invalid_parameter", args[2]));
			}
		} else {
			player.sendMessage("/" + label + " changeProfession <shopname> <profession>");
		}
		return true;
	}

	private boolean performRenameCommand(String label, String[] args, Player player)
			throws ShopSystemException, GeneralEconomyException {
		if (args.length == 3) {
			rentshopManager.getRentShopByUniqueName(args[1] + "_" + player.getName()).changeShopName(args[2]);
			player.sendMessage(messageWrapper.getString("shop_rename", args[1], args[2]));
		} else {
			player.sendMessage("/" + label + " rename <oldName> <newName>");
		}
		return true;
	}

	private boolean performEditShopCommand(String label, String[] args, Player player)
			throws ShopSystemException, GeneralEconomyException {
		if (args.length == 2) {
			rentshopManager.getRentShopByUniqueName(args[1] + "_" + player.getName()).openEditor(player);
		} else {
			player.sendMessage("/" + label + " editShop <shopname>");
		}
		return true;
	}
}