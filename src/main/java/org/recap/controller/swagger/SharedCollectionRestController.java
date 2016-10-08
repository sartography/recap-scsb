package org.recap.controller.swagger;

import io.swagger.annotations.*;
import org.recap.ReCAPConstants;
import org.recap.model.DeAccessionDBResponseEntity;
import org.recap.util.DeAccessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenchulakshmig on 6/10/16.
 */
@RestController
@RequestMapping("/sharedCollection")
@Api(value = "sharedCollection", description = "Shared Collection", position = 1)
public class SharedCollectionRestController {

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.persistence.url}")
    String scsbPersistenceUrl;

    @Value("${scsb.solr.client.url}")
    String scsbSolrClientUrl;

    @Autowired
    DeAccessionUtil deAccessionUtil;

    @RequestMapping(value = "/itemAvailabilityStatus", method = RequestMethod.GET)
    @ApiOperation(value = "itemAvailabilityStatus",
            notes = "Item Availability Status", nickname = "itemAvailabilityStatus", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity itemAvailabilityStatus(@ApiParam(value = "Item Barcode", required = true, name = "itemBarcode") @RequestParam String itemBarcode) {
        RestTemplate restTemplate = new RestTemplate();
        String itemStatus = null;
        try {
            itemStatus = restTemplate.getForObject(serverProtocol + scsbPersistenceUrl + "item/getItemStatusByBarcodeAndIsDeletedFalse?barcode=" + itemBarcode, String.class);
        } catch (Exception exception) {
            ResponseEntity responseEntity = new ResponseEntity("Scsb Persistence Service is Unavailable.", HttpStatus.SERVICE_UNAVAILABLE);
            return responseEntity;
        }
        if (StringUtils.isEmpty(itemStatus)) {
            ResponseEntity responseEntity = new ResponseEntity("Item Barcode doesn't exist in SCSB database.", HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity responseEntity = new ResponseEntity(itemStatus, HttpStatus.OK);
            return responseEntity;
        }
    }

    @RequestMapping(value = "/deAccession", method = RequestMethod.GET)
    @ApiOperation(value = "deAccession",
            notes = "De Accession", nickname = "deaccession", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity deAccession(@ApiParam(value = "Item Barcodes with ',' separated", required = true, name = "itemBarcodes") @RequestParam String itemBarcodes) {
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = deAccessionUtil.deAccessionItemsInDB(itemBarcodes);
        if (!CollectionUtils.isEmpty(deAccessionDBResponseEntities)) {
            Map<String, String> resultMap = new HashMap<>();
            for (DeAccessionDBResponseEntity deAccessionDBResponseEntity : deAccessionDBResponseEntities) {
                if (deAccessionDBResponseEntity.getStatus().equalsIgnoreCase(ReCAPConstants.FAILURE)) {
                    resultMap.put(deAccessionDBResponseEntity.getBarcode(), deAccessionDBResponseEntity.getStatus() + " - " + deAccessionDBResponseEntity.getReasonForFailure());
                } else {
                    resultMap.put(deAccessionDBResponseEntity.getBarcode(), deAccessionDBResponseEntity.getStatus());
                }
            }
            ResponseEntity responseEntity = new ResponseEntity(resultMap, HttpStatus.OK);
            return responseEntity;
        }
        return null;
    }

}
