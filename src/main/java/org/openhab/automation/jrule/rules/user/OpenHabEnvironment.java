package org.openhab.automation.jrule.rules.user;

import java.util.Optional;

import org.openhab.automation.jrule.items.JRuleNumberItem;
import org.openhab.automation.jrule.items.JRuleStringItem;
import org.openhab.automation.jrule.items.JRuleSwitchItem;

public class OpenHabEnvironment {
	
	protected JRuleSwitchItem getSwitchItem(String name) {
		Optional<JRuleSwitchItem> itemOption = JRuleSwitchItem.forNameOptional(name);
		if (itemOption.isPresent()) {
			return itemOption.get();
		}
		return null;
	}

	public JRuleNumberItem getNumberItem(String name) {
		Optional<JRuleNumberItem> itemOption = JRuleNumberItem.forNameOptional(name);
		if (itemOption.isPresent()) {
			return itemOption.get();
		}
		return null;
	}
	
	public JRuleStringItem getStringItem(String name) {
		Optional<JRuleStringItem> itemOption = JRuleStringItem.forNameOptional(name);
		if (itemOption.isPresent()) {
			return itemOption.get();
		}
		return null;
	}

}
