package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.bank.logic.api.BankAccount;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.shopsystem.logic.api.Playershop;
import org.ue.shopsystem.logic.api.ShopsystemException;
import org.ue.townsystem.logic.api.Plot;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.Townworld;
import org.ue.townsystem.logic.api.TownworldManager;

@ExtendWith(MockitoExtension.class)
public class ShopValidationHandlerImplTest {

	@InjectMocks
	ShopValidationHandlerImpl validationHandler;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ConfigManager configManager;
	@Mock
	TownworldManager townworldManager;

	@Test
	public void checkForPricesGreaterThenZeroTest() {
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForPricesGreaterThenZero(0, 0));
		assertEquals(ExceptionMessageEnum.INVALID_PRICES, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForPricesGreaterThenZeroTestValid1() {
		assertDoesNotThrow(() -> validationHandler.checkForPricesGreaterThenZero(1, 2));
	}

	@Test
	public void checkForPricesGreaterThenZeroTestValid2() {
		assertDoesNotThrow(() -> validationHandler.checkForPricesGreaterThenZero(1, 0));
	}

	@Test
	public void checkForValidAmountTest1() {
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForValidAmount(70));
		assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		assertEquals(1, e.getParams().length);
		assertEquals(70, e.getParams()[0]);
	}

