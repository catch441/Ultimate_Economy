package com.ue.shopsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.impl.RentshopImpl;
import com.ue.shopsystem.logic.impl.RentshopManagerImpl;
import com.ue.shopsystem.logic.impl.ShopSystemException;
import com.ue.townsystem.logic.impl.TownSystemException;
import com.ue.ultimate_economy.EconomyVillager;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

public class RentshopTest {

	private static final String PLUS = "http://textures.minecraft.net/texture/"
			+ "9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777";
	private static final String ONE = "http://textures.minecraft.net/texture/"
			+ "d2a6f0e84daefc8b21aa99415b16ed5fdaa6d8dc0c3cd591f49ca832b575";
	private static final String SEVEN = "http://textures.minecraft.net/texture/"
			+ "9e198fd831cb61f3927f21cf8a7463af5ea3c7e43bd3e8ec7d2948631cce879";
	
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
			assertEquals(EconomyVillager.PLAYERSHOP_RENTABLE, shop.getShopVillager().getMetadata("ue-type").get(0).value());
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
			assertEquals(EconomyVillager.PLAYERSHOP_RENTABLE, shop.getShopVillager().getMetadata("ue-type").get(0).value());
		} catch (GeneralEconomyException | TownSystemException | EconomyPlayerException | ShopSystemException e) {
			fail();
		}
	}

	@Test
	public void moveShopTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			Location newLocation = new Location(world, 10, 3, 20);
			shop.moveShop(newLocation);
			assertEquals(newLocation, shop.getShopLocation());
		} catch (GeneralEconomyException | TownSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void isRentableTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			assertTrue(shop.isRentable());
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).increasePlayerAmount(100, false);
			shop.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 10);
			assertFalse(shop.isRentable());
		} catch (GeneralEconomyException | EconomyPlayerException | ShopSystemException e) {
			fail();
		}
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
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			assertEquals("10.0", String.valueOf(shop.getRentalFee()));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void openRentGUITest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.openRentGUI(player);
			assertNotNull(player.getOpenInventory());
			assertEquals("RentShop#R0", ((ChestInventoryMock) player.getOpenInventory().getTopInventory()).getName());
		} catch (GeneralEconomyException | ShopSystemException e) {
			fail();
		}
	}

	@Test
	public void openRentGUITestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.openRentGUI(player);
			fail();
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThis shop is rented!", e.getMessage());
		}
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
	public void addShopItemTestWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.addShopItem(0, 0, 0, null);
			fail();
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe shop is not rented!", e.getMessage());
		}
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
	public void editShopItemTestWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.editShopItem(0, "none", "none", "1");
			fail();
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe shop is not rented!", e.getMessage());
		}
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
	public void getShopItemTestWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.getShopItem(new ItemStack(Material.STONE));
			fail();
		} catch (GeneralEconomyException | ShopSystemException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe shop is not rented!", e.getMessage());
		}
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
	public void isAvailableTestWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.isAvailable(0);
			fail();
		} catch (GeneralEconomyException | ShopSystemException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe shop is not rented!", e.getMessage());
		}
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
	public void openShopInventoryTestWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.openShopInventory(player);
			fail();
		} catch (GeneralEconomyException | ShopSystemException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe shop is not rented!", e.getMessage());
		}
	}
	
	@Test
	public void openShopInventoryTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.openShopInventory(player);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void decreaseStockTestWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.decreaseStock(0,0);
			fail();
		} catch (GeneralEconomyException | ShopSystemException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe shop is not rented!", e.getMessage());
		}
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
			shop.decreaseStock(0,5);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void increaseStockTestWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.increaseStock(0,10);
			fail();
		} catch (GeneralEconomyException | ShopSystemException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe shop is not rented!", e.getMessage());
		}
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
	public void removeShopItemTestWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.removeShopItem(0);
			fail();
		} catch (GeneralEconomyException | ShopSystemException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe shop is not rented!", e.getMessage());
		}
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
	public void openStockpileTestWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.openStockpile(player);
			fail();
		} catch (GeneralEconomyException | ShopSystemException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe shop is not rented!", e.getMessage());
		}
	}
	
	@Test
	public void openStockpileTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.openStockpile(player);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void openSlotEditorTestWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.openSlotEditor(player,0);
			fail();
		} catch (GeneralEconomyException | ShopSystemException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe shop is not rented!", e.getMessage());
		}
	}
	
	@Test
	public void openSlotEditorTestWithRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(100, false);
			shop.rentShop(ecoPlayer, 10);
			shop.openSlotEditor(player,0);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void openEditorTestWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.openEditor(player);
			fail();
		} catch (GeneralEconomyException | ShopSystemException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe shop is not rented!", e.getMessage());
		}
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
	public void buyShopItemWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.buyShopItem(0,EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0),false);
			fail();
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe shop is not rented!", e.getMessage());
		}
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
			shop.buyShopItem(0,EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0),false);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void sellShopItemWithNotRented() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			Rentshop shop = RentshopManagerImpl.createRentShop(location, 9, 10);
			shop.sellShopItem(0,1,EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0),false);
			fail();
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe shop is not rented!", e.getMessage());
		}
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
			shop.sellShopItem(0,1,EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0),false);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}
}
