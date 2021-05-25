package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anySet;
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
public class RentshopImplTest {

	@InjectMocks
	RentshopImpl rentshop;
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
		InventoryGuiHandler rentGuiHandler;
		ShopEditorHandler editorHandler;

		public Mocks(Villager villager, Inventory inventory, InventoryGuiHandler rentGuiHandler,
				ShopEditorHandler editorHandler) {
			this.villager = villager;
			this.inventory = inventory;
			this.rentGuiHandler = rentGuiHandler;
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
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		ShopEditorHandler editorHandler = mock(ShopEditorHandler.class);
		ShopSlotEditorHandler slotEditorHandler = mock(ShopSlotEditorHandler.class);
		InventoryGuiHandler rentGuiHandler = mock(InventoryGuiHandler.class);
		Inventory backLink = mock(Inventory.class);
		InventoryGuiHandler customizer = mock(InventoryGuiHandler.class);
		when(provider.createEconomyVillagerCustomizeHandler(rentshop, null, Profession.NITWIT)).thenReturn(customizer);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(editorHandler.getInventory()).thenReturn(backLink);
		when(provider.createShopEditorHandler()).thenReturn(editorHandler);
		when(provider.createShopSlotEditorHandler(backLink)).thenReturn(slotEditorHandler);
		when(provider.createRentshopGuiHandler(rentshop)).thenReturn(rentGuiHandler);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0"))).thenReturn(inv);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);

		rentshop.setupNew("R0", loc, 9, 5.5);

		verify(customizer).updateBackLink(backLink);
		verify(shopDao).setupSavefile("R0");
		verify(shopDao).saveLocation("", loc);
		verify(shopDao).saveShopName("RentShop#R0");
		verify(shopDao, never()).saveOwner(any(EconomyPlayer.class));
		verify(villager).setCustomName("RentShop#R0");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setSilent(true);
		verify(villager).setVillagerLevel(2);
		verify(villager).setCollidable(false);
		verify(villager).setInvulnerable(true);
		verify(villager).setProfession(Profession.NITWIT);
		verify(villager).setMetadata(eq("ue-id"), any(FixedMetadataValue.class));
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		assertEquals("R0", rentshop.getId());
		assertEquals(loc, rentshop.getLocation());
		assertNull(rentshop.getOwner());
		assertEquals("RentShop#R0", rentshop.getName());

		verify(meta).setDisplayName("Info");
		verify(infoItem).setItemMeta(meta);
		verify(meta).setLore(Arrays.asList("§6Rightclick: §asell specified amount", "§6Shift-Rightclick: §asell all",
				"§6Leftclick: §abuy"));
		verify(inv).setItem(8, infoItem);

		// rentshop
		assertEquals(5.5, rentshop.getRentalFee());
		verify(shopDao).saveRentalFee(5.5);
		assertTrue(rentshop.isRentable());
		verify(shopDao).saveRentable(true);
		assertNotNull(rentshop.getEditorHandler());
	}

