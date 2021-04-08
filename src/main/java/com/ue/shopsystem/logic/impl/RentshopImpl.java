package com.ue.shopsystem.logic.impl;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.common.api.CustomSkullService;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.EconomyVillager;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.RentshopRentGuiHandler;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.townsystem.logic.api.TownworldManager;
import com.ue.townsystem.logic.impl.TownSystemException;

public class RentshopImpl extends PlayershopImpl implements Rentshop {

	// 72 minecraft days = 24000 ticks = one RL day
	private static final int RENTDAY_LENGTH = 24000; 
	private double rentalFee;
	private long expiresAt;
	private boolean rentable;
	private RentshopRentGuiHandlerImpl rentGuiHandler;

	/**
	 * Inject constructor.
	 * 
	 * @param shopDao
	 * @param serverProvider
	 * @param skullService
	 * @param validationHandler
	 * @param ecoPlayerManager
	 * @param messageWrapper
	 * @param configManager
	 * @param townworldManager
	 * @param playershopManager
	 * @param generalValidator
	 */
	@Inject
	public RentshopImpl(ShopDao shopDao, ServerProvider serverProvider, CustomSkullService skullService,
			ShopValidationHandler validationHandler, EconomyPlayerManager ecoPlayerManager,
			MessageWrapper messageWrapper, ConfigManager configManager, TownworldManager townworldManager,
			PlayershopManager playershopManager, GeneralEconomyValidationHandler generalValidator) {
		super(shopDao, serverProvider, skullService, validationHandler, ecoPlayerManager, messageWrapper,
				configManager, townworldManager, playershopManager, generalValidator);
	}
	
