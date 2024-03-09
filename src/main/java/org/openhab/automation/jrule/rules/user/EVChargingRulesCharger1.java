package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;

public class EVChargingRulesCharger1 extends EVChargingRules {

	
	static final String RULE_NAME_MODE_CHANGE = "evcr_charger_1_ModeChangeRule";
	static final String RULE_NAME_ENABLE_RULE = "evcr_charger_1_EnableRule";
	static final String RULE_NAME_DISABLE_RULE = "evcr_charger_1_DisableRule";
	
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
	
	@JRuleName(RULE_NAME_ENABLE_RULE)
	public void evcr_charger_1_EnableRule(JRuleItemEvent event) {
		logInfo(RULE_NAME_ENABLE_RULE + ": {}", event.getState());
		if (event.getState() != null && event.getState().stringValue() != null) {
			charger1.enableRule(event.getState().stringValue());
		}
	}
	
	@JRuleName(RULE_NAME_DISABLE_RULE)
	public void evcr_charger_1_DisableRule(JRuleItemEvent event) {
		logInfo(RULE_NAME_DISABLE_RULE + ": {}", event.getState());
		if (event.getState() != null && event.getState().stringValue() != null) {
			charger1.disableRule(event.getState().stringValue());
		}
	}
	
}
