package org.recap.controller.swagger;


import org.recap.ReCAPConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

/**
 * Created by hemalathas on 10/4/17.
 */
@RestController
@RequestMapping("/purgeRestController")
public class PurgeRestController {

    private static final Logger logger = LoggerFactory.getLogger(PurgeRestController.class);

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.circ.url}")
    String scsbCircUrl;

    public String getServerProtocol() {
        return serverProtocol;
    }

    public String getScsbCircUrl() {
        return scsbCircUrl;
    }

    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    public HttpEntity getHttpEntity(){
        return new HttpEntity<>(getHttpHeaders());
    }

    public Logger getLogger() {
        return logger;
    }

    @RequestMapping(value = "/purgeEmailAddress", method = RequestMethod.GET)
    public ResponseEntity purgeEmailAddress(){
        ResponseEntity<Map> responseEntity = null;
        Map response = null;
        try{
            HttpEntity requestEntity = getHttpEntity();
            responseEntity = getRestTemplate().exchange(getServerProtocol() + getScsbCircUrl()+ ReCAPConstants.REST_URL_PURGE_EMAIL_ADDRESS, HttpMethod.GET,requestEntity,Map.class);
            response = responseEntity.getBody();
        }catch(Exception e){
            getLogger().error("Exception",e);
        }

        return new ResponseEntity(response,getHttpHeaders(),HttpStatus.OK);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(ReCAPConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

}
