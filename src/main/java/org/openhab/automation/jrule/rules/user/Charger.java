package org.openhab.automation.jrule.rules.user;

import java.util.ArrayList;

import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

public class Charger {

	final private OpenHabEnvironment openHabEnvironment;
	
	enum MODE_VALUE {
		OFF, FAST, RULES
	}
	
	enum RULE_NAME {
		CO2, CHEAP, PV, TARGET, TIMER 
	}
	
	int minAmps = 8;
	int maxAmps = 16;
	
	int number;
	String name;
	MODE_VALUE mode = MODE_VALUE.FAST;
	RULE_NAME activeRule = null;
	ArrayList<RULE_NAME> enabledRules = new ArrayList<RULE_NAME>();
	
	
	public Charger(int number) {
		this(new OpenHabEnvironment(), number);
	}
	
	public Charger(OpenHabEnvironment openHabEnvironment, int number) {
		this.openHabEnvironment = openHabEnvironment;
		this.number = number;
		this.name = "evcr_charger_" + number;
		setAmps(maxAmps);
		setPhases(3);
		switchOn();
	}

	/**
	 * @param modeValue
	 * @return Create lock
	 */
	public boolean handleMode(String modeValue) {
		switch (modeValue) {
		case "OFF": {
			switchOff();
			this.mode = MODE_VALUE.OFF;
			return switchOff();
		}
		case "FAST": {
			this.mode = MODE_VALUE.FAST;
			return activateFastCharging();
		}
		case "RULES": {
			this.mode = MODE_VALUE.RULES;
			return handlePolling();
		}
		default:
			throw new IllegalArgumentException("Unexpected evcr_charger_" + number + " value: " + modeValue);
		}
	}
	
	public boolean handlePolling() {
		if (mode != MODE_VALUE.RULES) {
			return false;
		}
		boolean fastChargingActivated = false;
		// CO2 
		// CHEAP 
		if (!fastChargingActivated && enabledRules.contains(RULE_NAME.CHEAP)) {
			double cheapPowerPrice = getCheapPowerPriceItem().getStateAsDecimal().doubleValue();
			double gridPowerPrice = getGridPowerPriceItem().getStateAsDecimal().doubleValue();
			if (cheapPowerPrice > gridPowerPrice) {
				activateFastCharging();
				activeRule = RULE_NAME.CHEAP;
				fastChargingActivated = true;
			} else if (activeRule == RULE_NAME.CHEAP) {
				activeRule = null;
			}
		}
		// TIMER
		// evcr-charger-1-timer-start
		// evcr-charger-1-timer-finish
		
		// TARGET
		
		if (!fastChargingActivated && enabledRules.contains(RULE_NAME.PV)) {
			return handleExportPower(getGridPowerItem().getStateAsDecimal());
		} else if (!fastChargingActivated) {
			return switchOff();
		}
		return false;
	}

	public boolean handleExportPower(JRuleValue state) {
		if (this.mode == MODE_VALUE.RULES && enabledRules.contains(RULE_NAME.PV)) {
			
			// Block if other rules are active.
			if (activeRule == null) {
				activeRule = RULE_NAME.PV;
			}
			if (activeRule != RULE_NAME.PV) {
				// Then we are not in control.
				return false;
			}
			
			double kwExported = Double.parseDouble(state.stringValue());
			double chargingPower = getChargingPowerItem().getStateAsDecimal().doubleValue();
			
			if (kwExported + chargingPower > getMinimPhase1Power()) {
			
			
				// Phases change
				if (getPhases() == 1 && kwExported > getMinimPhase3Power()) {
					setAmps(minAmps);
					setPhases(3);
					return true;
				} else if (getPhases() == 3 && kwExported < getMinimPhase3Power()) {
					setPhases(1);
					return true;
				}
				
				if (isOff()) {
					switchOn();
					return true;
				}
			
				int calcAmps = Double.valueOf((kwExported + chargingPower) / 240 / getPhases()).intValue();
				setAmps(calcAmps);
			} else {
				switchOff();
				setPhases(1);
				setAmps(minAmps);
			}
			return true;
		}
		return false;
	} 

