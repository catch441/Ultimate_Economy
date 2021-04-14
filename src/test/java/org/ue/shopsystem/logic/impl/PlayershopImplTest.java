package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

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
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.ue.common.api.CustomSkullService;
import org.ue.common.api.SkullTextureEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.api.GeneralEconomyValidationHandler;
import org.ue.general.GeneralEconomyException;
import org.ue.shopsystem.dataaccess.api.ShopDao;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopValidationHandler;
import org.ue.shopsystem.logic.ShopSystemException;
import org.ue.townsystem.logic.api.TownsystemValidationHandler;
import org.ue.townsystem.logic.api.TownworldManager;
import org.ue.townsystem.logic.TownSystemException;

@ExtendWith(MockitoExtension.class)
public class PlayershopImplTest {

	@InjectMocks
	PlayershopImpl playershop;
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
	@Mock
	GeneralEconomyValidationHandler generalValidator;

	@Test
	public void setupNewTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack stockInfoItem = mock(ItemStack.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemStack stuff = mock(ItemStack.class);
		ItemMeta stuffMeta = mock(ItemMeta.class);
		ItemMeta infoItemMeta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		when(infoItemMeta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(infoItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(stuff);

		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(stuff.getItemMeta()).thenReturn(stuffMeta);
		when(infoItem.getItemMeta()).thenReturn(infoItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(customSkullService.getSkullWithName(any(SkullTextureEnum.class), anyString())).thenReturn(stockInfoItem);
		playershop.setupNew("myshop", ecoPlayer, "P0", loc, 9);

		verify(shopDao).setupSavefile("P0");
		verify(shopDao).saveShopLocation(loc);
		verify(shopDao).saveShopName("myshop");
		verify(shopDao).saveOwner(ecoPlayer);
		verify(playershop.getShopVillager()).setCustomName("myshop_catch441");
		verify(playershop.getShopVillager()).setCustomNameVisible(true);
		verify(playershop.getShopVillager()).setSilent(true);
		verify(playershop.getShopVillager()).setVillagerLevel(2);
		verify(playershop.getShopVillager()).setCollidable(false);
		verify(playershop.getShopVillager()).setInvulnerable(true);
		verify(playershop.getShopVillager()).setProfession(Profession.NITWIT);
		verify(playershop.getShopVillager()).setMetadata(eq("ue-id"), any(FixedMetadataValue.class));
		verify(playershop.getShopVillager()).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		assertEquals("P0", playershop.getShopId());
		assertEquals(loc, playershop.getShopLocation());
		assertEquals(ecoPlayer, playershop.getOwner());
		assertEquals("myshop", playershop.getName());

		verify(infoItemMeta).setDisplayName("Info");
		verify(infoItem, times(2)).setItemMeta(infoItemMeta);
		verify(infoItemMeta).setLore(Arrays.asList("§6Rightclick: §asell specified amount",
				"§6Shift-Rightclick: §asell all", "§6Leftclick: §abuy"));
		verify(inv).setItem(8, infoItem);
	}

	@Test
	public void setupExistingTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack stockInfoItem = mock(ItemStack.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemStack stuff = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		ItemMeta shopItemStackMeta = mock(ItemMeta.class);
		ItemMeta stuffMeta = mock(ItemMeta.class);
		ItemMeta infoItemMeta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ShopItemImpl shopItem = mock(ShopItemImpl.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		when(infoItemMeta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(infoItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(stuff);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(shopItemStack.getItemMeta()).thenReturn(shopItemStackMeta);
		when(stuff.getItemMeta()).thenReturn(stuffMeta);
		when(infoItem.getItemMeta()).thenReturn(infoItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(customSkullService.getSkullWithName(any(SkullTextureEnum.class), anyString())).thenReturn(stockInfoItem);

		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(shopDao.loadShopName()).thenReturn("myshop");
		when(shopDao.loadShopSize()).thenReturn(9);
		assertDoesNotThrow(() -> when(shopDao.loadShopLocation()).thenReturn(loc));
		when(shopDao.loadShopVillagerProfession()).thenReturn(Profession.ARMORER);
		when(shopDao.loadOwner(null)).thenReturn("catch441");
		when(shopDao.loadItemHashList()).thenReturn(Arrays.asList(5435345));
		when(shopDao.loadItem(5435345)).thenReturn(shopItem);
		when(shopDao.loadStock(5435345)).thenReturn(7);
		when(shopItem.getItemHash()).thenReturn(5435345);
		when(shopItem.getSlot()).thenReturn(0);
		when(shopItem.getAmount()).thenReturn(5);
		when(shopItem.getSellPrice()).thenReturn(2.0);
		when(shopItem.getBuyPrice()).thenReturn(3.0);
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(inv.getItem(0)).thenReturn(shopItemStack);
		assertDoesNotThrow(() -> playershop.setupExisting(null, "P0"));

		verify(shopDao).setupSavefile("P0");
		verify(playershop.getShopVillager()).setCustomName("myshop_catch441");
		verify(playershop.getShopVillager()).setCustomNameVisible(true);
		verify(playershop.getShopVillager()).setSilent(true);
		verify(playershop.getShopVillager()).setVillagerLevel(2);
		verify(playershop.getShopVillager()).setCollidable(false);
		verify(playershop.getShopVillager()).setInvulnerable(true);
		verify(playershop.getShopVillager()).setProfession(Profession.NITWIT);
		verify(playershop.getShopVillager()).setMetadata(eq("ue-id"), any(FixedMetadataValue.class));
		verify(playershop.getShopVillager()).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		assertEquals("P0", playershop.getShopId());
		assertEquals(loc, playershop.getShopLocation());
		assertEquals(ecoPlayer, playershop.getOwner());
		assertEquals("myshop", playershop.getName());
		verify(shopItem).setStock(7);
		assertDoesNotThrow(() -> assertEquals(shopItem, playershop.getShopItem(0)));

		verify(infoItemMeta).setDisplayName("Info");
		verify(infoItem, times(2)).setItemMeta(infoItemMeta);
		verify(infoItemMeta).setLore(Arrays.asList("§6Rightclick: §asell specified amount",
				"§6Shift-Rightclick: §asell all", "§6Leftclick: §abuy"));
		verify(inv).setItem(8, infoItem);

		verify(shopItemStack, times(2)).setItemMeta(shopItemStackMeta);
		verify(shopItemStackMeta).setLore(Arrays.asList("§65 buy for §a3.0 $", "§65 sell for §a2.0 $"));
		verify(shopItemStackMeta).setLore(Arrays.asList("§a0§6 Items"));
		verify(inv).setItem(0, shopItemStack);
	}

	private void createPlayershop() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(customSkullService.getSkullWithName(any(SkullTextureEnum.class), anyString())).thenReturn(infoItem);
		playershop.setupNew("myshop", ecoPlayer, "P0", loc, 9);
	}

	@Test
	public void openSlotEditorTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory editor = mock(Inventory.class);
		Inventory slotEditor = mock(Inventory.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editor);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(slotEditor);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(customSkullService.getSkullWithName(any(SkullTextureEnum.class), anyString())).thenReturn(infoItem);
		playershop.setupNew("myshop", ecoPlayer, "P0", loc, 9);
		assertDoesNotThrow(
				() -> when(validationHandler.isSlotEmpty(0, playershop.getShopInventory(), 1)).thenReturn(true));

		assertDoesNotThrow(() -> playershop.openSlotEditor(player, 0));

		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSlot(0, 8));
		verify(player).openInventory(slotEditor);
		// verify that the selected slot method is executed
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).isSlotEmpty(0, playershop.getShopInventory(), 1));
	}

	@Test
	public void openSlotEditorTestWithInvalidSlot() throws GeneralEconomyException {
		createPlayershop();
		Player player = mock(Player.class);
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSlot(0, 8);
		assertThrows(GeneralEconomyException.class, () -> playershop.openSlotEditor(player, 0));
		verify(player, never()).openInventory(any(Inventory.class));
	}

	@Test
	public void buyShopItemTestWithTooSmallStock() throws ShopSystemException {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(0, 1, 2, stack));
		doThrow(ShopSystemException.class).when(validationHandler).checkForValidStockDecrease(0, 1);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);

		assertThrows(ShopSystemException.class, () -> playershop.buyShopItem(0, ecoPlayer, true));

		verify(player, never()).sendMessage(anyString());
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithInvalidSlot() throws GeneralEconomyException {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(0, 1, 2, stack));
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSlot(9, 8);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertThrows(GeneralEconomyException.class, () -> playershop.buyShopItem(9, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithEmptySlot() throws GeneralEconomyException, ShopSystemException {
		createPlayershop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForSlotIsNotEmpty(3,
				playershop.getShopInventory(), 1);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertThrows(GeneralEconomyException.class, () -> playershop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithOfflinePlayer() throws EconomyPlayerException {
		createPlayershop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerIsOnline(ecoPlayer);
		assertThrows(EconomyPlayerException.class, () -> playershop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithFullInventory() throws EconomyPlayerException {
		createPlayershop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerInventoryNotFull(inv);
		assertThrows(EconomyPlayerException.class, () -> playershop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithSingular() {
		createPlayershop();
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
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> playershop.increaseStock(3, 1));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(configManager.getCurrencyText(2.0)).thenReturn("$");
		when(messageWrapper.getString("shop_buy_singular", "1", 2.0, "$")).thenReturn("my message");

		assertDoesNotThrow(() -> playershop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidStockDecrease(1, 1));
		assertDoesNotThrow(() -> verify(generalValidator, times(3)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(5)).checkForSlotIsNotEmpty(3, playershop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(1);
		verify(inv).addItem(stackCloneClone);
		verify(shopDao, times(2)).saveStock("item string".hashCode(), 0);
		verify(player).sendMessage("my message");
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(2.0, true));
		assertDoesNotThrow(() -> verify(playershop.getOwner()).increasePlayerAmount(2.0, false));
		assertDoesNotThrow(() -> assertEquals(0, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void buyShopItemTestWithPlural() {
		createPlayershop();
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
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 4, stack));
		assertDoesNotThrow(() -> playershop.increaseStock(3, 2));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(configManager.getCurrencyText(4.0)).thenReturn("$");
		when(messageWrapper.getString("shop_buy_plural", "2", 4.0, "$")).thenReturn("my message");

		assertDoesNotThrow(() -> playershop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidStockDecrease(2, 2));
		assertDoesNotThrow(() -> verify(generalValidator, times(3)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(5)).checkForSlotIsNotEmpty(3, playershop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(2);
		verify(inv).addItem(stackCloneClone);
		verify(shopDao, times(2)).saveStock("item string".hashCode(), 0);
		verify(player).sendMessage("my message");
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(4.0, true));
		assertDoesNotThrow(() -> verify(playershop.getOwner()).increasePlayerAmount(4.0, false));
		assertDoesNotThrow(() -> assertEquals(0, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void buyShopItemTestWithNoBuyPrice() {
		createPlayershop();
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
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 0, stack));
		assertDoesNotThrow(() -> playershop.increaseStock(3, 2));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);

		assertDoesNotThrow(() -> playershop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, never()).checkForValidStockDecrease(anyInt(), anyInt()));
		assertDoesNotThrow(() -> verify(generalValidator, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(4)).checkForSlotIsNotEmpty(3, playershop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		// only at setup
		verify(shopDao, times(1)).saveStock("item string".hashCode(), 0);

		verify(player, never()).sendMessage(anyString());
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).increasePlayerAmount(anyDouble(), eq(true)));
		assertDoesNotThrow(() -> assertEquals(2, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void buyShopItemTestWithSingularAsOwner() {
		createPlayershop();
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
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> playershop.increaseStock(3, 1));
		when(playershop.getOwner().getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(messageWrapper.getString("shop_got_item_singular", "1")).thenReturn("my message");

		assertDoesNotThrow(() -> playershop.buyShopItem(3, playershop.getOwner(), true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidStockDecrease(1, 1));
		assertDoesNotThrow(() -> verify(generalValidator, times(3)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(playershop.getOwner()));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(5)).checkForSlotIsNotEmpty(3, playershop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(1);
		verify(inv).addItem(stackCloneClone);
		verify(shopDao, times(2)).saveStock("item string".hashCode(), 0);
		verify(player).sendMessage("my message");
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).increasePlayerAmount(2.0, false));
		assertDoesNotThrow(() -> assertEquals(0, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void buyShopItemTestWithPluralAsOwner() {
		createPlayershop();
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
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> playershop.increaseStock(3, 3));
		when(playershop.getOwner().getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(messageWrapper.getString("shop_got_item_plural", "2")).thenReturn("my message");

		assertDoesNotThrow(() -> playershop.buyShopItem(3, playershop.getOwner(), true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidStockDecrease(3, 2));
		assertDoesNotThrow(() -> verify(generalValidator, times(3)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(playershop.getOwner()));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(5)).checkForSlotIsNotEmpty(3, playershop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(2);
		verify(inv).addItem(stackCloneClone);
		verify(shopDao, times(1)).saveStock("item string".hashCode(), 1);
		verify(player).sendMessage("my message");
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).increasePlayerAmount(2.0, false));
		assertDoesNotThrow(() -> assertEquals(1, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void buyShopItemTestWithPluralAsOwnerAndSmallerStockAsAmount() {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(stack.getAmount()).thenReturn(5);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> playershop.increaseStock(3, 3));
		when(playershop.getOwner().getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(messageWrapper.getString("shop_got_item_plural", "3")).thenReturn("my message");

		assertDoesNotThrow(() -> playershop.buyShopItem(3, playershop.getOwner(), true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidStockDecrease(3, 3));
		assertDoesNotThrow(() -> verify(generalValidator, times(3)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(playershop.getOwner()));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(5)).checkForSlotIsNotEmpty(3, playershop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(3);
		verify(inv).addItem(stackCloneClone);
		verify(shopDao, times(2)).saveStock("item string".hashCode(), 0);
		verify(player).sendMessage("my message");
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).increasePlayerAmount(2.0, false));
		assertDoesNotThrow(() -> assertEquals(0, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void buyShopItemTestWithNotEnoughMoney() throws GeneralEconomyException, EconomyPlayerException {
		createPlayershop();
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
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> playershop.increaseStock(3, 1));
		reset(shopDao);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		doThrow(EconomyPlayerException.class).when(ecoPlayer).decreasePlayerAmount(2.0, true);

		assertThrows(EconomyPlayerException.class, () -> playershop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler).checkForValidStockDecrease(1, 1));
		assertDoesNotThrow(() -> verify(generalValidator, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(4)).checkForSlotIsNotEmpty(3, playershop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone, never()).setAmount(anyInt());
		verify(shopDao, never()).saveStock("item string".hashCode(), 0);
		verify(player, never()).sendMessage(anyString());
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).increasePlayerAmount(2.0, false));
		assertDoesNotThrow(() -> assertEquals(1, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void sellShopItemTestWithInvalidSlot() throws GeneralEconomyException {
		createPlayershop();
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSlot(-1, 8);
		assertThrows(GeneralEconomyException.class, () -> playershop.sellShopItem(-1, 1, null, true));
	}

	@Test
	public void sellShopItemTestWithEmptySlot() throws GeneralEconomyException, ShopSystemException {
		createPlayershop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForSlotIsNotEmpty(1,
				playershop.getShopInventory(), 1);
		assertThrows(GeneralEconomyException.class, () -> playershop.sellShopItem(1, 1, null, true));
	}

	@Test
	public void sellShopItemTestWithOfflinePlayer() throws EconomyPlayerException {
		createPlayershop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerIsOnline(ecoPlayer);
		assertThrows(EconomyPlayerException.class, () -> playershop.sellShopItem(1, 1, ecoPlayer, true));
	}

	@Test
	public void sellShopItemTestWithOwnerNotEnoughMoney() throws GeneralEconomyException, ShopSystemException {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		doThrow(ShopSystemException.class).when(validationHandler)
				.checkForShopOwnerHasEnoughMoney(playershop.getOwner(), 1.0);

		assertThrows(ShopSystemException.class, () -> playershop.sellShopItem(3, 1, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSlot(3, 8));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(3)).checkForSlotIsNotEmpty(3, playershop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler).checkForShopOwnerHasEnoughMoney(playershop.getOwner(), 1.0));
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(1.0, false));
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).decreasePlayerAmount(1.0, true));
		assertDoesNotThrow(() -> assertEquals(0, playershop.getShopItem(3).getStock()));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void sellShopItemTestWithSingular() {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemStack stackCloneCloneClone = mock(ItemStack.class);
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
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		when(stackCloneClone.clone()).thenReturn(stackCloneCloneClone);
		when(contentStackClone.toString()).thenReturn("itemString");
		when(stackCloneCloneClone.toString()).thenReturn("itemString");
		when(configManager.getCurrencyText(1.0)).thenReturn("$");
		when(messageWrapper.getString("shop_sell_singular", "1", 1.0, "$")).thenReturn("my message");

		assertDoesNotThrow(() -> playershop.sellShopItem(3, 1, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(generalValidator, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(4)).checkForSlotIsNotEmpty(3, playershop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler).checkForShopOwnerHasEnoughMoney(playershop.getOwner(), 1.0));
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(1.0, false));
		assertDoesNotThrow(() -> verify(playershop.getOwner()).decreasePlayerAmount(1.0, true));
		assertDoesNotThrow(() -> assertEquals(1, playershop.getShopItem(3).getStock()));
		verify(inv).removeItem(contentStackClone);
		verify(player).sendMessage("my message");
	}

	@Test
	public void sellShopItemTestOnlyBuyPrice() {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 0, 2, stack));

		assertDoesNotThrow(() -> playershop.sellShopItem(3, 1, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSlot(3, 8));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(3)).checkForSlotIsNotEmpty(3, playershop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler, never())
				.checkForShopOwnerHasEnoughMoney(eq(playershop.getOwner()), anyDouble()));
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(anyDouble(), eq(false)));
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).decreasePlayerAmount(anyDouble(), eq(true)));
		assertDoesNotThrow(() -> assertEquals(0, playershop.getShopItem(3).getStock()));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void sellShopItemTestWithPlural() {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemStack stackCloneCloneClone = mock(ItemStack.class);
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
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		when(stackCloneClone.clone()).thenReturn(stackCloneCloneClone);
		when(contentStackClone.toString()).thenReturn("itemString");
		when(stackCloneCloneClone.toString()).thenReturn("itemString");
		when(configManager.getCurrencyText(10.0)).thenReturn("$");
		when(messageWrapper.getString("shop_sell_plural", "10", 10.0, "$")).thenReturn("my message");

		assertDoesNotThrow(() -> playershop.sellShopItem(3, 10, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(generalValidator, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(4)).checkForSlotIsNotEmpty(3, playershop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(
				() -> verify(validationHandler).checkForShopOwnerHasEnoughMoney(playershop.getOwner(), 10.0));
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(10.0, false));
		assertDoesNotThrow(() -> verify(playershop.getOwner()).decreasePlayerAmount(10.0, true));
		assertDoesNotThrow(() -> assertEquals(10, playershop.getShopItem(3).getStock()));
		verify(inv).removeItem(contentStackClone);
		verify(player).sendMessage("my message");
	}

	@Test
	public void sellShopItemTestWithSingularAsOwner() {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemStack stackCloneCloneClone = mock(ItemStack.class);
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
		when(playershop.getOwner().getPlayer()).thenReturn(player);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(contentStack.clone()).thenReturn(contentStackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		when(stackCloneClone.clone()).thenReturn(stackCloneCloneClone);
		when(contentStackClone.toString()).thenReturn("itemString");
		when(stackCloneCloneClone.toString()).thenReturn("itemString");
		when(messageWrapper.getString("shop_added_item_singular", "1")).thenReturn("my message");

		assertDoesNotThrow(() -> playershop.sellShopItem(3, 1, playershop.getOwner(), true));

		assertDoesNotThrow(() -> verify(generalValidator, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(4)).checkForSlotIsNotEmpty(3, playershop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(playershop.getOwner()));
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).increasePlayerAmount(1.0, false));
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).decreasePlayerAmount(1.0, true));
		assertDoesNotThrow(() -> assertEquals(1, playershop.getShopItem(3).getStock()));
		verify(inv).removeItem(contentStackClone);
		verify(player).sendMessage("my message");
	}

	@Test
	public void sellShopItemTestWithPluralAsOwner() {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemStack stackCloneCloneClone = mock(ItemStack.class);
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
		when(playershop.getOwner().getPlayer()).thenReturn(player);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(contentStack.clone()).thenReturn(contentStackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		when(stackCloneClone.clone()).thenReturn(stackCloneCloneClone);
		when(contentStackClone.toString()).thenReturn("itemString");
		when(stackCloneCloneClone.toString()).thenReturn("itemString");
		when(messageWrapper.getString("shop_added_item_plural", "10")).thenReturn("my message");

		assertDoesNotThrow(() -> playershop.sellShopItem(3, 10, playershop.getOwner(), true));

		assertDoesNotThrow(() -> verify(generalValidator, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(4)).checkForSlotIsNotEmpty(3, playershop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(playershop.getOwner()));
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).increasePlayerAmount(10.0, false));
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).decreasePlayerAmount(10.0, true));
		assertDoesNotThrow(() -> assertEquals(10, playershop.getShopItem(3).getStock()));
		verify(inv).removeItem(contentStackClone);
		verify(player).sendMessage("my message");
	}

	@Test
	public void isOwnerTest() {
		createPlayershop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertTrue(playershop.isOwner(playershop.getOwner()));
		assertFalse(playershop.isOwner(ecoPlayer));
	}

	@Test
	public void increaseStockTest() {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack shopInvStack = mock(ItemStack.class);
		ItemMeta shopInvStackMeta = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(shopInvStack.getItemMeta()).thenReturn(shopInvStackMeta);
		when(playershop.getShopInventory().getItem(3)).thenReturn(shopInvStack);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));

		assertDoesNotThrow(() -> playershop.increaseStock(3, 1));

		assertDoesNotThrow(() -> verify(generalValidator).checkForPositiveValue(1));
		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSlot(3, 8));
		verify(shopDao).saveStock("item string".hashCode(), 1);
		verify(shopInvStackMeta).setLore(Arrays.asList("§a1§6 Item"));
	}

	@Test
	public void increaseStockTestWithInvalidStock() throws GeneralEconomyException {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack shopInvStack = mock(ItemStack.class);
		ItemMeta shopInvStackMeta = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(shopInvStack.getItemMeta()).thenReturn(shopInvStackMeta);
		when(playershop.getShopInventory().getItem(3)).thenReturn(shopInvStack);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		reset(shopDao);
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForPositiveValue(-10);
		assertThrows(GeneralEconomyException.class, () -> playershop.increaseStock(3, -10));

		verify(shopDao, never()).saveStock(eq("item string".hashCode()), anyInt());
	}

	@Test
	public void decreaseStockTest() {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack shopInvStack = mock(ItemStack.class);
		ItemMeta shopInvStackMeta = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(shopInvStack.getItemMeta()).thenReturn(shopInvStackMeta);
		when(playershop.getShopInventory().getItem(3)).thenReturn(shopInvStack);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(shopInvStackMeta.getLore()).thenReturn(new ArrayList<>(Arrays.asList("buy", "sell")));
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));

		assertDoesNotThrow(() -> playershop.increaseStock(3, 10));
		assertDoesNotThrow(() -> playershop.decreaseStock(3, 5));

		assertDoesNotThrow(() -> verify(generalValidator).checkForPositiveValue(5));
		assertDoesNotThrow(() -> verify(generalValidator, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidStockDecrease(10, 5));
		verify(shopDao).saveStock("item string".hashCode(), 5);
		verify(shopInvStackMeta, times(3)).setLore(Arrays.asList("buy", "sell", "§a5§6 Items"));
		assertDoesNotThrow(() -> assertEquals(5, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void decreaseStockTestWithToSingular() {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack shopInvStack = mock(ItemStack.class);
		ItemMeta shopInvStackMeta = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(shopInvStack.getItemMeta()).thenReturn(shopInvStackMeta);
		when(playershop.getShopInventory().getItem(3)).thenReturn(shopInvStack);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));

		assertDoesNotThrow(() -> playershop.increaseStock(3, 10));
		assertDoesNotThrow(() -> playershop.decreaseStock(3, 9));

		assertDoesNotThrow(() -> verify(generalValidator).checkForPositiveValue(9));
		assertDoesNotThrow(() -> verify(generalValidator, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidStockDecrease(10, 9));
		verify(shopDao).saveStock("item string".hashCode(), 1);
		verify(shopInvStackMeta).setLore(Arrays.asList("§a1§6 Item"));
		assertDoesNotThrow(() -> assertEquals(1, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void decreaseStockTestWithInvalidStock() throws GeneralEconomyException {
		createPlayershop();
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForPositiveValue(-10);
		assertThrows(GeneralEconomyException.class, () -> playershop.decreaseStock(0, -10));
		verify(shopDao, never()).saveStock(anyInt(), anyInt());
	}

	@Test
	public void decreaseStockTestWithInvalidSlot() throws GeneralEconomyException {
		createPlayershop();
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSlot(-10, 8);
		assertThrows(GeneralEconomyException.class, () -> playershop.decreaseStock(-10, 1));
		verify(shopDao, never()).saveStock(anyInt(), anyInt());
	}

	@Test
	public void isAvailableTest() {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack shopInvStack = mock(ItemStack.class);
		ItemMeta shopInvStackMeta = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(shopInvStack.getItemMeta()).thenReturn(shopInvStackMeta);
		when(playershop.getShopInventory().getItem(3)).thenReturn(shopInvStack);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> playershop.addShopItem(4, 1, 2, stack));

		assertDoesNotThrow(() -> playershop.increaseStock(3, 1));

		assertDoesNotThrow(() -> assertTrue(playershop.isAvailable(3)));
		assertDoesNotThrow(() -> assertFalse(playershop.isAvailable(4)));
		assertDoesNotThrow(() -> verify(generalValidator, times(2)).checkForValidSlot(3, 8));
	}

	@Test
	public void isAvailableTestWithInvalidSlot() throws GeneralEconomyException {
		createPlayershop();
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSlot(8, 8);
		assertThrows(GeneralEconomyException.class, () -> assertTrue(playershop.isAvailable(8)));
	}

	@Test
	public void changeOwnerTest() {
		createPlayershop();
		EconomyPlayer newOwner = mock(EconomyPlayer.class);
		when(newOwner.getName()).thenReturn("wejink");
		when(playershopManager.getPlayerShopUniqueNameList()).thenReturn(new ArrayList<>());
		assertDoesNotThrow(() -> playershop.changeOwner(newOwner));
		assertEquals(newOwner, playershop.getOwner());
		verify(shopDao).saveOwner(newOwner);
		verify(playershop.getShopVillager()).setCustomName("myshop_wejink");
		assertDoesNotThrow(
				() -> verify(validationHandler).checkForChangeOwnerIsPossible(new ArrayList<>(), newOwner, "myshop"));
	}

	@Test
	public void changeOwnerTestNoPossible() throws ShopSystemException {
		createPlayershop();
		EconomyPlayer newOwner = mock(EconomyPlayer.class);
		doThrow(ShopSystemException.class).when(validationHandler).checkForChangeOwnerIsPossible(new ArrayList<>(),
				newOwner, "myshop");
		assertThrows(ShopSystemException.class, () -> playershop.changeOwner(newOwner));
		assertFalse(newOwner.equals(playershop.getOwner()));
		verify(shopDao, never()).saveOwner(newOwner);
	}

	@Test
	public void addItemTest() {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(playershop.getShopInventory().getItem(0)).thenReturn(stackClone);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> playershop.addShopItem(0, 1, 4, stack));
		verify(shopDao).saveStock("item string".hashCode(), 0);
		assertDoesNotThrow(() -> verify(validationHandler).checkForItemDoesNotExist(eq("item string".hashCode()), anyList()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsEmpty(0, playershop.getShopInventory(), 2));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidPrice("1.0"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidPrice("4.0"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(1.0, 4.0));
		assertDoesNotThrow(() -> assertEquals(1, playershop.getItemList().size()));
		ShopItem shopItem = assertDoesNotThrow(() -> playershop.getShopItem(0));
		verify(shopDao).saveShopItem(shopItem, false);
		assertEquals(2, shopItem.getAmount());
		assertEquals(0, shopItem.getStock());
		assertEquals(4.0, shopItem.getBuyPrice());
		assertEquals(1.0, shopItem.getSellPrice());
		assertEquals(0, shopItem.getSlot());
		assertEquals("item string".hashCode(), shopItem.getItemHash());
		assertEquals(stackCloneClone, shopItem.getItemStack());
		// verify that the set occupied method of the editor is called
		verify(customSkullService).getSkullWithName(SkullTextureEnum.SLOTFILLED, "Slot 1");
		verify(playershop.getShopInventory()).setItem(0, stackClone);
		verify(stackClone).setAmount(2);
		verify(stackMetaClone).setLore(Arrays.asList("§62 buy for §a4.0 $", "§62 sell for §a1.0 $"));
		verify(stackMetaClone).setLore(Arrays.asList("§a0§6 Items"));
	}

	@Test
	public void changeSizeTest() {
		createPlayershop();

		assertDoesNotThrow(() -> playershop.changeShopSize(18));

		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSize(18));
		assertDoesNotThrow(
				() -> verify(validationHandler).checkForResizePossible(playershop.getShopInventory(), 9, 18, 2));
		assertEquals(18, playershop.getSize());
		verify(shopDao).saveShopSize(18);
		verify(serverProvider).createInventory(playershop.getShopVillager(), 18, "myshop");
		verify(serverProvider).createInventory(playershop.getShopVillager(), 18, "myshop-Editor");
	}

	@Test
	public void changeSizeTestWithResizeNotPossible() throws ShopSystemException, GeneralEconomyException {
		createPlayershop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForResizePossible(playershop.getShopInventory(),
				9, 18, 2);
		assertThrows(ShopSystemException.class, () -> playershop.changeShopSize(18));
		assertEquals(9, playershop.getSize());
	}

	@Test
	public void moveShopTest() {
		createPlayershop();
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(world.getName()).thenReturn("world");
		when(loc.getWorld()).thenReturn(world);
		when(townworldManager.isTownWorld("world")).thenReturn(false);

		assertDoesNotThrow(() -> playershop.moveShop(loc));

		assertEquals(loc, playershop.getShopLocation());
		verify(shopDao).saveShopLocation(loc);
		verify(playershop.getShopVillager()).teleport(loc);
	}

	@Test
	public void moveShopTestWithNoPlotPermission() throws EconomyPlayerException, TownSystemException {
		createPlayershop();
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(world.getName()).thenReturn("world");
		when(loc.getWorld()).thenReturn(world);
		when(townworldManager.isTownWorld("world")).thenReturn(true);
		doThrow(TownSystemException.class).when(validationHandler).checkForPlayerHasPermissionAtLocation(loc,
				playershop.getOwner());

		assertThrows(TownSystemException.class, () -> playershop.moveShop(loc));

		assertFalse(loc.equals(playershop.getShopLocation()));
		verify(shopDao, never()).saveShopLocation(loc);
		verify(playershop.getShopVillager(), never()).teleport(loc);
	}

	@Test
	public void moveShopTestWithPermission() {
		createPlayershop();
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(world.getName()).thenReturn("world");
		when(loc.getWorld()).thenReturn(world);
		when(townworldManager.isTownWorld("world")).thenReturn(true);

		assertDoesNotThrow(() -> playershop.moveShop(loc));

		assertEquals(loc, playershop.getShopLocation());
		verify(shopDao).saveShopLocation(loc);
		verify(playershop.getShopVillager()).teleport(loc);
	}

	@Test
	public void changeShopNameTestWithInvalidName() throws ShopSystemException {
		createPlayershop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForValidShopName("my_name");
		assertThrows(ShopSystemException.class, () -> playershop.changeShopName("my_name"));
		assertEquals("myshop", playershop.getName());
		verify(shopDao, never()).saveShopName("my_shop");
	}

	@Test
	public void changeShopNameTestWithExistingName() throws GeneralEconomyException {
		createPlayershop();
		when(playershopManager.getPlayerShopUniqueNameList()).thenReturn(new ArrayList<>());
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForShopNameIsFree(new ArrayList<>(),
				"newShop", playershop.getOwner());
		assertThrows(GeneralEconomyException.class, () -> playershop.changeShopName("newShop"));
		assertEquals("myshop", playershop.getName());
		verify(shopDao, never()).saveShopName("newShop");
	}

	@Test
	public void changeShopNameTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory editor = mock(Inventory.class);
		Inventory slotEditor = mock(Inventory.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editor);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(slotEditor);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(customSkullService.getSkullWithName(any(SkullTextureEnum.class), anyString())).thenReturn(infoItem);
		playershop.setupNew("myshop", ecoPlayer, "P0", loc, 9);

		Inventory invNew = mock(Inventory.class);
		Inventory editorNew = mock(Inventory.class);
		Inventory slotEditorNew = mock(Inventory.class);

		when(serverProvider.createInventory(playershop.getShopVillager(), 9, "newName")).thenReturn(invNew);
		when(serverProvider.createInventory(playershop.getShopVillager(), 9, "newName-Editor")).thenReturn(editorNew);
		when(serverProvider.createInventory(playershop.getShopVillager(), 27, "newName-SlotEditor"))
				.thenReturn(slotEditorNew);
		when(playershop.getOwner().getName()).thenReturn("catch441");

		assertDoesNotThrow(() -> playershop.changeShopName("newName"));

		assertEquals("newName", playershop.getName());
		verify(shopDao).saveShopName("newName");
		verify(invNew).setContents(inv.getContents());
		verify(editorNew).setContents(editor.getContents());
		verify(slotEditorNew).setContents(slotEditor.getContents());

		verify(playershop.getShopVillager()).setCustomName("newName_catch441");

	}

	@Test
	public void removeItemTest() {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		ItemMeta stackMeta = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stack.getItemMeta()).thenReturn(stackMeta);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(playershop.getShopInventory().getItem(3)).thenReturn(stack);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		reset(playershop.getShopInventory());
		assertDoesNotThrow(() -> playershop.removeShopItem(3));

		assertDoesNotThrow(() -> verify(validationHandler).checkForItemCanBeDeleted(3, 9));
		assertDoesNotThrow(() -> verify(generalValidator, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(5)).checkForSlotIsNotEmpty(3, playershop.getShopInventory(), 1));
		verify(playershop.getShopInventory()).clear(3);
		verify(shopDao).saveShopItem(any(), eq(true));
		assertDoesNotThrow(() -> assertEquals(0, playershop.getItemList().size()));
	}
}
