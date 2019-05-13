package org.recap.controller.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.recap.ReCAPConstants;
import org.recap.Service.RestHeaderService;
import org.recap.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hemalathas on 1/11/16.
 */
@RestController
@RequestMapping("/requestItem")
@Api(value = "requestItem")
public class RequestItemRestController {

    private static final Logger logger = LoggerFactory.getLogger(RequestItemRestController.class);

    @Value("${scsb.circ.url}")
    private String scsbCircUrl;

    @Autowired
    RestHeaderService restHeaderService;

    @Autowired
    private ProducerTemplate producer;

    public RestHeaderService getRestHeaderService(){
        return restHeaderService;
    }

    /**
     * Gets scsb circ url.
     *
     * @return the scsb circ url
     */
    public String getScsbCircUrl() {
        return scsbCircUrl;
    }

    /**
     * Gets rest template.
     *
     * @return the rest template
     */
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    /**
     * Gets producer.
     *
     * @return the producer
     */
    public ProducerTemplate getProducer() {
        return producer;
    }


    /**
     * Gets item request information.
     *
     * @return the item request information
     */
    public ItemRequestInformation getItemRequestInformation() {
        return new ItemRequestInformation();
    }

    /**
     * Gets item information request.
     *
     * @return the item information request
     */
    public ItemInformationRequest getItemInformationRequest() {
        return new ItemInformationRequest();
    }

