package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 24/3/17.
 */
public class ItemCheckinResponseUT extends BaseTestCase {

    @Test
    public void testItemCheckinResponse() {
        ItemCheckinResponse itemCheckinResponse = new ItemCheckinResponse();
        itemCheckinResponse.setAlert(false);
        itemCheckinResponse.setMagneticMedia(true);
        itemCheckinResponse.setResensitize(true);
        itemCheckinResponse.setTransactionDate(new Date().toString());
        itemCheckinResponse.setInstitutionID("1");
        itemCheckinResponse.setPatronIdentifier("45213436588");
        itemCheckinResponse.setTitleIdentifier("test");
        itemCheckinResponse.setDueDate(new Date().toString());
        itemCheckinResponse.setFeeAmount("156");
        itemCheckinResponse.setMediaType("test");
        itemCheckinResponse.setBibId("1");
        itemCheckinResponse.setISBN("145345");
        itemCheckinResponse.setLCCN("454558");
        itemCheckinResponse.setPermanentLocation("test");
        itemCheckinResponse.setSortBin("test");
        itemCheckinResponse.setCollectionCode("test");
        itemCheckinResponse.setCallNumber("X");
        itemCheckinResponse.setDestinationLocation("CUL");
        itemCheckinResponse.setAlertType("test");
        itemCheckinResponse.setHoldPatronId("1");
        itemCheckinResponse.setHoldPatronName("test");
        itemCheckinResponse.setJobId("1");
        itemCheckinResponse.setFeeType("test");
        itemCheckinResponse.setProcessed(true);
        itemCheckinResponse.setUpdatedDate(new Date().toString());
        itemCheckinResponse.setCreatedDate(new Date().toString());
        itemCheckinResponse.setSecurityInhibit("test");
        itemCheckinResponse.setCurrencyType("test");
        assertNotNull(itemCheckinResponse);
    }

}