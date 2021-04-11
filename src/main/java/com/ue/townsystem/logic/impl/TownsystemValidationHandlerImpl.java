package com.ue.townsystem.logic.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.Chunk;
import org.bukkit.Location;

import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerExceptionMessageEnum;
import com.ue.townsystem.logic.api.Plot;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.townsystem.logic.api.TownworldManager;

import dagger.Lazy;

public class TownsystemValidationHandlerImpl implements TownsystemValidationHandler {

	private final MessageWrapper messageWrapper;
	private final Lazy<TownworldManager> townworldManager;
	private final ServerProvider serverProvider;

	@Inject
	public TownsystemValidationHandlerImpl(MessageWrapper messageWrapper, Lazy<TownworldManager> townworldManager,
			ServerProvider serverProvider) {
		this.messageWrapper = messageWrapper;
		this.townworldManager = townworldManager;
		this.serverProvider = serverProvider;
	}

	@Override
	public void checkForWorldExists(String world) throws TownSystemException {
		if (serverProvider.getWorld(world) == null) {
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
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION);
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
	public void checkForPlayerHasDeputyPermission(boolean hasDeputyPermissions) throws EconomyPlayerException {
		if (!hasDeputyPermissions) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION);
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
	public void checkForPlayerIsMayor(EconomyPlayer mayor, EconomyPlayer player) throws EconomyPlayerException {
		if (!mayor.equals(player)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION);
		}
	}

	@Override
	public void checkForPlayerIsNotMayor(EconomyPlayer mayor, EconomyPlayer player) throws EconomyPlayerException {
		if (mayor.equals(player)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.YOU_ARE_THE_OWNER);
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

	@Override
	public void checkForTownworldDoesNotExist(Map<String, Townworld> townworlds, String world)
			throws TownSystemException {
		if (townworlds.containsKey(world)) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.TOWNWORLD_ALREADY_EXIST);
		}
	}

	@Override
	public void checkForTownworldExists(Map<String, Townworld> townworlds, String world) throws TownSystemException {
		if (!townworlds.containsKey(world)) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.TOWNWORLD_DOES_NOT_EXIST);
		}
	}

	@Override
	public void checkForChunkIsFree(Townworld townworld, Location location) throws TownSystemException {
		if (!townworld.isChunkFree(location.getChunk())) {
			throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.CHUNK_ALREADY_CLAIMED);
		}
	}

	@Override
	public void checkForTownworldPlotPermission(Location location, EconomyPlayer ecoPlayer)
			throws EconomyPlayerException, TownSystemException {
		if (townworldManager.get().isTownWorld(location.getWorld().getName())) {
			Townworld townworld = townworldManager.get().getTownWorldByName(location.getWorld().getName());
			if (townworld.isChunkFree(location.getChunk())) {
				throw new EconomyPlayerException(messageWrapper,
						EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION);
			} else {
				Town town = townworld.getTownByChunk(location.getChunk());
				if (!town.hasBuildPermissions(ecoPlayer,
						town.getPlotByChunk(location.getChunk().getX() + "/" + location.getChunk().getZ()))) {
					throw new EconomyPlayerException(messageWrapper,
							EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION);
				}
			}
		}
	}
}
