package org.ue.common.logic.impl;

import java.util.List;

import org.ue.common.logic.api.GeneralValidationHandler;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;

public class GeneralValidationHandlerImpl implements GeneralValidationHandler {

	protected final MessageWrapper messageWrapper;

	public GeneralValidationHandlerImpl(MessageWrapper messageWrapper) {
		this.messageWrapper = messageWrapper;
	}

	@Override
	public <T extends Enum<T>> void checkForValidEnum(Enum<? extends T>[] enumList, String value)
			throws GeneralEconomyException {
		boolean valid = false;
		value = value.toUpperCase();
		for (Enum<? extends T> enumValue : enumList) {
			if (enumValue.name().equals(value)) {
				valid = true;
			}
		}
		if (!valid) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					value);
		}
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
	public <T> void checkForValueNotInList(List<T> list, T value) throws GeneralEconomyException {
		if (list.contains(value)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, value.toString().toLowerCase());
		}
	}

	@Override
	public <T> void checkForValueInList(List<T> list, T value) throws GeneralEconomyException {
		if (!list.contains(value)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, value.toString().toLowerCase());
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
