package org.ue.general.impl;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.bank.logic.api.BankException;
import org.ue.common.utils.Updater;
import org.ue.common.utils.Updater.UpdateResult;
import org.ue.economyplayer.logic.api.EconomyPlayerEventHandler;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyvillager.logic.api.EconomyVillagerType;
import org.ue.jobsystem.logic.api.JobcenterManager;
import org.ue.jobsystem.logic.api.JobsystemEventHandler;
import org.ue.shopsystem.logic.api.AdminshopManager;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.RentshopManager;
import org.ue.shopsystem.logic.api.ShopEventHandler;
import org.ue.spawnersystem.logic.api.SpawnersystemEventHandler;
import org.ue.townsystem.logic.api.TownsystemEventHandler;

public class UltimateEconomyEventHandlerImpl implements Listener {

	private static final Logger log = LoggerFactory.getLogger(UltimateEconomyEventHandlerImpl.class);
	private final EconomyPlayerEventHandler ecoPlayerEventHandler;
	private final JobsystemEventHandler jobsystemEventHandler;
	private final ShopEventHandler shopEventHandler;
	private final TownsystemEventHandler townSystemEventHandler;
	private final SpawnersystemEventHandler spawnerSystemEventHandler;
	private final Updater updater;
	private final AdminshopManager adminshopManager;
	private final PlayershopManager playershopManager;
	private final RentshopManager rentshopManager;
	private final JobcenterManager jobcenterManager;
	
	public UltimateEconomyEventHandlerImpl(JobcenterManager jobcenterManager, RentshopManager rentshopManager,
			PlayershopManager playershopManager, AdminshopManager adminshopManager, Updater updater,
			SpawnersystemEventHandler spawnerSystemEventHandler, TownsystemEventHandler townSystemEventHandler,
			ShopEventHandler shopEventHandler, JobsystemEventHandler jobsystemEventHandler,
			EconomyPlayerEventHandler ecoPlayerEventHandler) {
		this.ecoPlayerEventHandler = ecoPlayerEventHandler;
		this.shopEventHandler = shopEventHandler;
		this.jobsystemEventHandler = jobsystemEventHandler;
		this.townSystemEventHandler = townSystemEventHandler;
		this.spawnerSystemEventHandler = spawnerSystemEventHandler;
		this.updater = updater;
		this.adminshopManager = adminshopManager;
		this.playershopManager = playershopManager;
		this.rentshopManager = rentshopManager;
		this.jobcenterManager = jobcenterManager;
	}

	private boolean firstJoin = true;
	
	/**
	 * Handles entity breed event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onEntityBreed(EntityBreedEvent event) {
		jobsystemEventHandler.handleBreedEvent(event);
	}

	/**
	 * Handles player teleport event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		townSystemEventHandler.handlePlayerTeleport(event);
	}

	/**
	 * Handles player interact event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		townSystemEventHandler.handlePlayerInteract(event);
	}

	/**
	 * Handles player interact entity event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onNPCOpenInv(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		if (entity instanceof Villager && entity.hasMetadata("ue-type")) {
			handleEconomyVillagerOpenInv(event, entity);
		}
	}

	private void handleEconomyVillagerOpenInv(PlayerInteractEntityEvent event, Entity entity) {
		EconomyVillagerType economyVillager = EconomyVillagerType
				.getEnum(entity.getMetadata("ue-type").get(0).value().toString());
		switch (economyVillager) {
		case JOBCENTER:
			jobsystemEventHandler.handleOpenInventory(event);
			break;
		case ADMINSHOP:
		case PLAYERSHOP:
		case RENTSHOP:
			shopEventHandler.handleOpenInventory(event);
			break;
		case PLOTSALE:
			townSystemEventHandler.handleOpenPlotSaleInventory(event);
			break;
		case TOWNMANAGER:
			townSystemEventHandler.handleOpenTownmanagerInventory(event);
			break;
		default:
			break;
		}
	}

	/**
	 * Handles entity death event.
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		jobsystemEventHandler.handleEntityDeath(event);
	}

	/**
	 * Handles inventory click event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onInvClickEvent(InventoryClickEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Entity) {
			Entity entity = (Entity) holder;
			spawnerSystemEventHandler.handleInventoryClick(event);
			handleEconomyVillagerInvClick(event, entity);
		}
	}

	private void handleEconomyVillagerInvClick(InventoryClickEvent event, Entity entity) {
		if (entity.hasMetadata("ue-type")) {
			EconomyVillagerType economyVillager = (EconomyVillagerType) entity.getMetadata("ue-type").get(0).value();
			switch (economyVillager) {
			case JOBCENTER:
				jobsystemEventHandler.handleInventoryClick(event);
				break;
			case ADMINSHOP:
			case PLAYERSHOP:
			case RENTSHOP:
				shopEventHandler.handleInventoryClick(event);
				break;
			case PLOTSALE:
			case TOWNMANAGER:
				townSystemEventHandler.handleInventoryClick(event);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Handles block place event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void setBlockEvent(BlockPlaceEvent event) {
		spawnerSystemEventHandler.handleSetBlockEvent(event);
		jobsystemEventHandler.handleSetBlock(event);
	}

	/**
	 * Handles block break event.
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void breakBlockEvent(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			if (event.getBlock().getBlockData().getMaterial() == Material.SPAWNER) {
				spawnerSystemEventHandler.handleBreakBlockEvent(event);
			} else {
				jobsystemEventHandler.handleBreakBlock(event);
			}
		}
	}

	/**
	 * Handles player join event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onJoinEvent(PlayerJoinEvent event) {
		try {
			ecoPlayerEventHandler.handleJoin(event);
			townSystemEventHandler.handlePlayerJoin(event);
		} catch (EconomyPlayerException | BankException e) {
			log.warn("[Ultimate_Economy] " + e.getMessage());
		}
		if (event.getPlayer().isOp()) {
			if (updater.getUpdateResult() == UpdateResult.UPDATE_AVAILABLE) {
				// TODO extract message into language file
				event.getPlayer().sendMessage(ChatColor.GOLD + "There is a newer version of " + ChatColor.GREEN
						+ "Ultimate_Economy " + ChatColor.GOLD + "available!");
			}
		}

		/*
		 * TODO: Only here to fix a cluser of issues. When spawning the villagers at
		 * startup without any player, then no changes to these villagers are visible
		 * ingame (rename, move ...). In spigot it works, but in paper it doesn't. This
		 * is just a quickfix and not a solution. [UE-139,UE-140]
		 */
		if (firstJoin) {
			jobcenterManager.loadAllJobcenters();
			adminshopManager.loadAllAdminShops();
			playershopManager.loadAllPlayerShops();
			rentshopManager.loadAllRentShops();
			firstJoin = false;
		}
	}

	/**
	 * Handles player fish event.
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFishingEvent(PlayerFishEvent event) {
		jobsystemEventHandler.handleFishing(event);
	}

	/**
	 * Handles entity transform event.
	 * 
	 * @param event
	 */
	@EventHandler()
	public void onEntityTransform(EntityTransformEvent event) {
		if (event.getEntity() instanceof Villager && event.getEntity().hasMetadata("ue-type")) {
			event.setCancelled(true);
		}
	}

	/**
	 * Handles player move event.
	 * 
	 * @param event
	 */
	@EventHandler()
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		townSystemEventHandler.handlerPlayerMove(event);
	}
}