    /**
     * Gets object mapper.
     *
     * @return the object mapper
     */
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    /**
     * Gets logger.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * This method will call scsb-circ microservice to place a item request in scsb.
     * @param itemRequestInfo the item request info
     * @return the item response information
     */
    @RequestMapping(value = ReCAPConstants.REST_URL_REQUEST_ITEM, method = RequestMethod.POST)
    @ApiOperation(value = "Request Item", notes = "The Request item API allows the user to raise a request (retrieve / recall / EDD) in SCSB for a valid item.", nickname = "requestItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemResponseInformation itemRequest(@ApiParam(value = "Parameters to place a request on an Item", required = true, name = "requestItemJson") @RequestBody ItemRequestInformation itemRequestInfo) {
        ItemResponseInformation itemResponseInformation = new ItemResponseInformation();
        List<?> itemBarcodes;
        HttpStatus statusCode;
        boolean bSuccess;
        String screenMessage;
        ObjectMapper objectMapper;
        ResponseEntity<String> responseEntity = null;
        try {
            logger.info("Item Request Information : {}",itemRequestInfo.toString());
            itemRequestInfo.setPatronBarcode(itemRequestInfo.getPatronBarcode() != null ? itemRequestInfo.getPatronBarcode().trim() : null);
            responseEntity = getRestTemplate().postForEntity(getScsbCircUrl() + ReCAPConstants.URL_REQUEST_ITEM_VALIDATE_ITEM_REQUEST, itemRequestInfo, String.class);
            statusCode = responseEntity.getStatusCode();
            screenMessage = responseEntity.getBody().toString();
        } catch (HttpClientErrorException httpEx) {
            logger.error("error-->", httpEx);
            statusCode = httpEx.getStatusCode();
            screenMessage = httpEx.getResponseBodyAsString();
        }
        try {
            if (statusCode != null && statusCode == HttpStatus.OK) {
                objectMapper = new ObjectMapper();
                itemBarcodes = itemRequestInfo.getItemBarcodes();
                itemRequestInfo.setItemBarcodes(null);
                itemRequestInfo.setRequestNotes(StringUtils.left(itemRequestInfo.getRequestNotes(),1000));
                for (int i = 0; i < itemBarcodes.size(); i++) {
                    itemRequestInfo.setItemBarcodes(Arrays.asList(itemBarcodes.get(i).toString().trim()));
                    String json = objectMapper.writeValueAsString(itemRequestInfo);
                    getProducer().sendBodyAndHeader(ReCAPConstants.REQUEST_ITEM_QUEUE, json, ReCAPConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
                }
                bSuccess = true;
                screenMessage = ReCAPConstants.REQUEST_MESSAGE_RECEVIED;
            } else {
                bSuccess = false;
            }

            itemResponseInformation.setSuccess(bSuccess);
            itemResponseInformation.setScreenMessage(screenMessage);
            itemResponseInformation.setItemBarcodes(itemRequestInfo.getItemBarcodes());
            itemResponseInformation.setTitleIdentifier(itemRequestInfo.getTitleIdentifier());
            itemResponseInformation.setDeliveryLocation(itemRequestInfo.getDeliveryLocation());
            itemResponseInformation.setEmailAddress(itemRequestInfo.getEmailAddress());
            itemResponseInformation.setPatronBarcode(itemRequestInfo.getPatronBarcode());
            itemResponseInformation.setRequestType(itemRequestInfo.getRequestType());
            itemResponseInformation.setRequestingInstitution(itemRequestInfo.getRequestingInstitution());
            logger.info("Message In Queue");
        } catch (Exception e) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION, e);
        }
        return itemResponseInformation;
    }

    /**
     * This method will call scsb-circ microservice to validate item request information.
     * @param itemRequestInfo the item request info
     * @return the response entity
     */
    @RequestMapping(value = ReCAPConstants.REST_URL_VALIDATE_REQUEST_ITEM, method = RequestMethod.POST)
    @ApiOperation(value = "validateItemRequestInformations",
            notes = "The Validate item request API is an internal API call made by SCSB to validate the various parameters of the request item API call. This is to ensure only valid data is allowed to be processed even when the request comes through the request item API.", nickname = "validateItemRequestInformation")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity<String> validateItemRequest(@ApiParam(value = "Parameters to validate information prior to request", required = true, name = "requestItemJson") @RequestBody ItemRequestInformation itemRequestInfo) {
        ResponseEntity<String> responseEntity = null;
        String response = null;
        try {
            responseEntity = getRestTemplate().postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInfo, String.class);
            response = (String) responseEntity.getBody();
        } catch (HttpClientErrorException httpEx) {
            logger.error("error-->", httpEx);
            HttpStatus statusCode = httpEx.getStatusCode();
            String responseBodyAsString = httpEx.getResponseBodyAsString();
            return new ResponseEntity<String>(responseBodyAsString, getHttpHeaders(), statusCode);
        } catch (Exception ex) {
            logger.error("scsbCircUrl", ex);
            logger.debug("scsbCircUrl : " + getScsbCircUrl());
            responseEntity = new ResponseEntity<String>("Scsb circ Service is Unavailable.", getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
            return responseEntity;
        }
        responseEntity = new ResponseEntity<String>(response, getHttpHeaders(), HttpStatus.OK);
        return responseEntity;
    }

    /**
     * This method will call scsb-circ microservice to checkout an item request from ILS.
     * @param itemCheckOutRequest the item check out request
     * @return the item checkout response
     */
    @RequestMapping(value = "/checkoutItem", method = RequestMethod.POST)
    @ApiOperation(value = "checkoutItem",
            notes = "The Check-out item API call is an internal call made by SCSB as part of the request API call.", nickname = "checkoutItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemCheckoutResponse checkoutItemRequest(@ApiParam(value = "Parameters for checking out an item", required = true, name = "requestItemJson") @RequestBody ItemCheckOutRequest itemCheckOutRequest) {
        ItemCheckoutResponse itemCheckoutResponse = null;
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        String response = "";
        try {
            itemRequestInfo.setPatronBarcode(itemCheckOutRequest.getPatronIdentifier());
            itemRequestInfo.setItemBarcodes(itemCheckOutRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemCheckOutRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemCheckOutRequest.getItemOwningInstitution());
            ResponseEntity<String> responseEntity = getRestTemplate().postForEntity(getScsbCircUrl() + "requestItem/checkoutItem", itemRequestInfo, String.class);
            response = responseEntity.getBody().toString();
            ObjectMapper om = getObjectMapper();
            itemCheckoutResponse = om.readValue(response, ItemCheckoutResponse.class);
        } catch (RestClientException ex) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION_REST, ex);
            itemCheckoutResponse = ItemCheckoutResponse.builder().screenMessage(ex.getMessage()).build();
        } catch (Exception ex) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION, ex);
            itemCheckoutResponse = ItemCheckoutResponse.builder().screenMessage(ex.getMessage()).build();
        }
        return itemCheckoutResponse;
    }

    /**
     * This method will call scsb-circ microservice to send a checkin an item into ILS.
     * @param itemCheckInRequest the item check in request
     * @return the abstract response item
     */
    @RequestMapping(value = "/checkinItem", method = RequestMethod.POST)
    @ApiOperation(value = "checkinItem",
            notes = "The Check-in item API is an internal call made by SCSB as part of the refile and accession API calls.", nickname = "checkinItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem checkinItemRequest(@ApiParam(value = "Parameters for checking in an item", required = true, name = "requestItemJson") @RequestBody ItemCheckInRequest itemCheckInRequest) {
        ItemCheckinResponse itemCheckinResponse = null;
        ResponseEntity<String> responseEntity = null;
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        String response = null;
        try {
            itemRequestInfo.setPatronBarcode(itemCheckInRequest.getPatronIdentifier());
            itemRequestInfo.setItemBarcodes(itemCheckInRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemCheckInRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemCheckInRequest.getItemOwningInstitution());
            responseEntity = getRestTemplate().postForEntity(getScsbCircUrl() + "requestItem/checkinItem", itemRequestInfo, String.class);
            response = (String) responseEntity.getBody();
            ObjectMapper om = getObjectMapper();
            itemCheckinResponse = om.readValue(response, ItemCheckinResponse.class);
        } catch (RestClientException ex) {
            getLogger().error(ReCAPConstants.REQUEST_EXCEPTION_REST, ex);
        } catch (Exception ex) {
            getLogger().error(ReCAPConstants.REQUEST_EXCEPTION, ex);
        }
        return itemCheckinResponse;
    }

    /**
     * This method will call scsb-circ microservice to place a hold on the item in ILS.
     * @param itemHoldRequest the item hold request
     * @return the abstract response item
     */
    @RequestMapping(value = "/holdItem", method = RequestMethod.POST)
    @ApiOperation(value = "holdItem",
            notes = "The Hold item API call is an internal call made by SCSB to the partner's ILS to place a hold request as part of the request API workflow.", nickname = "holdItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem holdItemRequest(@ApiParam(value = "Parameters for placing a hold on the item in the ILS", required = true, name = "requestItemJson") @RequestBody ItemHoldRequest itemHoldRequest) {
        ItemHoldResponse itemHoldResponse = null;
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        String response = "";
        try {
            itemRequestInfo.setItemBarcodes(itemHoldRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemHoldRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemHoldRequest.getItemOwningInstitution());
            itemRequestInfo.setPatronBarcode(itemHoldRequest.getPatronIdentifier());
            itemRequestInfo.setBibId(itemHoldRequest.getBibId());
            itemRequestInfo.setDeliveryLocation(itemHoldRequest.getPickupLocation());
            itemRequestInfo.setTrackingId(itemHoldRequest.getTrackingId());
            itemRequestInfo.setTitleIdentifier(itemHoldRequest.getTitle());
            itemRequestInfo.setAuthor(itemHoldRequest.getAuthor());
            itemRequestInfo.setCallNumber(itemHoldRequest.getCallNumber());

            ResponseEntity<String> responseEntity = getRestTemplate().postForEntity(getScsbCircUrl() + ReCAPConstants.URL_REQUEST_ITEM_HOLD, itemRequestInfo, String.class);
            response = responseEntity.getBody().toString();
            ObjectMapper om = getObjectMapper();
            itemHoldResponse = om.readValue(response, ItemHoldResponse.class);
        } catch (RestClientException ex) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION_REST, ex);
            logger.error(ReCAPConstants.REQUEST_EXCEPTION_REST + ex.getMessage());
            itemHoldResponse = ItemHoldResponse.builder().screenMessage(ex.getMessage()).build();
        } catch (Exception ex) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION, ex);
            logger.error(ReCAPConstants.REQUEST_EXCEPTION + ex.getMessage());
            itemHoldResponse = ItemHoldResponse.builder().screenMessage(ex.getMessage()).build();
        }
        return itemHoldResponse;
    }

    /**
     * This method will call scsb-circ microservice to cancel a hold on the item in ILS.
     * @param itemHoldCancelRequest the item hold cancel request
     * @return the abstract response item
     */
    @RequestMapping(value = "/cancelHoldItem", method = RequestMethod.POST)
    @ApiOperation(value = "cancelHoldItem",
            notes = "This internal call cancels a hold request in the partner ILS as part of the Cancel Request API.", nickname = "cancelHoldItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem cancelHoldItemRequest(@ApiParam(value = "Parameters for canceling a hold on the Item", required = true, name = "requestItemJson") @RequestBody ItemHoldCancelRequest itemHoldCancelRequest) {
        ItemHoldResponse itemHoldResponse = null;
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        String response = "";
        try {
            itemRequestInfo.setItemBarcodes(itemHoldCancelRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemHoldCancelRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemHoldCancelRequest.getItemOwningInstitution());
            itemRequestInfo.setPatronBarcode(itemHoldCancelRequest.getPatronIdentifier());
            itemRequestInfo.setBibId(itemHoldCancelRequest.getBibId());
            itemRequestInfo.setDeliveryLocation(itemHoldCancelRequest.getPickupLocation());
            itemRequestInfo.setTrackingId(itemHoldCancelRequest.getTrackingId());

            ResponseEntity<String> responseEntity = getRestTemplate().postForEntity(getScsbCircUrl() + "requestItem/cancelHoldItem", itemRequestInfo, String.class);
            response = responseEntity.getBody().toString();
            ObjectMapper om = getObjectMapper();
            itemHoldResponse = om.readValue(response, ItemHoldResponse.class);
        } catch (RestClientException ex) {
            getLogger().error(ReCAPConstants.REQUEST_EXCEPTION_REST, ex);
            getLogger().error(ReCAPConstants.REQUEST_EXCEPTION_REST + ex.getMessage());
            itemHoldResponse = ItemHoldResponse.builder().screenMessage(ex.getMessage()).build();
        } catch (Exception ex) {
            getLogger().error(ReCAPConstants.REQUEST_EXCEPTION, ex);
            getLogger().error(ReCAPConstants.REQUEST_EXCEPTION + ex.getMessage());
            itemHoldResponse = ItemHoldResponse.builder().screenMessage(ex.getMessage()).build();
        }
        return itemHoldResponse;
    }

    /**
     * This method will call scsb-circ microservice to create a bibliographic record in ILS.
     *
     * @param itemCreateBibRequest the item create bib request
     * @return the abstract response item
     */
    @RequestMapping(value = "/createBib", method = RequestMethod.POST)
    @ApiOperation(value = "createBib",
            notes = "The Create bibliographic record API is an internal call made by SCSB to partner ILS as part of the request API for cross partner borrowing. Usually when an item owned by another partner is requesting, the requesting institution will not have the metadata of the item that is being requested. In order to place the hold for the patron against the item, the Create bib record API creates a temporary record against which the hold can be placed and subsequent charge and discharge processes can be done.", nickname = "createBib")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem createBibRequest(@ApiParam(value = "Parameters for creating a temporary bibliographic record in the ILS", required = true, name = "requestItemJson") @RequestBody ItemCreateBibRequest itemCreateBibRequest) {
        ItemCreateBibResponse itemCreateBibResponse = null;
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        String response;

        try {
            itemRequestInfo.setItemBarcodes(itemCreateBibRequest.getItemBarcodes());
            itemRequestInfo.setPatronBarcode(itemCreateBibRequest.getPatronIdentifier());
            itemRequestInfo.setItemOwningInstitution(itemCreateBibRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemCreateBibRequest.getItemOwningInstitution());
            itemRequestInfo.setTitleIdentifier(itemCreateBibRequest.getTitleIdentifier());

            ResponseEntity<String> responseEntity = getRestTemplate().postForEntity(getScsbCircUrl() + ReCAPConstants.URL_REQUEST_ITEM_CREATEBIB, itemRequestInfo, String.class);
            response = responseEntity.getBody().toString();
            ObjectMapper om = getObjectMapper();
            itemCreateBibResponse = om.readValue(response, ItemCreateBibResponse.class);
        } catch (RestClientException ex) {
            getLogger().error(ReCAPConstants.REQUEST_EXCEPTION_REST, ex);
            getLogger().error(ReCAPConstants.REQUEST_EXCEPTION_REST + ex.getMessage());
            itemCreateBibResponse = ItemCreateBibResponse.builder().screenMessage(ex.getMessage()).build();
        } catch (Exception ex) {
            getLogger().error(ReCAPConstants.REQUEST_EXCEPTION, ex);
            getLogger().error(ReCAPConstants.REQUEST_EXCEPTION + ex.getMessage());
            itemCreateBibResponse = ItemCreateBibResponse.builder().screenMessage(ex.getMessage()).build();
        }
        return itemCreateBibResponse;
    }

    /**
     * This method will call scsb-circ microservice retrieve item information and circulation status from ILS.
     *
     * @param itemRequestInfo the item request info
     * @return the abstract response item
     */
    @RequestMapping(value = "/itemInformation", method = RequestMethod.POST)
    @ApiOperation(value = "itemInformation", notes = "The Item information API call is made internally by SCSB as part of the request API call.", nickname = "itemInformation")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem itemInformation(@ApiParam(value = "Parameters to retrieve the item information from the ILS", required = true, name = "requestItemJson") @RequestBody ItemInformationRequest itemRequestInfo) {
        HttpEntity<ItemInformationResponse> responseEntity;
        ItemInformationResponse itemInformationResponse;
        ItemInformationRequest itemInformationRequest = getItemInformationRequest();
        try {
            itemInformationRequest.setItemBarcodes(itemRequestInfo.getItemBarcodes());
            itemInformationRequest.setItemOwningInstitution(itemRequestInfo.getItemOwningInstitution());
            HttpEntity<ItemInformationRequest> request = new HttpEntity<ItemInformationRequest>(itemInformationRequest);
            responseEntity = getRestTemplate().exchange(getScsbCircUrl() + ReCAPConstants.URL_REQUEST_ITEM_INFORMATION, org.springframework.http.HttpMethod.POST, request, ItemInformationResponse.class);
            itemInformationResponse = responseEntity.getBody();
        } catch (RestClientException ex) {
            getLogger().error("RestClient : ", ex);
            itemInformationResponse = ItemInformationResponse.builder().screenMessage(ex.getMessage()).build();
        } catch (Exception ex) {
            getLogger().error(ReCAPConstants.LOG_ERROR, ex);
            itemInformationResponse = ItemInformationResponse.builder().screenMessage(ex.getMessage()).build();
        }
        return itemInformationResponse;
    }

    /**
     * This method will call scsb-circ microservice to recall an already retrieved item in ILS.
     *
     * @param itemRecalRequest the item recal request
     * @return the abstract response item
     */
    @RequestMapping(value = "/recall", method = RequestMethod.POST)
    @ApiOperation(value = "recall",
            notes = "The Recall API is used internally by SCSB during request API calls with request type RECALL.", nickname = "RecallItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem recallItem(@ApiParam(value = "Parameters to recall an item", required = true, name = "requestItemJson") @RequestBody ItemRecalRequest itemRecalRequest) {
        ItemRecallResponse itemRecallResponse = null;
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        String response;
        try {
            itemRequestInfo.setItemBarcodes(itemRecalRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemRecalRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemRecalRequest.getItemOwningInstitution());
            itemRequestInfo.setPatronBarcode(itemRecalRequest.getPatronIdentifier());
            itemRequestInfo.setBibId(itemRecalRequest.getBibId());
            itemRequestInfo.setDeliveryLocation(itemRecalRequest.getPickupLocation());

            ResponseEntity<String> responseEntity = getRestTemplate().postForEntity(getScsbCircUrl() + ReCAPConstants.URL_REQUEST_ITEM_RECALL, itemRequestInfo, String.class);
            response = responseEntity.getBody().toString();
            ObjectMapper om = getObjectMapper();
            itemRecallResponse = om.readValue(response, ItemRecallResponse.class);
        } catch (RestClientException ex) {
            getLogger().error(ReCAPConstants.LOG_ERROR_REST_CLIENT, ex);
            getLogger().error(ReCAPConstants.LOG_ERROR_REST_CLIENT + ex.getMessage());
            itemRecallResponse = ItemRecallResponse.builder().screenMessage(ex.getMessage()).build();
        } catch (Exception ex) {
            getLogger().error(ReCAPConstants.LOG_ERROR, ex);
            getLogger().error(ReCAPConstants.LOG_ERROR + ex.getMessage());
            itemRecallResponse = ItemRecallResponse.builder().screenMessage(ex.getMessage()).build();
        }
        return itemRecallResponse;
    }

    /**
     * This method will call scsb-circ microservice to retrieve patron information from ILS.
     *
     * @param patronInformationRequest the patron information request
     * @return the patron information response
     */
    @RequestMapping(value = "/patronInformation", method = RequestMethod.POST)
    @ApiOperation(value = "patronInformation", notes = "The Patron information API is used internally by SCSB as part of the request API.", nickname = "patronInformation")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public PatronInformationResponse patronInformation(@ApiParam(value = "Parameters to retrieve the patron information from the ILS", required = true, name = "requestpatron") @RequestBody PatronInformationRequest patronInformationRequest) {
        HttpEntity<PatronInformationResponse> responseEntity;
        PatronInformationResponse patronInformation = null;
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();

        try {
            itemRequestInformation.setPatronBarcode(patronInformationRequest.getPatronIdentifier());
            itemRequestInformation.setItemOwningInstitution(patronInformationRequest.getItemOwningInstitution());
            HttpEntity<ItemRequestInformation> request = new HttpEntity<ItemRequestInformation>(itemRequestInformation);
            responseEntity = getRestTemplate().exchange(getScsbCircUrl() + ReCAPConstants.URL_REQUEST_PATRON_INFORMATION, HttpMethod.POST, request, PatronInformationResponse.class);
            patronInformation = responseEntity.getBody();
        } catch (RestClientException ex) {
            getLogger().error(ReCAPConstants.LOG_ERROR_REST_CLIENT, ex);
            patronInformation = PatronInformationResponse.builder().screenMessage(ex.getMessage()).build();
        } catch (Exception ex) {
            getLogger().error(ReCAPConstants.LOG_ERROR, ex);
            patronInformation = PatronInformationResponse.builder().screenMessage(ex.getMessage()).build();
        }
        return patronInformation;
    }

    /**
     *This method will call scsb-circ microservice to refile an item back into scsb database and mark the item as available.
     *
     * @param itemRefileRequest the item refile request
     * @return the item refile response
     */
    @RequestMapping(value = "/refile", method = RequestMethod.POST)
    @ApiOperation(value = "refile", notes = "The Refile item API is called when ReCAP staff refile the iitem into LAS, and LAS will call SCSB with the details of the refile.", nickname = "Re-File")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemRefileResponse refileItem(@ApiParam(value = "Parameters to refile an Item", required = true, name = "itemBarcode") @RequestBody ItemRefileRequest itemRefileRequest) {
        logger.info("Refile Request Received");
        ItemRefileResponse itemRefileResponse;
        HttpEntity<ItemRefileResponse> responseEntity;
        HttpEntity<ItemRefileRequest> request = new HttpEntity<ItemRefileRequest>(itemRefileRequest);
        logger.info("Refile request received for the barcodes : {} where request id's are : {}",itemRefileRequest.getItemBarcodes(),itemRefileRequest.getRequestIds());
        responseEntity = getRestTemplate().exchange(getScsbCircUrl() + ReCAPConstants.URL_REQUEST_RE_FILE, HttpMethod.POST, request, ItemRefileResponse.class);
        itemRefileResponse = responseEntity.getBody();
        logger.info("Item Refile Response : {}",itemRefileResponse.getScreenMessage());
        return itemRefileResponse;
    }

    /**
     * This method will call scsb-circ microservice to cancel the request from scsb.
     *
     * @param requestId the request id
     * @return the cancel request response
     */
    @RequestMapping(value = "/cancelRequest", method = RequestMethod.POST)
    @ApiOperation(value = "cancelRequest", notes = "The Cancel Request API will be used by both partners and ReCAP users to cancel a request placed through SCSB. Partners will incorporate the API into their discovery systems to provide the patrons a way to cancel requests that have been raised by them. ReCAP users would use it through the SCSB UI to cancel requests that are difficult to process.", nickname = "cancelRequest")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public CancelRequestResponse cancelRequest(@ApiParam(value = "Parameters for canceling a request on the Item", required = true, name = "requestId") @RequestParam Integer requestId) {
        CancelRequestResponse cancelRequestResponse;
        HttpEntity<Object> request = new HttpEntity<>(getRestHeaderService().getHttpHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getScsbCircUrl() + ReCAPConstants.URL_REQUEST_CANCEL).queryParam("requestId", requestId);
        HttpEntity<CancelRequestResponse> responseEntity = getRestTemplate().exchange(builder.build().encode().toUri(), HttpMethod.POST, request, CancelRequestResponse.class);
        cancelRequestResponse = responseEntity.getBody();
        return cancelRequestResponse;
    }

    /**
     * This method will place bulk request message in to the queue to initiate the process.
     * @param bulkRequestId
     * @return
     */
    @RequestMapping(value = "/bulkRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "bulkRequest", notes = "The Bulk Request API is internally called by SCSB UI which will be probably initiated by LAS users.", nickname = "bulkRequest")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public BulkRequestResponse bulkRequest(@ApiParam(value = "Parameters for initiating bulk request", required = true, name = "bulkRequestId") @RequestParam Integer bulkRequestId) {
        getProducer().sendBody(ReCAPConstants.BULK_REQUEST_ITEM_QUEUE, bulkRequestId);
        BulkRequestResponse bulkRequestResponse = new BulkRequestResponse();
        bulkRequestResponse.setBulkRequestId(bulkRequestId);
        bulkRequestResponse.setSuccess(true);
        bulkRequestResponse.setScreenMessage(ReCAPConstants.BULK_REQUEST_MESSAGE_RECEIVED);
        return bulkRequestResponse;
    }

    /**
     * This method validates the patron information by calling an api in scsb-circ micro service.
     * @param bulkRequestInformation
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/patronValidationBulkRequest", method = RequestMethod.POST)
    public Boolean patronValidation(@RequestBody BulkRequestInformation bulkRequestInformation){
        return new RestTemplate().postForEntity(scsbCircUrl + "/requestItem/patronValidationBulkRequest", bulkRequestInformation, Boolean.class).getBody();
    }

    /**
     * This method refiles the item in ILS. Currently only NYPL has the refile endpoint.
     * @param itemBarcode
     * @param owningInstitution
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/refileItemInILS", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "refileItemInILS",
            notes = "The Refile item API is an internal call made by SCSB as part of the refile and accession API calls.", nickname = "refileItemInILS")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem refileItemInILS(@ApiParam(value = "Parameters for refiling an item", required = true, name = "itemBarcode") @RequestParam String itemBarcode,
                                                @ApiParam(value = "Parameters for refiling an item", required = true, name = "owningInstitution") @RequestParam String owningInstitution) {
        ItemRefileResponse itemRefileResponse = null;
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        try {
            itemRequestInfo.setItemBarcodes(Arrays.asList(itemBarcode));
            itemRequestInfo.setItemOwningInstitution(owningInstitution);
            ResponseEntity<ItemRefileResponse> responseEntity = getRestTemplate().postForEntity(getScsbCircUrl() + "requestItem/refileItemInILS", itemRequestInfo, ItemRefileResponse.class);
            itemRefileResponse = responseEntity.getBody();
        } catch (RestClientException ex) {
            getLogger().error(ReCAPConstants.REQUEST_EXCEPTION_REST, ex);
        } catch (Exception ex) {
            getLogger().error(ReCAPConstants.REQUEST_EXCEPTION, ex);
        }
        return itemRefileResponse;
    }

    /**
     * This method will replace the requests to LAS queue.
     *
     * @param replaceRequest the replace request body
     * @return the string response
     */
    @SuppressWarnings("unchecked")
	@ApiIgnore
    @RequestMapping(value = "/replaceRequest", method = RequestMethod.POST)
    @ApiOperation(value = "replaceRequest", notes = "Resubmit the failed requests to LAS", nickname = "Replace Request")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity<Object> replaceRequestToLAS(@ApiParam(value = "Parameters to replace the request", required = true, name = "replaceRequest") @RequestBody ReplaceRequest replaceRequest) {
        try {
            Map<String, String> resultMap = getRestTemplate().postForObject(getScsbCircUrl() + ReCAPConstants.URL_REQUEST_REPLACE, replaceRequest, Map.class);
            return new ResponseEntity<Object>(resultMap, getHttpHeaders(), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ReCAPConstants.LOG_ERROR, ex);
            return new ResponseEntity<Object>(ReCAPConstants.SCSB_CIRC_SERVICE_UNAVAILABLE, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(ReCAPConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

}