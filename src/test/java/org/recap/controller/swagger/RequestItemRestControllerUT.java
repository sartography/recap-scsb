package org.recap.controller.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ReCAPConstants;
import org.recap.model.ItemRequestInformation;
import org.recap.model.ItemResponseInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 4/11/16.
 */
public class RequestItemRestControllerUT extends BaseTestCase{

    private Logger logger = Logger.getLogger(RequestItemRestControllerUT.class);

    @Autowired
    RequestItemRestController requestItemRestController;

    @Autowired
    CamelContext camelContext;
    @Autowired
    private ProducerTemplate producer;

    @Autowired
    ConsumerTemplate consumer;

    @Test
    public void testValidateItemRequestBorrowDirectRequest() throws JSONException {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setRequestType("Borrow Direct");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setPatronBarcode("45678915");

        ResponseEntity responseEntity = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(), ReCAPConstants.VALID_REQUEST);
    }

    @Test
    public void testValidateRequestWithInvalidRequestingInst() throws JSONException {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setRequestType("Borrow Direct");
        itemRequestInformation.setRequestingInstitution("PULd");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");

        ResponseEntity responseEntity = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(),ReCAPConstants.INVALID_REQUEST_INSTITUTION+"\n");
    }

    @Test
    public void testValidateRequestWithInvalidPatronBarcode() throws JSONException {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("g75dfgsf");
        itemRequestInformation.setRequestType("Borrow Direct");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");

        ResponseEntity responseEntity = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(),"Patron barcode not found");
    }

    @Test
    public void testValidateRequestWithEDDRequestType() throws JSONException {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setRequestType("EDD");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");

        ResponseEntity responseEntity = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(),ReCAPConstants.START_PAGE_AND_END_PAGE_REQUIRED+"\n");
    }

    /**
     * Request Item Not Available for Retrival
     */
    @Test
    public void testRequestItem() {
        ItemResponseInformation itemResponseInformation = null;
        try {
            ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
            itemRequestInformation.setItemBarcodes(Arrays.asList("PULTST54322"));
            itemRequestInformation.setPatronBarcode("45678912");
            itemRequestInformation.setRequestType(ReCAPConstants.REQUEST_TYPE_RETRIEVAL);
            itemRequestInformation.setRequestingInstitution(ReCAPConstants.PRINCETON);
            itemRequestInformation.setEmailAddress("ksudhish@gmail.com");
            itemRequestInformation.setDeliveryLocation("PA");
            itemRequestInformation.setTitleIdentifier("100 days :$bin the Land of the Thousand Hills");
            itemResponseInformation = requestItemRestController.itemRequest(itemRequestInformation);

        } catch (Exception e) {
            logger.error("UT Execption: ",e);
        }
        assertNotNull(itemResponseInformation);
        assertTrue(itemResponseInformation.isSuccess());
    }

    @Test
    public void testScenario01(){
        ItemResponseInformation itemResponseInformation = null;
        try {
            ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
            itemRequestInformation.setItemBarcodes(Arrays.asList("PULTST54333"));
            itemRequestInformation.setPatronBarcode("45678912");
            itemRequestInformation.setRequestType(ReCAPConstants.REQUEST_TYPE_RETRIEVAL);
            itemRequestInformation.setRequestingInstitution(ReCAPConstants.PRINCETON);
            itemRequestInformation.setEmailAddress("ksudhish@gmail.com");
            itemRequestInformation.setDeliveryLocation("PA");
            itemRequestInformation.setTitleIdentifier("100 days :$bin the Land of the Thousand Hills");
            itemResponseInformation = requestItemRestController.itemRequest(itemRequestInformation);

        } catch (Exception e) {
            logger.error("UT Execption: ",e);
        }
        assertNotNull(itemResponseInformation);
        assertTrue(itemResponseInformation.isSuccess());
    }

    /**
     *  Failure when Item not found in SCSB
     */
    @Test
    public void testScenario02(){
        ItemResponseInformation itemResponseInformation = null;
        try {
            ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
            itemRequestInformation.setItemBarcodes(Arrays.asList("PULTST54329"));
            itemRequestInformation.setPatronBarcode("45678912");
            itemRequestInformation.setRequestType(ReCAPConstants.REQUEST_TYPE_RETRIEVAL);
            itemRequestInformation.setRequestingInstitution(ReCAPConstants.PRINCETON);
            itemRequestInformation.setEmailAddress("ksudhish@gmail.com");
            itemRequestInformation.setDeliveryLocation("PA");
            itemRequestInformation.setTitleIdentifier("100 days :$bin the Land of the Thousand Hills");
            itemResponseInformation = requestItemRestController.itemRequest(itemRequestInformation);

        } catch (Exception e) {
            logger.error("UT Execption: ",e);
        }
        assertNotNull(itemResponseInformation);
        assertTrue(itemResponseInformation.isSuccess());
    }

    /**
     * Recall is not Checked Out in ILS
     */
    @Test
    public void testScenario03(){
        ItemResponseInformation itemResponseInformation = null;
        try {
            ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
            itemRequestInformation.setItemBarcodes(Arrays.asList("PULTST54322"));
            itemRequestInformation.setPatronBarcode("45678913");
            itemRequestInformation.setRequestType(ReCAPConstants.REQUEST_TYPE_RECALL);
            itemRequestInformation.setRequestingInstitution(ReCAPConstants.PRINCETON);
            itemRequestInformation.setEmailAddress("ksudhish@gmail.com");
            itemRequestInformation.setDeliveryLocation("PA");
            itemRequestInformation.setTitleIdentifier("100 days :$bin the Land of the Thousand Hills");
            itemResponseInformation = requestItemRestController.itemRequest(itemRequestInformation);

        } catch (Exception e) {
            logger.error("UT Execption: ",e);
        }
        assertNotNull(itemResponseInformation);
        assertTrue(itemResponseInformation.isSuccess());
    }


    @Test
    public void testScenario04(){
        ItemResponseInformation itemResponseInformation = null;
        try {
            ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
            itemRequestInformation.setItemBarcodes(Arrays.asList("PULTST54333"));
            itemRequestInformation.setPatronBarcode("RECAPTST01");
            itemRequestInformation.setRequestType(ReCAPConstants.REQUEST_TYPE_RETRIEVAL);
            itemRequestInformation.setRequestingInstitution(ReCAPConstants.COLUMBIA);
            itemRequestInformation.setEmailAddress("ksudhish@gmail.com");
            itemRequestInformation.setDeliveryLocation("PA");
            itemRequestInformation.setTitleIdentifier("100 days :$bin the Land of the Thousand Hills");
            itemResponseInformation = requestItemRestController.itemRequest(itemRequestInformation);

        } catch (Exception e) {
            logger.error("UT Execption: ",e);
        }
        assertNotNull(itemResponseInformation);
        assertTrue(itemResponseInformation.isSuccess());
    }


    @Test
    public void testScenario05(){

    }


    @Test
    public void testScenario06(){

    }

    @Test
    public void testQueue() {
        String testString ="Test";
        String body="";
        try {
            producer.sendBody(ReCAPConstants.REQUEST_ITEM_QUEUE, testString);
            logger.info("Start");
        }catch(Exception e){
            logger.error(e);
        }
    }

    @Test
    public void testQueuewithObject() {
        logger.info("Send Item Request ");
        try {
            ItemRequestInformation itemRequestInformation=new ItemRequestInformation();
            itemRequestInformation.setItemBarcodes(Arrays.asList("PULTST54322"));
            itemRequestInformation.setPatronBarcode("45678912");
            itemRequestInformation.setRequestType(ReCAPConstants.REQUEST_TYPE_RETRIEVAL);
            itemRequestInformation.setRequestingInstitution(ReCAPConstants.PRINCETON);
            itemRequestInformation.setEmailAddress("ksudhish@gmail.com");
            itemRequestInformation.setDeliveryLocation("PA");
            itemRequestInformation.setTitleIdentifier("New BooK");

            String json = "";
            ObjectMapper objectMapper= new ObjectMapper();
            json = objectMapper.writeValueAsString(itemRequestInformation);

            producer.sendBodyAndHeader(ReCAPConstants.REQUEST_ITEM_QUEUE, json, ReCAPConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInformation.getRequestType());
        } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
        }catch(Exception e){
            logger.error(e.getMessage());
        }
    }
}