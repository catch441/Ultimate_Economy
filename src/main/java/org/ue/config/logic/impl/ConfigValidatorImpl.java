package org.ue.config.logic.impl;

import java.util.Arrays;
import java.util.List;

import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.impl.GeneralValidatorImpl;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigException;
import org.ue.config.logic.api.ConfigValidator;

public class ConfigValidatorImpl extends GeneralValidatorImpl<ConfigException>
		implements ConfigValidator {
	
	private static final List<String> languages = Arrays.asList("cs", "de", "sw", "en", "fr", "zh", "ru", "es", "lt", "it", "pl");

	public ConfigValidatorImpl(MessageWrapper messageWrapper) {
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
		String match = "";
		switch (lang) {
		case "pt":
			match = "BR";
			break;
		case "sw":
			match = "DE";
			break;
		case "cs":
			match = "CZ";
			break;
		case "en":
			match = "US";
			break;
		case "zh":
			match = "CN";
			break;
		default:
			match = lang.toUpperCase();
		}
		return country.equals(match);
	}
}
