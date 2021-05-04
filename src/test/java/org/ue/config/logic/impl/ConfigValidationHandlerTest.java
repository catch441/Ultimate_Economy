package org.ue.config.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
	ConfigValidationHandlerImpl validationHandler;
	@Mock
	MessageWrapper messageWrapper;
	
	@Test
	public void checkForSupportedLanguageTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForSupportedLanguage("de"));
	}
	
	@Test
	public void checkForSupportedLanguageTestFail() {
		try {
			validationHandler.checkForSupportedLanguage("ab");
		} catch (ConfigException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("ab", e.getParams()[0]);
			assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
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
	public void checkForMatchingCountryTestFail() {
		try {
			validationHandler.checkForCountryMatching("de", "CN");
		} catch (ConfigException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("CN", e.getParams()[0]);
			assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
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
