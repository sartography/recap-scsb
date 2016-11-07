package org.recap.request;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ReCAPConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 3/11/16.
 */
public class PatronValidatorServiceUT extends BaseTestCase {

    @Autowired
    PatronValidatorService patronValidatorService;

    @Test
    public void testValidPatron(){
        ResponseEntity responseEntity = patronValidatorService.patronValidation("PUL","45678915");
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(),"Patron validated successfully.");
    }


    @Test
    public void testInvalidPatron(){
        ResponseEntity responseEntity = patronValidatorService.patronValidation("PUL","gd235dgg");
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(),"Patron barcode not found");
    }

    @Test
    public void testEmptyPatronBarcode(){
        ResponseEntity responseEntity = patronValidatorService.patronValidation("PUL","");
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(), ReCAPConstants.EMPTY_PATRON_BARCODE);
    }



}