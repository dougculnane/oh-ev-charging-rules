package org.openhab.automation.jrule.rules.user;

import java.time.Duration;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenCronTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.openhab.automation.jrule.rules.event.JRuleTimerEvent;

import com.github.dougculnane.oh_ev_charging_rules.Charger;
import com.github.dougculnane.oh_ev_charging_rules.GoeCharger_API2;

public class EVChargingRules extends JRule {
 
	static final Duration TIME_LOCK_FOR_CHARGER = Duration.ofSeconds(91);
	static final String RULE_NAME_EXPORT_POWER = "evcr_export_power";
	static final String CHARGER_POLLING_RULE_NAME = "evcr_charger_polling";
	
	protected static Charger charger1 = new GoeCharger_API2(1);
	protected static Charger charger2 = new GoeCharger_API2(2);
	
	@JRuleName(RULE_NAME_EXPORT_POWER)
	@JRuleWhenItemChange(item = "evcr_export_power")
	public void evcr_ExportPowerRule(JRuleItemEvent event) {
		logDebug(RULE_NAME_EXPORT_POWER + " from: {} to: {}", event.getOldState(), event.getState());		
		if (event.getState() != null) {
			double wattsExported = Double.valueOf(event.getState().toString());
			boolean stateHasChanged = false;
			if (charger1.isReady() && !stateHasChanged) {
				stateHasChanged = charger1.handleExportPower(wattsExported);
			}
			if (charger2.isReady() && !stateHasChanged) {
				stateHasChanged = charger2.handleExportPower(wattsExported);
			}
		}
	}
	
	/**
	 * Poll every 1 second passed a minute for time based rules.
	 * @param event
	 */
	@JRuleName(CHARGER_POLLING_RULE_NAME)
	@JRuleWhenCronTrigger(cron = "1 * * * * * *")
	public void pollChargers(JRuleTimerEvent event) {
		logDebug(CHARGER_POLLING_RULE_NAME + " Cron Trigger.");
		if (charger1.isReady()) {
			charger1.handlePolling();
		}
		if (charger2.isReady()) {
			charger2.handlePolling();
		}
	}
}
