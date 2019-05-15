package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 24/3/17.
 */
public class PatronInformationResponseUT extends BaseTestCase {

    @Test
    public void testPatronInformationResponse(){
        PatronInformationResponse patronInformationResponse = PatronInformationResponse
    		.builder()
	        .patronIdentifier("455835644")
	        .patronName("john")
	        .email("hemalatha.s@htcindia.com")
	        .birthDate(new Date().toString())
	        .phone("9874563210")
	        .permanentLocation("PB")
	        .pickupLocation("PB")
	        .chargedItemsCount(1)
	        .chargedItemsLimit(1)
	        .feeLimit("1")
	        .feeType("test")
	        .holdItemsCount(1)
	        .holdItemsLimit(1)
	        .unavailableHoldsCount(1)
	        .fineItemsCount(1)
	        .feeAmount("1425")
	        .homeAddress("test")
	        .items(Arrays.asList("1454"))
	        .itemType("test")
	        .overdueItemsCount(1)
	        .overdueItemsLimit(1)
	        .pacAccessType("test")
	        .patronGroup("test")
	        .dueDate(new Date().toString())
	        .expirationDate(new Date().toString())
	        .status("testing")
	        .patronType("test")
	        .build();

        assertNotNull(patronInformationResponse);
    }

}