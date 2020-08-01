package com.ue.shopsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
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
import com.ue.shopsystem.impl.ShopEditorHandler;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;
import be.seeseemelk.mockbukkit.inventory.PlayerInventoryViewMock;

public class ShopEditorHandlerTest {

	private static final String SLOTEMPTY = "http://textures.minecraft.net/texture/"
			+ "b55d5019c8d55bcb9dc3494ccc3419757f89c3384cf3c9abec3f18831f35b0";
	private static final String SLOTFILLED = "http://textures.minecraft.net/texture/"
			+ "9e42f682e430b55b61204a6f8b76d5227d278ed9ec4d98bda4a7a4830a4b6";
	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
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
		try {
			AdminshopController.getAdminshopList().get(0).addShopItem(0, 1, 1, new ItemStack(Material.STICK));
			AdminshopController.getAdminshopList().get(0).addShopItem(1, 1, 1, new ItemStack(Material.STONE));
			ShopEditorHandler handler = new ShopEditorHandler((AbstractShopImpl) AdminshopController.getAdminshopList().get(0));
			ChestInventoryMock editor = (ChestInventoryMock) handler.getEditorInventory();
			assertEquals(9, editor.getSize());
			assertEquals("myshop-Editor", editor.getName());
			assertEquals(AdminshopController.getAdminshopList().get(0).getShopVillager(), editor.getHolder());
			checkInventory(editor);
			NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
			assertEquals(SLOTFILLED,
					editor.getItem(0).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
			assertEquals(SLOTFILLED,
					editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}	
	}
	
	@Test
	public void setOccupiedTestTrue() {
		ShopEditorHandler handler = new ShopEditorHandler((AbstractShopImpl) AdminshopController.getAdminshopList().get(0));
		handler.setOccupied(true, 0);
		ChestInventoryMock editor = (ChestInventoryMock) handler.getEditorInventory();
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(Material.PLAYER_HEAD, editor.getItem(0).getType());
		assertEquals("Slot 1", editor.getItem(0).getItemMeta().getDisplayName());
		assertEquals(SLOTFILLED,
				editor.getItem(0).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	}
	
	@Test
	public void setOccupiedTestFalse() {
		ShopEditorHandler handler = new ShopEditorHandler((AbstractShopImpl) AdminshopController.getAdminshopList().get(0));
		handler.setOccupied(true, 0);
		handler.setOccupied(false, 0);
		ChestInventoryMock editor = (ChestInventoryMock) handler.getEditorInventory();
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(Material.PLAYER_HEAD, editor.getItem(0).getType());
		assertEquals("Slot 1", editor.getItem(0).getItemMeta().getDisplayName());
		assertEquals(SLOTEMPTY,
				editor.getItem(0).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	}
	
	@Test
	public void changeInventoryNameTest() {
		ShopEditorHandler handler = new ShopEditorHandler((AbstractShopImpl) AdminshopController.getAdminshopList().get(0));
		handler.changeInventoryName("kth");
		ChestInventoryMock editor = (ChestInventoryMock) handler.getEditorInventory();
		assertEquals(9, editor.getSize());
		assertEquals("kth-Editor", editor.getName());
		checkInventory(editor);
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(SLOTEMPTY,
				editor.getItem(0).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals(SLOTEMPTY,
				editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	}
	
	private void checkInventory(ChestInventoryMock editor) {
		assertEquals(Material.PLAYER_HEAD, editor.getItem(0).getType());
		assertEquals(Material.PLAYER_HEAD, editor.getItem(1).getType());
		assertEquals(Material.PLAYER_HEAD, editor.getItem(2).getType());
		assertEquals(Material.PLAYER_HEAD, editor.getItem(3).getType());
		assertEquals(Material.PLAYER_HEAD, editor.getItem(4).getType());
		assertEquals(Material.PLAYER_HEAD, editor.getItem(5).getType());
		assertEquals(Material.PLAYER_HEAD, editor.getItem(6).getType());
		assertEquals(Material.PLAYER_HEAD, editor.getItem(7).getType());
		assertNull(editor.getItem(8));
		assertEquals("Slot 1", editor.getItem(0).getItemMeta().getDisplayName());
		assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
		assertEquals("Slot 3", editor.getItem(2).getItemMeta().getDisplayName());
		assertEquals("Slot 4", editor.getItem(3).getItemMeta().getDisplayName());
		assertEquals("Slot 5", editor.getItem(4).getItemMeta().getDisplayName());
		assertEquals("Slot 6", editor.getItem(5).getItemMeta().getDisplayName());
		assertEquals("Slot 7", editor.getItem(6).getItemMeta().getDisplayName());
		assertEquals("Slot 8", editor.getItem(7).getItemMeta().getDisplayName());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(SLOTEMPTY,
				editor.getItem(2).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals(SLOTEMPTY,
				editor.getItem(3).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals(SLOTEMPTY,
				editor.getItem(4).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals(SLOTEMPTY,
				editor.getItem(5).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals(SLOTEMPTY,
				editor.getItem(6).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals(SLOTEMPTY,
				editor.getItem(7).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	}
	
	@Test
	public void handleInventoryClickTestFree() {
		AbstractShopImpl shop = (AbstractShopImpl) AdminshopController.getAdminshopList().get(0);
		ShopEditorHandler handler = new ShopEditorHandler(shop);
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleInventoryClick(event);
		assertEquals("myshop-SlotEditor", ((ChestInventoryMock) player.getOpenInventory().getTopInventory()).getName());
	}
	
	@Test
	public void handleInventoryClickTestBottomInvClick() {
		AbstractShopImpl shop = (AbstractShopImpl) AdminshopController.getAdminshopList().get(0);
		ShopEditorHandler handler = new ShopEditorHandler(shop);
		try {
			shop.openEditor(player);
		} catch (ShopSystemException e) {
			fail();
		}
		InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 9, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleInventoryClick(event);
		assertEquals("myshop-Editor", ((ChestInventoryMock) player.getOpenInventory().getTopInventory()).getName());
	}
	
	@Test
	public void handleInventoryClickTestWithFilled() {
		AbstractShopImpl shop = (AbstractShopImpl) AdminshopController.getAdminshopList().get(0);
		try {
			shop.addShopItem(0, 1, 1, new ItemStack(Material.STONE));
		} catch (ShopSystemException | PlayerException | GeneralEconomyException e) {
			fail();
		}
		ShopEditorHandler handler = new ShopEditorHandler(shop);
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ONE);		
		handler.handleInventoryClick(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
		assertEquals("myshop-SlotEditor", slotEditor.getName());
		assertEquals(new ItemStack(Material.STONE), slotEditor.getItem(0));
	}
}
