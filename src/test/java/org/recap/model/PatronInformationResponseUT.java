package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 24/3/17.
 */
public class PatronInformationResponseUT extends BaseTestCase {

    @Test
    public void testPatronInformationResponse() {
        PatronInformationResponse patronInformationResponse = new PatronInformationResponse();
        patronInformationResponse.setPatronIdentifier("455835644");
        patronInformationResponse.setPatronName("john");
        patronInformationResponse.setEmail("hemalatha.s@htcindia.com");
        patronInformationResponse.setBirthDate(new Date().toString());
        patronInformationResponse.setPhone("9874563210");
        patronInformationResponse.setPermanentLocation("PB");
        patronInformationResponse.setPickupLocation("PB");
        patronInformationResponse.setChargedItemsCount(1);
        patronInformationResponse.setChargedItemsLimit(1);
        patronInformationResponse.setFeeLimit("1");
        patronInformationResponse.setFeeType("test");
        patronInformationResponse.setHoldItemsCount(1);
        patronInformationResponse.setHoldItemsLimit(1);
        patronInformationResponse.setUnavailableHoldsCount(1);
        patronInformationResponse.setFineItemsCount(1);
        patronInformationResponse.setFeeAmount("1425");
        patronInformationResponse.setHomeAddress("test");
        patronInformationResponse.setItems(Arrays.asList("1454"));
        patronInformationResponse.setItemType("test");
        patronInformationResponse.setOverdueItemsCount(1);
        patronInformationResponse.setOverdueItemsLimit(1);
        patronInformationResponse.setPacAccessType("test");
        patronInformationResponse.setPatronGroup("test");
        patronInformationResponse.setDueDate(new Date().toString());
        patronInformationResponse.setExpirationDate(new Date().toString());
        patronInformationResponse.setStatus("testing");
        patronInformationResponse.setPatronType("test");
        assertNotNull(patronInformationResponse);
    }

}