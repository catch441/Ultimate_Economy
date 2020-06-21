package com.ue.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

public class ConfigControllerTest {

    private static ServerMock server;

    /**
     * Init for tests.
     */
    @BeforeAll
    public static void initPlugin() {
	server = MockBukkit.mock();
	MockBukkit.load(UltimateEconomy.class);
	UltimateEconomy.getInstance.getDataFolder().delete();
	server.addPlayer("catch441");
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
	server.setPlayers(0);
	UltimateEconomy.getInstance.getDataFolder().delete();
	MockBukkit.unload();
    }

    /**
     * Unload all.
     */
    @AfterEach
    public void unload() {
	UltimateEconomy.getInstance.getDataFolder().delete();
	MockBukkit.unload();
	server = MockBukkit.mock();
	MockBukkit.load(UltimateEconomy.class);
    }

    @Test
    public void setupConfigInitTest() {
	ConfigController.setupConfig();
	assertEquals(2, ConfigController.getMaxJobs());
	assertEquals(1, ConfigController.getMaxJoinedTowns());
	assertEquals(3, ConfigController.getMaxPlayershops());
	assertTrue(ConfigController.isHomeSystem());
	assertFalse(ConfigController.isWildernessInteraction());
	assertFalse(ConfigController.isExtendedInteraction());
	assertEquals("$", ConfigController.getCurrencyPl());
	assertEquals("$", ConfigController.getCurrencySg());
	assertEquals(14, ConfigController.getMaxRentedDays());
	assertEquals(3, ConfigController.getMaxHomes());
    }

    @Test
    public void setupConfigAfterReloadWithoutChangesTest() {
	ConfigController.setupConfig();

	MockBukkit.unload();
	server = MockBukkit.mock();
	MockBukkit.load(UltimateEconomy.class);

	assertEquals(3, ConfigController.getMaxHomes());
	assertEquals(2, ConfigController.getMaxJobs());
	assertEquals(1, ConfigController.getMaxJoinedTowns());
	assertEquals(3, ConfigController.getMaxPlayershops());
	assertTrue(ConfigController.isHomeSystem());
	assertFalse(ConfigController.isWildernessInteraction());
	assertFalse(ConfigController.isExtendedInteraction());
	assertEquals("$", ConfigController.getCurrencyPl());
	assertEquals("$", ConfigController.getCurrencySg());
	assertEquals(14, ConfigController.getMaxRentedDays());
    }

    @Test
    public void setExtendedInteractionTest() {
	assertFalse(ConfigController.isExtendedInteraction());
	ConfigController.setExtendedInteraction(true);
	assertTrue(ConfigController.isExtendedInteraction());
	File file = new File(UltimateEconomy.getInstance.getDataFolder(), "config.yml");
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	assertTrue(config.getBoolean("ExtendedInteraction"));
    }

    @Test
    public void setWildernessInteractionTest() {
	assertFalse(ConfigController.isWildernessInteraction());
	ConfigController.setWildernessInteraction(true);
	EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
	assertTrue(ecoPlayer.getPlayer().hasPermission("ultimate_economy.wilderness"));
	File file = new File(UltimateEconomy.getInstance.getDataFolder(), "config.yml");
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	assertTrue(config.getBoolean("WildernessInteraction"));
    }

