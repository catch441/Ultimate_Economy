package org.ue.spawnersystem.logic.impl;

import java.util.List;

import javax.inject.Inject;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.spawnersystem.logic.api.SpawnerManager;
import org.ue.spawnersystem.logic.api.SpawnersystemEventHandler;

public class SpawnerystemEventHandlerImpl implements SpawnersystemEventHandler {

	private final MessageWrapper messageWrapper;
	private final ServerProvider serverProvider;
	private final SpawnerManager spawnerManager;

	@Inject
	public SpawnerystemEventHandlerImpl(MessageWrapper messageWrapper, ServerProvider serverProvider,
			SpawnerManager spawnerManager) {
		this.messageWrapper = messageWrapper;
		this.serverProvider = serverProvider;
		this.spawnerManager = spawnerManager;
	}

	@Override
	public void handleInventoryClick(InventoryClickEvent event) {
		if (event.getCurrentItem() != null && event.getInventory().getType() == InventoryType.ANVIL
				&& event.getCurrentItem().getType() == Material.SPAWNER) {
			event.setCancelled(true);
		}
	}

	@Override
	public void handleBreakBlockEvent(BlockBreakEvent event) {
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
				event.getPlayer().sendMessage(messageWrapper.getErrorString(ExceptionMessageEnum.INVENTORY_FULL));
			} else if (event.getPlayer().getName().equals(blockname)) {
				if (!event.getBlock().getMetadata("entity").isEmpty()) {
					spawnerManager.removeSpawner(event.getBlock().getLocation());
					ItemStack stack = serverProvider.createItemStack(Material.SPAWNER, 1);
					ItemMeta meta = stack.getItemMeta();
					meta.setDisplayName(event.getBlock().getMetadata("entity").get(0).asString() + "-"
							+ event.getPlayer().getName());
					stack.setItemMeta(meta);
					event.getPlayer().getInventory().addItem(stack);
				}
			} else {
				event.setCancelled(true);
				event.getPlayer()
						.sendMessage(messageWrapper.getErrorString(ExceptionMessageEnum.YOU_HAVE_NO_PERMISSION));
			}
		}
	}

	private void handleBreakSpawnerInCreative(BlockBreakEvent event) {
		if (!event.getBlock().getMetadata("entity").isEmpty()) {
			spawnerManager.removeSpawner(event.getBlock().getLocation());
		}
	}

	@Override
	public void handleSetBlockEvent(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			if (event.getBlock().getBlockData().getMaterial() == Material.SPAWNER
					&& event.getItemInHand().getItemMeta().getDisplayName().contains("-")) {
				handleSetSpawner(event);
			}
		}
	}

	private void handleSetSpawner(BlockPlaceEvent event) {
		String spawnerowner = event.getItemInHand().getItemMeta().getDisplayName()
				.substring(event.getItemInHand().getItemMeta().getDisplayName().lastIndexOf("-") + 1);
		if (spawnerowner.equals(event.getPlayer().getName())) {
			String string = event.getItemInHand().getItemMeta().getDisplayName();
			BlockState blockState = event.getBlock().getState();
			CreatureSpawner spawner = ((CreatureSpawner) blockState);
			spawner.setSpawnedType(EntityType.valueOf(string.substring(0, string.lastIndexOf("-"))));
			blockState.update();
			event.getBlock().setMetadata("name", new FixedMetadataValue(serverProvider.getJavaPluginInstance(),
					string.substring(string.lastIndexOf("-") + 1)));
			event.getBlock().setMetadata("entity", new FixedMetadataValue(serverProvider.getJavaPluginInstance(),
					string.substring(0, string.lastIndexOf("-"))));
			spawnerManager.addSpawner(string.substring(0, string.lastIndexOf("-")), event.getPlayer(),
					event.getBlock().getLocation());
		} else {
			event.getPlayer().sendMessage(messageWrapper.getErrorString(ExceptionMessageEnum.YOU_HAVE_NO_PERMISSION));
			event.setCancelled(true);
		}
	}
}
