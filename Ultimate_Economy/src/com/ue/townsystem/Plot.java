package com.ue.townsystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.exceptions.townsystem.ChunkNotClaimedByThisTownException;
import com.ue.exceptions.townsystem.PlayerIsAlreadyCoOwnerException;
import com.ue.exceptions.townsystem.PlotIsAlreadyForSaleException;
import com.ue.exceptions.townsystem.PlotIsNotForSaleException;

public class Plot {

	private String owner;
	private List<String> coOwners;
	private final String chunkCoords;
	private boolean isForSale;
	private double salePrice;
	private final String townName;
	
	/**
	 * <p>
	 * Represents a plot in a town.
	 * <p>
	 * @param file
	 * @param owner
	 * @param chunkCoords	(format "X/Z")
	 * @param townName
	 */
	public Plot(File file,String owner,String chunkCoords,String townName) {
		this.chunkCoords = chunkCoords;
		setOwner(file,owner);
		this.townName = townName;
		isForSale = false;
		salePrice = 0;
		coOwners = new ArrayList<>();
	}
	
	/**
	 * <p>
	 * Get the owner of this plot.
	 * <p>
	 * @return
	 */
	public String getOwner() {
		return owner;
	}
	
	/**
	 * <p>
	 * Set the owner of this plot.
	 * <p>
	 * @param file
	 * @param owner
	 */
	public void setOwner(File file,String owner) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Town." + townName + ".Plots." + chunkCoords + ".owner", owner);
		this.owner = owner;
		save(file,config);
	}
	
	/**
	 * <p>
	 * Get a list of all coOwners of this plot.
	 * <p>
	 * @return List
	 */
	public List<String> getCoOwners() {
		return coOwners;
	}
	
	/**
	 * <p>
	 * Set the list of coOwners of this plot.
	 * <p>
	 * @param file
	 * @param coOwners
	 */
	public void setCoOwners(List<String> coOwners) {
		this.coOwners = coOwners;
	}
	
	/**
	 * <p>
	 * Add a coOwner to this plot.
	 * <p>
	 * @param file
	 * @param citizen
	 * @throws PlayerIsAlreadyCoOwnerException
	 */
	public void addCoOwner(File file,String citizen) throws PlayerIsAlreadyCoOwnerException {
		if(!coOwners.contains(citizen)) {
			coOwners.add(citizen);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Town." + townName + ".Plots." + chunkCoords + ".coOwners", coOwners);
			save(file,config);
		}
		else {
			throw new PlayerIsAlreadyCoOwnerException(citizen, "plot");
		}
	}
	
	/**
	 * <p>
	 * Set 'isForSale' without saving it in the file.
	 * <p>
	 * @param isForSale
	 */
	public void setForSale(boolean isForSale) {
		this.isForSale = isForSale;
	}
	
	/**
	 * <p>
	 * Get the chunk coords of this plot.
	 * <p>
	 * @return String
	 */
	public String getChunkCoords() {
		return chunkCoords;
	}
	
	/**
	 * <p>
	 * Returns true if the player is the owner of this plot.
	 * <p>
	 * @param owner
	 * @return booelan
	 */
	public boolean isOwner(String owner) {
		if(this.owner.equals(owner)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * <p>
	 * Returns true if the player is a coOwner of this plot.
	 * <p>
	 * @param coOwner
	 * @return boolean
	 */
	public boolean isCoOwner(String coOwner) {
		boolean is = false;
		for(String string:coOwners) {
			if(string.equals(coOwner)) {
				is = true;
				break;
			}
		}
		return is;
	}

	/**
	 * <p>
	 * Returns true if this plot is for sale.
	 * <p>
	 * @return boolean
	 */
	public boolean isForSale() {
		return isForSale;
	}
	
	/**
	 * <p>
	 * Set this plot for sale with saving it in the file.
	 * <p>
	 * @param file
	 * @param isForSale
	 * @param salePrice	Ignored if isForSale is 'false'
	 * @throws PlotIsAlreadyForSaleException
	 * @throws PlotIsNotForSaleException
	 */
	public void setForSale(File file,boolean isForSale,double salePrice) throws PlotIsAlreadyForSaleException, PlotIsNotForSaleException {
		if(this.isForSale && isForSale) {
			throw new PlotIsAlreadyForSaleException(chunkCoords);
		}
		else {
			setForSale(isForSale);
			this.isForSale = isForSale;
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Town." + townName + ".Plots." + chunkCoords + ".isForSale", isForSale);
			save(file,config);
			if(isForSale) {
				setSalePrice(file, salePrice);
			}
		}
	}

	/**
	 * <p>
	 * Returns the salePrice for this slot.
	 * <p>
	 * @return double
	 */
	public double getSalePrice() {
		return salePrice;
	}

	/**
	 * <p>
	 * Set the salePrice for this plot.
	 * <p>
	 * @param file
	 * @param salePrice
	 * @throws PlotIsNotForSaleException
	 */
	public void setSalePrice(File file,double salePrice) throws PlotIsNotForSaleException {
		if(!isForSale) {
			throw new PlotIsNotForSaleException(chunkCoords);
		}
		else {
			setSalePrice(salePrice);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Town." + townName + ".Plots." + chunkCoords + ".salePrice", salePrice);
			save(file,config);
		}
	}
	
	/**
	 * <p>
	 * Set 'salePrice' without saving.
	 * <p>
	 * @param salePrice
	 */
	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}
	
	/**
	 * <p>
	 * Saves a config in a file.
	 * <p>
	 * @param file
	 * @param config
	 */
	private void save(File file,FileConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Plot loadPlot(File file,String townName,String coords) throws ChunkNotClaimedByThisTownException {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(config.getStringList("Towns." + townName + ".chunks").contains(coords)) {
			Plot plot = new Plot(file, config.getString("Town." + townName + ".Plots." + coords + ".owner"), coords, townName);
			plot.setForSale(config.getBoolean("Town." + townName + ".Plots." + coords + ".isForSale"));
			plot.setCoOwners(config.getStringList("Town." + townName + ".Plots." + coords + ".coOwners"));
			return plot;
		}
		else {
			throw new ChunkNotClaimedByThisTownException(coords);
		}
	}
}
