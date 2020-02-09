package com.ue.shopsystem.commands.playershop;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.shopsystem.controller.PlayershopController;

public enum PlayershopCommandEnum {

    CREATE {
	@Override
	boolean perform(String label, String[] args, Player player)
		throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 3) {
		PlayershopController.createPlayerShop(args[1],
			player.getLocation(), Integer.valueOf(args[2]),
			EconomyPlayerController.getEconomyPlayerByName(player.getName()));
		player.sendMessage(MessageWrapper.getString("shop_create", args[1]));
	    } else {
		player.sendMessage("/" + label + " create <shop> <size> <- size have to be a multible of 9");
	    }
	    return true;
	}
    },
    DELETE {
	@Override
	boolean perform(String label, String[] args, Player player)
		throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 2) {
		PlayershopController.deletePlayerShop(
			PlayershopController.getPlayerShopByUniqueName(args[1] + "_" + player.getName()));
		player.sendMessage(MessageWrapper.getString("shop_delete", args[1]));
	    } else {
		player.sendMessage("/" + label + " delete <shop>");
	    }
	    return true;
	}
    },
    DELETE_OTHER {
	@Override
	boolean perform(String label, String[] args, Player player)
		throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
	    if (player.hasPermission("ultimate_economy.adminshop")) {
		if (args.length == 2) {
		    PlayershopController.deletePlayerShop(PlayershopController.getPlayerShopByUniqueName(args[1]));
		    player.sendMessage(MessageWrapper.getString("shop_delete", args[1]));
		} else {
		    player.sendMessage("/" + label + " deleteOther <shop>");
		}
	    }
	    return true;
	}
    },
    RENAME {
	@Override
	boolean perform(String label, String[] args, Player player)
		throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 3) {
		PlayershopController.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
			.changeShopName(args[2]);
		player.sendMessage(MessageWrapper.getString("shop_rename", args[1], args[2]));
	    } else {
		player.sendMessage("/" + label + " rename <oldName> <newName>");
	    }
	    return true;
	}
    },
    RESIZE {
	@Override
	boolean perform(String label, String[] args, Player player)
		throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 3) {
		PlayershopController.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
			.changeShopSize(Integer.valueOf(args[2]));
		player.sendMessage(MessageWrapper.getString("shop_resize", args[2]));
	    } else {
		player.sendMessage("/" + label + " resize <shop> <new size>");
	    }
	    return true;
	}
    },
    MOVE {
	@Override
	boolean perform(String label, String[] args, Player player)
		throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 2) {
		PlayershopController.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
			.moveShop(player.getLocation());
	    } else {
		player.sendMessage("/" + label + " move <shop>");
	    }
	    return true;
	}
    },
    CHANGE_OWNER {
	@Override
	boolean perform(String label, String[] args, Player player)
		throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 3) {
		EconomyPlayer newOwner = EconomyPlayerController.getEconomyPlayerByName(args[2]);
		PlayershopController.getPlayerShopByUniqueName(args[1] + "_" + player.getName()).changeOwner(newOwner);
		player.sendMessage(MessageWrapper.getString("shop_changeOwner1", args[2]));
		if (newOwner.isOnline()) {
		    newOwner.getPlayer()
			    .sendMessage(MessageWrapper.getString("shop_changeOwner", args[1], player.getName()));
		}
	    } else {
		player.sendMessage("/" + label + " changeOwner <shop> <new owner>");
	    }
	    return true;
	}
    },
    CHANGEPROFESSION {
	@Override
	boolean perform(String label, String[] args, Player player)
		throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 3) {
		PlayershopController.getPlayerShopByUniqueName(args[1] + "_" + player.getName())
			.changeProfession(Profession.valueOf(args[2].toUpperCase()));
		player.sendMessage(MessageWrapper.getString("profession_changed"));
	    } else {
		player.sendMessage("/" + label + " changeProfession <shop> <profession>");
	    }
	    return true;
	}
    },
    EDIT_SHOP {
	@Override
	boolean perform(String label, String[] args, Player player)
		throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException {
	    if (args.length == 2) {
		PlayershopController.getPlayerShopByUniqueName(args[1] + "_" + player.getName()).openEditor(player);
	    } else {
		player.sendMessage("/" + label + " editShop <shop>");
	    }
	    return true;
	}
    };

    abstract boolean perform(String label, String[] args, Player player)
	    throws ShopSystemException, TownSystemException, PlayerException, GeneralEconomyException;

    /**
     * Returns a enum. Return null, if no enum is found.
     * 
     * @param value
     * @return playershop command enum
     */
    public static PlayershopCommandEnum getEnum(String value) {
	for (PlayershopCommandEnum command : values()) {
	    if (command.name().equalsIgnoreCase(value)) {
		return command;
	    }
	}
	return null;
    }
}
