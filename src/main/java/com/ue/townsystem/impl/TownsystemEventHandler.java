package com.ue.townsystem.impl;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Villager;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.townsystem.api.Plot;
import com.ue.townsystem.api.Town;
import com.ue.townsystem.api.Townworld;
import com.ue.townsystem.api.TownworldController;

public class TownsystemEventHandler {

	/**
	 * Handles the player teleport event for the townsystem.
	 * 
	 * @param event
	 */
	public void handlePlayerTeleport(PlayerTeleportEvent event) {
		TownworldController.handleTownWorldLocationCheck(event.getPlayer().getWorld().getName(),
				event.getTo().getChunk(), event.getPlayer().getName());
	}

	/**
	 * Handles the player join event for the townsystem.
	 * 
	 * @param event
	 */
	public void handlePlayerJoin(PlayerJoinEvent event) {
		TownworldController.handleTownWorldLocationCheck(event.getPlayer().getWorld().getName(),
				event.getPlayer().getLocation().getChunk(), event.getPlayer().getName());
	}

	/**
	 * Handles the player move event for the townsystem.
	 * 
	 * @param event
	 */
	public void handlerPlayerMove(PlayerMoveEvent event) {
		// check, if player positions changed the chunk
		if (event.getFrom().getChunk().getX() != event.getTo().getChunk().getX()
				|| event.getFrom().getChunk().getZ() != event.getTo().getChunk().getZ()) {
			TownworldController.handleTownWorldLocationCheck(event.getTo().getWorld().getName(),
					event.getTo().getChunk(), event.getPlayer().getName());
		}
	}

	/**
	 * Handles the open townmanager inventory event.
	 * 
	 * @param event
	 */
	public void handleOpenTownmanagerInventory(PlayerInteractEntityEvent event) {
		try {
			Townworld townworld2 = TownworldController.getTownWorldByName(event.getRightClicked().getWorld().getName());
			Town town2 = townworld2.getTownByChunk(event.getRightClicked().getLocation().getChunk());
			town2.openTownManagerVillagerInv(event.getPlayer());
		} catch (TownSystemException e) {
		}
	}

	/**
	 * Handles the open plot sale inventory event.
	 * 
	 * @param event
	 */
	public void handleOpenPlotSaleInventory(PlayerInteractEntityEvent event) {
		try {
			Townworld townworld = TownworldController.getTownWorldByName(event.getRightClicked().getWorld().getName());
			Town town = townworld.getTownByChunk(event.getRightClicked().getLocation().getChunk());
			Plot plot = town.getPlotByChunk(event.getRightClicked().getLocation().getChunk().getX() + "/"
					+ event.getRightClicked().getLocation().getChunk().getZ());
			plot.openSaleVillagerInv(event.getPlayer());
		} catch (TownSystemException e) {
		}
	}

	/**
	 * Handles the townmanager and plot seller villager inventory click. TODO
	 * [UE-76]
	 * 
	 * @param event
	 */
	public void handleInventoryClick(InventoryClickEvent event) {
		try {
			Townworld townWorld = TownworldController.getTownWorldByName(event.getWhoClicked().getWorld().getName());
			Chunk chunk = ((Villager) event.getClickedInventory().getHolder()).getLocation().getChunk();
			EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName(event.getWhoClicked().getName());
			Town town = townWorld.getTownByChunk(chunk);
			Plot plot = town.getPlotByChunk(chunk.getX() + "/" + chunk.getZ());
			switch (event.getCurrentItem().getItemMeta().getDisplayName()) {
			case "Buy":
				if (!ecoPlayer.hasEnoughtMoney(plot.getSalePrice())) {
					throw PlayerException.getException(PlayerExceptionMessageEnum.NOT_ENOUGH_MONEY_PERSONAL);
				} else {
					EconomyPlayer receiver = plot.getOwner();
					ecoPlayer.payToOtherPlayer(receiver, plot.getSalePrice(), false);
					town.buyPlot(ecoPlayer, chunk.getX(), chunk.getZ());
					event.getWhoClicked().sendMessage(ChatColor.GOLD + "Congratulation! You bought this plot!");
				}
				break;
			case "Cancel Sale":
				if (plot.isOwner(ecoPlayer)) {
					plot.removeFromSale(ecoPlayer);
					event.getWhoClicked().sendMessage(ChatColor.GOLD + "You removed this plot from sale!");
				}
				break;
			case "Join":
				town.joinTown(ecoPlayer);
				event.getWhoClicked().sendMessage(ChatColor.GOLD + "You joined the town " + town.getTownName() + ".");
				break;
			case "Leave":
				town.leaveTown(ecoPlayer);
				event.getWhoClicked().sendMessage(ChatColor.GOLD + "You left the town " + town.getTownName() + ".");
				break;
			default:
				break;
			}
			event.getWhoClicked().closeInventory();
		} catch (TownSystemException | GeneralEconomyException e) {
		} catch (PlayerException e) {
			event.getWhoClicked().sendMessage(ChatColor.RED + e.getMessage());
		}
	}
	
	/**
	 * Handles the player interact event for the townsystem.
	 * 
	 * @param event
	 */
	public void handlePlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null) {
			Location location = event.getClickedBlock().getLocation();
			try {
				Townworld townworld = TownworldController.getTownWorldByName(location.getWorld().getName());
				EconomyPlayer economyPlayer = EconomyPlayerController
						.getEconomyPlayerByName(event.getPlayer().getName());
				if (townworld.isChunkFree(location.getChunk())) {
					if (!event.getPlayer().hasPermission("ultimate_economy.wilderness")) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(MessageWrapper.getErrorString("wilderness"));
					}
				} else {
					Town town = townworld.getTownByChunk(location.getChunk());
					if (hasNoBuildPermission(event, location, economyPlayer, town)) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(MessageWrapper.getErrorString("no_permission_on_plot"));
					}
				}
			} catch (TownSystemException | PlayerException e) {
			}
		}
	}
	
	private boolean hasNoBuildPermission(PlayerInteractEvent event, Location location, EconomyPlayer economyPlayer,
			Town town) throws TownSystemException {
		if (!event.getPlayer().hasPermission("ultimate_economy.towninteract")
				&& !town.hasBuildPermissions(economyPlayer,
						town.getPlotByChunk(location.getChunk().getX() + "/" + location.getChunk().getZ()))
				|| (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && isDoorOrGate(event)
						&& ConfigController.isExtendedInteraction())) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isDoorOrGate(PlayerInteractEvent event) {
		if (event.getClickedBlock().getType().toString().contains("DOOR")
				|| event.getClickedBlock().getType().toString().contains("GATE")) {
			return true;
		} else {
			return false;
		}
	}
}
