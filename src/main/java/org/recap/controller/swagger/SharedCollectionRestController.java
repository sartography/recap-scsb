package org.recap.controller.swagger;

import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.recap.ReCAPConstants;
import org.recap.model.AccessionRequest;
import org.recap.model.BibItemAvailabityStatusRequest;
import org.recap.model.DeAccessionRequest;
import org.recap.model.ItemAvailabityStatusRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenchulakshmig on 6/10/16.
 */
@RestController
@RequestMapping("/sharedCollection")
@Api(value = "sharedCollection")
public class SharedCollectionRestController {

    private static final Logger logger = LoggerFactory.getLogger(SharedCollectionRestController.class);

    /**
     * The Scsb solr client url.
     */
    @Value("${scsb.solr.client.url}")
    String scsbSolrClientUrl;

    /**
     * The Scsb circ url.
     */
    @Value("${scsb.circ.url}")
    String scsbCircUrl;

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
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }

    /**
     * Gets scsb solr client url.
     *
     * @return the scsb solr client url
     */
    public String getScsbSolrClientUrl() {
        return scsbSolrClientUrl;
    }

    /**
     * Item availability status response entity.
     * This method will make rest call to sole client project to get Item availability status
     *
     * @param itemAvailabityStatus the item availabity status
     * @return the response entity
     */
    @RequestMapping(value = "/itemAvailabilityStatus", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "itemAvailabilityStatus",
            notes = "Item Availability Status", nickname = "itemAvailabilityStatus")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity itemAvailabilityStatus(@ApiParam(value = "Item Barcodes with ',' separated", required = true, name = "itemBarcodes") @RequestBody ItemAvailabityStatusRequest itemAvailabityStatus) {
        String response;
        try {
            response = getRestTemplate().postForObject(getScsbSolrClientUrl() + "/sharedCollection/itemAvailabilityStatus", itemAvailabityStatus, String.class);
        } catch (Exception exception) {
            logger.error(ReCAPConstants.LOG_ERROR, exception);
            return new ResponseEntity(ReCAPConstants.SCSB_SOLR_CLIENT_SERVICE_UNAVAILABLE, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (StringUtils.isEmpty(response)) {
            return new ResponseEntity(ReCAPConstants.ITEM_BARCDE_DOESNOT_EXIST, getHttpHeaders(), HttpStatus.OK);
        } else {
            return new ResponseEntity(response, getHttpHeaders(), HttpStatus.OK);
        }
    }

    /**
     * Bib availability status response entity.
     *
     * @param bibItemAvailabityStatusRequest the bib item availabity status request
     * @return the response entity
     */
    @RequestMapping(value = "/bibAvailabilityStatus", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "bibAvailabilityStatus",
            notes = "Bibliography Availability Status", nickname = "bibAvailabilityStatus")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),@ApiResponse(code = 503, message = "Service Not Available")})
    @ResponseBody
    public ResponseEntity bibAvailabilityStatus(@ApiParam(value = "Owning Inst BibID, or SCSB BibId", required = true, name = "") @RequestBody BibItemAvailabityStatusRequest bibItemAvailabityStatusRequest) {
        String response;
        try {
            response = getRestTemplate().postForObject(getScsbSolrClientUrl() + "/sharedCollection/bibAvailabilityStatus", bibItemAvailabityStatusRequest, String.class);
        } catch (Exception exception) {
            logger.error(ReCAPConstants.LOG_ERROR, exception);
            return new ResponseEntity(ReCAPConstants.SCSB_SOLR_CLIENT_SERVICE_UNAVAILABLE, getHttpHeaders(), HttpStatus.OK);
        }
        return new ResponseEntity(response, getHttpHeaders(), HttpStatus.OK);
    }

    /**
     * De accession response entity.
     * This method will call circ project to make soft delete the  given items in the database
     *
     * @param deAccessionRequest the de accession request
     * @return the response entity
     */
    @RequestMapping(value = "/deAccession", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "deaccession",
            notes = "Deaccession", nickname = "deaccession")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity deAccession(@ApiParam(value = "Provide item barcodes to be deaccessioned, separated by comma", required = true, name = "itemBarcodes") @RequestBody DeAccessionRequest deAccessionRequest) {
        try {
            Map<String, String> resultMap = getRestTemplate().postForObject(getScsbCircUrl() + "/sharedCollection/deAccession", deAccessionRequest, Map.class);
            return new ResponseEntity(resultMap, getHttpHeaders(), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ReCAPConstants.LOG_ERROR, ex);
            return new ResponseEntity(ReCAPConstants.SCSB_SOLR_CLIENT_SERVICE_UNAVAILABLE, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * Accession batch response entity.
     *
     *
     * @param accessionRequestList the accession request list
     * @return the response entity
     */
    @RequestMapping(value = "/accessionBatch", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "accessionBatch",
            notes = "Accession Batch", nickname = "accessionBatch")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity accessionBatch(@ApiParam(value = "Item Barcode and Customer Code", required = true, name = "Item Barcode And Customer Code") @RequestBody List<AccessionRequest> accessionRequestList) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            String responseMessage = getRestTemplate().postForObject(getScsbSolrClientUrl() + "sharedCollection/accessionBatch", accessionRequestList, String.class);
            ResponseEntity responseEntity = new ResponseEntity(responseMessage, getHttpHeaders(), HttpStatus.OK);
            stopWatch.stop();
            logger.info("Total time taken for saving accession request-->{}sec", stopWatch.getTotalTimeSeconds());
            return responseEntity;
        } catch (Exception exception) {
            logger.error(ReCAPConstants.LOG_ERROR, exception);
            return new ResponseEntity(ReCAPConstants.SCSB_SOLR_CLIENT_SERVICE_UNAVAILABLE, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * Accession response entity.
     * This method will call solr client project to add new item in the database
     * @param accessionRequestList the accession request list
     * @return the response entity
     */
    @RequestMapping(value = "/accession", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "accession",
            notes = "Accession", nickname = "accession")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity accession(@ApiParam(value = "Item Barcode and Customer Code", required = true, name = "Item Barcode And Customer Code") @RequestBody List<AccessionRequest> accessionRequestList) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            ResponseEntity responseEntity;
            List<LinkedHashMap> linkedHashMapList = getRestTemplate().postForObject(getScsbSolrClientUrl() + "sharedCollection/accession", accessionRequestList, List.class);
            if (null != linkedHashMapList && linkedHashMapList.get(0).get("message").toString().contains(ReCAPConstants.ONGOING_ACCESSION_LIMIT_EXCEED_MESSAGE)) {
                responseEntity = new ResponseEntity(linkedHashMapList, getHttpHeaders(), HttpStatus.BAD_REQUEST);
            } else {
                responseEntity = new ResponseEntity(linkedHashMapList, getHttpHeaders(), HttpStatus.OK);
            }
            stopWatch.stop();
            logger.info("Total time taken for accession-->{}sec",stopWatch.getTotalTimeSeconds());
            return responseEntity;
        } catch (Exception exception) {
            logger.error(ReCAPConstants.LOG_ERROR, exception);
            return new ResponseEntity(ReCAPConstants.SCSB_SOLR_CLIENT_SERVICE_UNAVAILABLE, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * Submit collection response entity.
     * This method will call circ project to update the items which is already present in the database
     * @param inputRecords the input records
     * @return the response entity
     */
    @RequestMapping(value = "/submitCollection", method = RequestMethod.POST)
    @ApiOperation(value = "submitCollection",
            notes = "Submit Collection", nickname = "submitCollection")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity submitCollection(@ApiParam(value = "Provide marc xml or scsb xml format to update the records", required = true, name = "inputRecords") @RequestBody String inputRecords) {
        ResponseEntity responseEntity;
        try {
            List<LinkedHashMap> linkedHashMapList =getRestTemplate().postForObject(getScsbCircUrl() + "sharedCollection/submitCollection", inputRecords, List.class);
            String message = linkedHashMapList != null ? linkedHashMapList.get(0).get("message").toString():ReCAPConstants.SUBMIT_COLLECTION_INTERNAL_ERROR;
            if (message.equalsIgnoreCase(ReCAPConstants.INVALID_MARC_XML_FORMAT_MESSAGE) || message.equalsIgnoreCase(ReCAPConstants.INVALID_SCSB_XML_FORMAT_MESSAGE)
                    || message.equalsIgnoreCase(ReCAPConstants.SUBMIT_COLLECTION_INTERNAL_ERROR)) {
                responseEntity = new ResponseEntity(linkedHashMapList, getHttpHeaders(), HttpStatus.BAD_REQUEST);
            } else {
                responseEntity = new ResponseEntity(linkedHashMapList, getHttpHeaders(), HttpStatus.OK);
            }
            return responseEntity;
        } catch (Exception exception) {
            logger.error(ReCAPConstants.LOG_ERROR, exception);
            responseEntity = new ResponseEntity(ReCAPConstants.SUBMIT_COLLECTION_INTERNAL_ERROR, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
            return responseEntity;
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(ReCAPConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

}