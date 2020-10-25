package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
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
	public void unknownCommandTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("smonething");
		String[] args = { "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, list.size());
	}
	
	@Test
	public void shopCommandTestMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("shop");
		String[] args = { "", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, list.size());
	}
	
	@Test
	public void shopCommandTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("shop");
		String[] args = { "" };
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}
	
	@Test
	public void shopCommandTestMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("shop");
		String[] args = { "1" };
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}
	
	@Test
	public void zeroArgsTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		String[] args = { "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
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
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		String[] args = { "r" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(5, list.size());
		assertEquals("create", list.get(0));
		assertEquals("rename", list.get(1));
		assertEquals("resize", list.get(2));
		assertEquals("changeProfession", list.get(3));
		assertEquals("addSpawner", list.get(4));
	}

	@Test
	public void deleteTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		String[] args = { "delete", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void deleteTestWithMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1"));
		String[] args = { "delete", "1" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}
	
	@Test
	public void adminshopTestWithNoArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		String[] args = { };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void deleteTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		String[] args = { "delete", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void editShopTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		String[] args = { "editShop", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void editShopTestWithMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1"));
		String[] args = { "editShop", "1" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void editShopTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		String[] args = { "editShop", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void resizeTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		String[] args = { "resize", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void resizeTestWithMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1"));
		String[] args = { "resize", "1" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void resizeTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		String[] args = { "resize", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void moveTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		String[] args = { "move", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void moveTestWithMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1"));
		String[] args = { "move", "1" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void moveTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		String[] args = { "move", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void renameTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		String[] args = { "rename", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void renameTestWithMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1"));
		String[] args = { "rename", "1" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void renameTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		String[] args = { "rename", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void addSpawnerTestTwoArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		String[] args = { "addSpawner", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void addSpawnerTestWithTwoArgsMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1"));
		String[] args = { "addSpawner", "1" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void addSpawnerTestThreeArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		String[] args = { "addSpawner", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(109, list.size());
	}

	@Test
	public void addSpawnerTestWithThreeArgsMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		String[] args = { "addSpawner", "myshop1", "spide" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, list.size());
		assertEquals("spider", list.get(0));
		assertEquals("cave_spider", list.get(1));
	}

	@Test
	public void addSpawnerTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		String[] args = { "addSpawner", "myshop1", "cow", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void changeProfessionTestTwoArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1", "myshop2"));
		String[] args = { "changeProfession", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void changeProfessionTestWithTwoArgsMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop1"));
		String[] args = { "changeProfession", "1" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void changeProfessionTestThreeArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		String[] args = { "changeProfession", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(15, list.size());
	}

	@Test
	public void changeProfessionTestWithThreeArgsMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		String[] args = { "changeProfession", "myshop1", "fletch" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, list.size());
		assertEquals("fletcher", list.get(0));
	}

	@Test
	public void changeProfessionTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("adminshop");
		String[] args = { "changeProfession", "myshop1", "fletcher", "" };
		List<String> list = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, list.size());
	}
}
