package org.recap.controller;

import org.recap.ReCAPConstants;
import org.recap.model.ReportsRequest;
import org.recap.model.ReportsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by rajeshbabuk on 13/1/17.
 */

@RestController
@RequestMapping("/reportsService")
public class ReportsRestController {

    private Logger logger = LoggerFactory.getLogger(UpdateCgdRestController.class);

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.solr.client.url}")
    String scsbSolrClient;

    public String getServerProtocol() {
        return serverProtocol;
    }

    public void setServerProtocol(String serverProtocol) {
        this.serverProtocol = serverProtocol;
    }

    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public String getScsbSolrClientUrl() {
        return scsbSolrClient;
    }

    public void setScsbSolrClientUrl(String scsbSolrClientUrl) {
        this.scsbSolrClient = scsbSolrClientUrl;
    }

    @RequestMapping(value="/accessionDeaccessionCounts", method = RequestMethod.POST)
    public ReportsResponse accessionDeaccessionCounts(@RequestBody ReportsRequest reportsRequest) {
        ReportsResponse reportsResponse = new ReportsResponse();
        try {
            HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, getHttpHeaders());

            ResponseEntity<ReportsResponse> responseEntity = getRestTemplate().exchange(getServerProtocol() + getScsbSolrClientUrl() + ReCAPConstants.URL_REPORTS_ACCESSION_DEACCESSION_COUNTS, HttpMethod.POST, httpEntity, ReportsResponse.class);
            reportsResponse = responseEntity.getBody();
        } catch (Exception e) {
            logger.error(ReCAPConstants.LOG_ERROR,e);
            reportsResponse.setMessage(e.getMessage());
        }
        return reportsResponse;
    }

    @RequestMapping(value="/cgdItemCounts", method = RequestMethod.POST)
    public ReportsResponse cgdItemCounts(@RequestBody ReportsRequest reportsRequest) {
        ReportsResponse reportsResponse = new ReportsResponse();
        try {
            RestTemplate restTemplate;
            HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, getHttpHeaders());

            ResponseEntity<ReportsResponse> responseEntity = getRestTemplate().exchange(getServerProtocol() + getScsbSolrClientUrl() + ReCAPConstants.URL_REPORTS_CGD_ITEM_COUNTS, HttpMethod.POST, httpEntity, ReportsResponse.class);
            reportsResponse = responseEntity.getBody();
        } catch (Exception e) {
            logger.error(ReCAPConstants.LOG_ERROR,e);
            reportsResponse.setMessage(e.getMessage());
        }
        return reportsResponse;
    }

    @RequestMapping(value="/deaccessionResults", method = RequestMethod.POST)
    public ReportsResponse deaccessionResults(@RequestBody ReportsRequest reportsRequest) {
        ReportsResponse reportsResponse = new ReportsResponse();
        try {
            RestTemplate restTemplate;
            HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, getHttpHeaders());

            ResponseEntity<ReportsResponse> responseEntity = getRestTemplate().exchange(getServerProtocol() + getScsbSolrClientUrl() + ReCAPConstants.URL_REPORTS_DEACCESSION_RESULTS, HttpMethod.POST, httpEntity, ReportsResponse.class);
            reportsResponse = responseEntity.getBody();
        } catch (Exception e) {
            logger.error(ReCAPConstants.LOG_ERROR,e);
            reportsResponse.setMessage(e.getMessage());
        }
        return reportsResponse;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(ReCAPConstants.API_KEY, ReCAPConstants.RECAP);
        return headers;
    }
}
