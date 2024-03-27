package com.github.dougculnane.oh_ev_charging_rules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.rules.user.OpenHabEnvironment;
import org.openhab.automation.jrule.rules.user.items.MockJRuleNumberItem;
import org.openhab.automation.jrule.rules.user.items.MockStringItem;

@RunWith(MockitoJUnitRunner.class)
public class TestCar {

	@Test
	void testChargerInit_Name() {
		Car car = getTestCar(1);
		assertEquals("evcr_car_1", car.getName());
		car.getNameItem().sendCommand("My Car");
		assertEquals("My Car", car.getName());
	}
	
	@Test
	void testMinutesNeededForTarget() {
		Car car = getTestCar(1);
		assertEquals(300, car.getMinutesNeededForTarget(10000));
		car.getTargetLevelItem().sendCommand(50);
		assertEquals(150, car.getMinutesNeededForTarget(10000));
		car.getBatteryLevelItem().sendCommand(50);
		assertEquals(0, car.getMinutesNeededForTarget(10000));
		car.getTargetLevelItem().sendCommand(100);
		assertEquals(150, car.getMinutesNeededForTarget(10000));
	}
	
	@Test
	void testGetTargetTime() {
		Car car = getTestCar(1);
		car.getTargetLevelItem().sendCommand(80);
		car.getBatteryLevelItem().sendCommand(50);
		assertEquals(90, car.getMinutesNeededForTarget(10000));
	}
	
	private Car getTestCar(int number) {
		final JRuleStringItem name = new MockStringItem("evcr_car_" + number + "_name");
		final JRuleNumberItem target = new MockJRuleNumberItem("evcr_car_" + number + "_target_level");
		final JRuleNumberItem battery = new MockJRuleNumberItem("evcr_car_" + number + "_battery_level");
		OpenHabEnvironment mock =  Mockito.mock(OpenHabEnvironment.class);
		Mockito.when(mock.getStringItem(name.getName())).thenReturn(name);
		Mockito.when(mock.getNumberItem(target.getName())).thenReturn(target);
		Mockito.when(mock.getNumberItem(battery.getName())).thenReturn(battery);
		return new Car(mock, number);
	}
}
