package org.ue.shopsystem.logic.impl;

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
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.RentshopManager;
import org.ue.shopsystem.logic.api.ShopsystemException;

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
		when(serverProvider.getWorldTime()).thenReturn(120001L);
		when(shop.getExpiresAt()).thenReturn(132000L);
		task.run();
		assertDoesNotThrow(() -> verify(shop, never()).resetShop());
		verify(player).sendMessage("my reminder");
	}

	@Test
	public void runTaskTestWithReset() {
		Rentshop shop = mock(Rentshop.class);
		when(rentshopManager.getRentShops()).thenReturn(Arrays.asList(shop));
		when(shop.isRentable()).thenReturn(false);		
		when(serverProvider.getWorldTime()).thenReturn(120001L);
		when(shop.getExpiresAt()).thenReturn(120000L);
		task.run();
		assertDoesNotThrow(() -> verify(shop).resetShop());
	}
	
	@Test
	public void runTaskTestWithResetError() {
		ShopsystemException e = mock(ShopsystemException.class);
		Rentshop shop = mock(Rentshop.class);
		assertDoesNotThrow(() -> doThrow(e).when(shop).resetShop());
		when(e.getMessage()).thenReturn("my error message");
		when(rentshopManager.getRentShops()).thenReturn(Arrays.asList(shop));
		when(shop.isRentable()).thenReturn(false);
		when(serverProvider.getWorldTime()).thenReturn(120001L);
		when(shop.getExpiresAt()).thenReturn(120000L);
		task.run();
		verify(e).getMessage();
	}
	
	@Test
	public void runTaskTestWithNoAction() {
		Rentshop shop = mock(Rentshop.class);
		when(rentshopManager.getRentShops()).thenReturn(Arrays.asList(shop));
		when(shop.isRentable()).thenReturn(false);
		when(serverProvider.getWorldTime()).thenReturn(120000L);
		when(shop.getExpiresAt()).thenReturn(120000L);
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
		when(serverProvider.getWorldTime()).thenReturn(120001L);
		when(shop.getExpiresAt()).thenReturn(132000L);
		task.run();
		assertDoesNotThrow(() -> verify(shop, never()).resetShop());
		verify(owner, never()).getPlayer();
	}
}
