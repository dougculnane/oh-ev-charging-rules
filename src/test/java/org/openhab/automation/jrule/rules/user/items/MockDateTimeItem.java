package org.openhab.automation.jrule.rules.user.items;

import java.util.Date;

import org.openhab.automation.jrule.items.JRuleDateTimeItem;
import org.openhab.automation.jrule.rules.value.JRuleDateTimeValue;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;

public class MockDateTimeItem  extends MockJRuleItem implements JRuleDateTimeItem {

	
	public MockDateTimeItem(String name) {
		super(name);
	}
	
	@Override
	public void sendCommand(JRuleDateTimeValue command) {
		this.value = command;
    }
	
	@Override
	public void sendCommand(Date date) {
		this.sendCommand(new JRuleDateTimeValue(date));
    }
	
	@Override
	public JRuleDateTimeValue getStateAsDateTime() {
        return (JRuleDateTimeValue) value;
    }
	
}
