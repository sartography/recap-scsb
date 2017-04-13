package org.recap.controller.swagger;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.ReCAPConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 10/4/17.
 */
public class PurgeRestControllerUT extends BaseTestCase{

    private static final Logger logger = LoggerFactory.getLogger(PurgeRestController.class);

    @Mock
    PurgeRestController purgeEmailAddressRestController;

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.circ.url}")
    String scsbCircUrl;

    public String getScsbCircUrl() {
        return scsbCircUrl;
    }

    public String getServerProtocol() {
        return serverProtocol;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    @Mock
    RestTemplate restTemplate;

    @Test
    public void testPurgeEmailAddress(){
        Mockito.when(purgeEmailAddressRestController.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(purgeEmailAddressRestController.getServerProtocol()).thenReturn(serverProtocol);
        Mockito.when(purgeEmailAddressRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(purgeEmailAddressRestController.getLogger()).thenReturn(logger);
        HttpEntity requestEntity = new HttpEntity<>(getHttpHeaders());
        Mockito.when(purgeEmailAddressRestController.getHttpEntity()).thenReturn(requestEntity);
        Map<String,Integer> map = new HashMap<>();
        map.put("physicalRequest",1);
        map.put("eddRequest",1);
        ResponseEntity responseEntity1 = new ResponseEntity(map, HttpStatus.OK);
        Mockito.when(purgeEmailAddressRestController.getRestTemplate().exchange(getServerProtocol() + getScsbCircUrl()+ ReCAPConstants.REST_URL_PURGE_EMAIL_ADDRESS, HttpMethod.GET,requestEntity,Map.class)).thenReturn(responseEntity1);
        Mockito.when(purgeEmailAddressRestController.purgeEmailAddress()).thenCallRealMethod();
        ResponseEntity responseEntity = purgeEmailAddressRestController.purgeEmailAddress();
        assertNotNull(responseEntity);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(ReCAPConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }



}