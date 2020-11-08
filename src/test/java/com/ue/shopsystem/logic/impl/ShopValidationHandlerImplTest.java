package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerExceptionMessageEnum;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.shopsystem.logic.api.Playershop;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.townsystem.logic.api.Plot;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.townsystem.logic.api.TownworldManager;
import com.ue.townsystem.logic.impl.TownSystemException;

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
	@Mock
	GeneralEconomyValidationHandler generalValidator;

	@Test
	public void checkForOnePriceGreaterThenZeroIfBothAvailableTest() {
		try {
			validationHandler.checkForOnePriceGreaterThenZeroIfBothAvailable("0", "0");
			fail();
		} catch (ShopSystemException e) {
			assertEquals(ShopExceptionMessageEnum.INVALID_PRICES, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForOnePriceGreaterThenZeroIfBothAvailableTestValid1() {
		assertDoesNotThrow(() -> validationHandler.checkForOnePriceGreaterThenZeroIfBothAvailable("none", "0"));
	}

	@Test
	public void checkForOnePriceGreaterThenZeroIfBothAvailableTestValid2() {
		assertDoesNotThrow(() -> validationHandler.checkForOnePriceGreaterThenZeroIfBothAvailable("0", "none"));
	}

	@Test
	public void checkForOnePriceGreaterThenZeroIfBothAvailableTestValid3() {
		assertDoesNotThrow(() -> validationHandler.checkForOnePriceGreaterThenZeroIfBothAvailable("1", "1"));
	}

	@Test
	public void checkForPricesGreaterThenZeroTest() {
		try {
			validationHandler.checkForPricesGreaterThenZero(0, 0);
			fail();
		} catch (ShopSystemException e) {
			assertEquals(ShopExceptionMessageEnum.INVALID_PRICES, e.getKey());
			assertEquals(0, e.getParams().length);
		}
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
	public void checkForSlotIsNotEmptyTest() {
		try {
			Inventory inv = mock(Inventory.class);
			when(inv.getSize()).thenReturn(9);
			validationHandler.checkForSlotIsNotEmpty(0, inv, 0);
			fail();
		} catch (ShopSystemException | GeneralEconomyException e) {
			assertTrue(e instanceof ShopSystemException);
			ShopSystemException ex = (ShopSystemException) e;
			assertEquals(ShopExceptionMessageEnum.INVENTORY_SLOT_EMPTY, ex.getKey());
			assertEquals(0, ex.getParams().length);
		}
	}

	@Test
	public void checkForSlotIsNotEmptyTestValid() {
		ItemStack stack = mock(ItemStack.class);
		Inventory inv = mock(Inventory.class);
		when(inv.getSize()).thenReturn(9);
		when(inv.getItem(0)).thenReturn(stack);
		assertDoesNotThrow(() -> validationHandler.checkForSlotIsNotEmpty(0, inv, 0));
	}

	@Test
	public void checkForSlotIsEmptyTest() {
		try {
			ItemStack stack = mock(ItemStack.class);
			Inventory inv = mock(Inventory.class);
			when(inv.getSize()).thenReturn(9);
			when(inv.getItem(0)).thenReturn(stack);
			validationHandler.checkForSlotIsEmpty(0, inv, 0);
			fail();
		} catch (GeneralEconomyException | EconomyPlayerException e) {
			assertTrue(e instanceof EconomyPlayerException);
			EconomyPlayerException ex = (EconomyPlayerException) e;
			assertEquals(EconomyPlayerExceptionMessageEnum.INVENTORY_SLOT_OCCUPIED, ex.getKey());
			assertEquals(0, ex.getParams().length);
		}
	}

	@Test
	public void checkForSlotIsEmptyTestValid() {
		Inventory inv = mock(Inventory.class);
		when(inv.getSize()).thenReturn(9);
		assertDoesNotThrow(() -> validationHandler.checkForSlotIsEmpty(0, inv, 0));
	}

	@Test
	public void isSlotEmpty() {
		ItemStack stack1 = mock(ItemStack.class);
		ItemStack stack2 = mock(ItemStack.class);
		when(stack1.getType()).thenReturn(Material.STONE);
		when(stack2.getType()).thenReturn(Material.AIR);
		Inventory inv = mock(Inventory.class);
		when(inv.getSize()).thenReturn(9);
		when(inv.getItem(0)).thenReturn(stack1);
		when(inv.getItem(2)).thenReturn(stack2);
		inv.setItem(0, new ItemStack(Material.STONE));
		inv.setItem(2, new ItemStack(Material.AIR));
		// false false
		assertDoesNotThrow(() -> assertFalse(validationHandler.isSlotEmpty(0, inv, 0)));
		// false true
		assertDoesNotThrow(() -> assertTrue(validationHandler.isSlotEmpty(2, inv, 0)));
		// true false
		assertDoesNotThrow(() -> assertTrue(validationHandler.isSlotEmpty(1, inv, 0)));
		// true true, not possible to produce
	}

	@Test
	public void checkForValidAmountTest1() {
		try {
			validationHandler.checkForValidAmount("70");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals("70", e.getParams()[0]);
		}
	}

	@Test
	public void checkForValidAmountTest2() {
		try {
			validationHandler.checkForValidAmount("-10");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals("-10", e.getParams()[0]);
		}
	}

	@Test
	public void checkForValidAmountTestValid1() {
		assertDoesNotThrow(() -> validationHandler.checkForValidAmount("none"));
	}

	@Test
	public void checkForValidAmountTestValid2() {
		assertDoesNotThrow(() -> validationHandler.checkForValidAmount("20"));
	}

	@Test
	public void checkForValidPriceTest1() {
		try {
			validationHandler.checkForValidPrice("-10");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals("-10", e.getParams()[0]);
		}
	}

	@Test
	public void checkForValidPriceTestValid1() {
		assertDoesNotThrow(() -> validationHandler.checkForValidPrice("none"));
	}

	@Test
	public void checkForValidPriceTestValid2() {
		assertDoesNotThrow(() -> validationHandler.checkForValidPrice("10"));
	}

	@Test
	public void checkForItemCanBeDeletedTest() {
		try {
			validationHandler.checkForItemCanBeDeleted(8, 9);
			fail();
		} catch (ShopSystemException e) {
			assertEquals(ShopExceptionMessageEnum.ITEM_CANNOT_BE_DELETED, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForItemCanBeDeletedTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForItemCanBeDeleted(5, 9));
	}

	@Test
	public void checkForValidStockDecreaseTest1() {
		try {
			validationHandler.checkForValidStockDecrease(10, 20);
			fail();
		} catch (ShopSystemException e) {
			assertEquals(ShopExceptionMessageEnum.ITEM_UNAVAILABLE, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForValidStockDecreaseTest2() {
		try {
			validationHandler.checkForValidStockDecrease(0, 0);
			fail();
		} catch (ShopSystemException e) {
			assertEquals(ShopExceptionMessageEnum.ITEM_UNAVAILABLE, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForValidStockDecreaseTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForValidStockDecrease(10, 5));
	}

	@Test
	public void checkForValidShopNameTest() {
		try {
			validationHandler.checkForValidShopName("invalid_");
			fail();
		} catch (ShopSystemException e) {
			assertEquals(ShopExceptionMessageEnum.INVALID_CHAR_IN_SHOP_NAME, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForValidShopNameTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForValidShopName("valid"));
	}

	@Test
	public void checkForIsRentableTest() {
		try {
			validationHandler.checkForIsRentable(false);
			fail();
		} catch (ShopSystemException e) {
			assertEquals(ShopExceptionMessageEnum.ALREADY_RENTED, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForIsRentableTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForIsRentable(true));
	}

	@Test
	public void checkForResizePossibleTest() {
		try {
			ItemStack stack = mock(ItemStack.class);
			Inventory inv = mock(Inventory.class);
			when(inv.getItem(14)).thenReturn(stack);
			when(inv.getItem(15)).thenReturn(null);
			when(inv.getItem(16)).thenReturn(null);
			validationHandler.checkForResizePossible(inv, 18, 9, 1);
			fail();
		} catch (ShopSystemException | GeneralEconomyException e) {
			assertTrue(e instanceof ShopSystemException);
			ShopSystemException ex = (ShopSystemException) e;
			assertEquals(ShopExceptionMessageEnum.RESIZING_FAILED, ex.getKey());
			assertEquals(0, ex.getParams().length);
		}
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
	public void checkForItemDoesNotExistTest() {
		try {
			ShopItem item = mock(ShopItem.class);
			when(item.getItemHash()).thenReturn(66344345);
			List<ShopItem> items = Arrays.asList(item);
			validationHandler.checkForItemDoesNotExist(66344345, items);
			fail();
		} catch (ShopSystemException e) {
			assertEquals(ShopExceptionMessageEnum.ITEM_ALREADY_EXISTS, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForItemDoesNotExistTestValid() {
		ShopItem item = mock(ShopItem.class);
		when(item.getItemHash()).thenReturn(66345);
		List<ShopItem> items = Arrays.asList(item);
		assertDoesNotThrow(() -> validationHandler.checkForItemDoesNotExist(66344345, items));
	}

	@Test
	public void checkForChangeOwnerIsPossibleTest() {
		try {
			EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
			when(ecoPlayer.getName()).thenReturn("catch441");
			List<String> list = Arrays.asList("myshop_catch441");
			validationHandler.checkForChangeOwnerIsPossible(list, ecoPlayer, "myshop");
			fail();
		} catch (ShopSystemException e) {
			assertEquals(ShopExceptionMessageEnum.SHOP_CHANGEOWNER_ERROR, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForChangeOwnerIsPossibleTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		List<String> list = Arrays.asList("myshop2");
		assertDoesNotThrow(() -> validationHandler.checkForChangeOwnerIsPossible(list, ecoPlayer, "myshop"));
	}

	@Test
	public void checkForShopNameIsFreeTest1() {
		try {
			EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
			when(ecoPlayer.getName()).thenReturn("catch441");
			List<String> list = Arrays.asList("myshop_catch441");
			validationHandler.checkForShopNameIsFree(list, "myshop", ecoPlayer);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals("myshop_catch441", e.getParams()[0]);
		}
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
		try {
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
			validationHandler.checkForPlayerHasPermissionAtLocation(loc, ecoPlayer);
			fail();
		} catch (EconomyPlayerException | TownSystemException e) {
			assertTrue(e instanceof EconomyPlayerException);
			EconomyPlayerException ex = (EconomyPlayerException) e;
			assertEquals(EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION, ex.getKey());
			assertEquals(0, ex.getParams().length);
		}
	}

	@Test
	public void checkForPlayerHasPermissionAtLocationTest2() {
		try {
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
			validationHandler.checkForPlayerHasPermissionAtLocation(loc, ecoPlayer);
			fail();
		} catch (EconomyPlayerException | TownSystemException e) {
			assertTrue(e instanceof EconomyPlayerException);
			EconomyPlayerException ex = (EconomyPlayerException) e;
			assertEquals(EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION, ex.getKey());
			assertEquals(0, ex.getParams().length);
		}
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
		try {
			validationHandler.checkForIsRented(true);
			fail();
		} catch (ShopSystemException e) {
			assertEquals(ShopExceptionMessageEnum.NOT_RENTED, e.getKey());
			assertEquals(0, e.getParams().length);
		}
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
		try {
			validationHandler.checkForPlayerIsOnline(ecoPlayer);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(EconomyPlayerExceptionMessageEnum.NOT_ONLINE, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForShopOwnerHasEnoughMoneyTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayer.hasEnoughtMoney(1)).thenReturn(true));
		assertDoesNotThrow(() -> validationHandler.checkForShopOwnerHasEnoughMoney(ecoPlayer, 1));
	}

	@Test
	public void checkForShopOwnerHasEnoughMoneyTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayer.hasEnoughtMoney(1)).thenReturn(false));
		try {
			validationHandler.checkForShopOwnerHasEnoughMoney(ecoPlayer, 1);
			fail();
		} catch (GeneralEconomyException | ShopSystemException e) {
			assertTrue(e instanceof ShopSystemException);
			ShopSystemException ex = (ShopSystemException) e;
			assertEquals(ShopExceptionMessageEnum.SHOPOWNER_NOT_ENOUGH_MONEY, ex.getKey());
			assertEquals(0, ex.getParams().length);
		}
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
		try {
			validationHandler.checkForPlayerInventoryNotFull(inv);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(EconomyPlayerExceptionMessageEnum.INVENTORY_FULL, e.getKey());
			assertEquals(0, e.getParams().length);
		}
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
		try {
			validationHandler.checkForRenamingSavefileIsPossible(file);
		} catch (ShopSystemException e) {
			assertEquals(ShopExceptionMessageEnum.ERROR_ON_RENAMING, e.getKey());
			assertEquals(0, e.getParams().length);
		}
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
		try {
			validationHandler.checkForMaxPlayershopsForPlayer(list, ecoPlayer);
		} catch (EconomyPlayerException e) {
			assertEquals(EconomyPlayerExceptionMessageEnum.MAX_REACHED, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}
}