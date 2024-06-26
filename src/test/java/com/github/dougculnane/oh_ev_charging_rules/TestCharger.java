package com.github.dougculnane.oh_ev_charging_rules;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openhab.automation.jrule.items.JRuleDateTimeItem;
import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.user.items.MockDateTimeItem;
import org.openhab.automation.jrule.rules.user.items.MockJRuleNumberItem;
import org.openhab.automation.jrule.rules.user.items.MockStringItem;
import org.openhab.automation.jrule.rules.user.items.MockSwitchItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

import com.github.dougculnane.oh_ev_charging_rules.Charger.MODE_VALUE;
import com.github.dougculnane.oh_ev_charging_rules.Charger.RULE_NAME;

import org.openhab.automation.jrule.rules.user.OpenHabEnvironment;

@RunWith(MockitoJUnitRunner.class)
class TestCharger {
	
	@Test
	void testChargerInit_FastMode() {
		Charger charger1 = getTestCharger(1);
		assertFastMode(charger1);
	}
		
	@Test
	void testChargerModeSwitching() {
		Charger charger1 = getTestCharger(1);
		charger1.handleMode("OFF");
		assertOffMode(charger1);
		charger1.handleMode("FAST");
		assertFastMode(charger1);	
		charger1.handleMode("RULES");
		assertRulesMode(charger1);
		charger1.handleMode("FAST");
		assertFastMode(charger1);
		charger1.handleMode("RULES");
		assertRulesMode(charger1);
		charger1.handleMode("OFF");
		assertOffMode(charger1);
	}
	
	@Test
	void testChargerModeRulesNoneActivated() {
		Charger charger1 = getTestCharger(1);
		// Rules mode.
		charger1.handleMode("RULES");
		assertRulesMode(charger1);
		charger1.handlePolling();
		charger1.enableRule(RULE_NAME.USE_EXPORT.toString());
		assertOff(charger1);
	}
	
