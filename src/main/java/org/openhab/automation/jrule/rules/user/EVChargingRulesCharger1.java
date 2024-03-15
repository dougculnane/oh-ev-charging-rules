package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;

public class EVChargingRulesCharger1 extends EVChargingRules {

	private static final String CHANGER_NUMBER = "1";
	static final String RULE_NAME_MODE_CHANGE = "evcr_charger_" + CHANGER_NUMBER + "_ModeChangeRule";
	static final String RULE_NAME_TOGGLE_RULE = "evcr_charger_" + CHANGER_NUMBER + "_ToggleRule";
	
	@JRuleName(RULE_NAME_MODE_CHANGE)
	@JRuleWhenItemChange(item = "evcr_charger_" + CHANGER_NUMBER + "_mode")
	public void evcr_charger_1_ModeChangeRule(JRuleItemEvent event) {
		logDebug(RULE_NAME_MODE_CHANGE + " from: {} to: {}", event.getOldState(), event.getState());
		if (event.getState() != null && event.getState().stringValue() != null) {
			if (isTimeLocked(CHARGER_1_LOCK_NAME)) {
				logDebug(CHARGER_1_LOCK_NAME + ": locked so skipping mode change");
			} else if (charger1.handleMode(event.getState().stringValue())) {
				getTimeLock(CHARGER_1_LOCK_NAME, TIME_LOCK_FOR_CHARGER);
			}
		}
	}
	
	@JRuleName(RULE_NAME_TOGGLE_RULE)
	@JRuleWhenItemChange(item = "evcr_charger_" + CHANGER_NUMBER + "_CHEAP_switch")
	@JRuleWhenItemChange(item = "evcr_charger_" + CHANGER_NUMBER + "_USE_EXPORT_switch")
	@JRuleWhenItemChange(item = "evcr_charger_" + CHANGER_NUMBER + "_BEST_GRID_switch")
	@JRuleWhenItemChange(item = "evcr_charger_" + CHANGER_NUMBER + "_TARGET_switch")
	@JRuleWhenItemChange(item = "evcr_charger_" + CHANGER_NUMBER + "_TIMER_switch")
	public void evcr_charger_1_EnableRule(JRuleItemEvent event) {
		logDebug(RULE_NAME_TOGGLE_RULE + " {}: {}", event.getItem().getName(), event.getState());
		if (event.getState() != null && event.getState().stringValue() != null) {
			String ruleName = event.getItem().getName()
					.replace("evcr_charger_" + CHANGER_NUMBER + "_", "")
					.replace("_switch", "");
			if (event.getState().stringValue() == "ON") {
				charger1.enableRule(ruleName);
			} else {
				charger1.disableRule(ruleName);
			}
			
			if (!isTimeLocked(CHARGER_1_LOCK_NAME) && charger1.handlePolling()) {
				getTimeLock(CHARGER_1_LOCK_NAME, TIME_LOCK_FOR_CHARGER);
			}
		}
	}

}
