package org.openhab.automation.jrule.rules.user;

import java.util.Calendar;

import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.items.JRuleStringItem;

public class Car {

	final private OpenHabEnvironment openHabEnvironment;
	
	int number;
	
	public Car(OpenHabEnvironment openHabEnvironment, int number) {
		this.openHabEnvironment = openHabEnvironment;
		this.number = number;
	}
	
	public String getName() {
		JRuleStringItem item = getNameItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsString();
		}
		return "evcr_car_" + number;
	}
	
	public int getMinutesNeededForTarget(double chargeRateWatts) {
		int target = getTargetLevel();;
		int level = getBatteryLevel();
		int batterSizeKW =  getBatterySize();
		double neededKW = (target-level) / 100 * batterSizeKW;
		Double minutes = neededKW * 60000 / chargeRateWatts;
		if (target > 90) {
			minutes = minutes + 60; // add hours for slower charging rate.
		}
		minutes = minutes + 30; // Add half an hours for losses.
		return minutes.intValue();
	}
	
	public boolean targetLevelReached() {
		return getBatteryLevel() >= getTargetLevel();
	}
	
	
	private int getBatterySize() {
		// TODO Auto-generated method stub
		return 50;
	}
	
	private int getBatteryLevel() {
		JRuleNumberItem item = getBatteryLevelItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsDecimal().intValue();
		}
		return 0;
	}
	
	
	private int getTargetLevel() {
		JRuleNumberItem item = getTargetLevelItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsDecimal().intValue();
		}
		return 100;
	}

	public Calendar getTargetTime() {
		JRuleStringItem item = getTargetTimeItem();
		if (item != null && item.getState() != null) {
			String[] value = item.getStateAsString().split(":");
			if (value.length == 2) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(value[0]));
				cal.set(Calendar.MINUTE, Integer.valueOf(value[1]));
				return cal;
			}
		}
		return null;
	}
	
	protected JRuleNumberItem getBatteryLevelItem() {
		return openHabEnvironment.getNumberItem("evcr_car_" + number + "_battery_level");
	}
	protected JRuleNumberItem getTargetLevelItem() {
		return openHabEnvironment.getNumberItem("evcr_car_" + number + "_target_level");
	}
	protected JRuleStringItem getTargetTimeItem() {
		return openHabEnvironment.getStringItem("evcr_car_" + number + "_target_time");
	}
	protected JRuleStringItem getNameItem() {
		return openHabEnvironment.getStringItem("evcr_car_" + number + "_name");
	}
	
}
