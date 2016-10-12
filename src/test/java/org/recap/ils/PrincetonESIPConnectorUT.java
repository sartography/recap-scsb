package org.recap.ils;

import com.ceridwen.circulation.SIP.exceptions.InvalidFieldLength;
import com.ceridwen.circulation.SIP.exceptions.MandatoryFieldOmitted;
import com.ceridwen.circulation.SIP.exceptions.MessageNotUnderstood;
import com.ceridwen.circulation.SIP.messages.ItemInformationResponse;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

/**
 * Created by saravanakumarp on 28/9/16.
 */
public class PrincetonESIPConnectorUT extends BaseTestCase {

    @Autowired
    private PrincetonESIPConnector princetonESIPConnector;

    @Test
    public void checkOut() throws MessageNotUnderstood, InvalidFieldLength, MandatoryFieldOmitted {
        ItemInformationResponse response = (ItemInformationResponse) princetonESIPConnector.checkOut("PUL","32101077423406",new java.util.Date());
        assertEquals("32101077423406",response.getItemIdentifier());
        assertEquals("Bolshevism, by an eye-witness from Wisconsin, by Lieutenant A. W. Kliefoth ...",response.getTitleIdentifier());
    }
}
