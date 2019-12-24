package com.ue.shopsystem;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

public class Spawner {

	public static void setSpawner(EntityType type,Block block) {
		BlockState blockState = block.getState();
        CreatureSpawner spawner = ((CreatureSpawner)blockState);
        spawner.setSpawnedType(type);
        blockState.update();
	}
}
