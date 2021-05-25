package org.ue.config.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigException;

@ExtendWith(MockitoExtension.class)
public class ConfigValidationHandlerTest {

	@InjectMocks
	ConfigValidatorImpl validationHandler;
	@Mock
	MessageWrapper messageWrapper;

	@Test
	public void checkForSupportedLanguageTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForSupportedLanguage("de"));
	}

	@Test
	public void checkForSupportedLanguageTestFail() {
		ConfigException e = assertThrows(ConfigException.class,
				() -> validationHandler.checkForSupportedLanguage("ab"));
		assertEquals(1, e.getParams().length);
		assertEquals("ab", e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
	}

	@Test
	public void checkForMatchingCountryTestValid1() {
		assertDoesNotThrow(() -> validationHandler.checkForCountryMatching("de", "DE"));
	}

	@Test
	public void checkForMatchingCountryTestValid2() {
		assertDoesNotThrow(() -> validationHandler.checkForCountryMatching("cs", "CZ"));
	}

	@Test
	public void checkForMatchingCountryTestValid3() {
		assertDoesNotThrow(() -> validationHandler.checkForCountryMatching("en", "US"));
	}

	@Test
	public void checkForMatchingCountryTestValid4() {
		assertDoesNotThrow(() -> validationHandler.checkForCountryMatching("zh", "CN"));
	}
	
	@Test
	public void checkForMatchingCountryTestValid5() {
		assertDoesNotThrow(() -> validationHandler.checkForCountryMatching("sw", "DE"));
	}
	
	@Test
	public void checkForMatchingCountryTestValid6() {
		assertDoesNotThrow(() -> validationHandler.checkForCountryMatching("pt", "BR"));
	}

	@Test
	public void checkForMatchingCountryTestFail() {
		ConfigException e = assertThrows(ConfigException.class,
				() -> validationHandler.checkForCountryMatching("de", "CN"));
		assertEquals(1, e.getParams().length);
		assertEquals("CN", e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
	}

	@Test
	public void createNewTest() {
		ConfigException exception = validationHandler.createNew(messageWrapper, ExceptionMessageEnum.INVALID_PARAMETER,
				"param");
		assertEquals(1, exception.getParams().length);
		assertEquals("param", exception.getParams()[0]);
		assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, exception.getKey());
	}
}
