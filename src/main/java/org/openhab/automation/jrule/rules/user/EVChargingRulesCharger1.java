package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;

public class EVChargingRulesCharger1 extends EVChargingRules {

	@JRuleName("evcr_charger_1_ModeChangeRule")
	@JRuleWhenItemChange(item = "evcr_charger_1_mode")
	public void evcr_charger_1_ModeChangeRule(JRuleItemEvent event) {
		logInfo("evcr_charger_1_ModeChangeRule from: {} to: {}", event.getOldState(), event.getState());
		if (event.getState() != null && event.getState().stringValue() != null) {
			charger1.handleMode(event.getState().stringValue());
		}
	}
	
	@JRuleName("evcr_charger_1_EnableRule")
	public void evcr_charger_1_EnableRule(JRuleItemEvent event) {
		logInfo("evcr_charger_1_EnableRule: {}", event.getState());
		if (event.getState() != null && event.getState().stringValue() != null) {
			charger1.enableRule(event.getState().stringValue());
		}
	}
	
	@JRuleName("evcr_charger_1_DisableRule")
	public void evcr_charger_1_DisableRule(JRuleItemEvent event) {
		logInfo("evcr_charger_1_DisableRule: {}", event.getState());
		if (event.getState() != null && event.getState().stringValue() != null) {
			charger1.disableRule(event.getState().stringValue());
		}
	}
	
}
