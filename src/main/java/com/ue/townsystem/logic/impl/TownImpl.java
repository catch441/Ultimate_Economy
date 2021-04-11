package com.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.EconomyVillager;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.townsystem.dataaccess.api.TownworldDao;
import com.ue.townsystem.logic.api.Plot;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.townsystem.logic.api.TownworldManager;

public class TownImpl implements Town {

	private static final Logger log = LoggerFactory.getLogger(TownImpl.class);
	private final MessageWrapper messageWrapper;
	private final TownsystemValidationHandler validationHandler;
	private final GeneralEconomyValidationHandler generalValidator;
	private final BankManager bankManager;
	private final TownworldManager townworldManager;
	private final ServerProvider serverProvider;
	private final TownworldDao townworldDao;

	private String townName;
	private EconomyPlayer mayor;
	private List<EconomyPlayer> citizens, deputies;
	private Location townSpawn;
	private Map<String, Plot> plots = new HashMap<>();
	private BankAccount bankAccount;
	private double tax; // TODO integrate tax system
	private Villager villager;
	private Inventory inventory;
	private Townworld townworld;

	/**
	 * Constructor for loading a existing town.
	 * 
	 * @param townName
	 * @param townworldManager
	 * @param bankManager
	 * @param validationHandler
	 * @param messageWrapper
	 * @param townworldDao
	 * @param townworld
	 * @param serverProvider
	 * @param generalValidator
	 * @throws EconomyPlayerException
	 * @throws TownSystemException
	 * @throws GeneralEconomyException
	 */
	public TownImpl(String townName, TownworldManager townworldManager, BankManager bankManager,
			TownsystemValidationHandler validationHandler, MessageWrapper messageWrapper, TownworldDao townworldDao,
			Townworld townworld, ServerProvider serverProvider,
			GeneralEconomyValidationHandler generalValidator)
			throws EconomyPlayerException, TownSystemException, GeneralEconomyException {
		this.townworldDao = townworldDao;
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		this.bankManager = bankManager;
		this.townworldManager = townworldManager;
		this.serverProvider = serverProvider;
		this.generalValidator = generalValidator;
		loadExistingTown(townworld, townName);
	}

	/**
	 * Constructor for creating a new town.
	 * 
	 * @param mayor
	 * @param townName
	 * @param location
	 * @param townworldManager
	 * @param bankManager
	 * @param validationHandler
	 * @param messageWrapper
	 * @param townworldDao
	 * @param townworld
	 * @param serverProvider
	 * @param generalValidator
	 * @throws EconomyPlayerException
	 */
	public TownImpl(EconomyPlayer mayor, String townName, Location location, TownworldManager townworldManager,
			BankManager bankManager, TownsystemValidationHandler validationHandler, MessageWrapper messageWrapper,
			TownworldDao townworldDao, Townworld townworld, ServerProvider serverProvider,
			GeneralEconomyValidationHandler generalValidator) throws EconomyPlayerException {
		this.townworldDao = townworldDao;
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		this.bankManager = bankManager;
		this.townworldManager = townworldManager;
		this.serverProvider = serverProvider;
		this.generalValidator = generalValidator;
		setupNewTown(townworld, mayor, townName, location);
	}

	private void setupNewTown(Townworld townworld, EconomyPlayer mayor, String townName, Location location)
			throws EconomyPlayerException {
		this.townName = townName;
		this.townworld = townworld;
		citizens = new ArrayList<>();
		deputies = new ArrayList<>();
		Chunk startChunk = location.getChunk();
		String startChunkCoords = startChunk.getX() + "/" + startChunk.getZ();
		plots.put(startChunkCoords,
				new PlotImpl(startChunkCoords, validationHandler, townworldDao, this, mayor, serverProvider));
		setupMayor(mayor);
		setupTownManager(location);
		bankAccount = bankManager.createBankAccount(0);
		townworldDao.saveTownBankIban(getTownName(), bankAccount.getIban());
		setupTownSpawn(startChunk);
		tax = 0.0;
		townworldDao.saveTax(getTownName(), tax);
	}

	private void spawnTownManagerVillager(Location location) {
		location.getChunk().load();
		Collection<Entity> entitys = location.getWorld().getNearbyEntities(location, 10, 10, 10);
		for (Entity entity : entitys) {
			if (entity.getName().equals(townName + " TownManager")) {
				entity.remove();
			}
		}
		villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
		villager.setCustomName(townName + " TownManager");
		villager.setCustomNameVisible(true);
		// set the tye of the villager
		villager.setMetadata("ue-type",
				new FixedMetadataValue(serverProvider.getJavaPluginInstance(), EconomyVillager.TOWNMANAGER));
		villager.setProfession(Villager.Profession.NITWIT);
		villager.setSilent(true);
		villager.setInvulnerable(true);
		villager.setCollidable(false);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
	}

