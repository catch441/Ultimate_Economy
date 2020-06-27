package com.ue.economyplayer.commands;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.api.Job;
import com.ue.language.MessageWrapper;
import com.ue.townsystem.api.TownworldController;

public enum PlayerCommandEnum {

    BANK {
	@Override
	boolean perform(String[] args, Player player, EconomyPlayer ecoPlayer) {
	    if (args.length == 1) {
		if (args[0].equals("on") || args[0].equals("off")) {
		    if (args[0].equals("on")) {
			ecoPlayer.setScoreBoardDisabled(false);
		    } else {
			ecoPlayer.setScoreBoardDisabled(true);
		    }
		} else {
		    return false;
		}
	    } else {
		return false;
	    }
	    return true;
	}
    },
    MONEY {
	@Override
	boolean perform(String[] args, Player player, EconomyPlayer ecoPlayer) throws PlayerException {
	    DecimalFormat dFormat = new DecimalFormat(".##");
	    dFormat.setRoundingMode(RoundingMode.DOWN);
	    if (args.length == 0) {
		player.sendMessage(
			MessageWrapper.getString("money_info", dFormat.format(ecoPlayer.getBankAccount().getAmount()),
				ConfigController.getCurrencyText(ecoPlayer.getBankAccount().getAmount())));
	    } else if (args.length == 1 && player.hasPermission("Ultimate_Economy.adminpay")) {
		EconomyPlayer otherPlayer = EconomyPlayerController.getEconomyPlayerByName(args[0]);
		player.sendMessage(
			MessageWrapper.getString("money_info", dFormat.format(otherPlayer.getBankAccount().getAmount()),
				ConfigController.getCurrencyText(otherPlayer.getBankAccount().getAmount())));
	    } else if (player.hasPermission("Ultimate_Economy.adminpay")) {
		player.sendMessage("/money or /money <player>");
	    } else {
		return false;
	    }
	    return true;
	}
    },
    MYJOBS {
	@Override
	boolean perform(String[] args, Player player, EconomyPlayer ecoPlayer) {
	    if (args.length == 0) {
		List<Job> jobs = ecoPlayer.getJobList();
		List<String> jobNames = new ArrayList<>();
		for (Job job : jobs) {
		    jobNames.add(job.getName());
		}
		player.sendMessage(MessageWrapper.getString("myjobs_info", jobNames.toString()));
	    } else {
		return false;
	    }
	    return true;
	}
    },
    HOME {
	@Override
	boolean perform(String[] args, Player player, EconomyPlayer ecoPlayer) throws PlayerException {
	    if (args.length == 1) {
		Location location = ecoPlayer.getHome(args[0]);
		player.teleport(location);
		TownworldController.handleTownWorldLocationCheck(player.getWorld().getName(),
			player.getLocation().getChunk(), player.getName());
	    } else if (args.length == 0) {
		Object[] homes = ecoPlayer.getHomeList().keySet().toArray();
		player.sendMessage(MessageWrapper.getString("home_info", homes));
	    }
	    return true;
	}
    },
    SETHOME {
	@Override
	boolean perform(String[] args, Player player, EconomyPlayer ecoPlayer) throws PlayerException {
	    if (args.length == 1) {
		ecoPlayer.addHome(args[0], player.getLocation(), true);
	    } else {
		return false;
	    }
	    return true;
	}
    },
    DELHOME {
	@Override
	boolean perform(String[] args, Player player, EconomyPlayer ecoPlayer) throws PlayerException {
	    if (args.length == 1) {
		ecoPlayer.removeHome(args[0], true);
	    } else {
		return false;
	    }
	    return true;
	}
    },
    PAY {
	@Override
	boolean perform(String[] args, Player player, EconomyPlayer ecoPlayer)
		throws NumberFormatException, GeneralEconomyException, PlayerException {
	    if (args.length == 2) {
		ecoPlayer.payToOtherPlayer(EconomyPlayerController.getEconomyPlayerByName(args[0]),
			Double.valueOf(args[1]), true);
	    } else {
		return false;
	    }
	    return true;
	}
    },
    GIVEMONEY {
	@Override
	boolean perform(String[] args, Player player, EconomyPlayer ecoPlayer)
		throws PlayerException, NumberFormatException, GeneralEconomyException {
	    if (args.length == 2) {
		double amount = Double.valueOf(args[1]);
		EconomyPlayer receiver = EconomyPlayerController.getEconomyPlayerByName(args[0]);
		if (amount < 0) {
		    receiver.decreasePlayerAmount(-amount, false);
		} else {
		    receiver.increasePlayerAmount(amount, true);
		}
	    } else {
		return false;
	    }
	    return true;
	}
    };

    abstract boolean perform(String[] args, Player player, EconomyPlayer ecoPlayer)
	    throws PlayerException, NumberFormatException, GeneralEconomyException;

    /**
     * Returns a enum. Return null, if no enum is found.
     * 
     * @param value
     * @return player command enum
     */
    public static PlayerCommandEnum getEnum(String value) {
	for (PlayerCommandEnum command : values()) {
	    if (command.name().equalsIgnoreCase(value)) {
		return command;
	    }
	}
	return null;
    }
}
