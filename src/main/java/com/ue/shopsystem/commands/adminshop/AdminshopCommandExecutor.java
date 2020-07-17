package com.ue.shopsystem.commands.adminshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;

public class AdminshopCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				if (args.length != 0) {
					return AdminshopCommandEnum.getEnum(args[0]).perform(label, args, player);
				}
				return false;
			} catch (TownSystemException | ShopSystemException | PlayerException | GeneralEconomyException e) {
				player.sendMessage(e.getMessage());
			} catch (NumberFormatException e) {
				player.sendMessage(MessageWrapper.getErrorString("invalid_parameter", "number"));
			}
		}
		return true;
	}
}
