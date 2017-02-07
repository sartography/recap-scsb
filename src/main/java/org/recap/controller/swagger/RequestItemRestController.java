package org.recap.controller.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.apache.camel.ProducerTemplate;
import org.recap.ReCAPConstants;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by hemalathas on 1/11/16.
 */
@RestController
@RequestMapping("/requestItem")
@Api(value = "requestItem")
public class RequestItemRestController {

    private Logger logger = LoggerFactory.getLogger(RequestItemRestController.class);

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.circ.url}")
    String scsbCircUrl;

    @Autowired
    private ProducerTemplate producer;

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

    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public ProducerTemplate getProducer() {
        return producer;
    }

    public void setProducer(ProducerTemplate producer) {
        this.producer = producer;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @RequestMapping(value = ReCAPConstants.REST_URL_REQUEST_ITEM, method = RequestMethod.POST)
    @ApiOperation(value = "Request Item", notes = "Item Request from Owning institution", nickname = "requestItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemResponseInformation itemRequest(@ApiParam(value = "Parameters for requesting an item", required = true, name = "requestItemJson") @RequestBody ItemRequestInformation itemRequestInfo) {
        ItemResponseInformation itemResponseInformation = new ItemResponseInformation();
        List itemBarcodes = null;
        boolean bSuccess = false;
        String screenMessage = "Request message recevied is in imporoper format, Please verify the message";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json;
            if (itemRequestInfo.getItemBarcodes() != null && ReCAPConstants.REQUEST_TYPE_LIST.contains(itemRequestInfo.getRequestType())) {
                itemBarcodes = itemRequestInfo.getItemBarcodes();
                itemRequestInfo.setItemBarcodes(null);
                if (itemBarcodes.size() > 1) {
                    for (int i = 0; i < itemBarcodes.size(); i++) {
                        itemRequestInfo.setItemBarcodes(Arrays.asList(itemBarcodes.get(i).toString().trim()));
                        json = objectMapper.writeValueAsString(itemRequestInfo);
                        producer.sendBodyAndHeader(ReCAPConstants.REQUEST_ITEM_QUEUE, json, ReCAPConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
                    }
                } else if (itemBarcodes.size() == 1) {
                    itemRequestInfo.setItemBarcodes(Arrays.asList(itemBarcodes.get(0).toString().trim()));
                    json = objectMapper.writeValueAsString(itemRequestInfo);
                    producer.sendBodyAndHeader(ReCAPConstants.REQUEST_ITEM_QUEUE, json, ReCAPConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
                }
                bSuccess = true;
                screenMessage = "Message recevied, your request will be processed";
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
        } catch (JsonProcessingException e) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION,e);
        }
        return itemResponseInformation;
    }

    @RequestMapping(value = ReCAPConstants.REST_URL_VALIDATE_REQUEST_ITEM, method = RequestMethod.POST)
    @ApiOperation(value = "validateItemRequestInformations",
            notes = "Validate Item Request Informations", nickname = "validateItemRequestInformation")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity validateItemRequest(@ApiParam(value = "Parameters for requesting an item", required = true, name = "requestItemJson") @RequestBody ItemRequestInformation itemRequestInfo) {
        ResponseEntity responseEntity = null;
        String response = null;
        try {
            responseEntity = getRestTemplate().postForEntity(getServerProtocol() + getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInfo, String.class);
            response = (String) responseEntity.getBody();
        }catch (HttpClientErrorException httpEx){
            HttpStatus statusCode = httpEx.getStatusCode();
            String responseBodyAsString = httpEx.getResponseBodyAsString();
            return new ResponseEntity(responseBodyAsString,getHttpHeaders(),statusCode);
        }catch(Exception ex){
            logger.debug("scsbCircUrl : "+getScsbCircUrl());
            responseEntity = new ResponseEntity("Scsb circ Service is Unavailable.", getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
            return responseEntity;
        }
        responseEntity =  new ResponseEntity(response,getHttpHeaders(), HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(value = "/checkoutItem", method = RequestMethod.POST)
    @ApiOperation(value = "checkoutItem",
            notes = "Checkout Item Request from Owning institution", nickname = "checkoutItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemCheckoutResponse checkoutItemRequest(@ApiParam(value = "Parameters for checking out an item", required = true, name = "requestItemJson") @RequestBody ItemCheckOutRequest itemCheckOutRequest) {
        ItemCheckoutResponse itemCheckoutResponse = null;
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemRequestInfo.setPatronBarcode(itemCheckOutRequest.getPatronIdentifier());
            itemRequestInfo.setItemBarcodes(itemCheckOutRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemCheckOutRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemCheckOutRequest.getItemOwningInstitution());
            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + "requestItem/checkoutItem", itemRequestInfo, String.class).getBody();
            ObjectMapper om = new ObjectMapper();
            itemCheckoutResponse = om.readValue(response, ItemCheckoutResponse.class);
        } catch (RestClientException ex) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION_REST, ex);
            itemCheckoutResponse.setScreenMessage(ex.getMessage());
        } catch (Exception ex) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION, ex);
            itemCheckoutResponse.setScreenMessage(ex.getMessage());
        }
        return itemCheckoutResponse;
    }

    @RequestMapping(value = "/checkinItem", method = RequestMethod.POST)
    @ApiOperation(value = "checkinItem",
            notes = "Checkin Item Request from Owning institution", nickname = "checkinItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem checkinItemRequest(@ApiParam(value = "Parameters for requesting an item", required = true, name = "requestItemJson") @RequestBody ItemCheckInRequest itemCheckInRequest) {
        ItemCheckinResponse itemCheckinResponse = null;
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemRequestInfo.setPatronBarcode(itemCheckInRequest.getPatronIdentifier());
            itemRequestInfo.setItemBarcodes(itemCheckInRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemCheckInRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemCheckInRequest.getItemOwningInstitution());
            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + "requestItem/checkinItem", itemRequestInfo, String.class).getBody();
            ObjectMapper om = new ObjectMapper();
            itemCheckinResponse = om.readValue(response, ItemCheckinResponse.class);
        } catch (RestClientException ex) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION_REST, ex);
        } catch (Exception ex) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION, ex);
        }
        return itemCheckinResponse;
    }

    @RequestMapping(value = "/holdItem", method = RequestMethod.POST)
    @ApiOperation(value = "holdItem",
            notes = "hold Item Request from Owning institution", nickname = "holdItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem holdItemRequest(@ApiParam(value = "Parameters for requesting an item", required = true, name = "requestItemJson") @RequestBody ItemHoldRequest itemHoldRequest) {
        ItemHoldResponse itemHoldResponse = null;
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
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

            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + ReCAPConstants.URL_REQUEST_ITEM_HOLD, itemRequestInfo, String.class).getBody();
            ObjectMapper om = new ObjectMapper();
            itemHoldResponse = om.readValue(response, ItemHoldResponse.class);
        } catch (RestClientException ex) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION_REST + ex.getMessage());
            itemHoldResponse.setScreenMessage(ex.getMessage());
        } catch (Exception ex) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION + ex.getMessage());
            itemHoldResponse.setScreenMessage(ex.getMessage());
        }
        return itemHoldResponse;
    }

    @RequestMapping(value = "/cancelHoldItem", method = RequestMethod.POST)
    @ApiOperation(value = "cancelHoldItem",
            notes = "Cancel hold Item Request from Owning institution", nickname = "cancelHoldItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem cancelHoldItemRequest(@ApiParam(value = "Parameters for requesting an item", required = true, name = "requestItemJson") @RequestBody ItemHoldCancelRequest itemHoldCancelRequest) {
        ItemHoldResponse itemHoldResponse = null;
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemRequestInfo.setItemBarcodes(itemHoldCancelRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemHoldCancelRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemHoldCancelRequest.getItemOwningInstitution());
            itemRequestInfo.setPatronBarcode(itemHoldCancelRequest.getPatronIdentifier());
            itemRequestInfo.setBibId(itemHoldCancelRequest.getBibId());
            itemRequestInfo.setDeliveryLocation(itemHoldCancelRequest.getPickupLocation());
            itemRequestInfo.setTrackingId(itemHoldCancelRequest.getTrackingId());

            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + "requestItem/cancelHoldItem", itemRequestInfo, String.class).getBody();
            ObjectMapper om = new ObjectMapper();
            itemHoldResponse = om.readValue(response, ItemHoldResponse.class);
        } catch (RestClientException ex) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION_REST + ex.getMessage());
            itemHoldResponse.setScreenMessage(ex.getMessage());
        } catch (Exception ex) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION + ex.getMessage());
            itemHoldResponse.setScreenMessage(ex.getMessage());
        }
        return itemHoldResponse;
    }

    @RequestMapping(value = "/createBib", method = RequestMethod.POST)
    @ApiOperation(value = "createBib",
            notes = "Create Bibliographic Request from Owning institution", nickname = "createBib")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem createBibRequest(@ApiParam(value = "Parameters for requesting an item", required = true, name = "requestItemJson") @RequestBody ItemCreateBibRequest itemCreateBibRequest) {
        ItemCreateBibResponse itemCreateBibResponse = new ItemCreateBibResponse();
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemRequestInfo.setItemBarcodes(itemCreateBibRequest.getItemBarcodes());
            itemRequestInfo.setPatronBarcode(itemCreateBibRequest.getPatronIdentifier());
            itemRequestInfo.setItemOwningInstitution(itemCreateBibRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemCreateBibRequest.getItemOwningInstitution());
            itemRequestInfo.setTitleIdentifier(itemCreateBibRequest.getTitleIdentifier());

            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + ReCAPConstants.URL_REQUEST_ITEM_CREATEBIB, itemRequestInfo, String.class).getBody();
            ObjectMapper om = new ObjectMapper();
            itemCreateBibResponse = om.readValue(response, ItemCreateBibResponse.class);
        } catch (RestClientException ex) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION_REST + ex.getMessage());
            itemCreateBibResponse.setScreenMessage(ex.getMessage());
        } catch (Exception ex) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION + ex.getMessage());
            itemCreateBibResponse.setScreenMessage(ex.getMessage());
        }
        return itemCreateBibResponse;
    }

    @RequestMapping(value = "/itemInformation", method = RequestMethod.POST)
    @ApiOperation(value = "itemInformation", notes = "item Information and status of Circulation", nickname = "itemInformation")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem itemInformation(@ApiParam(value = "Parameters for requesting an item", required = true, name = "requestItemJson") @RequestBody ItemInformationRequest itemRequestInfo) {
        HttpEntity<ItemInformationResponse> responseEntity = null;
        ItemInformationResponse itemInformationResponse = null;
        ItemInformationRequest itemInformationRequest = new ItemInformationRequest();
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemInformationRequest.setItemBarcodes(itemRequestInfo.getItemBarcodes());
            itemInformationRequest.setItemOwningInstitution(itemRequestInfo.getItemOwningInstitution());
            HttpEntity request = new HttpEntity(itemInformationRequest);
            responseEntity = restTemplate.exchange(serverProtocol + scsbCircUrl + ReCAPConstants.URL_REQUEST_ITEM_INFORMATION, HttpMethod.POST, request, ItemInformationResponse.class);
            itemInformationResponse = responseEntity.getBody();
        } catch (RestClientException ex) {
            logger.error("RestClient : ", ex);
            if (itemInformationResponse == null) {
                itemInformationResponse = new ItemInformationResponse();
            }
            itemInformationResponse.setScreenMessage(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Exception : ", ex);
            if (itemInformationResponse == null) {
                itemInformationResponse = new ItemInformationResponse();
            }
            itemInformationResponse.setScreenMessage(ex.getMessage());
        }
        return itemInformationResponse;
    }

    @RequestMapping(value = "/recall", method = RequestMethod.POST)
    @ApiOperation(value = "recall",
            notes = "Recall Item Request from Owning institution", nickname = "RecallItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem recallItem(@ApiParam(value = "Parameters for requesting an item", required = true, name = "requestItemJson") @RequestBody ItemRecalRequest itemRecalRequest) {
        ItemRecallResponse itemRecallResponse = new ItemRecallResponse();
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemRequestInfo.setItemBarcodes(itemRecalRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemRecalRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemRecalRequest.getItemOwningInstitution());
            itemRequestInfo.setPatronBarcode(itemRecalRequest.getPatronIdentifier());
            itemRequestInfo.setBibId(itemRecalRequest.getBibId());
            itemRequestInfo.setDeliveryLocation(itemRecalRequest.getPickupLocation());

            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + ReCAPConstants.URL_REQUEST_ITEM_RECALL, itemRequestInfo, String.class).getBody();
            ObjectMapper om = new ObjectMapper();
            itemRecallResponse = om.readValue(response, ItemRecallResponse.class);
        }catch(RestClientException ex){
            logger.error("RestClient : "+ ex.getMessage());
            itemRecallResponse.setScreenMessage(ex.getMessage());
        }catch(Exception ex){
            logger.error("Exception : "+ex.getMessage());
            itemRecallResponse.setScreenMessage(ex.getMessage());
        }
        return itemRecallResponse;
    }

    @RequestMapping(value = "/patronInformation"  , method = RequestMethod.POST)
    @ApiOperation(value = "patronInformation"     , notes = "Patron Information", nickname = "patronInformation")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public PatronInformationResponse patronInformation(@ApiParam(value = "Parameters for requesting an patron" , required = true , name = "requestpatron") @RequestBody PatronInformationRequest patronInformationRequest){
        HttpEntity<PatronInformationResponse> responseEntity = null;
        PatronInformationResponse patronInformation =null;
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemRequestInformation.setPatronBarcode (patronInformationRequest.getPatronIdentifier());
            itemRequestInformation.setItemOwningInstitution(patronInformationRequest.getItemOwningInstitution());
            HttpEntity request = new HttpEntity(itemRequestInformation);
            responseEntity = restTemplate.exchange(serverProtocol + scsbCircUrl +   ReCAPConstants.URL_REQUEST_PATRON_INFORMATION, HttpMethod.POST, request, PatronInformationResponse.class);
            patronInformation = responseEntity.getBody();
        }catch(RestClientException ex){
            logger.error("RestClient : ",ex);
            patronInformation.setScreenMessage(ex.getMessage());
        }catch(Exception ex){
            logger.error("Exception : ",ex);
            patronInformation.setScreenMessage(ex.getMessage());
        }
        return patronInformation;
    }

    @RequestMapping(value = "/refile"  , method = RequestMethod.POST)
    @ApiOperation(value = "refile"     , notes = "Re-File for testing item status change back to Available and execute Solr Index", nickname = "Re-File")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemRefileResponse refileItem(@ApiParam(value = "Parameters for requesting re-file" , required = true , name = "itemBarcode") @RequestBody ItemRefileRequest itemRefileRequest){
        ItemRefileResponse itemRefileResponse =null;
        HttpEntity<ItemRefileResponse> responseEntity = null;
        HttpEntity request = new HttpEntity(itemRefileRequest);
        RestTemplate restTemplate = new RestTemplate();

        responseEntity = restTemplate.exchange(serverProtocol + scsbCircUrl +   ReCAPConstants.URL_REQUEST_RE_FILE, HttpMethod.POST, request, ItemRefileResponse.class);
        itemRefileResponse =responseEntity.getBody();

        return itemRefileResponse;
    }

    @RequestMapping(value = "/cancelRequest", method = RequestMethod.POST)
    @ApiOperation(value = "cancelRequest", notes = "Cancel and existing request", nickname = "cancelRequest")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public CancelRequestResponse cancelRequest(@ApiParam(value = "Parameters for cancelling request", required = true, name = "requestId") @RequestParam Integer requestId) {
        CancelRequestResponse cancelRequestResponse = null;
        HttpEntity request = new HttpEntity<>(getHttpHeadersAuth());
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverProtocol + scsbCircUrl + ReCAPConstants.URL_REQUEST_CANCEL).queryParam("requestId", requestId);
        HttpEntity<CancelRequestResponse> responseEntity  = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, request, CancelRequestResponse.class);
        cancelRequestResponse = responseEntity.getBody();
        return cancelRequestResponse;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(ReCAPConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

    private HttpHeaders getHttpHeadersAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(ReCAPConstants.API_KEY, ReCAPConstants.RECAP);
        return headers;
    }
}