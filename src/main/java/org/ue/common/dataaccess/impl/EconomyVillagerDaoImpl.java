package org.ue.common.dataaccess.impl;

import org.bukkit.Location;
import org.bukkit.entity.Villager.Profession;
import org.ue.common.dataaccess.api.EconomyVillagerDao;
import org.ue.common.utils.SaveFileUtils;
import org.ue.common.utils.ServerProvider;

public abstract class EconomyVillagerDaoImpl extends SaveFileUtils implements EconomyVillagerDao {

	protected final ServerProvider serverProvider;

	public EconomyVillagerDaoImpl(ServerProvider serverProvider) {
		this.serverProvider = serverProvider;
	}

	@Override
	public void saveLocation(String prefix, Location location) {
		prefix = prefixValidationCheck(prefix);
		config.set(prefix + "Location.x", location.getX());
		config.set(prefix + "Location.y", location.getY());
		config.set(prefix + "Location.z", location.getZ());
		config.set(prefix + "Location.world", location.getWorld().getName());
		save();
	}

	@Override
	public void saveProfession(String prefix, Profession profession) {
		prefix = prefixValidationCheck(prefix);
		config.set(prefix + "Profession", profession.name());
		save();
	}

	@Override
	public void saveSize(String prefix, int size) {
		prefix = prefixValidationCheck(prefix);
		config.set(prefix + "Size", size);
		save();
	}
	
	@Override
	public void saveVisible(String prefix, boolean visible) {
		prefix = prefixValidationCheck(prefix);
		config.set(prefix + "visible", visible);
		save();
	}

	@Override
	public boolean loadVisible(String prefix) {
		prefix = prefixValidationCheck(prefix);
		addVisibleIfNotSet(prefix);
		return config.getBoolean(prefix + "visible");
	}

	@Override
	public Profession loadProfession(String prefix) {
		prefix = prefixValidationCheck(prefix);
		addProfessionIfNotSet(prefix);
		return Profession.valueOf(config.getString(prefix + "Profession"));
	}

	@Override
	public Location loadLocation(String prefix) {
		prefix = prefixValidationCheck(prefix);
		convertToCorrectLocation(prefix);
		if(!config.isSet(prefix + "Location.world")) {
			return null;
		}
		return new Location(serverProvider.getWorld(config.getString(prefix + "Location.world")), config.getDouble(prefix + "Location.x"),
				config.getDouble(prefix + "Location.y"), config.getDouble(prefix + "Location.z"));
	}

	@Override
	public int loadSize(String prefix) {
		prefix = prefixValidationCheck(prefix);
		convertToCorrectSize(prefix);
		addSizeIfNotSet(prefix);
		return config.getInt(prefix + "Size");
	}
	
	/**
	 * @since 1.2.7
	 */
	@Deprecated
	private void addSizeIfNotSet(String prefix) {
		if (!config.isSet(prefix + "Size")) {
			saveSize(prefix, 9);
		}
	}
	
	/**
	 * @since 1.2.7
	 */
	@Deprecated
	private void addVisibleIfNotSet(String prefix) {
		if(!config.isSet(prefix + "visible")) {
			// plot
			if(prefix.contains(".Plots.")) {
				String otherPrefix = prefix.replace("SaleVillager", "isForSale");
				saveVisible(prefix, config.getBoolean(otherPrefix));
			} else {
				saveVisible(prefix, true);
			}
		}
	}

	/**
	 * @since 1.2.7
	 */
	@Deprecated
	private void addProfessionIfNotSet(String prefix) {
		if (!config.isSet(prefix + "Profession")) {
			saveProfession(prefix, Profession.NITWIT);
		}
	}

	/**
	 * @since 1.2.7
	 */
	@Deprecated
	private void convertToCorrectSize(String prefix) {
		if (config.isSet("JobCenterSize")) {
			int size = config.getInt("JobCenterSize");
			config.set("JobCenterSize", null);
			save();
			saveSize(prefix, size);
		} else if (config.isSet("ShopSize")) {
			int size = config.getInt("ShopSize");
			config.set("ShopSize", null);
			save();
			saveSize(prefix, size);
		}
	}

	/**
	 * @since 1.2.7
	 */
	@Deprecated
	private void convertToCorrectLocation(String prefix) {
		if (config.isSet("ShopLocation.World")) {
			Location location = new Location(serverProvider.getWorld(config.getString("ShopLocation.World")),
					config.getDouble("ShopLocation.x"), config.getDouble("ShopLocation.y"),
					config.getDouble("ShopLocation.z"));
			config.set("ShopLocation", null);
			save();
			saveLocation(prefix, location);
		} else if (config.isSet("JobcenterLocation.World")) {
			Location location = new Location(serverProvider.getWorld(config.getString("JobcenterLocation.World")),
					config.getDouble("JobcenterLocation.x"), config.getDouble("JobcenterLocation.y"),
					config.getDouble("JobcenterLocation.z"));
			config.set("JobcenterLocation", null);
			save();
			saveLocation(prefix, location);
		} else if(config.isSet(prefix + "x")) {
			Location location = new Location(serverProvider.getWorld(config.getString(prefix + "world")),
					config.getDouble(prefix + "x"), config.getDouble(prefix + "y"),
					config.getDouble(prefix + "z"));
			config.set(prefix + "x", null);
			config.set(prefix + "y", null);
			config.set(prefix + "z", null);
			config.set(prefix + "world", null);
			save();
			saveLocation(prefix, location);
		}
	}
	
	private String prefixValidationCheck(String prefix) {
		if(prefix == null) {
			prefix = "";
		}
		if(!prefix.isEmpty()) {
			prefix+= ".";
		}
		return prefix;
	}
}