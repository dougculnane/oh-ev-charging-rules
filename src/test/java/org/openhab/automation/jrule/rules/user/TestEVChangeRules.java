package org.openhab.automation.jrule.rules.user;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhab.automation.jrule.internal.JRuleConfig;
import org.openhab.automation.jrule.internal.engine.JRuleEngine;
import org.openhab.automation.jrule.internal.test.JRuleMockedEventBus;
import org.openhab.automation.jrule.rules.event.JRuleItemEvent;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class TestEVChangeRules {
	

    public void setUp() throws Exception {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.ALL);
    }

    @Test
    public void testMyRule() {
    	
    	//EVChangeRules evChargerRules = Mockito.mock(EVChangeRules.class);
    	 
        JRuleEngine engine = JRuleEngine.get();
        JRuleConfig config = new JRuleConfig(new HashedMap<>());
        //engine.setItemRegistry(new ItemRegistry());
        engine.setConfig(config);
        //engine.add(evChargerRules, true);
        JRuleMockedEventBus eventBus = new JRuleMockedEventBus();
        eventBus.fire("eventlog.txt");
        
        //verify(evChargerRules, times(2)).execEVCR_1_ModeChangeRule(any());
        
    	//Charger charger1 = new Charger(1);
		//charger1.handleMode("FAST");
    }
}
