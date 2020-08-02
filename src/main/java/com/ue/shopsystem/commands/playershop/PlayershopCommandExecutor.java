package com.ue.shopsystem.commands.playershop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;

public class PlayershopCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				if (args.length != 0) {
					return PlayershopCommandEnum.getEnum(args[0]).perform(label, args, player);
				}
				return false;
			} catch (TownSystemException | EconomyPlayerException | ShopSystemException | GeneralEconomyException e) {
				player.sendMessage(e.getMessage());
			} catch (IllegalArgumentException e) {
				player.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[2]));
			}
		}
		return true;
	}
}
