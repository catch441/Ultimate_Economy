package com.ue.townsystem.commands;

import javax.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerExceptionMessageEnum;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.exceptions.TownSystemException;
import com.ue.townsystem.api.Plot;
import com.ue.townsystem.api.Town;
import com.ue.townsystem.api.TownController;
import com.ue.townsystem.api.Townworld;
import com.ue.townsystem.api.TownworldController;
import com.ue.ultimate_economy.GeneralEconomyException;

public class TownCommandExecutor implements CommandExecutor {

	@Inject
	ConfigManager configManager;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getEconomyPlayerByName(player.getName());
				if (args.length != 0) {
					return performCommand(label, args, player, ecoPlayer);
				}
			} catch (EconomyPlayerException | TownSystemException | GeneralEconomyException e) {
				player.sendMessage(e.getMessage());
			} catch (NumberFormatException e) {
				player.sendMessage(MessageWrapper.getErrorString("invalid_parameter"));
			}
		}
		return true;
	}

	private boolean performCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws GeneralEconomyException, EconomyPlayerException, TownSystemException {
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
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		if (args.length == 2) {
			Townworld townworld = TownworldController.getTownWorldByName(player.getWorld().getName());
			TownController.createTown(townworld, args[1], player.getLocation(), ecoPlayer);
			player.sendMessage(MessageWrapper.getString("town_create", args[1]));
		} else {
			player.sendMessage("/" + label + " create <town>");
		}
		return true;
	}

	private boolean performDeleteCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws GeneralEconomyException, TownSystemException, EconomyPlayerException {
		if (args.length == 2) {
			Town town = TownController.getTown(args[1]);
			TownController.dissolveTown(town, ecoPlayer);
			player.sendMessage(MessageWrapper.getString("town_delete", args[1]));
		} else {
			player.sendMessage("/" + label + " delete <town>");
		}
		return true;
	}

	private boolean performExpandCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws GeneralEconomyException, TownSystemException, EconomyPlayerException {
		if (args.length == 2) {
			Town town = TownController.getTown(args[1]);
			town.expandTown(player.getLocation().getChunk(), ecoPlayer, true);
		} else {
			player.sendMessage("/" + label + " expand <town>");
		}
		return true;
	}

	private boolean performRenameCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws GeneralEconomyException, TownSystemException, EconomyPlayerException {
		if (args.length == 3) {
			Town town = TownController.getTown(args[1]);
			town.renameTown(args[2], ecoPlayer, true);
		} else {
			player.sendMessage("/" + label + " rename <old name> <new name>");
		}
		return true;
	}

	private boolean performSetTownSpawnCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws GeneralEconomyException, TownSystemException, EconomyPlayerException {
		if (args.length == 2) {
			Town town = TownController.getTown(args[1]);
			town.changeTownSpawn(player.getLocation(), ecoPlayer, true);
		} else {
			player.sendMessage("/" + label + " setTownSpawn <town>");
		}
		return true;
	}

	private boolean performAddDeputyCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws GeneralEconomyException, EconomyPlayerException, TownSystemException {
		if (args.length == 3) {
			Town town = TownController.getTown(args[1]);
			if (!town.isMayor(ecoPlayer)) {
				throw EconomyPlayerException.getException(EconomyPlayerExceptionMessageEnum.TOWN_NOT_TOWN_OWNER);
			} else {
				town.addDeputy(EconomyPlayerManagerImpl.getEconomyPlayerByName(args[2]));
				player.sendMessage(MessageWrapper.getString("town_addCoOwner", args[2]));
			}
		} else {
			player.sendMessage("/" + label + " addDeputy <town> <player>");
		}
		return true;
	}

	private boolean performRemoveDeputyCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws GeneralEconomyException, EconomyPlayerException, TownSystemException {
		if (args.length == 3) {
			Town town = TownController.getTown(args[1]);
			if (!town.isMayor(ecoPlayer)) {
				throw EconomyPlayerException.getException(EconomyPlayerExceptionMessageEnum.TOWN_NOT_TOWN_OWNER);
			} else {
				town.removeDeputy(EconomyPlayerManagerImpl.getEconomyPlayerByName(args[2]));
				player.sendMessage(MessageWrapper.getString("town_removeCoOwner", args[2]));
			}

		} else {
			player.sendMessage("/" + label + " removeDeputy <town> <player>");
		}
		return true;
	}

	private boolean performMoveTownmanagerCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws TownSystemException, EconomyPlayerException {
		if (args.length == 1) {
			Townworld townworld = TownworldController.getTownWorldByName(player.getWorld().getName());
			townworld.getTownByChunk(player.getLocation().getChunk()).moveTownManagerVillager(player.getLocation(),
					ecoPlayer);
		} else {
			player.sendMessage("/" + label + " moveTownManager");
		}
		return true;
	}

	private boolean performTpCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws GeneralEconomyException {
		if (args.length == 2) {
			Town town = TownController.getTown(args[1]);
			player.teleport(town.getTownSpawn());
		} else {
			player.sendMessage("/" + label + " tp <town>");
		}
		return true;
	}

	private boolean performPayCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws GeneralEconomyException, EconomyPlayerException {
		if (args.length == 3) {
			Town town = TownController.getTown(args[1]);
			double amount = Double.valueOf(args[2]);
			ecoPlayer.decreasePlayerAmount(amount, true);
			town.increaseTownBankAmount(amount);
			player.sendMessage(
					MessageWrapper.getString("town_pay", args[1], amount, configManager.getCurrencyText(amount)));
		} else {
			player.sendMessage("/" + label + " pay <town> <amount>");
		}
		return true;
	}

	private boolean performWithdrawCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws EconomyPlayerException, GeneralEconomyException, TownSystemException {
		if (args.length == 3) {
			Town town = TownController.getTown(args[1]);
			if (town.hasDeputyPermissions(ecoPlayer)) {
				double amount = Double.valueOf(args[2]);
				town.decreaseTownBankAmount(amount);
				ecoPlayer.increasePlayerAmount(amount, true);
			} else {
				throw EconomyPlayerException.getException(EconomyPlayerExceptionMessageEnum.NO_PERMISSION);
			}
		} else {
			player.sendMessage("/" + label + " pay <town> <amount>");
		}
		return true;
	}

	private boolean performBankCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws GeneralEconomyException {
		if (args.length == 2) {
			Town town = TownController.getTown(args[1]);
			if (town.hasDeputyPermissions(ecoPlayer)) {
				player.sendMessage(MessageWrapper.getString("town_bank", town.getTownBankAmount(),
						configManager.getCurrencyText(town.getTownBankAmount())));
			}
		} else {
			player.sendMessage("/" + label + " bank <town>");
		}
		return true;
	}

	private boolean performPlotCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
			throws NumberFormatException, TownSystemException, EconomyPlayerException {
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
			throws TownSystemException, NumberFormatException, EconomyPlayerException {
		if (args.length == 3) {
			Townworld townworld = TownworldController.getTownWorldByName(player.getWorld().getName());
			Town town = townworld.getTownByChunk(player.getLocation().getChunk());
			Plot plot = town.getPlotByChunk(
					player.getLocation().getChunk().getX() + "/" + player.getLocation().getChunk().getZ());
			plot.setForSale(Double.valueOf(args[2]), player.getLocation(), ecoPlayer, true);
		} else {
			player.sendMessage("/" + label + " plot setForSale <price>");
		}
		return true;
	}

	private boolean performPlotSetForRent(String label, String[] args, Player player, EconomyPlayer ecoPlayer) {
		if (args.length == 4) {
			// TODO
		} else {
			player.sendMessage("/" + label + " plot setForRent <town> <price/24h>");
		}
		return true;
	}
}
