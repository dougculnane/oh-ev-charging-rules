package com.github.dougculnane.oh_ev_charging_rules;

import java.util.Calendar;

import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.rules.user.OpenHabEnvironment;

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
		double neededW = (batterSizeKW * 1000 * (target-level)) / 100;
		Double minutes = neededW * 60 / chargeRateWatts;
		return minutes.intValue();
	}
	
	public boolean targetLevelReached() {
		return getBatteryLevel() >= getTargetLevel();
	}
	
	public boolean maxLevelReached() {
		return getBatteryLevel() >= getMaxLevel();
	}	
	
	private int getBatterySize() {
		JRuleNumberItem item = getBatterySizelItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsDecimal().intValue();
		}
		return 50;
	}
	
	public int getBatteryLevel() {
		JRuleNumberItem item = getBatteryLevelItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsDecimal().intValue();
		}
		return 0;
	}
	
	public int getMaxLevel() {
		JRuleNumberItem item = getMaxLevelItem();
		if (item != null && item.getState() != null) {
			return item.getStateAsDecimal().intValue();
		}
		return 100;		
	}
	
	public int getTargetLevel() {
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
				int hour = Integer.valueOf(value[0]);
				int mins = Integer.valueOf(value[1]);
				if (cal.get(Calendar.HOUR_OF_DAY) > hour 
						|| (cal.get(Calendar.HOUR_OF_DAY) == hour && cal.get(Calendar.MINUTE) > mins)) {
					cal.add(Calendar.HOUR_OF_DAY, 24);
				}
				cal.set(Calendar.HOUR_OF_DAY, hour);
				cal.set(Calendar.MINUTE, mins);
				return cal;
			}
		}
		return null;
	}
	
	protected JRuleNumberItem getBatteryLevelItem() {
		return openHabEnvironment.getNumberItem("evcr_car_" + number + "_battery_level");
	}
	protected JRuleNumberItem getBatterySizelItem() {
		return openHabEnvironment.getNumberItem("evcr_car_" + number + "_battery_size");
	}
	protected JRuleNumberItem getTargetLevelItem() {
		return openHabEnvironment.getNumberItem("evcr_car_" + number + "_target_level");
	}
	protected JRuleNumberItem getMaxLevelItem() {
		return openHabEnvironment.getNumberItem("evcr_car_" + number + "_max_level");
	}
	protected JRuleStringItem getTargetTimeItem() {
		return openHabEnvironment.getStringItem("evcr_car_" + number + "_target_time");
	}
	protected JRuleStringItem getNameItem() {
		return openHabEnvironment.getStringItem("evcr_car_" + number + "_name");
	}
	
}
