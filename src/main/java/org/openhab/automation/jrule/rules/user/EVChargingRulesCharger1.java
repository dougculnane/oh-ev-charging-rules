package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.items.JRuleItem;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.openhab.automation.jrule.rules.value.JRuleValue;

public class EVChargingRulesCharger1 extends EVChargingRules {

	private static final String CHANGER_NUMBER = "1";
	static final String RULE_NAME_MODE_CHANGE = "evcr_charger_" + CHANGER_NUMBER + "_ModeChangeRule";
	static final String RULE_NAME_TOGGLE_RULE = "evcr_charger_" + CHANGER_NUMBER + "_ToggleRule";
	
	@JRuleName(RULE_NAME_MODE_CHANGE)
	@JRuleWhenItemChange(item = "evcr_charger_" + CHANGER_NUMBER + "_mode")
	public void evcr_charger_1_ModeChangeRule(JRuleItemEvent event) {
		JRuleValue state = event.getState();
		logDebug(RULE_NAME_MODE_CHANGE + " from: {} to: {}", event.getOldState(), state);
		if (state != null && state.stringValue() != null) {
			charger1.handleMode(state.stringValue());
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
		JRuleItem item = event.getItem();
		if (item != null && item.getName() != null && event.getState() != null) {				
			String ruleName = item.getName()
					.replace("evcr_charger_" + CHANGER_NUMBER + "_", "")
					.replace("_switch", "");
			if (event.getState().stringValue() == "ON") {
				charger1.enableRule(ruleName);
			} else {
				charger1.disableRule(ruleName);
			}
		}
	}

}
