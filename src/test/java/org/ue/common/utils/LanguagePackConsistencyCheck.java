package org.ue.common.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LanguagePackConsistencyCheck {

	MessageWrapperImpl messageWrapper = new MessageWrapperImpl();
	
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
		assertFalse(messageWrapper.getString("deleted", "args1").equals("!deleted!"));
		assertFalse(messageWrapper.getString("created", "args1").equals("!created!"));
		assertFalse(messageWrapper.getString("added", "args1").equals("!added!"));
		assertFalse(messageWrapper.getString("removed", "args1").equals("!removed!"));
		assertFalse(messageWrapper.getString("job_not_exist_in_jobcenter").equals("!job_not_exist_in_jobcenter!"));
		assertFalse(messageWrapper.getString("job_already_exists_in_jobcenter").equals("!job_already_exists_in_jobcenter!"));
		assertFalse(messageWrapper.getString("plot_is_already_for_sale").equals("!plot_is_already_for_sale!"));
		assertFalse(messageWrapper.getString("plot_is_not_for_sale").equals("!plot_is_not_for_sale!"));
		assertFalse(messageWrapper.getString("chunk_is_not_connected_with_town").equals("!chunk_is_not_connected_with_town!"));
		assertFalse(messageWrapper.getString("chunk_already_claimed").equals("!chunk_already_claimed!"));
		assertFalse(messageWrapper.getString("chunk_not_claimed_by_town").equals("!chunk_not_claimed_by_town!"));
		assertFalse(messageWrapper.getString("chunk_not_claimed").equals("!chunk_not_claimed!"));
		assertFalse(messageWrapper.getString("player_is_not_citizen").equals("!player_is_not_citizen!"));
		assertFalse(messageWrapper.getString("player_is_already_deputy").equals("!player_is_already_deputy!"));
		assertFalse(messageWrapper.getString("player_is_already_owner").equals("!player_is_already_owner!"));
		assertFalse(messageWrapper.getString("player_is_no_deputy").equals("!player_is_no_deputy!"));
		assertFalse(messageWrapper.getString("player_is_already_resident").equals("!player_is_already_resident!"));
		assertFalse(messageWrapper.getString("player_is_no_resident").equals("!player_is_no_resident!"));
		assertFalse(messageWrapper.getString("location_not_in_town").equals("!location_not_in_town!"));
		assertFalse(messageWrapper.getString("town_has_not_enough_money").equals("!town_has_not_enough_money!"));
		assertFalse(messageWrapper.getString("townworld_does_not_exist").equals("!townworld_does_not_exist!"));
		assertFalse(messageWrapper.getString("townworld_already_exist").equals("!townworld_already_exist!"));
		assertFalse(messageWrapper.getString("world_does_not_exist", "arg1").equals("!world_does_not_exist!"));
		assertFalse(messageWrapper.getString("shop_changeOwner_error").equals("!shop_changeOwner_error!"));
		assertFalse(messageWrapper.getString("invalid_char_in_shop_name").equals("!invalid_char_in_shop_name!"));
		assertFalse(messageWrapper.getString("invalid_prices").equals("!invalid_prices!"));
		assertFalse(messageWrapper.getString("item_already_exists_in_shop").equals("!item_already_exists_in_shop!"));
		assertFalse(messageWrapper.getString("item_does_not_exist_in_shop").equals("!item_does_not_exist_in_shop!"));
		assertFalse(messageWrapper.getString("item_cannot_be_deleted").equals("!item_cannot_be_deleted!"));
		assertFalse(messageWrapper.getString("item_unavailable").equals("!item_unavailable!"));
		assertFalse(messageWrapper.getString("already_rented").equals("!already_rented!"));
		assertFalse(messageWrapper.getString("not_rented").equals("!not_rented!"));
		assertFalse(messageWrapper.getString("error_on_rename").equals("!error_on_rename!"));
		assertFalse(messageWrapper.getString("resizing_failed").equals("!resizing_failed!"));
		assertFalse(messageWrapper.getString("you_have_no_permission").equals("!you_have_no_permission!"));
		assertFalse(messageWrapper.getString("inventory_full").equals("!inventory_full!"));
		assertFalse(messageWrapper.getString("wilderness").equals("!wilderness!"));
		assertFalse(messageWrapper.getString("no_permission_on_plot").equals("!no_permission_on_plot!"));
		assertFalse(messageWrapper.getString("town_already_joined").equals("!town_already_joined!"));
		assertFalse(messageWrapper.getString("town_not_joined").equals("!town_not_joined!"));
		assertFalse(messageWrapper.getString("you_are_not_owner").equals("!you_are_not_owner!"));
		assertFalse(messageWrapper.getString("you_are_the_owner").equals("!you_are_the_owner!"));
		assertFalse(messageWrapper.getString("outside_of_the_plot").equals("!outside_of_the_plot!"));
		assertFalse(messageWrapper.getString("you_are_already_citizen").equals("!you_are_already_citizen!"));
		assertFalse(messageWrapper.getString("you_are_no_citizen").equals("!you_are_no_citizen!"));
		assertFalse(messageWrapper.getString("job_already_joined").equals("!job_already_joined!"));
		assertFalse(messageWrapper.getString("job_not_joined").equals("!job_not_joined!"));
		assertFalse(messageWrapper.getString("not_enough_money_personal").equals("!not_enough_money_personal!"));
		assertFalse(messageWrapper.getString("not_enough_money_non_personal").equals("!not_enough_money_non_personal!"));
		assertFalse(messageWrapper.getString("shopowner_not_enough_money").equals("!shopowner_not_enough_money!"));
		assertFalse(messageWrapper.getString("max_reached").equals("!max_reached!"));
		assertFalse(messageWrapper.getString("not_online").equals("!not_online!"));
		assertFalse(messageWrapper.getString("invalid_parameter", "arg1").equals("!invalid_parameter!"));
		assertFalse(messageWrapper.getString("does_not_exist", "arg1").equals("!does_not_exist!"));
		assertFalse(messageWrapper.getString("already_exists", "arg1").equals("!already_exists!"));
		assertFalse(messageWrapper.getString("not_enough_money").equals("!not_enough_money!"));
		assertFalse(messageWrapper.getString("rent_reminder").equals("!rent_reminder!"));
		assertFalse(messageWrapper.getString("bank").equals("!bank!"));
		assertFalse(messageWrapper.getString("restart").equals("!restart!"));
		assertFalse(messageWrapper.getString("profession_changed").equals("!profession_changed!"));
		assertFalse(messageWrapper.getString("town_plot_setForSale").equals("!town_plot_setForSale!"));
		assertFalse(messageWrapper.getString("town_expand").equals("!town_expand!"));
		assertFalse(messageWrapper.getString("rent_rented").equals("!rent_rented!"));
		assertFalse(messageWrapper.getString("config_change", "arg1").equals("!config_change!"));
		assertFalse(messageWrapper.getString("townworld_enable", "arg1").equals("!townworld_enable!"));
		assertFalse(messageWrapper.getString("townworld_disable", "arg1").equals("!townworld_disable!"));
		assertFalse(messageWrapper.getString("myjobs_info", "arg1").equals("!myjobs_info!"));
		assertFalse(messageWrapper.getString("joblist_info", "arg1").equals("!joblist_info!"));
		assertFalse(messageWrapper.getString("shoplist_info", "arg1").equals("!shoplist_info!"));
		assertFalse(messageWrapper.getString("got_money_with_sender", "arg1", "arg2", "arg3").equals("!got_money_with_sender!"));
		assertFalse(messageWrapper.getString("gave_money", "arg1", "arg2", "arg3").equals("!gave_money!"));
		assertFalse(messageWrapper.getString("home_info", "arg1").equals("!home_info!"));
		assertFalse(messageWrapper.getString("jobinfo_fishingprice", "arg1", "arg2", "arg3").equals("!jobinfo_fishingprice!"));
		assertFalse(messageWrapper.getString("jobinfo_killprice", "arg1", "arg2", "arg3").equals("!jobinfo_killprice!"));
		assertFalse(messageWrapper.getString("jobinfo_breedprice", "arg1", "arg2", "arg3").equals("!jobinfo_breedprice!"));
		assertFalse(messageWrapper.getString("town_create", "arg1").equals("!town_create!"));
		assertFalse(messageWrapper.getString("town_delete", "arg1").equals("!town_delete!"));
		assertFalse(messageWrapper.getString("town_rename", "arg1", "arg2").equals("!town_rename!"));
		assertFalse(messageWrapper.getString("town_setTownSpawn", "arg1", "arg2", "arg3").equals("!town_setTownSpawn!"));
		assertFalse(messageWrapper.getString("shop_changeOwner1", "arg1").equals("!shop_changeOwner1!"));
		assertFalse(messageWrapper.getString("town_pay", "arg1", "arg2", "arg3").equals("!town_pay!"));
		assertFalse(messageWrapper.getString("town_bank", "arg1", "arg2").equals("!town_bank!"));
		assertFalse(messageWrapper.getString("money_info", "arg1", "arg2").equals("!money_info!"));
		assertFalse(messageWrapper.getString("got_money", "arg1", "arg2").equals("!got_money!"));
		assertFalse(messageWrapper.getString("townworld_setFoundationPrice", "arg1", "arg2").equals("!townworld_setFoundationPrice!"));
		assertFalse(messageWrapper.getString("townworld_setExpandPrice", "arg1", "arg2").equals("!townworld_setExpandPrice!"));
		assertFalse(messageWrapper.getString("shop_resize", "arg1").equals("!shop_resize!"));
		assertFalse(messageWrapper.getString("shop_rename", "arg1", "arg2").equals("!shop_rename!"));
		assertFalse(messageWrapper.getString("shop_changeOwner", "arg1", "arg2").equals("!shop_changeOwner!"));
		assertFalse(messageWrapper.getString("jobinfo_info", "arg1").equals("!jobinfo_info!"));
		assertFalse(messageWrapper.getString("job_left", "arg1").equals("!job_left!"));
		assertFalse(messageWrapper.getString("job_join", "arg1").equals("!job_join!"));
		assertFalse(messageWrapper.getString("shop_got_item_plural", "arg1").equals("!shop_got_item_plural!"));
		assertFalse(messageWrapper.getString("shop_got_item_singular", "arg1").equals("!shop_got_item_singular!"));
		assertFalse(messageWrapper.getString("shop_added_item_plural", "arg1").equals("!shop_added_item_plural!"));
		assertFalse(messageWrapper.getString("shop_added_item_singular", "arg1").equals("!shop_added_item_singular!"));
		assertFalse(messageWrapper.getString("shop_sell_plural", "arg1", "arg2", "arg3").equals("!shop_sell_plural!"));
		assertFalse(messageWrapper.getString("shop_buy_plural", "arg1", "arg2", "arg3").equals("!shop_buy_plural!"));
		assertFalse(messageWrapper.getString("shop_buy_singular", "arg1", "arg2", "arg3").equals("!shop_buy_singular!"));
		assertFalse(messageWrapper.getString("shop_sell_singular", "arg1", "arg2", "arg3").equals("!shop_sell_singular!"));
	}
}
