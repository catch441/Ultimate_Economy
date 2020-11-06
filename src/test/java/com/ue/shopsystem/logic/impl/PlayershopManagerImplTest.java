package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.ServiceComponent;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.shopsystem.logic.api.Playershop;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.impl.TownSystemException;

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
	ConfigDao configDao;
	@Mock
	Logger logger;
	@Mock
	ServerProvider serverProvider;
	@Mock
	GeneralEconomyValidationHandler generalValidator;

	@Test
	public void createNewPlayershopTestWithInvalidSize() throws GeneralEconomyException {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSize(5);
		assertThrows(GeneralEconomyException.class,
				() -> playershopManager.createPlayerShop("myshop", loc, 5, ecoPlayer));
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao, never()).savePlayershopIds(anyList());
	}

	@Test
	public void createNewAdminshopTestWithExistingName() throws GeneralEconomyException {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForShopNameIsFree(anyList(), eq("myshop"),
				eq(ecoPlayer));
		assertThrows(GeneralEconomyException.class,
				() -> playershopManager.createPlayerShop("myshop", loc, 5, ecoPlayer));
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
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForMaxPlayershopsForPlayer(anyList(),
				eq(ecoPlayer));
		assertThrows(EconomyPlayerException.class,
				() -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao, never()).savePlayershopIds(anyList());
	}

	@Test
	public void createNewPlayershopTestWithNoPlotPermission() throws EconomyPlayerException, TownSystemException {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(townsystemValidationHandler).checkForTownworldPlotPermission(loc,
				ecoPlayer);
		assertThrows(EconomyPlayerException.class,
				() -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao, never()).savePlayershopIds(anyList());
	}

	@Test
	public void createNewPlayershopTest() {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Playershop shop = mock(Playershop.class);
		when(shop.getShopId()).thenReturn("P0");
		when(serviceComponent.getPlayershop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));

		assertDoesNotThrow(() -> verify(validationHandler).checkForValidShopName("myshop"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForMaxPlayershopsForPlayer(anyList(), eq(ecoPlayer)));
		assertDoesNotThrow(
				() -> verify(validationHandler).checkForShopNameIsFree(anyList(), eq("myshop"), eq(ecoPlayer)));
		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSize(9));
		assertDoesNotThrow(() -> verify(townsystemValidationHandler).checkForTownworldPlotPermission(loc, ecoPlayer));
		assertEquals(1, playershopManager.getPlayerShops().size());
		Playershop result = playershopManager.getPlayerShops().get(0);
		assertEquals(shop, result);
		verify(shop).setupNew("myshop", ecoPlayer, "P0", loc, 9);
		verify(configDao).savePlayershopIds(Arrays.asList("P0"));
	}

	@Test
	public void generateFreePlayerShopIdTest() {
		String id1 = playershopManager.generateFreePlayerShopId();
		Playershop shop = createPlayershop();
		when(shop.getShopId()).thenReturn("P0");
		String id2 = playershopManager.generateFreePlayerShopId();
		assertEquals("P0", id1);
		assertEquals("P1", id2);
	}

	private Playershop createPlayershop() {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Playershop shop = mock(Playershop.class);
		when(serviceComponent.getPlayershop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
		return shop;
	}

	@Test
	public void getPlayerShopUniqueNameListTest() {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Playershop shop = mock(Playershop.class);
		when(serviceComponent.getPlayershop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
		when(shop.getOwner()).thenReturn(ecoPlayer);
		when(shop.getName()).thenReturn("myshop");
		when(ecoPlayer.getName()).thenReturn("catch441");
		List<String> list = playershopManager.getPlayerShopUniqueNameList();
		assertEquals(1, list.size());
		assertEquals("myshop_catch441", list.get(0));
	}

	@Test
	public void getPlayerShopByUniqueNameTest() {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Playershop shop = mock(Playershop.class);
		when(serviceComponent.getPlayershop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
		when(ecoPlayer.getName()).thenReturn("catch441");
		when(shop.getName()).thenReturn("myshop");
		when(shop.getOwner()).thenReturn(ecoPlayer);
		Playershop result = assertDoesNotThrow(() -> playershopManager.getPlayerShopByUniqueName("myshop_catch441"));
		assertNotNull(result);
		assertEquals(shop, result);
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
		Playershop shop1 = createPlayershop();
		when(shop1.getOwner()).thenReturn(mock(EconomyPlayer.class));
		Playershop shop2 = createPlayershop();
		when(shop1.getShopId()).thenReturn("P0");
		when(shop2.getShopId()).thenReturn("P1");
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
		Playershop shop = createPlayershop();
		when(shop.getShopId()).thenReturn("P0");
		List<String> list = playershopManager.getPlayershopIdList();
		assertEquals(1, list.size());
		assertEquals("P0", list.get(0));
	}

	@Test
	public void despawnAllVillagersTest() {
		Playershop shop = createPlayershop();
		playershopManager.despawnAllVillagers();
		verify(shop).despawnVillager();
	}

	@Test
	public void deletePlayershopTest() {
		Playershop shop = createPlayershop();
		playershopManager.deletePlayerShop(shop);
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao).savePlayershopIds(new ArrayList<>());
		verify(shop).deleteShop();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllPlayerShopsTest() {
		Playershop shop = mock(Playershop.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serviceComponent.getPlayershop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(configDao.hasPlayerShopNames()).thenReturn(false);
		when(configDao.loadPlayershopIds()).thenReturn(Arrays.asList("P0"));
		playershopManager.loadAllPlayerShops();
		assertEquals(1, playershopManager.getPlayerShops().size());
		assertEquals(shop, playershopManager.getPlayerShops().get(0));
		assertDoesNotThrow(() -> verify(shop).setupExisting(null, "P0"));
		verifyNoInteractions(logger);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllPlayerShopsTestWithLoadingError()
			throws TownSystemException, ShopSystemException, GeneralEconomyException {
		Playershop shop = mock(Playershop.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serviceComponent.getPlayershop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		TownSystemException e = mock(TownSystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		when(configDao.hasPlayerShopNames()).thenReturn(false);
		when(configDao.loadPlayershopIds()).thenReturn(Arrays.asList("P0"));
		doThrow(e).when(shop).setupExisting(null, "P0");
		playershopManager.loadAllPlayerShops();
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(logger).warn("[Ultimate_Economy] Failed to load the shop P0");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");
		verifyNoMoreInteractions(logger);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllPlayershopsTestWithOldLoading() {
		Playershop shop = mock(Playershop.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serviceComponent.getPlayershop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(configDao.hasPlayerShopNames()).thenReturn(true);
		when(configDao.loadPlayerShopNames()).thenReturn(Arrays.asList("myshop"));
		playershopManager.loadAllPlayerShops();
		assertEquals(1, playershopManager.getPlayerShops().size());
		assertEquals(shop, playershopManager.getPlayerShops().get(0));
		assertDoesNotThrow(() -> verify(shop).setupExisting("myshop", "P0"));
		verify(configDao).removeDeprecatedPlayerShopNames();
		verify(configDao).savePlayershopIds(anyList());
		verifyNoInteractions(logger);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllPlayershopsTestWithOldLoadingAndLoadError()
			throws TownSystemException, ShopSystemException, GeneralEconomyException {
		Playershop shop = mock(Playershop.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serviceComponent.getPlayershop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		TownSystemException e = mock(TownSystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		when(configDao.hasPlayerShopNames()).thenReturn(true);
		when(configDao.loadPlayerShopNames()).thenReturn(Arrays.asList("myshop"));
		doThrow(e).when(shop).setupExisting("myshop", "P0");
		playershopManager.loadAllPlayerShops();
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(logger).warn("[Ultimate_Economy] Failed to load the shop myshop");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");
		verifyNoMoreInteractions(logger);
		verify(configDao).removeDeprecatedPlayerShopNames();
		verify(configDao).savePlayershopIds(anyList());
	}
}
