package com.ue.shopsystem.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.shopsystem.api.Rentshop;
import com.ue.ultimate_economy.UltimateEconomy;

public class RentshopImpl extends PlayershopImpl implements Rentshop {

	private double rentalFee;
	private long rentUntil;
	private boolean rentable; // true, if the shop is not rented
	private Inventory rentShopGUIInv; // a GUI to rent this shop.

	/**
	 * Constructor for creating a new rentShop. No validation, if the shopId is
	 * unique.
	 * 
	 * @param spawnLocation
	 * @param size
	 * @param shopId
	 * @param rentalFee
	 */
	public RentshopImpl(Location spawnLocation, int size, String shopId, double rentalFee) {
		super("RentShop#" + shopId, null, shopId, spawnLocation, size);
		setupNewRentshop(rentalFee);
	}

	/**
	 * Constructor for loading an existing playershop. No validation, if the shopId
	 * is unique.
	 * 
	 * @param shopId
	 * @throws TownSystemException
	 * @throws PlayerException
	 * @throws ShopSystemException
	 * @throws GeneralEconomyException
	 */
	public RentshopImpl(String shopId)
			throws TownSystemException, PlayerException, GeneralEconomyException, ShopSystemException {
		super(null, shopId);
		loadExistingRentshop();
	}

	/*
	 * API methods
	 * 
	 */

	/**
	 * Overridde, because the position didn't have to be validated for build
	 * permissions like the parent playershop. {@inheritDoc}
	 */
	@Override
	public void moveShop(Location location) throws TownSystemException {
		setupShopLocation(location);
		getShopVillager().teleport(location);
	}

	@Override
	public boolean isRentable() {
		return rentable;
	}

	@Override
	public long getRentUntil() {
		return rentUntil;
	}

	@Override
	public double getRentalFee() {
		return rentalFee;
	}

	/**
	 * Overridden, because the naming convention is a other and a check for rentable
	 * is needed. {@inheritDoc}
	 */
	@Override
	public void changeShopName(String name) throws ShopSystemException, GeneralEconomyException {
		getValidationHandler().checkForValidShopName(name);
		if (!isRentable()) {
			super.changeShopName(name);
		} else {
			setupShopName(name);
			getShopVillager().setCustomName(name + "#" + getShopId());
		}
	}

	@Override
	public void rentShop(EconomyPlayer player, int duration)
			throws ShopSystemException, GeneralEconomyException, PlayerException {
		getValidationHandler().checkForIsRentable(isRentable());
		// throws a playerexception, if the player has not enough money.
		player.decreasePlayerAmount(duration * getRentalFee(), true);
		changeOwner(player);
		changeShopName("Shop#" + getShopId());
		rentable = false;
		rentUntil = Calendar.getInstance().getTimeInMillis() + (86400000 * duration);
		getSavefileHandler().saveRentable(isRentable());
		getSavefileHandler().saveRentUntil(getRentUntil());
	}

	@Override
	public void changeRentalFee(double fee) throws GeneralEconomyException {
		getValidationHandler().checkForPositiveValue(fee);
		rentalFee = fee;
		getSavefileHandler().saveRentalFee(fee);
	}

	/**
	 * Overriden, because of is not rented check.
	 */
	@Override
	public void changeShopSize(int newSize) throws ShopSystemException, PlayerException, GeneralEconomyException {
		getValidationHandler().checkForIsRentable(isRentable());
		super.changeShopSize(newSize);
	}

	/**
	 * Overridden, because of the rentable validation. Only possible, if the shop is
	 * not rented. Should not be used. Use the rentShop() method instead.
	 * {@inheritDoc}
	 */
	@Override
	public void changeOwner(EconomyPlayer newOwner) throws PlayerException, ShopSystemException {
		getValidationHandler().checkForIsRentable(isRentable());
		super.changeOwner(newOwner);
	}

	@Override
	public void openRentGUI(Player player) throws ShopSystemException {
		getValidationHandler().checkForIsRentable(isRentable());
		player.openInventory(getRentShopGuiInventory());
	}

	@Override
	public Inventory getRentShopGuiInventory() {
		return rentShopGUIInv;
	}

	@Override
	public void resetShop() throws ShopSystemException, GeneralEconomyException {
		setOwner(null);
		getSavefileHandler().saveOwner(null);
		rentUntil = 0L;
		rentable = true;
		getSavefileHandler().saveRentUntil(0L);
		getSavefileHandler().saveRentable(true);
		changeProfession(Profession.NITWIT);
		changeShopName("RentShop");
		removeAllItems();
	}

	/*
	 * Utility methods
	 * 
	 */

	private void removeAllItems() throws ShopSystemException, GeneralEconomyException {
		for (int i = 0; i < (getSize() - 2); i++) {
			if (!getValidationHandler().isSlotEmpty(i, getShopInventory(), 2)) {
				removeShopItem(i);
			}
		}
	}

	/*
	 * Rentshop gui utility methods
	 * 
	 */

	private void refreshRentalFeeOnRentGUI(int duration) {
		List<String> loreList = new ArrayList<>();
		loreList.add(ChatColor.GOLD + "RentalFee: " + ChatColor.GREEN + (duration * getRentalFee()));
		ItemStack stack = getRentShopGuiInventory().getItem(0);
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(loreList);
		stack.setItemMeta(meta);
	}

