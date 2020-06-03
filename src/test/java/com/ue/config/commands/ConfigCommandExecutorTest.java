package com.ue.config.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.bukkit.command.CommandExecutor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.config.api.ConfigController;
import com.ue.player.api.EconomyPlayerController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class ConfigCommandExecutorTest {
    
    private static ServerMock server;
    private static PlayerMock player;
    private static CommandExecutor executor;

    /**
     * Init shop for tests.
     */
    @BeforeAll
    public static void initPlugin() {
	server = MockBukkit.mock();
	MockBukkit.load(UltimateEconomy.class);
	player = server.addPlayer("catch441");
	executor = new ConfigCommandExecutor();
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
	
    }
    
    //@Test
    public void languageCommandTestWithAll() {
	// TODO languageCommandTestWithAll
	String[] args = { "language", "en", "US" };
	executor.onCommand(player, null, "ue-config", args);
	//assertEquals("center", center.getName());
	String message = player.nextMessage();
	assertEquals("§6The jobcenter §acenter§6 was created.", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxRentedDaysCommandTestWithAll() {
	String[] args = { "maxRentedDays", "8" };
	executor.onCommand(player, null, "ue-config", args);
	assertEquals(8,ConfigController.getMaxRentedDays());
	String message = player.nextMessage();
	assertEquals("§6The configuration was changed to §a8§6.", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxRentedDaysCommandTestWithInvalidArgumentNumber() {
	String[] args = { "maxRentedDays" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("/ue-config maxRentedDays <number>", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxRentedDaysCommandTestWithInvalidNumber() {
	String[] args = { "maxRentedDays", "-1" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("§c§cThe parameter §4-1§c is invalid!", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxHomesCommandTestWithAll() {
	String[] args = { "maxHomes", "8" };
	executor.onCommand(player, null, "ue-config", args);
	assertEquals(8,ConfigController.getMaxHomes());
	String message = player.nextMessage();
	assertEquals("§6The configuration was changed to §a8§6.", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxHomesCommandTestWithInvalidArgumentNumber() {
	String[] args = { "maxHomes" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("/ue-config maxHomes <number>", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxHomesCommandTestWithInvalidNumber() {
	String[] args = { "maxHomes", "-1" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("§c§cThe parameter §4-1§c is invalid!", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxJobsCommandTestWithAll() {
	String[] args = { "maxJobs", "8" };
	executor.onCommand(player, null, "ue-config", args);
	assertEquals(8,ConfigController.getMaxJobs());
	String message = player.nextMessage();
	assertEquals("§6The configuration was changed to §a8§6.", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxJobsCommandTestWithInvalidArgumentNumber() {
	String[] args = { "maxJobs" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("/ue-config maxJobs <number>", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxJobsCommandTestWithInvalidNumber() {
	String[] args = { "maxJobs", "-1" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("§c§cThe parameter §4-1§c is invalid!", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxJoinedTownsCommandTestWithAll() {
	String[] args = { "maxJoinedTowns", "8" };
	executor.onCommand(player, null, "ue-config", args);
	assertEquals(8,ConfigController.getMaxJoinedTowns());
	String message = player.nextMessage();
	assertEquals("§6The configuration was changed to §a8§6.", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxJoinedTownsCommandTestWithInvalidArgumentNumber() {
	String[] args = { "maxJoinedTowns" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("/ue-config maxJoinedTowns <number>", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxJoinedTownsCommandTestWithInvalidNumber() {
	String[] args = { "maxJoinedTowns", "-1" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("§c§cThe parameter §4-1§c is invalid!", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxPlayershopsCommandTestWithAll() {
	String[] args = { "maxPlayershops", "8" };
	executor.onCommand(player, null, "ue-config", args);
	assertEquals(8,ConfigController.getMaxPlayershops());
	String message = player.nextMessage();
	assertEquals("§6The configuration was changed to §a8§6.", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxPlayershopsCommandTestWithInvalidArgumentNumber() {
	String[] args = { "maxPlayershops" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("/ue-config maxPlayershops <number>", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void maxPlayershopsCommandTestWithInvalidNumber() {
	String[] args = { "maxPlayershops", "-1" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("§c§cThe parameter §4-1§c is invalid!", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void homesCommandTestWithAll() {
	String[] args = { "homes", "false" };
	executor.onCommand(player, null, "ue-config", args);
	assertFalse(ConfigController.isHomeSystem());
	String message = player.nextMessage();
	assertEquals("§6The configuration was changed to §afalse§6.", message);
	String message2 = player.nextMessage();
	assertEquals("§6Please restart the server!", message2);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void homesCommandTestWithInvalidArgumentNumber() {
	String[] args = { "homes" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("/ue-config homes <true/false>", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void homesCommandTestWithInvalidNumber() {
	String[] args = { "homes", "abc" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("§cThe parameter §4abc§c is invalid!", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void extendedInteractionCommandTestWithAll() {
	String[] args = { "extendedInteraction", "false" };
	executor.onCommand(player, null, "ue-config", args);
	assertFalse(ConfigController.isExtendedInteraction());
	String message = player.nextMessage();
	assertEquals("§6The configuration was changed to §afalse§6.", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void extendedInteractionCommandTestWithInvalidArgumentNumber() {
	String[] args = { "extendedInteraction" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("/ue-config extendedInteraction <true/false>", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void extendedInteractionCommandTestWithInvalidNumber() {
	String[] args = { "extendedInteraction", "abc" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("§cThe parameter §4abc§c is invalid!", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void wildernessInteractionCommandTestWithAll() {
	String[] args = { "wildernessInteraction", "false" };
	executor.onCommand(player, null, "ue-config", args);
	assertFalse(ConfigController.isWildernessInteraction());
	String message = player.nextMessage();
	assertEquals("§6The configuration was changed to §afalse§6.", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void wildernessInteractionCommandTestWithInvalidArgumentNumber() {
	String[] args = { "wildernessInteraction" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("/ue-config wildernessInteraction <true/false>", message);
	assertNull(player.nextMessage());
    }
    
    @Test
    public void wildernessInteractionCommandTestWithInvalidNumber() {
	String[] args = { "wildernessInteraction", "abc" };
	executor.onCommand(player, null, "ue-config", args);
	String message = player.nextMessage();
	assertEquals("§cThe parameter §4abc§c is invalid!", message);
	assertNull(player.nextMessage());
    }
}
