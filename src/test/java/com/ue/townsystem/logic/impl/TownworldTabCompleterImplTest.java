package com.ue.townsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.ServerProvider;

@ExtendWith(MockitoExtension.class)
public class TownworldTabCompleterImplTest {

	@InjectMocks
	TownworldTabCompleterImpl tabCompleter;
	@Mock
	ServerProvider serverProvider;
	
	@Test
	public void zeroArgsTest() {
		String[] args = { "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(4, list.size());
		assertEquals("enable", list.get(0));
		assertEquals("disable", list.get(1));
		assertEquals("setFoundationPrice", list.get(2));
		assertEquals("setExpandPrice", list.get(3));
	}
	
	@Test
	public void zeroArgsTestWithMatching() {
		String[] args = { "set" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("setFoundationPrice", list.get(0));
		assertEquals("setExpandPrice", list.get(1));
	}
	
	@Test
	public void moreArgsTestWithInvalidArg() {
		String[] args = { "bla", "other" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}
	
	@Test
	public void enableTest() {
		World world1 = mock(World.class);
		World world2 = mock(World.class);
		when(world2.getName()).thenReturn("world2");
		when(world1.getName()).thenReturn("world1");
		when(serverProvider.getWorlds()).thenReturn(Arrays.asList(world1, world2));
		String[] args = { "enable", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("world1", list.get(0));
		assertEquals("world2", list.get(1));
	}
	
	@Test
	public void enableTestWithMatching() {
		World world1 = mock(World.class);
		World world2 = mock(World.class);
		when(world2.getName()).thenReturn("world2");
		when(world1.getName()).thenReturn("world1");
		when(serverProvider.getWorlds()).thenReturn(Arrays.asList(world1, world2));
		String[] args = { "enable", "2" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("world2", list.get(0));
	}
	
	@Test
	public void enableTestWithMoreArgs() {
		String[] args = { "enable", "world", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}
	
	@Test
	public void disableTest() {
		World world1 = mock(World.class);
		World world2 = mock(World.class);
		when(world2.getName()).thenReturn("world2");
		when(world1.getName()).thenReturn("world1");
		when(serverProvider.getWorlds()).thenReturn(Arrays.asList(world1, world2));
		String[] args = { "disable", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("world1", list.get(0));
		assertEquals("world2", list.get(1));
	}
	
	@Test
	public void disableTestWithMatching() {
		World world1 = mock(World.class);
		World world2 = mock(World.class);
		when(world2.getName()).thenReturn("world2");
		when(world1.getName()).thenReturn("world1");
		when(serverProvider.getWorlds()).thenReturn(Arrays.asList(world1, world2));
		String[] args = { "disable", "2" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("world2", list.get(0));
	}
	
	@Test
	public void disableTestWithMoreArgs() {
		String[] args = { "disable", "world", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}
	
	@Test
	public void setFoundationPriceTest() {
		World world1 = mock(World.class);
		World world2 = mock(World.class);
		when(world2.getName()).thenReturn("world2");
		when(world1.getName()).thenReturn("world1");
		when(serverProvider.getWorlds()).thenReturn(Arrays.asList(world1, world2));
		String[] args = { "setFoundationPrice", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("world1", list.get(0));
		assertEquals("world2", list.get(1));
	}
	
	@Test
	public void setFoundationPriceTestWithMatching() {
		World world1 = mock(World.class);
		World world2 = mock(World.class);
		when(world2.getName()).thenReturn("world2");
		when(world1.getName()).thenReturn("world1");
		when(serverProvider.getWorlds()).thenReturn(Arrays.asList(world1, world2));
		String[] args = { "setFoundationPrice", "2" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("world2", list.get(0));
	}
	
	@Test
	public void setFoundationPriceTestWithMoreArgs() {
		String[] args = { "setFoundationPrice", "world", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}
	
	@Test
	public void setExpandPriceTest() {
		World world1 = mock(World.class);
		World world2 = mock(World.class);
		when(world2.getName()).thenReturn("world2");
		when(world1.getName()).thenReturn("world1");
		when(serverProvider.getWorlds()).thenReturn(Arrays.asList(world1, world2));
		String[] args = { "setExpandPrice", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("world1", list.get(0));
		assertEquals("world2", list.get(1));
	}
	
	@Test
	public void setExpandPriceTestWithMatching() {
		World world1 = mock(World.class);
		World world2 = mock(World.class);
		when(world2.getName()).thenReturn("world2");
		when(world1.getName()).thenReturn("world1");
		when(serverProvider.getWorlds()).thenReturn(Arrays.asList(world1, world2));
		String[] args = { "setExpandPrice", "2" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("world2", list.get(0));
	}
	
	@Test
	public void setExpandPriceTestWithMoreArgs() {
		String[] args = { "setExpandPrice", "world", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}
}
