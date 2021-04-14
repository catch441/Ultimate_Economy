package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.ServiceComponent;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.general.api.GeneralEconomyValidationHandler;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;
import org.ue.shopsystem.logic.api.Adminshop;
import org.ue.shopsystem.logic.api.ShopValidationHandler;
import org.ue.shopsystem.logic.ShopSystemException;
import org.ue.townsystem.logic.TownSystemException;

@ExtendWith(MockitoExtension.class)
public class AdminshopManagerImplTest {

	@InjectMocks
	AdminshopManagerImpl adminshopManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ShopValidationHandler validationHandler;
	@Mock
	ServerProvider serverProvider;
	@Mock
	ConfigDao configDao;
	@Mock
	GeneralEconomyValidationHandler generalValidator;

	@Test
	public void createAdminshopTestWithInvalidSize() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSize(5);
		assertThrows(GeneralEconomyException.class, () -> adminshopManager.createAdminShop("myshop", null, 5));
		assertEquals(0, adminshopManager.getAdminshopList().size());
		verifyNoInteractions(configDao);
	}

	@Test
	public void createAdminshopTestWithExistingName() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValueNotInList(anyList(), eq("my_shop"));
		assertThrows(GeneralEconomyException.class, () -> adminshopManager.createAdminShop("my_shop", null, 5));
		assertEquals(0, adminshopManager.getAdminshopList().size());
		verifyNoInteractions(configDao);
	}

	@Test
	public void createAdminshopTestWithInvalidName() throws ShopSystemException {
		doThrow(ShopSystemException.class).when(validationHandler).checkForValidShopName("my_shop");
		assertThrows(ShopSystemException.class, () -> adminshopManager.createAdminShop("my_shop", null, 5));
		assertEquals(0, adminshopManager.getAdminshopList().size());
		verifyNoInteractions(configDao);
	}

	@Test
	public void createAdminshopTestSuccess() {
		Location loc = mock(Location.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Adminshop shop = mock(Adminshop.class);
		when(serviceComponent.getAdminshop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> adminshopManager.createAdminShop("myshop", loc, 9));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidShopName("myshop"));
		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSize(9));
		assertDoesNotThrow(() -> verify(generalValidator).checkForValueNotInList(anyList(), eq("myshop")));
		verify(configDao).saveAdminshopIds(anyList());
		assertEquals(1, adminshopManager.getAdminshopList().size());
		Adminshop result = adminshopManager.getAdminshopList().get(0);
		assertEquals(shop, result);
		verify(shop).setupNew("myshop", "A0", loc, 9);
	}

	@Test
	public void deleteAdminshopTest() {
		Adminshop shop = mock(Adminshop.class);
		adminshopManager.deleteAdminShop(shop);
		verify(shop).deleteShop();
		;
		verify(configDao).saveAdminshopIds(new ArrayList<>());
		assertEquals(0, adminshopManager.getAdminshopList().size());
	}

	@Test
	public void getAdminShopByNameFailTest() {
		try {
			Adminshop shop = createAdminshop();
			when(shop.getName()).thenReturn("myshop");
			adminshopManager.getAdminShopByName("myshop2");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("myshop2", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
		}
	}

	@Test
	public void getAdminShopByNameTest() {
		Adminshop shop = createAdminshop();
		when(shop.getName()).thenReturn("myshop");
		Adminshop result = assertDoesNotThrow(() -> adminshopManager.getAdminShopByName("myshop"));
		assertNotNull(result);
		assertEquals(result, shop);
	}

	@Test
	public void getAdminshopByIdTest() {
		Adminshop shop = createAdminshop();
		when(shop.getShopId()).thenReturn("A0");
		Adminshop result = assertDoesNotThrow(() -> adminshopManager.getAdminShopById("A0"));
		assertNotNull(result);
		assertEquals(result, shop);
	}

	@Test
	public void getAdminshopByIdFailTest() {
		try {
			Adminshop shop = createAdminshop();
			when(shop.getShopId()).thenReturn("A0");
			adminshopManager.getAdminShopById("A1");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("A1", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
		}
	}

	@Test
	public void generateFreeAdminShopIdTest() {
		String id1 = adminshopManager.generateFreeAdminShopId();
		Adminshop shop = createAdminshop();
		when(shop.getShopId()).thenReturn("A0");
		String id2 = adminshopManager.generateFreeAdminShopId();
		assertEquals("A0", id1);
		assertEquals("A1", id2);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllAdminShopsTest() {
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Adminshop shop = mock(Adminshop.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getAdminshop()).thenReturn(shop);
		when(configDao.hasAdminShopNames()).thenReturn(false);
		when(configDao.loadAdminshopIds()).thenReturn(Arrays.asList("A0"));

		adminshopManager.loadAllAdminShops();
		assertEquals(shop, adminshopManager.getAdminshopList().get(0));
		assertEquals(1, adminshopManager.getAdminshopList().size());
		assertDoesNotThrow(() -> verify(shop).setupExisting(null, "A0"));
	}

	@Test
	public void despawnAllVillagersTest() {
		Adminshop shop1 = createAdminshop();
		Adminshop shop2 = createAdminshop();
		adminshopManager.despawnAllVillagers();
		verify(shop1).despawnVillager();
		verify(shop2).despawnVillager();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllAdminShopsTestWithLoadingError()
			throws TownSystemException, ShopSystemException, GeneralEconomyException {
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		TownSystemException e = mock(TownSystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		Adminshop shop = mock(Adminshop.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getAdminshop()).thenReturn(shop);
		when(configDao.hasAdminShopNames()).thenReturn(false);
		when(configDao.loadAdminshopIds()).thenReturn(Arrays.asList("A0"));
		doThrow(e).when(shop).setupExisting(null, "A0");
		adminshopManager.loadAllAdminShops();
		assertEquals(0, adminshopManager.getAdminshopList().size());
		verify(e).getMessage();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllAdminshopsOldTest() {
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Adminshop shop = mock(Adminshop.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getAdminshop()).thenReturn(shop);
		when(configDao.hasAdminShopNames()).thenReturn(true);
		when(configDao.loadAdminShopNames()).thenReturn(Arrays.asList("myshop"));
		adminshopManager.loadAllAdminShops();
		assertEquals(1, adminshopManager.getAdminshopList().size());
		assertEquals(shop, adminshopManager.getAdminshopList().get(0));
		assertDoesNotThrow(() -> verify(shop).setupExisting("myshop", "A0"));
		verify(configDao).removeDeprecatedAdminshopNames();
		verify(configDao).saveAdminshopIds(anyList());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllAdminshopsOldTestWithLoadingError()
			throws TownSystemException, ShopSystemException, GeneralEconomyException {
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Adminshop shop = mock(Adminshop.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getAdminshop()).thenReturn(shop);
		TownSystemException e = mock(TownSystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(configDao.hasAdminShopNames()).thenReturn(true);
		when(configDao.loadAdminShopNames()).thenReturn(Arrays.asList("myshop"));
		doThrow(e).when(shop).setupExisting("myshop", "A0");
		adminshopManager.loadAllAdminShops();
		assertEquals(0, adminshopManager.getAdminshopList().size());
		verify(e).getMessage();
		verify(configDao).removeDeprecatedAdminshopNames();
		verify(configDao).saveAdminshopIds(anyList());
	}

	private Adminshop createAdminshop() {
		Location loc = mock(Location.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Adminshop shop = mock(Adminshop.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getAdminshop()).thenReturn(shop);
		assertDoesNotThrow(() -> adminshopManager.createAdminShop("myshop", loc, 9));
		return shop;
	}
}
