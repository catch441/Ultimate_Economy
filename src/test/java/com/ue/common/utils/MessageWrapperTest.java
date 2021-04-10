package com.ue.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MessageWrapperTest {

	@InjectMocks
	MessageWrapper messageWrapper;

	@Test
	public void loadLanguageTest() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		assertEquals("language.MessagesBundle", messageWrapper.messages.getBaseBundleName());
		assertEquals("US", messageWrapper.messages.getLocale().getCountry());
		assertEquals("en", messageWrapper.messages.getLocale().getLanguage());
	}

	@Test
	public void getErrorStringTest() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getErrorString("restart");
		assertEquals("§cPlease restart the server!", message);
	}

	@Test
	public void getErrorStringTestWithMissingMessage() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getErrorString("nothing");
		assertEquals("!nothing!", message);
	}

	@Test
	public void getStringTest() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getString("restart");
		assertEquals("§6Please restart the server!", message);
	}

	@Test
	public void getStringTestWithMissingMessage() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getString("nothing");
		assertEquals("!nothing!", message);
	}

	@Test
	public void getErrorStringTestWithParams() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getErrorString("got_money_with_sender", "10", "stuff", "catch");
		assertEquals("§cYou got §410§c §4stuff§c from §4catch§c.", message);
	}

	@Test
	public void getErrorStringTestWithNullParams() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getErrorString("got_money_with_sender", null, null, null);
		assertEquals("§cYou got §4null§c §4null§c from §4null§c.", message);
	}

	@Test
	public void getErrorStringTestWithParamsAndMissingMessage() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getErrorString("nothing", "10", "stuff", "catch");
		assertEquals("!nothing!", message);
	}

	@Test
	public void getStringTestWithParams() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getString("got_money_with_sender", "10", "stuff", "catch");
		assertEquals("§6You got §a10§6 §astuff§6 from §acatch§6.", message);
	}

	@Test
	public void getStringTestWithParamsAndMissingMessage() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getString("nothing", "10", "stuff", "catch");
		assertEquals("!nothing!", message);
	}

	@Test
	public void getStringTestWithNullParams() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getString("got_money_with_sender", null, null, null);
		assertEquals("§6You got §anull§6 §anull§6 from §anull§6.", message);
	}
	
	// test with format symbol replacement ' -> ''
	
	@Test
	public void getErrorStringTestWithParamsFr() {
		messageWrapper.loadLanguage(new Locale("fr", "FR"));
		String message = messageWrapper.getErrorString("shop_sell_singular", "10", "12", "$");
		assertEquals("§c§410§c l'article a été vendu pour §412§c §4$§c.", message);
	}
	
	@Test
	public void getStringTestWithParamsFr() {
		messageWrapper.loadLanguage(new Locale("fr", "FR"));
		String message = messageWrapper.getString("shop_sell_singular", "10", "12", "$");
		assertEquals("§6§a10§6 l'article a été vendu pour §a12§6 §a$§6.", message);
	}
}
