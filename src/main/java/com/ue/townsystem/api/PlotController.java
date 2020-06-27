package com.ue.townsystem.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.townsystem.impl.PlotImpl;
import com.ue.townsystem.impl.TownImpl;

public class PlotController {

    /**
     * Returns a Plot loaded by the parameters from the savefile. Load
     * Economyplayers before.
     * 
     * @param townImpl
     * @param coords
     * @return Plot
     * @throws TownSystemException
     * @throws PlayerException
     *             if EconomyPlayers are not loaded before.
     */
    public static Plot loadPlot(TownImpl townImpl, String coords) throws TownSystemException, PlayerException {
	FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
	if (config.getStringList("Towns." + townImpl.getTownName() + ".chunks").contains(coords)) {
	    EconomyPlayer economyPlayer = EconomyPlayerController.getEconomyPlayerByName(
		    config.getString("Towns." + townImpl.getTownName() + ".Plots." + coords + ".owner"));
	    PlotImpl plotImpl = new PlotImpl(townImpl, economyPlayer, coords);
	    plotImpl.setIsForSale(
		    config.getBoolean("Towns." + townImpl.getTownName() + ".Plots." + coords + ".isForSale"));
	    List<EconomyPlayer> residents = new ArrayList<>();
	    for (String name : config
		    .getStringList("Towns." + townImpl.getTownName() + ".Plots." + coords + ".coOwners")) {
		residents.add(EconomyPlayerController.getEconomyPlayerByName(name));
	    }
	    plotImpl.setResidents(residents);
	    plotImpl.setSalePrice(
		    config.getDouble("Towns." + townImpl.getTownName() + ".Plots." + coords + ".salePrice"));
	    if (plotImpl.isForSale()) {
		Location location = new Location(
			Bukkit.getWorld(config.getString(
				"Towns." + townImpl.getTownName() + ".Plots." + coords + ".SaleVillager.world")),
			config.getDouble("Towns." + townImpl.getTownName() + ".Plots." + coords + ".SaleVillager.x"),
			config.getDouble("Towns." + townImpl.getTownName() + ".Plots." + coords + ".SaleVillager.y"),
			config.getDouble("Towns." + townImpl.getTownName() + ".Plots." + coords + ".SaleVillager.z"));
		plotImpl.spawnSaleVillager(location);
	    }
	    return plotImpl;
	} else {
	    throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_NOT_CLAIMED_BY_TOWN);
	}
    }
}
