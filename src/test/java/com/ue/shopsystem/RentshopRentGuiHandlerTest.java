package com.ue.shopsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.shopsystem.api.RentshopController;
import com.ue.shopsystem.impl.RentshopRentGuiHandler;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;
import be.seeseemelk.mockbukkit.inventory.PlayerInventoryViewMock;

public class RentshopRentGuiHandlerTest {
	
	private static final String PLUS = "http://textures.minecraft.net/texture/"
			+ "9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777";
	private static final String ONE = "http://textures.minecraft.net/texture/"
			+ "d2a6f0e84daefc8b21aa99415b16ed5fdaa6d8dc0c3cd591f49ca832b575";
	private static final String SEVEN = "http://textures.minecraft.net/texture/"
			+ "9e198fd831cb61f3927f21cf8a7463af5ea3c7e43bd3e8ec7d2948631cce879";
	private static final String MINUS = "http://textures.minecraft.net/texture/"
			+ "935e4e26eafc11b52c11668e1d6634e7d1d0d21c411cb085f9394268eb4cdfba";
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
	
	@Test
	public void constructorTest() {
		try {
			RentshopController.createRentShop(new Location(world,6,7,6), 9, 4.5);
			RentshopRentGuiHandler handler = new RentshopRentGuiHandler(RentshopController.getRentShops().get(0));
			ChestInventoryMock gui = (ChestInventoryMock) handler.getRentGui();
			assertEquals(9, gui.getSize());
			assertEquals("RentShop#R0", gui.getName());
			assertEquals(1, gui.getItem(0).getAmount());
			assertEquals(Material.GREEN_WOOL, gui.getItem(0).getType());
			assertEquals(ChatColor.YELLOW + "Rent", gui.getItem(0).getItemMeta().getDisplayName());
			assertEquals(1, gui.getItem(0).getItemMeta().getLore().size());
			assertEquals("§6RentalFee: §a4.5", gui.getItem(0).getItemMeta().getLore().get(0));		
			assertEquals(1, gui.getItem(1).getAmount());
			assertEquals(Material.CLOCK, gui.getItem(1).getType());
			assertEquals(ChatColor.YELLOW + "Duration", gui.getItem(1).getItemMeta().getDisplayName());
			assertEquals(1, gui.getItem(1).getItemMeta().getLore().size());
			assertEquals("§6Duration: §a1§6 Day", gui.getItem(1).getItemMeta().getLore().get(0));
			NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
			assertEquals(1, gui.getItem(3).getAmount());
			assertEquals(Material.PLAYER_HEAD, gui.getItem(3).getType());
			assertEquals("plus", gui.getItem(3).getItemMeta().getDisplayName());
			assertEquals(PLUS,
					gui.getItem(3).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));	
			assertEquals(1, gui.getItem(4).getAmount());
			assertEquals(Material.PLAYER_HEAD, gui.getItem(4).getType());
			assertEquals("one", gui.getItem(4).getItemMeta().getDisplayName());
			assertEquals(1, gui.getItem(4).getItemMeta().getLore().size());
			assertEquals("§6Duration: §a1§6 Day", gui.getItem(4).getItemMeta().getLore().get(0));
			assertEquals(ONE,
					gui.getItem(4).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
			assertEquals(1, gui.getItem(5).getAmount());
			assertEquals(Material.PLAYER_HEAD, gui.getItem(5).getType());
			assertEquals("seven", gui.getItem(5).getItemMeta().getDisplayName());
			assertEquals(1, gui.getItem(5).getItemMeta().getLore().size());
			assertEquals("§6Duration: §a1§6 Day", gui.getItem(5).getItemMeta().getLore().get(0));
			assertEquals(SEVEN,
					gui.getItem(5).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleRentShopGuiClickTestPlusClick() {
		try {
			RentshopController.createRentShop(new Location(world,6,7,6), 9, 4.5);
			RentshopRentGuiHandler handler = new RentshopRentGuiHandler(RentshopController.getRentShops().get(0));
			PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getRentGui());
			InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 3, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(event);
			ChestInventoryMock gui = (ChestInventoryMock) handler.getRentGui();
			NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
			assertEquals(1, gui.getItem(3).getAmount());
			assertEquals(Material.PLAYER_HEAD, gui.getItem(3).getType());
			assertEquals("minus", gui.getItem(3).getItemMeta().getDisplayName());
			assertEquals(MINUS,
					gui.getItem(3).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));	
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleRentShopGuiClickTestMinusClick() {
		try {
			RentshopController.createRentShop(new Location(world,6,7,6), 9, 4.5);
			RentshopRentGuiHandler handler = new RentshopRentGuiHandler(RentshopController.getRentShops().get(0));
			PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getRentGui());
			InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 3, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(event);
			handler.handleRentShopGUIClick(event);
			ChestInventoryMock gui = (ChestInventoryMock) handler.getRentGui();
			NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
			assertEquals(1, gui.getItem(3).getAmount());
			assertEquals(Material.PLAYER_HEAD, gui.getItem(3).getType());
			assertEquals("plus", gui.getItem(3).getItemMeta().getDisplayName());
			assertEquals(PLUS,
					gui.getItem(3).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));	
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleRentShopGuiClickTestPlusOneClick() {
		try {
			RentshopController.createRentShop(new Location(world,6,7,6), 9, 4.5);
			RentshopRentGuiHandler handler = new RentshopRentGuiHandler(RentshopController.getRentShops().get(0));
			PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getRentGui());
			InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 4, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(event);
			ChestInventoryMock gui = (ChestInventoryMock) handler.getRentGui();
			List<String> list = new ArrayList<>();
			list.add("§6Duration: §a2§6 Days");
			assertEquals(list, gui.getItem(1).getItemMeta().getLore());
			assertEquals(list, gui.getItem(4).getItemMeta().getLore());
			assertEquals(list, gui.getItem(5).getItemMeta().getLore());
			list.clear();
			list.add("§6RentalFee: §a9.0");
			assertEquals(list, gui.getItem(0).getItemMeta().getLore());
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleRentShopGuiClickTestMinusOneClick() {
		try {
			RentshopController.createRentShop(new Location(world,6,7,6), 9, 4.5);
			RentshopRentGuiHandler handler = new RentshopRentGuiHandler(RentshopController.getRentShops().get(0));
			PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getRentGui());
			InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 4, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(event);
			InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 3, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(switchToMinus);
			handler.handleRentShopGUIClick(event);
			ChestInventoryMock gui = (ChestInventoryMock) handler.getRentGui();
			List<String> list = new ArrayList<>();
			list.add("§6Duration: §a1§6 Day");
			assertEquals(list, gui.getItem(1).getItemMeta().getLore());
			assertEquals(list, gui.getItem(4).getItemMeta().getLore());
			assertEquals(list, gui.getItem(5).getItemMeta().getLore());
			list.clear();
			list.add("§6RentalFee: §a4.5");
			assertEquals(list, gui.getItem(0).getItemMeta().getLore());
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleRentShopGuiClickTestMinusOneClickMore() {
		try {
			RentshopController.createRentShop(new Location(world,6,7,6), 9, 4.5);
			RentshopRentGuiHandler handler = new RentshopRentGuiHandler(RentshopController.getRentShops().get(0));
			PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getRentGui());
			InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 4, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(event);
			InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 3, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(switchToMinus);
			handler.handleRentShopGUIClick(event);
			handler.handleRentShopGUIClick(event);
			handler.handleRentShopGUIClick(event);
			ChestInventoryMock gui = (ChestInventoryMock) handler.getRentGui();
			List<String> list = new ArrayList<>();
			list.add("§6Duration: §a1§6 Day");
			assertEquals(list, gui.getItem(1).getItemMeta().getLore());
			assertEquals(list, gui.getItem(4).getItemMeta().getLore());
			assertEquals(list, gui.getItem(5).getItemMeta().getLore());
			list.clear();
			list.add("§6RentalFee: §a4.5");
			assertEquals(list, gui.getItem(0).getItemMeta().getLore());
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleRentShopGuiClickTestPlusOneClickMore() {
		try {
			ConfigController.setMaxRentedDays(3);
			RentshopController.createRentShop(new Location(world,6,7,6), 9, 4.5);
			RentshopRentGuiHandler handler = new RentshopRentGuiHandler(RentshopController.getRentShops().get(0));
			PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getRentGui());
			InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 4, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(event);
			handler.handleRentShopGUIClick(event);
			handler.handleRentShopGUIClick(event);
			handler.handleRentShopGUIClick(event);
			ChestInventoryMock gui = (ChestInventoryMock) handler.getRentGui();
			List<String> list = new ArrayList<>();
			list.add("§6Duration: §a3§6 Days");
			assertEquals(list, gui.getItem(1).getItemMeta().getLore());
			assertEquals(list, gui.getItem(4).getItemMeta().getLore());
			assertEquals(list, gui.getItem(5).getItemMeta().getLore());
			list.clear();
			list.add("§6RentalFee: §a13.5");
			assertEquals(list, gui.getItem(0).getItemMeta().getLore());
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
			ConfigController.setMaxRentedDays(14);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void handleRentShopGuiClickTestPlusSevenClick() {
		try {
			RentshopController.createRentShop(new Location(world,6,7,6), 9, 4.5);
			RentshopRentGuiHandler handler = new RentshopRentGuiHandler(RentshopController.getRentShops().get(0));
			PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getRentGui());
			InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 5, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(event);
			ChestInventoryMock gui = (ChestInventoryMock) handler.getRentGui();
			List<String> list = new ArrayList<>();
			list.add("§6Duration: §a8§6 Days");
			assertEquals(list, gui.getItem(1).getItemMeta().getLore());
			assertEquals(list, gui.getItem(4).getItemMeta().getLore());
			assertEquals(list, gui.getItem(5).getItemMeta().getLore());
			list.clear();
			list.add("§6RentalFee: §a36.0");
			assertEquals(list, gui.getItem(0).getItemMeta().getLore());
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleRentShopGuiClickTestMinusSevenClick() {
		try {
			RentshopController.createRentShop(new Location(world,6,7,6), 9, 4.5);
			RentshopRentGuiHandler handler = new RentshopRentGuiHandler(RentshopController.getRentShops().get(0));
			PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getRentGui());
			InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 5, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(event);
			InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 3, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(switchToMinus);
			handler.handleRentShopGUIClick(event);
			ChestInventoryMock gui = (ChestInventoryMock) handler.getRentGui();
			List<String> list = new ArrayList<>();
			list.add("§6Duration: §a1§6 Day");
			assertEquals(list, gui.getItem(1).getItemMeta().getLore());
			assertEquals(list, gui.getItem(4).getItemMeta().getLore());
			assertEquals(list, gui.getItem(5).getItemMeta().getLore());
			list.clear();
			list.add("§6RentalFee: §a4.5");
			assertEquals(list, gui.getItem(0).getItemMeta().getLore());
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleRentShopGuiClickTestMinusSevenClickMore() {
		try {
			RentshopController.createRentShop(new Location(world,6,7,6), 9, 4.5);
			RentshopRentGuiHandler handler = new RentshopRentGuiHandler(RentshopController.getRentShops().get(0));
			PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getRentGui());
			InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 5, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(event);
			InventoryClickEvent switchToMinus = new InventoryClickEvent(view, SlotType.CONTAINER, 3, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(switchToMinus);
			handler.handleRentShopGUIClick(event);
			handler.handleRentShopGUIClick(event);
			handler.handleRentShopGUIClick(event);
			ChestInventoryMock gui = (ChestInventoryMock) handler.getRentGui();
			List<String> list = new ArrayList<>();
			list.add("§6Duration: §a1§6 Day");
			assertEquals(list, gui.getItem(1).getItemMeta().getLore());
			assertEquals(list, gui.getItem(4).getItemMeta().getLore());
			assertEquals(list, gui.getItem(5).getItemMeta().getLore());
			list.clear();
			list.add("§6RentalFee: §a4.5");
			assertEquals(list, gui.getItem(0).getItemMeta().getLore());
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleRentShopGuiClickTestPlusSevenClickMore() {
		try {
			ConfigController.setMaxRentedDays(14);
			RentshopController.createRentShop(new Location(world,6,7,6), 9, 4.5);
			RentshopRentGuiHandler handler = new RentshopRentGuiHandler(RentshopController.getRentShops().get(0));
			PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getRentGui());
			InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 5, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(event);
			handler.handleRentShopGUIClick(event);
			handler.handleRentShopGUIClick(event);
			handler.handleRentShopGUIClick(event);
			ChestInventoryMock gui = (ChestInventoryMock) handler.getRentGui();
			List<String> list = new ArrayList<>();
			list.add("§6Duration: §a14§6 Days");
			assertEquals(list, gui.getItem(1).getItemMeta().getLore());
			assertEquals(list, gui.getItem(4).getItemMeta().getLore());
			assertEquals(list, gui.getItem(5).getItemMeta().getLore());
			list.clear();
			list.add("§6RentalFee: §a63.0");
			assertEquals(list, gui.getItem(0).getItemMeta().getLore());
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleRentShopGuiClickTestRentClickError() {
		try {
			RentshopController.createRentShop(new Location(world,6,7,6), 9, 4.5);
			RentshopRentGuiHandler handler = new RentshopRentGuiHandler(RentshopController.getRentShops().get(0));
			PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getRentGui());
			InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(event);
			assertEquals("§cYou have not enough money!", player.nextMessage());
			assertNull(player.nextMessage());
			assertNull(player.getOpenInventory().getTopInventory());
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleRentShopGuiClickTestRentClick() {
		try {
			EconomyPlayerController.getAllEconomyPlayers().get(0).increasePlayerAmount(4.5, false);
			RentshopController.createRentShop(new Location(world,6,7,6), 9, 4.5);
			RentshopRentGuiHandler handler = new RentshopRentGuiHandler(RentshopController.getRentShops().get(0));
			PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getRentGui());
			InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleRentShopGUIClick(event);
			assertEquals("§6You rented this shop.", player.nextMessage());
			assertNull(player.nextMessage());
			assertNull(player.getOpenInventory().getTopInventory());
			assertTrue(RentshopController.getRentShops().get(0).isOwner(EconomyPlayerController.getAllEconomyPlayers().get(0)));
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
}