	private boolean switchOn() {
		JRuleSwitchItem switchItem = getSwitchItem();
		if (switchItem != null && switchItem.getStateAsOnOff() != JRuleOnOffValue.ON) {
			switchItem.sendCommand(JRuleOnOffValue.ON);
			return true;
		}
		return false;
	}
	private boolean switchOff() {
		JRuleSwitchItem switchItem = getSwitchItem();
		if (switchItem != null && switchItem.getStateAsOnOff() != JRuleOnOffValue.OFF) {
			switchItem.sendCommand(JRuleOnOffValue.OFF);
			return true;
		}
		return false;
	}
	protected boolean isOn() {
		JRuleSwitchItem switchItem = getSwitchItem();
		if (switchItem != null) {
			return switchItem.getStateAsOnOff() == JRuleOnOffValue.ON;
		}
		return false;
	}
	protected boolean isOff() {
		JRuleSwitchItem switchItem = getSwitchItem();
		if (switchItem != null) {
			return switchItem.getStateAsOnOff() == JRuleOnOffValue.OFF;
		}
		return false;
	}
	protected int getAmps() {
		return getAmpsItem().getStateAsDecimal().intValue();
	}
	private boolean setAmps(int amps) {
		if (amps < minAmps) {
			amps = minAmps;
		}
		if (amps > maxAmps) {
			amps = maxAmps;
		}
		JRuleNumberItem ampsItem = getAmpsItem();
		if (ampsItem != null && ampsItem.getStateAsDecimal().intValue() != amps) {
			ampsItem.sendCommand(new JRuleDecimalValue(amps));
			return true;
		}
		return false;
	}
	protected int getPhases() {
		return getPhasesItem().getStateAsDecimal().intValue();
	}
	private boolean setPhases(int phases) {
		JRuleNumberItem phasesItem = getPhasesItem();
		if (phasesItem != null && (phasesItem.getStateAsDecimal() == null || phasesItem.getStateAsDecimal().intValue() != phases)) {
			phasesItem.sendCommand(new JRuleDecimalValue(phases));
			return true;
		}
		return false;
	}
	
	private Double getMinimPhase1Power() {
		return Double.valueOf((240 * minAmps));
	}
	private Double getMinimPhase3Power() {
		return Double.valueOf((240 * minAmps * 3));
	}
	private boolean activateFastCharging() {
		return setAmps(maxAmps)
		    | setPhases(3)
		    | switchOn();
	}
	
	public void enableRule(String ruleName) {
		RULE_NAME rule = RULE_NAME.valueOf(ruleName);
		if (!enabledRules.contains(rule)) {
			enabledRules.add(rule);		
		}
	}

	public void disableRule(String ruleName) {
		RULE_NAME rule = RULE_NAME.valueOf(ruleName);
		if (enabledRules.contains(rule)) {
			enabledRules.remove(rule);
		}
	}
	
	private JRuleSwitchItem getSwitchItem() {
		return openHabEnvironment.getSwitchItem("evcr_charger_" + number + "_switch");
	}
	private JRuleNumberItem getAmpsItem() {
		return openHabEnvironment.getNumberItem("evcr_charger_" + number + "_amps");
	}
	private JRuleNumberItem getPhasesItem() {
		return openHabEnvironment.getNumberItem("evcr_charger_" + number + "_phases");
	}
	protected JRuleNumberItem getGridPowerItem() {
		return openHabEnvironment.getNumberItem("evcr_export_power");
	}
	protected JRuleNumberItem getCheapPowerPriceItem() {
		return openHabEnvironment.getNumberItem("evcr_cheap_power_price");
	}
	protected JRuleNumberItem getGridPowerPriceItem() {
		return openHabEnvironment.getNumberItem("evcr_grid_power_price");
	}
	private JRuleNumberItem getChargingPowerItem() {
		return openHabEnvironment.getNumberItem("evcr_charger_" + number + "_power");
	}
}
