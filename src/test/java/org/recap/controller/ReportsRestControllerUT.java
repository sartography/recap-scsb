package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.ReCAPConstants;
import org.recap.model.ReportsRequest;
import org.recap.model.ReportsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 13/1/17.
 */
public class ReportsRestControllerUT extends BaseControllerUT {

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.solr.client.url}")
    String scsbSolrClientUrl;

    @Mock
    RestTemplate mockRestTemplate;

    @Mock
    ReportsRestController reportsRestController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    public String getServerProtocol() {
        return serverProtocol;
    }

    public void setServerProtocol(String serverProtocol) {
        this.serverProtocol = serverProtocol;
    }

    public String getScsbSolrClientUrl() {
        return scsbSolrClientUrl;
    }

    public void setScsbSolrClientUrl(String scsbSolrClientUrl) {
        this.scsbSolrClientUrl = scsbSolrClientUrl;
    }

    @Test
    public void accessionDeaccessionCounts() throws Exception {
        ReportsRequest reportsRequest = new ReportsRequest();
        reportsRequest.setAccessionDeaccessionFromDate("09/27/2016");
        reportsRequest.setAccessionDeaccessionToDate("01/27/2017");
        reportsRequest.setOwningInstitutions(Arrays.asList("CUL", "PUL", "NYPL"));
        reportsRequest.setCollectionGroupDesignations(Arrays.asList("Private", "Open", "Shared"));
        ReportsResponse reportsResponse = new ReportsResponse();
        reportsResponse.setMessage(ReCAPConstants.SUCCESS);
        ResponseEntity<ReportsResponse> responseEntity = new ResponseEntity<ReportsResponse>(reportsResponse,HttpStatus.OK);

        HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, getHttpHeaders());
        Mockito.when(mockRestTemplate.exchange(getServerProtocol() + getScsbSolrClientUrl() + ReCAPConstants.URL_REPORTS_ACCESSION_DEACCESSION_COUNTS, HttpMethod.POST,httpEntity, ReportsResponse.class)).thenReturn(responseEntity);
        Mockito.when(reportsRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(reportsRestController.getServerProtocol()).thenReturn(serverProtocol);
        Mockito.when(reportsRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClientUrl);
        Mockito.when(reportsRestController.accessionDeaccessionCounts(reportsRequest)).thenCallRealMethod();
        ReportsResponse reportsResponse1 = reportsRestController.accessionDeaccessionCounts(reportsRequest);
        assertNotNull(reportsResponse1);
    }

    @Test
    public void cgdItemCounts() throws Exception {
        ReportsRequest reportsRequest = new ReportsRequest();
        reportsRequest.setOwningInstitutions(Arrays.asList("CUL", "PUL", "NYPL"));
        reportsRequest.setCollectionGroupDesignations(Arrays.asList("Private", "Open", "Shared"));
        ReportsResponse reportsResponse = new ReportsResponse();
        reportsResponse.setMessage(ReCAPConstants.SUCCESS);
        ResponseEntity<ReportsResponse> responseEntity = new ResponseEntity<ReportsResponse>(reportsResponse,HttpStatus.OK);
        HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, getHttpHeaders());
        Mockito.when(mockRestTemplate.exchange(getServerProtocol() + getScsbSolrClientUrl() + ReCAPConstants.URL_REPORTS_CGD_ITEM_COUNTS, HttpMethod.POST,httpEntity, ReportsResponse.class)).thenReturn(responseEntity);
        Mockito.when(reportsRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(reportsRestController.getServerProtocol()).thenReturn(serverProtocol);
        Mockito.when(reportsRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClientUrl);
        Mockito.when(reportsRestController.cgdItemCounts(reportsRequest)).thenCallRealMethod();
        ReportsResponse reportsResponse1 = reportsRestController.cgdItemCounts(reportsRequest);
        assertNotNull(reportsResponse1);
    }

    @Test
    public void deaccessionResults() throws Exception {
        ReportsRequest reportsRequest = new ReportsRequest();
        reportsRequest.setAccessionDeaccessionFromDate("09/27/2016");
        reportsRequest.setAccessionDeaccessionToDate("01/27/2017");
        reportsRequest.setDeaccessionOwningInstitution("PUL");
        ReportsResponse reportsResponse = new ReportsResponse();
        reportsResponse.setMessage(ReCAPConstants.SUCCESS);
        ResponseEntity<ReportsResponse> responseEntity = new ResponseEntity<ReportsResponse>(reportsResponse,HttpStatus.OK);
        HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, getHttpHeaders());
        Mockito.when(mockRestTemplate.exchange(getServerProtocol() + getScsbSolrClientUrl() + ReCAPConstants.URL_REPORTS_DEACCESSION_RESULTS, HttpMethod.POST,httpEntity, ReportsResponse.class)).thenReturn(responseEntity);
        Mockito.when(reportsRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(reportsRestController.getServerProtocol()).thenReturn(serverProtocol);
        Mockito.when(reportsRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClientUrl);
        Mockito.when(reportsRestController.deaccessionResults(reportsRequest)).thenCallRealMethod();
        ReportsResponse reportsResponse1 = reportsRestController.deaccessionResults(reportsRequest);
        assertNotNull(reportsResponse1);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(ReCAPConstants.API_KEY, ReCAPConstants.RECAP);
        return headers;
    }
}
