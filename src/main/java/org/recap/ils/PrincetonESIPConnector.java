package org.recap.ils;

import com.ceridwen.circulation.SIP.server.MessageHandlerDummyImpl;
import com.ceridwen.circulation.SIP.server.SocketDaemon;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by chenchulakshmig on 6/10/16.
 */
@Service
public class PrincetonESIPConnector extends ESIPConnector {

    @Value("${ils.princeton}")
    private String princetonILS;

    static SocketDaemon thread;

    public PrincetonESIPConnector() {
        PrincetonESIPConnector.thread = new SocketDaemon(princetonILS, 7031, new MessageHandlerDummyImpl());
        PrincetonESIPConnector.thread.setStrictChecksumChecking(true);
        PrincetonESIPConnector.thread.start();
    }

    @Override
    public String getHost() {
        return princetonILS;
    }
}
