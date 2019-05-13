package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 24/3/17.
 */
public class ItemInformationResponseUT extends BaseTestCase{

    @Test
    public void testItemInformationResponse(){
        ItemInformationResponse itemInformationResponse = ItemInformationResponse
    		.builder()
	        .expirationDate(new Date().toString())
	        .titleIdentifier("test")
	        .dueDate(new Date().toString())
	        .circulationStatus("test")
	        .securityMarker("test")
	        .feeType("test")
	        .transactionDate(new Date().toString())
	        .holdQueueLength("1")
	        .holdPickupDate(new Date().toString())
	        .recallDate(new Date().toString())
	        .owner("PUL")
	        .mediaType("test")
	        .permanentLocation("test")
	        .currentLocation("test")
	        .bibID("1")
	        .ISBN("254564")
	        .LCCN("524545578")
	        .currencyType("test")
	        .callNumber("56465")
	        .itemType("test")
	        .bibIds(Arrays.asList("254"))
	        .source("test")
	        .createdDate(new Date().toString())
	        .updatedDate(new Date().toString())
	        .deletedDate(new Date().toString())
	        .isDeleted(false)
	        .build();
        
        assertNotNull(itemInformationResponse);
    }

}