package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 24/3/17.
 */
public class ItemRecallResponseUT extends BaseTestCase{

    @Test
    public void testItemRecallResponse(){
        ItemRecallResponse itemRecallResponse = ItemRecallResponse
    		.builder()
	        .available(true)
	        .transactionDate(new Date().toString())
	        .institutionID("1")
	        .patronIdentifier("463556464")
	        .titleIdentifier("test")
	        .expirationDate(new Date().toString())
	        .pickupLocation("PB")
	        .queuePosition("1")
	        .bibId("1")
	        .ISBN("25464")
	        .LCCN("424242")
	        .build();

        assertNotNull(itemRecallResponse);
    }

}