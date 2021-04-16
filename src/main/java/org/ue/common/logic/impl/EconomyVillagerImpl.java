package org.ue.common.logic.impl;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ue.common.dataaccess.api.EconomyVillagerDao;
import org.ue.common.logic.api.EconomyVillager;
import org.ue.common.logic.api.EconomyVillagerType;
import org.ue.common.logic.api.EconomyVillagerValidationHandler;
import org.ue.common.utils.ServerProvider;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.GeneralEconomyException;
import org.ue.shopsystem.logic.ShopSystemException;
import org.ue.townsystem.logic.TownSystemException;

public abstract class EconomyVillagerImpl implements EconomyVillager {

	protected final ServerProvider serverProvider;
	private final EconomyVillagerDao ecoVillagerDao;
	private final EconomyVillagerValidationHandler validationHandler;
	private Villager villager;
	private Location location;
	private Inventory inventory;
	private int size;
	int reservedSlots;
	private String inventoryTitle;

	public EconomyVillagerImpl(ServerProvider serverProvider, EconomyVillagerDao ecoVillagerDao,
			EconomyVillagerValidationHandler validationHandler) {
		this.serverProvider = serverProvider;
		this.ecoVillagerDao = ecoVillagerDao;
		this.validationHandler = validationHandler;
	}

	@Override
	public void changeProfession(Profession profession) {
		villager.setProfession(profession);
		ecoVillagerDao.saveProfession(profession);
	}

	@Override
	public void changeLocation(Location location) throws EconomyPlayerException, TownSystemException {
		villager.teleport(location);
		this.location = location;
		ecoVillagerDao.saveLocation(location);
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public void despawn() {
		villager.remove();
	}

	@Override
	public void changeSize(int newSize) throws ShopSystemException, GeneralEconomyException {
		validationHandler.checkForValidSize(newSize);
		validationHandler.checkForResizePossible(inventory, size, newSize, reservedSlots);
		ItemStack[] content = new ItemStack[newSize];
		int maxOldSizeUnreservedSlotIndex = size - reservedSlots - 1;
		// copy content
		for (int i = 0; i < size; i++) {
			if (i <= maxOldSizeUnreservedSlotIndex) {
				content[i] = inventory.getItem(i);
			} else {
				break;
			}
		}
		// copy reserved slots
		for (int i = 0; i < reservedSlots; i++) {
			content[newSize - 1 - i] = inventory.getItem(newSize - 1 - i);
		}
		size = newSize;
		ecoVillagerDao.saveSize(size);
		inventory = serverProvider.createInventory(villager, size, inventoryTitle);
		inventory.setContents(content);
	}

	@Override
	public void openInventory(Player player) throws ShopSystemException {
		player.openInventory(inventory);
	}

	@Override
	public int getSize() {
		return size;
	}
	
	@Override
	public Inventory createVillagerInventory(int size, String title) {
		return serverProvider.createInventory(villager, size, title);
	}

	protected void addItemStack(ItemStack item, int slot, boolean reserved, boolean override)
			throws GeneralEconomyException, EconomyPlayerException {
		int checkSize = size;
		if (!reserved) {
			checkSize -= reservedSlots;
		}
		if (!override) {
			validationHandler.checkForSlotIsEmpty(inventory, slot);
		}
		validationHandler.checkForValidSlot(slot, checkSize);
		inventory.setItem(slot, item);
	}

	protected Inventory getInventory() {
		return inventory;
	}

	protected int getReservedSlots() {
		return reservedSlots;
	}

	protected Villager getVillager() {
		return villager;
	}

	protected void changeInventoryName(String newName) {
		Inventory inventoryNew = serverProvider.createInventory(villager, getSize(), newName);
		inventoryNew.setContents(inventory.getContents());
		inventory = inventoryNew;
	}

	protected void setupNewEconomyVillager(Location location, EconomyVillagerType villagerType, String name, int size,
			int reservedSlots) {
		this.reservedSlots = reservedSlots;
		this.location = location;
		this.size = size;
		inventoryTitle = name;
		ecoVillagerDao.saveLocation(location);
		ecoVillagerDao.saveSize(size);
		ecoVillagerDao.saveProfession(Profession.NITWIT);
		setupVillager(Profession.NITWIT, name, villagerType);
		inventory = serverProvider.createInventory(villager, size, name);
	}

	protected void setupExistingEconomyVillager(EconomyVillagerType villagerType, String name, int reservedSlots)
			throws TownSystemException {
		this.reservedSlots = reservedSlots;
		this.location = ecoVillagerDao.loadLocation();
		this.size = ecoVillagerDao.loadSize();
		inventoryTitle = name;
		Profession profession = ecoVillagerDao.loadProfession();
		setupVillager(profession, name, villagerType);
		inventory = serverProvider.createInventory(villager, size, name);
	}

	private void setupVillager(Profession profession, String name, EconomyVillagerType villagerType) {
		location.getChunk().load();
		Collection<Entity> entitys = location.getWorld().getNearbyEntities(location, 10, 10, 10);
		for (Entity e : entitys) {
			if (name.equals(e.getCustomName())) {
				e.remove();
			}
		}
		villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
		villager.setCustomName(name);
		villager.setMetadata("ue-type", new FixedMetadataValue(serverProvider.getJavaPluginInstance(), villagerType));
		villager.setCustomNameVisible(true);
		villager.setProfession(profession);
		villager.setSilent(true);
		villager.setCollidable(false);
		villager.setInvulnerable(true);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
	}
}
