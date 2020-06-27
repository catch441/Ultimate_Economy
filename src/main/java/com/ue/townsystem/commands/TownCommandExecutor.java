package com.ue.townsystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;

public class TownCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (sender instanceof Player) {
	    Player player = (Player) sender;
	    try {
		EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName(player.getName());
		if (args.length != 0) {
		    TownCommandEnum commandEnum = TownCommandEnum.getEnum(args[0]);
		    if(commandEnum != null) {
			return commandEnum.perform(label, args, player, ecoPlayer);
		    }
		}
		return false;
	    } catch (PlayerException | TownSystemException | GeneralEconomyException e) {
		player.sendMessage(e.getMessage());
	    } catch (NumberFormatException e) {
		player.sendMessage(MessageWrapper.getErrorString("invalid_parameter"));
	    }
	}
	return true;
    }
}
