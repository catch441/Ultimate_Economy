package com.ue.ultimate_economy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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

import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.Updater;
import com.ue.common.utils.Updater.UpdateResult;
import com.ue.economyplayer.logic.api.EconomyPlayerEventHandler;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.jobsystem.logic.api.JobsystemEventHandler;
import com.ue.shopsystem.logic.api.ShopEventHandler;
import com.ue.shopsystem.logic.impl.Spawner;
import com.ue.townsystem.logic.api.TownsystemEventHandler;

public class UltimateEconomyEventHandler implements Listener {

	private final EconomyPlayerEventHandler ecoPlayerEventHandler;
	private final MessageWrapper messageWrapper;
	private final JobsystemEventHandler jobsystemEventHandler;
	private final ShopEventHandler shopEventHandler;
	private final ServerProvider serverProvider;
	private final TownsystemEventHandler townSystemEventHandler;
	private UpdateResult updateResult;
	private List<String> spawnerlist;
	private File spawner;

	/**
	 * Constructor of ultimate economy event handler.
	 * 
	 * @param townSystemEventHandler
	 * @param serverProvider
	 * @param shopEventHandler
	 * @param jobsystemEventHandler
	 * @param ecoPlayerEventHandler
	 * @param messageWrapper
	 * @param spawnerlist
	 * @param spawner
	 */
	public UltimateEconomyEventHandler(TownsystemEventHandler townSystemEventHandler, ServerProvider serverProvider,
			ShopEventHandler shopEventHandler, JobsystemEventHandler jobsystemEventHandler,
			EconomyPlayerEventHandler ecoPlayerEventHandler, MessageWrapper messageWrapper, List<String> spawnerlist,
			File spawner) {
		this.ecoPlayerEventHandler = ecoPlayerEventHandler;
		this.shopEventHandler = shopEventHandler;
		this.jobsystemEventHandler = jobsystemEventHandler;
		this.messageWrapper = messageWrapper;
		this.serverProvider = serverProvider;
		this.townSystemEventHandler = townSystemEventHandler;
		// version check
		updateResult = Updater.checkForUpdate(serverProvider.getPluginInstance().getDescription().getVersion());
		this.spawnerlist = spawnerlist;
		this.spawner = spawner;
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
		EconomyVillager economyVillager = EconomyVillager
				.getEnum(entity.getMetadata("ue-type").get(0).value().toString());
		switch (economyVillager) {
		case JOBCENTER:
			jobsystemEventHandler.handleOpenInventory(event);
			break;
		case ADMINSHOP:
		case PLAYERSHOP:
		case PLAYERSHOP_RENTABLE:
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
	 * Handle entity damabe by entity event.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onHitVillagerEvent(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Villager
				&& event.getEntity().hasMetadata("ue-type")) {
			Player damager = (Player) event.getDamager();
			damager.sendMessage(messageWrapper.getErrorString("villager_hitevent"));
			event.setCancelled(true);
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
			preventSpawnerRenaming(event);
			handleEconomyVillagerInvClick(event, entity);
		}
	}

	private void preventSpawnerRenaming(InventoryClickEvent event) {
		if (event.getCurrentItem() != null && event.getInventory().getType() == InventoryType.ANVIL
				&& event.getCurrentItem().getType() == Material.SPAWNER) {
			event.setCancelled(true);
		}
	}

	private void handleEconomyVillagerInvClick(InventoryClickEvent event, Entity entity) {
		if (entity.hasMetadata("ue-type")) {
			EconomyVillager economyVillager = (EconomyVillager) entity.getMetadata("ue-type").get(0).value();
			switch (economyVillager) {
			case JOBCENTER:
				jobsystemEventHandler.handleInventoryClick(event);
				break;
			case ADMINSHOP:
			case PLAYERSHOP:
			case PLAYERSHOP_RENTABLE:
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
			}
		}
		jobsystemEventHandler.handleSetBlock(event);
	}

	private void handleSetSpawner(BlockPlaceEvent event) {
		String spawnerowner = event.getItemInHand().getItemMeta().getDisplayName()
				.substring(event.getItemInHand().getItemMeta().getDisplayName().lastIndexOf("-") + 1);
		if (spawnerowner.equals(event.getPlayer().getName())) {
			String string = event.getItemInHand().getItemMeta().getDisplayName();
			Spawner.setSpawner(EntityType.valueOf(string.substring(0, string.lastIndexOf("-"))), event.getBlock());
			event.getBlock().setMetadata("name", new FixedMetadataValue(serverProvider.getPluginInstance(),
					string.substring(string.lastIndexOf("-") + 1)));
			event.getBlock().setMetadata("entity", new FixedMetadataValue(serverProvider.getPluginInstance(),
					string.substring(0, string.lastIndexOf("-"))));
			YamlConfiguration config = YamlConfiguration.loadConfiguration(spawner);
			double x = event.getBlock().getX();
			double y = event.getBlock().getY();
			double z = event.getBlock().getZ();
			String spawnername = String.valueOf(x) + String.valueOf(y) + String.valueOf(z);
			spawnername = spawnername.replace(".", "-");
			spawnerlist.add(spawnername);
			serverProvider.getPluginInstance().getConfig().set("Spawnerlist", spawnerlist);
			serverProvider.getPluginInstance().saveConfig();
			config.set(spawnername + ".X", x);
			config.set(spawnername + ".Y", y);
			config.set(spawnername + ".Z", z);
			config.set(spawnername + ".World", event.getBlock().getWorld().getName());
			config.set(spawnername + ".player", string.substring(string.lastIndexOf("-") + 1));
			config.set(spawnername + ".EntityType", string.substring(0, string.lastIndexOf("-")));
			saveFile(spawner, config);
		} else {
			event.getPlayer().sendMessage(messageWrapper.getErrorString("no_permission_set_spawner"));
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
				jobsystemEventHandler.handleBreakBlock(event);
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
				event.getPlayer().sendMessage(messageWrapper.getErrorString("inventory_full"));
			} else if (event.getPlayer().getName().equals(blockname)) {
				if (!event.getBlock().getMetadata("entity").isEmpty()) {
					YamlConfiguration config = YamlConfiguration.loadConfiguration(spawner);
					double x = event.getBlock().getX();
					double y = event.getBlock().getY();
					double z = event.getBlock().getZ();
					String spawnername = String.valueOf(x) + String.valueOf(y) + String.valueOf(z);
					spawnername = spawnername.replace(".", "-");
					spawnerlist.remove(spawnername);
					serverProvider.getPluginInstance().getConfig().set("Spawnerlist", spawnerlist);
					serverProvider.getPluginInstance().saveConfig();
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
				event.getPlayer().sendMessage(messageWrapper.getErrorString("no_permission_break_spawner"));
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
			serverProvider.getPluginInstance().getConfig().set("Spawnerlist", spawnerlist);
			serverProvider.getPluginInstance().saveConfig();
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
		try {
			ecoPlayerEventHandler.handleJoin(event);
			townSystemEventHandler.handlePlayerJoin(event);
		} catch (EconomyPlayerException e) {
			Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
		}
		if (event.getPlayer().isOp()) {
			if (updateResult == UpdateResult.UPDATE_AVAILABLE) {
				event.getPlayer().sendMessage(ChatColor.GOLD + "There is a newer version of " + ChatColor.GREEN
						+ "Ultimate_Economy " + ChatColor.GOLD + "available!");
			}
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
