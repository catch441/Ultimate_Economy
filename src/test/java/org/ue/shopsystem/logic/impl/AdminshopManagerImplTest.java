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
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.ServiceComponent;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.shopsystem.logic.api.Adminshop;
import org.ue.shopsystem.logic.api.ShopValidationHandler;
import org.ue.shopsystem.logic.api.ShopsystemException;

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

	@Test
	public void createAdminshopTestWithInvalidSize() throws ShopsystemException {
		doThrow(ShopsystemException.class).when(validationHandler).checkForValidSize(5);
		assertThrows(ShopsystemException.class, () -> adminshopManager.createAdminShop("myshop", null, 5));
		assertEquals(0, adminshopManager.getAdminshopList().size());
		verifyNoInteractions(configDao);
	}

	@Test
	public void createAdminshopTestWithExistingName() throws ShopsystemException {
		doThrow(ShopsystemException.class).when(validationHandler).checkForValueNotInList(anyList(), eq("my_shop"));
		assertThrows(ShopsystemException.class, () -> adminshopManager.createAdminShop("my_shop", null, 5));
		assertEquals(0, adminshopManager.getAdminshopList().size());
		verifyNoInteractions(configDao);
	}

	@Test
	public void createAdminshopTestWithInvalidName() throws ShopsystemException {
		doThrow(ShopsystemException.class).when(validationHandler).checkForValidShopName("my_shop");
		assertThrows(ShopsystemException.class, () -> adminshopManager.createAdminShop("my_shop", null, 5));
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
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidSize(9));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueNotInList(anyList(), eq("myshop")));
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
			Adminshop shop = createAdminshop("A0");
			when(shop.getName()).thenReturn("myshop");
			adminshopManager.getAdminShopByName("myshop2");
			fail();
		} catch (ShopsystemException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("myshop2", e.getParams()[0]);
			assertEquals(ExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
		}
	}

	@Test
	public void getAdminShopByNameTest() {
		Adminshop shop = createAdminshop("A0");
		when(shop.getName()).thenReturn("myshop");
		Adminshop result = assertDoesNotThrow(() -> adminshopManager.getAdminShopByName("myshop"));
		assertNotNull(result);
		assertEquals(result, shop);
	}

	@Test
	public void getAdminshopByIdTest() {
		Adminshop shop = createAdminshop("A0");
		Adminshop result = assertDoesNotThrow(() -> adminshopManager.getAdminShopById("A0"));
		assertEquals(result, shop);
	}

	@Test
	public void getAdminshopByIdFailTest() throws ShopsystemException {
		createAdminshop("A0");
		doThrow(ShopsystemException.class).when(validationHandler).checkForValueExists(null, "A1");
		assertThrows(ShopsystemException.class, () -> adminshopManager.getAdminShopById("A1"));
	}

	@Test
	public void generateFreeAdminShopIdTest() {
		String id1 = adminshopManager.generateFreeAdminShopId();
		createAdminshop("A0");
		String id2 = adminshopManager.generateFreeAdminShopId();
		assertEquals("A0", id1);
		assertEquals("A1", id2);
	}

	@Test
	public void loadAllAdminShopsTest() {
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Adminshop shop = mock(Adminshop.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getAdminshop()).thenReturn(shop);
		when(configDao.loadAdminshopIds()).thenReturn(Arrays.asList("A0"));

		adminshopManager.loadAllAdminShops();
		assertEquals(shop, adminshopManager.getAdminshopList().get(0));
		assertEquals(1, adminshopManager.getAdminshopList().size());
		assertDoesNotThrow(() -> verify(shop).setupExisting("A0"));
	}

	@Test
	public void despawnAllVillagersTest() {
		Adminshop shop1 = createAdminshop("A0");
		Adminshop shop2 = createAdminshop("A1");
		adminshopManager.despawnAllVillagers();
		verify(shop1).despawn();
		verify(shop2).despawn();
	}

	@Test
	public void loadAllAdminShopsTestWithLoadingError() throws EconomyPlayerException {
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(e.getMessage()).thenReturn("my error message");
		Adminshop shop = mock(Adminshop.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getAdminshop()).thenReturn(shop);
		when(configDao.loadAdminshopIds()).thenReturn(Arrays.asList("A0"));
		doThrow(e).when(shop).setupExisting("A0");
		adminshopManager.loadAllAdminShops();
		assertEquals(0, adminshopManager.getAdminshopList().size());
		verify(e).getMessage();
	}

	private Adminshop createAdminshop(String id) {
		Location loc = mock(Location.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Adminshop shop = mock(Adminshop.class);
		when(shop.getShopId()).thenReturn(id);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getAdminshop()).thenReturn(shop);
		assertDoesNotThrow(() -> adminshopManager.createAdminShop("myshop", loc, 9));
		return shop;
	}
}
