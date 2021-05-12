package org.ue.common.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.bukkit.ChatColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.api.MessageWrapper;

public class MessageWrapperImpl implements MessageWrapper {

	private static final Logger log = LoggerFactory.getLogger(MessageWrapperImpl.class);
	protected ResourceBundle messages;

	@Override
	public void loadLanguage(Locale locale) {
		log.info("[Ultimate_Economy] Loading language file: '" + locale.getLanguage() + "' '"
				+ locale.getCountry() + "'");
		messages = ResourceBundle.getBundle("language.MessagesBundle", locale, new UTF8Control());
	}
	
	@Override
	public Locale getLocale() {
		return messages.getLocale();
	}

	@Override
	public String getErrorString(ExceptionMessageEnum key) {
		try {
			return ChatColor.RED + messages.getString(key.getValue());
		} catch (MissingResourceException e) {
			return '!' + key.getValue() + '!';
		}
	}

	@Override
	public String getErrorString(ExceptionMessageEnum key, Object... params) {
		try {
			List<String> colored = new ArrayList<>();
			for (Object object : params) {
				if (object != null) {
					colored.add("§4" + object.toString() + "§c");
				} else {
					colored.add("§4null§c");
				}
			}
			String message = "§c" + messages.getString(key.getValue());
			message = message.replace("'", "''");
			String newMessage = MessageFormat.format(message, colored.toArray());
			return ChatColor.translateAlternateColorCodes('§', newMessage);
		} catch (MissingResourceException e) {
			return '!' + key.getValue() + '!';
		}
	}

	@Override
	public String getString(MessageEnum key) {
		try {
			return ChatColor.GOLD + messages.getString(key.getValue());
		} catch (MissingResourceException e) {
			return '!' + key.getValue() + '!';
		}
	}

	@Override
	public String getString(MessageEnum key, Object... params) {
		try {
			List<String> colored = new ArrayList<>();

			for (Object object : params) {
				if (object != null) {
					colored.add("§a" + object.toString() + "§6");
				} else {
					colored.add("§anull§6");
				}
			}
			String message = "§6" + messages.getString(key.getValue());
			message = message.replace("'", "''");
			String newMessage = MessageFormat.format(message, colored.toArray());
			return ChatColor.translateAlternateColorCodes('§', newMessage);
		} catch (MissingResourceException e) {
			return '!' + key.getValue() + '!';
		}
	}
}
