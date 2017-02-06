package org.recap.controller.swagger;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.HttpMethod;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCase;
import org.recap.ReCAPConstants;
import org.recap.model.ItemRequestInformation;
import org.recap.model.ItemResponseInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
/**
 * Created by hemalathas on 4/11/16.
 */
public class RequestItemRestControllerUT extends BaseTestCase{
    private Logger logger = Logger.getLogger(RequestItemRestControllerUT.class);

    @Mock
    RequestItemRestController requestItemRestController;

    @Autowired
    CamelContext camelContext;

    @Autowired
    private ProducerTemplate producer;

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.circ.url}")
    String scsbCircUrl;

    @Mock
    ConsumerTemplate consumer;

    @Mock
    RestTemplate mockRestTemplate;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    public String getServerProtocol() {
        return serverProtocol;
    }

    public void setServerProtocol(String serverProtocol) {
        this.serverProtocol = serverProtocol;
    }

    public String getScsbCircUrl() {
        return scsbCircUrl;
    }

    public void setScsbCircUrl(String scsbCircUrl) {
        this.scsbCircUrl = scsbCircUrl;
    }

    @Test
    public void testValidRequest() throws JSONException {
        ResponseEntity responseEntity = new ResponseEntity(ReCAPConstants.VALID_REQUEST, HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setRequestType("Borrow Direct");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setPatronBarcode("45678915");

        Mockito.when(mockRestTemplate.postForEntity(getServerProtocol() + getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getServerProtocol()).thenReturn(serverProtocol);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.validateItemRequest(itemRequestInformation)).thenCallRealMethod();
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals(responseEntity.getBody(), ReCAPConstants.VALID_REQUEST);
    }



    @Test
    public void testRequestWithInvalidRequestingInst() throws JSONException {
        ResponseEntity responseEntity = new ResponseEntity(ReCAPConstants.INVALID_REQUEST_INSTITUTION+"\n", HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setRequestType("Borrow Direct");
        itemRequestInformation.setRequestingInstitution("PULd");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");


        Mockito.when(mockRestTemplate.postForEntity(getServerProtocol() + getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getServerProtocol()).thenReturn(serverProtocol);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.validateItemRequest(itemRequestInformation)).thenCallRealMethod();
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals(responseEntity.getBody(),ReCAPConstants.INVALID_REQUEST_INSTITUTION+"\n");
    }

    @Test
    public void testRequestParameterWithInvalidPatronBarcode() throws JSONException {
        ResponseEntity responseEntity = new ResponseEntity("Patron barcode not found", HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("g75dfgsf");
        itemRequestInformation.setRequestType("Borrow Direct");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");

        Mockito.when(mockRestTemplate.postForEntity(getServerProtocol() + getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getServerProtocol()).thenReturn(serverProtocol);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.validateItemRequest(itemRequestInformation)).thenCallRealMethod();
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals(responseEntity.getBody(),"Patron barcode not found");
    }

    @Test
    public void testRequestParameterWithEDDRequestType() throws JSONException {
        ResponseEntity responseEntity = new ResponseEntity(ReCAPConstants.START_PAGE_AND_END_PAGE_REQUIRED+"\n", HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setRequestType("EDD");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");

        Mockito.when(mockRestTemplate.postForEntity(getServerProtocol() + getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getServerProtocol()).thenReturn(serverProtocol);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.validateItemRequest(itemRequestInformation)).thenCallRealMethod();
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
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