	@Test
	void testChargerModeRulesUSE_EXPORTActivated_StreadySunUp() {
		Charger charger1 = getTestCharger(1);
		charger1.activateFastCharging();
		// Rules mode.
		charger1.handleMode("OFF");
		charger1.enableRule(RULE_NAME.USE_EXPORT.toString());
		assertOffMode(charger1);
		charger1.handlePolling();
		charger1.handleMode("RULES");
		sendPVData(charger1, 100, false, 3, 16);
		sendPVData(charger1, 1000, false, 3, 16);
		sendPVData(charger1, 1500, false, 3, 16);
		sendPVData(charger1, 1800, true, 1, 7);
		sendPVData(charger1, 1500, true, 1, 6);
		sendPVData(charger1, 2000, true, 1 , 8);
		sendPVData(charger1, 2200, true, 1 , 9);
		sendPVData(charger1, 2400, true, 1 , 10);
		sendPVData(charger1, 2600, true, 1 , 10);
		sendPVData(charger1, 2800, true, 1 , 11);
		sendPVData(charger1, 3000, true, 1 , 12);
		sendPVData(charger1, 3200, true, 1 , 13);
		sendPVData(charger1, 3400, true, 1 , 14);
		sendPVData(charger1, 3600, true, 1 , 15);
		sendPVData(charger1, 3800, true, 1 , 15);
		sendPVData(charger1, 4000, true, 1 , 16);
		sendPVData(charger1, 4500, true, 1 , 16);
		sendPVData(charger1, 5000, true, 1 , 16);
		sendPVData(charger1, 5500, true, 1 , 16);
		sendPVData(charger1, 6000, true, 1 , 16);
		sendPVData(charger1, 6300, true, 3 , 8);
		sendPVData(charger1, 6500, true, 3 , 9);
		sendPVData(charger1, 7000, true, 3 , 9);
		sendPVData(charger1, 7500, true, 3 , 10);
		sendPVData(charger1, 8000, true, 3 , 11);
		sendPVData(charger1, 8500, true, 3 , 11);
		sendPVData(charger1, 9000, true, 3 , 12);
		sendPVData(charger1, 9500, true, 3 , 13);
		sendPVData(charger1, 10000, true, 3 , 13);
		sendPVData(charger1, 10500, true, 3 , 14);
		sendPVData(charger1, 11000, true, 3 , 15);
		sendPVData(charger1, 11500, true, 3 , 15);
		sendPVData(charger1, 12000, true, 3 , 16);
		sendPVData(charger1, 12500, true, 3 , 16);
		sendPVData(charger1, 13000, true, 3 , 16);
		sendPVData(charger1, 12500, true, 3 , 16);
		sendPVData(charger1, 12000, true, 3 , 16);
		sendPVData(charger1, 12000, true, 3 , 16);
		sendPVData(charger1, 11500, true, 3 , 15);
		sendPVData(charger1, 10500, true, 3 , 14);
		sendPVData(charger1, 10000, true, 3 , 13);
		sendPVData(charger1, 9000, true, 3 , 12);
		sendPVData(charger1, 8500, true, 3 , 11);
		sendPVData(charger1, 7500, true, 3 , 10);
		sendPVData(charger1, 7000, true, 3 , 9);
		sendPVData(charger1, 6000, true, 3 , 8);
		sendPVData(charger1, 5500, true, 1 , 16);
		sendPVData(charger1, 5000, true, 1 , 16);
		sendPVData(charger1, 4500, true, 1 , 16);
		sendPVData(charger1, 4000, true, 1 , 16);
		sendPVData(charger1, 3800, true, 1 , 15);
		sendPVData(charger1, 3800, true, 1 , 15);
		sendPVData(charger1, 3600, true, 1 , 15);
		sendPVData(charger1, 3400, true, 1 , 14);
		sendPVData(charger1, 3200, true, 1 , 13);
		sendPVData(charger1, 3000, true, 1 , 12);
		sendPVData(charger1, 2800, true, 1 , 11);
		sendPVData(charger1, 2600, true, 1 , 10);
		sendPVData(charger1, 2400, true, 1 , 10);
		sendPVData(charger1, 2200, true, 1 , 9);
		sendPVData(charger1, 2000, true, 1 , 8);
		sendPVData(charger1, 1800, true, 1, 7);
		sendPVData(charger1, 1500, true, 1, 6);
		sendPVData(charger1, 1000, false, 1, 6);	
		sendPVData(charger1, 100, false, 1, 6);
		
		
		charger1.disableRule(RULE_NAME.USE_EXPORT.toString());
		charger1.handlePolling();
		assertNull(charger1.getActiveRule());
	}
	
	@Test
	void testChargerModeRulesPVActivated_PhaseTransition() {
		Charger charger1 = getTestCharger(1);
		charger1.activateFastCharging();
		// Rules mode.
		charger1.handleMode("OFF");
		charger1.enableRule(RULE_NAME.USE_EXPORT.toString());
		assertOffMode(charger1);
		charger1.handlePolling();
		charger1.handleMode("RULES");
		charger1.handlePolling();
		sendPVData(charger1, 1000, false, 3, 16);
		sendPVData(charger1, 1440, false, 3, 16); // still of switching phases.
		sendPVData(charger1, 1500, false, 3, 16);
		sendPVData(charger1, 1700, true, 1, 7);
		sendPVData(charger1, 1500, true, 1, 6);
		sendPVData(charger1, 1950, true, 1, 8);
		sendPVData(charger1, 3200, true, 1 , 13);
		sendPVData(charger1, 4500, true, 1 , 16);  
		sendPVData(charger1, 4500, true, 1 , 16);
		sendPVData(charger1, 5000, true, 1 , 16);
		sendPVData(charger1, 5500, true, 1 , 16);
		sendPVData(charger1, 6000, true, 1 , 16);
		sendPVData(charger1, 6300, true, 3 , 8); // test flip flop.
		sendPVData(charger1, 6000, true, 3 , 8);
		sendPVData(charger1, 5800, true, 3 , 8);
		sendPVData(charger1, 6500, true, 3 , 9);
		sendPVData(charger1, 5500, true, 1 , 16);
		sendPVData(charger1, 5800, true, 1 , 16);
		sendPVData(charger1, 6100, true, 3 , 8);
	    // TODO deal with flutter between phases.
	}
	
