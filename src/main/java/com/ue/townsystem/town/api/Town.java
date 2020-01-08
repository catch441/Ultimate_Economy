package com.ue.townsystem.town.api;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.TownSystemException;
import com.ue.player.api.EconomyPlayer;
import com.ue.townsystem.townworld.api.Townworld;

public interface Town {
	
	/**
	 * Returns the town bank amount.
	 * 
	 * @return double
	 */
	public double getTownBankAmount();
	
	/**
	 * Decrease the town bank amount.
	 * 
	 * @param amount
	 * @throws TownSystemException 
	 */
	public void decreaseTownBankAmount(double amount) throws TownSystemException;
	
	/**
	 * Increase the town bank amount.
	 * 
	 * @param amount
	 */
	public void increaseTownBankAmount(double amount);
	
	/**
	 * Set a player as CoOwner of a town
	 * 
	 * @param coOwner
	 * @throws TownSystemException
	 * @throws PlayerException
	 */
	public void addCoOwner(String coOwner) throws TownSystemException, PlayerException;
	
	/**
	 * Removes a coOwner from the town.
	 * 
	 * @param coOwner
	 * @throws TownSystemException
	 */
	public void removeCoOwner(String coOwner) throws TownSystemException;
	
	/**
	 * Opens the inventory of the TownManager.
	 * 
	 */
	public void openTownManagerVillagerInv(Player player);
	
	/**
	 * Returns true if player is the townOwner, town coOwner, plot owner or plot
	 * coOwner.
	 * 
	 * @param player
	 * @param plot
	 * @return boolean
	 * @throws TownSystemException
	 */
	public boolean hasBuildPermissions(String player, Plot plot) throws TownSystemException;
	
	/**
	 * Returns true if player is townOwner or coOwner
	 * 
	 * @param player
	 * @return boolean
	 * @throws TownSystemException
	 */
	public boolean hasCoOwnerPermission(String player) throws TownSystemException;
	
	/**
	 * Returns true if player is a citizen of this town.
	 * 
	 * @param player
	 * @return boolean
	 */
	public boolean isPlayerCitizen(String player);
	
	/**
	 * Get list of citizens.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<String> getCitizens();
	
	/**
	 * Returns true if player is townowner.
	 * 
	 * @param player
	 * @return boolean
	 * @throws TownSystemException
	 */
	public boolean isTownOwner(String player) throws TownSystemException;
	
	/**
	 * Expands a town by a new chunk.
	 * 
	 * @param chunk
	 * @param player
	 * @throws TownSystemException
	 */
	public void expandTown(Chunk chunk, String player) throws TownSystemException;
	
	/**
	 * Renames this town.
	 * 
	 * @param newName
	 * @param file townworld savefile
	 * @throws TownSystemException 
	 * @throws PlayerException 
	 */
	public void renameTown(String newName, String player) throws TownSystemException, PlayerException;
	
	/**
	 * Joins a player to a town.
	 * 
	 * @param ecoPlayer
	 * @throws PlayerException
	 * @throws TownSystemException
	 */
	public void joinTown(EconomyPlayer ecoPlayer) throws PlayerException, TownSystemException;
	
	/**
	 * Leaves a player from a town.
	 * 
	 * @param ecoPlayer
	 * @throws TownSystemException
	 * @throws PlayerException
	 */
	public void leaveTown(EconomyPlayer ecoPlayer) throws TownSystemException, PlayerException;

	/**
	 * Despawns all town villagers
	 * 
	 */
	public void despawnAllVillagers();
	
	/**
	 * Buy a plot in a town if the plot is for sale. Did not handle payment.
	 * 
	 * @param citizen
	 * @param chunk
	 *            (format "X/Z")
	 * @throws TownSystemException
	 */
	public void buyPlot(String citizen, int chunkX, int chunkZ) throws TownSystemException;
	
	/**
	 * Returns a Plot by chunk coords.
	 * 
	 * @param chunkCoords Format X/Z
	 * @return Plot
	 * @throws TownSystemException
	 */
	public Plot getPlotByChunk(String chunkCoords) throws TownSystemException;
	
	/**
	 * Get town name.
	 * 
	 * @return String
	 */
	public String getTownName();
	
	/**
	 * Returns true if the cunk is owned by any town
	 * 
	 * @param chunk
	 * @return boolean
	 */
	public boolean isClaimedByTown(Chunk chunk);
	
	/**
	 * Returns the townworld.
	 * 
	 * @return Townworld
	 */
	public Townworld getTownworld();
	
	/**
	 * Set the town spawn location.
	 * 
	 * @param townSpawn
	 * @throws TownSystemException
	 */
	public void setTownSpawn(Location townSpawn) throws TownSystemException;
	
	/**
	 * Get the town spawn location.
	 * 
	 * @return Location
	 */
	public Location getTownSpawn();
	
	/**
	 * Moves a town manager villager.
	 * 
	 * @param location
	 * @param player
	 * @throws TownSystemException
	 */
	public void moveTownManagerVillager(Location location, String player) throws TownSystemException;
}
