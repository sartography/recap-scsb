package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.ReCAPConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by rajeshbabuk on 3/1/17.
 */
public class UpdateCgdRestControllerUT extends BaseControllerUT {

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.solr.client.url}")
    String scsbSolrClient;

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock
    private UpdateCgdRestController updateCgdRestController;

    @Mock
    private UriComponentsBuilder builder;


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
        return scsbSolrClient;
    }

    public void setScsbSolrClientUrl(String scsbSolrClientUrl) {
        this.scsbSolrClient = scsbSolrClient;
    }

    @Test
    public void updateCgdForItem() throws Exception {
        String itemBarcode = "3568783121445";
        String owningInstitution = "PUL";
        String oldCollectionGroupDesignation = "Shared";
        String newCollectionGroupDesignation = "Private";
        String cgdChangeNotes = "Notes";
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(ReCAPConstants.SUCCESS,HttpStatus.OK);
        updateCgdRestController.setScsbSolrClientUrl(getScsbSolrClientUrl());
        updateCgdRestController.setServerProtocol(getServerProtocol());
        HttpEntity requestEntity = new HttpEntity<>(getHttpHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverProtocol + scsbSolrClient + ReCAPConstants.URL_UPDATE_CGD)
                .queryParam(ReCAPConstants.CGD_UPDATE_ITEM_BARCODE, itemBarcode)
                .queryParam(ReCAPConstants.OWNING_INSTITUTION, owningInstitution)
                .queryParam(ReCAPConstants.OLD_CGD, oldCollectionGroupDesignation)
                .queryParam(ReCAPConstants.NEW_CGD, newCollectionGroupDesignation)
                .queryParam(ReCAPConstants.CGD_CHANGE_NOTES, cgdChangeNotes);
        Mockito.when(mockRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, String.class)).thenReturn(responseEntity);
        Mockito.when(updateCgdRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(updateCgdRestController.getServerProtocol()).thenReturn(serverProtocol);
        Mockito.when(updateCgdRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClient);
        Mockito.when(updateCgdRestController.updateCgdForItem(itemBarcode,owningInstitution,oldCollectionGroupDesignation,newCollectionGroupDesignation,cgdChangeNotes)).thenCallRealMethod();
        String response = updateCgdRestController.updateCgdForItem(itemBarcode,owningInstitution,oldCollectionGroupDesignation,newCollectionGroupDesignation,cgdChangeNotes);
        assertNotNull(response);
        assertEquals(response,ReCAPConstants.SUCCESS);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(ReCAPConstants.API_KEY, ReCAPConstants.RECAP);
        return headers;
    }
}
