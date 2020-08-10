package com.ue.economyplayer.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.ue.economyplayer.logic.api.EconomyPlayerManager;

public class EconomyPlayerTabCompleterImpl implements TabCompleter {

	@Inject
	EconomyPlayerManager ecoPlayerManager;

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
		} else if (command.getName().equals("delhome") || command.getName().equals("home")) {
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
					ecoPlayerManager.getEconomyPlayerByName(playerName).getHomeList().keySet());
			if ("".equals(arg)) {
				list = temp;
			} else {
				for (String homeName : temp) {
					if (homeName.contains(arg)) {
						list.add(homeName);
					}
				}
			}
		} catch (EconomyPlayerException e) {
		}
		return list;
	}
}
