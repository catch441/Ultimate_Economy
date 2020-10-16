package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.TownworldManager;

@ExtendWith(MockitoExtension.class)
public class RentshopImplTest {

	@Mock
	ServerProvider serverProvider;
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
	CustomSkullService customSkullService;
	@Mock
	ConfigManager configManager;
	@Mock
	ShopDao shopDao;
	@Mock
	PlayershopManager playershopManager;
	@Mock
	TownworldManager townworldManager;
	@Mock
	EconomyPlayerManager ecoPlayerManager;

	@Test
	public void constructorNewTest() {
		Plugin plugin = mock(Plugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack stockInfoItem = mock(ItemStack.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemStack stuff = mock(ItemStack.class);
		ItemMeta stuffMeta = mock(ItemMeta.class);
		ItemMeta meta = mock(ItemMeta.class);
		ItemMeta stockInfoItemMeta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory invStock = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(stockInfoItemMeta.getDisplayName()).thenReturn("Stock");
		when(stockInfoItem.getItemMeta()).thenReturn(stockInfoItemMeta);
		when(stuff.getItemMeta()).thenReturn(stuffMeta);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0-Editor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0-SlotEditor")))
				.thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0"))).thenReturn(inv);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0-Stock"))).thenReturn(invStock);
		when(serverProvider.createItemStack(Material.CRAFTING_TABLE, 1)).thenReturn(stockInfoItem);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(infoItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.CLOCK, 1)).thenReturn(stuff);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(customSkullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		RentshopImpl shop = new RentshopImpl(loc, 9, "R0", 5.5, shopDao, serverProvider, customSkullService, logger,
				validationHandler, null, messageWrapper, configManager, townworldManager, playershopManager);

		verify(shopDao).setupSavefile("R0");
		verify(shopDao).saveItemNames(new ArrayList<>());
		verify(shopDao).saveShopLocation(loc);
		verify(shopDao).saveShopName("RentShop#R0");
		verify(shopDao, never()).saveOwner(any(EconomyPlayer.class));
		verify(shop.getShopVillager()).setCustomName("RentShop#R0");
		verify(shop.getShopVillager()).setCustomNameVisible(true);
		verify(shop.getShopVillager()).setSilent(true);
		verify(shop.getShopVillager()).setVillagerLevel(2);
		verify(shop.getShopVillager()).setCollidable(false);
		verify(shop.getShopVillager()).setInvulnerable(true);
		verify(shop.getShopVillager()).setProfession(Profession.NITWIT);
		verify(shop.getShopVillager()).setMetadata(eq("ue-id"), any(FixedMetadataValue.class));
		verify(shop.getShopVillager(), times(2)).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		assertEquals("R0", shop.getShopId());
		assertEquals(loc, shop.getShopLocation());
		assertNull(shop.getOwner());
		assertEquals("RentShop#R0", shop.getName());

		verify(meta).setDisplayName("Info");
		verify(infoItem, times(4)).setItemMeta(meta);
		verify(meta).setLore(Arrays.asList("§6Rightclick: §asell specified amount", "§6Shift-Rightclick: §asell all",
				"§6Leftclick: §abuy"));
		verify(inv).setItem(8, infoItem);

		verify(stockInfoItemMeta).setDisplayName("Stock");
		verify(stockInfoItem, times(3)).setItemMeta(stockInfoItemMeta);
		verify(stockInfoItemMeta).setLore(Arrays.asList(ChatColor.RED + "Only for Shopowner",
				ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "open/close stockpile"));
		verify(inv).setItem(7, stockInfoItem);

		// stockpile
		verify(stockInfoItemMeta).setDisplayName("Infos");
		verify(stockInfoItemMeta)
				.setLore(Arrays.asList(ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "close stockpile",
						ChatColor.GOLD + "Rightclick: " + ChatColor.GREEN + "add specified amount",
						ChatColor.GOLD + "Shift-Rightclick: " + ChatColor.GREEN + "add all",
						ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "get specified amount"));
		assertDoesNotThrow(() -> verify(shop.getStockpileInventory()).setItem(8, stockInfoItem));
		// rentshop
		assertEquals(5.5, shop.getRentalFee());
		verify(shopDao).saveRentalFee(5.5);
		assertTrue(shop.isRentable());
		verify(shopDao).saveRentable(true);
		assertNotNull(shop.getEditorHandler());
	}

	private RentshopImpl createRentshop() {
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
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0-Editor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0-SlotEditor")))
				.thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0"))).thenReturn(inv);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0-Stock"))).thenReturn(invStock);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(customSkullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		return new RentshopImpl(loc, 9, "R0", 5.5, shopDao, serverProvider, customSkullService, logger,
				validationHandler, null, messageWrapper, configManager, townworldManager, playershopManager);
	}

	@Test
	public void constructorLoadTestWithNotRented() {
		Plugin plugin = mock(Plugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack stockInfoItem = mock(ItemStack.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemStack stuff = mock(ItemStack.class);
		ItemMeta stuffMeta = mock(ItemMeta.class);
		ItemMeta infoItemMeta = mock(ItemMeta.class);
		ItemMeta stockInfoItemMeta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory invStock = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		when(stockInfoItemMeta.getDisplayName()).thenReturn("Stock");
		when(infoItemMeta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0-Editor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0-SlotEditor")))
				.thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0"))).thenReturn(inv);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0-Stock"))).thenReturn(invStock);
		when(serverProvider.createItemStack(Material.CRAFTING_TABLE, 1)).thenReturn(stockInfoItem);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(infoItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.CLOCK, 1)).thenReturn(stuff);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(stuff.getItemMeta()).thenReturn(stuffMeta);
		when(infoItem.getItemMeta()).thenReturn(infoItemMeta);
		when(stockInfoItem.getItemMeta()).thenReturn(stockInfoItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(customSkullService.getSkullWithName(anyString(), anyString())).thenReturn(stockInfoItem);

		when(shopDao.loadShopName()).thenReturn("RentShop#R0");
		when(shopDao.loadShopSize()).thenReturn(9);
		assertDoesNotThrow(() -> when(shopDao.loadShopLocation()).thenReturn(loc));
		when(shopDao.loadShopVillagerProfession()).thenReturn(Profession.ARMORER);
		when(shopDao.loadOwner(null)).thenReturn(null);
		when(shopDao.loadItemNameList()).thenReturn(new ArrayList<>());
		when(shopDao.loadRentable()).thenReturn(true);
		when(shopDao.loadRentalFee()).thenReturn(5.5);
		when(shopDao.loadRentUntil()).thenReturn(0L);
		RentshopImpl shop = assertDoesNotThrow(
				() -> new RentshopImpl("R0", shopDao, serverProvider, customSkullService, logger, validationHandler,
						ecoPlayerManager, messageWrapper, configManager, townworldManager, playershopManager));

		verify(shopDao).setupSavefile("R0");
		verify(shop.getShopVillager(), times(2)).setCustomName("RentShop#R0");
		verify(shop.getShopVillager()).setCustomNameVisible(true);
		verify(shop.getShopVillager()).setSilent(true);
		verify(shop.getShopVillager()).setVillagerLevel(2);
		verify(shop.getShopVillager()).setCollidable(false);
		verify(shop.getShopVillager()).setInvulnerable(true);
		verify(shop.getShopVillager()).setProfession(Profession.NITWIT);
		verify(shop.getShopVillager()).setMetadata(eq("ue-id"), any(FixedMetadataValue.class));
		verify(shop.getShopVillager(), times(2)).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		assertEquals("R0", shop.getShopId());
		assertEquals(loc, shop.getShopLocation());
		assertNull(shop.getOwner());
		assertEquals("RentShop#R0", shop.getName());

		verify(stockInfoItemMeta).setDisplayName("Stock");
		verify(stockInfoItem, times(5)).setItemMeta(stockInfoItemMeta);
		verify(stockInfoItemMeta).setLore(Arrays.asList(ChatColor.RED + "Only for Shopowner",
				ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "open/close stockpile"));
		verify(inv).setItem(7, stockInfoItem);

		verify(infoItemMeta).setDisplayName("Info");
		verify(infoItem, times(2)).setItemMeta(infoItemMeta);
		verify(infoItemMeta).setLore(Arrays.asList("§6Rightclick: §asell specified amount",
				"§6Shift-Rightclick: §asell all", "§6Leftclick: §abuy"));
		verify(inv).setItem(8, infoItem);

		// stockpile
		verify(stockInfoItemMeta).setDisplayName("Infos");
		verify(stockInfoItemMeta)
				.setLore(Arrays.asList(ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "close stockpile",
						ChatColor.GOLD + "Rightclick: " + ChatColor.GREEN + "add specified amount",
						ChatColor.GOLD + "Shift-Rightclick: " + ChatColor.GREEN + "add all",
						ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "get specified amount"));
		assertDoesNotThrow(() -> verify(shop.getStockpileInventory()).setItem(8, stockInfoItem));
		// rentshop
		assertEquals(5.5, shop.getRentalFee());
		assertTrue(shop.isRentable());
		assertEquals(0L, shop.getRentUntil());
		assertNotNull(shop.getEditorHandler());
	}

	@Test
	public void moveShopTest() {
		Rentshop shop = createRentshop();
		Location loc = mock(Location.class);
		assertDoesNotThrow(() -> shop.moveShop(loc));

		assertEquals(loc, shop.getShopLocation());
		verify(shopDao).saveShopLocation(loc);
		verify(shop.getShopVillager()).teleport(loc);
	}

	@Test
	public void isRentableTest() {
		Rentshop shop = createRentshop();
		assertTrue(shop.isRentable());
		rentThisShop(shop);
		assertFalse(shop.isRentable());
	}

	@Test
	public void changeShopNameTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.changeShopName("newName"));
		verify(shop.getShopVillager(), never()).setCustomName("newName");
	}

	@Test
	public void changeShopNameTestWithRented() {
		Rentshop shop = createRentshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Inventory inv = mock(Inventory.class);
		Inventory invStock = mock(Inventory.class);
		Inventory editor = mock(Inventory.class);
		Inventory slotEditor = mock(Inventory.class);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-Editor")))
				.thenReturn(editor);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-SlotEditor")))
				.thenReturn(slotEditor);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0"))).thenReturn(inv);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-Stock")))
				.thenReturn(invStock);

		assertDoesNotThrow(() -> shop.rentShop(ecoPlayer, 1));

		Inventory invNew = mock(Inventory.class);
		Inventory editorNew = mock(Inventory.class);
		Inventory slotEditorNew = mock(Inventory.class);
		Inventory stockNew = mock(Inventory.class);

		when(serverProvider.createInventory(shop.getShopVillager(), 9, "newName")).thenReturn(invNew);
		when(serverProvider.createInventory(shop.getShopVillager(), 9, "newName-Editor")).thenReturn(editorNew);
		when(serverProvider.createInventory(shop.getShopVillager(), 27, "newName-SlotEditor"))
				.thenReturn(slotEditorNew);
		when(serverProvider.createInventory(shop.getShopVillager(), 9, "newName-Stock")).thenReturn(stockNew);
		when(ecoPlayer.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> shop.changeShopName("newName"));

		assertEquals("newName", shop.getName());
		verify(shopDao).saveShopName("newName");
		verify(invNew).setContents(inv.getContents());
		verify(editorNew).setContents(editor.getContents());
		verify(slotEditorNew).setContents(slotEditor.getContents());

		verify(shop.getShopVillager()).setCustomName("newName_catch441");
	}

	@Test
	public void rentShopTest() {
		Rentshop shop = createRentshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);

		Inventory inv = mock(Inventory.class);
		Inventory invStock = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-Editor")))
				.thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-SlotEditor")))
				.thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0"))).thenReturn(inv);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-Stock")))
				.thenReturn(invStock);
		when(serverProvider.getActualTime()).thenReturn(10L);

		assertDoesNotThrow(() -> shop.rentShop(ecoPlayer, 2));

		verify(shopDao).saveOwner(ecoPlayer);
		verify(shopDao).saveRentable(false);
		verify(shopDao).saveRentUntil(172800010L);
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(11.0, true));
		assertFalse(shop.isRentable());
		assertEquals(172800010L, shop.getRentUntil());
		assertEquals(ecoPlayer, shop.getOwner());
		assertEquals("Shop#R0", shop.getName());
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForIsRentable(true));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(1));
		verify(shop.getShopVillager()).setCustomName("Shop#R0_catch441");
	}

	@Test
	public void rentShopTestWithNotEnoughMoney() throws GeneralEconomyException, EconomyPlayerException {
		Rentshop shop = createRentshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(ecoPlayer).decreasePlayerAmount(5.5, true);
		assertThrows(EconomyPlayerException.class, () -> shop.rentShop(ecoPlayer, 1));
	}

	@Test
	public void rentShopTestWithInvalidDuration() throws GeneralEconomyException {
		Rentshop shop = createRentshop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForPositiveValue(-1);
		assertThrows(GeneralEconomyException.class, () -> shop.rentShop(null, 0));
	}

	@Test
	public void rentShopTestWithAlreadyRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		rentThisShop(shop);
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRentable(false);
		assertThrows(ShopSystemException.class, () -> shop.rentShop(null, 1));
	}

	@Test
	public void resetShopTest() {
		Rentshop shop = createRentshop();
		rentThisShop(shop);

		assertDoesNotThrow(() -> shop.resetShop());

		verify(shopDao).saveOwner(null);
		verify(shopDao, times(2)).saveRentable(true);
		verify(shopDao).saveRentUntil(0L);
		assertNull(shop.getOwner());
		assertEquals(0L, shop.getRentUntil());
		assertTrue(shop.isRentable());
		verify(shop.getShopVillager(), times(2)).setProfession(Profession.NITWIT);
		assertEquals("RentShop#R0", shop.getName());
		verify(shop.getShopVillager(), times(2)).setCustomName("RentShop#R0");
		assertDoesNotThrow(() -> assertEquals(0, shop.getItemList().size()));
	}

	@Test
	public void changeRentalFee() {
		Rentshop shop = createRentshop();
		assertDoesNotThrow(() -> shop.changeRentalFee(4.4));
		assertEquals(4.4, shop.getRentalFee());
		verify(shopDao).saveRentalFee(4.4);
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(4.4));
	}

	@Test
	public void changeRentalFeeWithNegativValue() throws GeneralEconomyException {
		Rentshop shop = createRentshop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForPositiveValue(-1);
		assertThrows(GeneralEconomyException.class, () -> shop.changeRentalFee(-1));
		assertEquals(5.5, shop.getRentalFee());
		verify(shopDao, never()).saveRentalFee(4.4);
	}

	@Test
	public void changeShopSizeTestWithNotRented() {
		Rentshop shop = createRentshop();

		assertDoesNotThrow(() -> shop.changeShopSize(18));

		assertDoesNotThrow(() -> verify(validationHandler).checkForValidSize(18));
		assertDoesNotThrow(() -> verify(validationHandler).checkForResizePossible(shop.getShopInventory(), 9, 18, 2));
		assertEquals(18, shop.getSize());
		verify(shopDao).saveShopSize(18);
		verify(serverProvider).createInventory(shop.getShopVillager(), 18, "RentShop#R0");
		verify(serverProvider).createInventory(shop.getShopVillager(), 18, "RentShop#R0-Stock");
		verify(serverProvider).createInventory(shop.getShopVillager(), 18, "RentShop#R0-Editor");
	}

	@Test
	public void changeShopSizeTestWithRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		rentThisShop(shop);
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRentable(false);
		assertThrows(ShopSystemException.class, () -> shop.changeShopSize(18));
		assertEquals(9, shop.getSize());
	}

	@Test
	public void getRentalFeeTest() {
		Rentshop shop = createRentshop();
		assertEquals(5.5, shop.getRentalFee());
	}

	@Test
	public void openRentGUITest() {
		RentshopImpl shop = createRentshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Inventory inv = mock(Inventory.class);
		Inventory invStock = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-Editor")))
				.thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-SlotEditor")))
				.thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0"))).thenReturn(inv);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-Stock")))
				.thenReturn(invStock);

		assertDoesNotThrow(() -> shop.rentShop(ecoPlayer, 1));
		Player player = mock(Player.class);
		assertDoesNotThrow(() -> shop.openRentGUI(player));
		verify(player).openInventory(shop.getRentGuiHandler().getRentGui());
	}

	@Test
	public void openRentGUITestWithRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		rentThisShop(shop);
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRentable(false);
		assertThrows(ShopSystemException.class, () -> shop.openRentGUI(null));
	}

	@Test
	public void changeOwnerTest() {
		Rentshop shop = createRentshop();
		EconomyPlayer newOwner = mock(EconomyPlayer.class);
		when(newOwner.getName()).thenReturn("wejink");
		when(playershopManager.getPlayerShopUniqueNameList()).thenReturn(new ArrayList<>());
		assertDoesNotThrow(() -> shop.changeOwner(newOwner));
		assertEquals(newOwner, shop.getOwner());
		verify(shopDao).saveOwner(newOwner);
		verify(shop.getShopVillager()).setCustomName("RentShop#R0_wejink");
		assertDoesNotThrow(() -> verify(validationHandler).checkForChangeOwnerIsPossible(new ArrayList<>(), newOwner,
				"RentShop#R0"));
	}

	@Test
	public void changeOwnerTestWithRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		rentThisShop(shop);
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRentable(false);
		assertThrows(ShopSystemException.class, () -> shop.changeOwner(null));
	}

	@Test
	public void addShopItemTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.addShopItem(0, 0, 0, null));
		assertEquals(0, shop.getItemList().size());
	}

	@Test
	public void addShopTestWithRented() {
		Rentshop shop = createRentshop();
		rentThisShop(shop);
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> shop.addShopItem(0, 1, 4, stack));
		verify(shopDao).saveItemNames(Arrays.asList("item string"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForItemDoesNotExist(eq("item string"), anyList()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsEmpty(0, shop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidPrice("1.0"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidPrice("4.0"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(1.0, 4.0));
		assertDoesNotThrow(() -> assertEquals(1, shop.getItemList().size()));
		ShopItem shopItem = assertDoesNotThrow(() -> shop.getShopItem(0));
		verify(shopDao).saveShopItem(shopItem, false);
		assertEquals(2, shopItem.getAmount());
		assertEquals(4.0, shopItem.getBuyPrice());
		assertEquals(1.0, shopItem.getSellPrice());
		assertEquals(0, shopItem.getSlot());
		assertEquals("item string", shopItem.getItemString());
		assertEquals(stackCloneClone, shopItem.getItemStack());
		// verify that the set occupied method of the editor is called
		verify(customSkullService).getSkullWithName("SLOTFILLED", "Slot 1");
		verify(shop.getShopInventory()).setItem(0, stackClone);
		verify(stackClone).setAmount(2);
		verify(stackMetaClone).setLore(Arrays.asList("§62 buy for §a4.0 $", "§62 sell for §a1.0 $"));
	}

	@Test
	public void editShopItemTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.editShopItem(0, null, null, null));
	}

	@Test
	public void editShopTestWithRented() {
		Rentshop shop = createRentshop();
		rentThisShop(shop);
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		ItemMeta stackMetaCloneClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackCloneClone.getItemMeta()).thenReturn(stackMetaCloneClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> shop.addShopItem(0, 1, 4, stack));
		when(stackCloneClone.getType()).thenReturn(Material.STONE);

		String response = assertDoesNotThrow(() -> shop.editShopItem(0, "5", "15", "25"));

		assertEquals("§6Updated §aamount §asellPrice §abuyPrice §6for item §astone", response);

		assertDoesNotThrow(() -> verify(validationHandler, times(5)).checkForIsRented(false));
		verify(shopDao).saveShopItemSellPrice("item string", 15.0);
		verify(shopDao).saveShopItemBuyPrice("item string", 25.0);
		verify(shopDao).saveShopItemAmount("item string", 5);
		assertDoesNotThrow(() -> assertEquals(5, shop.getShopItem(0).getAmount()));
		assertDoesNotThrow(() -> assertEquals(15.0, shop.getShopItem(0).getSellPrice()));
		assertDoesNotThrow(() -> assertEquals(25.0, shop.getShopItem(0).getBuyPrice()));
		verify(shop.getShopInventory()).setItem(0, stackCloneClone);
		verify(stackCloneClone).setAmount(5);
		verify(stackMetaCloneClone).setLore(Arrays.asList("§65 buy for §a25.0 $", "§65 sell for §a15.0 $"));
	}

	@Test
	public void getShopItemTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.getShopItem(null));
	}

	@Test
	public void getShopItemTestWithRentedAndSlot() {
		Rentshop shop = createRentshop();
		rentThisShop(shop);
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));

		ShopItem shopItem = assertDoesNotThrow(() -> shop.getShopItem(3));

		assertEquals(stackCloneClone, shopItem.getItemStack());
	}

	@Test
	public void getShopItemTestWithRentedAndItem() {
		Rentshop shop = createRentshop();
		rentThisShop(shop);
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemStack searchStack = mock(ItemStack.class);
		ItemStack searchStackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		ItemMeta searchStackCloneMeta = mock(ItemMeta.class);
		when(searchStackClone.toString()).thenReturn("item string");
		when(searchStackCloneMeta.hasLore()).thenReturn(true);
		ArrayList<String> lore = new ArrayList<>();
		lore.add("some lore");
		lore.add("2 buy for 10");
		lore.add("2 sell for 5");
		when(searchStackCloneMeta.getLore()).thenReturn(lore);
		when(searchStackClone.getItemMeta()).thenReturn(searchStackCloneMeta);
		when(searchStack.clone()).thenReturn(searchStackClone);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> shop.addShopItem(3, 1, 2, stack));

		ShopItem shopItem = assertDoesNotThrow(() -> shop.getShopItem(searchStack));

		assertEquals(stackCloneClone, shopItem.getItemStack());
	}

	@Test
	public void isAvailableTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.isAvailable(0));
	}

	@Test
	public void isAvailableTestWithRented() {
		Rentshop shop = createRentshop();
		rentThisShop(shop);
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
		assertDoesNotThrow(() -> verify(validationHandler, times(13)).checkForIsRented(false));
	}

	@Test
	public void openShopInventoryTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.openShopInventory(null));
	}

	@Test
	public void openShopInventoryTestWithRented() {
		Rentshop shop = createRentshop();
		rentThisShop(shop);
		Player player = mock(Player.class);
		assertDoesNotThrow(() -> shop.openShopInventory(player));
		verify(player).openInventory(shop.getShopInventory());
	}

	@Test
	public void decreaseStockTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.decreaseStock(0, 0));
	}

	@Test
	public void decreaseStockTestWithRented() {
		Rentshop shop = createRentshop();
		rentThisShop(shop);
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

		assertDoesNotThrow(() -> verify(validationHandler, times(10)).checkForIsRented(false));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(5));
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidStockDecrease(10, 5));
		verify(shopDao).saveStock("item string", 5);
		verify(shopInvStackMeta).setLore(Arrays.asList("§a5§6 Items"));
		assertDoesNotThrow(() -> assertEquals(5, shop.getShopItem(3).getStock()));
	}

	@Test
	public void increaseStockTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.increaseStock(0, 0));
	}

	@Test
	public void increaseStockTestWithRented() {
		Rentshop shop = createRentshop();
		rentThisShop(shop);
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

		assertDoesNotThrow(() -> verify(validationHandler, times(7)).checkForIsRented(false));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidSlot(3, 9, 2));
		verify(shopDao).saveStock("item string", 1);
		verify(shopInvStackMeta).setLore(Arrays.asList("§a1§6 Item"));
		assertDoesNotThrow(() -> verify(shop.getStockpileInventory(), times(2)).setItem(3, shopInvStackClone));
	}

	@Test
	public void removeShopItemTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.removeShopItem(0));
	}

	@Test
	public void removeShopItemTestWithRented() {
		Rentshop shop = createRentshop();
		rentThisShop(shop);

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

		assertDoesNotThrow(() -> verify(validationHandler, times(6)).checkForIsRented(false));
		assertDoesNotThrow(() -> verify(validationHandler).checkForItemCanBeDeleted(3, 9));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidSlot(3, 9, 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsNotEmpty(3, shop.getShopInventory(), 2));
		verify(shop.getShopInventory()).clear(3);
		verify(shopDao).saveShopItem(any(), eq(true));
		assertDoesNotThrow(() -> assertEquals(0, shop.getItemList().size()));
		assertDoesNotThrow(() -> verify(shop.getStockpileInventory()).clear(3));
	}

	@Test
	public void openStockpileTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.openStockpile(null));
	}

	@Test
	public void openStockpileTestWithRented() {
		Rentshop shop = createRentshop();
		rentThisShop(shop);

		Player player = mock(Player.class);
		assertDoesNotThrow(() -> shop.openStockpile(player));
		assertDoesNotThrow(() -> verify(player).openInventory(shop.getStockpileInventory()));
	}

	private void rentThisShop(Rentshop shop) {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);

		Inventory inv = mock(Inventory.class);
		Inventory invStock = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-Editor")))
				.thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-SlotEditor")))
				.thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0"))).thenReturn(inv);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-Stock")))
				.thenReturn(invStock);

		assertDoesNotThrow(() -> shop.rentShop(ecoPlayer, 1));
	}

	@Test
	public void openSlotEditorTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.openSlotEditor(null, 0));
	}

	@Test
	public void openSlotEditorTestWithRented() {
		Rentshop shop = createRentshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Inventory inv = mock(Inventory.class);
		Inventory invStock = mock(Inventory.class);
		Inventory slotEditor = mock(Inventory.class);
		Inventory editor = mock(Inventory.class);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-Editor")))
				.thenReturn(editor);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-SlotEditor")))
				.thenReturn(slotEditor);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0"))).thenReturn(inv);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-Stock")))
				.thenReturn(invStock);
		assertDoesNotThrow(() -> shop.rentShop(ecoPlayer, 1));
		assertDoesNotThrow(() -> when(validationHandler.isSlotEmpty(0, shop.getShopInventory(), 1)).thenReturn(true));

		Player player = mock(Player.class);
		assertDoesNotThrow(() -> shop.openSlotEditor(player, 0));

		assertDoesNotThrow(() -> verify(validationHandler).checkForValidSlot(0, 9, 2));
		verify(player).openInventory(slotEditor);
		// verify that the selected slot method is executed
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).isSlotEmpty(0, shop.getShopInventory(), 1));
	}

	@Test
	public void openEditorTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.openEditor(null));
	}

	@Test
	public void openEditorTestWithRented() {
		Rentshop shop = createRentshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Inventory inv = mock(Inventory.class);
		Inventory invStock = mock(Inventory.class);
		Inventory slotEditor = mock(Inventory.class);
		Inventory editor = mock(Inventory.class);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-Editor")))
				.thenReturn(editor);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-SlotEditor")))
				.thenReturn(slotEditor);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0"))).thenReturn(inv);
		when(serverProvider.createInventory(eq(shop.getShopVillager()), anyInt(), eq("Shop#R0-Stock")))
				.thenReturn(invStock);
		assertDoesNotThrow(() -> shop.rentShop(ecoPlayer, 1));
		Player player = mock(Player.class);

		assertDoesNotThrow(() -> shop.openEditor(player));

		verify(player).openInventory(editor);
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForIsRented(false));
	}

	@Test
	public void buyShopItemWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.buyShopItem(0, null, true));
	}

	@Test
	public void buyShopItemTestWithRented() {
		Rentshop shop = createRentshop();
		rentThisShop(shop);

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

		assertDoesNotThrow(() -> verify(validationHandler, times(9)).checkForIsRented(false));
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
	public void sellShopItemWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.sellShopItem(0, 1, null, false));
	}

	@Test
	public void sellShopItemTestWithRented() {
		Rentshop shop = createRentshop();
		rentThisShop(shop);
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

		assertDoesNotThrow(() -> verify(validationHandler, times(7)).checkForIsRented(false));
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
}
