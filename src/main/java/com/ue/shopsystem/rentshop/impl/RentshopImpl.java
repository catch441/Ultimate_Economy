package com.ue.shopsystem.rentshop.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.player.EconomyPlayer;
import com.ue.shopsystem.playershop.impl.PlayershopImpl;
import com.ue.shopsystem.rentshop.api.Rentshop;
import com.ue.shopsystem.rentshop.api.RentshopController;

import ultimate_economy.UEVillagerType;
import ultimate_economy.Ultimate_Economy;

public class RentshopImpl extends PlayershopImpl implements Rentshop{

	private double rentalFee;
	private long rentUntil;
	private boolean rentable; //true, if the shop is not rented
	private Inventory rentShopGUIInv; // a GUI to rent this shop.
	
	/**
	 * Constructor for creating a new rentShop.
	 * No validation, if the shopId is unique.
	 * 
	 * @param dataFolder
	 * @param spawnLocation
	 * @param size
	 * @param shopId
	 * @param rentalFee
	 */
	public RentshopImpl(File dataFolder, Location spawnLocation, int size, String shopId, double rentalFee) {
		super(dataFolder, "RentShop#" + shopId,"", shopId, spawnLocation, size);
		saveRentalFeeToFile(rentalFee);
		this.rentalFee = rentalFee;
		saveRentableToFile(true);
		setupRentShopGUI();
		setupVillager();
	}
	
