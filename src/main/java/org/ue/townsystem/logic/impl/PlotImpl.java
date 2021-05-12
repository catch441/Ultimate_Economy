package org.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyvillager.logic.api.EconomyVillagerType;
import org.ue.economyvillager.logic.impl.EconomyVillagerImpl;
import org.ue.townsystem.dataaccess.api.TownworldDao;
import org.ue.townsystem.logic.api.Plot;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.TownsystemValidator;

public class PlotImpl extends EconomyVillagerImpl<TownsystemException> implements Plot {

	private final TownsystemValidator validationHandler;
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
	 * @param chunkCoords       (format "X/Z")
	 * @param validationHandler
	 * @param townworldDao
	 * @param town
	 * @param owner
	 * @param serverProvider
	 * @param skullService
	 * @param messageWrapper
	 */
	public PlotImpl(String chunkCoords, TownsystemValidator validationHandler, TownworldDao townworldDao,
			Town town, EconomyPlayer owner, ServerProvider serverProvider, CustomSkullService skullService,
			MessageWrapper messageWrapper) {
		super(messageWrapper, serverProvider, townworldDao, validationHandler, skullService,
				"Towns." + town.getTownName() + ".Plots." + chunkCoords + ".SaleVillager");
		this.chunkCoords = chunkCoords;
		this.town = town;
		this.townworldDao = townworldDao;
		this.validationHandler = validationHandler;
		setupNewPlot(owner);
	}

	/**
	 * Loading an existing plot constructor.
	 * 
	 * @param chunkCoords       (format "X/Z")
	 * @param validationHandler
	 * @param townworldDao
	 * @param town
	 * @param serverProvider
	 * @param skullService
	 * @param messageWrapper
	 * @throws EconomyPlayerException
	 */
	public PlotImpl(String chunkCoords, TownsystemValidator validationHandler, TownworldDao townworldDao,
			Town town, ServerProvider serverProvider, CustomSkullService skullService, MessageWrapper messageWrapper)
			throws EconomyPlayerException {
		super(messageWrapper, serverProvider, townworldDao, validationHandler, skullService,
				"Towns." + town.getTownName() + ".Plots." + chunkCoords + ".SaleVillager");
		this.chunkCoords = chunkCoords;
		this.town = town;
		this.townworldDao = townworldDao;
		this.validationHandler = validationHandler;
		loadExistingPlot();
	}

	private void loadExistingPlot() throws EconomyPlayerException {
		owner = townworldDao.loadPlotOwner(town.getTownName(), chunkCoords);
		residents = townworldDao.loadResidents(town.getTownName(), chunkCoords);
		salePrice = townworldDao.loadPlotSalePrice(town.getTownName(), chunkCoords);
		isForSale = townworldDao.loadPlotIsForSale(town.getTownName(), chunkCoords);
		setupExistingEconomyVillager(EconomyVillagerType.PLOTSALE, "Plot " + chunkCoords, 0);
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
		setupNewEconomyVillager(null, EconomyVillagerType.PLOTSALE, "Plot " + chunkCoords, 9, 0, false);
		setupSaleVillagerInventory();
	}

	private void setupSaleVillagerInventory() {
		ItemStack buyStack = serverProvider.createItemStack(Material.GREEN_WOOL, 1);
		ItemMeta buyItemMeta = buyStack.getItemMeta();
		buyItemMeta.setDisplayName("Buy");
		List<String> listBuy = new ArrayList<String>();
		listBuy.add(ChatColor.GOLD + "Price: " + ChatColor.GREEN + salePrice);
		listBuy.add(ChatColor.GOLD + "Is sold by " + ChatColor.GREEN + owner.getName());
		buyItemMeta.setLore(listBuy);
		buyStack.setItemMeta(buyItemMeta);
		getInventory().setItem(0, buyStack);
		ItemStack cancelStack = serverProvider.createItemStack(Material.RED_WOOL, 1);
		ItemMeta cancelItemMeta = cancelStack.getItemMeta();
		List<String> listCancel = new ArrayList<String>();
		listCancel.add(ChatColor.RED + "Only for plot owner!");
		cancelItemMeta.setDisplayName("Cancel Sale");
		cancelItemMeta.setLore(listCancel);
		cancelStack.setItemMeta(cancelItemMeta);
		getInventory().setItem(8, cancelStack);
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

	@Override
	public void openInventory(Player player) throws TownsystemException {
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
		changeSalePriceInInventory(salePrice);
		changeLocation(playerLocation);
		setVisible(true);
		townworldDao.savePlotSalePrice(town.getTownName(), chunkCoords, salePrice);
		townworldDao.savePlotIsForSale(town.getTownName(), chunkCoords, isForSale);
	}

	private void changeSalePriceInInventory(double salePrice) {
		ItemStack stack = getInventory().getItem(0);
		ItemMeta buyItemMeta = stack.getItemMeta();
		List<String> listBuy = buyItemMeta.getLore();
		listBuy.set(0, ChatColor.GOLD + "Price: " + ChatColor.GREEN + salePrice);
		buyItemMeta.setLore(listBuy);
		stack.setItemMeta(buyItemMeta);
		getInventory().setItem(0, stack);
	}

	@Override
	public double getSalePrice() {
		return salePrice;
	}

	@Override
	public Town getTown() {
		return town;
	}
}
