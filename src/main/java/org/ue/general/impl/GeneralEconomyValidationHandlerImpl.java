package org.ue.general.impl;

import java.util.List;

import javax.inject.Inject;

import org.ue.common.utils.api.MessageWrapper;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;
import org.ue.general.api.GeneralEconomyValidationHandler;

public class GeneralEconomyValidationHandlerImpl implements GeneralEconomyValidationHandler {

	private final MessageWrapper messageWrapper;

	@Inject
	public GeneralEconomyValidationHandlerImpl(MessageWrapper messageWrapper) {
		this.messageWrapper = messageWrapper;
	}

	@Override
	public void checkForPositiveValue(double value) throws GeneralEconomyException {
		if (value < 0) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					value);
		}
	}

	@Override
	public void checkForValueGreaterZero(double value) throws GeneralEconomyException {
		if (value <= 0) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					value);
		}
	}

	@Override
	public void checkForValueNotInList(List<String> list, String value) throws GeneralEconomyException {
		if (list.contains(value)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, value);
		}
	}

	@Override
	public void checkForValueInList(List<String> list, String value) throws GeneralEconomyException {
		if (!list.contains(value)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, value);
		}
	}
	
	@Override
	public void checkForValueExists(Object value, String name) throws GeneralEconomyException {
		if (value == null) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, value);
		}
	}

	@Override
	public void checkForValidSize(int size) throws GeneralEconomyException {
		if (size < 9 || size % 9 != 0 || size > 54) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					size);
		}
	}

	@Override
	public void checkForValidSlot(int slot, int size) throws GeneralEconomyException {
		if (slot < 0 || slot >= size) {
			// +1 for player readable style
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					slot + 1);
		}
	}
}
