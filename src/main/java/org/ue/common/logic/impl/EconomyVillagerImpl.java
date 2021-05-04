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
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.utils.ServerProvider;

public abstract class EconomyVillagerImpl<T extends GeneralEconomyException> implements EconomyVillager<T> {

	protected final ServerProvider serverProvider;
	private final EconomyVillagerDao ecoVillagerDao;
	private final EconomyVillagerValidationHandler<T> validationHandler;
	private Villager villager;
	private Location location;
	private Inventory inventory;
	private int size;
	private int reservedSlots;
	private String inventoryTitle;
	private boolean visible;
	private Profession profession;
	private EconomyVillagerType villagerType;
	private String savePrefix;

	public EconomyVillagerImpl(ServerProvider serverProvider, EconomyVillagerDao ecoVillagerDao,
			EconomyVillagerValidationHandler<T> validationHandler, String savePrefix) {
		this.serverProvider = serverProvider;
		this.ecoVillagerDao = ecoVillagerDao;
		this.validationHandler = validationHandler;
		this.savePrefix = savePrefix;
	}

	@Override
	public void changeProfession(Profession profession) {
		this.profession = profession;
		ecoVillagerDao.saveProfession(savePrefix, profession);
		if(visible) {
			villager.setProfession(profession);
		}
	}

	@Override
	public void changeLocation(Location location) throws T {
		this.location = location;
		ecoVillagerDao.saveLocation(savePrefix, location);
		if(visible) {
			villager.teleport(location);
		}
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public void despawn() {
		if(visible) {
			villager.remove();
			villager = null;
		}
	}

	@Override
	public void changeSize(int newSize) throws T {
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
			content[newSize - 1 - i] = inventory.getItem(size - 1 - i);
		}
		size = newSize;
		ecoVillagerDao.saveSize(savePrefix, size);
		inventory = serverProvider.createInventory(villager, size, inventoryTitle);
		inventory.setContents(content);
	}

	@Override
	public void openInventory(Player player) throws T {
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

	@Override
	public void setVisible(boolean visible) throws T {
		if(visible) {
			setupVillager();
			changeInventoryName(inventoryTitle);
		} else {
			despawn();
		}
		this.visible = visible;
		ecoVillagerDao.saveVisible(savePrefix, visible);
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
			int reservedSlots, boolean visible) {
		this.reservedSlots = reservedSlots;
		this.location = location;
		this.size = size;
		this.visible = visible;
		this.villagerType = villagerType;
		inventoryTitle = name;
		profession = Profession.NITWIT;
		ecoVillagerDao.saveVisible(savePrefix, visible);
		ecoVillagerDao.saveSize(savePrefix, size);
		ecoVillagerDao.saveProfession(savePrefix, profession);
		if (visible) {
			ecoVillagerDao.saveLocation(savePrefix, location);
			setupVillager();
		}
		// villager is null, when visible is false
		inventory = serverProvider.createInventory(villager, size, name);
	}

	protected void setupExistingEconomyVillager(EconomyVillagerType villagerType, String name, int reservedSlots) {
		this.reservedSlots = reservedSlots;
		this.villagerType = villagerType;
		location = ecoVillagerDao.loadLocation(savePrefix);
		size = ecoVillagerDao.loadSize(savePrefix);
		visible = ecoVillagerDao.loadVisible(savePrefix);
		inventoryTitle = name;
		profession = ecoVillagerDao.loadProfession(savePrefix);
		if (visible) {
			setupVillager();
		}
		// villager is null, when visible is false
		inventory = serverProvider.createInventory(villager, size, name);
	}

	private void setupVillager() {
		location.getChunk().load();
		Collection<Entity> entitys = location.getWorld().getNearbyEntities(location, 10, 10, 10);
		for (Entity e : entitys) {
			if (e.getName().startsWith(inventoryTitle)) {
				e.remove();
			}
		}
		villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
		villager.setCustomName(inventoryTitle);
		villager.setMetadata("ue-type", new FixedMetadataValue(serverProvider.getJavaPluginInstance(), villagerType));
		villager.setCustomNameVisible(true);
		villager.setProfession(profession);
		villager.setSilent(true);
		villager.setCollidable(false);
		villager.setInvulnerable(true);
		villager.setVillagerLevel(2);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
	}
}
