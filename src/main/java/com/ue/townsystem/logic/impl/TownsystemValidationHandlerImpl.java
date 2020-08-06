package com.ue.townsystem.logic.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerExceptionMessageEnum;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.townsystem.api.TownController;
import com.ue.townsystem.logic.api.Plot;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

public class TownsystemValidationHandlerImpl implements TownsystemValidationHandler {

	private final MessageWrapper messageWrapper;

	/**
	 * Inject constructor.
	 * 
	 * @param messageWrapper
	 */
	@Inject
	public TownsystemValidationHandlerImpl(MessageWrapper messageWrapper) {
		this.messageWrapper = messageWrapper;
	}

	@Override
	public void checkForWorldExists(String world) throws TownSystemException {
		if (Bukkit.getWorld(world) == null) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.WORLD_DOES_NOT_EXIST, world);
		}
	}

	@Override
	public void checkForLocationInsidePlot(String chunkCoords, Location newLocation) throws EconomyPlayerException {
		if (chunkCoords.equals(newLocation.getChunk().getX() + "/" + newLocation.getChunk().getZ())) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.OUTSIDE_OF_THE_PLOT);
		}
	}

	@Override
	public void checkForPlayerIsNotResidentOfPlot(List<EconomyPlayer> residents, EconomyPlayer player)
			throws TownSystemException {
		if (residents.contains(player)) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.PLAYER_IS_ALREADY_RESIDENT);
		}
	}

	@Override
	public void checkForPlayerIsResidentOfPlot(List<EconomyPlayer> residents, EconomyPlayer player)
			throws TownSystemException {
		if (!residents.contains(player)) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.PLAYER_IS_NO_RESIDENT);
		}
	}

	@Override
	public void checkForIsPlotOwner(EconomyPlayer owner, EconomyPlayer player) throws EconomyPlayerException {
		if (!owner.equals(player)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.NO_PERMISSION);
		}
	}

	@Override
	public void checkForPlotIsNotForSale(boolean isForSale) throws TownSystemException {
		if (isForSale) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.PLOT_IS_ALREADY_FOR_SALE);
		}
	}

	@Override
	public void checkForPlayerIsDeputy(List<EconomyPlayer> deputies, EconomyPlayer player) throws TownSystemException {
		if (!deputies.contains(player)) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.PLAYER_IS_NO_DEPUTY);
		}
	}

	@Override
	public void checkForPlayerIsCitizen(List<EconomyPlayer> citizens, EconomyPlayer player) throws TownSystemException {
		if (!citizens.contains(player)) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.PLAYER_IS_NOT_CITIZEN);
		}
	}

	@Override
	public void checkForTownHasEnoughMoney(double townBankAmount, double amount) throws TownSystemException {
		if (amount > townBankAmount) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.TOWN_HAS_NOT_ENOUGH_MONEY);
		}
	}

	@Override
	public void checkForLocationIsInTown(Map<String, Plot> chunkList, Location townSpawn) throws TownSystemException {
		if (!chunkList.containsKey(townSpawn.getChunk().getX() + "/" + townSpawn.getChunk().getZ())) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.LOCATION_NOT_IN_TOWN);
		}
	}

	@Override
	public void checkForPlayerHasDeputyPermission(boolean hasDeputyPermissions)
			throws TownSystemException, EconomyPlayerException {
		if (!hasDeputyPermissions) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.NO_PERMISSION);
		}
	}

	@Override
	public void checkForChunkNotClaimed(Townworld townworld, Chunk chunk) throws TownSystemException {
		if (!townworld.isChunkFree(chunk)) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.CHUNK_ALREADY_CLAIMED);
		}
	}

	@Override
	public void checkForPlayerIsNotPlotOwner(EconomyPlayer citizen, Plot plot) throws EconomyPlayerException {
		if (plot.isOwner(citizen)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.YOU_ARE_THE_OWNER);
		}
	}

	@Override
	public void checkForPlotIsForSale(boolean isForSale) throws TownSystemException {
		if (!isForSale) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.PLOT_IS_NOT_FOR_SALE);
		}
	}

	@Override
	public void checkForPlayerIsCitizenPersonalError(List<EconomyPlayer> citizens, EconomyPlayer citizen)
			throws EconomyPlayerException {
		if (!citizens.contains(citizen)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.YOU_ARE_NO_CITIZEN);
		}
	}

	@Override
	public void checkForPlayerIsMayor(EconomyPlayer mayor, EconomyPlayer player)
			throws TownSystemException, EconomyPlayerException {
		if (!mayor.equals(player)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.NO_PERMISSION);
		}
	}

	@Override
	public void checkForPlayerIsNotMayor(EconomyPlayer mayor, EconomyPlayer player)
			throws EconomyPlayerException, TownSystemException {
		if (mayor.equals(player)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.YOU_ARE_THE_OWNER);
		}
	}

	@Override
	public void checkForTownDoesNotExist(String newName) throws GeneralEconomyException {
		if (TownController.getTownNameList().contains(newName)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS,
					newName);
		}
	}

	@Override
	public void checkForChunkIsNotClaimedByThisTown(Map<String, Plot> chunkList, String chunkCoords)
			throws TownSystemException {
		if (chunkList.containsKey(chunkCoords)) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.CHUNK_ALREADY_CLAIMED);
		}
	}

	@Override
	public void checkForChunkIsClaimedByThisTown(Map<String, Plot> chunkList, String chunkCoords)
			throws TownSystemException {
		if (!chunkList.containsKey(chunkCoords)) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.CHUNK_NOT_CLAIMED_BY_TOWN);
		}
	}
	
	@Override
	public void checkForPositiveAmount(double amount) throws GeneralEconomyException {
		if (amount < 0) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, amount);
		}
	}

	@Override
	public void checkForPlayerDidNotReachedMaxTowns(EconomyPlayer ecoPlayer) throws EconomyPlayerException {
		if (ecoPlayer.reachedMaxJoinedTowns()) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.MAX_REACHED);
		}
	}

	@Override
	public void checkForPlayerIsNotCitizenPersonal(List<EconomyPlayer> citizens, EconomyPlayer newCitizen)
			throws EconomyPlayerException {
		if (citizens.contains(newCitizen)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.YOU_ARE_ALREADY_CITIZEN);
		}
	}

	@Override
	public void checkForPlayerIsNotDeputy(List<EconomyPlayer> deputies, EconomyPlayer player)
			throws TownSystemException {
		if (deputies.contains(player)) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.PLAYER_IS_ALREADY_DEPUTY);
		}
	}

	@Override
	public void checkForChunkIsConnectedToTown(boolean isChunkConnectedToTown) throws TownSystemException {
		if (!isChunkConnectedToTown) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.CHUNK_IS_NOT_CONNECTED_WITH_TOWN);
		}
	}
}
