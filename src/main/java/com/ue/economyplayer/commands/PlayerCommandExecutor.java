package com.ue.economyplayer.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.language.MessageWrapper;

public class PlayerCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	try {
	    if ("givemoney".equals(label)) {
		return PlayerCommandEnum.GIVEMONEY.perform(args, null, null);
	    } else if (sender instanceof Player) {
		Player player = (Player) sender;
		EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName(player.getName());
		PlayerCommandEnum commandEnum = PlayerCommandEnum.getEnum(label);
		if (commandEnum != null) {
		    return commandEnum.perform(args, player, ecoPlayer);
		}
		return false;
	    }
	} catch (PlayerException | GeneralEconomyException e) {
	    sender.sendMessage(e.getMessage());
	} catch (NumberFormatException e) {
	    sender.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[1]));
	}
	return true;
    }
}