	/**
	 * Constructor for loading an existing playershop.
	 * No validation, if the shopId is unique.
	 * 
	 * @param dataFolder
	 * @param server
	 * @param shopId
	 */
	public RentshopImpl(File dataFolder, Server server, String shopId) {
		super(dataFolder, server, null,shopId);
		loadRentalFee();
		loadRentable();
		loadRentUntil();
		setupRentShopGUI();
		setupVillager();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////overridden
	
	/**
	 * Overridde, because the position didn't have to be validatet for build permissions like the parent playershop
	 */
	@Override
	public void moveShop(Location location) throws TownSystemException {
		saveLocationToFile(location);
		villager.teleport(location);
	}
	
	/**
	 * Overridden, because the naming convention is a other
	 * and a check for rentable.
	 */
	@Override
	public void changeShopName(String name) throws ShopSystemException {
		if(!isRentable()) {
			//Name validation
			if(name.contains("_" )) {
				throw new ShopSystemException(ShopSystemException.INVALID_CHAR_IN_SHOP_NAME);
			} else {
				saveShopNameToFile(name);
				villager.setCustomName(name + "_" + owner);
				changeInventoryNames(name);
			}
		} else {
			if(name.contains("_" )) {
				throw new ShopSystemException(ShopSystemException.INVALID_CHAR_IN_SHOP_NAME);
			} else {
				saveShopNameToFile(name);
				villager.setCustomName(name + "#" + getShopId());
			}
		}
	}
	
	/**
	 * NOT FOR COMMERCIAL USE.
	 * <p>
	 * Overridden, because of the rentable validation.
	 * Only possible, if the shop is not rented.
	 * 
	 */
	@Override
	public void changeOwner(String newOwner) throws PlayerException, ShopSystemException {
		if(isRentable()) {
			this.owner = newOwner;
			saveOwnerToFile(newOwner);
		}
	}
	
	/**
	 * Overridden, because of the rentable validation.
	 * Returns null, if the shop is free.
	 * 
	 * @return owner 
	 */
	@Override
	public String getOwner() {
		if(!rentable) {
			return owner;
		} else {
			return null;
		}
	}
	
	/**
	 * Only, if the shop is not rented.
	 */
	@Override
	public void changeShopSize(int newSize) throws ShopSystemException {
		if(isRentable()) {
			super.changeShopSize(newSize);
		} else {
			throw new ShopSystemException(ShopSystemException.RENTED);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////setup
	
	private void setupVillager() {
		// set the type of the villager
		villager.setMetadata("ue-type", new FixedMetadataValue(Ultimate_Economy.getInstance, UEVillagerType.PLAYERSHOP_RENTABLE));
		//if not rentable, then change to the custom name choosen by the tenant
		if(!isRentable()) {
			villager.setCustomName(getName() + "_" + owner);		
		} else {
			villager.setCustomName("RentShop#" + getShopId());
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////save file edit methods
	
	/**
	 * --Save file edit method--
	 * <p>
	 * Not for commercial use.
	 * <p>
	 * Saves the rentable state of the shop to the savefile.
	 * 
	 * @param id
	 */
	private void saveRentableToFile(boolean rentable) {
		this.rentable = rentable;
		config = YamlConfiguration.loadConfiguration(file);
		config.set("Rentable", rentable);
		save();
	}
	
	/**
	 * --Save file edit method--
	 * <p>
	 * Not for commercial use.
	 * <p>
	 * Saves the rental fee the shop to the savefile.
	 * 
	 * @param rentalFee
	 */
	private void saveRentalFeeToFile(double rentalFee) {
		config = YamlConfiguration.loadConfiguration(file);
		config.set("RentalFee", rentalFee);
		save();
	}
	
	/**
	 * --Save file edit method--
	 * <p>
	 * Not for commercial use.
	 * <p>
	 * Saves the rent until time to the savefile.
	 * 
	 * @param timeUntil
	 */
	private void saveRentUntilTimeToFile(long timeUntil) {
		rentUntil = timeUntil;
		config = YamlConfiguration.loadConfiguration(file);
		config.set("RentUntil", timeUntil);
		save();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////Save file read / get methods
	
	/**
	 * --Save file read method--
	 * <p>
	 * Reads the rental fee
	 * 
	 */
	private void loadRentalFee() {
		config = YamlConfiguration.loadConfiguration(file);
		rentalFee = config.getInt("RentalFee");
	}
	
	/**
	 * --Save file read method--
	 * <p>
	 * Reads the rentable value.
	 * 
	 */
	private void loadRentable() {
		config = YamlConfiguration.loadConfiguration(file);
		rentable = config.getBoolean("Rentable");
	}
	
	/**
	 * --Save file read method--
	 * <p>
	 * Reads the rent until value.
	 * 
	 */
	private void loadRentUntil() {
		config = YamlConfiguration.loadConfiguration(file);
		rentUntil = config.getLong("RentUntil");
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////get methods
	
	public boolean isRentable() {
		return rentable;
	}
	
	/**
	 * --Get Method--
	 * <p>
	 * Returns the rental fee amount.
	 * 
	 * @return rentalFee
	 */
	public double getRentalFee() {
		return rentalFee;
	}
	
	public double getRentUntil() {
		return rentUntil;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////rent shop methods
	
	public void openRentGUI(Player player) {
		if(isRentable()) {
			player.openInventory(rentShopGUIInv);
		}
	}
	
	/**
	 * --RentShop Method--
	 * <p>
	 * Rent this shop for a given player.
	 * 
	 * @param player
	 * @throws ShopSystemException should not be thrown
	 * @throws PlayerException should not be thrown
	 */
	public void rentShop(String player, int duration) throws ShopSystemException, PlayerException {
		if(isRentable()) {
			EconomyPlayer economyPlayer = EconomyPlayer.getEconomyPlayerByName(player);
			//throws a playerexception, if the player has not enough money.
			economyPlayer.decreasePlayerAmount(duration*rentalFee, true);
			changeOwner(player);
			saveRentableToFile(false);
			saveRentUntilTimeToFile(Calendar.getInstance().getTimeInMillis() + (86400000*duration));
			changeShopName("Shop#" + getShopId());
		} else {
			throw new ShopSystemException(ShopSystemException.RENTED);
		}
	}
	
	public void resetShop() {
		saveOwnerToFile("");
		saveRentUntilTimeToFile(0L);
		saveRentableToFile(true);
		changeProfession(Profession.NITWIT);
		try {
			changeShopName("RentShop");
		} catch (ShopSystemException e1) {
			//never happens
		}
		//remove all shopitems
		for (int i=0; i<(size-2); i++) {
			try {
				removeShopItem(i);
			} catch (ShopSystemException e) {}
		}
	}
	
	/**
	 * --RentShop Method--
	 * <p>
	 * Change the rental fee of this shop.
	 * 
	 * @param fee
	 * @throws PlayerException PlayerException.INVALID_NUMBER
	 */
	public void changeRentalFee(double fee) throws PlayerException {
		if(fee < 0) {
			throw new PlayerException(PlayerException.INVALID_NUMBER);
		} else {
			saveRentalFeeToFile(fee);
			rentalFee = fee;
		}
	}
	
	/**
	 * --RentShop Method--
	 * <p>
	 * Setup the rent shop GUI for a rentable shop.
	 * 
	 */
	private void setupRentShopGUI() {
		rentShopGUIInv = Bukkit.createInventory(villager, size, getName());
		List<String> loreList = new ArrayList<>();
		loreList.add(ChatColor.GOLD + "RentalFee: " + ChatColor.GREEN + getRentalFee());
		ItemStack itemStack = new ItemStack(Material.GREEN_WOOL, 1);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "Rent");
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		rentShopGUIInv.setItem(0, itemStack);
		loreList.clear();
		loreList.add(ChatColor.GOLD + "Duration: " + ChatColor.GREEN + 1 + ChatColor.GOLD + " Day");
		itemStack.setType(Material.CLOCK);
		meta = itemStack.getItemMeta();
		meta.setLore(loreList);
		meta.setDisplayName(ChatColor.YELLOW + "Duration");
		itemStack.setItemMeta(meta);
		rentShopGUIInv.setItem(1, itemStack);
		itemStack = getSkull(PLUS, "plus");
		rentShopGUIInv.setItem(3, itemStack);
		itemStack = getSkull(ONE, "one");
		meta = itemStack.getItemMeta();
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		rentShopGUIInv.setItem(4, itemStack);
		itemStack = getSkull(SEVEN, "seven");
		meta = itemStack.getItemMeta();
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		rentShopGUIInv.setItem(5, itemStack);
	}
	
	/**
	 * --RentShop Method--
	 * <p>
	 * Handles the clickevent for the rentGUI.
	 * 
	 * @param event InventoryClickEvent
	 * @throws PlayerException 
	 * @throws ShopSystemException 
	 */
	public void handleRentShopGUIClick(InventoryClickEvent event) throws ShopSystemException, PlayerException {
		if (event.getCurrentItem().getItemMeta() != null) {
			String durationString = event.getInventory().getItem(1).getItemMeta().getLore().get(0);
			durationString = ChatColor.stripColor(durationString);
			int duration = Integer.valueOf(durationString.substring(durationString.indexOf(" ") + 1, durationString.lastIndexOf(" ")));
			String operation = event.getInventory().getItem(3).getItemMeta().getDisplayName();
			String command = event.getCurrentItem().getItemMeta().getDisplayName();
			command = ChatColor.stripColor(command);
			switch(command) {
				case "plus":
					switchPlusMinusRentGUI("plus");
					break;
				case "minus":
					switchPlusMinusRentGUI("minus");
					break;
				case "one":
					if(operation.equals("plus")) {
						if(duration < RentshopController.getMaxRentedDays()) {
							duration++;
						}
					} else if(duration > 1){
						duration--;
					}
					refreshDurationOnRentGUI(duration);
					refreshRentalFeeOnRentGUI(duration);
					break;
				case "seven":
					if(operation.equals("plus")) {
						if(duration < RentshopController.getMaxRentedDays()) {
							duration += 7;
							if(duration > RentshopController.getMaxRentedDays()) {
								duration = RentshopController.getMaxRentedDays();
							}
						}
					} else if(duration > 7){
						duration -= 7;
					}
					refreshDurationOnRentGUI(duration);
					refreshRentalFeeOnRentGUI(duration);
					break;
				case "Rent": 
					rentShop(event.getWhoClicked().getName(), duration);
					event.getWhoClicked().sendMessage(
							ChatColor.GOLD + Ultimate_Economy.messages.getString("rent_rented"));
					event.getWhoClicked().closeInventory();
					break;
			}
		}
	}
	
	private void refreshRentalFeeOnRentGUI(int duration) {
		List<String> loreList = new ArrayList<>();
		loreList.add(ChatColor.GOLD + "RentalFee: " + ChatColor.GREEN + (duration * getRentalFee()));
		ItemStack stack = rentShopGUIInv.getItem(0);
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(loreList);
		stack.setItemMeta(meta);
	}
	
	private void refreshDurationOnRentGUI(int duration) {
		List<String> loreList = new ArrayList<>();
		if(duration > 1) {
			loreList.add(ChatColor.GOLD + "Duration: " +ChatColor.GREEN + duration + ChatColor.GOLD + " Days");

		} else {
			loreList.add(ChatColor.GOLD + "Duration: " +ChatColor.GREEN + duration + ChatColor.GOLD + " Day");
		}
		ItemStack stack = rentShopGUIInv.getItem(5);
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(loreList);
		stack.setItemMeta(meta);
		stack = rentShopGUIInv.getItem(4);
		meta = stack.getItemMeta();
		meta.setLore(loreList);
		stack.setItemMeta(meta);
		stack = rentShopGUIInv.getItem(1);
		meta = stack.getItemMeta();
		meta.setLore(loreList);
		stack.setItemMeta(meta);
	}
	
	private void switchPlusMinusRentGUI(String state) {
		if (state.equals("plus")) {
			ItemStack item = getSkull(MINUS, "minus");
			rentShopGUIInv.setItem(3, item);
		} else {
			ItemStack item = getSkull(PLUS, "plus");
			rentShopGUIInv.setItem(3, item);
		}
	}
}
