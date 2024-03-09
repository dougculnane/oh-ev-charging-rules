package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;

public class EVChargingRulesCharger1 extends EVChargingRules {

	static final String RULE_NAME_MODE_CHANGE = "evcr_charger_1_ModeChangeRule";
	static final String RULE_NAME_TOGGLE_RULE = "evcr_charger_1_ToggleRule";
	
	@JRuleName(RULE_NAME_MODE_CHANGE)
	@JRuleWhenItemChange(item = "evcr_charger_1_mode")
	public void evcr_charger_1_ModeChangeRule(JRuleItemEvent event) {
		logInfo(RULE_NAME_MODE_CHANGE + " from: {} to: {}", event.getOldState(), event.getState());
		if (event.getState() != null && event.getState().stringValue() != null) {
			if (isTimeLocked(RULE_NAME_MODE_CHANGE)) {
				logInfo(RULE_NAME_MODE_CHANGE + ": locked");
			} else if (charger1.handleMode(event.getState().stringValue())) {
				getTimeLock(RULE_NAME_MODE_CHANGE, TIME_LOCK_FOR_CHARGER);
	        }
		}
	}
	
	@JRuleName(RULE_NAME_TOGGLE_RULE)
	@JRuleWhenItemChange(item = "evcr_charger_1_CHEAP_switch")
	@JRuleWhenItemChange(item = "evcr_charger_1_USE_EXPORT_switch")
	@JRuleWhenItemChange(item = "evcr_charger_1_TARGET_switch")
	@JRuleWhenItemChange(item = "evcr_charger_1_TIMER_switch")
	public void evcr_charger_1_EnableRule(JRuleItemEvent event) {
		logInfo(RULE_NAME_TOGGLE_RULE + " {}: {}", event.getItem().getName(), event.getState());
		if (event.getState() != null && event.getState().stringValue() != null) {
			String ruleName = event.getItem().getName()
					.replace("evcr_charger_1_", "")
					.replace("_switch", "");
			if (event.getState().stringValue() == "ON") {
				charger1.enableRule(ruleName);
			} else {
				charger1.disableRule(ruleName);
			}
		}
	}

}
