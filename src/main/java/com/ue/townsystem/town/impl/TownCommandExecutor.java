package com.ue.townsystem.town.impl;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.config.api.ConfigController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.townsystem.town.api.Plot;
import com.ue.townsystem.town.api.Town;
import com.ue.townsystem.town.api.TownController;
import com.ue.townsystem.townworld.api.Townworld;
import com.ue.townsystem.townworld.api.TownworldController;

public class TownCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (sender instanceof Player) {
	    Player player = (Player) sender;
	    try {
		EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName(player.getName());
		if (args.length != 0) {
		    switch (args[0]) {
		    case "create":
			return handleCreateCommand(label, args, player, ecoPlayer);
		    case "delete":
			return handleDeleteCommand(label, args, player, ecoPlayer);
		    case "expand":
			return handleExpandCommand(label, args, player, ecoPlayer);
		    case "rename":
			return handleRenameCommand(label, args, player, ecoPlayer);
		    case "setTownSpawn":
			return handleSetTownSpawnCommand(label, args, player, ecoPlayer);
		    case "addDeputy":
			return handleAddDeputyCommand(label, args, player, ecoPlayer);
		    case "removeDeputy":
			return handleRemoveDeputyCommand(label, args, player, ecoPlayer);
		    case "moveTownManager":
			return handleMoveTownManagerCommand(label, args, player, ecoPlayer);
		    case "tp":
			return handleTpCommand(label, args, player);
		    case "pay":
			return handlePayCommand(label, args, player, ecoPlayer);
		    case "withdraw":
			return handleWithdrawCommand(label, args, player, ecoPlayer);
		    case "bank":
			return handleBankCommand(label, args, player, ecoPlayer);
		    case "plot":
			return handlePlotCommand(label, args, player, ecoPlayer);
		    default:
			return false;
		    }
		} else {
		    return false;
		}
	    } catch (PlayerException | TownSystemException | GeneralEconomyException e) {
		player.sendMessage(e.getMessage());
	    } catch (NumberFormatException e) {
		player.sendMessage(MessageWrapper.getErrorString("invalid_parameter"));
	    }
	}
	return true;
    }

    private boolean handlePlotCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws TownSystemException, PlayerException {
	if (args.length > 1) {
	    if (args[1].equals("setForSale")) {
		handleSetForSaleCommand(label, args, player, ecoPlayer);
	    } else if (args[1].equals("setForRent")) {
		handleSetForRentCommand(label, args, player);
	    }
	} else {
	    player.sendMessage("/" + label + " plot [setForSale/setForRent]");
	}
	return true;
    }

    private void handleSetForRentCommand(String label, String[] args, Player player) {
	if (args.length == 4) {
	    // TODO
	} else {
	    player.sendMessage("/" + label + " plot setForRent <town> <price/24h>");
	}
    }

    private void handleSetForSaleCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws TownSystemException, PlayerException {
	if (args.length == 3) {
	    Townworld townworld = TownworldController.getTownWorldByName(player.getWorld().getName());
	    Town town = townworld.getTownByChunk(player.getLocation().getChunk());
	    Plot plot = town.getPlotByChunk(
		    player.getLocation().getChunk().getX() + "/" + player.getLocation().getChunk().getZ());
	    plot.setForSale(Double.valueOf(args[2]), player.getLocation(), ecoPlayer, true);
	} else {
	    player.sendMessage("/" + label + " plot setForSale <price>");
	}
    }

    private boolean handleBankCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws TownSystemException {
	if (args.length == 2) {
	    Townworld townworld = TownworldController.getTownWorldByName(args[1]);
	    Town town = townworld.getTownByName(args[1]);
	    if (town.hasDeputyPermissions(ecoPlayer)) {
		player.sendMessage(MessageWrapper.getString("town_bank", town.getTownBankAmount(),
			ConfigController.getCurrencyText(town.getTownBankAmount())));
	    }
	} else {
	    player.sendMessage("/" + label + " bank <town>");
	}
	return true;
    }

    private boolean handleWithdrawCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws TownSystemException, GeneralEconomyException, PlayerException {
	if (args.length == 3) {
	    Townworld townworld = TownworldController.getTownWorldByName(args[1]);
	    Town town = townworld.getTownByName(args[1]);
	    if (town.hasDeputyPermissions(ecoPlayer)) {
		double amount = Double.valueOf(args[2]);
		town.decreaseTownBankAmount(amount);
		ecoPlayer.increasePlayerAmount(amount, true);
	    } else {
		throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
	    }
	} else {
	    player.sendMessage("/" + label + " pay <town> <amount>");
	}
	return true;
    }

    private boolean handlePayCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws TownSystemException, GeneralEconomyException, PlayerException {
	if (args.length == 3) {
	    Townworld townworld = TownworldController.getTownWorldByName(args[1]);
	    double amount = Double.valueOf(args[2]);
	    ecoPlayer.decreasePlayerAmount(amount, true);
	    townworld.getTownByName(args[1]).increaseTownBankAmount(amount);
	    player.sendMessage(
		    MessageWrapper.getString("town_pay", args[1], amount, ConfigController.getCurrencyText(amount)));
	} else {
	    player.sendMessage("/" + label + " pay <town> <amount>");
	}
	return true;
    }

    private boolean handleTpCommand(String label, String[] args, Player player) throws TownSystemException {
	if (args.length == 2) {
	    Townworld townworld = TownworldController.getTownWorldByName(args[1]);
	    player.teleport(townworld.getTownByName(args[1]).getTownSpawn());
	} else {
	    player.sendMessage("/" + label + " tp <town>");
	}
	return true;
    }

    private boolean handleRemoveDeputyCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws TownSystemException, PlayerException {
	if (args.length == 3) {
	    Townworld world = TownworldController.getTownWorldByName(args[1]);
	    Town town = world.getTownByName(args[1]);
	    if (!town.isMayor(ecoPlayer)) {
		throw PlayerException.getException(PlayerExceptionMessageEnum.TOWN_NOT_TOWN_OWNER);
	    } else {
		town.removeDeputy(EconomyPlayerController.getEconomyPlayerByName(args[2]));
		player.sendMessage(MessageWrapper.getString("town_removeCoOwner", args[2]));
	    }

	} else {
	    player.sendMessage("/" + label + " removeDeputy <town> <player>");
	}
	return true;
    }

    private boolean handleAddDeputyCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws TownSystemException, PlayerException {
	if (args.length == 3) {
	    Townworld world = TownworldController.getTownWorldByName(args[1]);
	    Town town = world.getTownByName(args[1]);
	    if (!town.isMayor(ecoPlayer)) {
		throw PlayerException.getException(PlayerExceptionMessageEnum.TOWN_NOT_TOWN_OWNER);
	    } else {
		town.addDeputy(EconomyPlayerController.getEconomyPlayerByName(args[2]));
		player.sendMessage(MessageWrapper.getString("town_addCoOwner", args[2]));
	    }
	} else {
	    player.sendMessage("/" + label + " addDeputy <town> <player>");
	}
	return true;
    }

    private boolean handleMoveTownManagerCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws TownSystemException, PlayerException {
	if (args.length == 1) {
	    Townworld townworld = TownworldController.getTownWorldByName(player.getWorld().getName());
	    townworld.getTownByChunk(player.getLocation().getChunk()).moveTownManagerVillager(player.getLocation(),
		    ecoPlayer);
	} else {
	    player.sendMessage("/" + label + " moveTownManager");
	}
	return true;
    }

    private boolean handleExpandCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws TownSystemException, PlayerException {
	if (args.length == 2) {
	    Townworld townworld = TownworldController.getTownWorldByName(player.getWorld().getName());
	    townworld.getTownByName(args[1]).expandTown(player.getLocation().getChunk(), ecoPlayer, true);
	} else {
	    player.sendMessage("/" + label + " expand <town>");
	}
	return true;
    }

    private boolean handleCreateCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws TownSystemException, PlayerException, GeneralEconomyException {
	if (args.length == 2) {
	    Townworld townworld = TownworldController.getTownWorldByName(player.getWorld().getName());
	    TownController.createTown(townworld, args[1], player.getLocation(), ecoPlayer);
	    player.sendMessage(MessageWrapper.getString("town_create", args[1]));
	} else {
	    player.sendMessage("/" + label + " create <town>");
	}
	return true;
    }

    private boolean handleSetTownSpawnCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws TownSystemException, PlayerException {
	if (args.length == 2) {
	    Townworld tWorld = TownworldController.getTownWorldByName(player.getWorld().getName());
	    tWorld.getTownByName(args[1]).setTownSpawn(player.getLocation(), ecoPlayer, true);
	} else {
	    player.sendMessage("/" + label + " setTownSpawn <town>");
	}
	return true;
    }

    private boolean handleDeleteCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws TownSystemException, PlayerException {
	if (args.length == 2) {
	    Townworld townworld = TownworldController.getTownWorldByName(player.getWorld().getName());
	    Town town = townworld.getTownByName(args[1]);
	    TownController.dissolveTown(town, ecoPlayer);
	    player.sendMessage(MessageWrapper.getString("town_delete", args[1]));
	} else {
	    player.sendMessage("/" + label + " delete <town>");
	}
	return true;
    }

    private boolean handleRenameCommand(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws TownSystemException, PlayerException {
	if (args.length == 3) {
	    Townworld townworld = TownworldController.getTownWorldByName(player.getWorld().getName());
	    townworld.getTownByName(args[1]).renameTown(args[2], ecoPlayer, true);
	} else {
	    player.sendMessage("/" + label + " rename <old name> <new name>");
	}
	return true;
    }
}
