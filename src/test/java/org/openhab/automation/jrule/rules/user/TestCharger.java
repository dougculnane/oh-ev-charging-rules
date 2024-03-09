package org.openhab.automation.jrule.rules.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.user.items.MockJRuleNumberItem;
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
		charger1.enableRule(RULE_NAME.PV.toString());
		assertOff(charger1, 1);
	}
	
	@Test
	void testChargerModeRulesPVActivated_StreadySunUp() {
		Charger charger1 = getTestCharger(1);
		// Rules mode.
		charger1.handleMode("OFF");
		charger1.enableRule(RULE_NAME.PV.toString());
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
	}
	
	@Test
	void testChargerModeRulesPVActivated_PhaseTransition() {
		Charger charger1 = getTestCharger(1);
		// Rules mode.
		charger1.handleMode("OFF");
		charger1.enableRule(RULE_NAME.PV.toString());
		assertOffMode(charger1, 1);
		charger1.handlePolling();
		charger1.handleMode("RULES");
		sendPVData(charger1, 4500, true, 1 , 8);
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
		OpenHabEnvironment mock =  Mockito.mock(OpenHabEnvironment.class);
		Mockito.when(mock.getNumberItem(gridPower.getName())).thenReturn(gridPower);
		Mockito.when(mock.getNumberItem(gridPowerPrice.getName())).thenReturn(gridPowerPrice);
		Mockito.when(mock.getNumberItem(cheapPowerPrice.getName())).thenReturn(cheapPowerPrice);
		Mockito.when(mock.getSwitchItem(mainSwitch.getName())).thenReturn(mainSwitch);
		Mockito.when(mock.getNumberItem(amps.getName())).thenReturn(amps);
		Mockito.when(mock.getNumberItem(phases.getName())).thenReturn(phases);
		Mockito.when(mock.getNumberItem(chargePower.getName())).thenReturn(chargePower);
		return new Charger(mock, number);
	}
	
	private void sendPVData(Charger charger, double gridPower, boolean isOn, int phases, int  amps) {
		charger.getGridPowerItem().sendCommand(gridPower);
		charger.handlePolling();
		assertEquals(isOn, charger.isOn());
		assertEquals(phases, charger.getPhases());
		assertEquals(amps, charger.getAmps());
	}
	
	private void assertFastMode(Charger charger, int number) {
		assertEquals(MODE_VALUE.FAST, charger.mode);
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
		assertEquals(MODE_VALUE.OFF, charger.mode);
		assertOff(charger, number);
	}
	
	private void assertOff(Charger charger, int number) {
		assertFalse(charger.isOn());
		assertTrue(charger.isOff());
		assertEquals("evcr_charger_" + number, charger.name);
	}
	
	private void assertRulesMode(Charger charger, int number) {
		assertEquals(MODE_VALUE.RULES, charger.mode);
		assertEquals("evcr_charger_" + number, charger.name);
	}

}