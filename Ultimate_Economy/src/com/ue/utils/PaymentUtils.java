package com.ue.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

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
	 * @return file (playerfile)
	 * @throws PlayerDoesNotExistException
	 * @throws PlayerHasNotEnoughtMoneyException
	 */
	public static File payToOtherPlayer(File playerfile,String sender,String reciever,double amount,boolean youAreSender) throws PlayerDoesNotExistException, PlayerHasNotEnoughtMoneyException {
		if(!playerExists(playerfile, sender)) {
			throw new PlayerDoesNotExistException(sender);
		}
		else if(!playerExists(playerfile, reciever)) {
			throw new PlayerDoesNotExistException(reciever);
		}
		else {
			playerfile = increasePlayerAmount(playerfile, reciever, amount);
			return decreasePlayerAmount(playerfile, sender, amount,youAreSender);
		}
	}
	 /**
	  * <p>
	  * Increase the bank amount of a player.
	  * <p>
	  * @param playerfile
	  * @param player
	  * @param amount
	  * @return file (playerfile)
	  * @throws PlayerDoesNotExistException
	  */
	public static File increasePlayerAmount(File playerfile,String player, double amount) throws PlayerDoesNotExistException {
		FileConfiguration config = YamlConfiguration.loadConfiguration(playerfile);
		Double realAmount = getPlayerBankAmount(playerfile, player);
		realAmount += amount;
		config.set(player + ".account amount", realAmount);
		playerfile = save(playerfile,config);
		if(Bukkit.getPlayer(player).isOnline()) {
			updateScoreBoard(playerfile, Bukkit.getPlayer(player));
		}
		return playerfile;
	}
	
	/**
	 * <p>
	 * Decrease the bank amount of a player.
	 * <p>
	 * @param playerfile
	 * @param player
	 * @param amount
	 * @return file (playerfile)
	 * @throws PlayerDoesNotExistException
	 * @throws PlayerHasNotEnoughtMoneyException 
	 */
	public static File decreasePlayerAmount(File playerfile,String player,double amount,boolean personal) throws PlayerDoesNotExistException, PlayerHasNotEnoughtMoneyException {
		if(playerHasEnoughtMoney(playerfile, player, amount)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(playerfile);
			Double realAmount = getPlayerBankAmount(playerfile, player);
			realAmount -= amount;
			config.set(player + ".account amount", realAmount);
			playerfile = save(playerfile,config);
			if(Bukkit.getPlayer(player).isOnline()) {
				updateScoreBoard(playerfile, Bukkit.getPlayer(player));
			}
			return playerfile;
		}
		else {
			if(personal) {
				throw new PlayerHasNotEnoughtMoneyException(player,true);
			}
			else {
				throw new PlayerHasNotEnoughtMoneyException(player);
			}
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
	
	private static File save(File file,FileConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * <p>
	 * Set the bank scoreboard of a player.
	 * <p>
	 * @param file
	 * @param p
	 * @param score
	 */
	public static void setScoreboard(File file,Player p,int score) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file); 
		if(!config.getBoolean(p.getName() + ".bank")) {
			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective o = board.registerNewObjective("test", "dummy","§6§lBank");
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			o.getScore("§6Money:").setScore(score);
			p.setScoreboard(board);
		}
		else {
			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			p.setScoreboard(board);
		}
	}
	
	/**
	 * <p>
	 * Update a bank scoreboard of a player.
	 * <p>
	 * @param file
	 * @param p
	 */
	public static void updateScoreBoard(File file,Player p) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		int score = (int) config.getDouble(p.getName() + ".account amount");
		setScoreboard(file,p,score);
	}
	
	/*public static void updateScoreBoardWithTownAmount(String townName,Integer score,Player p) {
		Scoreboard board = p.getScoreboard();
		if(board != null) {
			Objective o = board.registerNewObjective("test", "dummy","§6§l" + townName);
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			o.getScore("§6Money:").setScore(score);
			p.setScoreboard(board);
		}
	}*/	
}
