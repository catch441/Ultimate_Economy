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

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.TownSystemException;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.townsystem.town.api.Plot;
import com.ue.townsystem.town.api.Town;
import com.ue.townsystem.town.api.TownController;
import com.ue.townsystem.townworld.api.Townworld;
import com.ue.ultimate_economy.UEVillagerType;
import com.ue.ultimate_economy.Ultimate_Economy;

public class TownImpl implements Town {

	private String townName;
	private String owner;
	private ArrayList<String> citizens, coOwners;
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
	 * @param owner
	 * @param townName
	 * @param location
	 * @param load false, if the town is a new one
	 * @throws TownSystemException
	 */
	public TownImpl(Townworld townworld, String owner, String townName, Location location,boolean load) throws TownSystemException {
		Chunk startChunk = location.getChunk();
		this.townworld = townworld;
		this.townName = townName;
		this.owner = owner;
		citizens = new ArrayList<>();
		coOwners = new ArrayList<>();
		chunkCoords = new ArrayList<>();
		plots = new ArrayList<>();
		if(load) {
			spawnTownManagerVillager(location);
		} else {
			setOwner(owner);
			addCitizen(owner);
			addPlot(new PlotImpl(this, owner, startChunk.getX() + "/" + startChunk.getZ()), owner);
			saveTownManagerVillager(location);
			setTownBankAmount(0);
			Location spawn = new Location(startChunk.getWorld(), (startChunk.getX() << 4) + 7, 0,
					(startChunk.getZ() << 4) + 7);
			spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));
			setTownSpawn(spawn);
		}
	}

	/**
	 * Spawns a town villager with saving.
	 * <p>
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
	 * <p>
	 * Spawns a villager without saving.
	 * <p>
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
		villager.setMetadata("ue-type", new FixedMetadataValue(Ultimate_Economy.getInstance, UEVillagerType.TOWNMANAGER));
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
	
	public void renameTown(String newName, String player) throws TownSystemException, PlayerException {
		if(TownController.getTownNameList().contains(newName)) {
			throw new TownSystemException(TownSystemException.TOWN_ALREADY_EXIST);
		} else if(!isTownOwner(player)) {
			throw new TownSystemException(TownSystemException.YOU_ARE_NOT_OWNER);
		} else {
			FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
			
			config.set("Towns." + newName + ".TownManagerVillager.x", villager.getLocation().getBlockX());
			config.set("Towns." + newName + ".TownManagerVillager.y", villager.getLocation().getY());
			config.set("Towns." + newName + ".TownManagerVillager.z", villager.getLocation().getZ());
			config.set("Towns." + newName + ".TownManagerVillager.world", villager.getLocation().getWorld().getName());
			config.set("Towns." + newName + ".townspawn",
					townSpawn.getX() + "/" + townSpawn.getY() + "/" + townSpawn.getZ());
			config.set("Towns." + newName + ".citizens", citizens);
			config.set("Towns." + newName + ".chunks", chunkCoords);
			config.set("Towns." + newName + ".owner", owner);
			config.set("Towns." + newName + ".coOwners", coOwners);
			
			config.set("Towns." + townName, null);
			for(String citizen: citizens) {
				EconomyPlayer economyPlayer = EconomyPlayerController.getEconomyPlayerByName(citizen);
				economyPlayer.removeJoinedTown(townName);
				economyPlayer.addJoinedTown(newName);
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
			
		}
	}
	
	public void expandTown(Chunk chunk, String player) throws TownSystemException {
		if (!townworld.chunkIsFree(chunk)) {
			throw new TownSystemException(TownSystemException.CHUNK_ALREADY_CLAIMED);
		} else {
			if (!chunkIsConnectedToTown(chunk.getX(), chunk.getZ())) {
				throw new TownSystemException(TownSystemException.CHUNK_IS_NOT_CONNECTED_WITH_TOWN);
			} else if (!hasCoOwnerPermission(player)) {
				throw new TownSystemException(TownSystemException.PLAYER_HAS_NO_PERMISSION);
			} else {
				decreaseTownBankAmount(townworld.getExpandPrice());
				addPlot(new PlotImpl(this, player, chunk.getX() + "/" + chunk.getZ()),player);
			}
		}
	}

	public void openTownManagerVillagerInv(Player player) {
		player.openInventory(inventory);
	}

	public void moveTownManagerVillager(Location location, String player) throws TownSystemException {
		if (!isClaimedByTown(location.getChunk())) {
			throw new TownSystemException(TownSystemException.CHUNK_NOT_CLAIMED_BY_TOWN);
		} else if (!isTownOwner(player)) {
			throw new TownSystemException(TownSystemException.YOU_ARE_NOT_OWNER);
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
	 * 
	 */
	public void despawnTownManagerVillager() {
		villager.remove();
	}

	public void despawnAllVillagers() {
		for (Plot plot : plots) {
			plot.despawnSaleVillager();
		}
		despawnTownManagerVillager();
	}

	public void buyPlot(String citizen, int chunkX, int chunkZ) throws TownSystemException {
		Plot plot = getPlotByChunk(chunkX + "/" + chunkZ);
		if (!plot.isForSale()) {
			throw new TownSystemException(TownSystemException.PLOT_IS_NOT_FOR_SALE);
		} else if (plot.isOwner(citizen)) {
			throw new TownSystemException(TownSystemException.YOU_ARE_ALREADY_OWNER);
		} else {
			if (plot.isCoOwner(citizen)) {
				plot.removeCoOwner(citizen);
			}
			plot.setOwner(citizen);
			plot.removeFromSale(citizen);
		}
	}

	/**
	 * Adds a plot to a town
	 * 
	 * @param plot
	 * @param player
	 * @return File
	 * @throws TownSystemException
	 */
	public void addPlot(Plot plot, String player) throws TownSystemException {
		if (player != null && !isPlayerCitizen(player)) {
			throw new TownSystemException(TownSystemException.YOU_ARE_NO_CITIZEN);
		} else if (chunkCoords.contains(plot.getChunkCoords())) {
			throw new TownSystemException(TownSystemException.CHUNK_ALREADY_CLAIMED);
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
	public void deletePlot(Plot plot, World world) throws TownSystemException {
		if (chunkCoords.contains(plot.getChunkCoords())) {
			chunkCoords.remove(plot.getChunkCoords());
			FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
			config.set("Towns." + townName + ".chunks", chunkCoords);
			config.set("Town." + townName + ".Plots." + chunkCoords, null);
			plots.remove(plot);
			save(townworld.getSaveFile(), config);
		} else {
			throw new TownSystemException(TownSystemException.CHUNK_NOT_CLAIMED_BY_TOWN);
		}
	}

	/**
	 * Get town owner
	 * 
	 * @return String
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Set town owner
	 * 
	 * @param owner
	 */
	public void setOwner(String owner) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
		this.owner = owner;
		config.set("Towns." + townName + ".owner", owner);
		save(townworld.getSaveFile(), config);
	}

	public ArrayList<String> getCitizens() {
		return citizens;
	}

	/**
	 * <p>
	 * Set all citizens.
	 * <p>
	 * 
	 * @param citizens
	 */
	public void setCitizens(List<String> citizens) {
		this.citizens = new ArrayList<>(citizens);
	}
	
	public void joinTown(EconomyPlayer ecoPlayer) throws PlayerException, TownSystemException {
		if (ecoPlayer.reachedMaxJoinedTowns()) {
			throw new PlayerException(PlayerException.MAX_JOINED_TOWNS);
		} else {
			addCitizen(ecoPlayer.getName());
			ecoPlayer.addJoinedTown(townName);
		}
	}

	public void leaveTown(EconomyPlayer ecoPlayer) throws TownSystemException, PlayerException {
		removeCitizen(ecoPlayer.getName());
		ecoPlayer.removeJoinedTown(townName);
	}

	/**
	 * Add a player as citizen to a town
	 * <p>
	 * 
	 * @param newCitizen
	 * @return file
	 * @throws TownSystemException
	 */
	public void addCitizen(String newCitizen) throws TownSystemException {
		if (!isPlayerCitizen(newCitizen)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
			citizens.add(newCitizen);
			config.set("Towns." + townName + ".citizens", citizens);
			save(townworld.getSaveFile(), config);
		} else {
			throw new TownSystemException(TownSystemException.YOU_ARE_ALREADY_CITIZEN);
		}
	}

	/**
	 * Remove a citizen from a town
	 * 
	 * @param citizen
	 * @throws TownSystemException
	 */
	public void removeCitizen(String citizen) throws TownSystemException {
		if (isTownOwner(citizen)) {
			throw new TownSystemException(TownSystemException.YOU_ARE_THE_OWNER);
		} else if (!isPlayerCitizen(citizen)) {
			throw new TownSystemException(TownSystemException.YOU_ARE_NO_CITIZEN);
		} else {
			if (isCoOwner(citizen)) {
				removeCoOwner(citizen);
			}
			for (Plot plot : plots) {
				if (plot.isCoOwner(citizen)) {
					plot.removeCoOwner(citizen);
				} else if (plot.isOwner(citizen)) {
					plot.setOwner(owner);
				}
			}
			FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
			citizens.remove(citizen);
			config.set("Towns." + townName + ".citizens", citizens);
			save(townworld.getSaveFile(), config);
		}
	}

	public boolean isPlayerCitizen(String player) {
		if (citizens.contains(player)) {
			return true;
		} else {
			return false;
		}
	}

	public String getTownName() {
		return townName;
	}

	/**
	 * <p>
	 * Get a list of all claimed chunks
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
	 * Set the plotlist
	 * 
	 * @param list
	 */
	public void setPlotList(ArrayList<Plot> list) {
		plots.addAll(list);
	}

	public Location getTownSpawn() {
		return townSpawn;
	}

	public void setTownSpawn(Location townSpawn) throws TownSystemException {
		if (chunkCoords.contains(townSpawn.getChunk().getX() + "/" + townSpawn.getChunk().getZ())) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
			this.townSpawn = townSpawn;
			config.set("Towns." + townName + ".townspawn",
					townSpawn.getX() + "/" + townSpawn.getY() + "/" + townSpawn.getZ());
			this.townSpawn = townSpawn;
			save(townworld.getSaveFile(), config);
		} else {
			throw new TownSystemException(TownSystemException.LOCATION_NOT_IN_TOWN);
		}
	}

	/**
	 * <p>
	 * Get a list of CoOwners of the town.
	 * <p>
	 * 
	 * @return ArrayList
	 */
	public ArrayList<String> getCoOwners() {
		return coOwners;
	}

	/**
	 * <p>
	 * Set all coOwners without saving.
	 * <p>
	 * 
	 * @param coOwners
	 */
	public void setCoOwners(List<String> coOwners) {
		this.coOwners.addAll(coOwners);
	}

	/**
	 * <p>
	 * Returns the tax of the town.
	 * <p>
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
	public int getNumberOfPlotsOwned(String player) throws TownSystemException {
		if (isPlayerCitizen(player)) {
			int number = 0;
			for (Plot plot : plots) {
				if (plot.getOwner().equals(player)) {
					number++;
				}
			}
			return number;
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NOT_CITIZEN);
		}

	}

	public Plot getPlotByChunk(String chunkCoords) throws TownSystemException {
		for (Plot plot : plots) {
			if (plot.getChunkCoords().equals(chunkCoords)) {
				return plot;
			}
		}
		throw new TownSystemException(TownSystemException.CHUNK_NOT_CLAIMED_BY_TOWN);
	}

	/**
	 * Returns true if player is townowner.
	 * 
	 * @param player
	 * @return boolean
	 * @throws TownSystemException
	 */
	public boolean isTownOwner(String player) throws TownSystemException {
		if (isPlayerCitizen(player)) {
			if (player.equals(owner)) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NOT_CITIZEN);
		}
	}

	/**
	 * <p>
	 * Returns true if player is coOwner of this town.
	 * <p>
	 * 
	 * @param player
	 * @return boolean
	 * @throws TownSystemException
	 */
	public boolean isCoOwner(String player) throws TownSystemException {
		if (isPlayerCitizen(player)) {
			if (coOwners.contains(player)) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NOT_CITIZEN);
		}
	}

	public void addCoOwner(String coOwner) throws TownSystemException, PlayerException {
		if (!coOwners.contains(coOwner)) {
			if(!isPlayerCitizen(coOwner)) {
				addCitizen(coOwner);
				EconomyPlayerController.getEconomyPlayerByName(coOwner).addJoinedTown(townName);
			}
			FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
			coOwners.add(coOwner);
			config.set("Towns." + townName + ".coOwners", coOwners);
			save(townworld.getSaveFile(), config);
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_ALREADY_COOWNERN);
		}
	}

	public void removeCoOwner(String coOwner) throws TownSystemException {
		if (coOwners.contains(coOwner)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
			coOwners.remove(coOwner);
			config.set("Towns." + townName + ".coOwners", coOwners);
			save(townworld.getSaveFile(), config);
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NO_COOWNER);
		}
	}
	
	/**
	 * <p>
	 * Returns true, if the town has enough money.
	 * <p>
	 * 
	 * @param amount
	 * @return
	 */
	public boolean hasEnoughMoney(double amount) {
		if(townBankAmount >= amount) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hasCoOwnerPermission(String player) throws TownSystemException {
		if (isPlayerCitizen(player)) {
			if (isCoOwner(player) || isTownOwner(player)) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NOT_CITIZEN);
		}
	}

	/**
	 * Returns true if player is the townOwner, town coOwner, plot owner or plot
	 * coOwner.
	 * 
	 * @param player
	 * @param plot
	 * @return boolean
	 * @throws TownSystemException
	 */
	public boolean hasBuildPermissions(String player, Plot plot) throws TownSystemException {
		if (isPlayerCitizen(player)) {
			if (isTownOwner(player) || isCoOwner(player) || plot.isOwner(player) || plot.isCoOwner(player)) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NOT_CITIZEN);
		}
	}

	/**
	 * <p>
	 * Return true if chunk is connected to this town.
	 * <p>
	 * 
	 * @param chunkX
	 * @param chunkZ
	 * @return boolean
	 */
	public boolean chunkIsConnectedToTown(int chunkX, int chunkZ) {
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

	public boolean isClaimedByTown(Chunk chunk) {
		boolean is = false;
		if (chunkCoords.contains(chunk.getX() + "/" + chunk.getZ())) {
			is = true;
		}
		return is;
	}

	public double getTownBankAmount() {
		return townBankAmount;
	}

	public void increaseTownBankAmount(double amount) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
		townBankAmount += amount;
		config.set("Towns." + townName + ".bank", townBankAmount);
		setTownBankAmount(townBankAmount);
		save(townworld.getSaveFile(), config);
	}

	public void decreaseTownBankAmount(double amount) throws TownSystemException {
		if (amount > townBankAmount) {
			throw new TownSystemException(TownSystemException.TOWN_HAS_NOT_ENOUGH_MONEY);
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
	 * @return
	 */
	public void setTownBankAmount(double amount) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
		config.set("Towns." + townName + ".bank", amount);
		this.townBankAmount = amount;
		save(townworld.getSaveFile(), config);
	}
	
	public Townworld getTownworld() {
		return townworld;
	}
}
