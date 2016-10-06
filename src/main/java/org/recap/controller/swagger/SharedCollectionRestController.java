package org.recap.controller.swagger;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

}
