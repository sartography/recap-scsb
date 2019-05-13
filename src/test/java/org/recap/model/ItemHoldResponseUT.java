package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 24/3/17.
 */
public class ItemHoldResponseUT extends BaseTestCase {

    @Test
    public void testItemHoldResponse(){
        ItemHoldResponse itemHoldResponse = ItemHoldResponse
    		.builder()
	        .available(true)
	        .transactionDate(new Date().toString())
	        .institutionID("1")
	        .patronIdentifier("43256835645")
	        .titleIdentifier("test")
	        .expirationDate(new Date().toString())
	        .pickupLocation("PB")
	        .queuePosition("1")
	        .bibId("1")
	        .ISBN("1")
	        .LCCN("1")
	        .trackingId("1")
	        .jobId("1")
	        .updatedDate(new Date().toString())
	        .createdDate(new Date().toString())
	        .build();
        
        assertNotNull(itemHoldResponse);

    }



}