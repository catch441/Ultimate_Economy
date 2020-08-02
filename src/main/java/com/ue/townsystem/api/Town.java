package com.ue.townsystem.api;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.exceptions.TownSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;

public interface Town {

    /**
     * Removes a chunk from a town.
     * 
     * @param plot
     * @throws TownSystemException
     */
    public void deletePlot(Plot plot) throws TownSystemException;

    /**
     * Return true if chunk is connected to this town.
     * 
     * @param chunkX
     * @param chunkZ
     * @return boolean
     */
    public boolean isChunkConnectedToTown(int chunkX, int chunkZ);
    
    /**
     * Returns the tax of the town.
     * 
     * @return double
     */
    public double getTax();

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
     * Set a player as deputy of a town.
     * 
     * @param player
     *            the player has to be a citizen of the town
     * @throws TownSystemException
     * @throws EconomyPlayerException
     */
    public void addDeputy(EconomyPlayer player)
	    throws TownSystemException, EconomyPlayerException;

    /**
     * Removes a deputy from the town.
     * 
     * @param player
     *            the player has to be a deputy of the town
     * @throws TownSystemException
     * @throws EconomyPlayerException
     */
    public void removeDeputy(EconomyPlayer player)
	    throws TownSystemException, EconomyPlayerException;

    /**
     * Opens the inventory of the TownManager.
     * 
     * @param player
     */
    public void openTownManagerVillagerInv(Player player);

    /**
     * Returns true if player is the mayor, deputy, plot owner or plot resident.
     * 
     * @param player
     *            can be any player
     * @param plot
     * @return boolean
     */
    public boolean hasBuildPermissions(EconomyPlayer player, Plot plot);

    /**
     * Returns true if player is mayor or deputy.
     * 
     * @param player
     *            the player has to be a citizen of the town
     * @return boolean
     */
    public boolean hasDeputyPermissions(EconomyPlayer player);

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
     * @param player
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
     * Returns true if player is mayor.
     * 
     * @param player
     *            the player has to be a citizen of the town
     * @return boolean
     */
    public boolean isMayor(EconomyPlayer player);

    /**
     * Get mayor.
     * 
     * @return String
     */
    public EconomyPlayer getMayor();

    /**
     * Returns true if player is a deputy of this town.
     * 
     * @param player
     *            the player has to be a citizen of the town
     * @return boolean
     */
    public boolean isDeputy(EconomyPlayer player);
    
    /**
     * Get a list of deputys of the town.
     * 
     * @return ArrayList of EconomyPlayers
     */
    public ArrayList<EconomyPlayer> getDeputies();

    /**
     * Expands a town by a new chunk.
     * 
     * @param chunk
     * @param player
     *            the player who want to use this method
     * @param sendMessage
     *            when true a message is send to the receiver and this player
     * @throws TownSystemException
     * @throws EconomyPlayerException
     */
    public void expandTown(Chunk chunk, EconomyPlayer player, boolean sendMessage)
	    throws TownSystemException, EconomyPlayerException;

    /**
     * Renames this town.
     * 
     * @param newName
     * @param player
     *            the player who want to use this method
     * @param sendMessage
     *            when true a message is send to the receiver and this player
     * @throws TownSystemException
     * @throws EconomyPlayerException
     * @throws GeneralEconomyException 
     */
    public void renameTown(String newName, EconomyPlayer player, boolean sendMessage)
	    throws TownSystemException, EconomyPlayerException, GeneralEconomyException;

    /**
     * Joins a player to a town.
     * 
     * @param player
     *            the player who wants to join the town
     * @throws EconomyPlayerException
     * @throws TownSystemException
     */
    public void joinTown(EconomyPlayer player) throws EconomyPlayerException, TownSystemException;

    /**
     * Leaves a player from a town.
     * 
     * @param player
     *            have to be a citizen
     * @throws TownSystemException
     * @throws EconomyPlayerException
     */
    public void leaveTown(EconomyPlayer player) throws TownSystemException, EconomyPlayerException;

    /**
     * Despawns all town villagers.
     * 
     */
    public void despawnAllVillagers();

    /**
     * Buy a plot in a town if the plot is for sale. Did not handle payment.
     * 
     * @param player
     *            the player who wants to by the plot, have to be a citizen of the
     *            town
     * @param chunkX
     * @param chunkZ
     * @throws TownSystemException
     * @throws EconomyPlayerException
     */
    public void buyPlot(EconomyPlayer player, int chunkX, int chunkZ) throws TownSystemException, EconomyPlayerException;

    /**
     * Returns a Plot by chunk coords.
     * 
     * @param chunkCoords
     *            Format X/Z
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
     * Returns true if the cunk is owned by any town.
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
     * Set the town spawn location. Deputy mayor permission needed.
     * 
     * @param townSpawn
     * @param ecoPlayer
     *            the player who wants to change the townspawn
     * @param sendMessage
     *            when true a message is send to the receiver and this player
     * @throws TownSystemException
     * @throws EconomyPlayerException
     */
    public void changeTownSpawn(Location townSpawn, EconomyPlayer ecoPlayer, boolean sendMessage)
	    throws TownSystemException, EconomyPlayerException;

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
     *            the player who wants to move the townmanager
     * @throws TownSystemException
     * @throws EconomyPlayerException
     */
    public void moveTownManagerVillager(Location location, EconomyPlayer player)
	    throws TownSystemException, EconomyPlayerException;
}
