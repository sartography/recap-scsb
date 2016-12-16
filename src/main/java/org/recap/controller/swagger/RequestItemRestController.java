package org.recap.controller.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.java.accessibility.util.EventID;
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
import java.util.Date;

/**
 * Created by hemalathas on 1/11/16.
 */
@RestController
@RequestMapping("/requestItem")
@Api(value = "requestItem", description = "Request Item", position = 1)
public class RequestItemRestController {

    private Logger logger = LoggerFactory.getLogger(RequestItemRestController.class);

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.circ.url}")
    String scsbCircUrl;

    @Autowired
    private ProducerTemplate producer;

    @RequestMapping(value = "/requestItem" , method = RequestMethod.POST)
    @ApiOperation(value = "Request Item", notes = "Item Request from Owning institution", nickname = "requestItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemResponseInformation itemRequest(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemRequestInformation itemRequestInfo){
        ItemResponseInformation itemResponseInformation = new ItemResponseInformation();
        ObjectMapper objectMapper= new ObjectMapper();
        String json ="";
        try {
            json = objectMapper.writeValueAsString(itemRequestInfo);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
        producer.sendBodyAndHeader(ReCAPConstants.REQUEST_ITEM_QUEUE, json, ReCAPConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
        itemResponseInformation.setSuccess(true);
        itemResponseInformation.setScreenMessage("Message recevied, your request will be processed");
        logger.info("Message In Queue");
        return itemResponseInformation;
    }

    @RequestMapping(value = "/validateItemRequestInformations" , method = RequestMethod.POST)
    @ApiOperation(value = "validateItemRequestInformations",
            notes = "Validate Item Request Informations", nickname = "validateItemRequestInformation", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity validateItemRequest(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemRequestInformation itemRequestInfo){
        ResponseEntity responseEntity = null;
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + "requestItem/validateItemRequestInformations", itemRequestInfo, String.class).getBody();
        }catch (HttpClientErrorException httpEx){
            HttpStatus statusCode = httpEx.getStatusCode();
            String responseBodyAsString = httpEx.getResponseBodyAsString();
            return new ResponseEntity(responseBodyAsString,getHttpHeaders(),statusCode);
        } catch(Exception ex){
            logger.debug("scsbCircUrl : "+scsbCircUrl);
            responseEntity = new ResponseEntity("Scsb circ Service is Unavailable.", getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
            return responseEntity;
        }
        responseEntity =  new ResponseEntity(response,getHttpHeaders(), HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(value = "/checkoutItem" , method = RequestMethod.POST)
    @ApiOperation(value = "checkoutItem",
            notes = "Checkout Item Request from Owning institution", nickname = "checkoutItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemResponseInformation checkoutItemRequest(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemCheckOutRequest itemCheckOutRequest){
        ItemResponseInformation itemResponseInformation= null;
        ItemRequestInformation itemRequestInfo= new ItemRequestInformation();
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemRequestInfo.setPatronBarcode(itemCheckOutRequest.getPatronIdentifier());
            itemRequestInfo.setItemBarcodes(itemCheckOutRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemCheckOutRequest.getItemOwningInstitution());
            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + "requestItem/checkoutItem", itemRequestInfo, String.class).getBody();
            ObjectMapper om = new ObjectMapper();
            itemResponseInformation = om.readValue(response, ItemResponseInformation.class);
        }catch(RestClientException ex){
            logger.error("RestClient : "+ ex.getMessage());
        }catch(Exception ex){
            logger.error("Exception : "+ex.getMessage());
        }
        return itemResponseInformation;
    }

    @RequestMapping(value = "/checkinItem" , method = RequestMethod.POST)
    @ApiOperation(value = "checkinItem",
            notes = "Checkin Item Request from Owning institution", nickname = "checkinItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemResponseInformation checkinItemRequest(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemCheckInRequest itemCheckInRequest){
        ItemResponseInformation itemResponseInformation= null;
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemRequestInfo.setPatronBarcode(itemCheckInRequest.getPatronIdentifier());
            itemRequestInfo.setItemBarcodes(itemCheckInRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemCheckInRequest.getItemOwningInstitution());
            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + "requestItem/checkinItem", itemRequestInfo, String.class).getBody();
            ObjectMapper om = new ObjectMapper();
            itemResponseInformation = om.readValue(response, ItemResponseInformation.class);
        }catch(RestClientException ex){
            logger.error("RestClient : "+ ex.getMessage());
        }catch(Exception ex){
            logger.error("Exception : "+ex.getMessage());
        }
        return itemResponseInformation;
    }

    @RequestMapping(value = "/holdItem" , method = RequestMethod.POST)
    @ApiOperation(value = "holdItem",
            notes = "hold Item Request from Owning institution", nickname = "holdItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemResponseInformation holdItemRequest(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemHoldRequest itemHoldRequest){
        ItemResponseInformation itemResponseInformation= null;
        ItemRequestInformation itemRequestInfo=new ItemRequestInformation();
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemRequestInfo.setItemBarcodes(itemHoldRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemHoldRequest.getItemOwningInstitution());
            itemRequestInfo.setPatronBarcode(itemHoldRequest.getPatronIdentifier());
            itemRequestInfo.setExpirationDate(itemHoldRequest.getExpirationDate());
            itemRequestInfo.setBibId(itemHoldRequest.getBibId());
            itemRequestInfo.setDeliveryLocation(itemHoldRequest.getPickupLocation());

            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + ReCAPConstants.URL_REQUEST_ITEM_HOLD, itemRequestInfo, String.class).getBody();
            ObjectMapper om = new ObjectMapper();
            itemResponseInformation = om.readValue(response, ItemResponseInformation.class);
        }catch(RestClientException ex){
            logger.error("RestClient : "+ ex.getMessage());
        }catch(Exception ex){
            logger.error("Exception : "+ex.getMessage());
        }
        return itemResponseInformation;
    }

    @RequestMapping(value = "/cancelHoldItem" , method = RequestMethod.POST)
    @ApiOperation(value = "cancelHoldItem",
            notes = "Cancel hold Item Request from Owning institution", nickname = "cancelHoldItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemResponseInformation cancelHoldItemRequest(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemHoldCancelRequest itemHoldCancelRequest){
        ItemResponseInformation itemResponseInformation= null;
        ItemRequestInformation itemRequestInfo=new ItemRequestInformation();
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemRequestInfo.setItemBarcodes(itemHoldCancelRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemHoldCancelRequest.getItemOwningInstitution());
            itemRequestInfo.setPatronBarcode(itemHoldCancelRequest.getPatronIdentifier());
            itemRequestInfo.setExpirationDate(itemHoldCancelRequest.getExpirationDate());
            itemRequestInfo.setBibId(itemHoldCancelRequest.getBibId());
            itemRequestInfo.setDeliveryLocation(itemHoldCancelRequest.getPickupLocation());

            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + "requestItem/cancelHoldItem", itemRequestInfo, String.class).getBody();
            ObjectMapper om = new ObjectMapper();
            itemResponseInformation = om.readValue(response, ItemResponseInformation.class);
        }catch(RestClientException ex){
            logger.error("RestClient : "+ ex.getMessage());
        }catch(Exception ex){
            logger.error("Exception : "+ex.getMessage());
        }
        return itemResponseInformation;
    }

    @RequestMapping(value = "/createBib" , method = RequestMethod.POST)
    @ApiOperation(value = "createBib",
            notes = "Create Bibliographic Request from Owning institution", nickname = "createBib")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemResponseInformation createBibRequest(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemCreateBibRequest itemCreateBibRequest){
        ItemResponseInformation itemResponseInformation= null;
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemCreateBibRequest.setItemBarcodes(itemCreateBibRequest.getItemBarcodes());
            itemCreateBibRequest.setPatronIdentifier(itemCreateBibRequest.getPatronIdentifier());
            itemCreateBibRequest.setItemOwningInstitution(itemCreateBibRequest.getItemOwningInstitution());
            itemCreateBibRequest.setTitleIdentifier(itemCreateBibRequest.getTitleIdentifier());

            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + ReCAPConstants.URL_REQUEST_ITEM_CREATEBIB, itemRequestInfo, String.class).getBody();
            ObjectMapper om = new ObjectMapper();
            itemResponseInformation = om.readValue(response, ItemResponseInformation.class);
        }catch(RestClientException ex){
            logger.error("RestClient : "+ ex.getMessage());
        }catch(Exception ex){
            logger.error("Exception : "+ex.getMessage());
        }
        return itemResponseInformation;
    }

    @RequestMapping(value = "/itemInformation"  , method = RequestMethod.POST)
    @ApiOperation(value = "itemInformation"     , notes = "item Information and status of Circulation", nickname = "itemInformation")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemInformationResponse itemInformation(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")
                                                        @RequestBody ItemInformationRequest itemRequestInfo){
        HttpEntity<ItemInformationResponse> responseEntity = null;
        ItemInformationResponse itemInformationResponse =null;
        ItemInformationRequest itemInformationRequest = new ItemInformationRequest();
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemInformationRequest.setItemBarcodes(itemRequestInfo.getItemBarcodes());
            itemInformationRequest.setItemOwningInstitution(itemRequestInfo.getItemOwningInstitution());
            HttpEntity request = new HttpEntity(itemInformationRequest);
            responseEntity = restTemplate.exchange(serverProtocol + scsbCircUrl +   ReCAPConstants.URL_REQUEST_ITEM_INFORMATION, HttpMethod.POST, request, ItemInformationResponse.class);
            itemInformationResponse = responseEntity.getBody();
        }catch(RestClientException ex){
            logger.error("RestClient : ",ex);
        }catch(Exception ex){
            logger.error("Exception : ",ex);
        }
        return itemInformationResponse;
    }

    @RequestMapping(value = "/recall" , method = RequestMethod.POST)
    @ApiOperation(value = "recall",
            notes = "Recall Item Request from Owning institution", nickname = "RecallItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemResponseInformation recallItem(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemRecalRequest itemRecalRequest){
        ItemResponseInformation itemResponseInformation= null;
        ItemRequestInformation itemRequestInfo=new ItemRequestInformation();
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemRequestInfo.setItemBarcodes(itemRecalRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemRecalRequest.getItemOwningInstitution());
            itemRequestInfo.setPatronBarcode(itemRecalRequest.getPatronIdentifier());
            itemRequestInfo.setExpirationDate(itemRecalRequest.getExpirationDate());
            itemRequestInfo.setBibId(itemRecalRequest.getBibId());
            itemRequestInfo.setDeliveryLocation(itemRecalRequest.getPickupLocation());

            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + ReCAPConstants.URL_REQUEST_ITEM_RECALL, itemRequestInfo, String.class).getBody();
            ObjectMapper om = new ObjectMapper();
            itemResponseInformation = om.readValue(response, ItemResponseInformation.class);
        }catch(RestClientException ex){
            logger.error("RestClient : "+ ex.getMessage());
        }catch(Exception ex){
            logger.error("Exception : "+ex.getMessage());
        }
        return itemResponseInformation;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(ReCAPConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

}