package org.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.ue.bank.logic.api.BankException;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerValidator;
import org.ue.economyvillager.logic.api.EconomyVillagerType;
import org.ue.economyvillager.logic.impl.EconomyVillagerImpl;
import org.ue.townsystem.dataaccess.api.TownworldDao;
import org.ue.townsystem.logic.api.Plot;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.TownsystemValidator;

public class PlotImpl extends EconomyVillagerImpl<TownsystemException> implements Plot {

	private final TownsystemValidator validationHandler;
	private final EconomyPlayerValidator ecoPlayerValidator;
	private final TownworldDao townworldDao;
	private final String chunkCoords;
	private EconomyPlayer owner;
	private List<EconomyPlayer> residents;
	private boolean isForSale;
	private double salePrice;
	private Town town;

	/**
	 * Creating a new plot constructor.
	 * 
	 * @param chunkCoords        (format "X/Z")
	 * @param validationHandler
	 * @param townworldDao
	 * @param town
	 * @param owner
	 * @param serverProvider
	 * @param skullService
	 * @param messageWrapper
	 * @param ecoPlayerValidator
	 */
	public PlotImpl(String chunkCoords, TownsystemValidator validationHandler, TownworldDao townworldDao, Town town,
			EconomyPlayer owner, ServerProvider serverProvider, CustomSkullService skullService,
			MessageWrapper messageWrapper, EconomyPlayerValidator ecoPlayerValidator) {
		super(messageWrapper, serverProvider, townworldDao, validationHandler, skullService,
				"Towns." + town.getTownName() + ".Plots." + chunkCoords + ".SaleVillager");
		this.chunkCoords = chunkCoords;
		this.town = town;
		this.townworldDao = townworldDao;
		this.validationHandler = validationHandler;
		this.ecoPlayerValidator = ecoPlayerValidator;
		setupNewPlot(owner);
	}

	/**
	 * Loading an existing plot constructor.
	 * 
	 * @param chunkCoords        (format "X/Z")
	 * @param validationHandler
	 * @param townworldDao
	 * @param town
	 * @param serverProvider
	 * @param skullService
	 * @param messageWrapper
	 * @param ecoPlayerValidator
	 * @throws EconomyPlayerException
	 */
	public PlotImpl(String chunkCoords, TownsystemValidator validationHandler, TownworldDao townworldDao, Town town,
			ServerProvider serverProvider, CustomSkullService skullService, MessageWrapper messageWrapper,
			EconomyPlayerValidator ecoPlayerValidator) throws EconomyPlayerException {
		super(messageWrapper, serverProvider, townworldDao, validationHandler, skullService,
				"Towns." + town.getTownName() + ".Plots." + chunkCoords + ".SaleVillager");
		this.chunkCoords = chunkCoords;
		this.town = town;
		this.townworldDao = townworldDao;
		this.validationHandler = validationHandler;
		this.ecoPlayerValidator = ecoPlayerValidator;
		loadExistingPlot();
	}

	private void loadExistingPlot() throws EconomyPlayerException {
		owner = townworldDao.loadPlotOwner(town.getTownName(), chunkCoords);
		residents = townworldDao.loadResidents(town.getTownName(), chunkCoords);
		salePrice = townworldDao.loadPlotSalePrice(town.getTownName(), chunkCoords);
		isForSale = townworldDao.loadPlotIsForSale(town.getTownName(), chunkCoords);
		setupExistingEconomyVillager(EconomyVillagerType.PLOTSALE, "Plot " + chunkCoords, chunkCoords, 0);
		setupSaleVillagerInventory();
	}

	private void setupNewPlot(EconomyPlayer owner) {
		setOwner(owner);
		isForSale = false;
		salePrice = 0;
		residents = new ArrayList<>();
		townworldDao.savePlotResidents(town.getTownName(), chunkCoords, residents);
		townworldDao.savePlotSalePrice(town.getTownName(), chunkCoords, salePrice);
		townworldDao.savePlotIsForSale(town.getTownName(), chunkCoords, isForSale);
		setupNewEconomyVillager(null, EconomyVillagerType.PLOTSALE, "Plot " + chunkCoords, chunkCoords, 9, 0, false);
		setupSaleVillagerInventory();
	}

	private void setupSaleVillagerInventory() {
		setupSalePriceInventoryItem(salePrice);
		List<String> listCancel = Arrays.asList(ChatColor.RED + "Only for plot owner!");
		setItem(Material.RED_WOOL, listCancel, ChatColor.RED + "Only for plot owner!", 8);
	}

