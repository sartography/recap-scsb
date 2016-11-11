package org.recap.controller.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.recap.controller.BaseControllerUT;
import org.recap.model.AccessionRequest;
import org.recap.model.DeAccessionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by chenchulakshmig on 14/10/16.
 */
public class SharedCollectionRestControllerUT extends BaseControllerUT {

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.solr.client.url}")
    String scsbSolrClientUrl;

    @Test
    public void itemAvailabilityStatus() throws Exception {
        String itemBarcode = "32101056185125";
        MvcResult savedResult = this.mockMvc.perform(get("/sharedCollection/itemAvailabilityStatus")
                .headers(getHttpHeaders())
                .param("itemBarcode", itemBarcode))
                .andExpect(status().isOk())
                .andReturn();

        String itemStatus = savedResult.getResponse().getContentAsString();
        assertNotNull(itemStatus);
        assertEquals(itemStatus, "Available");
    }

    @Test
    public void deAccession() throws Exception {
        Random random = new Random();
        String itemBarcode = String.valueOf(random.nextInt());

        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        deAccessionRequest.setItemBarcodes(Arrays.asList(itemBarcode));

        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/sharedCollection/deAccession")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(deAccessionRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
    }

    @Test
    public void accession() throws Exception {

        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PB");
        accessionRequest.setItemBarcode("32101095533293");

        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/sharedCollection/accession")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(accessionRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertEquals(result, "Success");
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api_key", "recap");
        return headers;
    }

}