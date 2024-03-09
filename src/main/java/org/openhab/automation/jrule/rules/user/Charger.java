package org.openhab.automation.jrule.rules.user;

import java.util.ArrayList;

import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.items.JRuleStringItem;
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
		CHEAP, USE_EXPORT, TARGET, TIMER
	}
	
	int minAmps = 8;
	int maxAmps = 16;
	
	int number;
	String name;
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
			setMode(MODE_VALUE.OFF);
			return switchOff();
		}
		case "FAST": {
			setMode(MODE_VALUE.FAST);
			return activateFastCharging();
		}
		case "RULES": {
			setMode(MODE_VALUE.RULES);
			return handlePolling();
		}
		default:
			throw new IllegalArgumentException("Unexpected evcr_charger_" + number + " value: " + modeValue);
		}
	}
	
	public boolean handlePolling() {
		if (getMode() != MODE_VALUE.RULES) {
			return false;
		}
		boolean fastChargingActivated = false;
		// CHEAP 
		if (!fastChargingActivated && enabledRules.contains(RULE_NAME.CHEAP)) {
			double cheapPowerPrice = getCheapPowerPrice();
			double gridPowerPrice = getGridPowerPrice();
			if (cheapPowerPrice > gridPowerPrice) {
				activateFastCharging();
				setActiveRule(RULE_NAME.CHEAP);
				fastChargingActivated = true;
			} else if (getActiveRule() == RULE_NAME.CHEAP) {
				setActiveRule(null);
			}
		}
		// TIMER
		// evcr-charger-1-timer-start
		// evcr-charger-1-timer-finish
		
		// TARGET
		
		if (!fastChargingActivated && enabledRules.contains(RULE_NAME.USE_EXPORT)) {
			return handleExportPower(getGridPowerItem().getStateAsDecimal());
		} else if (!fastChargingActivated) {
			return switchOff();
		}
		return false;
	}

	private double getChargingPower() {
		JRuleNumberItem item = getChargingPowerItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsDecimal().doubleValue();
		}
		return 0;
	}
	
	private double getGridPowerPrice() {
		JRuleNumberItem item = getGridPowerPriceItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsDecimal().doubleValue();
		}
		return 0;
	}

	private double getCheapPowerPrice() {
		JRuleNumberItem item = getCheapPowerPriceItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsDecimal().doubleValue();
		}
		return 0;
	}

	public boolean handleExportPower(JRuleValue state) {
		if (getMode() == MODE_VALUE.RULES && enabledRules.contains(RULE_NAME.USE_EXPORT)) {
			RULE_NAME activeRule = getActiveRule();
			// Block if other rules are active.
			if (activeRule == null) {
				setActiveRule(RULE_NAME.USE_EXPORT);
			}
			if (activeRule != RULE_NAME.USE_EXPORT) {
				// Then we are not in control.
				return false;
			}
			
			double kwExported = Double.parseDouble(state.stringValue());
			double chargingPower = getChargingPower();
			
			if (kwExported + chargingPower > getMinimPhase1Power()) {
			
				// Phases change
				if (getPhases() == 1 && kwExported > getMinimPhase3Power()) {
					setAmps(minAmps);
					setPhases(3);
					return true;
				} else if (getPhases() == null || (getPhases() == 3 && kwExported < getMinimPhase3Power())) {
					setPhases(1);
					return true;
				}
				
				int calcAmps = Double.valueOf((kwExported + chargingPower) / 240 / getPhases()).intValue();
				setAmps(calcAmps);
				
				if (isOff()) {
					switchOn();
				}
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
		if (switchItem != null && (switchItem.getState() == null || switchItem.getStateAsOnOff() != JRuleOnOffValue.ON)) {
			switchItem.sendCommand(JRuleOnOffValue.ON);
			return true;
		}
		return false;
	}
	private boolean switchOff() {
		JRuleSwitchItem switchItem = getSwitchItem();
		if (switchItem != null && (switchItem.getState() == null || switchItem.getStateAsOnOff() != JRuleOnOffValue.OFF)) {
			switchItem.sendCommand(JRuleOnOffValue.OFF);
			return true;
		}
		return false;
	}
	protected boolean isOn() {
		JRuleSwitchItem switchItem = getSwitchItem();
		return switchItem != null 
				&& switchItem.getState() != null 
				&& switchItem.getStateAsOnOff() == JRuleOnOffValue.ON;
	}
	protected boolean isOff() {
		JRuleSwitchItem switchItem = getSwitchItem();
		return switchItem == null
				|| switchItem.getState() == null 
				|| switchItem.getStateAsOnOff() == JRuleOnOffValue.OFF;
	}
	protected int getAmps() {
		JRuleNumberItem item = getAmpsItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsDecimal().intValue();
		}
		return 0;
	}
	private boolean setAmps(int amps) {
		if (amps < minAmps) {
			amps = minAmps;
		}
		if (amps > maxAmps) {
			amps = maxAmps;
		}
		JRuleNumberItem ampsItem = getAmpsItem();
		if (ampsItem != null && (ampsItem.getStateAsDecimal() == null || ampsItem.getStateAsDecimal().intValue() != amps)) {
			ampsItem.sendCommand(new JRuleDecimalValue(amps));
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
		try {
			RULE_NAME rule = RULE_NAME.valueOf(ruleName);
			if (enabledRules.contains(rule)) {
				enabledRules.remove(rule);
			}
		} catch (IllegalArgumentException e) {
		}
	}
	public RULE_NAME getActiveRule() {
		JRuleStringItem activeRuleItem = getActiveRuleItem();
		if (activeRuleItem != null &&  activeRuleItem.getState() != null) {
			try {
				return RULE_NAME.valueOf(activeRuleItem.getStateAsString());
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
		return null;
	}
	private void setActiveRule(RULE_NAME ruleName) {
		JRuleStringItem activeRuleItem = getActiveRuleItem();
		if (ruleName != null && activeRuleItem != null && (activeRuleItem.getState() == null || !activeRuleItem.getStateAsString().equals(ruleName.toString()))) {
			activeRuleItem.sendCommand(ruleName.toString());
		} else if (ruleName == null && activeRuleItem != null) {
			activeRuleItem.sendCommand("");
		}
	}
	public MODE_VALUE getMode() {
		JRuleStringItem modeItem = getModeItem();
		if (modeItem != null && modeItem.getState() != null) {
			try {
				return MODE_VALUE.valueOf(modeItem.getStateAsString());
			} catch (IllegalArgumentException e) {
			}
		}
		// Default to FAST mode
		handleMode(MODE_VALUE.FAST.toString());
		return MODE_VALUE.FAST;
	}
	public void setMode(MODE_VALUE mode) {
		JRuleStringItem modeItem = getModeItem();
		if (modeItem != null && (modeItem.getState() == null || !modeItem.getStateAsString().equals(mode.toString()))) {
			modeItem.sendCommand(mode.toString());
		}
	}
	
	public Integer getPhases() {
		JRuleNumberItem item = getPhasesItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsDecimal().intValue();
		}
		return null;
	}
	private boolean setPhases(int phases) {
		JRuleNumberItem phasesItem = getPhasesItem();
		if (phasesItem != null && (phasesItem.getState() == null || phasesItem.getStateAsDecimal().intValue() != phases)) {
			phasesItem.sendCommand(new JRuleDecimalValue(phases));
			return true;
		}
		return false;
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
	private JRuleStringItem getModeItem() {
		return openHabEnvironment.getStringItem("evcr_charger_" + number + "_mode");
	}
	private JRuleStringItem getActiveRuleItem() {
		return openHabEnvironment.getStringItem("evcr_charger_" + number + "_active_rule");
	}
}
