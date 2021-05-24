package org.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.ue.bank.logic.api.BankException;
import org.ue.common.utils.ServerProvider;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.jobsystem.logic.api.JobcenterManager;
import org.ue.jobsystem.logic.api.JobsystemEventHandler;
import org.ue.jobsystem.logic.api.JobsystemException;

public class JobsystemEventHandlerImpl implements JobsystemEventHandler {

	private final ArrayList<Material> lootTypeFish = new ArrayList<>(
			Arrays.asList(Material.COD, Material.SALMON, Material.TROPICAL_FISH, Material.PUFFERFISH));
	private final ArrayList<Material> lootTypeTreasure = new ArrayList<>(
			Arrays.asList(Material.BOW, Material.ENCHANTED_BOOK, Material.FISHING_ROD, Material.NAME_TAG,
					Material.NAUTILUS_SHELL, Material.SADDLE, Material.LILY_PAD));
	private final ArrayList<Material> crops = new ArrayList<>(Arrays.asList(Material.POTATOES, Material.CARROTS,
			Material.WHEAT, Material.NETHER_WART_BLOCK, Material.BEETROOTS, Material.COCOA));
	private final EconomyPlayerManager ecoPlayerManager;
	private final JobcenterManager jobcenterManager;
	private final ServerProvider serverProvider;

	public JobsystemEventHandlerImpl(ServerProvider serverProvider, JobcenterManager jobcenterManager,
			EconomyPlayerManager ecoPlayerManager) {
		this.ecoPlayerManager = ecoPlayerManager;
		this.jobcenterManager = jobcenterManager;
		this.serverProvider = serverProvider;
	}

	@Override
	public void handleBreedEvent(EntityBreedEvent event) {
		try {
			EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(event.getBreeder().getName());
			payForBreedJob(event.getEntityType(), ecoPlayer);
		} catch (EconomyPlayerException e) {
		}
	}

	@Override
	public void handleSetBlock(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL && !(event.getBlock().getType() == Material.SPAWNER)) {
			event.getBlock().setMetadata("placedBy",
					new FixedMetadataValue(serverProvider.getJavaPluginInstance(), event.getPlayer().getName()));
		}
	}

	@Override
	public void handleEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.getKiller() instanceof Player) {
			try {
				EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(entity.getKiller().getName());
				if (entity.getKiller().getGameMode() == GameMode.SURVIVAL) {
					payForKillJob(entity, ecoPlayer);
				}
			} catch (EconomyPlayerException e) {
			}
		}
	}

	private void payForBreedJob(EntityType entity, EconomyPlayer ecoPlayer) {
		for (Job job : ecoPlayer.getJobList()) {
			try {
				double d = job.getBreedPrice(entity);
				ecoPlayer.increasePlayerAmount(d, false);
				break;
			} catch (JobsystemException | BankException e) {
			}
		}
	}

	private void payForKillJob(LivingEntity entity, EconomyPlayer ecoPlayer) {
		for (Job job : ecoPlayer.getJobList()) {
			try {
				double d = job.getKillPrice(entity.getType().toString());
				ecoPlayer.increasePlayerAmount(d, false);
				break;
			} catch (JobsystemException | BankException e) {
			}
		}
	}

	@Override
	public void handleBreakBlock(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			try {
				EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(event.getPlayer().getName());
				List<MetadataValue> list = event.getBlock().getMetadata("placedBy");
				payForBreakJob(ecoPlayer, event.getBlock(), list);
			} catch (EconomyPlayerException e) {
			}
		}
	}

	private void payForBreakJob(EconomyPlayer ecoPlayer, Block block, List<MetadataValue> list) {
		for (Job job : ecoPlayer.getJobList()) {
			try {
				if (crops.contains(block.getType())) {
					payForCrops(block, ecoPlayer, job);
				} else if (list.isEmpty()) {
					double d = job.getBlockPrice(block.getType().toString());
					ecoPlayer.increasePlayerAmount(d, false);
				}
				break;
			} catch (JobsystemException | BankException e) {
			}
		}
	}

	private void payForCrops(Block block, EconomyPlayer ecoPlayer, Job job) throws JobsystemException, BankException {
		Ageable ageable = (Ageable) block.getBlockData();
		if (ageable.getAge() == ageable.getMaximumAge()) {
			double d = job.getBlockPrice(block.getType().toString());
			ecoPlayer.increasePlayerAmount(d, false);
		}
	}

	@Override
	public void handleFishing(PlayerFishEvent event) {
		if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
			try {
				EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(event.getPlayer().getName());
				List<Job> jobList = ecoPlayer.getJobList();
				if (!jobList.isEmpty()) {
					Item caught = (Item) event.getCaught();
					if (caught != null) {
						String lootType = getFishingLootType(caught);
						payForFisherJob(ecoPlayer, jobList, lootType);
					}
				}
			} catch (ClassCastException | EconomyPlayerException e) {
			}
		}
	}

	private void payForFisherJob(EconomyPlayer ecoPlayer, List<Job> jobList, String lootType) {
		for (Job job : jobList) {
			try {
				Double price = job.getFisherPrice(lootType);
				ecoPlayer.increasePlayerAmount(price, false);
				break;
			} catch (JobsystemException | BankException e) {
			}
		}
	}

	private String getFishingLootType(Item caught) {
		Material item = caught.getItemStack().getType();
		if (lootTypeFish.contains(item)) {
			return "fish";
		} else if (lootTypeTreasure.contains(item)) {
			return "treasure";
		} else {
			return "junk";
		}
	}

	@Override
	public void handleInventoryClick(InventoryClickEvent event) {
		Entity entity = (Entity) event.getInventory().getHolder();
		String id = (String) entity.getMetadata("ue-id").get(0).value();
		try {
			Jobcenter jobcenter = jobcenterManager.getJobcenterByName(id);
			EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(event.getWhoClicked().getName());
			jobcenter.handleInventoryClick(event.getClick(), event.getRawSlot(), ecoPlayer);
		} catch (JobsystemException | EconomyPlayerException e) {
		}
	}

	@Override
	public void handleOpenInventory(PlayerInteractEntityEvent event) {
		try {
			event.setCancelled(true);
			jobcenterManager.getJobcenterByName(event.getRightClicked().getCustomName())
					.openInventory(event.getPlayer());
		} catch (JobsystemException e) {
		}
	}
}
