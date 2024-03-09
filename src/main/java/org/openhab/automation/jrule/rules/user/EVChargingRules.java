package org.openhab.automation.jrule.rules.user;

import java.time.Duration;
import java.util.function.Consumer;

import org.openhab.automation.jrule.internal.handler.JRuleTimerHandler;
import org.openhab.automation.jrule.internal.handler.JRuleTimerHandler.JRuleTimer;
import org.openhab.automation.jrule.rules.JRule;
import org.openhab.automation.jrule.rules.JRuleName;
import org.openhab.automation.jrule.rules.JRuleWhenItemChange;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

public class EVChargingRules extends JRule {
 
	static final Duration TIME_LOCK_FOR_CHARGER = Duration.ofSeconds(30);
	static final Duration CHARGER_POLLING_DURATION = Duration.ofSeconds(60);
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
			if (charger1.handleExportPower(event.getState())) {
				getTimeLock(RULE_NAME_EXPORT_POWER, TIME_LOCK_FOR_CHARGER);
			} else if (charger2.handleExportPower(event.getState())) {
				getTimeLock(RULE_NAME_EXPORT_POWER, TIME_LOCK_FOR_CHARGER);
			}
        }
	}
	
	@JRuleName(CHARGER_POLLING_RULE_NAME)
	@JRuleWhenItemChange(item = "evcr_charger_polling_switch")
	protected void pollChargers(JRuleItemEvent event) {
		logInfo(CHARGER_POLLING_RULE_NAME + " from: {} to: {}", event.getOldState(), event.getState());
		if (event.getState() == JRuleOnOffValue.ON) {
			if (isTimerRunning(CHARGER_POLLING_RULE_NAME)) {
				return;
			}
			createTimer(CHARGER_POLLING_RULE_NAME, CHARGER_POLLING_DURATION, new Consumer<JRuleTimerHandler.JRuleTimer>() {
				@Override
				public void accept(JRuleTimer t) {
	            	logInfo(CHARGER_POLLING_RULE_NAME + " Polling Chargers.");
	            	if (!charger1.handlePolling()) {
	            		charger2.handlePolling();
	            	}
				}
	        });
		} else {
			cancelTimer(CHARGER_POLLING_RULE_NAME);
		}
	}

}
