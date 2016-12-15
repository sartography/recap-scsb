package org.recap.controller.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.*;
import org.apache.camel.builder.DefaultFluentProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
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

import static org.junit.Assert.*;

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
    public void testValidRequest() throws JSONException {
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
    public void testRequestWithInvalidRequestingInst() throws JSONException {
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
    public void testRequestParameterWithInvalidPatronBarcode() throws JSONException {
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
    public void testRequestParameterWithEDDRequestType() throws JSONException {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setRequestType("EDD");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");

        ResponseEntity responseEntity = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(),ReCAPConstants.START_PAGE_AND_END_PAGE_REQUIRED+"\n");
    }

    @Test
    public void testRequestItem() {
        ItemResponseInformation itemResponseInformation = null;
        try {
            ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
            itemRequestInformation.setPatronBarcode("32101077423406");
            itemRequestInformation.setRequestType(ReCAPConstants.REQUEST_TYPE_RETRIEVAL);
            itemRequestInformation.setRequestingInstitution(ReCAPConstants.PRINCETON);
            itemRequestInformation.setEmailAddress("ksudhish@gmail.com");

            itemResponseInformation = requestItemRestController.itemRequest(itemRequestInformation);

        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(itemResponseInformation);
    }

    @Test
    public void testQueue() {
        String testString ="Test";
        String body="";
        String endpointUri="scsbactivemq:queue:Request.Item";
        try {
            producer.sendBody(endpointUri, testString);
            logger.info("Start");
        }catch(Exception e){
            logger.error(e);
        }
    }

    @Test
    public void testTopic() {
        String testString ="Test";
        String body="";
        String endpointUri="scsbactivemq:topic:PUL.Request";
        try {
            producer.sendBody(endpointUri, testString);
            logger.info("Start");
            Exchange receive = consumer.receive(endpointUri);
            logger.info(receive.getOut().getBody());
        }catch(Exception e){
            logger.error(e);
        } finally {

        }
//        assertEquals(testString, body);
    }

    @Test
    public void testJMS() {
        String testString ="Test";
        String body="";
        try {

        }catch(Exception e){
            logger.error(e);
        } finally {

        }
//        assertEquals(testString, body);
    }

    @Test
    public void testQueuewithObject() {
        logger.info("Send Item Request ");
        try {
            ItemRequestInformation itemRequestInformation=new ItemRequestInformation();
            itemRequestInformation.setItemBarcodes(Arrays.asList("PULTST54321"));
            itemRequestInformation.setPatronBarcode("45678913");
            itemRequestInformation.setExpirationDate("20161231    190405");
            itemRequestInformation.setRequestType(ReCAPConstants.REQUEST_TYPE_RETRIEVAL);
            itemRequestInformation.setRequestingInstitution(ReCAPConstants.PRINCETON);
            itemRequestInformation.setEmailAddress("ksudhish@gmail.com");
            itemRequestInformation.setDeliveryLocation("htcsc");
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