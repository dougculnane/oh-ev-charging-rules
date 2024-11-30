package com.github.dougculnane.oh_ev_charging_rules;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import org.openhab.automation.jrule.items.JRuleDateTimeItem;
import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.user.OpenHabEnvironment;
import org.openhab.automation.jrule.rules.value.JRuleDateTimeValue;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

public abstract class Charger {

	/**
	 * Counter for check that the charger gets stuck in a not ready state.
	 */
	private static int notReadyCount = 0;
	
	/**
	 * Number of time the charge is allowed to be not ready before we consider it stuck.
	 */
	private static final int NOT_READY_LIMIT = 5;
	
	final protected OpenHabEnvironment openHabEnvironment;

	enum MODE_VALUE {
		DISABLE, OFF, FAST, RULES
	}

	enum RULE_NAME {
		CHEAP, BEST_GRID, USE_EXPORT, TARGET, TIMER, MAX
	}

	int number;

	public Charger(int number) {
		this(new OpenHabEnvironment(), number);
	}

	public Charger(OpenHabEnvironment openHabEnvironment, int number) {
		this.openHabEnvironment = openHabEnvironment;
		this.number = number;
	}

	abstract boolean useExportPower(double watts);

	abstract double getMinimPower();

	abstract double getFastChargeRate();

	/**
	 * 
	 * @return true if state changes
	 */	
	abstract boolean activateFastCharging();

	/**
	 * Is the charger ready to make a decision or change state.
	 * @return
	 */
	private boolean isReady() {
		boolean ready = false;
		switch (this.getMode()) {
		case OFF: {
			ready = !switchOff();
			break;
		}
		case FAST: {
			ready = !activateFastCharging();
			break;
		}
		default:
			// RULES
			Double power = getChargingPower();
			if (isOn()) {
				Integer phases = getPhases();
				Integer amps = getAmps();
				if (power != null && phases != null && amps != null) {
					Integer expectedPower = amps * phases * 240;
					// power about right.
					ready = Math.abs(expectedPower.intValue() - power.intValue()) < (650 * phases);
				 }
			} else if (isOff()) {
				ready = power != null && Math.abs(power) < 50;		
			}
			break;
		}
		if (ready) {
			notReadyCount = 0;
		} else if (notReadyCount >= NOT_READY_LIMIT) {
			notReadyCount = 0;
			ready = true;			
		} else {
			notReadyCount++;
		}
		return ready;
	}
	
