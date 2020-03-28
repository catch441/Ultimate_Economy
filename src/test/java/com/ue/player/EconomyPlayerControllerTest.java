package com.ue.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.bank.api.BankController;
import com.ue.exceptions.PlayerException;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class EconomyPlayerControllerTest {

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
	player = server.addPlayer("catch441");
	EconomyPlayerController.deleteEconomyPlayer(EconomyPlayerController.getAllEconomyPlayers().get(0));
    }

    /**
     * Unload mock bukkit.
     */
    @AfterAll
    public static void deleteSavefiles() {
	UltimateEconomy.getInstance.getDataFolder().delete();
	server.setPlayers(0);
	MockBukkit.unload();
    }

    /**
     * Unload all.
     */
    @AfterEach
    public void unload() {
	int size = EconomyPlayerController.getAllEconomyPlayers().size();
	for (int i = 0; i < size; i++) {
	    EconomyPlayerController.deleteEconomyPlayer(EconomyPlayerController.getAllEconomyPlayers().get(0));
	}
    }
    
    /**
     * Test create economy player.
     * 
     */
    @Test
    public void createEconomyPlayerTest() {
	try {
	    EconomyPlayerController.createEconomyPlayer("catch441");
	    EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
	    assertEquals(1,EconomyPlayerController.getAllEconomyPlayers().size());
	    assertEquals("catch441", ecoPlayer.getName());
	    assertEquals(player,ecoPlayer.getPlayer());
	    assertTrue(ecoPlayer.getJobList().isEmpty());
	    assertTrue(ecoPlayer.getHomeList().isEmpty());
	    assertTrue(ecoPlayer.getJoinedTownList().isEmpty());
	    assertEquals(BankController.getBankAccounts().get(0),ecoPlayer.getBankAccount()); 
	    // check savefile
	    File saveFile = EconomyPlayerController.getPlayerFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals(1, config.getStringList("Player").size());
	    assertEquals("catch441", config.getStringList("Player").get(0));
	    assertEquals(BankController.getBankAccounts().get(0).getIban(),config.getString("catch441.Iban"));
	    assertTrue(config.getBoolean("catch441.bank"));
	} catch (PlayerException e) {
	    assertTrue(false);
	}
    }
    
    // TODO do more tests
}
