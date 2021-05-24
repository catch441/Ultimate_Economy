package org.ue.townsystem.logic.impl;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ue.bank.logic.api.BankException;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.townsystem.logic.api.Plot;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownCommandEnum;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.TownsystemValidator;
import org.ue.townsystem.logic.api.Townworld;
import org.ue.townsystem.logic.api.TownworldManager;

public class TownCommandExecutorImpl implements CommandExecutor {

	private final ConfigManager configManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private final TownworldManager townworldManager;
	private final MessageWrapper messageWrapper;
	private final TownsystemValidator townsystemValidationHandler;

	public TownCommandExecutorImpl(ConfigManager configManager, EconomyPlayerManager ecoPlayerManager,
			TownworldManager townworldManager, MessageWrapper messageWrapper,
			TownsystemValidator townsystemValidationHandler) {
		this.configManager = configManager;
		this.ecoPlayerManager = ecoPlayerManager;
		this.townsystemValidationHandler = townsystemValidationHandler;
		this.messageWrapper = messageWrapper;
		this.townworldManager = townworldManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(player.getName());
				if (args.length != 0) {
					return performCommand(label, args, player, ecoPlayer);
				}
				return false;
			} catch (EconomyPlayerException | TownsystemException | BankException e) {
				player.sendMessage(e.getMessage());
			} catch (NumberFormatException e) {
				player.sendMessage(messageWrapper.getErrorString(ExceptionMessageEnum.INVALID_PARAMETER));
			}
		}
		return true;
	}

	private boolean performCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws TownsystemException, BankException, EconomyPlayerException {
		switch (TownCommandEnum.getEnum(args[0])) {
		case ADDDEPUTY:
			return performAddDeputyCommand(label, args, player, ecoPlayer);
		case BANK:
			return performBankCommand(label, args, player, ecoPlayer);
		case CREATE:
			return performCreateCommand(label, args, player, ecoPlayer);
		case DELETE:
			return performDeleteCommand(label, args, player, ecoPlayer);
		case EXPAND:
			return performExpandCommand(label, args, player, ecoPlayer);
		case MOVETOWNMANAGER:
			return performMoveTownmanagerCommand(label, args, player, ecoPlayer);
		case PAY:
			return performPayCommand(label, args, player, ecoPlayer);
		case PLOT:
			return performPlotCommand(label, args, player, ecoPlayer);
		case REMOVEDEPUTY:
			return performRemoveDeputyCommand(label, args, player, ecoPlayer);
		case RENAME:
			return performRenameCommand(label, args, player, ecoPlayer);
		case SETTOWNSPAWN:
			return performSetTownSpawnCommand(label, args, player, ecoPlayer);
		case TP:
			return performTpCommand(label, args, player, ecoPlayer);
		case WITHDRAW:
			return performWithdrawCommand(label, args, player, ecoPlayer);
		default:
			return false;
		}
	}

	private boolean performCreateCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws TownsystemException, EconomyPlayerException, BankException {
		if (args.length == 2) {
			Townworld townworld = townworldManager.getTownWorldByName(player.getWorld().getName());
			townworld.foundTown(args[1], player.getLocation(), ecoPlayer);
			player.sendMessage(messageWrapper.getString(MessageEnum.TOWN_CREATE, args[1]));
		} else {
			player.sendMessage("/" + label + " create <town>");
		}
		return true;
	}

	private boolean performDeleteCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws TownsystemException, EconomyPlayerException {
		if (args.length == 2) {
			Town town = townworldManager.getTownByName(args[1]);
			town.getTownworld().dissolveTown(ecoPlayer, town);
			player.sendMessage(messageWrapper.getString(MessageEnum.TOWN_DELETE, args[1]));
		} else {
			player.sendMessage("/" + label + " delete <town>");
		}
		return true;
	}

	private boolean performExpandCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws TownsystemException, BankException {
		if (args.length == 2) {
			Town town = townworldManager.getTownByName(args[1]);
			town.expandTown(player.getLocation().getChunk(), ecoPlayer);
			player.sendMessage(messageWrapper.getString(MessageEnum.TOWN_EXPAND));
		} else {
			player.sendMessage("/" + label + " expand <town>");
		}
		return true;
	}

	private boolean performRenameCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws TownsystemException, EconomyPlayerException {
		if (args.length == 3) {
			Town town = townworldManager.getTownByName(args[1]);
			town.renameTown(args[2], ecoPlayer);
			townworldManager.performTownworldLocationCheckAllPlayers();
			player.sendMessage(messageWrapper.getString(MessageEnum.TOWN_RENAME, args[1], args[2]));
		} else {
			player.sendMessage("/" + label + " rename <old name> <new name>");
		}
		return true;
	}

	private boolean performSetTownSpawnCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws TownsystemException {
		if (args.length == 2) {
			Town town = townworldManager.getTownByName(args[1]);
			town.changeTownSpawn(player.getLocation(), ecoPlayer);
			player.sendMessage(
					messageWrapper.getString(MessageEnum.TOWN_SETTOWNSPAWN, (int) player.getLocation().getX(),
							(int) player.getLocation().getY(), (int) player.getLocation().getZ()));
		} else {
			player.sendMessage("/" + label + " setTownSpawn <town>");
		}
		return true;
	}

	private boolean performAddDeputyCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws TownsystemException, EconomyPlayerException {
		if (args.length == 3) {
			Town town = townworldManager.getTownByName(args[1]);
			townsystemValidationHandler.checkForPlayerIsMayor(town.getMayor(), ecoPlayer);
			town.addDeputy(ecoPlayerManager.getEconomyPlayerByName(args[2]));
			player.sendMessage(messageWrapper.getString(MessageEnum.ADDED, args[2]));
		} else {
			player.sendMessage("/" + label + " addDeputy <town> <player>");
		}
		return true;
	}

	private boolean performRemoveDeputyCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws TownsystemException, EconomyPlayerException {
		if (args.length == 3) {
			Town town = townworldManager.getTownByName(args[1]);
			townsystemValidationHandler.checkForPlayerIsMayor(town.getMayor(), ecoPlayer);
			town.removeDeputy(ecoPlayerManager.getEconomyPlayerByName(args[2]));
			player.sendMessage(messageWrapper.getString(MessageEnum.REMOVED, args[2]));
		} else {
			player.sendMessage("/" + label + " removeDeputy <town> <player>");
		}
		return true;
	}

	private boolean performMoveTownmanagerCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws TownsystemException {
		if (args.length == 1) {
			Townworld townworld = townworldManager.getTownWorldByName(player.getWorld().getName());
			townworld.getTownByChunk(player.getLocation().getChunk()).changeLocation(player.getLocation(), ecoPlayer);
		} else {
			player.sendMessage("/" + label + " moveTownManager");
		}
		return true;
	}

	private boolean performTpCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws TownsystemException {
		if (args.length == 2) {
			Town town = townworldManager.getTownByName(args[1]);
			player.teleport(town.getTownSpawn());
		} else {
			player.sendMessage("/" + label + " tp <town>");
		}
		return true;
	}

	private boolean performPayCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws NumberFormatException, TownsystemException, BankException, EconomyPlayerException {
		if (args.length == 3) {
			Town town = townworldManager.getTownByName(args[1]);
			double amount = Double.valueOf(args[2]);
			ecoPlayer.decreasePlayerAmount(amount, true);
			town.increaseTownBankAmount(amount);
			player.sendMessage(messageWrapper.getString(MessageEnum.TOWN_PAY, args[1], amount,
					configManager.getCurrencyText(amount)));
		} else {
			player.sendMessage("/" + label + " pay <town> <amount>");
		}
		return true;
	}

	private boolean performWithdrawCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws NumberFormatException, TownsystemException, BankException {
		if (args.length == 3) {
			Town town = townworldManager.getTownByName(args[1]);
			townsystemValidationHandler.checkForPlayerHasDeputyPermission(town.hasDeputyPermissions(ecoPlayer));
			double amount = Double.valueOf(args[2]);
			town.decreaseTownBankAmount(amount);
			ecoPlayer.increasePlayerAmount(amount, true);
		} else {
			player.sendMessage("/" + label + " withdraw <town> <amount>");
		}
		return true;
	}

	private boolean performBankCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws TownsystemException {
		if (args.length == 2) {
			Town town = townworldManager.getTownByName(args[1]);
			if (town.hasDeputyPermissions(ecoPlayer)) {
				player.sendMessage(messageWrapper.getString(MessageEnum.TOWN_BANK, town.getTownBankAmount(),
						configManager.getCurrencyText(town.getTownBankAmount())));
			}
		} else {
			player.sendMessage("/" + label + " bank <town>");
		}
		return true;
	}

	private boolean performPlotCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws TownsystemException {
		if (args.length > 1) {
			if (args[1].equals("setForSale")) {
				return performPlotSetForSaleCommand(label, args, player, ecoPlayer);
			} else if (args[1].equals("setForRent")) {
				return performPlotSetForRent(label, args, player, ecoPlayer);
			}
		} else {
			player.sendMessage("/" + label + " plot [setForSale/setForRent]");
		}
		return true;
	}

	private boolean performPlotSetForSaleCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws NumberFormatException, TownsystemException {
		if (args.length == 3) {
			Townworld townworld = townworldManager.getTownWorldByName(player.getWorld().getName());
			Town town = townworld.getTownByChunk(player.getLocation().getChunk());
			Plot plot = town.getPlotByChunk(
					player.getLocation().getChunk().getX() + "/" + player.getLocation().getChunk().getZ());
			plot.setForSale(Double.valueOf(args[2]), player.getLocation(), ecoPlayer);
			player.sendMessage(messageWrapper.getString(MessageEnum.TOWN_PLOT_SETFORSALE));
		} else {
			player.sendMessage("/" + label + " plot setForSale <price>");
		}
		return true;
	}

	private boolean performPlotSetForRent(String label, String[] args, Player player, EconomyPlayer ecoPlayer) {
		if (args.length == 3) {
			// TODO set for rent
		} else {
			player.sendMessage("/" + label + " plot setForRent <price/24h>");
		}
		return true;
	}
}
