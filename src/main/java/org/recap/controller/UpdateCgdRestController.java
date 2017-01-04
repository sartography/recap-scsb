package org.recap.controller;

import org.recap.ReCAPConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
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

    @RequestMapping(value="/updateCgd", method = RequestMethod.GET)
    public String updateCgdForItem(@RequestParam Integer itemId, @RequestParam String newCollectionGroupDesignation, @RequestParam String cgdChangeNotes) {
        String statusResponse = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity requestEntity = new HttpEntity<>(getHttpHeaders());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverProtocol + scsbSolrClient + ReCAPConstants.URL_UPDATE_CGD)
                    .queryParam("itemId", itemId)
                    .queryParam("newCollectionGroupDesignation", newCollectionGroupDesignation)
                    .queryParam("cgdChangeNotes", cgdChangeNotes);

            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, String.class);
            statusResponse = responseEntity.getBody();
        } catch (Exception e) {
            logger.error(e.getMessage());
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
