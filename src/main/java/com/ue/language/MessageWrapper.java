package com.ue.language;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.ue.ultimate_economy.Ultimate_Economy;

public class MessageWrapper {
	
	private static ResourceBundle messages;
	//TODO add method to change language
	
	public static void loadLanguage() {
		Locale currentLocale;
		if (Ultimate_Economy.getInstance.getConfig().getString("localeLanguage") == null) {
			Ultimate_Economy.getInstance.getConfig().set("localeLanguage", "en");
			Ultimate_Economy.getInstance.getConfig().set("localeCountry", "US");
			currentLocale = new Locale("en", "US");
			Bukkit.getLogger().info("Loading default language file: 'en' 'US'");
		} else {
			String lang = Ultimate_Economy.getInstance.getConfig().getString("localeLanguage");
			String country = Ultimate_Economy.getInstance.getConfig().getString("localeCountry");
			currentLocale = new Locale(lang, country);
			Bukkit.getLogger().info("Loading language file: '" + lang + "' '" + country + "'");
		}
		messages = ResourceBundle.getBundle("language.MessagesBundle", currentLocale, new UTF8Control());
	}
	
	public static String getErrorString(String key) {
        try {
            return ChatColor.RED + messages.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
	
	public static String getErrorString(String key,Object... params) {
		try {
        	List<String> colored = new ArrayList<>();
        	for(Object object: params) {
        		colored.add("§4" + object.toString() + "§c");
        	}
        	String message = "§c" + messages.getString(key);
        	String newMessage = MessageFormat.format(message, colored.toArray());
            return ChatColor.translateAlternateColorCodes('§', newMessage);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static String getString(String key) {
        try {
            return ChatColor.GOLD + messages.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
    public static String getString(String key, Object... params ) {
        try {
        	List<String> colored = new ArrayList<>();
        	for(Object object: params) {
        		colored.add("§a" + object.toString() + "§6");
        	}
        	String message = "§6" + messages.getString(key);
        	String newMessage = MessageFormat.format(message, colored.toArray());
            return ChatColor.translateAlternateColorCodes('§', newMessage);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
