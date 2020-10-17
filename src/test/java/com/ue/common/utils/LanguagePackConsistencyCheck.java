package com.ue.common.utils;

import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
public class LanguagePackConsistencyCheck {

	@InjectMocks
	MessageWrapper messageWrapper;
	@Mock
	Logger logger;
	
	@Test
	public void checkEnUsLanguage() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
		checkKeys();
	}
	
	@Test
	public void checkCsCZLanguage() {
		messageWrapper.loadLanguage(new Locale("cs", "CZ"));
		checkKeys();
	}
	
	@Test
	public void checkdeDELanguage() {
		messageWrapper.loadLanguage(new Locale("de", "DE"));
		checkKeys();
	}
	
	@Test
	public void checkEsESLanguage() {
		messageWrapper.loadLanguage(new Locale("es", "ES"));
		checkKeys();
	}
	
	@Test
	public void checkfrFRLanguage() {
		messageWrapper.loadLanguage(new Locale("fr", "FR"));
		checkKeys();
	}
	
	@Test
	public void checkItITLanguage() {
		messageWrapper.loadLanguage(new Locale("it", "IT"));
		checkKeys();
	}
	
	@Test
	public void checkLtLTLanguage() {
		messageWrapper.loadLanguage(new Locale("lt", "LT"));
		checkKeys();
	}
	
	@Test
	public void checkPlPLLanguage() {
		messageWrapper.loadLanguage(new Locale("pl", "PL"));
		checkKeys();
	}
	
	@Test
	public void checkRuRULanguage() {
		messageWrapper.loadLanguage(new Locale("ru", "RU"));
		checkKeys();
	}
	
	@Test
	public void checkZhCNLanguage() {
		messageWrapper.loadLanguage(new Locale("zh", "CN"));
		checkKeys();
	}

	private void checkKeys() {
		assertTrue(messageWrapper.getString("entity_already_exists") != "!entity_already_exists!");
		assertTrue(messageWrapper.getString("entity_does_not_exist") != "!entity_does_not_exist!");
		assertTrue(messageWrapper.getString("item_already_exists_in_job") != "!item_already_exists_in_job!");
		assertTrue(messageWrapper.getString("item_does_not_exist_in_job") != "!item_does_not_exist_in_job!");
		assertTrue(messageWrapper.getString("job_not_exist_in_jobcenter") != "!job_not_exist_in_jobcenter!");
		assertTrue(messageWrapper.getString("job_already_exists_in_jobcenter") != "!job_already_exists_in_jobcenter!");
		assertTrue(messageWrapper.getString("loottype_already_exists") != "!loottype_already_exists!");
		assertTrue(messageWrapper.getString("loottype_does_not_exist") != "!loottype_does_not_exist!");
		assertTrue(messageWrapper.getString("plot_is_already_for_sale") != "!plot_is_already_for_sale!");
		assertTrue(messageWrapper.getString("plot_is_not_for_sale") != "!plot_is_not_for_sale!");
		assertTrue(messageWrapper.getString("chunk_is_not_connected_with_town") != "!chunk_is_not_connected_with_town!");
		assertTrue(messageWrapper.getString("chunk_already_claimed") != "!chunk_already_claimed!");
		assertTrue(messageWrapper.getString("chunk_not_claimed_by_town") != "!chunk_not_claimed_by_town!");
		assertTrue(messageWrapper.getString("chunk_not_claimed") != "!chunk_not_claimed!");
		assertTrue(messageWrapper.getString("player_is_not_mayor") != "!player_is_not_mayor!");
		assertTrue(messageWrapper.getString("player_is_already_owner") != "!player_is_already_owner!");
		assertTrue(messageWrapper.getString("player_is_not_citizen") != "!player_is_not_citizen!");
		assertTrue(messageWrapper.getString("player_is_already_citizen") != "!player_is_already_citizen!");
		assertTrue(messageWrapper.getString("player_is_already_deputy") != "!player_is_already_deputy!");
		assertTrue(messageWrapper.getString("player_is_no_deputy") != "!player_is_no_deputy!");
		assertTrue(messageWrapper.getString("player_is_already_citizen") != "!player_is_already_citizen!");
		assertTrue(messageWrapper.getString("player_is_already_resident") != "!player_is_already_resident!");
		assertTrue(messageWrapper.getString("player_is_no_resident") != "!player_is_no_resident!");
		assertTrue(messageWrapper.getString("location_not_in_town") != "!location_not_in_town!");
		assertTrue(messageWrapper.getString("town_has_not_enough_money") != "!town_has_not_enough_money!");
		assertTrue(messageWrapper.getString("townworld_does_not_exist") != "!townworld_does_not_exist!");
		assertTrue(messageWrapper.getString("townworld_already_exist") != "!townworld_already_exist!");
		assertTrue(messageWrapper.getString("world_does_not_exist", "arg1") != "!world_does_not_exist!");
		assertTrue(messageWrapper.getString("shop_changeOwner_error") != "!shop_changeOwner_error!");
		assertTrue(messageWrapper.getString("invalid_char_in_shop_name") != "!invalid_char_in_shop_name!");
		assertTrue(messageWrapper.getString("invalid_prices") != "!invalid_prices!");
		assertTrue(messageWrapper.getString("inventory_slot_empty") != "!inventory_slot_empty!");
		assertTrue(messageWrapper.getString("item_already_exists_in_shop") != "!item_already_exists_in_shop!");
		assertTrue(messageWrapper.getString("item_does_not_exist_in_shop") != "!item_does_not_exist_in_shop!");
		assertTrue(messageWrapper.getString("item_cannot_be_deleted") != "!item_cannot_be_deleted!");
		assertTrue(messageWrapper.getString("item_unavailable") != "!item_unavailable!");
		assertTrue(messageWrapper.getString("already_rented") != "!already_rented!");
		assertTrue(messageWrapper.getString("not_rented") != "!not_rented!");
		assertTrue(messageWrapper.getString("error_on_rename") != "!error_on_rename!");
		assertTrue(messageWrapper.getString("resizing_failed") != "!resizing_failed!");
		assertTrue(messageWrapper.getString("player_has_no_permission") != "!player_has_no_permission!");
		assertTrue(messageWrapper.getString("inventory_full") != "!inventory_full!");
		assertTrue(messageWrapper.getString("no_permission_set_spawner") != "!no_permission_set_spawner!");
		assertTrue(messageWrapper.getString("no_permission_break_spawner") != "!no_permission_break_spawner!");
		assertTrue(messageWrapper.getString("inventory_slot_occupied") != "!inventory_slot_occupied!");
		assertTrue(messageWrapper.getString("wilderness") != "!wilderness!");
		assertTrue(messageWrapper.getString("town_not_town_owner") != "!town_not_town_owner!");
		assertTrue(messageWrapper.getString("no_permission_on_plot") != "!no_permission_on_plot!");
		assertTrue(messageWrapper.getString("town_already_joined") != "!town_already_joined!");
		assertTrue(messageWrapper.getString("town_not_joined") != "!town_not_joined!");
		assertTrue(messageWrapper.getString("you_are_not_owner") != "!you_are_not_owner!");
		assertTrue(messageWrapper.getString("you_are_the_owner") != "!you_are_the_owner!");
		assertTrue(messageWrapper.getString("outside_of_the_plot") != "!outside_of_the_plot!");
		assertTrue(messageWrapper.getString("you_are_already_citizen") != "!you_are_already_citizen!");
		assertTrue(messageWrapper.getString("you_are_no_citizen") != "!you_are_no_citizen!");
		assertTrue(messageWrapper.getString("job_already_joined") != "!job_already_joined!");
		assertTrue(messageWrapper.getString("job_not_joined") != "!job_not_joined!");
		assertTrue(messageWrapper.getString("not_enough_money_personal") != "!not_enough_money_personal!");
		assertTrue(messageWrapper.getString("not_enough_money_non_personal") != "!not_enough_money_non_personal!");
		assertTrue(messageWrapper.getString("shopowner_not_enough_money") != "!shopowner_not_enough_money!");
		assertTrue(messageWrapper.getString("player_does_not_exist") != "!player_does_not_exist!");
		assertTrue(messageWrapper.getString("player_already_exist") != "!player_already_exist!");
		assertTrue(messageWrapper.getString("home_does_not_exist") != "!home_does_not_exist!");
		assertTrue(messageWrapper.getString("home_already_exist") != "!home_already_exist!");
		assertTrue(messageWrapper.getString("max_reached") != "!max_reached!");
		assertTrue(messageWrapper.getString("not_online") != "!not_online!");
		assertTrue(messageWrapper.getString("invalid_parameter", "arg1") != "!invalid_parameter!");
		assertTrue(messageWrapper.getString("does_not_exist", "arg1") != "!does_not_exist!");
		assertTrue(messageWrapper.getString("already_exists", "arg1") != "!already_exists!");
		assertTrue(messageWrapper.getString("not_enough_money") != "!not_enough_money!");
		assertTrue(messageWrapper.getString("rent_reminder") != "!rent_reminder!");
		assertTrue(messageWrapper.getString("bank") != "!bank!");
		assertTrue(messageWrapper.getString("restart") != "!restart!");
		assertTrue(messageWrapper.getString("shop_editItem_errorinfo") != "!shop_editItem_errorinfo!");
		assertTrue(messageWrapper.getString("profession_changed") != "!profession_changed!");
		assertTrue(messageWrapper.getString("town_plot_setForSale") != "!town_plot_setForSale!");
		assertTrue(messageWrapper.getString("town_expand") != "!town_expand!");
		assertTrue(messageWrapper.getString("rent_rented") != "!rent_rented!");
		assertTrue(messageWrapper.getString("config_change", "arg1") != "!config_change!");
		assertTrue(messageWrapper.getString("townworld_enable", "arg1") != "!townworld_enable!");
		assertTrue(messageWrapper.getString("townworld_disable", "arg1") != "!townworld_disable!");
		assertTrue(messageWrapper.getString("myjobs_info", "arg1") != "!myjobs_info!");
		assertTrue(messageWrapper.getString("joblist_info", "arg1") != "!joblist_info!");
		assertTrue(messageWrapper.getString("shoplist_info", "arg1") != "!shoplist_info!");
		assertTrue(messageWrapper.getString("got_money_with_sender", "arg1", "arg2", "arg3") != "!got_money_with_sender!");
		assertTrue(messageWrapper.getString("gave_money", "arg1", "arg2", "arg3") != "!gave_money!");
		assertTrue(messageWrapper.getString("home_info", "arg1") != "!home_info!");
		assertTrue(messageWrapper.getString("sethome", "arg1") != "!sethome!");
		assertTrue(messageWrapper.getString("delhome", "arg1") != "!delhome!");
		assertTrue(messageWrapper.getString("jobinfo_fishingprice", "arg1", "arg2", "arg3") != "!jobinfo_fishingprice!");
		assertTrue(messageWrapper.getString("jobinfo_killprice", "arg1", "arg2", "arg3") != "!jobinfo_killprice!");
		assertTrue(messageWrapper.getString("town_create", "arg1") != "!town_create!");
		assertTrue(messageWrapper.getString("town_delete", "arg1") != "!town_delete!");
		assertTrue(messageWrapper.getString("town_rename", "arg1", "arg2") != "!town_rename!");
		assertTrue(messageWrapper.getString("town_setTownSpawn", "arg1", "arg2", "arg3") != "!town_setTownSpawn!");
		assertTrue(messageWrapper.getString("town_addCoOwner", "arg1") != "!town_addCoOwner!");
		assertTrue(messageWrapper.getString("town_removeCoOwner", "arg1") != "!town_removeCoOwner!");
		assertTrue(messageWrapper.getString("shop_changeOwner1", "arg1") != "!shop_changeOwner1!");
		assertTrue(messageWrapper.getString("town_pay", "arg1", "arg2", "arg3") != "!town_pay!");
		assertTrue(messageWrapper.getString("town_bank", "arg1", "arg2") != "!town_bank!");
		assertTrue(messageWrapper.getString("money_info", "arg1", "arg2") != "!money_info!");
		assertTrue(messageWrapper.getString("got_money", "arg1", "arg2") != "!got_money!");
		assertTrue(messageWrapper.getString("townworld_setFoundationPrice", "arg1", "arg2") != "!townworld_setFoundationPrice!");
		assertTrue(messageWrapper.getString("townworld_setExpandPrice", "arg1", "arg2") != "!townworld_setExpandPrice!");
		assertTrue(messageWrapper.getString("shop_resize", "arg1") != "!shop_resize!");
		assertTrue(messageWrapper.getString("shop_rename", "arg1", "arg2") != "!shop_rename!");
		assertTrue(messageWrapper.getString("shop_addItem", "arg1") != "!shop_addItem!");
		assertTrue(messageWrapper.getString("shop_removeItem", "arg1") != "!shop_removeItem!");
		assertTrue(messageWrapper.getString("jobcenter_addItem", "arg1") != "!jobcenter_addItem!");
		assertTrue(messageWrapper.getString("jobcenter_removeItem", "arg1") != "!jobcenter_removeItem!");
		assertTrue(messageWrapper.getString("shop_addSpawner", "arg1") != "!shop_addSpawner!");
		assertTrue(messageWrapper.getString("shop_removeSpawner", "arg1") != "!shop_removeSpawner!");
		assertTrue(messageWrapper.getString("shop_create", "arg1") != "!shop_create!");
		assertTrue(messageWrapper.getString("shop_delete", "arg1") != "!shop_delete!");
		assertTrue(messageWrapper.getString("shop_changeOwner", "arg1", "arg2") != "!shop_changeOwner!");
		assertTrue(messageWrapper.getString("jobcenter_create", "arg1") != "!jobcenter_create!");
		assertTrue(messageWrapper.getString("jobcenter_delete", "arg1") != "!jobcenter_delete!");
		assertTrue(messageWrapper.getString("jobinfo_info", "arg1") != "!jobinfo_info!");
		assertTrue(messageWrapper.getString("jobcenter_removeJob", "arg1") != "!jobcenter_removeJob!");
		assertTrue(messageWrapper.getString("jobcenter_createJob", "arg1") != "!jobcenter_createJob!");
		assertTrue(messageWrapper.getString("jobcenter_delJob", "arg1") != "!jobcenter_delJob!");
		assertTrue(messageWrapper.getString("job_left", "arg1") != "!job_left!");
		assertTrue(messageWrapper.getString("job_join", "arg1") != "!job_join!");
		assertTrue(messageWrapper.getString("jobcenter_addMob", "arg1") != "!jobcenter_addMob!");
		assertTrue(messageWrapper.getString("jobcenter_removeMob", "arg1") != "!jobcenter_removeMob!");
		assertTrue(messageWrapper.getString("jobcenter_addFisher", "arg1") != "!jobcenter_addFisher!");
		assertTrue(messageWrapper.getString("jobcenter_removeFisher", "arg1") != "!jobcenter_removeFisher!");
		assertTrue(messageWrapper.getString("shop_got_item_plural", "arg1") != "!shop_got_item_plural!");
		assertTrue(messageWrapper.getString("shop_got_item_singular", "arg1") != "!shop_got_item_singular!");
		assertTrue(messageWrapper.getString("shop_added_item_plural", "arg1") != "!shop_added_item_plural!");
		assertTrue(messageWrapper.getString("shop_added_item_singular", "arg1") != "!shop_added_item_singular!");
		assertTrue(messageWrapper.getString("shop_sell_plural", "arg1", "arg2", "arg3") != "!shop_sell_plural!");
		assertTrue(messageWrapper.getString("shop_buy_plural", "arg1", "arg2", "arg3") != "!shop_buy_plural!");
		assertTrue(messageWrapper.getString("shop_buy_singular", "arg1", "arg2", "arg3") != "!shop_buy_singular!");
		assertTrue(messageWrapper.getString("shop_sell_singular", "arg1", "arg2", "arg3") != "!shop_sell_singular!");
	}
}
