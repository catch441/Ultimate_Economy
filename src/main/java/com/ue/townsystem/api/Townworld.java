package com.ue.townsystem.api;

import java.io.File;
import java.util.List;

import org.bukkit.Chunk;

import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.exceptions.TownSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;

public interface Townworld {
    
    /**
     * Returns the townlist.
     * 
     * @return List
     */
    public List<Town> getTownList();

    /**
     * Adds a town to the townworld. You don't have to add a town, after you created
     * one with the TownController.
     * 
     * @param town
     * @throws GeneralEconomyException 
     */
    public void addTown(Town town) throws GeneralEconomyException;

    /**
     * Removes a town from the townworld. You don't have to remove a town, after you
     * dissolved one with the TownController.
     * 
     * @param town
     * @throws GeneralEconomyException 
     */
    public void removeTown(Town town) throws GeneralEconomyException;

    /**
     * Returns town by chunk.
     * <p>
     * 
     * @param chunk
     * @return Town
     * @throws TownSystemException
     */
    public Town getTownByChunk(Chunk chunk) throws TownSystemException;

    /**
     * Returns the Town World name.
     * 
     * @return String
     */
    public String getWorldName();

    /**
     * Returns a town by townname.
     * 
     * @param townName
     * @return Town
     * @throws GeneralEconomyException 
     */
    public Town getTownByName(String townName) throws GeneralEconomyException;

    /**
     * Returns a list of all townnames in this townworld.
     * 
     * @return List
     */
    public List<String> getTownNameList();

    /**
     * Sets the town name list. Not necessary if you load the townworld with the
     * TownworldController.
     * 
     * @param names
     */
    public void setTownNameList(List<String> names);

    /**
     * Returns the savefile of this townworld.
     * 
     * @return File
     */
    public File getSaveFile();

    /**
     * Set the FoundationPrice for a town in this townworld. Set 'saving' true if
     * the value should be saved in the file.
     * 
     * @param foundationPrice
     * @param saving
     *            saves the value into the savefile.
     */
    public void setFoundationPrice(double foundationPrice, boolean saving);

    /**
     * Returns the founding price of this townworld.
     * 
     * @return double
     */
    public double getFoundationPrice();

    /**
     * Set the ExpandPrice for a town in this townworld. Set 'saving' true if the
     * value should be saved in the file.
     * 
     * @param expandPrice
     * @param saving
     */
    public void setExpandPrice(double expandPrice, boolean saving);

    /**
     * Returns the expand price for a town in this townworld.
     * 
     * @return double
     */
    public double getExpandPrice();

    /**
     * Despawns all town villagers in this townworld.
     * 
     */
    public void despawnAllTownVillagers();

    /**
     * Returns true if the chunk is not claimed by any town.
     * 
     * @param chunk
     * @return boolean
     */
    public boolean isChunkFree(Chunk chunk);

    /**
     * Delets all save files and towns.
     * <p>
     * @throws GeneralEconomyException 
     * @throws EconomyPlayerException 
     * @throws TownSystemException 
     */
    public void delete() throws TownSystemException, EconomyPlayerException, GeneralEconomyException;
}
