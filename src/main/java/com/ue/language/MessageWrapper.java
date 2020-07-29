package com.ue.language;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.ue.ultimate_economy.UltimateEconomy;

public class MessageWrapper {

	private static ResourceBundle messages;
	// TODO add method to change language

	/**
	 * Load the language from the save file. Loads default en US.
	 */
	public static void loadLanguage() {
		Locale currentLocale;
		if (UltimateEconomy.getInstance.getConfig().getString("localeLanguage") == null) {
			UltimateEconomy.getInstance.getConfig().set("localeLanguage", "en");
			UltimateEconomy.getInstance.getConfig().set("localeCountry", "US");
			currentLocale = new Locale("en", "US");
			Bukkit.getLogger().info("[Ultimate_Economy] Loading default language file: 'en' 'US'");
		} else {
			String lang = UltimateEconomy.getInstance.getConfig().getString("localeLanguage");
			String country = UltimateEconomy.getInstance.getConfig().getString("localeCountry");
			currentLocale = new Locale(lang, country);
			Bukkit.getLogger().info("[Ultimate_Economy] Loading language file: '" + lang + "' '" + country + "'");
		}
		messages = ResourceBundle.getBundle("language.MessagesBundle", currentLocale, new UTF8Control());
	}
	
	/**
	 * Returns the message bundle with the actiual language.
	 * @return messages as ResourceBundle
	 */
	public static ResourceBundle getMessages() {
		return messages;
	}

	/**
	 * Returns a error message with the chatcolor red.
	 * 
	 * @param key
	 * @return message
	 */
	public static String getErrorString(String key) {
		try {
			return ChatColor.RED + getMessages().getString(key);
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
	public static String getErrorString(String key, Object... params) {
		try {
			List<String> colored = new ArrayList<>();
			for (Object object : params) {
				if (object != null) {
					colored.add("§4" + object.toString() + "§c");
				} else {
					colored.add("§4null§c");
				}
			}
			String message = "§c" + getMessages().getString(key);
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
	public static String getString(String key) {
		try {
			return ChatColor.GOLD + getMessages().getString(key);
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
	public static String getString(String key, Object... params) {
		try {
			List<String> colored = new ArrayList<>();

			for (Object object : params) {
				if (object != null) {
					colored.add("§a" + object.toString() + "§6");
				} else {
					colored.add("§anull§6");
				}				
			}
			String message = "§6" + getMessages().getString(key);
			String newMessage = MessageFormat.format(message, colored.toArray());
			return ChatColor.translateAlternateColorCodes('§', newMessage);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
