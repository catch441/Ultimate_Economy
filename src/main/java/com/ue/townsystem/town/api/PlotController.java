package com.ue.townsystem.town.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.exceptions.TownSystemException;
import com.ue.townsystem.town.impl.PlotImpl;
import com.ue.townsystem.town.impl.TownImpl;

public class PlotController {

	/**
	 * Returns a Plot loaded by the parameters from the savefile.
	 * 
	 * @param townImpl
	 * @param coords
	 * @return Plot
	 * @throws TownSystemException
	 */
	public static Plot loadPlot(TownImpl townImpl, String coords) throws TownSystemException {
		FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
		if (config.getStringList("Towns." + townImpl.getTownName() + ".chunks").contains(coords)) {
			PlotImpl plotImpl = new PlotImpl(townImpl,config.getString("Towns." + townImpl.getTownName() + ".Plots." + coords + ".owner"), coords);
			plotImpl.setIsForSale(config.getBoolean("Towns." + townImpl.getTownName() + ".Plots." + coords + ".isForSale"));
			plotImpl.setCoOwners(config.getStringList("Towns." + townImpl.getTownName() + ".Plots." + coords + ".coOwners"));
			plotImpl.setSalePrice(config.getDouble("Towns." + townImpl.getTownName() + ".Plots." + coords + ".salePrice"));
			if (plotImpl.isForSale()) {
				Location location = new Location(
						Bukkit.getWorld(
								config.getString("Towns." + townImpl.getTownName() + ".Plots." + coords + ".SaleVillager.world")),
						config.getDouble("Towns." + townImpl.getTownName() + ".Plots." + coords + ".SaleVillager.x"),
						config.getDouble("Towns." + townImpl.getTownName() + ".Plots." + coords + ".SaleVillager.y"),
						config.getDouble("Towns." + townImpl.getTownName() + ".Plots." + coords + ".SaleVillager.z"));
				plotImpl.spawnSaleVillager(location);
			}
			return plotImpl;
		} else {
			throw new TownSystemException(TownSystemException.CHUNK_NOT_CLAIMED_BY_TOWN);
		}
	}
}
