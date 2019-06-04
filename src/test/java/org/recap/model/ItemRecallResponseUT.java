package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 24/3/17.
 */
public class ItemRecallResponseUT extends BaseTestCase {

    @Test
    public void testItemRecallResponse() {
        ItemRecallResponse itemRecallResponse = new ItemRecallResponse();
        itemRecallResponse.setAvailable(true);
        itemRecallResponse.setTransactionDate(new Date().toString());
        itemRecallResponse.setInstitutionID("1");
        itemRecallResponse.setPatronIdentifier("463556464");
        itemRecallResponse.setTitleIdentifier("test");
        itemRecallResponse.setExpirationDate(new Date().toString());
        itemRecallResponse.setPickupLocation("PB");
        itemRecallResponse.setQueuePosition("1");
        itemRecallResponse.setBibId("1");
        itemRecallResponse.setISBN("25464");
        itemRecallResponse.setLCCN("424242");
        assertNotNull(itemRecallResponse);
    }

}