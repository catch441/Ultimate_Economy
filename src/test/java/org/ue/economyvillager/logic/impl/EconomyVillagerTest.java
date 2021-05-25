package org.ue.economyvillager.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.doThrow;

import java.util.Arrays;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Villager.Type;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.logic.api.InventoryGuiHandler;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.UltimateEconomyProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyvillager.dataaccess.api.EconomyVillagerDao;
import org.ue.economyvillager.logic.api.EconomyVillagerType;
import org.ue.economyvillager.logic.api.EconomyVillagerValidator;

@ExtendWith(MockitoExtension.class)
public class EconomyVillagerTest {

	@Mock
	ServerProvider serverProvider;
	@Mock
	EconomyVillagerDao ecoVillagerDao;
	@Mock
	EconomyVillagerValidator<AbstractException> validationHandler;
	@Mock
	CustomSkullService customSkullService;
	@Mock
	UltimateEconomyProvider provider;

	private static class AbstractVillager extends EconomyVillagerImpl<AbstractException> {
		public AbstractVillager(ServerProvider serverProvider, EconomyVillagerDao ecoVillagerDao,
				EconomyVillagerValidator<AbstractException> validationHandler, CustomSkullService customSkullService) {
			super(serverProvider, ecoVillagerDao, validationHandler, customSkullService);
		}

		@Override
		public void handleInventoryClick(ClickType clickType, int rawSlot, EconomyPlayer whoClicked) {
		}
	}

	private static class AbstractException extends GeneralEconomyException {
		private static final long serialVersionUID = 1L;

		public AbstractException(MessageWrapper messageWrapper, ExceptionMessageEnum key, Object[] params) {
			super(messageWrapper, key, params);
		}
	}

	private AbstractVillager createVillager() {
		return new AbstractVillager(serverProvider, ecoVillagerDao, validationHandler, customSkullService);
	}

