package org.ue.common.logic.impl;

import java.util.List;

import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.logic.api.GeneralValidator;
import org.ue.common.utils.api.MessageWrapper;

public abstract class GeneralValidatorImpl<T extends GeneralEconomyException>
		implements GeneralValidator<T> {

	protected final MessageWrapper messageWrapper;

	public GeneralValidatorImpl(MessageWrapper messageWrapper) {
		this.messageWrapper = messageWrapper;

	}

	protected abstract T createNew(MessageWrapper messageWrapper, ExceptionMessageEnum key, Object... params);

	@Override
	public <S extends Enum<S>> void checkForValidEnum(Enum<? extends S>[] enumList, String value) throws T {
		boolean valid = false;
		value = value.toUpperCase();
		for (Enum<? extends S> enumValue : enumList) {
			if (enumValue.name().equals(value)) {
				valid = true;
			}
		}
		if (!valid) {
			throw createNew(messageWrapper, ExceptionMessageEnum.INVALID_PARAMETER, value.toLowerCase());
		}
	}
	
	@Override
	public void checkForNotReachedMax(boolean reachedMax) throws T {
		if (reachedMax) {
			throw createNew(messageWrapper, ExceptionMessageEnum.MAX_REACHED);
		}
	}

	@Override
	public void checkForPositiveValue(Double value) throws T {
		if (value < 0) {
			throw createNew(messageWrapper, ExceptionMessageEnum.INVALID_PARAMETER, value);
		}
	}

	@Override
	public void checkForValueGreaterZero(double value) throws T {
		if (value <= 0) {
			throw createNew(messageWrapper, ExceptionMessageEnum.INVALID_PARAMETER, value);
		}
	}

	@Override
	public <S> void checkForValueNotInList(List<S> list, S value) throws T {
		if (list.contains(value)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.ALREADY_EXISTS, value.toString().toLowerCase());
		}
	}

	@Override
	public <S> void checkForValueInList(List<S> list, S value) throws T {
		if (!list.contains(value)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.DOES_NOT_EXIST, value.toString().toLowerCase());
		}
	}

	@Override
	public void checkForValueExists(Object value, String name) throws T {
		if (value == null) {
			throw createNew(messageWrapper, ExceptionMessageEnum.DOES_NOT_EXIST, name);
		}
	}

	@Override
	public void checkForValidSize(int size) throws T {
		if (size < 9 || size % 9 != 0 || size > 54) {
			throw createNew(messageWrapper, ExceptionMessageEnum.INVALID_PARAMETER, size);
		}
	}

	@Override
	public void checkForValidSlot(int slot, int size) throws T {
		if (slot < 0 || slot >= size) {
			// +1 for player readable style
			throw createNew(messageWrapper, ExceptionMessageEnum.INVALID_PARAMETER, slot + 1);
		}
	}
}
