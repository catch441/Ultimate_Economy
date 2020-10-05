package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
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
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.Playershop;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.impl.PlayershopImpl;
import com.ue.shopsystem.logic.impl.RentshopImpl;
import com.ue.shopsystem.logic.impl.RentshopManagerImpl;
import com.ue.shopsystem.logic.impl.ShopSystemException;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.TownworldManager;
import com.ue.townsystem.logic.impl.TownSystemException;
import com.ue.ultimate_economy.EconomyVillager;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

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
	@Mock
	EconomyPlayerManager ecoPlayerManager;

	/**
	 * Location location = new Location(world, 1.5, 2.3, 6.9); try { Rentshop shop =
	 * RentshopManagerImpl.createRentShop(location, 9, 5); assertEquals(world,
	 * shop.getWorld()); assertEquals("R0", shop.getShopId());
	 * assertEquals("RentShop#R0", shop.getName()); assertNull(shop.getOwner());
	 * assertEquals(EconomyVillager.PLAYERSHOP_RENTABLE,
	 * shop.getShopVillager().getMetadata("ue-type").get(0).value());
	 * assertEquals("5.0", String.valueOf(shop.getRentalFee()));
	 * assertTrue(shop.isRentable()); assertEquals(0L, shop.getRentUntil()); //
	 * check rentshop gui shop.openRentGUI(player); ChestInventoryMock gui =
	 * (ChestInventoryMock) player.getOpenInventory().getTopInventory();
	 * player.closeInventory(); NamespacedKey key = new
	 * NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	 * assertEquals("RentShop#R0", gui.getName()); assertEquals(9, gui.getSize());
	 * assertEquals(Material.GREEN_WOOL, gui.getItem(0).getType());
	 * assertEquals(ChatColor.YELLOW + "Rent",
	 * gui.getItem(0).getItemMeta().getDisplayName()); assertEquals(ChatColor.GOLD +
	 * "RentalFee: " + ChatColor.GREEN + 5.0,
	 * gui.getItem(0).getItemMeta().getLore().get(0)); assertEquals(Material.CLOCK,
	 * gui.getItem(1).getType()); assertEquals(1,
	 * gui.getItem(1).getItemMeta().getLore().size()); assertEquals(ChatColor.YELLOW
	 * + "Duration", gui.getItem(1).getItemMeta().getDisplayName());
	 * assertEquals(ChatColor.GOLD + "Duration: " + ChatColor.GREEN + 1 +
	 * ChatColor.GOLD + " Day", gui.getItem(1).getItemMeta().getLore().get(0));
	 * assertEquals(Material.PLAYER_HEAD, gui.getItem(3).getType());
	 * assertNull(gui.getItem(3).getItemMeta().getLore()); assertEquals("plus",
	 * gui.getItem(3).getItemMeta().getDisplayName()); assertEquals(PLUS,
	 * gui.getItem(3).getItemMeta().getPersistentDataContainer().get(key,
	 * PersistentDataType.STRING)); assertEquals(Material.PLAYER_HEAD,
	 * gui.getItem(4).getType()); assertEquals(1,
	 * gui.getItem(4).getItemMeta().getLore().size()); assertEquals("one",
	 * gui.getItem(4).getItemMeta().getDisplayName()); assertEquals(ChatColor.GOLD +
	 * "Duration: " + ChatColor.GREEN + 1 + ChatColor.GOLD + " Day",
	 * gui.getItem(4).getItemMeta().getLore().get(0)); assertEquals(ONE,
	 * gui.getItem(4).getItemMeta().getPersistentDataContainer().get(key,
	 * PersistentDataType.STRING)); assertEquals(Material.PLAYER_HEAD,
	 * gui.getItem(4).getType()); assertEquals(1,
	 * gui.getItem(5).getItemMeta().getLore().size()); assertEquals("seven",
	 * gui.getItem(5).getItemMeta().getDisplayName()); assertEquals(ChatColor.GOLD +
	 * "Duration: " + ChatColor.GREEN + 1 + ChatColor.GOLD + " Day",
	 * gui.getItem(5).getItemMeta().getLore().get(0)); assertEquals(SEVEN,
	 * gui.getItem(5).getItemMeta().getPersistentDataContainer().get(key,
	 * PersistentDataType.STRING)); // check savefile File saveFile = new
	 * File(UltimateEconomy.getInstance.getDataFolder(), "R0.yml");
	 * YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	 * assertEquals("5.0", config.getString("RentalFee"));
	 * assertTrue(config.getBoolean("Rentable")); assertEquals(1,
	 * UltimateEconomy.getInstance.getConfig().getStringList("RentShopIds").size());
	 * assertEquals("R0",
	 * UltimateEconomy.getInstance.getConfig().getStringList("RentShopIds").get(0));
	 * assertNull(config.getString("RentUntil")); } catch (GeneralEconomyException |
	 * ShopSystemException e) { fail(); }
	 */

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
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			RentshopManagerImpl.createRentShop(location, 9, 10);
			Rentshop shop = new RentshopImpl("R0");
			assertEquals("10.0", String.valueOf(shop.getRentalFee()));
			assertTrue(shop.isRentable());
			assertEquals("RentShop#R0", shop.getShopVillager().getCustomName());
			assertNull(shop.getOwner());
			assertEquals(EconomyVillager.PLAYERSHOP_RENTABLE,
					shop.getShopVillager().getMetadata("ue-type").get(0).value());
			// check rentshop gui
			shop.openRentGUI(player);
			ChestInventoryMock gui = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals(9, gui.getSize());
			assertEquals("RentShop#R0", gui.getName());
			assertEquals(1, gui.getItem(0).getAmount());
			assertEquals(Material.GREEN_WOOL, gui.getItem(0).getType());
			assertEquals(ChatColor.YELLOW + "Rent", gui.getItem(0).getItemMeta().getDisplayName());
			assertEquals(1, gui.getItem(0).getItemMeta().getLore().size());
			assertEquals("§6RentalFee: §a10.0", gui.getItem(0).getItemMeta().getLore().get(0));
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
		} catch (GeneralEconomyException | TownSystemException | EconomyPlayerException | ShopSystemException e) {
			fail();
		}
	}

	@Test
	public void constructorLoadTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop setup = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).increasePlayerAmount(100, false);
			setup.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 10);
			Rentshop shop = new RentshopImpl("R0");
			assertEquals("10.0", String.valueOf(shop.getRentalFee()));
			assertFalse(shop.isRentable());
			assertEquals("Shop#R0_catch441", shop.getShopVillager().getCustomName());
			assertEquals(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), shop.getOwner());
			assertEquals(EconomyVillager.PLAYERSHOP_RENTABLE,
					shop.getShopVillager().getMetadata("ue-type").get(0).value());
		} catch (GeneralEconomyException | TownSystemException | EconomyPlayerException | ShopSystemException e) {
			fail();
		}
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
	public void changeShopNameTestWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.changeShopName("newname");
			assertEquals("newname", shop.getName());
			assertEquals("newname#R0", shop.getShopVillager().getCustomName());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "R0.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals("newname", config.getString("ShopName"));
		} catch (GeneralEconomyException | ShopSystemException e) {
			fail();
		}
	}

	@Test
	public void changeShopNameTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).increasePlayerAmount(100, false);
			shop.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 10);
			shop.changeShopName("newname");
			assertEquals("newname", shop.getName());
			assertEquals("newname_catch441", shop.getShopVillager().getCustomName());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "R0.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals("newname", config.getString("ShopName"));
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void rentShopTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			assertEquals(ecoPlayer, shop.getOwner());
			assertEquals("Shop#R0", shop.getName());
			assertFalse(shop.isRentable());
			// TODO rent until
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "R0.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertFalse(config.getBoolean("Rentable"));
			assertNotNull(config.getString("RentUntil"));
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void rentShopTestWithNotEnoughMoney() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 10);
			fail();
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou have not enough money!", e.getMessage());
		}
	}

	@Test
	public void rentShopTestWithAlreadyRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.rentShop(ecoPlayer, 10);
			fail();
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThis shop is rented!", e.getMessage());
		}
	}

	@Test
	public void resetShopTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.addShopItem(0, 1, 1, new ItemStack(Material.STONE));
			shop.resetShop();
			assertNull(shop.getOwner());
			assertEquals(0L, shop.getRentUntil());
			assertTrue(shop.isRentable());
			assertEquals(Profession.NITWIT, shop.getShopVillager().getProfession());
			assertEquals("RentShop", shop.getName());
			assertEquals("RentShop#R0", shop.getShopVillager().getCustomName());
			assertEquals(0, shop.getItemList().size());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "R0.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertTrue(config.getBoolean("Rentable"));
			assertEquals(0L, config.getLong("RentUntil"));
			assertNull(config.getString("Owner"));
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void changeRentalFee() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.changeRentalFee(25);
			assertEquals("25.0", String.valueOf(shop.getRentalFee()));
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "R0.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals("25.0", config.getString("RentalFee"));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void changeRentalFeeWithNegativValue() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.changeRentalFee(-25);
			fail();
		} catch (GeneralEconomyException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4-25.0§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void changeShopSizeTestWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.changeShopSize(18);
			assertEquals(18, shop.getSize());
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void changeShopSizeTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.changeShopSize(18);
			fail();
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThis shop is rented!", e.getMessage());
		}
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
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			shop.changeOwner(ecoPlayer);
			assertEquals(ecoPlayer, shop.getOwner());
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void changeOwnerTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.changeOwner(ecoPlayer);
			fail();
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThis shop is rented!", e.getMessage());
		}
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
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.addShopItem(0, 1, 0, new ItemStack(Material.STONE));
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void editShopItemTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.editShopItem(0, null, null, null));
	}

	@Test
	public void editShopTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.addShopItem(0, 1, 0, new ItemStack(Material.STONE));
			shop.editShopItem(0, "none", "none", "1");
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void getShopItemTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.getShopItem(null));
	}

	@Test
	public void getShopItemTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.addShopItem(0, 1, 0, new ItemStack(Material.STONE));
			shop.getShopItem(new ItemStack(Material.STONE));
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void isAvailableTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.isAvailable(0));
	}

	@Test
	public void isAvailableTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.addShopItem(0, 1, 0, new ItemStack(Material.STONE));
			shop.isAvailable(0);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
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
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.addShopItem(0, 1, 0, new ItemStack(Material.STONE));
			shop.increaseStock(0, 10);
			shop.decreaseStock(0, 5);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void increaseStockTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.increaseStock(0, 0));
	}

	@Test
	public void increaseStockTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.addShopItem(0, 1, 0, new ItemStack(Material.STONE));
			shop.increaseStock(0, 10);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void removeShopItemTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.removeShopItem(0));
	}

	@Test
	public void removeShopItemTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.addShopItem(0, 1, 0, new ItemStack(Material.STONE));
			shop.removeShopItem(0);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
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
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.openSlotEditor(player, 0);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void openEditorTestWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.openEditor(null));
	}

	@Test
	public void openEditorTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.openEditor(player);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void buyShopItemWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.buyShopItem(0, null, true));
	}

	@Test
	public void buyShopItemTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.addShopItem(0, 1, 0, new ItemStack(Material.STONE));
			shop.increaseStock(0, 10);
			shop.buyShopItem(0, EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), false);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void sellShopItemWithNotRented() throws ShopSystemException {
		Rentshop shop = createRentshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForIsRented(true);
		assertThrows(ShopSystemException.class, () -> shop.sellShopItem(0, 1, null, false));
	}

	@Test
	public void sellShopItemTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.addShopItem(0, 1, 0, new ItemStack(Material.STONE));
			shop.sellShopItem(0, 1, EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), false);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}
}
