package com.ue.townsystem.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.townsystem.api.Plot;
import com.ue.townsystem.api.PlotController;
import com.ue.townsystem.api.Town;
import com.ue.townsystem.api.TownController;
import com.ue.townsystem.api.Townworld;
import com.ue.townsystem.api.TownworldController;
import com.ue.ultimate_economy.UltimateEconomy;

public class TownImpl implements Town {

    private String townName;
    private EconomyPlayer mayor;
    private ArrayList<EconomyPlayer> citizens = new ArrayList<>(), deputies = new ArrayList<>();
    private ArrayList<String> chunkCoords = new ArrayList<>();
    private Location townSpawn;
    private ArrayList<Plot> plots = new ArrayList<>();
    private double townBankAmount;
    private double tax; // TODO integrate tax system
    private Villager villager;
    private Inventory inventory;
    private Townworld townworld;

    /**
     * Constructor for loading a existing town.
     * 
     * @param townworld
     * @param townName
     * @throws TownSystemException
     * @throws PlayerException
     */
    public TownImpl(Townworld townworld, String townName) throws PlayerException, TownSystemException {
	loadTown(townworld, townName);
    }

    /**
     * Constructor for creating a new town.
     * 
     * @param townworld
     * @param mayor
     * @param townName
     * @param location
     * @throws TownSystemException
     * @throws PlayerException
     */
    public TownImpl(Townworld townworld, EconomyPlayer mayor, String townName, Location location)
	    throws TownSystemException, PlayerException {
	this.townworld = townworld;
	this.townName = townName;
	Chunk startChunk = location.getChunk();

	addPlot(new PlotImpl(this, mayor, startChunk.getX() + "/" + startChunk.getZ()));
	setupMayor(mayor);
	setupTownManager(location);
	setupTownBankAmount();
	setupTownSpawn(startChunk);
	setupTax();
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
	    throws PlayerException, GeneralEconomyException, TownSystemException {
	checkForTownDowsNotExist(newName);
	checkForPlayerIsMayor(player);
	String oldName = getTownName();
	saveTownManagerLocation();
	FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	List<String> deputyList = new ArrayList<>();
	for (EconomyPlayer deputy : deputies) {
	    deputyList.add(deputy.getName());
	}
	List<String> citizenList = new ArrayList<>();
	for (EconomyPlayer citizen : citizens) {
	    citizenList.add(citizen.getName());
	}
	config.set("Towns." + newName + ".townspawn",
		townSpawn.getX() + "/" + townSpawn.getY() + "/" + townSpawn.getZ());
	config.set("Towns." + newName + ".citizens", citizenList);
	config.set("Towns." + newName + ".chunks", chunkCoords);
	config.set("Towns." + newName + ".owner", mayor.getName());
	config.set("Towns." + newName + ".coOwners", deputyList);
	config.set("Towns." + newName + ".Plots", ""); // TODO FIXME add plots on renameing
	config.set("Towns." + townName, null);
	for (EconomyPlayer citizen : citizens) {
	    citizen.removeJoinedTown(townName);
	    citizen.addJoinedTown(newName);
	}
	villager.setCustomName(newName + " TownManager");
	List<String> townNameList = TownController.getTownNameList();
	townNameList.remove(townName);
	List<String> townNames = townworld.getTownNameList();
	townNames.remove(townName);
	townNames.add(newName);
	townworld.setTownNameList(townNames);
	townName = newName;
	townNameList.add(townName);
	config.set("TownNames", townNameList);
	save(config);
	if (player.isOnline() && sendMessage) {
	    performLocationCheckForAllPlayers();
	    player.getPlayer().sendMessage(MessageWrapper.getString("town_rename", oldName, newName));
	}

    }

    @Override
    public void expandTown(Chunk chunk, EconomyPlayer player, boolean sendMessage)
	    throws TownSystemException, PlayerException {
	checkForChunkNotClaimed(chunk);
	checkForPlayerHasDeputyPermission(player);
	checkForChunkIsConnectedToTown(chunk);
	decreaseTownBankAmount(getTownworld().getExpandPrice());
	addPlot(new PlotImpl(this, player, chunk.getX() + "/" + chunk.getZ()));
	performLocationCheckForAllPlayers();
	if (player.isOnline() && sendMessage) {
	    player.getPlayer().sendMessage(MessageWrapper.getString("town_expand"));
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
	    throws TownSystemException, PlayerException {
	checkForLocationIsInTown(location);
	checkForPlayerIsMayor(player);
	villager.teleport(location);
	saveTownManagerLocation();
    }

    @Override
    public void despawnAllVillagers() {
	for (Plot plot : getPlots()) {
	    plot.despawnSaleVillager();
	}
	villager.remove();
    }

    @Override
    public void buyPlot(EconomyPlayer citizen, int chunkX, int chunkZ) throws TownSystemException, PlayerException {
	Plot plot = getPlotByChunk(chunkX + "/" + chunkZ);
	checkForPlotIsForSale(plot);
	checkForPlayerIsNotPlotOwner(citizen, plot);
	for (EconomyPlayer resident : plot.getResidents()) {
	    plot.removeResident(resident);
	}
	plot.setOwner(citizen);
	plot.removeFromSale(citizen);

    }

    private void addPlot(Plot plot) throws TownSystemException {
	checkForChunkIsNotClaimedByThisTown(plot);
	getPlots().add(plot);
	getChunkList().add(plot.getChunkCoords());
	saveChunkCoordList();
    }

    private List<Plot> getPlots() {
	return plots;
    }

    @Override
    public void deletePlot(Plot plot) throws TownSystemException {
	checkForChunkIsClaimedByThisTown(plot);
	getChunkList().remove(plot.getChunkCoords());
	getPlots().remove(plot);
	saveRemovePlot(plot);
	saveChunkCoordList();
    }

    @Override
    public EconomyPlayer getMayor() {
	return mayor;
    }

    private void saveMayor(EconomyPlayer player) {
	FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	this.mayor = player;
	config.set("Towns." + townName + ".owner", player.getName());
	save(config);
    }

    @Override
    public ArrayList<EconomyPlayer> getCitizens() {
	return citizens;
    }

    /**
     * Set all citizens.
     * 
     * @param citizens
     */
    public void setCitizens(List<EconomyPlayer> citizens) {
	this.citizens = new ArrayList<>(citizens);
    }

    @Override
    public void joinTown(EconomyPlayer ecoPlayer) throws PlayerException, TownSystemException {
	checkForPlayerDidNotReachedMaxTowns(ecoPlayer);
	checkForPlayerIsNotCitizenPersonal(ecoPlayer);
	getCitizens().add(ecoPlayer);
	saveCitizens();
	ecoPlayer.addJoinedTown(getTownName());
    }

    @Override
    public void leaveTown(EconomyPlayer ecoPlayer) throws TownSystemException, PlayerException {
	checkForPlayerIsNotMayor(ecoPlayer);
	checkForPlayerIsCitizenPersonalError(ecoPlayer);
	if (isDeputy(ecoPlayer)) {
	    removeDeputy(ecoPlayer);
	}
	for (Plot plot : getPlots()) {
	    if (plot.isResident(ecoPlayer)) {
		plot.removeResident(ecoPlayer);
	    } else if (plot.isOwner(ecoPlayer)) {
		plot.setOwner(getMayor());
	    }
	}
	getCitizens().remove(ecoPlayer);
	saveCitizens();
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

    /**
     * <p>
     * Get a list of all claimed chunks.
     * <p>
     * 
     * @return ArrayList
     */
    public ArrayList<String> getChunkList() {
	return chunkCoords;
    }

    /**
     * <p>
     * Set the chunklist.
     * <p>
     * 
     * @param chunkCoords
     */
    public void setChunkList(List<String> chunkCoords) {
	this.chunkCoords.addAll(chunkCoords);
    }

    /**
     * Set the plotlist.
     * 
     * @param list
     */
    public void setPlotList(ArrayList<Plot> list) {
	plots.addAll(list);
    }

    @Override
    public Location getTownSpawn() {
	return townSpawn;
    }

    @Override
    public void changeTownSpawn(Location townSpawn, EconomyPlayer ecoPlayer, boolean sendMessage)
	    throws TownSystemException, PlayerException {
	checkForPlayerHasDeputyPermission(ecoPlayer);
	checkForLocationIsInTown(townSpawn);
	this.townSpawn = townSpawn;
	saveTownSpawn();
	if (ecoPlayer.isOnline() && sendMessage) {
	    ecoPlayer.getPlayer().sendMessage(MessageWrapper.getString("town_setTownSpawn", (int) getTownSpawn().getX(),
		    (int) getTownSpawn().getY(), (int) getTownSpawn().getZ()));
	}
    }

    @Override
    public ArrayList<EconomyPlayer> getDeputies() {
	return deputies;
    }

    @Override
    public double getTax() {
	return tax;
    }

    /**
     * Returns the number of owned plots of a citizen.
     * 
     * @param player
     * @return int
     * @throws TownSystemException
     */
    public int getNumberOfPlotsOwned(EconomyPlayer player) throws TownSystemException {
	checkForPlayerIsCitizen(player);
	int number = 0;
	for (Plot plot : getPlots()) {
	    if (plot.getOwner().equals(player)) {
		number++;
	    }
	}
	return number;
    }

    @Override
    public Plot getPlotByChunk(String chunkCoords) throws TownSystemException {
	for (Plot plot : getPlots()) {
	    if (plot.getChunkCoords().equals(chunkCoords)) {
		return plot;
	    }
	}
	throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_NOT_CLAIMED_BY_TOWN);
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
    public void addDeputy(EconomyPlayer player) throws TownSystemException, PlayerException {
	checkForPlayerIsNotDeputy(player);
	if (!isPlayerCitizen(player)) {
	    joinTown(player);
	}
	getDeputies().add(player);
	saveDeputies();
    }

    @Override
    public void removeDeputy(EconomyPlayer player) throws TownSystemException, PlayerException {
	checkForPlayerIsDeputy(player);
	getDeputies().remove(player);
	saveDeputies();
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
	for (String coords : chunkCoords) {
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

    private void save(FileConfiguration config) {
	try {
	    config.save(getTownworld().getSaveFile());
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public boolean isClaimedByTown(Chunk chunk) {
	if (getChunkList().contains(chunk.getX() + "/" + chunk.getZ())) {
	    return true;
	}
	return false;
    }

    @Override
    public double getTownBankAmount() {
	return townBankAmount;
    }

    @Override
    public void increaseTownBankAmount(double amount) {
	townBankAmount += amount;
	saveTownBankAmount();
    }

    @Override
    public void decreaseTownBankAmount(double amount) throws TownSystemException {
	checkForTownHasEnoughMoney(amount);
	townBankAmount -= amount;
	saveTownBankAmount();
    }

    @Override
    public Townworld getTownworld() {
	return townworld;
    }

    /*
     * Save methods
     */

    private void saveTownManagerLocation() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getTownworld().getSaveFile());
	config.set("Towns." + getTownName() + ".TownManagerVillager.x", villager.getLocation().getX());
	config.set("Towns." + getTownName() + ".TownManagerVillager.y", villager.getLocation().getY());
	config.set("Towns." + getTownName() + ".TownManagerVillager.z", villager.getLocation().getZ());
	config.set("Towns." + getTownName() + ".TownManagerVillager.world",
		villager.getLocation().getWorld().getName());
	save(config);
    }

    private void saveTownBankAmount() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getTownworld().getSaveFile());
	config.set("Towns." + getTownName() + ".bank", getTownBankAmount());
	save(config);
    }

    private void saveRemovePlot(Plot plot) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getTownworld().getSaveFile());
	config.set("Town." + getTownName() + ".Plots." + plot.getChunkCoords(), null);
	save(config);
    }

    private void saveChunkCoordList() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getTownworld().getSaveFile());
	config.set("Towns." + getTownName() + ".chunks", getChunkList());
	save(config);
    }

