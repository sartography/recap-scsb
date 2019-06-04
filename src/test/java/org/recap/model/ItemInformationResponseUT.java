package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 24/3/17.
 */
public class ItemInformationResponseUT extends BaseTestCase{

    @Test
    public void testItemInformationResponse(){
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
		itemInformationResponse.setExpirationDate(new Date().toString());
		itemInformationResponse.setTitleIdentifier("test");
		itemInformationResponse.setDueDate(new Date().toString());
		itemInformationResponse.setCirculationStatus("test");
		itemInformationResponse.setSecurityMarker("test");
		itemInformationResponse.setFeeType("test");
		itemInformationResponse.setTransactionDate(new Date().toString());
		itemInformationResponse.setHoldQueueLength("1");
		itemInformationResponse.setHoldPickupDate(new Date().toString());
		itemInformationResponse.setRecallDate(new Date().toString());
		itemInformationResponse.setOwner("PUL");
		itemInformationResponse.setMediaType("test");
		itemInformationResponse.setPermanentLocation("test");
		itemInformationResponse.setCurrentLocation("test");
		itemInformationResponse.setBibID("1");
		itemInformationResponse.setISBN("254564");
		itemInformationResponse.setLCCN("524545578");
		itemInformationResponse.setCurrencyType("test");
		itemInformationResponse.setCallNumber("56465");
		itemInformationResponse.setItemType("test");
		itemInformationResponse.setBibIds(Arrays.asList("254"));
		itemInformationResponse.setSource("test");
		itemInformationResponse.setCreatedDate(new Date().toString());
		itemInformationResponse.setUpdatedDate(new Date().toString());
		itemInformationResponse.setDeletedDate(new Date().toString());
		itemInformationResponse.setDeleted(false);
        assertNotNull(itemInformationResponse);
    }

}