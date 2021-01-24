package com.ue.townsystem.logic.impl;

import javax.inject.Inject;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerExceptionMessageEnum;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.townsystem.logic.api.Plot;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemEventHandler;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.townsystem.logic.api.TownworldManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class TownsystemEventHandlerImpl implements TownsystemEventHandler {

	private final ConfigManager configManager;
	private final TownworldManager townworldManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private final MessageWrapper messageWrapper;
	private final EconomyPlayerValidationHandler ecoPlayerValidationHandler;

	@Override
	public void handlePlayerTeleport(PlayerTeleportEvent event) {
		try {
			EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(event.getPlayer().getName());
			townworldManager.performTownWorldLocationCheck(event.getPlayer().getWorld().getName(),
					event.getTo().getChunk(), ecoPlayer);
		} catch (GeneralEconomyException e) {
		}
	}

	@Override
	public void handlePlayerJoin(PlayerJoinEvent event) {
		try {
			EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(event.getPlayer().getName());
			townworldManager.performTownWorldLocationCheck(event.getPlayer().getWorld().getName(),
					event.getPlayer().getLocation().getChunk(), ecoPlayer);
		} catch (GeneralEconomyException e) {
		}
	}

	@Override
	public void handlerPlayerMove(PlayerMoveEvent event) {
		// check, if player positions changed the chunk
		if (event.getFrom().getChunk().getX() != event.getTo().getChunk().getX()
				|| event.getFrom().getChunk().getZ() != event.getTo().getChunk().getZ()) {
			try {
				EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(event.getPlayer().getName());
				townworldManager.performTownWorldLocationCheck(event.getTo().getWorld().getName(),
						event.getTo().getChunk(), ecoPlayer);
			} catch (GeneralEconomyException e) {
			}
		}
	}

	@Override
	public void handleOpenTownmanagerInventory(PlayerInteractEntityEvent event) {
		event.setCancelled(true);
		try {
			Townworld townworld = townworldManager.getTownWorldByName(event.getRightClicked().getWorld().getName());
			Town town = townworld.getTownByChunk(event.getRightClicked().getLocation().getChunk());
			town.openTownManagerVillagerInv(event.getPlayer());
		} catch (TownSystemException e) {
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
			plot.openSaleVillagerInv(event.getPlayer());
		} catch (TownSystemException e) {
		}
	}

	@Override
	public void handleInventoryClick(InventoryClickEvent event) {
		if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null) {
			// TODO UE-119 extract messages
			event.setCancelled(true);
			if (event.getClickedInventory().getHolder() instanceof Villager) {
				try {
					Townworld townWorld = townworldManager
							.getTownWorldByName(event.getWhoClicked().getWorld().getName());
					Chunk chunk = ((Villager) event.getClickedInventory().getHolder()).getLocation().getChunk();
					EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(event.getWhoClicked().getName());
					Town town = townWorld.getTownByChunk(chunk);
					Plot plot = town.getPlotByChunk(chunk.getX() + "/" + chunk.getZ());
					switch (event.getCurrentItem().getItemMeta().getDisplayName()) {
					case "Buy":
						handleBuyClick(event, chunk, ecoPlayer, town, plot);
						break;
					case "Cancel Sale":
						handleCancelSaleClick(event, ecoPlayer, plot);
						break;
					case "Join":
						handleJoinClick(event, ecoPlayer, town);
						break;
					case "Leave":
						handleLeaveClick(event, ecoPlayer, town);
						break;
					default:
						break;
					}
					event.getWhoClicked().closeInventory();
				} catch (TownSystemException | GeneralEconomyException e) {
				} catch (EconomyPlayerException e) {
					event.getWhoClicked().sendMessage(e.getMessage());
				}
			}
		}
	}

	private void handleLeaveClick(InventoryClickEvent event, EconomyPlayer ecoPlayer, Town town)
			throws TownSystemException, EconomyPlayerException {
		town.leaveTown(ecoPlayer);
		event.getWhoClicked().sendMessage(ChatColor.GOLD + "You left the town " + town.getTownName() + ".");
	}

	private void handleJoinClick(InventoryClickEvent event, EconomyPlayer ecoPlayer, Town town)
			throws EconomyPlayerException {
		town.joinTown(ecoPlayer);
		event.getWhoClicked().sendMessage(ChatColor.GOLD + "You joined the town " + town.getTownName() + ".");
	}

	private void handleCancelSaleClick(InventoryClickEvent event, EconomyPlayer ecoPlayer, Plot plot)
			throws EconomyPlayerException {
		if (plot.isOwner(ecoPlayer)) {
			plot.removeFromSale(ecoPlayer);
			event.getWhoClicked().sendMessage(ChatColor.GOLD + "You removed this plot from sale!");
		}
	}

	private void handleBuyClick(InventoryClickEvent event, Chunk chunk, EconomyPlayer ecoPlayer, Town town, Plot plot)
			throws EconomyPlayerException, GeneralEconomyException, TownSystemException {
		ecoPlayerValidationHandler.checkForEnoughMoney(ecoPlayer.getBankAccount(), plot.getSalePrice(), true);
		EconomyPlayer receiver = plot.getOwner();
		ecoPlayer.payToOtherPlayer(receiver, plot.getSalePrice(), false);
		town.buyPlot(ecoPlayer, chunk.getX(), chunk.getZ());
		event.getWhoClicked().sendMessage(ChatColor.GOLD + "Congratulation! You bought this plot!");
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
						event.getPlayer().sendMessage(
								messageWrapper.getErrorString(EconomyPlayerExceptionMessageEnum.WILDERNESS.getValue()));
					}
				} else {
					Town town = townworld.getTownByChunk(location.getChunk());
					if (hasNoBuildPermission(event, location, economyPlayer, town)) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(messageWrapper
								.getErrorString(EconomyPlayerExceptionMessageEnum.NO_PERMISSION_ON_PLOT.getValue()));
					}
				}
			} catch (TownSystemException | GeneralEconomyException e) {
			}
		}
	}

	private boolean hasNoBuildPermission(PlayerInteractEvent event, Location location, EconomyPlayer economyPlayer,
			Town town) throws TownSystemException {
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