	@Test
	void testChargerModeRulesCheapActivated() {

		Charger charger1 = getTestCharger(1);
		// Rules mode.
		charger1.handleMode("FAST");
		charger1.enableRule(RULE_NAME.CHEAP.toString());
		charger1.enableRule(RULE_NAME.USE_EXPORT.toString());
		assertFastMode(charger1);
		charger1.handlePolling();
		charger1.getExportPowerItem().sendCommand(-12000);
		charger1.getCheapPowerPriceItem().sendCommand(8.08);
		charger1.getGridPowerPriceItem().sendCommand(20.20);
		charger1.handlePolling();
		assertFastMode(charger1);
		charger1.handleMode("RULES");
		charger1.handlePolling();
		assertOff(charger1);
		charger1.getGridPowerPriceItem().sendCommand(15.15);
		charger1.handlePolling();
		assertOff(charger1);
		
		//do a bit of solar then stop
		sendPVData(charger1, 1500, false, 3, 16); 
		sendPVData(charger1, 1700, true, 1, 7);
		sendPVData(charger1, 1500, true, 1, 6);
		sendPVData(charger1, 1700, true, 1, 7);
		sendPVData(charger1, 4500, true, 1, 16);
		sendPVData(charger1, 2400, true, 1, 10);
		sendPVData(charger1, 500, false, 1, 10);
		assertNull(charger1.getActiveRule());
		
		assertOff(charger1);
		charger1.getGridPowerPriceItem().sendCommand(7.07);
		charger1.handlePolling();
		assertFast(charger1);
		charger1.getExportPowerItem().sendCommand(charger1.getFastChargeRate() * -1);
		assertEquals(RULE_NAME.CHEAP, charger1.getActiveRule());
		charger1.getGridPowerPriceItem().sendCommand(15.15);
		charger1.handlePolling();
		assertNull(charger1.getActiveRule());
		assertOff(charger1);
		charger1.getGridPowerPriceItem().sendCommand(-0.10);
		charger1.handlePolling();
		assertFast(charger1);
		charger1.getGridPowerPriceItem().sendCommand(15.15);
		charger1.handlePolling();
		assertOff(charger1);
		
		//do a bit of solar then stop
		sendPVData(charger1, 1000, false, 3, 16);
		sendPVData(charger1, 1500, false, 3, 16);
		sendPVData(charger1, 1700, true, 1, 7);
		sendPVData(charger1, 4500, true, 1, 16);
		sendPVData(charger1, 2400, true, 1, 10);
		sendPVData(charger1, 5000, true, 1, 16);
		assertEquals(RULE_NAME.USE_EXPORT, charger1.getActiveRule());
		charger1.getGridPowerPriceItem().sendCommand(-0.10);
		charger1.handlePolling();
		assertFast(charger1);
		assertEquals(RULE_NAME.CHEAP, charger1.getActiveRule());
	}
	
	@Test
	void testEnableRules() {
		Charger charger1 = getTestCharger(1);
		assertFalse(charger1.isRuleEnabled(RULE_NAME.BEST_GRID));
		assertFalse(charger1.isRuleEnabled(RULE_NAME.CHEAP));
		assertFalse(charger1.isRuleEnabled(RULE_NAME.TARGET));
		assertFalse(charger1.isRuleEnabled(RULE_NAME.TIMER));
		assertFalse(charger1.isRuleEnabled(RULE_NAME.USE_EXPORT));
		charger1.enableRule(RULE_NAME.BEST_GRID.toString());
		charger1.enableRule(RULE_NAME.CHEAP.toString());
		charger1.enableRule(RULE_NAME.TARGET.toString());
		charger1.enableRule(RULE_NAME.TIMER.toString());
		charger1.enableRule(RULE_NAME.USE_EXPORT.toString());
		assertTrue(charger1.isRuleEnabled(RULE_NAME.BEST_GRID));
		assertTrue(charger1.isRuleEnabled(RULE_NAME.CHEAP));
		assertTrue(charger1.isRuleEnabled(RULE_NAME.TARGET));
		assertTrue(charger1.isRuleEnabled(RULE_NAME.TIMER));
		assertTrue(charger1.isRuleEnabled(RULE_NAME.USE_EXPORT));
	}
	
