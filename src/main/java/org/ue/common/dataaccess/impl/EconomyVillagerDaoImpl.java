package org.ue.common.dataaccess.impl;

import org.bukkit.Location;
import org.bukkit.entity.Villager.Profession;
import org.ue.common.dataaccess.api.EconomyVillagerDao;
import org.ue.common.utils.SaveFileUtils;
import org.ue.common.utils.ServerProvider;
import org.ue.townsystem.logic.TownSystemException;

public abstract class EconomyVillagerDaoImpl extends SaveFileUtils implements EconomyVillagerDao {
	
	protected final ServerProvider serverProvider;
	
	public EconomyVillagerDaoImpl(ServerProvider serverProvider) {
		this.serverProvider = serverProvider;
	}

	@Override
	public void saveLocation(Location location) {
		config.set("Location.x", location.getX());
		config.set("Location.y", location.getY());
		config.set("Location.z", location.getZ());
		config.set("Location.world", location.getWorld().getName());
		save();
	}
	
	@Override
	public void saveProfession(Profession profession) {
		config.set("Profession", profession.name());
		save();
	}
	
	@Override
	public void saveSize(int size) {
		config.set("Size", size);
		save();
	}
	
	@Override
	public Profession loadProfession() {
		addProfessionIfNotSet();
		return Profession.valueOf(config.getString("Profession"));
	}
	
	@Override
	public Location loadLocation() throws TownSystemException {
		convertToCorrectLocation();
		return new Location(serverProvider.getWorld(config.getString("Location.world")),
				config.getDouble("Location.x"), config.getDouble("Location.y"),
				config.getDouble("Location.z"));
	}
	
	@Override
	public int loadSize() {
		convertToCorrectSize();
		return config.getInt("size");
	}
	
	/**
	 * @since 1.2.7
	 */
	@Deprecated
	private void addProfessionIfNotSet() {
		if (!config.isSet("Profession")) {
			saveProfession(Profession.NITWIT);
		}
	}
	
	/**
	 * @since 1.2.7
	 */
	@Deprecated
	private void convertToCorrectSize() {
		if (config.isSet("JobCenterSize")) {
			int size = config.getInt("JobCenterSize");
			config.set("JobCenterSize",null);
			save();
			saveSize(size);
		} else if(config.isSet("ShopSize")) {
			int size = config.getInt("ShopSize");
			config.set("ShopSize",null);
			save();
			saveSize(size);
		}
	}
	
	/**
	 * @since 1.2.7
	 */
	@Deprecated
	private void convertToCorrectLocation() {
		if (config.isSet("ShopLocation.World")) {
			Location location = new Location(serverProvider.getWorld(config.getString("ShopLocation.World")),
					config.getDouble("ShopLocation.x"), config.getDouble("ShopLocation.y"),
					config.getDouble("ShopLocation.z"));
			config.set("ShopLocation", null);
			save();
			saveLocation(location);
		} else if(config.isSet("JobcenterLocation.World")) {
			Location location = new Location(serverProvider.getWorld(config.getString("JobcenterLocation.World")),
					config.getDouble("JobcenterLocation.x"), config.getDouble("JobcenterLocation.y"),
					config.getDouble("JobcenterLocation.z"));
			config.set("JobcenterLocation", null);
			save();
			saveLocation(location);
		}
	}
}