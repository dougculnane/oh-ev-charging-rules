package org.openhab.automation.jrule.rules.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.user.items.MockJRuleNumberItem;
import org.openhab.automation.jrule.rules.user.items.MockStringItem;
import org.openhab.automation.jrule.rules.user.items.MockSwitchItem;
import org.openhab.automation.jrule.rules.user.Charger.MODE_VALUE;
import org.openhab.automation.jrule.rules.user.Charger.RULE_NAME;

@RunWith(MockitoJUnitRunner.class)
class TestCharger {
	
	@Test
	void testChargerInit_FastMode() {
		Charger charger1 = getTestCharger(1);
		assertFastMode(charger1, 1);
	}
		
	@Test
	void testChargerModeSwitching() {
		Charger charger1 = getTestCharger(1);
		charger1.handleMode("OFF");
		assertOffMode(charger1, 1);
		charger1.handleMode("FAST");
		assertFastMode(charger1, 1);	
		charger1.handleMode("RULES");
		assertRulesMode(charger1, 1);
		charger1.handleMode("FAST");
		assertFastMode(charger1, 1);
		charger1.handleMode("RULES");
		assertRulesMode(charger1, 1);
		charger1.handleMode("OFF");
		assertOffMode(charger1, 1);
	}
	
	@Test
	void testChargerModeRulesNoneActivated() {
		Charger charger1 = getTestCharger(1);
		// Rules mode.
		charger1.handleMode("RULES");
		assertRulesMode(charger1, 1);
		charger1.handlePolling();
		charger1.enableRule(RULE_NAME.USE_EXPORT.toString());
		assertOff(charger1, 1);
	}
	
	@Test
	void testChargerModeRulesUSE_EXPORTActivated_StreadySunUp() {
		Charger charger1 = getTestCharger(1);
		// Rules mode.
		charger1.handleMode("OFF");
		charger1.enableRule(RULE_NAME.USE_EXPORT.toString());
		assertOffMode(charger1, 1);
		charger1.handlePolling();
		charger1.handleMode("RULES");
		sendPVData(charger1, 100, false, 1, 8);
		sendPVData(charger1, 1000, false, 1, 8);
		sendPVData(charger1, 1500, false, 1, 8);
		sendPVData(charger1, 1800, false, 1, 8);
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
		sendPVData(charger1, 6000, true, 3 , 8);
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
		sendPVData(charger1, 5500, true, 1 , 8);
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
		sendPVData(charger1, 1800, false, 1, 8);
		sendPVData(charger1, 1500, false, 1, 8);
		sendPVData(charger1, 1000, false, 1, 8);	
		sendPVData(charger1, 100, false, 1, 8);
		
		
		charger1.disableRule(RULE_NAME.USE_EXPORT.toString());
		charger1.handlePolling();
		assertNull(charger1.getActiveRule());
	}
	
	@Test
	void testChargerModeRulesPVActivated_PhaseTransition() {
		Charger charger1 = getTestCharger(1);
		// Rules mode.
		charger1.handleMode("OFF");
		charger1.enableRule(RULE_NAME.USE_EXPORT.toString());
		assertOffMode(charger1, 1);
		charger1.handlePolling();
		charger1.handleMode("RULES");
		sendPVData(charger1, 1500, false, 1 , 8);  // switching phases so still off.
		sendPVData(charger1, 1500, false, 1, 8);
		sendPVData(charger1, 3200, true, 1 , 13);
		sendPVData(charger1, 4500, true, 1 , 16);  
		sendPVData(charger1, 4500, true, 1 , 16);
		sendPVData(charger1, 5000, true, 1 , 16);
		sendPVData(charger1, 5500, true, 1 , 16);
		sendPVData(charger1, 6000, true, 3 , 8);
		sendPVData(charger1, 6500, true, 3 , 9);
		sendPVData(charger1, 5500, true, 1 , 9);
		sendPVData(charger1, 5800, true, 3 , 8);
	    // TODO deal with flutter between phases.
	}
	
