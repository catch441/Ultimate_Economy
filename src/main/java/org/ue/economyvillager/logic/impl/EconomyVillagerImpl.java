package org.ue.economyvillager.logic.impl;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Villager.Type;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.logic.api.InventoryGuiHandler;
import org.ue.common.logic.impl.InventoryGuiHandlerImpl;
import org.ue.common.utils.ServerProvider;
import org.ue.economyvillager.dataaccess.api.EconomyVillagerDao;
import org.ue.economyvillager.logic.api.EconomyVillager;
import org.ue.economyvillager.logic.api.EconomyVillagerType;
import org.ue.economyvillager.logic.api.EconomyVillagerValidator;

public abstract class EconomyVillagerImpl<T extends GeneralEconomyException> extends InventoryGuiHandlerImpl
		implements EconomyVillager<T> {

	private final EconomyVillagerDao ecoVillagerDao;
	private final EconomyVillagerValidator<T> validator;
	private InventoryGuiHandler customizeGuiHandler;
	private Villager villager;
	private Location location;
	private int size;
	private int reservedSlots;
	private String inventoryTitle;
	private boolean visible;
	private Profession profession;
	private Type biomeType;
	private EconomyVillagerType villagerType;
	private String savePrefix;
	private String id;

	public EconomyVillagerImpl(ServerProvider serverProvider, EconomyVillagerDao ecoVillagerDao,
			EconomyVillagerValidator<T> validator, CustomSkullService skullService) {
		super(skullService, serverProvider, null);
		this.ecoVillagerDao = ecoVillagerDao;
		this.validator = validator;

	}

	@Override
	public void changeBiomeType(Type biomeType) {
		this.biomeType = biomeType;
		ecoVillagerDao.saveBiomeType(savePrefix, biomeType);
		if (visible) {
			villager.setVillagerType(biomeType);
		}
	}

	@Override
	public void changeProfession(Profession profession) {
		this.profession = profession;
		ecoVillagerDao.saveProfession(savePrefix, profession);
		if (visible) {
			villager.setProfession(profession);
		}
	}

	@Override
	public void changeLocation(Location location) throws T {
		this.location = location;
		ecoVillagerDao.saveLocation(savePrefix, location);
		if (visible) {
			villager.teleport(location);
		}
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public void despawn() {
		if (visible) {
			villager.remove();
			villager = null;
		}
	}

	@Override
	public void changeSize(int newSize) throws T {
		validator.checkForValidSize(newSize);
		validator.checkForResizePossible(inventory, size, newSize, reservedSlots);
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
	public int getSize() {
		return size;
	}

	@Override
	public Inventory createVillagerInventory(int size, String title) {
		return serverProvider.createInventory(villager, size, title);
	}

	@Override
	public void setVisible(boolean visible) throws T {
		if (visible) {
			setupVillager();
			changeInventoryName(inventoryTitle);
		} else {
			despawn();
		}
		this.visible = visible;
		ecoVillagerDao.saveVisible(savePrefix, visible);
	}

	@Override
	public InventoryGuiHandler getCustomizeGuiHandler() {
		return customizeGuiHandler;
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

	protected void setupNewEconomyVillager(Location location, EconomyVillagerType villagerType, String name, String id,
			int size, int reservedSlots, boolean visible, String savePrefix) {
		this.reservedSlots = reservedSlots;
		this.savePrefix = savePrefix;
		this.location = location;
		this.size = size;
		this.visible = visible;
		this.villagerType = villagerType;
		this.id = id;
		inventoryTitle = name;
		profession = Profession.NITWIT;
		ecoVillagerDao.saveVisible(savePrefix, visible);
		ecoVillagerDao.saveSize(savePrefix, size);
		ecoVillagerDao.saveProfession(savePrefix, profession);
		if (visible) {
			ecoVillagerDao.saveLocation(savePrefix, location);
			setupVillager();
			this.biomeType = villager.getVillagerType();
			ecoVillagerDao.saveBiomeType(savePrefix, biomeType);
		}
		// villager is null, when visible is false
		inventory = serverProvider.createInventory(villager, size, name);
		customizeGuiHandler = serverProvider.getProvider().createEconomyVillagerCustomizeHandler(this, biomeType,
				profession);
	}

	protected void setupExistingEconomyVillager(EconomyVillagerType villagerType, String name, String id,
			int reservedSlots, String savePrefix) {
		this.reservedSlots = reservedSlots;
		this.villagerType = villagerType;
		this.savePrefix = savePrefix;
		this.id = id;
		location = ecoVillagerDao.loadLocation(savePrefix);
		biomeType = ecoVillagerDao.loadBiomeType(savePrefix);
		size = ecoVillagerDao.loadSize(savePrefix);
		visible = ecoVillagerDao.loadVisible(savePrefix);
		inventoryTitle = name;
		profession = ecoVillagerDao.loadProfession(savePrefix);
		if (visible) {
			setupVillager();
		}
		// villager is null, when visible is false
		inventory = serverProvider.createInventory(villager, size, name);
		customizeGuiHandler = serverProvider.getProvider().createEconomyVillagerCustomizeHandler(this, biomeType,
				profession);
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
		villager.setMetadata("ue-id", new FixedMetadataValue(serverProvider.getJavaPluginInstance(), id));
		villager.setCustomNameVisible(true);
		villager.setProfession(profession);
		if (biomeType != null) {
			villager.setVillagerType(biomeType);
		}
		villager.setSilent(true);
		villager.setCollidable(false);
		villager.setInvulnerable(true);
		villager.setVillagerLevel(2);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
	}
}