    @Test
    public void setMaxRentedDaysTest() {
	assertEquals(14, ConfigController.getMaxRentedDays());
	try {
	    ConfigController.setMaxRentedDays(7);
	    assertEquals(7, ConfigController.getMaxRentedDays());
	    File file = new File(UltimateEconomy.getInstance.getDataFolder(), "config.yml");
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	    assertEquals(7, config.getInt("MaxRentedDays"));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void setMaxRentedDaysExceptionTest() {
	try {
	    ConfigController.setMaxRentedDays(-10);
	    assertTrue(false);
	} catch (GeneralEconomyException e) {
	    assertEquals("§cThe parameter §4-10§c is invalid!", e.getMessage());
	}
    }

    @Test
    public void setMaxPlayershopsTest() {
	assertEquals(3, ConfigController.getMaxPlayershops());
	try {
	    ConfigController.setMaxPlayershops(1);
	    ;
	    assertEquals(1, ConfigController.getMaxPlayershops());
	    File file = new File(UltimateEconomy.getInstance.getDataFolder(), "config.yml");
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	    assertEquals(1, config.getInt("MaxPlayershops"));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void setMaxPlayershopsExceptionTest() {
	try {
	    ConfigController.setMaxPlayershops(-10);
	    assertTrue(false);
	} catch (GeneralEconomyException e) {
	    assertEquals("§cThe parameter §4-10§c is invalid!", e.getMessage());
	}
    }

    @Test
    public void setMaxHomesTest() {
	assertEquals(3, ConfigController.getMaxHomes());
	try {
	    ConfigController.setMaxHomes(1);
	    ;
	    assertEquals(1, ConfigController.getMaxHomes());
	    File file = new File(UltimateEconomy.getInstance.getDataFolder(), "config.yml");
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	    assertEquals(1, config.getInt("MaxHomes"));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void setMaxHomesExceptionTest() {
	try {
	    ConfigController.setMaxHomes(-10);
	    assertTrue(false);
	} catch (GeneralEconomyException e) {
	    assertEquals("§cThe parameter §4-10§c is invalid!", e.getMessage());
	}
    }

    @Test
    public void setMaxJobsTest() {
	assertEquals(2, ConfigController.getMaxJobs());
	try {
	    ConfigController.setMaxJobs(1);
	    assertEquals(1, ConfigController.getMaxJobs());
	    File file = new File(UltimateEconomy.getInstance.getDataFolder(), "config.yml");
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	    assertEquals(1, config.getInt("MaxJobs"));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void setMaxJobsExceptionTest() {
	try {
	    ConfigController.setMaxJobs(-10);
	    assertTrue(false);
	} catch (GeneralEconomyException e) {
	    assertEquals("§cThe parameter §4-10§c is invalid!", e.getMessage());
	}
    }

    @Test
    public void setMaxJoinedTownesTest() {
	assertEquals(1, ConfigController.getMaxJoinedTowns());
	try {
	    ConfigController.setMaxJoinedTowns(3);
	    assertEquals(3, ConfigController.getMaxJoinedTowns());
	    File file = new File(UltimateEconomy.getInstance.getDataFolder(), "config.yml");
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	    assertEquals(3, config.getInt("MaxJoinedTowns"));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void setMaxJoinedTownesExceptionTest() {
	try {
	    ConfigController.setMaxJoinedTowns(-10);
	    assertTrue(false);
	} catch (GeneralEconomyException e) {
	    assertEquals("§cThe parameter §4-10§c is invalid!", e.getMessage());
	}
    }

    @Test
    public void setHomeSystemTest() {
	assertTrue(ConfigController.isHomeSystem());
	ConfigController.setHomeSystem(false);
	assertFalse(ConfigController.isHomeSystem());
	File file = new File(UltimateEconomy.getInstance.getDataFolder(), "config.yml");
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	assertFalse(config.getBoolean("homes"));
    }
    
    @Test
    public void setCurrencyPlTest() {
	assertEquals("$",ConfigController.getCurrencyPl());
	ConfigController.setCurrencyPl("Coins");
	assertEquals("Coins",ConfigController.getCurrencyPl());
	File file = new File(UltimateEconomy.getInstance.getDataFolder(), "config.yml");
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	assertEquals("Coins", config.getString("currencyPl"));
    }
    
    @Test
    public void setCurrencySgTest() {
	assertEquals("$",ConfigController.getCurrencySg());
	ConfigController.setCurrencySg("Coin");
	assertEquals("Coin",ConfigController.getCurrencySg());
	File file = new File(UltimateEconomy.getInstance.getDataFolder(), "config.yml");
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	assertEquals("Coin", config.getString("currencySg"));
    }
    
    @Test
    public void getCurrencyTestPl() {
	ConfigController.setCurrencyPl("Coins");
	String text = ConfigController.getCurrencyText(10.0);
	assertEquals("Coins",text);
    }
    
    @Test
    public void getCurrencyTestSg() {
	ConfigController.setCurrencySg("Coin");
	String text = ConfigController.getCurrencyText(1.0);
	assertEquals("Coin",text);
    }
}
