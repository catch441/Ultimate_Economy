package com.ue.config.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConfigTabCompleterImplTest {
	
	@InjectMocks
	ConfigTabCompleterImpl tabCompleter;

	@Test
	public void zeroArgsTest() {
		String[] args = { "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(12, result.size());
		assertEquals("language", result.get(0));
		assertEquals("maxHomes", result.get(1));
		assertEquals("homes", result.get(2));
		assertEquals("maxRentedDays", result.get(3));
		assertEquals("maxJobs", result.get(4));
		assertEquals("maxJoinedTowns", result.get(5));
		assertEquals("extendedInteraction", result.get(6));
		assertEquals("maxPlayershops", result.get(7));
		assertEquals("wildernessInteraction", result.get(8));
		assertEquals("currency", result.get(9));
		assertEquals("startAmount", result.get(10));
		assertEquals("allowQuickshop", result.get(11));
	}

	@Test
	public void invalidArgTest() {
		String[] args = { "foo", "foo" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void zeroArgsTestWithMatching() {
		String[] args = { "e" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(9, result.size());
		assertEquals("language", result.get(0));
		assertEquals("maxHomes", result.get(1));
		assertEquals("homes", result.get(2));
		assertEquals("maxRentedDays", result.get(3));
		assertEquals("maxJoinedTowns", result.get(4));
		assertEquals("maxPlayershops", result.get(5));
		assertEquals("extendedInteraction", result.get(6));
		assertEquals("wildernessInteraction", result.get(7));
		assertEquals("currency", result.get(8));
	}

	@Test
	public void maxHomesArgTest() {
		String[] args = { "maxHomes" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void maxRentedDaysArgTest() {
		String[] args = { "maxRentedDays" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}
	
	@Test
	public void startAmountArgTest() {
		String[] args = { "startAmount" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void maxJobsArgTest() {
		String[] args = { "maxJobs" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void maxJoinedTownsArgTest() {
		String[] args = { "maxJoinedTowns" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void maxPlayershopsArgTest() {
		String[] args = { "maxPlayershops" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void currencyArgTest() {
		String[] args = { "currency" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}
	
	@Test
	public void allowQuickshopArgTestWithOneArg() {
		String[] args = { "allowQuickshop" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void extendedInteractionArgTestWithOneArg() {
		String[] args = { "extendedInteraction" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void wildernessInteractionArgTestWithOneArg() {
		String[] args = { "wildernessInteraction" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void homesArgTest() {
		String[] args = { "homes", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("true", result.get(0));
		assertEquals("false", result.get(1));
	}

	@Test
	public void extendedInteractionArgTest() {
		String[] args = { "extendedInteraction", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("true", result.get(0));
		assertEquals("false", result.get(1));
	}
	
	@Test
	public void allowQuickshopArgTest() {
		String[] args = { "allowQuickshop", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("true", result.get(0));
		assertEquals("false", result.get(1));
	}

	@Test
	public void wildernessInteractionArgTest() {
		String[] args = { "wildernessInteraction", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("true", result.get(0));
		assertEquals("false", result.get(1));
	}

	@Test
	public void wildernessInteractionArgTestWithMatching() {
		String[] args = { "wildernessInteraction", "t" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("true", result.get(0));
	}

	@Test
	public void extendedInteractionArgTestWithMatching() {
		String[] args = { "extendedInteraction", "f" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("false", result.get(0));
	}

	@Test
	public void homesArgTestWithMatching() {
		String[] args = { "homes", "u" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("true", result.get(0));
	}

	@Test
	public void languageArgTest() {
		String[] args = { "language", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(10, result.size());
		assertEquals("de", result.get(0));
		assertEquals("en", result.get(1));
		assertEquals("cs", result.get(2));
		assertEquals("fr", result.get(3));
		assertEquals("zh", result.get(4));
		assertEquals("ru", result.get(5));
		assertEquals("es", result.get(6));
		assertEquals("lt", result.get(7));
		assertEquals("it", result.get(8));
		assertEquals("pl", result.get(9));
	}

	@Test
	public void languageArgTestWithMatching() {
		String[] args = { "language", "e" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(3, result.size());
		assertEquals("de", result.get(0));
		assertEquals("en", result.get(1));
		assertEquals("es", result.get(2));
	}

	@Test
	public void languageCountryArgTestDefault() {
		String[] args = { "language", "de", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("DE", result.get(0));
	}

	@Test
	public void languageCountryArgTestCN() {
		String[] args = { "language", "zh", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("CN", result.get(0));
	}

	@Test
	public void languageCountryArgTestUS() {
		String[] args = { "language", "en", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("US", result.get(0));
	}

	@Test
	public void languageCountryArgTestCZ() {
		String[] args = { "language", "cs", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("CZ", result.get(0));
	}

	@Test
	public void languageCountryArgTestMatching() {
		String[] args = { "language", "cs", "C" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("CZ", result.get(0));
	}

	@Test
	public void languageArgTestWithToManyArgs() {
		String[] args = { "language", "de", "DE", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}
}
