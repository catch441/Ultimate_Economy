package com.ue.shopsystem.logic.api;

import java.util.List;

import org.bukkit.Location;

import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.logic.impl.ShopSystemException;

public interface AdminshopManager {

	/**
	 * This method returns a AdminShop by it's name.
	 * 
	 * @param name
	 * @return adminshop
	 * @throws GeneralEconomyException
	 */
	public Adminshop getAdminShopByName(String name) throws GeneralEconomyException;
	
	/**
	 * This method returns a AdminShop by it's id.
	 * 
	 * @param id
	 * @return adminshop
	 * @throws GeneralEconomyException
	 */
	public Adminshop getAdminShopById(String id) throws GeneralEconomyException;
	
	/**
	 * Returns the list of adminshop ids.
	 * 
	 * @return List of Strings
	 */
	public List<String> getAdminshopIdList();
	
	/**
	 * Returns a list of all adminshop names.
	 * 
	 * @return list of adminshop names
	 */
	public List<String> getAdminshopNameList();
	
	/**
	 * Returns the adminshop list.
	 * 
	 * @return list of adminshops
	 */
	public List<Adminshop> getAdminshopList();
	
	/**
	 * Returns a free unique id for a adminshop.
	 * 
	 * @return String
	 */
	public String generateFreeAdminShopId();
	
	/**
	 * This method should be used to create a new adminshop.
	 * 
	 * @param name
	 * @param spawnLocation
	 * @param size
	 * @throws ShopSystemException
	 * @throws GeneralEconomyException
	 */
	public void createAdminShop(String name, Location spawnLocation, int size)
			throws ShopSystemException, GeneralEconomyException;
	
	/**
	 * This method should be used to delete a adminshop.
	 * 
	 * @param adminshop
	 */
	public void deleteAdminShop(Adminshop adminshop);
	
	/**
	 * This method despawns all adminshop villager.
	 */
	public void despawnAllVillagers();
	
	/**
	 * This method loads all adminShops.
	 * 
	 */
	public void loadAllAdminShops();
}