	@Test
	void testChargerModeRulesBestPrice() {

		Charger charger1 = getTestCharger(1);
		// Rules mode.
		charger1.handleMode("FAST");
		charger1.handleMode("OFF");
		charger1.enableRule(RULE_NAME.BEST_GRID.toString());
		charger1.enableRule(RULE_NAME.USE_EXPORT.toString());
		assertOffMode(charger1);
		charger1.handleMode("RULES");
		sendPVData(charger1, 1500, false, 3, 16);
		sendPVData(charger1, 1700, true, 1, 7);
		sendPVData(charger1, 1500, true, 1, 6);
		assertEquals(RULE_NAME.USE_EXPORT, charger1.getActiveRule());
		Calendar cal = Calendar.getInstance();
		charger1.getBestGridStartItem().sendCommand(cal.getTime());
		cal.add(Calendar.MINUTE, 5);
		charger1.getBestGridFinishItem().sendCommand(cal.getTime());
		charger1.handlePolling();
		assertEquals(RULE_NAME.BEST_GRID, charger1.getActiveRule());
		assertFast(charger1);
	}
	
	@Test
	void testChargerModeRulesCheapOverridesExported() {
		JRuleNumberItem cheapPowerPrice = new MockJRuleNumberItem("evcr_cheap_power_price");
		cheapPowerPrice.sendCommand(10.0);
		Charger charger1 = getTestCharger(1);
		// Rules mode.
		charger1.handleMode("FAST");
		charger1.enableRule(RULE_NAME.CHEAP.toString());
		assertFastMode(charger1);
		charger1.handlePolling();
		charger1.getCheapPowerPriceItem().sendCommand(0.08);
		charger1.getGridPowerPriceItem().sendCommand(0.20);
		charger1.handlePolling();
		assertFastMode(charger1);
		charger1.handleMode("RULES");
		assertOff(charger1);
		charger1.getGridPowerPriceItem().sendCommand(0.15);
		charger1.handlePolling();
		assertOff(charger1);
		charger1.getGridPowerPriceItem().sendCommand(0.08);
		charger1.handlePolling();
		assertOff(charger1);
		charger1.getGridPowerPriceItem().sendCommand(0.07);
		charger1.handlePolling();
		assertFast(charger1);
		charger1.getGridPowerPriceItem().sendCommand(0.15);
		charger1.handlePolling();
		assertOff(charger1);
		charger1.getGridPowerPriceItem().sendCommand(-0.10);
		charger1.handlePolling();
		assertFast(charger1);
	}
	
	@Test
	void testTimer() {
		Charger charger = getTestCharger(3);
		// Rules mode.
		charger.handleMode("FAST");
		charger.enableRule(RULE_NAME.TIMER.toString());
		assertFastMode(charger);
		charger.handlePolling();
		charger.getTimerStartItem().sendCommand("02:00");
		charger.getTimerFinishItem().sendCommand("02:00");
		charger.handlePolling();
		assertFastMode(charger);
		charger.handleMode("RULES");
		assertOff(charger);
		Calendar cal = Calendar.getInstance();
		charger.getTimerStartItem().sendCommand(cal.get(Calendar.HOUR_OF_DAY) + ":00");
		cal.add(Calendar.HOUR_OF_DAY,1);
		charger.getTimerFinishItem().sendCommand(cal.get(Calendar.HOUR_OF_DAY) + ":00");
		charger.handlePolling();
		assertFast(charger);
		assertEquals(RULE_NAME.TIMER, charger.getActiveRule());
	}
	
