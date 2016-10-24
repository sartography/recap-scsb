package org.recap.ils;

import com.ceridwen.circulation.SIP.server.MessageHandlerDummyImpl;
import com.ceridwen.circulation.SIP.server.SocketDaemon;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by chenchulakshmig on 6/10/16.
 */
@Service
public class ColumbiaESIPConnector extends ESIPConnector {

    @Value("${ils.columbia}")
    private String columbiaILS;

    static SocketDaemon thread;

    public ColumbiaESIPConnector() {
        ColumbiaESIPConnector.thread = new SocketDaemon(columbiaILS, 7031, new MessageHandlerDummyImpl());
        ColumbiaESIPConnector.thread.setStrictChecksumChecking(true);
        ColumbiaESIPConnector.thread.start();
    }

    @Override
    public String getHost() {
        return columbiaILS;
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
