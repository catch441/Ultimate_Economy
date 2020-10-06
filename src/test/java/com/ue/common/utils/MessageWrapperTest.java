package com.ue.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.common.utils.MessageWrapper;

@ExtendWith(MockitoExtension.class)
public class MessageWrapperTest {

	@InjectMocks
	MessageWrapper messageWrapper;
	@Mock
	Logger logger;

	@Test
	public void loadLanguageTest() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		assertEquals("language.MessagesBundle", messageWrapper.messages.getBaseBundleName());
		assertEquals("US", messageWrapper.messages.getLocale().getCountry());
		assertEquals("en", messageWrapper.messages.getLocale().getLanguage());
		verify(logger).info("[Ultimate_Economy] Loading language file: 'en' 'US'");;
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
		String message = messageWrapper.getErrorString("got_money_with_sender", "10", "kth", "kthschnll");
		assertEquals("§cYou got §410§c §4kth§c from §4kthschnll§c.", message);
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
		String message = messageWrapper.getErrorString("nothing", "10", "kth", "kthschnll");
		assertEquals("!nothing!", message);
	}

	@Test
	public void getStringTestWithParams() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getString("got_money_with_sender", "10", "kth", "kthschnll");
		assertEquals("§6You got §a10§6 §akth§6 from §akthschnll§6.", message);
	}

	@Test
	public void getStringTestWithParamsAndMissingMessage() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getString("nothing", "10", "kth", "kthschnll");
		assertEquals("!nothing!", message);
	}

	@Test
	public void getStringTestWithNullParams() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getString("got_money_with_sender", null, null, null);
		assertEquals("§6You got §anull§6 §anull§6 from §anull§6.", message);
	}
}
