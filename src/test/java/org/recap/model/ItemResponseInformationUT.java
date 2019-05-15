package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 24/3/17.
 */
public class ItemResponseInformationUT extends BaseTestCase{

    @Test
    public void testItemResponseInformation(){
        ItemResponseInformation itemResponseInformation = ItemResponseInformation
    		.builder()
	        .patronBarcode("453454564")
	        .itemBarcodes(Arrays.asList("4534"))
	        .requestType("Recall")
	        .deliveryLocation("PB")
	        .requestingInstitution("CUL")
	        .bibliographicId("1")
	        .expirationDate(new Date().toString())
	        .screenMessage("test")
	        .success(true)
	        .emailAddress("hemalatha.s@htcindia.com")
	        .titleIdentifier("test")
	        .build();

        assertNotNull(itemResponseInformation);
    }
}