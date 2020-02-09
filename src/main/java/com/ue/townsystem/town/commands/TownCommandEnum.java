package com.ue.townsystem.town.commands;

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

public enum TownCommandEnum {

    CREATE {
	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
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
    },
    DELETE {
	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
		throws TownSystemException, PlayerException, GeneralEconomyException {
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
    },
    EXPAND {
	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
		throws TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 2) {
		Townworld townworld = TownworldController.getTownWorldByName(player.getWorld().getName());
		townworld.getTownByName(args[1]).expandTown(player.getLocation().getChunk(), ecoPlayer, true);
	    } else {
		player.sendMessage("/" + label + " expand <town>");
	    }
	    return true;
	}
    },
    RENAME {
	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
		throws TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 3) {
		Townworld townworld = TownworldController.getTownWorldByName(player.getWorld().getName());
		townworld.getTownByName(args[1]).renameTown(args[2], ecoPlayer, true);
	    } else {
		player.sendMessage("/" + label + " rename <old name> <new name>");
	    }
	    return true;
	}
    },
    SETTOWNSPAWN {
	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
		throws TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 2) {
		Townworld tWorld = TownworldController.getTownWorldByName(player.getWorld().getName());
		tWorld.getTownByName(args[1]).setTownSpawn(player.getLocation(), ecoPlayer, true);
	    } else {
		player.sendMessage("/" + label + " setTownSpawn <town>");
	    }
	    return true;
	}
    },
    ADDDEPUTY {
	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
		throws TownSystemException, PlayerException, GeneralEconomyException {
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
    },
    REMOVEDEPUTY {
	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
		throws TownSystemException, PlayerException, GeneralEconomyException {
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
    },
    MOVETOWNMANAGER {
	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
		throws TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 1) {
		Townworld townworld = TownworldController.getTownWorldByName(player.getWorld().getName());
		townworld.getTownByChunk(player.getLocation().getChunk()).moveTownManagerVillager(player.getLocation(),
			ecoPlayer);
	    } else {
		player.sendMessage("/" + label + " moveTownManager");
	    }
	    return true;
	}
    },
    TP {
	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
		throws TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 2) {
		Townworld townworld = TownworldController.getTownWorldByName(args[1]);
		player.teleport(townworld.getTownByName(args[1]).getTownSpawn());
	    } else {
		player.sendMessage("/" + label + " tp <town>");
	    }
	    return true;
	}
    },
    PAY {
	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
		throws TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 3) {
		Townworld townworld = TownworldController.getTownWorldByName(args[1]);
		double amount = Double.valueOf(args[2]);
		ecoPlayer.decreasePlayerAmount(amount, true);
		townworld.getTownByName(args[1]).increaseTownBankAmount(amount);
		player.sendMessage(MessageWrapper.getString("town_pay", args[1], amount,
			ConfigController.getCurrencyText(amount)));
	    } else {
		player.sendMessage("/" + label + " pay <town> <amount>");
	    }
	    return true;
	}
    },
    WITHDRAW {
	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
		throws TownSystemException, PlayerException, GeneralEconomyException {
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
    },
    BANK {
	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
		throws TownSystemException, PlayerException, GeneralEconomyException {
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
    },
    PLOT {
	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
		throws TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length > 1) {
		if (args[1].equals("setForSale")) {
		    return PLOT_SETFORSALE.perform(label, args, player, ecoPlayer);
		} else if (args[1].equals("setForRent")) {
		    return PLOT_SETFORRENT.perform(label, args, player, ecoPlayer);
		}
	    } else {
		player.sendMessage("/" + label + " plot [setForSale/setForRent]");
	    }
	    return true;
	}
    },
    PLOT_SETFORSALE {
	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
		throws TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 4) {
		// TODO
	    } else {
		player.sendMessage("/" + label + " plot setForRent <town> <price/24h>");
	    }
	    return true;
	}

    },
    PLOT_SETFORRENT {

	@Override
	boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
		throws TownSystemException, PlayerException, GeneralEconomyException {
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

    };

    abstract boolean perform(String label, String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws TownSystemException, PlayerException, GeneralEconomyException;

    /**
     * Returns a enum. Return null, if no enum is found.
     * 
     * @param value
     * @return town command enum
     */
    public static TownCommandEnum getEnum(String value) {
	for (TownCommandEnum command : values()) {
	    if (command.name().equalsIgnoreCase(value)) {
		return command;
	    }
	}
	return null;
    }
}
