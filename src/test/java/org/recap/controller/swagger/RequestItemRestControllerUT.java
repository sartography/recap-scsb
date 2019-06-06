package org.recap.controller.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.ReCAPConstants;
import org.recap.common.model.CancelRequestResponse;
import org.recap.controller.BaseControllerUT;
import org.recap.model.*;
import org.recap.service.RestHeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

public class RequestItemRestControllerUT extends BaseControllerUT {
    private static final Logger logger = LogManager.getLogger(RequestItemRestControllerUT.class);
    @Value("${scsb.circ.url}")
    String scsbCircUrl;
    @Mock
    private RequestItemRestController requestItemRestController;
    @SuppressWarnings("unused")
    @Mock
    private RequestItemRestController getRequestItemRestController;
    @SuppressWarnings("unused")
    @MockBean
    private CamelContext camelContext;
    @MockBean
    private ProducerTemplate producer;
    @Mock
    private ProducerTemplate producerTemplate;
    @Mock
    private ConsumerTemplate consumer;

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @MockBean
    private RestHeaderService restHeaderService;

    public String getScsbCircUrl() {
        String url = scsbCircUrl;
        return scsbCircUrl;
    }

    public void setScsbCircUrl(String scsbCircUrl) {
        this.scsbCircUrl = scsbCircUrl;
    }

    @Test
    public void testValidRequest() throws JSONException {
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(ReCAPConstants.VALID_REQUEST, HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = ItemRequestInformation.builder()
                .itemBarcodes(Arrays.asList("32101047717911"))
                .titleIdentifier("105 paintings in the John Herron Art Museum.").itemOwningInstitution("PUL")
                .requestType("EDD").requestingInstitution("CUL").emailAddress("hemalatha.s@htcindia.com")
                .patronBarcode("45678915").deliveryLocation("PB").startPage("I").endPage("XII").build();

        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations",
                itemRequestInformation, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.validateItemRequest(itemRequestInformation)).thenCallRealMethod();
        ResponseEntity<?> responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals(responseEntity.getBody(), ReCAPConstants.VALID_REQUEST);
    }

    @Test
    public void testRequestWithInvalidRequestingInst() throws JSONException {
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(
                ReCAPConstants.INVALID_REQUEST_INSTITUTION + "\n", HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = ItemRequestInformation.builder().patronBarcode("45678915")
                .requestType("Borrow Direct").itemOwningInstitution("CUL").requestingInstitution("PULd")
                .emailAddress("hemalatha.s@htcindia.com").build();

        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations",
                itemRequestInformation, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.validateItemRequest(itemRequestInformation)).thenCallRealMethod();
        ResponseEntity<?> responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals(responseEntity.getBody(), ReCAPConstants.INVALID_REQUEST_INSTITUTION + "\n");
    }

    @Test
    public void testRequestParameterWithInvalidPatronBarcode() throws JSONException {
        ResponseEntity<String> responseEntity = new ResponseEntity<String>("Patron barcode not found", HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = ItemRequestInformation.builder().patronBarcode("g75dfgsf")
                .requestType("Borrow Direct").requestingInstitution("PUL").emailAddress("hemalatha.s@htcindia.com")
                .build();

        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations",
                itemRequestInformation, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.validateItemRequest(itemRequestInformation)).thenCallRealMethod();
        ResponseEntity<?> responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals(responseEntity.getBody(), "Patron barcode not found");
    }

    @Test
    public void testRequestParameterWithEDDRequestType() throws JSONException {
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(
                ReCAPConstants.START_PAGE_AND_END_PAGE_REQUIRED + "\n", HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = ItemRequestInformation.builder().patronBarcode("45678915")
                .requestType("EDD").requestingInstitution("PUL").emailAddress("hemalatha.s@htcindia.com").build();

        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations",
                itemRequestInformation, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.validateItemRequest(itemRequestInformation)).thenCallRealMethod();
        ResponseEntity<?> responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals(responseEntity.getBody(), ReCAPConstants.START_PAGE_AND_END_PAGE_REQUIRED + "\n");
    }

