package org.openhab.automation.jrule.rules.user;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;

public class EVChargingRules extends JRule {
 
	protected static Charger charger1 = new Charger(1);
	protected static Charger charger2 = new Charger(2);
	
	@JRuleName("evcr_export_power")
	@JRuleWhenItemChange(item = "evcr_export_power")
	public void evcr_ExportPowerRule(JRuleItemEvent event) {
		logInfo("evcr_ExportPowerRule from: {} to: {}", event.getOldState(), event.getState());
		if (!charger1.handleExportPower(event.getState())) {
			charger2.handleExportPower(event.getState());
		}
	}
	
}
