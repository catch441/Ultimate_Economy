package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.shopsystem.api.AdminshopController;
import com.ue.shopsystem.impl.AbstractShopImpl;
import com.ue.shopsystem.impl.ShopItem;
import com.ue.shopsystem.impl.ShopSlotEditorHandler;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;
import be.seeseemelk.mockbukkit.inventory.PlayerInventoryViewMock;

public class ShopSlotEditorHandlerTest {

	private static final String MINUS = "http://textures.minecraft.net/texture/"
			+ "935e4e26eafc11b52c11668e1d6634e7d1d0d21c411cb085f9394268eb4cdfba";
	private static final String TWENTY = "http://textures.minecraft.net/texture/"
			+ "f7b29a1bb25b2ad8ff3a7a38228189c9461f457a4da98dae29384c5c25d85";
	private static final String TEN = "http://textures.minecraft.net/texture/"
			+ "b0cf9794fbc089dab037141f67875ab37fadd12f3b92dba7dd2288f1e98836";
	private static final String ONE = "http://textures.minecraft.net/texture/"
			+ "d2a6f0e84daefc8b21aa99415b16ed5fdaa6d8dc0c3cd591f49ca832b575";
	private static final String K_OFF = "http://textures.minecraft.net/texture/"
			+ "e883b5beb4e601c3cbf50505c8bd552e81b996076312cffe27b3cc1a29e3";
	private static final String PLUS = "http://textures.minecraft.net/texture/"
			+ "9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777";
	private static final String K_ON = "http://textures.minecraft.net/texture/"
			+ "d42a4802b6b2deb49cfbb4b7e267e2f9ad45da24c73286f97bef91d21616496";
	private static final String BUY = "http://textures.minecraft.net/texture/"
			+ "e5da4847272582265bdaca367237c96122b139f4e597fbc6667d3fb75fea7cf6";
	private static final String SELL = "http://textures.minecraft.net/texture/"
			+ "abae89e92ac362635ba3e9fb7c12b7ddd9b38adb11df8aa1aff3e51ac428a4";
	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		server.setPlayers(0);
		EconomyPlayerController.getAllEconomyPlayers().clear();
		player = server.addPlayer("kthschnll");
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		int size2 = EconomyPlayerController.getAllEconomyPlayers().size();
		for (int i = 0; i < size2; i++) {
			EconomyPlayerController.deleteEconomyPlayer(EconomyPlayerController.getAllEconomyPlayers().get(0));
		}
		UltimateEconomy.getInstance.getDataFolder().delete();
		server.setPlayers(0);
		MockBukkit.unload();
	}
	
	@BeforeEach
	public void load() {
		try {
			AdminshopController.createAdminShop("myshop", new Location(world,5,3,7), 9);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
	}

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {
		int size = AdminshopController.getAdminshopList().size();
		for (int i = 0; i < size; i++) {
			try {
				AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
			} catch (ShopSystemException e) {
				fail();
			}
		}
	}
	
	@Test
	public void constructorTest() {
		ShopSlotEditorHandler handler = new ShopSlotEditorHandler((AbstractShopImpl) AdminshopController.getAdminshopList().get(0));
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals("myshop-SlotEditor", slotEditor.getName());
		checkSlotEditorInventory(slotEditor);
	}
	
	private void checkSlotEditorInventory(ChestInventoryMock slotEditor) {
		assertEquals(27, slotEditor.getSize());
		assertEquals(Material.RED_WOOL, slotEditor.getItem(7).getType());
		assertEquals(Material.GREEN_WOOL, slotEditor.getItem(8).getType());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(12).getType());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(21).getType());
		assertEquals(Material.BARRIER, slotEditor.getItem(26).getType());
		assertEquals("§cexit without save", slotEditor.getItem(7).getItemMeta().getDisplayName());
		assertEquals("§esave changes", slotEditor.getItem(8).getItemMeta().getDisplayName());
		assertEquals("factor off", slotEditor.getItem(12).getItemMeta().getDisplayName());
		assertEquals("factor off", slotEditor.getItem(21).getItemMeta().getDisplayName());
		assertEquals("§cremove item", slotEditor.getItem(26).getItemMeta().getDisplayName());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(K_OFF, slotEditor.getItem(12).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals(K_OFF, slotEditor.getItem(21).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
	}
	
	@Test
	public void changeInventoryNameTest() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		handler.changeInventoryName("kth");
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals("kth-SlotEditor", slotEditor.getName());
		checkSlotEditorInventory(slotEditor);
	}
	
	@Test
	public void setSelectedSlotTestEmpty() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			handler.setSelectedSlot(1);
			ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(11).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(20).getType());
			NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
			assertEquals(PLUS, slotEditor.getItem(2).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(PLUS, slotEditor.getItem(11).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(PLUS, slotEditor.getItem(20).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals("plus", slotEditor.getItem(2).getItemMeta().getDisplayName());
			assertEquals("plus", slotEditor.getItem(11).getItemMeta().getDisplayName());
			assertEquals("plus", slotEditor.getItem(20).getItemMeta().getDisplayName());
			
			List<String> list = new ArrayList<String>();
			list.add("§6Price: 0.0");
			
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(4).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(13).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(22).getType());
			assertEquals(ONE, slotEditor.getItem(4).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(ONE, slotEditor.getItem(13).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(ONE, slotEditor.getItem(22).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals("one", slotEditor.getItem(4).getItemMeta().getDisplayName());
			assertEquals("one", slotEditor.getItem(13).getItemMeta().getDisplayName());
			assertEquals("one", slotEditor.getItem(22).getItemMeta().getDisplayName());
			assertEquals(list, slotEditor.getItem(13).getItemMeta().getLore());
			assertEquals(list, slotEditor.getItem(22).getItemMeta().getLore());
			
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(5).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(14).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(23).getType());
			assertEquals(TEN, slotEditor.getItem(5).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(TEN, slotEditor.getItem(14).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(TEN, slotEditor.getItem(23).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals("ten", slotEditor.getItem(5).getItemMeta().getDisplayName());
			assertEquals("ten", slotEditor.getItem(14).getItemMeta().getDisplayName());
			assertEquals("ten", slotEditor.getItem(23).getItemMeta().getDisplayName());
			assertEquals(list, slotEditor.getItem(14).getItemMeta().getLore());
			assertEquals(list, slotEditor.getItem(23).getItemMeta().getLore());
			
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(6).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(15).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(24).getType());
			assertEquals(TWENTY, slotEditor.getItem(6).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(TWENTY, slotEditor.getItem(15).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(TWENTY, slotEditor.getItem(24).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals("twenty", slotEditor.getItem(6).getItemMeta().getDisplayName());
			assertEquals("twenty", slotEditor.getItem(15).getItemMeta().getDisplayName());
			assertEquals("twenty", slotEditor.getItem(24).getItemMeta().getDisplayName());
			assertEquals(list, slotEditor.getItem(15).getItemMeta().getLore());
			assertEquals(list, slotEditor.getItem(24).getItemMeta().getLore());	
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(9).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(18).getType());
			assertEquals(SELL, slotEditor.getItem(9).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(BUY, slotEditor.getItem(18).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(list, slotEditor.getItem(9).getItemMeta().getLore());
			assertEquals(list, slotEditor.getItem(18).getItemMeta().getLore());	
			assertEquals(Material.BARRIER, slotEditor.getItem(0).getType());
			assertEquals("§aselect item", slotEditor.getItem(0).getItemMeta().getDisplayName());
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void setSelectedSlotTestFilled() {
		ItemStack stack = new ItemStack(Material.POTION);
		stack.setAmount(20);
		ItemStack clone = stack.clone();
		try {
			AdminshopController.getAdminshopList().get(0).addShopItem(0, 2, 3, stack);
		} catch (ShopSystemException | PlayerException | GeneralEconomyException e1) {
			fail();
		}
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			handler.setSelectedSlot(0);
			ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(11).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(20).getType());
			NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
			assertEquals(PLUS, slotEditor.getItem(2).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(PLUS, slotEditor.getItem(11).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(PLUS, slotEditor.getItem(20).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals("plus", slotEditor.getItem(2).getItemMeta().getDisplayName());
			assertEquals("plus", slotEditor.getItem(11).getItemMeta().getDisplayName());
			assertEquals("plus", slotEditor.getItem(20).getItemMeta().getDisplayName());		
			List<String> sellPrice = new ArrayList<String>();
			sellPrice.add("§6Price: 3.0");
			List<String> buyPrice = new ArrayList<String>();
			buyPrice.add("§6Price: 2.0");			
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(4).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(13).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(22).getType());
			assertEquals(ONE, slotEditor.getItem(4).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(ONE, slotEditor.getItem(13).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(ONE, slotEditor.getItem(22).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals("one", slotEditor.getItem(4).getItemMeta().getDisplayName());
			assertEquals("one", slotEditor.getItem(13).getItemMeta().getDisplayName());
			assertEquals("one", slotEditor.getItem(22).getItemMeta().getDisplayName());
			assertEquals(sellPrice, slotEditor.getItem(13).getItemMeta().getLore());
			assertEquals(buyPrice, slotEditor.getItem(22).getItemMeta().getLore());		
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(5).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(14).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(23).getType());
			assertEquals(TEN, slotEditor.getItem(5).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(TEN, slotEditor.getItem(14).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(TEN, slotEditor.getItem(23).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals("ten", slotEditor.getItem(5).getItemMeta().getDisplayName());
			assertEquals("ten", slotEditor.getItem(14).getItemMeta().getDisplayName());
			assertEquals("ten", slotEditor.getItem(23).getItemMeta().getDisplayName());
			assertEquals(sellPrice, slotEditor.getItem(14).getItemMeta().getLore());
			assertEquals(buyPrice, slotEditor.getItem(23).getItemMeta().getLore());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(6).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(15).getType());
			assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(24).getType());
			assertEquals(TWENTY, slotEditor.getItem(6).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(TWENTY, slotEditor.getItem(15).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals(TWENTY, slotEditor.getItem(24).getItemMeta().getPersistentDataContainer().get(key,
					PersistentDataType.STRING));
			assertEquals("twenty", slotEditor.getItem(6).getItemMeta().getDisplayName());
			assertEquals("twenty", slotEditor.getItem(15).getItemMeta().getDisplayName());
			assertEquals("twenty", slotEditor.getItem(24).getItemMeta().getDisplayName());
			assertEquals(sellPrice, slotEditor.getItem(15).getItemMeta().getLore());
			assertEquals(buyPrice, slotEditor.getItem(24).getItemMeta().getLore());
			assertEquals(clone, slotEditor.getItem(0));
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleSlotEditorTestSelectedItemClick() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
			player.closeInventory();
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 2, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
	}
	
	@Test
	public void handleSlotEditorTestPlusClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 2, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(MINUS, slotEditor.getItem(2).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("minus", slotEditor.getItem(2).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(11).getType());
		assertEquals(PLUS, slotEditor.getItem(11).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(11).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(20).getType());
		assertEquals(PLUS, slotEditor.getItem(20).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(20).getItemMeta().getDisplayName());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestPlusClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 11, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(11).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(MINUS, slotEditor.getItem(11).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("minus", slotEditor.getItem(11).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(20).getType());
		assertEquals(PLUS, slotEditor.getItem(20).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(20).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		assertEquals(PLUS, slotEditor.getItem(2).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(2).getItemMeta().getDisplayName());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestPlusClick3() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 20, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(PLUS, slotEditor.getItem(2).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(2).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		assertEquals(PLUS, slotEditor.getItem(11).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(11).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		assertEquals(MINUS, slotEditor.getItem(20).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("minus", slotEditor.getItem(20).getItemMeta().getDisplayName());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestMinusClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 2, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(PLUS, slotEditor.getItem(2).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(2).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(11).getType());
		assertEquals(PLUS, slotEditor.getItem(11).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(11).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(20).getType());
		assertEquals(PLUS, slotEditor.getItem(20).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(20).getItemMeta().getDisplayName());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestMinusClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 11, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(11).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(PLUS, slotEditor.getItem(11).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(11).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(20).getType());
		assertEquals(PLUS, slotEditor.getItem(20).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(20).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		assertEquals(PLUS, slotEditor.getItem(2).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(2).getItemMeta().getDisplayName());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestMinusClick3() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 20, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(PLUS, slotEditor.getItem(2).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(2).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		assertEquals(PLUS, slotEditor.getItem(11).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(11).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		assertEquals(PLUS, slotEditor.getItem(20).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(20).getItemMeta().getDisplayName());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorOffClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 12, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(12).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(K_ON, slotEditor.getItem(12).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("factor on", slotEditor.getItem(12).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(21).getType());
		assertEquals(K_OFF, slotEditor.getItem(21).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("factor off", slotEditor.getItem(21).getItemMeta().getDisplayName());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorOffClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 21, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();	
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(21).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(K_ON, slotEditor.getItem(21).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("factor on", slotEditor.getItem(21).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(12).getType());
		assertEquals(K_OFF, slotEditor.getItem(12).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("factor off", slotEditor.getItem(12).getItemMeta().getDisplayName());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorOnClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 12, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(12).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(K_OFF, slotEditor.getItem(12).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("factor off", slotEditor.getItem(12).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(21).getType());
		assertEquals(K_OFF, slotEditor.getItem(21).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("factor off", slotEditor.getItem(21).getItemMeta().getDisplayName());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorOnClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 21, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(21).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(K_OFF, slotEditor.getItem(21).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("factor off", slotEditor.getItem(21).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(12).getType());
		assertEquals(K_OFF, slotEditor.getItem(12).getItemMeta().getPersistentDataContainer().get(key,
				PersistentDataType.STRING));
		assertEquals("factor off", slotEditor.getItem(12).getItemMeta().getDisplayName());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestPlusOneClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 4, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.BARRIER, slotEditor.getItem(0).getType());
		assertEquals(2, slotEditor.getItem(0).getAmount());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestPlusOneClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 13, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 1.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 0.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}

	private void checkBuySellInfoItems(ChestInventoryMock slotEditor, List<String> sellPrice, List<String> buyPrice) {
		assertEquals(sellPrice, slotEditor.getItem(9).getItemMeta().getLore());
		assertEquals(sellPrice, slotEditor.getItem(11).getItemMeta().getLore());
		assertEquals(sellPrice, slotEditor.getItem(13).getItemMeta().getLore());
		assertEquals(sellPrice, slotEditor.getItem(14).getItemMeta().getLore());
		assertEquals(sellPrice, slotEditor.getItem(15).getItemMeta().getLore());
		assertEquals(buyPrice, slotEditor.getItem(18).getItemMeta().getLore());
		assertEquals(buyPrice, slotEditor.getItem(20).getItemMeta().getLore());
		assertEquals(buyPrice, slotEditor.getItem(22).getItemMeta().getLore());
		assertEquals(buyPrice, slotEditor.getItem(23).getItemMeta().getLore());
		assertEquals(buyPrice, slotEditor.getItem(24).getItemMeta().getLore());
	}
	
	@Test
	public void handleSlotEditorTestPlusOneClick3() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 22, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 0.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 1.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestMinusOneClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 4, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 2, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.BARRIER, slotEditor.getItem(0).getType());
		assertEquals(2, slotEditor.getItem(0).getAmount());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestMinusOneClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 13, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 11, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);	
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 1.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 0.0");		
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestMinusOneClick3() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 22, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 20, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);		
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 0.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 1.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestPlusTenClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 5, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.BARRIER, slotEditor.getItem(0).getType());
		assertEquals(11, slotEditor.getItem(0).getAmount());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestPlusTenClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 14, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 10.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 0.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestPlusTenClick3() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 23, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 0.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 10.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestMinusTenClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 5, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 2, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.BARRIER, slotEditor.getItem(0).getType());
		assertEquals(11, slotEditor.getItem(0).getAmount());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestMinusTenClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 14, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 11, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 10.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 0.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestMinusTenClick3() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 23, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 20, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 0.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 10.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestPlusTwentyClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 6, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.BARRIER, slotEditor.getItem(0).getType());
		assertEquals(21, slotEditor.getItem(0).getAmount());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestPlusTwentyClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 15, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 20.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 0.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestPlusTwentyClick3() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 24, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 0.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 20.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestMinusTwentyClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 6, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 2, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.BARRIER, slotEditor.getItem(0).getType());
		assertEquals(21, slotEditor.getItem(0).getAmount());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestMinusTwentyClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 15, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 11, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 20.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 0.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestMinusTwentyClick3() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 24, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 20, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 0.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 20.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestAddMoreAmount() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 6, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.BARRIER, slotEditor.getItem(0).getType());
		assertEquals(64, slotEditor.getItem(0).getAmount());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestRemoveMoreAmount() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 6, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 2, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.BARRIER, slotEditor.getItem(0).getType());
		assertEquals(1, slotEditor.getItem(0).getAmount());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestMinusMoreMoney1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 15, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 11, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 0.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 0.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestMinusMoreMoney2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 24, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 20, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 0.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 0.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestSelectItemClick() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		ItemStack stack = new ItemStack(Material.STONE);
		stack.setAmount(20);
		player.getInventory().setItem(0, stack);
		InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 54, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.STONE, slotEditor.getItem(0).getType());
		assertEquals(1, slotEditor.getItem(0).getAmount());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestSelectItemClickSpawner() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		ItemStack stack = new ItemStack(Material.SPAWNER);
		player.getInventory().setItem(0, stack);
		InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 54, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.BARRIER, slotEditor.getItem(0).getType());
		assertEquals(1, slotEditor.getItem(0).getAmount());
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestExitWithoutSave() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 7, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(event);
			assertEquals(0,  AdminshopController.getAdminshopList().get(0).getItemList().size());
			ChestInventoryMock editor = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myshop-Editor", editor.getName());
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleSlotEditorTestSaveChangesNew() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
			ItemStack stack = new ItemStack(Material.STONE);
			stack.setAmount(20);
			player.getInventory().setItem(0, stack);
			InventoryClickEvent addItem = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 54, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(addItem);
			InventoryClickEvent increaseAmount = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 6, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(increaseAmount);
			InventoryClickEvent addBuyPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 24, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(addBuyPrice);
			InventoryClickEvent addSellPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 14, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(addSellPrice);	
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 8, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(event);
			assertEquals(1,  AdminshopController.getAdminshopList().get(0).getItemList().size());
			ChestInventoryMock editor = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myshop-Editor", editor.getName());
			ShopItem shopItem = AdminshopController.getAdminshopList().get(0).getItemList().get(0);		
			assertEquals(21, shopItem.getAmount());
			assertEquals("20.0", String.valueOf(shopItem.getSellPrice()));
			assertEquals("10.0", String.valueOf(shopItem.getBuyPrice()));
			assertEquals(0, shopItem.getSlot());
			stack.setAmount(1);
			assertEquals(stack, shopItem.getItemStack());
			assertEquals("ItemStack{STONE x 1}", shopItem.getItemString());
			assertEquals("§6The item §astone§6 was added to the shop.", player.nextMessage());
			assertNull(player.nextMessage());
			player.closeInventory();
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleSlotEditorTestSaveChangesNewWithNoPrices() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
			ItemStack stack = new ItemStack(Material.STONE);
			stack.setAmount(20);
			player.getInventory().setItem(0, stack);
			InventoryClickEvent addItem = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 54, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(addItem);
			InventoryClickEvent increaseAmount = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 6, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(increaseAmount);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 8, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(event);
			assertEquals(0,  AdminshopController.getAdminshopList().get(0).getItemList().size());
			ChestInventoryMock editor = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myshop-SlotEditor", editor.getName());
			assertEquals("§cOne of the prices have to be above 0!", player.nextMessage());
			assertNull(player.nextMessage());
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleSlotEditorTestSaveChangesEdit() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).addShopItem(0, 4.0, 19.0, new ItemStack(Material.STONE));
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
			ItemStack stack = new ItemStack(Material.STONE);
			stack.setAmount(20);
			player.getInventory().setItem(0, stack);
			InventoryClickEvent addItem = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 54, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(addItem);
			InventoryClickEvent increaseAmount = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 6, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(increaseAmount);
			InventoryClickEvent addBuyPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 24, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(addBuyPrice);
			InventoryClickEvent addSellPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 14, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(addSellPrice);	
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 8, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(event);
			assertEquals("§6Updated §aamount §asellPrice §abuyPrice §6for item §astone", player.nextMessage());
			assertNull(player.nextMessage());
			assertEquals(1,  AdminshopController.getAdminshopList().get(0).getItemList().size());
			ChestInventoryMock editor = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myshop-Editor", editor.getName());
			ShopItem shopItem = AdminshopController.getAdminshopList().get(0).getItemList().get(0);		
			assertEquals(21, shopItem.getAmount());
			assertEquals("39.0", String.valueOf(shopItem.getSellPrice()));
			assertEquals("14.0", String.valueOf(shopItem.getBuyPrice()));
			assertEquals(0, shopItem.getSlot());
			stack.setAmount(1);
			assertEquals(stack, shopItem.getItemStack());
			assertEquals("ItemStack{STONE x 1}", shopItem.getItemString());
			player.closeInventory();
		} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleSlotEditorTestSaveChangesOtherItem() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).addShopItem(0, 4.0, 19.0, new ItemStack(Material.STONE));
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
			ItemStack stack = new ItemStack(Material.ACACIA_BOAT);
			stack.setAmount(1);
			player.getInventory().setItem(0, stack);
			InventoryClickEvent addItem = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 54, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(addItem);	
			InventoryClickEvent increaseAmount = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 6, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(increaseAmount);
			InventoryClickEvent addBuyPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 24, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(addBuyPrice);
			InventoryClickEvent addSellPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 14, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(addSellPrice);	
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 8, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(event);
			assertEquals("§6The item §astone§6 was removed from the shop.", player.nextMessage());
			assertEquals("§6The item §aacacia_boat§6 was added to the shop.", player.nextMessage());
			assertNull(player.nextMessage());
			assertEquals(1,  AdminshopController.getAdminshopList().get(0).getItemList().size());
			ChestInventoryMock editor = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myshop-Editor", editor.getName());
			ShopItem shopItem = AdminshopController.getAdminshopList().get(0).getItemList().get(0);		
			assertEquals(21, shopItem.getAmount());
			assertEquals("39.0", String.valueOf(shopItem.getSellPrice()));
			assertEquals("14.0", String.valueOf(shopItem.getBuyPrice()));
			assertEquals(0, shopItem.getSlot());
			stack.setAmount(1);
			assertEquals(stack, shopItem.getItemStack());
			assertEquals("ItemStack{ACACIA_BOAT x 1}", shopItem.getItemString());
			player.closeInventory();
		} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleSlotEditorTestSaveChangesOtherItemSpawner() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			ItemStack spawner = new ItemStack(Material.SPAWNER);
			ItemMeta meta = spawner.getItemMeta();
			meta.setDisplayName("COW");
			spawner.setItemMeta(meta);
			AdminshopController.getAdminshopList().get(0).addShopItem(0, 4.0, 19.0, spawner);
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
			ItemStack stack = new ItemStack(Material.ACACIA_BOAT);
			stack.setAmount(1);
			player.getInventory().setItem(0, stack);
			InventoryClickEvent addItem = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 54, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(addItem);	
			InventoryClickEvent increaseAmount = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 6, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(increaseAmount);
			InventoryClickEvent addBuyPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 24, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(addBuyPrice);
			InventoryClickEvent addSellPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 14, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(addSellPrice);	
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 8, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(event);
			assertEquals("§6The spawner §acow§6 was removed from shop.", player.nextMessage());
			assertEquals("§6The item §aacacia_boat§6 was added to the shop.", player.nextMessage());
			assertNull(player.nextMessage());
			assertEquals(1,  AdminshopController.getAdminshopList().get(0).getItemList().size());
			ChestInventoryMock editor = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myshop-Editor", editor.getName());
			ShopItem shopItem = AdminshopController.getAdminshopList().get(0).getItemList().get(0);		
			assertEquals(21, shopItem.getAmount());
			assertEquals("39.0", String.valueOf(shopItem.getSellPrice()));
			assertEquals("14.0", String.valueOf(shopItem.getBuyPrice()));
			assertEquals(0, shopItem.getSlot());
			stack.setAmount(1);
			assertEquals(stack, shopItem.getItemStack());
			assertEquals("ItemStack{ACACIA_BOAT x 1}", shopItem.getItemString());
			player.closeInventory();
		} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleSlotEditorTestRemoveItem() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
			AdminshopController.getAdminshopList().get(0).addShopItem(0, 4.0, 19.0, new ItemStack(Material.STONE));
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 26, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
			handler.handleSlotEditor(event);
			assertEquals(0,  AdminshopController.getAdminshopList().get(0).getItemList().size());
			ChestInventoryMock editor = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myshop-Editor", editor.getName());
			assertEquals("§6The item §astone§6 was removed from the shop.", player.nextMessage());
			assertNull(player.nextMessage());
		} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleSlotEditorTestFactorMinusTwentyClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent turnFactorOn = new InventoryClickEvent(view, SlotType.CONTAINER, 12, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(turnFactorOn);
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 15, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 11, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 20000.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 0.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorMinusTwentyClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent turnFactorOn = new InventoryClickEvent(view, SlotType.CONTAINER, 21, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(turnFactorOn);
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 24, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 20, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 0.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 20000.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorPlusTwentyClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent turnFactorOn = new InventoryClickEvent(view, SlotType.CONTAINER, 12, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(turnFactorOn);
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 15, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 20000.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 0.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorPlusTwentyClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent turnFactorOn = new InventoryClickEvent(view, SlotType.CONTAINER, 21, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(turnFactorOn);
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 24, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 0.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 20000.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorMinusTenClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent turnFactorOn = new InventoryClickEvent(view, SlotType.CONTAINER, 12, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(turnFactorOn);
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 14, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 11, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 10000.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 0.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorMinusTenClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent turnFactorOn = new InventoryClickEvent(view, SlotType.CONTAINER, 21, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(turnFactorOn);
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 23, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 20, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 0.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 10000.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorPlusTenClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent turnFactorOn = new InventoryClickEvent(view, SlotType.CONTAINER, 12, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(turnFactorOn);
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 14, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 10000.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 0.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorPlusTenClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent turnFactorOn = new InventoryClickEvent(view, SlotType.CONTAINER, 21, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(turnFactorOn);
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 23, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 0.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 10000.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorMinusOneClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent turnFactorOn = new InventoryClickEvent(view, SlotType.CONTAINER, 12, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(turnFactorOn);
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 13, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 11, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 1000.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 0.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorMinusOneClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent turnFactorOn = new InventoryClickEvent(view, SlotType.CONTAINER, 21, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(turnFactorOn);
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 22, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 20, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(switchToMinus);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 0.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 1000.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorPlusOneClick1() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent turnFactorOn = new InventoryClickEvent(view, SlotType.CONTAINER, 12, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(turnFactorOn);
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 13, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 1000.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 0.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
	
	@Test
	public void handleSlotEditorTestFactorPlusOneClick2() {
		ShopSlotEditorHandler handler = ((AbstractShopImpl) AdminshopController.getAdminshopList().get(0)).getSlotEditorHandler();
		try {
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent turnFactorOn = new InventoryClickEvent(view, SlotType.CONTAINER, 21, ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(turnFactorOn);
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 22, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		List<String> sellPrice = new ArrayList<String>();
		sellPrice.add("§6Price: 0.0");
		List<String> buyPrice = new ArrayList<String>();
		buyPrice.add("§6Price: 1000.0");
		checkBuySellInfoItems(slotEditor, sellPrice, buyPrice);
		player.closeInventory();
	}
}
