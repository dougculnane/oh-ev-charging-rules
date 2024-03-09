package org.openhab.automation.jrule.rules.user;

import java.time.Duration;

import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenCronTrigger;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.openhab.automation.jrule.rules.event.JRuleTimerEvent;

public class EVChargingRules extends JRule {
 
	static final Duration TIME_LOCK_FOR_CHARGER = Duration.ofSeconds(5);   // 30 for real
	static final Duration CHARGER_POLLING_DURATION = Duration.ofSeconds(10); // 60 for real.
	static final String CHARGER_POLLING_RULE_NAME = "evcr_charger_polling";
	static final String RULE_NAME_EXPORT_POWER = "evcr_export_power";
	
	protected static Charger charger1 = new Charger(1);
	protected static Charger charger2 = new Charger(2);
	
	@JRuleName(RULE_NAME_EXPORT_POWER)
	@JRuleWhenItemChange(item = "evcr_export_power")
	public void evcr_ExportPowerRule(JRuleItemEvent event) {
		logInfo(RULE_NAME_EXPORT_POWER + " from: {} to: {}", event.getOldState(), event.getState());
		if (isTimeLocked(RULE_NAME_EXPORT_POWER)) {
			logInfo(RULE_NAME_EXPORT_POWER + ": locked");
		} else {
			double wattsExported = event.getState() != null ? Double.valueOf(event.getState().toString()) : 0;
			if (charger1.handleExportPower(wattsExported)) {
				getTimeLock(RULE_NAME_EXPORT_POWER, TIME_LOCK_FOR_CHARGER);
			} else if (charger2.handleExportPower(wattsExported)) {
				getTimeLock(RULE_NAME_EXPORT_POWER, TIME_LOCK_FOR_CHARGER);
			}
        }
	}
	
	@JRuleName(CHARGER_POLLING_RULE_NAME)
	@JRuleWhenCronTrigger(cron = "*/10 * * * * * *")
	public void pollChargers(JRuleTimerEvent event) {
		logInfo(CHARGER_POLLING_RULE_NAME + " Cron Trigger.");
		if (!charger1.handlePolling()) {
    		charger2.handlePolling();
    	}
	}

}
