package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;

public class EVChargingRulesCharger2 extends EVChargingRules {

	@JRuleName("evcr_charger_2_ModeChangeRule")
	@JRuleWhenItemChange(item = "evcr_charger_2_mode")
	public void evcr_charger_2_ModeChangeRule(JRuleItemEvent event) {
		logInfo("evcr_charger_2_ModeChangeRule from: {} to: {}", event.getOldState(), event.getState());
		if (event.getState() != null && event.getState().stringValue() != null) {
			charger2.handleMode(event.getState().stringValue());
		}
	}
	
	@JRuleName("evcr_charger_2_EnableRule")
	public void evcr_charger_2_EnableRule(JRuleItemEvent event) {
		logInfo("evcr_charger_2_EnableRule: {}", event.getState());
		if (event.getState() != null && event.getState().stringValue() != null) {
			charger2.enableRule(event.getState().stringValue());
		}
	}
	
	@JRuleName("evcr_charger_2_DisableRule")
	public void evcr_charger_2_DisableRule(JRuleItemEvent event) {
		logInfo("evcr_charger_2_DisableRule: {}", event.getState());
		if (event.getState() != null && event.getState().stringValue() != null) {
			charger2.disableRule(event.getState().stringValue());
		}
	}
	
}
