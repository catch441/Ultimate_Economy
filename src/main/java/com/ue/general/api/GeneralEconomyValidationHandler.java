package com.ue.general.api;

import java.util.List;

import com.ue.general.impl.GeneralEconomyException;

public interface GeneralEconomyValidationHandler {

	/**
	 * Check if the slot is inside the size range.
	 * 
	 * @param slot
	 * @param size subtract the reserved slots from the size before calling this
	 *             method
	 * @throws GeneralEconomyException
	 */
	public void checkForValidSlot(int slot, int size) throws GeneralEconomyException;

	/**
	 * Check if the value is a multiple of 9 and not greater then 54 and not smaller then 9.
	 * 
	 * @param size
	 * @throws GeneralEconomyException
	 */
	public void checkForValidSize(int size) throws GeneralEconomyException;

	/**
	 * Check if the value is >= 0.
	 * 
	 * @param value
	 * @throws GeneralEconomyException
	 */
	public void checkForPositiveValue(double value) throws GeneralEconomyException;

	/**
	 * Check if value is > 0.
	 * 
	 * @param value
	 * @throws GeneralEconomyException
	 */
	public void checkForValueGreaterZero(double value) throws GeneralEconomyException;

	/**
	 * Check if the value is not in the list.
	 * 
	 * @param list
	 * @param value
	 * @throws GeneralEconomyException
	 */
	public void checkForValueNotInList(List<String> list, String value) throws GeneralEconomyException;

	/**
	 * Check for value is in list.
	 * 
	 * @param list
	 * @param value
	 * @throws GeneralEconomyException
	 */
	public void checkForValueInList(List<String> list, String value) throws GeneralEconomyException;
}
