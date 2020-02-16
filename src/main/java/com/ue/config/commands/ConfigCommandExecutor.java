package com.ue.config.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.language.MessageWrapper;

public class ConfigCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (args.length != 0) {
	    try {
		ConfigCommandEnum commandEnum = ConfigCommandEnum.getEnum(args[0]);
		if(commandEnum != null) {
		    return commandEnum.perform(label, args, sender);
		}
		return false;
	    } catch (GeneralEconomyException e) {
		sender.sendMessage(ChatColor.RED + e.getMessage());
	    } catch (IllegalArgumentException e) {
		sender.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[1]));
	    }
	} else {
	    return false;
	}
	return true;
    }
}