	private void setupSalePriceInventoryItem(double salePrice) {
		List<String> listBuy = new ArrayList<String>();
		listBuy.add(ChatColor.GOLD + "Price: " + ChatColor.GREEN + salePrice);
		listBuy.add(ChatColor.GOLD + "Is sold by " + ChatColor.GREEN + owner.getName());
		setItem(Material.GREEN_WOOL, listBuy, "Buy", 0);
	}

	/**
	 * Overridden because it should only be possible to change the visible if the
	 * plot is for sale.
	 */
	@Override
	public void setVisible(boolean visible) throws TownsystemException {
		validationHandler.checkForPlotIsForSale(isForSale());
		super.setVisible(visible);
	}

	@Override
	public void changeLocation(Location newLocation) throws TownsystemException {
		validationHandler.checkForPlotIsForSale(isForSale());
		validationHandler.checkForLocationInsidePlot(chunkCoords, newLocation);
		super.changeLocation(newLocation);
	}

	/**
	 * Not implemented. Use openInventoryWithCheck instead.
	 */
	@Override
	public void openInventory(Player player) {
	}

	@Override
	public void openInventoryWithCheck(Player player) throws TownsystemException {
		validationHandler.checkForPlotIsForSale(isForSale());
		super.openInventory(player);
	}

	@Override
	public EconomyPlayer getOwner() {
		return owner;
	}

	@Override
	public void setOwner(EconomyPlayer player) {
		this.owner = player;
		townworldDao.savePlotOwner(town.getTownName(), chunkCoords, player);
	}

	@Override
	public List<EconomyPlayer> getResidents() {
		return new ArrayList<>(residents);
	}

	@Override
	public void addResident(EconomyPlayer player) throws TownsystemException {
		validationHandler.checkForPlayerIsNotResidentOfPlot(residents, player);
		residents.add(player);
		townworldDao.savePlotResidents(town.getTownName(), chunkCoords, residents);
	}

	@Override
	public void removeResident(EconomyPlayer player) throws TownsystemException {
		validationHandler.checkForPlayerIsResidentOfPlot(residents, player);
		residents.remove(player);
		townworldDao.savePlotResidents(town.getTownName(), chunkCoords, residents);
	}

	@Override
	public String getChunkCoords() {
		return chunkCoords;
	}

	@Override
	public boolean isOwner(EconomyPlayer owner) {
		if (this.owner.equals(owner)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isResident(EconomyPlayer player) {
		boolean is = false;
		for (EconomyPlayer resident : residents) {
			if (resident.equals(player)) {
				is = true;
				break;
			}
		}
		return is;
	}

	@Override
	public boolean isForSale() {
		return isForSale;
	}

	@Override
	public void removeFromSale(EconomyPlayer owner) throws TownsystemException {
		validationHandler.checkForIsPlotOwner(getOwner(), owner);
		setVisible(false);
		isForSale = false;
		salePrice = 0;
		townworldDao.savePlotSalePrice(town.getTownName(), chunkCoords, salePrice);
		townworldDao.savePlotIsForSale(town.getTownName(), chunkCoords, isForSale);
	}

	@Override
	public void setForSale(double salePrice, Location playerLocation, EconomyPlayer player) throws TownsystemException {
		validationHandler.checkForIsPlotOwner(getOwner(), player);
		validationHandler.checkForPlotIsNotForSale(isForSale());
		isForSale = true;
		this.salePrice = salePrice;
		setupSalePriceInventoryItem(salePrice);
		changeLocation(playerLocation);
		setVisible(true);
		townworldDao.savePlotSalePrice(town.getTownName(), chunkCoords, salePrice);
		townworldDao.savePlotIsForSale(town.getTownName(), chunkCoords, isForSale);
	}

	@Override
	public double getSalePrice() {
		return salePrice;
	}

	@Override
	public Town getTown() {
		return town;
	}

	@Override
	public void handleInventoryClick(ClickType clickType, int rawSlot, EconomyPlayer whoClicked) {
		// TODO UE-119 extract messages
		try {
			if (rawSlot == 0) {
				ecoPlayerValidator.checkForEnoughMoney(whoClicked.getBankAccount(), getSalePrice(), true);
				whoClicked.payToOtherPlayer(getOwner(), getSalePrice(), false);
				town.buyPlot(whoClicked, this);
				whoClicked.getPlayer().sendMessage(ChatColor.GOLD + "Congratulation! You bought this plot!");
				whoClicked.getPlayer().closeInventory();
			} else if (rawSlot == 8) {
				if (isOwner(whoClicked)) {
					removeFromSale(whoClicked);
					whoClicked.getPlayer().sendMessage(ChatColor.GOLD + "You removed this plot from sale!");
				}
				whoClicked.getPlayer().closeInventory();
			}
		} catch (EconomyPlayerException | TownsystemException | BankException e) {
			whoClicked.getPlayer().sendMessage(e.getMessage());
		}
	}
}
