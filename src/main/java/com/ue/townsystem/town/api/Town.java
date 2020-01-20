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
	public void addCoOwner(EconomyPlayer coOwner) throws TownSystemException, PlayerException;
	
	/**
	 * Removes a coOwner from the town.
	 * 
	 * @param coOwner
	 * @throws TownSystemException
	 */
	public void removeCoOwner(EconomyPlayer coOwner) throws TownSystemException;
	
	/**
	 * Opens the inventory of the TownManager.
	 * 
	 * @param player
	 */
	public void openTownManagerVillagerInv(Player player);
	
	/**
	 * Returns true if player is the townOwner, town coOwner, plot owner or plot
	 * coOwner.
	 * 
	 * @param player the player who want to use this method
	 * @param plot
	 * @return boolean
	 * @throws TownSystemException
	 */
	public boolean hasBuildPermissions(EconomyPlayer player, Plot plot) throws TownSystemException;
	
	/**
	 * Returns true if player is townOwner or coOwner
	 * 
	 * @param player the player who want to use this method
	 * @return boolean
	 * @throws TownSystemException
	 */
	public boolean hasCoOwnerPermission(EconomyPlayer player) throws TownSystemException;
	
	/**
	 * Returns true, if the town has enough money.
	 * 
	 * @param amount
	 * @return boolean
	 */
	public boolean hasEnoughMoney(double amount);
	
	/**
	 * Returns true if player is a citizen of this town.
	 * 
	 * @param player the player who want to use this method
	 * @return boolean
	 */
	public boolean isPlayerCitizen(EconomyPlayer player);
	
	/**
	 * Get list of citizens.
	 * 
	 * @return ArrayList of EconomyPlayers
	 */
	public ArrayList<EconomyPlayer> getCitizens();
	
	/**
	 * Returns true if player is townowner.
	 * 
	 * @param player
	 * @return boolean
	 * @throws TownSystemException
	 */
	public boolean isTownOwner(EconomyPlayer player) throws TownSystemException;
	
	/**
	 * Returns true if player is coOwner of this town.
	 * 
	 * @param player
	 * @return boolean
	 * @throws TownSystemException
	 */
	public boolean isCoOwner(EconomyPlayer player) throws TownSystemException;
	
	/**
	 * Expands a town by a new chunk.
	 * 
	 * @param chunk
	 * @param player the player who want to use this method
	 * @param sendMessage when true a message is send to the receiver and this player
	 * @throws TownSystemException
	 * @throws PlayerException 
	 */
	public void expandTown(Chunk chunk, EconomyPlayer player,boolean sendMessage) throws TownSystemException, PlayerException;
	
	/**
	 * Renames this town.
	 * 
	 * @param newName
	 * @param player the player who want to use this method
	 * @param sendMessage when true a message is send to the receiver and this player
	 * @throws TownSystemException 
	 * @throws PlayerException 
	 */
	public void renameTown(String newName, EconomyPlayer player,boolean sendMessage) throws TownSystemException, PlayerException;
	
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
	 * @param chunkX
	 * @param chunkZ
	 * @throws TownSystemException
	 * @throws PlayerException 
	 */
	public void buyPlot(EconomyPlayer citizen, int chunkX, int chunkZ) throws TownSystemException, PlayerException;
	
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
	 * Set the town spawn location. CoOwner permission needed.
	 * 
	 * @param townSpawn
	 * @param ecoPlayer the player who wants to change the townspawn
	 * @param sendMessage when true a message is send to the receiver and this player
	 * @throws TownSystemException
	 * @throws PlayerException 
	 */
	public void setTownSpawn(Location townSpawn,EconomyPlayer ecoPlayer, boolean sendMessage) throws TownSystemException, PlayerException;
	
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
	 * @throws PlayerException 
	 */
	public void moveTownManagerVillager(Location location, EconomyPlayer player) throws TownSystemException, PlayerException;
}
