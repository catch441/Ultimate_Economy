package com.ue.ultimate_economy;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.api.JobcenterController;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.shopsystem.adminshop.api.Adminshop;
import com.ue.shopsystem.adminshop.api.AdminshopController;
import com.ue.shopsystem.api.Shop;
import com.ue.shopsystem.impl.Spawner;
import com.ue.shopsystem.playershop.api.Playershop;
import com.ue.shopsystem.playershop.api.PlayershopController;
import com.ue.shopsystem.rentshop.api.Rentshop;
import com.ue.shopsystem.rentshop.api.RentshopController;
import com.ue.townsystem.town.api.Plot;
import com.ue.townsystem.town.api.Town;
import com.ue.townsystem.townworld.api.Townworld;
import com.ue.townsystem.townworld.api.TownworldController;
import com.ue.updater.Updater;
import com.ue.updater.Updater.UpdateResult;

public class Ultimate_EconomyEventHandler implements Listener {

	private Ultimate_Economy plugin;
	private List<String> playerlist;
	private UpdateResult updateResult;
	private List<String> spawnerlist;
	private File spawner;

	public Ultimate_EconomyEventHandler(Ultimate_Economy plugin, List<String> spawnerlist, File spawner) {
		this.plugin = plugin;
		playerlist = YamlConfiguration.loadConfiguration(EconomyPlayerController.getPlayerFile())
				.getStringList("Player");
		// version check
		updateResult = Updater.checkForUpdate(plugin.getDescription().getVersion());
		this.spawnerlist = spawnerlist;
		this.spawner = spawner;
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		TownworldController.handleTownWorldLocationCheck(event.getPlayer().getWorld().getName(),
				event.getTo().getChunk(), event.getPlayer().getName());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null) {
			Location location = event.getClickedBlock().getLocation();
			try {
				Townworld townworld = TownworldController.getTownWorldByName(location.getWorld().getName());
				if (townworld.chunkIsFree(location.getChunk())) {
					if (!event.getPlayer().hasPermission("ultimate_economy.wilderness")) {
						event.setCancelled(true);
						event.getPlayer()
								.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("wilderness"));
					}
				} else {
					Town town = townworld.getTownByChunk(location.getChunk());
					if (!event.getPlayer().hasPermission("ultimate_economy.towninteract")
							&& (!town.isPlayerCitizen(event.getPlayer().getName())
									|| !town.hasBuildPermissions(event.getPlayer().getName(), town.getPlotByChunk(
											location.getChunk().getX() + "/" + location.getChunk().getZ())))) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(
								ChatColor.RED + Ultimate_Economy.messages.getString("no_permission_on_plot"));
					}
				}
			} catch (TownSystemException e) {
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onNPCOpenInv(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		if (entity instanceof Villager && entity.hasMetadata("ue-type")) {
			String shopId = "";
			if (entity.hasMetadata("ue-id")) {
				shopId = (String) entity.getMetadata("ue-id").get(0).value();
			}
			UEVillagerType type = (UEVillagerType) entity.getMetadata("ue-type").get(0).value();
			event.setCancelled(true);
			try {
				switch (type) {
					case PLOTSALE:
						Townworld townworld = TownworldController.getTownWorldByName(entity.getWorld().getName());
						Town town = townworld.getTownByChunk(entity.getLocation().getChunk());
						Plot plot = town.getPlotByChunk(
								entity.getLocation().getChunk().getX() + "/" + entity.getLocation().getChunk().getZ());
						plot.openSaleVillagerInv(event.getPlayer());
						break;
					case TOWNMANAGER:
						Townworld townworld2 = TownworldController.getTownWorldByName(entity.getWorld().getName());
						Town town2 = townworld2.getTownByChunk(entity.getLocation().getChunk());
						town2.openTownManagerVillagerInv(event.getPlayer());
						break;
					case ADMINSHOP:
						AdminshopController.getAdminShopById(shopId).openInv(event.getPlayer());
						break;
					case PLAYERSHOP:
						PlayershopController.getPlayerShopById(shopId).openInv(event.getPlayer());
						break;
					case JOBCENTER:
						JobcenterController.getJobCenterByName(entity.getCustomName()).openInv(event.getPlayer());
						break;
					case PLAYERSHOP_RENTABLE:
						Rentshop shop = RentshopController.getRentShopById(shopId);
						if (shop.isRentable()) {
							shop.openRentGUI(event.getPlayer());
							;
						} else {
							shop.openInv(event.getPlayer());
						}
						break;
				}
			} catch (TownSystemException | ShopSystemException | JobSystemException e) {
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onHitVillagerEvent(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Villager
				&& event.getEntity().hasMetadata("ue-type")) {
			Player damager = (Player) event.getDamager();
			UEVillagerType type = (UEVillagerType) event.getEntity().getMetadata("ue-type").get(0).value();
			event.setCancelled(true);

			switch (type) {
				case PLOTSALE:
					damager.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("sale_villager_hitevent"));
					break;
				case TOWNMANAGER:
					damager.sendMessage(
							ChatColor.RED + Ultimate_Economy.messages.getString("townManager_villager_hitevent"));
					break;
				case ADMINSHOP:
				case PLAYERSHOP_RENTABLE:
				case PLAYERSHOP:
					damager.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("shop_villager_hitevent"));
					break;
				case JOBCENTER:
					damager.sendMessage(
							ChatColor.RED + Ultimate_Economy.messages.getString("jobcenter_villager_hitevent"));
					break;
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.getKiller() instanceof Player) {
			try {
				EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName(entity.getKiller().getName());
				if (!ecoPlayer.getJobList().isEmpty() && entity.getKiller().getGameMode() != GameMode.CREATIVE
						&& entity.getKiller().getGameMode() != GameMode.SPECTATOR) {
					List<Job> jobList = ecoPlayer.getJobList();
					for (Job job : jobList) {
						try {
							double d = job.getKillPrice(entity.getType().toString());
							ecoPlayer.increasePlayerAmount(d, false);
							break;
						} catch (PlayerException | JobSystemException e) {
						}
					}
				}
			} catch (PlayerException e1) {
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
			}
			else if (entity.hasMetadata("ue-type") && event.getCurrentItem() != null
					&& event.getCurrentItem().getItemMeta() != null) {
				String shopId = "";
				if (entity.hasMetadata("ue-id")) {
					shopId = (String) entity.getMetadata("ue-id").get(0).value();
				}
				UEVillagerType ueVillagerType = (UEVillagerType) entity.getMetadata("ue-type").get(0).value();
				ClickType clickType = event.getClick();
				ItemStack clickedItem = event.getCurrentItem();
				event.setCancelled(true);
				try {
					switch (ueVillagerType) {
						case PLOTSALE:
						case TOWNMANAGER:
							TownworldController.getTownWorldByName(player.getWorld().getName())
									.handleTownVillagerInvClick(event);
							player.closeInventory();
							break;
						case JOBCENTER:
							EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName(player.getName());
							String displayname = clickedItem.getItemMeta().getDisplayName();
							if (clickType == ClickType.RIGHT && displayname != null) {
								if (!ecoPlayer.getJobList().isEmpty()) {
									ecoPlayer.leaveJob(JobController.getJobByName(displayname), true);
								} else if (!displayname.equals("Info")) {
									player.sendMessage(
											ChatColor.RED + Ultimate_Economy.messages.getString("no_job_joined"));
								}
							} else if (clickType == ClickType.LEFT && displayname != null) {
								ecoPlayer.joinJob(JobController.getJobByName(displayname), true);
							}
							break;
						case PLAYERSHOP:
							Playershop playershop = PlayershopController.getPlayerShopById(shopId);
							handleShopInvClickEvent(playershop, player, event);
							break;
						case ADMINSHOP:
							Adminshop adminshop = AdminshopController.getAdminShopById(shopId);
							handleShopInvClickEvent(adminshop, player, event);
							break;
						case PLAYERSHOP_RENTABLE:
							Rentshop rentshop = RentshopController.getRentShopById(shopId);
							if (rentshop.isRentable()) {
								rentshop.handleRentShopGUIClick(event);
							} else {
								handleShopInvClickEvent(rentshop, player, event);
							}
							break;
					}
				} catch (TownSystemException | JobSystemException | ShopSystemException | IllegalArgumentException e) {
				} catch (PlayerException e) {
					player.sendMessage(ChatColor.RED + e.getMessage());
				}
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void setBlockEvent(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL
				&& event.getBlock().getBlockData().getMaterial() == Material.SPAWNER
				&& event.getItemInHand().getItemMeta().getDisplayName().contains("-")) {
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
				event.getPlayer()
						.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("no_permision_set_spawner"));
				event.setCancelled(true);
			}
		} else if (event.getPlayer().getGameMode() == GameMode.SURVIVAL
				&& !(event.getBlock().getBlockData().getMaterial() == Material.SPAWNER)) {
			event.getBlock().setMetadata("placedBy", new FixedMetadataValue(plugin, event.getPlayer().getName()));
			;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler(priority = EventPriority.HIGHEST)
	public void breakBlockEvent(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			List<MetadataValue> list = event.getBlock().getMetadata("placedBy");
			if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
				try {
					EconomyPlayer ecoPlayer = EconomyPlayerController
							.getEconomyPlayerByName(event.getPlayer().getName());
					List<Job> jobList = ecoPlayer.getJobList();
					if (!jobList.isEmpty()) {
						Material blockMaterial = event.getBlock().getBlockData().getMaterial();
						for (Job job : jobList) {
							try {
								if (blockMaterial == Material.POTATOES || blockMaterial == Material.CARROTS
										|| blockMaterial == Material.WHEAT
										|| blockMaterial == Material.NETHER_WART_BLOCK
										|| blockMaterial == Material.BEETROOTS || blockMaterial == Material.COCOA) {
									Ageable ageable = (Ageable) event.getBlock().getBlockData();
									if (ageable.getAge() == ageable.getMaximumAge()) {
										double d = job.getItemPrice(blockMaterial.toString());
										ecoPlayer.increasePlayerAmount(d, false);
									}
								} else if (list.isEmpty() || !list.isEmpty()
										&& !list.get(0).asString().contains(event.getPlayer().getName())) {
									double d = job.getItemPrice(blockMaterial.toString());
									ecoPlayer.increasePlayerAmount(d, false);
								}
								break;
							} catch (JobSystemException e) {
							} catch (PlayerException e) {
								Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
							}
						}
					}
				} catch (PlayerException e1) {
					Bukkit.getLogger().log(Level.WARNING, e1.getMessage(), e1);
				}
				if (event.getBlock().getBlockData().getMaterial() == Material.SPAWNER) {
					List<MetadataValue> blockmeta = event.getBlock().getMetadata("name");
					if (!blockmeta.isEmpty()) {
						MetadataValue s = blockmeta.get(0);
						String blockname = s.asString();
						if (event.getPlayer().getInventory().firstEmpty() == -1) {
							event.setCancelled(true);
							event.getPlayer().sendMessage(
									ChatColor.RED + Ultimate_Economy.messages.getString("no_permision_break_spawner"));
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
							event.getPlayer().sendMessage(
									ChatColor.RED + Ultimate_Economy.messages.getString("no_permision_break_spawner"));
						}
					}
				}
			} else {
				if (event.getBlock().getBlockData().getMaterial() == Material.SPAWNER) {
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
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onJoinEvent(PlayerJoinEvent event) {
		String playername = event.getPlayer().getName();
		try {
			if (!playerlist.contains(playername)) {
				playerlist.add(playername);
				EconomyPlayerController.createEconomyPlayer(playername);
			}
			EconomyPlayer economyPlayer = EconomyPlayerController.getEconomyPlayerByName(playername);
			economyPlayer.updateScoreBoard();
			economyPlayer.getBossBar().addPlayer(event.getPlayer());
			TownworldController.handleTownWorldLocationCheck(event.getPlayer().getWorld().getName(),
					event.getPlayer().getLocation().getChunk(), event.getPlayer().getName());

		} catch (PlayerException e) {
			Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
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
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFishingEvent(PlayerFishEvent event) {
		if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
			try {
				EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName(event.getPlayer().getName());
				List<Job> jobList = ecoPlayer.getJobList();
				if (!jobList.isEmpty()) {
					String lootType = "";
					Item caught = (Item) event.getCaught();
					if (caught != null) {
						switch (caught.getItemStack().getType().toString()) {
							case "COD":
							case "SALMON":
							case "TROPICAL_FISH":
							case "PUFFERFISH":
								lootType = "fish";
								break;
							case "BOW":
							case "ENCHANTED_BOOK":
							case "FISHING_ROD":
							case "NAME_TAG":
							case "NAUTILUS_SHELL":
							case "SADDLE":
							case "LILY_PAD":
								lootType = "treasure";
								break;
							default:
								lootType = "junk";
						}
						for (Job job : jobList) {
							try {
								Double price = job.getFisherPrice(lootType);
								ecoPlayer.increasePlayerAmount(price, false);
								break;
							} catch (JobSystemException e) {
							}
						}
					}
				}
			} catch (ClassCastException | PlayerException e) {
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler()
	public void onEntityTransform(EntityTransformEvent event) {
		if (event.getEntity() instanceof Villager && event.getEntity().hasMetadata("ue-type")) {
			event.setCancelled(true);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

	private void handleShopInvClickEvent(Shop shop, Player player, InventoryClickEvent event) {
		ItemMeta clickedItemMeta = event.getCurrentItem().getItemMeta();
		try {
			if (event.getView().getTitle().equals(shop.getName() + "-Editor")
					&& clickedItemMeta.getDisplayName().contains("Slot")) {
				int slot = Integer.valueOf(clickedItemMeta.getDisplayName().substring(5));
				shop.openSlotEditor(player, slot);
			} else if (event.getView().getTitle().equals(shop.getName() + "-SlotEditor")) {
				shop.handleSlotEditor(event);
				String command = clickedItemMeta.getDisplayName();
				if (command.equals(ChatColor.RED + "remove item") || command.equals(ChatColor.RED + "exit without save")
						|| command.equals(ChatColor.YELLOW + "save changes")) {
					shop.openEditor(player);
				}
			} else {
				handleBuySell(shop, event, player);
			}
		} catch (IllegalArgumentException | ShopSystemException e) {
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void handleBuySell(Shop shop, InventoryClickEvent event, Player playe) {
		event.setCancelled(true);
		boolean isPlayershop = false;
		boolean alreadysay = false;
		ClickType clickType = event.getClick();
		Inventory inventoryplayer = event.getWhoClicked().getInventory();
		Playershop playershop = null;
		if (shop instanceof Playershop) {
			isPlayershop = true;
			playershop = (Playershop) shop;
		}
		// Playershop
		if (isPlayershop && clickType == ClickType.MIDDLE && playershop.getOwner().equals(playe.getName())) {
			playershop.switchStockpile();
		} //
		else {
			for (String itemString : shop.getItemList()) {
				// only relevant for adminshop
				boolean isSpawner = false;
				// standardize the itemstack for the string
				ItemStack clickedItemReal = event.getCurrentItem();
				ItemStack clickedItem = new ItemStack(clickedItemReal);
				ItemMeta itemMeta = clickedItem.getItemMeta();
				if (itemMeta.hasLore()) {
					List<String> loreList = itemMeta.getLore();
					Iterator<String> loreIter = loreList.iterator();
					while (loreIter.hasNext()) {
						String lore = loreIter.next();
						if (lore.contains(" buy for ") || lore.contains(" sell for ")) {
							loreIter.remove();
						}
					}
					itemMeta.setLore(loreList);
					clickedItem.setItemMeta(itemMeta);
				}
				clickedItem.setAmount(1);
				String clickedItemString = clickedItem.toString();
				if (itemString.contains("blockMaterial=SPAWNER")) {
					isSpawner = true;
				}
				if (itemString.equals(clickedItemString)) {
					try {
						double sellprice = shop.getItemSellPrice(itemString);
						double buyprice = shop.getItemBuyPrice(itemString);
						int amount = shop.getItemAmount(itemString);
						EconomyPlayer playerShopOwner = null;
						if (isPlayershop) {
							playerShopOwner = EconomyPlayerController.getEconomyPlayerByName(playershop.getOwner());
						}
						EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName(playe.getName());
						if (clickType == ClickType.LEFT) {
							if (buyprice != 0.0 && ecoPlayer.hasEnoughtMoney(buyprice)
									|| (isPlayershop && playe.getName().equals(playerShopOwner.getName()))) {
								if (!isPlayershop || playershop.isAvailable(clickedItemString)) {

									if (inventoryplayer.firstEmpty() != -1) {

										// only adminshop
										if (isSpawner) {
											ItemStack stack = new ItemStack(Material.SPAWNER, amount);
											ItemMeta meta = stack.getItemMeta();
											meta.setDisplayName(
													clickedItem.getItemMeta().getDisplayName() + "-" + playe.getName());
											stack.setItemMeta(meta);
											inventoryplayer.addItem(stack);
											ecoPlayer.decreasePlayerAmount(buyprice, true);
											if (amount > 1) {
												playe.sendMessage(
														ChatColor.GREEN + String.valueOf(amount) + " " + ChatColor.GOLD
																+ Ultimate_Economy.messages.getString("shop_buy_plural")
																+ " " + ChatColor.GREEN + buyprice + ChatColor.GREEN
																+ "$" + ChatColor.GOLD + ".");
											} else {
												playe.sendMessage(
														ChatColor.GREEN + String.valueOf(amount) + " " + ChatColor.GOLD
																+ Ultimate_Economy.messages
																		.getString("shop_buy_singular")
																+ " " + ChatColor.GREEN + buyprice + ChatColor.GREEN
																+ "$" + ChatColor.GOLD + ".");
											}
										} //
										else if (!isSpawner) {

											ItemStack itemStack = shop.getItemStack(itemString);
											if (isPlayershop) {
												playershop.decreaseStock(itemString, amount);
												// if the player is in stockpile mode, then the stockpile gets refreshed
												playershop.setupStockpile();
											}
											itemStack.setAmount(amount);
											inventoryplayer.addItem(itemStack);
											if (!isPlayershop || !playerShopOwner.getName().equals(playe.getName())) {
												ecoPlayer.decreasePlayerAmount(buyprice, true);

												// only playershop
												if (isPlayershop) {
													playerShopOwner.increasePlayerAmount(buyprice, false);
												}
												if (amount > 1) {
													playe.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " "
															+ ChatColor.GOLD
															+ Ultimate_Economy.messages.getString("shop_buy_plural")
															+ " " + ChatColor.GREEN + buyprice + ChatColor.GREEN + "$"
															+ ChatColor.GOLD + ".");
												} else {
													playe.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " "
															+ ChatColor.GOLD
															+ Ultimate_Economy.messages.getString("shop_buy_singular")
															+ " " + ChatColor.GREEN + buyprice + ChatColor.GREEN + "$"
															+ ChatColor.GOLD + ".");
												}
											}
											// only playershop
											else if (isPlayershop
													&& playerShopOwner.getName().equals(playe.getName())) {
												if (amount > 1) {
													playe.sendMessage(ChatColor.GOLD
															+ Ultimate_Economy.messages
																	.getString("shop_got_item_plural1")
															+ " " + ChatColor.GREEN + String.valueOf(amount)
															+ ChatColor.GOLD + " " + Ultimate_Economy.messages
																	.getString("shop_got_item_plural2"));
												} else {
													playe.sendMessage(ChatColor.GOLD
															+ Ultimate_Economy.messages
																	.getString("shop_got_item_singular1")
															+ " " + ChatColor.GREEN + String.valueOf(amount)
															+ ChatColor.GOLD + " " + Ultimate_Economy.messages
																	.getString("shop_got_item_singular2"));
												}
											}
											break;
										}
									} else {
										playe.sendMessage(
												ChatColor.RED + Ultimate_Economy.messages.getString("inventory_full"));
									}
								}
								// only playershop
								else if (isPlayershop) {
									playe.sendMessage(
											ChatColor.GOLD + Ultimate_Economy.messages.getString("item_unavailable"));
								}
							} else if (!ecoPlayer.hasEnoughtMoney(buyprice) && !alreadysay) {
								playe.sendMessage(ChatColor.RED
										+ Ultimate_Economy.messages.getString("not_enough_money_personal"));
								alreadysay = true;
							}
						} else if (clickType == ClickType.RIGHT && !itemString.contains("ANVIL_0")
								&& !itemString.contains("CRAFTING_TABLE_0") && sellprice != 0.0
								|| clickType == ClickType.RIGHT && isPlayershop
										&& playe.getName().equals(playerShopOwner.getName())
										&& inventoryplayer.containsAtLeast(clickedItem, amount)) {

							ItemStack itemStack = shop.getItemStack(itemString);
							itemStack.setAmount(amount);
							if (inventoryContainsItems(inventoryplayer, itemStack, amount)) {
								if (isPlayershop && !playerShopOwner.getName().equals(playe.getName())
										|| !isPlayershop) {
									if (!isPlayershop || (isPlayershop && playerShopOwner.hasEnoughtMoney(sellprice))) {
										ecoPlayer.increasePlayerAmount(sellprice, false);
										// only playershop
										if (isPlayershop) {
											playerShopOwner.decreasePlayerAmount(sellprice, false);
											playershop.increaseStock(clickedItemString, amount);
										}
										if (amount > 1) {
											playe.sendMessage(
													ChatColor.GREEN + String.valueOf(amount) + " " + ChatColor.GOLD
															+ Ultimate_Economy.messages.getString("shop_sell_plural")
															+ " " + ChatColor.GREEN + sellprice + ChatColor.GREEN + "$"
															+ ChatColor.GOLD + ".");
										} else {
											playe.sendMessage(
													ChatColor.GREEN + String.valueOf(amount) + " " + ChatColor.GOLD
															+ Ultimate_Economy.messages.getString("shop_sell_singular")
															+ " " + ChatColor.GREEN + sellprice + ChatColor.GREEN + "$"
															+ ChatColor.GOLD + ".");
										}
										removeItemFromInventory(inventoryplayer, itemStack, amount);
									}
									// only playershop
									else if (isPlayershop) {
										playe.sendMessage(ChatColor.RED
												+ Ultimate_Economy.messages.getString("shopowner_not_enough_money"));
									}
								}
								// only playershop
								else if (isPlayershop) {
									if (amount > 1) {
										playe.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("shop_added_item_plural1") + " "
												+ ChatColor.GREEN + String.valueOf(amount) + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_added_item_plural2"));
									} else {
										playe.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("shop_added_item_singular1") + " "
												+ ChatColor.GREEN + String.valueOf(amount) + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_added_item_singular2"));
									}
									playershop.increaseStock(clickedItemString, amount);
									// if the player is in stockpile mode, then the stockpile gets refreshed
									playershop.setupStockpile();
									removeItemFromInventory(inventoryplayer, itemStack, amount);
								}
								break;
							}
						} else if (clickType == ClickType.SHIFT_RIGHT && sellprice != 0.0
								|| clickType == ClickType.SHIFT_RIGHT && isPlayershop
										&& playe.getName().equals(playerShopOwner.getName())
										&& inventoryplayer.containsAtLeast(clickedItem, amount)) {
							ItemStack itemStack = shop.getItemStack(itemString);
							if (inventoryContainsItems(inventoryplayer, itemStack, 1)) {
								ItemStack[] i = inventoryplayer.getStorageContents();
								int itemAmount = 0;
								double iA = 0.0;
								double newprice = 0;
								for (ItemStack is1 : i) {
									if (is1 != null) {
										ItemStack is = new ItemStack(is1);
										itemStack.setAmount(is.getAmount());
										if (is.toString().equals(itemStack.toString())) {
											itemAmount = itemAmount + is.getAmount();
										}
									}
								}
								iA = Double.valueOf(String.valueOf(itemAmount));
								newprice = sellprice / amount * iA;
								if (isPlayershop && !playerShopOwner.getName().equals(playe.getName())
										|| !isPlayershop) {
									if ((isPlayershop && playerShopOwner.hasEnoughtMoney(newprice)) || !isPlayershop) {
										if (itemAmount > 1) {
											playe.sendMessage(
													ChatColor.GREEN + String.valueOf(itemAmount) + " " + ChatColor.GOLD
															+ Ultimate_Economy.messages.getString("shop_sell_plural")
															+ " " + ChatColor.GREEN + newprice + ChatColor.GREEN + "$"
															+ ChatColor.GOLD + ".");
										} else {
											playe.sendMessage(
													ChatColor.GREEN + String.valueOf(itemAmount) + " " + ChatColor.GOLD
															+ Ultimate_Economy.messages.getString("shop_sell_singular")
															+ " " + ChatColor.GREEN + newprice + ChatColor.GREEN + "$"
															+ ChatColor.GOLD + ".");
										}
										ecoPlayer.increasePlayerAmount(newprice, false);
										// only playershop
										if (isPlayershop) {
											playerShopOwner.decreasePlayerAmount(newprice, false);
											playershop.increaseStock(clickedItemString, amount);
										}
										itemStack.setAmount(itemAmount);
										removeItemFromInventory(inventoryplayer, itemStack, itemAmount);
									}
									// only playershop
									else if (isPlayershop) {
										playe.sendMessage(ChatColor.RED
												+ Ultimate_Economy.messages.getString("shopowner_not_enough_money"));
									}
								}
								// only playershop
								else if (isPlayershop) {
									if (itemAmount > 1) {
										playe.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("shop_added_item_plural1") + " "
												+ ChatColor.GREEN + String.valueOf(itemAmount) + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_added_item_plural2"));
									} else {
										playe.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("shop_added_item_singular1") + " "
												+ ChatColor.GREEN + String.valueOf(itemAmount) + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("shop_added_item_singular2"));
									}
									playershop.increaseStock(clickedItemString, itemAmount);
									// if the player is in stockpile mode, then the stockpile gets refreshed
									playershop.setupStockpile();
									itemStack.setAmount(itemAmount);
									removeItemFromInventory(inventoryplayer, itemStack, itemAmount);
								}

								break;
							}
						}
					} catch (PlayerException | ShopSystemException e) {
						Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
					}
				}
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void removeItemFromInventory(Inventory inventory, ItemStack item, int amount) {
		int amount2 = amount;
		int amountStack = 0;
		int repairCosts = 0;
		boolean isRepairable = false;
		for (ItemStack s : inventory.getStorageContents()) {
			if (s != null) {
				ItemStack stack = new ItemStack(s);
				Repairable repairable = (Repairable) stack.getItemMeta();
				item.setAmount(1);
				amountStack = stack.getAmount();
				stack.setAmount(1);
				if (item.equals(stack) && amount2 != 0) {

					if (isRepairable) {
						repairable.setRepairCost(repairCosts);
						stack.setItemMeta((ItemMeta) repairable);
					}
					if (amount2 >= amountStack) {
						stack.setAmount(amountStack);
						inventory.removeItem(stack);
						amount2 -= amountStack;
					} else {
						stack.setAmount(amount2);
						inventory.removeItem(stack);
						amount2 -= amount2;
					}
				}
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean inventoryContainsItems(Inventory inventory, ItemStack item, int amount) {
		boolean bool = false;
		int realAmount = 0;
		int amountStack = 0;
		for (ItemStack s : inventory.getStorageContents()) {
			if (s != null) {
				ItemStack stack = new ItemStack(s);
				item.setAmount(1);
				amountStack = stack.getAmount();
				stack.setAmount(1);
				if (item.equals(stack)) {
					realAmount += amountStack;
				}
			}
		}
		if (realAmount >= amount) {
			bool = true;
		}
		return bool;
	}

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
