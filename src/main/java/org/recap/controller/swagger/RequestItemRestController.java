package org.recap.controller.swagger;

import io.swagger.annotations.*;
import org.recap.ReCAPConstants;
import org.recap.model.ItemRequestInformation;
import org.recap.request.ItemValidatorService;
import org.recap.request.PatronValidatorService;
import org.recap.request.RequestParamaterValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by hemalathas on 1/11/16.
 */
@RestController
@RequestMapping("/requestItem")
@Api(value = "requestItem", description = "Request Item", position = 1)
public class RequestItemRestController {

    @Autowired
    RequestParamaterValidatorService requestParamaterValidatorService;

    @Autowired
    PatronValidatorService patronValidatorService;

    @Autowired
    ItemValidatorService itemValidatorService;

    @RequestMapping(value = "/validateItemRequestInformations" , method = RequestMethod.POST)
    @ApiOperation(value = "validateItemRequestInformations",
            notes = "Validate Item Request Informations", nickname = "validateItemRequestInformation", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity validateItemRequestInformation(@ApiParam(value = "Parameters for requesting an item" , required = true , name = "requestItemJson")@RequestBody ItemRequestInformation itemRequestInfo){

        ResponseEntity responseEntity = null;
        responseEntity = requestParamaterValidatorService.validateItemRequestParameters(itemRequestInfo);
        if(responseEntity == null){
            responseEntity= patronValidatorService.patronValidation(itemRequestInfo.getRequestingInstitution(),itemRequestInfo.getPatronBarcode());
            if(responseEntity.getBody().equals(ReCAPConstants.VALID_PATRON)){
                //TO DO Item Barcode Validation
                return new ResponseEntity(ReCAPConstants.VALID_REQUEST,getHttpHeaders(), HttpStatus.OK);
            }
        }
        return responseEntity;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(ReCAPConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }
}
