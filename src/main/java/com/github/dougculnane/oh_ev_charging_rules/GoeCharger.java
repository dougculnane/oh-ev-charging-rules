package com.github.dougculnane.oh_ev_charging_rules;

import org.openhab.automation.jrule.rules.user.OpenHabEnvironment;

public class GoeCharger extends Charger {

	private static final int MAX_AMPS = 16;
	
	public GoeCharger(int number) {
		super(number);
	}
	
	public GoeCharger(OpenHabEnvironment openHabEnvironment, int number) {
		super(openHabEnvironment, number);
	}

	@Override
	protected boolean activateFastCharging() {
		return switchOn(3, MAX_AMPS);
	}

	@Override
	protected double getFastChargeRate() {
		return 240 * MAX_AMPS * 3;
	}

	@Override
	boolean switchOn(double watts) {
		if (watts > getMinimPhase3Power()) {
			int calcAmps = Double.valueOf(watts / 240 / 3).intValue();;
			return switchOn(3, calcAmps);
		} else if (watts > getMinimPhase1Power()) {
			int calcAmps = Double.valueOf(watts / 240).intValue();
			return switchOn(1, calcAmps);
		} else {
			return switchOff();
		}
	}

	@Override
	protected double getMinimPower() {
		return getMinimPhase1Power();
	}
	
	private boolean switchOn(int phases, int amps) {
		return setPower(phases, amps) | switchOn();
	}
	
	private boolean setPower(int phases, int amps) {
		boolean changes = setPhases(phases);
		if (amps < getMinAmps(phases)) {
			amps = getMinAmps(phases);
		}
		if (amps > MAX_AMPS) {
			amps = MAX_AMPS;
		}
		setAmps(amps);
		return changes;
	}
	
	private Double getMinimPhase1Power() {
		return Double.valueOf((240 * getMinAmps(1)));
	}
	private Double getMinimPhase3Power() {
		return Double.valueOf((240 * getMinAmps(3) * 3));
	}
	
	private int getMinAmps(int phases) {
		if (phases == 1) {
			return 6;
		}
		return 8;
	}

}
