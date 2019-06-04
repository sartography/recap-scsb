package org.recap.controller;

import java.util.Date;

import lombok.AccessLevel;
import lombok.Getter;
import org.recap.ReCAPConstants;
import org.recap.service.RestHeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

@Getter
public class ReCAPController {

    @Value("${scsb.circ.url}")
    private String scsbCircUrl;

    @Value("${scsb.solr.client.url}")
    private String scsbSolrClientUrl;

    @Value("${scsb.etl.url}")
    private String scsbEtlUrl;

    @Value("${scsb.batch.schedule.url}")
    private String scsbScheduleUrl;

    @Autowired
    private RestHeaderService restHeaderService;

    public ReCAPController() {
		super();
	}

    public RestTemplate getRestTemplate(){
        return new RestTemplate();
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