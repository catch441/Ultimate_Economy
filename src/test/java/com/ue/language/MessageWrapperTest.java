package com.ue.language;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ResourceBundle;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.common.utils.MessageWrapper;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;

public class MessageWrapperTest {

	/**
	 * Init for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
		MockBukkit.load(UltimateEconomy.class);
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		UltimateEconomy.getInstance.getDataFolder().delete();
		MockBukkit.unload();
	}

	@AfterEach
	public void resetLanguage() {
		UltimateEconomy.getInstance.getConfig().set("localeLanguage", "en");
		UltimateEconomy.getInstance.getConfig().set("localeCountry", "US");
		UltimateEconomy.getInstance.saveConfig();
		MessageWrapper.loadLanguage();
	}

	@Test
	public void loadLanguageTestWithNoConfig() {
		UltimateEconomy.getInstance.getConfig().set("localeLanguage", null);
		UltimateEconomy.getInstance.saveConfig();
		MessageWrapper.loadLanguage();
		ResourceBundle bundle = MessageWrapper.getMessages();
		assertEquals("language.MessagesBundle", bundle.getBaseBundleName());
		assertEquals("US", bundle.getLocale().getCountry());
		assertEquals("en", bundle.getLocale().getLanguage());
	}

	@Test
	public void loadLanguageTestWithConfig() {
		UltimateEconomy.getInstance.getConfig().set("localeLanguage", "de");
		UltimateEconomy.getInstance.getConfig().set("localeCountry", "DE");
		UltimateEconomy.getInstance.saveConfig();
		MessageWrapper.loadLanguage();
		ResourceBundle bundle = MessageWrapper.getMessages();
		assertEquals("language.MessagesBundle", bundle.getBaseBundleName());
		assertEquals("DE", bundle.getLocale().getCountry());
		assertEquals("de", bundle.getLocale().getLanguage());
	}

	@Test
	public void getErrorStringTest() {
		String message = MessageWrapper.getErrorString("restart");
		assertEquals("§cPlease restart the server!", message);
	}

	@Test
	public void getErrorStringTestWithMissingMessage() {
		String message = MessageWrapper.getErrorString("kthschnll");
		assertEquals("!kthschnll!", message);
	}

	@Test
	public void getStringTest() {
		String message = MessageWrapper.getString("restart");
		assertEquals("§6Please restart the server!", message);
	}

	@Test
	public void getStringTestWithMissingMessage() {
		String message = MessageWrapper.getString("kthschnll");
		assertEquals("!kthschnll!", message);
	}

	@Test
	public void getErrorStringTestWithParams() {
		String message = MessageWrapper.getErrorString("got_money_with_sender", "10", "kth", "kthschnll");
		assertEquals("§cYou got §410§c §4kth§c from §4kthschnll§c.", message);
	}
	
	@Test
	public void getErrorStringTestWithNullParams() {
		String message = MessageWrapper.getErrorString("got_money_with_sender", null, null, null);
		assertEquals("§cYou got §4null§c §4null§c from §4null§c.", message);
	}
	
	@Test
	public void getErrorStringTestWithParamsAndMissingMessage() {
		String message = MessageWrapper.getErrorString("kthschnll", "10", "kth", "kthschnll");
		assertEquals("!kthschnll!", message);
	}
	
	@Test
	public void getStringTestWithParams() {
		String message = MessageWrapper.getString("got_money_with_sender", "10", "kth", "kthschnll");
		assertEquals("§6You got §a10§6 §akth§6 from §akthschnll§6.", message);
	}
	
	@Test
	public void getStringTestWithParamsAndMissingMessage() {
		String message = MessageWrapper.getString("kthschnll", "10", "kth", "kthschnll");
		assertEquals("!kthschnll!", message);
	}
	
	@Test
	public void getStringTestWithNullParams() {
		String message = MessageWrapper.getString("got_money_with_sender", null, null, null);
		assertEquals("§6You got §anull§6 §anull§6 from §anull§6.", message);
	}
}
