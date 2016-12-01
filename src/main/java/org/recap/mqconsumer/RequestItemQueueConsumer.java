package org.recap.mqconsumer;

import org.apache.camel.Body;
import org.apache.camel.Consume;
import org.apache.camel.Header;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Component;

/**
 * Created by sudhishk on 29/11/16.
 */
@Component
public class RequestItemQueueConsumer {

    private Logger logger = Logger.getLogger(RequestItemQueueConsumer.class);

    @Consume(uri = "scsbactivemq:queue:Request.Item?concurrentConsumers=3&selector=RequestType='Request'",context = "camel-1")
    public void requestItemOnMessage(@Header("JMSCorrelationID") String correlationID,@Header("RequestType") String requestType, @Body String body) {
        logger.info("Start Message Processing");
        logger.info("Body -> " +body.toString());
        logger.info("Request -> " +requestType);
    }

    @Consume(uri = "scsbactivemq:queue:Request.Item?concurrentConsumers=3&selector=RequestType='Hold'",context = "camel-1")
    public void requestItemHoldOnMessage(@Header("JMSCorrelationID") String correlationID,@Header("RequestType") String requestType, @Body String body) {
        logger.info("Start Message Processing");
        logger.info("Body -> " +body.toString());
        logger.info("Hold -> " +requestType);
    }

    @Consume(uri = "scsbactivemq:topic:PUL.Hold?concurrentConsumers=3",context = "camel-1")
    public void requestItemTopicOnMessage(@Header("JMSCorrelationID") String correlationID, @Body String body) {
        logger.info("Start Message Processing");
        logger.info("Body -> " +body.toString());
    }
}
