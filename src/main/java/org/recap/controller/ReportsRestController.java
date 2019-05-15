package org.recap.controller;

import org.recap.ReCAPConstants;
import org.recap.model.ReportsRequest;
import org.recap.model.ReportsResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rajeshbabuk on 13/1/17.
 */
@RestController
@RequestMapping("/reportsService")
public class ReportsRestController extends ReCAPController {

    /**
     * This method will call scsb-solr-client microservice to get total counts of accessioned and deaccessioned items in scsb.
     * @param reportsRequest the reports request
     * @return the reports response
     */
    @RequestMapping(value="/accessionDeaccessionCounts", method = RequestMethod.POST)
    public ReportsResponse accessionDeaccessionCounts(@RequestBody ReportsRequest reportsRequest) {
        ReportsResponse reportsResponse = new ReportsResponse();
        try {
            HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, getRestHeaderService().getHttpHeaders());

            ResponseEntity<ReportsResponse> responseEntity = getRestTemplate().exchange(getScsbSolrClientUrl() + ReCAPConstants.URL_REPORTS_ACCESSION_DEACCESSION_COUNTS, HttpMethod.POST, httpEntity, ReportsResponse.class);
            reportsResponse = responseEntity.getBody();
        } catch (Exception e) {
            getLogger().error(ReCAPConstants.LOG_ERROR,e);
            reportsResponse.setMessage(e.getMessage());
        }
        return reportsResponse;
    }

    /**
     * This method will call scsb-solr-client microservice to get items counts based on the collection group designation.
     * @param reportsRequest the reports request
     * @return the reports response
     */
    @RequestMapping(value="/cgdItemCounts", method = RequestMethod.POST)
    public ReportsResponse cgdItemCounts(@RequestBody ReportsRequest reportsRequest) {
        ReportsResponse reportsResponse = new ReportsResponse();
        try {
            HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, getRestHeaderService().getHttpHeaders());

            ResponseEntity<ReportsResponse> responseEntity = getRestTemplate().exchange(getScsbSolrClientUrl() + ReCAPConstants.URL_REPORTS_CGD_ITEM_COUNTS, HttpMethod.POST, httpEntity, ReportsResponse.class);
            reportsResponse = responseEntity.getBody();
        } catch (Exception e) {
            getLogger().error(ReCAPConstants.LOG_ERROR,e);
            reportsResponse.setMessage(e.getMessage());
        }
        return reportsResponse;
    }

    /**
     * This method will call scsb-solr-client microservice to get the items which are deaccessioned in scsb.
     *
     * @param reportsRequest the reports request
     * @return the reports response
     */
    @RequestMapping(value="/deaccessionResults", method = RequestMethod.POST)
    public ReportsResponse deaccessionResults(@RequestBody ReportsRequest reportsRequest) {
        ReportsResponse reportsResponse = new ReportsResponse();
        try {
            HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, getRestHeaderService().getHttpHeaders());

            ResponseEntity<ReportsResponse> responseEntity = getRestTemplate().exchange(getScsbSolrClientUrl() + ReCAPConstants.URL_REPORTS_DEACCESSION_RESULTS, HttpMethod.POST, httpEntity, ReportsResponse.class);
            reportsResponse = responseEntity.getBody();
        } catch (Exception e) {
            getLogger().error(ReCAPConstants.LOG_ERROR,e);
            reportsResponse.setMessage(e.getMessage());
        }
        return reportsResponse;
    }

    /**
     * This method will call scsb-solr-client microservice to get the item incomplete records results.
     *
     * @param reportsRequest the reports request
     * @return the reports response
     */
    @RequestMapping(value="/incompleteRecords", method = RequestMethod.POST)
    public ReportsResponse incompleteRecords(@RequestBody ReportsRequest reportsRequest) {
        ReportsResponse reportsResponse = new ReportsResponse();
        try {
            HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, getRestHeaderService().getHttpHeaders());
            ResponseEntity<ReportsResponse> responseEntity = getRestTemplate().exchange(getScsbSolrClientUrl() + ReCAPConstants.URL_REPORTS_INCOMPLETE_RESULTS, HttpMethod.POST, httpEntity, ReportsResponse.class);
            reportsResponse = responseEntity.getBody();
        } catch (Exception e) {
            getLogger().error(ReCAPConstants.LOG_ERROR,e);
            reportsResponse.setMessage(e.getMessage());
        }
        return reportsResponse;
    }
}
