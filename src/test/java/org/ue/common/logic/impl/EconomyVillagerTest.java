package org.ue.common.logic.impl;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.dataaccess.api.EconomyVillagerDao;
import org.ue.common.logic.api.EconomyVillagerType;
import org.ue.common.logic.api.EconomyVillagerValidationHandler;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;

@ExtendWith(MockitoExtension.class)
public class EconomyVillagerTest {

	@Mock
	ServerProvider serverProvider;
	@Mock
	EconomyVillagerDao ecoVillagerDao;
	@Mock
	EconomyVillagerValidationHandler<AbstractException> validationHandler;

	private static class AbstractVillager extends EconomyVillagerImpl<AbstractException> {
		public AbstractVillager(ServerProvider serverProvider, EconomyVillagerDao ecoVillagerDao,
				EconomyVillagerValidationHandler<AbstractException> validationHandler, String savePrefix) {
			super(serverProvider, ecoVillagerDao, validationHandler, savePrefix);
		}
	}

	private static class AbstractException extends GeneralEconomyException {
		private static final long serialVersionUID = 1L;

		public AbstractException(MessageWrapper messageWrapper, ExceptionMessageEnum key, Object[] params) {
			super(messageWrapper, key, params);
		}
	}

	private AbstractVillager createVillager() {
		return new AbstractVillager(serverProvider, ecoVillagerDao, validationHandler, "");
	}

	private Villager setupMocks(AbstractVillager ecoVillager) {
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
		ecoVillager.setupNewEconomyVillager(loc, EconomyVillagerType.ADMINSHOP, "myName", 9, 2, true);
		return villager;
	}

	@Test
	public void setupNewEconomyVillagerTest() {
		AbstractVillager ecoVillager = createVillager();
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
		ecoVillager.setupNewEconomyVillager(loc, EconomyVillagerType.ADMINSHOP, "myName", 9, 2, true);
		verify(ecoVillagerDao).saveProfession("", Profession.NITWIT);
		verify(ecoVillagerDao).saveSize("", 9);
		verify(ecoVillagerDao).saveVisible("", true);
		verify(ecoVillagerDao).saveLocation("", loc);
		verify(chunk).load();
		verify(villager).setCustomName("myName");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.NITWIT);
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
	}
	
	@Test
	public void setupExistingEconomyVillagerTest() {
		AbstractVillager ecoVillager = createVillager();
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
		when(ecoVillagerDao.loadLocation("")).thenReturn(loc);
		when(ecoVillagerDao.loadVisible("")).thenReturn(true);
		when(ecoVillagerDao.loadProfession("")).thenReturn(Profession.ARMORER);
		when(ecoVillagerDao.loadSize("")).thenReturn(9);
		ecoVillager.setupExistingEconomyVillager(EconomyVillagerType.ADMINSHOP, "myName", 2);
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
	}

	@Test
	public void despawnTest() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager);
		ecoVillager.despawn();
		verify(villager).remove();
	}

	@Test
	public void createVillagerInventoryTest() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager);
		Inventory inv = mock(Inventory.class);
		when(serverProvider.createInventory(villager, 18, "title")).thenReturn(inv);
		Inventory result = ecoVillager.createVillagerInventory(18, "title");
		assertEquals(inv, result);
	}

	@Test
	public void changeProfessionTest() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager);
		ecoVillager.changeProfession(Profession.FARMER);
		verify(ecoVillagerDao).saveProfession("", Profession.FARMER);
		verify(villager).setProfession(Profession.FARMER);
	}

	@Test
	public void changeLocationTest() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager);
		Location loc = mock(Location.class);
		assertDoesNotThrow(() -> ecoVillager.changeLocation(loc));
		verify(ecoVillagerDao).saveLocation("", loc);
		verify(villager).teleport(loc);
		assertEquals(loc, ecoVillager.getLocation());
	}

	@Test
	public void changeInventoryNameTest() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager);
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
		Villager villager = setupMocks(ecoVillager);
		assertDoesNotThrow(() -> ecoVillager.setVisible(false));
		verify(villager).remove();
		verify(ecoVillagerDao).saveVisible("", false);
	}

	@Test
	public void openInventoryTest() {
		AbstractVillager ecoVillager = createVillager();
		setupMocks(ecoVillager);
		Player player = mock(Player.class);
		assertDoesNotThrow(() -> ecoVillager.openInventory(player));
		verify(player).openInventory(ecoVillager.getInventory());
	}

	@Test
	public void setVisibleTestTrue() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager);
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
		verify(ecoVillagerDao).saveVisible("", true);
		verify(serverProvider, times(2)).createInventory(villager, 9, "myName");
	}

	@Test
	public void changeSizeTestWithInvalidSize() throws AbstractException {
		AbstractVillager ecoVillager = createVillager();
		setupMocks(ecoVillager);
		reset(ecoVillagerDao);
		doThrow(AbstractException.class).when(validationHandler).checkForValidSize(5);
		assertThrows(AbstractException.class, () -> ecoVillager.changeSize(5));
		verifyNoMoreInteractions(ecoVillagerDao);
	}

	@Test
	public void changeSizeTestWithNotPossible() throws AbstractException {
		AbstractVillager ecoVillager = createVillager();
		setupMocks(ecoVillager);
		reset(ecoVillagerDao);
		doThrow(AbstractException.class).when(validationHandler).checkForResizePossible(ecoVillager.getInventory(), 9,
				9, 2);
		assertThrows(AbstractException.class, () -> ecoVillager.changeSize(9));
		verifyNoMoreInteractions(ecoVillagerDao);
	}

	@Test
	public void changeSizeTest() {
		AbstractVillager ecoVillager = createVillager();
		Villager villager = setupMocks(ecoVillager);

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
		verify(ecoVillagerDao).saveSize("", 18);
		assertEquals(inv, ecoVillager.getInventory());
		ItemStack[] resultContents = new ItemStack[18];
		resultContents[17] = infoItem;
		resultContents[0] = filled;
		verify(inv).setContents(resultContents);
	}
}
