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

    /**
     * The Server protocol.
     */
    @Value("${server.protocol}")
    String serverProtocol;

    /**
     * The Scsb circ url.
     */
    @Value("${scsb.circ.url}")
    String scsbCircUrl;

    /**
     * Gets server protocol.
     *
     * @return the server protocol
     */
    public String getServerProtocol() {
        return serverProtocol;
    }

    /**
     * Gets scsb circ url.
     *
     * @return the scsb circ url
     */
    public String getScsbCircUrl() {
        return scsbCircUrl;
    }

    /**
     * Get rest template rest template.
     *
     * @return the rest template
     */
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    /**
     * Get http entity http entity.
     *
     * @return the http entity
     */
    public HttpEntity getHttpEntity(){
        return new HttpEntity<>(getHttpHeaders());
    }

    /**
     * Gets logger.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Purge patron's email address response entity.
     * This method will call circ project to remove patron's email address after 60/90 days
     * @return the response entity
     */
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

    /**
     * Purge exception requests response entity.
     * This method will call circ project to remove exception request in the database
     *
     * @return the response entity
     */
    @RequestMapping(value = "/purgeExceptionRequests", method = RequestMethod.GET)
    public ResponseEntity purgeExceptionRequests() {
        Map response = null;
        try {
            HttpEntity requestEntity = getHttpEntity();
            ResponseEntity<Map> responseEntity = getRestTemplate().exchange(getServerProtocol() + getScsbCircUrl() + ReCAPConstants.REST_URL_PURGE_EXCEPTION_REQUESTS, HttpMethod.GET, requestEntity, Map.class);
            response = responseEntity.getBody();
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity(response, getHttpHeaders(), HttpStatus.OK);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(ReCAPConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

}
