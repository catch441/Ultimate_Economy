package com.ue.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Villager.Profession;

public class TabCompleterUtils {
	
	protected void addIfMatching(List<String> list, String command, String arg) {
		if (command.contains(arg)) {
			list.add(command);
		}
	}
	
	protected List<String> getProfessions(String arg) {
		List<String> list = new ArrayList<>();
		for (Profession profession : Profession.values()) {
			if (arg.isEmpty()) {
				list.add(profession.name().toLowerCase());
			} else if (profession.name().toLowerCase().contains(arg)) {
				list.add(profession.name().toLowerCase());
			}
		}
		return list;
	}

}