	/**
	 * @param modeValue
	 * @return true if state change
	 */
	public boolean handleMode(String modeValue) {
		switch (modeValue) {
		case "DISABLE": {
			setMode(MODE_VALUE.DISABLE);
			return false;
		}
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
			return handleMode(getMode().toString());
		}
		final Car car = getConnectedCar();
		if (car != null) {
			if (car.maxLevelReached()) {
				setActiveRule(RULE_NAME.MAX);
				return switchOff();
			} else if (getActiveRule() == RULE_NAME.MAX) {
				setActiveRule(null);
			}
		}

		boolean fastChargingActivated = false;

		// TIMER
		if (!fastChargingActivated && isRuleEnabled(RULE_NAME.TIMER)) {
			Date now = new Date();
			Date timerStart = getTimerStart();
			Date timerFinish = getTimerFinish();
			if (timerStart != null && timerFinish != null && now.after(timerStart) && now.before(timerFinish)) {
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
			ZonedDateTime bestPriceStart = getBestGridStart();
			ZonedDateTime bestPriceFinish = getBestGridFinish();
			if (bestPriceStart != null && bestPriceFinish != null && now.isAfter(bestPriceStart)
					&& now.isBefore(bestPriceFinish)) {
				setActiveRule(RULE_NAME.BEST_GRID);
				fastChargingActivated = true;
			} else if (getActiveRule() == RULE_NAME.BEST_GRID) {
				setActiveRule(null);
			}
		}

		// TARGET
		if (!fastChargingActivated 
				&& car != null 
				&& isRuleEnabled(RULE_NAME.TARGET) 
				&& car.getTargetTime() != null) {
			int bufferMins = 30;
			int neededMins = car.getMinutesNeededForTarget(getFastChargeRate());
			Calendar cal = car.getTargetTime();
			cal.add(Calendar.MINUTE, (neededMins * -1) - bufferMins);
			if (neededMins > (0 - bufferMins)
					&& cal.before(Calendar.getInstance())) {
				setActiveRule(RULE_NAME.TARGET);
				if (!car.targetLevelReached()) {
					fastChargingActivated = true;
				}
			} else if (getActiveRule() == RULE_NAME.TARGET) {
				setActiveRule(null);
			}
		} else if (getActiveRule() == RULE_NAME.TARGET 
				&& (car == null || !car.targetLevelReached())) {
			setActiveRule(null);
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

	public boolean handleExportPower(double wattsExported) {
		if (getMode() == MODE_VALUE.RULES && isRuleEnabled(RULE_NAME.USE_EXPORT)) {
			// Block if other rules are active.
			RULE_NAME activeRule = getActiveRule();
			if (activeRule != null && activeRule != RULE_NAME.USE_EXPORT) {
				// Then we are not in control.
				return false;
			}
			if (isReady()) {
				Double chargingPower = getChargingPower();
				if (chargingPower != null) {
					double availablePower = wattsExported + chargingPower;
					if (availablePower > getMinimPower()) {
						setActiveRule(RULE_NAME.USE_EXPORT);
						return useExportPower(availablePower);
					} else {
						setActiveRule(null);
						return switchOff();
					}
				}
			}
		}
		return false;
	}

	protected Double getChargingPower() {
		// Get power from the item that is linked to the charger output hopefully.
		JRuleNumberItem item = getChargingPowerItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsDecimal().doubleValue();
		}
		return null;
	}

	public String getName() {
		JRuleStringItem item = getNameItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsString();
		}
		return "evcr_charger_" + number;
	}

	protected boolean setAmps(int amps) {
		JRuleNumberItem ampsItem = getAmpsItem();
		if (ampsItem != null && (ampsItem.getState() == null || ampsItem.getStateAsDecimal() == null
				|| ampsItem.getStateAsDecimal().intValue() != amps)) {
			ampsItem.sendCommand(new JRuleDecimalValue(amps));
			return true;
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

	/**
	 * @return true if state changes.
	 */
	protected boolean switchOn() {
		JRuleSwitchItem switchItem = getSwitchItem();
		if (switchItem != null
				&& (switchItem.getState() == null || switchItem.getStateAsOnOff() != JRuleOnOffValue.ON)) {
			switchItem.sendCommand(JRuleOnOffValue.ON);
			return true;
		}
		return false;
	}

	/**
	 * @return true if state changes.
	 */
	protected boolean switchOff() {
		JRuleSwitchItem switchItem = getSwitchItem();
		if (switchItem != null
				&& (switchItem.getState() == null || switchItem.getStateAsOnOff() != JRuleOnOffValue.OFF)) {
			switchItem.sendCommand(JRuleOnOffValue.OFF);
			return true;
		}
		return false;
	}

	/**
	 * @return true if is On state.
	 */
	protected boolean isOn() {
		JRuleSwitchItem switchItem = getSwitchItem();
		return switchItem != null && switchItem.getState() != null
				&& switchItem.getStateAsOnOff() == JRuleOnOffValue.ON;
	}

	/**
	 * @return true if id Off state.
	 */
	protected boolean isOff() {
		JRuleSwitchItem switchItem = getSwitchItem();
		return switchItem == null || switchItem.getState() == null
				|| switchItem.getStateAsOnOff() == JRuleOnOffValue.OFF;
	}

	protected int getAmps() {
		JRuleNumberItem item = getAmpsItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsDecimal().intValue();
		}
		return 0;
	}

	boolean isRuleEnabled(RULE_NAME ruleName) {
		JRuleSwitchItem item = getRuleSwitchItem(ruleName);
		return item != null && item.getState() != null && item.getStateAsOnOff() == JRuleOnOffValue.ON;
	}
	
	boolean isExport1PhaseOnly() {
		JRuleSwitchItem item = getExport1PhaseOnlySwitchItem();
		return item != null && item.getState() != null && item.getStateAsOnOff() == JRuleOnOffValue.ON;
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
		if (activeRuleItem != null && activeRuleItem.getState() != null) {
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
		if (ruleName != null && activeRuleItem != null && (activeRuleItem.getState() == null
				|| !activeRuleItem.getStateAsString().equals(ruleName.toString()))) {
			activeRuleItem.sendCommand(ruleName.toString());
		} else if (ruleName == null && activeRuleItem != null && (activeRuleItem.getState() == null
				|| (activeRuleItem.getState() != null && !activeRuleItem.getStateAsString().equals("")))) {
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

	protected boolean setPhases(int phases) {
		JRuleNumberItem phasesItem = getPhasesItem();
		if ((phases == 1 || phases == 3) && phasesItem != null
				&& (phasesItem.getState() == null || phasesItem.getStateAsDecimal().intValue() != phases)) {
			phasesItem.sendCommand(new JRuleDecimalValue(phases));
			return true;
		}
		return false;
	}

	private ZonedDateTime getBestGridFinish() {
		JRuleDateTimeItem item = getBestGridFinishItem();
		if (item != null && item.getState() != null) {
			JRuleDateTimeValue value = item.getStateAsDateTime();
			return value.getValue();
		}
		return null;
	}

	private ZonedDateTime getBestGridStart() {
		JRuleDateTimeItem item = getBestGridStartItem();
		if (item != null && item.getState() != null) {
			JRuleDateTimeValue value = item.getStateAsDateTime();
			return value.getValue();
		}
		return null;
	}

	protected JRuleNumberItem getChargingPowerItem() {
		return openHabEnvironment.getNumberItem("evcr_charger_" + number + "_power");
	}

	private JRuleSwitchItem getRuleSwitchItem(RULE_NAME ruleName) {
		return openHabEnvironment.getSwitchItem("evcr_charger_" + number + "_" + ruleName.toString() + "_switch");
	}

	protected JRuleSwitchItem getSwitchItem() {
		return openHabEnvironment.getSwitchItem("evcr_charger_" + number + "_switch");
	}

	protected JRuleSwitchItem getExport1PhaseOnlySwitchItem() {
		return openHabEnvironment.getSwitchItem("evcr_charger_" + number + "_EXPORT_1phase_switch");
	}
	
	protected JRuleNumberItem getAmpsItem() {
		return openHabEnvironment.getNumberItem("evcr_charger_" + number + "_amps");
	}

	protected JRuleNumberItem getPhasesItem() {
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

	private JRuleStringItem getModeItem() {
		return openHabEnvironment.getStringItem("evcr_charger_" + number + "_mode");
	}

	private JRuleStringItem getActiveRuleItem() {
		return openHabEnvironment.getStringItem("evcr_charger_" + number + "_active_rule");
	}

	protected JRuleDateTimeItem getBestGridFinishItem() {
		return openHabEnvironment.getDateTimeItem("evcr_charger_" + number + "_BEST_GRID_finish");
	}

	protected JRuleDateTimeItem getBestGridStartItem() {
		return openHabEnvironment.getDateTimeItem("evcr_charger_" + number + "_BEST_GRID_start");
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
