package com.ue.townsystem.town.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
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

import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.player.api.EconomyPlayer;
import com.ue.townsystem.town.api.Plot;
import com.ue.townsystem.town.api.Town;
import com.ue.townsystem.town.api.TownController;
import com.ue.townsystem.townworld.api.Townworld;
import com.ue.townsystem.townworld.api.TownworldController;
import com.ue.ultimate_economy.UltimateEconomy;

public class TownImpl implements Town {

    private String townName;
    private EconomyPlayer mayor;
    private ArrayList<EconomyPlayer> citizens, deputys;
    private ArrayList<String> chunkCoords;
    private Location townSpawn;
    private ArrayList<Plot> plots;
    private double townBankAmount;
    private double tax; // TODO integrate tax system
    private Villager villager;
    private Inventory inventory;
    private Townworld townworld;

    /**
     * Creates a town object.
     * 
     * @param townworld
     * @param mayor
     * @param townName
     * @param location
     * @param load
     *            false, if the town is a new one
     * @throws TownSystemException
     * @throws PlayerException
     */
    public TownImpl(Townworld townworld, EconomyPlayer mayor, String townName, Location location, boolean load)
	    throws TownSystemException, PlayerException {
	Chunk startChunk = location.getChunk();
	this.townworld = townworld;
	this.townName = townName;
	this.mayor = mayor;
	citizens = new ArrayList<>();
	deputys = new ArrayList<>();
	chunkCoords = new ArrayList<>();
	plots = new ArrayList<>();
	if (load) {
	    spawnTownManagerVillager(location);
	} else {
	    setMayor(mayor);
	    addCitizen(mayor);
	    addPlot(new PlotImpl(this, mayor, startChunk.getX() + "/" + startChunk.getZ()), mayor);
	    saveTownManagerVillager(location);
	    setTownBankAmount(0);
	    Location spawn = new Location(startChunk.getWorld(), (startChunk.getX() << 4) + 7, 0,
		    (startChunk.getZ() << 4) + 7);
	    spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));
	    setTownSpawn(spawn, mayor, false);
	}
    }

    /**
     * Spawns a town villager with saving.
     * 
     * @param location
     */
    private void saveTownManagerVillager(Location location) {
	FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	config.set("Towns." + townName + ".TownManagerVillager.x", location.getX());
	config.set("Towns." + townName + ".TownManagerVillager.y", location.getY());
	config.set("Towns." + townName + ".TownManagerVillager.z", location.getZ());
	config.set("Towns." + townName + ".TownManagerVillager.world", location.getWorld().getName());
	spawnTownManagerVillager(location);
	save(townworld.getSaveFile(), config);
    }

    /**
     * Spawns a villager without saving.
     * 
     * @param location
     */
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
	inventory = Bukkit.createInventory(villager, 9, townName + " TownManager");
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

    @Override
    public void renameTown(String newName, EconomyPlayer player, boolean sendMessage)
	    throws TownSystemException, PlayerException {
	if (TownController.getTownNameList().contains(newName)) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.TOWN_ALREADY_EXIST);
	} else if (!isMayor(player)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
	} else {
	    FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	    String oldName = getTownName();
	    config.set("Towns." + newName + ".TownManagerVillager.x", villager.getLocation().getBlockX());
	    config.set("Towns." + newName + ".TownManagerVillager.y", villager.getLocation().getY());
	    config.set("Towns." + newName + ".TownManagerVillager.z", villager.getLocation().getZ());
	    config.set("Towns." + newName + ".TownManagerVillager.world", villager.getLocation().getWorld().getName());
	    config.set("Towns." + newName + ".townspawn",
		    townSpawn.getX() + "/" + townSpawn.getY() + "/" + townSpawn.getZ());
	    config.set("Towns." + newName + ".citizens", citizens);
	    config.set("Towns." + newName + ".chunks", chunkCoords);
	    config.set("Towns." + newName + ".owner", mayor);
	    config.set("Towns." + newName + ".coOwners", deputys);
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
	    save(townworld.getSaveFile(), config);
	    if (player.isOnline() && sendMessage) {
		for (Player p : Bukkit.getOnlinePlayers()) {
		    TownworldController.handleTownWorldLocationCheck(p.getWorld().getName(), p.getLocation().getChunk(),
			    p.getName());
		}
		player.getPlayer().sendMessage(MessageWrapper.getString("town_rename", oldName, newName));
	    }
	}
    }

    @Override
    public void expandTown(Chunk chunk, EconomyPlayer player, boolean sendMessage)
	    throws TownSystemException, PlayerException {
	if (!townworld.isChunkFree(chunk)) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_ALREADY_CLAIMED);
	} else {
	    if (!isChunkConnectedToTown(chunk.getX(), chunk.getZ())) {
		throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_IS_NOT_CONNECTED_WITH_TOWN);
	    } else if (!hasDeputyPermissions(player)) {
		throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
	    } else {
		decreaseTownBankAmount(townworld.getExpandPrice());
		addPlot(new PlotImpl(this, player, chunk.getX() + "/" + chunk.getZ()), player);
		for (Player p : Bukkit.getOnlinePlayers()) {
		    TownworldController.handleTownWorldLocationCheck(p.getWorld().getName(), p.getLocation().getChunk(),
			    p.getName());
		}
		if (player.isOnline() && sendMessage) {
		    player.getPlayer().sendMessage(MessageWrapper.getString("town_expand"));
		}
	    }
	}
    }

    @Override
    public void openTownManagerVillagerInv(Player player) {
	player.openInventory(inventory);
    }

    @Override
    public void moveTownManagerVillager(Location location, EconomyPlayer player)
	    throws TownSystemException, PlayerException {
	if (!isClaimedByTown(location.getChunk())) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_NOT_CLAIMED_BY_TOWN);
	} else if (!isMayor(player)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.YOU_ARE_NOT_OWNER);
	} else {
	    FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	    config.set("Towns." + townName + ".TownManagerVillager.x", location.getX());
	    config.set("Towns." + townName + ".TownManagerVillager.y", location.getY());
	    config.set("Towns." + townName + ".TownManagerVillager.z", location.getZ());
	    config.set("Towns." + townName + ".TownManagerVillager.world", location.getWorld().getName());
	    villager.teleport(location);
	    save(townworld.getSaveFile(), config);
	}
    }

    /**
     * Despawns the town managerVillager.
     */
    public void despawnTownManagerVillager() {
	villager.remove();
    }

    @Override
    public void despawnAllVillagers() {
	for (Plot plot : plots) {
	    plot.despawnSaleVillager();
	}
	despawnTownManagerVillager();
    }

    @Override
    public void buyPlot(EconomyPlayer citizen, int chunkX, int chunkZ) throws TownSystemException, PlayerException {
	Plot plot = getPlotByChunk(chunkX + "/" + chunkZ);
	if (!plot.isForSale()) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.PLOT_IS_NOT_FOR_SALE);
	} else if (plot.isOwner(citizen)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.YOU_ARE_THE_OWNER);
	} else {
	    if (plot.isResident(citizen)) {
		plot.removeResident(citizen);
	    }
	    plot.setOwner(citizen);
	    plot.removeFromSale(citizen);
	}
    }

    /**
     * Adds a plot to a town.
     * 
     * @param plot
     * @param player
     * @throws PlayerException
     * @throws TownSystemException
     */
    public void addPlot(Plot plot, EconomyPlayer player) throws PlayerException, TownSystemException {
	if (player != null && !isPlayerCitizen(player)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.YOU_ARE_NO_CITIZEN);
	} else if (chunkCoords.contains(plot.getChunkCoords())) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_ALREADY_CLAIMED);
	} else {
	    plots.add(plot);
	    chunkCoords.add(plot.getChunkCoords());
	    FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	    config.set("Towns." + townName + ".chunks", chunkCoords);
	    save(townworld.getSaveFile(), config);
	}
    }

    /**
     * Removes a chunk from a town.
     * 
     * @param plot
     * @throws TownSystemException
     */
    public void deletePlot(Plot plot) throws TownSystemException {
	if (chunkCoords.contains(plot.getChunkCoords())) {
	    chunkCoords.remove(plot.getChunkCoords());
	    FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	    config.set("Towns." + townName + ".chunks", chunkCoords);
	    config.set("Town." + townName + ".Plots." + chunkCoords, null);
	    plots.remove(plot);
	    save(townworld.getSaveFile(), config);
	} else {
	    throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_NOT_CLAIMED_BY_TOWN);
	}
    }

    @Override
    public EconomyPlayer getMayor() {
	return mayor;
    }

    @Override
    public void setMayor(EconomyPlayer player) {
	FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	this.mayor = player;
	config.set("Towns." + townName + ".owner", player.getName());
	save(townworld.getSaveFile(), config);
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
	if (ecoPlayer.reachedMaxJoinedTowns()) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.MAX_REACHED);
	} else {
	    addCitizen(ecoPlayer);
	    ecoPlayer.addJoinedTown(townName);
	}
    }

    @Override
    public void leaveTown(EconomyPlayer ecoPlayer) throws TownSystemException, PlayerException {
	removeCitizen(ecoPlayer);
	ecoPlayer.removeJoinedTown(townName);
    }

    // TODO move personal exceptions
    @Override
    public void addCitizen(EconomyPlayer newCitizen) throws PlayerException {
	if (!isPlayerCitizen(newCitizen)) {
	    FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	    citizens.add(newCitizen);
	    List<String> list = new ArrayList<>();
	    for (EconomyPlayer economyPlayer : citizens) {
		list.add(economyPlayer.getName());
	    }
	    config.set("Towns." + townName + ".citizens", list);
	    save(townworld.getSaveFile(), config);
	} else {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.YOU_ARE_ALREADY_CITIZEN);
	}
    }

    @Override
    public void removeCitizen(EconomyPlayer citizen) throws TownSystemException, PlayerException {
	if (isMayor(citizen)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.YOU_ARE_THE_OWNER);
	} else if (!isPlayerCitizen(citizen)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.YOU_ARE_NO_CITIZEN);
	} else {
	    if (isDeputy(citizen)) {
		removeDeputy(citizen);
	    }
	    for (Plot plot : plots) {
		if (plot.isResident(citizen)) {
		    plot.removeResident(citizen);
		} else if (plot.isOwner(citizen)) {
		    plot.setOwner(mayor);
		}
	    }
	    FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	    citizens.remove(citizen);
	    List<String> list = new ArrayList<>();
	    for (EconomyPlayer economyPlayer : citizens) {
		list.add(economyPlayer.getName());
	    }
	    config.set("Towns." + townName + ".citizens", list);
	    save(townworld.getSaveFile(), config);
	}
    }

    @Override
    public boolean isPlayerCitizen(EconomyPlayer player) {
	if (citizens.contains(player)) {
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
    public void setTownSpawn(Location townSpawn, EconomyPlayer ecoPlayer, boolean sendMessage)
	    throws TownSystemException, PlayerException {
	if (chunkCoords.contains(townSpawn.getChunk().getX() + "/" + townSpawn.getChunk().getZ())) {
	    if (hasDeputyPermissions(ecoPlayer)) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
		this.townSpawn = townSpawn;
		config.set("Towns." + townName + ".townspawn",
			townSpawn.getX() + "/" + townSpawn.getY() + "/" + townSpawn.getZ());
		this.townSpawn = townSpawn;
		save(townworld.getSaveFile(), config);
		if (ecoPlayer.isOnline() && sendMessage) {
		    ecoPlayer.getPlayer().sendMessage(MessageWrapper.getString("town_setTownSpawn",
			    (int) townSpawn.getX(), (int) townSpawn.getY(), (int) townSpawn.getZ()));
		}
	    } else {
		throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
	    }
	} else {
	    throw TownSystemException.getException(TownExceptionMessageEnum.LOCATION_NOT_IN_TOWN);
	}
    }

    /**
     * Get a list of deputys of the town.
     * 
     * @return ArrayList of EconomyPlayers
     */
    public ArrayList<EconomyPlayer> getDeputys() {
	return deputys;
    }

    /**
     * Set all deputys without saving.
     * 
     * @param deputys
     */
    public void setDeputys(List<EconomyPlayer> deputys) {
	this.deputys.addAll(deputys);
    }

    /**
     * Returns the tax of the town.
     * 
     * @return double
     */
    public double getTax() {
	return tax;
    }

    /**
     * Set tax with saving.
     * 
     * @param tax
     */
    public void setTax(double tax) {
	FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	config.set("Towns." + townName + ".tax", tax);
	this.tax = tax;
	save(townworld.getSaveFile(), config);
    }

    /**
     * Returns the number of owned plots of a citizen.
     * 
     * @param player
     * @return int
     * @throws TownSystemException
     */
    public int getNumberOfPlotsOwned(EconomyPlayer player) throws TownSystemException {
	if (isPlayerCitizen(player)) {
	    int number = 0;
	    for (Plot plot : plots) {
		if (plot.getOwner().equals(player)) {
		    number++;
		}
	    }
	    return number;
	} else {
	    throw TownSystemException.getException(TownExceptionMessageEnum.PLAYER_IS_NOT_CITIZEN);
	}

    }

    @Override
    public Plot getPlotByChunk(String chunkCoords) throws TownSystemException {
	for (Plot plot : plots) {
	    if (plot.getChunkCoords().equals(chunkCoords)) {
		return plot;
	    }
	}
	throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_NOT_CLAIMED_BY_TOWN);
    }

    @Override
    public boolean isMayor(EconomyPlayer player) throws TownSystemException {
	if (isPlayerCitizen(player)) {
	    if (player.equals(mayor)) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    throw TownSystemException.getException(TownExceptionMessageEnum.PLAYER_IS_NOT_CITIZEN);
	}
    }

    @Override
    public boolean isDeputy(EconomyPlayer player) throws TownSystemException {
	if (isPlayerCitizen(player)) {
	    if (deputys.contains(player)) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    throw TownSystemException.getException(TownExceptionMessageEnum.PLAYER_IS_NOT_CITIZEN);
	}
    }

    @Override
    public void addDeputy(EconomyPlayer player)
	    throws TownSystemException, PlayerException {
	if (!deputys.contains(player)) {
	    if (!isPlayerCitizen(player)) {
		addCitizen(player);
		player.addJoinedTown(townName);
	    }
	    FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	    deputys.add(player);
	    List<String> list = new ArrayList<>();
	    for (EconomyPlayer economyPlayer : deputys) {
		list.add(economyPlayer.getName());
	    }
	    config.set("Towns." + townName + ".coOwners", list);
	    save(townworld.getSaveFile(), config);
	} else {
	    throw TownSystemException.getException(TownExceptionMessageEnum.PLAYER_IS_ALREADY_DEPUTY);
	}
    }

    @Override
    public void removeDeputy(EconomyPlayer player) throws TownSystemException, PlayerException {
	if (deputys.contains(player)) {
	    FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	    deputys.remove(player);
	    List<String> list = new ArrayList<>();
	    for (EconomyPlayer economyPlayer : deputys) {
		list.add(economyPlayer.getName());
	    }
	    config.set("Towns." + townName + ".coOwners", list);
	    save(townworld.getSaveFile(), config);
	} else {
	    throw TownSystemException.getException(TownExceptionMessageEnum.PLAYER_IS_NO_DEPUTY);
	}
    }

    @Override
    public boolean hasEnoughMoney(double amount) {
	if (townBankAmount >= amount) {
	    return true;
	} else {
	    return false;
	}
    }

    @Override
    public boolean hasDeputyPermissions(EconomyPlayer player) throws TownSystemException {
	if (isPlayerCitizen(player)) {
	    if (isDeputy(player) || isMayor(player)) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    throw TownSystemException.getException(TownExceptionMessageEnum.PLAYER_IS_NOT_CITIZEN);
	}
    }

    @Override
    public boolean hasBuildPermissions(EconomyPlayer player, Plot plot) throws TownSystemException {
	if (isPlayerCitizen(player)) {
	    if (isMayor(player) || isDeputy(player) || plot.isOwner(player) || plot.isResident(player)) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    throw TownSystemException.getException(TownExceptionMessageEnum.PLAYER_IS_NOT_CITIZEN);
	}
    }

    @Override
    public boolean isChunkConnectedToTown(int chunkX, int chunkZ) {
	for (String coords : chunkCoords) {
	    int x = Integer.valueOf(coords.substring(0, coords.indexOf("/")));
	    int z = Integer.valueOf(coords.substring(coords.indexOf("/") + 1));
	    int newX = x - chunkX;
	    int newZ = z - chunkZ;
	    if ((newX == 0 && newZ == 1) || (newX == 1 && newZ == 0) || (newX == 0 && newZ == -1)
		    || (newX == -1 && newZ == 0)) {
		return true;
	    }
	}
	return false;
    }

    private void save(File file, FileConfiguration config) {
	try {
	    config.save(file);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public boolean isClaimedByTown(Chunk chunk) {
	boolean is = false;
	if (chunkCoords.contains(chunk.getX() + "/" + chunk.getZ())) {
	    is = true;
	}
	return is;
    }

    @Override
    public double getTownBankAmount() {
	return townBankAmount;
    }

    @Override
    public void increaseTownBankAmount(double amount) {
	FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	townBankAmount += amount;
	config.set("Towns." + townName + ".bank", townBankAmount);
	setTownBankAmount(townBankAmount);
	save(townworld.getSaveFile(), config);
    }

    @Override
    public void decreaseTownBankAmount(double amount) throws TownSystemException {
	if (amount > townBankAmount) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.TOWN_HAS_NOT_ENOUGH_MONEY);
	} else {
	    FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	    townBankAmount -= amount;
	    config.set("Towns." + townName + ".bank", townBankAmount);
	    setTownBankAmount(townBankAmount);
	    save(townworld.getSaveFile(), config);
	}
    }

    /**
     * Set town bank amount with saving.
     * 
     * @param amount
     */
    public void setTownBankAmount(double amount) {
	FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	config.set("Towns." + townName + ".bank", amount);
	this.townBankAmount = amount;
	save(townworld.getSaveFile(), config);
    }

    @Override
    public Townworld getTownworld() {
	return townworld;
    }
}
