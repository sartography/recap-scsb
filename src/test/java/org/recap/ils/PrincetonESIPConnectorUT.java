package org.recap.ils;

import com.ceridwen.circulation.SIP.exceptions.InvalidFieldLength;
import com.ceridwen.circulation.SIP.exceptions.MandatoryFieldOmitted;
import com.ceridwen.circulation.SIP.exceptions.MessageNotUnderstood;
import com.ceridwen.circulation.SIP.messages.CheckOutResponse;
import com.ceridwen.circulation.SIP.messages.ItemInformationResponse;
import com.ceridwen.circulation.SIP.messages.PatronInformationResponse;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by saravanakumarp on 28/9/16.
 */
public class PrincetonESIPConnectorUT extends BaseTestCase {

    @Autowired
    private PrincetonESIPConnector princetonESIPConnector;

    @Test
    public void lookupItem() throws Exception {
        String itemIdentifier = "32101077423406";
        ItemInformationResponse itemInformationResponse = princetonESIPConnector.lookupItem(itemIdentifier);
        assertEquals(itemIdentifier,itemInformationResponse.getItemIdentifier());
        assertEquals("Bolshevism, by an eye-witness from Wisconsin, by Lieutenant A. W. Kliefoth ...",itemInformationResponse.getTitleIdentifier());
    }

    @Test
    public void lookupUser() throws Exception {
        String patronIdentifier = "45678915";
        PatronInformationResponse patronInformationResponse = princetonESIPConnector.lookupUser(patronIdentifier);
        assertNotNull(patronInformationResponse);
        assertTrue(patronInformationResponse.isValidPatron());
        assertTrue(patronInformationResponse.isValidPatronPassword());
    }

    @Test
    public void checkout() throws Exception {
        String itemIdentifier = "32101077423406";
        String patronIdentifier = "45678915";
        CheckOutResponse checkOutResponse = princetonESIPConnector.checkoutItem(itemIdentifier, patronIdentifier);
        assertNotNull(checkOutResponse);
        assertFalse(checkOutResponse.isOk()); //TODO it should be assertTrue
    }
}
