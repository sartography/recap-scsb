package org.recap.ils;

import com.ceridwen.circulation.SIP.server.MessageHandlerDummyImpl;
import com.ceridwen.circulation.SIP.server.SocketDaemon;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by chenchulakshmig on 6/10/16.
 */

public class NewYorkESIPConnector extends ESIPConnector{

    @Value("${ils.newyork}")
    private String newYorkILS;

    static SocketDaemon thread;

    public NewYorkESIPConnector(){
        NewYorkESIPConnector.thread = new SocketDaemon(newYorkILS, 7031, new MessageHandlerDummyImpl());
        NewYorkESIPConnector.thread.setStrictChecksumChecking(true);
        NewYorkESIPConnector.thread.start();
    }

    @Override
    public String getHost() {
        return newYorkILS;
    }

    @Override
    public String getOperatorUserId() {
        return null;
    }

    @Override
    public String getOperatorPassword() {
        return null;
    }

    @Override
    public String getOperatorLocation() {
        return null;
    }
}
