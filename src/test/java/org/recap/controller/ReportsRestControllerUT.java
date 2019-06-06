package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.ReCAPConstants;
import org.recap.service.RestHeaderService;
import org.recap.model.DeaccessionItemResultsRow;
import org.recap.model.ReportsRequest;
import org.recap.model.ReportsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ReportsRestControllerUT extends BaseControllerUT {

    @Value("${scsb.solr.client.url}")
    String scsbSolrClientUrl;

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock
    private ReportsRestController reportsRestController;

    @MockBean
    private RestHeaderService restHeaderService;

    public String getScsbSolrClientUrl() {
        return scsbSolrClientUrl;
    }

    public void setScsbSolrClientUrl(String scsbSolrClientUrl) {
        this.scsbSolrClientUrl = scsbSolrClientUrl;
    }

    @Test
    public void givenReportsRequest_whenGetReportsRequest_thenReturnReportsResponse() throws Exception {
        ReportsRequest reportsRequest = ReportsRequest.builder().deaccessionOwningInstitution("CUL").build();
        reportsRequest.setDeaccessionOwningInstitution("CUL");
        ReportsResponse reportsResponse = ReportsResponse.builder().build();
        Mockito.when(reportsRestController._getResponse(reportsRequest, ReCAPConstants.URL_REPORTS_ACCESSION_DEACCESSION_COUNTS)).thenReturn(reportsResponse);

        mvc.perform(get("/" + ReCAPConstants.URL_REPORTS_ACCESSION_DEACCESSION_COUNTS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].deaccessionOwningInstitution", is(reportsRequest.getDeaccessionOwningInstitution())));
    }

    @Test
    public void accessionDeaccessionCounts() throws Exception {
        ReportsRequest reportsRequest = ReportsRequest
    		.builder()
	        .accessionDeaccessionFromDate("09/27/2016")
	        .accessionDeaccessionToDate("01/27/2017")
	        .owningInstitutions(Arrays.asList("CUL", "PUL", "NYPL"))
	        .collectionGroupDesignations(Arrays.asList("Private", "Open", "Shared"))
	        .pageNumber(1)
	        .pageSize(10)
	        .deaccessionOwningInstitution("CUL")
	        .incompleteRequestingInstitution("PUL")
	        .incompletePageNumber(10)
	        .incompletePageSize(10)
	        .export(true)
	        .build();
        DeaccessionItemResultsRow deaccessionItemResultsRow = getDeaccessionItemResultsRow();
        ReportsResponse reportsResponse = ReportsResponse.builder().message(ReCAPConstants.SUCCESS).build();
        reportsResponse.setDeaccessionItemResultsRows(Arrays.asList(deaccessionItemResultsRow));
        ResponseEntity<ReportsResponse> responseEntity = new ResponseEntity<ReportsResponse>(reportsResponse,HttpStatus.OK);

        HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, restHeaderService.getHttpHeaders());
        Mockito.when(mockRestTemplate.exchange(getScsbSolrClientUrl() + ReCAPConstants.URL_REPORTS_ACCESSION_DEACCESSION_COUNTS, HttpMethod.POST,httpEntity, ReportsResponse.class)).thenReturn(responseEntity);
        Mockito.when(reportsRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(reportsRestController.getRestHeaderService()).thenReturn(restHeaderService);
        Mockito.when(reportsRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClientUrl);
        Mockito.when(reportsRestController.accessionDeaccessionCounts(reportsRequest)).thenCallRealMethod();
        ReportsResponse reportsResponse1 = reportsRestController.accessionDeaccessionCounts(reportsRequest);
        assertNotNull(reportsResponse1);
        assertNotNull(deaccessionItemResultsRow.getCgd());
        assertNotNull(deaccessionItemResultsRow.getDeaccessionDate());
        assertNotNull(deaccessionItemResultsRow.getDeaccessionNotes());
        assertEquals(deaccessionItemResultsRow.getDeaccessionOwnInst(),"PUL");
        assertNotNull(deaccessionItemResultsRow.getItemBarcode());
        assertNotNull(deaccessionItemResultsRow.getItemId());
        assertNotNull(deaccessionItemResultsRow.getTitle());
        assertNotNull(reportsRequest.getAccessionDeaccessionFromDate());
        assertNotNull(reportsRequest.getAccessionDeaccessionToDate());
        assertNotNull(reportsRequest.getOwningInstitutions());
        assertNotNull(reportsRequest.getCollectionGroupDesignations());
        assertNotNull(reportsRequest.getPageNumber());
        assertNotNull(reportsRequest.getPageSize());
        assertNotNull(reportsRequest.getDeaccessionOwningInstitution());
        assertNotNull(reportsRequest.getIncompleteRequestingInstitution());
        assertNotNull(reportsRequest.getIncompletePageNumber());
        assertNotNull(reportsRequest.getIncompletePageSize());
        assertNotNull(reportsRequest.isExport());
    }

    public DeaccessionItemResultsRow getDeaccessionItemResultsRow(){
        DeaccessionItemResultsRow deaccessionItemResultsRow = DeaccessionItemResultsRow
    		.builder()
	        .itemId(123)
	        .cgd("Open")
	        .deaccessionDate(new Date().toString())
	        .deaccessionNotes("test")
	        .title("test")
	        .deaccessionOwnInst("PUL")
	        .itemBarcode("326598741256985")
	        .build();
        return deaccessionItemResultsRow;
    }

    @Test
    public void cgdItemCounts() throws Exception {
        ReportsRequest reportsRequest = ReportsRequest
    		.builder()
	        .owningInstitutions(Arrays.asList("CUL", "PUL", "NYPL"))
	        .collectionGroupDesignations(Arrays.asList("Private", "Open", "Shared"))
	        .build();
        ReportsResponse reportsResponse = ReportsResponse.builder().message(ReCAPConstants.SUCCESS).build();
        ResponseEntity<ReportsResponse> responseEntity = new ResponseEntity<ReportsResponse>(reportsResponse,HttpStatus.OK);
        HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, restHeaderService.getHttpHeaders());
        Mockito.when(mockRestTemplate.exchange(getScsbSolrClientUrl() + ReCAPConstants.URL_REPORTS_CGD_ITEM_COUNTS, HttpMethod.POST,httpEntity, ReportsResponse.class)).thenReturn(responseEntity);
        Mockito.when(reportsRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(reportsRestController.getRestHeaderService()).thenReturn(restHeaderService);
        Mockito.when(reportsRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClientUrl);
        Mockito.when(reportsRestController.cgdItemCounts(reportsRequest)).thenCallRealMethod();
        ReportsResponse reportsResponse1 = reportsRestController.cgdItemCounts(reportsRequest);
        assertNotNull(reportsResponse1);
    }

    @Test
    public void deaccessionResults() throws Exception {
        ReportsRequest reportsRequest = ReportsRequest
    		.builder()
    		.accessionDeaccessionFromDate("09/27/2016")
	        .accessionDeaccessionToDate("01/27/2017")
	        .deaccessionOwningInstitution("PUL")
	        .build();
        ReportsResponse reportsResponse = ReportsResponse.builder().message(ReCAPConstants.SUCCESS).build();
        ResponseEntity<ReportsResponse> responseEntity = new ResponseEntity<ReportsResponse>(reportsResponse,HttpStatus.OK);
        HttpEntity<ReportsRequest> httpEntity = new HttpEntity<>(reportsRequest, restHeaderService.getHttpHeaders());
        Mockito.when(mockRestTemplate.exchange(getScsbSolrClientUrl() + ReCAPConstants.URL_REPORTS_DEACCESSION_RESULTS, HttpMethod.POST,httpEntity, ReportsResponse.class)).thenReturn(responseEntity);
        Mockito.when(reportsRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(reportsRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClientUrl);
        Mockito.when(reportsRestController.getRestHeaderService()).thenReturn(restHeaderService);
        Mockito.when(reportsRestController.deaccessionResults(reportsRequest)).thenCallRealMethod();
        ReportsResponse reportsResponse1 = reportsRestController.deaccessionResults(reportsRequest);
        assertNotNull(reportsResponse1);
    }

}
