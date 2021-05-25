package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
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
import java.util.HashSet;

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
import org.ue.bank.logic.api.BankException;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.InventoryGuiHandler;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.UltimateEconomyProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.shopsystem.dataaccess.api.ShopDao;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.ShopEditorHandler;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopSlotEditorHandler;
import org.ue.shopsystem.logic.api.ShopValidator;
import org.ue.shopsystem.logic.api.ShopsystemException;
import org.ue.townsystem.logic.api.TownworldManager;

@ExtendWith(MockitoExtension.class)
public class PlayershopImplTest {

	@InjectMocks
	PlayershopImpl playershop;
	@Mock
	ServerProvider serverProvider;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ShopValidator validationHandler;
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

	private class Mocks {
		Villager villager;
		Inventory inventory;
		ShopEditorHandler editorHandler;

		public Mocks(Villager villager, Inventory inventory, ShopEditorHandler editorHandler) {
			this.villager = villager;
			this.inventory = inventory;
			this.editorHandler = editorHandler;
		}
	}

	@Test
	public void setupNewTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta infoItemMeta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		ShopEditorHandler editorHandler = mock(ShopEditorHandler.class);
		ShopSlotEditorHandler slotEditorHandler = mock(ShopSlotEditorHandler.class);
		Inventory backLink = mock(Inventory.class);
		InventoryGuiHandler customizer = mock(InventoryGuiHandler.class);
		when(provider.createEconomyVillagerCustomizeHandler(playershop, null, Profession.NITWIT)).thenReturn(customizer);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(editorHandler.getInventory()).thenReturn(backLink);
		when(provider.createShopEditorHandler()).thenReturn(editorHandler);
		when(provider.createShopSlotEditorHandler(backLink)).thenReturn(slotEditorHandler);
		when(ecoPlayer.getName()).thenReturn("catch441");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(infoItem);

		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(infoItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		playershop.setupNew("myshop", ecoPlayer, "P0", loc, 9);

		verify(customizer).updateBackLink(backLink);;
		verify(editorHandler).setup(playershop, 1);
		verify(slotEditorHandler).setupSlotEditor(playershop);
		verify(shopDao).setupSavefile("P0");
		verify(shopDao).saveLocation("", loc);
		verify(shopDao).saveShopName("myshop");
		verify(shopDao).saveOwner(ecoPlayer);
		verify(villager).setCustomName("myshop_catch441");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setSilent(true);
		verify(villager).setVillagerLevel(2);
		verify(villager).setCollidable(false);
		verify(villager).setInvulnerable(true);
		verify(villager).setProfession(Profession.NITWIT);
		verify(villager).setMetadata(eq("ue-id"), any(FixedMetadataValue.class));
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		assertEquals("P0", playershop.getId());
		assertEquals(loc, playershop.getLocation());
		assertEquals(ecoPlayer, playershop.getOwner());
		assertEquals("myshop", playershop.getName());

		verify(infoItemMeta).setDisplayName("Info");
		verify(infoItem).setItemMeta(infoItemMeta);
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
		ItemStack infoItem = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		ItemMeta shopItemStackMeta = mock(ItemMeta.class);
		ItemMeta infoItemMeta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ShopItemImpl shopItem = mock(ShopItemImpl.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		ShopEditorHandler editorHandler = mock(ShopEditorHandler.class);
		ShopSlotEditorHandler slotEditorHandler = mock(ShopSlotEditorHandler.class);
		Inventory backLink = mock(Inventory.class);
		InventoryGuiHandler customizer = mock(InventoryGuiHandler.class);
		when(provider.createEconomyVillagerCustomizeHandler(playershop, null, Profession.ARMORER)).thenReturn(customizer);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(editorHandler.getInventory()).thenReturn(backLink);
		when(provider.createShopEditorHandler()).thenReturn(editorHandler);
		when(provider.createShopSlotEditorHandler(backLink)).thenReturn(slotEditorHandler);
		when(ecoPlayer.getName()).thenReturn("catch441");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(infoItem);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(shopItemStack.getItemMeta()).thenReturn(shopItemStackMeta);
		when(infoItem.getItemMeta()).thenReturn(infoItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);

		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(shopDao.loadShopName()).thenReturn("myshop");
		when(shopDao.loadSize("")).thenReturn(9);
		assertDoesNotThrow(() -> when(shopDao.loadLocation("")).thenReturn(loc));
		when(shopDao.loadProfession("")).thenReturn(Profession.ARMORER);
		when(shopDao.loadOwner()).thenReturn("catch441");
		when(shopDao.loadItemSlotList()).thenReturn(Arrays.asList(0));
		when(shopDao.loadItem(0)).thenReturn(shopItem);
		when(shopDao.loadStock(0)).thenReturn(7);
		when(shopDao.loadVisible("")).thenReturn(true);
		when(shopItem.getSlot()).thenReturn(0);
		when(shopItem.getAmount()).thenReturn(5);
		when(shopItem.getSellPrice()).thenReturn(2.0);
		when(shopItem.getBuyPrice()).thenReturn(3.0);
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(inv.getItem(0)).thenReturn(shopItemStack);
		assertDoesNotThrow(() -> playershop.setupExisting("P0"));

		verify(customizer).updateBackLink(backLink);
		verify(editorHandler).setup(playershop, 1);
		verify(slotEditorHandler).setupSlotEditor(playershop);
		verify(shopDao).setupSavefile("P0");
		verify(villager).setCustomName("myshop_catch441");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setSilent(true);
		verify(villager).setVillagerLevel(2);
		verify(villager).setCollidable(false);
		verify(villager).setInvulnerable(true);
		verify(villager).setProfession(Profession.ARMORER);
		verify(villager).setMetadata(eq("ue-id"), any(FixedMetadataValue.class));
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		assertEquals("P0", playershop.getId());
		assertEquals(loc, playershop.getLocation());
		assertEquals(ecoPlayer, playershop.getOwner());
		assertEquals("myshop", playershop.getName());
		verify(shopItem).setStock(7);
		assertDoesNotThrow(() -> assertEquals(shopItem, playershop.getShopItem(0)));

		verify(infoItemMeta).setDisplayName("Info");
		verify(infoItem).setItemMeta(infoItemMeta);
		verify(infoItemMeta).setLore(Arrays.asList("§6Rightclick: §asell specified amount",
				"§6Shift-Rightclick: §asell all", "§6Leftclick: §abuy"));
		verify(inv).setItem(8, infoItem);

		verify(shopItemStack, times(2)).setItemMeta(shopItemStackMeta);
		verify(shopItemStackMeta).setLore(Arrays.asList("§65 buy for §a3.0 $", "§65 sell for §a2.0 $"));
		verify(shopItemStackMeta).setLore(Arrays.asList("§a0§6 Items"));
		verify(inv).setItem(0, shopItemStack);
	}

	@Test
	public void buyShopItemTestWithTooSmallStock() throws ShopsystemException {
		createPlayershop();
		addShopItem(null, 0, 1, 2);

		doThrow(ShopsystemException.class).when(validationHandler).checkForValidStockDecrease(0, 1);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);

		assertThrows(ShopsystemException.class, () -> playershop.buyShopItem(0, ecoPlayer, true));

		verify(player, never()).sendMessage(anyString());
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithInvalidSlot() throws ShopsystemException {
		createPlayershop();
		addShopItem(null, 0, 1, 2);

		doThrow(ShopsystemException.class).when(validationHandler).checkForValidSlot(8, 8);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertThrows(ShopsystemException.class, () -> playershop.buyShopItem(8, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithEmptySlot() throws ShopsystemException {
		createPlayershop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForSlotIsNotEmpty(new HashSet<Integer>(), 3);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertThrows(ShopsystemException.class, () -> playershop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithOfflinePlayer() throws ShopsystemException {
		createPlayershop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(ShopsystemException.class).when(validationHandler).checkForPlayerIsOnline(ecoPlayer);
		assertThrows(ShopsystemException.class, () -> playershop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithFullInventory() throws ShopsystemException {
		createPlayershop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		doThrow(ShopsystemException.class).when(validationHandler).checkForPlayerInventoryNotFull(inv);
		assertThrows(ShopsystemException.class, () -> playershop.buyShopItem(3, ecoPlayer, true));

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
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));

		assertDoesNotThrow(() -> playershop.increaseStock(3, 1));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(configManager.getCurrencyText(2.0)).thenReturn("$");
		when(messageWrapper.getString(MessageEnum.SHOP_BUY_SINGULAR, "1", 2.0, "$")).thenReturn("my message");
		reset(validationHandler);
		assertDoesNotThrow(() -> playershop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidStockDecrease(1, 1));
		assertDoesNotThrow(() -> verify(validationHandler, times(4)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler, times(4))
				.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(3)), 3));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(1);
		verify(inv).addItem(stackCloneClone);
		verify(shopDao, times(2)).saveStock(3, 0);
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
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 4, stack));
		assertDoesNotThrow(() -> playershop.increaseStock(3, 2));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(configManager.getCurrencyText(4.0)).thenReturn("$");
		when(messageWrapper.getString(MessageEnum.SHOP_BUY_PLURAL, "2", 4.0, "$")).thenReturn("my message");
		reset(validationHandler);
		assertDoesNotThrow(() -> playershop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidStockDecrease(2, 2));
		assertDoesNotThrow(() -> verify(validationHandler, times(4)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler, times(4))
				.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(3)), 3));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(2);
		verify(inv).addItem(stackCloneClone);
		verify(shopDao, times(2)).saveStock(3, 0);
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
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 0, stack));
		assertDoesNotThrow(() -> playershop.increaseStock(3, 2));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		reset(validationHandler);
		assertDoesNotThrow(() -> playershop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, never()).checkForValidStockDecrease(anyInt(), anyInt()));
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler, times(2))
				.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(3)), 3));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		// only at setup
		verify(shopDao, times(1)).saveStock(3, 0);

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
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> playershop.increaseStock(3, 1));
		when(playershop.getOwner().getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(messageWrapper.getString(MessageEnum.SHOP_GOT_ITEM_SINGULAR, "1")).thenReturn("my message");
		reset(validationHandler);
		assertDoesNotThrow(() -> playershop.buyShopItem(3, playershop.getOwner(), true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidStockDecrease(1, 1));
		assertDoesNotThrow(() -> verify(validationHandler, times(4)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(playershop.getOwner()));
		assertDoesNotThrow(() -> verify(validationHandler, times(4))
				.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(3)), 3));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(1);
		verify(inv).addItem(stackCloneClone);
		verify(shopDao, times(2)).saveStock(3, 0);
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
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> playershop.increaseStock(3, 3));
		when(playershop.getOwner().getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(messageWrapper.getString(MessageEnum.SHOP_GOT_ITEM_PLURAL, "2")).thenReturn("my message");
		reset(validationHandler);
		assertDoesNotThrow(() -> playershop.buyShopItem(3, playershop.getOwner(), true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidStockDecrease(3, 2));
		assertDoesNotThrow(() -> verify(validationHandler, times(4)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(playershop.getOwner()));
		assertDoesNotThrow(() -> verify(validationHandler, times(4))
				.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(3)), 3));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(2);
		verify(inv).addItem(stackCloneClone);
		verify(shopDao, times(1)).saveStock(3, 1);
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
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> playershop.increaseStock(3, 3));
		when(playershop.getOwner().getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(messageWrapper.getString(MessageEnum.SHOP_GOT_ITEM_PLURAL, "3")).thenReturn("my message");
		reset(validationHandler);
		assertDoesNotThrow(() -> playershop.buyShopItem(3, playershop.getOwner(), true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidStockDecrease(3, 3));
		assertDoesNotThrow(() -> verify(validationHandler, times(4)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(playershop.getOwner()));
		assertDoesNotThrow(() -> verify(validationHandler, times(4))
				.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(3)), 3));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(3);
		verify(inv).addItem(stackCloneClone);
		verify(shopDao, times(2)).saveStock(3, 0);
		verify(player).sendMessage("my message");
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).increasePlayerAmount(2.0, false));
		assertDoesNotThrow(() -> assertEquals(0, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void buyShopItemTestWithNotEnoughMoney() throws EconomyPlayerException, BankException {
		createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(stack.getAmount()).thenReturn(1);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> playershop.increaseStock(3, 1));
		reset(shopDao);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		doThrow(EconomyPlayerException.class).when(ecoPlayer).decreasePlayerAmount(2.0, true);
		reset(validationHandler);
		assertThrows(EconomyPlayerException.class, () -> playershop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler).checkForValidStockDecrease(1, 1));
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler, times(2))
				.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(3)), 3));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone, never()).setAmount(anyInt());
		verify(shopDao, never()).saveStock(3, 0);
		verify(player, never()).sendMessage(anyString());
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).increasePlayerAmount(2.0, false));
		assertDoesNotThrow(() -> assertEquals(1, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void sellShopItemTestWithInvalidSlot() throws ShopsystemException {
		createPlayershop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForValidSlot(-1, 8);
		assertThrows(ShopsystemException.class, () -> playershop.sellShopItem(-1, 1, null, true));
	}

	@Test
	public void sellShopItemTestWithEmptySlot() throws ShopsystemException {
		createPlayershop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForSlotIsNotEmpty(new HashSet<Integer>(), 1);
		assertThrows(ShopsystemException.class, () -> playershop.sellShopItem(1, 1, null, true));
	}

	@Test
	public void sellShopItemTestWithOfflinePlayer() throws ShopsystemException {
		createPlayershop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(ShopsystemException.class).when(validationHandler).checkForPlayerIsOnline(ecoPlayer);
		assertThrows(ShopsystemException.class, () -> playershop.sellShopItem(1, 1, ecoPlayer, true));
	}

	@Test
	public void sellShopItemTestWithOwnerNotEnoughMoney() throws ShopsystemException {
		createPlayershop();
		addShopItem(null, 3, 1, 2);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(ShopsystemException.class).when(validationHandler)
				.checkForShopOwnerHasEnoughMoney(playershop.getOwner(), 1.0);
		assertThrows(ShopsystemException.class, () -> playershop.sellShopItem(3, 1, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler, times(2))
				.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(3)), 3));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler).checkForShopOwnerHasEnoughMoney(playershop.getOwner(), 1.0));
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(1.0, false));
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).decreasePlayerAmount(1.0, true));
		assertDoesNotThrow(() -> assertEquals(0, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void sellShopItemTestWithSingular() {
		createPlayershop();
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
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(contentStack.clone()).thenReturn(contentStackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		when(configManager.getCurrencyText(1.0)).thenReturn("$");
		when(messageWrapper.getString(MessageEnum.SHOP_SELL_SINGULAR, "1", 1.0, "$")).thenReturn("my message");
		when(stackCloneClone.isSimilar(contentStack)).thenReturn(true);
		reset(validationHandler);
		assertDoesNotThrow(() -> playershop.sellShopItem(3, 1, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(4)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler, times(4))
				.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(3)), 3));
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
		addShopItem(null, 3, 0, 2);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);

		assertDoesNotThrow(() -> playershop.sellShopItem(3, 1, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler, times(2))
				.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(3)), 3));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler, never())
				.checkForShopOwnerHasEnoughMoney(eq(playershop.getOwner()), anyDouble()));
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(anyDouble(), eq(false)));
		assertDoesNotThrow(() -> verify(playershop.getOwner(), never()).decreasePlayerAmount(anyDouble(), eq(true)));
		assertDoesNotThrow(() -> assertEquals(0, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void sellShopItemTestWithPlural() {
		createPlayershop();
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
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(contentStack.clone()).thenReturn(contentStackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));

		when(configManager.getCurrencyText(10.0)).thenReturn("$");
		when(messageWrapper.getString(MessageEnum.SHOP_SELL_PLURAL, "10", 10.0, "$")).thenReturn("my message");
		when(stackCloneClone.isSimilar(contentStack)).thenReturn(true);
		reset(validationHandler);
		assertDoesNotThrow(() -> playershop.sellShopItem(3, 10, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(4)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler, times(4))
				.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(3)), 3));
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
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(contentStack.clone()).thenReturn(contentStackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		when(messageWrapper.getString(MessageEnum.SHOP_ADDED_ITEM_SINGULAR, "1")).thenReturn("my message");
		when(stackCloneClone.isSimilar(contentStack)).thenReturn(true);
		reset(validationHandler);
		assertDoesNotThrow(() -> playershop.sellShopItem(3, 1, playershop.getOwner(), true));

		assertDoesNotThrow(() -> verify(validationHandler, times(4)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler, times(4))
				.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(3)), 3));
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
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(contentStack.clone()).thenReturn(contentStackClone);
		assertDoesNotThrow(() -> playershop.addShopItem(3, 1, 2, stack));
		when(messageWrapper.getString(MessageEnum.SHOP_ADDED_ITEM_PLURAL, "10")).thenReturn("my message");
		when(stackCloneClone.isSimilar(contentStack)).thenReturn(true);
		reset(validationHandler);
		assertDoesNotThrow(() -> playershop.sellShopItem(3, 10, playershop.getOwner(), true));

		assertDoesNotThrow(() -> verify(validationHandler, times(4)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler, times(4))
				.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(3)), 3));
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
		Mocks mocks = createPlayershop();
		ItemMeta meta = addShopItem(mocks.inventory, 3, 1, 2);
		assertDoesNotThrow(() -> playershop.increaseStock(3, 1));

		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(1.0));
		assertDoesNotThrow(() -> verify(validationHandler, times(3)).checkForValidSlot(3, 8));
		verify(shopDao).saveStock(3, 1);
		verify(meta).setLore(Arrays.asList("§a1§6 Item"));
	}

	@Test
	public void increaseStockTestWithInvalidStock() throws ShopsystemException {
		createPlayershop();
		reset(shopDao);
		doThrow(ShopsystemException.class).when(validationHandler).checkForPositiveValue(-10.0);
		assertThrows(ShopsystemException.class, () -> playershop.increaseStock(3, -10));

		verify(shopDao, never()).saveStock(eq(3), anyInt());
	}

	@Test
	public void decreaseStockTest() {
		Mocks mocks = createPlayershop();
		ItemMeta meta = addShopItem(mocks.inventory, 3, 1, 2);
		when(meta.getLore()).thenReturn(new ArrayList<>(Arrays.asList("buy", "sell")));

		assertDoesNotThrow(() -> playershop.increaseStock(3, 10));
		reset(validationHandler);
		assertDoesNotThrow(() -> playershop.decreaseStock(3, 5));

		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(5.0));
		assertDoesNotThrow(() -> verify(validationHandler, times(3)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidStockDecrease(10, 5));
		verify(shopDao).saveStock(3, 5);
		verify(meta, times(2)).setLore(Arrays.asList("buy", "sell", "§a5§6 Items"));
		assertDoesNotThrow(() -> assertEquals(5, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void decreaseStockTestWithToSingular() {
		Mocks mocks = createPlayershop();
		ItemMeta meta = addShopItem(mocks.inventory, 3, 1, 2);

		assertDoesNotThrow(() -> playershop.increaseStock(3, 10));
		reset(validationHandler);
		assertDoesNotThrow(() -> playershop.decreaseStock(3, 9));

		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(9.0));
		assertDoesNotThrow(() -> verify(validationHandler, times(3)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidStockDecrease(10, 9));
		verify(shopDao).saveStock(3, 1);
		verify(meta).setLore(Arrays.asList("§a1§6 Item"));
		assertDoesNotThrow(() -> assertEquals(1, playershop.getShopItem(3).getStock()));
	}

	@Test
	public void decreaseStockTestWithInvalidStock() throws ShopsystemException {
		createPlayershop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForPositiveValue(-10.0);
		assertThrows(ShopsystemException.class, () -> playershop.decreaseStock(0, -10));
		verify(shopDao, never()).saveStock(anyInt(), anyInt());
	}

	@Test
	public void decreaseStockTestWithInvalidSlot() throws ShopsystemException {
		createPlayershop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForValidSlot(-10, 8);
		assertThrows(ShopsystemException.class, () -> playershop.decreaseStock(-10, 1));
		verify(shopDao, never()).saveStock(anyInt(), anyInt());
	}

	@Test
	public void isAvailableTest() {
		Mocks mocks = createPlayershop();
		addShopItem(mocks.inventory, 3, 1, 2);
		addShopItem(mocks.inventory, 4, 1, 2);
		assertDoesNotThrow(() -> playershop.increaseStock(3, 1));
		reset(validationHandler);
		assertDoesNotThrow(() -> assertTrue(playershop.isAvailable(3)));
		assertDoesNotThrow(() -> assertFalse(playershop.isAvailable(4)));
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 8));
	}

	@Test
	public void isAvailableTestWithInvalidSlot() throws ShopsystemException {
		createPlayershop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForValidSlot(8, 8);
		assertThrows(ShopsystemException.class, () -> assertTrue(playershop.isAvailable(8)));
	}

	@Test
	public void changeOwnerTest() {
		Mocks mocks = createPlayershop();
		EconomyPlayer newOwner = mock(EconomyPlayer.class);
		when(newOwner.getName()).thenReturn("wejink");
		when(playershopManager.getPlayerShopUniqueNameList()).thenReturn(new ArrayList<>());
		assertDoesNotThrow(() -> playershop.changeOwner(newOwner));
		assertEquals(newOwner, playershop.getOwner());
		verify(shopDao).saveOwner(newOwner);
		verify(mocks.villager).setCustomName("myshop_wejink");
		assertDoesNotThrow(
				() -> verify(validationHandler).checkForChangeOwnerIsPossible(new ArrayList<>(), newOwner, "myshop"));
	}

	@Test
	public void changeOwnerTestNoPossible() throws ShopsystemException {
		createPlayershop();
		EconomyPlayer newOwner = mock(EconomyPlayer.class);
		doThrow(ShopsystemException.class).when(validationHandler).checkForChangeOwnerIsPossible(new ArrayList<>(),
				newOwner, "myshop");
		assertThrows(ShopsystemException.class, () -> playershop.changeOwner(newOwner));
		assertFalse(newOwner.equals(playershop.getOwner()));
		verify(shopDao, never()).saveOwner(newOwner);
	}

	@Test
	public void addItemTest() {
		Mocks mocks = createPlayershop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(mocks.inventory.getItem(0)).thenReturn(stackClone);
		when(stack.getAmount()).thenReturn(2);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> playershop.addShopItem(0, 1, 4, stack));

		verify(shopDao).saveStock(0, 0);
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsEmpty(anySet(), eq(0)));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(1.0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(4.0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(1.0, 4.0));
		assertDoesNotThrow(() -> assertEquals(1, playershop.getItemList().size()));
		ShopItem shopItem = assertDoesNotThrow(() -> playershop.getShopItem(0));
		verify(shopDao).saveShopItem(shopItem, false);
		assertEquals(2, shopItem.getAmount());
		assertEquals(0, shopItem.getStock());
		assertEquals(4.0, shopItem.getBuyPrice());
		assertEquals(1.0, shopItem.getSellPrice());
		assertEquals(0, shopItem.getSlot());
		assertEquals(stackCloneClone, shopItem.getItemStack());
		verify(mocks.editorHandler).setOccupied(true, 0);
	}

	@Test
	public void changeSizeTest() {
		Mocks mocks = createPlayershop();
		reset(mocks.editorHandler);
		assertDoesNotThrow(() -> playershop.changeSize(18));

		assertDoesNotThrow(() -> verify(validationHandler).checkForValidSize(18));
		assertDoesNotThrow(() -> verify(validationHandler).checkForResizePossible(mocks.inventory, 9, 18, 1));
		
		assertEquals(18, playershop.getSize());
		verify(shopDao).saveSize("", 18);
		verify(serverProvider).createInventory(mocks.villager, 18, "myshop");
		verify(mocks.editorHandler).setup(playershop, 1);
	}

	@Test
	public void changeSizeTestWithResizeNotPossible() throws ShopsystemException {
		Mocks mocks = createPlayershop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForResizePossible(mocks.inventory, 9, 18, 1);
		assertThrows(ShopsystemException.class, () -> playershop.changeSize(18));
		assertEquals(9, playershop.getSize());
	}

	@Test
	public void moveShopTest() {
		Mocks mocks = createPlayershop();
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(world.getName()).thenReturn("world");
		when(loc.getWorld()).thenReturn(world);
		when(townworldManager.isTownWorld("world")).thenReturn(false);

		assertDoesNotThrow(() -> playershop.changeLocation(loc));

		assertEquals(loc, playershop.getLocation());
		verify(shopDao).saveLocation("", loc);
		verify(mocks.villager).teleport(loc);
	}

	@Test
	public void moveShopTestWithNoPlotPermission() throws ShopsystemException {
		Mocks mocks = createPlayershop();
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(world.getName()).thenReturn("world");
		when(loc.getWorld()).thenReturn(world);
		when(townworldManager.isTownWorld("world")).thenReturn(true);
		doThrow(ShopsystemException.class).when(validationHandler).checkForPlayerHasPermissionAtLocation(loc,
				playershop.getOwner());

		assertThrows(ShopsystemException.class, () -> playershop.changeLocation(loc));

		assertFalse(loc.equals(playershop.getLocation()));
		verify(shopDao, never()).saveLocation("", loc);
		verify(mocks.villager, never()).teleport(loc);
	}

	@Test
	public void moveShopTestWithPermission() {
		Mocks mocks = createPlayershop();
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(world.getName()).thenReturn("world");
		when(loc.getWorld()).thenReturn(world);
		when(townworldManager.isTownWorld("world")).thenReturn(true);

		assertDoesNotThrow(() -> playershop.changeLocation(loc));

		assertEquals(loc, playershop.getLocation());
		verify(shopDao).saveLocation("", loc);
		verify(mocks.villager).teleport(loc);
	}

	@Test
	public void changeShopNameTestWithInvalidName() throws ShopsystemException {
		createPlayershop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForValidShopName("my_name");
		assertThrows(ShopsystemException.class, () -> playershop.changeShopName("my_name"));
		assertEquals("myshop", playershop.getName());
		verify(shopDao, never()).saveShopName("my_shop");
	}

	@Test
	public void changeShopNameTestWithExistingName() throws ShopsystemException {
		createPlayershop();
		when(playershopManager.getPlayerShopUniqueNameList()).thenReturn(new ArrayList<>());
		doThrow(ShopsystemException.class).when(validationHandler).checkForShopNameIsFree(new ArrayList<>(), "newShop",
				playershop.getOwner());
		assertThrows(ShopsystemException.class, () -> playershop.changeShopName("newShop"));
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
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		ShopEditorHandler editorHandler = mock(ShopEditorHandler.class);
		ShopSlotEditorHandler slotEditorHandler = mock(ShopSlotEditorHandler.class);
		Inventory backLink = mock(Inventory.class);
		InventoryGuiHandler customizer = mock(InventoryGuiHandler.class);
		when(provider.createEconomyVillagerCustomizeHandler(playershop, null, Profession.NITWIT)).thenReturn(customizer);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(editorHandler.getInventory()).thenReturn(backLink);
		when(provider.createShopEditorHandler()).thenReturn(editorHandler);
		when(provider.createShopSlotEditorHandler(backLink)).thenReturn(slotEditorHandler);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		playershop.setupNew("myshop", ecoPlayer, "P0", loc, 9);

		Inventory invNew = mock(Inventory.class);

		when(serverProvider.createInventory(villager, 9, "newName")).thenReturn(invNew);
		when(playershop.getOwner().getName()).thenReturn("catch441");

		assertDoesNotThrow(() -> playershop.changeShopName("newName"));

		assertEquals("newName", playershop.getName());
		verify(shopDao).saveShopName("newName");
		verify(invNew).setContents(inv.getContents());

		verify(villager).setCustomName("newName_catch441");

	}

	@Test
	public void removeItemTest() {
		Mocks mocks = createPlayershop();
		addShopItem(mocks.inventory, 3, 1, 2);
		assertDoesNotThrow(() -> playershop.removeShopItem(3));

		assertDoesNotThrow(() -> verify(validationHandler).checkForItemCanBeDeleted(3, 9));
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForSlotIsNotEmpty(anySet(), eq(3)));
		verify(mocks.inventory).clear(3);
		verify(shopDao).saveShopItem(any(), eq(true));
		assertDoesNotThrow(() -> assertEquals(0, playershop.getItemList().size()));
	}

	private Mocks createPlayershop() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		ShopEditorHandler editorHandler = mock(ShopEditorHandler.class);
		ShopSlotEditorHandler slotEditorHandler = mock(ShopSlotEditorHandler.class);
		Inventory backLink = mock(Inventory.class);
		InventoryGuiHandler customizer = mock(InventoryGuiHandler.class);
		when(provider.createEconomyVillagerCustomizeHandler(playershop, null, Profession.NITWIT)).thenReturn(customizer);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(editorHandler.getInventory()).thenReturn(backLink);
		when(provider.createShopEditorHandler()).thenReturn(editorHandler);
		when(provider.createShopSlotEditorHandler(backLink)).thenReturn(slotEditorHandler);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		playershop.setupNew("myshop", ecoPlayer, "P0", loc, 9);
		reset(validationHandler);
		return new Mocks(villager, inv, editorHandler);
	}

	private ItemMeta addShopItem(Inventory inventory, int slot, double sell, double buy) {
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		ItemMeta stackMeta = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		if (inventory != null) {
			when(inventory.getItem(slot)).thenReturn(stack);
			when(stack.getItemMeta()).thenReturn(stackMeta);
		}
		assertDoesNotThrow(() -> playershop.addShopItem(slot, sell, buy, stack));
		reset(validationHandler);
		return stackMeta;
	}
}
