package org.recap.controller;

import java.util.Date;

import org.recap.ReCAPConstants;
import org.recap.service.RestHeaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

public class ReCAPController {

    @Value("${scsb.circ.url}")
    private String scsbCircUrl;

    @Value("${scsb.solr.client.url}")
    private String scsbSolrClient;

    @Value("${scsb.etl.url}")
    private String scsbEtlUrl;

    private static final Logger logger = LoggerFactory.getLogger(EncryptEmailAddressRestController.class);

    @Autowired
    private RestHeaderService restHeaderService;

    public ReCAPController() {
		super();
	}

    public String getScsbCircUrl() {
        return scsbCircUrl;
    }

    public String getScsbSolrClientUrl() {
        return scsbSolrClient;
    }

    public String getScsbEtlUrl() {
        return scsbEtlUrl;
    }

    public RestHeaderService getRestHeaderService(){
        return restHeaderService;
    }

    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    protected Logger getLogger() {
        return logger;
    }

    protected HttpEntity<Object> getHttpEntity(){
        return new HttpEntity<>(getHttpHeaders());
    }

    protected HttpHeaders getHttpHeaders() {
	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.add(ReCAPConstants.RESPONSE_DATE, new Date().toString());
	    return responseHeaders;
	}

    protected HttpStatus getHttpStatus(String message){
        if(message.equals(ReCAPConstants.DATADUMP_PROCESS_STARTED) || message.equals(ReCAPConstants.DATADUMP_NO_RECORD) || message.contains(ReCAPConstants.XML)){
            return HttpStatus.OK;
        }else{
            return HttpStatus.BAD_REQUEST;
        }
    }

}