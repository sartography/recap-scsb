package org.recap.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by akulak on 21/9/17.
 */
@Slf4j
@RestController
@RequestMapping("/encryptEmailAddressService")
public class EncryptEmailAddressRestController extends ReCAPController {
	
    @RequestMapping(value = "/encryptEmailAddress", method = RequestMethod.GET)
    public ResponseEntity<String> encryptEmailAddress() {
        String response = "";
        try {
            HttpEntity<Object> requestEntity = getHttpEntity();
            ResponseEntity<String> exchange = getRestTemplate().exchange(getScsbCircUrl() + "/encryptEmailAddress/startEncryptEmailAddress", HttpMethod.GET, requestEntity, String.class);
            response  = exchange.getBody();
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return new ResponseEntity<String>(response, getHttpHeaders(), HttpStatus.OK);
    }
}
