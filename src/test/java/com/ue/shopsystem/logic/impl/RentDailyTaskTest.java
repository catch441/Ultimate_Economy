package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.RentshopManager;

@ExtendWith(MockitoExtension.class)
public class RentDailyTaskTest {
	
	@InjectMocks
	RentDailyTask task;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	RentshopManager rentshopManager;
	@Mock
	ServerProvider serverProvider;

	@Test
	public void runTaskTestWithReminder() {
		EconomyPlayer owner = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		Rentshop shop = mock(Rentshop.class);
		when(messageWrapper.getString("rent_reminder")).thenReturn("my reminder");
		when(shop.getOwner()).thenReturn(owner);
		when(owner.getPlayer()).thenReturn(player);
		when(owner.isOnline()).thenReturn(true);
		when(rentshopManager.getRentShops()).thenReturn(Arrays.asList(shop));
		when(shop.isRentable()).thenReturn(false);
		when(serverProvider.getActualTime()).thenReturn(9999999L);
		when(shop.getExpiresAt()).thenReturn(10000000L);
		task.run();
		assertDoesNotThrow(() -> verify(shop, never()).resetShop());
		verify(player).sendMessage("my reminder");
	}

	@Test
	public void runTaskTestWithReset() {
		Rentshop shop = mock(Rentshop.class);
		when(rentshopManager.getRentShops()).thenReturn(Arrays.asList(shop));
		when(shop.isRentable()).thenReturn(false);
		when(serverProvider.getActualTime()).thenReturn(20000000L);
		when(shop.getExpiresAt()).thenReturn(10000000L);
		task.run();
		assertDoesNotThrow(() -> verify(shop).resetShop());
	}
	
	@Test
	public void runTaskTestWithResetError() {
		ShopSystemException e = mock(ShopSystemException.class);
		Rentshop shop = mock(Rentshop.class);
		assertDoesNotThrow(() -> doThrow(e).when(shop).resetShop());
		when(e.getMessage()).thenReturn("my error message");
		when(rentshopManager.getRentShops()).thenReturn(Arrays.asList(shop));
		when(shop.isRentable()).thenReturn(false);
		when(serverProvider.getActualTime()).thenReturn(20000000L);
		when(shop.getExpiresAt()).thenReturn(10000000L);
		task.run();
		verify(e).getMessage();
	}
	
	@Test
	public void runTaskTestWithNoAction() {
		Rentshop shop = mock(Rentshop.class);
		when(rentshopManager.getRentShops()).thenReturn(Arrays.asList(shop));
		when(shop.isRentable()).thenReturn(false);
		when(serverProvider.getActualTime()).thenReturn(10000000L);
		when(shop.getExpiresAt()).thenReturn(20000000L);
		task.run();
		verify(shop, never()).getOwner();
	}
	
	@Test
	public void runTaskTestWithReminderOffline() {
		EconomyPlayer owner = mock(EconomyPlayer.class);
		Rentshop shop = mock(Rentshop.class);
		when(shop.getOwner()).thenReturn(owner);
		when(owner.isOnline()).thenReturn(false);
		when(rentshopManager.getRentShops()).thenReturn(Arrays.asList(shop));
		when(shop.isRentable()).thenReturn(false);
		when(serverProvider.getActualTime()).thenReturn(9999999L);
		when(shop.getExpiresAt()).thenReturn(10000000L);
		task.run();
		assertDoesNotThrow(() -> verify(shop, never()).resetShop());
		verify(owner, never()).getPlayer();
	}
}
