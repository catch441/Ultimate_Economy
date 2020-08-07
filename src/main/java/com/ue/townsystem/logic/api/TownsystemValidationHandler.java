package com.ue.townsystem.logic.api;

import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.exceptions.TownSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;

public interface TownsystemValidationHandler {

	/**
	 * Check for world exists on server.
	 * 
	 * @param world
	 * @throws TownSystemException
	 */
	public void checkForWorldExists(String world) throws TownSystemException;

	/**
	 * Check for location is inside plot.
	 * 
	 * @param chunkCoords
	 * @param newLocation
	 * @throws EconomyPlayerException
	 */
	public void checkForLocationInsidePlot(String chunkCoords, Location newLocation) throws EconomyPlayerException;

	/**
	 * Check for playerr is not resident of the plot.
	 * 
	 * @param residents
	 * @param player
	 * @throws TownSystemException
	 */
	public void checkForPlayerIsNotResidentOfPlot(List<EconomyPlayer> residents, EconomyPlayer player)
			throws TownSystemException;

	/**
	 * Check for player is resident of the plot.
	 * 
	 * @param residents
	 * @param player
	 * @throws TownSystemException
	 */
	public void checkForPlayerIsResidentOfPlot(List<EconomyPlayer> residents, EconomyPlayer player)
			throws TownSystemException;

	/**
	 * Check for player is plot owner.
	 * 
	 * @param owner
	 * @param player
	 * @throws EconomyPlayerException
	 */
	public void checkForIsPlotOwner(EconomyPlayer owner, EconomyPlayer player) throws EconomyPlayerException;

	/**
	 * Check for plot is not for sale.
	 * 
	 * @param isForSale
	 * @throws TownSystemException
	 */
	public void checkForPlotIsNotForSale(boolean isForSale) throws TownSystemException;

	/**
	 * Check for player is a deputy.
	 * 
	 * @param deputies
	 * @param player
	 * @throws TownSystemException
	 */
	public void checkForPlayerIsDeputy(List<EconomyPlayer> deputies, EconomyPlayer player) throws TownSystemException;

	/**
	 * Check for player is citizen.
	 * 
	 * @param citizens
	 * @param player
	 * @throws TownSystemException
	 */
	public void checkForPlayerIsCitizen(List<EconomyPlayer> citizens, EconomyPlayer player) throws TownSystemException;

	/**
	 * Check for town has enough money.
	 * 
	 * @param townBankAmount
	 * @param amount
	 * @throws TownSystemException
	 */
	public void checkForTownHasEnoughMoney(double townBankAmount, double amount) throws TownSystemException;

	/**
	 * Check for location is in town.
	 * 
	 * @param chunkList
	 * @param townSpawn
	 * @throws TownSystemException
	 */
	public void checkForLocationIsInTown(Map<String, Plot> chunkList, Location townSpawn) throws TownSystemException;

	/**
	 * Check for player is not plot owner.
	 * 
	 * @param citizen
	 * @param plot
	 * @throws EconomyPlayerException
	 */
	public void checkForPlayerIsNotPlotOwner(EconomyPlayer citizen, Plot plot) throws EconomyPlayerException;

	/**
	 * Check for plot is for sale.
	 * 
	 * @param isForSale
	 * @throws TownSystemException
	 */
	public void checkForPlotIsForSale(boolean isForSale) throws TownSystemException;

	/**
	 * Check for player is citicen with personal error.
	 * 
	 * @param citizens
	 * @param citizen
	 * @throws EconomyPlayerException
	 */
	public void checkForPlayerIsCitizenPersonalError(List<EconomyPlayer> citizens, EconomyPlayer citizen)
			throws EconomyPlayerException;

	/**
	 * Check for player is mayor.
	 * 
	 * @param mayor
	 * @param player
	 * @throws TownSystemException
	 * @throws EconomyPlayerException
	 */
	public void checkForPlayerIsMayor(EconomyPlayer mayor, EconomyPlayer player)
			throws TownSystemException, EconomyPlayerException;

	/**
	 * Check for player is not mayor.
	 * 
	 * @param mayor
	 * @param player
	 * @throws EconomyPlayerException
	 * @throws TownSystemException
	 */
	public void checkForPlayerIsNotMayor(EconomyPlayer mayor, EconomyPlayer player)
			throws EconomyPlayerException, TownSystemException;

	/**
	 * Check for town does not exist.
	 * 
	 * @param townNames
	 * @param newName
	 * @throws GeneralEconomyException
	 */
	public void checkForTownDoesNotExist(List<String> townNames, String newName) throws GeneralEconomyException;

	/**
	 * Check for chunk is not claimed by the town.
	 * 
	 * @param chunkList
	 * @param chunkCoords
	 * @throws TownSystemException
	 */
	public void checkForChunkIsNotClaimedByThisTown(Map<String, Plot> chunkList, String chunkCoords)
			throws TownSystemException;

	/**
	 * Check for chunk is claimed by the town.
	 * 
	 * @param chunkList
	 * @param chunkCoords
	 * @throws TownSystemException
	 */
	public void checkForChunkIsClaimedByThisTown(Map<String, Plot> chunkList, String chunkCoords)
			throws TownSystemException;

	/**
	 * Check for player did not reached max joined towns.
	 * 
	 * @param ecoPlayer
	 * @throws EconomyPlayerException
	 */
	public void checkForPlayerDidNotReachedMaxTowns(EconomyPlayer ecoPlayer) throws EconomyPlayerException;

	/**
	 * Check for player is not citizen with personal error.
	 * 
	 * @param citizens
	 * @param newCitizen
	 * @throws EconomyPlayerException
	 */
	public void checkForPlayerIsNotCitizenPersonal(List<EconomyPlayer> citizens, EconomyPlayer newCitizen)
			throws EconomyPlayerException;

	/**
	 * Check for player is not deputy.
	 * 
	 * @param deputies
	 * @param player
	 * @throws TownSystemException
	 */
	public void checkForPlayerIsNotDeputy(List<EconomyPlayer> deputies, EconomyPlayer player)
			throws TownSystemException;

	/**
	 * Check for chunk is not claimed.
	 * 
	 * @param townworld
	 * @param chunk
	 * @throws TownSystemException
	 */
	public void checkForChunkNotClaimed(Townworld townworld, Chunk chunk) throws TownSystemException;

	/**
	 * Check for player has deputy permission.
	 * 
	 * @param hasDeputyPermissions
	 * @throws TownSystemException
	 * @throws EconomyPlayerException
	 */
	public void checkForPlayerHasDeputyPermission(boolean hasDeputyPermissions)
			throws TownSystemException, EconomyPlayerException;

	/**
	 * Check for chunk is connected to town.
	 * 
	 * @param isChunkConnectedToTown
	 * @throws TownSystemException
	 */
	public void checkForChunkIsConnectedToTown(boolean isChunkConnectedToTown) throws TownSystemException;

	/**
	 * Check for positive amount.
	 * 
	 * @param amount
	 * @throws GeneralEconomyException
	 */
	public void checkForPositiveAmount(double amount) throws GeneralEconomyException;

	/**
	 * Check for townworld does not exist.
	 * 
	 * @param townworlds
	 * @param world
	 * @throws TownSystemException
	 */
	public void checkForTownworldDoesNotExist(Map<String, Townworld> townworlds, String world) throws TownSystemException;

	/**
	 * Check for townworld does exist.
	 * 
	 * @param townworlds
	 * @param world
	 * @throws TownSystemException
	 */
	public void checkForTownworldExists(Map<String, Townworld> townworlds, String world) throws TownSystemException;
}
