package com.ue.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.exceptions.banksystem.PlayerDoesNotExistException;
import com.ue.exceptions.banksystem.PlayerHasNotEnoughtMoneyException;

public abstract class PaymentUtils {

	/**
	 * <p>
	 * Returns true if player has at minimum 'amount' on the bank acoount.
	 * <p>
	 * @param playerfile
	 * @param player
	 * @param amount
	 * @return boolean
	 * @throws PlayerDoesNotExistException
	 */
	public static boolean playerHasEnoughtMoney(File playerfile,String player,double amount) throws PlayerDoesNotExistException {
		if(playerExists(playerfile, player)) {
			if(getPlayerBankAmount(playerfile, player) >= amount) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			throw new PlayerDoesNotExistException(player);
		}
	}
	
	/**
	 * <p>
	 * Transfers a money amount from a player to anouther player.
	 * <p>
	 * @param playerfile
	 * @param sender
	 * @param reciever
	 * @param amount
	 * @param youAreSender (true if the sender is the command executor)
	 * @throws PlayerDoesNotExistException
	 * @throws PlayerHasNotEnoughtMoneyException
	 */
	public static void payToOtherPlayer(File playerfile,String sender,String reciever,double amount,boolean youAreSender) throws PlayerDoesNotExistException, PlayerHasNotEnoughtMoneyException {
		if(!playerExists(playerfile, sender)) {
			throw new PlayerDoesNotExistException(sender);
		}
		else if(!playerExists(playerfile, reciever)) {
			throw new PlayerDoesNotExistException(reciever);
		}
		else if(!playerHasEnoughtMoney(playerfile, sender, amount)) {
			if(youAreSender) {
				throw new PlayerHasNotEnoughtMoneyException(sender,true);
			}
			else {
				throw new PlayerHasNotEnoughtMoneyException(sender);
			}
		}
		else {
			increasePlayerAmount(playerfile, reciever, amount);
			decreasePlayerAmount(playerfile, sender, amount);
		}
	}
	 /**
	  * <p>
	  * Increase the bank amount of a player.
	  * <p>
	  * @param playerfile
	  * @param player
	  * @param amount
	  * @throws PlayerDoesNotExistException
	  */
	public static void increasePlayerAmount(File playerfile,String player, double amount) throws PlayerDoesNotExistException {
		FileConfiguration config = YamlConfiguration.loadConfiguration(playerfile);
		Double realAmount = getPlayerBankAmount(playerfile, player);
		realAmount += amount;
		config.set(player + ".account amount", realAmount);
		save(playerfile,config);
	}
	
	/**
	 * <p>
	 * Decrease the bank amount of a player.
	 * <p>
	 * @param playerfile
	 * @param player
	 * @param amount
	 * @throws PlayerDoesNotExistException
	 */
	public static void decreasePlayerAmount(File playerfile,String player,double amount) throws PlayerDoesNotExistException {
		if(playerHasEnoughtMoney(playerfile, player, amount)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(playerfile);
			Double realAmount = getPlayerBankAmount(playerfile, player);
			realAmount -= amount;
			config.set(player + ".account amount", realAmount);
			save(playerfile,config);
		}
	}
	
	/**
	 * <p>
	 * Get the bank amount of a player.
	 * <p>
	 * @param playerfile
	 * @param player
	 * @return double
	 * @throws PlayerDoesNotExistException 
	 */
	public static double getPlayerBankAmount(File playerfile,String player) throws PlayerDoesNotExistException {
		FileConfiguration config = YamlConfiguration.loadConfiguration(playerfile);
		if(playerExists(playerfile,player)) {
			return config.getDouble(player + ".account amount");
		}
		else {
			throw new PlayerDoesNotExistException(player);
		}
	}
	
	/**
	 * <p>
	 * Returns true if the player was on this server.
	 * <p>
	 * @param playerfile
	 * @param player
	 * @return boolean
	 */
	public static boolean playerExists(File playerfile,String player) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(playerfile);
		if(config.getStringList("Player").contains(player)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private static void save(File file,FileConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
