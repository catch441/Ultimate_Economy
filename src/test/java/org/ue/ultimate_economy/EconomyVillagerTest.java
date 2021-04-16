package org.ue.ultimate_economy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.ue.common.logic.api.EconomyVillagerType;

public class EconomyVillagerTest {

	@Test
	public void getEnumAdminshop() {
		assertEquals(EconomyVillagerType.ADMINSHOP, EconomyVillagerType.getEnum("adminshop"));
	}
	
	@Test
	public void getEnumPlayershop() {
		assertEquals(EconomyVillagerType.PLAYERSHOP, EconomyVillagerType.getEnum("playershop"));
	}
	
	@Test
	public void getEnumRentshop() {
		assertEquals(EconomyVillagerType.RENTSHOP, EconomyVillagerType.getEnum("playershop_Rentable"));
	}
	
	@Test
	public void getEnumPlotsale() {
		assertEquals(EconomyVillagerType.PLOTSALE, EconomyVillagerType.getEnum("plotSale"));
	}
	
	@Test
	public void getEnumTownmanager() {
		assertEquals(EconomyVillagerType.TOWNMANAGER, EconomyVillagerType.getEnum("townManager"));
	}
	
	@Test
	public void getEnumJobcenter() {
		assertEquals(EconomyVillagerType.JOBCENTER, EconomyVillagerType.getEnum("jobcenter"));
	}
	
	@Test
	public void getEnumUndefined() {
		assertEquals(EconomyVillagerType.UNDEFINED, EconomyVillagerType.getEnum("kthschnll"));
	}
}