	@Override
	public void setupNew(String shopId, Location spawnLocation, int size, double rentalFee) {
		super.setupNew("RentShop#" + shopId, null, shopId, spawnLocation, size);
		setupRentalFee(rentalFee);
		setupRentable();
		rentGuiHandler = new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager, skullService, configManager,
				this, serverProvider);
		setupEconomyVillagerType();
	}
	
	@Override
	public void setupExisting(String name, String shopId)
			throws TownSystemException, ShopSystemException, GeneralEconomyException {
		super.setupExisting(name, shopId);
		rentalFee = getShopDao().loadRentalFee();
		rentable = getShopDao().loadRentable();
		expiresAt = getShopDao().loadExpiresAt();
		rentGuiHandler = new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager, skullService, configManager,
				this, serverProvider);
		setupEconomyVillagerType();
		setupVillagerName();
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		validationHandler.checkForIsRented(isRentable());
		super.addShopItem(slot, sellPrice, buyPrice, itemStack);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public String editShopItem(int slot, String newAmount, String newSellPrice, String newBuyPrice)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		validationHandler.checkForIsRented(isRentable());
		return super.editShopItem(slot, newAmount, newSellPrice, newBuyPrice);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public ShopItem getShopItem(int slot) throws GeneralEconomyException, ShopSystemException {
		validationHandler.checkForIsRented(isRentable());
		return super.getShopItem(slot);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public ShopItem getShopItem(ItemStack stack) throws ShopSystemException {
		validationHandler.checkForIsRented(isRentable());
		return super.getShopItem(stack);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public boolean isAvailable(int slot) throws ShopSystemException, GeneralEconomyException {
		validationHandler.checkForIsRented(isRentable());
		return super.isAvailable(slot);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void openShopInventory(Player player) throws ShopSystemException {
		validationHandler.checkForIsRented(isRentable());
		super.openShopInventory(player);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void decreaseStock(int slot, int stock) throws GeneralEconomyException, ShopSystemException {
		validationHandler.checkForIsRented(isRentable());
		super.decreaseStock(slot, stock);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void increaseStock(int slot, int stock) throws GeneralEconomyException, ShopSystemException {
		validationHandler.checkForIsRented(isRentable());
		super.increaseStock(slot, stock);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void removeShopItem(int slot) throws ShopSystemException, GeneralEconomyException {
		validationHandler.checkForIsRented(isRentable());
		super.removeShopItem(slot);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void openSlotEditor(Player player, int slot) throws ShopSystemException, GeneralEconomyException {
		validationHandler.checkForIsRented(isRentable());
		super.openSlotEditor(player, slot);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void openEditor(Player player) throws ShopSystemException {
		validationHandler.checkForIsRented(isRentable());
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
		validationHandler.checkForIsRented(isRentable());
		super.buyShopItem(slot, ecoPlayer, sendMessage);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void sellShopItem(int slot, int amount, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, ShopSystemException, EconomyPlayerException {
		validationHandler.checkForIsRented(isRentable());
		super.sellShopItem(slot, amount, ecoPlayer, sendMessage);
	}

	@Override
	public boolean isRentable() {
		return rentable;
	}

	@Override
	public long getExpiresAt() {
		return expiresAt;
	}

	@Override
	public double getRentalFee() {
		return rentalFee;
	}

	/**
	 * Overridden, because check for rentable is needed. {@inheritDoc}
	 */
	@Override
	public void changeShopName(String name) throws ShopSystemException, GeneralEconomyException {
		validationHandler.checkForIsRented(isRentable());
		super.changeShopName(name);
	}

	@Override
	public void rentShop(EconomyPlayer player, int duration)
			throws ShopSystemException, GeneralEconomyException, EconomyPlayerException {
		validationHandler.checkForIsRentable(isRentable());
		// minus 1 because the validator can then prevent, that the duration is 0
		generalValidator.checkForPositiveValue(duration - 1);
		// throws a playerexception, if the player has not enough money.
		player.decreasePlayerAmount(duration * getRentalFee(), true);
		changeOwner(player);
		rentable = false;
		changeShopName("Shop#" + getShopId());
		expiresAt = RENTDAY_LENGTH * duration + serverProvider.getWorldTime();
		getShopDao().saveRentable(isRentable());
		getShopDao().saveExpiresAt(getExpiresAt());
	}

	@Override
	public void changeRentalFee(double fee) throws GeneralEconomyException {
		generalValidator.checkForPositiveValue(fee);
		rentalFee = fee;
		getShopDao().saveRentalFee(fee);
	}

	/**
	 * Overriden, because of is not rented check.
	 */
	@Override
	public void changeShopSize(int newSize)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		validationHandler.checkForIsRentable(isRentable());
		super.changeShopSize(newSize);
	}

	/**
	 * Overridden, because of the rentable validation. Only possible, if the shop is
	 * not rented. Should not be used. Use the rentShop() method instead.
	 * {@inheritDoc}
	 */
	@Override
	public void changeOwner(EconomyPlayer newOwner) throws EconomyPlayerException, ShopSystemException {
		validationHandler.checkForIsRentable(isRentable());
		super.changeOwner(newOwner);
	}

	@Override
	public void openRentGUI(Player player) throws ShopSystemException {
		validationHandler.checkForIsRentable(isRentable());
		player.openInventory(getRentGuiHandler().getRentGui());
	}

	@Override
	public void resetShop() throws ShopSystemException, GeneralEconomyException {
		removeAllItems();
		setOwner(null);
		expiresAt = 0L;
		getShopDao().saveOwner(null);
		getShopDao().saveExpiresAt(0L);
		changeProfession(Profession.NITWIT);

		setupShopName("RentShop#" + getShopId());
		changeInventoryNames("RentShop#" + getShopId());
		getShopVillager().setCustomName("RentShop#" + getShopId());

		rentable = true;
		getShopDao().saveRentable(true);
	}

	/*
	 * Utility methods
	 * 
	 */

	private void removeAllItems() throws ShopSystemException, GeneralEconomyException {
		for (int i = 0; i < (getSize() - 2); i++) {
			if (!validationHandler.isSlotEmpty(i, getShopInventory(), 2)) {
				removeShopItem(i);
			}
		}
	}

	protected RentshopRentGuiHandler getRentGuiHandler() {
		return rentGuiHandler;
	}

	private void setupEconomyVillagerType() {
		getShopVillager().setMetadata("ue-type",
				new FixedMetadataValue(serverProvider.getJavaPluginInstance(), EconomyVillager.PLAYERSHOP_RENTABLE));
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
}
