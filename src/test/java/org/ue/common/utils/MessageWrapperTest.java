package org.ue.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.MessageEnum;

@ExtendWith(MockitoExtension.class)
public class MessageWrapperTest {

	@InjectMocks
	MessageWrapperImpl messageWrapper;

	@Test
	public void loadLanguageTest() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		assertEquals("US", messageWrapper.getLocale().getCountry());
		assertEquals("en", messageWrapper.getLocale().getLanguage());
	}

	@Test
	public void getErrorStringTest() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getErrorString(ExceptionMessageEnum.ITEM_UNAVAILABLE);
		assertEquals("§cThis item is unavailable!", message);
	}

	@Test
	public void getErrorStringTestWithMissingMessage() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getErrorString(ExceptionMessageEnum.DEV_TEST);
		assertEquals("!nothing!", message);
	}

	@Test
	public void getStringTest() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getString(MessageEnum.RESTART);
		assertEquals("§6Please restart the server!", message);
	}

	@Test
	public void getStringTestWithMissingMessage() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getString(MessageEnum.DEV_TEST);
		assertEquals("!nothing!", message);
	}

	@Test
	public void getErrorStringTestWithParams() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getErrorString(ExceptionMessageEnum.SLOT_EMPTY, "10");
		assertEquals("§cSlot §410§c is empty!", message);
	}

	@Test
	public void getErrorStringTestWithNullParams() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getErrorString(ExceptionMessageEnum.ALREADY_EXISTS, null, null);
		assertEquals("§c§4null§c already exists!", message);
	}

	@Test
	public void getErrorStringTestWithParamsAndMissingMessage() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getErrorString(ExceptionMessageEnum.DEV_TEST, "10", "stuff", "catch");
		assertEquals("!nothing!", message);
	}

	@Test
	public void getStringTestWithParams() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getString(MessageEnum.GOT_MONEY_WITH_SENDER, "10", "stuff", "catch");
		assertEquals("§6You got §a10§6 §astuff§6 from §acatch§6.", message);
	}

	@Test
	public void getStringTestWithParamsAndMissingMessage() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getString(MessageEnum.DEV_TEST, "10", "stuff", "catch");
		assertEquals("!nothing!", message);
	}

	@Test
	public void getStringTestWithNullParams() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		String message = messageWrapper.getString(MessageEnum.GOT_MONEY_WITH_SENDER, null, null, null);
		assertEquals("§6You got §anull§6 §anull§6 from §anull§6.", message);
	}
	
	// test with format symbol replacement ' -> ''
	
	@Test
	public void getErrorStringTestWithParamsFr() {
		messageWrapper.loadLanguage(new Locale("fr", "FR"));
		String message = messageWrapper.getErrorString(ExceptionMessageEnum.NOT_ENOUGH_MONEY_PERSONAL);
		assertEquals("§cTu n'as pas assez d'argent!", message);
	}
	
	@Test
	public void getStringTestWithParamsFr() {
		messageWrapper.loadLanguage(new Locale("fr", "FR"));
		String message = messageWrapper.getString(MessageEnum.SHOP_SELL_SINGULAR, "10", "12", "$");
		assertEquals("§6§a10§6 l'article a été vendu pour §a12§6 §a$§6.", message);
	}
}
