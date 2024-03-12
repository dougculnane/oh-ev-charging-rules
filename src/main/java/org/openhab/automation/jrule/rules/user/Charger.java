package org.openhab.automation.jrule.rules.user;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import org.openhab.automation.jrule.items.JRuleDateTimeItem;
import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.value.JRuleDateTimeValue;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

public class Charger {

	final private OpenHabEnvironment openHabEnvironment;
	
	enum MODE_VALUE {
		OFF, FAST, RULES
	}
	
	enum RULE_NAME {
		CHEAP, BEST_GRID, USE_EXPORT, TARGET, TIMER
	}
	
	int minAmps = 8;
	int maxAmps = 16;
	
	int number;
	
	public Charger(int number) {
		this(new OpenHabEnvironment(), number);
	}
	
	public Charger(OpenHabEnvironment openHabEnvironment, int number) {
		this.openHabEnvironment = openHabEnvironment;
		this.number = number;
		setAmps(maxAmps);
		setPhases(3);
		switchOn();
	}
	
	public String getName() {
		JRuleStringItem item = getNameItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsString();
		}
		return "evcr_charger_" + number;
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
		final Car car = getConnectedCar();
		if (car != null && car.targetLevelReached() ) {
        	setActiveRule(RULE_NAME.TARGET);
        	return switchOff();
        }
		
		boolean fastChargingActivated = false;
		
		// TIMER
		if (!fastChargingActivated && isRuleEnabled(RULE_NAME.TIMER)) {
			Date now = new Date();
			Date timerStart = getTimerStart();
			Date timerFinish = getTimerFinish();
			if (timerStart != null 
					&& timerFinish != null
					&& now.after(timerStart) 
					&& now.before(timerFinish)) {
				setActiveRule(RULE_NAME.TIMER);
				fastChargingActivated = true;
			} else if (getActiveRule() == RULE_NAME.TIMER) {
				setActiveRule(null);
			}
		}
		
		// CHEAP 
		if (!fastChargingActivated && isRuleEnabled(RULE_NAME.CHEAP)) {
			double cheapPowerPrice = getCheapPowerPrice();
			double gridPowerPrice = getGridPowerPrice();
			if (cheapPowerPrice > gridPowerPrice) {
				setActiveRule(RULE_NAME.CHEAP);
				fastChargingActivated = true;
			} else if (getActiveRule() == RULE_NAME.CHEAP) {
				setActiveRule(null);
			}
		}
		
		// BEST_GRID
		if (!fastChargingActivated && isRuleEnabled(RULE_NAME.BEST_GRID)) {
			ZonedDateTime now = ZonedDateTime.now();
			ZonedDateTime bestPriceStart = getBestPriceStart();
			ZonedDateTime bestPriceFinish = getBestPriceFinish();
			if (bestPriceStart != null 
					&& bestPriceFinish != null
					&& now.isAfter(bestPriceStart) 
					&& now.isBefore(bestPriceFinish)) {
				setActiveRule(RULE_NAME.BEST_GRID);
				fastChargingActivated = true;
			} else if (getActiveRule() == RULE_NAME.BEST_GRID) {
				setActiveRule(null);
			}
		}
		
		// TARGET
		if (!fastChargingActivated && car != null && isRuleEnabled(RULE_NAME.TARGET)) {
			
			int neededMins = car.getMinutesNeededForTarget(240 * this.maxAmps * 3);
			Calendar now = Calendar.getInstance();
			Calendar cal = car.getTargetTime();
			cal.add(Calendar.MINUTE, (neededMins * -1));
			
			if (neededMins > 0	&&  cal.before(now)) {
				setActiveRule(RULE_NAME.TARGET);
				fastChargingActivated = true;
			} else if (getActiveRule() == RULE_NAME.TARGET) {
				setActiveRule(null);
			}
		}
		
