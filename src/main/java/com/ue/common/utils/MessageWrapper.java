package com.ue.common.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.bukkit.ChatColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageWrapper {

	private static final Logger log = LoggerFactory.getLogger(MessageWrapper.class);
	protected ResourceBundle messages;

	/**
	 * Load the language from the config.
	 * @param locale
	 */
	public void loadLanguage(Locale locale) {
		log.info("[Ultimate_Economy] Loading language file: '" + locale.getLanguage() + "' '"
				+ locale.getCountry() + "'");
		messages = ResourceBundle.getBundle("language.MessagesBundle", locale, new UTF8Control());
	}

	/**
	 * Returns a error message with the chatcolor red.
	 * 
	 * @param key
	 * @return message
	 */
	public String getErrorString(String key) {
		try {
			return ChatColor.RED + messages.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * Returns a error message with the chatcolor red.
	 * 
	 * @param key
	 * @param params
	 * @return message
	 */
	public String getErrorString(String key, Object... params) {
		try {
			List<String> colored = new ArrayList<>();
			for (Object object : params) {
				if (object != null) {
					colored.add("§4" + object.toString() + "§c");
				} else {
					colored.add("§4null§c");
				}
			}
			String message = "§c" + messages.getString(key);
			message = message.replace("'", "''");
			String newMessage = MessageFormat.format(message, colored.toArray());
			return ChatColor.translateAlternateColorCodes('§', newMessage);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * Returns a message with chatcolor gold.
	 * 
	 * @param key
	 * @return messgage
	 */
	public String getString(String key) {
		try {
			return ChatColor.GOLD + messages.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * Returns a message with chatcolor gold and green.
	 * 
	 * @param key
	 * @param params
	 * @return message
	 */
	public String getString(String key, Object... params) {
		try {
			List<String> colored = new ArrayList<>();

			for (Object object : params) {
				if (object != null) {
					colored.add("§a" + object.toString() + "§6");
				} else {
					colored.add("§anull§6");
				}
			}
			String message = "§6" + messages.getString(key);
			message = message.replace("'", "''");
			String newMessage = MessageFormat.format(message, colored.toArray());
			return ChatColor.translateAlternateColorCodes('§', newMessage);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
