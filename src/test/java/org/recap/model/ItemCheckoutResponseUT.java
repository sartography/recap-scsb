package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 24/3/17.
 */
public class ItemCheckoutResponseUT extends BaseTestCase{

    @Test
    public void testItemCheckoutResponse(){
        ItemCheckoutResponse itemCheckoutResponse = ItemCheckoutResponse
    		.builder()
	        .renewal(true)
	        .magneticMedia(true)
	        .desensitize(true)
	        .transactionDate(new Date().toString())
	        .institutionID("1")
	        .patronIdentifier("414654557")
	        .titleIdentifier("test")
	        .dueDate(new Date().toString())
	        .feeType("test")
	        .mediaType("test")
	        .bibId("1")
	        .ISBN("5664471")
	        .LCCN("56646547")
	        .jobId("4")
	        .processed(true)
	        .updatedDate(new Date().toString())
	        .createdDate(new Date().toString())
	        .securityInhibit("test")
	        .currencyType("test")
	        .feeAmount("test")
	        .build();

        assertNotNull(itemCheckoutResponse);
    }




}