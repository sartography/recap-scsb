package org.recap.camel.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;


/**
 * Created by premkb on 12/10/16.
 */
@Component
public class ActivemqRegistrar {

    /**
     *  Instantiates a new Activemq registrar to use Activemq instead of Camel's in-memory queue.
     *
     * @param camelContext     the camel context
     * @param defaultBrokerURL the default broker url
     * @throws JMSException the jms exception
     */
    @Autowired
    public ActivemqRegistrar(CamelContext camelContext , @Value("${activemq.broker.url}") String defaultBrokerURL) throws JMSException {
    	ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(defaultBrokerURL);
        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setConnectionFactory(connectionFactory);
        activeMQComponent.setTrustAllPackages(true);
        camelContext.addComponent("scsbactivemq", activeMQComponent);
    }

}