	@Test
	void test1PhaseOnlyExport() {
		Charger charger = getTestCharger(1);
		charger.enableRule(RULE_NAME.USE_EXPORT.toString());
		assertFalse(charger.isExport1PhaseOnly());
		
		charger.getExport1PhaseOnlySwitchItem().sendCommand(JRuleOnOffValue.ON);
		assertEquals(JRuleOnOffValue.ON, charger.getExport1PhaseOnlySwitchItem().getStateAsOnOff());
		assertTrue(charger.isExport1PhaseOnly());
		
		charger.handleMode("FAST");
		assertFastMode(charger);
		charger.enableRule(RULE_NAME.USE_EXPORT.toString());
		charger.handleMode("OFF");
		charger.handleMode("RULES");
		sendPVData(charger, 1700, true, 1, 7);
		sendPVData(charger, 1500, true, 1, 6);
		sendPVData(charger, 1950, true, 1, 8);
		sendPVData(charger, 3200, true, 1 , 13);
		sendPVData(charger, 4500, true, 1 , 16);  
		sendPVData(charger, 4500, true, 1 , 16);
		sendPVData(charger, 10000, true, 1 , 16);
		sendPVData(charger, 20000, true, 1 , 16);
		charger.handleMode("FAST");
		assertFastMode(charger);
		charger.handleMode("OFF");
		assertOffMode(charger);

	}
	
	private Charger getTestCharger(int number) {
		final JRuleNumberItem gridPower = new MockJRuleNumberItem("evcr_export_power");
		final JRuleNumberItem gridPowerPrice = new MockJRuleNumberItem("evcr_grid_power_price");
		final JRuleNumberItem cheapPowerPrice = new MockJRuleNumberItem("evcr_cheap_power_price");
		final JRuleSwitchItem mainSwitch = new MockSwitchItem("evcr_charger_" + number + "_switch");
		final JRuleNumberItem amps = new MockJRuleNumberItem("evcr_charger_" + number + "_amps");
		final JRuleNumberItem phases = new MockJRuleNumberItem("evcr_charger_" + number + "_phases");
		final JRuleNumberItem chargePower = new MockJRuleNumberItem("evcr_charger_" + number + "_power");
		final JRuleStringItem mode = new MockStringItem("evcr_charger_" + number + "_mode");
		final JRuleStringItem activeRule = new MockStringItem("evcr_charger_" + number + "_active_rule");
		final JRuleSwitchItem rule_CHEAP_switch	= new MockSwitchItem("evcr_charger_" + number + "_CHEAP_switch");
		final JRuleSwitchItem rule_USE_EXPORT_switch = new MockSwitchItem("evcr_charger_" + number + "_USE_EXPORT_switch");
		final JRuleSwitchItem rule_TARGET_switch = new MockSwitchItem("evcr_charger_" + number + "_TARGET_switch");
		final JRuleSwitchItem rule_TIMER_switch	= new MockSwitchItem("evcr_charger_" + number + "_TIMER_switch");
		final JRuleStringItem evcr_charger_TIMER_start = new MockStringItem("evcr_charger_" + number + "_TIMER_start");
		final JRuleStringItem evcr_charger_TIMER_finish = new MockStringItem("evcr_charger_" + number + "_TIMER_finish");
		final JRuleSwitchItem rule_BEST_GRID_switch = new MockSwitchItem("evcr_charger_" + number + "_BEST_GRID_switch");	
		final JRuleDateTimeItem evcr_charger_BEST_GRID_start = new MockDateTimeItem("evcr_charger_" + number + "_BEST_GRID_start");
		final JRuleDateTimeItem evcr_charger_BEST_GRID_finish = new MockDateTimeItem("evcr_charger_" + number + "_BEST_GRID_finish");
		final JRuleSwitchItem export_1phase_switch	= new MockSwitchItem("evcr_charger_" + number + "_EXPORT_1phase_switch");
		OpenHabEnvironment mock =  Mockito.mock(OpenHabEnvironment.class);
		Mockito.when(mock.getNumberItem(gridPower.getName())).thenReturn(gridPower);
		Mockito.when(mock.getNumberItem(gridPowerPrice.getName())).thenReturn(gridPowerPrice);
		Mockito.when(mock.getNumberItem(cheapPowerPrice.getName())).thenReturn(cheapPowerPrice);
		Mockito.when(mock.getSwitchItem(mainSwitch.getName())).thenReturn(mainSwitch);
		Mockito.when(mock.getNumberItem(amps.getName())).thenReturn(amps);
		Mockito.when(mock.getNumberItem(phases.getName())).thenReturn(phases);
		Mockito.when(mock.getNumberItem(chargePower.getName())).thenReturn(chargePower);
		Mockito.when(mock.getStringItem(mode.getName())).thenReturn(mode);
		Mockito.when(mock.getStringItem(activeRule.getName())).thenReturn(activeRule);
		Mockito.when(mock.getSwitchItem(rule_CHEAP_switch.getName())).thenReturn(rule_CHEAP_switch);
		Mockito.when(mock.getSwitchItem(rule_USE_EXPORT_switch.getName())).thenReturn(rule_USE_EXPORT_switch);
		Mockito.when(mock.getSwitchItem(rule_TARGET_switch.getName())).thenReturn(rule_TARGET_switch);
		Mockito.when(mock.getSwitchItem(rule_TIMER_switch.getName())).thenReturn(rule_TIMER_switch);
		Mockito.when(mock.getSwitchItem(rule_BEST_GRID_switch.getName())).thenReturn(rule_BEST_GRID_switch);
		Mockito.when(mock.getStringItem(evcr_charger_TIMER_start.getName())).thenReturn(evcr_charger_TIMER_start);
		Mockito.when(mock.getStringItem(evcr_charger_TIMER_finish.getName())).thenReturn(evcr_charger_TIMER_finish);
		Mockito.when(mock.getDateTimeItem(evcr_charger_BEST_GRID_start.getName())).thenReturn(evcr_charger_BEST_GRID_start);
		Mockito.when(mock.getDateTimeItem(evcr_charger_BEST_GRID_finish.getName())).thenReturn(evcr_charger_BEST_GRID_finish);
		Mockito.when(mock.getSwitchItem(export_1phase_switch.getName())).thenReturn(export_1phase_switch);		
		return new GoeCharger_API2(mock, number);
	}
	
