package com.ue.townsystem.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.townsystem.api.TownController;
import com.ue.townsystem.api.TownworldController;
import com.ue.townsystem.dataaccess.api.TownsystemDao;
import com.ue.townsystem.logic.api.Plot;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.townsystem.logic.impl.PlotImpl;
import com.ue.ultimate_economy.EconomyVillager;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

public class TownImpl implements Town {

	@Inject
	MessageWrapper messageWrapper;
	@Inject
	TownsystemValidationHandler validationHandler;
	@Inject
	BankManager bankManager;
	private final TownsystemDao townsystemDao;
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
	 * @param townsystemDao
	 * @param townworld
	 * @param townName
	 * @throws TownSystemException
	 * @throws EconomyPlayerException
	 * @throws GeneralEconomyException
	 */
	public TownImpl(TownsystemDao townsystemDao, Townworld townworld, String townName)
			throws EconomyPlayerException, TownSystemException, GeneralEconomyException {
		this.townsystemDao = townsystemDao;
		loadExistingTown(townworld, townName);
	}

	/**
	 * Constructor for creating a new town.
	 * 
	 * @param townsystemDao
	 * @param townworld
	 * @param mayor
	 * @param townName
	 * @param location
	 * @throws TownSystemException
	 * @throws EconomyPlayerException
	 * @throws GeneralEconomyException 
	 */
	public TownImpl(TownsystemDao townsystemDao, Townworld townworld, EconomyPlayer mayor, String townName,
			Location location) throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		this.townsystemDao = townsystemDao;
		setupNewTown(townworld, mayor, townName, location);
	}

	private void setupNewTown(Townworld townworld, EconomyPlayer mayor, String townName, Location location)
			throws EconomyPlayerException, TownSystemException, GeneralEconomyException {
		this.townName = townName;
		this.townworld = townworld;
		citizens = new ArrayList<>();
		deputies = new ArrayList<>();
		Chunk startChunk = location.getChunk();
		String startChunkCoords = startChunk.getX() + "/" + startChunk.getZ();
		getPlots().put(startChunkCoords, new PlotImpl(townsystemDao, this, mayor, startChunkCoords));
		setupMayor(mayor);
		setupTownManager(location);
		bankAccount = bankManager.createBankAccount(0);
		setupTownSpawn(startChunk);
		setTax(0);
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
				new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.TOWNMANAGER));
		villager.setProfession(Villager.Profession.NITWIT);
		villager.setSilent(true);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
		villager.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30000000, 30000000));
	}

	@Override
	public void renameTown(String newName, EconomyPlayer player, boolean sendMessage)
			throws EconomyPlayerException, GeneralEconomyException, TownSystemException {
		validationHandler.checkForTownDoesNotExist(newName);
		validationHandler.checkForPlayerIsMayor(getMayor(), player);
		String oldName = getTownName();
		townName = newName;
		for (EconomyPlayer citizen : getCitizens()) {
			citizen.removeJoinedTown(oldName);
			citizen.addJoinedTown(newName);
		}
		villager.setCustomName(getTownName() + " TownManager");

		List<String> townNameList = TownController.getTownNameList();
		townNameList.remove(oldName);

		townworld.setTownNameList(townNames);

		if (player.isOnline() && sendMessage) {
			performLocationCheckForAllPlayers();
			player.getPlayer().sendMessage(messageWrapper.getString("town_rename", oldName, newName));
		}

	}

	@Override
	public void expandTown(Chunk chunk, EconomyPlayer player, boolean sendMessage)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		validationHandler.checkForChunkNotClaimed(getTownworld(), chunk);
		validationHandler.checkForPlayerHasDeputyPermission(hasDeputyPermissions(player));
		validationHandler.checkForChunkIsConnectedToTown(isChunkConnectedToTown(chunk.getX(), chunk.getZ()));
		decreaseTownBankAmount(getTownworld().getExpandPrice());
		Plot plot = new PlotImpl(townsystemDao, this, player, chunk.getX() + "/" + chunk.getZ());
		getPlots().put(plot.getChunkCoords(), plot);
		performLocationCheckForAllPlayers();
		if (player.isOnline() && sendMessage) {
			player.getPlayer().sendMessage(messageWrapper.getString("town_expand"));
		}
	}

	private void performLocationCheckForAllPlayers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			TownworldController.handleTownWorldLocationCheck(p.getWorld().getName(), p.getLocation().getChunk(),
					p.getName());
		}
	}

	@Override
	public void openTownManagerVillagerInv(Player player) {
		player.openInventory(inventory);
	}

	@Override
	public void moveTownManagerVillager(Location location, EconomyPlayer player)
			throws TownSystemException, EconomyPlayerException {
		validationHandler.checkForLocationIsInTown(getPlots(), location);
		validationHandler.checkForPlayerIsMayor(getMayor(), player);
		villager.teleport(location);
		townsystemDao.saveTownManagerLocation(getTownName(), location);
	}

	@Override
	public void despawnAllVillagers() {
		for (Entry<String, Plot> plot : getPlots().entrySet()) {
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

	private Map<String, Plot> getPlots() {
		return plots;
	}

	@Override
	public void deletePlot(Plot plot) throws TownSystemException {
		validationHandler.checkForChunkIsClaimedByThisTown(getPlots(), plot.getChunkCoords());
		getPlots().remove(plot.getChunkCoords());
		townsystemDao.saveRemovePlot(getTownName(), plot.getChunkCoords());
	}

	@Override
	public EconomyPlayer getMayor() {
		return mayor;
	}

	@Override
	public List<EconomyPlayer> getCitizens() {
		return citizens;
	}

	@Override
	public void joinTown(EconomyPlayer ecoPlayer) throws EconomyPlayerException, TownSystemException {
		validationHandler.checkForPlayerDidNotReachedMaxTowns(ecoPlayer);
		validationHandler.checkForPlayerIsNotCitizenPersonal(getCitizens(), ecoPlayer);
		getCitizens().add(ecoPlayer);
		townsystemDao.saveCitizens(getTownName(), getCitizens());
		ecoPlayer.addJoinedTown(getTownName());
	}

	@Override
	public void leaveTown(EconomyPlayer ecoPlayer) throws TownSystemException, EconomyPlayerException {
		validationHandler.checkForPlayerIsNotMayor(getMayor(), ecoPlayer);
		validationHandler.checkForPlayerIsCitizenPersonalError(getCitizens(), ecoPlayer);
		if (isDeputy(ecoPlayer)) {
			removeDeputy(ecoPlayer);
		}
		for (Entry<String, Plot> plot : getPlots().entrySet()) {
			if (plot.getValue().isResident(ecoPlayer)) {
				plot.getValue().removeResident(ecoPlayer);
			} else if (plot.getValue().isOwner(ecoPlayer)) {
				plot.getValue().setOwner(getMayor());
			}
		}
		getCitizens().remove(ecoPlayer);
		townsystemDao.saveCitizens(getTownName(), getCitizens());
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
	public void changeTownSpawn(Location townSpawn, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws TownSystemException, EconomyPlayerException {
		validationHandler.checkForPlayerHasDeputyPermission(hasDeputyPermissions(ecoPlayer));
		validationHandler.checkForLocationIsInTown(getPlots(), townSpawn);
		this.townSpawn = townSpawn;
		townsystemDao.saveTownSpawn(getTownName(), getTownSpawn());
		if (ecoPlayer.isOnline() && sendMessage) {
			ecoPlayer.getPlayer().sendMessage(messageWrapper.getString("town_setTownSpawn", (int) getTownSpawn().getX(),
					(int) getTownSpawn().getY(), (int) getTownSpawn().getZ()));
		}
	}

	@Override
	public List<EconomyPlayer> getDeputies() {
		return deputies;
	}

	@Override
	public double getTax() {
		return tax;
	}

	@Override
	public Plot getPlotByChunk(String chunkCoords) throws TownSystemException {
		validationHandler.checkForChunkIsClaimedByThisTown(getPlots(), chunkCoords);
		for (Entry<String, Plot> plot : getPlots().entrySet()) {
			if (plot.getKey().equals(chunkCoords)) {
				return plot.getValue();
			}
		}
		// cannot happen because of validation
		return null;
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
		getDeputies().add(player);
		townsystemDao.saveDeputies(getTownName(), getDeputies());
	}

	@Override
	public void removeDeputy(EconomyPlayer player) throws TownSystemException {
		validationHandler.checkForPlayerIsDeputy(getDeputies(), player);
		getDeputies().remove(player);
		townsystemDao.saveDeputies(getTownName(), getDeputies());
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
		for (String coords : getPlots().keySet()) {
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
		if (getPlots().containsKey(chunk.getX() + "/" + chunk.getZ())) {
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
		validationHandler.checkForPositiveAmount(tax);
		this.tax = tax;
		townsystemDao.saveTax(getTownName(), tax);
	}

	/*
	 * Setup methods
	 * 
	 */

	private void setupTownSpawn(Chunk startChunk) {
		Location spawn = new Location(startChunk.getWorld(), (startChunk.getX() << 4) + 7, 0,
				(startChunk.getZ() << 4) + 7);
		spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));
		townSpawn = spawn;
		townsystemDao.saveTownSpawn(getTownName(), getTownSpawn());
	}

	private void setupTownManagerInventory() {
		inventory = Bukkit.createInventory(villager, 9, getTownName() + " TownManager");
		ItemStack itemStack = new ItemStack(Material.GREEN_WOOL, 1);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName("Join");
		itemStack.setItemMeta(meta);
		inventory.setItem(0, itemStack);
		itemStack = new ItemStack(Material.RED_WOOL, 1);
		meta = itemStack.getItemMeta();
		meta.setDisplayName("Leave");
		itemStack.setItemMeta(meta);
		inventory.setItem(1, itemStack);
	}

	private void setupTownManager(Location location) {
		setupTownManagerInventory();
		spawnTownManagerVillager(location);
		townsystemDao.saveTownManagerLocation(getTownName(), location);
	}

	private void setupMayor(EconomyPlayer mayor) throws EconomyPlayerException, TownSystemException {
		this.mayor = mayor;
		townsystemDao.saveMayor(getTownName(), getMayor());
		joinTown(mayor);
	}

	private void loadExistingTown(Townworld townworld, String townName)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		this.townworld = townworld;
		this.townName = townName;
		loadTownManagerVillager();
		deputies = townsystemDao.loadDeputies(getTownName());
		citizens = townsystemDao.loadCitizens(getTownName());
		mayor = townsystemDao.loadMayor(getTownName());
		townSpawn = townsystemDao.loadTownSpawn(getTownName());
		bankAccount = bankManager.getBankAccountByIban(townsystemDao.loadTownBankIban(getTownName()));
		tax = townsystemDao.loadTax(getTownName());
		loadPlots();
	}

	private void loadPlots() {
		for (String coords : townsystemDao.loadTownPlotCoords(getTownName())) {
			Plot plot = new PlotImpl(townsystemDao, this, coords);
			plots.put(coords, plot);
		}
	}

	private void loadTownManagerVillager() throws TownSystemException {
		Location location = townsystemDao.loadTownManagerLocation(getTownName());
		setupTownManagerInventory();
		spawnTownManagerVillager(location);
	}
}
