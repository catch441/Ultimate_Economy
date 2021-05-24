package org.ue.economyplayer.logic.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.ue.common.utils.TabCompleterUtils;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;

public class EconomyPlayerTabCompleterImpl extends TabCompleterUtils implements TabCompleter {

	private final EconomyPlayerManager ecoPlayerManager;
	
	public EconomyPlayerTabCompleterImpl(EconomyPlayerManager ecoPlayerManager) {
		this.ecoPlayerManager = ecoPlayerManager;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if (command.getName().equals("bank")) {
			if (args[0].equals("")) {
				list.add("on");
				list.add("off");
			} else if (args.length == 1) {
				addIfMatching(list, "on", args[0]);
				addIfMatching(list, "off", args[0]);
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
			List<String> homes = new ArrayList<String>(
					ecoPlayerManager.getEconomyPlayerByName(playerName).getHomeList().keySet());
			list = getMatchingList(homes, arg);
		} catch (EconomyPlayerException e) {
		}
		return list;
	}
}
