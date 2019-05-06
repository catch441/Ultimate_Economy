package com.ue.townsystem;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ue.exceptions.TownSystemException;
import com.ue.exceptions.banksystem.TownHasNotEnoughMoneyException;

public class Town {

	private String townName;
	private String owner;
	private ArrayList<String> citizens,coOwners;
	private ArrayList<String> chunkCoords;
	private Location townSpawn;
	private ArrayList<Plot> plots;
	private File file;
	private double townBankAmount;
	private double tax; //TODO integrate tax system
	private Villager villager;
	private Inventory inventory;
	
	/**
	 * <p>
	 * Creates a town object.
	 * <p>
	 * @param file 
	 * @param owner
	 * @param townName
	 * @param startChunk
	 * @throws TownSystemException 
	 */
	public Town(File file,String owner,String townName,Location location) throws TownSystemException {
		Chunk startChunk = location.getChunk();
		this.townName = townName;
		this.owner = owner;
		citizens = new ArrayList<>();
		coOwners = new ArrayList<>();
		chunkCoords = new ArrayList<>();
		plots = new ArrayList<>();
		setOwner(file, owner);
		file = addCitizen(file, owner);
		file = addChunk(file, startChunk.getX(), startChunk.getZ(),null);
		file = setTownBankAmount(file,0);
		Location spawn = new Location(startChunk.getWorld(), (startChunk.getX() << 4) + 7, 0, (startChunk.getZ() << 4) + 7);
		spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));
		this.file = setTownSpawn(file, spawn);
		this.file = spawnTownManagerVillager(this.file, location);
	}
	
	/**
	 * <p>
	 * Only for loading.
	 * <p>
	 * @param owner
	 * @param townName
	 */
	private Town (String owner,String townName,Location location){
		this.townName = townName;
		this.owner = owner;
		citizens = new ArrayList<>();
		coOwners = new ArrayList<>();
		chunkCoords = new ArrayList<>();
		plots = new ArrayList<>();
		spawnTownManagerVillager(location);
	}
	
	/**
	 * <p>
	 * Returns the File.
	 * <p>
	 * @return file
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * <p>
	 * Spawns a town villager with saving.
	 * <p>
	 * @param file
	 * @param location
	 * @return file
	 */
	private File spawnTownManagerVillager(File file, Location location) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Towns." + townName + ".TownManagerVillager.x", location.getX());
		config.set("Towns." + townName + ".TownManagerVillager.y", location.getY());
		config.set("Towns." + townName + ".TownManagerVillager.z", location.getZ());
		config.set("Towns." + townName + ".TownManagerVillager.world", location.getWorld().getName());
		spawnTownManagerVillager(location);
		return save(file, config);
	}
	
	/**
	 * <p>
	 * Spawns a villager without saving.
	 * <p>
	 * @param location
	 */
	private void spawnTownManagerVillager(Location location) {
		Collection<Entity> entitys = location.getWorld().getNearbyEntities(location, 10,10,10);
		for(Entity entity:entitys) {
			if(entity.getName().equals(townName + " TownManager")) {
				entity.remove();
			}
		}
		villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);     
		villager.setCustomName(townName + " TownManager");
		villager.setCustomNameVisible(true);
		villager.setProfession(Villager.Profession.NITWIT);
		villager.setSilent(true);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000,30000000));             
		villager.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30000000,30000000));
		inventory = Bukkit.createInventory(null, 9, townName + " TownManager");
		ItemStack itemStack = new ItemStack(Material.GREEN_WOOL,1);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName("Join");
		itemStack.setItemMeta(meta);
		inventory.setItem(0,itemStack);
		itemStack = new ItemStack(Material.RED_WOOL,1);
		meta = itemStack.getItemMeta();
		meta.setDisplayName("Leave");
		itemStack.setItemMeta(meta);
		inventory.setItem(1, itemStack);
	}
	
	/**
	 * <p>
	 * Opens the inventory of the TownManager.
	 * <p>
	 */
	public void openTownManagerVillagerInv(Player player) {
		player.openInventory(inventory);
	}
	
	/**
	 * 
	 * @param file
	 * @param location
	 * @param player
	 * @return
	 * @throws TownSystemException
	 */
	public File moveTownManagerVillager(File file,Location location,String player) throws TownSystemException {
		if(!isClaimedByTown(location.getChunk())) {
			throw new TownSystemException(TownSystemException.CHUNK_NOT_CLAIMED_BY_TOWN);
		}
		else if(isTownOwner(player)) {
			throw new TownSystemException(TownSystemException.YOU_ARE_NOT_OWNER);
		}
		else {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Towns." + townName + ".TownManagerVillager.x", location.getX());
			config.set("Towns." + townName + ".TownManagerVillager.y", location.getY());
			config.set("Towns." + townName + ".TownManagerVillager.z", location.getZ());
			config.set("Towns." + townName + ".TownManagerVillager.world", location.getWorld().getName());
			villager.teleport(location);
			return save(file, config);
		}
	}
	
	/**
	 * <p>
	 * Despawns the town managerVillager.
	 * <p>
	 */
	public void despawnTownManagerVillager() {
		villager.remove();
	}
	
	/**
	 * <p>
	 * Removes the town managerVillager with saving.
	 * <p>
	 * @param file
	 * @return
	 */
	private File removeTownManagerVillager(File file) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Towns." + townName + ".TownManagerVillager", null);
		World world = villager.getLocation().getWorld();
		villager.remove();
		world.save();
		return save(file, config);
	}
	
	/**
	 * <p>
	 * Despawns all town villagers
	 * <p>
	 */
	public void despawnAllVillagers() {
		for(Plot plot:plots) {
			plot.despawnSaleVillager();
		}
		despawnTownManagerVillager();
	}
	
	/**
	 * <p>
	 * Delets this town in savefile,
	 * <p>
	 * @param file
	 * @return
	 */
	public File deleteTown(File file) {
		file = removeTownManagerVillager(file);
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Towns." + townName, null);
		return save(file, config);
	}
	
	/**
	 * <p>
	 * 	Set a plot for sale.
	 * <p>
	 * @param file
	 * @param salePrice
	 * @param chunkX
	 * @param chunkZ
	 * @param player
	 * @return File
	 * @throws TownSystemException 
	 */
	public File setPlotForSale(File file,double salePrice,String player,Location location) throws TownSystemException {
		String coords = location.getChunk().getX() + "/" + location.getChunk().getZ();
		if(!chunkCoords.contains(coords)) {
			throw new TownSystemException(TownSystemException.CHUNK_NOT_CLAIMED_BY_TOWN);
		}
		else {
			Plot plot = getPlotByChunkCoords(coords);
			if(plot.isOwner(player)) {
				return plot.setForSale(file,salePrice,location);
			}
			else {
				throw new TownSystemException(TownSystemException.PLAYER_HAS_NO_PERMISSION);
			}
		}
	}
	
	/**
	 * <p>
	 * Remove a plot from sale.
	 * <p>
	 * @param file
	 * @param chunkX
	 * @param chunkZ
	 * @param player
	 * @return File
	 * @throws TownSystemException
	 */
	public File removePlotFromSale(File file,int chunkX,int chunkZ,String player) throws TownSystemException {
		String coords = chunkX + "/" + chunkZ;
		if(!isPlayerCitizen(player)) {
			throw new TownSystemException(TownSystemException.YOU_ARE_NO_CITIZEN);
		}
		else if(!chunkCoords.contains(coords)) {
			throw new TownSystemException(TownSystemException.CHUNK_NOT_CLAIMED_BY_TOWN);
		}
		else {
			return getPlotByChunkCoords(coords).removeFromSale(file, player);
		}
	}
	
	/**
	 * <p>
	 * Buy a plot in a town if the plot is for sale.
	 * Did not handle payment.
	 * <p>
	 * @param townworldfile
	 * @param citizen
	 * @param chunk	(format "X/Z")
	 * @return File (townworldfile)
	 * @throws TownSystemException 
	 */
	public File buyPlot(File townworldfile,String citizen,int chunkX,int chunkZ) throws TownSystemException {
		Plot plot = getPlotByChunkCoords(chunkX + "/" + chunkZ);
		if(!plot.isForSale()) {
			throw new TownSystemException(TownSystemException.PLOT_IS_NOT_FOR_SALE);
		}
		else if(plot.isOwner(citizen)) {
			throw new TownSystemException(TownSystemException.YOU_ARE_ALREADY_OWNER);
		}
		else {
			if(plot.isCoOwner(citizen)) {
				townworldfile = plot.removeCoOwner(townworldfile, citizen);
			}
			townworldfile = plot.setOwner(townworldfile, citizen);
			return removePlotFromSale(townworldfile, chunkX, chunkZ, citizen);
		}
	}
	
	/**
	 * <p>
	 * Adds a chunk to a town
	 * <p>
	 * @param file
	 * @param chunkX
	 * @param chunkZ
	 * @param player
	 * @return File
	 * @throws TownSystemException
	 */
	public File addChunk(File file,int chunkX,int chunkZ,String player) throws TownSystemException {
		String coords = chunkX + "/" + chunkZ;
		if(player != null && !isPlayerCitizen(player)) {
			throw new TownSystemException(TownSystemException.YOU_ARE_NO_CITIZEN);
		}
		else if(chunkCoords.contains(coords)) {
			throw new TownSystemException(TownSystemException.CHUNK_ALREADY_CLAIMED);
		}
		else {
			Plot plot = new Plot(file, owner, coords, townName);
			plots.add(plot);
			file = plot.getSaveFile();
			chunkCoords.add(coords);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Towns." + townName + ".chunks", chunkCoords);
			return save(file,config);
		}
	}
	
	/**
	 * <p>
	 * Removes a chunk from a town.
	 * <p>
	 * @param file
	 * @param chunk	(format "X/Z")
	 * @return file
	 * @throws TownSystemException 
	 */
	public File removeChunk(File file,int chunkX,int chunkZ,World world) throws TownSystemException {
		String coords = chunkX + "/" + chunkZ;
		if(chunkCoords.contains(coords)) {
			chunkCoords.remove(coords);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Towns." + townName + ".chunks", chunkCoords);
			config.set("Town." + townName + ".Plots." + chunkCoords, null);
			//TODO not for future, find a better solution
			//world.regenerateChunk(chunkX, chunkZ);
			//world.save();
			int index = -1;
			for(Plot plot: plots) {
				if(plot.getChunkCoords().equals(coords)) {
					index = plots.indexOf(plot);
					break;
				}
			}
			if(index != -1) {
				plots.remove(index);
			}
			return save(file,config);
		}
		else {
			throw new TownSystemException(TownSystemException.CHUNK_NOT_CLAIMED_BY_TOWN);
		}
	}
	
	/**
	 * <p>
	 * Get town owner
	 * <p>
	 * @return String
	 */
	public String getOwner() {
		return owner;
	}
	
	/**
	 * <p>
	 * Set town owner
	 * <p>
	 * @param file
	 * @param owner
	 * @return file
	 */
	public File setOwner(File file,String owner) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		this.owner = owner;
		config.set("Towns." + townName + ".owner", owner);
		return save(file,config);
	}
	
	/**
	 * <p>
	 * Get list of citizens
	 * <p>
	 * @return ArrayList
	 */
	public ArrayList<String> getCitizens() {
		return citizens;
	}
	/**
	 * <p>
	 * Set all citizens.
	 * <p>
	 * @param citizens
	 */
	private void setCitizens(List<String> citizens) {
		this.citizens.addAll(citizens);
	}
	
	/**
	 * <p>
	 * Add a player as citizen to a town
	 * <p>
	 * @param file
	 * @param newCitizen
	 * @return file
	 * @throws TownSystemException 
	 */
	public File addCitizen(File file,String newCitizen) throws TownSystemException {
		if(!isPlayerCitizen(newCitizen)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			citizens.add(newCitizen);
			config.set("Towns." + townName + ".citizens", citizens);
			return save(file,config);
		}
		else {
			throw new TownSystemException(TownSystemException.YOU_ARE_ALREADY_CITIZEN);
		}
	}
	
	/**
	 * <p>
	 * Remove a player as citizen from a town
	 * <p>
	 * @param file
	 * @param citizen
	 * @return file
	 * @throws TownSystemException 
	 */
	public File removeCitizen(File file,String citizen) throws TownSystemException {
		if(isTownOwner(citizen)) {
			throw new TownSystemException(TownSystemException.YOU_ARE_THE_OWNER);
		}
		else if(!isPlayerCitizen(citizen)) {
			throw new TownSystemException(TownSystemException.YOU_ARE_NO_CITIZEN);
		}
		else {
			if(isCoOwner(citizen)) {
				file = removeCoOwner(file, citizen);
			}
			for(Plot plot:plots) {
				if(plot.isCoOwner(citizen)) {
					file = plot.removeCoOwner(file, citizen);
				}
				else if(plot.isOwner(citizen)) {
					file = plot.setOwner(file, owner);
				}
			}
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			citizens.remove(citizen);
			config.set("Towns." + townName + ".citizens", citizens);
			return save(file,config);
		}
	}
	
	/**
	 * <p>
	 * Returns true if player is a citizen of this town.
	 * <p>
	 * @param player
	 * @return
	 */
	public boolean isPlayerCitizen(String player) {
		if(citizens.contains(player)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * <p>
	 * Get town name
	 * <p>
	 * @return String
	 */
	public String getTownName() {
		return townName;
	}
	
	/**
	 * <p>
	 * Get a list of all claimed chunks
	 * <p>
	 * @return ArrayList
	 */
	public ArrayList<String> getChunkList() {
		return chunkCoords;
	}
	/**
	 * <p>
	 * Set the chunklist.
	 * <p>
	 * @param chunkCoords
	 */
	private void setChunkList(List<String> chunkCoords) {
		this.chunkCoords.addAll(chunkCoords);
	}
	
	/**
	 * <p>
	 * Set the plotlist
	 * <p>
	 * @param list
	 */
	private void setPlotList(ArrayList<Plot> list) {
		plots.addAll(list);
	}
	
	/**
	 * <p>
	 * Get the town spawn location.
	 * <p>
	 * @return Location
	 */
	public Location getTownSpawn() {
		return townSpawn;
	}
	
	/**
	 * <p>
	 * Set the town spawn location.
	 * <p>
	 * @param file
	 * @param townSpawn
	 * @return file
	 * @throws TownSystemException 
	 */
	public File setTownSpawn(File file,Location townSpawn) throws TownSystemException {
		if(chunkCoords.contains(townSpawn.getChunk().getX() + "/" + townSpawn.getChunk().getZ())) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			this.townSpawn = townSpawn;
			config.set("Towns." + townName + ".townspawn", townSpawn.getX() + "/" + townSpawn.getY() + "/" + townSpawn.getZ());
			return save(file,config);
		}
		else {
			throw new TownSystemException(TownSystemException.LOCATION_NOT_IN_TOWN);
		}
	}
	
	/**
	 * <p>
	 * Set TownSpawn without saving.
	 * <p>
	 * @param location
	 */
	private void setTownSpawn(Location location) {
		townSpawn = location;
	}
	
	/**
	 * <p>
	 * Get a list of CoOwners of the town.
	 * <p>
	 * @return ArrayList
	 */
	public ArrayList<String> getCoOwners() {
		return coOwners;
	}
	
	/**
	 * <p>
	 * Set all coOwners without saving.
	 * <p>
	 * @param coOwners
	 */
	private void setCoOwners(List<String> coOwners) {
		this.coOwners.addAll(coOwners);
	}
	
	/**
	 * <p>
	 * Returns the tax  of the town.
	 * <p>
	 * @return double
	 */
	public double getTax() {
		return tax;
	}

	/**
	 * <p>
	 * Set tax without saving.
	 * <p>
	 * @param tax
	 */
	private void setTax(double tax) {
		this.tax = tax;
	}
	
	/**
	 * <p>
	 * Set tax with saving.
	 * <p>
	 * @param file
	 * @param tax
	 * @return file
	 */
	public File setTax(File file,double tax) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Towns." + townName + ".tax", tax);
		setTax(tax);
		return save(file, config);
	}
	
	/**
	 * <p>
	 * Returns the number of owned plots of a citizen.
	 * <p>
	 * @param player
	 * @return int
	 * @throws TownSystemException 
	 */
	public int getNumberOfPlotsOwned(String player) throws TownSystemException {
		if(isPlayerCitizen(player)) {
			int number = 0;
			for(Plot plot:plots) {
				if(plot.getOwner().equals(player)) {
					number++;
				}
			}
			return number;
		}
		else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NOT_CITIZEN);
		}
		
	}
	
	/**
	 * <p>
	 * Returns a Plot by chunk coords.
	 * <p>
	 * @param chunkX
	 * @param chunkZ
	 * @return Plot
	 * @throws TownSystemException 
	 */
	public Plot getPlotByChunk(int chunkX,int chunkZ) throws TownSystemException {
		for(Plot plot:plots) {
			if(plot.getChunkCoords().equals(chunkX + "/" + chunkZ)) {
				return plot;
			}
		}
		throw new TownSystemException(TownSystemException.CHUNK_NOT_CLAIMED_BY_TOWN);
	}
	
	 /**
	  * <p>
	  * Returns true if player is townowner.
	  * <p>
	  * @param player
	  * @return boolean
	 * @throws TownSystemException 
	  */
	public boolean isTownOwner(String player) throws TownSystemException {
		if(isPlayerCitizen(player)) {
			if(player.equals(owner)) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NOT_CITIZEN);
		}
	}
	
	/**
	 * <p>
	 * 	Returns true if player is coOwner of this town.
	 * <p>
	 * @param player
	 * @return boolean
	 * @throws TownSystemException 
	 */
	public boolean isCoOwner(String player) throws TownSystemException {
		if(isPlayerCitizen(player)) {
			if(coOwners.contains(player)) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NOT_CITIZEN);
		}
	}
	
	/**
	 * <p>
	 * Set a player as CoOwner of a town
	 * <p>
	 * @param file
	 * @param coOwner
	 * @return file
	 * @throws TownSystemException 
	 */
	public File addCoOwner(File file,String coOwner) throws TownSystemException {
		if(!coOwners.contains(coOwner)) {
			file = addCitizen(file, coOwner);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			coOwners.add(coOwner);
			config.set("Towns." + townName + ".coOwners", coOwners);
			return save(file,config);
		}
		else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_ALREADY_COOWNERN);
		}
	}
	
	/**
	 * <p>
	 * Removes a coOwner from the town.
	 * <p>
	 * @param file
	 * @param coOwner
	 * @return File
	 * @throws TownSystemException
	 */
	public File removeCoOwner(File file,String coOwner) throws TownSystemException {
		if(coOwners.contains(coOwner)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			coOwners.remove(coOwner);
			config.set("Towns." + townName + ".coOwners", coOwners);
			return save(file,config);
		}
		else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NO_COOWNER);
		}
	}
	
	/**
	 * <p>
	 * Returns true if player is townOwner or coOwner
	 * <p>
	 * @param player
	 * @return boolean
	 * @throws TownSystemException 
	 */
	public boolean hasCoOwnerPermission(String player) throws TownSystemException {
		if(isPlayerCitizen(player)) {
			if(isCoOwner(player) || isTownOwner(player)) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NOT_CITIZEN);
		}
	}
	
	/**
	 * <p>
	 * Returns true if player is the townOwner, town coOwner, plot owner or plot coOwner.
	 * <p>
	 * @param player
	 * @param chunk (format "X/Z")
	 * @return boolean
	 * @throws TownSystemException 
	 */
	public boolean hasBuildPermissions(String player,String chunk) throws TownSystemException {
		if(isPlayerCitizen(player)) {
			Plot plot = getPlotByChunkCoords(chunk);
			if(isTownOwner(player) || isCoOwner(player) || plot.isOwner(player) || plot.isCoOwner(player)) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NOT_CITIZEN);
		}
	}
	
	/**
	 * <p>
	 * Return true if chunk is connected to this town.
	 * <p>
	 * @param chunkX
	 * @param chunkZ
	 * @return boolean
	 */
	public boolean chunkIsConnectedToTown(int chunkX,int chunkZ) {
		for(String coords:chunkCoords) {
			int x = Integer.valueOf(coords.substring(0,coords.indexOf("/")));
			int z = Integer.valueOf(coords.substring(coords.indexOf("/")+1));
			int newX = x-chunkX;
			int newZ = z-chunkZ;
			if((newX == 0 && newZ == 1) || (newX == 1 && newZ == 1) || (newX == 0 && newZ == -1) || (newX == -1 && newZ == 0)) {
				return true;
			}
		}
		return false;
	}
	
	private File save(File file,FileConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * <p>
	 * Returns true if the cunk is owned by any town
	 * <p>
	 * @param chunk	(format "X/Z")
	 * @return
	 */
	public boolean isClaimedByTown(Chunk chunk) {
		boolean is = false;
		if(chunkCoords.contains(chunk.getX() + "/" + chunk.getZ())) {
			is = true;
		}
		return is;
	}	

	/**
	 * <p>
	 * Returns the town bank amount.
	 * <p>
	 * @return double
	 */
	public double getTownBankAmount() {
		return townBankAmount;
	}

	/**
	 * <p>
	 * Increase the town bank amount.
	 * <p>
	 * @param amount
	 * @return File (townworldfile)
	 */
	public File increaseTownBankAmount(File file,double amount) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		townBankAmount += amount;
		config.set("Towns." + townName + ".bank", townBankAmount);
		setTownBankAmount(townBankAmount);
		return save(file, config);
	}
	
	/**
	 * <p>
	 * Decrease the town bank amount.
	 * <p>
	 * @param file
	 * @param amount
	 * @return File (townworldfile)
	 * @throws TownHasNotEnoughMoneyException
	 */
	public File decreaseTownBankAmount(File file,double amount) throws TownHasNotEnoughMoneyException {
		if(amount > townBankAmount) {
			throw new TownHasNotEnoughMoneyException(townName);
		}
		else {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			townBankAmount -= amount;
			config.set("Towns." + townName + ".bank", townBankAmount);
			setTownBankAmount(townBankAmount);
			return save(file, config);
		}
	}
	
	/**
	 * <p>
	 * Set town bank amount with saving.
	 * <p>
	 * @param file
	 * @param amount
	 * @return
	 */
	public File setTownBankAmount(File file,double amount) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Towns." + townName + ".bank", amount);
		setTownBankAmount(townBankAmount);
		return save(file, config);
	}
	
	private void setTownBankAmount(double townBankAmount) {
		this.townBankAmount = townBankAmount;
	}
	
	/**
	 * <p>
	 * Get a plot with the chunk coords.
	 * <p>
	 * @param coords
	 * @return Plot
	 * @throws TownSystemException 
	 */
	public Plot getPlotByChunkCoords(String coords) throws TownSystemException {
		Plot p = null;
		for(Plot plot:plots) {
			if(plot.getChunkCoords().equals(coords)) {
				p = plot;
			}
		}
		if(p != null) {
			return p;
		}
		else {
			throw new TownSystemException(TownSystemException.CHUNK_NOT_CLAIMED_BY_TOWN);
		}
	}
	
	/**
	 * <p>
	 * Static method for loading a existing town by name.
	 * <p>
	 * @param file
	 * @param townName
	 * @return Town
	 * @throws TownSystemException 
	 */
	public static Town loadTown(File file,String townName) throws TownSystemException {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(config.getStringList("TownNames").contains(townName)) {
			Location location = new Location(Bukkit.getWorld(config.getString("Towns." + townName + ".TownManagerVillager.world")),
					config.getDouble("Towns." + townName + ".TownManagerVillager.x"),
					config.getDouble("Towns." + townName + ".TownManagerVillager.y"),
					config.getDouble("Towns." + townName + ".TownManagerVillager.z"));
			Town town = new Town(config.getString("Towns." + townName + ".owner"), townName,location);
			town.setCoOwners(config.getStringList("Towns." + townName + ".coOwners"));
			town.setCitizens(config.getStringList("Towns." + townName + ".citizens"));
			town.setChunkList(config.getStringList("Towns." + townName + ".chunks"));
			town.setTax(config.getDouble("Towns." + townName + ".tax"));
			town.setTownBankAmount(config.getDouble("Towns." + townName + ".bank"));
			String locationString = config.getString("Towns." + townName + ".townspawn");
			town.setTownSpawn(new Location(Bukkit.getWorld(config.getString("World")), Double.valueOf(locationString.substring(0, locationString.indexOf("/"))), Double.valueOf(locationString.substring(locationString.indexOf("/")+1,locationString.lastIndexOf("/"))), Double.valueOf(locationString.substring(locationString.lastIndexOf("/")+1))));
			ArrayList<Plot> plotList = new ArrayList<>();
			for(String coords:town.getChunkList()) {
				Plot plot = Plot.loadPlot(file, townName, coords);
				plotList.add(plot);		
			}
			town.setPlotList(plotList);
			return town;
		}
		else {
			throw new TownSystemException(TownSystemException.TOWN_DOES_NOT_EXISTS);
		}
	}
}
