package org.openhab.automation.jrule.rules.user.items;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.automation.jrule.items.JRuleItem;
import org.openhab.automation.jrule.items.metadata.JRuleItemMetadata;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.types.State;

public class TestItem implements JRuleItem {

	String name;
	JRuleValue value;
	
	public TestItem(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
    public JRuleValue getState() {
        return value;
    }
	
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NonNullByDefault Map<String, JRuleItemMetadata> getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addMetadata(String namespace, JRuleItemMetadata metadata) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public @NonNullByDefault List<String> getTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<ZonedDateTime> lastUpdated(String persistenceServiceId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public boolean changedSince(ZonedDateTime timestamp, String persistenceServiceId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updatedSince(ZonedDateTime timestamp, String persistenceServiceId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<JRuleValue> getHistoricState(ZonedDateTime timestamp, String persistenceServiceId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<JRuleValue> getStateAt(ZonedDateTime timestamp, String persistenceServiceId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<State> getPreviousState(boolean skipEquals, String persistenceServiceId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public void persist(JRuleValue state, ZonedDateTime time, String persistenceServiceId) {
		// TODO Auto-generated method stub
		
	}
}
