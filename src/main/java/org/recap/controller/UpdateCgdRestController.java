package org.recap.controller;

import org.recap.ReCAPConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by rajeshbabuk on 3/1/17.
 */

@RestController
@RequestMapping("/updateCgdService")
public class UpdateCgdRestController {

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

    @RequestMapping(value="/updateCgd", method = RequestMethod.GET)
    public String updateCgdForItem(@RequestParam String itemBarcode, @RequestParam String owningInstitution, @RequestParam String oldCollectionGroupDesignation, @RequestParam String newCollectionGroupDesignation, @RequestParam String cgdChangeNotes) {
        String statusResponse = null;
        try {
            RestTemplate restTemplate;
            HttpEntity requestEntity = new HttpEntity<>(getHttpHeaders());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getServerProtocol() + getScsbSolrClientUrl() + ReCAPConstants.URL_UPDATE_CGD)
                    .queryParam(ReCAPConstants.CGD_UPDATE_ITEM_BARCODE, itemBarcode)
                    .queryParam(ReCAPConstants.OWNING_INSTITUTION, owningInstitution)
                    .queryParam(ReCAPConstants.OLD_CGD, oldCollectionGroupDesignation)
                    .queryParam(ReCAPConstants.NEW_CGD, newCollectionGroupDesignation)
                    .queryParam(ReCAPConstants.CGD_CHANGE_NOTES, cgdChangeNotes);

            ResponseEntity<String> responseEntity = getRestTemplate().exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, String.class);
            statusResponse = responseEntity.getBody();
        } catch (Exception e) {
            logger.error(ReCAPConstants.LOG_ERROR,e);
            statusResponse = ReCAPConstants.FAILURE + "-" + e.getMessage();
        }
        return statusResponse;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(ReCAPConstants.API_KEY, ReCAPConstants.RECAP);
        return headers;
    }
}
