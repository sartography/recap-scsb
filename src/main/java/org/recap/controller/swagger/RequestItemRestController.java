package org.recap.controller.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.recap.ReCAPConstants;
import org.recap.model.ItemRequestInformation;
import org.recap.model.ItemResponseInformation;
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

    @RequestMapping(value = "/requestItem" , method = RequestMethod.POST)
    @ApiOperation(value = "checkoutItem",
            notes = "Checkout Item Request from Owning institution", nickname = "checkoutItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemResponseInformation itemRequest(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemRequestInformation itemRequestInfo){
        ItemResponseInformation itemResponseInformation = null;
        String patronResponse = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            //  Lookup Patron
            patronResponse = restTemplate.postForEntity(serverProtocol + scsbCircUrl + "requestItem/lookupPatron", itemRequestInfo, String.class).getBody();
            ObjectMapper om = new ObjectMapper();
            itemResponseInformation = om.readValue(patronResponse, ItemResponseInformation.class);
            // Validate Patron
            String ValidPatronresponse = restTemplate.postForEntity(serverProtocol + scsbCircUrl + "requestItem/validateItemRequestInformations", itemRequestInfo, String.class).getBody();
            // Action based on Request Type
            if(itemRequestInfo.getRequestType().equalsIgnoreCase(ReCAPConstants.REQUEST_TYPE_RETRIEVAL)) {
            }else if(itemRequestInfo.getRequestType().equalsIgnoreCase(ReCAPConstants.REQUEST_TYPE_HOLD)){
            }else if(itemRequestInfo.getRequestType().equalsIgnoreCase(ReCAPConstants.REQUEST_TYPE_RECALL)){
            }else if(itemRequestInfo.getRequestType().equalsIgnoreCase(ReCAPConstants.REQUEST_TYPE_EDD)){
            }else if(itemRequestInfo.getRequestType().equalsIgnoreCase(ReCAPConstants.REQUEST_TYPE_BORROW_DIRECT)){
            }

            if(itemRequestInfo.isOwningInstitutionItem()){
                String holdResponse = restTemplate.postForEntity(serverProtocol + scsbCircUrl + "requestItem/holdItem", itemRequestInfo, String.class).getBody();
                ItemResponseInformation holdItemResponse = om.readValue(holdResponse, ItemResponseInformation.class);
                if(holdItemResponse.isSuccess()){ // If Hold command is successfully

                    if(itemRequestInfo.isOwningInstitutionItem()){

                    }else{ // Item does not belong to requesting Institute
                        String checkoutResponse = restTemplate.postForEntity(serverProtocol + scsbCircUrl + "requestItem/checkoutItem", itemRequestInfo, String.class).getBody();
                        ItemResponseInformation checkoutItemResponse = om.readValue(holdResponse, ItemResponseInformation.class);
                        if(checkoutItemResponse.isSuccess()){

                        }else{

                        }
                    }
                }else{ // If Hold command Failure

                }
            }else{// Not the Owning Institute

            }



        }catch(RestClientException ex){
            logger.error("RestClient : "+ ex.getMessage());
        }catch(Exception ex){
            logger.error("Exception : "+ex.getMessage());
        }
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
    public ItemResponseInformation checkoutItemRequest(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemRequestInformation itemRequestInfo){
        ItemResponseInformation itemResponseInformation= null;
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
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
    public ItemResponseInformation checkinItemRequest(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemRequestInformation itemRequestInfo){
        ItemResponseInformation itemResponseInformation= null;
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
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
    public ItemResponseInformation holdItemRequest(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemRequestInformation itemRequestInfo){
        ItemResponseInformation itemResponseInformation= null;
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + "requestItem/holdItem", itemRequestInfo, String.class).getBody();
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
    public ItemResponseInformation cancelHoldItemRequest(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemRequestInformation itemRequestInfo){
        ItemResponseInformation itemResponseInformation= null;
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
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
            notes = "Breate Bibliographic Request from Owning institution", nickname = "createBib")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemResponseInformation createBibRequest(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemRequestInformation itemRequestInfo){
        ItemResponseInformation itemResponseInformation= null;
        String response = "";
        RestTemplate restTemplate = new RestTemplate();
        try {
            response = restTemplate.postForEntity(serverProtocol + scsbCircUrl + "requestItem/createBib", itemRequestInfo, String.class).getBody();
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