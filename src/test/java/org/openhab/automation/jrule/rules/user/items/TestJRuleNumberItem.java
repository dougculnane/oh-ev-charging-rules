package org.openhab.automation.jrule.rules.user.items;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;

public class TestJRuleNumberItem extends TestItem implements JRuleNumberItem {

	public TestJRuleNumberItem(String name) {
		super(name);
		this.value = new JRuleDecimalValue("0");
	}
	
	@Override
	public JRuleDecimalValue getStateAsDecimal() {
        return (JRuleDecimalValue) value;
    }
	
	@Override
	public void sendCommand(JRuleDecimalValue command) {
		this.value = command;
    }
	
	@Override
	public void sendCommand(double command) {
		sendCommand(new JRuleDecimalValue(command));
    }
	
	@Override
	public void sendCommand(int command) {
		sendCommand(new JRuleDecimalValue(command));
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
