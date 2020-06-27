package com.ue.eventhandling;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.economyplayer.impl.EconomyPlayerSavefileHandler;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.jobsystem.impl.JobSystemEventHandler;
import com.ue.language.MessageWrapper;
import com.ue.shopsystem.impl.Spawner;
import com.ue.townsystem.api.Town;
import com.ue.townsystem.api.Townworld;
import com.ue.townsystem.api.TownworldController;
import com.ue.ultimate_economy.UltimateEconomy;
import com.ue.updater.Updater;
import com.ue.updater.Updater.UpdateResult;

public class UltimateEconomyEventHandler implements Listener {

	private UltimateEconomy plugin;
	private List<String> playerlist;
	private UpdateResult updateResult;
	private List<String> spawnerlist;
	private File spawner;
	private JobSystemEventHandler jobSystemEventHandler;

	/**
	 * Constructor of ultimate economy event handler.
	 * 
	 * @param plugin
	 * @param spawnerlist
	 * @param spawner
	 */
	public UltimateEconomyEventHandler(UltimateEconomy plugin, List<String> spawnerlist, File spawner) {
		this.plugin = plugin;
		playerlist = EconomyPlayerSavefileHandler.loadPlayerList();
		// version check
		updateResult = Updater.checkForUpdate(plugin.getDescription().getVersion());
		this.spawnerlist = spawnerlist;
		this.spawner = spawner;
		jobSystemEventHandler = new JobSystemEventHandler();
	}

