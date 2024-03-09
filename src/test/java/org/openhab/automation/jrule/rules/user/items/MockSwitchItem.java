package org.openhab.automation.jrule.rules.user.items;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.openhab.automation.jrule.items.JRuleSwitchItem;
import org.openhab.automation.jrule.rules.value.JRuleOnOffValue;

public class MockSwitchItem extends MockJRuleItem implements JRuleSwitchItem {

	
	public MockSwitchItem(String name) {
		super(name);
		this.value = JRuleOnOffValue.ON;
	}

	@Override
    public JRuleOnOffValue getStateAsOnOff() {
        return (JRuleOnOffValue) value;
    }
	
	@Override
	public void sendCommand(JRuleOnOffValue command) {
		this.value = command;
    }
	
	@Override
	public Optional<Double> maximumSince(ZonedDateTime timestamp, String persistenceServiceId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<Double> minimumSince(ZonedDateTime timestamp, String persistenceServiceId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<Double> varianceSince(ZonedDateTime timestamp, String persistenceServiceId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<Double> deviationSince(ZonedDateTime timestamp, String persistenceServiceId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<Double> averageSince(ZonedDateTime timestamp, String persistenceServiceId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<Double> sumSince(ZonedDateTime timestamp, String persistenceServiceId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

}
