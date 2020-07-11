package com.ue.shopsystem.impl;

import java.util.Calendar;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.api.Rentshop;
import com.ue.ultimate_economy.UltimateEconomy;

public class RentshopImpl extends PlayershopImpl implements Rentshop {

	private double rentalFee;
	private long rentUntil;
	private boolean rentable; // true, if the shop is not rented
	private RentshopRentGuiHandler rentGuiHandler;
	
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
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack)
			throws ShopSystemException, PlayerException, GeneralEconomyException {
		getValidationHandler().checkForIsRented(isRentable());
		super.addShopItem(slot, sellPrice, buyPrice, itemStack);
	}
	
	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public String editShopItem(int slot, String newAmount, String newSellPrice, String newBuyPrice)
			throws ShopSystemException, PlayerException, GeneralEconomyException {
		getValidationHandler().checkForIsRented(isRentable());
		return super.editShopItem(slot, newAmount, newSellPrice, newBuyPrice);
	}
	
	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public ShopItem getShopItem(int slot) throws GeneralEconomyException, ShopSystemException {
		getValidationHandler().checkForIsRented(isRentable());
		return super.getShopItem(slot);
	}
	
	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public ShopItem getShopItem(ItemStack stack) throws ShopSystemException {
		getValidationHandler().checkForIsRented(isRentable());
		return super.getShopItem(stack);
	}
	
	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public boolean isAvailable(int slot) throws ShopSystemException, GeneralEconomyException {
		getValidationHandler().checkForIsRented(isRentable());
		return super.isAvailable(slot);
	}
	
	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void openShopInventory(Player player) throws ShopSystemException {
		getValidationHandler().checkForIsRented(isRentable());
		super.openShopInventory(player);
	}
	
	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void decreaseStock(int slot, int stock) throws GeneralEconomyException, ShopSystemException {
		getValidationHandler().checkForIsRented(isRentable());
		super.decreaseStock(slot, stock);
	}
	
	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void increaseStock(int slot, int stock) throws GeneralEconomyException, ShopSystemException {
		getValidationHandler().checkForIsRented(isRentable());
		super.increaseStock(slot, stock);
	}
	
	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void removeShopItem(int slot) throws ShopSystemException, GeneralEconomyException {
		getValidationHandler().checkForIsRented(isRentable());
		super.removeShopItem(slot);
	}
	
	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void openStockpile(Player player) throws ShopSystemException {
		getValidationHandler().checkForIsRented(isRentable());
		super.openStockpile(player);
	}
	
	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void openSlotEditor(Player player, int slot) throws ShopSystemException, GeneralEconomyException {
		getValidationHandler().checkForIsRented(isRentable());
		super.openSlotEditor(player, slot);
	}
	
	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void openEditor(Player player) throws ShopSystemException {
		getValidationHandler().checkForIsRented(isRentable());
		super.openEditor(player);
	}

	/**
	 * Overridde, because the position didn't have to be validated for build
	 * permissions like the parent playershop. {@inheritDoc}
	 */
	@Override
	public void moveShop(Location location) {
		setupShopLocation(location);
		getShopVillager().teleport(location);
	}
	
	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void buyShopItem(int slot, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, PlayerException, ShopSystemException {
		getValidationHandler().checkForIsRented(isRentable());
		super.buyShopItem(slot, ecoPlayer, sendMessage);
	}
	
	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void sellShopItem(int slot, int amount, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, ShopSystemException, PlayerException {
		getValidationHandler().checkForIsRented(isRentable());
		super.sellShopItem(slot, amount, ecoPlayer, sendMessage);
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
		player.openInventory(getRentGuiHandler().getRentGui());
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
	
	protected RentshopRentGuiHandler getRentGuiHandler() {
		return rentGuiHandler;
	}

	/*
	 * Setup methods
	 * 
	 */

	private void setupNewRentshop(double rentalFee) {
		setupRentalFee(rentalFee);
		setupRentable();
		rentGuiHandler = new RentshopRentGuiHandler(this);
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

	/*
	 * Loading methods
	 * 
	 */

	private void loadExistingRentshop() {
		rentalFee = getSavefileHandler().loadRentalFee();
		rentable = getSavefileHandler().loadRentable();
		rentUntil = getSavefileHandler().loadRentUntil();
		rentGuiHandler = new RentshopRentGuiHandler(this);
		setupEconomyVillagerType();
		setupVillagerName();
	}
}
