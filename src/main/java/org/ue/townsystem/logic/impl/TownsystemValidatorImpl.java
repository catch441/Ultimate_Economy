package org.ue.townsystem.logic.impl;

import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.ue.bank.logic.api.BankAccount;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyvillager.logic.impl.EconomyVillagerValidatorImpl;
import org.ue.townsystem.logic.api.Plot;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.TownsystemValidator;
import org.ue.townsystem.logic.api.Townworld;
import org.ue.townsystem.logic.api.TownworldManager;

public class TownsystemValidatorImpl extends EconomyVillagerValidatorImpl<TownsystemException>
		implements TownsystemValidator {

	private TownworldManager townworldManager;

	public TownsystemValidatorImpl(MessageWrapper messageWrapper,
			ServerProvider serverProvider) {
		super(serverProvider, messageWrapper);
	}
	
	public void lazyInjection(TownworldManager townworldManager) {
		this.townworldManager = townworldManager;
	}

	@Override
	protected TownsystemException createNew(MessageWrapper messageWrapper, ExceptionMessageEnum key, Object... params) {
		return new TownsystemException(messageWrapper, key, params);
	}
	
	@Override
	public void checkForEnoughMoney(BankAccount account, double amount, boolean personal)
			throws TownsystemException {
		if (account.getAmount() < amount) {
			if (personal) {
				throw createNew(messageWrapper,
						ExceptionMessageEnum.NOT_ENOUGH_MONEY_PERSONAL);
			} else {
				throw createNew(messageWrapper,
						ExceptionMessageEnum.NOT_ENOUGH_MONEY_NON_PERSONAL);
			}
		}
	}

	@Override
	public void checkForWorldExists(String world) throws TownsystemException {
		if (serverProvider.getWorld(world) == null) {
			throw createNew(messageWrapper, ExceptionMessageEnum.WORLD_DOES_NOT_EXIST, world);
		}
	}

	@Override
	public void checkForLocationInsidePlot(String chunkCoords, Location newLocation) throws TownsystemException {
		if (!chunkCoords.equals(newLocation.getChunk().getX() + "/" + newLocation.getChunk().getZ())) {
			throw createNew(messageWrapper, ExceptionMessageEnum.OUTSIDE_OF_THE_PLOT);
		}
	}

	@Override
	public void checkForPlayerIsNotResidentOfPlot(List<EconomyPlayer> residents, EconomyPlayer player)
			throws TownsystemException {
		if (residents.contains(player)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.PLAYER_IS_ALREADY_RESIDENT);
		}
	}

	@Override
	public void checkForPlayerIsResidentOfPlot(List<EconomyPlayer> residents, EconomyPlayer player)
			throws TownsystemException {
		if (!residents.contains(player)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.PLAYER_IS_NO_RESIDENT);
		}
	}

	@Override
	public void checkForIsPlotOwner(EconomyPlayer owner, EconomyPlayer player) throws TownsystemException {
		if (!owner.equals(player)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.YOU_HAVE_NO_PERMISSION);
		}
	}

	@Override
	public void checkForPlotIsNotForSale(boolean isForSale) throws TownsystemException {
		if (isForSale) {
			throw createNew(messageWrapper, ExceptionMessageEnum.PLOT_IS_ALREADY_FOR_SALE);
		}
	}

	@Override
	public void checkForPlayerIsDeputy(List<EconomyPlayer> deputies, EconomyPlayer player) throws TownsystemException {
		if (!deputies.contains(player)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.PLAYER_IS_NO_DEPUTY);
		}
	}

	@Override
	public void checkForPlayerIsCitizen(List<EconomyPlayer> citizens, EconomyPlayer player) throws TownsystemException {
		if (!citizens.contains(player)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.PLAYER_IS_NOT_CITIZEN);
		}
	}

	@Override
	public void checkForTownHasEnoughMoney(double townBankAmount, double amount) throws TownsystemException {
		if (amount > townBankAmount) {
			throw createNew(messageWrapper, ExceptionMessageEnum.TOWN_HAS_NOT_ENOUGH_MONEY);
		}
	}

	@Override
	public void checkForLocationIsInTown(Map<String, Plot> chunkList, Location townSpawn) throws TownsystemException {
		if (!chunkList.containsKey(townSpawn.getChunk().getX() + "/" + townSpawn.getChunk().getZ())) {
			throw createNew(messageWrapper, ExceptionMessageEnum.LOCATION_NOT_IN_TOWN);
		}
	}

	@Override
	public void checkForPlayerHasDeputyPermission(boolean hasDeputyPermissions) throws TownsystemException {
		if (!hasDeputyPermissions) {
			throw createNew(messageWrapper, ExceptionMessageEnum.YOU_HAVE_NO_PERMISSION);
		}
	}

	@Override
	public void checkForChunkNotClaimed(Townworld townworld, Chunk chunk) throws TownsystemException {
		if (!townworld.isChunkFree(chunk)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.CHUNK_ALREADY_CLAIMED);
		}
	}

	@Override
	public void checkForPlayerIsNotPlotOwner(EconomyPlayer citizen, Plot plot) throws TownsystemException {
		if (plot.isOwner(citizen)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.YOU_ARE_THE_OWNER);
		}
	}

	@Override
	public void checkForPlotIsForSale(boolean isForSale) throws TownsystemException {
		if (!isForSale) {
			throw createNew(messageWrapper, ExceptionMessageEnum.PLOT_IS_NOT_FOR_SALE);
		}
	}

	@Override
	public void checkForPlayerIsCitizenPersonalError(List<EconomyPlayer> citizens, EconomyPlayer citizen)
			throws TownsystemException {
		if (!citizens.contains(citizen)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.YOU_ARE_NO_CITIZEN);
		}
	}

	@Override
	public void checkForPlayerIsMayor(EconomyPlayer mayor, EconomyPlayer player) throws TownsystemException {
		if (!mayor.equals(player)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.YOU_HAVE_NO_PERMISSION);
		}
	}

	@Override
	public void checkForPlayerIsNotMayor(EconomyPlayer mayor, EconomyPlayer player) throws TownsystemException {
		if (mayor.equals(player)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.YOU_ARE_THE_OWNER);
		}
	}

	@Override
	public void checkForChunkIsNotClaimedByThisTown(Map<String, Plot> chunkList, String chunkCoords)
			throws TownsystemException {
		if (chunkList.containsKey(chunkCoords)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.CHUNK_ALREADY_CLAIMED);
		}
	}

	@Override
	public void checkForChunkIsClaimedByThisTown(Map<String, Plot> chunkList, String chunkCoords)
			throws TownsystemException {
		if (!chunkList.containsKey(chunkCoords)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.CHUNK_NOT_CLAIMED_BY_TOWN);
		}
	}

	@Override
	public void checkForPlayerDidNotReachedMaxTowns(EconomyPlayer ecoPlayer) throws TownsystemException {
		if (ecoPlayer.reachedMaxJoinedTowns()) {
			throw createNew(messageWrapper, ExceptionMessageEnum.MAX_REACHED);
		}
	}

	@Override
	public void checkForPlayerIsNotCitizenPersonal(List<EconomyPlayer> citizens, EconomyPlayer newCitizen)
			throws TownsystemException {
		if (citizens.contains(newCitizen)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.YOU_ARE_ALREADY_CITIZEN);
		}
	}

	@Override
	public void checkForPlayerIsNotDeputy(List<EconomyPlayer> deputies, EconomyPlayer player)
			throws TownsystemException {
		if (deputies.contains(player)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.PLAYER_IS_ALREADY_DEPUTY);
		}
	}

	@Override
	public void checkForChunkIsConnectedToTown(boolean isChunkConnectedToTown) throws TownsystemException {
		if (!isChunkConnectedToTown) {
			throw createNew(messageWrapper, ExceptionMessageEnum.CHUNK_IS_NOT_CONNECTED_WITH_TOWN);
		}
	}

	@Override
	public void checkForTownworldDoesNotExist(Map<String, Townworld> townworlds, String world)
			throws TownsystemException {
		if (townworlds.containsKey(world)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.TOWNWORLD_ALREADY_EXIST);
		}
	}

	@Override
	public void checkForTownworldExists(Map<String, Townworld> townworlds, String world) throws TownsystemException {
		if (!townworlds.containsKey(world)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.TOWNWORLD_DOES_NOT_EXIST);
		}
	}

	@Override
	public void checkForChunkIsFree(Townworld townworld, Location location) throws TownsystemException {
		if (!townworld.isChunkFree(location.getChunk())) {
			throw createNew(messageWrapper, ExceptionMessageEnum.CHUNK_ALREADY_CLAIMED);
		}
	}

	@Override
	public void checkForTownworldPlotPermission(Location location, EconomyPlayer ecoPlayer) throws TownsystemException {
		if (townworldManager.isTownWorld(location.getWorld().getName())) {
			Townworld townworld = townworldManager.getTownWorldByName(location.getWorld().getName());
			if (townworld.isChunkFree(location.getChunk())) {
				throw createNew(messageWrapper, ExceptionMessageEnum.YOU_HAVE_NO_PERMISSION);
			} else {
				Town town = townworld.getTownByChunk(location.getChunk());
				if (!town.hasBuildPermissions(ecoPlayer,
						town.getPlotByChunk(location.getChunk().getX() + "/" + location.getChunk().getZ()))) {
					throw createNew(messageWrapper, ExceptionMessageEnum.YOU_HAVE_NO_PERMISSION);
				}
			}
		}
	}
}
