package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.common.utils.ComponentProvider;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.ServiceComponent;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.Playershop;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.impl.AbstractShopImpl;
import com.ue.shopsystem.logic.impl.PlayershopImpl;
import com.ue.shopsystem.logic.impl.ShopSystemException;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.TownworldManager;
import com.ue.townsystem.logic.impl.TownSystemException;
import com.ue.townsystem.logic.impl.TownworldManagerImpl;
import com.ue.ultimate_economy.EconomyVillager;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

@ExtendWith(MockitoExtension.class)
public class PlayershopImplTest {

	@Mock
	ServerProvider serverProvider;
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
	CustomSkullService customSkullService;
	@Mock
	ConfigManager configManager;
	@Mock
	ShopDao shopDao;
	@Mock
	PlayershopManager playershopManager;
	@Mock
	TownworldManager townworldManager;

	@Test
	public void newConstructorTest() {
		Plugin plugin = mock(Plugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory invStock = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Stock"))).thenReturn(invStock);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(customSkullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		Playershop shop = new PlayershopImpl("myshop", ecoPlayer, "P0", loc, 9, shopDao, serverProvider, customSkullService,
				logger, validationHandler, null, messageWrapper, configManager, townworldManager, playershopManager);
		
		// TODO
	}

	@Test
	public void loadConstructorTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			EconomyPlayer owner = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.STONE));
			shop.increaseStock(0, 10);
			Playershop loaded = new PlayershopImpl(null, "P0");
			assertEquals(1, loaded.getItemList().size());
			assertEquals(10, loaded.getShopItem(0).getStock());
			assertEquals(Material.STONE, loaded.getShopItem(0).getItemStack().getType());
			assertEquals("2.0", String.valueOf(loaded.getShopItem(0).getBuyPrice()));
			assertEquals("1.0", String.valueOf(loaded.getShopItem(0).getSellPrice()));
			assertEquals(owner, loaded.getOwner());
			assertEquals("myshop_catch441", loaded.getShopVillager().getCustomName());
			assertEquals(EconomyVillager.PLAYERSHOP, loaded.getShopVillager().getMetadata("ue-type").get(0).value());
			// check shop inventory
			ChestInventoryMock shopInv = (ChestInventoryMock) loaded.getShopInventory();
			assertEquals(9, shopInv.getSize());
			assertEquals("myshop", shopInv.getName());
			assertEquals(Material.CRAFTING_TABLE, shopInv.getItem(7).getType());
			assertEquals("Stock", shopInv.getItem(7).getItemMeta().getDisplayName());
			assertEquals(2, shopInv.getItem(7).getItemMeta().getLore().size());
			assertEquals(ChatColor.RED + "Only for Shopowner", shopInv.getItem(7).getItemMeta().getLore().get(0));
			assertEquals(ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "open/close stockpile",
					shopInv.getItem(7).getItemMeta().getLore().get(1));
			assertEquals(Material.ANVIL, shopInv.getItem(8).getType());
			assertEquals("Info", shopInv.getItem(8).getItemMeta().getDisplayName());
			assertEquals("§6Rightclick: §asell specified amount", shopInv.getItem(8).getItemMeta().getLore().get(0));
			assertEquals("§6Shift-Rightclick: §asell all", shopInv.getItem(8).getItemMeta().getLore().get(1));
			assertEquals("§6Leftclick: §abuy", shopInv.getItem(8).getItemMeta().getLore().get(2));
			// check editor inventory
			loaded.openEditor(owner.getPlayer());
			ChestInventoryMock editor = (ChestInventoryMock) owner.getPlayer().getOpenInventory().getTopInventory();
			owner.getPlayer().closeInventory();
			assertEquals(loaded.getShopVillager(), editor.getHolder());
			assertEquals(9, editor.getSize());
			assertEquals("myshop-Editor", editor.getName());
			assertNull(editor.getItem(7));
			assertNull(editor.getItem(8));
			// check stock inventory
			ChestInventoryMock stock = (ChestInventoryMock) loaded.getStockpileInventory();
			assertEquals(loaded.getShopVillager(), stock.getHolder());
			assertEquals(9, stock.getSize());
			assertEquals("myshop-Stock", stock.getName());
			assertEquals(Material.STONE, stock.getItem(0).getType());
			assertEquals("§a10§6 Items", stock.getItem(0).getItemMeta().getLore().get(0));
			assertNull(stock.getItem(1));
			assertNull(stock.getItem(2));
			assertNull(stock.getItem(3));
			assertNull(stock.getItem(4));
			assertNull(stock.getItem(5));
			assertNull(stock.getItem(6));
			assertNull(stock.getItem(7));
			assertEquals("Infos", stock.getItem(8).getItemMeta().getDisplayName());
			assertEquals(ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "close stockpile",
					stock.getItem(8).getItemMeta().getLore().get(0));
			assertEquals(ChatColor.GOLD + "Rightclick: " + ChatColor.GREEN + "add specified amount",
					stock.getItem(8).getItemMeta().getLore().get(1));
			assertEquals(ChatColor.GOLD + "Shift-Rightclick: " + ChatColor.GREEN + "add all",
					stock.getItem(8).getItemMeta().getLore().get(2));
			assertEquals(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "get specified amount",
					stock.getItem(8).getItemMeta().getLore().get(3));
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	private PlayershopImpl createPlayershop() {
		Plugin plugin = mock(Plugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory invStock = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Stock"))).thenReturn(invStock);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(customSkullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		return new PlayershopImpl("myshop", ecoPlayer, "P0", loc, 9, shopDao, serverProvider, customSkullService,
				logger, validationHandler, null, messageWrapper, configManager, townworldManager, playershopManager);
	}

	@Test
	public void buyShopItemTestWithTooSmallStock() throws ShopSystemException {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> shop.addShopItem(0, 1, 2, stack));
		doThrow(ShopSystemException.class).when(validationHandler).checkForValidStockDecrease(0, 1);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);

		assertThrows(ShopSystemException.class, () -> shop.buyShopItem(0, ecoPlayer, true));

		verify(player, never()).sendMessage(anyString());
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithInvalidSlot() throws GeneralEconomyException {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> shop.addShopItem(0, 1, 2, stack));
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidSlot(9, 9, 2);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertThrows(GeneralEconomyException.class, () -> shop.buyShopItem(9, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithEmptySlot() throws GeneralEconomyException, ShopSystemException {
		Playershop shop = createPlayershop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForSlotIsNotEmpty(3,
				shop.getShopInventory(), 2);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertThrows(GeneralEconomyException.class, () -> shop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithOfflinePlayer() throws EconomyPlayerException {
		Playershop shop = createPlayershop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerIsOnline(ecoPlayer);
		assertThrows(EconomyPlayerException.class, () -> shop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithFullInventory() throws EconomyPlayerException {
		Playershop shop = createPlayershop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerInventoryNotFull(inv);
		assertThrows(EconomyPlayerException.class, () -> shop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithSingular() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> shop.increaseStock(3, 1));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(configManager.getCurrencyText(2.0)).thenReturn("$");
		when(messageWrapper.getString("shop_buy_singular", "1", 2.0, "$")).thenReturn("my message");

		assertDoesNotThrow(() -> shop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidStockDecrease(1, 1));
		assertDoesNotThrow(() -> verify(validationHandler, times(3)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsNotEmpty(3, shop.getShopInventory(), 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(1);
		verify(inv).addItem(stackCloneClone);
		verify(shopDao, times(2)).saveStock("item string", 0);
		verify(player).sendMessage("my message");
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(2.0, true));
		assertDoesNotThrow(() -> verify(shop.getOwner()).increasePlayerAmount(2.0, false));
		assertDoesNotThrow(() -> assertEquals(0, shop.getShopItem(3).getStock()));
	}

	@Test
	public void buyShopItemTestWithPlural() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 4, stack));
		assertDoesNotThrow(() -> shop.increaseStock(3, 2));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(configManager.getCurrencyText(4.0)).thenReturn("$");
		when(messageWrapper.getString("shop_buy_plural", "2", 4.0, "$")).thenReturn("my message");

		assertDoesNotThrow(() -> shop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidStockDecrease(2, 2));
		assertDoesNotThrow(() -> verify(validationHandler, times(3)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsNotEmpty(3, shop.getShopInventory(), 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(2);
		verify(inv).addItem(stackCloneClone);
		verify(shopDao, times(2)).saveStock("item string", 0);
		verify(player).sendMessage("my message");
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(4.0, true));
		assertDoesNotThrow(() -> verify(shop.getOwner()).increasePlayerAmount(4.0, false));
		assertDoesNotThrow(() -> assertEquals(0, shop.getShopItem(3).getStock()));
	}

	@Test
	public void buyShopItemTestWithNoBuyPrice() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 0, stack));
		assertDoesNotThrow(() -> shop.increaseStock(3, 2));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);

		assertDoesNotThrow(() -> shop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, never()).checkForValidStockDecrease(anyInt(), anyInt()));
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsNotEmpty(3, shop.getShopInventory(), 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		// only at setup
		verify(shopDao, times(1)).saveStock("item string", 0);

		verify(player, never()).sendMessage(anyString());
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
		assertDoesNotThrow(() -> verify(shop.getOwner(), never()).increasePlayerAmount(anyDouble(), eq(true)));
		assertDoesNotThrow(() -> assertEquals(2, shop.getShopItem(3).getStock()));
	}

	@Test
	public void buyShopItemTestWithSingularAsOwner() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> shop.increaseStock(3, 1));
		when(shop.getOwner().getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(messageWrapper.getString("shop_got_item_singular", "1")).thenReturn("my message");

		assertDoesNotThrow(() -> shop.buyShopItem(3, shop.getOwner(), true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidStockDecrease(1, 1));
		assertDoesNotThrow(() -> verify(validationHandler, times(3)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(shop.getOwner()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsNotEmpty(3, shop.getShopInventory(), 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(1);
		verify(inv).addItem(stackCloneClone);
		verify(shopDao, times(2)).saveStock("item string", 0);
		verify(player).sendMessage("my message");
		assertDoesNotThrow(() -> verify(shop.getOwner(), never()).increasePlayerAmount(2.0, false));
		assertDoesNotThrow(() -> assertEquals(0, shop.getShopItem(3).getStock()));
	}

	@Test
	public void buyShopItemTestWithPluralAsOwner() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> shop.increaseStock(3, 3));
		when(shop.getOwner().getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(messageWrapper.getString("shop_got_item_plural", "2")).thenReturn("my message");

		assertDoesNotThrow(() -> shop.buyShopItem(3, shop.getOwner(), true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidStockDecrease(3, 2));
		assertDoesNotThrow(() -> verify(validationHandler, times(3)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(shop.getOwner()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsNotEmpty(3, shop.getShopInventory(), 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(2);
		verify(inv).addItem(stackCloneClone);
		verify(shopDao, times(1)).saveStock("item string", 1);
		verify(player).sendMessage("my message");
		assertDoesNotThrow(() -> verify(shop.getOwner(), never()).increasePlayerAmount(2.0, false));
		assertDoesNotThrow(() -> assertEquals(1, shop.getShopItem(3).getStock()));
	}

	@Test
	public void buyShopItemTestWithNotEnoughMoney() throws GeneralEconomyException, EconomyPlayerException {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> shop.increaseStock(3, 1));
		reset(shopDao);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		doThrow(EconomyPlayerException.class).when(ecoPlayer).decreasePlayerAmount(2.0, true);

		assertThrows(EconomyPlayerException.class, () -> shop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(1)).checkForValidStockDecrease(1, 1));
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsNotEmpty(3, shop.getShopInventory(), 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone, never()).setAmount(anyInt());
		verify(shopDao, never()).saveStock("item string", 0);
		verify(player, never()).sendMessage(anyString());
		assertDoesNotThrow(() -> verify(shop.getOwner(), never()).increasePlayerAmount(2.0, false));
		assertDoesNotThrow(() -> assertEquals(1, shop.getShopItem(3).getStock()));
	}

	@Test
	public void sellShopItemTestWithInvalidSlot() throws GeneralEconomyException {
		Playershop shop = createPlayershop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidSlot(-1, 9, 2);
		assertThrows(GeneralEconomyException.class, () -> shop.sellShopItem(-1, 1, null, true));
	}

	@Test
	public void sellShopItemTestWithEmptySlot() throws GeneralEconomyException, ShopSystemException {
		Playershop shop = createPlayershop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForSlotIsNotEmpty(1,
				shop.getShopInventory(), 2);
		assertThrows(GeneralEconomyException.class, () -> shop.sellShopItem(1, 1, null, true));
	}

	@Test
	public void sellShopItemTestWithOfflinePlayer() throws EconomyPlayerException {
		Playershop shop = createPlayershop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerIsOnline(ecoPlayer);
		assertThrows(EconomyPlayerException.class, () -> shop.sellShopItem(1, 1, ecoPlayer, true));
	}

	@Test
	public void sellShopItemTestWithOwnerNotEnoughMoney() throws GeneralEconomyException, ShopSystemException {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));
		doThrow(ShopSystemException.class).when(validationHandler).checkForShopOwnerHasEnoughMoney(shop.getOwner(),
				1.0);

		assertThrows(ShopSystemException.class, () -> shop.sellShopItem(3, 1, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(1)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsNotEmpty(3, shop.getShopInventory(), 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler).checkForShopOwnerHasEnoughMoney(shop.getOwner(), 1.0));
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(1.0, false));
		assertDoesNotThrow(() -> verify(shop.getOwner(), never()).decreasePlayerAmount(1.0, true));
		assertDoesNotThrow(() -> assertEquals(0, shop.getShopItem(3).getStock()));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void sellShopItemTestWithSingular() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemStack contentStack = mock(ItemStack.class);
		ItemStack contentStackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		ItemStack[] contents = new ItemStack[1];
		contents[0] = contentStack;
		when(contentStackClone.getAmount()).thenReturn(10);
		when(inv.getStorageContents()).thenReturn(contents);
		when(player.getInventory()).thenReturn(inv);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(contentStack.clone()).thenReturn(contentStackClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));
		when(stackCloneClone.isSimilar(contentStackClone)).thenReturn(true);
		when(configManager.getCurrencyText(1.0)).thenReturn("$");
		when(messageWrapper.getString("shop_sell_singular", "1", 1.0, "$")).thenReturn("my message");

		assertDoesNotThrow(() -> shop.sellShopItem(3, 1, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsNotEmpty(3, shop.getShopInventory(), 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler).checkForShopOwnerHasEnoughMoney(shop.getOwner(), 1.0));
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(1.0, false));
		assertDoesNotThrow(() -> verify(shop.getOwner()).decreasePlayerAmount(1.0, true));
		assertDoesNotThrow(() -> assertEquals(1, shop.getShopItem(3).getStock()));
		verify(inv).removeItem(contentStackClone);
		verify(player).sendMessage("my message");
	}

	@Test
	public void sellShopItemTestOnlyBuyPrice() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 0, 2, stack));

		assertDoesNotThrow(() -> shop.sellShopItem(3, 1, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(1)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsNotEmpty(3, shop.getShopInventory(), 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler, never()).checkForShopOwnerHasEnoughMoney(eq(shop.getOwner()),
				anyDouble()));
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(anyDouble(), eq(false)));
		assertDoesNotThrow(() -> verify(shop.getOwner(), never()).decreasePlayerAmount(anyDouble(), eq(true)));
		assertDoesNotThrow(() -> assertEquals(0, shop.getShopItem(3).getStock()));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void sellShopItemTestWithPlural() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemStack contentStack = mock(ItemStack.class);
		ItemStack contentStackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		ItemStack[] contents = new ItemStack[1];
		contents[0] = contentStack;
		when(contentStackClone.getAmount()).thenReturn(10);
		when(inv.getStorageContents()).thenReturn(contents);
		when(player.getInventory()).thenReturn(inv);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(contentStack.clone()).thenReturn(contentStackClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));
		when(stackCloneClone.isSimilar(contentStackClone)).thenReturn(true);
		when(configManager.getCurrencyText(10.0)).thenReturn("$");
		when(messageWrapper.getString("shop_sell_plural", "10", 10.0, "$")).thenReturn("my message");

		assertDoesNotThrow(() -> shop.sellShopItem(3, 10, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsNotEmpty(3, shop.getShopInventory(), 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler).checkForShopOwnerHasEnoughMoney(shop.getOwner(), 10.0));
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(10.0, false));
		assertDoesNotThrow(() -> verify(shop.getOwner()).decreasePlayerAmount(10.0, true));
		assertDoesNotThrow(() -> assertEquals(10, shop.getShopItem(3).getStock()));
		verify(inv).removeItem(contentStackClone);
		verify(player).sendMessage("my message");
	}

	@Test
	public void sellShopItemTestWithSingularAsOwner() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemStack contentStack = mock(ItemStack.class);
		ItemStack contentStackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		ItemStack[] contents = new ItemStack[1];
		contents[0] = contentStack;
		when(contentStackClone.getAmount()).thenReturn(10);
		when(inv.getStorageContents()).thenReturn(contents);
		when(player.getInventory()).thenReturn(inv);
		when(shop.getOwner().getPlayer()).thenReturn(player);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(contentStack.clone()).thenReturn(contentStackClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));
		when(stackCloneClone.isSimilar(contentStackClone)).thenReturn(true);
		when(messageWrapper.getString("shop_added_item_singular", "1")).thenReturn("my message");

		assertDoesNotThrow(() -> shop.sellShopItem(3, 1, shop.getOwner(), true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsNotEmpty(3, shop.getShopInventory(), 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(shop.getOwner()));
		assertDoesNotThrow(() -> verify(shop.getOwner(), never()).increasePlayerAmount(1.0, false));
		assertDoesNotThrow(() -> verify(shop.getOwner(), never()).decreasePlayerAmount(1.0, true));
		assertDoesNotThrow(() -> assertEquals(1, shop.getShopItem(3).getStock()));
		verify(inv).removeItem(contentStackClone);
		verify(player).sendMessage("my message");
	}

	@Test
	public void sellShopItemTestWithPluralAsOwner() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemStack contentStack = mock(ItemStack.class);
		ItemStack contentStackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		ItemStack[] contents = new ItemStack[1];
		contents[0] = contentStack;
		when(contentStackClone.getAmount()).thenReturn(10);
		when(inv.getStorageContents()).thenReturn(contents);
		when(player.getInventory()).thenReturn(inv);
		when(shop.getOwner().getPlayer()).thenReturn(player);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(contentStack.clone()).thenReturn(contentStackClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));
		when(stackCloneClone.isSimilar(contentStackClone)).thenReturn(true);
		when(messageWrapper.getString("shop_added_item_plural", "10")).thenReturn("my message");

		assertDoesNotThrow(() -> shop.sellShopItem(3, 10, shop.getOwner(), true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsNotEmpty(3, shop.getShopInventory(), 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(shop.getOwner()));
		assertDoesNotThrow(() -> verify(shop.getOwner(), never()).increasePlayerAmount(10.0, false));
		assertDoesNotThrow(() -> verify(shop.getOwner(), never()).decreasePlayerAmount(10.0, true));
		assertDoesNotThrow(() -> assertEquals(10, shop.getShopItem(3).getStock()));
		verify(inv).removeItem(contentStackClone);
		verify(player).sendMessage("my message");
	}

	@Test
	public void isOwnerTest() {
		Playershop shop = createPlayershop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertTrue(shop.isOwner(shop.getOwner()));
		assertFalse(shop.isOwner(ecoPlayer));
	}

	@Test
	public void increaseStockTest() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack shopInvStack = mock(ItemStack.class);
		ItemStack shopInvStackClone = mock(ItemStack.class);
		ItemMeta shopInvStackMeta = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(shopInvStackClone.getItemMeta()).thenReturn(shopInvStackMeta);
		when(shop.getShopInventory().getItem(3)).thenReturn(shopInvStack);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(shopInvStack.clone()).thenReturn(shopInvStackClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));

		assertDoesNotThrow(() -> shop.increaseStock(3, 1));

		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidSlot(3, 9, 2));
		verify(shopDao).saveStock("item string", 1);
		verify(shopInvStackMeta).setLore(Arrays.asList("§a1§6 Item"));
		assertDoesNotThrow(() -> verify(shop.getStockpileInventory(), times(2)).setItem(3, shopInvStackClone));
	}

	@Test
	public void increaseStockTestWithInvalidStock() throws GeneralEconomyException {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack shopInvStack = mock(ItemStack.class);
		ItemStack shopInvStackClone = mock(ItemStack.class);
		ItemMeta shopInvStackMeta = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(shopInvStackClone.getItemMeta()).thenReturn(shopInvStackMeta);
		when(shop.getShopInventory().getItem(3)).thenReturn(shopInvStack);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(shopInvStack.clone()).thenReturn(shopInvStackClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));
		reset(shopDao);
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForPositiveValue(-10);
		assertThrows(GeneralEconomyException.class, () -> shop.increaseStock(3, -10));

		verify(shopDao, never()).saveStock(anyString(), anyInt());
	}

	@Test
	public void decreaseStockTest() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack shopInvStack = mock(ItemStack.class);
		ItemStack shopInvStackClone = mock(ItemStack.class);
		ItemMeta shopInvStackMeta = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(shopInvStackClone.getItemMeta()).thenReturn(shopInvStackMeta);
		when(shop.getShopInventory().getItem(3)).thenReturn(shopInvStack);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(shopInvStack.clone()).thenReturn(shopInvStackClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));

		assertDoesNotThrow(() -> shop.increaseStock(3, 10));
		assertDoesNotThrow(() -> shop.decreaseStock(3, 5));

		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(5));
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidStockDecrease(10, 5));
		verify(shopDao).saveStock("item string", 5);
		verify(shopInvStackMeta).setLore(Arrays.asList("§a5§6 Items"));
		assertDoesNotThrow(() -> assertEquals(5, shop.getShopItem(3).getStock()));
	}

	@Test
	public void decreaseStockTestWithToSingular() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack shopInvStack = mock(ItemStack.class);
		ItemStack shopInvStackClone = mock(ItemStack.class);
		ItemMeta shopInvStackMeta = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(shopInvStackClone.getItemMeta()).thenReturn(shopInvStackMeta);
		when(shop.getShopInventory().getItem(3)).thenReturn(shopInvStack);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(shopInvStack.clone()).thenReturn(shopInvStackClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));

		assertDoesNotThrow(() -> shop.increaseStock(3, 10));
		assertDoesNotThrow(() -> shop.decreaseStock(3, 9));

		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(9));
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidStockDecrease(10, 9));
		verify(shopDao).saveStock("item string", 1);
		verify(shopInvStackMeta).setLore(Arrays.asList("§a1§6 Item"));
		assertDoesNotThrow(() -> assertEquals(1, shop.getShopItem(3).getStock()));
	}

	@Test
	public void decreaseStockTestWithInvalidStock() throws GeneralEconomyException {
		Playershop shop = createPlayershop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForPositiveValue(-10);
		assertThrows(GeneralEconomyException.class, () -> shop.decreaseStock(0, -10));
		verify(shopDao, never()).saveStock(anyString(), anyInt());
	}

	@Test
	public void decreaseStockTestWithInvalidSlot() throws GeneralEconomyException {
		Playershop shop = createPlayershop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidSlot(-10, 9, 2);
		assertThrows(GeneralEconomyException.class, () -> shop.decreaseStock(-10, 1));
		verify(shopDao, never()).saveStock(anyString(), anyInt());
	}

	@Test
	public void isAvailableTest() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack shopInvStack = mock(ItemStack.class);
		ItemStack shopInvStackClone = mock(ItemStack.class);
		ItemMeta shopInvStackMeta = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(shopInvStackClone.getItemMeta()).thenReturn(shopInvStackMeta);
		when(shop.getShopInventory().getItem(3)).thenReturn(shopInvStack);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(shopInvStack.clone()).thenReturn(shopInvStackClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> shop.addShopItem(4, 1, 2, stack));

		assertDoesNotThrow(() -> shop.increaseStock(3, 1));

		assertDoesNotThrow(() -> assertTrue(shop.isAvailable(3)));
		assertDoesNotThrow(() -> assertFalse(shop.isAvailable(4)));
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 9, 2));
	}

	@Test
	public void isAvailableTestWithInvalidSlot() throws GeneralEconomyException {
		Playershop shop = createPlayershop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidSlot(8, 9, 2);
		assertThrows(GeneralEconomyException.class, () -> assertTrue(shop.isAvailable(8)));
	}

	@Test
	public void changeOwnerTest() {
		Playershop shop = createPlayershop();
		EconomyPlayer newOwner = mock(EconomyPlayer.class);
		when(newOwner.getName()).thenReturn("wejink");
		when(playershopManager.getPlayerShopUniqueNameList()).thenReturn(new ArrayList<>());
		assertDoesNotThrow(() -> shop.changeOwner(newOwner));
		assertEquals(newOwner, shop.getOwner());
		verify(shopDao).saveOwner(newOwner);
		verify(shop.getShopVillager()).setCustomName("myshop_wejink");
		assertDoesNotThrow(
				() -> verify(validationHandler).checkForChangeOwnerIsPossible(new ArrayList<>(), newOwner, "myshop"));
	}

	@Test
	public void changeOwnerTestNoPossible() throws ShopSystemException {
		Playershop shop = createPlayershop();
		EconomyPlayer newOwner = mock(EconomyPlayer.class);
		doThrow(ShopSystemException.class).when(validationHandler).checkForChangeOwnerIsPossible(new ArrayList<>(),
				newOwner, "myshop");
		assertThrows(ShopSystemException.class, () -> shop.changeOwner(newOwner));
		assertFalse(newOwner.equals(shop.getOwner()));
		verify(shopDao, never()).saveOwner(newOwner);
	}

	@Test
	public void addItemTest() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		ItemMeta stackMetaCloneClone = mock(ItemMeta.class);
		when(shop.getShopInventory().getItem(0)).thenReturn(stackClone);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stackCloneClone.getItemMeta()).thenReturn(stackMetaCloneClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> shop.addShopItem(0, 1, 4, stack));
		verify(shopDao).saveStock("item string", 0);
		verify(shopDao).saveItemNames(Arrays.asList("item string"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForItemDoesNotExist(eq("item string"), anyList()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsEmpty(0, shop.getShopInventory(), 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidPrice("1.0"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidPrice("4.0"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(1.0, 4.0));
		assertDoesNotThrow(() -> assertEquals(1, shop.getItemList().size()));
		ShopItem shopItem = assertDoesNotThrow(() -> shop.getShopItem(0));
		verify(shopDao).saveShopItem(shopItem, false);
		assertEquals(2, shopItem.getAmount());
		assertEquals(0, shopItem.getStock());
		assertEquals(4.0, shopItem.getBuyPrice());
		assertEquals(1.0, shopItem.getSellPrice());
		assertEquals(0, shopItem.getSlot());
		assertEquals("item string", shopItem.getItemString());
		assertEquals(stackClone, shopItem.getItemStack());
		// verify that the set occupied method of the editor is called
		verify(customSkullService).getSkullWithName("SLOTFILLED", "Slot 1");
		verify(shop.getShopInventory()).setItem(0, stackClone);
		verify(stackClone).setAmount(2);
		verify(stackMetaClone).setLore(Arrays.asList("§62 buy for §a4.0 $", "§62 sell for §a1.0 $"));
	}

	@Test
	public void changeSizeTest() {
		Playershop shop = createPlayershop();

		assertDoesNotThrow(() -> shop.changeShopSize(18));

		assertDoesNotThrow(() -> verify(validationHandler).checkForValidSize(18));
		assertDoesNotThrow(() -> verify(validationHandler).checkForResizePossible(shop.getShopInventory(), 9, 18, 2));
		assertEquals(18, shop.getSize());
		verify(shopDao).saveShopSize(18);
		verify(serverProvider).createInventory(shop.getShopVillager(), 18, "myshop");
		verify(serverProvider).createInventory(shop.getShopVillager(), 18, "myshop-Stock");
		verify(serverProvider).createInventory(shop.getShopVillager(), 18, "myshop-Editor");
	}

	@Test
	public void changeSizeTestWithResizeNotPossible() throws ShopSystemException, GeneralEconomyException {
		Playershop shop = createPlayershop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForResizePossible(shop.getShopInventory(), 9,
				18, 2);
		assertThrows(ShopSystemException.class, () -> shop.changeShopSize(18));
		assertEquals(9, shop.getSize());
	}

	@Test
	public void openStockpileTest() {
		Playershop shop = createPlayershop();
		Player player = mock(Player.class);
		assertDoesNotThrow(() -> shop.openStockpile(player));
		assertDoesNotThrow(() -> verify(player).openInventory(shop.getStockpileInventory()));
	}

	@Test
	public void moveShopTest() {
		Playershop shop = createPlayershop();
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(world.getName()).thenReturn("world");
		when(loc.getWorld()).thenReturn(world);
		when(townworldManager.isTownWorld("world")).thenReturn(false);

		assertDoesNotThrow(() -> shop.moveShop(loc));

		assertEquals(loc, shop.getShopLocation());
		verify(shopDao).saveShopLocation(loc);
		verify(shop.getShopVillager()).teleport(loc);
	}

	@Test
	public void moveShopTestWithNoPlotPermission() throws EconomyPlayerException, TownSystemException {
		Playershop shop = createPlayershop();
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(world.getName()).thenReturn("world");
		when(loc.getWorld()).thenReturn(world);
		when(townworldManager.isTownWorld("world")).thenReturn(true);
		doThrow(TownSystemException.class).when(validationHandler).checkForPlayerHasPermissionAtLocation(loc,
				shop.getOwner());

		assertThrows(TownSystemException.class, () -> shop.moveShop(loc));

		assertFalse(loc.equals(shop.getShopLocation()));
		verify(shopDao, never()).saveShopLocation(loc);
		verify(shop.getShopVillager(), never()).teleport(loc);
	}

	@Test
	public void moveShopTestWithPermission() {
		Playershop shop = createPlayershop();
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(world.getName()).thenReturn("world");
		when(loc.getWorld()).thenReturn(world);
		when(townworldManager.isTownWorld("world")).thenReturn(true);

		assertDoesNotThrow(() -> shop.moveShop(loc));

		assertEquals(loc, shop.getShopLocation());
		verify(shopDao).saveShopLocation(loc);
		verify(shop.getShopVillager()).teleport(loc);
	}

	@Test
	public void changeShopNameTestWithInvalidName() throws ShopSystemException {
		Playershop shop = createPlayershop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForValidShopName("my_name");
		assertThrows(ShopSystemException.class, () -> shop.changeShopName("my_name"));
		assertEquals("myshop", shop.getName());
		verify(shopDao, never()).saveShopName("my_shop");
	}

	@Test
	public void changeShopNameTestWithExistingName() throws GeneralEconomyException {
		Playershop shop = createPlayershop();
		when(playershopManager.getPlayerShopUniqueNameList()).thenReturn(new ArrayList<>());
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForShopNameIsFree(new ArrayList<>(),
				"newShop", shop.getOwner());
		assertThrows(GeneralEconomyException.class, () -> shop.changeShopName("newShop"));
		assertEquals("myshop", shop.getName());
		verify(shopDao, never()).saveShopName("newShop");
	}

	@Test
	public void changeShopNameTest() {
		Plugin plugin = mock(Plugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory invStock = mock(Inventory.class);
		Inventory editor = mock(Inventory.class);
		Inventory slotEditor = mock(Inventory.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editor);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(slotEditor);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Stock"))).thenReturn(invStock);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(customSkullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		Playershop shop = new PlayershopImpl("myshop", ecoPlayer, "P0", loc, 9, shopDao, serverProvider,
				customSkullService, logger, validationHandler, null, messageWrapper, configManager, townworldManager,
				playershopManager);

		Inventory invNew = mock(Inventory.class);
		Inventory editorNew = mock(Inventory.class);
		Inventory slotEditorNew = mock(Inventory.class);
		Inventory stockNew = mock(Inventory.class);

		when(serverProvider.createInventory(shop.getShopVillager(), 9, "newName")).thenReturn(invNew);
		when(serverProvider.createInventory(shop.getShopVillager(), 9, "newName-Editor")).thenReturn(editorNew);
		when(serverProvider.createInventory(shop.getShopVillager(), 27, "newName-SlotEditor"))
				.thenReturn(slotEditorNew);
		when(serverProvider.createInventory(shop.getShopVillager(), 9, "newName-Stock")).thenReturn(stockNew);
		when(shop.getOwner().getName()).thenReturn("catch441");

		assertDoesNotThrow(() -> shop.changeShopName("newName"));

		assertEquals("newName", shop.getName());
		verify(shopDao).saveShopName("newName");
		verify(invNew).setContents(inv.getContents());
		verify(editorNew).setContents(editor.getContents());
		verify(slotEditorNew).setContents(slotEditor.getContents());

		verify(shop.getShopVillager()).setCustomName("newName_catch441");

	}

	@Test
	public void removeItemTest() {
		Playershop shop = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(shop.getShopInventory().getItem(3)).thenReturn(stack);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));
		reset(shop.getShopInventory());
		assertDoesNotThrow(() -> shop.removeShopItem(3));

		assertDoesNotThrow(() -> verify(validationHandler).checkForItemCanBeDeleted(3, 9));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsNotEmpty(3, shop.getShopInventory(), 2));
		verify(shop.getShopInventory()).clear(3);
		verify(shopDao).saveShopItem(any(), eq(true));
		assertDoesNotThrow(() -> assertEquals(0, shop.getItemList().size()));
		assertDoesNotThrow(() -> verify(shop.getStockpileInventory()).clear(3));
	}
}
