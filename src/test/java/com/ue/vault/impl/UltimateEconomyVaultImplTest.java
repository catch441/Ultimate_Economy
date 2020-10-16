package com.ue.vault.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.ServerProvider;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;

import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

@ExtendWith(MockitoExtension.class)
public class UltimateEconomyVaultImplTest {

	@InjectMocks
	UltimateEconomyVaultImpl vault;
	@Mock
	ConfigManager configManager;
	@Mock
	BankManager bankManager;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	ServerProvider serverProvider;

	@Test
	public void currencyNamePluralTest() {
		when(configManager.getCurrencyPl()).thenReturn("$");
		assertEquals("$", vault.currencyNamePlural());
	}

	@Test
	public void currencyNameSingularTest() {
		when(configManager.getCurrencySg()).thenReturn("$");
		assertEquals("$", vault.currencyNameSingular());
	}

	@Test
	public void getBalanceTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(1.5);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		assertEquals("1.5", String.valueOf(vault.getBalance(player)));
	}

	@Test
	public void getBalanceTestError() {
		Player player = mock(Player.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441"))
				.thenThrow(EconomyPlayerException.class));
		when(player.getName()).thenReturn("catch441");
		assertEquals("0.0", String.valueOf(vault.getBalance(player)));
	}

	@Test
	public void getBalanceTestWithString() throws EconomyPlayerException {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(1.5);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("catch441"));
		assertEquals("1.5", String.valueOf(vault.getBalance("catch441")));
	}

	@Test
	public void getBalanceTestWithStringWorld() throws EconomyPlayerException {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(1.5);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("catch441"));
		assertEquals("1.5", String.valueOf(vault.getBalance("catch441", "world")));
	}

	@Test
	public void getBalanceTestWithStringNoPlayer() throws EconomyPlayerException {
		BankAccount account = mock(BankAccount.class);
		assertDoesNotThrow(() -> when(bankManager.getBankAccountByIban("mybank")).thenReturn(account));
		when(account.getAmount()).thenReturn(1.5);
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("catch441"));
		assertEquals("1.5", String.valueOf(vault.getBalance("mybank")));
	}

	@Test
	public void getBalanceTestWithStringError() throws EconomyPlayerException, GeneralEconomyException {
		when(bankManager.getBankAccountByIban("mybank")).thenThrow(GeneralEconomyException.class);
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("catch441"));
		assertEquals("0.0", String.valueOf(vault.getBalance("mybank")));
	}

	@Test
	public void getBalanceTestWorld() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(1.5);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		assertEquals("1.5", String.valueOf(vault.getBalance(player, "world")));
	}

	@Test
	public void getNameTest() {
		assertEquals("Ultimate_Economy", vault.getName());
	}

	@Test
	public void hasAccountTest() {
		Player player = mock(Player.class);
		assertTrue(vault.hasAccount(player));
	}

	@Test
	public void hasAccountTestWorld() {
		Player player = mock(Player.class);
		assertTrue(vault.hasAccount(player, "world"));
	}

	@Test
	public void hasAccountTestString() {
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("catch441"));
		assertTrue(vault.hasAccount("catch441"));
	}

	@Test
	public void hasAccountTestStringNotPlayer() {
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("nothing"));
		when(bankManager.getIbanList()).thenReturn(Arrays.asList("myiban"));
		assertTrue(vault.hasAccount("myiban"));
		assertFalse(vault.hasAccount("myiban2"));
	}

	@Test
	public void hasAccountTestStringWorld() {
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("catch441"));
		assertTrue(vault.hasAccount("catch441", "world"));
	}

	@Test
	public void hasTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayer.hasEnoughtMoney(1.5)).thenReturn(true));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		assertTrue(vault.has(player, 1.5));
	}

	@Test
	public void hasTestWorld() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayer.hasEnoughtMoney(1.5)).thenReturn(true));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		assertTrue(vault.has(player, "world", 1.5));
	}

	@Test
	public void hasTestWithNoEcoPlayer() throws EconomyPlayerException {
		Player player = mock(Player.class);
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(EconomyPlayerException.class);
		when(player.getName()).thenReturn("catch441");
		assertFalse(vault.has(player, 1.5));
	}

	@Test
	public void hasTestString() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayer.hasEnoughtMoney(1.5)).thenReturn(true));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		assertTrue(vault.has("catch441", 1.5));
	}

	@Test
	public void hasTestStringWorld() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayer.hasEnoughtMoney(1.5)).thenReturn(true));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		assertTrue(vault.has("catch441", "world", 1.5));
	}

	@Test
	public void hasTestWithStringNoEcoPlayer() throws EconomyPlayerException {
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(EconomyPlayerException.class);
		assertFalse(vault.has("catch441", 1.5));
	}

	@Test
	public void hasBankSupportTest() {
		assertTrue(vault.hasBankSupport());
	}

	@Test
	public void isEnabledTest() {
		assertFalse(vault.isEnabled());
		when(serverProvider.getJavaPluginInstance()).thenReturn(mock(JavaPlugin.class));
		assertTrue(vault.isEnabled());
	}

	@Test
	public void getBanksTest() {
		when(bankManager.getIbanList()).thenReturn(Arrays.asList("myiban"));
		assertEquals(Arrays.asList("myiban"), vault.getBanks());
	}

	@Test
	public void bankDepositTest() {
		BankAccount account = mock(BankAccount.class);
		assertDoesNotThrow(() -> when(bankManager.getBankAccountByIban("myiban")).thenReturn(account));
		EconomyResponse response = vault.bankDeposit("myiban", 1.5);
		assertDoesNotThrow(() -> verify(account).increaseAmount(1.5));
		assertEquals(ResponseType.SUCCESS, response.type);
		assertEquals("1.5", String.valueOf(response.amount));
	}

	@Test
	public void bankDepositTestNoBankAccount() throws GeneralEconomyException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		when(e.getMessage()).thenReturn("error");
		when(bankManager.getBankAccountByIban("myiban")).thenThrow(e);
		EconomyResponse response = vault.bankDeposit("myiban", 1.5);
		assertEquals(ResponseType.FAILURE, response.type);
		assertEquals("error", response.errorMessage);
	}

	@Test
	public void depositPlayerTest() {
		BankAccount account = mock(BankAccount.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("catch441"));
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(5.5);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		EconomyResponse response = vault.depositPlayer("catch441", 1.5);
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(1.5, false));
		assertEquals(ResponseType.SUCCESS, response.type);
		assertEquals("1.5", String.valueOf(response.amount));
		assertEquals("5.5", String.valueOf(response.balance));
	}

	@Test
	public void depositPlayerTestPlayer() {
		Player player = mock(Player.class);
		BankAccount account = mock(BankAccount.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("catch441"));
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(5.5);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		EconomyResponse response = vault.depositPlayer(player, 1.5);
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(1.5, false));
		assertEquals(ResponseType.SUCCESS, response.type);
		assertEquals("1.5", String.valueOf(response.amount));
		assertEquals("5.5", String.valueOf(response.balance));
	}

	@Test
	public void depositPlayerTestWorldPlayer() {
		Player player = mock(Player.class);
		BankAccount account = mock(BankAccount.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("catch441"));
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(5.5);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		EconomyResponse response = vault.depositPlayer(player, "world", 1.5);
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(1.5, false));
		assertEquals(ResponseType.SUCCESS, response.type);
		assertEquals("1.5", String.valueOf(response.amount));
		assertEquals("5.5", String.valueOf(response.balance));
	}

	@Test
	public void depositPlayerTestStringWorld() {
		BankAccount account = mock(BankAccount.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("catch441"));
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(5.5);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		EconomyResponse response = vault.depositPlayer("catch441", "world", 1.5);
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(1.5, false));
		assertEquals(ResponseType.SUCCESS, response.type);
		assertEquals("1.5", String.valueOf(response.amount));
		assertEquals("5.5", String.valueOf(response.balance));
	}

	@Test
	public void depositPlayerTestError() throws EconomyPlayerException {
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(e.getMessage()).thenReturn("error");
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(e);
		EconomyResponse response = vault.depositPlayer(player, 1.5);
		assertEquals(ResponseType.FAILURE, response.type);
		assertEquals("error", response.errorMessage);
	}

	@Test
	public void fractionalDigitsTest() {
		assertEquals(-1, vault.fractionalDigits());
	}

	@Test
	public void formatTest() {
		assertEquals("1.5", vault.format(1.5));
	}

	@Test
	public void withdrawPlayerTestString() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(5.5);
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("catch441"));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		EconomyResponse response = vault.withdrawPlayer("catch441", 1.5);
		assertEquals(ResponseType.SUCCESS, response.type);
		assertEquals("1.5", String.valueOf(response.amount));
		assertEquals("5.5", String.valueOf(response.balance));
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(1.5, false));
	}
	
	@Test
	public void withdrawPlayerTestError() throws EconomyPlayerException {
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(e.getMessage()).thenReturn("error");
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(e);
		EconomyResponse response = vault.withdrawPlayer("catch441", 1.5);
		assertEquals(ResponseType.FAILURE, response.type);
		assertEquals("error", response.errorMessage);
	}
	
	@Test
	public void withdrawPlayerTestStringWorld() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(5.5);
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("catch441"));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		EconomyResponse response = vault.withdrawPlayer("catch441", "world", 1.5);
		assertEquals(ResponseType.SUCCESS, response.type);
		assertEquals("1.5", String.valueOf(response.amount));
		assertEquals("5.5", String.valueOf(response.balance));
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(1.5, false));
	}
	
	@Test
	public void withdrawPlayerTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		when(player.getName()).thenReturn("catch441");
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(5.5);
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("catch441"));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		EconomyResponse response = vault.withdrawPlayer(player, 1.5);
		assertEquals(ResponseType.SUCCESS, response.type);
		assertEquals("1.5", String.valueOf(response.amount));
		assertEquals("5.5", String.valueOf(response.balance));
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(1.5, false));
	}
	
	@Test
	public void withdrawPlayerTestWord() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		when(player.getName()).thenReturn("catch441");
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(5.5);
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(Arrays.asList("catch441"));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		EconomyResponse response = vault.withdrawPlayer(player, "world", 1.5);
		assertEquals(ResponseType.SUCCESS, response.type);
		assertEquals("1.5", String.valueOf(response.amount));
		assertEquals("5.5", String.valueOf(response.balance));
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(1.5, false));
	}
	
	@Test
	public void isBankMemberTestString() {
		EconomyResponse response = vault.isBankMember("", "");
		assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
	}
	
	@Test
	public void isBankMemberTest() {
		Player player = mock(Player.class);
		EconomyResponse response = vault.isBankMember("", player);
		assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
	}
	
	@Test
	public void isBankOwnerTestString() {
		EconomyResponse response = vault.isBankOwner("", "");
		assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
	}
	
	@Test
	public void isBankOwnerTest() {
		Player player = mock(Player.class);
		EconomyResponse response = vault.isBankOwner("", player);
		assertEquals(ResponseType.NOT_IMPLEMENTED, response.type);
	}
	
	@Test
	public void createPlayerAccountTestString() {
		assertFalse(vault.createPlayerAccount("catch441"));
		assertFalse(vault.createPlayerAccount("catch441", "world"));
	}
	
	@Test
	public void createPlayerAccountTest() {
		Player player = mock(Player.class);
		assertFalse(vault.createPlayerAccount(player));
		assertFalse(vault.createPlayerAccount(player, "world"));
	}
	
	@Test
	public void bankBalanceTest() {
		BankAccount account = mock(BankAccount.class);
		assertDoesNotThrow(() -> when(bankManager.getBankAccountByIban("myiban")).thenReturn(account));
		when(account.getAmount()).thenReturn(1.5);
		EconomyResponse response = vault.bankBalance("myiban");
		assertEquals(ResponseType.SUCCESS, response.type);
		assertEquals("1.5", String.valueOf(response.balance));
	}
	
	@Test
	public void bankBalanceTestError() throws GeneralEconomyException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		when(e.getMessage()).thenReturn("error");
		when(bankManager.getBankAccountByIban("myiban")).thenThrow(e);
		EconomyResponse response = vault.bankBalance("myiban");
		assertEquals(ResponseType.FAILURE, response.type);
		assertEquals("error", response.errorMessage);
	}
	
	@Test
	public void createBankTestString() {
		BankAccount account = mock(BankAccount.class);
		assertDoesNotThrow(() -> when(bankManager.createExternalBankAccount(0, "myiban")).thenReturn(account));
		EconomyResponse response = vault.createBank("myiban", "catch441");
		assertEquals(ResponseType.SUCCESS, response.type);
	}
	
	@Test
	public void createBankTest() {
		Player player = mock(Player.class);
		BankAccount account = mock(BankAccount.class);
		assertDoesNotThrow(() -> when(bankManager.createExternalBankAccount(0, "myiban")).thenReturn(account));
		EconomyResponse response = vault.createBank("myiban", player);
		assertEquals(ResponseType.SUCCESS, response.type);
	}
	
	@Test
	public void createBankTestError() throws GeneralEconomyException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		when(e.getMessage()).thenReturn("error");
		when(bankManager.createExternalBankAccount(0, "myiban")).thenThrow(e);
		EconomyResponse response = vault.createBank("myiban", "catch441");
		assertEquals(ResponseType.FAILURE, response.type);
		assertEquals("error", response.errorMessage);
	}
	
	@Test
	public void deleteBankTest() {
		BankAccount account = mock(BankAccount.class);
		assertDoesNotThrow(() -> when(bankManager.getBankAccountByIban("myiban")).thenReturn(account));
		EconomyResponse response = vault.deleteBank("myiban");
		assertEquals(ResponseType.SUCCESS, response.type);
		verify(bankManager).deleteBankAccount(account);
	}
	
	@Test
	public void deleteBankTestError() throws GeneralEconomyException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		when(e.getMessage()).thenReturn("error");
		when(bankManager.getBankAccountByIban("myiban")).thenThrow(e);
		EconomyResponse response = vault.deleteBank("myiban");
		assertEquals(ResponseType.FAILURE, response.type);
		assertEquals("error", response.errorMessage);
	}
	
	@Test
	public void bankWithdrawTest() {
		BankAccount account = mock(BankAccount.class);
		when(account.getAmount()).thenReturn(5.5);
		assertDoesNotThrow(() -> when(bankManager.getBankAccountByIban("myiban")).thenReturn(account));
		EconomyResponse response = vault.bankWithdraw("myiban", 1.5);
		assertEquals(ResponseType.SUCCESS, response.type);
		assertEquals("1.5", String.valueOf(response.amount));
		assertEquals("5.5", String.valueOf(response.balance));
		assertDoesNotThrow(() -> verify(account).decreaseAmount(1.5));
	}
	
	@Test
	public void bankWithdrawTestError() throws GeneralEconomyException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		when(e.getMessage()).thenReturn("error");
		when(bankManager.getBankAccountByIban("myiban")).thenThrow(e);
		EconomyResponse response = vault.bankWithdraw("myiban", 1.5);
		assertEquals(ResponseType.FAILURE, response.type);
		assertEquals("error", response.errorMessage);
	}
	
	@Test
	public void bankHasTestError() throws GeneralEconomyException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		when(e.getMessage()).thenReturn("error");
		when(bankManager.getBankAccountByIban("myiban")).thenThrow(e);
		EconomyResponse response = vault.bankHas("myiban", 1.5);
		assertEquals(ResponseType.FAILURE, response.type);
		assertEquals("error", response.errorMessage);
	}
	
	@Test
	public void bankHasTest() {
		BankAccount account = mock(BankAccount.class);
		when(account.getAmount()).thenReturn(5.5);
		assertDoesNotThrow(() -> when(account.hasAmount(1.5)).thenReturn(true));
		assertDoesNotThrow(() -> when(bankManager.getBankAccountByIban("myiban")).thenReturn(account));
		EconomyResponse response = vault.bankHas("myiban", 1.5);
		assertEquals(ResponseType.SUCCESS, response.type);
		assertEquals("5.5", String.valueOf(response.balance));
	}
	
	@Test
	public void bankHasTestNo() {
		BankAccount account = mock(BankAccount.class);
		when(account.getAmount()).thenReturn(5.5);
		assertDoesNotThrow(() -> when(account.hasAmount(1.5)).thenReturn(false));
		assertDoesNotThrow(() -> when(bankManager.getBankAccountByIban("myiban")).thenReturn(account));
		EconomyResponse response = vault.bankHas("myiban", 1.5);
		assertEquals(ResponseType.FAILURE, response.type);
		assertEquals("5.5", String.valueOf(response.balance));
		assertEquals("Bank account has not enough money!", response.errorMessage);
	}
}
