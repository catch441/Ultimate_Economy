package org.ue.common.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.MessageEnum;

@ExtendWith(MockitoExtension.class)
public class LanguagePackConsistencyCheck {

	MessageWrapperImpl messageWrapper = new MessageWrapperImpl();

	@Test
	public void checkEnUsLanguage() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		checkKeys();
	}

	@Test
	public void checkCsCZLanguage() {
		messageWrapper.loadLanguage(new Locale("cs", "CZ"));
		checkKeys();
	}

	@Test
	public void checkdeDELanguage() {
		messageWrapper.loadLanguage(new Locale("de", "DE"));
		checkKeys();
	}

	@Test
	public void checkEsESLanguage() {
		messageWrapper.loadLanguage(new Locale("es", "ES"));
		checkKeys();
	}

	@Test
	public void checkfrFRLanguage() {
		messageWrapper.loadLanguage(new Locale("fr", "FR"));
		checkKeys();
	}

	@Test
	public void checkItITLanguage() {
		messageWrapper.loadLanguage(new Locale("it", "IT"));
		checkKeys();
	}

	@Test
	public void checkLtLTLanguage() {
		messageWrapper.loadLanguage(new Locale("lt", "LT"));
		checkKeys();
	}

	@Test
	public void checkPlPLLanguage() {
		messageWrapper.loadLanguage(new Locale("pl", "PL"));
		checkKeys();
	}

	@Test
	public void checkRuRULanguage() {
		messageWrapper.loadLanguage(new Locale("ru", "RU"));
		checkKeys();
	}

	@Test
	public void checkZhCNLanguage() {
		messageWrapper.loadLanguage(new Locale("zh", "CN"));
		checkKeys();
	}

	private void checkKeys() {
		for (MessageEnum message : MessageEnum.values()) {
			if(message != MessageEnum.DEV_TEST) {
				assertFalse(messageWrapper.getString(message, "args1").equals("!" + message.getValue() + "!"));
			}
		}
		for(ExceptionMessageEnum exception: ExceptionMessageEnum.values()) {
			if(exception != ExceptionMessageEnum.DEV_TEST) {
				assertFalse(messageWrapper.getErrorString(exception, "args1").equals("!" + exception.getValue() + "!"));
			}
		}
	}
}
