package org.recap.ils;

import com.ceridwen.circulation.SIP.exceptions.*;
import com.ceridwen.circulation.SIP.messages.*;
import com.ceridwen.circulation.SIP.messages.Message;
import com.ceridwen.circulation.SIP.server.MessageHandlerDummyImpl;
import com.ceridwen.circulation.SIP.server.SocketDaemon;
import com.ceridwen.circulation.SIP.transport.SocketConnection;
import com.ceridwen.circulation.SIP.types.enumerations.ProtocolVersion;
import com.ceridwen.circulation.SIP.types.flagfields.SupportedMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by saravanakumarp on 22/9/16.
 */
@Service
public class ESIPConnector {

    @Value("${ils.princeton}")
    private String princetonILS;

    static SocketDaemon thread;

    public ESIPConnector(){
        ESIPConnector.thread = new SocketDaemon(princetonILS, 7031, new MessageHandlerDummyImpl());
        ESIPConnector.thread.setStrictChecksumChecking(true);
        ESIPConnector.thread.start();

    }

    public Message checkOut(String institionId, String itemIdentifier, java.util.Date transactionDate) {
        Message request, response;
        String output = null;
        com.ceridwen.circulation.SIP.transport.SocketConnection connection;

        connection = new SocketConnection();
        ((SocketConnection) connection).setHost(princetonILS);
        ((SocketConnection) connection).setPort(7031);
        ((SocketConnection) connection).setConnectionTimeout(30000);
        ((SocketConnection) connection).setIdleTimeout(30000);
        ((SocketConnection) connection).setRetryAttempts(2);
        ((SocketConnection) connection).setRetryWait(500);

        try {
            connection.connect();


        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }

        /**
         * It is necessary to send a SC Status with protocol version 2.0 else
         * server will assume 1.0)
         */

        request = new SCStatus();
        ((SCStatus) request).setProtocolVersion(ProtocolVersion.VERSION_2_00);

        try {
            response = connection.send(request);
        } catch (RetriesExceeded e) {
            e.printStackTrace();
            return null;
        } catch (ConnectionFailure e) {
            e.printStackTrace();
            return null;
        } catch (MessageNotUnderstood e) {
            e.printStackTrace();
            return null;
        } catch (ChecksumError e) {
            e.printStackTrace();
            return null;
        } catch (SequenceError e) {
            e.printStackTrace();
            return null;
        } catch (MandatoryFieldOmitted e) {
            e.printStackTrace();
            return null;
        } catch (InvalidFieldLength e) {
            e.printStackTrace();
            return null;
        }

        if (!(response instanceof ACSStatus)) {
            System.err.println("Error - Status Request did not return valid response from server.");
            return null;

        }

        /**
         * For debugging XML handling code (but could be useful in Cocoon)
         */
        //response.xmlEncode(System.out);

        /**
         * Check if the server can support checkout
         */
        if (!((ACSStatus) response).getSupportedMessages().isSet(SupportedMessages.CHECK_OUT)) {
            System.out.println("Check out not supported");
            return null;
        }

        ItemInformation requestiteminfo = new ItemInformation();

        /**
         * The code below would be the normal way of creating the request
         */

        ((ItemInformation) requestiteminfo).setInstitutionId(institionId);
        ((ItemInformation) requestiteminfo).setItemIdentifier(itemIdentifier);
        ((ItemInformation) requestiteminfo).setTransactionDate(transactionDate);


        try {
            response = connection.send(requestiteminfo);
        } catch (RetriesExceeded e) {
            e.printStackTrace();
            return null;
        } catch (ConnectionFailure e) {
            e.printStackTrace();
            return null;
        } catch (MessageNotUnderstood e) {
            e.printStackTrace();
            return null;
        } catch (ChecksumError e) {
            e.printStackTrace();
            return null;
        } catch (SequenceError e) {
            e.printStackTrace();
            return null;
        } catch (MandatoryFieldOmitted e) {
            e.printStackTrace();
            return null;
        } catch (InvalidFieldLength e) {
            e.printStackTrace();
            return null;
        }
        response.xmlEncode(System.out);
        connection.disconnect();
        return response;
    }

    public void stopServer() {
        /**
         * Stop simple socket server
         */

        ESIPConnector.thread.shutdown();
    }
}


