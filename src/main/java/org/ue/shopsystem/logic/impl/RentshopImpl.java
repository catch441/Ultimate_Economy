package org.ue.shopsystem.logic.impl;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.ue.bank.logic.api.BankException;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyvillager.logic.api.EconomyVillagerType;
import org.ue.shopsystem.dataaccess.api.ShopDao;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.RentshopRentGuiHandler;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopValidator;
import org.ue.shopsystem.logic.api.ShopsystemException;
import org.ue.townsystem.logic.api.TownworldManager;

public class RentshopImpl extends PlayershopImpl implements Rentshop {

	// 1 minecraft day = 24000 Ticks
	// 72 minecraft days = 1.728.000 ticks = one RL day
	private static final int RENTDAY_LENGTH = 1_728_000;
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
	 */
	@Inject
	public RentshopImpl(ShopDao shopDao, ServerProvider serverProvider, CustomSkullService skullService,
			ShopValidator validationHandler, EconomyPlayerManager ecoPlayerManager,
			MessageWrapper messageWrapper, ConfigManager configManager, TownworldManager townworldManager,
			PlayershopManager playershopManager) {
		super(shopDao, serverProvider, skullService, validationHandler, ecoPlayerManager, messageWrapper, configManager,
				townworldManager, playershopManager);
	}

	@Override
	public void setupNew(String shopId, Location spawnLocation, int size, double rentalFee) {
		name = "RentShop#" + shopId;
		setupNew(EconomyVillagerType.RENTSHOP, name, shopId, spawnLocation, size, 1);
		getEditorHandler().setup(1);
		this.rentalFee = rentalFee;
		shopDao.saveRentalFee(rentalFee);
		this.rentable = true;
		shopDao.saveRentable(rentable);
		rentGuiHandler = new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager, skullService, configManager,
				this, serverProvider);
	}

	@Override
	public void setupExisting(String shopId) throws EconomyPlayerException {
		setupExisting(EconomyVillagerType.RENTSHOP, shopId, 1);
		getEditorHandler().setup(1);
		loadStock();
		loadOwner();
		rentalFee = shopDao.loadRentalFee();
		rentable = shopDao.loadRentable();
		expiresAt = shopDao.loadExpiresAt();
		rentGuiHandler = new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager, skullService, configManager,
				this, serverProvider);
		if (isRentable()) {
			getVillager().setCustomName("RentShop#" + getShopId());
		}
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack) throws ShopsystemException {
		validationHandler.checkForIsRented(isRentable());
		super.addShopItem(slot, sellPrice, buyPrice, itemStack);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public String editShopItem(int slot, Integer newAmount, Double newSellPrice, Double newBuyPrice)
			throws ShopsystemException {
		validationHandler.checkForIsRented(isRentable());
		return super.editShopItem(slot, newAmount, newSellPrice, newBuyPrice);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public ShopItem getShopItem(int slot) throws ShopsystemException {
		validationHandler.checkForIsRented(isRentable());
		return super.getShopItem(slot);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public ShopItem getShopItem(ItemStack stack) throws ShopsystemException {
		validationHandler.checkForIsRented(isRentable());
		return super.getShopItem(stack);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public boolean isAvailable(int slot) throws ShopsystemException {
		validationHandler.checkForIsRented(isRentable());
		return super.isAvailable(slot);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 * 
	 * @throws ShopsystemException
	 */
	@Override
	public void openInventory(Player player) throws ShopsystemException {
		validationHandler.checkForIsRented(isRentable());
		super.openInventory(player);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void decreaseStock(int slot, int stock) throws ShopsystemException {
		validationHandler.checkForIsRented(isRentable());
		super.decreaseStock(slot, stock);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void increaseStock(int slot, int stock) throws ShopsystemException {
		validationHandler.checkForIsRented(isRentable());
		super.increaseStock(slot, stock);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void removeShopItem(int slot) throws ShopsystemException {
		validationHandler.checkForIsRented(isRentable());
		super.removeShopItem(slot);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void openSlotEditor(Player player, int slot) throws ShopsystemException {
		validationHandler.checkForIsRented(isRentable());
		super.openSlotEditor(player, slot);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void openEditor(Player player) throws ShopsystemException {
		validationHandler.checkForIsRented(isRentable());
		super.openEditor(player);
	}

	/**
	 * Overridden, because the position didn't have to be validated for build
	 * permissions like the parent playershop. {@inheritDoc}
	 */
	@Override
	public void changeLocation(Location location) {
		superChangeLocation(location);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void buyShopItem(int slot, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws ShopsystemException, BankException, EconomyPlayerException {
		validationHandler.checkForIsRented(isRentable());
		super.buyShopItem(slot, ecoPlayer, sendMessage);
	}

	/**
	 * Overridden, because of rentable value. {@inheritDoc}
	 */
	@Override
	public void sellShopItem(int slot, int amount, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws ShopsystemException, BankException, EconomyPlayerException {
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
	public void changeShopName(String name) throws ShopsystemException {
		validationHandler.checkForIsRented(isRentable());
		super.changeShopName(name);
	}

	@Override
	public void rentShop(EconomyPlayer player, int duration)
			throws ShopsystemException, BankException, EconomyPlayerException {
		validationHandler.checkForIsRentable(isRentable());
		// minus 1 because then the validator can prevent, that the duration is 0
		validationHandler.checkForPositiveValue(Double.valueOf(duration - 1));
		// checks if the player has enough money.
		player.decreasePlayerAmount(duration * getRentalFee(), true);
		changeOwner(player);
		rentable = false;
		changeShopName("Shop#" + getShopId());
		expiresAt = RENTDAY_LENGTH * duration + serverProvider.getWorldTime();
		shopDao.saveRentable(isRentable());
		shopDao.saveExpiresAt(getExpiresAt());
	}

	@Override
	public void changeRentalFee(double fee) throws ShopsystemException {
		validationHandler.checkForPositiveValue(fee);
		rentalFee = fee;
		shopDao.saveRentalFee(fee);
	}

	/**
	 * Overridden, because of is not rented check. {@inheritDoc}
	 */
	@Override
	public void changeSize(int newSize) throws ShopsystemException {
		validationHandler.checkForIsRentable(isRentable());
		super.changeSize(newSize);
	}

	/**
	 * Overridden, because of the rentable validation. Only possible, if the shop is
	 * not rented. Should not be used. Use the rentShop() method instead.
	 * {@inheritDoc}
	 */
	@Override
	public void changeOwner(EconomyPlayer newOwner) throws ShopsystemException {
		validationHandler.checkForIsRentable(isRentable());
		super.changeOwner(newOwner);
	}

	@Override
	public void openRentGUI(Player player) throws ShopsystemException {
		validationHandler.checkForIsRentable(isRentable());
		player.openInventory(getRentGuiHandler().getRentGui());
	}

	@Override
	public void resetShop() throws ShopsystemException {
		removeAllItems();
		setOwner(null);
		expiresAt = 0L;
		shopDao.saveOwner(null);
		shopDao.saveExpiresAt(0L);
		changeProfession(Profession.NITWIT);
		changeShopName("RentShop#" + getShopId());
		rentable = true;
		shopDao.saveRentable(true);
	}

	private void removeAllItems() throws ShopsystemException {
		for (ShopItem item : getItemList()) {
			removeShopItem(item.getSlot());
		}
	}

	protected RentshopRentGuiHandler getRentGuiHandler() {
		return rentGuiHandler;
	}
}