    @Test
    public void testCancelRequest() {
        HttpEntity<Object> request = new HttpEntity<>(restHeaderService.getHttpHeaders());
        CancelRequestResponse cancelRequestResponse = CancelRequestResponse.builder()
                .screenMessage("Request cancelled.").success(true).build();
        ResponseEntity<CancelRequestResponse> responseEntity = new ResponseEntity<CancelRequestResponse>(
                cancelRequestResponse, HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(getScsbCircUrl() + ReCAPConstants.URL_REQUEST_CANCEL).queryParam("requestId", 129);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getRestHeaderService()).thenReturn(restHeaderService);
        Mockito.when(requestItemRestController.getRestTemplate().exchange(builder.build().encode().toUri(),
                org.springframework.http.HttpMethod.POST, request, CancelRequestResponse.class))
                .thenReturn(responseEntity);
        Mockito.when(requestItemRestController.cancelRequest(129)).thenCallRealMethod();
        cancelRequestResponse = requestItemRestController.cancelRequest(129);
        assertNotNull(cancelRequestResponse);
        assertEquals(cancelRequestResponse.getScreenMessage(), "Request cancelled.");
        assertTrue(cancelRequestResponse.isSuccess());
    }

    @Test
    public void testcheckinItemRequest() throws IOException {
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(ReCAPConstants.PRINCETON, HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        ItemCheckInRequest itemCheckInRequest = new ItemCheckInRequest();
        itemCheckInRequest.setPatronIdentifier("45678915");
        itemCheckInRequest.setItemBarcodes(Arrays.asList("123"));
        itemCheckInRequest.setItemOwningInstitution("PUL");

        ItemRequestInformation itemRequestInfo = ItemRequestInformation.builder()
                .patronBarcode(itemCheckInRequest.getPatronIdentifier())
                .itemBarcodes(itemCheckInRequest.getItemBarcodes())
                .itemOwningInstitution(itemCheckInRequest.getItemOwningInstitution())
                .requestingInstitution(itemCheckInRequest.getItemOwningInstitution()).build();

        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemCheckinResponse.class))
                .thenReturn(getItemCheckInResponse());
        Mockito.when(requestItemRestController.getRestTemplate()
                .postForEntity(getScsbCircUrl() + "requestItem/checkinItem", itemRequestInfo, null, String.class))
                .thenReturn(responseEntity);
        Mockito.when(requestItemRestController.checkinItemRequest(itemCheckInRequest)).thenReturn(getItemCheckInResponse());
        AbstractResponseItem abstractResponseItem = requestItemRestController.checkinItemRequest(itemCheckInRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.getItemBarcode().contains("123"));
    }

    @Test
    public void testItemInformationPUL() {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setItemOwningInstitution("PUL");
        itemInformationResponse.setSuccess(true);
        itemInformationResponse.setBibID("111");
        itemInformationResponse.setCreatedDate(new Date().toString());

        ResponseEntity<ItemInformationResponse> responseEntity = new ResponseEntity<>(itemInformationResponse,
                HttpStatus.OK);
        ItemInformationRequest itemInformationRequest = new ItemInformationRequest();
        itemInformationRequest.setItemBarcodes(Arrays.asList("233"));
        itemInformationRequest.setItemOwningInstitution("PUL");

        HttpEntity<ItemInformationRequest> request = new HttpEntity<ItemInformationRequest>(itemInformationRequest);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getRestTemplate().exchange(
                getScsbCircUrl() + ReCAPConstants.URL_REQUEST_ITEM_INFORMATION,
                org.springframework.http.HttpMethod.POST, request, ItemInformationResponse.class))
                .thenReturn(responseEntity);
        Mockito.when(requestItemRestController.itemInformation(itemInformationRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.itemInformation(itemInformationRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());
    }

    @Test
    public void testItemInformationCUL() {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setItemOwningInstitution("CUL");
        itemInformationResponse.setSuccess(true);
        itemInformationResponse.setBibID("111");
        itemInformationResponse.setCreatedDate(new Date().toString());

        ResponseEntity<ItemInformationResponse> responseEntity = new ResponseEntity<>(itemInformationResponse,
                HttpStatus.OK);
        ItemInformationRequest itemInformationRequest = new ItemInformationRequest();

        itemInformationRequest.setItemBarcodes(Arrays.asList("233"));
        itemInformationRequest.setItemOwningInstitution("PUL");

        HttpEntity<ItemInformationRequest> request = new HttpEntity<ItemInformationRequest>(itemInformationRequest);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getRestTemplate().exchange(
                getScsbCircUrl() + ReCAPConstants.URL_REQUEST_ITEM_INFORMATION,
                org.springframework.http.HttpMethod.POST, request, ItemInformationResponse.class))
                .thenReturn(responseEntity);
        Mockito.when(requestItemRestController.itemInformation(itemInformationRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.itemInformation(itemInformationRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());
    }

    @Test
    public void testcancelHoldItemRequest() throws IOException {
        ItemHoldCancelRequest itemHoldCancelRequest = new ItemHoldCancelRequest();
        itemHoldCancelRequest.setItemBarcodes(Arrays.asList("3216549874100225"));
        itemHoldCancelRequest.setItemOwningInstitution("PUL");
        itemHoldCancelRequest.setPickupLocation("PB");
        itemHoldCancelRequest.setBibId("3256");
        itemHoldCancelRequest.setPatronIdentifier("456328965");
        itemHoldCancelRequest.setTrackingId("2");

        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setAvailable(true);
        itemHoldResponse.setBibId("14533");
        itemHoldResponse.setTransactionDate(new Date().toString());
        itemHoldResponse.setCreatedDate(new Date().toString());
        itemHoldResponse.setExpirationDate(new Date().toString());
        itemHoldResponse.setInstitutionID("PUL");
        itemHoldResponse.setSuccess(true);

        ItemRequestInformation itemRequestInfo = ItemRequestInformation.builder().build();
        ResponseEntity<String> responseEntity = new ResponseEntity<String>("test", HttpStatus.OK);
        String response = responseEntity.getBody();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getRestTemplate()
                .postForEntity(getScsbCircUrl() + "requestItem/cancelHoldItem", itemRequestInfo, String.class))
                .thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemHoldResponse.class))
                .thenReturn(itemHoldResponse);
        Mockito.when(requestItemRestController.cancelHoldItemRequest(itemHoldCancelRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController
                .cancelHoldItemRequest(itemHoldCancelRequest);
        assertNotNull(abstractResponseItem);

    }

    @Test
    public void testItemRefile() {
        ItemRefileRequest itemRefileRequest = ItemRefileRequest.builder().itemBarcode("123").requestId(1).build();
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        itemRefileResponse.setScreenMessage("Successfully Refiled");
        itemRefileResponse.setSuccess(true);

        ResponseEntity<ItemRefileResponse> responseEntity = new ResponseEntity<ItemRefileResponse>(itemRefileResponse,
                HttpStatus.OK);

        HttpEntity<ItemRefileRequest> request = new HttpEntity<ItemRefileRequest>(itemRefileRequest);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getRestTemplate().exchange(
                getScsbCircUrl() + ReCAPConstants.URL_REQUEST_RE_FILE, org.springframework.http.HttpMethod.POST,
                request, ItemRefileResponse.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.refileItem(itemRefileRequest)).thenCallRealMethod();
        ItemRefileResponse itemRefileResponse1 = requestItemRestController.refileItem(itemRefileRequest);
        assertNotNull(itemRefileResponse1);
        assertNotNull(itemRefileRequest.getItemBarcodes());
        assertNotNull(itemRefileRequest.getRequestIds());
        assertNotNull(itemRefileResponse.getScreenMessage());
        assertNotNull(itemRefileResponse.isSuccess());
    }

    @Test
    public void testCheckoutItemRequest() throws IOException {
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(ReCAPConstants.PRINCETON, HttpStatus.OK);
        String response = responseEntity.getBody();
        ItemCheckOutRequest itemCheckOutRequest = new ItemCheckOutRequest();
        itemCheckOutRequest.setPatronIdentifier("45678915");
        itemCheckOutRequest.setItemBarcodes(Arrays.asList("123"));
        itemCheckOutRequest.setItemOwningInstitution("PUL");

        ItemRequestInformation itemRequestInfo = ItemRequestInformation.builder()
                .patronBarcode(itemCheckOutRequest.getPatronIdentifier())
                .itemBarcodes(itemCheckOutRequest.getItemBarcodes())
                .itemOwningInstitution(itemCheckOutRequest.getItemOwningInstitution())
                .requestingInstitution(itemCheckOutRequest.getItemOwningInstitution()).build();

        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemCheckoutResponse.class))
                .thenReturn(getItemCheckoutResponse());
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/checkoutItem", itemRequestInfo,
                String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.checkoutItemRequest(itemCheckOutRequest)).thenCallRealMethod();
        ItemCheckoutResponse itemCheckoutResponse = requestItemRestController.checkoutItemRequest(itemCheckOutRequest);
        assertNotNull(itemCheckoutResponse);

    }

    @Test
    public void testHoldRequestItem() throws Exception {
        System.out.println("\n\n\n\n*** testHoldRequestItem ***\n\n");

        ItemHoldRequest itemHoldRequest = new ItemHoldRequest();
        itemHoldRequest.setItemOwningInstitution("PUL");
        itemHoldRequest.setTrackingId("564");
        itemHoldRequest.setCallNumber("5645");
        itemHoldRequest.setItemBarcodes(Arrays.asList("32101047717911"));
        itemHoldRequest.setPatronIdentifier("42659872");
        itemHoldRequest.setBibId("14533");
        itemHoldRequest.setAuthor("John");
        itemHoldRequest.setPickupLocation("PB");

        ItemRequestInformation itemRequestInfo = ItemRequestInformation
                .builder()
                .itemBarcodes(itemHoldRequest.getItemBarcodes())
                .itemOwningInstitution(itemHoldRequest.getItemOwningInstitution())
                .requestingInstitution(itemHoldRequest.getItemOwningInstitution())
                .patronBarcode(itemHoldRequest.getPatronIdentifier())
                .bibId(itemHoldRequest.getBibId())
                .deliveryLocation(itemHoldRequest.getPickupLocation())
                .trackingId(itemHoldRequest.getTrackingId())
                .titleIdentifier(itemHoldRequest.getTitle())
                .author(itemHoldRequest.getAuthor())
                .callNumber(itemHoldRequest.getCallNumber())
                .build();

        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setAvailable(true);
        itemHoldResponse.setBibId(itemHoldRequest.getBibId());
        itemHoldResponse.setTransactionDate(new Date().toString());
        itemHoldResponse.setCreatedDate(new Date().toString());
        itemHoldResponse.setExpirationDate(new Date().toString());
        itemHoldResponse.setInstitutionID(itemHoldRequest.getItemOwningInstitution());
        itemHoldResponse.setSuccess(true);

        ResponseEntity<ItemHoldResponse> responseEntity = new ResponseEntity<>(itemHoldResponse, HttpStatus.OK);
        String url = getScsbCircUrl() + ReCAPConstants.URL_REQUEST_ITEM_HOLD;
        Mockito.when(requestItemRestController._post(url, itemRequestInfo, ItemHoldResponse.class)).thenReturn(responseEntity);
        AbstractResponseItem abstractResponseItem = requestItemRestController.holdItemRequest(itemHoldRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());
    }


    @Test
    public void testCreateBibRequest() throws Exception {
        ItemCreateBibRequest itemCreateBibRequest = new ItemCreateBibRequest();
        itemCreateBibRequest.setPatronIdentifier("4568723");
        itemCreateBibRequest.setTitleIdentifier("test");
        itemCreateBibRequest.setItemBarcodes(Arrays.asList("4564"));
        itemCreateBibRequest.setItemOwningInstitution("PUL");

        ItemCreateBibResponse itemCreateBibResponse = new ItemCreateBibResponse();
        itemCreateBibResponse.setBibId("45564");
        itemCreateBibResponse.setItemId("5886");
        itemCreateBibResponse.setSuccess(true);

        ItemRequestInformation itemRequestInfo = ItemRequestInformation.builder().build();
        ResponseEntity<ItemCreateBibResponse> responseEntity = new ResponseEntity<ItemCreateBibResponse>(
                itemCreateBibResponse, HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getRestTemplate().postForEntity(
                getScsbCircUrl() + ReCAPConstants.URL_REQUEST_ITEM_CREATEBIB, itemRequestInfo,
                ItemCreateBibResponse.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemCreateBibResponse.class))
                .thenReturn(itemCreateBibResponse);
        Mockito.when(requestItemRestController.createBibRequest(itemCreateBibRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.createBibRequest(itemCreateBibRequest);
        assertNotNull(abstractResponseItem);

        assertTrue(abstractResponseItem.isSuccess());
        assertNotNull(itemCreateBibResponse.getBibId());
        assertNotNull(itemCreateBibResponse.getItemId());
    }

    @Test
    public void testRecallItem() throws IOException {
        ItemRecallRequest itemRecallRequest = new ItemRecallRequest();
        itemRecallRequest.setItemBarcodes(Arrays.asList("254"));
        itemRecallRequest.setItemOwningInstitution("PUL");
        itemRecallRequest.setPatronIdentifier("4856956");
        itemRecallRequest.setBibId("4563");
        itemRecallRequest.setPickupLocation("PB");

        ItemRecallResponse itemRecallResponse = new ItemRecallResponse();
        itemRecallResponse.setAvailable(true);
        itemRecallResponse.setPatronIdentifier("4569325");
        itemRecallResponse.setInstitutionID("PUL");
        itemRecallResponse.setExpirationDate(new Date().toString());
        itemRecallResponse.setSuccess(true);

        ItemRequestInformation itemRequestInfo = ItemRequestInformation.builder().build();
        ResponseEntity<ItemRecallResponse> responseEntity = new ResponseEntity<ItemRecallResponse>(itemRecallResponse,
                HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getRestTemplate().postForEntity(
                getScsbCircUrl() + ReCAPConstants.URL_REQUEST_ITEM_RECALL, itemRequestInfo, ItemRecallResponse.class))
                .thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemRecallResponse.class))
                .thenReturn(itemRecallResponse);
        Mockito.when(requestItemRestController.recallItem(itemRecallRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.recallItem(itemRecallRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());

    }

    @Test
    public void testPatronInformation() {
        PatronInformationRequest patronInformationRequest = PatronInformationRequest.builder()
                .patronIdentifier("4562398").itemOwningInstitution("PUL").build();

        PatronInformationResponse patronInformationResponse = new PatronInformationResponse();
        patronInformationResponse.setPatronIdentifier("45623298");
        patronInformationResponse.setPatronName("John");
        patronInformationResponse.setPickupLocation("PB");
        patronInformationResponse.setDueDate(new Date().toString());
        patronInformationResponse.setExpirationDate(new Date().toString());
        patronInformationResponse.setEmail("hemalatha.s@htcindia.com");
        patronInformationResponse.setScreenMessage("Patron validated successfully.");

        ItemRequestInformation itemRequestInfo = ItemRequestInformation.builder().build();
        HttpEntity<ItemRequestInformation> request = new HttpEntity<ItemRequestInformation>(itemRequestInfo);
        ResponseEntity<PatronInformationResponse> responseEntity = new ResponseEntity<PatronInformationResponse>(
                patronInformationResponse, HttpStatus.OK);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getRestTemplate().exchange(
                getScsbCircUrl() + ReCAPConstants.URL_REQUEST_PATRON_INFORMATION,
                org.springframework.http.HttpMethod.POST, request, PatronInformationResponse.class))
                .thenReturn(responseEntity);
        Mockito.when(requestItemRestController.patronInformation(patronInformationRequest)).thenCallRealMethod();
        PatronInformationResponse informationResponse = requestItemRestController
                .patronInformation(patronInformationRequest);
        assertNotNull(informationResponse);
        assertEquals(informationResponse.getScreenMessage(), "Patron validated successfully.");
    }

    @Test
    public void testGetterServices() {
        Mockito.when(requestItemRestController.getRestTemplate()).thenCallRealMethod();
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenCallRealMethod();
        Mockito.when(requestItemRestController.getObjectMapper()).thenCallRealMethod();
        Mockito.when(requestItemRestController.getProducer()).thenCallRealMethod();
        assertNotEquals(requestItemRestController.getRestTemplate(), mockRestTemplate);
        assertNotEquals(requestItemRestController.getScsbCircUrl(), scsbCircUrl);
        assertNotEquals(requestItemRestController.getObjectMapper(), objectMapper);
        assertNotEquals(requestItemRestController.getProducer(), producerTemplate);
    }

    @Test
    public void testRequestItem() {
        ItemResponseInformation itemResponseInformation = null;
        ResponseEntity<String> responseEntity = new ResponseEntity<String>("Success", HttpStatus.OK);
        try {
            ItemRequestInformation itemRequestInfo = ItemRequestInformation.builder().patronBarcode("32101077423406")
                    .itemBarcodes(Arrays.asList("3652147896532455")).requestType(ReCAPConstants.REQUEST_TYPE_RETRIEVAL)
                    .requestingInstitution(ReCAPConstants.PRINCETON).emailAddress("ksudhish@gmail.com").build();

            Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
            Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
            Mockito.when(requestItemRestController.getProducer()).thenReturn(producerTemplate);
            Mockito.when(requestItemRestController.getRestTemplate().postForEntity(
                    getScsbCircUrl() + ReCAPConstants.URL_REQUEST_ITEM_VALIDATE_ITEM_REQUEST, itemRequestInfo,
                    String.class)).thenReturn(responseEntity);
            Mockito.when(requestItemRestController.itemRequest(itemRequestInfo)).thenCallRealMethod();
            itemResponseInformation = requestItemRestController.itemRequest(itemRequestInfo);
            assertNotNull(itemResponseInformation);
            assertTrue(itemResponseInformation.isSuccess());
            assertEquals(itemResponseInformation.getScreenMessage(),
                    "Message received, your request will be processed");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQueue() {
        try {
            String testString = "Test";
            String endpointUri = "scsbactivemq:queue:Request.Item";
            producer.sendBody(endpointUri, testString);
            logger.info("Start");
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Test
    public void testTopic() {
        try {
            String testString = "Test";
            String endpointUri = "scsbactivemq:topic:PUL.Request";
            producer.sendBody(endpointUri, testString);
            logger.info("Start");
            Exchange receive = consumer.receive(endpointUri);
            logger.info(receive.getOut().getBody());
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Test
    public void testQueuewithObject() {
        logger.info("Send Item Request ");
        try {
            ItemRequestInformation itemRequestInformation = ItemRequestInformation.builder()
                    .itemBarcodes(Arrays.asList("PULTST54321")).patronBarcode("45678913")
                    .requestType(ReCAPConstants.REQUEST_TYPE_RETRIEVAL).requestingInstitution(ReCAPConstants.PRINCETON)
                    .emailAddress("ksudhish@gmail.com").deliveryLocation("htcsc").titleIdentifier("New BooK").build();

            String json = "";
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(itemRequestInformation);

            producer.sendBodyAndHeader(ReCAPConstants.REQUEST_ITEM_QUEUE, json,
                    ReCAPConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInformation.getRequestType());
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private ItemCheckinResponse getItemCheckInResponse() {
        ItemCheckinResponse itemCheckinResponse = new ItemCheckinResponse();
        itemCheckinResponse.setBibId("234");
        itemCheckinResponse.setCreatedDate(new Date().toString());
        itemCheckinResponse.setDueDate(new Date().toString());
        itemCheckinResponse.setItemBarcode("123");
        itemCheckinResponse.setItemOwningInstitution("PUL");
        itemCheckinResponse.setPatronIdentifier("45678915");
        itemCheckinResponse.setScreenMessage("Item checked in successfully");
        itemCheckinResponse.setSuccess(true);
        return itemCheckinResponse;
    }

    private ItemCheckoutResponse getItemCheckoutResponse() {
        ItemCheckoutResponse itemCheckoutResponse = new ItemCheckoutResponse();
        itemCheckoutResponse.setDesensitize(true);
        itemCheckoutResponse.setDueDate(new Date().toString());
        itemCheckoutResponse.setItemBarcode("12345");
        itemCheckoutResponse.setMagneticMedia(true);
        itemCheckoutResponse.setPatronIdentifier("45697123");
        itemCheckoutResponse.setProcessed(true);
        itemCheckoutResponse.setRenewal(true);
        itemCheckoutResponse.setSuccess(true);
        return itemCheckoutResponse;
    }

}