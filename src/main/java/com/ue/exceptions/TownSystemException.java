package com.ue.exceptions;

import com.ue.language.MessageWrapper;

public class TownSystemException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public static TownSystemException getException(TownExceptionMessageEnum key, Object... params) {
		switch (key) {
			case TOWN_ALREADY_EXIST:
				return new TownSystemException(MessageWrapper.getErrorString("town_already_exist"));
			case TOWN_DOES_NOT_EXIST:
				return new TownSystemException(MessageWrapper.getErrorString("town_does_not_exist"));
			case PLOT_IS_ALREADY_FOR_SALE:
				return new TownSystemException(MessageWrapper.getErrorString("plot_is_already_for_sale"));
			case PLOT_IS_NOT_FOR_SALE:
				return new TownSystemException(MessageWrapper.getErrorString("plot_is_not_for_sale"));
			case CHUNK_IS_NOT_CONNECTED_WITH_TOWN:
				return new TownSystemException(MessageWrapper.getErrorString("chunk_is_not_connected_with_town"));
			case CHUNK_ALREADY_CLAIMED:
				return new TownSystemException(MessageWrapper.getErrorString("chunk_already_claimed"));
			case CHUNK_NOT_CLAIMED_BY_TOWN:
				return new TownSystemException(MessageWrapper.getErrorString("chunk_not_claimed_by_town"));
			case CHUNK_NOT_CLAIMED:
				return new TownSystemException(MessageWrapper.getErrorString("chunk_not_claimed"));
			case PLAYER_IS_NOT_MAYOR:
				return new TownSystemException(MessageWrapper.getErrorString("player_is_not_mayor"));
			case PLAYER_IS_NOT_OWNER:
				return new TownSystemException(MessageWrapper.getErrorString("player_is_not_owner"));
			case PLAYER_IS_ALREADY_OWNER:
				return new TownSystemException(MessageWrapper.getErrorString("player_is_already_owner"));
			case PLAYER_IS_NOT_CITIZEN:
				return new TownSystemException(MessageWrapper.getErrorString("player_is_not_citizen"));
			case LOCATION_NOT_IN_TOWN:
				return new TownSystemException(MessageWrapper.getErrorString("location_not_in_town"));
			case PLAYER_IS_ALREADY_CITIZEN:
				return new TownSystemException(MessageWrapper.getErrorString("player_is_already_citizen"));
			case TOWNWORLD_DOES_NOT_EXIST:
				return new TownSystemException(MessageWrapper.getErrorString("townworld_does_not_exist"));
			case TOWNWORLD_ALREADY_EXIST:
				return new TownSystemException(MessageWrapper.getErrorString("townworld_already_exist"));
			case WORLD_DOES_NOT_EXIST:
				return new TownSystemException(MessageWrapper.getErrorString("world_does_not_exist",params));
			case PLAYER_IS_ALREADY_DEPUTY:
				return new TownSystemException(MessageWrapper.getErrorString("player_is_already_deputy"));
			case PLAYER_IS_NO_DEPUTY:
				return new TownSystemException(MessageWrapper.getErrorString("player_is_no_deputy"));
			case TOWN_HAS_NOT_ENOUGH_MONEY:
				return new TownSystemException(MessageWrapper.getErrorString("town_has_not_enough_money"));
			case PLAYER_IS_ALREADY_RESIDENT:
				return new TownSystemException(MessageWrapper.getErrorString("player_is_already_resident"));
			case PLAYER_IS_NO_RESIDENT:
				return new TownSystemException(MessageWrapper.getErrorString("player_is_no_resident"));
			default:
				return null;
		}
	}
	
	private TownSystemException(String msg) {
		super(msg);
	}
}
