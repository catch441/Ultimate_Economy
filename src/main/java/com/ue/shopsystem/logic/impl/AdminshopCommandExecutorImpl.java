package com.ue.shopsystem.logic.impl;

import javax.inject.Inject;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.logic.api.AdminshopManager;
import com.ue.ultimate_economy.GeneralEconomyException;

public class AdminshopCommandExecutorImpl implements CommandExecutor {

	private final AdminshopManager adminshopManager;
	private final MessageWrapper messageWrapper;

	/**
	 * Inject constructor.
	 * 
	 * @param adminshopManager
	 * @param messageWrapper
	 */
	@Inject
	public AdminshopCommandExecutorImpl(AdminshopManager adminshopManager, MessageWrapper messageWrapper) {
		this.adminshopManager = adminshopManager;
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
			} catch (TownSystemException | ShopSystemException | EconomyPlayerException | GeneralEconomyException e) {
				player.sendMessage(e.getMessage());
			} catch (NumberFormatException e) {
				player.sendMessage(messageWrapper.getErrorString("invalid_parameter", "number"));
			}
		}
		return true;
	}

	private boolean performCommand(String label, String[] args, Player player)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException, TownSystemException {
		switch (AdminshopCommandEnum.getEnum(args[0])) {
		case ADDSPAWNER:
			return performAddSpawnerCommand(label, args, player);
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
			return false;
		}
	}

	private boolean performCreateCommand(String label, String[] args, Player player)
			throws NumberFormatException, ShopSystemException, GeneralEconomyException {
		if (args.length == 3) {
			adminshopManager.createAdminShop(args[1], player.getLocation(), Integer.valueOf(args[2]));
			player.sendMessage(messageWrapper.getString("shop_create", args[1]));
		} else {
			player.sendMessage("/" + label + " create <shopname> <size> <- size have to be a multible of 9");
		}
		return true;
	}

	private boolean performDeleteCommand(String label, String[] args, Player player)
			throws ShopSystemException, GeneralEconomyException {
		if (args.length == 2) {
			adminshopManager.deleteAdminShop(adminshopManager.getAdminShopByName(args[1]));
			player.sendMessage(messageWrapper.getString("shop_delete", args[1]));
		} else {
			player.sendMessage("/" + label + " delete <shopname>");
		}
		return true;
	}

	private boolean performRenameCommand(String label, String[] args, Player player)
			throws ShopSystemException, GeneralEconomyException {
		if (args.length == 3) {
			adminshopManager.getAdminShopByName(args[1]).changeShopName(args[2]);
			player.sendMessage(messageWrapper.getString("shop_rename", args[1], args[2]));
		} else {
			player.sendMessage("/" + label + " rename <oldName> <newName>");
		}
		return true;
	}

	private boolean performResizeCommand(String label, String[] args, Player player)
			throws NumberFormatException, ShopSystemException, GeneralEconomyException, EconomyPlayerException {
		if (args.length == 3) {
			adminshopManager.getAdminShopByName(args[1]).changeShopSize(Integer.valueOf(args[2]));
			player.sendMessage(messageWrapper.getString("shop_resize", args[2]));
		} else {
			player.sendMessage("/" + label + " resize <shopname> <new size>");
		}
		return true;
	}

	private boolean performMoveCommand(String label, String[] args, Player player)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		if (args.length == 2) {
			adminshopManager.getAdminShopByName(args[1]).moveShop(player.getLocation());
		} else {
			player.sendMessage("/" + label + " move <shopname>");
		}
		return true;
	}

	private boolean performEditShopCommand(String label, String[] args, Player player)
			throws ShopSystemException, GeneralEconomyException {
		if (args.length == 2) {
			adminshopManager.getAdminShopByName(args[1]).openEditor(player);
		} else {
			player.sendMessage("/" + label + " editShop <shopname>");
		}
		return true;
	}

	private boolean performChangeProfessionCommand(String label, String[] args, Player player)
			throws GeneralEconomyException {
		if (args.length == 3) {
			try {
				adminshopManager.getAdminShopByName(args[1])
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

	private boolean performAddSpawnerCommand(String label, String[] args, Player player)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		if (args.length == 5) {
			try {
				EntityType.valueOf(args[2].toUpperCase());
				ItemStack itemStack = new ItemStack(Material.SPAWNER, 1);
				ItemMeta meta = itemStack.getItemMeta();
				meta.setDisplayName(args[2].toUpperCase());
				itemStack.setItemMeta(meta);
				adminshopManager.getAdminShopByName(args[1]).addShopItem(Integer.valueOf(args[3]) - 1, 0.0,
						Double.valueOf(args[4]), itemStack);
				player.sendMessage(messageWrapper.getString("shop_addSpawner", args[2]));
			} catch (IllegalArgumentException e) {
				player.sendMessage(messageWrapper.getErrorString("invalid_parameter", args[2]));
			}
		} else {
			player.sendMessage("/" + label + " addSpawner <shopname> <entity type> <slot> <buyPrice>");
		}
		return true;
	}
}
