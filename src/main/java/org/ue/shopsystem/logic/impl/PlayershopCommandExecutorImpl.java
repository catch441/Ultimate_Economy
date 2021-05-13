package org.ue.shopsystem.logic.impl;

import javax.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.shopsystem.logic.api.PlayershopCommandEnum;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.ShopsystemException;

public class PlayershopCommandExecutorImpl implements CommandExecutor {

	private final MessageWrapper messageWrapper;
	private final PlayershopManager playershopManager;
	private final EconomyPlayerManager ecoPlayerManager;

	@Inject
	public PlayershopCommandExecutorImpl(MessageWrapper messageWrapper, PlayershopManager playershopManager,
			EconomyPlayerManager ecoPlayerManager) {
		this.messageWrapper = messageWrapper;
		this.playershopManager = playershopManager;
		this.ecoPlayerManager = ecoPlayerManager;
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
			} catch (EconomyPlayerException | ShopsystemException e) {
				player.sendMessage(e.getMessage());
			} catch (IllegalArgumentException e) {
				player.sendMessage(messageWrapper.getErrorString(ExceptionMessageEnum.INVALID_PARAMETER, args[2]));
			}
		}
		return true;
	}

	private boolean performCommand(String label, String[] args, Player player)
			throws IllegalArgumentException, ShopsystemException, EconomyPlayerException {
		switch (PlayershopCommandEnum.getEnum(args[0])) {
		case CHANGEOWNER:
			return performChangeOwnerCommand(label, args, player);
		case CREATE:
			return performCreateCommand(label, args, player);
		case DELETE:
			return performDeleteCommand(label, args, player);
		case DELETEOTHER:
			return performDeleteOtherCommand(label, args, player);
		case EDITSHOP:
			return performEditShopCommand(label, args, player);
		case MOVE:
			return performMoveCommand(label, args, player);
		case RENAME:
			return performRenameCommand(label, args, player);
		default:
			return false;
		}
	}

	private boolean performCreateCommand(String label, String[] args, Player player)
			throws NumberFormatException, ShopsystemException, EconomyPlayerException {
		if (args.length == 3) {
			playershopManager.createPlayerShop(args[1], player.getLocation(), Integer.valueOf(args[2]),
					ecoPlayerManager.getEconomyPlayerByName(player.getName()));
			player.sendMessage(messageWrapper.getString(MessageEnum.CREATED, args[1]));
		} else {
			player.sendMessage("/" + label + " create <shop> <size> <- size have to be a multible of 9");
		}
		return true;
	}

	private boolean performDeleteCommand(String label, String[] args, Player player) throws ShopsystemException {
		if (args.length == 2) {
			playershopManager
					.deletePlayerShop(playershopManager.getPlayerShopByUniqueName(args[1] + "_" + player.getName()));
			player.sendMessage(messageWrapper.getString(MessageEnum.DELETED, args[1]));
		} else {
			player.sendMessage("/" + label + " delete <shop>");
		}
		return true;
	}

	private boolean performDeleteOtherCommand(String label, String[] args, Player player) throws ShopsystemException {
		if (player.hasPermission("ultimate_economy.adminshop")) {
			if (args.length == 2) {
				playershopManager.deletePlayerShop(playershopManager.getPlayerShopByUniqueName(args[1]));
				player.sendMessage(messageWrapper.getString(MessageEnum.DELETED, args[1]));
			} else {
				player.sendMessage("/" + label + " deleteOther <shop>");
			}
		}
		return true;
	}

	private boolean performRenameCommand(String label, String[] args, Player player) throws ShopsystemException {
		if (args.length == 3) {
			playershopManager.getPlayerShopByUniqueName(args[1] + "_" + player.getName()).changeShopName(args[2]);
			player.sendMessage(messageWrapper.getString(MessageEnum.SHOP_RENAME, args[1], args[2]));
		} else {
			player.sendMessage("/" + label + " rename <oldName> <newName>");
		}
		return true;
	}

	private boolean performMoveCommand(String label, String[] args, Player player) throws ShopsystemException {
		if (args.length == 2) {
			playershopManager.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
					.changeLocation(player.getLocation());
		} else {
			player.sendMessage("/" + label + " move <shop>");
		}
		return true;
	}

	private boolean performChangeOwnerCommand(String label, String[] args, Player player)
			throws EconomyPlayerException, ShopsystemException {
		if (args.length == 3) {
			EconomyPlayer newOwner = ecoPlayerManager.getEconomyPlayerByName(args[2]);
			playershopManager.getPlayerShopByUniqueName(args[1] + "_" + player.getName()).changeOwner(newOwner);
			player.sendMessage(messageWrapper.getString(MessageEnum.SHOP_CHANGEOWNER1, args[2]));
			if (newOwner.isOnline()) {
				newOwner.getPlayer()
						.sendMessage(messageWrapper.getString(MessageEnum.SHOP_CHANGEOWNER, args[1], player.getName()));
			}
		} else {
			player.sendMessage("/" + label + " changeOwner <shop> <new owner>");
		}
		return true;
	}

	private boolean performEditShopCommand(String label, String[] args, Player player) throws ShopsystemException {
		if (args.length == 2) {
			playershopManager.getPlayerShopByUniqueName(args[1] + "_" + player.getName()).getEditorHandler()
					.openInventory(player);
		} else {
			player.sendMessage("/" + label + " editShop <shop>");
		}
		return true;
	}
}
