package com.github.dougculnane.oh_ev_charging_rules;

import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.rules.user.OpenHabEnvironment;

/**
 * Uses the Go-eCharger Binding: https://www.openhab.org/addons/bindings/goecharger/ 
 * API version 2
 */
public class GoeCharger_API2 extends Charger {

	private static final int MAX_AMPS = 16;
	
	public GoeCharger_API2(int number) {
		super(number);
	}
	
	public GoeCharger_API2(OpenHabEnvironment openHabEnvironment, int number) {
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
	boolean useExportPower(double watts) {
		int flipFlopMarginPhases = 300;
		boolean allowSwitchTo3phases = !isExport1PhaseOnly();
		Double min3PhasePower = getMinimPhase3Power();
		if (allowSwitchTo3phases && watts > min3PhasePower){
			Integer phases = getPhases();
			if (phases != null && phases == 3) {
				// remove the flop margin
				flipFlopMarginPhases = flipFlopMarginPhases * -1;
			}
		}
		int flipFlopMarginOnOff = 240;
		if (this.isOn()) {
			flipFlopMarginOnOff = flipFlopMarginOnOff * -1;
		}
		if (allowSwitchTo3phases && watts > min3PhasePower + flipFlopMarginPhases) {
			int calcAmps = Double.valueOf(watts / 240 / 3).intValue();;
			return switchOn(3, calcAmps);
		} else if (watts > getMinimPhase1Power() + flipFlopMarginOnOff) {
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
	
	@Override
	protected boolean switchOn() {
		// In API version 2 the switch is read only
		// set force state to neutral (Neutral=0, Off=1, On=2)
		super.switchOn();
		JRuleNumberItem item = getForceStateItem();
		if (item != null 
				&& (item.getState() == null	|| item.getStateAsDecimal().intValue() != 2) ) {
			item.sendCommand(2);
			return true;
		}
		return false;
	}
	
	@Override
	protected boolean switchOff() {
		// In API version 2 the switch is read only
		// set force state to neutral (Neutral=0, Off=1, On=2)
		super.switchOff();
		JRuleNumberItem item = getForceStateItem();
		if (item != null && (item.getState() == null || item.getStateAsDecimal().intValue() != 1)) {
			item.sendCommand(1);
			return true;
		}
		return false;
	}
	
	private JRuleNumberItem getForceStateItem() {
		return openHabEnvironment.getNumberItem("evcr_charger_" + number + "_state");
	}

	private boolean switchOn(int phases, int amps) {
		return setPower(phases, amps) | switchOn();
	}
	
	private boolean setPower(int phases, int amps) {
		if (amps < getMinAmps(phases)) {
			amps = getMinAmps(phases);
		}
		if (amps > MAX_AMPS) {
			amps = MAX_AMPS;
		}
		return setAmps(amps) | setPhases(phases);
	}
	
	private Double getMinimPhase1Power() {
		return Double.valueOf((240 * getMinAmps(1)));
	}
	private Double getMinimPhase3Power() {
		return Double.valueOf((240 * getMinAmps(3) * 3));
	}
	
	/**
	 * My Charger can not do 6 amps on 3 phases.
	 * 
	 * @param phases
	 * @return
	 */
	private int getMinAmps(int phases) {
		if (phases == 1) {
			return 6;
		}
		return 8;
	}

}
