package com.ue.jobsystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;

public class JobCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				if (args.length != 0) {
					JobCommandEnum commandEnum = JobCommandEnum.getEnum(args[0]);
					if (commandEnum != null) {
						return commandEnum.perform(label, args, player);
					}
				}
				return false;
			} catch (JobSystemException | EconomyPlayerException | GeneralEconomyException e) {
				player.sendMessage(e.getMessage());
			} catch (NumberFormatException e) {
				player.sendMessage(MessageWrapper.getErrorString("invalid_parameter", "number"));
			}
		}
		return true;
	}
}
