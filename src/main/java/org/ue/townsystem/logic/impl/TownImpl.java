package org.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankException;
import org.ue.bank.logic.api.BankManager;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.ExceptionMessageEnum;
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
import org.ue.townsystem.logic.api.Townworld;
import org.ue.townsystem.logic.api.TownworldManager;

public class TownImpl extends EconomyVillagerImpl<TownsystemException> implements Town {

	private static final Logger log = LoggerFactory.getLogger(TownImpl.class);
	private final MessageWrapper messageWrapper;
	private final TownsystemValidator validationHandler;
	private final BankManager bankManager;
	private final TownworldManager townworldManager;
	private final TownworldDao townworldDao;

	private String townName;
	private EconomyPlayer mayor;
	private List<EconomyPlayer> citizens = new ArrayList<>();
	private List<EconomyPlayer> deputies = new ArrayList<>();
	private Location townSpawn;
	private Map<String, Plot> plots = new HashMap<>();
	private BankAccount bankAccount;
	private double tax; // TODO integrate tax system
	private Townworld townworld;

	/**
	 * Inject constructor.
	 * 
	 * @param townworldManager
	 * @param bankManager
	 * @param validationHandler
	 * @param messageWrapper
	 * @param townworldDao
	 * @param townworld
	 * @param serverProvider
	 * @param skullService
	 */
	public TownImpl(TownworldManager townworldManager, BankManager bankManager, TownsystemValidator validationHandler,
			MessageWrapper messageWrapper, TownworldDao townworldDao, Townworld townworld,
			ServerProvider serverProvider, CustomSkullService skullService) {
		super(serverProvider, townworldDao, validationHandler, skullService);
		this.townworldDao = townworldDao;
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		this.bankManager = bankManager;
		this.townworldManager = townworldManager;
	}

	@Override
	public void setupNew(Townworld townworld, EconomyPlayer mayor, String townName, Location location)
			throws EconomyPlayerException {
		bankAccount = bankManager.createBankAccount(0);
		this.townName = townName;
		this.townworld = townworld;
		this.mayor = mayor;
		tax = 0.0;
		townworldDao.saveTax(townName, tax);
		citizens.add(mayor);
		mayor.addJoinedTown(getTownName());
		townworldDao.saveMayor(townName, mayor);
		townworldDao.saveCitizens(townName, citizens);
		townworldDao.saveTownBankIban(townName, bankAccount.getIban());
		Chunk startChunk = setupStartChunk(mayor, location);
		setupTownSpawn(startChunk);
		String savePrefix = "Towns." + townName + ".TownManagerVillager";
		setupNewEconomyVillager(location, EconomyVillagerType.TOWNMANAGER, townName + " TownManager", townName, 9, 0,
				true, savePrefix);
		setupTownManagerInventory();
	}

	@Override
	public void setupExisting(Townworld townworld, String townName)
			throws TownsystemException, BankException, EconomyPlayerException {
		this.townworld = townworld;
		this.townName = townName;
		deputies = townworldDao.loadDeputies(townName);
		citizens = townworldDao.loadCitizens(townName);
		mayor = townworldDao.loadMayor(townName);
		townSpawn = townworldDao.loadTownSpawn(townName);
		bankAccount = bankManager.getBankAccountByIban(townworldDao.loadTownBankIban(townName));
		tax = townworldDao.loadTax(townName);
		String savePrefix = "Towns." + townName + ".TownManagerVillager";
		setupExistingEconomyVillager(EconomyVillagerType.TOWNMANAGER, townName + " TownManager", townName, 0,
				savePrefix);
		setupTownManagerInventory();
		loadPlots();
	}

	@Override
	public void renameTown(String newName, EconomyPlayer player) throws TownsystemException, EconomyPlayerException {
		List<String> townNameList = townworldManager.getTownNameList();
		validationHandler.checkForValueNotInList(townNameList, newName);
		validationHandler.checkForPlayerIsMayor(mayor, player);
		String oldName = getTownName();
		townName = newName;
		for (EconomyPlayer citizen : getCitizens()) {
			citizen.removeJoinedTown(oldName);
			citizen.addJoinedTown(newName);
		}
		getVillager().setCustomName(townName + " TownManager");
		townNameList.remove(oldName);
		townNameList.add(newName);
		((TownworldManagerImpl) townworldManager).setTownNameList(townNameList);
		townworldDao.saveRenameTown(oldName, newName);
	}