	@Test
	void testChargerModeRulesCheapActivated() {

		Charger charger1 = getTestCharger(1);
		// Rules mode.
		charger1.handleMode("FAST");
		charger1.enableRule(RULE_NAME.CHEAP.toString());
		charger1.enableRule(RULE_NAME.USE_EXPORT.toString());
		assertFastMode(charger1, 1);
		charger1.handlePolling();
		charger1.getGridPowerItem().sendCommand(500);
		charger1.getCheapPowerPriceItem().sendCommand(8.08);
		charger1.getGridPowerPriceItem().sendCommand(20.20);
		charger1.handlePolling();
		assertFastMode(charger1, 1);
		charger1.handleMode("RULES");
		charger1.handlePolling();
		assertOff(charger1, 1);
		charger1.getGridPowerPriceItem().sendCommand(15.15);
		charger1.handlePolling();
		assertOff(charger1, 1);
		
		//do a bit of solar then stop
		sendPVData(charger1, 1500, false, 1 , 8);  // switching phases so still off.
		sendPVData(charger1, 1500, false, 1, 8);
		sendPVData(charger1, 4500, true, 1 , 16);
		sendPVData(charger1, 2400, true, 1, 10);
		sendPVData(charger1, 500, false, 1 , 8);
		assertNull(charger1.getActiveRule());
		
		assertOff(charger1, 1);
		charger1.getGridPowerPriceItem().sendCommand(7.07);
		charger1.handlePolling();
		assertFast(charger1, 1);
		assertEquals(RULE_NAME.CHEAP, charger1.getActiveRule());
		charger1.getGridPowerPriceItem().sendCommand(15.15);
		charger1.handlePolling();
		assertNull(charger1.getActiveRule());
		assertOff(charger1, 1);
		charger1.getGridPowerPriceItem().sendCommand(-0.10);
		charger1.handlePolling();
		assertFast(charger1, 1);
		charger1.getGridPowerPriceItem().sendCommand(15.15);
		charger1.handlePolling();
		assertOff(charger1, 1);
		
		//do a bit of solar then stop
		sendPVData(charger1, 1500, false, 1 , 8);  // switching phases so still off.
		sendPVData(charger1, 1500, false, 1, 8);
		sendPVData(charger1, 4500, true, 1, 16);
		sendPVData(charger1, 2400, true, 1, 10);
		sendPVData(charger1, 5000, true, 1, 16);
		assertEquals(RULE_NAME.USE_EXPORT, charger1.getActiveRule());
		charger1.getGridPowerPriceItem().sendCommand(-0.10);
		charger1.handlePolling();
		assertFast(charger1, 1);
		assertEquals(RULE_NAME.CHEAP, charger1.getActiveRule());
	}
	
	
	@Test
	void testChargerModeRulesCheapOverridesExported() {
		JRuleNumberItem cheapPowerPrice = new MockJRuleNumberItem("evcr_cheap_power_price");
		cheapPowerPrice.sendCommand(10.0);
		Charger charger1 = getTestCharger(1);
		// Rules mode.
		charger1.handleMode("FAST");
		charger1.enableRule(RULE_NAME.CHEAP.toString());
		assertFastMode(charger1, 1);
		charger1.handlePolling();
		charger1.getCheapPowerPriceItem().sendCommand(0.08);
		charger1.getGridPowerPriceItem().sendCommand(0.20);
		charger1.handlePolling();
		assertFastMode(charger1, 1);
		charger1.handleMode("RULES");
		assertOff(charger1, 1);
		charger1.getGridPowerPriceItem().sendCommand(0.15);
		charger1.handlePolling();
		assertOff(charger1, 1);
		charger1.getGridPowerPriceItem().sendCommand(0.08);
		charger1.handlePolling();
		assertOff(charger1, 1);
		charger1.getGridPowerPriceItem().sendCommand(0.07);
		charger1.handlePolling();
		assertFast(charger1, 1);
		charger1.getGridPowerPriceItem().sendCommand(0.15);
		charger1.handlePolling();
		assertOff(charger1, 1);
		charger1.getGridPowerPriceItem().sendCommand(-0.10);
		charger1.handlePolling();
		assertFast(charger1, 1);
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
		return new Charger(mock, number);
	}
	
	private void sendPVData(Charger charger, double gridPower, boolean isOn, int phases, int  amps) {
		charger.getGridPowerItem().sendCommand(gridPower);
		charger.handlePolling();
		assertEquals(isOn, charger.isOn());
		if (charger.isOn()) {
			assertTrue(charger.getActiveRule() == RULE_NAME.USE_EXPORT);
		}
		assertEquals(phases, charger.getPhases());
		assertEquals(amps, charger.getAmps());
	}
	
	private void assertFastMode(Charger charger, int number) {
		assertEquals(MODE_VALUE.FAST, charger.getMode());
		assertFast(charger, number);
	}
	
	private void assertFast(Charger charger, int number) {
		assertTrue(charger.isOn());
		assertFalse(charger.isOff());
		assertEquals("evcr_charger_" + number, charger.name);
		assertEquals(charger.maxAmps, charger.getAmps());
		assertEquals(3, charger.getPhases());
	}
	
	private void assertOffMode(Charger charger, int number) {
		assertEquals(MODE_VALUE.OFF, charger.getMode());
		assertOff(charger, number);
	}
	
	private void assertOff(Charger charger, int number) {
		assertFalse(charger.isOn());
		assertTrue(charger.isOff());
		assertEquals("evcr_charger_" + number, charger.name);
	}
	
	private void assertRulesMode(Charger charger, int number) {
		assertEquals(MODE_VALUE.RULES, charger.getMode());
		assertEquals("evcr_charger_" + number, charger.name);
	}

}