		// USE Export or switch off
		if (fastChargingActivated) {
			return activateFastCharging();
		} else if (!fastChargingActivated && isRuleEnabled(RULE_NAME.USE_EXPORT)) {
			return handleExportPower(getExportPower());
		} else if (!fastChargingActivated) {
			return switchOff();
		}
		return false;
	}


	private Car getConnectedCar() {
		JRuleNumberItem item = getConnectedCarItem();
		if (item != null && item.getState() != null) {
			return new Car(openHabEnvironment, item.getStateAsDecimal().intValue());
		}
		return null;
	}

	private Date getTimerFinish() {
		JRuleStringItem item = getTimerFinishItem();
		if (item != null && item.getState() != null) {
			String[] value = item.getStateAsString().split(":");
			if (value.length == 2) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(value[0]));
				cal.set(Calendar.MINUTE, Integer.valueOf(value[1]));
				return cal.getTime();
			}
			
		}
		return null;
	}

	private Date getTimerStart() {
		JRuleStringItem item = getTimerStartItem();
		if (item != null && item.getState() != null) {
			String[] value = item.getStateAsString().split(":");
			if (value.length == 2) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(value[0]));
				cal.set(Calendar.MINUTE, Integer.valueOf(value[1]));
				return cal.getTime();
			}
			
		}
		return null;
	}

	private double getExportPower() {
		JRuleNumberItem item = getExportPowerItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsDecimal().doubleValue();
		}
		return 0;
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

	public boolean handleExportPower(double wattsExported) {
		if (getMode() == MODE_VALUE.RULES && isRuleEnabled(RULE_NAME.USE_EXPORT)) {
			// Block if other rules are active.
			RULE_NAME activeRule = getActiveRule();
			if (activeRule != null && activeRule != RULE_NAME.USE_EXPORT) {
				// Then we are not in control.
				return false;
			}
			
			double chargingPower = getChargingPower();			
			if (wattsExported + chargingPower > getMinimPhase1Power()) {
			
				// Phases change
				if (getPhases() == 1 && wattsExported > getMinimPhase3Power()) {
					setAmps(minAmps);
					setPhases(3);
					return true;
				} else if (getPhases() == null || (getPhases() == 3 && wattsExported < getMinimPhase3Power())) {
					setPhases(1);
					return true;
				}
				
				int calcAmps = Double.valueOf((wattsExported + chargingPower) / 240 / getPhases()).intValue();
				setAmps(calcAmps);
				switchOn();
				setActiveRule(RULE_NAME.USE_EXPORT);
			} else {
				switchOff();
				setPhases(1);
				setAmps(minAmps);
				setActiveRule(null);
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
	
	boolean isRuleEnabled(RULE_NAME ruleName) {
		JRuleSwitchItem item = getRuleSwitchItem(ruleName);
		return item != null 
				&& item.getState() != null 
				&& item.getStateAsOnOff() == JRuleOnOffValue.ON;
	}
	
	public void enableRule(String ruleName) {
		try {
			RULE_NAME rule = RULE_NAME.valueOf(ruleName);
			JRuleSwitchItem item = getRuleSwitchItem(rule);
			if (item != null && (item.getState() == null || item.getState() != JRuleOnOffValue.ON)) {
				item.sendCommand(JRuleOnOffValue.ON);
			}
		} catch (IllegalArgumentException e) {
		}
	}

	public void disableRule(String ruleName) {
		try {
			RULE_NAME rule = RULE_NAME.valueOf(ruleName);
			if (getActiveRule() == rule) {
				setActiveRule(null);
			}
			JRuleSwitchItem item = getRuleSwitchItem(rule);
			if (item != null && (item.getState() == null || item.getState() != JRuleOnOffValue.OFF)) {
				item.sendCommand(JRuleOnOffValue.OFF);
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
		if (ruleName != null 
				&& activeRuleItem != null 
				&& (activeRuleItem.getState() == null || !activeRuleItem.getStateAsString().equals(ruleName.toString()))) {
			activeRuleItem.sendCommand(ruleName.toString());
		} else if (ruleName == null 
				&& activeRuleItem != null 
				&& (activeRuleItem.getState() == null || (activeRuleItem.getState() != null && !activeRuleItem.getStateAsString().equals("")))) {
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
	
	private ZonedDateTime getBestPriceFinish() {
		JRuleDateTimeItem item = getBestPriceFinishItem();
		if (item != null && item.getState() == null) {
			JRuleDateTimeValue value = item.getStateAsDateTime();
			return value.getValue();
		}
		return null;
	}

	private ZonedDateTime getBestPriceStart() {
		JRuleDateTimeItem item = getBestPriceStartItem();
		if (item != null && item.getState() == null) {
			JRuleDateTimeValue value = item.getStateAsDateTime();
			return value.getValue();
		}
		return null;
	}
	
	private JRuleSwitchItem getRuleSwitchItem(RULE_NAME ruleName) {
		return openHabEnvironment.getSwitchItem("evcr_charger_" + number + "_" + ruleName.toString() + "_switch");
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
	protected JRuleNumberItem getExportPowerItem() {
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
	private JRuleDateTimeItem getBestPriceFinishItem() {
		return openHabEnvironment.getDateTimeItem("evcr_charger_" + number + "_BEST_PRICE_finish");
	}
	private JRuleDateTimeItem getBestPriceStartItem() {
		return openHabEnvironment.getDateTimeItem("evcr_charger_" + number + "_BEST_PRICE_start");
	}
	protected JRuleStringItem getTimerFinishItem() {
		return openHabEnvironment.getStringItem("evcr_charger_" + number + "_TIMER_finish");
	}
	protected JRuleStringItem getTimerStartItem() {
		return openHabEnvironment.getStringItem("evcr_charger_" + number + "_TIMER_start");
	}
	protected JRuleStringItem getNameItem() {
		return openHabEnvironment.getStringItem("evcr_charger_" + number + "_name");
	}
	private JRuleNumberItem getConnectedCarItem() {
		return openHabEnvironment.getNumberItem("evcr_charger_" + number + "_connected_car");
	}
	
}
