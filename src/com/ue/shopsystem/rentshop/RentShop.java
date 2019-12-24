package com.ue.shopsystem.rentshop;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
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
import com.ue.shopsystem.playershop.PlayerShop;

import ultimate_economy.UEVillagerType;
import ultimate_economy.Ultimate_Economy;

public class RentShop extends PlayerShop {
	
	private static List<RentShop> rentShopList = new ArrayList<>();
	private static int maxRentedDays;

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
	private RentShop(File dataFolder, Location spawnLocation, int size, String shopId, double rentalFee) {
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
	private RentShop(File dataFolder, Server server, String shopId) {
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
	
	/**
	 * --Get Method--
	 * <p>
	 * Returns true, if this shop is not rented by a other player
	 * 
	 * @return rentable
	 */
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
	
	/**
	 * --Get Method--
	 * <p>
	 * Returns the rental until time in milliseconds.
	 * 
	 * @return rentUntil
	 */
	public double getRentUntil() {
		return rentUntil;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////rent shop methods
	
	/**
	 * --RentShop Method--
	 * <p>
	 * Opens the rentshop GUI, if the shop is not rented.
	 * 
	 * @param player
	 */
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
	
	/**
	 * --RentShop Method--
	 * <p>
	 * Resets the entire shop. Sets the shop back to the "rentable" state.
	 * Removes all items from the shop.
	 * 
	 */
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
	 * Reduces the remaining time of the rent.
	 */
	public void handleDaily() {
		if(!isRentable()) {
			if(Calendar.getInstance().getTimeInMillis() >= rentUntil) {
				resetShop();
			} else if((rentUntil - Calendar.getInstance().getTimeInMillis()) < 600000) {
				if (Bukkit.getPlayer(getOwner()) != null && Bukkit.getPlayer(getOwner()).isOnline()) {
					Bukkit.getPlayer(getOwner()).sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("rent_reminder"));
				}
			}
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
	 * @param event
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
						if(duration < maxRentedDays) {
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
						if(duration < maxRentedDays) {
							duration += 7;
							if(duration > maxRentedDays) {
								duration = maxRentedDays;
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
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////// static methods
	
	/**
	 * Returns a free unique id for a rentshop.
	 * 
	 * @return String
	 */
	public static String generateFreeRentShopId() {
		int id = -1;
		boolean free = false;
		while(!free) {
			id++;
			if(!getRentShopIdList().contains("R" + id)) {
				free = true;
			}
		}
		return "R" + id;
	}
	
	/**
	 * This method returns a list of rentshop ids.
	 * 
	 * @return List of Strings
	 */
	public static List<String> getRentShopIdList() {
		List<String> list = new ArrayList<>();
		for (RentShop shop : rentShopList) {
			list.add(shop.getShopId());
		}
		return list;
	}

	/**
	 * This method returns a rentshop by the shipId.
	 * 
	 * @param id
	 * @return RentShop
	 * @throws ShopSystemException
	 */
	public static RentShop getRentShopById(String id) throws ShopSystemException {
		for (RentShop shop : rentShopList) {
			if (shop.getShopId().equals(id)) {
				return shop;
			}
		}
		throw new ShopSystemException(ShopSystemException.SHOP_DOES_NOT_EXIST);
	}
	
	/**
	 * This method returns a rentshop by a unique name.
	 * <p>
	 * rented: name = name_owner unique name.
	 * <p>
	 * not rented: name = RentShop# + shopId
	 * 
	 * @param id
	 * @return RentShop
	 * @throws ShopSystemException
	 */
	public static RentShop getRentShopByUniqueName(String name) throws ShopSystemException {
		for (RentShop shop : rentShopList) {
			if(shop.isRentable()) {
				if (name.equals("RentShop#" + shop.getShopId())) {
					return shop;
				}
			} else {
				if (name.equals(shop.getName() + "_" + shop.getOwner())) {
					return shop;
				}
			}
		}
		throw new ShopSystemException(ShopSystemException.SHOP_DOES_NOT_EXIST);
	}
	
	/**
	 * This method returns a list of rentshop names.
	 * name = name_owner || RentShop# + id for unique names
	 * 
	 * @return List of Strings
	 */
	public static List<String> getRentShopUniqueNameList() {
		List<String> list = new ArrayList<>();
		for (RentShop shop : rentShopList) {
			if(shop.isRentable()) {
				list.add("RentShop#" + shop.getShopId());
			} else {
				list.add(shop.getName() + "_" + shop.getOwner());
			}
		}
		return list;
	}
	
	/*
	 * Returns all rentshops
	 * @return List<RentShop>
	 */
	public static List<RentShop> getRentShops() {
		return rentShopList;
	}

	/**
	 * This method should be used to create a new rentshop.
	 * 
	 * @param dataFolder
	 * @param spawnLocation
	 * @param size
	 * @param rentalFee
	 * @return RentShop
	 * @throws ShopSystemException
	 * @throws TownSystemException 
	 */
	public static RentShop createRentShop(File dataFolder, Location spawnLocation, int size, double rentalFee)
			throws ShopSystemException, TownSystemException {		
		if (size % 9 != 0) {
			throw new ShopSystemException(ShopSystemException.INVALID_INVENTORY_SIZE);
		} else if(rentalFee < 0) {
			throw new ShopSystemException(PlayerException.INVALID_NUMBER);
		} else {
			RentShop shop = new RentShop(dataFolder, spawnLocation, size, generateFreeRentShopId(), rentalFee);
			rentShopList.add(shop);
			return shop;
		}
	}

	/**
	 * This method should be used to delete a rentshop.
	 * 
	 * @param id
	 * @throws ShopSystemException
	 */
	public static void deleteRentShop(String name) throws ShopSystemException {
		RentShop shop = getRentShopByUniqueName(name);
		rentShopList.remove(shop);
		shop.deleteShop();
	}

	/**
	 * This method despawns all rentshop villager.
	 */
	public static void despawnAllVillagers() {
		for (RentShop shop : rentShopList) {
			shop.despawnVillager();
		}
	}

	/**
	 * This method loads all rentShops.
	 * 
	 * @param fileConfig
	 * @param dataFolder
	 * @param server
	 */
	public static void loadAllRentShops(FileConfiguration fileConfig, File dataFolder, Server server) {
		for (String shopId : fileConfig.getStringList("RentShopIds")) {
			File file = new File(dataFolder, shopId + ".yml");
			if (file.exists()) {
				rentShopList.add(new RentShop(dataFolder, server, shopId));
			} else {
				Bukkit.getLogger().log(Level.WARNING, ShopSystemException.CANNOT_LOAD_SHOP,
						new ShopSystemException(ShopSystemException.CANNOT_LOAD_SHOP));
			}
		}
	}
	
	/**
	 * This method sets the maxRentedDays value.
	 * 
	 * @param config
	 * @param days
	 * @throws PlayerException
	 */
	public static void setMaxRentedDays(FileConfiguration config,int days) throws PlayerException {
		if (days < 0) {
			throw new PlayerException(PlayerException.INVALID_NUMBER);
		} else {
			config.set("MaxRentedDays", days);
			maxRentedDays = days;
		}
	}
	
	/**
	 * Loads the maxRentedDays value.
	 * 
	 * @param fileConfig
	 */
	public static void setupConfig(FileConfiguration fileConfig) {
		if (!fileConfig.isSet("MaxRentedDays")) {
			fileConfig.set("MaxRentedDays", 14);
			maxRentedDays = 3;
		} else {
			maxRentedDays = fileConfig.getInt("MaxRentedDays");
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////// hidden static methods
	
	/**
	 * HIDDEN, NO FUNCTIONALITY
	 */
	public static void loadAllPlayerShops(FileConfiguration fileConfig, File dataFolder, Server server) {}
	/**
	 * HIDDEN, NO FUNCTIONALITY
	 */
	public static void deletePlayerShop(String name) throws ShopSystemException {}
	/**
	 * HIDDEN, NO FUNCTIONALITY
	 */
	public static void createPlayerShop(File dataFolder, String name, Location spawnLocation, int size, String playerName) {}
	/**
	 * HIDDEN, NO FUNCTIONALITY
	 */
	public static List<String> getPlayershopIdList() {return null;}
	/**
	 * HIDDEN, NO FUNCTIONALITY
	 */
	public static List<PlayerShop> getPlayerShops() {return null;}
	/**
	 * HIDDEN, NO FUNCTIONALITY
	 */
	public static PlayerShop getPlayerShopById(String id) throws ShopSystemException {return null;}
	/**
	 * HIDDEN, NO FUNCTIONALITY
	 */
	public static PlayerShop getPlayerShopByUniqueName(String name) throws ShopSystemException {return null;}
	/**
	 * HIDDEN, NO FUNCTIONALITY
	 */
	public static List<String> getPlayerShopUniqueNameList() {return null;}
	/**
	 * HIDDEN, NO FUNCTIONALITY
	 */
	public static String generateFreePlayerShopId() {return null;}
}
