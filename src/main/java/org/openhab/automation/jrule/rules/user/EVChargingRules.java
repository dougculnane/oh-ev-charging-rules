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
 
	static final Duration TIME_LOCK_FOR_CHARGER = Duration.ofSeconds(60);
	static final String CHARGER_1_LOCK_NAME = "evcr_charger_1_lock";
	static final String CHARGER_2_LOCK_NAME = "evcr_charger_2_lock";
	static final String RULE_NAME_EXPORT_POWER = "evcr_export_power";
	static final String CHARGER_POLLING_RULE_NAME = "evcr_charger_polling";
	
	protected static Charger charger1 = new GoeCharger_API2(1);
	protected static Charger charger2 = new GoeCharger_API2(2);
	
	@JRuleName(RULE_NAME_EXPORT_POWER)
	@JRuleWhenItemChange(item = "evcr_export_power")
	public void evcr_ExportPowerRule(JRuleItemEvent event) {
		logDebug(RULE_NAME_EXPORT_POWER + " from: {} to: {}", event.getOldState(), event.getState());
		
		double wattsExported = event.getState() != null ? Double.valueOf(event.getState().toString()) : 0;
		
		if (isTimeLocked(CHARGER_1_LOCK_NAME)) {
			logDebug(CHARGER_1_LOCK_NAME + ": locked so skipping export power change");
		} else if (charger1.handleExportPower(wattsExported)) {
			getTimeLock(CHARGER_1_LOCK_NAME, TIME_LOCK_FOR_CHARGER);
		}
		
		if (isTimeLocked(CHARGER_2_LOCK_NAME)) {
			logDebug(CHARGER_2_LOCK_NAME + ": locked so skipping export power change");
		} else if (charger2.handleExportPower(wattsExported)) {
			getTimeLock(CHARGER_2_LOCK_NAME, TIME_LOCK_FOR_CHARGER);
		}
	}
	
	@JRuleName(CHARGER_POLLING_RULE_NAME)
	@JRuleWhenCronTrigger(cron = "*/10 * * * * * *")
	public void pollChargers(JRuleTimerEvent event) {
		logDebug(CHARGER_POLLING_RULE_NAME + " Cron Trigger.");
		
		if (isTimeLocked(CHARGER_1_LOCK_NAME)) {
			logDebug(CHARGER_1_LOCK_NAME + ": locked so skipping polling");
		} else {
			if (charger1.handlePolling()) {
				getTimeLock(CHARGER_1_LOCK_NAME, TIME_LOCK_FOR_CHARGER);
			}
		}
		
		if (isTimeLocked(CHARGER_2_LOCK_NAME)) {
			logDebug(CHARGER_2_LOCK_NAME + ": locked so skipping polling");
		} else {
			if (charger2.handlePolling()) {
				getTimeLock(CHARGER_2_LOCK_NAME, TIME_LOCK_FOR_CHARGER);
			}
		}
	}

}
