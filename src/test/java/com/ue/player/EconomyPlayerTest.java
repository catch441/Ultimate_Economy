package com.ue.player;

import org.bukkit.Material;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import com.ue.player.api.EconomyPlayerController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class EconomyPlayerTest {

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
}
