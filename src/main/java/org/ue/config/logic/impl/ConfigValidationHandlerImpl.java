package org.ue.config.logic.impl;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.impl.GeneralValidationHandlerImpl;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigException;
import org.ue.config.logic.api.ConfigValidationHandler;

public class ConfigValidationHandlerImpl extends GeneralValidationHandlerImpl<ConfigException>
		implements ConfigValidationHandler {
	
	private static final List<String> languages = Arrays.asList("cs", "de", "en", "fr", "zh", "ru", "es", "lt", "it", "pl");

	@Inject
	public ConfigValidationHandlerImpl(MessageWrapper messageWrapper) {
		super(messageWrapper);
	}

	@Override
	protected ConfigException createNew(MessageWrapper messageWrapper, ExceptionMessageEnum key, Object... params) {
		return new ConfigException(messageWrapper, key, params);
	}

	@Override
	public void checkForSupportedLanguage(String language) throws ConfigException {
		if (!languages.contains(language)) {
			throw new ConfigException(messageWrapper, ExceptionMessageEnum.INVALID_PARAMETER,
					language);
		}
	}
	
	@Override
	public void checkForCountryMatching(String lang, String country) throws ConfigException {
		if (!isCountryMatching(lang, country)) {
			throw new ConfigException(messageWrapper, ExceptionMessageEnum.INVALID_PARAMETER,
					country);
		}
	}
	
	private boolean isCountryMatching(String lang, String country) {
		switch (lang) {
		case "cs":
			if ("CZ".equals(country)) {
				return true;
			}
		case "en":
			if ("US".equals(country)) {
				return true;
			}
		case "zh":
			if ("CN".equals(country)) {
				return true;
			}
		default:
			if (lang.toUpperCase().equals(country)) {
				return true;
			}
		}
		return false;
	}
}
