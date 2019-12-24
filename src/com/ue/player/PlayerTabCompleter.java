package com.ue.player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.ue.exceptions.PlayerException;

public class PlayerTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		 if (command.getName().equals("bank")) {
			if (args[0].equals("")) {
				list.add("on");
				list.add("off");
			} else if (args.length == 1) {
				if ("on".contains(args[0])) {
					list.add("on");
				}
				if ("off".contains(args[0])) {
					list.add("off");
				}
			}
		} else if (command.getName().equals("delHome") || command.getName().equals("home")) {
				if (args.length <= 1) {
					list = getHomeList(args[0], sender.getName());
				}
			}
		return list;
	}
	
	private List<String> getHomeList(String arg, String playerName) {
		List<String> list = new ArrayList<>();
		try {
			List<String> temp = new ArrayList<String>(
					EconomyPlayer.getEconomyPlayerByName(playerName).getHomeList().keySet());
			if (arg.equals("")) {
				list = temp;
			} else {
				for (String homeName : temp) {
					if (homeName.contains(arg)) {
						list.add(homeName);
					}
				}
			}
		} catch (PlayerException e) {
			Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
		}
		return list;
	}
}
