package org.openhab.automation.jrule.rules.user.items;

import org.openhab.automation.jrule.internal.handler.JRuleEventHandler;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.rules.value.JRuleStringValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;

public class MockStringItem extends MockJRuleItem implements JRuleStringItem {

	
	public MockStringItem(String name) {
		super(name);
		//this.value = new JRuleStringValue("");
	}

	@Override
    public String getStateAsString() {
        return value.toString();
    }
	
	@Override
	public void sendCommand(JRuleStringValue command) {
		this.value = command;
    }
	
	@Override
	public void sendUncheckedCommand(JRuleValue command) {
		this.value = (JRuleStringValue) command;
    }

}