    private void saveCitizens() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getTownworld().getSaveFile());
	List<String> list = new ArrayList<>();
	for (EconomyPlayer economyPlayer : getCitizens()) {
	    list.add(economyPlayer.getName());
	}
	config.set("Towns." + getTownName() + ".citizens", list);
	save(config);
    }

    private void saveTownSpawn() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getTownworld().getSaveFile());
	config.set("Towns." + getTownName() + ".townspawn",
		getTownSpawn().getX() + "/" + getTownSpawn().getY() + "/" + getTownSpawn().getZ());
	save(config);
    }

    private void saveDeputies() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getTownworld().getSaveFile());
	List<String> list = new ArrayList<>();
	for (EconomyPlayer economyPlayer : getDeputies()) {
	    list.add(economyPlayer.getName());
	}
	config.set("Towns." + getTownName() + ".coOwners", list);
	save(config);
    }

    private void saveTax() {
	FileConfiguration config = YamlConfiguration.loadConfiguration(getTownworld().getSaveFile());
	config.set("Towns." + getTownName() + ".tax", getTax());
	save(config);
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
	saveTownSpawn();
    }

    private void setupTownBankAmount() {
	townBankAmount = 0;
	saveTownBankAmount();
    }

    private void setupTax() {
	tax = 0;
	saveTax();
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
	saveTownManagerLocation();
    }

    private void setupMayor(EconomyPlayer mayor) throws PlayerException, TownSystemException {
	this.mayor = mayor;
	saveMayor(mayor);
	joinTown(mayor);
    }

    /*
     * Loading methods
     * 
     */

    private void loadTown(Townworld townworld, String townName) throws TownSystemException, PlayerException {
	this.townworld = townworld;
	this.townName = townName;
	loadTownManagerVillager();
	loadDeputies();
	loadCitizens();
	loadMayor();
	loadTownSpawn();
	loadTownBankAmount();
	loadTax();
	loadPlots();
    }

    private void loadPlots() throws TownSystemException, PlayerException {
	FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	setChunkList(config.getStringList("Towns." + getTownName() + ".chunks"));
	ArrayList<Plot> plotList = new ArrayList<>();
	for (String coords : getChunkList()) {
	    Plot plot = PlotController.loadPlot(this, coords);
	    plotList.add(plot);
	}
	setPlotList(plotList);
    }

    private void loadTax() {
	FileConfiguration config = YamlConfiguration.loadConfiguration(getTownworld().getSaveFile());
	tax = config.getDouble("Towns." + getTownName() + ".tax");
    }

    private void loadTownBankAmount() {
	FileConfiguration config = YamlConfiguration.loadConfiguration(getTownworld().getSaveFile());
	townBankAmount = config.getDouble("Towns." + getTownName() + ".bank");
    }

    private void loadTownSpawn() throws TownSystemException, NumberFormatException, PlayerException {
	FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	World world = Bukkit.getWorld(config.getString("World"));
	checkForWorldExists(world);
	String locationString = config.getString("Towns." + getTownName() + ".townspawn");
	townSpawn = new Location(world, Double.valueOf(locationString.substring(0, locationString.indexOf("/"))),
		Double.valueOf(
			locationString.substring(locationString.indexOf("/") + 1, locationString.lastIndexOf("/"))),
		Double.valueOf(locationString.substring(locationString.lastIndexOf("/") + 1)));
    }

    private void loadDeputies() throws PlayerException {
	FileConfiguration config = YamlConfiguration.loadConfiguration(getTownworld().getSaveFile());
	List<EconomyPlayer> deputys = new ArrayList<>();
	for (String name : config.getStringList("Towns." + getTownName() + ".coOwners")) {
	    deputys.add(EconomyPlayerController.getEconomyPlayerByName(name));
	}
	this.deputies = new ArrayList<>(deputys);
    }

    private void loadMayor() throws PlayerException {
	FileConfiguration config = YamlConfiguration.loadConfiguration(getTownworld().getSaveFile());
	mayor = EconomyPlayerController.getEconomyPlayerByName(config.getString("Towns." + getTownName() + ".owner"));
    }

    private void loadCitizens() throws PlayerException {
	FileConfiguration config = YamlConfiguration.loadConfiguration(getTownworld().getSaveFile());
	List<EconomyPlayer> citizens = new ArrayList<>();
	for (String name : config.getStringList("Towns." + getTownName() + ".citizens")) {
	    citizens.add(EconomyPlayerController.getEconomyPlayerByName(name));
	}
	this.citizens = new ArrayList<>(citizens);
    }

    private void loadTownManagerVillager() throws TownSystemException {
	FileConfiguration config = YamlConfiguration.loadConfiguration(getTownworld().getSaveFile());
	World world = Bukkit.getWorld(config.getString("World"));
	checkForWorldExists(world);
	Location location = new Location(world, config.getDouble("Towns." + getTownName() + ".TownManagerVillager.x"),
		config.getDouble("Towns." + getTownName() + ".TownManagerVillager.y"),
		config.getDouble("Towns." + getTownName() + ".TownManagerVillager.z"));
	setupTownManagerInventory();
	spawnTownManagerVillager(location);
    }

    /*
     * Validation check methods
     * 
     */

    private void checkForWorldExists(World world) throws TownSystemException {
	if (world == null) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.WORLD_DOES_NOT_EXIST,
		    YamlConfiguration.loadConfiguration(getTownworld().getSaveFile()).getString("World"));
	}
    }

    private void checkForPlayerIsDeputy(EconomyPlayer player) throws TownSystemException {
	if (!getDeputies().contains(player)) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.PLAYER_IS_NO_DEPUTY);
	}
    }

    private void checkForPlayerIsCitizen(EconomyPlayer player) throws TownSystemException {
	if (!isPlayerCitizen(player)) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.PLAYER_IS_NOT_CITIZEN);
	}
    }

    private void checkForTownHasEnoughMoney(double amount) throws TownSystemException {
	if (amount > getTownBankAmount()) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.TOWN_HAS_NOT_ENOUGH_MONEY);
	}
    }

    private void checkForLocationIsInTown(Location townSpawn) throws TownSystemException {
	if (!getChunkList().contains(townSpawn.getChunk().getX() + "/" + townSpawn.getChunk().getZ())) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.LOCATION_NOT_IN_TOWN);
	}
    }

    private void checkForPlayerHasDeputyPermission(EconomyPlayer ecoPlayer)
	    throws TownSystemException, PlayerException {
	if (!hasDeputyPermissions(ecoPlayer)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
	}
    }

    private void checkForChunkNotClaimed(Chunk chunk) throws TownSystemException {
	if (!getTownworld().isChunkFree(chunk)) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_ALREADY_CLAIMED);
	}
    }

    private void checkForPlayerIsNotPlotOwner(EconomyPlayer citizen, Plot plot) throws PlayerException {
	if (plot.isOwner(citizen)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.YOU_ARE_THE_OWNER);
	}
    }

    private void checkForPlotIsForSale(Plot plot) throws TownSystemException {
	if (!plot.isForSale()) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.PLOT_IS_NOT_FOR_SALE);
	}
    }

    private void checkForPlayerIsCitizenPersonalError(EconomyPlayer citizen) throws PlayerException {
	if (!isPlayerCitizen(citizen)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.YOU_ARE_NO_CITIZEN);
	}
    }

    private void checkForPlayerIsMayor(EconomyPlayer player) throws TownSystemException, PlayerException {
	if (!isMayor(player)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
	}
    }

    private void checkForPlayerIsNotMayor(EconomyPlayer player) throws PlayerException, TownSystemException {
	if (isMayor(player)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.YOU_ARE_THE_OWNER);
	}
    }

    private void checkForTownDowsNotExist(String newName) throws GeneralEconomyException {
	if (TownController.getTownNameList().contains(newName)) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, newName);
	}
    }

    private void checkForChunkIsNotClaimedByThisTown(Plot plot) throws TownSystemException {
	if (getChunkList().contains(plot.getChunkCoords())) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_ALREADY_CLAIMED);
	}
    }

    private void checkForChunkIsClaimedByThisTown(Plot plot) throws TownSystemException {
	if (!getChunkList().contains(plot.getChunkCoords())) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_NOT_CLAIMED_BY_TOWN);
	}
    }

    private void checkForPlayerDidNotReachedMaxTowns(EconomyPlayer ecoPlayer) throws PlayerException {
	if (ecoPlayer.reachedMaxJoinedTowns()) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.MAX_REACHED);
	}
    }

    private void checkForPlayerIsNotCitizenPersonal(EconomyPlayer newCitizen) throws PlayerException {
	if (isPlayerCitizen(newCitizen)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.YOU_ARE_ALREADY_CITIZEN);
	}
    }

    private void checkForPlayerIsNotDeputy(EconomyPlayer player) throws TownSystemException {
	if (getDeputies().contains(player)) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.PLAYER_IS_ALREADY_DEPUTY);
	}
    }

    private void checkForChunkIsConnectedToTown(Chunk chunk) throws TownSystemException {
	if (!isChunkConnectedToTown(chunk.getX(), chunk.getZ())) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_IS_NOT_CONNECTED_WITH_TOWN);
	}
    }
}
