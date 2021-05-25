package org.ue.common.utils;

import java.util.ArrayList;
import java.util.List;

public abstract class TabCompleterUtils {
	
	protected void addIfMatching(List<String> list, String command, String arg) {
		if (command.contains(arg)) {
			list.add(command);
		}
	}
	
	protected List<String> getMatchingList(List<String> fullList, String arg) {
		List<String> list = new ArrayList<>();
		for (String cmd : fullList) {
			addIfMatching(list, cmd, arg);
		}
		return list;
	}
	
	protected <T extends Enum<T>> List<String> getMatchingEnumList(Enum<? extends T>[] fullList, String arg) {
		List<String> list = new ArrayList<>();
		for (Enum<? extends T> value : fullList) {
			addIfMatching(list, value.name().toLowerCase(), arg);
		}
		return list;
	}
}
