package org.recap.controller;

import org.recap.ReCAPConstants;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by akulak on 27/2/18 .
 */
@RestController
@RequestMapping("/deAccessionService")
public class DeAccessionRestController extends ReCAPController {

    /**
     * This method will call scsb-circ microservice to deaccession an item in SCSB.
     *
     * @param deAccessionRequest the de accession request
     * @return the map
     */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/deaccession", method = RequestMethod.POST)
    public Map<String, String> deAccession(@RequestBody String deAccessionRequest) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(deAccessionRequest, getRestHeaderService().getHttpHeaders());
            resultMap = getRestTemplate()
            				.postForObject(
        						getScsbCircUrl() + "/sharedCollection/deAccession",
        						requestEntity,
        						Map.class
    						);
        } catch (Exception ex) {
            getLogger().error(ReCAPConstants.LOG_ERROR,ex);
        }
        return resultMap;
    }
}