	/**
	 * Handles player teleport event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		TownworldController.handleTownWorldLocationCheck(event.getPlayer().getWorld().getName(),
				event.getTo().getChunk(), event.getPlayer().getName());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Handles player interact event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
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

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Handles player interact entity event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onNPCOpenInv(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		if (entity instanceof Villager && entity.hasMetadata("ue-type")) {
			String shopId = "";
			if (entity.hasMetadata("ue-id")) {
				shopId = (String) entity.getMetadata("ue-id").get(0).value();
			}
			EconomyVillager economyVillager = (EconomyVillager) entity.getMetadata("ue-type").get(0).value();
			event.setCancelled(true);
			switch (economyVillager) {
			case JOBCENTER:
				jobSystemEventHandler.handleOpenInventory(event);
				break;
			case ADMINSHOP:
				break;
			case PLAYERSHOP:
				break;
			case PLAYERSHOP_RENTABLE:
				break;
			case PLOTSALE:
				break;
			case TOWNMANAGER:
				break;
			default:
				break;
			}
			try {
				economyVillager.performOpenInventory(entity, shopId, event.getPlayer());
			} catch (TownSystemException | ShopSystemException | GeneralEconomyException e) {
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Handle entity damabe by entity event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onHitVillagerEvent(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Villager
				&& event.getEntity().hasMetadata("ue-type")) {
			Player damager = (Player) event.getDamager();
			EconomyVillager type = (EconomyVillager) event.getEntity().getMetadata("ue-type").get(0).value();
			event.setCancelled(true);
			switch (type) {
			case PLOTSALE:
			case TOWNMANAGER:
			case ADMINSHOP:
			case PLAYERSHOP_RENTABLE:
			case PLAYERSHOP:
			case JOBCENTER:
				damager.sendMessage(MessageWrapper.getErrorString("villager_hitevent"));
				break;
			default:
				break;
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Handles entity death event.
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		jobSystemEventHandler.handleEntityDeath(event);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Handles inventory click event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onInvClickEvent(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Entity) {
			Entity entity = (Entity) holder;
			// prevents that spawners can be renamed in a anvil
			if (event.getCurrentItem() != null && event.getInventory().getType() == InventoryType.ANVIL
					&& event.getCurrentItem().getType() == Material.SPAWNER) {
				event.setCancelled(true);
			} else if (entity.hasMetadata("ue-type") && event.getCurrentItem() != null
					&& event.getCurrentItem().getItemMeta() != null) {
				String shopId = "";
				if (entity.hasMetadata("ue-id")) {
					shopId = (String) entity.getMetadata("ue-id").get(0).value();
				}
				EconomyVillager economyVillager = (EconomyVillager) entity.getMetadata("ue-type").get(0).value();
				event.setCancelled(true);
				switch (economyVillager) {
				case JOBCENTER:
					jobSystemEventHandler.handleInventoryClick(event);
					break;
				case ADMINSHOP:
					break;
				case PLAYERSHOP:
					break;
				case PLAYERSHOP_RENTABLE:
					break;
				case PLOTSALE:
					break;
				case TOWNMANAGER:
					break;
				default:
					break;
				}
				try {
					economyVillager.performHandleInventoryClick(event, shopId);
				} catch (TownSystemException | ShopSystemException | IllegalArgumentException
						| GeneralEconomyException e) {
				} catch (PlayerException e) {
					player.sendMessage(ChatColor.RED + e.getMessage());
				}
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Handles block place event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void setBlockEvent(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			if (event.getBlock().getBlockData().getMaterial() == Material.SPAWNER
					&& event.getItemInHand().getItemMeta().getDisplayName().contains("-")) {
				handleSetSpawner(event);
			} else if (!(event.getBlock().getBlockData().getMaterial() == Material.SPAWNER)) {
				event.getBlock().setMetadata("placedBy", new FixedMetadataValue(plugin, event.getPlayer().getName()));
			}
		}
	}

	private void handleSetSpawner(BlockPlaceEvent event) {
		String spawnerowner = event.getItemInHand().getItemMeta().getDisplayName()
				.substring(event.getItemInHand().getItemMeta().getDisplayName().lastIndexOf("-") + 1);
		if (spawnerowner.equals(event.getPlayer().getName())) {
			String string = event.getItemInHand().getItemMeta().getDisplayName();
			Spawner.setSpawner(EntityType.valueOf(string.substring(0, string.lastIndexOf("-"))), event.getBlock());
			event.getBlock().setMetadata("name",
					new FixedMetadataValue(plugin, string.substring(string.lastIndexOf("-") + 1)));
			event.getBlock().setMetadata("entity",
					new FixedMetadataValue(plugin, string.substring(0, string.lastIndexOf("-"))));
			YamlConfiguration config = YamlConfiguration.loadConfiguration(spawner);
			double x = event.getBlock().getX();
			double y = event.getBlock().getY();
			double z = event.getBlock().getZ();
			String spawnername = String.valueOf(x) + String.valueOf(y) + String.valueOf(z);
			spawnername = spawnername.replace(".", "-");
			spawnerlist.add(spawnername);
			plugin.getConfig().set("Spawnerlist", spawnerlist);
			plugin.saveConfig();
			config.set(spawnername + ".X", x);
			config.set(spawnername + ".Y", y);
			config.set(spawnername + ".Z", z);
			config.set(spawnername + ".World", event.getBlock().getWorld().getName());
			config.set(spawnername + ".player", string.substring(string.lastIndexOf("-") + 1));
			config.set(spawnername + ".EntityType", string.substring(0, string.lastIndexOf("-")));
			saveFile(spawner, config);
		} else {
			event.getPlayer().sendMessage(MessageWrapper.getErrorString("no_permission_set_spawner"));
			event.setCancelled(true);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Handles block break event.
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void breakBlockEvent(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			if (event.getBlock().getBlockData().getMaterial() == Material.SPAWNER) {
				handleBreakSpawner(event);
			} else {
				jobSystemEventHandler.handleBreakBlock(event);
			}
		}
	}

	private void handleBreakSpawner(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			handleBreakSpawnerInSurvival(event);
		} else {
			handleBreakSpawnerInCreative(event);
		}
	}

	private void handleBreakSpawnerInSurvival(BlockBreakEvent event) {
		List<MetadataValue> blockmeta = event.getBlock().getMetadata("name");
		if (!blockmeta.isEmpty()) {
			MetadataValue s = blockmeta.get(0);
			String blockname = s.asString();
			if (event.getPlayer().getInventory().firstEmpty() == -1) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(MessageWrapper.getErrorString("inventory_full"));
			} else if (event.getPlayer().getName().equals(blockname)) {
				if (!event.getBlock().getMetadata("entity").isEmpty()) {
					YamlConfiguration config = YamlConfiguration.loadConfiguration(spawner);
					double x = event.getBlock().getX();
					double y = event.getBlock().getY();
					double z = event.getBlock().getZ();
					String spawnername = String.valueOf(x) + String.valueOf(y) + String.valueOf(z);
					spawnername = spawnername.replace(".", "-");
					spawnerlist.remove(spawnername);
					plugin.getConfig().set("Spawnerlist", spawnerlist);
					plugin.saveConfig();
					config.set(spawnername, null);
					saveFile(spawner, config);
					ItemStack stack = new ItemStack(Material.SPAWNER, 1);
					ItemMeta meta = stack.getItemMeta();
					meta.setDisplayName(event.getBlock().getMetadata("entity").get(0).asString() + "-"
							+ event.getPlayer().getName());
					stack.setItemMeta(meta);
					event.getPlayer().getInventory().addItem(stack);
				}
			} else {
				event.setCancelled(true);
				event.getPlayer().sendMessage(MessageWrapper.getErrorString("no_permission_break_spawner"));
			}
		}
	}

	private void handleBreakSpawnerInCreative(BlockBreakEvent event) {
		if (!event.getBlock().getMetadata("entity").isEmpty()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(spawner);
			double x = event.getBlock().getX();
			double y = event.getBlock().getY();
			double z = event.getBlock().getZ();
			String spawnername = String.valueOf(x) + String.valueOf(y) + String.valueOf(z);
			spawnername = spawnername.replace(".", "-");
			spawnerlist.remove(spawnername);
			plugin.getConfig().set("Spawnerlist", spawnerlist);
			plugin.saveConfig();
			config.set(spawnername, null);
			saveFile(spawner, config);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Handles player join event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onJoinEvent(PlayerJoinEvent event) {
		String playername = event.getPlayer().getName();
		try {
			if (!playerlist.contains(playername)) {
				playerlist.add(playername);
				EconomyPlayerController.createEconomyPlayer(playername);
			}
			EconomyPlayer economyPlayer = EconomyPlayerController.getEconomyPlayerByName(playername);
			economyPlayer.setPlayer(event.getPlayer());
			if (ConfigController.isWildernessInteraction()) {
				economyPlayer.addWildernessPermission();
			}
			TownworldController.handleTownWorldLocationCheck(event.getPlayer().getWorld().getName(),
					event.getPlayer().getLocation().getChunk(), event.getPlayer().getName());
		} catch (PlayerException e) {
			Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
		}
		if (event.getPlayer().isOp()) {
			if (updateResult == UpdateResult.UPDATE_AVAILABLE) {
				event.getPlayer().sendMessage(ChatColor.GOLD + "There is a newer version of " + ChatColor.GREEN
						+ "Ultimate_Economy " + ChatColor.GOLD + "available!");
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Handles player fish event.
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFishingEvent(PlayerFishEvent event) {
		jobSystemEventHandler.handleFishing(event);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Handles player move event.
	 * 
	 * @param event
	 */
	@EventHandler()
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		// check, if player positions changed the chunk
		if (event.getFrom().getChunk().getX() != event.getTo().getChunk().getX()
				|| event.getFrom().getChunk().getZ() != event.getTo().getChunk().getZ()) {
			TownworldController.handleTownWorldLocationCheck(event.getTo().getWorld().getName(),
					event.getTo().getChunk(), event.getPlayer().getName());
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Methods
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void saveFile(File file, FileConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