	@Override
	public void expandTown(Chunk chunk, EconomyPlayer player) throws TownsystemException, BankException {
		validationHandler.checkForChunkNotClaimed(getTownworld(), chunk);
		validationHandler.checkForPlayerHasDeputyPermission(hasDeputyPermissions(player));
		validationHandler.checkForChunkIsConnectedToTown(isChunkConnectedToTown(chunk.getX(), chunk.getZ()));
		decreaseTownBankAmount(getTownworld().getExpandPrice());
		Plot plot = serverProvider.getProvider().createPlot(townworldDao);
		plot.setupNew(mayor, this, chunk.getX() + "/" + chunk.getZ());
		plots.put(plot.getChunkCoords(), plot);
		townworldManager.performTownworldLocationCheckAllPlayers();
	}

	@Override
	public void changeLocation(Location location, EconomyPlayer player) throws TownsystemException {
		validationHandler.checkForLocationIsInTown(plots, location);
		validationHandler.checkForPlayerIsMayor(mayor, player);
		changeLocation(location);
	}

	@Override
	public void despawnAllVillagers() {
		for (Entry<String, Plot> plot : plots.entrySet()) {
			plot.getValue().despawn();
		}
		despawn();
	}

	@Override
	public void buyPlot(EconomyPlayer citizen, Plot plot) throws TownsystemException {
		validationHandler.checkForChunkIsClaimedByThisTown(plots, plot.getChunkCoords());
		validationHandler.checkForPlotIsForSale(plot.isForSale());
		validationHandler.checkForPlayerIsNotPlotOwner(citizen, plot);
		for (EconomyPlayer resident : plot.getResidents()) {
			plot.removeResident(resident);
		}
		plot.setOwner(citizen);
		plot.removeFromSale(citizen);
	}

	@Override
	public void deletePlot(Plot plot) throws TownsystemException {
		validationHandler.checkForChunkIsClaimedByThisTown(plots, plot.getChunkCoords());
		plots.remove(plot.getChunkCoords());
		plot.despawn();
		townworldDao.saveRemovePlot(getTownName(), plot.getChunkCoords());
	}

	@Override
	public EconomyPlayer getMayor() {
		return mayor;
	}

	@Override
	public List<EconomyPlayer> getCitizens() {
		return new ArrayList<>(citizens);
	}

	@Override
	public void joinTown(EconomyPlayer ecoPlayer) throws TownsystemException, EconomyPlayerException {
		validationHandler.checkForPlayerDidNotReachedMaxTowns(ecoPlayer);
		validationHandler.checkForPlayerIsNotCitizenPersonal(citizens, ecoPlayer);
		citizens.add(ecoPlayer);
		townworldDao.saveCitizens(getTownName(), citizens);
		ecoPlayer.addJoinedTown(getTownName());
	}

	@Override
	public void leaveTown(EconomyPlayer ecoPlayer) throws TownsystemException, EconomyPlayerException {
		validationHandler.checkForPlayerIsNotMayor(getMayor(), ecoPlayer);
		validationHandler.checkForPlayerIsCitizenPersonalError(citizens, ecoPlayer);
		if (isDeputy(ecoPlayer)) {
			removeDeputy(ecoPlayer);
		}
		for (Entry<String, Plot> plot : plots.entrySet()) {
			if (plot.getValue().isResident(ecoPlayer)) {
				plot.getValue().removeResident(ecoPlayer);
			} else if (plot.getValue().isOwner(ecoPlayer)) {
				plot.getValue().setOwner(getMayor());
			}
		}
		citizens.remove(ecoPlayer);
		townworldDao.saveCitizens(getTownName(), citizens);
		ecoPlayer.removeJoinedTown(getTownName());
	}