	@Override
	public void renameTown(String newName, EconomyPlayer player)
			throws GeneralEconomyException, EconomyPlayerException {
		List<String> townNameList = townworldManager.getTownNameList();
		generalValidator.checkForValueNotInList(townNameList, newName);
		validationHandler.checkForPlayerIsMayor(getMayor(), player);
		String oldName = getTownName();
		townName = newName;
		for (EconomyPlayer citizen : getCitizens()) {
			citizen.removeJoinedTown(oldName);
			citizen.addJoinedTown(newName);
		}
		villager.setCustomName(getTownName() + " TownManager");
		townNameList.remove(oldName);
		townNameList.add(newName);
		((TownworldManagerImpl) townworldManager).setTownNameList(townNameList);
		townworldDao.saveRenameTown(oldName, newName);
	}

	@Override
	public void expandTown(Chunk chunk, EconomyPlayer player)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		validationHandler.checkForChunkNotClaimed(getTownworld(), chunk);
		validationHandler.checkForPlayerHasDeputyPermission(hasDeputyPermissions(player));
		validationHandler.checkForChunkIsConnectedToTown(isChunkConnectedToTown(chunk.getX(), chunk.getZ()));
		decreaseTownBankAmount(getTownworld().getExpandPrice());
		Plot plot = new PlotImpl(chunk.getX() + "/" + chunk.getZ(), validationHandler, townworldDao, this, player,
				serverProvider);
		plots.put(plot.getChunkCoords(), plot);
		townworldManager.performTownworldLocationCheckAllPlayers();
	}

	@Override
	public void openTownManagerVillagerInv(Player player) {
		player.openInventory(inventory);
	}

	@Override
	public void moveTownManagerVillager(Location location, EconomyPlayer player)
			throws TownSystemException, EconomyPlayerException {
		validationHandler.checkForLocationIsInTown(plots, location);
		validationHandler.checkForPlayerIsMayor(getMayor(), player);
		villager.teleport(location);
		townworldDao.saveTownManagerLocation(getTownName(), location);
	}

	@Override
	public void despawnAllVillagers() {
		for (Entry<String, Plot> plot : plots.entrySet()) {
			plot.getValue().despawnSaleVillager();
		}
		villager.remove();
	}

	@Override
	public void buyPlot(EconomyPlayer citizen, int chunkX, int chunkZ)
			throws TownSystemException, EconomyPlayerException {
		Plot plot = getPlotByChunk(chunkX + "/" + chunkZ);
		validationHandler.checkForPlotIsForSale(plot.isForSale());
		validationHandler.checkForPlayerIsNotPlotOwner(citizen, plot);
		for (EconomyPlayer resident : plot.getResidents()) {
			plot.removeResident(resident);
		}
		plot.setOwner(citizen);
		plot.removeFromSale(citizen);
	}

	@Override
	public void deletePlot(Plot plot) throws TownSystemException {
		validationHandler.checkForChunkIsClaimedByThisTown(plots, plot.getChunkCoords());
		plots.remove(plot.getChunkCoords());
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
	public void joinTown(EconomyPlayer ecoPlayer) throws EconomyPlayerException {
		validationHandler.checkForPlayerDidNotReachedMaxTowns(ecoPlayer);
		validationHandler.checkForPlayerIsNotCitizenPersonal(getCitizens(), ecoPlayer);
		citizens.add(ecoPlayer);
		townworldDao.saveCitizens(getTownName(), getCitizens());
		ecoPlayer.addJoinedTown(getTownName());
	}

	@Override
	public void leaveTown(EconomyPlayer ecoPlayer) throws TownSystemException, EconomyPlayerException {
		validationHandler.checkForPlayerIsNotMayor(getMayor(), ecoPlayer);
		validationHandler.checkForPlayerIsCitizenPersonalError(getCitizens(), ecoPlayer);
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
		townworldDao.saveCitizens(getTownName(), getCitizens());
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
	public void changeTownSpawn(Location townSpawn, EconomyPlayer ecoPlayer)
			throws TownSystemException, EconomyPlayerException {
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
	public Plot getPlotByChunk(String chunkCoords) throws TownSystemException {
		for (Entry<String, Plot> plot : plots.entrySet()) {
			if (plot.getKey().equals(chunkCoords)) {
				return plot.getValue();
			}
		}
		throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.CHUNK_NOT_CLAIMED_BY_TOWN);
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
	public void addDeputy(EconomyPlayer player) throws TownSystemException, EconomyPlayerException {
		validationHandler.checkForPlayerIsNotDeputy(getDeputies(), player);
		if (!isPlayerCitizen(player)) {
			joinTown(player);
		}
		deputies.add(player);
		townworldDao.saveDeputies(getTownName(), getDeputies());
	}

	@Override
	public void removeDeputy(EconomyPlayer player) throws TownSystemException {
		validationHandler.checkForPlayerIsDeputy(getDeputies(), player);
		deputies.remove(player);
		townworldDao.saveDeputies(getTownName(), getDeputies());
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
	public void increaseTownBankAmount(double amount) throws GeneralEconomyException {
		bankAccount.increaseAmount(amount);
	}

	@Override
	public void decreaseTownBankAmount(double amount) throws TownSystemException, GeneralEconomyException {
		validationHandler.checkForTownHasEnoughMoney(getTownBankAmount(), amount);
		bankAccount.decreaseAmount(amount);
	}

	@Override
	public Townworld getTownworld() {
		return townworld;
	}

	@Override
	public void setTax(double tax) throws GeneralEconomyException {
		generalValidator.checkForPositiveValue(tax);
		this.tax = tax;
		townworldDao.saveTax(getTownName(), tax);
	}

	private void setupTownSpawn(Chunk startChunk) {
		Location spawn = new Location(startChunk.getWorld(), (startChunk.getX() << 4) + 7, 0,
				(startChunk.getZ() << 4) + 7);
		spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));
		townSpawn = spawn;
		townworldDao.saveTownSpawn(getTownName(), getTownSpawn());
	}

	private void setupTownManagerInventory() {
		inventory = serverProvider.createInventory(villager, 9, getTownName() + " TownManager");
		ItemStack joinItem = serverProvider.createItemStack(Material.GREEN_WOOL, 1);
		ItemMeta joinItemMeta = joinItem.getItemMeta();
		joinItemMeta.setDisplayName("Join");
		joinItem.setItemMeta(joinItemMeta);
		inventory.setItem(0, joinItem);
		ItemStack leaveItem = serverProvider.createItemStack(Material.RED_WOOL, 1);
		ItemMeta leaveItemMeta = leaveItem.getItemMeta();
		leaveItemMeta.setDisplayName("Leave");
		leaveItem.setItemMeta(leaveItemMeta);
		inventory.setItem(1, leaveItem);
	}

	private void setupTownManager(Location location) {
		spawnTownManagerVillager(location);
		setupTownManagerInventory();
		townworldDao.saveTownManagerLocation(getTownName(), location);
	}

	private void setupMayor(EconomyPlayer mayor) throws EconomyPlayerException {
		this.mayor = mayor;
		townworldDao.saveMayor(getTownName(), getMayor());
		citizens.add(mayor);
		townworldDao.saveCitizens(getTownName(), getCitizens());
		mayor.addJoinedTown(getTownName());
	}

	private void loadExistingTown(Townworld townworld, String townName)
			throws EconomyPlayerException, NumberFormatException, TownSystemException, GeneralEconomyException {
		this.townworld = townworld;
		this.townName = townName;
		loadTownManagerVillager();
		deputies = townworldDao.loadDeputies(getTownName());
		citizens = townworldDao.loadCitizens(getTownName());
		mayor = townworldDao.loadMayor(getTownName());
		townSpawn = townworldDao.loadTownSpawn(getTownName());
		bankAccount = bankManager.getBankAccountByIban(townworldDao.loadTownBankIban(getTownName()));
		tax = townworldDao.loadTax(getTownName());
		loadPlots();
	}

	private void loadPlots() {
		for (String coords : townworldDao.loadTownPlotCoords(getTownName())) {
			try {
				Plot plot = new PlotImpl(coords, validationHandler, townworldDao, this, serverProvider);
				plots.put(coords, plot);
			} catch (GeneralEconomyException | TownSystemException e) {
				log.warn("[Ultimate_Economy] Failed to load plot " + coords + " of town " + getTownName());
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}

	private void loadTownManagerVillager() throws TownSystemException {
		Location location = townworldDao.loadTownManagerLocation(getTownName());
		spawnTownManagerVillager(location);
		setupTownManagerInventory();
	}
}
