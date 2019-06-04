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
        ItemCheckoutResponse itemCheckoutResponse = new ItemCheckoutResponse();
		itemCheckoutResponse.setRenewal(true);
		itemCheckoutResponse.setMagneticMedia(true);
		itemCheckoutResponse.setDesensitize(true);
		itemCheckoutResponse.setTransactionDate(new Date().toString());
		itemCheckoutResponse.setInstitutionID("1");
		itemCheckoutResponse.setPatronIdentifier("414654557");
		itemCheckoutResponse.setTitleIdentifier("test");
		itemCheckoutResponse.setDueDate(new Date().toString());
		itemCheckoutResponse.setFeeType("test");
		itemCheckoutResponse.setMediaType("test");
		itemCheckoutResponse.setBibId("1");
		itemCheckoutResponse.setISBN("5664471");
		itemCheckoutResponse.setLCCN("56646547");
		itemCheckoutResponse.setJobId("4");
		itemCheckoutResponse.setProcessed(true);
		itemCheckoutResponse.setUpdatedDate(new Date().toString());
		itemCheckoutResponse.setCreatedDate(new Date().toString());
		itemCheckoutResponse.setSecurityInhibit("test");
		itemCheckoutResponse.setCurrencyType("test");
		itemCheckoutResponse.setFeeAmount("test");

        assertNotNull(itemCheckoutResponse);
    }




}