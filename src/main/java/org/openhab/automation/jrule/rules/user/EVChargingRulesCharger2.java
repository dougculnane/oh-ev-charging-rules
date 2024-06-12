package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.items.JRuleItem;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.openhab.automation.jrule.rules.value.JRuleValue;

public class EVChargingRulesCharger2 extends EVChargingRules {

	private static final String CHANGER_NUMBER = "2";
	static final String RULE_NAME_MODE_CHANGE = "evcr_charger_" + CHANGER_NUMBER + "_ModeChangeRule";
	static final String RULE_NAME_TOGGLE_RULE = "evcr_charger_" + CHANGER_NUMBER + "_ToggleRule";
	
	@JRuleName(RULE_NAME_MODE_CHANGE)
	@JRuleWhenItemChange(item = "evcr_charger_" + CHANGER_NUMBER + "_mode")
	public void evcr_charger_2_ModeChangeRule(JRuleItemEvent event) {
		JRuleValue state = event.getState();
		logDebug(RULE_NAME_MODE_CHANGE + " from: {} to: {}", event.getOldState(), state);
		if (state != null && state.stringValue() != null) {
			charger2.handleMode(state.stringValue());
		}
	}
	
	@JRuleName(RULE_NAME_TOGGLE_RULE)
	@JRuleWhenItemChange(item = "evcr_charger_" + CHANGER_NUMBER + "_CHEAP_switch")
	@JRuleWhenItemChange(item = "evcr_charger_" + CHANGER_NUMBER + "_USE_EXPORT_switch")
	@JRuleWhenItemChange(item = "evcr_charger_" + CHANGER_NUMBER + "_BEST_GRID_switch")
	@JRuleWhenItemChange(item = "evcr_charger_" + CHANGER_NUMBER + "_TARGET_switch")
	@JRuleWhenItemChange(item = "evcr_charger_" + CHANGER_NUMBER + "_TIMER_switch")
	public void evcr_charger_2_EnableRule(JRuleItemEvent event) {
		JRuleItem item = event.getItem();
		JRuleValue state = event.getState();
		if (item != null && item.getName() != null && state != null) {
			logDebug(RULE_NAME_TOGGLE_RULE + " {}: {}", item.getName(), state);
			String ruleName = item.getName()
					.replace("evcr_charger_" + CHANGER_NUMBER + "_", "")
					.replace("_switch", "");
			if (state.stringValue() == "ON") {
				charger2.enableRule(ruleName);
			} else {
				charger2.disableRule(ruleName);
			}
		}
	}

}
