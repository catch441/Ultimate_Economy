package org.ue.common.dataaccess.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Villager.Profession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.utils.ServerProvider;
import org.ue.economyvillager.dataaccess.impl.EconomyVillagerDaoImpl;

@ExtendWith(MockitoExtension.class)
public class EconomyVillagerDaoImplTest {

	@Mock
	ServerProvider serverProvider;

	private class AbstractDao extends EconomyVillagerDaoImpl {

		public AbstractDao(ServerProvider serverProvider) {
			super(serverProvider);
		}

		public void setup() {
			file = new File("src", "villager.yml");
			if (!file.exists()) {
				createFile(file);
			}
			config = YamlConfiguration.loadConfiguration(file);
		}

	}

	@AfterEach
	public void unload() {
		new File("src/villager.yml").delete();
	}

	@Test
	public void saveLocationTest() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();
		File file = new File("src/villager.yml");
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(loc.getX()).thenReturn(1.5);
		when(loc.getY()).thenReturn(2.5);
		when(loc.getZ()).thenReturn(3.5);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");

		dao.saveLocation("prefix", loc);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("1.5", config.getString("prefix.Location.x"));
		assertEquals("2.5", config.getString("prefix.Location.y"));
		assertEquals("3.5", config.getString("prefix.Location.z"));
		assertEquals("world", config.getString("prefix.Location.world"));
	}

	@Test
	public void saveProfessionTest() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();
		File file = new File("src/villager.yml");

		dao.saveProfession("prefix", Profession.ARMORER);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("ARMORER", config.getString("prefix.Profession"));
	}

	@Test
	public void saveSizeTest() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();
		File file = new File("src/villager.yml");

		dao.saveSize("prefix", 9);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(9, config.getInt("prefix.Size"));
	}

	@Test
	public void saveVisibleTest() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();
		File file = new File("src/villager.yml");

		dao.saveVisible("prefix", true);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(true, config.getBoolean("prefix.visible"));
	}

	@Test
	public void loadProfessionTest() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();
		dao.saveProfession("prefix", Profession.ARMORER);
		Profession result = dao.loadProfession("prefix");
		assertEquals(Profession.ARMORER, result);
	}

	@Test
	public void loadProfessionTestWithNotSet() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();
		Profession result = dao.loadProfession("prefix");
		assertEquals(Profession.NITWIT, result);
	}

	@Test
	public void loadVisibleTest() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();
		dao.saveVisible("prefix", false);
		boolean result = dao.loadVisible("prefix");
		assertFalse(result);
	}

	@Test
	public void loadVisibleTestWithNotSet() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();
		boolean result = dao.loadVisible("prefix");
		assertTrue(result);
		File file = new File("src/villager.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertTrue(config.getBoolean("prefix.visible"));
	}

	@Test
	public void loadVisibleTestWithNotSetPlot() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();
		File file = new File("src/villager.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("prefix.Plots.isForSale", true);
		save(file, config);
		dao.setup();
		boolean result = dao.loadVisible("prefix.Plots.SaleVillager");
		assertTrue(result);
		YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file);
		assertTrue(config2.getBoolean("prefix.Plots.SaleVillager.visible"));
	}

	@Test
	public void loadSizeTest() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();
		dao.saveSize("", 18);
		int result = dao.loadSize(null);
		assertEquals(18, result);
	}

	@Test
	public void loadSizeTestWithNotSet() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();
		int result = dao.loadSize("prefix");
		assertEquals(9, result);
		File file = new File("src/villager.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(9, config.getInt("prefix.Size"));
	}
	
	@Test
	public void loadSizeTestWithJobcenterConvert() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();
		File file = new File("src/villager.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("JobCenterSize", 18);
		save(file, config);
		dao.setup();
		
		int result = dao.loadSize("prefix");
		assertEquals(18, result);
		YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file);
		assertEquals(18, config2.getInt("prefix.Size"));
		assertFalse(config2.isSet("JobCenterSize"));
	}
	
	@Test
	public void loadSizeTestWithShopConvert() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();
		File file = new File("src/villager.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopSize", 18);
		save(file, config);
		dao.setup();
		
		int result = dao.loadSize("prefix");
		assertEquals(18, result);
		YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file);
		assertEquals(18, config2.getInt("prefix.Size"));
		assertFalse(config2.isSet("ShopSize"));
	}
	
	@Test
	public void loadLocationTest() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(loc.getX()).thenReturn(1.5);
		when(loc.getY()).thenReturn(2.5);
		when(loc.getZ()).thenReturn(3.5);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(serverProvider.getWorld("world")).thenReturn(world);
		dao.saveLocation("prefix", loc);

		Location result = dao.loadLocation("prefix");
		assertEquals(1.5, result.getX());
		assertEquals(2.5, result.getY());
		assertEquals(3.5, result.getZ());
		assertEquals(world, result.getWorld());
	}
	
	@Test
	public void loadLocationTestWithShopConvert() {
		AbstractDao dao = new AbstractDao(serverProvider);
		World world = mock(World.class);
		when(serverProvider.getWorld("world")).thenReturn(world);
		when(world.getName()).thenReturn("world");
		
		dao.setup();
		File file = new File("src/villager.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopLocation.x", 1.5);
		config.set("ShopLocation.y", 2.5);
		config.set("ShopLocation.z", 3.5);
		config.set("ShopLocation.World", "world");
		save(file, config);
		dao.setup();

		Location result = dao.loadLocation("prefix");
		assertEquals(1.5, result.getX());
		assertEquals(2.5, result.getY());
		assertEquals(3.5, result.getZ());
		assertEquals(world, result.getWorld());
		YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file);
		assertFalse(config2.isSet("ShopLocation"));
		assertEquals("1.5", config2.getString("prefix.Location.x"));
		assertEquals("2.5", config2.getString("prefix.Location.y"));
		assertEquals("3.5", config2.getString("prefix.Location.z"));
		assertEquals("world", config2.getString("prefix.Location.world"));
	}
	
	@Test
	public void loadLocationTestWithJobcenterConvert() {
		AbstractDao dao = new AbstractDao(serverProvider);
		World world = mock(World.class);
		when(serverProvider.getWorld("world")).thenReturn(world);
		when(world.getName()).thenReturn("world");
		
		dao.setup();
		File file = new File("src/villager.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("JobcenterLocation.x", 1.5);
		config.set("JobcenterLocation.y", 2.5);
		config.set("JobcenterLocation.z", 3.5);
		config.set("JobcenterLocation.World", "world");
		save(file, config);
		dao.setup();

		Location result = dao.loadLocation("prefix");
		assertEquals(1.5, result.getX());
		assertEquals(2.5, result.getY());
		assertEquals(3.5, result.getZ());
		assertEquals(world, result.getWorld());
		YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file);
		assertFalse(config2.isSet("JobcenterLocation"));
		assertEquals("1.5", config2.getString("prefix.Location.x"));
		assertEquals("2.5", config2.getString("prefix.Location.y"));
		assertEquals("3.5", config2.getString("prefix.Location.z"));
		assertEquals("world", config2.getString("prefix.Location.world"));
	}
	
	@Test
	public void loadLocationTestWithPlotConvert() {
		AbstractDao dao = new AbstractDao(serverProvider);
		World world = mock(World.class);
		when(serverProvider.getWorld("world")).thenReturn(world);
		when(world.getName()).thenReturn("world");
		
		dao.setup();
		File file = new File("src/villager.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("prefix.x", 1.5);
		config.set("prefix.y", 2.5);
		config.set("prefix.z", 3.5);
		config.set("prefix.world", "world");
		save(file, config);
		dao.setup();

		Location result = dao.loadLocation("prefix");
		assertEquals(1.5, result.getX());
		assertEquals(2.5, result.getY());
		assertEquals(3.5, result.getZ());
		assertEquals(world, result.getWorld());
		YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file);
		assertFalse(config2.isSet("prefix.x"));
		assertFalse(config2.isSet("prefix.y"));
		assertFalse(config2.isSet("prefix.z"));
		assertFalse(config2.isSet("prefix.world"));
		assertEquals("1.5", config2.getString("prefix.Location.x"));
		assertEquals("2.5", config2.getString("prefix.Location.y"));
		assertEquals("3.5", config2.getString("prefix.Location.z"));
		assertEquals("world", config2.getString("prefix.Location.world"));
	}
	
	@Test
	public void loadLocationTestWithNotSet() {
		AbstractDao dao = new AbstractDao(serverProvider);
		dao.setup();

		Location result = dao.loadLocation("prefix");
		assertNull(result);
	}

	private void save(File file, YamlConfiguration config) {
		assertDoesNotThrow(() -> config.save(file));
	}
}
