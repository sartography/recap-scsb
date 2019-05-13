package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 24/3/17.
 */
public class ItemCheckinResponseUT extends BaseTestCase {

    @Test
    public void testItemCheckinResponse(){
        ItemCheckinResponse itemCheckinResponse = ItemCheckinResponse
        	.builder()
	        .alert(false)
	        .magneticMedia(true)
	        .resensitize(true)
	        .transactionDate(new Date().toString())
	        .institutionID("1")
	        .patronIdentifier("45213436588")
	        .titleIdentifier("test")
	        .dueDate(new Date().toString())
	        .feeAmount("156")
	        .mediaType("test")
	        .bibId("1")
	        .ISBN("145345")
	        .LCCN("454558")
	        .permanentLocation("test")
	        .sortBin("test")
	        .collectionCode("test")
	        .callNumber("X")
	        .destinationLocation("CUL")
	        .alertType("test")
	        .holdPatronId("1")
	        .holdPatronName("test")
	        .jobId("1")
	        .feeType("test")
	        .processed(true)
	        .updatedDate(new Date().toString())
	        .createdDate(new Date().toString())
	        .securityInhibit("test")
	        .currencyType("test")
	        .build();

        assertNotNull(itemCheckinResponse);
    }

}