	@Test
	public void checkForValidAmountTest2() {
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForValidAmount(-10));
		assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		assertEquals(1, e.getParams().length);
		assertEquals(-10, e.getParams()[0]);
	}

	@Test
	public void checkForValidAmountTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForValidAmount(20));
	}

	@Test
	public void checkForItemCanBeDeletedTest() {
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForItemCanBeDeleted(8, 9));
		assertEquals(ExceptionMessageEnum.ITEM_CANNOT_BE_DELETED, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForItemCanBeDeletedTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForItemCanBeDeleted(5, 9));
	}

	@Test
	public void checkForValidStockDecreaseTest1() {
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForValidStockDecrease(10, 20));
		assertEquals(ExceptionMessageEnum.ITEM_UNAVAILABLE, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForValidStockDecreaseTest2() {
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForValidStockDecrease(0, 0));
		assertEquals(ExceptionMessageEnum.ITEM_UNAVAILABLE, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForValidStockDecreaseTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForValidStockDecrease(10, 5));
	}

	@Test
	public void checkForValidShopNameTest() {
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForValidShopName("invalid_"));
		assertEquals(ExceptionMessageEnum.INVALID_CHAR_IN_SHOP_NAME, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForValidShopNameTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForValidShopName("valid"));
	}

	@Test
	public void checkForIsRentableTest() {
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForIsRentable(false));
		assertEquals(ExceptionMessageEnum.ALREADY_RENTED, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForIsRentableTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForIsRentable(true));
	}

	@Test
	public void checkForResizePossibleTest() {
		ItemStack stack = mock(ItemStack.class);
		Inventory inv = mock(Inventory.class);
		when(inv.getItem(14)).thenReturn(stack);
		when(inv.getItem(15)).thenReturn(null);
		when(inv.getItem(16)).thenReturn(null);
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForResizePossible(inv, 18, 9, 1));
		assertEquals(ExceptionMessageEnum.RESIZING_FAILED, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForResizePossibleTestValid1() {
		Inventory inv = mock(Inventory.class);
		assertDoesNotThrow(() -> validationHandler.checkForResizePossible(inv, 18, 9, 1));
	}

	@Test
	public void checkForResizePossibleTestValid2() {
		Inventory inv = mock(Inventory.class);
		assertDoesNotThrow(() -> validationHandler.checkForResizePossible(inv, 9, 18, 1));
	}

	@Test
	public void checkForChangeOwnerIsPossibleTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		List<String> list = Arrays.asList("myshop_catch441");
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForChangeOwnerIsPossible(list, ecoPlayer, "myshop"));
		assertEquals(ExceptionMessageEnum.SHOP_CHANGEOWNER_ERROR, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForChangeOwnerIsPossibleTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		List<String> list = Arrays.asList("myshop2");
		assertDoesNotThrow(() -> validationHandler.checkForChangeOwnerIsPossible(list, ecoPlayer, "myshop"));
	}

	@Test
	public void checkForShopNameIsFreeTest1() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		List<String> list = Arrays.asList("myshop_catch441");
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForShopNameIsFree(list, "myshop", ecoPlayer));
		assertEquals(ExceptionMessageEnum.ALREADY_EXISTS, e.getKey());
		assertEquals(1, e.getParams().length);
		assertEquals("myshop_catch441", e.getParams()[0]);
	}

	@Test
	public void checkForShopNameIsFreeTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		List<String> list = Arrays.asList("myshop");
		assertDoesNotThrow(() -> validationHandler.checkForShopNameIsFree(list, "myshop_catch441", ecoPlayer));
	}

	@Test
	public void checkForPlayerHasPermissionAtLocationTest1() {
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Chunk chunk = mock(Chunk.class);
		Townworld townworld = mock(Townworld.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(world.getName()).thenReturn("myWorld");
		when(townworld.isChunkFree(chunk)).thenReturn(true);
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("myWorld")).thenReturn(townworld));
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForPlayerHasPermissionAtLocation(loc, ecoPlayer));
		assertEquals(ExceptionMessageEnum.YOU_HAVE_NO_PERMISSION, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForPlayerHasPermissionAtLocationTest3() throws TownsystemException {
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		doThrow(TownsystemException.class).when(townworldManager).getTownWorldByName("world");
		assertDoesNotThrow(() -> validationHandler.checkForPlayerHasPermissionAtLocation(loc, null));
	}

	@Test
	public void checkForPlayerHasPermissionAtLocationTest2() {
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Chunk chunk = mock(Chunk.class);
		Townworld townworld = mock(Townworld.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Plot plot = mock(Plot.class);
		when(chunk.getX()).thenReturn(10);
		when(chunk.getZ()).thenReturn(15);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("myWorld");
		when(loc.getChunk()).thenReturn(chunk);
		when(townworld.isChunkFree(chunk)).thenReturn(false);
		when(town.hasBuildPermissions(ecoPlayer, plot)).thenReturn(false);
		assertDoesNotThrow(() -> when(town.getPlotByChunk("10/15")).thenReturn(plot));
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("myWorld")).thenReturn(townworld));
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForPlayerHasPermissionAtLocation(loc, ecoPlayer));
		assertEquals(ExceptionMessageEnum.YOU_HAVE_NO_PERMISSION, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForPlayerHasPermissionAtLocationTestValid() {
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Chunk chunk = mock(Chunk.class);
		Townworld townworld = mock(Townworld.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Plot plot = mock(Plot.class);
		when(chunk.getX()).thenReturn(10);
		when(chunk.getZ()).thenReturn(15);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("myWorld");
		when(loc.getChunk()).thenReturn(chunk);
		when(townworld.isChunkFree(chunk)).thenReturn(false);
		when(town.hasBuildPermissions(ecoPlayer, plot)).thenReturn(true);
		assertDoesNotThrow(() -> when(town.getPlotByChunk("10/15")).thenReturn(plot));
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("myWorld")).thenReturn(townworld));
		assertDoesNotThrow(() -> validationHandler.checkForPlayerHasPermissionAtLocation(loc, ecoPlayer));
	}

	@Test
	public void checkForIsRentedTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForIsRented(false));
	}

	@Test
	public void checkForIsRentedTest() {
		ShopsystemException e = assertThrows(ShopsystemException.class, () -> validationHandler.checkForIsRented(true));
		assertEquals(ExceptionMessageEnum.NOT_RENTED, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForPlayerIsOnlineTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.isOnline()).thenReturn(true);
		assertDoesNotThrow(() -> validationHandler.checkForPlayerIsOnline(ecoPlayer));
	}

	@Test
	public void checkForPlayerIsOnlineTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.isOnline()).thenReturn(false);
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForPlayerIsOnline(ecoPlayer));
		assertEquals(ExceptionMessageEnum.NOT_ONLINE, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForShopOwnerHasEnoughMoneyTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(1.0);
		assertDoesNotThrow(() -> validationHandler.checkForShopOwnerHasEnoughMoney(ecoPlayer, 1));
	}

	@Test
	public void checkForShopOwnerHasEnoughMoneyTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(1.0);
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForShopOwnerHasEnoughMoney(ecoPlayer, 10));
		assertEquals(ExceptionMessageEnum.SHOPOWNER_NOT_ENOUGH_MONEY, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForPlayerInventoryNotFullTestValid() {
		Inventory inv = mock(Inventory.class);
		when(inv.firstEmpty()).thenReturn(4);
		assertDoesNotThrow(() -> validationHandler.checkForPlayerInventoryNotFull(inv));
	}

	@Test
	public void checkForPlayerInventoryNotFullTest() {
		Inventory inv = mock(Inventory.class);
		when(inv.firstEmpty()).thenReturn(-1);
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForPlayerInventoryNotFull(inv));
		assertEquals(ExceptionMessageEnum.INVENTORY_FULL, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForRenamingSavefileIsPossibleTestValid() {
		File file = mock(File.class);
		when(file.exists()).thenReturn(false);
		assertDoesNotThrow(() -> validationHandler.checkForRenamingSavefileIsPossible(file));
	}

	@Test
	public void checkForRenamingSavefileIsPossibleTest() {
		File file = mock(File.class);
		when(file.exists()).thenReturn(true);
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForRenamingSavefileIsPossible(file));
		assertEquals(ExceptionMessageEnum.ERROR_ON_RENAMING, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void checkForMaxPlayershopsForPlayerTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Playershop shop = mock(Playershop.class);
		when(shop.isOwner(ecoPlayer)).thenReturn(false);
		when(configManager.getMaxPlayershops()).thenReturn(1);
		List<Playershop> list = Arrays.asList(shop);
		assertDoesNotThrow(() -> validationHandler.checkForMaxPlayershopsForPlayer(list, ecoPlayer));
	}

	@Test
	public void checkForMaxPlayershopsForPlayerTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Playershop shop = mock(Playershop.class);
		when(shop.isOwner(ecoPlayer)).thenReturn(true);
		when(configManager.getMaxPlayershops()).thenReturn(1);
		List<Playershop> list = Arrays.asList(shop);
		ShopsystemException e = assertThrows(ShopsystemException.class,
				() -> validationHandler.checkForMaxPlayershopsForPlayer(list, ecoPlayer));
		assertEquals(ExceptionMessageEnum.MAX_REACHED, e.getKey());
		assertEquals(0, e.getParams().length);
	}
}