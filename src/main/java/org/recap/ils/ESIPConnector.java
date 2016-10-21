package org.recap.ils;

import com.ceridwen.circulation.SIP.exceptions.*;
import com.ceridwen.circulation.SIP.messages.*;
import com.ceridwen.circulation.SIP.transport.SocketConnection;
import com.ceridwen.circulation.SIP.types.enumerations.ProtocolVersion;
import com.ceridwen.circulation.SIP.types.flagfields.SupportedMessages;
import com.sun.tools.javac.comp.Check;

/**
 * Created by saravanakumarp on 22/9/16.
 */
public abstract class ESIPConnector {

    public Message checkoutItem(String itemIdentifier, String patronIdentifier) {
        Message request, response;
        SocketConnection connection = getSocketConnection();
        if (connection == null) return null;

        /**
         * It is necessary to send a SC Status with protocol version 2.0 else
         * server will assume 1.0)
         */
        request = new SCStatus();
        ((SCStatus) request).setProtocolVersion(ProtocolVersion.VERSION_2_00);

        response = getResponse(request, connection);
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

        CheckOut checkOut = new CheckOut();
        checkOut.setItemIdentifier(itemIdentifier);
        checkOut.setPatronIdentifier(patronIdentifier);
        checkOut.setItemIdentifier("PUL");
        checkOut.setTerminalPassword("MdlW@419r&");

        response = getResponse(checkOut, connection);
        response.xmlEncode(System.out);
        connection.disconnect();
        return response;


    }

    public Message lookupItem(String institutionId, String itemIdentifier, java.util.Date transactionDate) {
        Message request, response;
        SocketConnection connection = getSocketConnection();
        if (connection == null) return null;

        /**
         * It is necessary to send a SC Status with protocol version 2.0 else
         * server will assume 1.0)
         */
        request = new SCStatus();
        ((SCStatus) request).setProtocolVersion(ProtocolVersion.VERSION_2_00);

        response = getResponse(request, connection);
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

        ItemInformation itemInformation = new ItemInformation();

        /**
         * The code below would be the normal way of creating the request
         */
        itemInformation.setInstitutionId(institutionId);
        itemInformation.setItemIdentifier(itemIdentifier);
        itemInformation.setTransactionDate(transactionDate);

        response = getResponse(itemInformation, connection);
        response.xmlEncode(System.out);
        connection.disconnect();
        return response;
    }

    public Message lookupUser(String patronIdentifier) {
        Message request, response;
        SocketConnection connection = getSocketConnection();
        if (connection == null) return null;

        request = new SCStatus();
        ((SCStatus) request).setProtocolVersion(ProtocolVersion.VERSION_2_00);

        response = getResponse(request, connection);
        if (!(response instanceof ACSStatus)) {
            System.err.println("Error - Status Request did not return valid response from server.");
            return null;
        }

       if (!((ACSStatus) response).getSupportedMessages().isSet(SupportedMessages.PATRON_INFORMATION)) {
            System.out.println("Patron Information service not supported");
            return null;
        }

        PatronInformation patronInformation = new PatronInformation();
        patronInformation.setPatronIdentifier(patronIdentifier);

        response = getResponse(patronInformation, connection);
        //response.xmlEncode(System.out);
        connection.disconnect();
        return response;
    }

    private Message getResponse(Message request, SocketConnection connection) {
        Message response;
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
        return response;
    }

    private SocketConnection getSocketConnection() {
        SocketConnection connection =new SocketConnection();

        connection.setHost(getHost());
        connection.setPort(7031);
        connection.setConnectionTimeout(30000);
        connection.setIdleTimeout(30000);
        connection.setRetryAttempts(2);
        connection.setRetryWait(500);

        try {
            connection.connect();
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
        return connection;
    }

    public abstract String getHost();

}

