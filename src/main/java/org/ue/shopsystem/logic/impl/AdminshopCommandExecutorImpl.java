package org.ue.shopsystem.logic.impl;

import java.util.List;

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
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.jobsystem.logic.api.JobsystemException;
import org.ue.shopsystem.logic.api.AdminshopCommandEnum;
import org.ue.shopsystem.logic.api.AdminshopManager;
import org.ue.shopsystem.logic.api.ShopsystemException;

public class AdminshopCommandExecutorImpl implements CommandExecutor {

	private final AdminshopManager adminshopManager;
	private final MessageWrapper messageWrapper;
	private final ServerProvider serverProvider;
	private final EconomyPlayerManager ecoPlayerManager;
	private final JobManager jobManager;
	private final ConfigManager configManager;

	@Inject
	public AdminshopCommandExecutorImpl(JobManager jobManager, EconomyPlayerManager ecoPlayerManager,
			AdminshopManager adminshopManager, MessageWrapper messageWrapper, ServerProvider serverProvider,
			ConfigManager configManager) {
		this.adminshopManager = adminshopManager;
		this.messageWrapper = messageWrapper;
		this.serverProvider = serverProvider;
		this.ecoPlayerManager = ecoPlayerManager;
		this.jobManager = jobManager;
		this.configManager = configManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				return handleCommand(label, args, player);
			} catch (JobsystemException | ShopsystemException | EconomyPlayerException e) {
				player.sendMessage(e.getMessage());
			} catch (NumberFormatException e) {
				player.sendMessage(messageWrapper.getErrorString("invalid_parameter", "number"));
			}
		}
		return true;
	}

	private boolean handleCommand(String label, String[] args, Player player)
			throws EconomyPlayerException, JobsystemException, ShopsystemException {
		switch (label) {
		case "shoplist":
			return handleShopListCommand(label, args, player);
		case "shop":
			return handleShopCommand(label, args, player);
		case "adminshop":
			if (args.length != 0) {
				return handleAdminshopCommand(label, args, player);
			}
			return false;
		default:
			return false;
		}
	}

	private boolean handleShopCommand(String label, String[] args, Player player)
			throws EconomyPlayerException, JobsystemException, ShopsystemException {
		if (args.length == 1) {
			EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(player.getName());
			if (configManager.isAllowQuickshop() || ecoPlayer.hasJob(jobManager.getJobByName(args[0]))) {
				adminshopManager.getAdminShopByName(args[0]).openInventory(player);
			} else {
				player.sendMessage(messageWrapper.getErrorString("job_not_joined"));
			}
			return true;
		}
		return false;
	}

	private boolean handleShopListCommand(String label, String[] args, Player player) {
		if (args.length == 0) {
			List<String> shopNames = adminshopManager.getAdminshopNameList();
			player.sendMessage(messageWrapper.getString("shoplist_info", shopNames.toString()));
			return true;
		}
		return false;
	}

	private boolean handleAdminshopCommand(String label, String[] args, Player player)
			throws NumberFormatException, ShopsystemException {
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
			throws NumberFormatException, ShopsystemException {
		if (args.length == 3) {
			adminshopManager.createAdminShop(args[1], player.getLocation(), Integer.valueOf(args[2]));
			player.sendMessage(messageWrapper.getString("created", args[1]));
		} else {
			player.sendMessage("/" + label + " create <shopname> <size> <- size have to be a multible of 9");
		}
		return true;
	}

	private boolean performDeleteCommand(String label, String[] args, Player player) throws ShopsystemException {
		if (args.length == 2) {
			adminshopManager.deleteAdminShop(adminshopManager.getAdminShopByName(args[1]));
			player.sendMessage(messageWrapper.getString("deleted", args[1]));
		} else {
			player.sendMessage("/" + label + " delete <shopname>");
		}
		return true;
	}

	private boolean performRenameCommand(String label, String[] args, Player player) throws ShopsystemException {
		if (args.length == 3) {
			adminshopManager.getAdminShopByName(args[1]).changeShopName(args[2]);
			player.sendMessage(messageWrapper.getString("shop_rename", args[1], args[2]));
		} else {
			player.sendMessage("/" + label + " rename <oldName> <newName>");
		}
		return true;
	}

	private boolean performResizeCommand(String label, String[] args, Player player)
			throws NumberFormatException, ShopsystemException {
		if (args.length == 3) {
			adminshopManager.getAdminShopByName(args[1]).changeSize(Integer.valueOf(args[2]));
			player.sendMessage(messageWrapper.getString("shop_resize", args[2]));
		} else {
			player.sendMessage("/" + label + " resize <shopname> <new size>");
		}
		return true;
	}

	private boolean performMoveCommand(String label, String[] args, Player player) throws ShopsystemException {
		if (args.length == 2) {
			adminshopManager.getAdminShopByName(args[1]).changeLocation(player.getLocation());
		} else {
			player.sendMessage("/" + label + " move <shopname>");
		}
		return true;
	}

	private boolean performEditShopCommand(String label, String[] args, Player player) throws ShopsystemException {
		if (args.length == 2) {
			adminshopManager.getAdminShopByName(args[1]).openEditor(player);
		} else {
			player.sendMessage("/" + label + " editShop <shopname>");
		}
		return true;
	}

	private boolean performChangeProfessionCommand(String label, String[] args, Player player)
			throws ShopsystemException {
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
			throws NumberFormatException, ShopsystemException {
		if (args.length == 5) {
			try {
				EntityType.valueOf(args[2].toUpperCase());
				ItemStack itemStack = serverProvider.createItemStack(Material.SPAWNER, 1);
				ItemMeta meta = itemStack.getItemMeta();
				meta.setDisplayName(args[2].toUpperCase());
				itemStack.setItemMeta(meta);
				adminshopManager.getAdminShopByName(args[1]).addShopItem(Integer.valueOf(args[3]) - 1, 0.0,
						Double.valueOf(args[4]), itemStack);
				player.sendMessage(messageWrapper.getString("added", args[2]));
			} catch (IllegalArgumentException e) {
				player.sendMessage(messageWrapper.getErrorString("invalid_parameter", args[2]));
			}
		} else {
			player.sendMessage("/" + label + " addSpawner <shopname> <entity type> <slot> <buyPrice>");
		}
		return true;
	}
}
