package com.ue.utils;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.exceptions.banksystem.PlayerDoesNotExistException;

public abstract class LimitationUtils {

	/**
	 * <p>
	 * Returns true if player has not reached the max number of joined towns.
	 * <p>
	 * @param playerfile
	 * @param configFile
	 * @param player
	 * @return boolean
	 * @throws PlayerDoesNotExistException
	 */
	public static boolean playerReachedMaxTowns(File playerfile,FileConfiguration configFile,String player) throws PlayerDoesNotExistException {
		if(!PaymentUtils.playerExists(playerfile, player)) {
			throw new PlayerDoesNotExistException(player);
		}
		else {
			FileConfiguration pConfig = YamlConfiguration.loadConfiguration(playerfile);
			if(pConfig.getStringList(player + ".joinedTowns").size() <= configFile.getInt("MaxJoinedTowns")) {
				return false;
			}
			else {
				return true;
			}
		}
	}
	
	//TODO other limitation methods
}
