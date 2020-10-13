package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.shopsystem.logic.api.AdminshopManager;

@ExtendWith(MockitoExtension.class)
public class AdminshopTabCompleterImplTest {
	
	@InjectMocks
	AdminshopTabCompleterImpl tabCompleter;
	@Mock
	AdminshopManager adminshopManager;

	@Test
	public void zeroArgsTest() {
		String[] args = { "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(8, list.size());
		assertEquals("create", list.get(0));
		assertEquals("delete", list.get(1));
		assertEquals("move", list.get(2));
		assertEquals("editShop", list.get(3));
		assertEquals("rename", list.get(4));
		assertEquals("resize", list.get(5));
		assertEquals("changeProfession", list.get(6));
		assertEquals("addSpawner", list.get(7));
	}

	@Test
	public void zeroArgsTestWithMatching() {
		String[] args = { "r" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(5, list.size());
		assertEquals("create", list.get(0));
		assertEquals("rename", list.get(1));
		assertEquals("resize", list.get(2));
		assertEquals("changeProfession", list.get(3));
		assertEquals("addSpawner", list.get(4));
	}

	@Test
	public void deleteTest() {
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		String[] args = { "delete", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void deleteTestWithMatching() {
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1"));
		String[] args = { "delete", "1" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void deleteTestWithMoreArgs() {
		String[] args = { "delete", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void editShopTest() {
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		String[] args = { "editShop", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void editShopTestWithMatching() {
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1"));
		String[] args = { "editShop", "1" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void editShopTestWithMoreArgs() {
		String[] args = { "editShop", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void resizeTest() {
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		String[] args = { "resize", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void resizeTestWithMatching() {
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1"));
		String[] args = { "resize", "1" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void resizeTestWithMoreArgs() {
		String[] args = { "resize", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void moveTest() {
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		String[] args = { "move", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void moveTestWithMatching() {
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1"));
		String[] args = { "move", "1" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void moveTestWithMoreArgs() {
		String[] args = { "move", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void renameTest() {
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		String[] args = { "rename", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void renameTestWithMatching() {
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1"));
		String[] args = { "rename", "1" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void renameTestWithMoreArgs() {
		String[] args = { "rename", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void addSpawnerTestTwoArgs() {
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		String[] args = { "addSpawner", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void addSpawnerTestWithTwoArgsMatching() {
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1"));
		String[] args = { "addSpawner", "1" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void addSpawnerTestThreeArgs() {
		String[] args = { "addSpawner", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(109, list.size());
	}

	@Test
	public void addSpawnerTestWithThreeArgsMatching() {
		String[] args = { "addSpawner", "myshop1", "spide" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("spider", list.get(0));
		assertEquals("cave_spider", list.get(1));
	}

	@Test
	public void addSpawnerTestWithMoreArgs() {
		String[] args = { "addSpawner", "myshop1", "cow", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void changeProfessionTestTwoArgs() {
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		String[] args = { "changeProfession", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void changeProfessionTestWithTwoArgsMatching() {
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1"));
		String[] args = { "changeProfession", "1" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void changeProfessionTestThreeArgs() {
		String[] args = { "changeProfession", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(15, list.size());
	}

	@Test
	public void changeProfessionTestWithThreeArgsMatching() {
		String[] args = { "changeProfession", "myshop1", "fletch" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("fletcher", list.get(0));
	}

	@Test
	public void changeProfessionTestWithMoreArgs() {
		String[] args = { "changeProfession", "myshop1", "fletcher", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}
}