	private Mocks createRentshop() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		ShopEditorHandler editorHandler = mock(ShopEditorHandler.class);
		ShopSlotEditorHandler slotEditorHandler = mock(ShopSlotEditorHandler.class);
		InventoryGuiHandler rentGuiHandler = mock(InventoryGuiHandler.class);
		Inventory backLink = mock(Inventory.class);
		InventoryGuiHandler customizer = mock(InventoryGuiHandler.class);
		when(provider.createEconomyVillagerCustomizeHandler(rentshop, null, Profession.NITWIT)).thenReturn(customizer);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(editorHandler.getInventory()).thenReturn(backLink);
		when(provider.createShopEditorHandler()).thenReturn(editorHandler);
		when(provider.createShopSlotEditorHandler(backLink)).thenReturn(slotEditorHandler);
		when(provider.createRentshopGuiHandler(rentshop)).thenReturn(rentGuiHandler);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0"))).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		rentshop.setupNew("R0", loc, 9, 5.5);
		return new Mocks(villager, inv, rentGuiHandler, editorHandler);
	}

	@Test
	public void setupExistingTestWithNotRented() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta infoItemMeta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		ShopEditorHandler editorHandler = mock(ShopEditorHandler.class);
		ShopSlotEditorHandler slotEditorHandler = mock(ShopSlotEditorHandler.class);
		InventoryGuiHandler rentGuiHandler = mock(InventoryGuiHandler.class);
		Inventory backLink = mock(Inventory.class);
		InventoryGuiHandler customizer = mock(InventoryGuiHandler.class);
		when(provider.createEconomyVillagerCustomizeHandler(rentshop, null, Profession.ARMORER)).thenReturn(customizer);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(editorHandler.getInventory()).thenReturn(backLink);
		when(provider.createShopEditorHandler()).thenReturn(editorHandler);
		when(provider.createShopSlotEditorHandler(backLink)).thenReturn(slotEditorHandler);
		when(provider.createRentshopGuiHandler(rentshop)).thenReturn(rentGuiHandler);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("RentShop#R0"))).thenReturn(inv);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(infoItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);

		when(shopDao.loadShopName()).thenReturn("RentShop#R0");
		when(shopDao.loadSize("")).thenReturn(9);
		assertDoesNotThrow(() -> when(shopDao.loadLocation("")).thenReturn(loc));
		when(shopDao.loadProfession("")).thenReturn(Profession.ARMORER);
		when(shopDao.loadOwner()).thenReturn(null);
		when(shopDao.loadItemSlotList()).thenReturn(new ArrayList<>());
		when(shopDao.loadRentable()).thenReturn(true);
		when(shopDao.loadRentalFee()).thenReturn(5.5);
		when(shopDao.loadExpiresAt()).thenReturn(0L);
		when(shopDao.loadVisible("")).thenReturn(true);

		assertDoesNotThrow(() -> rentshop.setupExisting("R0"));

		verify(customizer).updateBackLink(backLink);
		verify(shopDao).setupSavefile("R0");
		verify(villager, times(2)).setCustomName("RentShop#R0");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setSilent(true);
		verify(villager).setVillagerLevel(2);
		verify(villager).setCollidable(false);
		verify(villager).setInvulnerable(true);
		verify(villager).setProfession(Profession.ARMORER);
		verify(villager).setMetadata(eq("ue-id"), any(FixedMetadataValue.class));
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		assertEquals("R0", rentshop.getId());
		assertEquals(loc, rentshop.getLocation());
		assertNull(rentshop.getOwner());
		assertEquals("RentShop#R0", rentshop.getName());

		verify(infoItemMeta).setDisplayName("Info");
		verify(infoItem).setItemMeta(infoItemMeta);
		verify(infoItemMeta).setLore(Arrays.asList("§6Rightclick: §asell specified amount",
				"§6Shift-Rightclick: §asell all", "§6Leftclick: §abuy"));
		verify(inv).setItem(8, infoItem);

		// rentshop
		assertEquals(5.5, rentshop.getRentalFee());
		assertTrue(rentshop.isRentable());
		assertEquals(0L, rentshop.getExpiresAt());
		assertNotNull(rentshop.getEditorHandler());
	}

	@Test
	public void moveShopTest() {
		Mocks mocks = createRentshop();
		Location loc = mock(Location.class);
		assertDoesNotThrow(() -> rentshop.changeLocation(loc));

		assertEquals(loc, rentshop.getLocation());
		verify(shopDao).saveLocation("", loc);
		verify(mocks.villager).teleport(loc);
	}

	@Test
	public void isRentableTest() {
		Mocks mocks = createRentshop();
		assertTrue(rentshop.isRentable());
		rentThisShop(rentshop, mocks);
		assertFalse(rentshop.isRentable());
	}

	@Test
	public void changeShopNameTestWithNotRented() throws ShopsystemException {
		Mocks mocks = createRentshop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopsystemException.class, () -> rentshop.changeShopName("newName"));
		verify(mocks.villager, never()).setCustomName("newName");
	}

	@Test
	public void changeShopNameTestWithRented() {
		Mocks mocks = createRentshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Inventory inv = mock(Inventory.class);
		when(serverProvider.createInventory(eq(mocks.villager), anyInt(), eq("Shop#R0"))).thenReturn(inv);

		assertDoesNotThrow(() -> rentshop.rentShop(ecoPlayer, 1));

		Inventory invNew = mock(Inventory.class);

		when(serverProvider.createInventory(mocks.villager, 9, "newName")).thenReturn(invNew);
		when(ecoPlayer.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> rentshop.changeShopName("newName"));

		assertEquals("newName", rentshop.getName());
		verify(shopDao).saveShopName("newName");
		verify(invNew).setContents(inv.getContents());

		verify(mocks.villager).setCustomName("newName_catch441");
	}

	@Test
	public void rentShopTest() {
		Mocks mocks = createRentshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);

		Inventory inv = mock(Inventory.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		when(serverProvider.createInventory(eq(mocks.villager), anyInt(), eq("Shop#R0"))).thenReturn(inv);
		when(serverProvider.getWorldTime()).thenReturn(12000L);

		assertDoesNotThrow(() -> rentshop.rentShop(ecoPlayer, 2));

		verify(shopDao).saveOwner(ecoPlayer);
		verify(shopDao).saveRentable(false);
		verify(shopDao).saveExpiresAt(3468000L);
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(11.0, true));
		assertFalse(rentshop.isRentable());
		assertEquals(3468000L, rentshop.getExpiresAt());
		assertEquals(ecoPlayer, rentshop.getOwner());
		assertEquals("Shop#R0", rentshop.getName());
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForIsRentable(true));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(1.0));
		verify(mocks.villager).setCustomName("Shop#R0_catch441");
	}

	@Test
	public void rentShopTestWithNotEnoughMoney() throws EconomyPlayerException, BankException {
		createRentshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(ecoPlayer).decreasePlayerAmount(5.5, true);
		assertThrows(EconomyPlayerException.class, () -> rentshop.rentShop(ecoPlayer, 1));
	}

	@Test
	public void rentShopTestWithInvalidDuration() throws ShopsystemException {
		createRentshop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForPositiveValue(-1.0);
		assertThrows(ShopsystemException.class, () -> rentshop.rentShop(null, 0));
	}

	@Test
	public void rentShopTestWithAlreadyRented() throws ShopsystemException {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRentable(false);
		assertThrows(ShopsystemException.class, () -> rentshop.rentShop(null, 1));
	}

	@Test
	public void resetShopTest() {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(stack.clone()).thenReturn(stack);
		assertDoesNotThrow(() -> rentshop.addShopItem(0, 1, 1, stack));

		assertDoesNotThrow(() -> rentshop.resetShop());

		verify(shopDao).saveOwner(null);
		verify(shopDao, times(2)).saveRentable(true);
		verify(shopDao).saveExpiresAt(0L);
		assertNull(rentshop.getOwner());
		assertEquals(0L, rentshop.getExpiresAt());
		assertTrue(rentshop.isRentable());
		verify(mocks.villager, times(2)).setProfession(Profession.NITWIT);
		assertEquals("RentShop#R0", rentshop.getName());
		verify(mocks.villager, times(2)).setCustomName("RentShop#R0");
		assertDoesNotThrow(() -> assertEquals(0, rentshop.getItemList().size()));
	}

	@Test
	public void changeRentalFee() {
		createRentshop();
		assertDoesNotThrow(() -> rentshop.changeRentalFee(4.4));
		assertEquals(4.4, rentshop.getRentalFee());
		verify(shopDao).saveRentalFee(4.4);
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(4.4));
	}

	@Test
	public void changeRentalFeeWithNegativValue() throws ShopsystemException {
		createRentshop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForPositiveValue(-1.0);
		assertThrows(ShopsystemException.class, () -> rentshop.changeRentalFee(-1));
		assertEquals(5.5, rentshop.getRentalFee());
		verify(shopDao, never()).saveRentalFee(4.4);
	}

	@Test
	public void changeShopSizeTestWithNotRented() {
		Mocks mocks = createRentshop();
		reset(mocks.editorHandler);
		assertDoesNotThrow(() -> rentshop.changeSize(18));

		assertDoesNotThrow(() -> verify(validationHandler).checkForValidSize(18));
		assertDoesNotThrow(() -> verify(validationHandler).checkForResizePossible(mocks.inventory, 9, 18, 1));
		assertEquals(18, rentshop.getSize());
		verify(shopDao).saveSize("", 18);
		verify(serverProvider).createInventory(mocks.villager, 18, "RentShop#R0");
		verify(mocks.editorHandler).setup(rentshop, 1);
	}

	@Test
	public void changeShopSizeTestWithRented() throws ShopsystemException {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRentable(false);
		assertThrows(ShopsystemException.class, () -> rentshop.changeSize(18));
		assertEquals(9, rentshop.getSize());
	}

	@Test
	public void getRentalFeeTest() {
		createRentshop();
		assertEquals(5.5, rentshop.getRentalFee());
	}

	@Test
	public void getRentGuiHandlerTest() {
		Mocks mocks = createRentshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Inventory inv = mock(Inventory.class);
		when(serverProvider.createInventory(eq(mocks.villager), anyInt(), eq("Shop#R0"))).thenReturn(inv);

		assertDoesNotThrow(() -> rentshop.rentShop(ecoPlayer, 1));
		InventoryGuiHandler result = assertDoesNotThrow(() -> rentshop.getRentGuiHandler());
		assertEquals(mocks.rentGuiHandler, result);
	}

	@Test
	public void getRentGuiHandlerTestWithRented() throws ShopsystemException {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRentable(false);
		assertThrows(ShopsystemException.class, () -> rentshop.getRentGuiHandler());
	}

	@Test
	public void changeOwnerTest() {
		Mocks mocks = createRentshop();
		EconomyPlayer newOwner = mock(EconomyPlayer.class);
		when(newOwner.getName()).thenReturn("wejink");
		when(playershopManager.getPlayerShopUniqueNameList()).thenReturn(new ArrayList<>());
		assertDoesNotThrow(() -> rentshop.changeOwner(newOwner));
		assertEquals(newOwner, rentshop.getOwner());
		verify(shopDao).saveOwner(newOwner);
		verify(mocks.villager).setCustomName("RentShop#R0_wejink");
		assertDoesNotThrow(() -> verify(validationHandler).checkForChangeOwnerIsPossible(new ArrayList<>(), newOwner,
				"RentShop#R0"));
	}

	@Test
	public void changeOwnerTestWithRented() throws ShopsystemException {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRentable(false);
		assertThrows(ShopsystemException.class, () -> rentshop.changeOwner(null));
	}

	@Test
	public void addShopItemTestWithNotRented() throws ShopsystemException {
		createRentshop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopsystemException.class, () -> rentshop.addShopItem(0, 0, 0, null));
		assertEquals(0, rentshop.getItemList().size());
	}

	@Test
	public void addShopTestWithRented() {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(2);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> rentshop.addShopItem(0, 1, 4, stack));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsEmpty(anySet(), eq(0)));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(1.0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(4.0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(1.0, 4.0));
		assertDoesNotThrow(() -> assertEquals(1, rentshop.getItemList().size()));
		ShopItem shopItem = assertDoesNotThrow(() -> rentshop.getShopItem(0));
		verify(shopDao).saveShopItem(shopItem, false);
		assertEquals(2, shopItem.getAmount());
		assertEquals(4.0, shopItem.getBuyPrice());
		assertEquals(1.0, shopItem.getSellPrice());
		assertEquals(0, shopItem.getSlot());
		assertEquals(stackCloneClone, shopItem.getItemStack());
		verify(mocks.editorHandler).setOccupied(true, 0);
	}

	@Test
	public void editShopItemTestWithNotRented() throws ShopsystemException {
		createRentshop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopsystemException.class, () -> rentshop.editShopItem(0, null, null, null));
	}

	@Test
	public void editShopTestWithRented() {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		ItemMeta stackMetaCloneClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(2);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackCloneClone.getItemMeta()).thenReturn(stackMetaCloneClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> rentshop.addShopItem(0, 1, 4, stack));
		when(stackCloneClone.getType()).thenReturn(Material.STONE);
		reset(validationHandler);
		String response = assertDoesNotThrow(() -> rentshop.editShopItem(0, 5, 15.0, 25.0));

		assertEquals("§6Updated §aamount §asellPrice §abuyPrice §6for item §astone", response);

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForIsRented(false));
		verify(shopDao).saveShopItemSellPrice(0, 15.0);
		verify(shopDao).saveShopItemBuyPrice(0, 25.0);
		verify(shopDao).saveShopItemAmount(0, 5);
		assertDoesNotThrow(() -> assertEquals(5, rentshop.getShopItem(0).getAmount()));
		assertDoesNotThrow(() -> assertEquals(15.0, rentshop.getShopItem(0).getSellPrice()));
		assertDoesNotThrow(() -> assertEquals(25.0, rentshop.getShopItem(0).getBuyPrice()));
		verify(mocks.inventory).setItem(0, stackCloneClone);
		verify(stackCloneClone).setAmount(5);
		verify(stackMetaCloneClone).setLore(Arrays.asList("§65 buy for §a25.0 $", "§65 sell for §a15.0 $"));
	}

	@Test
	public void getShopItemTestWithNotRented() throws ShopsystemException {
		createRentshop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopsystemException.class, () -> rentshop.getShopItem(null));
	}

	@Test
	public void getShopItemTestWithRentedAndSlot() {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> rentshop.addShopItem(3, 1, 2, stack));

		ShopItem shopItem = assertDoesNotThrow(() -> rentshop.getShopItem(3));

		assertEquals(stackCloneClone, shopItem.getItemStack());
	}

	@Test
	public void getShopItemTestWithRentedAndItem() {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);
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
		when(stackCloneClone.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> rentshop.addShopItem(3, 1, 2, stack));

		ShopItem shopItem = assertDoesNotThrow(() -> rentshop.getShopItem(searchStack));

		assertEquals(stackCloneClone, shopItem.getItemStack());
	}

	@Test
	public void isAvailableTestWithNotRented() throws ShopsystemException {
		createRentshop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopsystemException.class, () -> rentshop.isAvailable(0));
	}

	@Test
	public void isAvailableTestWithRented() {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack shopInvStack = mock(ItemStack.class);
		ItemMeta shopInvStackMeta = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(shopInvStack.getItemMeta()).thenReturn(shopInvStackMeta);
		when(mocks.inventory.getItem(3)).thenReturn(shopInvStack);
		when(stack.getAmount()).thenReturn(1);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> rentshop.addShopItem(3, 1, 2, stack));
		assertDoesNotThrow(() -> rentshop.addShopItem(4, 1, 2, stack));

		assertDoesNotThrow(() -> rentshop.increaseStock(3, 1));
		reset(validationHandler);
		assertDoesNotThrow(() -> assertTrue(rentshop.isAvailable(3)));
		assertDoesNotThrow(() -> assertFalse(rentshop.isAvailable(4)));
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler, times(4)).checkForIsRented(false));
	}

	@Test
	public void openInventoryWithCheckTestWithNotRented() throws ShopsystemException {
		createRentshop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopsystemException.class, () -> rentshop.openInventoryWithCheck(null));
	}

	@Test
	public void openInventoryWithCheckTestWithRented() {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);
		Player player = mock(Player.class);
		assertDoesNotThrow(() -> rentshop.openInventoryWithCheck(player));
		verify(player).openInventory(mocks.inventory);
	}

	@Test
	public void decreaseStockTestWithNotRented() throws ShopsystemException {
		createRentshop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopsystemException.class, () -> rentshop.decreaseStock(0, 0));
	}

	@Test
	public void decreaseStockTestWithRented() {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack shopInvStack = mock(ItemStack.class);
		ItemMeta shopInvStackMeta = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(shopInvStack.getItemMeta()).thenReturn(shopInvStackMeta);
		when(mocks.inventory.getItem(3)).thenReturn(shopInvStack);
		when(stack.getAmount()).thenReturn(1);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> rentshop.addShopItem(3, 1, 2, stack));

		assertDoesNotThrow(() -> rentshop.increaseStock(3, 10));
		reset(validationHandler);
		assertDoesNotThrow(() -> rentshop.decreaseStock(3, 5));

		assertDoesNotThrow(() -> verify(validationHandler, times(3)).checkForIsRented(false));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(5.0));
		assertDoesNotThrow(() -> verify(validationHandler, times(3)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidStockDecrease(10, 5));
		verify(shopDao).saveStock(3, 5);
		verify(shopInvStackMeta).setLore(Arrays.asList("§a5§6 Items"));
		assertDoesNotThrow(() -> assertEquals(5, rentshop.getShopItem(3).getStock()));
	}

	@Test
	public void increaseStockTestWithNotRented() throws ShopsystemException {
		createRentshop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopsystemException.class, () -> rentshop.increaseStock(0, 0));
	}

	@Test
	public void increaseStockTestWithRented() {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack shopInvStack = mock(ItemStack.class);
		ItemMeta shopInvStackMeta = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(shopInvStack.getItemMeta()).thenReturn(shopInvStackMeta);
		when(mocks.inventory.getItem(3)).thenReturn(shopInvStack);
		when(stack.getAmount()).thenReturn(1);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> rentshop.addShopItem(3, 1, 2, stack));
		reset(validationHandler);
		assertDoesNotThrow(() -> rentshop.increaseStock(3, 1));

		assertDoesNotThrow(() -> verify(validationHandler, times(3)).checkForIsRented(false));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(1.0));
		assertDoesNotThrow(() -> verify(validationHandler, times(3)).checkForValidSlot(3, 8));
		verify(shopDao).saveStock(3, 1);
		verify(shopInvStackMeta).setLore(Arrays.asList("§a1§6 Item"));
	}

	@Test
	public void removeShopItemTestWithNotRented() throws ShopsystemException {
		createRentshop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopsystemException.class, () -> rentshop.removeShopItem(0));
	}

	@Test
	public void removeShopItemTestWithRented() {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);

		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		ItemMeta stackMeta = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.getItemMeta()).thenReturn(stackMeta);
		when(stack.clone()).thenReturn(stackClone);
		when(mocks.inventory.getItem(3)).thenReturn(stack);
		assertDoesNotThrow(() -> rentshop.addShopItem(3, 1, 2, stack));
		reset(mocks.inventory);
		reset(validationHandler);
		assertDoesNotThrow(() -> rentshop.removeShopItem(3));

		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForIsRented(false));
		assertDoesNotThrow(() -> verify(validationHandler).checkForItemCanBeDeleted(3, 9));
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).checkForSlotIsNotEmpty(anySet(), eq(3)));
		verify(mocks.inventory).clear(3);
		verify(shopDao).saveShopItem(any(), eq(true));
		assertDoesNotThrow(() -> assertEquals(0, rentshop.getItemList().size()));
	}

	private void rentThisShop(RentshopImpl shop, Mocks mocks) {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(serverProvider.createInventory(eq(mocks.villager), anyInt(), eq("Shop#R0"))).thenReturn(mocks.inventory);
		assertDoesNotThrow(() -> shop.rentShop(ecoPlayer, 1));
		reset(validationHandler);
	}

	@Test
	public void buyShopItemWithNotRented() throws ShopsystemException {
		createRentshop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopsystemException.class, () -> rentshop.buyShopItem(0, null, true));
	}

	@Test
	public void buyShopItemTestWithRented() {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);

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
		assertDoesNotThrow(() -> rentshop.addShopItem(3, 1, 4, stack));
		assertDoesNotThrow(() -> rentshop.increaseStock(3, 2));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(configManager.getCurrencyText(4.0)).thenReturn("$");
		when(messageWrapper.getString(MessageEnum.SHOP_BUY_PLURAL, "2", 4.0, "$")).thenReturn("my message");
		reset(validationHandler);
		assertDoesNotThrow(() -> rentshop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(4)).checkForIsRented(false));
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
		assertDoesNotThrow(() -> verify(rentshop.getOwner()).increasePlayerAmount(4.0, false));
		assertDoesNotThrow(() -> assertEquals(0, rentshop.getShopItem(3).getStock()));
	}

	@Test
	public void sellShopItemWithNotRented() throws ShopsystemException {
		createRentshop();
		doThrow(ShopsystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopsystemException.class, () -> rentshop.sellShopItem(0, 1, null, false));
	}

	@Test
	public void sellShopItemTestWithRented() {
		Mocks mocks = createRentshop();
		rentThisShop(rentshop, mocks);
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
		assertDoesNotThrow(() -> rentshop.addShopItem(3, 1, 2, stack));
		when(configManager.getCurrencyText(1.0)).thenReturn("$");
		when(messageWrapper.getString(MessageEnum.SHOP_SELL_SINGULAR, "1", 1.0, "$")).thenReturn("my message");
		when(stackCloneClone.isSimilar(contentStack)).thenReturn(true);
		reset(validationHandler);
		assertDoesNotThrow(() -> rentshop.sellShopItem(3, 1, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(validationHandler, times(4)).checkForIsRented(false));
		assertDoesNotThrow(() -> verify(validationHandler, times(4)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler, times(4))
				.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(3)), 3));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler).checkForShopOwnerHasEnoughMoney(rentshop.getOwner(), 1.0));
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(1.0, false));
		assertDoesNotThrow(() -> verify(rentshop.getOwner()).decreasePlayerAmount(1.0, true));
		assertDoesNotThrow(() -> assertEquals(1, rentshop.getShopItem(3).getStock()));
		verify(inv).removeItem(contentStackClone);
		verify(player).sendMessage("my message");
	}
}