	private void refreshDurationOnRentGUI(int duration) {
		List<String> loreList = new ArrayList<>();
		if (duration > 1) {
			loreList.add(ChatColor.GOLD + "Duration: " + ChatColor.GREEN + duration + ChatColor.GOLD + " Days");

		} else {
			loreList.add(ChatColor.GOLD + "Duration: " + ChatColor.GREEN + duration + ChatColor.GOLD + " Day");
		}
		ItemStack stack = getRentShopGuiInventory().getItem(5);
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(loreList);
		stack.setItemMeta(meta);
		stack = getRentShopGuiInventory().getItem(4);
		meta = stack.getItemMeta();
		meta.setLore(loreList);
		stack.setItemMeta(meta);
		stack = getRentShopGuiInventory().getItem(1);
		meta = stack.getItemMeta();
		meta.setLore(loreList);
		stack.setItemMeta(meta);
	}

	private void switchPlusMinusRentGUI(String state) {
		if ("plus".equals(state)) {
			ItemStack item = getSkull(MINUS, "minus");
			getRentShopGuiInventory().setItem(3, item);
		} else {
			ItemStack item = getSkull(PLUS, "plus");
			getRentShopGuiInventory().setItem(3, item);
		}
	}

	/*
	 * Handle rentshop gui click
	 * 
	 */

	@Override
	public void handleRentShopGUIClick(InventoryClickEvent event)
			throws ShopSystemException, GeneralEconomyException, PlayerException {
		if (event.getCurrentItem().getItemMeta() != null) {
			String durationString = event.getInventory().getItem(1).getItemMeta().getLore().get(0);
			durationString = ChatColor.stripColor(durationString);
			int duration = Integer.valueOf(
					durationString.substring(durationString.indexOf(" ") + 1, durationString.lastIndexOf(" ")));
			String operation = event.getInventory().getItem(3).getItemMeta().getDisplayName();
			String command = event.getCurrentItem().getItemMeta().getDisplayName();
			command = ChatColor.stripColor(command);
			switch (command) {
			case "plus":
				switchPlusMinusRentGUI("plus");
				break;
			case "minus":
				switchPlusMinusRentGUI("minus");
				break;
			case "one":
				handlePlusMinusOneGuiClick(duration, operation);
				break;
			case "seven":
				handlePlusMinusSevenGuiClick(duration, operation);
				break;
			case "Rent":
				rentShop(EconomyPlayerController.getEconomyPlayerByName(event.getWhoClicked().getName()), duration);
				event.getWhoClicked().sendMessage(MessageWrapper.getString("rent_rented"));
				event.getWhoClicked().closeInventory();
				break;
			default:
				break;
			}
		}
	}

	private void handlePlusMinusSevenGuiClick(int duration, String operation) {
		if ("plus".equals(operation)) {
			if (duration < ConfigController.getMaxRentedDays()) {
				duration += 7;
				if (duration > ConfigController.getMaxRentedDays()) {
					duration = ConfigController.getMaxRentedDays();
				}
			}
		} else if (duration > 7) {
			duration -= 7;
		}
		refreshDurationOnRentGUI(duration);
		refreshRentalFeeOnRentGUI(duration);
	}

	private void handlePlusMinusOneGuiClick(int duration, String operation) {
		if ("plus".equals(operation)) {
			if (duration < ConfigController.getMaxRentedDays()) {
				duration++;
			}
		} else if (duration > 1) {
			duration--;
		}
		refreshDurationOnRentGUI(duration);
		refreshRentalFeeOnRentGUI(duration);
	}

	/*
	 * Setup methods
	 * 
	 */

	private void setupNewRentshop(double rentalFee) {
		setupRentalFee(rentalFee);
		setupRentable();
		setupRentShopGUI();
		setupEconomyVillagerType();
	}

	private void setupEconomyVillagerType() {
		getShopVillager().setMetadata("ue-type",
				new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.PLAYERSHOP_RENTABLE));
	}

	private void setupVillagerName() {
		if (isRentable()) {
			getShopVillager().setCustomName("RentShop#" + getShopId());
		}
	}

	private void setupRentable() {
		this.rentable = true;
		getSavefileHandler().saveRentable(true);
	}

	private void setupRentalFee(double rentalFee) {
		this.rentalFee = rentalFee;
		getSavefileHandler().saveRentalFee(rentalFee);
	}

	private void setupRentShopGUI() {
		rentShopGUIInv = Bukkit.createInventory(getShopVillager(), 9, getName());
		List<String> loreList = new ArrayList<>();
		loreList.add(ChatColor.GOLD + "RentalFee: " + ChatColor.GREEN + getRentalFee());
		ItemStack itemStack = new ItemStack(Material.GREEN_WOOL, 1);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "Rent");
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		getRentShopGuiInventory().setItem(0, itemStack);
		loreList.clear();
		loreList.add(ChatColor.GOLD + "Duration: " + ChatColor.GREEN + 1 + ChatColor.GOLD + " Day");
		itemStack.setType(Material.CLOCK);
		meta = itemStack.getItemMeta();
		meta.setLore(loreList);
		meta.setDisplayName(ChatColor.YELLOW + "Duration");
		itemStack.setItemMeta(meta);
		getRentShopGuiInventory().setItem(1, itemStack);
		itemStack = getSkull(PLUS, "plus");
		getRentShopGuiInventory().setItem(3, itemStack);
		itemStack = getSkull(ONE, "one");
		meta = itemStack.getItemMeta();
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		getRentShopGuiInventory().setItem(4, itemStack);
		itemStack = getSkull(SEVEN, "seven");
		meta = itemStack.getItemMeta();
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		getRentShopGuiInventory().setItem(5, itemStack);
	}

	/*
	 * Loading methods
	 * 
	 */

	private void loadExistingRentshop() {
		rentalFee = getSavefileHandler().loadRentalFee();
		rentable = getSavefileHandler().loadRentable();
		rentUntil = getSavefileHandler().loadRentUntil();
		setupRentShopGUI();
		setupEconomyVillagerType();
		setupVillagerName();
	}
}
