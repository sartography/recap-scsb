package org.recap.controller.swagger;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ReCAPConstants;
import org.recap.model.ItemRequestInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 4/11/16.
 */
public class RequestItemRestControllerUT extends BaseTestCase{

    @Autowired
    RequestItemRestController requestItemRestController;

    @Test
    public void testValidRequest(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setRequestType("Borrow Direct");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setPatronBarcode("45678915");

        ResponseEntity responseEntity = requestItemRestController.validateItemRequestInformation(itemRequestInformation);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(), ReCAPConstants.VALID_REQUEST);
    }

    @Test
    public void testRequestWithInvalidRequestingInst(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setRequestType("Borrow Direct");
        itemRequestInformation.setRequestingInstitution("PULd");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");

        ResponseEntity responseEntity = requestItemRestController.validateItemRequestInformation(itemRequestInformation);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(),ReCAPConstants.INVALID_REQUEST_INSTITUTION+"\n");
    }

    @Test
    public void testRequestParameterWithInvalidPatronBarcode(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("g75dfgsf");
        itemRequestInformation.setRequestType("Borrow Direct");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");

        ResponseEntity responseEntity = requestItemRestController.validateItemRequestInformation(itemRequestInformation);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(),"Patron barcode not found");
    }

    @Test
    public void testRequestParameterWithEDDRequestType(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setRequestType("EDD");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");

        ResponseEntity responseEntity = requestItemRestController.validateItemRequestInformation(itemRequestInformation);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(),ReCAPConstants.START_PAGE_AND_END_PAGE_REQUIRED+"\n");
    }





}