	@Override
	public boolean isPlayerCitizen(EconomyPlayer player) {
		if (getCitizens().contains(player)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getTownName() {
		return townName;
	}

	@Override
	public Location getTownSpawn() {
		return townSpawn;
	}

	@Override
	public void changeTownSpawn(Location townSpawn, EconomyPlayer ecoPlayer) throws TownsystemException {
		validationHandler.checkForPlayerHasDeputyPermission(hasDeputyPermissions(ecoPlayer));
		validationHandler.checkForLocationIsInTown(plots, townSpawn);
		this.townSpawn = townSpawn;
		townworldDao.saveTownSpawn(getTownName(), getTownSpawn());
	}

	@Override
	public List<EconomyPlayer> getDeputies() {
		return new ArrayList<>(deputies);
	}

	@Override
	public double getTax() {
		return tax;
	}

	@Override
	public Plot getPlotByChunk(String chunkCoords) throws TownsystemException {
		for (Entry<String, Plot> plot : plots.entrySet()) {
			if (plot.getKey().equals(chunkCoords)) {
				return plot.getValue();
			}
		}
		throw new TownsystemException(messageWrapper, ExceptionMessageEnum.CHUNK_NOT_CLAIMED_BY_TOWN);
	}

	@Override
	public boolean isMayor(EconomyPlayer player) {
		if (getMayor().equals(player)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isDeputy(EconomyPlayer player) {
		if (getDeputies().contains(player)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void addDeputy(EconomyPlayer player) throws TownsystemException, EconomyPlayerException {
		validationHandler.checkForPlayerIsNotDeputy(deputies, player);
		if (!isPlayerCitizen(player)) {
			joinTown(player);
		}
		deputies.add(player);
		townworldDao.saveDeputies(getTownName(), deputies);
	}

	@Override
	public void removeDeputy(EconomyPlayer player) throws TownsystemException {
		validationHandler.checkForPlayerIsDeputy(deputies, player);
		deputies.remove(player);
		townworldDao.saveDeputies(getTownName(), deputies);
	}

	@Override
	public boolean hasEnoughMoney(double amount) {
		if (getTownBankAmount() >= amount) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean hasDeputyPermissions(EconomyPlayer player) {
		if (isPlayerCitizen(player) && (isDeputy(player) || isMayor(player))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean hasBuildPermissions(EconomyPlayer player, Plot plot) {
		if (isPlayerCitizen(player)
				&& (hasDeputyPermissions(player) || plot.isOwner(player) || plot.isResident(player))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isChunkConnectedToTown(int chunkX, int chunkZ) {
		for (String coords : plots.keySet()) {
			int x = Integer.valueOf(coords.substring(0, coords.indexOf("/")));
			int z = Integer.valueOf(coords.substring(coords.indexOf("/") + 1));
			int newX = x - chunkX;
			int newZ = z - chunkZ;
			int compare = Math.abs(newX) + Math.abs(newZ);
			if (compare == 1) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isClaimedByTown(Chunk chunk) {
		if (plots.containsKey(chunk.getX() + "/" + chunk.getZ())) {
			return true;
		}
		return false;
	}

	@Override
	public double getTownBankAmount() {
		return bankAccount.getAmount();
	}

	@Override
	public void increaseTownBankAmount(double amount) throws BankException {
		bankAccount.increaseAmount(amount);
	}

	@Override
	public void decreaseTownBankAmount(double amount) throws TownsystemException, BankException {
		validationHandler.checkForTownHasEnoughMoney(getTownBankAmount(), amount);
		bankAccount.decreaseAmount(amount);
	}

	@Override
	public Townworld getTownworld() {
		return townworld;
	}

	@Override
	public void setTax(double tax) throws TownsystemException {
		validationHandler.checkForPositiveValue(tax);
		this.tax = tax;
		townworldDao.saveTax(getTownName(), tax);
	}

	@Override
	public void handleInventoryClick(ClickType clickType, int rawSlot, EconomyPlayer whoClicked) {
		// TODO UE-119 extract messages
		try {
			if (rawSlot == 0) {
				joinTown(whoClicked);
				whoClicked.getPlayer().sendMessage(ChatColor.GOLD + "You joined the town " + getTownName() + ".");
				whoClicked.getPlayer().closeInventory();
			} else if (rawSlot == 1) {
				leaveTown(whoClicked);
				whoClicked.getPlayer().sendMessage(ChatColor.GOLD + "You left the town " + getTownName() + ".");
				whoClicked.getPlayer().closeInventory();
			}
		} catch (TownsystemException | EconomyPlayerException e) {
			whoClicked.getPlayer().sendMessage(e.getMessage());
			whoClicked.getPlayer().closeInventory();
		}
	}

	private void setupTownSpawn(Chunk startChunk) {
		Location spawn = new Location(startChunk.getWorld(), (startChunk.getX() << 4) + 7, 0,
				(startChunk.getZ() << 4) + 7);
		spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));
		townSpawn = spawn;
		townworldDao.saveTownSpawn(getTownName(), getTownSpawn());
	}

	private Chunk setupStartChunk(EconomyPlayer mayor, Location location) {
		Chunk startChunk = location.getChunk();
		String startChunkCoords = startChunk.getX() + "/" + startChunk.getZ();
		Plot plot = serverProvider.getProvider().createPlot(townworldDao);
		plot.setupNew(mayor, this, startChunkCoords);
		plots.put(startChunkCoords, plot);
		return startChunk;
	}

	private void setupTownManagerInventory() {
		setItem(Material.GREEN_WOOL, null, "Join", 0);
		setItem(Material.RED_WOOL, null, "Leave", 1);
	}

	private void loadPlots() {
		for (String coords : townworldDao.loadTownPlotCoords(getTownName())) {
			try {
				Plot plot = serverProvider.getProvider().createPlot(townworldDao);
				plot.setupExisting(this, coords);
				plots.put(coords, plot);
			} catch (EconomyPlayerException e) {
				log.warn("[Ultimate_Economy] Failed to load plot " + coords + " of town " + getTownName());
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}
}
