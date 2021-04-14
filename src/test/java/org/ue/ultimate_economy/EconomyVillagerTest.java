package org.ue.ultimate_economy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.ue.general.EconomyVillager;

public class EconomyVillagerTest {

	@Test
	public void getEnumAdminshop() {
		assertEquals(EconomyVillager.ADMINSHOP, EconomyVillager.getEnum("adminshop"));
	}
	
	@Test
	public void getEnumPlayershop() {
		assertEquals(EconomyVillager.PLAYERSHOP, EconomyVillager.getEnum("playershop"));
	}
	
	@Test
	public void getEnumRentshop() {
		assertEquals(EconomyVillager.PLAYERSHOP_RENTABLE, EconomyVillager.getEnum("playershop_Rentable"));
	}
	
	@Test
	public void getEnumPlotsale() {
		assertEquals(EconomyVillager.PLOTSALE, EconomyVillager.getEnum("plotSale"));
	}
	
	@Test
	public void getEnumTownmanager() {
		assertEquals(EconomyVillager.TOWNMANAGER, EconomyVillager.getEnum("townManager"));
	}
	
	@Test
	public void getEnumJobcenter() {
		assertEquals(EconomyVillager.JOBCENTER, EconomyVillager.getEnum("jobcenter"));
	}
	
	@Test
	public void getEnumUndefined() {
		assertEquals(EconomyVillager.UNDEFINED, EconomyVillager.getEnum("kthschnll"));
	}
}
