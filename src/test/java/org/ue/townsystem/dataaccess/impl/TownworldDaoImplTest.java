package org.ue.townsystem.dataaccess.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankManager;
import org.ue.common.utils.ServerProvider;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.general.GeneralEconomyException;
import org.ue.townsystem.logic.api.TownsystemValidationHandler;

@ExtendWith(MockitoExtension.class)
public class TownworldDaoImplTest {

	@InjectMocks
	TownworldDaoImpl dao;
	@Mock
	TownsystemValidationHandler validationHandler;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	BankManager bankManager;
	@Mock
	ServerProvider serverProvider;

	/**
	 * Delete savefile.
	 */
	@AfterEach
	public void cleanUp() {
		File file = new File("src/world_TownWorld.yml");
		file.delete();
	}

	@Test
	public void setupSavefileTest() {
		File result = new File("src/world_TownWorld.yml");
		assertFalse(result.exists());
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		assertTrue(result.exists());
	}

	@Test
	public void setupSavefileLoadTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.saveWorldName("myworld");
		dao.setupSavefile("world");
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertTrue(file.exists());
		assertTrue(config.isSet("World"));
	}

	@Test
	public void deleteSavefileTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		File file = new File("src/world_TownWorld.yml");
		assertTrue(file.exists());
		dao.deleteSavefile();
		assertFalse(file.exists());
	}

	@Test
	public void savePlotIsForSaleTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.savePlotIsForSale("town", "1/2", true);
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("true", config.getString("Towns.town.Plots.1/2.isForSale"));
	}

	@Test
	public void savePlotSalePriceTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.savePlotSalePrice("town", "1/2", 1.5);
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(1.5, config.getDouble("Towns.town.Plots.1/2.salePrice"));
	}

	@Test
	public void saveTownSpawnTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		Location loc = mock(Location.class);
		when(loc.getX()).thenReturn(1.1);
		when(loc.getY()).thenReturn(2.2);
		when(loc.getZ()).thenReturn(3.3);
		dao.saveTownSpawn("town", loc);
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("1.1/2.2/3.3", config.getString("Towns.town.townspawn"));
	}

	@Test
	public void saveMayorTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.saveMayor("town", ecoPlayer);
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("catch441", config.getString("Towns.town.owner"));
	}

	@Test
	public void saveTaxTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.saveTax("town", 1.5);
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(1.5, config.getDouble("Towns.town.tax"));
	}

	@Test
	public void saveTownBankIbanTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.saveTownBankIban("town", "myiban");
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("myiban", config.getString("Towns.town.Iban"));
	}

	@Test
	public void saveFoundationPriceTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.saveFoundationPrice(1.5);
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(1.5, config.getDouble("Config.foundationPrice"));
	}

	@Test
	public void saveExpandPriceTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.saveExpandPrice(1.5);
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(1.5, config.getDouble("Config.expandPrice"));
	}

	@Test
	public void savePlotVillagerLocationTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(loc.getX()).thenReturn(1.1);
		when(loc.getY()).thenReturn(2.2);
		when(loc.getZ()).thenReturn(3.3);
		dao.savePlotVillagerLocation("town", "1/2", loc);
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(1.1, config.getDouble("Towns.town.Plots.1/2.SaleVillager.x"));
		assertEquals(2.2, config.getDouble("Towns.town.Plots.1/2.SaleVillager.y"));
		assertEquals(3.3, config.getDouble("Towns.town.Plots.1/2.SaleVillager.z"));
		assertEquals("world", config.getString("Towns.town.Plots.1/2.SaleVillager.world"));
	}

	@Test
	public void savePlotOwnerTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.savePlotOwner("town", "1/2", ecoPlayer);
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("catch441", config.getString("Towns.town.Plots.1/2.owner"));
	}

	@Test
	public void saveCitizensTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.saveCitizens("town", Arrays.asList(ecoPlayer));
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(Arrays.asList("catch441"), config.getStringList("Towns.town.citizens"));
	}

	@Test
	public void saveDeputiesTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.saveDeputies("town", Arrays.asList(ecoPlayer));
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(Arrays.asList("catch441"), config.getStringList("Towns.town.coOwners"));
	}

	@Test
	public void savePlotResidentsTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.savePlotResidents("town", "1/2", Arrays.asList(ecoPlayer));
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(Arrays.asList("catch441"), config.getStringList("Towns.town.Plots.1/2.coOwners"));
	}

	@Test
	public void saveTownManagerLocationTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(loc.getX()).thenReturn(1.1);
		when(loc.getY()).thenReturn(2.2);
		when(loc.getZ()).thenReturn(3.3);
		dao.saveTownManagerLocation("town", loc);
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(1.1, config.getDouble("Towns.town.TownManagerVillager.x"));
		assertEquals(2.2, config.getDouble("Towns.town.TownManagerVillager.y"));
		assertEquals(3.3, config.getDouble("Towns.town.TownManagerVillager.z"));
		assertEquals("world", config.getString("Towns.town.TownManagerVillager.world"));
	}

	@Test
	public void loadPlotIsForSaleTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.savePlotIsForSale("town", "1/2", true);
		assertTrue(dao.loadPlotIsForSale("town", "1/2"));
	}

	@Test
	public void loadPlotSalePriceTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.savePlotSalePrice("town", "1/2", 1.5);
		assertEquals("1.5", String.valueOf(dao.loadPlotSalePrice("town", "1/2")));
	}

	@Test
	public void loadTaxTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.saveTax("town", 1.5);
		assertEquals("1.5", String.valueOf(dao.loadTax("town")));
	}

	@Test
	public void loadMayorTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.saveMayor("town", ecoPlayer);
		assertDoesNotThrow(() -> assertEquals(ecoPlayer, dao.loadMayor("town")));
	}

	@Test
	public void loadPlotOwnerTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.savePlotOwner("town", "1/2", ecoPlayer);
		assertDoesNotThrow(() -> assertEquals(ecoPlayer, dao.loadPlotOwner("town", "1/2")));
	}

	@Test
	public void loadDeputiesTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.saveDeputies("town", Arrays.asList(ecoPlayer));
		assertDoesNotThrow(() -> assertEquals(Arrays.asList(ecoPlayer), dao.loadDeputies("town")));
	}
	
	@Test
	public void loadDeputiesTestWithLoadingError() throws GeneralEconomyException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		when(e.getMessage()).thenReturn("my error message");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(e);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.saveDeputies("town", Arrays.asList(ecoPlayer));
		assertEquals(0, dao.loadDeputies("town").size());
		verify(e).getMessage();
	}
	
	@Test
	public void loadCitizensTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.saveCitizens("town", Arrays.asList(ecoPlayer));
		assertDoesNotThrow(() -> assertEquals(Arrays.asList(ecoPlayer), dao.loadCitizens("town")));
	}
	
	@Test
	public void loadCitizensTestWithLoadingError() throws GeneralEconomyException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		when(e.getMessage()).thenReturn("my error message");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(e);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.saveCitizens("town", Arrays.asList(ecoPlayer));
		assertEquals(0, dao.loadCitizens("town").size());
		verify(e).getMessage();
	}
	
	@Test
	public void loadResidentsTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.savePlotResidents("town", "1/2", Arrays.asList(ecoPlayer));
		assertDoesNotThrow(() -> assertEquals(Arrays.asList(ecoPlayer), dao.loadResidents("town", "1/2")));
	}
	
	@Test
	public void loadResidentsTestWithLoadingError() throws GeneralEconomyException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		when(e.getMessage()).thenReturn("my error message");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(e);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.savePlotResidents("town", "1/2", Arrays.asList(ecoPlayer));
		assertEquals(0, dao.loadResidents("town", "1/2").size());
		verify(e).getMessage();
	}

	@Test
	public void loadFoundationPriceTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.saveFoundationPrice(1.5);
		assertEquals("1.5", String.valueOf(dao.loadFoundationPrice()));
	}

	@Test
	public void loadExpandPriceTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.saveExpandPrice(1.5);
		assertEquals("1.5", String.valueOf(dao.loadExpandPrice()));
	}

	@Test
	public void loadTownSpawnTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		World world = mock(World.class);
		Location loc = mock(Location.class);
		when(loc.getX()).thenReturn(1.1);
		when(loc.getY()).thenReturn(2.2);
		when(loc.getZ()).thenReturn(3.3);
		dao.saveTownSpawn("town", loc);
		dao.saveWorldName("world");
		when(serverProvider.getWorld("world")).thenReturn(world);
		Location loaded = assertDoesNotThrow(() -> dao.loadTownSpawn("town"));
		assertEquals("1.1", String.valueOf(loaded.getX()));
		assertEquals("2.2", String.valueOf(loaded.getY()));
		assertEquals("3.3", String.valueOf(loaded.getZ()));
		assertEquals(world, loaded.getWorld());
		assertDoesNotThrow(() -> verify(validationHandler).checkForWorldExists("world"));
	}

	@Test
	public void loadTownManagerLocationTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(loc.getX()).thenReturn(1.1);
		when(loc.getY()).thenReturn(2.2);
		when(loc.getZ()).thenReturn(3.3);
		dao.saveWorldName("world");
		dao.saveTownManagerLocation("town", loc);
		when(serverProvider.getWorld("world")).thenReturn(world);
		Location loaded = assertDoesNotThrow(() -> dao.loadTownManagerLocation("town"));
		assertEquals("1.1", String.valueOf(loaded.getX()));
		assertEquals("2.2", String.valueOf(loaded.getY()));
		assertEquals("3.3", String.valueOf(loaded.getZ()));
		assertEquals(world, loaded.getWorld());
		assertDoesNotThrow(() -> verify(validationHandler).checkForWorldExists("world"));
	}

	@Test
	public void loadPlotVillagerLocationTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(loc.getX()).thenReturn(1.1);
		when(loc.getY()).thenReturn(2.2);
		when(loc.getZ()).thenReturn(3.3);
		dao.saveWorldName("world");
		dao.savePlotVillagerLocation("town", "1/2", loc);
		when(serverProvider.getWorld("world")).thenReturn(world);
		Location loaded = assertDoesNotThrow(() -> dao.loadPlotVillagerLocation("town", "1/2"));
		assertEquals("1.1", String.valueOf(loaded.getX()));
		assertEquals("2.2", String.valueOf(loaded.getY()));
		assertEquals("3.3", String.valueOf(loaded.getZ()));
		assertEquals(world, loaded.getWorld());
		assertDoesNotThrow(() -> verify(validationHandler).checkForWorldExists("world"));
	}

	@Test
	public void saveRenameTownTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.savePlotResidents("town", "1/2", Arrays.asList(ecoPlayer));
		dao.saveRenameTown("town", "newtown");
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertFalse(config.isSet("Towns.town.Plots.1/2.coOwners"));
		assertEquals(Arrays.asList("catch441"), config.getStringList("Towns.newtown.Plots.1/2.coOwners"));
	}
	
	@Test
	public void saveRemovePlotTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		dao.savePlotOwner("town", "1/2", ecoPlayer);
		dao.saveRemovePlot("town", "1/2");
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertFalse(config.isSet("Towns.town.Plots.1/2"));
	}
	
	@Test
	public void loadTownPlotCoordsTestWithOldRemove() {
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Towns.town.chunks.1/2", "stuff");
		save(file, config);		
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.savePlotIsForSale("town", "1/2", true);
		assertEquals(Arrays.asList("1/2"), dao.loadTownPlotCoords("town"));
		File file2 = new File("src/world_TownWorld.yml");
		YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file2);
		assertFalse(config2.isSet("Towns.town.chunks"));
	}
	
	@Test
	public void loadTownworldTownNamesTestWithOldRemove() {
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("TownNames", Arrays.asList("town1", "town2"));
		save(file, config);		
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.savePlotIsForSale("town", "1/2", true);
		dao.loadTownworldTownNames();
		assertEquals(Arrays.asList("town"), dao.loadTownworldTownNames());
		File file2 = new File("src/world_TownWorld.yml");
		YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file2);
		assertFalse(config2.isSet("TownNames"));
	}
	
	@Test
	public void loadTownworldTownNamesTestWithNoTowns() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.loadTownworldTownNames();
		assertEquals(0, dao.loadTownworldTownNames().size());
	}
	
	@Test
	public void loadTownBankIbanTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		dao.saveTownBankIban("town", "myiban");
		assertEquals("myiban", dao.loadTownBankIban("town"));
	}
	
	@Test
	public void loadTownBankIbanTestWithOldConvert() {
		BankAccount account = mock(BankAccount.class);
		when(bankManager.createBankAccount(1.5)).thenReturn(account);
		when(account.getIban()).thenReturn("myiban");
		File file = new File("src/world_TownWorld.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Towns.town.bank", 1.5);
		save(file, config);
		
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile("world");
		assertEquals("myiban", dao.loadTownBankIban("town"));
		YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file);
		assertFalse(config2.isSet("Towns.town.bank"));
		verify(bankManager).createBankAccount(1.5);
	}
	
	private void save(File file, YamlConfiguration config) {
		assertDoesNotThrow(() -> config.save(file));
	}
}
