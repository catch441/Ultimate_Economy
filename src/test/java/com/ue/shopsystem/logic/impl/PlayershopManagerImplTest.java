package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.common.utils.ComponentProvider;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.ServiceComponent;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.Playershop;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.impl.TownSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

@ExtendWith(MockitoExtension.class)
public class PlayershopManagerImplTest {

	@InjectMocks
	PlayershopManagerImpl playershopManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ShopValidationHandler validationHandler;
	@Mock
	TownsystemValidationHandler townsystemValidationHandler;
	@Mock
	ComponentProvider componentProvider;
	@Mock
	ConfigDao configDao;
	@Mock
	Logger logger;
	@Mock
	ServerProvider serverProvider;
	@Mock
	CustomSkullService customSkullService;

	@Test
	public void createNewPlayershopTestWithInvalidSize() throws GeneralEconomyException {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidSize(5);
		assertThrows(GeneralEconomyException.class, () -> playershopManager.createPlayerShop("myshop", loc, 5, ecoPlayer));
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao, never()).savePlayershopIds(anyList());
	}

	@Test
	public void createNewAdminshopTestWithExistingName() throws GeneralEconomyException {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForShopNameIsFree(anyList(),eq("myshop"), eq(ecoPlayer));
		assertThrows(GeneralEconomyException.class, () -> playershopManager.createPlayerShop("myshop", loc, 5, ecoPlayer));
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao, never()).savePlayershopIds(anyList());
	}

	@Test
	public void createNewAdminshopTestWithInvalidName() throws ShopSystemException {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(ShopSystemException.class).when(validationHandler).checkForValidShopName("myshop_");
		assertThrows(ShopSystemException.class, () -> playershopManager.createPlayerShop("myshop_", loc, 9, ecoPlayer));
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao, never()).savePlayershopIds(anyList());
	}

	@Test
	public void createNewAdminshopTestWithMaxShopsReached() throws EconomyPlayerException {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForMaxPlayershopsForPlayer(anyList(), eq(ecoPlayer));
		assertThrows(EconomyPlayerException.class, () -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao, never()).savePlayershopIds(anyList());
	}

	@Test
	public void createNewPlayershopTestWithNoPlotPermission() throws EconomyPlayerException, TownSystemException {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(townsystemValidationHandler).checkForTownworldPlotPermission(loc, ecoPlayer);
		assertThrows(EconomyPlayerException.class, () -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao, never()).savePlayershopIds(anyList());
	}

	@Test
	public void createNewPlayershopTest() {
		Plugin plugin = mock(Plugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), anyString())).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(componentProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(customSkullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		assertDoesNotThrow(() -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
		
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidShopName("myshop"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForMaxPlayershopsForPlayer(anyList(), eq(ecoPlayer)));
		assertDoesNotThrow(() -> verify(validationHandler).checkForShopNameIsFree(anyList(), eq("myshop"), eq(ecoPlayer)));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidSize(9));
		assertDoesNotThrow(() -> verify(townsystemValidationHandler).checkForTownworldPlotPermission(loc, ecoPlayer));
		assertEquals(1, playershopManager.getPlayerShops().size());
		Playershop shop = playershopManager.getPlayerShops().get(0);
		assertEquals(9, shop.getSize());
		assertEquals(ecoPlayer, shop.getOwner());
		assertEquals("myshop", shop.getName());
		verify(configDao).savePlayershopIds(anyList());
		// verify that the new constructor is used
		verify(shopDao, never()).loadShopName();
	}

	@Test
	public void generateFreePlayerShopIdTest() {
		String id1 = playershopManager.generateFreePlayerShopId();
		createPlayershop();
		String id2 = playershopManager.generateFreePlayerShopId();
		assertEquals("P0", id1);
		assertEquals("P1", id2);
	}

	private void createPlayershop() {
		Plugin plugin = mock(Plugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), anyString())).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(componentProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(customSkullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		assertDoesNotThrow(() -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
	}

	@Test
	public void getPlayerShopUniqueNameListTest() {
		createPlayershop();
		EconomyPlayer ecoPlayer = playershopManager.getPlayerShops().get(0).getOwner();
		when(ecoPlayer.getName()).thenReturn("catch441");
		List<String> list = playershopManager.getPlayerShopUniqueNameList();
		assertEquals(1, list.size());
		assertEquals("myshop_catch441", list.get(0));
	}

	@Test
	public void getPlayerShopByUniqueNameTest() {
		createPlayershop();
		EconomyPlayer ecoPlayer = playershopManager.getPlayerShops().get(0).getOwner();
		when(ecoPlayer.getName()).thenReturn("catch441");
		Playershop shop = assertDoesNotThrow(() -> playershopManager.getPlayerShopByUniqueName("myshop_catch441"));
		assertNotNull(shop);
		assertEquals("P0", shop.getShopId());
		assertEquals("myshop", shop.getName());
		assertEquals("catch441", shop.getOwner().getName());
	}

	@Test
	public void getPlayerShopByUniqueNameTestWithNoShop() {
		try {
			playershopManager.getPlayerShopByUniqueName("myshop_catch441");
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals("myshop_catch441", e.getParams()[0]);
		}
	}

	@Test
	public void getPlayerShopByIdTest() {
		createPlayershop();
		createPlayershop();
		Playershop shop = assertDoesNotThrow(() -> playershopManager.getPlayerShopById("P1"));
		assertNotNull(shop);
		assertEquals("P1", shop.getShopId());
	}

	@Test
	public void getPlayerShopByIdTestWithNoShop() {
		try {
			playershopManager.getPlayerShopById("P0");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals("P0", e.getParams()[0]);
		}
	}

	@Test
	public void getPlayershopIdListTest() {
		createPlayershop();
		List<String> list = playershopManager.getPlayershopIdList();
		assertEquals(1, list.size());
		assertEquals("P0", list.get(0));
	}

	@Test
	public void despawnAllVillagersTest() {
		createPlayershop();
		createPlayershop();
		Playershop shop1 = assertDoesNotThrow(() -> playershopManager.getPlayerShops().get(0));
		Playershop shop2 = assertDoesNotThrow(() -> playershopManager.getPlayerShops().get(0));
		playershopManager.despawnAllVillagers();
		verify(shop1.getShopVillager()).remove();
		verify(shop2.getShopVillager()).remove();
	}

	@Test
	public void deletePlayershopTest() {
		Plugin plugin = mock(Plugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), anyString())).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(componentProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(customSkullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		assertDoesNotThrow(() -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
		reset(configDao);
		Playershop shop = playershopManager.getPlayerShops().get(0);
		playershopManager.deletePlayerShop(shop);
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao).savePlayershopIds(anyList());
		// verify that the delete shop method is executed on the playershop
		verify(shopDao).deleteFile();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllPlayerShopsTest() {
		Plugin plugin = mock(Plugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		assertDoesNotThrow(() -> when(shopDao.loadShopLocation()).thenReturn(loc));
		when(shopDao.loadShopSize()).thenReturn(9);
		when(shopDao.loadShopName()).thenReturn("myshop");
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), anyString())).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(componentProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(customSkullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		when(configDao.hasPlayerShopNames()).thenReturn(false);
		when(configDao.loadPlayershopIds()).thenReturn(Arrays.asList("P0"));
		playershopManager.loadAllPlayerShops();
		assertEquals(1, playershopManager.getPlayerShops().size());
		assertEquals("P0", playershopManager.getPlayerShops().get(0).getShopId());
		// to verify that the loading constructor of the shop is used
		verify(shopDao).loadShopName();
		verifyNoInteractions(logger);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllPlayerShopsTestWithLoadingError() throws TownSystemException {
		Location loc = mock(Location.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		TownSystemException e = mock(TownSystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		assertDoesNotThrow(() -> when(shopDao.loadShopLocation()).thenReturn(loc));
		when(shopDao.loadShopSize()).thenReturn(9);
		when(shopDao.loadShopName()).thenReturn("myshop");
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(componentProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(configDao.hasPlayerShopNames()).thenReturn(false);
		when(configDao.loadPlayershopIds()).thenReturn(Arrays.asList("P0"));
		when(shopDao.loadShopLocation()).thenThrow(e);
		playershopManager.loadAllPlayerShops();
		assertEquals(0, playershopManager.getPlayerShops().size());
		// to verify that the loading constructor of the shop is used
		verify(shopDao).loadShopName();
		verify(logger).warn("[Ultimate_Economy] Failed to load the shop P0");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");
		verifyNoMoreInteractions(logger);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllPlayershopsTestWithOldLoading() {
		Plugin plugin = mock(Plugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		assertDoesNotThrow(() -> when(shopDao.loadShopLocation()).thenReturn(loc));
		when(shopDao.loadShopSize()).thenReturn(9);
		when(shopDao.loadShopName()).thenReturn("myshop");
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), anyString())).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(componentProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(customSkullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		when(configDao.hasPlayerShopNames()).thenReturn(true);
		when(configDao.loadPlayerShopNames()).thenReturn(Arrays.asList("myshop"));
		playershopManager.loadAllPlayerShops();
		assertEquals(1, playershopManager.getPlayerShops().size());
		assertEquals("P0", playershopManager.getPlayerShops().get(0).getShopId());
		assertEquals("myshop", playershopManager.getPlayerShops().get(0).getName());
		// to verify that the loading constructor of the shop is used
		verify(shopDao).loadShopName();
		verify(configDao).removeDeprecatedPlayerShopNames();
		verify(configDao).savePlayershopIds(anyList());
		verifyNoInteractions(logger);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllPlayershopsTestWithOldLoadingAndLoadError() throws TownSystemException {
		Plugin plugin = mock(Plugin.class);
		Location loc = mock(Location.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		TownSystemException e = mock(TownSystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		assertDoesNotThrow(() -> when(shopDao.loadShopLocation()).thenReturn(loc));
		when(shopDao.loadShopSize()).thenReturn(9);
		when(shopDao.loadShopName()).thenReturn("myshop");
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(componentProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(configDao.hasPlayerShopNames()).thenReturn(true);
		when(configDao.loadPlayerShopNames()).thenReturn(Arrays.asList("myshop"));
		when(shopDao.loadShopLocation()).thenThrow(e);
		playershopManager.loadAllPlayerShops();
		assertEquals(0, playershopManager.getPlayerShops().size());
		// to verify that the loading constructor of the shop is used
		verify(shopDao).loadShopName();
		verify(logger).warn("[Ultimate_Economy] Failed to load the shop myshop");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");
		verifyNoMoreInteractions(logger);
		verify(configDao).removeDeprecatedPlayerShopNames();
		verify(configDao).savePlayershopIds(anyList());
	}
}