	private Villager setupMocks(AbstractVillager ecoVillager, int size) {
		Location loc = mock(Location.class);
		Inventory inv = mock(Inventory.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		Villager villager = mock(Villager.class);
		Villager duplicate = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		InventoryGuiHandler customizer = mock(InventoryGuiHandler.class);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(loc.getChunk()).thenReturn(chunk);
		when(duplicate.getName()).thenReturn("myName");
		when(villager.getVillagerType()).thenReturn(Type.DESERT);
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(Arrays.asList(duplicate));
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createInventory(villager, size, "myName")).thenReturn(inv);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createEconomyVillagerCustomizeHandler(ecoVillager, Type.DESERT, Profession.NITWIT))
				.thenReturn(customizer);
		ecoVillager.setupNewEconomyVillager(loc, EconomyVillagerType.ADMINSHOP, "myName", "id", size, 2, true,
				"savePrefix");
		return villager;
	}

	@Test
	public void setupNewEconomyVillagerTest() {
		AbstractVillager ecoVillager = createVillager();
		InventoryGuiHandler customizer = mock(InventoryGuiHandler.class);
		Location loc = mock(Location.class);
		Inventory inv = mock(Inventory.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		Villager villager = mock(Villager.class);
		Villager duplicate = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(loc.getChunk()).thenReturn(chunk);
		when(duplicate.getName()).thenReturn("myName");
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(Arrays.asList(duplicate));
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createInventory(villager, 9, "myName")).thenReturn(inv);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(villager.getVillagerType()).thenReturn(Type.DESERT);
		when(provider.createEconomyVillagerCustomizeHandler(ecoVillager, Type.DESERT, Profession.NITWIT))
				.thenReturn(customizer);
		ecoVillager.setupNewEconomyVillager(loc, EconomyVillagerType.ADMINSHOP, "myName", "id", 9, 2, true,
				"savePrefix");
		verify(ecoVillagerDao).saveProfession("savePrefix", Profession.NITWIT);
		verify(ecoVillagerDao).saveSize("savePrefix", 9);
		verify(ecoVillagerDao).saveVisible("savePrefix", true);
		verify(ecoVillagerDao).saveLocation("savePrefix", loc);
		verify(chunk).load();
		verify(villager).setCustomName("myName");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.NITWIT);
		verify(villager).setSilent(true);
		verify(villager).setInvulnerable(true);
		verify(villager).setCollidable(false);
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		verify(villager).setMetadata(eq("ue-id"), any(FixedMetadataValue.class));
		verify(villager).addPotionEffect(any(PotionEffect.class));
		verify(duplicate).remove();
		assertEquals(inv, ecoVillager.getInventory());
		assertEquals(9, ecoVillager.getSize());
		assertEquals(2, ecoVillager.getReservedSlots());
		assertEquals(villager, ecoVillager.getVillager());
		assertEquals(loc, ecoVillager.getLocation());
		assertEquals(customizer, ecoVillager.getCustomizeGuiHandler());
		assertEquals("id", ecoVillager.getId());
	}

	@Test
	public void setupExistingEconomyVillagerTest() {
		AbstractVillager ecoVillager = createVillager();
		InventoryGuiHandler customizer = mock(InventoryGuiHandler.class);
		Location loc = mock(Location.class);
		Inventory inv = mock(Inventory.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		Villager villager = mock(Villager.class);
		Villager duplicate = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		when(provider.createEconomyVillagerCustomizeHandler(ecoVillager, Type.DESERT, Profession.ARMORER))
				.thenReturn(customizer);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(loc.getChunk()).thenReturn(chunk);
		when(duplicate.getName()).thenReturn("myName");
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(Arrays.asList(duplicate));
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createInventory(villager, 9, "myName")).thenReturn(inv);
		when(ecoVillagerDao.loadLocation("savePrefix")).thenReturn(loc);
		when(ecoVillagerDao.loadVisible("savePrefix")).thenReturn(true);
		when(ecoVillagerDao.loadProfession("savePrefix")).thenReturn(Profession.ARMORER);
		when(ecoVillagerDao.loadSize("savePrefix")).thenReturn(9);
		when(ecoVillagerDao.loadBiomeType("savePrefix")).thenReturn(Type.DESERT);
		ecoVillager.setupExistingEconomyVillager(EconomyVillagerType.ADMINSHOP, "myName", "id", 2, "savePrefix");
		verify(chunk).load();
		verify(villager).setCustomName("myName");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.ARMORER);
		verify(villager).setSilent(true);
		verify(villager).setInvulnerable(true);
		verify(villager).setCollidable(false);
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		verify(villager).addPotionEffect(any(PotionEffect.class));
		verify(duplicate).remove();
		assertEquals(inv, ecoVillager.getInventory());
		assertEquals(9, ecoVillager.getSize());
		assertEquals(2, ecoVillager.getReservedSlots());
		assertEquals(villager, ecoVillager.getVillager());
		assertEquals(loc, ecoVillager.getLocation());
		assertEquals(customizer, ecoVillager.getCustomizeGuiHandler());
		assertEquals("id", ecoVillager.getId());
	}

	@Test
	public void despawnTest() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager,9);
		ecoVillager.despawn();
		verify(villager).remove();
	}

	@Test
	public void createVillagerInventoryTest() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager,9);
		Inventory inv = mock(Inventory.class);
		when(serverProvider.createInventory(villager, 18, "title")).thenReturn(inv);
		Inventory result = ecoVillager.createVillagerInventory(18, "title");
		assertEquals(inv, result);
	}

	@Test
	public void changeProfessionTest() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager,9);
		ecoVillager.changeProfession(Profession.FARMER);
		verify(ecoVillagerDao).saveProfession("savePrefix", Profession.FARMER);
		verify(villager).setProfession(Profession.FARMER);
	}
	
	@Test
	public void changeBiomeTypeTest() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager,9);
		ecoVillager.changeBiomeType(Type.JUNGLE);
		verify(ecoVillagerDao).saveBiomeType("savePrefix", Type.JUNGLE);
		verify(villager).setVillagerType(Type.JUNGLE);
	}

	@Test
	public void changeLocationTest() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager,9);
		Location loc = mock(Location.class);
		assertDoesNotThrow(() -> ecoVillager.changeLocation(loc));
		verify(ecoVillagerDao).saveLocation("savePrefix", loc);
		verify(villager).teleport(loc);
		assertEquals(loc, ecoVillager.getLocation());
	}

	@Test
	public void changeInventoryNameTest() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager,9);
		Inventory inv = mock(Inventory.class);
		ItemStack[] contents = new ItemStack[9];
		when(ecoVillager.getInventory().getContents()).thenReturn(contents);
		when(serverProvider.createInventory(villager, 9, "newName")).thenReturn(inv);
		ecoVillager.changeInventoryName("newName");
		verify(inv).setContents(contents);
		assertEquals(inv, ecoVillager.getInventory());
	}

	@Test
	public void setVisibleTestFalse() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager,9);
		assertDoesNotThrow(() -> ecoVillager.setVisible(false));
		verify(villager).remove();
		verify(ecoVillagerDao).saveVisible("savePrefix", false);
	}

	@Test
	public void openInventoryTest() {
		AbstractVillager ecoVillager = createVillager();
		setupMocks(ecoVillager,9);
		Player player = mock(Player.class);
		assertDoesNotThrow(() -> ecoVillager.openInventory(player));
		verify(player).openInventory(ecoVillager.getInventory());
	}

	@Test
	public void setVisibleTestTrue() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager,9);
		reset(villager);
		reset(ecoVillagerDao);
		assertDoesNotThrow(() -> ecoVillager.setVisible(true));
		verify(villager).setCustomName("myName");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.NITWIT);
		verify(villager).setSilent(true);
		verify(villager).setInvulnerable(true);
		verify(villager).setCollidable(false);
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		verify(villager).addPotionEffect(any(PotionEffect.class));
		verify(ecoVillagerDao).saveVisible("savePrefix", true);
		verify(serverProvider, times(2)).createInventory(villager, 9, "myName");
	}

	@Test
	public void changeSizeTestWithInvalidSize() throws AbstractException {
		AbstractVillager ecoVillager = createVillager();
		setupMocks(ecoVillager,9);
		reset(ecoVillagerDao);
		doThrow(AbstractException.class).when(validationHandler).checkForValidSize(5);
		assertThrows(AbstractException.class, () -> ecoVillager.changeSize(5));
		verifyNoMoreInteractions(ecoVillagerDao);
	}

	@Test
	public void changeSizeTestWithNotPossible() throws AbstractException {
		AbstractVillager ecoVillager = createVillager();
		setupMocks(ecoVillager,9);
		reset(ecoVillagerDao);
		doThrow(AbstractException.class).when(validationHandler).checkForResizePossible(ecoVillager.getInventory(), 9,
				9, 2);
		assertThrows(AbstractException.class, () -> ecoVillager.changeSize(9));
		verifyNoMoreInteractions(ecoVillagerDao);
	}

	@Test
	public void changeSizeTestGreater() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager,9);

		Inventory oldInv = ecoVillager.getInventory();
		Inventory inv = mock(Inventory.class);
		ItemStack filled = mock(ItemStack.class);
		ItemStack infoItem = mock(ItemStack.class);
		when(oldInv.getItem(1)).thenReturn(null);
		when(oldInv.getItem(2)).thenReturn(null);
		when(oldInv.getItem(3)).thenReturn(null);
		when(oldInv.getItem(4)).thenReturn(null);
		when(oldInv.getItem(5)).thenReturn(null);
		when(oldInv.getItem(6)).thenReturn(null);
		when(oldInv.getItem(7)).thenReturn(null);
		when(oldInv.getItem(0)).thenReturn(filled);
		when(oldInv.getItem(8)).thenReturn(infoItem);
		when(serverProvider.createInventory(villager, 18, "myName")).thenReturn(inv);

		assertDoesNotThrow(() -> ecoVillager.changeSize(18));
		verify(ecoVillagerDao).saveSize("savePrefix", 18);
		assertEquals(inv, ecoVillager.getInventory());
		ItemStack[] resultContents = new ItemStack[18];
		resultContents[17] = infoItem;
		resultContents[0] = filled;
		verify(inv).setContents(resultContents);
	}
	
	@Test
	public void changeSizeTestSmaller() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager,18);

		Inventory oldInv = ecoVillager.getInventory();
		Inventory inv = mock(Inventory.class);
		ItemStack filled = mock(ItemStack.class);
		ItemStack infoItem = mock(ItemStack.class);
		when(oldInv.getItem(1)).thenReturn(null);
		when(oldInv.getItem(2)).thenReturn(null);
		when(oldInv.getItem(3)).thenReturn(null);
		when(oldInv.getItem(4)).thenReturn(null);
		when(oldInv.getItem(5)).thenReturn(null);
		when(oldInv.getItem(6)).thenReturn(null);

		when(oldInv.getItem(16)).thenReturn(null);
		when(oldInv.getItem(0)).thenReturn(filled);
		when(oldInv.getItem(17)).thenReturn(infoItem);
		when(serverProvider.createInventory(villager, 9, "myName")).thenReturn(inv);

		assertDoesNotThrow(() -> ecoVillager.changeSize(9));
		verify(ecoVillagerDao).saveSize("savePrefix", 9);
		assertEquals(inv, ecoVillager.getInventory());
		ItemStack[] resultContents = new ItemStack[9];
		resultContents[8] = infoItem;
		resultContents[0] = filled;
		verify(inv).setContents(resultContents);
	}
}
