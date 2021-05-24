package org.ue.economyplayer.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankManager;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.UltimateEconomyProvider;
import org.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerValidator;

@ExtendWith(MockitoExtension.class)
public class EconomyPlayerManagerImplTest {

	@InjectMocks
	EconomyPlayerManagerImpl ecoPlayerManager;
	@Mock
	EconomyPlayerDao ecoPlayerDao;
	@Mock
	EconomyPlayerValidator validationHandler;
	@Mock
	BankManager bankManager;
	@Mock
	ServerProvider serverProvider;
	@Mock
	UltimateEconomyProvider provider;
	
	private EconomyPlayer createEcoPlayerMock(String name) {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(serverProvider.getPlayer(name)).thenReturn(player);
		when(provider.createEconomyPlayer()).thenReturn(ecoPlayer);
		assertDoesNotThrow(() -> ecoPlayerManager.createEconomyPlayer(name));
		return ecoPlayer;
	}

	@Test
	public void createEconomyPlayerTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(serverProvider.getPlayer("catch441")).thenReturn(player);
		when(provider.createEconomyPlayer()).thenReturn(ecoPlayer);
		assertDoesNotThrow(() -> ecoPlayerManager.createEconomyPlayer("catch441"));
		assertEquals(1, ecoPlayerManager.getAllEconomyPlayers().size());
		assertEquals(ecoPlayer, ecoPlayerManager.getAllEconomyPlayers().get(0));
		verify(ecoPlayer).setupNew(player, "catch441");
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueNotInList(new ArrayList<>(), "catch441"));
	}

	@Test
	public void createEconomyPlayerTestWithExistingName() throws EconomyPlayerException {
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForValueNotInList(new ArrayList<>(),
				"catch441");
		assertThrows(EconomyPlayerException.class, () -> ecoPlayerManager.createEconomyPlayer("catch441"));
		assertEquals(0, ecoPlayerManager.getAllEconomyPlayers().size());
	}

	@Test
	public void getEconomyPlayerNameListTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock("catch441");
		when(ecoPlayer.getName()).thenReturn("catch441");	
		List<String> list = ecoPlayerManager.getEconomyPlayerNameList();
		assertEquals(1, list.size());
		assertEquals("catch441", list.get(0));
	}

	@Test
	public void getEconomyPlayerByNameTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock("catch441");
		EconomyPlayer result = assertDoesNotThrow(() -> ecoPlayerManager.getEconomyPlayerByName("catch441"));
		assertEquals(ecoPlayer, result);
	}

	@Test
	public void getEconomyPlayerByNameTestWithNoPlayer() throws EconomyPlayerException {
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForValueExists(null, "catch441");
		assertThrows(EconomyPlayerException.class, () -> ecoPlayerManager.getEconomyPlayerByName("catch441"));
	}

	@Test
	public void getAllEconomyPlayersTest() {
		EconomyPlayer ecoPlayer1 = createEcoPlayerMock("catch441");
		EconomyPlayer ecoPlayer2 = createEcoPlayerMock("Wulfgar");
		List<EconomyPlayer> list = ecoPlayerManager.getAllEconomyPlayers();
		assertEquals(2, list.size());
		assertEquals(ecoPlayer1, list.get(0));
		assertEquals(ecoPlayer2, list.get(1));
	}

	@Test
	public void deleteEconomyPlayerTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock("catch441");
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(ecoPlayer.getName()).thenReturn("catch441");
		
		ecoPlayerManager.deleteEconomyPlayer(ecoPlayer);
		assertEquals(0, ecoPlayerManager.getAllEconomyPlayers().size());
		verify(ecoPlayerDao).deleteEconomyPlayer("catch441");
		verify(bankManager).deleteBankAccount(account);
	}

	@Test
	public void loadAllEconomyPlayers() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(serverProvider.getPlayer("catch441")).thenReturn(player);
		when(provider.createEconomyPlayer()).thenReturn(ecoPlayer);
		when(ecoPlayerDao.loadPlayerList()).thenReturn(Arrays.asList("catch441"));
		
		ecoPlayerManager.loadAllEconomyPlayers();
		List<EconomyPlayer> list = ecoPlayerManager.getAllEconomyPlayers();
		assertEquals(1, list.size());
		assertEquals(ecoPlayer, list.get(0));
		verify(ecoPlayer).setupExisting(player, "catch441");
		verify(ecoPlayerDao).setupSavefile();
	}
}
