package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.api.Playershop;
import com.ue.shopsystem.api.PlayershopController;
import com.ue.townsystem.api.TownController;
import com.ue.townsystem.api.TownworldController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;

public class PlayershopControllerTest {

	private static ServerMock server;
	private static WorldMock world;
	private static Player player;

	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		player = server.addPlayer("catch441");
		server.addPlayer("kthschnll");
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

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {
		int size = PlayershopController.getPlayerShops().size();
		for (int i = 0; i < size; i++) {
			PlayershopController.deletePlayerShop(PlayershopController.getPlayerShops().get(0));
		}
	}

	@Test
	public void createNewPlayershopTestWithInvalidSize() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 5,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			fail();
		} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §45§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void createNewAdminshopTestWithExistingName() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			ConfigController.setMaxPlayershops(2);
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			fail();
		} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§c§4myshop_catch441§c already exists!", e.getMessage());
		}
	}

	@Test
	public void createNewAdminshopTestWithInvalidName() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop_", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			fail();
		} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThis shopname is invalid! Use a name without _!", e.getMessage());
		}
	}

	@Test
	public void createNewAdminshopTestWithMaxShopsReached() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			ConfigController.setMaxPlayershops(1);
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			PlayershopController.createPlayerShop("myshop1", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			fail();
		} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cYou have already reached the maximum!", e.getMessage());
			try {
				ConfigController.setMaxPlayershops(2);
			} catch (GeneralEconomyException e1) {
				fail();
			}
		}
	}

	@Test
	public void createNewPlayershopTestWithNoPlotPermission() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			TownworldController.createTownWorld(world.getName());
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			fail();
		} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cYou dont have the permission to do that!", e.getMessage());
			try {
				TownworldController.deleteTownWorld(world.getName());
			} catch (TownSystemException | PlayerException | GeneralEconomyException e1) {
				fail();
			}
		}
	}

	@Test
	public void createNewPlayershopTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			Playershop response = PlayershopController.getPlayerShops().get(0);
			assertEquals(world, response.getWorld());
			assertEquals("P0", response.getShopId());
			assertEquals("myshop", response.getName());
			assertEquals(EconomyPlayerController.getAllEconomyPlayers().get(0), response.getOwner());
			assertEquals(EconomyVillager.PLAYERSHOP, response.getShopVillager().getMetadata("ue-type").get(0).value());
			// check shop inventory
			ChestInventoryMock shopInv = (ChestInventoryMock) response.getShopInventory();
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
			response.openEditor(player);
			ChestInventoryMock editor = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			player.closeInventory();
			assertEquals(9, editor.getSize());
			assertEquals("myshop-Editor", editor.getName());
			assertNull(editor.getItem(7));
			assertNull(editor.getItem(8));
			// check stock inventory
			ChestInventoryMock stock = (ChestInventoryMock) response.getStockpileInventory();
			assertEquals(9, stock.getSize());
			assertEquals("myshop-Stock", stock.getName());
			assertNull(stock.getItem(0));
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
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "P0.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals("catch441", config.getString("Owner"));
			assertEquals(1, UltimateEconomy.getInstance.getConfig().getStringList("PlayerShopIds").size());
			assertEquals("P0", UltimateEconomy.getInstance.getConfig().getStringList("PlayerShopIds").get(0));
		} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
			fail();
		}
	}

	@Test
	public void createNewPlayershopTestWithNoPlotPermissions() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {

			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));

		} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void createNewPlayershopTestWithNoPlotPermissions2() {
		Location location = new Location(world, 1.5, 29.3, 6.9);
		try {
			TownworldController.createTownWorld(world.getName());
			TownController.createTown(TownworldController.getTownWorldList().get(0), "mytown", location,
					EconomyPlayerController.getAllEconomyPlayers().get(1));
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			fail();
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cYou dont have the permission to do that!", e.getMessage());
			try {
				TownworldController.deleteTownWorld(world.getName());
			} catch (TownSystemException | PlayerException | GeneralEconomyException e1) {
				fail();
			}
		}
	}

	@Test
	public void generateFreePlayerShopIdTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			String id1 = PlayershopController.generateFreePlayerShopId();
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			String id2 = PlayershopController.generateFreePlayerShopId();
			assertEquals("P0", id1);
			assertEquals("P1", id2);
		} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
			fail();
		}
	}

	@Test
	public void getPlayerShopUniqueNameListTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			List<String> list = PlayershopController.getPlayerShopUniqueNameList();
			assertEquals(1, list.size());
			assertEquals("myshop_catch441", list.get(0));
		} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
			System.out.println(e.getMessage());
			fail();
		}
	}

	@Test
	public void getPlayerShopByUniqueNameTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop2", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShopByUniqueName("myshop_catch441");
			assertNotNull(shop);
			assertEquals("P1", shop.getShopId());
			assertEquals("myshop", shop.getName());
			assertEquals("catch441", shop.getOwner().getName());
		} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
			fail();
		}
	}

	@Test
	public void getPlayerShopByUniqueNameTestWithNoShop() {
		try {
			PlayershopController.getPlayerShopByUniqueName("myshop_catch441");
			fail();
		} catch (GeneralEconomyException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§c§4myshop_catch441§c does not exist!", e.getMessage());
		}
	}

	@Test
	public void getPlayerShopByIdTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop2", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShopById("P1");
			assertNotNull(shop);
			assertEquals("P1", shop.getShopId());
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
			fail();
		}
	}

	@Test
	public void getPlayerShopByIdTestWithNoShop() {
		try {
			PlayershopController.getPlayerShopById("P0");
			fail();
		} catch (GeneralEconomyException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§c§4P0§c does not exist!", e.getMessage());
		}
	}

	@Test
	public void getPlayershopIdListTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			List<String> list = PlayershopController.getPlayershopIdList();
			assertEquals(1, list.size());
			assertEquals("P0", list.get(0));
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
			fail();
		}
	}

	@Test
	public void despawnAllVillagersTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			Collection<Entity> entities1 = world
					.getNearbyEntities(PlayershopController.getPlayerShops().get(0).getShopLocation(), 0, 0, 0);
			PlayershopController.despawnAllVillagers();
			Collection<Entity> entities2 = world
					.getNearbyEntities(PlayershopController.getPlayerShops().get(0).getShopLocation(), 0, 0, 0);
			assertEquals(1, entities1.size());
			assertEquals(0, entities2.size());
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
			fail();
		}
	}

	@Test
	public void deletePlayershopTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			PlayershopController.deletePlayerShop(PlayershopController.getPlayerShops().get(0));
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "P0.yml");
			assertFalse(saveFile.exists());
			assertEquals(0, UltimateEconomy.getInstance.getConfig().getStringList("PlayerShopIds").size());
			assertEquals(0, PlayershopController.getPlayerShops().size());
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
			fail();
		}
	}

	@Test
	public void loadAllPlayerShopsTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			PlayershopController.getPlayerShops().get(0).despawnVillager();
			assertEquals(1, PlayershopController.getPlayerShops().size());
			PlayershopController.getPlayerShops().clear();
			assertEquals(0, PlayershopController.getPlayerShops().size());
			PlayershopController.loadAllPlayerShops();
			assertEquals(1, PlayershopController.getPlayerShops().size());
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			assertEquals(world, shop.getWorld());
			assertEquals("P0", shop.getShopId());
			assertEquals("myshop", shop.getName());
			assertEquals(EconomyVillager.PLAYERSHOP, shop.getShopVillager().getMetadata("ue-type").get(0).value());
			assertEquals(0, shop.getItemList().size());
			assertEquals(location, shop.getShopLocation());
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
			fail();
		}
	}

	@Test
	public void loadAllPlayerShopsTestWithItem() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 10, 10, new ItemStack(Material.STONE));
			shop.increaseStock(0, 25);
			shop.despawnVillager();
			PlayershopController.getPlayerShops().clear();
			assertEquals(0, PlayershopController.getPlayerShops().size());
			PlayershopController.loadAllPlayerShops();
			assertEquals(1, PlayershopController.getPlayerShops().size());
			Playershop response = PlayershopController.getPlayerShops().get(0);
			assertEquals(world, response.getWorld());
			assertEquals("P0", response.getShopId());
			assertEquals("myshop", response.getName());
			assertEquals(EconomyVillager.PLAYERSHOP, response.getShopVillager().getMetadata("ue-type").get(0).value());
			assertEquals(1, response.getItemList().size());
			assertEquals(location, response.getShopLocation());
			assertEquals(ChatColor.GREEN + "25" + ChatColor.GOLD + " Items",
					response.getStockpileInventory().getItem(0).getItemMeta().getLore().get(0));
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
			fail();
		}
	}

	@Test
	public void loadAllPlayerShopsTestWithNoSavefile() {
		Location location = new Location(world, 10.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "P0.yml");
			file.delete();
			PlayershopController.getPlayerShops().clear();
			PlayershopController.loadAllPlayerShops();
			assertEquals(0, PlayershopController.getPlayerShops().size());
			UltimateEconomy.getInstance.getConfig().set("PlayerShopIds", new ArrayList<>());
			UltimateEconomy.getInstance.saveConfig();
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
			fail();
		}
	}

	@Test
	public void loadAllPlayershopsTestWithOldLoading() {
		Location location = new Location(world, 1.5, 29.3, 6.9);
		try {
			List<String> list = new ArrayList<>();
			list.add("myshop_catch441");
			UltimateEconomy.getInstance.getConfig().set("PlayerShopNames", list);
			UltimateEconomy.getInstance.saveConfig();
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "P0.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			File oldFile = new File(UltimateEconomy.getInstance.getDataFolder(), "myshop_catch441.yml");
			oldFile.createNewFile();
			config.save(oldFile);
			file.delete();
			PlayershopController.getPlayerShops().clear();
			PlayershopController.loadAllPlayerShops();
			assertEquals(1, PlayershopController.getPlayerShops().size());
			assertFalse(UltimateEconomy.getInstance.getConfig().isSet("PlayerShopNames"));
			assertEquals(1, UltimateEconomy.getInstance.getConfig().getStringList("PlayerShopIds").size());
			assertEquals("P0", UltimateEconomy.getInstance.getConfig().getStringList("PlayerShopIds").get(0));
			assertTrue(file.exists());
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException
				| IOException e) {
			fail();
		}
	}

	@Test
	public void loadAllPlayershopsTestWithOldLoadingAndNoSavefile() {
		Location location = new Location(world, 1.5, 29.3, 6.9);
		try {
			List<String> list = new ArrayList<>();
			list.add("myshop_catch441");
			UltimateEconomy.getInstance.getConfig().set("PlayerShopNames", list);
			UltimateEconomy.getInstance.saveConfig();
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			PlayershopController.getPlayerShops().clear();
			PlayershopController.loadAllPlayerShops();
			assertEquals(0, PlayershopController.getPlayerShops().size());
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
			fail();
		}
	}

	@Test
	public void loadAllPlayershopsTestWithOldLoadingAndLoadError() {
		Location location = new Location(world, 1.5, 29.3, 6.9);
		try {
			List<String> list = new ArrayList<>();
			list.add("myshop_catch441");
			UltimateEconomy.getInstance.getConfig().set("PlayerShopNames", list);
			UltimateEconomy.getInstance.saveConfig();
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "P0.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			File oldFile = new File(UltimateEconomy.getInstance.getDataFolder(), "myshop_catch441.yml");
			oldFile.createNewFile();
			config.save(oldFile);
			file.delete();
			PlayershopController.getPlayerShops().clear();
			EconomyPlayerController.getAllEconomyPlayers().clear();
			PlayershopController.loadAllPlayerShops();
			assertEquals(0, PlayershopController.getPlayerShops().size());
			EconomyPlayerController.loadAllEconomyPlayers();
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException
				| IOException e) {
			fail();
		}
	}
	
	@Test
	public void loadAllPlayershopsTestWithLoadError() {
		Location location = new Location(world, 1.5, 29.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerController.getAllEconomyPlayers().get(0));
			PlayershopController.getPlayerShops().clear();
			EconomyPlayerController.getAllEconomyPlayers().clear();
			PlayershopController.loadAllPlayerShops();
			assertEquals(0, PlayershopController.getPlayerShops().size());
			EconomyPlayerController.loadAllEconomyPlayers();
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
			fail();
		}
	}
}
