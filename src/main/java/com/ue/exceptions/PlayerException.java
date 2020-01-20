package com.ue.exceptions;

import com.ue.language.MessageWrapper;

public class PlayerException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public static PlayerException getException(PlayerExceptionMessageEnum key, Object... params) {
		switch (key) {
			case INVALID_PARAMETER:
				return new PlayerException(MessageWrapper.getErrorString("invalid_parameter", params));
			case NO_PERMISSION_SET_SPAWNER:
				return new PlayerException(MessageWrapper.getErrorString("no_permision_set_spawner"));
			case NO_PERMISSION_BREAK_SPAWNER:
				return new PlayerException(MessageWrapper.getErrorString("no_permision_break_spawner"));
			case WILDERNESS:
				return new PlayerException(MessageWrapper.getErrorString("wilderness"));
			case TOWN_NOT_TOWN_OWNER:
				return new PlayerException(MessageWrapper.getErrorString("town_not_town_owner"));
			case NO_PERMISSION_ON_PLOT:
				return new PlayerException(MessageWrapper.getErrorString("no_permission_on_plot"));
			case JOB_ALREADY_JOINED:
				return new PlayerException(MessageWrapper.getErrorString("job_already_joined"));
			case JOB_NOT_JOINED:
				return new PlayerException(MessageWrapper.getErrorString("job_not_joined"));
			case TOWN_ALREADY_JOINED:
				return new PlayerException(MessageWrapper.getErrorString("town_already_joined"));
			case TOWN_NOT_JOINED:
				return new PlayerException(MessageWrapper.getErrorString("town_not_joined"));
			case NOT_ENOUGH_MONEY_PERSONAL:
				return new PlayerException(MessageWrapper.getErrorString("not_enough_money_personal"));
			case YOU_ARE_NOT_OWNER:
				return new PlayerException(MessageWrapper.getErrorString("you_are_not_owner"));
			case YOU_ARE_THE_OWNER:
				return new PlayerException(MessageWrapper.getErrorString("you_are_the_owner"));
			case OUTSIDE_OF_THE_PLOT:
				return new PlayerException(MessageWrapper.getErrorString("outside_of_the_plot"));
			case YOU_ARE_ALREADY_CITIZEN:
				return new PlayerException(MessageWrapper.getErrorString("you_are_already_citizen"));
			case YOU_ARE_NO_CITIZEN:
				return new PlayerException(MessageWrapper.getErrorString("you_are_no_citizen"));
			case SHOPOWNER_NOT_ENOUGH_MONEY:
				return new PlayerException(MessageWrapper.getErrorString("shopowner_not_enough_money"));
			case ENCHANTMENTLIST_INCOMPLETE:
				return new PlayerException(MessageWrapper.getErrorString("enchantmentlist_incomplete"));
			case NO_PERMISSION:
				return new PlayerException(MessageWrapper.getErrorString("player_has_no_permission"));
			case PLAYER_DOES_NOT_EXIST:
				return new PlayerException(MessageWrapper.getErrorString("player_does_not_exist"));
			case PLAYER_ALREADY_EXIST:
				return new PlayerException(MessageWrapper.getErrorString("player_already_exist"));
			case HOME_DOES_NOT_EXIST:
				return new PlayerException(MessageWrapper.getErrorString("home_does_not_exist"));
			case HOME_ALREADY_EXIST:
				return new PlayerException(MessageWrapper.getErrorString("home_already_exist"));
			case INVENTORY_SLOT_OCCUPIED:
				return new PlayerException(MessageWrapper.getErrorString("inventory_slot_occupied"));
			case NOT_ENOUGH_MONEY_NON_PERSONAL:
				return new PlayerException(MessageWrapper.getErrorString("not_enough_money_non_personal"));
			case MAX_REACHED:
				return new PlayerException(MessageWrapper.getErrorString("max_reached"));
			default: 
				return null;
		}
	}

	private PlayerException(String msg) {
		super(msg);
	}
}
