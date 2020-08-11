package com.ue.shopsystem.logic.impl;

import java.util.Calendar;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.townsystem.logic.impl.TownSystemException;
import com.ue.ultimate_economy.EconomyVillager;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

public class RentshopImpl extends PlayershopImpl implements Rentshop {

	private double rentalFee;
	private long rentUntil;
	private boolean rentable; // true, if the shop is not rented
	private RentshopRentGuiHandlerImpl rentGuiHandler;

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
	 * @throws EconomyPlayerException
	 * @throws ShopSystemException
	 * @throws GeneralEconomyException
	 */
	public RentshopImpl(String shopId)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException, ShopSystemException {
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
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		getValidationHandler().checkForIsRented(isRentable());
		super.addShopItem(slot, sellPrice, buyPrice, itemStack);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public String editShopItem(int slot, String newAmount, String newSellPrice, String newBuyPrice)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
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
			throws GeneralEconomyException, EconomyPlayerException, ShopSystemException {
		getValidationHandler().checkForIsRented(isRentable());
		super.buyShopItem(slot, ecoPlayer, sendMessage);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void sellShopItem(int slot, int amount, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, ShopSystemException, EconomyPlayerException {
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
			changeInventoryNames(name);
			getShopVillager().setCustomName(name + "#" + getShopId());
		}
	}

	@Override
	public void rentShop(EconomyPlayer player, int duration)
			throws ShopSystemException, GeneralEconomyException, EconomyPlayerException {
		getValidationHandler().checkForIsRentable(isRentable());
		// throws a playerexception, if the player has not enough money.
		player.decreasePlayerAmount(duration * getRentalFee(), true);
		changeOwner(player);
		changeShopName("Shop#" + getShopId());
		rentable = false;
		setRentUntil(Calendar.getInstance().getTimeInMillis() + (86400000 * duration));
		getShopDao().saveRentable(isRentable());
		getShopDao().saveRentUntil(getRentUntil());
	}

	@Override
	public void changeRentalFee(double fee) throws GeneralEconomyException {
		getValidationHandler().checkForPositiveValue(fee);
		rentalFee = fee;
		getShopDao().saveRentalFee(fee);
	}

	/**
	 * Overriden, because of is not rented check.
	 */
	@Override
	public void changeShopSize(int newSize)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		getValidationHandler().checkForIsRentable(isRentable());
		super.changeShopSize(newSize);
	}

	/**
	 * Overridden, because of the rentable validation. Only possible, if the shop is
	 * not rented. Should not be used. Use the rentShop() method instead.
	 * {@inheritDoc}
	 */
	@Override
	public void changeOwner(EconomyPlayer newOwner) throws EconomyPlayerException, ShopSystemException {
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
		removeAllItems();
		setOwner(null);
		getShopDao().saveOwner(null);
		setRentUntil(0L);
		rentable = true;
		getShopDao().saveRentUntil(0L);
		getShopDao().saveRentable(true);
		changeProfession(Profession.NITWIT);
		changeShopName("RentShop");
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

	protected RentshopRentGuiHandlerImpl getRentGuiHandler() {
		return rentGuiHandler;
	}

	/**
	 * Set the rent until value. Only public for unit tests.
	 * 
	 * @param rentUntil
	 */
	public void setRentUntil(long rentUntil) {
		this.rentUntil = rentUntil;
	}

	/*
	 * Setup methods
	 * 
	 */

	private void setupNewRentshop(double rentalFee) {
		setupRentalFee(rentalFee);
		setupRentable();
		rentGuiHandler = new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager, skullService, configManager,
				this);
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
		getShopDao().saveRentable(true);
	}

	private void setupRentalFee(double rentalFee) {
		this.rentalFee = rentalFee;
		getShopDao().saveRentalFee(rentalFee);
	}

	/*
	 * Loading methods
	 * 
	 */

	private void loadExistingRentshop() {
		rentalFee = getShopDao().loadRentalFee();
		rentable = getShopDao().loadRentable();
		setRentUntil(getShopDao().loadRentUntil());
		rentGuiHandler = new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager, skullService, configManager,
				this);
		setupEconomyVillagerType();
		setupVillagerName();
	}
}
