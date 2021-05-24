package org.ue.townsystem.logic.impl;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.townsystem.logic.api.Plot;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.TownsystemEventHandler;
import org.ue.townsystem.logic.api.Townworld;
import org.ue.townsystem.logic.api.TownworldManager;

public class TownsystemEventHandlerImpl implements TownsystemEventHandler {

	private final ConfigManager configManager;
	private final TownworldManager townworldManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private final MessageWrapper messageWrapper;

	public TownsystemEventHandlerImpl(ConfigManager configManager, TownworldManager townworldManager,
			EconomyPlayerManager ecoPlayerManager, MessageWrapper messageWrapper) {
		this.configManager = configManager;
		this.townworldManager = townworldManager;
		this.ecoPlayerManager = ecoPlayerManager;
		this.messageWrapper = messageWrapper;
	}

	@Override
	public void handlePlayerTeleport(PlayerTeleportEvent event) {
		try {
			EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(event.getPlayer().getName());
			townworldManager.performTownWorldLocationCheck(ecoPlayer, null);
		} catch (EconomyPlayerException e) {
		}
	}

	@Override
	public void handlePlayerJoin(PlayerJoinEvent event) {
		try {
			EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(event.getPlayer().getName());
			townworldManager.performTownWorldLocationCheck(ecoPlayer, null);
		} catch (EconomyPlayerException e) {
		}
	}

	@Override
	public void handlerPlayerMove(PlayerMoveEvent event) {
		// check, if player positions changed the chunk
		if (event.getFrom().getChunk().getX() != event.getTo().getChunk().getX()
				|| event.getFrom().getChunk().getZ() != event.getTo().getChunk().getZ()) {
			try {
				EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(event.getPlayer().getName());
				townworldManager.performTownWorldLocationCheck(ecoPlayer, event.getTo());
			} catch (EconomyPlayerException e) {
			}
		}
	}

	@Override
	public void handleOpenTownmanagerInventory(PlayerInteractEntityEvent event) {
		event.setCancelled(true);
		try {
			Townworld townworld = townworldManager.getTownWorldByName(event.getRightClicked().getWorld().getName());
			Town town = townworld.getTownByChunk(event.getRightClicked().getLocation().getChunk());
			town.openInventory(event.getPlayer());
		} catch (TownsystemException e) {
		}
	}

	@Override
	public void handleOpenPlotSaleInventory(PlayerInteractEntityEvent event) {
		event.setCancelled(true);
		try {
			Townworld townworld = townworldManager.getTownWorldByName(event.getRightClicked().getWorld().getName());
			Town town = townworld.getTownByChunk(event.getRightClicked().getLocation().getChunk());
			Plot plot = town.getPlotByChunk(event.getRightClicked().getLocation().getChunk().getX() + "/"
					+ event.getRightClicked().getLocation().getChunk().getZ());
			plot.openInventoryWithCheck(event.getPlayer());
		} catch (TownsystemException e) {
		}
	}

	@Override
	public void handleInventoryClick(InventoryClickEvent event) {
		event.setCancelled(true);
		try {
			EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(event.getWhoClicked().getName());
			Townworld townWorld = townworldManager.getTownWorldByName(event.getWhoClicked().getWorld().getName());
			Chunk chunk = ((Entity) event.getClickedInventory().getHolder()).getLocation().getChunk();
			Town town = townWorld.getTownByChunk(chunk);

			String inventoryName = event.getView().getTitle();
			if (inventoryName.contains("TownManager")) {
				town.handleInventoryClick(event.getClick(), event.getRawSlot(), ecoPlayer);
			} else if (inventoryName.contains("Plot")) {
				Plot plot = town.getPlotByChunk(chunk.getX() + "/" + chunk.getZ());
				plot.handleInventoryClick(event.getClick(), event.getRawSlot(), ecoPlayer);
			}
		} catch (TownsystemException | EconomyPlayerException e) {
		}
	}

	@Override
	public void handlePlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null) {
			Location location = event.getClickedBlock().getLocation();
			try {
				Townworld townworld = townworldManager.getTownWorldByName(location.getWorld().getName());
				EconomyPlayer economyPlayer = ecoPlayerManager.getEconomyPlayerByName(event.getPlayer().getName());
				if (townworld.isChunkFree(location.getChunk())) {
					if (!event.getPlayer().hasPermission("ultimate_economy.wilderness")) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(messageWrapper.getErrorString(ExceptionMessageEnum.WILDERNESS));
					}
				} else {
					Town town = townworld.getTownByChunk(location.getChunk());
					if (hasNoBuildPermission(event, location, economyPlayer, town)) {
						event.setCancelled(true);
						event.getPlayer()
								.sendMessage(messageWrapper.getErrorString(ExceptionMessageEnum.NO_PERMISSION_ON_PLOT));
					}
				}
			} catch (TownsystemException | EconomyPlayerException e) {
			}
		}
	}

	private boolean hasNoBuildPermission(PlayerInteractEvent event, Location location, EconomyPlayer economyPlayer,
			Town town) throws TownsystemException {
		if (!event.getPlayer().hasPermission("ultimate_economy.towninteract")
				&& !town.hasBuildPermissions(economyPlayer,
						town.getPlotByChunk(location.getChunk().getX() + "/" + location.getChunk().getZ()))
				|| (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
						&& isDoorOrGate(event.getClickedBlock().getType()) && configManager.isExtendedInteraction())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isDoorOrGate(Material material) {
		if (material.toString().contains("DOOR") || material.toString().contains("GATE")) {
			return true;
		} else {
			return false;
		}
	}
}
