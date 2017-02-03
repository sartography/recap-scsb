package org.recap.controller.swagger;

import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.recap.ReCAPConstants;
import org.recap.model.AccessionRequest;
import org.recap.model.DeAccessionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * Created by chenchulakshmig on 6/10/16.
 */
@RestController
@RequestMapping("/sharedCollection")
@Api(value = "sharedCollection", description = "Shared Collection", position = 1)
public class SharedCollectionRestController {

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.solr.client.url}")
    String scsbSolrClientUrl;

    @Value("${scsb.circ.url}")
    String scsbCircUrl;

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

    public String getScsbSolrClientUrl() {
        return scsbSolrClientUrl;
    }

    public void setScsbSolrClientUrl(String scsbSolrClientUrl) {
        this.scsbSolrClientUrl = scsbSolrClientUrl;
    }

    @RequestMapping(value = "/itemAvailabilityStatus", method = RequestMethod.GET)
    @ApiOperation(value = "itemAvailabilityStatus",
            notes = "Item Availability Status", nickname = "itemAvailabilityStatus", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity itemAvailabilityStatus(@ApiParam(value = "Item Barcode", required = true, name = "itemBarcode") @RequestParam String itemBarcode) {
        RestTemplate restTemplate = new RestTemplate();
        String itemStatus = null;
        try {
            itemStatus = getRestTemplate()
                    .getForObject(getServerProtocol()+getScsbSolrClientUrl()+"/sharedCollection/itemAvailabilityStatus?itemBarcode="+itemBarcode,  String.class);
        } catch (Exception exception) {
            ResponseEntity responseEntity = new ResponseEntity("Scsb Solr Client Service is Unavailable.", getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
            return responseEntity;
        }
        if (StringUtils.isEmpty(itemStatus)) {
            ResponseEntity responseEntity = new ResponseEntity(ReCAPConstants.ITEM_BARCDE_DOESNOT_EXIST, getHttpHeaders(), HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity responseEntity = new ResponseEntity(itemStatus, getHttpHeaders(), HttpStatus.OK);
            return responseEntity;
        }
    }

    @RequestMapping(value = "/deAccession", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "deAccession",
            notes = "De Accession", nickname = "deaccession", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity deAccession(@ApiParam(value = "Item Barcodes with ',' separated", required = true, name = "itemBarcodes") @RequestBody DeAccessionRequest deAccessionRequest) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = getRestTemplate().postForObject(getServerProtocol() + getScsbSolrClientUrl() + "/sharedCollection/deAccession", deAccessionRequest, String.class);
            ResponseEntity responseEntity = new ResponseEntity(response, getHttpHeaders(), HttpStatus.OK);
            return responseEntity;
        } catch (Exception ex) {
            ResponseEntity responseEntity = new ResponseEntity("Scsb Solr Client Service is Unavailable.", getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
            return responseEntity;
        }
    }

    @RequestMapping(value = "/accession", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "accession",
            notes = "Accession", nickname = "accession", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity accession(@ApiParam(value = "Item Barcode and Customer Code", required = true, name = "Item Barcode And Customer Code") @RequestBody AccessionRequest accessionRequest) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = getRestTemplate().postForObject(getServerProtocol() + getScsbSolrClientUrl() + "sharedCollection/accession", accessionRequest, String.class);
            ResponseEntity responseEntity = new ResponseEntity(response, getHttpHeaders(), HttpStatus.OK);
            return responseEntity;
        } catch (Exception exception) {
            ResponseEntity responseEntity = new ResponseEntity("Scsb Solr Client Service is Unavailable.", getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
            return responseEntity;
        }
    }

    @RequestMapping(value = "/submitCollection", method = RequestMethod.POST)
    @ApiOperation(value = "submitCollection",
            notes = "Submit Collection", nickname = "submitCollection", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity submitCollection(@ApiParam(value = "Provide marc xml or scsb xml format to update the records" , required = true, name = "inputRecords")@RequestBody String inputRecords) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        ResponseEntity responseEntity;
        try {
            String response = getRestTemplate().postForObject(getServerProtocol() + getScsbCircUrl() + "sharedCollection/submitCollection", inputRecords, String.class);
            if(response.equalsIgnoreCase(ReCAPConstants.INVALID_MARC_XML_FORMAT_MESSAGE) || response.equalsIgnoreCase(ReCAPConstants.INVALID_SCSB_XML_FORMAT_MESSAGE)
                    || response.equalsIgnoreCase(ReCAPConstants.SUBMIT_COLLECTION_INTERNAL_ERROR)){
                responseEntity = new ResponseEntity(response, getHttpHeaders(), HttpStatus.BAD_REQUEST);
            }else{
                responseEntity = new ResponseEntity(response, getHttpHeaders(), HttpStatus.OK);
            }
            return responseEntity;
        } catch (Exception exception) {
            exception.printStackTrace();
            responseEntity = new ResponseEntity("Scsb Circ Service is Unavailable.", getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
            return responseEntity;
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(ReCAPConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

}