	private void sendPVData(Charger charger, double gridPower, boolean isOn, int phases, int  amps) {
		// update charger state we are ready fast for test.
		if (charger.isOn()) {
			charger.getChargingPowerItem().sendCommand(charger.getAmps() * charger.getPhases() * 240);
		} else {
			charger.getChargingPowerItem().sendCommand(0);
		}
		charger.getExportPowerItem().sendCommand(gridPower - getChargingPower(charger));
		charger.handlePolling();
		assertEquals(isOn, charger.isOn());
		if (charger.isOn()) {
			assertTrue(charger.getActiveRule() == RULE_NAME.USE_EXPORT);
		} else {
			//assertTrue(charger.getActiveRule() != RULE_NAME.USE_EXPORT);
		}
		assertEquals(phases, charger.getPhases());
		assertEquals(amps, charger.getAmps());
	}
	
	private double getChargingPower(Charger charger) {
		Double chargeingPower = charger.getChargingPower();
		if (chargeingPower != null) {
			return chargeingPower;
		}
		return 0;
	}

	private void assertFastMode(Charger charger) {
		assertEquals(MODE_VALUE.FAST, charger.getMode());
		assertFast(charger);
	}
	
	private void assertFast(Charger charger) {
		assertTrue(charger.isOn());
		assertFalse(charger.isOff());
		assertEquals("evcr_charger_" + charger.number, charger.getName());
		assertEquals(16, charger.getAmps());
		assertEquals(3, charger.getPhases());
		charger.getChargingPowerItem().sendCommand(charger.getAmps() * charger.getPhases() * 240);
	}
	
	private void assertOffMode(Charger charger) {
		assertEquals(MODE_VALUE.OFF, charger.getMode());
		assertOff(charger);
	}
	
	private void assertOff(Charger charger) {
		assertFalse(charger.isOn());
		assertTrue(charger.isOff());
		assertEquals("evcr_charger_" + charger.number, charger.getName());
		charger.getChargingPowerItem().sendCommand(0);
	}
	
	private void assertRulesMode(Charger charger) {
		assertEquals(MODE_VALUE.RULES, charger.getMode());
		assertEquals("evcr_charger_" + charger.number, charger.getName());
	}

}