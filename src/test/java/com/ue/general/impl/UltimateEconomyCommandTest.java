package com.ue.general.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UltimateEconomyCommandTest {
	
	@Test
	public void getPluginTest() {
		Plugin plugin = mock(Plugin.class);
		UltimateEconomyCommand command = new UltimateEconomyCommand("commandName", plugin);
		assertEquals(plugin, command.getPlugin());
	}

	@Test
	public void setExecutorTest() {
		Plugin plugin = mock(Plugin.class);
		CommandExecutor executor = mock(CommandExecutor.class);
		UltimateEconomyCommand command = new UltimateEconomyCommand("commandName", plugin);
		command.setExecutor(executor);
		assertEquals(executor, command.getExecutor());
		command.setExecutor(null);
		assertEquals(plugin, command.getExecutor());
	}

	@Test
	public void setTabCompleterTest() {
		Plugin plugin = mock(Plugin.class);
		TabCompleter completer = mock(TabCompleter.class);
		UltimateEconomyCommand command = new UltimateEconomyCommand("commandName", plugin);
		command.setTabCompleter(completer);
		assertEquals(completer, command.getTabCompleter());
	}

	@Test
	public void toStringTest() {
		Plugin plugin = mock(Plugin.class);
		PluginDescriptionFile description = mock(PluginDescriptionFile.class);
		when(plugin.getDescription()).thenReturn(description);
		when(description.getFullName()).thenReturn("ultimate_economy");
		UltimateEconomyCommand command = new UltimateEconomyCommand("commandName", plugin);
		assertEquals("com.ue.general.impl.UltimateEconomyCommand(commandName, ultimate_economy)", command.toString());
	}

	@Test
	public void tabCompleteTest1() {
		Plugin plugin = mock(Plugin.class);
		TabCompleter completer = mock(TabCompleter.class);
		CommandSender sender = mock(CommandSender.class);
		UltimateEconomyCommand command = new UltimateEconomyCommand("commandName", plugin);
		command.setTabCompleter(completer);
		String[] args = new String[1];
		args[0] = "args1";
		when(completer.onTabComplete(sender, command, "alias", args)).thenReturn(Arrays.asList("result"));
		assertEquals(Arrays.asList("result"), command.tabComplete(sender, "alias", args));
	}
	
	@Test
	public void tabCompleterTest2() {
		Plugin plugin = mock(Plugin.class);
		TabCompleter completer = mock(TabCompleter.class);
		CommandSender sender = mock(CommandSender.class);
		NullPointerException e = mock(NullPointerException.class);
		UltimateEconomyCommand command = new UltimateEconomyCommand("commandName", plugin);
		command.setTabCompleter(completer);
		PluginDescriptionFile description = mock(PluginDescriptionFile.class);
		when(plugin.getDescription()).thenReturn(description);
		when(description.getFullName()).thenReturn("ultimate_economy");
		String[] args = new String[1];
		args[0] = "args1";
		when(completer.onTabComplete(sender, command, "alias", args)).thenThrow(e);
		try {
			command.tabComplete(sender, "alias", args);
		} catch (CommandException ex) {
			assertEquals("Unhandled exception during tab completion for command '/alias args1' in plugin ultimate_economy", ex.getMessage());
			assertEquals(e, ex.getCause());
		}
	}
	
	@Test
	public void tabCompleteTest3() {
		Plugin plugin = mock(Plugin.class);
		CommandExecutor executor = mock(CommandExecutor.class);
		CommandSender sender = mock(CommandSender.class);
		UltimateEconomyCommand command = new UltimateEconomyCommand("commandName", plugin);
		command.setExecutor(executor);
		String[] args = new String[0];
		assertEquals(new ArrayList<>(), command.tabComplete(sender, "alias", args));
	}
	
	@Test
	public void tabCompleteTest4() {
		Plugin plugin = mock(Plugin.class);
		CommandSender sender = mock(CommandSender.class);
		UltimateEconomyCommand command = new UltimateEconomyCommand("commandName", plugin);
		command.setExecutor(new MockExecutor());
		String[] args = new String[0];
		assertEquals(Arrays.asList("completer result"), command.tabComplete(sender, "alias", args));
	}

	@Test
	public void executeTest() {
		Plugin plugin = mock(Plugin.class);
		CommandExecutor executor = mock(CommandExecutor.class);
		CommandSender sender = mock(CommandSender.class);
		UltimateEconomyCommand command = new UltimateEconomyCommand("commandName", plugin);
		command.setExecutor(executor);
		String[] args = new String[1];
		args[0] = "args1";
		when(plugin.isEnabled()).thenReturn(false);
		assertFalse(command.execute(sender, "label", args));
	}

	@Test
	public void executeTest2() {
		Plugin plugin = mock(Plugin.class);
		CommandExecutor executor = mock(CommandExecutor.class);
		CommandSender sender = mock(CommandSender.class);
		UltimateEconomyCommand command = new UltimateEconomyCommand("commandName", plugin);
		command.setExecutor(executor);
		String[] args = new String[1];
		args[0] = "args1";
		when(plugin.isEnabled()).thenReturn(true);
		when(executor.onCommand(sender, command, "label", args)).thenReturn(true);
		assertTrue(command.execute(sender, "label", args));
	}

	@Test
	public void executeTestWithErrorOnCommand() {
		Plugin plugin = mock(Plugin.class);
		CommandExecutor executor = mock(CommandExecutor.class);
		CommandSender sender = mock(CommandSender.class);
		NullPointerException e = mock(NullPointerException.class);
		PluginDescriptionFile description = mock(PluginDescriptionFile.class);
		when(plugin.getDescription()).thenReturn(description);
		when(description.getFullName()).thenReturn("ultimate_economy");
		UltimateEconomyCommand command = new UltimateEconomyCommand("commandName", plugin);
		command.setExecutor(executor);
		String[] args = new String[1];
		args[0] = "args1";
		when(plugin.isEnabled()).thenReturn(true);
		when(executor.onCommand(sender, command, "label", args)).thenThrow(e);
		try {
			command.execute(sender, "label", args);
		} catch (CommandException ex) {
			assertEquals("Unhandled exception executing command 'label' in plugin ultimate_economy", ex.getMessage());
			assertEquals(e, ex.getCause());
		}
	}
	
	@Test
	public void executeTest3() {
		Plugin plugin = mock(Plugin.class);
		CommandExecutor executor = mock(CommandExecutor.class);
		CommandSender sender = mock(CommandSender.class);
		UltimateEconomyCommand command = new UltimateEconomyCommand("commandName", plugin);
		command.setExecutor(executor);
		command.setUsage("do this");
		String[] args = new String[1];
		args[0] = "args1";
		when(plugin.isEnabled()).thenReturn(true);
		when(executor.onCommand(sender, command, "label", args)).thenReturn(false);
		assertFalse(command.execute(sender, "label", args));
		verify(sender).sendMessage("do this");
	}
	
	@Test
	public void executeTest4() {
		Plugin plugin = mock(Plugin.class);
		CommandExecutor executor = mock(CommandExecutor.class);
		CommandSender sender = mock(CommandSender.class);
		UltimateEconomyCommand command = new UltimateEconomyCommand("commandName", plugin);
		command.setExecutor(executor);
		command.setUsage("do this");
		String[] args = new String[1];
		args[0] = "args1";
		when(plugin.isEnabled()).thenReturn(true);
		command.setPermission("stuff");
		when(sender.hasPermission("stuff")).thenReturn(false);
		assertTrue(command.execute(sender, "label", args));
	}
	
	private class MockExecutor implements TabCompleter, CommandExecutor {

		@Override
		public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
			return Arrays.asList("completer result");
		}

		@Override
		public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
			return false;
		}
		
	}
}
